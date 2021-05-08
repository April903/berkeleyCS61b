public class ArrayDeque<T> implements Deque<T> {
    private int size;
    private static final int RFACTOR = 2;
    public T[] items;
    private int nextFirst;
    public int nextLast;

    public ArrayDeque () {
        size = 0;
        items = (T[]) new Object[8];
        nextFirst = 0;
        nextLast = 1;
    }

    public ArrayDeque (ArrayDeque other) {
        size = other.size();
        items = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            items[i] = (T) other.get(i);
        }
        nextFirst = size - 1;
        nextLast = 0;
    }

    private void resize (int newSize) {
        if (newSize < size()) {
            return;
        }

        T[] temp = (T[]) new Object[newSize + 1];
        for (int i = 1; i < size + 1; i++) {
            int idx = (nextFirst + i) % items.length;
            temp[i] = items[idx];
        }
        items = temp;
        nextFirst = 0;
        nextLast = size + 1;
    }

    @Override
    public void addFirst(T item) {
        if (size() == items.length) {
            resize (size * RFACTOR);
        }
        items[nextFirst] = item;
        nextFirst--;
        if (nextFirst < 0) {
            nextFirst += items.length;
        }
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size() == items.length) {
            resize (size * RFACTOR);
        }
        items[nextLast] = item;
        nextLast = (nextLast + 1) % items.length;

        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            int idx = (nextFirst + i + 1) % items.length;
            if (i != size - 1) {
                System.out.print(items[idx] + " ");
            }
            else {
                System.out.println(items[idx]);
            }
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        T item = get(0);
        nextFirst = (nextFirst + 1) % items.length;
        size--;

        double ratio = (double) size / items.length;
        if (items.length >= 16 && ratio < 0.25) {
            resize (size / RFACTOR);
        }

        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        T item = get(size() - 1);
        nextLast--;
        if (nextLast < 0) {
            nextLast += items.length;
        }
        size--;

        double ratio = (double) size / items.length;
        if (items.length >= 16 && ratio < 0.25) {
            resize (size / RFACTOR);
        }
        return item;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index > size() - 1) {
            return null;
        }

        return items[(nextFirst + 1 + index) % items.length];
    }
}
