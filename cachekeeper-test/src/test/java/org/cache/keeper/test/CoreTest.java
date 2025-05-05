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

package org.cache.keeper.test;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.cache.keeper.core.CacheKeeper;
import org.cache.keeper.core.cacheoperator.redis.RedisCacheOperator;
import org.cache.keeper.core.cacheoperator.redis.jedis.JedisConfiguration;
import org.cache.keeper.core.cacheoperator.redis.jedis.JedisImpl;
import org.cache.keeper.core.config.CacheKeeperConfiguration;
import org.cache.keeper.core.config.CachePenetrationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * core test
 */
@Testcontainers
class CoreTest {

    @SuppressWarnings("rawtypes")
    @Container
    public GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:8.0-rc1"))
            .withExposedPorts(6379);

    @BeforeEach
    public void setUp() {
        assert redis.isRunning();
    }

    @Test
    void testConfigError() {
        // connection mode not set
        assertThrows(IllegalArgumentException.class, () ->
                RedisCacheOperator.Builder.newBuilder()
                        .redisClientType(RedisCacheOperator.RedisClientType.JEDIS)
                        .redisConfiguration(JedisConfiguration.Builder.newBuilder()
                                .standaloneHost(redis.getHost())
                                .standalonePort(redis.getFirstMappedPort())
                                .build()).build().initClient());
    }

    @Test
    void testReadCache() {
        RedisCacheOperator<String, String> cacheOperator = RedisCacheOperator.Builder.newBuilder()
                .redisClientType(RedisCacheOperator.RedisClientType.JEDIS)
                .redisConfiguration(JedisConfiguration.Builder.newBuilder()
                        .connectionMode(JedisConfiguration.ConnectionMode.STANDALONE)
                        .standaloneHost(redis.getHost())
                        .standalonePort(redis.getFirstMappedPort())
                        .build())
                .build();

        CacheKeeperConfiguration<String, String> config = CacheKeeperConfiguration.Builder.newBuilder()
                .cacheExpireTime(10L)
                .cacheExpireTimeUnit(TimeUnit.SECONDS)
                .cachePenetrationStrategy(CachePenetrationStrategy.NONE)
                .cacheOperator(cacheOperator)
                .build();

        CacheKeeper<String, String> cacheKeeper = CacheKeeper.Builder.newBuilder()
                .config(config)
                .build();
        JedisImpl<String, String> jedisClient = (JedisImpl<String, String>) cacheOperator.getRedisClient();
        jedisClient.set("testKey", "value");
        String value = cacheKeeper.readCache("testKey", key -> "value");
        assert value.equals("value");
    }

    @Test
    void readNullCache() {
        RedisCacheOperator<String, String> cacheOperator = RedisCacheOperator.Builder.newBuilder()
                .redisClientType(RedisCacheOperator.RedisClientType.JEDIS)
                .redisConfiguration(JedisConfiguration.Builder.newBuilder()
                        .connectionMode(JedisConfiguration.ConnectionMode.STANDALONE)
                        .standaloneHost(redis.getHost())
                        .standalonePort(redis.getFirstMappedPort())
                        .build())
                .build();

        CacheKeeperConfiguration<String, String> config = CacheKeeperConfiguration.Builder.newBuilder()
                .cacheExpireTime(10L)
                .cacheExpireTimeUnit(TimeUnit.SECONDS)
                .cachePenetrationStrategy(CachePenetrationStrategy.NONE)
                .cacheOperator(cacheOperator)
                .build();

        CacheKeeper<String, String> cacheKeeper = CacheKeeper.Builder.newBuilder()
                .config(config)
                .build();
        String value = cacheKeeper.readCache("testKey", key -> "value");
        assert value.equals("value");
    }

