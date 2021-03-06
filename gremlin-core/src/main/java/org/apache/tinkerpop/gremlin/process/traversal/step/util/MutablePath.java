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
package org.apache.tinkerpop.gremlin.process.traversal.step.util;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.Pop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class MutablePath implements Path, Serializable {

    protected final List<Object> objects;
    protected final List<Set<String>> labels;

    protected MutablePath() {
        this(10);
    }

    private MutablePath(final int capacity) {
        this.objects = new ArrayList<>(capacity);
        this.labels = new ArrayList<>(capacity);
    }

    public static Path make() {
        return new MutablePath();
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone,CloneDoesntDeclareCloneNotSupportedException")
    public MutablePath clone() {
        final MutablePath clone = new MutablePath(this.objects.size());
        // TODO: Why is this not working Hadoop serialization-wise?... Its cause DetachedPath's clone needs to detach on clone.
        /*final MutablePath clone = (MutablePath) super.clone();
        clone.objects = new ArrayList<>();
        clone.labels = new ArrayList<>();*/
        clone.objects.addAll(this.objects);
        clone.labels.addAll(this.labels);
        return clone;
    }


    @Override
    public int size() {
        return this.objects.size();
    }

    @Override
    public Path extend(final Object object, final Set<String> labels) {
        this.objects.add(object);
        this.labels.add(new LinkedHashSet<>(labels));
        return this;
    }

    @Override
    public <A> A get(int index) {
        return (A) this.objects.get(index);
    }

    @Override
    public <A> A get(final Pop pop, final String label) {
        if(Pop.all == pop) {
            if (this.hasLabel(label)) {
                final Object object = this.get(label);
                if (object instanceof List)
                    return (A) object;
                else
                    return (A) Collections.singletonList(object);
            } else {
                return (A) Collections.emptyList();
            }
        }  else {
            // Override default to avoid building temporary list, and to stop looking when we find the label.
            if (Pop.last == pop) {
                for (int i = this.labels.size() - 1; i >= 0; i--) {
                    if (labels.get(i).contains(label))
                        return (A) objects.get(i);
                }
            } else {
                for (int i = 0; i != this.labels.size(); i++) {
                    if (labels.get(i).contains(label))
                        return (A) objects.get(i);
                }
            }
            throw Path.Exceptions.stepWithProvidedLabelDoesNotExist(label);
        }
    }

    @Override
    public boolean hasLabel(final String label) {
        return this.labels.stream().filter(l -> l.contains(label)).findAny().isPresent();
    }

    @Override
    public void addLabel(final String label) {
        this.labels.get(this.labels.size() - 1).add(label);
    }

    @Override
    public List<Object> objects() {
        return Collections.unmodifiableList(this.objects);
    }

    @Override
    public List<Set<String>> labels() {
        return Collections.unmodifiableList(this.labels);
    }

    @Override
    public String toString() {
        return this.objects.toString();
    }
}
