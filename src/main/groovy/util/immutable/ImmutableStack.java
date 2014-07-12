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

/**
 * An immutable, persistent stack.
 * The elements can be null.
 * <p/>
 * You can create an instance by {@code [] as ImmutableStack}.
 * <p/>
 * Example:
 * <pre class="groovyTestCase">
 * def stack = [] as ImmutableStack
 * stack += 1
 * assert 1 == stack[0]
 * stack -= [1]
 * assert 0 == stack.size()
 * </pre>
 *
 * @author harold
 * @author Yu Kobayashi
 * @since 2.4.0
 */
public interface ImmutableStack<E> extends ImmutableList<E> {
    /**
     * Same of {@link #get(int)}.
     * <p/>
     * Complexity: O(n)
     *
     * @param index index of the element to return
     * @return the element at the specified position in this stack
     * @throws IndexOutOfBoundsException if index &lt; 0 || index &gt;= size()
     */
    E getAt(int index);

    /**
     * Complexity: O(n)
     *
     * @param index index of the element to return
     * @return the element at the specified position in this stack
     * @throws IndexOutOfBoundsException if index &lt; 0 || index &gt;= size()
     */
    E get(int index);

    /**
     * Returns a stack consisting of the elements of this with e prepended.
     * <p/>
     * Complexity: O(1)
     *
     * @param element an element to append
     * @return a stack which contains the element and all of the elements of this
     */
    ImmutableStack<E> plus(E element);

    /**
     * Returns a stack consisting of the elements of this with list prepended in reverse.
     * <p/>
     * Complexity: O(list.size())
     *
     * @param iterable elements to append
     * @return a stack which contains all of the elements of iterable and this
     */
    ImmutableStack<E> plus(Iterable<? extends E> iterable);

    /**
     * Complexity: O(n)
     *
     * @param index   an index to insert
     * @param element an element to insert
     * @return a stack consisting of the elements of this with the element inserted at the specified index.
     * @throws IndexOutOfBoundsException if index &lt; 0 || index &gt; size()
     */
    ImmutableStack<E> plusAt(int index, E element);

    /**
     * Complexity: O(n + list.size())
     *
     * @param index    an index to insert
     * @param iterable elements to insert
     * @return a stack consisting of the elements of this with the iterable inserted at the specified index.
     * @throws IndexOutOfBoundsException if index &lt; 0 || index &gt; size()
     */
    ImmutableStack<E> plusAt(int index, Iterable<? extends E> iterable);

    /**
     * Complexity: O(n)
     *
     * @param index   an index to replace
     * @param element an element to replace
     * @return a stack consisting of the elements of this with the element replacing at the specified index.
     * @throws IndexOutOfBoundsException if index &lt; 0 || index &gt;= size()
     */
    ImmutableStack<E> replaceAt(int index, E element);

    /**
     * Complexity: O(n)
     *
     * @param element an element to remove
     * @return this with a single instance of the element removed, if the element is in this list
     */
    ImmutableStack<E> minus(Object element);

    /**
     * Complexity: O(n * list.size())
     *
     * @param iterable elements to remove
     * @return this with all elements of the iterable completely removed
     */
    ImmutableStack<E> minus(Iterable<?> iterable);

    /**
     * Complexity: O(n)
     *
     * @param index an index to remove
     * @return a stack consisting of the elements of this with the element at the specified index removed.
     * @throws IndexOutOfBoundsException if index &lt; 0 || index &gt; size()
     */
    ImmutableStack<E> minusAt(int index);

    /**
     * Complexity: O(n)
     *
     * @param start a start index
     * @param end   a end index
     * @return a view of the specified range within this list
     */
    ImmutableStack<E> subList(int start, int end);

    /**
     * Complexity: O(n)
     *
     * @param start a start index
     * @return subList(start, this.size())
     */
    ImmutableStack<E> subList(int start);
}