    @Test
    void readNullCacheWithPenetrationConfigError() {
        RedisCacheOperator<String, String> cacheOperator = RedisCacheOperator.Builder.newBuilder()
                .redisClientType(RedisCacheOperator.RedisClientType.JEDIS)
                .redisConfiguration(JedisConfiguration.Builder.newBuilder()
                        .connectionMode(JedisConfiguration.ConnectionMode.STANDALONE)
                        .standaloneHost(redis.getHost())
                        .standalonePort(redis.getFirstMappedPort())
                        .build())
                .build();

        assertThrows(IllegalArgumentException.class, () -> CacheKeeperConfiguration.Builder.newBuilder()
                .cacheExpireTime(10L)
                .cacheExpireTimeUnit(TimeUnit.SECONDS)
                .cachePenetrationStrategy(CachePenetrationStrategy.WRITE_NULL_SOME_TIME)
                .cacheOperator(cacheOperator)
                .build());
    }

    @Test
    void readNullCacheWithPenetration() {
        RedisCacheOperator<String, String> cacheOperator = RedisCacheOperator.Builder.newBuilder()
                .redisClientType(RedisCacheOperator.RedisClientType.JEDIS)
                .redisConfiguration(JedisConfiguration.Builder.newBuilder()
                        .connectionMode(JedisConfiguration.ConnectionMode.STANDALONE)
                        .standaloneHost(redis.getHost())
                        .standalonePort(redis.getFirstMappedPort())
                        .build())
                .build();

        CacheKeeperConfiguration<String, String> config = CacheKeeperConfiguration.Builder.newBuilder()
                .cacheExpireTime(10L)
                .cacheExpireTimeUnit(TimeUnit.SECONDS)
                .cachePenetrationStrategy(CachePenetrationStrategy.WRITE_NULL_SOME_TIME)
                .missCacheNullValueTimeMs(10_000L)
                .cacheOperator(cacheOperator)
                .build();
        CacheKeeper<String, String> cacheKeeper = CacheKeeper.Builder.newBuilder()
                .config(config)
                .build();
        AtomicInteger reloadCacheCount = new AtomicInteger(0);
        String value = cacheKeeper.readCache("testKey", key -> {
            reloadCacheCount.incrementAndGet();
            return null;
        });
        try {
            Thread.sleep(Duration.ofMillis(1000));
        } catch (InterruptedException e) {
            // ignore
        }
        cacheKeeper.readCache("testKey", key -> {
            reloadCacheCount.incrementAndGet();
            return null;
        });
        assert value == null;
        assert reloadCacheCount.get() == 1;
    }

    @Test
    void readWriteCurrently() {
        RedisCacheOperator<String, String> cacheOperator = RedisCacheOperator.Builder.newBuilder()
                .redisClientType(RedisCacheOperator.RedisClientType.JEDIS)
                .redisConfiguration(JedisConfiguration.Builder.newBuilder()
                        .connectionMode(JedisConfiguration.ConnectionMode.STANDALONE)
                        .standaloneHost(redis.getHost())
                        .standalonePort(redis.getFirstMappedPort())
                        .build())
                .build();

        CacheKeeperConfiguration<String, String> config = CacheKeeperConfiguration.Builder.newBuilder()
                .cacheExpireTime(10L)
                .cacheExpireTimeUnit(TimeUnit.SECONDS)
                .cachePenetrationStrategy(CachePenetrationStrategy.WRITE_NULL_SOME_TIME)
                .missCacheNullValueTimeMs(10_000L)
                .cacheOperator(cacheOperator)
                .build();
        CacheKeeper<String, String> cacheKeeper = CacheKeeper.Builder.newBuilder()
                .config(config)
                .build();
        // read cache
        AtomicInteger atomicInteger = new AtomicInteger(0);
        cacheKeeper.readCache("testKey", key -> String.valueOf(atomicInteger.incrementAndGet()));
        // update db to 2
        atomicInteger.incrementAndGet();
        // delete cache
        cacheKeeper.deleteCache("testKey");
        // read the cache again
        cacheKeeper.readCache("testKey", key -> {
            // sleep to wait the cache delete
            try {
                Thread.sleep(Duration.ofSeconds(1));
            } catch (InterruptedException e) {
                // ignore
            }
            return "old value";
        });
        atomicInteger.incrementAndGet();
        cacheKeeper.deleteCache("testKey");

        String testKey = cacheKeeper.readCache("testKey", key -> "testValue");
        assert Objects.equals(testKey, "testValue");
    }

}
