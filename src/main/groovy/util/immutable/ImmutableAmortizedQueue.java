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
 * @author mtklein
 * @author Yu Kobayashi
 * @since 2.4.0
 */
final class ImmutableAmortizedQueue<E> extends AbstractQueue<E> implements ImmutableQueue<E>, Serializable {
    private static final ImmutableAmortizedQueue<Object> EMPTY = new ImmutableAmortizedQueue<Object>();
    private static final long serialVersionUID = 5550057968536499274L;

    @SuppressWarnings("unchecked")
    public static <E> ImmutableAmortizedQueue<E> empty() {
        return (ImmutableAmortizedQueue<E>) EMPTY;
    }

    public static <E> ImmutableAmortizedQueue<E> singleton(E e) {
        return ImmutableAmortizedQueue.<E>empty().plus(e);
    }

    public static <E> ImmutableAmortizedQueue<E> from(Iterable<? extends E> iterable) {
        return ImmutableAmortizedQueue.<E>empty().plus(iterable);
    }

    private final ImmutableStack<E> front;
    private final ImmutableStack<E> back;

    private ImmutableAmortizedQueue() {
        front = ImmutableCollections.<E>stack();
        back = ImmutableCollections.<E>stack();
    }

    private ImmutableAmortizedQueue(ImmutableAmortizedQueue<E> queue, E e) {
        // Guarantee that there is always at least 1 element in front, which makes peek worst-case O(1).
        if (queue.front.size() == 0) {
            this.front = queue.front.plus(e);
            this.back = queue.back;
        } else {
            this.front = queue.front;
            this.back = queue.back.plus(e);
        }
    }

    private ImmutableAmortizedQueue(ImmutableStack<E> front, ImmutableStack<E> back) {
        this.front = front;
        this.back = back;
    }

    // Worst-case O(n)
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private ImmutableQueue<E> queue = ImmutableAmortizedQueue.this;

            public boolean hasNext() {
                return queue.size() > 0;
            }

            public E next() {
                if (!hasNext())
                    throw new NoSuchElementException();

                E e = queue.peek();
                queue = queue.tail();
                return e;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (E e : this) {
            hashCode += (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object that) {
        if (!(that instanceof ImmutableQueue))
            return false;
        ImmutableQueue queue = (ImmutableQueue) that;

        if (size() != queue.size())
            return false;

        if (size() == 0) {
            return queue.size() == 0;
        } else {
            Object obj1 = peek();
            Object obj2 = queue.peek();
            return (obj1 == null ? obj2 == null : obj1.equals(obj2)) && tail().equals(queue.tail());
        }
    }

    // Worst-case O(1)
    @Override
    public int size() {
        return front.size() + back.size();
    }

    // Worst-case O(1)
    public E peek() {
        if (size() == 0)
            return null;
        return front.get(0);
    }

    // Worst-case O(1)
    public E element() {
        if (size() == 0)
            throw new NoSuchElementException();
        return front.get(0);
    }

    // Amortized O(1), worst-case O(n)
    public ImmutableAmortizedQueue<E> tail() {
        if (size() == 0)
            throw new NoSuchElementException();

        int fsize = front.size();

        if (fsize == 0) {
            //If there's nothing on front, dump back onto front
            //(as stacks, this goes in reverse like we want)
            //and take one off.
            return new ImmutableAmortizedQueue<E>(ImmutableCollections.<E>stack().plus(back), ImmutableCollections.<E>stack()).tail();
        } else if (fsize == 1) {
            //If there's one element on front, dump back onto front,
            //but now we've already removed the head.
            return new ImmutableAmortizedQueue<E>(ImmutableCollections.<E>stack().plus(back), ImmutableCollections.<E>stack());
        } else {
            //If there's more than one on front, we pop one off.
            return new ImmutableAmortizedQueue<E>(front.minusAt(0), back);
        }
    }

    // Worst-case O(1)
    public ImmutableAmortizedQueue<E> plus(E element) {
        return new ImmutableAmortizedQueue<E>(this, element);
    }

    // Worst-case O(k)
    public ImmutableAmortizedQueue<E> plus(Iterable<? extends E> iterable) {
        ImmutableAmortizedQueue<E> result = this;
        for (E e : iterable) {
            result = result.plus(e);
        }
        return result;
    }

    // These 2 methods not guaranteed to be fast.

    public ImmutableAmortizedQueue<E> minus(Object element) {
        ArrayList<E> list = new ArrayList<E>(this);
        list.remove(element);
        return from(list);
    }

    public ImmutableAmortizedQueue<E> minus(Iterable<?> iterable) {
        ArrayList<E> list = new ArrayList<E>(this);
        list.removeAll(DefaultGroovyMethods.asCollection(iterable));
        return from(list);
    }

    @Deprecated
    public boolean offer(E o) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public E poll() {
        throw new UnsupportedOperationException();
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
