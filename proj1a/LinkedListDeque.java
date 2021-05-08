public class LinkedListDeque<T> {

    private static class Node<T> {
        public T data;
        public Node<T> prev;
        public Node<T> next;

        public Node (T item, Node<T> p, Node<T> n) {
            data = item;
            prev = p;
            next = n;
        }
    }

    private Node<T> sentinel;
    private int size;

    public LinkedListDeque () {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public LinkedListDeque (LinkedListDeque other) {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;

        for (int i = 0; i < other.size(); i++) {
            addLast((T) other.get(i));
        }
    }

    public void addFirst (T item) {
        Node<T> temp = sentinel.next;
        sentinel.next = temp.prev = new Node (item, sentinel, sentinel.next);
        size++;
    }

    public void addLast (T item) {
        Node<T> temp = sentinel.prev;
        sentinel.prev = temp.next = new Node (item, sentinel.prev, sentinel);
        size++;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node<T> curr = sentinel.next;
        while (curr != sentinel) {
            if (curr.next == sentinel) {
                System.out.println(curr.data);
            }
            else {
                System.out.print(curr.data + " ");
            }
            curr = curr.next;
        }
    }

    public T removeFirst() {
        if (sentinel.next != sentinel) {
            size--;
            sentinel.next = sentinel.next.next;
            sentinel.next.prev = sentinel;
            return sentinel.next.data;
        }
        else {
            return null;
        }
    }

    public T removeLast() {
        if (sentinel.prev != sentinel) {
            size--;
            sentinel.prev = sentinel.prev.prev;
            sentinel.prev.next = sentinel;
            return sentinel.prev.data;
        }
        else {
            return null;
        }
    }

    public T get (int index) {

        if (index >= size) {
            return null;
        }

        Node<T> curr = sentinel;
        int mid = size() / 2;
        int i = -1;
        if (index < mid){
            while (i < index) {
                curr = curr.next;
                i++;
            }
        }

        else {
            while (i < size() - 1 - index) {
                curr = curr.prev;
                i++;
            }
        }

        return curr.data;
    }

    public T getRecursive (int index) {
        if (index >= size) {
            return null;
        }

        if (index == 0) {
            return sentinel.next.data;
        } else {
            LinkedListDeque<T> temp = new LinkedListDeque<>(this);
            temp.removeFirst();
            return temp.getRecursive(index - 1);
        }
    }
}
