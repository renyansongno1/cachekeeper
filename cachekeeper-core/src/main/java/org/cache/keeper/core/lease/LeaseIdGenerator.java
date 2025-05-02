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

package org.cache.keeper.core.lease;

import java.util.UUID;

/**
 * lease id generator
 */
public class LeaseIdGenerator {

    /**
     * static class single instance
     */
    public static LeaseIdGenerator INSTANCE = new LeaseIdGenerator();

    private LeaseIdGenerator(){}

    public static LeaseIdGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * generate one lease id
     * @return id
     */
    public String generateLeaseId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
