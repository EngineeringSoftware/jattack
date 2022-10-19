package sketchy.transformer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.Assert;
import org.junit.Test;
import sketchy.Constants;
import sketchy.ast.exp.BAriExp;
import sketchy.ast.exp.LogExp;
import sketchy.compiler.CompilationException;
import sketchy.compiler.InMemoryCompiler;
import sketchy.data.Data;
import sketchy.util.TypeUtil;

import java.lang.reflect.InvocationTargetException;

public class HoleExtractorTest {

    @Test
    public void test() {
        // Extraction looks right
        CompilationUnit origCu = StaticJavaParser.parse(
                "import sketchy.annotation.Entry;\n" +
                "import static sketchy.Sketchy.*;\n" +
                "public class Sk {\n" +
                "    static int s1;\n" +
                "    @Entry\n" +
                "    public static int m() {\n" +
                "        if (logic(relation(intId(), intId()), relation(intId(), intId())).eval(1)) {\n" +
                "            s1 = arithmetic(intId(), intId()).eval(2);\n" +
                "        }\n" +
                "        return s1;\n" +
                "    }\n" +
                "}");
        CompilationUnit expectedCu = StaticJavaParser.parse(String.format(
                "import sketchy.annotation.Entry;\n" +
                "import static sketchy.Sketchy.*;\n" +
                "public class %s {\n" +
                "    public static void %s() {\n" +
                "        %s.%s().put(1, logic(relation(intId(), intId()), relation(intId(), intId())));\n" +
                        "%s.%s().put(2, arithmetic(intId(), intId()));" +
                "    }\n" +
                "}",
                HoleExtractor.CLASS_NAME,
                HoleExtractor.METHOD_NAME,
                Constants.DATA_CLZ,
                Constants.GET_AST_CACHE_METHOD,
                Constants.DATA_CLZ,
                Constants.GET_AST_CACHE_METHOD));
        HoleExtractor he = new HoleExtractor(origCu);
        he.transform();
        CompilationUnit actualCu = he.getCu();
        Assert.assertEquals(expectedCu, actualCu);

        // Extraction executes right
        Data.clearASTCache();
        InMemoryCompiler compiler = new InMemoryCompiler();
        try {
            System.err.println(he.getSrcCode());
            ClassLoader cl = compiler.compileAndRedefine(
                    HoleExtractor.CLASS_NAME, he.getSrcCode());
            Class<?> clz = TypeUtil.loadClz(HoleExtractor.CLASS_NAME, true, cl);
            clz.getDeclaredMethod(HoleExtractor.METHOD_NAME).invoke(null);
        } catch (CompilationException e) {
            Assert.fail("Not compile!");
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(Data.getASTOfHole(1) instanceof LogExp);
        Assert.assertTrue(Data.getASTOfHole(2) instanceof BAriExp);
        Data.clearASTCache();
    }
}
