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
import java.util.*;

/**
 * A simple persistent stack. Elements can be null.
 * <p/>
 * This implementation is thread-safe (assuming Java's AbstractSequentialList is thread-safe),
 * although its iterators may not be.
 *
 * @author harold
 * @author Yu Kobayashi
 * @since 2.4.0
 */
final class ImmutableConsStack<E> extends AbstractSequentialList<E> implements ImmutableStack<E>, Serializable {
    private static final ImmutableConsStack<Object> EMPTY = new ImmutableConsStack<Object>();
    private static final long serialVersionUID = -7306791346408620911L;

    /**
     * @return an empty stack
     */
    @SuppressWarnings("unchecked")
    public static <E> ImmutableConsStack<E> empty() {
        return (ImmutableConsStack<E>) EMPTY;
    }

    /**
     * @return empty().plus(e)
     */
    public static <E> ImmutableConsStack<E> singleton(E e) {
        return ImmutableConsStack.<E>empty().plus(e);
    }

    /**
     * @return a stack consisting of the elements of list in the order of list.iterator()
     */
    @SuppressWarnings("unchecked")
    public static <E> ImmutableConsStack<E> from(Iterable<? extends E> list) {
        // but that's good enough for an immutable
        // (i.e. we can't mess someone else up by adding the wrong type to it)
        return from(list.iterator());
    }

    private static <E> ImmutableConsStack<E> from(Iterator<? extends E> i) {
        if (!i.hasNext()) return empty();
        E e = i.next();
        return from(i).plus(e);
    }

    private final E first;
    private final ImmutableConsStack<E> rest;
    private final int size;

    // not externally instantiable (or subclassable):
    private ImmutableConsStack() { // EMPTY constructor
        if (EMPTY != null)
            throw new RuntimeException("empty constructor should only be used once");
        size = 0;
        first = null;
        rest = null;
    }

    private ImmutableConsStack(E first, ImmutableConsStack<E> rest) {
        this.first = first;
        this.rest = rest;

        size = 1 + rest.size;
    }

    public int size() {
        return size;
    }

    public E get(int i) {
        if (i < 0 || i >= size)
            throw new IndexOutOfBoundsException();

        return subList(i).first;
    }

    public E getAt(int i) {
        return get(i);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int idx = 0;
            ImmutableConsStack<E> next = ImmutableConsStack.this;

            public boolean hasNext() {
                return idx < size;
            }

            public E next() {
                if (idx >= size)
                    throw new NoSuchElementException();
                idx++;

                E e = next.first;
                next = next.rest;
                return e;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public ListIterator<E> listIterator(int index) {
        return new ArrayListIterator<E>((E[]) toArray(), index);
    }

    public ImmutableConsStack<E> subList(int start, int end) {
        if (start < 0 || end > size || start > end)
            throw new IndexOutOfBoundsException();
        if (end == size) // want a substack
            return subList(start); // this is faster
        if (start == end) // want nothing
            return empty();

        if (start == 0) // want the current element
            return new ImmutableConsStack<E>(first, rest.subList(0, end - 1));

        // otherwise, don't want the current element:
        return rest.subList(start - 1, end - 1);
    }

    public ImmutableConsStack<E> subList(int start) {
        if (start < 0 || start > size)
            throw new IndexOutOfBoundsException();

        return start == 0 ? this : rest.subList(start - 1);
    }

    public ImmutableConsStack<E> plus(E element) {
        return new ImmutableConsStack<E>(element, this);
    }

    public ImmutableConsStack<E> plus(Iterable<? extends E> iterable) {
        ImmutableConsStack<E> result = this;
        for (E e : iterable) {
            result = result.plus(e);
        }
        return result;
    }

    public ImmutableConsStack<E> plusAt(int index, E element) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException();

        if (index == 0) // insert at beginning
            return plus(element);

        return new ImmutableConsStack<E>(first, rest.plusAt(index - 1, element));
    }

    public ImmutableConsStack<E> plusAt(int index, Iterable<? extends E> iterable) {
        // TODO inefficient if iterable is empty
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException();

        if (index == 0)
            return plus(iterable);

        return new ImmutableConsStack<E>(first, rest.plusAt(index - 1, iterable));
    }

    public ImmutableConsStack<E> replaceAt(int index, E element) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        if (index == 0)
            return objectEquals(first, element) ? this : new ImmutableConsStack<E>(element, rest);

        ImmutableConsStack<E> newRest = rest.replaceAt(index - 1, element);
        return newRest == rest ? this : new ImmutableConsStack<E>(first, newRest);
    }

    public ImmutableConsStack<E> minus(Object element) {
        if (size == 0)
            return this;

        if (objectEquals(first, element)) // found it
            return rest; // don't recurse (only remove one)

        // otherwise keep looking:
        ImmutableConsStack<E> newRest = rest.minus(element);
        return newRest == rest ? this : new ImmutableConsStack<E>(first, newRest);
    }

    public ImmutableConsStack<E> minus(Iterable<?> iterable) {
        if (size == 0)
            return this;

        Collection<?> list = DefaultGroovyMethods.asCollection(iterable);
        if (list.contains(first)) // get rid of current element
            return rest.minus(list); // recursively delete all

        // either way keep looking:
        ImmutableConsStack<E> newRest = rest.minus(list);
        return newRest == rest ? this : new ImmutableConsStack<E>(first, newRest);
    }

    public ImmutableConsStack<E> minusAt(int index) {
        return minus(get(index));
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
