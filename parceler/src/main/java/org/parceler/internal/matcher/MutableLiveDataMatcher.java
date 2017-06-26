package org.parceler.internal.matcher;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;
import org.androidtransfuse.util.matcher.Matchers;
import org.parceler.internal.Generators;

public class MutableLiveDataMatcher implements Matcher<ASTType> {

    private static final Matcher<ASTType> MATCHER = Matchers.type(new ASTStringType("android.arch.lifecycle.MutableLiveData")).ignoreGenerics().build();

    private final Generators generators;

    public MutableLiveDataMatcher(Generators generators) {
        this.generators = generators;
    }

    @Override
    public boolean matches(ASTType input) {
        return MATCHER.matches(input) &&
                input.getGenericArgumentTypes().size() == 1 &&
                FluentIterable.from(input.getGenericArgumentTypes()).allMatch(new Predicate<ASTType>() {
                    public boolean apply(ASTType astType) {
                        return generators.matches(astType);
                    }
                });
    }
}
