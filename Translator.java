package ch.mobi.tuto.translator;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
/*
 * The goal of this exercise is to translate a code in words:
 * translate("1") -> "<one>"
 * translate("123") -> "<one><two><three>"
 * translate("12341513") -> "<one><two><three><?><one><three>"
 * 
 * Only 1,2 and 3 digits are translated, so the translation algorithm is made of four different treatments:
 * 1 -> "<one>"
 * 2 -> "<two>"
 * 3 -> "<three>"
 * others -> "<?>"
 */

/**
 * Imperative way.
 * 
 * Performance: *** | The input String is read only once.
 * Readability: *   | Cognitive complexity of the algorithm = 9
 * Coupling: *      | All treatments of the algorithm are in the same method.
 */
class ImperativeTranslator {
    String translate(String code) {
        StringBuilder buffer = new StringBuilder();
        for (char c: code.toCharArray()) {
            if(c == '1') {
                buffer.append("<one>");
            }
            else if(c == '2') {
                buffer.append("<two>");
            }
            else if(c == '3') {
                buffer.append("<three>");
            }
            else {
                buffer.append("<?>");
            }
        }
        return buffer.toString();
    }

    @Test
    void shouldTranslate() {
        assertEquals("<one><two><three><?><one><three>", translate("123413"));
    }
}

/**
 * Functional way.
 *
 * Performance: **  | The input String is read 4 time.
 * Readability: **  | Cognitive complexity of the algorithm = 1 but it will increase if the algorithm has numerous or complex treatments.
 * Coupling:    *   | All treatments of the algorithm are in the same method.
 */
class FunctionalTranslator {
    
    String translate(String code) { // translate with a simple chain of methods.
        return code.replace("1", "<one>")
                .replace("2", "<two>")
                .replace("3", "<three>")
                .replaceAll("[0-9]", "<?>");
    }

    @Test
    void shouldTranslate() {
        assertEquals("<one><two><three><?><one><three>", translate("123413"));
    }

}

/**
 * Simple chain of responsibility.
 *
 * Performance: *   | The input String is read 4 time and 5 classes are instanciated.
 * Readability: *** | Cognitive complexity of the algorithm = 1 and it will not increase if the algorithm has numerous or complex treatments.
 * Coupling:    *** | All treatments of the algorithm are in separated classes.
 */
interface Handler { // Interface for links in the chain.
    String translate(String code);
}

class ChainOfResponsibilityTranslator {

    // English translators are in separated classes.
    private Handler e1 = i -> i.replace("1", "<one>");
    private Handler e2 = i -> i.replace("2", "<two>");
    private Handler e3 = i -> i.replace("3", "<three>");
    private Handler other = i -> i.replaceAll("[0-9]", "<?>");

    String translate(String code) { // translate with a chain of classes (containing complex treatments).
        return other.translate(e3.translate(e2.translate(e1.translate(code))));
    }

    @Test
    void shouldTranslate() {
        assertEquals("<one><two><three><?><one><three>", translate("123413"));
    }

}

/**
 * Configurable chain of responsibility using {@link Function}. 
 *
 * Performance: *   | The input String is read 4 time and many classes are instanciated.
 * Readability: *** | Cognitive complexity of the algorithm = 1.
 * Coupling:    *** | All treatments of the algorithm are in separated classes.
 * Flexibility: *** | We can compose algorithm "on the fly".
 */
interface FunctionHandler extends Function<String, String> {} // Interface for links in the chain.

class ChainTranslator {

    // English translators are in separated classes.
    private FunctionHandler e1 = i -> i.replace("1", "<one>");
    private FunctionHandler e2 = i -> i.replace("2", "<two>");
    private FunctionHandler e3 = i -> i.replace("3", "<three>");
    // French  translators are in separated classes.
    private FunctionHandler f1 = i -> i.replace("1", "<un>");
    private FunctionHandler f2 = i -> i.replace("2", "<deux>");
    private FunctionHandler f3 = i -> i.replace("3", "<trois>");

    private FunctionHandler other = i -> i.replaceAll("[0-9]", "<?>");

    String translate(String i, Function<String, String> chain) { // translate with the chain in parameter.
        return chain.apply(i);
    }
    
    @Test
    void shouldTranslate() {
        var englishChain = e1.andThen(e2).andThen(e3).andThen(other); // chaining treatments using Function interface.
        assertEquals("<one><two><three><?><one><three>", translate("123413", englishChain));
        
        var frenchChain = f1.andThen(f2).andThen(f3);
        assertEquals("<un><deux><trois><deux><un>", translate("12321", frenchChain));

        var mixedChain = e1.andThen(f2).andThen(i -> i.replace("3", "<drei>")); // Add treatment "on the fly".
        assertEquals("<one><deux><drei><deux><one>", translate("12321", mixedChain));
    }

}
