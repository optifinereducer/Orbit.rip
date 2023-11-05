package net.frozenorb.hydrogenapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
@Data @AllArgsConstructor @NoArgsConstructor @Document(collection = "punishments")
public class Punishment extends Expirable {
    @Id private String id;
    @Indexed private String uuid;
    @Indexed private String userIp;
    @Indexed private String publicReason;
    @Indexed private String privateReason;
    @Indexed private String type;
    @Indexed private String actorType;
    @Indexed private String actorName;
    @Indexed private Map<String, String> metadata;

    public Punishment(String uuid, String userIp, String publicReason, String privateReason, String type, String actorType, String actorName, String addedBy, String addedByIp, long addedAt, long expiresAt, long expiresIn, Map<String, String> metadata){
        this.uuid = uuid;
        this.userIp = userIp;
        this.publicReason = publicReason;
        this.privateReason = privateReason;
        this.type = type;
        this.actorType = actorType;
        this.actorName = actorName;
        this.metadata = metadata;

        this.setExpiresIn((expiresIn == -1000 ? -1 : expiresIn - 1000));
        this.setExpiresAt(getExpiresIn() == -1 ? 0 : System.currentTimeMillis() + getExpiresIn());
        this.setAddedAt(addedAt);
        this.setAddedBy(addedBy);
        this.setAddedByIp(addedByIp);
    }

    @Override
    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", uuid);
        json.put("publicReason", publicReason);
        json.put("privateReason", privateReason);
        json.put("type", type);
        json.put("actorType", actorType);
        json.put("actorName", actorName);
        json.putAll(super.toJSON());
        return json;
    }

}
