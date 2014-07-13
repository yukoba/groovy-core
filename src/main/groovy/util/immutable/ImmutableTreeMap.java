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
 * An immutable and persistent map from non-null keys to nullable values.
 * <p/>
 * This map uses a given integer map to map hashcodes to lists of elements
 * with the same hashcode. Thus if all elements have the same hashcode, performance
 * is reduced to that of an association list.
 * <p/>
 * This implementation is thread-safe (assuming Java's AbstractMap and AbstractSet are thread-safe),
 * although its iterators may not be.
 *
 * @author harold
 * @author Yu Kobayashi
 * @since 2.4.0
 */
final class ImmutableTreeMap<K, V> extends AbstractMap<K, V> implements ImmutableMap<K, V>, Serializable {
    private static final ImmutableTreeMap<Object, Object> EMPTY = ImmutableTreeMap.empty(ImmutableIntTreeMap.<ImmutableList<Entry<Object, Object>>>empty());
    private static final long serialVersionUID = -3538508895284495077L;

    /**
     * @return an empty map
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ImmutableTreeMap<K, V> empty() {
        return (ImmutableTreeMap<K, V>) EMPTY;
    }

    /**
     * @return empty().plus(key, value)
     */
    public static <K, V> ImmutableTreeMap<K, V> singleton(K key, V value) {
        return ImmutableTreeMap.<K, V>empty().plus(key, value);
    }

    /**
     * @return empty().plus(map)
     */
    public static <K, V> ImmutableTreeMap<K, V> from(Map<? extends K, ? extends V> map) {
        return ImmutableTreeMap.<K, V>empty().plus(map);
    }

    /**
     * @return a map backed by an empty version of intMap, i.e. backed by intMap.minusAll(intMap.keySet())
     */
    public static <K, V> ImmutableTreeMap<K, V> empty(ImmutableMap<Integer, ImmutableList<Entry<K, V>>> intMap) {
        return new ImmutableTreeMap<K, V>(intMap.minus(intMap.keySet()), 0);
    }

    private final ImmutableMap<Integer, ImmutableList<Entry<K, V>>> intMap;
    private final int size;

    // not externally instantiable (or subclassable):
    private ImmutableTreeMap(ImmutableMap<Integer, ImmutableList<Entry<K, V>>> intMap, int size) {
        this.intMap = intMap;
        this.size = size;
    }

    // this cache variable is thread-safe since assignment in Java is atomic:
    private transient Set<Entry<K, V>> entrySet;

    public Set<Entry<K, V>> entrySet() {
        if (entrySet == null)
            entrySet = new AbstractSet<Entry<K, V>>() {
                // REQUIRED METHODS OF AbstractSet //
                @Override
                public int size() {
                    return size;
                }

                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return new SequenceIterator<Entry<K, V>>(intMap.values().iterator());
                }

                // OVERRIDDEN METHODS OF AbstractSet //
                @Override
                public boolean contains(Object e) {
                    if (!(e instanceof Entry))
                        return false;
                    V value = get(((Entry<?, ?>) e).getKey());
                    return value != null && value.equals(((Entry<?, ?>) e).getValue());
                }
            };
        return entrySet;
    }


    public int size() {
        return size;
    }

    public boolean containsKey(Object key) {
        return keyIndexIn(getEntries(key.hashCode()), key) != -1;
    }

    public V get(Object key) {
        ImmutableList<Entry<K, V>> entries = getEntries(key.hashCode());
        for (Entry<K, V> entry : entries) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public ImmutableTreeMap<K, V> plus(Map<? extends K, ? extends V> map) {
        ImmutableTreeMap<K, V> result = this;
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            result = result.plus(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public ImmutableTreeMap<K, V> minus(Iterable<?> keys) {
        ImmutableTreeMap<K, V> result = this;
        for (Object key : keys) {
            result = result.minus(key);
        }
        return result;
    }

    public ImmutableTreeMap<K, V> plus(K key, V value) {
        ImmutableList<Entry<K, V>> entries = getEntries(key.hashCode());
        int size0 = entries.size();
        int i = keyIndexIn(entries, key);
        if (i != -1)
            entries = entries.minusAt(i);
        entries = entries.plus(new SimpleImmutableEntry<K, V>(key, value));
        return new ImmutableTreeMap<K, V>(intMap.plus(key.hashCode(), entries), size - size0 + entries.size());
    }

    public ImmutableTreeMap<K, V> minus(Object key) {
        ImmutableList<Entry<K, V>> entries = getEntries(key.hashCode());
        int i = keyIndexIn(entries, key);
        if (i == -1) // key not in this
            return this;
        entries = entries.minusAt(i);
        if (entries.size() == 0) // get rid of the entire hash entry
            return new ImmutableTreeMap<K, V>(intMap.minus(key.hashCode()), size - 1);
        // otherwise replace hash entry with new smaller one:
        return new ImmutableTreeMap<K, V>(intMap.plus(key.hashCode(), entries), size - 1);
    }

    private ImmutableList<Entry<K, V>> getEntries(int hash) {
        ImmutableList<Entry<K, V>> entries = intMap.get(hash);
        if (entries == null) return ImmutableConsStack.empty();
        return entries;
    }

    private static <K, V> int keyIndexIn(ImmutableList<Entry<K, V>> entries, Object key) {
        int i = 0;
        for (Entry<K, V> entry : entries) {
            if (entry.getKey().equals(key))
                return i;
            i++;
        }
        return -1;
    }

    static class SequenceIterator<E> implements Iterator<E> {
        private final Iterator<ImmutableList<E>> i;
        private ImmutableList<E> seq = ImmutableConsStack.empty();

        SequenceIterator(Iterator<ImmutableList<E>> i) {
            this.i = i;
        }

        public boolean hasNext() {
            return seq.size() > 0 || i.hasNext();
        }

        public E next() {
            if (seq.size() == 0)
                seq = i.next();
            final E result = seq.get(0);
            seq = seq.subList(1, seq.size());
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Deprecated
    public V putAt(K key, V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public V put(K k, V v) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public V remove(Object k) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
