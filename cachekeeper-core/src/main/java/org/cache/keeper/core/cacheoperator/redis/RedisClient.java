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

public interface RedisClient<K, V> {

    /**
     * init redis client
     * the client must init by config
     * @param redisConfiguration redis client config
     */
    void initClient(RedisConfiguration redisConfiguration);

    /**
     * get value by key
     * @param key key
     * @return value
     */
    V get(K key);

    /**
     * normal set
     * @param key key
     * @param value value
     */
    void set(K key, V value);

    /**
     * load lua script
     * @param luaScript lua script
     * @return sha
     */
    String loadLuaScript(String luaScript);

    /**
     * execute the lua script
     * @param luaScript lua script
     * @param keys keys
     * @param args args
     */
    void evalLua(String luaScript, String[] keys, String[] args);

    /**
     * execute the lua sha
     * @param luaSha lua sha
     * @param keys keys
     * @param args args
     */
    void evalSha(String luaSha, String[] keys, String[] args);

    /**
     * push data to queue
     * @param queueName key name
     * @param value value
     */
    void lPush(String queueName, String value);

    /**
     * delete data by key
     * @param key key
     */
    void delete(String key);
}
