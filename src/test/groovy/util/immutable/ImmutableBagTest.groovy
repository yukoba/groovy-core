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

package groovy.util.immutable

/**
 * @author Yu Kobayashi
 */
class ImmutableBagTest extends GroovyTestCase {
    void testSupportedOperation() {
        ImmutableBag<Integer> bag = ImmutableCollections.bag()
        check([], bag)

        shouldFail(NullPointerException) {
            bag + (Integer) null
        }

        bag -= 1
        check([], bag)

        bag = bag + 1 + 2
        check([1, 2], bag)

        bag = bag + 1 + 2
        check([1, 2, 1, 2], bag)

        bag = bag - 1 - 2 - 2
        check([1], bag)

        bag += [2, 3]
        check([1, 2, 3], bag)

        bag -= [2, 1]
        check([3], bag)
    }

    private void check(List<Integer> answer, ImmutableBag<Integer> bag) {
        // toString, toArray
        if (answer.size() == (answer as Set).size()) {
            assert answer.toString() == bag.toString()
            assert Arrays.equals(answer as Integer[], bag.toArray())
        }

        // equals, hashCode
        assert ImmutableCollections.bag(answer) == bag
        assert ImmutableCollections.bag(answer).hashCode() == bag.hashCode()

        // size, isEmpty
        assert answer.size() == bag.size()
        assert answer.isEmpty() == bag.isEmpty()

        // contains
        for (int i = 0; i <= 4; i++) {
            assert answer.contains(i) == bag.contains(i)
        }

        // containsAll
        assert bag.containsAll([])
        assert bag.containsAll(answer)
        if (answer.size() >= 3) {
            assert bag.containsAll([answer[0], answer.last()])
        }

        // Iterator
        Iterator iterAnswer = answer.iterator()
        Iterator iter = bag.iterator()
        while (true) {
            if (iterAnswer.hasNext()) {
                assert iter.hasNext()
                assert answer.contains(iter.next())
                iterAnswer.next()
            } else {
                shouldFail(NoSuchElementException) {
                    iter.next()
                }
                break
            }
        }
    }

    @SuppressWarnings("GrDeprecatedAPIUsage")
    void testUnsupportedOperation() {
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.bag().add(1)
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.bag().remove(1)
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.bag().addAll([1, 2])
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.bag().removeAll([1, 2])
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.bag().retainAll([1, 2])
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.bag().clear()
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.bag([0]).iterator().remove()
        }
    }
}
