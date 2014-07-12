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

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map.Entry;

/**
 * A persistent vector. Elements can be null.
 * <p/>
 * This implementation is backed by an ImmutableIntTreeMap and
 * supports logarithmic-time querying, setting, insertion,
 * and removal.
 * <p/>
 * This implementation is thread-safe (assuming Java's AbstractList is thread-safe)
 * although its iterators may not be.
 *
 * @author harold
 * @author Yu Kobayashi
 * @since 2.4.0
 */
final class ImmutableTreeList<E> extends AbstractList<E> implements ImmutableList<E>, Serializable {
    private static final ImmutableTreeList<Object> EMPTY = new ImmutableTreeList<Object>();
    private static final long serialVersionUID = -6788530879100442978L;

    /**
     * @return an empty vector
     */
    @SuppressWarnings("unchecked")
    public static <E> ImmutableTreeList<E> empty() {
        return (ImmutableTreeList<E>) EMPTY;
    }

    /**
     * @return empty().plus(e)
     */
    public static <E> ImmutableTreeList<E> singleton(E e) {
        return ImmutableTreeList.<E>empty().plus(e);
    }

    /**
     * @return empty().plus(list)
     */
    @SuppressWarnings("unchecked")
    public static <E> ImmutableTreeList<E> from(Iterable<? extends E> list) {
        if (list instanceof ImmutableTreeList)
            return (ImmutableTreeList<E>) list; // (actually we only know it's ImmutableTreeList<? extends E>)

        // but that's good enough for an immutable
        // (i.e. we can't mess someone else up by adding the wrong type to it)
        return ImmutableTreeList.<E>empty().plus(list);
    }

    private final ImmutableIntTreeMap<E> map;

    private ImmutableTreeList() {
        this(ImmutableIntTreeMap.<E>empty());
    }

    private ImmutableTreeList(ImmutableIntTreeMap<E> map) {
        this.map = map;
    }

    public int size() {
        return map.size();
    }

    public E get(int index) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException();

        return map.getAt(index);
    }

    public E getAt(int i) {
        return get(i);
    }

    @Override
    public Iterator<E> iterator() {
        return map.values().iterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListIterator<E> listIterator(int index) {
        return new ArrayListIterator<E>((E[]) toArray(), index);
    }

    public ImmutableTreeList<E> subList(int start, int end) {
        int size = size();
        if (start < 0 || end > size || start > end)
            throw new IndexOutOfBoundsException();
        if (start == end)
            return empty();
        if (start == 0) {
            if (end == size)
                return this;
            // remove from end:
            return this.minusAt(size - 1).subList(start, end);
        }
        // remove from start:
        return this.minusAt(0).subList(start - 1, end - 1);
    }

    public ImmutableTreeList<E> subList(int start) {
        return subList(start, size());
    }

    public ImmutableTreeList<E> plus(E element) {
        return new ImmutableTreeList<E>(map.plus(size(), element));
    }

    public ImmutableTreeList<E> plus(Iterable<? extends E> iterable) {
        ImmutableTreeList<E> result = this;
        for (E e : iterable) {
            result = result.plus(e);
        }
        return result;
    }

    public ImmutableTreeList<E> plusAt(int index, E element) {
        if (index < 0 || index > size())
            throw new IndexOutOfBoundsException();
        return new ImmutableTreeList<E>(map.withKeysChangedAbove(index, 1).plus(index, element));
    }

    public ImmutableTreeList<E> plusAt(int index, Iterable<? extends E> iterable) {
        if (index < 0 || index > size())
            throw new IndexOutOfBoundsException();

        Collection<? extends E> list = DefaultGroovyMethods.asCollection(iterable);
        if (list.size() == 0)
            return this;

        ImmutableIntTreeMap<E> map = this.map.withKeysChangedAbove(index, list.size());
        int i = index;
        for (E e : list) {
            map = map.plus(i++, e);
        }
        return new ImmutableTreeList<E>(map);
    }

    public ImmutableList<E> replaceAt(int index, E element) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException();

        ImmutableIntTreeMap<E> map = this.map.plus(index, element);
        if (map == this.map)
            return this;
        return new ImmutableTreeList<E>(map);
    }

    public ImmutableTreeList<E> minus(Object element) {
        for (Entry<Integer, E> entry : map.entrySet()) {
            if (objectEquals(entry.getValue(), element)) {
                return minusAt(entry.getKey());
            }
        }
        return this;
    }

    public ImmutableTreeList<E> minus(Iterable<?> iterable) {
        ImmutableTreeList<E> result = this;
        for (Object e : iterable) {
            result = result.minus(e);
        }
        return result;
    }

    public ImmutableTreeList<E> minusAt(int index) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException();

        return new ImmutableTreeList<E>(map.minus(index).withKeysChangedAbove(index, -1));
    }

    private static boolean objectEquals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
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
    public boolean addAll(int index, Collection<? extends E> c) {
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

    @Override
    @Deprecated
    public void add(int index, E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }
}
