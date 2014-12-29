package com.github.blamevic.irc;

import java.util.Iterator;

/**
 * Supports a simple iteration over a generic collection.
 *
 * Like IEnumerator for dotNet
 *
 * @param <E> the type of elements returned by this iterator
 *
 * @see java.util.Iterator
 */
public abstract class Enumerator<E> implements Iterable<E> {
    /**
     * @return the current item in the collection
     */
    public abstract E getCurrent();

    /**
     * Moves the Enumerator to the next item in the collection
     * @return whether the Enumerator can move to the next item
     */
    public abstract boolean moveNext();

    /**
     * @see java.lang.Iterable
     */
    @Override
    public Iterator<E> iterator() {
        return new EnumeratorIterator<>(this);
    }

    /**
     * Resets the Enumerator to the beginning of the collection
     *
     * @throws java.lang.UnsupportedOperationException when the reset
     *     operation is not supported by the underlying collection
     */
    public void reset() {
        throw new UnsupportedOperationException("reset");
    }

    public static class EnumeratorIterator<T> implements Iterator<T> {
        private Enumerator<T> enumerator;

        private T next;

        public EnumeratorIterator(Enumerator<T> enumerator) {
            this.enumerator = enumerator;
        }

        @Override
        public boolean hasNext() {
            setNext();

            return this.next != null;
        }

        @Override
        public T next() {
            setNext();

            T theNext = this.next;
            this.next = null;
            return theNext;
        }

        private void setNext() {
            if (this.next == null) {
                if (enumerator.moveNext()) {
                    this.next = enumerator.getCurrent();
                }
            }
        }
    }
}
