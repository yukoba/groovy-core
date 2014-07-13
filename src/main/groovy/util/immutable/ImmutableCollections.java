/*
 * Copyright 2003-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package groovy.util.immutable;

import java.util.Map;

/**
 * A static utility class for getting empty immutable and persistent collections or creating immutable and persistent collections from mutable collections backed by the 'default' implementations.
 *
 * @author mtklein
 * @author Yu Kobayashi
 * @since 2.4.0
 */
public final class ImmutableCollections {
    /**
     * non-instantiable
     */
    private ImmutableCollections() {
    }

    /**
     * Creates an empty immutable stack.
     *
     * @return an empty immutable stack
     */
    static <E> ImmutableStack<E> stack() {
        return ImmutableConsStack.empty();
    }

    /**
     * Creates an immutable stack from an iterable.
     *
     * @param iterable creates from
     * @return the immutable stack
     */
    static <E> ImmutableStack<E> stack(Iterable<? extends E> iterable) {
        return ImmutableConsStack.from(iterable);
    }

    /**
     * Creates an empty immutable deque.
     *
     * @return an empty immutable deque
     */
    public static <E> ImmutableDeque<E> deque() {
        return ImmutableAmortizedDeque.empty();
    }

    /**
     * Creates an immutable deque from an iterable.
     *
     * @param iterable creates from
     * @return the immutable deque
     */
    public static <E> ImmutableDeque<E> deque(Iterable<? extends E> iterable) {
        return ImmutableAmortizedDeque.from(iterable);
    }

    /**
     * Creates an empty immutable list.
     *
     * @return an empty immutable list
     */
    public static <E> ImmutableList<E> list() {
        return ImmutableTreeList.empty();
    }

    /**
     * Creates an immutable list from an iterable.
     *
     * @param iterable creates from
     * @return the immutable list
     */
    public static <E> ImmutableList<E> list(Iterable<? extends E> iterable) {
        return ImmutableTreeList.from(iterable);
    }

    /**
     * Creates an empty immutable set.
     *
     * @return an empty immutable set
     */
    public static <E> ImmutableSet<E> set() {
        return ImmutableMapSet.empty();
    }

    /**
     * Creates an immutable set from an iterable.
     *
     * @param iterable creates from
     * @return the immutable set
     */
    public static <E> ImmutableSet<E> set(Iterable<? extends E> iterable) {
        return ImmutableMapSet.from(iterable);
    }

    /**
     * Creates an empty immutable list set.
     *
     * @return an empty immutable list set
     */
    public static <E> ImmutableListSet<E> listSet() {
        return ImmutableTreeSet.empty();
    }

    /**
     * Creates an immutable list set from an iterable.
     *
     * @param iterable creates from
     * @return the immutable list set
     */
    public static <E> ImmutableListSet<E> listSet(Iterable<? extends E> iterable) {
        return ImmutableTreeSet.from(iterable);
    }

    /**
     * Creates an empty immutable bag.
     *
     * @return an empty immutable bag
     */
    static <E> ImmutableBag<E> bag() {
        return ImmutableMapBag.empty();
    }

    /**
     * Creates an immutable bag from an iterable.
     *
     * @param iterable creates from
     * @return the immutable bag
     */
    static <E> ImmutableBag<E> bag(Iterable<? extends E> iterable) {
        return ImmutableMapBag.from(iterable);
    }

    /**
     * Creates an empty immutable map.
     *
     * @return an empty immutable map
     */
    public static <K, V> ImmutableMap<K, V> map() {
        return ImmutableTreeMap.empty();
    }

    /**
     * Creates an immutable map from a mutable map.
     *
     * @param map creates from
     * @return the immutable map
     */
    public static <K, V> ImmutableMap<K, V> map(Map<? extends K, ? extends V> map) {
        return ImmutableTreeMap.from(map);
    }
}
