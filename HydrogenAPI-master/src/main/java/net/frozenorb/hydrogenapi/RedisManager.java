package net.frozenorb.hydrogenapi;

import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {

    @Getter
    private JedisPool jedisPool;

    public boolean init() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(128);
        jedisPool = new JedisPool(config, "127.0.0.1", 6379, 20000, null, 0);
        System.out.println("Connected to redis");
        return true;
    }

}
