package net.frozenorb.hydrogenapi.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Data @AllArgsConstructor @NoArgsConstructor @Document(collection = "servergroups")
public class ServerGroup {

    @Id private String _id;
    @Indexed private String id;

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("image", ""); // these two are unused so let's just make them placeholders
        json.put("announcements", new JSONArray());
        return json;
    }
}
