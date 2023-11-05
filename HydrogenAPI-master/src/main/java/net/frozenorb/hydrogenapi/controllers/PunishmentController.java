package net.frozenorb.hydrogenapi.controllers;

import net.frozenorb.hydrogenapi.repository.PunishmentRepository;
import net.frozenorb.hydrogenapi.models.Punishment;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PunishmentController {

    @Autowired private PunishmentRepository punishmentRepository;

    @GetMapping(path = "/punishments")
    public ResponseEntity<List> getPunishments(@RequestParam("user") String uuid){
        List<JSONObject> punishments = new ArrayList<>();

        punishmentRepository.findByUuid(uuid).forEach(punishment -> {
            punishments.add(punishment.toJSON());
        });

        return new ResponseEntity<>(punishments, HttpStatus.OK);
    }

    @GetMapping(path = "/staffPunishments")
    public ResponseEntity<List> getStaffPunishments(@RequestParam("user") String uuid){
        List<JSONObject> punishments = punishmentRepository.findAll().stream().filter(punishment -> punishment.getAddedBy() != null && punishment.getAddedBy().equalsIgnoreCase(uuid)).map(Punishment::toJSON).collect(Collectors.toList());
        return new ResponseEntity<>(punishments, HttpStatus.OK);
    }

    @PostMapping(path = "/punishments")
    public ResponseEntity<JSONObject> punish(@RequestHeader(value = "MHQ-Authorization") String apiKey, @RequestBody Map<String, Object> body){
        String player = body.get("user").toString();
        String playerIp = null;
        if(body.containsKey("userIp"))
            playerIp = body.get("userIp").toString();

        String addedBy = null;
        String addedByIp = null;
        if(body.containsKey("addedBy") && body.containsKey("addedByIp")){
            addedBy = body.get("addedBy").toString();
            addedByIp = body.get("addedByIp").toString();
        }

        String publicReason = body.get("publicReason").toString();
        String privateReason = body.get("privateReason").toString();
        String punishType = body.get("type").toString();
        Map<String, String> metadata = (Map<String, String>) body.get("metadata");

        long expiresIn = -1;

        if(body.containsKey("expiresIn"))
            expiresIn = (int) body.get("expiresIn");
        //if(expiresIn != -1)
        //    expiresIn += System.currentTimeMillis() / 1000;

        punishmentRepository.save(new Punishment(
                player,
                playerIp,
                publicReason,
                privateReason,
                punishType,
                "Server",
                apiKey,
                addedBy,
                addedByIp,
                System.currentTimeMillis(),
                expiresIn,
                expiresIn * 1000,
                metadata
        ));

        JSONObject response = new JSONObject();
        response.put("success", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "/users/{uuid}/activePunishment")
    public ResponseEntity<JSONObject> pardon(@PathVariable("uuid") String uuid, @RequestBody Map<String, String> body){
        String type = body.get("type");
        String reason = body.get("reason");

        String removedBy = null;
        String removedByIp = null;
        if(body.containsKey("removedBy") && body.containsKey("removedByIp")){
            removedBy = body.get("removedBy");
            removedByIp = body.get("removedByIp");
        }

        // can't use lambda cause punishment variable would need to be final, 10/10
        Punishment punishment = null;
        for(Punishment _punishment : punishmentRepository.findByUuid(uuid)){
            if(_punishment.getType().equals(type) && _punishment.isActive()){
                punishment = _punishment;
            }
        }

        JSONObject json = new JSONObject();

        if(punishment != null) {
            punishment.setRemovedBy(removedBy);
            punishment.setRemovedByIp(removedByIp);
            punishment.setRemovalReason(reason);
            punishment.setRemovedAt(System.currentTimeMillis());
            punishmentRepository.save(punishment);
            json.put("success", true);
        } else {
            json.put("success", false);
            json.put("message", "Player doesn't have an active punishment");
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

}
