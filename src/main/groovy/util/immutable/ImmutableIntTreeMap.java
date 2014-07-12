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

import java.io.Serializable;
import java.util.*;

/**
 * An efficient persistent map from integer keys to non-null values.
 * <p/>
 * Iteration occurs in the integer order of the keys.
 * <p/>
 * This implementation is thread-safe (assuming Java's AbstractMap and AbstractSet are thread-safe),
 * although its iterators may not be.
 * <p/>
 * The balanced tree is based on the Glasgow Haskell Compiler's Data.Map implementation,
 * which in turn is based on "size balanced binary trees" as described by:
 * <p/>
 * Stephen Adams, "Efficient sets: a balancing act",
 * Journal of Functional Programming 3(4):553-562, October 1993,
 * http://www.swiss.ai.mit.edu/~adams/BB/.
 * <p/>
 * J. Nievergelt and E.M. Reingold, "Binary search trees of bounded balance",
 * SIAM journal of computing 2(1), March 1973.
 *
 * @author harold
 * @author Yu Kobayashi
 * @since 2.4.0
 */
final class ImmutableIntTreeMap<V> extends AbstractMap<Integer, V> implements ImmutableMap<Integer, V>, Serializable {
    private static final ImmutableIntTreeMap<Object> EMPTY = new ImmutableIntTreeMap<Object>(IntTree.EMPTYNODE);
    private static final long serialVersionUID = -777938930078650943L;

    /**
     * @return an empty map
     */
    @SuppressWarnings("unchecked")
    public static <V> ImmutableIntTreeMap<V> empty() {
        return (ImmutableIntTreeMap<V>) EMPTY;
    }

    /**
     * @return empty().plus(key, value)
     */
    public static <V> ImmutableIntTreeMap<V> singleton(Integer key, V value) {
        return ImmutableIntTreeMap.<V>empty().plus(key, value);
    }

    /**
     * @return empty().plus(map)
     */
    @SuppressWarnings("unchecked")
    public static <V> ImmutableIntTreeMap<V> from(Map<? extends Integer, ? extends V> map) {
        // but that's good enough for an immutable
        // (i.e. we can't mess someone else up by adding the wrong type to it)
        return ImmutableIntTreeMap.<V>empty().plus(map);
    }

    private final IntTree<V> root;

    // not externally instantiable (or subclassable):
    private ImmutableIntTreeMap(IntTree<V> root) {
        this.root = root;
    }

    private ImmutableIntTreeMap<V> withRoot(IntTree<V> root) {
        if (root == this.root) return this;
        return new ImmutableIntTreeMap<V>(root);
    }

    ImmutableIntTreeMap<V> withKeysChangedAbove(int key, int delta) {
        // TODO check preconditions of changeKeysAbove()
        return withRoot(root.changeKeysAbove(key, delta));
    }

    ImmutableIntTreeMap<V> withKeysChangedBelow(int key, int delta) {
        // TODO check preconditions of changeKeysAbove()
        return withRoot(root.changeKeysBelow(key, delta));
    }

    // this cache variable is thread-safe, since assignment in Java is atomic:
    private transient Set<Entry<Integer, V>> entrySet;

    public Set<Entry<Integer, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new AbstractSet<Entry<Integer, V>>() {
                @Override
                public int size() { // same as Map
                    return ImmutableIntTreeMap.this.size();
                }

                @Override
                public Iterator<Entry<Integer, V>> iterator() {
                    return root.iterator();
                }

                @Override
                public boolean contains(Object e) {
                    if (!(e instanceof Entry))
                        return false;
                    V value = get(((Entry<?, ?>) e).getKey());
                    return value != null && value.equals(((Entry<?, ?>) e).getValue());
                }
            };
        }
        return entrySet;
    }

    public int size() {
        return root.size();
    }

    public boolean containsKey(Object key) {
        return key instanceof Integer && root.containsKey((Integer) key);
    }

    public V getAt(Object key) {
        return get(key);
    }

    public V get(Object key) {
        return key instanceof Integer ? root.get((Integer) key) : null;
    }

    public ImmutableIntTreeMap<V> plus(Integer key, V value) {
        return withRoot(root.plus(key, value));
    }

    public ImmutableIntTreeMap<V> plus(Map<? extends Integer, ? extends V> map) {
        IntTree<V> root = this.root;
        for (Entry<? extends Integer, ? extends V> entry : map.entrySet()) {
            root = root.plus(entry.getKey(), entry.getValue());
        }
        return withRoot(root);
    }

    public ImmutableIntTreeMap<V> minus(Object key) {
        return key instanceof Integer ? withRoot(root.minus((Integer) key)) : this;
    }

    public ImmutableIntTreeMap<V> minus(Iterable<?> keys) {
        IntTree<V> root = this.root;
        for (Object key : keys) {
            if (key instanceof Integer) {
                root = root.minus((Integer) key);
            }
        }
        return withRoot(root);
    }

    @Deprecated
    public V putAt(Integer key, V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public V put(Integer k, V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public V remove(Object k) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void putAll(Map<? extends Integer, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
