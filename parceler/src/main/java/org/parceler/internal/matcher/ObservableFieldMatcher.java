/**
 * Copyright 2011-2015 John Ericksen
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
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;
import org.androidtransfuse.util.matcher.Matchers;
import org.parceler.internal.Generators;

/**
 * @author John Ericksen
 */
public class ObservableFieldMatcher implements Matcher<ASTType> {

    private static final Matcher<ASTType> OBSERVABLE_FIELD_TYPE_MATCHER = Matchers.type(new ASTStringType("android.databinding.ObservableField")).ignoreGenerics().build();


    private final Generators generators;

    public ObservableFieldMatcher(Generators generators) {
        this.generators = generators;
    }

    @Override
    public boolean matches(ASTType input) {
        return OBSERVABLE_FIELD_TYPE_MATCHER.matches(input) &&
                input.getGenericParameters().size() == 1 &&
                FluentIterable.from(input.getGenericParameters()).allMatch(new Predicate<ASTType>() {
                    public boolean apply(ASTType astType) {
                        return generators.matches(astType);
                    }
                });
    }
}
