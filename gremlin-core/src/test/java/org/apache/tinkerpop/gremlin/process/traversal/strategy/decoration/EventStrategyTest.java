/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process.traversal.strategy.decoration;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.DefaultGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.Mutating;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.event.ConsoleMutationListener;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.event.MutationListener;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
@RunWith(Parameterized.class)
public class EventStrategyTest {
    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"addInE()", new DefaultGraphTraversal<>(EmptyGraph.instance()).addInE("test", "x"), 1},
                {"addInE(args)", new DefaultGraphTraversal<>(EmptyGraph.instance()).addInE("test", "x", "this", "that"), 1},
                {"addOutE()", new DefaultGraphTraversal<>(EmptyGraph.instance()).addOutE("test", "x"), 1},
                {"addOutE(args)", new DefaultGraphTraversal<>(EmptyGraph.instance()).addOutE("test", "x", "this", "that"), 1},
                {"addE(IN)", new DefaultGraphTraversal<>(EmptyGraph.instance()).addE(Direction.IN, "test", "test"), 1},
                {"addE(IN,args)", new DefaultGraphTraversal<>(EmptyGraph.instance()).addE(Direction.IN, "test", "test", "this", "that"), 1},
                {"addE(OUT)", new DefaultGraphTraversal<>(EmptyGraph.instance()).addE(Direction.OUT, "test", "test"), 1},
                {"addE(OUT,args)", new DefaultGraphTraversal<>(EmptyGraph.instance()).addE(Direction.OUT, "test", "test", "this", "that"), 1},
                {"addV()", new DefaultGraphTraversal<>(EmptyGraph.instance()).addV(), 1},
                {"addV(args)", new DefaultGraphTraversal<>(EmptyGraph.instance()).addV("test", "this"), 1},
                {"addV().property(k,v)", new DefaultGraphTraversal<>(EmptyGraph.instance()).addV().property("test", "that"), 2},
                {"properties().drop()", new DefaultGraphTraversal<>(EmptyGraph.instance()).properties().drop(), 1},
                {"properties(k).drop()", new DefaultGraphTraversal<>(EmptyGraph.instance()).properties("test").drop(), 1},
                {"out().drop()", new DefaultGraphTraversal<>(EmptyGraph.instance()).out().drop(), 1},
                {"out(args).drop()", new DefaultGraphTraversal<>(EmptyGraph.instance()).out("test").drop(), 1},
                {"outE().drop()", new DefaultGraphTraversal<>(EmptyGraph.instance()).outE().drop(), 1},
                {"outE().properties().drop()", new DefaultGraphTraversal<>(EmptyGraph.instance()).outE().properties().drop(), 1},
                {"outE(args).drop()", new DefaultGraphTraversal<>(EmptyGraph.instance()).outE("test").drop(), 1}});
    }

    @Parameterized.Parameter(value = 0)
    public String name;

    @Parameterized.Parameter(value = 1)
    public Traversal traversal;

    @Parameterized.Parameter(value = 2)
    public int expectedMutatingStepsFound;

    @Test
    public void shouldEventOnMutatingSteps() {
        final MutationListener listener1 = new ConsoleMutationListener(EmptyGraph.instance());
        final EventStrategy eventStrategy = EventStrategy.build()
                .addListener(listener1).create();

        eventStrategy.apply(traversal.asAdmin());

        final AtomicInteger mutatingStepsFound = new AtomicInteger(0);
        traversal.asAdmin().getSteps().stream()
                .filter(s -> s instanceof Mutating)
                .forEach(s -> {
                    final Mutating mutating = (Mutating) s;
                    assertEquals(1, mutating.getMutatingCallbackRegistry().getCallbacks().size());
                    mutatingStepsFound.incrementAndGet();
                });

        assertEquals(expectedMutatingStepsFound, mutatingStepsFound.get());
    }
}
