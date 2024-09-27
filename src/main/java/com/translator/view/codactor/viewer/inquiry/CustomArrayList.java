package com.translator.view.codactor.viewer.inquiry;

import java.util.*;

public class CustomArrayList<E> extends ArrayList<E> {
    public CustomArrayList() {
        super();
    }

    @Override
    public void add(int index, E element) {
        System.out.println("Adding element at index " + index + ": " + element);
        super.add(index, element);
    }

    @Override
    public boolean add(E e) {
        System.out.println("Adding element: " + e);
        return super.add(e);
    }

    @Override
    public E remove(int index) {
        E removedElement = super.remove(index);
        System.out.println("Removing element at index " + index + ": " + removedElement);
        return removedElement;
    }

    @Override
    public boolean remove(Object o) {
        System.out.println("Removing element: " + o);
        return super.remove(o);
    }

    @Override
    public void clear() {
        System.out.println("Clearing list");
        super.clear();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        System.out.println("Removing all elements in collection: " + c);
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        System.out.println("Retaining all elements in collection: " + c);
        return super.retainAll(c);
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        System.out.println("removeRange called from " + fromIndex + " to " + toIndex);
        super.removeRange(fromIndex, toIndex);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<E> itr = CustomArrayList.super.iterator();
            private int currentIndex = -1;

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public E next() {
                currentIndex++;
                return itr.next();
            }

            @Override
            public void remove() {
                System.out.println("Iterator removing element at index: " + currentIndex);
                itr.remove();
            }
        };
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIterator<E>() {
            private final ListIterator<E> itr = CustomArrayList.super.listIterator();

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public E next() {
                return itr.next();
            }

            @Override
            public boolean hasPrevious() {
                return itr.hasPrevious();
            }

            @Override
            public E previous() {
                return itr.previous();
            }

            @Override
            public int nextIndex() {
                return itr.nextIndex();
            }

            @Override
            public int previousIndex() {
                return itr.previousIndex();
            }

            @Override
            public void remove() {
                System.out.println("ListIterator removing element at index: " + itr.nextIndex());
                itr.remove();
            }

            @Override
            public void set(E e) {
                itr.set(e);
            }

            @Override
            public void add(E e) {
                itr.add(e);
            }
        };
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        final List<E> sub = super.subList(fromIndex, toIndex);
        return new AbstractList<E>() {
            @Override
            public E get(int index) {
                return sub.get(index);
            }

            @Override
            public int size() {
                return sub.size();
            }

            @Override
            public E remove(int index) {
                E removedElement = sub.remove(index);
                System.out.println("Removing element from subList at index " + index + ": " + removedElement);
                return removedElement;
            }

            @Override
            public void clear() {
                System.out.println("Clearing subList");
                sub.clear();
            }

            // Override other methods as needed
        };
    }
}
