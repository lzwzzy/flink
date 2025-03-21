/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.rest.handler.job.metrics;

import org.apache.flink.runtime.metrics.dump.QueryScopeInfo;

import java.util.Collections;
import java.util.Map;

/** Tests for {@link JobManagerMetricsHandler}. */
class JobManagerMetricsHandlerTestBase extends MetricsHandlerTestBase<JobManagerMetricsHandler> {

    @Override
    JobManagerMetricsHandler getMetricsHandler() {
        return new JobManagerMetricsHandler(
                leaderRetriever, TIMEOUT, TEST_HEADERS, mockMetricFetcher);
    }

    @Override
    QueryScopeInfo getQueryScopeInfo() {
        return new QueryScopeInfo.JobManagerQueryScopeInfo();
    }

    @Override
    Map<String, String> getPathParameters() {
        return Collections.emptyMap();
    }
}
