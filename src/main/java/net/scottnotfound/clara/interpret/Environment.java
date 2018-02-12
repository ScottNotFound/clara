package net.scottnotfound.clara.interpret;

import java.util.HashMap;
import java.util.Map;

class Environment {

    private final Map<String, Object> values = new HashMap<>();
    private final Environment enclosing;

    Environment() {
        this.enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String key, Object value) {
        values.put(key, value);
    }

    Object get(Token token) {
        if (values.containsKey(token.lexeme)) {
            return values.get(token.lexeme);
        }
        if (enclosing != null) {
            return enclosing.get(token);
        }
        return null;
    }

    void assign(Token token, Object value) {
        if (values.containsKey(token.lexeme)) {
            values.put(token.lexeme, value);
            return;
        }
        if (enclosing != null) {
            enclosing.assign(token, value);
            return;
        }
        throw new RuntimeError(token, "Undefined variable '" + token.lexeme + "'.");
    }

    Object getAt(Integer distance, String lexeme) {
        return ancestor(distance).values.get(lexeme);
    }

    Environment ancestor(Integer distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }

    void assignAt(Integer distance, Token token, Object value) {
        ancestor(distance).values.put(token.lexeme, value);
    }
}
