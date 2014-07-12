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
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * A map-backed persistent bag.
 * <p/>
 * If the backing map is thread-safe, then this implementation is thread-safe
 * (assuming Java's AbstractCollection is thread-safe), although its iterators
 * may not be.
 *
 * @author harold
 * @author Yu Kobayashi
 * @since 2.4.0
 */
final class ImmutableMapBag<E> extends AbstractCollection<E> implements ImmutableBag<E>, Serializable {
    private static final ImmutableMapBag<Object> EMPTY = empty(ImmutableCollections.<Object, Integer>map());
    private static final long serialVersionUID = 7567737490423868674L;

    /**
     * @return an empty bag
     */
    @SuppressWarnings("unchecked")
    public static <E> ImmutableMapBag<E> empty() {
        return (ImmutableMapBag<E>) EMPTY;
    }

    /**
     * @return empty().plus(e)
     */
    public static <E> ImmutableMapBag<E> singleton(E e) {
        return ImmutableMapBag.<E>empty().plus(e);
    }

    /**
     * @return empty().plus(list)
     */
    public static <E> ImmutableMapBag<E> from(Iterable<? extends E> list) {
        return ImmutableMapBag.<E>empty().plus(list);
    }

    /**
     * @return a ImmutableMapBag backed by an empty version of map, i.e. by map.minusAll(map.keySet())
     */
    public static <E> ImmutableMapBag<E> empty(ImmutableMap<E, Integer> map) {
        return new ImmutableMapBag<E>(map.minus(map.keySet()), 0);
    }

    private final ImmutableMap<E, Integer> map;
    private final int size;

    // not instantiable (or subclassable):
    private ImmutableMapBag(ImmutableMap<E, Integer> map, int size) {
        this.map = map;
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<Entry<E, Integer>> i = map.entrySet().iterator();
        return new Iterator<E>() {
            private E e;
            private int n = 0;

            public boolean hasNext() {
                return n > 0 || i.hasNext();
            }

            public E next() {
                if (n == 0) { // finished with current element
                    Entry<E, Integer> entry = i.next();
                    e = entry.getKey();
                    n = entry.getValue();
                }
                n--;
                return e;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean contains(Object e) {
        return map.containsKey(e);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (E e : this) {
            hashCode += e.hashCode();
        }
        return hashCode;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object that) {
        if (!(that instanceof ImmutableBag))
            return false;
        if (!(that instanceof ImmutableMapBag)) {
            // make that into a ImmutableMapBag
            that = ImmutableMapBag.empty().plus(that);
        }
        return this.map.equals(((ImmutableMapBag<?>) that).map);
    }

    public ImmutableMapBag<E> plus(E element) {
        return new ImmutableMapBag<E>(map.plus(element, count(element) + 1), size + 1);
    }

    @SuppressWarnings("unchecked")
    public ImmutableMapBag<E> minus(Object element) {
        int n = count(element);
        if (n == 0)
            return this;
        if (n == 1) // remove from map
            return new ImmutableMapBag<E>(map.minus(element), size - 1);
        // otherwise just decrement count:
        return new ImmutableMapBag<E>(map.plus((E) element, n - 1), size - 1);
    }

    public ImmutableMapBag<E> plus(Iterable<? extends E> iterable) {
        ImmutableMapBag<E> bag = this;
        for (E e : iterable) {
            bag = bag.plus(e);
        }
        return bag;
    }

    public ImmutableMapBag<E> minus(Iterable<?> iterable) {
        // removes _all_ elements found in list, i.e. counts are irrelevant:
        ImmutableMap<E, Integer> map = this.map.minus(iterable);
        return new ImmutableMapBag<E>(map, size(map)); // (completely recomputes size)
    }

    @SuppressWarnings("unchecked")
    private int count(Object o) {
        return contains(o) ? map.get((E) o) : 0;
    }

    private static int size(ImmutableMap<?, Integer> map) {
        int size = 0;
        for (Integer n : map.values()) {
            size += n;
        }
        return size;
    }

    @Override
    @Deprecated
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
