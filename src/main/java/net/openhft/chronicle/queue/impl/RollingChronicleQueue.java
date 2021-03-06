/*
 * Copyright 2016 higherfrequencytrading.com
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
package net.openhft.chronicle.queue.impl;

import net.openhft.chronicle.core.annotation.Nullable;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.RollCycle;
import net.openhft.chronicle.queue.TailerDirection;
import net.openhft.chronicle.queue.impl.single.StoreRecovery;
import net.openhft.chronicle.wire.WireType;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.function.Function;

public interface RollingChronicleQueue extends ChronicleQueue {

    long epoch();

    /**
     * @param cycle          the cycle
     * @param epoch          an epoch offset as the number of number of milliseconds since January
     *                       1, 1970, 00:00:00 GMT
     * @param createIfAbsent create missing stores if true, or return null if missing
     * @return the {@code WireStore} associated with this {@code cycle}, or null if !createIfAbsent
     * is false and absent
     */
    @Nullable
    WireStore storeForCycle(int cycle, final long epoch, boolean createIfAbsent);

    /**
     * @param store the {@code store} to release
     */
    void release(@NotNull WireStore store);

    /**
     * @return the first cycle number found, or Integer.MAX_VALUE is none found.
     */
    int firstCycle();

    /**
     * @return the lastCycle available or Integer.MIN_VALUE if none is found.
     */
    int lastCycle();

    /**
     * the next available cycle, no cycle will be created by this method, this method is typically
     * used by a tailer to jump to the next cycle when the cycles are not adjacent.
     *
     * @param currentCycle the current cycle
     * @param direction    the direction
     * @return the next available cycle from the current cycle, or -1 if there is no next cycle
     */
    int nextCycle(int currentCycle, @NotNull TailerDirection direction) throws ParseException;

    /**
     * The number of excerpts between the indexes, {@code index1} inclusive, {@code index2}
     * exclusive.
     *
     * When {@code index1} and {@code index2} are in different cycles which are not adjacent, this
     * operation can be expensive, as the index count for each intermediate cycle has to be found
     * and calculated. As such, and in this situation, it's not recommended to call this method
     * regularly in latency sensitive systems.
     *
     * @param index1 from index, the index provided must exist.  To improve performance no checking
     *               is  carried out to validate if an excerpt exists at this index.
     * @param index2 to index, the index provided must exist. To improve performance no checking is
     *               carried out to validate if an excerpt exists at this index.
     * @return the number of excerpts between the indexes, {@code index1} inclusive, {@code index2}
     * exclusive.
     * @throws java.lang.IllegalStateException if the cycle of {@code index1} or {@code index2} can
     *                                         not be ascertained
     */
    long countExcerpts(long index1, long index2) throws java.lang.IllegalStateException;

    /**
     * @return the current cycle
     */
    int cycle();

    /**
     * @return the number of entries in an index meta data entry
     */
    int indexCount();

    /**
     * @return the spacing between indexed entries. If 1 then every entry is indexed.
     */
    int indexSpacing();

    RollCycle rollCycle();

    Function<WireType, StoreRecovery> recoverySupplier();

}
