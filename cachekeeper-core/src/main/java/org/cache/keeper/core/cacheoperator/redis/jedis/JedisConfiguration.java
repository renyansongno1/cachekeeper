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

import org.cache.keeper.core.cacheoperator.redis.RedisConfiguration;

public class JedisConfiguration implements RedisConfiguration {

    private JedisConfiguration(){}

    private ConnectionMode connectionMode;

    private String standaloneHost = "127.0.0.1";

    private Integer standalonePort = 6379;

    private String standalonePassword;

    private Integer standaloneDatabase = 0;

    private Integer standaloneTimeout = 2000;

    private Integer standaloneMaxTotal = 8;

    private Integer standaloneMaxIdle = 8;

    private String sentinelMasterName;

    private String[] sentinelHosts;

    private String sentinelPassword;

    private Integer sentinelTimeout = 2000;

    private Integer sentinelMaxTotal = 8;

    private Integer sentinelMaxIdle = 8;

    private String[] clusterNodes;

    private Integer clusterTimeout = 2000;

    private Integer clusterMaxAttempts = 5;

    private Integer clusterMaxTotal = 8;

    private Integer clusterMaxIdle = 8;

    // getter start
    public ConnectionMode getConnectionMode() {
        return connectionMode;
    }

    public String getStandaloneHost() {
        return standaloneHost;
    }

    public Integer getStandalonePort() {
        return standalonePort;
    }

    public String getStandalonePassword() {
        return standalonePassword;
    }

    public Integer getStandaloneDatabase() {
        return standaloneDatabase;
    }

    public Integer getStandaloneTimeout() {
        return standaloneTimeout;
    }

    public Integer getStandaloneMaxTotal() {
        return standaloneMaxTotal;
    }

    public Integer getStandaloneMaxIdle() {
        return standaloneMaxIdle;
    }

    public String getSentinelMasterName() {
        return sentinelMasterName;
    }

    public String[] getSentinelHosts() {
        return sentinelHosts;
    }

    public String getSentinelPassword() {
        return sentinelPassword;
    }

    public Integer getSentinelTimeout() {
        return sentinelTimeout;
    }

    public Integer getSentinelMaxTotal() {
        return sentinelMaxTotal;
    }

    public Integer getSentinelMaxIdle() {
        return sentinelMaxIdle;
    }

    public String[] getClusterNodes() {
        return clusterNodes;
    }

    public Integer getClusterTimeout() {
        return clusterTimeout;
    }

    public Integer getClusterMaxAttempts() {
        return clusterMaxAttempts;
    }

    public Integer getClusterMaxTotal() {
        return clusterMaxTotal;
    }

    public Integer getClusterMaxIdle() {
        return clusterMaxIdle;
    }

    // getter end


    public static class Builder {
        private ConnectionMode connectionMode;

        private String standaloneHost = "127.0.0.1";

        private Integer standalonePort = 6379;

        private String standalonePassword;

        private Integer standaloneDatabase = 0;

        private Integer standaloneTimeout = 2000;

        private Integer standaloneMaxTotal = 8;

        private Integer standaloneMaxIdle = 8;

        private String sentinelMasterName;

        private String[] sentinelHosts;

        private String sentinelPassword;

        private Integer sentinelTimeout = 2000;

        private Integer sentinelMaxTotal = 8;

        private Integer sentinelMaxIdle = 8;

        private String[] clusterNodes;

        private Integer clusterTimeout = 2000;

        private  Integer clusterMaxAttempts = 5;

        private Integer clusterMaxTotal = 8;

        private Integer clusterMaxIdle = 8;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder connectionMode(ConnectionMode connectionMode) {
            this.connectionMode = connectionMode;
            return this;
        }

        public Builder standaloneHost(String standaloneHost) {
            this.standaloneHost = standaloneHost;
            return this;
        }

        public Builder standalonePort(Integer standalonePort) {
            this.standalonePort = standalonePort;
            return this;
        }

        public Builder standalonePassword(String standalonePassword) {
            this.standalonePassword = standalonePassword;
            return this;
        }

        public Builder standaloneDatabase(Integer standaloneDatabase) {
            this.standaloneDatabase = standaloneDatabase;
            return this;
        }

        public Builder standaloneTimeout(Integer standaloneTimeout) {
            this.standaloneTimeout = standaloneTimeout;
            return this;
        }

        public Builder standaloneMaxTotal(Integer standaloneMaxTotal) {
            this.standaloneMaxTotal = standaloneMaxTotal;
            return this;
        }

        public Builder standaloneMaxIdle(Integer standaloneMaxIdle) {
            this.standaloneMaxIdle = standaloneMaxIdle;
            return this;
        }

        public Builder sentinelMasterName(String sentinelMasterName) {
            this.sentinelMasterName = sentinelMasterName;
            return this;
        }

        public Builder sentinelHosts(String[] sentinelHosts) {
            this.sentinelHosts = sentinelHosts;
            return this;
        }

        public Builder sentinelPassword(String sentinelPassword) {
            this.sentinelPassword = sentinelPassword;
            return this;
        }

        public Builder sentinelTimeout(Integer sentinelTimeout) {
            this.sentinelTimeout = sentinelTimeout;
            return this;
        }

        public Builder sentinelMaxTotal(Integer sentinelMaxTotal) {
            this.sentinelMaxTotal = sentinelMaxTotal;
            return this;
        }

        public Builder sentinelMaxIdle(Integer sentinelMaxIdle) {
            this.sentinelMaxIdle = sentinelMaxIdle;
            return this;
        }

        public Builder clusterNodes(String[] clusterNodes) {
            this.clusterNodes = clusterNodes;
            return this;
        }

        public Builder clusterTimeout(Integer clusterTimeout) {
            this.clusterTimeout = clusterTimeout;
            return this;
        }

        public Builder clusterMaxAttempts(Integer clusterMaxAttempts) {
            this.clusterMaxAttempts = clusterMaxAttempts;
            return this;
        }

        public Builder clusterMaxTotal(Integer clusterMaxTotal) {
            this.clusterMaxTotal = clusterMaxTotal;
            return this;
        }

        public Builder clusterMaxIdle(Integer clusterMaxIdle) {
            this.clusterMaxIdle = clusterMaxIdle;
            return this;
        }

        public JedisConfiguration build() {
            JedisConfiguration configuration = new JedisConfiguration();
            configuration.connectionMode = connectionMode;
            configuration.standaloneHost = standaloneHost;
            configuration.standalonePort = standalonePort;
            configuration.standalonePassword = standalonePassword;
            configuration.standaloneDatabase = standaloneDatabase;
            configuration.standaloneTimeout = standaloneTimeout;
            configuration.standaloneMaxTotal = standaloneMaxTotal;
            configuration.standaloneMaxIdle = standaloneMaxIdle;
            configuration.sentinelMasterName = sentinelMasterName;
            configuration.sentinelHosts = sentinelHosts;
            configuration.sentinelPassword = sentinelPassword;
            configuration.sentinelTimeout = sentinelTimeout;
            configuration.sentinelMaxTotal = sentinelMaxTotal;
            configuration.sentinelMaxIdle = sentinelMaxIdle;
            configuration.clusterNodes = clusterNodes;
            configuration.clusterTimeout = clusterTimeout;
            configuration.clusterMaxAttempts = clusterMaxAttempts;
            configuration.clusterMaxTotal = clusterMaxTotal;
            configuration.clusterMaxIdle = clusterMaxIdle;
            return configuration;
        }
    }

    public static enum ConnectionMode {
        STANDALONE, CLUSTER, SENTINEL
    }

}
