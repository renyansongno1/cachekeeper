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

package org.cache.keeper.core.cacheoperator;

import java.util.concurrent.TimeUnit;

/**
 * cache operator
 */
public interface ICacheOperator<K, V> {

    /**
     * read cache by key
     * @param key cache key
     * @return cache value nullable
     */
    V readCache(K key);

    /**
     * write cache
     * this method must valid the lease, if the lease is not exist, do not write the expire cache to db
     * @param key cache key
     * @param value cache value
     * @param leaseId lease id
     * @param expireTime expire time
     * @param expireTimeUnit expire time unit
     */
    void writeCache(K key, V value, String leaseId, Long expireTime, TimeUnit expireTimeUnit);

    /**
     * save lease id
     * @param leaseId lease id
     */
    void saveLeaseId(String leaseId);

    /**
     * clear all lease id when cache delete
     */
    void deleteCacheAndClearAllLeaseId(K key);

}
