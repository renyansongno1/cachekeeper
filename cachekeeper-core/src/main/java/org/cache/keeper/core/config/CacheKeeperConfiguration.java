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

package org.cache.keeper.core.config;

import org.cache.keeper.core.cacheoperator.ICacheOperator;

import java.util.concurrent.TimeUnit;

/**
 * config for cache keeper
 */
public class CacheKeeperConfiguration<K, V> {

    /**
     * cache operator
     */
    private ICacheOperator<K, V> cacheOperator;

    /**
     * cache miss and db miss strategy
     */
    private CachePenetrationStrategy cachePenetrationStrategy = CachePenetrationStrategy.NONE;

    /**
     * cache miss and db miss strategy time
     */
    private Long missCacheNullValueTimeMs;

    /**
     * cache expire time
     * in the cache keeper, all cache must have expired time
     */
    private Long cacheExpireTime;

    /**
     * cache expire time unit
     */
    private TimeUnit cacheExpireTimeUnit;

    // getter start
    private CacheKeeperConfiguration() {
    }

    public ICacheOperator<K, V> getCacheOperator() {
        return cacheOperator;
    }

    public CachePenetrationStrategy getCachePenetrationStrategy() {
        return cachePenetrationStrategy;
    }

    public Long getMissCacheNullValueTimeMs() {
        return missCacheNullValueTimeMs;
    }

    public Long getCacheExpireTime() {
        return cacheExpireTime;
    }

    public TimeUnit getCacheExpireTimeUnit() {
        return cacheExpireTimeUnit;
    }
    // getter end

    /**
     * check config
     */
    private void checkConfig() {
        if (this.cachePenetrationStrategy == CachePenetrationStrategy.WRITE_NULL_SOME_TIME
                && this.missCacheNullValueTimeMs == null) {
            throw new IllegalArgumentException("missCacheNullValueTimeMs must be set when cachePenetrationStrategy is WRITE_NULL_SOME_TIME");
        }
        if (this.cacheExpireTime == null) {
            throw new IllegalArgumentException("cacheExpireTime must be set");
        }
        if (this.cacheExpireTimeUnit == null) {
            throw new IllegalArgumentException("cacheExpireTimeUnit must be set");
        }
    }

    public static class Builder<K, V> {
        private ICacheOperator<K, V> cacheOperator;
        private CachePenetrationStrategy cachePenetrationStrategy;
        private Long missCacheNullValueTimeMs;
        private Long cacheExpireTime;
        private TimeUnit cacheExpireTimeUnit;

        private Builder() {
        }

        public static <K, V> Builder<K, V> newBuilder() {
            return new Builder<>();
        }

        public Builder<K, V> cacheOperator(ICacheOperator<K, V> cacheOperator) {
            this.cacheOperator = cacheOperator;
            return this;
        }

        public Builder<K, V> cachePenetrationStrategy(CachePenetrationStrategy cachePenetrationStrategy) {
            this.cachePenetrationStrategy = cachePenetrationStrategy;
            return this;
        }

        public Builder<K, V> missCacheNullValueTimeMs(Long missCacheNullValueTimeMs) {
            this.missCacheNullValueTimeMs = missCacheNullValueTimeMs;
            return this;
        }

        public Builder<K, V> cacheExpireTime(Long cacheExpireTime) {
            this.cacheExpireTime = cacheExpireTime;
            return this;
        }

        public Builder<K, V> cacheExpireTimeUnit(TimeUnit cacheExpireTimeUnit) {
            this.cacheExpireTimeUnit = cacheExpireTimeUnit;
            return this;
        }

        public CacheKeeperConfiguration<K, V> build() {
            CacheKeeperConfiguration<K, V> config = new CacheKeeperConfiguration<>();
            config.cacheOperator = this.cacheOperator;
            config.cachePenetrationStrategy = cachePenetrationStrategy;
            config.missCacheNullValueTimeMs = missCacheNullValueTimeMs;
            config.cacheExpireTime = cacheExpireTime;
            config.cacheExpireTimeUnit = cacheExpireTimeUnit;
            config.checkConfig();
            return config;
        }
    }

}
