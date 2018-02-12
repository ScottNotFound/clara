package net.scottnotfound.clara.lang;

class Return extends RuntimeException {

    final Object value;

    Return(Object value) {
        super();
        this.value = value;
    }
}
