package net.frozenorb.hydrogenapi;

import lombok.Getter;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
public class SettingsManager {

    @Getter
    private HashMap<String, String> settings = new HashMap<>();

    // there's probably a better way to do this
    //   but as said by value game devs, too bad!
    public boolean init(boolean dev){
        // go a directory up in dev mode so the file isn't in the target folder
        File file = new File("settings.txt");

        try {
            if(!file.exists()){
                file.createNewFile();

                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("redis-host::localhost");
                writer.newLine();
                writer.write("ban-permanent-message::&cYour account has been suspended from the VeltPvP Network. %newline%%newline%Appeal at https://veltpvp.com/support");
                writer.newLine();
                writer.write("ban-temporary-message::&cYour account has been temporarily suspended from the MineHQ Network. %newline%%newline%Expires in %time_remaining%.");
                writer.newLine();
                writer.write("blacklist-message::&cYour account has been blacklisted from the MineHQ Network. %newline%%newline%This type of punishment cannot be appealed.");
                writer.newLine();
                writer.write("log-ips::false");
                writer.close();

                System.out.println("Edit the config.txt file with details to your redis & database server then restart the web api.");

                return false;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            while((line = reader.readLine()) != null){
                String[] lineSplit = line.split("::");
                settings.put(lineSplit[0], lineSplit[1].replaceAll("&", "§").replaceAll("%newline%", "\n"));
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
