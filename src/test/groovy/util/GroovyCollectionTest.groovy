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

package groovy.util

/**
 * @author Yu Kobayashi
 */
class GroovyCollectionTest extends GroovyTestCase {
    void testAsType() {
        def c1 = [1, 2, 3] as SizeOneCollection<Integer>
        assert 1 == c1.size()
        assert 3 == c1[0]
    }

    void testPlus() {
        def c1 = [1, 2, 3] as SizeOneCollection<Integer>
        def c2 = [4, 5, 6] as SizeOneCollection<Integer>
        def c3 = c1 + c2
        assert c3 instanceof SizeOneCollection
        assert 1 == c3.size()
        assert 6 == c3[0]
    }

    void testCollect() {
        def c1 = [1, 2, 3] as SizeOneCollection<Integer>
        def c2 = c1.collect { it + 1 }
        assert 1 == c2.size()
        assert 4 == c2[0]
    }

    void testEach() {
        def c1 = [1, 2, 3] as SizeOneCollection<Integer>
        c1.each { assert 3 == it }
        c1 = [] as SizeOneCollection<Integer>
        c1.each { assert false }
    }

    static class SizeOneCollection<E> implements GroovyCollection<E> {
        E e

        public SizeOneCollection<E> createSimilar() {
            new SizeOneCollection<E>()
        }

        int size() {
            e == null ? 0 : 1
        }

        boolean isEmpty() {
            e == null
        }

        boolean contains(Object o) {
            e == o
        }

        Iterator iterator() {
            e == null ? [].iterator() : [e].iterator()
        }

        Object[] toArray() {
            e == null ? [] as Object[] : [e] as Object[]
        }

        def <T> T[] toArray(T[] a) {
            if (e == null) {
                return a
            } else {
                if (a.length >= 1) {
                    a[0] = (T) e
                    return a
                } else {
                    (T[]) ([e] as Object[])
                }
            }
        }

        boolean add(Object o) {
            e = (E) o
            true
        }

        boolean remove(Object o) {
            if (e == o) {
                e = null
                true
            } else {
                false
            }
        }

        boolean addAll(Collection c) {
            c.size() > 0 ? add(c.last()) : false
        }

        void clear() {
            e = null
        }

        boolean retainAll(Collection c) {
            if (c.contains(e)) {
                false
            } else {
                e = null
                true
            }
        }

        boolean removeAll(Collection c) {
            c.each { remove(it) }
        }

        boolean containsAll(Collection c) {
            return e != null && c.contains(e)
        }
    }
}
