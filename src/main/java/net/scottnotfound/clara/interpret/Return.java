package net.scottnotfound.clara.interpret;

class Return extends RuntimeException {

    final Object value;

    Return(Object value) {
        super();
        this.value = value;
    }
}
