package net.frozenorb.hydrogenapi.controllers;

import net.frozenorb.hydrogenapi.models.Expirable;
import net.frozenorb.hydrogenapi.repository.PlayerRepository;
import net.frozenorb.hydrogenapi.repository.RankGrantRepository;
import net.frozenorb.hydrogenapi.models.RankGrant;
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
public class GrantController {

    @Autowired private RankGrantRepository rankGrantRepository;
    @Autowired private PlayerRepository playerRepository;

    @GetMapping(path = "/grants")
    public ResponseEntity<List> getGrants(@RequestParam("user") String uuid){
        List<JSONObject> grants = new ArrayList<>();
        rankGrantRepository.findByUuid(uuid).forEach(rankGrant -> grants.add(rankGrant.toJSON()));
        return new ResponseEntity<>(grants, HttpStatus.OK);
    }

    @PostMapping(path = "/grants")
    public ResponseEntity<JSONObject> grant(@RequestBody Map<String, Object> body){
        String user = body.get("user").toString();
        String reason = body.get("reason").toString();

        String addedBy = null;
        String addedByIp = null;
        if(body.containsKey("addedBy") && body.containsKey("addedByIp")){
            addedBy = body.get("addedBy").toString();
            addedByIp = body.get("addedByIp").toString();
        }

        List<String> scopes = (List) body.get("scopes");
        String rank = body.get("rank").toString();

        long expiresIn = -1;
        if(body.containsKey("expiresIn"))
            expiresIn = System.currentTimeMillis() - Long.valueOf((int) body.get("expiresIn") * 1000);

        rankGrantRepository.save(new RankGrant(user, reason, rank, scopes, expiresIn, System.currentTimeMillis(), addedBy, addedByIp));

        return ResponseUtil.success;
    }

    @DeleteMapping(path = "/grants/{grantid}")
    public ResponseEntity<JSONObject> deleteGrant(@PathVariable("grantid") String grantId, @RequestBody Map<String, String> body){
        String removedBy = body.get("removedBy");
        String removedByIp = body.get("removedByIp");
        String removalReason = body.get("reason");

        RankGrant grant = rankGrantRepository.findById(grantId);
        grant.setRemovedBy(removedBy);
        grant.setRemovedByIp(removedByIp);
        grant.setRemovalReason(removalReason);
        grant.setRemovedAt(System.currentTimeMillis());
        rankGrantRepository.save(grant);
        return ResponseUtil.success;
    }

}