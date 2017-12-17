/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper GmbH licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.util;

import com.graphhopper.routing.ch.PrepareEncoder;
import com.graphhopper.storage.CHGraph;

/**
 * The state returned from the EdgeIterator of a CHGraph
 * <p>
 *
 * @author Peter Karich
 * @see CHGraph
 * @see CHEdgeIterator
 */
public interface CHEdgeIteratorState extends EdgeIteratorState {
    int getSkippedEdge1();

    int getSkippedEdge2();

    /**
     * Sets the edges that this shortcut skips. Those skipped edges can be shortcuts too.
     */
    CHEdgeIteratorState setSkippedEdges(int edge1, int edge2);

    /**
     * Sets the first and last original edges that are skipped by this shortcut. For example the following edge is a
     * shortcut from x to y that skips the edges x->v and v->y (which themselves may be be shortcuts):
     * x --> u --> v --> w --> y
     * \--------->/ \-------->/
     *
     * @param firstOrigEdge the first original edge that is bridged by this shortcut, x->u in the above example
     * @param lastOrigEdge  the last original edge that is bridged by this shortcut, w->y in the above example
     */
    CHEdgeIteratorState setOuterOrigEdges(int firstOrigEdge, int lastOrigEdge);

    /**
     * @return true if this edge is a shortcut, false otherwise.
     */
    boolean isShortcut();

    /**
     * This method is only used on preparation.
     *
     * @see PrepareEncoder#getScMergeStatus(long, long)
     */
    int getMergeStatus(long flags);

    /**
     * Returns the weight of this shortcut.
     */
    double getWeight();

    /**
     * Sets the weight calculated from Weighting.calcWeight, only applicable if isShortcut is true.
     */
    CHEdgeIteratorState setWeight(double weight);
}
