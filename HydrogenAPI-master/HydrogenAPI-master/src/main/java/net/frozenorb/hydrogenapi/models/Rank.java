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
@Data @AllArgsConstructor @NoArgsConstructor @Document(collection = "ranks")
public class Rank {
    @Id private String id;
    @Indexed private String rankid;
    @Indexed private String inheritsFromId;
    @Indexed private int generalWeight;
    @Indexed private int displayWeight;
    @Indexed private String displayName;
    @Indexed private String gamePrefix;
    @Indexed private String gameColor;
    @Indexed private boolean staffRank;
    @Indexed private boolean grantRequiresTotp;
    @Indexed private String queueMessage;
    @Indexed private List<String> permissions = new ArrayList<>();

    public JSONObject toJSON(){
        JSONObject rank = new JSONObject();
        rank.put("id", rankid);
        rank.put("inheritsFromId", inheritsFromId);
        rank.put("generalWeight", generalWeight);
        rank.put("displayWeight", displayWeight);
        rank.put("displayName", displayName);
        rank.put("gamePrefix", gamePrefix);
        rank.put("gameColor", gameColor);
        rank.put("staffRank", staffRank);
        rank.put("grantRequiredTotp", grantRequiresTotp);
        rank.put("queueMessage", queueMessage);
        rank.put("permissions", permissions);
        return rank;
    }

}
