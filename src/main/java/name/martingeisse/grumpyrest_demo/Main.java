package name.martingeisse.grumpyrest_demo;

import name.martingeisse.grumpyjson.JsonEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public record TestRecord(int foo, String bar) {}

    public record AnotherRecord(TestRecord a, TestRecord b, int c) {}

    public static void main(String[] args) throws Exception {
        // Launcher.launch();

        JsonEngine engine = new JsonEngine();
        System.out.println(engine.stringify(
                new AnotherRecord(new TestRecord(42, "xxx"), new TestRecord(123, "bla"), 99)));

        System.out.println(engine.parse("{}", TestRecord.class));
    }

}
