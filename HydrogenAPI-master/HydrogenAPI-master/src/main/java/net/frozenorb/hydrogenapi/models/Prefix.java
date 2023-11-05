package net.frozenorb.hydrogenapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Data @AllArgsConstructor @NoArgsConstructor @Document(collection = "prefixes")
public class Prefix {
    @Id private String id;
    @Indexed private String prefixid;
    @Indexed private String displayName;
    @Indexed private String prefix;
    @Indexed private boolean purchasable;
    @Indexed private String buttonName;
    @Indexed private String buttonDescription;

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", prefixid);
        json.put("displayName", displayName);
        json.put("prefix", prefix);
        json.put("purchaseable", true); // misspelled on purpose, leave it
        json.put("buttonName", buttonName);
        json.put("buttonDescription", buttonDescription);
        return json;
    }

}
