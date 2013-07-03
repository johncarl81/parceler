package org.parceler.internal;

import org.androidtransfuse.config.EnterableScope;
import org.androidtransfuse.scope.ScopeKey;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal based Scope implementation.  May only be used between enter() and exit() calls on a given thread.
 *
 * @author John Ericksen
 */
public class ThreadLocalScope implements EnterableScope {

    private final ThreadLocal<Map<ScopeKey<?>, Object>> values = new ThreadLocal<Map<ScopeKey<?>, Object>>();

    @Override
    public void enter() {
        values.set(new HashMap<ScopeKey<?>, Object>());
    }

    @Override
    public void exit() {
        values.remove();
    }

    @Override
    public <T> void seed(ScopeKey<T> key, T value) {
        Map<ScopeKey<?>, Object> scopedObjects = getScopedObjectMap(key);
        scopedObjects.put(key, value);
    }

    @Override
    public <T> T getScopedObject(final ScopeKey<T> key, final Provider<T> unscoped) {
        Map<ScopeKey<?>, Object> scopedObjects = getScopedObjectMap(key);

        @SuppressWarnings("unchecked")
        T current = (T) scopedObjects.get(key);
        if (current == null && !scopedObjects.containsKey(key)) {
            current = unscoped.get();
            scopedObjects.put(key, current);
        }
        return current;
    }

    private <T> Map<ScopeKey<?>, Object> getScopedObjectMap(ScopeKey<T> key) {
        Map<ScopeKey<?>, Object> scopedObjects = values.get();
        if (scopedObjects == null) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
        }
        return scopedObjects;
    }
}