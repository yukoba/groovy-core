/*
 * Copyright 2003-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.tools.shell.completion

import org.codehaus.groovy.antlr.GroovySourceToken

/**
 * Completor completing groovy keywords that appear after identifiers
 */
class InfixKeywordSyntaxCompletor implements IdentifierCompletor {

    // INFIX keywords can only occur after identifiers
    private static final String[] INFIX_KEYWORDS = [
            'in',
            'instanceof',
            'extends',
            'implements',
            ]

    @Override
    boolean complete(final List<GroovySourceToken> tokens, final List<CharSequence> candidates) {
        String prefix = tokens.last().text
        boolean foundMatch = false
        for (String varName in INFIX_KEYWORDS) {
            if (varName.startsWith(prefix)) {
                candidates << varName
                foundMatch = true
            }
        }
        return foundMatch
    }
}
