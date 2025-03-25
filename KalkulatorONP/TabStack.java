public class TabStack {
    private String[] stack = new String[20];
    private int size = 0;

    public String pop() {
        if (size == 0) {
            throw new IllegalStateException("Błąd: Próba zdjęcia elementu z pustego stosu.");
        }
        size--;
        return stack[size];
    }

    public void push(String a) {
        if (size >= stack.length) {
            throw new ArrayIndexOutOfBoundsException("Błąd: Przepełnienie stosu.");
        }
        stack[size] = a;
        size++;
    }

    public String toString() {
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; i < size; i++) {
            tmp.append(stack[i]).append(" ");
        }
        return tmp.toString();
    }

    public int getSize() {
        return size;
    }

    public String showValue(int i) {
        if (i < size) {
            return stack[i];
        } else {
            return null;
        }
    }
}
