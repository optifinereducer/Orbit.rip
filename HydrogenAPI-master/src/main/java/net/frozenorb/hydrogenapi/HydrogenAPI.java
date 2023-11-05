package net.frozenorb.hydrogenapi;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import lombok.Getter;
import net.frozenorb.hydrogenapi.controllers.RankController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ControllerAdvice(annotations = RestController.class)
public class HydrogenAPI {

    @Getter private static GoogleAuthenticator googleAuthenticator;

    @Getter private static SettingsManager settingsManager = new SettingsManager();
    @Getter private static RedisManager redisManager = new RedisManager();

    //idk why people dont do this in springboot it is super helpful lmao
    @Getter private static RankController rankController = new RankController();

    public static void main(String[] args){
        if(!settingsManager.init(true) || !redisManager.init())
            return;

        googleAuthenticator = new GoogleAuthenticator();

        SpringApplication.run(HydrogenAPI.class, args);
    }

}
