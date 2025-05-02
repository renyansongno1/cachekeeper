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

    @Override
    public V readCache(K key) {
        return null;
    }

    @Override
    public void writeCache(K key, V value, String leaseId, Long expireTime, TimeUnit expireTimeUnit) {

    }

    @Override
    public void saveLeaseId(String leaseId) {

    }

    @Override
    public void deleteCacheAndClearAllLeaseId(K key) {

    }
}
