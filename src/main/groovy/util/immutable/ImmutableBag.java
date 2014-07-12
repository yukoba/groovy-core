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
 * An immutable, persistent unordered collection allowing duplicate elements.
 * The elements must be non-null.
 * <p/>
 * You can create an instance by {@code [] as ImmutableBag}.
 * <p/>
 * Example:
 * <pre class="groovyTestCase">
 * def bag = [] as ImmutableBag
 * bag += 1
 * assert bag.contains(1)
 * bag -= [1]
 * assert !bag.contains(1)
 * </pre>
 *
 * @author harold
 * @author Yu Kobayashi
 * @since 2.4.0
 */
public interface ImmutableBag<E> extends ImmutableCollection<E> {
    /**
     * Complexity: O(log n)
     *
     * @param element an non-null element to append
     * @return a bag which contains the element and all of the elements of this
     */
    ImmutableBag<E> plus(E element);

    /**
     * Complexity: O((log n) * iterable.size())
     *
     * @param iterable contains non-null elements to append
     * @return a bag which contains all of the elements of iterable and this
     */
    ImmutableBag<E> plus(Iterable<? extends E> iterable);

    /**
     * Complexity: O(log n)
     *
     * @param element an element to remove
     * @return this with a single instance of the element removed, if the element is in this bag
     */
    ImmutableBag<E> minus(Object element);

    /**
     * Complexity: O((log n) * iterable.size())
     *
     * @param iterable elements to remove
     * @return this with all elements of the iterable completely removed
     */
    ImmutableBag<E> minus(Iterable<?> iterable);
}
