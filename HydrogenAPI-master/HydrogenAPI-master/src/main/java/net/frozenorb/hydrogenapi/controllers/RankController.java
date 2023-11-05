package net.frozenorb.hydrogenapi.controllers;

import net.frozenorb.hydrogenapi.models.Rank;
import net.frozenorb.hydrogenapi.repository.RankRepository;
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
public class RankController {

    @Autowired private RankRepository rankRepository;

    @GetMapping(path = "/ranks")
    public ResponseEntity<List> getRanks(){
        List<JSONObject> ranks = new ArrayList<>();

        rankRepository.findAll().forEach(rank -> ranks.add(rank.toJSON()));

        return new ResponseEntity<>(ranks, HttpStatus.OK);
    }

    //non springboot oriented just to get the rank from the repo. No other usage
    public Rank getRankFromString(String string) {
        return rankRepository.findByRankid(string);
    }

    @PostMapping(value = "/ranks/{rank}/update")
    public ResponseEntity<JSONObject> update(@PathVariable("rank") String rankName, @RequestBody Map<String, Object> requestBody){
        Rank rank = rankRepository.findByRankid(rankName.toLowerCase());
        if(requestBody.containsKey("generalWeight")) rank.setGeneralWeight((int) requestBody.get("generalWeight"));
        if(requestBody.containsKey("displayWeight")) rank.setDisplayWeight((int) requestBody.get("displayWeight"));
        if(requestBody.containsKey("gamePrefix")) rank.setGamePrefix(requestBody.get("gamePrefix").toString());
        if(requestBody.containsKey("gameColor")) rank.setGameColor(requestBody.get("gameColor").toString());
        if(requestBody.containsKey("displayName")) rank.setDisplayName(requestBody.get("displayName").toString());
        if(requestBody.containsKey("staffRank")) rank.setStaffRank((boolean) requestBody.get("staffRank"));
        if(requestBody.containsKey("grantRequiresTotp")) rank.setGrantRequiresTotp((boolean) requestBody.get("grantRequiresTotp"));
        if(requestBody.containsKey("permissions")) rank.setPermissions((List<String>) requestBody.get("permissions"));
        if(requestBody.containsKey("addPermission")){
            List<String> perms = new ArrayList<>(rank.getPermissions());
            perms.add(requestBody.get("addPermission").toString());
            rank.setPermissions(perms);
        }
        if(requestBody.containsKey("removePermission")){
            List<String> perms = new ArrayList<>(rank.getPermissions());
            perms.remove(requestBody.get("removePermission").toString());
            rank.setPermissions(perms);
        }
        rankRepository.save(rank);
        return ResponseUtil.success;
    }

    @GetMapping(path = "/ranks/{rank}/details")
    public ResponseEntity<JSONObject> rankDetails(@PathVariable("rank") String rankName) {
        Rank rank = rankRepository.findByRankid(rankName.toLowerCase());
        return new ResponseEntity<>(rank.toJSON(), HttpStatus.OK);
    }
}
