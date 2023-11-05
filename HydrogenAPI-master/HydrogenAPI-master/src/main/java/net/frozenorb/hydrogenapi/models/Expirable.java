package net.frozenorb.hydrogenapi.models;

import lombok.Data;
import org.json.simple.JSONObject;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public abstract class Expirable {

    @Indexed private long addedAt;
    @Indexed private String addedBy;
    @Indexed private String addedByIp;
    @Indexed private long expiresIn = -1;
    @Indexed private long expiresAt = -1L;
    @Indexed private String removedBy = null;
    @Indexed private String removedByIp = null;
    @Indexed private long removedAt = 0;
    @Indexed private String removalReason = null;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("expiresIn", getExpiresIn());
        json.put("expiresAt", expiresAt);
        json.put("addedAt", addedAt);

        if(addedBy != null && addedByIp != null) {
            json.put("addedBy", addedBy);
            json.put("addedByIp", addedByIp);
        }

        if(removedAt != 0){
            json.put("removedBy", removedBy);
            json.put("removedByIp", removedByIp);
            json.put("removalReason", removalReason);
            json.put("removedAt", removedAt);
        }

        return json;
    }

    public boolean isActive() {
        // removed
        if(this.removedAt != 0) {
            return false;
        }

        // permanent
        if(this.expiresIn == -1) {
            return true;
        }

        // expired
        return System.currentTimeMillis() < getExpiresAt();
    }

}
