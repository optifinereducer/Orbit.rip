package net.frozenorb.hydrogenapi.controllers;

import net.frozenorb.hydrogenapi.repository.PlayerRepository;
import net.frozenorb.hydrogenapi.repository.PrefixGrantRepository;
import net.frozenorb.hydrogenapi.models.PrefixGrant;
import net.frozenorb.hydrogenapi.repository.PrefixRepository;
import net.frozenorb.hydrogenapi.utils.ResponseUtil;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RestController
public class PrefixController {

    @Autowired private PlayerRepository playerRepository;
    @Autowired private PrefixRepository prefixRepository;
    @Autowired private PrefixGrantRepository prefixGrantRepository;

    @GetMapping(path = "/prefixes")
    public ResponseEntity<List> getPrefixes(){
        List<JSONObject> prefixes = new ArrayList<>();

        prefixRepository.findAll().forEach(prefix -> prefixes.add(prefix.toJSON()));

        return new ResponseEntity<>(prefixes, HttpStatus.OK);
    }

    @PostMapping(path = "/prefixes")
    public ResponseEntity<JSONObject> grantPrefix(@RequestBody Map<String, Object> body){
        String user = body.get("user").toString();
        String reason = body.get("reason").toString();
        String prefix = body.get("prefix").toString();
        List<String> scopes = (List) body.get("scopes");

        String addedBy = null;
        String addedByIp = null;
        if(body.containsKey("addedBy") && body.containsKey("addedByIp")){
            addedBy = body.get("addedBy").toString();
            addedByIp = body.get("addedByIp").toString();
        }

        long expiresIn = -1;
        if(body.containsKey("expiresIn"))
            expiresIn = (int) body.get("expiresIn");

		prefixGrantRepository.save(new PrefixGrant(user, reason, prefix, scopes, expiresIn, System.currentTimeMillis(), addedBy, addedByIp));

        return ResponseUtil.success;
    }

    @GetMapping(path = "/prefixes/grants")
    public ResponseEntity<List> getPrefixGrants(@RequestParam("user") String uuid){
        List<JSONObject> grants = new ArrayList<>();

        prefixGrantRepository.findByUuid(uuid).forEach(prefixGrant -> {
            grants.add(prefixGrant.toJSON());
        });

        return new ResponseEntity<>(grants, HttpStatus.OK);
    }

    @DeleteMapping(path = "/prefixes/{prefixid}")
    public ResponseEntity<JSONObject> deletePrefixGrant(@PathVariable("prefixid") String prefixid, @RequestBody Map<String, String> body){
        String removedBy = body.get("removedBy");
        String removedByIp = body.get("removedByIp");
        String removalReason = body.get("reason");

        PrefixGrant grant = prefixGrantRepository.findById(prefixid);
        grant.setRemovedBy(removedBy);
        grant.setRemovedByIp(removedByIp);
        grant.setRemovalReason(removalReason);
        grant.setRemovedAt(System.currentTimeMillis() / 1000);
        prefixGrantRepository.save(grant);

        return ResponseUtil.success;
    }

}
