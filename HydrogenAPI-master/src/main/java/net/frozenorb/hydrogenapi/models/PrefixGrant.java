package net.frozenorb.hydrogenapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
@Data @AllArgsConstructor @NoArgsConstructor @Document(collection = "prefixgrants")
public class PrefixGrant extends Expirable {
    @Id private String id;
    @Indexed private String uuid;
    @Indexed private String reason;
    @Indexed private String prefix;
    @Indexed private List<String> scopes = new ArrayList<>();

    public PrefixGrant(String uuid, String reason, String prefix, List<String> scopes, long expiresIn, long addedAt, String addedBy, String addedByIp){
        this.uuid = uuid;
        this.reason = reason;
        this.prefix = prefix;
        this.scopes = scopes;

        this.setExpiresIn((expiresIn == -1 ? -1 : expiresIn * 1000));
        this.setExpiresAt(getExpiresIn() == -1 ? 0 : System.currentTimeMillis() + getExpiresIn());
        //this.setExpiresAt(getExpiresIn() == -1 ? 0 : System.currentTimeMillis() + getExpiresIn());
        this.setAddedAt(addedAt);
        this.setAddedBy(addedBy);
        this.setAddedByIp(addedByIp);
    }

    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("uuid", uuid);
        json.put("reason", reason);
        json.put("prefix", prefix);
        json.put("scopes", scopes);
        json.putAll(super.toJSON());
        return json;
    }
}
