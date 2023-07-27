import java.util.*;

public class Main
{
    static int value = 0;
    
    public static void main(String[] args) {
        String status = Optional.of(1).map(i -> setValue(i)).orElse(setDefaultValue());
        System.out.println("Status -> " + status);
        System.out.println("Value -> " + value);
    }
    
    private static String setDefaultValue() {
        setValue(99);
        return "DEFAULTED WITH " + 99;
    }
    
    private static String setValue(int input) {
        value = input;
        return "UPDATED WITH " + input;
    }
    
}
