package net.frozenorb.hydrogenapi.utils;

import net.frozenorb.hydrogenapi.HydrogenAPI;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ServerUtil {

    private static JedisPool jedis = HydrogenAPI.getRedisManager().getJedisPool();

    public static void update(String name, Map<String, Object> body) {
        try (Jedis jedis1 = jedis.getResource()) {
            String key = "servers:" + name;
            jedis1.hset(key, "id", name);
            jedis1.hset(key, "displayName", name);
            jedis1.hset(key, "serverGroup", name);
            jedis1.hset(key, "serverIp", "127.0.0.1");
            jedis1.hset(key, "lastTps", body.get("lastTps").toString());
            jedis1.hset(key, "lastUpdatedAt", System.currentTimeMillis() / 1000 + "");
            jedis1.expire(key, 40); // heartbeat is send every 30sec so let's give it 10 more as a buffer

            jedis1.sadd("servers", name);
        }

/*        new Thread(() -> CompletableFuture.runAsync(() -> {
            try (Jedis jedis1 = jedis.getResource()) {
                String key = "servers:" + name;
                jedis1.hset(key, "id", name);
                jedis1.hset(key, "displayName", name);
                jedis1.hset(key, "serverGroup", name);
                jedis1.hset(key, "serverIp", "127.0.0.1");
                jedis1.hset(key, "lastTps", body.get("lastTps").toString());
                jedis1.hset(key, "lastUpdatedAt", System.currentTimeMillis() / 1000 + "");
                jedis1.expire(key, 40); // heartbeat is send every 30sec so let's give it 10 more as a buffer

                jedis1.sadd("servers", name);
            }
        })).start();*/
    }

    public static Set<JSONObject> getServersAsJSON() {
        Set<JSONObject> servers = new HashSet<>();

        try (Jedis jedis1 = jedis.getResource()) {
            jedis1.keys("servers:*").forEach(key -> {
                JSONObject json = new JSONObject(jedis1.hgetAll(key));
                servers.add(json);
            });
        }
        return servers;
    }

}
