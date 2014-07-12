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
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * A map-backed persistent set.
 * <p/>
 * If the backing map is thread-safe, then this implementation is thread-safe
 * (assuming Java's AbstractSet is thread-safe), although its iterators
 * may not be.
 *
 * @author harold
 * @author Yu Kobayashi
 * @since 2.4.0
 */
final class ImmutableMapSet<E> extends AbstractSet<E> implements ImmutableSet<E>, Serializable {
    private static final ImmutableMapSet<Object> EMPTY = from(ImmutableCollections.map());
    private static final long serialVersionUID = -1344047864698590249L;

    /**
     * @return an empty set
     */
    @SuppressWarnings("unchecked")
    public static <E> ImmutableMapSet<E> empty() {
        return (ImmutableMapSet<E>) EMPTY;
    }

    /**
     * @return empty().plus(e)
     */
    public static <E> ImmutableMapSet<E> singleton(E e) {
        return ImmutableMapSet.<E>empty().plus(e);
    }

    /**
     * @return empty().plus(list)
     */
    public static <E> ImmutableMapSet<E> from(Iterable<? extends E> list) {
        return ImmutableMapSet.<E>empty().plus(list);
    }

    /**
     * @return a PSet with the elements of map.keySet(), backed by map
     */
    @SuppressWarnings("unchecked")
    public static <E> ImmutableMapSet<E> from(ImmutableMap<E, ?> map) {
        return new ImmutableMapSet<E>((ImmutableMap<E, Object>) map);
    }

    /**
     * @return from(map).plus(e)
     */
    public static <E> ImmutableMapSet<E> from(ImmutableMap<E, ?> map, E e) {
        return from(map).plus(e);
    }

    /**
     * @return from(map).plusAll(list)
     */
    public static <E> ImmutableMapSet<E> from(ImmutableMap<E, ?> map, Collection<? extends E> list) {
        return from(map).plus(list);
    }

    private final ImmutableMap<E, Object> map;

    // not instantiable (or subclassable):
    private ImmutableMapSet(ImmutableMap<E, Object> map) {
        this.map = map;
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean contains(Object e) {
        return map.containsKey(e);
    }

    private static enum In {
        IN
    }

    public ImmutableMapSet<E> plus(E element) {
        return contains(element) ? this : new ImmutableMapSet<E>(map.plus(element, In.IN));
    }

    public ImmutableMapSet<E> minus(Object element) {
        return contains(element) ? new ImmutableMapSet<E>(map.minus(element)) : this;
    }

    public ImmutableMapSet<E> plus(Iterable<? extends E> iterable) {
        ImmutableMap<E, Object> map = this.map;
        for (E e : iterable)
            map = map.plus(e, In.IN);
        return from(map);
    }

    public ImmutableMapSet<E> minus(Iterable<?> iterable) {
        return from(map.minus(iterable));
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
