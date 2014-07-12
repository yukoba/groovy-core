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
 * @author Yu Kobayashi
 * @since 2.4.0
 */
final class ImmutableTreeSet<E> extends AbstractSet<E> implements ImmutableListSet<E>, Serializable {
    private static final ImmutableTreeSet<Object> EMPTY = new ImmutableTreeSet<Object>();
    private static final long serialVersionUID = -5920090981526873189L;

    @SuppressWarnings("unchecked")
    public static <E> ImmutableTreeSet<E> empty() {
        return (ImmutableTreeSet<E>) EMPTY;
    }

    public static <E> ImmutableTreeSet<E> singleton(E e) {
        return ImmutableTreeSet.<E>empty().plus(e);
    }

    @SuppressWarnings("unchecked")
    public static <E> ImmutableTreeSet<E> from(Iterable<? extends E> list) {
        if (list instanceof ImmutableTreeSet)
            return (ImmutableTreeSet<E>) list;
        return ImmutableTreeSet.<E>empty().plus(list);
    }

    private final ImmutableSet<E> contents;
    private final ImmutableList<E> order;

    private ImmutableTreeSet() {
        this(ImmutableCollections.<E>set(), ImmutableCollections.<E>list());
    }

    private ImmutableTreeSet(ImmutableSet<E> contents, ImmutableList<E> order) {
        this.contents = contents;
        this.order = order;
    }

    @Override
    public Iterator<E> iterator() {
        return order.iterator();
    }

    @Override
    public int size() {
        return contents.size();
    }

    public E get(int index) {
        return getAt(index);
    }

    public E getAt(int index) {
        return order.get(index);
    }

    public int indexOf(Object o) {
        if (!contents.contains(o))
            return -1;
        return order.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        if (!contents.contains(o))
            return -1;
        return order.lastIndexOf(o);
    }

    public ImmutableTreeSet<E> plus(E element) {
        if (contents.contains(element))
            return this;
        return new ImmutableTreeSet<E>(contents.plus(element), order.plus(element));
    }

    public ImmutableTreeSet<E> plus(Iterable<? extends E> iterable) {
        ImmutableTreeSet<E> s = this;
        for (E e : iterable) {
            s = s.plus(e);
        }
        return s;
    }

    public ImmutableTreeSet<E> minus(Object element) {
        if (!contents.contains(element))
            return this;
        return new ImmutableTreeSet<E>(contents.minus(element), order.minus(element));
    }

    public ImmutableTreeSet<E> minus(Iterable<?> iterable) {
        ImmutableTreeSet<E> s = this;
        for (Object e : iterable) {
            s = s.minus(e);
        }
        return s;
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
