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

package org.cache.keeper.core.cacheoperator.redis;

import org.cache.keeper.core.cacheoperator.ICacheOperator;

import java.util.concurrent.TimeUnit;

/**
 * cache operator Redis impl
 */
public class RedisCacheOperator<K, V> implements ICacheOperator<K, V> {

    private static final RedisCacheOperator<?, ?> INSTANCE = new RedisCacheOperator<>();

    private static final String LEASE_ID_QUEUE_NAME = "cache_keeper_lease_list";

    private static final String WRITE_CACHE_CHECK_LUA_SCRIPT = """
            -- check leaseId
            local queueKey = KEYS[1]       -- queue key
            local leaseId = ARGV[1]        -- leaseId
            local cacheKey = ARGV[2]       -- key
            local cacheValue = ARGV[3]     -- value
            local expireTime = tonumber(ARGV[4])  -- expire time
            
            local exists = redis.call('LPOS', queueKey, leaseId)
            
            if exists ~= nil then
                redis.call('SET', cacheKey, cacheValue)
                redis.call('EXPIRE', cacheKey, expireTime)
                return 1
            else
                return 0
            end
            """;

    /**
     * redis client type
     */
    private RedisClientType redisClientType;

    /**
     * redis client configuration
     */
    private RedisConfiguration redisConfiguration;

    /**
     * the redis client
     */
    private RedisClient<K, V> redisClient;

    private Boolean useLuaSha;

    private String writeCacheScriptSha = null;

    private RedisCacheOperator(){}

    private void valid() {
        if (redisClientType == null) {
            throw new RuntimeException("redis client type is null");
        }
    }

    @SuppressWarnings("unchecked")
    public void initClient() {
        redisClient = RedisClientFactory.getInstance().createClient(redisClientType);
        redisClient.initClient(redisConfiguration);
        if (Boolean.TRUE.equals(useLuaSha)) {
            writeCacheScriptSha = redisClient.loadLuaScript(WRITE_CACHE_CHECK_LUA_SCRIPT);
        }
    }

    // getter start
    public RedisClient<K, V> getRedisClient() {
        return redisClient;
    }
    // getter end

    public static class Builder {
        private RedisClientType redisClientType;
        private RedisConfiguration redisConfiguration;
        private Boolean useLuaSha;

        private Builder(){}

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder redisClientType(RedisClientType redisClientType) {
            this.redisClientType = redisClientType;
            return this;
        }

        public Builder redisConfiguration(RedisConfiguration redisConfiguration) {
            this.redisConfiguration = redisConfiguration;
            return this;
        }

        public Builder useLuaSha(Boolean useLuaSha) {
            this.useLuaSha = useLuaSha;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <K, V> RedisCacheOperator<K, V> build() {
            RedisCacheOperator<K, V> instance = (RedisCacheOperator<K, V>) INSTANCE;
            instance.redisClientType = redisClientType;
            instance.redisConfiguration = redisConfiguration;
            instance.useLuaSha = useLuaSha;
            instance.valid();
            instance.initClient();
            return instance;
        }
    }

    @Override
    public V readCache(K key) {
        return redisClient.get(key);
    }

    @Override
    public void writeCache(K key, V value, String leaseId, Long expireTime, TimeUnit expireTimeUnit) {
        String[] keys = {LEASE_ID_QUEUE_NAME};
        String[] args = {leaseId,
                key.toString(),
                value == null ? "" :value.toString(),
                String.valueOf(expireTimeUnit.toSeconds(expireTime))};
        if (writeCacheScriptSha != null && !writeCacheScriptSha.isEmpty()) {
            redisClient.evalSha(writeCacheScriptSha, keys, args);
            return;
        }
        redisClient.evalLua(WRITE_CACHE_CHECK_LUA_SCRIPT, keys, args);
    }

    @Override
    public void saveLeaseId(String leaseId) {
        // save the lease id to list?
        if (leaseId == null || leaseId.isEmpty()) {
            throw new RuntimeException("lease id is null");
        }
        redisClient.lPush(LEASE_ID_QUEUE_NAME, leaseId);
    }

    @Override
    public void deleteCacheAndClearAllLeaseId(K key) {
        redisClient.delete(key.toString());
    }

    public enum RedisClientType {
        JEDIS,
        REDISSON,
        LETTUCE,
    }
}
