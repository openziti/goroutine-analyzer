package goroutine.analyzer;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class StackDumpParserTest {
    @Test
    public void testStackDump() throws Exception {
        var path = Paths.get("/home", "plorenz", "tmp", "fabric-freeze", "1", "6dBmMM4MR.0.dump");
        StackDumpParser parser = new StackDumpParser();
        Files.lines(path).forEach(line -> parser.processLine(line));
        Assert.assertEquals(288, parser.getResults().size());

        for (var stack : parser.getResults()) {
            stack.dump();
            System.out.println();
        }
    }
}
