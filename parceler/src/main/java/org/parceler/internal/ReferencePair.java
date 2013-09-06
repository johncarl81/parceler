package org.parceler.internal;

/**
 * @author John Ericksen
 */
public class ReferencePair<T extends Reference> {

    private final String name;
    private final T setter;
    private final Reference accessor;

    public ReferencePair(String name, T setter, Reference accessor) {
        this.name = name;
        this.setter = setter;
        this.accessor = accessor;
    }

    public String getName() {
        return name;
    }

    public T getSetter() {
        return setter;
    }

    public Reference getAccessor() {
        return accessor;
    }
}
