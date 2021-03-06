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
package org.apache.tinkerpop.gremlin.process.traversal.step.filter;

import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.process.AbstractGremlinProcessTest;
import org.apache.tinkerpop.gremlin.process.GremlinProcessRunner;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.tinkerpop.gremlin.LoadGraphWith.GraphData.MODERN;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.bothE;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.dedup;
import static org.junit.Assert.*;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Stephen Mallette (http://stephen.genoprime.com)
 * @author Daniel Kuppitz (http://gremlin.guru)
 */
@RunWith(GremlinProcessRunner.class)
public abstract class DedupTest extends AbstractGremlinProcessTest {

    public abstract Traversal<Vertex, String> get_g_V_both_dedup_name();

    public abstract Traversal<Vertex, String> get_g_V_both_hasXlabel_softwareX_dedup_byXlangX_name();

    public abstract Traversal<Vertex, String> get_g_V_both_name_order_byXa_bX_dedup_value();

    public abstract Traversal<Vertex, String> get_g_V_both_both_name_dedup();

    public abstract Traversal<Vertex, Vertex> get_g_V_both_both_dedup();

    public abstract Traversal<Vertex, Vertex> get_g_V_both_both_dedup_byXlabelX();

    public abstract Traversal<Vertex, Map<String, Set<Double>>> get_g_V_group_byXlabelX_byXbothE_valuesXweightX_foldX_byXdedupXlocalXX();

    public abstract Traversal<Vertex, Map<String, Vertex>> get_g_V_asXaX_both_asXbX_dedupXa_bX_byXlabelX_selectXa_bX();

    public abstract Traversal<Vertex, Path> get_g_V_asXaX_outXcreatedX_asXbX_inXcreatedX_asXcX_dedupXa_bX_path();

    @Test
    @LoadGraphWith(MODERN)
    public void g_V_both_dedup_name() {
        final Traversal<Vertex, String> traversal = get_g_V_both_dedup_name();
        printTraversalForm(traversal);
        final List<String> names = traversal.toList();
        assertEquals(6, names.size());
        assertTrue(names.contains("marko"));
        assertTrue(names.contains("vadas"));
        assertTrue(names.contains("lop"));
        assertTrue(names.contains("josh"));
        assertTrue(names.contains("ripple"));
        assertTrue(names.contains("peter"));
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(MODERN)
    public void g_V_both_hasXlabel_softwareX_dedup_byXlangX_name() {
        final Traversal<Vertex, String> traversal = get_g_V_both_hasXlabel_softwareX_dedup_byXlangX_name();
        printTraversalForm(traversal);
        final List<String> names = traversal.toList();
        assertEquals(1, names.size());
        assertTrue(names.contains("lop") || names.contains("ripple"));
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(MODERN)
    public void g_V_both_name_order_byXa_bX_dedup_value() {
        final Traversal<Vertex, String> traversal = get_g_V_both_name_order_byXa_bX_dedup_value();
        printTraversalForm(traversal);
        final List<String> names = traversal.toList();
        assertEquals(6, names.size());
        assertEquals("josh", names.get(0));
        assertEquals("lop", names.get(1));
        assertEquals("marko", names.get(2));
        assertEquals("peter", names.get(3));
        assertEquals("ripple", names.get(4));
        assertEquals("vadas", names.get(5));
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(MODERN)
    public void g_V_both_both_name_dedup() {
        final Traversal<Vertex, String> traversal = get_g_V_both_both_name_dedup();
        printTraversalForm(traversal);
        checkResults(Arrays.asList("marko", "vadas", "josh", "peter", "lop", "ripple"), traversal);
    }

    @Test
    @LoadGraphWith(MODERN)
    public void g_V_both_both_dedup() {
        final Traversal<Vertex, Vertex> traversal = get_g_V_both_both_dedup();
        printTraversalForm(traversal);
        checkResults(Arrays.asList(
                convertToVertex(graph, "marko"),
                convertToVertex(graph, "vadas"),
                convertToVertex(graph, "josh"),
                convertToVertex(graph, "peter"),
                convertToVertex(graph, "lop"),
                convertToVertex(graph, "ripple")), traversal);
    }

    @Test
    @LoadGraphWith(MODERN)
    public void g_V_both_both_dedup_byXlabelX() {
        final Traversal<Vertex, Vertex> traversal = get_g_V_both_both_dedup_byXlabelX();
        printTraversalForm(traversal);
        final List<Vertex> vertices = traversal.toList();
        assertEquals(2, vertices.size());
    }

    @Test
    @LoadGraphWith(MODERN)
    public void g_V_group_byXlabelX_byXbothE_valuesXweightX_foldX_byXdedupXlocalXX() {
        final Traversal<Vertex, Map<String, Set<Double>>> traversal =
                get_g_V_group_byXlabelX_byXbothE_valuesXweightX_foldX_byXdedupXlocalXX();
        printTraversalForm(traversal);
        assertTrue(traversal.hasNext());
        final Map<String, Set<Double>> map = traversal.next();
        assertFalse(traversal.hasNext());
        assertEquals(2, map.size());
        assertEquals(3, map.get("software").size());
        assertEquals(4, map.get("person").size());
        assertEquals(new HashSet<>(Arrays.asList(0.2, 0.4, 1.0)), map.get("software"));
        assertEquals(new HashSet<>(Arrays.asList(0.2, 0.4, 0.5, 1.0)), map.get("person"));
    }

    @Test
    @LoadGraphWith(MODERN)
    public void g_V_asXaX_both_asXbX_dedupXa_bX_byXlabelX_selectXa_bX() {
        final Traversal<Vertex, Map<String, Vertex>> traversal = get_g_V_asXaX_both_asXbX_dedupXa_bX_byXlabelX_selectXa_bX();
        printTraversalForm(traversal);
        int personPersonCounter = 0;
        int personSoftwareCounter = 0;
        int softwarePersonCounter = 0;
        while (traversal.hasNext()) {
            final Map<String, Vertex> map = traversal.next();
            assertEquals(2, map.size());
            if (map.get("a").label().equals("person") && map.get("b").label().equals("person"))
                personPersonCounter++;
            else if (map.get("a").label().equals("person") && map.get("b").label().equals("software"))
                personSoftwareCounter++;
            else if (map.get("a").label().equals("software") && map.get("b").label().equals("person"))
                softwarePersonCounter++;
            else
                fail("Bad result type: " + map);
        }
        assertEquals(1, personPersonCounter);
        assertEquals(1, personSoftwareCounter);
        assertEquals(1, softwarePersonCounter);
    }

    @Test
    @LoadGraphWith(MODERN)
    public void g_V_asXaX_outXcreatedX_asXbX_inXcreatedX_asXcX_dedupXa_bX_path() {
        final Traversal<Vertex, Path> traversal = get_g_V_asXaX_outXcreatedX_asXbX_inXcreatedX_asXcX_dedupXa_bX_path();
        printTraversalForm(traversal);
        int counter = 0;
        final Set<List<Vertex>> results = new HashSet<>();
        while (traversal.hasNext()) {
            final Path path = traversal.next();
            assertEquals(3, path.size());
            assertTrue(results.add(Arrays.asList(path.get("a"), path.get("b"))));
            counter++;
        }
        assertEquals(4, counter);
        assertEquals(4, results.size());
    }


    public static class Traversals extends DedupTest {
        @Override
        public Traversal<Vertex, String> get_g_V_both_dedup_name() {
            return g.V().both().dedup().values("name");
        }

        @Override
        public Traversal<Vertex, String> get_g_V_both_hasXlabel_softwareX_dedup_byXlangX_name() {
            return g.V().both().has(T.label, "software").dedup().by("lang").values("name");
        }

        @Override
        public Traversal<Vertex, String> get_g_V_both_name_order_byXa_bX_dedup_value() {
            return g.V().both().<String>properties("name").order().by((a, b) -> a.value().compareTo(b.value())).dedup().value();
        }

        @Override
        public Traversal<Vertex, String> get_g_V_both_both_name_dedup() {
            return g.V().both().both().<String>values("name").dedup();
        }

        @Override
        public Traversal<Vertex, Vertex> get_g_V_both_both_dedup() {
            return g.V().both().both().dedup();
        }

        @Override
        public Traversal<Vertex, Vertex> get_g_V_both_both_dedup_byXlabelX() {
            return g.V().both().both().dedup().by(T.label);
        }

        @Override
        public Traversal<Vertex, Map<String, Set<Double>>> get_g_V_group_byXlabelX_byXbothE_valuesXweightX_foldX_byXdedupXlocalXX() {
            return g.V().<String, Set<Double>>group().by(T.label).by(bothE().values("weight").fold()).by(dedup(Scope.local));
        }

        @Override
        public Traversal<Vertex, Map<String, Vertex>> get_g_V_asXaX_both_asXbX_dedupXa_bX_byXlabelX_selectXa_bX() {
            return g.V().as("a").both().as("b").dedup("a", "b").by(T.label).select("a","b");
        }

        @Override
        public Traversal<Vertex, Path> get_g_V_asXaX_outXcreatedX_asXbX_inXcreatedX_asXcX_dedupXa_bX_path() {
            return g.V().as("a").out("created").as("b").in("created").as("c").dedup("a", "b").path();
        }
    }
}
