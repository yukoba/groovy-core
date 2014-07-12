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

import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * An immutable, persistent queue.
 * The elements can be null.
 * <p/>
 * You can create an instance by {@code [] as ImmutableQueue}.
 * <p/>
 * Example:
 * <pre class="groovyTestCase">
 * def queue = [] as ImmutableQueue
 * queue += 1
 * assert 1 == queue.peek()
 * queue -= [1]
 * assert 0 == queue.size()
 * </pre>
 *
 * @author mtklein
 * @author Yu Kobayashi
 * @since 2.4.0
 */
public interface ImmutableQueue<E> extends ImmutableCollection<E>, Queue<E> {
    /**
     * Complexity: O(1)
     *
     * @return the first element of this queue
     */
    E peek();

    /**
     * Complexity: O(1)
     *
     * @return the first element of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    E element();

    /**
     * Complexity: amortized O(1), worst-case O(n)
     *
     * @return a queue without its first element
     * @throws NoSuchElementException if this queue is empty
     */
    ImmutableQueue<E> tail();

    /**
     * Complexity: O(1)
     *
     * @param element an element to append
     * @return a queue which contains the element and all of the elements of this
     */
    ImmutableQueue<E> plus(E element);

    /**
     * Complexity: O(iterable.size())
     *
     * @param iterable elements to append
     * @return a queue which contains all of the elements of iterable and this
     */
    ImmutableQueue<E> plus(Iterable<? extends E> iterable);

    /**
     * Complexity: O(n)
     *
     * @param element an element to remove
     * @return this with a single instance of the element removed, if the element is in this queue
     */
    ImmutableQueue<E> minus(Object element);

    /**
     * Complexity: O(n + iterable.size())
     *
     * @param iterable elements to remove
     * @return this with all elements of the iterable completely removed
     */
    ImmutableQueue<E> minus(Iterable<?> iterable);

    /**
     * Always throws {@link UnsupportedOperationException}.
     */
    @Deprecated
    boolean offer(E e);

    /**
     * Always throws {@link UnsupportedOperationException}.
     */
    @Deprecated
    E poll();

    /**
     * Always throws {@link UnsupportedOperationException}.
     */
    @Deprecated
    E remove();
}
