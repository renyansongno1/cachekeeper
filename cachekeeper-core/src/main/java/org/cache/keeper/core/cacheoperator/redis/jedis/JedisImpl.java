/*
 * Copyright 2025 [cache keeper]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cache.keeper.core.cacheoperator.redis.jedis;

import org.cache.keeper.core.cacheoperator.redis.RedisClient;
import org.cache.keeper.core.cacheoperator.redis.RedisConfiguration;
import redis.clients.jedis.*;
import redis.clients.jedis.util.Pool;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class JedisImpl<K, V> implements RedisClient<K, V> {

    public static final JedisImpl INSTANCE = new JedisImpl();
    private JedisImpl() {}

    public static JedisImpl getInstance() {
        return INSTANCE;
    }

    private JedisConfiguration config = null;

    private Pool<Jedis> jedis = null;

    private JedisCluster jedisCluster = null;

    @Override
    public void initClient(RedisConfiguration redisConfiguration) {
        if (redisConfiguration instanceof JedisConfiguration jedisConfiguration) {
            this.config = jedisConfiguration;
            switch (jedisConfiguration.getConnectionMode()) {
                case STANDALONE -> {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(jedisConfiguration.getStandaloneMaxTotal());
                    jedisPoolConfig.setMaxIdle(jedisConfiguration.getStandaloneMaxIdle());
                    jedis = new JedisPool(jedisPoolConfig,
                            jedisConfiguration.getStandaloneHost(),
                            jedisConfiguration.getStandalonePort(),
                            jedisConfiguration.getStandaloneTimeout(),
                            jedisConfiguration.getStandalonePassword(),
                            jedisConfiguration.getStandaloneDatabase());
                }
                case SENTINEL -> {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(jedisConfiguration.getSentinelMaxTotal());
                    jedisPoolConfig.setMaxIdle(jedisConfiguration.getSentinelMaxIdle());
                    jedis = new JedisSentinelPool(jedisConfiguration.getSentinelMasterName(),
                            Set.of(jedisConfiguration.getSentinelHosts()),
                            jedisPoolConfig,
                            jedisConfiguration.getSentinelTimeout(),
                            jedisConfiguration.getSentinelPassword()
                            );
                }
                case CLUSTER -> {
                    Set<HostAndPort> nodes = Arrays.stream(jedisConfiguration.getClusterNodes())
                            .map(HostAndPort::from)
                            .collect(Collectors.toSet());
                    int timeout = jedisConfiguration.getClusterTimeout();
                    int maxAttempts = jedisConfiguration.getClusterMaxAttempts();
                    ConnectionPoolConfig jedisPoolConfig = new ConnectionPoolConfig();
                    jedisPoolConfig.setMaxTotal(jedisConfiguration.getClusterMaxTotal());
                    jedisPoolConfig.setMaxIdle(jedisConfiguration.getClusterMaxIdle());
                    jedisCluster = new JedisCluster(nodes, timeout, maxAttempts, jedisPoolConfig);
                }
                default -> throw new IllegalArgumentException("Unsupported connection mode: " + jedisConfiguration.getConnectionMode());
            }
        } else {
            throw new IllegalArgumentException("redis configuration is not JedisConfiguration");
        }
    }

    @Override
    public V get(K key) {
        if (config.getConnectionMode() == JedisConfiguration.ConnectionMode.CLUSTER) {
            String value = jedisCluster.get(key.toString());
            return (V) value;
        }
        return (V) jedis.getResource().get(key.toString());
    }

    @Override
    public String loadLuaScript(String luaScript) {
        if (config.getConnectionMode() == JedisConfiguration.ConnectionMode.CLUSTER) {
            return jedisCluster.scriptLoad(luaScript);
        }
        return jedis.getResource().scriptLoad(luaScript);
    }

    @Override
    public void evalLua(String luaScript, String[] keys, String[] args) {
        if (config.getConnectionMode() == JedisConfiguration.ConnectionMode.CLUSTER) {
            jedisCluster.eval(luaScript, Arrays.stream(keys).toList(), Arrays.stream(args).toList());
            return;
        }
        jedis.getResource().eval(luaScript, Arrays.stream(keys).toList(), Arrays.stream(args).toList());
    }

    @Override
    public void evalSha(String luaSha, String[] keys, String[] args) {
        if (config.getConnectionMode() == JedisConfiguration.ConnectionMode.CLUSTER) {
            jedisCluster.evalsha(luaSha, Arrays.stream(keys).toList(), Arrays.stream(args).toList());
            return;
        }
        jedis.getResource().evalsha(luaSha, Arrays.stream(keys).toList(), Arrays.stream(args).toList());
    }

    @Override
    public void lPush(String queueName, String value) {
        if (config.getConnectionMode() == JedisConfiguration.ConnectionMode.CLUSTER) {
            jedisCluster.lpush(queueName, value);
        }
        jedis.getResource().lpush(queueName, value);
    }

    @Override
    public void delete(String key) {
        if (config.getConnectionMode() == JedisConfiguration.ConnectionMode.CLUSTER) {
            jedisCluster.del(key);
        }
        jedis.getResource().del(key);
    }
}
