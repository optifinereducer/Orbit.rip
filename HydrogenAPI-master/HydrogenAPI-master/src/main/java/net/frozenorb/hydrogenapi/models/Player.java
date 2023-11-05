package net.frozenorb.hydrogenapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
@Data @AllArgsConstructor @NoArgsConstructor @Document(collection = "players")
public class Player {
    @Id private String id;
    @Indexed private String uuid;
    @Indexed private String username;
    @Indexed private String iconColor;
    @Indexed private String nameColor;
    @Indexed private String activePrefix;
    @Indexed private String email;
    @Indexed private String totpSecret;
    @Indexed private long lastSeenAt;
    @Indexed private String lastSeenOn;
    @Indexed private boolean online = false;
    @Indexed private long lastTotpAuthentication;
    @Indexed private List<String> ranks = new ArrayList<>();
    @Indexed private List<String> prefixes = new ArrayList<>();
    @Indexed private List<String> ipLog = new ArrayList<>();

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("uuid", uuid);
        json.put("username", username);
        json.put("online", online);
        json.put("lastSeenOn", lastSeenOn);
        json.put("lastSeenAt", lastSeenAt);
        json.put("ranks", ranks);
        json.put("scopeRanks", ranks);
        json.put("prefixes", prefixes);
        json.put("ipLog", ipLog);

        if(totpSecret != null){
            json.put("lastTotpAuthentication", lastTotpAuthentication);
            json.put("totpSecret", totpSecret);
        }

        if(iconColor != null)
            json.put("iconColor", iconColor);

        if(nameColor != null)
            json.put("nameColor", nameColor);

        if(activePrefix != null)
            json.put("activePrefix", activePrefix);

        return json;
    }
}
