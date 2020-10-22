package goroutine.analyzer;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class StackDumpParserTest {
    @Test
    public void testStackDump() throws Exception {
        var path = Paths.get("/home", "plorenz", "tmp", "fabric-freeze", "1", "6dBmMM4MR.0.dump");
        StackDumpParser parser = new StackDumpParser();
        Files.lines(path).forEach(parser::processLine);
        Assert.assertEquals(288, parser.getResults().size());

        for (var stack : parser.getResults()) {
            stack.dump();
            System.out.println();
        }
    }

    String dots = """
            goroutine 1257 [chan receive]:
            github.com/openziti/sdk-golang/ziti/edge.(*MsgChannel).WriteTraced(0xc0002c8bd0, 0xc0232c553e, 0x2800, 0x2800, 0x0, 0x0, 0x0, 0x0, 0x2800, 0x0, ...)
            	/home/plorenz/work/nf/sdk-golang/ziti/edge/conn.go:148 +0x42e
            github.com/openziti/edge/router/xgress_edge.(*localMessageSink).WritePayload(0xc0002c8bd0, 0xc0232c553e, 0x2800, 0x2800, 0x0, 0x1, 0x1, 0xc01fbcd220)
            	/home/plorenz/work/nf/edge/router/xgress_edge/fabric.go:97 +0xa5
            github.com/openziti/fabric/router/xgress.(*Xgress).tx(0xc000c406e0)
            	/home/plorenz/work/nf/fabric/router/xgress/xgress.go:309 +0x274
            created by github.com/openziti/fabric/router/xgress.(*Xgress).Start
            	/home/plorenz/work/nf/fabric/router/xgress/xgress.go:182 +0x10b

            """;

    @Test
    public void testDotsStackDump() {
        StackDumpParser parser = new StackDumpParser();
        Arrays.stream(dots.split("\n")).forEach(parser::processLine);
        var results = parser.getResults();
        Assert.assertEquals(1, parser.getResults().size());
        var stack = results.get(0);
        Assert.assertEquals(4, stack.elements.size());

        var element = stack.elements.get(0);
        System.out.println(element.toString());
        Assert.assertEquals("github.com/openziti/sdk-golang/ziti/edge", element.pkg);
        Assert.assertEquals("edge", element.shortPkg);
        Assert.assertEquals("(*MsgChannel)", element.receiver);
        Assert.assertEquals("/home/plorenz/work/nf/sdk-golang/ziti/edge/conn.go", element.path);
        Assert.assertEquals(148, element.line);
        Assert.assertFalse(element.createdBy);

        element = stack.elements.get(1);
        System.out.println(element.toString());
        Assert.assertEquals("github.com/openziti/edge/router/xgress_edge", element.pkg);
        Assert.assertEquals("xgress_edge", element.shortPkg);
        Assert.assertEquals("(*localMessageSink)", element.receiver);
        Assert.assertEquals("/home/plorenz/work/nf/edge/router/xgress_edge/fabric.go", element.path);
        Assert.assertEquals(97, element.line);
        Assert.assertFalse(element.createdBy);
    }
}
