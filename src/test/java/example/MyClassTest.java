package example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyClassTest {
    @Test
    void myFunction() {
        MyClass myClass = new MyClass();
        String result = myClass.myFunction("Java");
        assertTrue(result.contains("Java"));
    }
}