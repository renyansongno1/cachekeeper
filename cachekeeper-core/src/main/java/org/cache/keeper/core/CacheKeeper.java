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

package org.cache.keeper.core;

import org.cache.keeper.core.config.CacheKeeperConfiguration;
import org.cache.keeper.core.lease.LeaseIdGenerator;

import java.util.concurrent.TimeUnit;

/**
 * the core class
 */
public class CacheKeeper<K, V> {

    @SuppressWarnings("rawtypes")
    public static CacheKeeper INSTANCE = new CacheKeeper();

    private CacheKeeper() {}

    private CacheKeeperConfiguration<K, V> config;

    public static class Builder<K, V> {
        public Builder<K, V> config(CacheKeeperConfiguration<K,V> config) {
            INSTANCE.config = config;
            return this;
        }

        @SuppressWarnings("rawtypes")
        public CacheKeeper build() {
            if (INSTANCE.config == null) {
                throw new RuntimeException("cache keeper config is null");
            }
            INSTANCE.validConfig();
            return INSTANCE;
        }
    }

    private void validConfig() {
        if (this.config == null) {
            throw new RuntimeException("cache keeper config is null");
        }
        if (this.config.getCacheOperator() == null) {
            throw new RuntimeException("cache keeper config cache operator is null");
        }
    }

    /**
     * read the cache, if the cache is not exist, reload it
     * and use the lease to keep the cache and db currently
     * @param key cache key
     * @param cacheReloader customer reloader
     * @return cache value
     */
    public V readCache(K key, ICacheReloader<K, V> cacheReloader) {
        // first use the cache operator to read the cache
        V cache = config.getCacheOperator().readCache(key);
        if (cache != null) {
            return cache;
        }
        // distribute lease id
        String leaseId = LeaseIdGenerator.getInstance().generateLeaseId();
        // write the lease to cache
        config.getCacheOperator().saveLeaseId(leaseId);
        // reload cache
        V reloadedCache = cacheReloader.reload(key);
        if (reloadedCache == null) {
            switch (config.getCachePenetrationStrategy()) {
                case NONE -> {
                    return null;
                }
                case WRITE_NULL_SOME_TIME -> {
                    config.getCacheOperator().writeCache(key, null, leaseId, config.getMissCacheNullValueTimeMs(), TimeUnit.MILLISECONDS);
                    return null;
                }
                default -> throw new IllegalStateException("Unexpected value: " + config.getCachePenetrationStrategy());
            }
        }
        config.getCacheOperator().writeCache(key, reloadedCache, leaseId, config.getCacheExpireTime(), config.getCacheExpireTimeUnit());
        return reloadedCache;
    }


}
