/**
 * Copyright 2013-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler.internal.matcher;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;
import org.androidtransfuse.util.matcher.Matchers;
import org.parceler.internal.Generators;

/**
 * Matches both the type of a collection and the
 *
 * @author John Ericksen
 */
public class GenericCollectionMatcher implements Matcher<ASTType> {

    private final Matcher<ASTType> collectionMatcher;
    private final Generators generators;
    private final int parameterCount;

    public GenericCollectionMatcher(ASTType collectionType, Generators generators, int parameterCount) {
        this.collectionMatcher = Matchers.type(collectionType).ignoreGenerics().build();
        this.generators = generators;
        this.parameterCount = parameterCount;
    }

    @Override
    public boolean matches(ASTType input) {

        return collectionMatcher.matches(input) &&
            input.getGenericParameters().size() == parameterCount &&
            FluentIterable.from(input.getGenericParameters()).allMatch(new Predicate<ASTType>() {
                public boolean apply(ASTType astType) {
                    return generators.matches(astType);
                }
            });
    }
}
