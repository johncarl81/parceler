package org.parceler.converter;

import java.util.LinkedList;

/**
 * @author John Ericksen
 */
public abstract class LinkedListParcelConverter<T> extends CollectionParcelConverter<T, LinkedList<T>> {
    @Override
    public LinkedList<T> createCollection() {
        return new LinkedList<T>();
    }
}
