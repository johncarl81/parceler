package org.parceler.internal;

import org.androidtransfuse.adapter.ASTAccessModifier;
import org.androidtransfuse.gen.invocationBuilder.InvocationBuilderStrategy;
import org.androidtransfuse.gen.invocationBuilder.ModifierInjectionBuilder;
import org.androidtransfuse.gen.invocationBuilder.PrivateInjectionBuilder;
import org.androidtransfuse.gen.invocationBuilder.PublicInjectionBuilder;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author John Ericksen
 */
public class ParcelerInvocationBuilderStrategy implements InvocationBuilderStrategy {

    private final Provider<PublicInjectionBuilder> publicProvider;
    private final Provider<PrivateInjectionBuilder> privateProvider;

    @Inject
    public ParcelerInvocationBuilderStrategy(Provider<PublicInjectionBuilder> publicProvider,
                                            Provider<PrivateInjectionBuilder> privateProvider) {
        this.publicProvider = publicProvider;
        this.privateProvider = privateProvider;
    }

    @Override
    public ModifierInjectionBuilder getInjectionBuilder(ASTAccessModifier modifier) {
        switch (modifier) {
            case PUBLIC:
            case PACKAGE_PRIVATE:
            case PROTECTED:
                return publicProvider.get();
            default:
                return privateProvider.get();
        }
    }
}
