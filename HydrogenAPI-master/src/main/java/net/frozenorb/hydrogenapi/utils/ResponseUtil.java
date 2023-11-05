package net.frozenorb.hydrogenapi.utils;

import lombok.experimental.UtilityClass;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@UtilityClass
public class ResponseUtil {

    public static final ResponseEntity<JSONObject> success;

    static {
        JSONObject json = new JSONObject();
        json.put("success", true);
        success = new ResponseEntity<>(json, HttpStatus.OK);
    }

}
