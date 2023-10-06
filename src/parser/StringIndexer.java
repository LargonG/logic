package parser;

public class StringIndexer {
    private final String value;
    private int cursor;


    public StringIndexer(String value) {
        this.value = value;
        this.cursor = 0;
    }

    public StringIndexer(String value, int startIndex) {
        this.value = value;
        this.cursor = startIndex;
    }

    public int read() {
        if (cursor >= value.length()) {
            return -1;
        }
        return value.charAt(cursor++) & 0xff;
    }

    public int readSymbol() {
        while (cursor < value.length() && Character.isWhitespace(value.codePointAt(cursor))) {
            cursor++;
        }

        return read();
    }

    public void back() {
        cursor--;
    }

    public void debug() {
        System.out.println("StringIndexer:"
                + "\nPosition: " + position()
                + "\nString: " + value
        );
    }

    /**
     * @param start inclusive
     * @param end   exclusive
     * @return substring
     */
    public String substring(int start, int end) {
        return value.substring(start, end);
    }

    public int position() {
        return cursor;
    }
}
