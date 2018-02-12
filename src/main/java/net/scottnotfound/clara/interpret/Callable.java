package net.scottnotfound.clara.interpret;

import java.util.List;

public interface Callable {

    Object call(Interpreter interpreter, List<Object> arguments);

    int arity();

}
