package net.frozenorb.hydrogenapi.controllers;

import com.authy.AuthyApiClient;
import com.authy.api.User;
import com.authy.api.Users;
import net.frozenorb.hydrogenapi.HydrogenAPI;
import net.frozenorb.hydrogenapi.models.Rank;
import net.frozenorb.hydrogenapi.models.RankGrant;
import net.frozenorb.hydrogenapi.repository.*;
import net.frozenorb.hydrogenapi.models.Player;
import net.frozenorb.hydrogenapi.utils.PlayerUtil;
import net.frozenorb.hydrogenapi.utils.ResponseUtil;
import net.frozenorb.hydrogenapi.utils.ServerUtil;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController @RequestMapping("/users/{uuid}")
public class UserController {

    @Autowired private PlayerRepository playerRepository;
    @Autowired private PlayerUtil playerUtil;
    @Autowired private PunishmentRepository punishmentRepository;
    @Autowired private RankGrantRepository rankGrantRepository;
    @Autowired private PrefixGrantRepository prefixGrantRepository;
    @Autowired private RankRepository rankRepository;

    @GetMapping
    public ResponseEntity<JSONObject> getPlayer(@PathVariable("uuid") String uuid){
        Player player = playerRepository.findByUuid(uuid);
        JSONObject response = new JSONObject();

        if(player == null){
            response.put("success", false);
            response.put("message", "Player hasn't joined the server before");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.putAll(player.toJSON());
        response.put("lastUsername", player.getUsername());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/formattedRankColor")
    public ResponseEntity<JSONObject> getFormattedRankColor(@PathVariable("uuid") String uuid) {
        Player player = playerRepository.findByUuid(uuid);

        JSONObject toRespond = new JSONObject();

        if (player == null) {
            toRespond.put("successs", false);
            toRespond.put("message", "Unable to complete the request. Player does not exist in spring");
            return new ResponseEntity<>(toRespond, HttpStatus.OK);
        }

        List<RankGrant> grants = playerUtil.getActiveRankGrants(player);
        grants.sort((o1, o2) -> rankRepository.findByRankid(o2.getRank()).getDisplayWeight() - rankRepository.findByRankid(o1.getRank()).getDisplayWeight());

        Optional<Rank> foundRank = grants.stream().map(grant -> rankRepository.findByRankid(grant.getRank())).findFirst();

        toRespond.put("displayedName", (foundRank.map(rank -> (rank.getGameColor() + player.getUsername())).orElseGet(() -> (player.getUsername()))));

        return new ResponseEntity<>(toRespond, HttpStatus.OK);
    }

    @GetMapping(path = "/details")
    public ResponseEntity<JSONObject> getDetails(@PathVariable("uuid") String uuid){
        Player player = playerRepository.findByUuid(uuid);
        JSONObject response = new JSONObject();

        if(player == null){
            response.put("success", false);
            response.put("message", "Player hasn't joined the server before");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.put("user", player.toJSON());
        response.put("ipLog", player.getIpLog());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<JSONObject> login(@RequestHeader(value = "MHQ-Authorization") String apiKey, @PathVariable("uuid") String uuid, @RequestBody Map<String, String> body){
        String username = body.get("username");
        String userIp = body.get("userIp");

        Player player = playerRepository.findByUuid(uuid);

        // get the player data here so if ip logging is enabled there isn't an error
        JSONObject response = playerUtil.getPlayerByUUID(uuid, username, userIp);

        playerUtil.setOnline(uuid, apiKey);

        if(HydrogenAPI.getSettingsManager().getSettings().get("log-ips").equals("true"))
            playerUtil.logIp(uuid, userIp);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/wasOnline")
    public ResponseEntity<JSONObject> loginStaff(@PathVariable("uuid") String uuid, HashMap<String, Object> requestBody){
        Player player = playerRepository.findByUuid(uuid);
        JSONObject object = new JSONObject();
        object.put("user", player.toJSON());
        object.put("wasOnline", player.isOnline());
        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    @PostMapping(path = "/registerEmail")
    public ResponseEntity<JSONObject> registerEmail(@PathVariable("uuid") String uuid, @RequestBody Map<String, String> body){
        String email = body.get("email");
        String userIp = body.get("userIp");

        Player player = playerRepository.findByUuid(uuid);
        player.setEmail(email);
        playerRepository.save(player);

        return ResponseUtil.success;
    }

    @PostMapping(path = "/prefix")
    public ResponseEntity<JSONObject> updateActivePrefix(@PathVariable("uuid") String uuid, @RequestBody Map<String, String> body){
        String prefix = body.get("prefix");

        Player player = playerRepository.findByUuid(uuid);
        player.setActivePrefix(prefix);
        playerRepository.save(player);

        return ResponseUtil.success;
    }

    @PostMapping(path = "/colors")
    public ResponseEntity<JSONObject> updateColors(@PathVariable("uuid") String uuid, @RequestBody Map<String, String> body){
        String iconColor = body.get("iconColor");
        String nameColor = body.get("nameColor");

        Player player = playerRepository.findByUuid(uuid);
        player.setIconColor(iconColor);
        player.setNameColor(nameColor);
        playerRepository.save(player);

        return null;
    }

    @PostMapping(path = "/setupTotp")
    public ResponseEntity<JSONObject> setupTOTP(@PathVariable("uuid") String uuid, @RequestBody Map<String, String> body){
        String secret = body.get("secret");
        String totpCode = body.get("totpCode");


        Player player = playerRepository.findByUuid(uuid);

        boolean authorized = HydrogenAPI.getGoogleAuthenticator().authorize(secret, Integer.parseInt(totpCode));
        if(!authorized){
            JSONObject response = new JSONObject();
            response.put("success", false);
            response.put("message", "Invalid totp code...");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        player.setTotpSecret(secret);
        playerRepository.save(player);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", true);
        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
    }

    @PostMapping(path = "/requireTotp")
    public ResponseEntity<JSONObject> requireTotp(@PathVariable("uuid") String uuid){
        Player player = playerRepository.findByUuid(uuid);
        player.setLastTotpAuthentication(0L);
        playerRepository.save(player);
        return ResponseUtil.success;
    }

    @PostMapping(path = "/verifyTotp")
    public ResponseEntity<JSONObject> verifyTOTP(@PathVariable("uuid") String uuid, @RequestBody Map<String, String> body){
        String ip = body.get("userIp");
        String code = body.get("totpCode");

        Player player = playerRepository.findByUuid(uuid);

        JSONObject json = new JSONObject();
        if(player.getTotpSecret() == null){
            json.put("success", false);
            json.put("authorized", false);
            json.put("message", "You do not have two-factor authentication setup! Type \"/2fasetup\" to begin the setup process.");
        }else {
            boolean authorized = HydrogenAPI.getGoogleAuthenticator().authorize(player.getTotpSecret(), Integer.parseInt(code));
            json.put("success", true);
            json.put("authorized", authorized);
            json.put("message", (authorized ? "Your identity has been verified." : "Invalid totp code..."));
            if(authorized){
                player.setLastTotpAuthentication(System.currentTimeMillis());
                playerRepository.save(player);
            }
        }
        playerRepository.save(player);
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @GetMapping(path = "/requiresTotp")
    public ResponseEntity<JSONObject> requiresTotp(@PathVariable("uuid") String uuid){
        JSONObject json = new JSONObject();

        Player player = playerRepository.findByUuid(uuid);

        boolean debug = false;

        if(debug){
            if(player.getTotpSecret() == null){
                json.put("required", false);
                json.put("message", "REQUIRES_SETUP");
            }else{
                json.put("required", true);
                json.put("message", "REQUIRED_UNAUTHORIZED");
            }
        }else{
            if(player.getTotpSecret() == null){
                json.put("required", false);
                json.put("message", "REQUIRES_SETUP");
            }else{
                System.out.println("time = " + (System.currentTimeMillis() - player.getLastTotpAuthentication()));
                if(System.currentTimeMillis() - player.getLastTotpAuthentication() > 1800 * 1000) {
                    json.put("required", true);
                    json.put("message", "REQUIRED_UNAUTHORIZED");
                }else{
                    json.put("required", false);
                    json.put("message", "NOT_REQUIRED_IP_PRE_AUTHORIZED");
                }
            }
        }

        /*if(player.getTotpSecret() == null){
            json.put("required", true);
            json.put("message", "Please setup your two-factor authentication using /2fasetup.");
        }else {
            if(System.currentTimeMillis() - player.getLastTotpAuthentication() < 30 * 1000) {
                json.put("required", false);
                json.put("message", "NOT_REQUIRED_IP_PRE_AUTHORIZED");
            }else{
                json.put("required", true);
                json.put("message", "REQUIRED_UNAUTHORIZED");
            }
        }*/
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

    @PostMapping(path = "/disposableLoginTokens")
    public ResponseEntity<JSONObject> getDisposableToken(@RequestBody Map<String, String> body){
        String uuid = body.get("user");
        String ip = body.get("userIp");

        JSONObject json = new JSONObject();

        Player player = playerRepository.findByUuid(uuid);
        if(player.getEmail() == null){
            json.put("success", false);
            json.put("message", "Your profile doesn't have an account.");
            return new ResponseEntity<>(json, HttpStatus.OK);
        }

        String token = UUID.randomUUID().toString();
        HydrogenAPI.getRedisManager().getJedisPool().getResource().hset("disposableTokens", token, uuid);

        json.put("token", token);
        return new ResponseEntity<>(json, HttpStatus.OK);
    }

}
