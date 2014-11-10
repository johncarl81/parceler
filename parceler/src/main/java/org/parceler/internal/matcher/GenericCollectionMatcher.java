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
