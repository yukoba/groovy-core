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
class ImmutableQueueTest extends GroovyTestCase {
    void testSupportedOperation() {
        def queue = [] as ImmutableQueue<Integer>
        check([], queue)

        queue -= 1
        check([], queue)

        queue += (Integer) null
        check([null], queue)

        queue -= (Integer) null
        check([], queue)

        queue = queue + 1 + 2
        check([1, 2], queue)

        queue = queue + 1 + 2
        check([1, 2, 1, 2], queue)

        queue = queue - 2
        check([1, 1, 2], queue)

        queue = queue - 2 - 1 - 1
        check([], queue)

        queue += [1]
        queue += [2, 3]
        check([1, 2, 3], queue)

        queue -= [2, 1]
        check([3], queue)
    }

    private void check(List<Integer> answer, ImmutableQueue<Integer> queue) {
        assert answer == queue as List<Integer>

        // toString, toArray
        assert answer.toString() == queue.toString()
        assert Arrays.equals(answer as Integer[], queue.toArray())

        // equals, hashCode
        assert ImmutableCollections.queue(answer) == queue
        assert ImmutableCollections.queue(answer).hashCode() == queue.hashCode()

        // size, isEmpty
        assert answer.size() == queue.size()
        assert answer.isEmpty() == queue.isEmpty()

        // contains
        for (int i = 0; i <= 4; i++) {
            assert answer.contains(i) == queue.contains(i)
        }

        // containsAll
        assert queue.containsAll([])
        assert queue.containsAll(answer)
        if (answer.size() >= 3) {
            assert queue.containsAll([answer[0], answer.last()])
        }

        // peek, element
        if (answer.size() == 0) {
            assert null == queue.peek()
            shouldFail(NoSuchElementException){
                queue.element()
            }
        } else {
            assert answer[0] == queue.peek()
            assert answer[0] == queue.element()
        }

        // tail
        if (answer.size() == 0) {
            shouldFail(NoSuchElementException) {
                answer.tail()
            }
            shouldFail(NoSuchElementException) {
                queue.tail()
            }
        } else {
            assert answer.tail() == queue.tail() as List<Integer>
        }

        // Iterator
        Iterator<Integer> iterAnswer = answer.iterator()
        Iterator<Integer> iter = queue.iterator()
        while (true) {
            if (iterAnswer.hasNext()) {
                assert iter.hasNext()
                assert iterAnswer.next() == iter.next()
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
            ImmutableCollections.queue().add(1)
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.queue().remove(1)
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.queue().addAll([1, 2])
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.queue().removeAll([1, 2])
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.queue().retainAll([1, 2])
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.queue().clear()
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.queue().offer(1)
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.queue([0]).poll()
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.queue([0]).remove()
        }
        shouldFail(UnsupportedOperationException) {
            ImmutableCollections.queue([0]).iterator().remove()
        }
    }
}
