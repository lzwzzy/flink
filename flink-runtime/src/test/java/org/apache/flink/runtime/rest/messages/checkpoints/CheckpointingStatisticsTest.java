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

package org.apache.flink.runtime.rest.messages.checkpoints;

import org.apache.flink.core.execution.SavepointFormatType;
import org.apache.flink.runtime.checkpoint.CheckpointStatsStatus;
import org.apache.flink.runtime.checkpoint.CheckpointType;
import org.apache.flink.runtime.checkpoint.SavepointType;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.rest.messages.RestResponseMarshallingTestBase;
import org.apache.flink.runtime.rest.messages.checkpoints.CheckpointStatistics.RestAPICheckpointType;
import org.apache.flink.testutils.junit.extensions.parameterized.NoOpTestExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Tests for {@link CheckpointingStatistics}. */
@ExtendWith(NoOpTestExtension.class)
class CheckpointingStatisticsTest extends RestResponseMarshallingTestBase<CheckpointingStatistics> {
    @Override
    protected Class<CheckpointingStatistics> getTestResponseClass() {
        return CheckpointingStatistics.class;
    }

    @Override
    protected CheckpointingStatistics getTestResponseInstance() throws Exception {

        final CheckpointingStatistics.Counts counts =
                new CheckpointingStatistics.Counts(1, 2, 3, 4, 5);
        final CheckpointingStatistics.Summary summary =
                new CheckpointingStatistics.Summary(
                        new StatsSummaryDto(1L, 1L, 1L, 0, 0, 0, 0, 0),
                        new StatsSummaryDto(1L, 1L, 1L, 0, 0, 0, 0, 0),
                        new StatsSummaryDto(2L, 2L, 2L, 0, 0, 0, 0, 0),
                        new StatsSummaryDto(3L, 3L, 3L, 0, 0, 0, 0, 0),
                        new StatsSummaryDto(4L, 4L, 4L, 0, 0, 0, 0, 0),
                        new StatsSummaryDto(5L, 5L, 5L, 0, 0, 0, 0, 0));

        final Map<JobVertexID, TaskCheckpointStatistics> checkpointStatisticsPerTask =
                new HashMap<>(2);

        checkpointStatisticsPerTask.put(
                new JobVertexID(),
                new TaskCheckpointStatistics(
                        1L, CheckpointStatsStatus.COMPLETED, 1L, 2L, 2L, 3L, 4L, 7, 8, 5, 6));

        checkpointStatisticsPerTask.put(
                new JobVertexID(),
                new TaskCheckpointStatistics(
                        1L, CheckpointStatsStatus.COMPLETED, 2L, 3L, 3L, 4L, 5L, 8, 9, 6, 7));

        final CheckpointStatistics.CompletedCheckpointStatistics completed =
                new CheckpointStatistics.CompletedCheckpointStatistics(
                        1L,
                        CheckpointStatsStatus.COMPLETED,
                        false,
                        null,
                        42L,
                        41L,
                        1337L,
                        1337L,
                        1L,
                        0L,
                        43L,
                        44L,
                        10,
                        10,
                        RestAPICheckpointType.valueOf(CheckpointType.CHECKPOINT, true),
                        Collections.emptyMap(),
                        null,
                        false);

        final CheckpointStatistics.CompletedCheckpointStatistics savepoint =
                new CheckpointStatistics.CompletedCheckpointStatistics(
                        2L,
                        CheckpointStatsStatus.COMPLETED,
                        true,
                        SavepointFormatType.CANONICAL.name(),
                        11L,
                        10L,
                        43L,
                        43L,
                        1L,
                        0L,
                        31337L,
                        0L,
                        9,
                        9,
                        RestAPICheckpointType.valueOf(
                                SavepointType.savepoint(SavepointFormatType.CANONICAL), false),
                        checkpointStatisticsPerTask,
                        "externalPath",
                        false);

        final CheckpointStatistics.FailedCheckpointStatistics failed =
                new CheckpointStatistics.FailedCheckpointStatistics(
                        3L,
                        CheckpointStatsStatus.FAILED,
                        false,
                        null,
                        5L,
                        10L,
                        4L,
                        4L,
                        2L,
                        0L,
                        21L,
                        22L,
                        11,
                        9,
                        RestAPICheckpointType.valueOf(CheckpointType.CHECKPOINT, true),
                        Collections.emptyMap(),
                        100L,
                        "Test failure");

        CheckpointingStatistics.RestoredCheckpointStatistics restored =
                new CheckpointingStatistics.RestoredCheckpointStatistics(4L, 1445L, true, "foobar");

        CheckpointStatistics.PendingCheckpointStatistics pending =
                new CheckpointStatistics.PendingCheckpointStatistics(
                        5L,
                        CheckpointStatsStatus.IN_PROGRESS,
                        false,
                        null,
                        42L,
                        41L,
                        1337L,
                        1337L,
                        1L,
                        0L,
                        15L,
                        16L,
                        10,
                        10,
                        RestAPICheckpointType.valueOf(CheckpointType.CHECKPOINT, true),
                        Collections.emptyMap());

        final CheckpointingStatistics.LatestCheckpoints latestCheckpoints =
                new CheckpointingStatistics.LatestCheckpoints(
                        completed, savepoint, failed, restored);

        return new CheckpointingStatistics(
                counts,
                summary,
                latestCheckpoints,
                Arrays.asList(completed, savepoint, failed, pending));
    }

    @Test
    void testRestAPICheckpointType() {
        // Test for aligned checkpoint
        assertThat(RestAPICheckpointType.valueOf(CheckpointType.CHECKPOINT, false))
                .isEqualTo(RestAPICheckpointType.CHECKPOINT);
        assertThat(RestAPICheckpointType.valueOf(CheckpointType.FULL_CHECKPOINT, false))
                .isEqualTo(RestAPICheckpointType.CHECKPOINT);

        // Test for unaligned checkpoint
        assertThat(RestAPICheckpointType.valueOf(CheckpointType.CHECKPOINT, true))
                .isEqualTo(RestAPICheckpointType.UNALIGNED_CHECKPOINT);
        assertThat(RestAPICheckpointType.valueOf(CheckpointType.FULL_CHECKPOINT, true))
                .isEqualTo(RestAPICheckpointType.UNALIGNED_CHECKPOINT);

        // Test for savepoint
        assertThat(
                        RestAPICheckpointType.valueOf(
                                SavepointType.savepoint(SavepointFormatType.NATIVE), false))
                .isEqualTo(RestAPICheckpointType.SAVEPOINT);
        assertThat(
                        RestAPICheckpointType.valueOf(
                                SavepointType.suspend(SavepointFormatType.NATIVE), false))
                .isEqualTo(RestAPICheckpointType.SYNC_SAVEPOINT);
        assertThat(
                        RestAPICheckpointType.valueOf(
                                SavepointType.terminate(SavepointFormatType.NATIVE), false))
                .isEqualTo(RestAPICheckpointType.SYNC_SAVEPOINT);

        assertThatThrownBy(
                        () ->
                                RestAPICheckpointType.valueOf(
                                        SavepointType.terminate(SavepointFormatType.NATIVE), true))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
