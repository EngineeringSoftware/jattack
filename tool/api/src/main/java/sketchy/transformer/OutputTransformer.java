package sketchy.transformer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import sketchy.Config;
import sketchy.Constants;
import sketchy.data.Data;
import sketchy.transformer.visitor.HoleFiller;
import sketchy.transformer.visitor.RemoveVisitor;
import sketchy.transformer.visitor.RenameVisitor;
import sketchy.util.JPUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Transform sketches in output.
 */
public class OutputTransformer extends Transformer {

    /*
     * Constants, read/computed from constructors.
     */
    private final String inClzName;
    private final String entryMethodName;
    private final boolean entryMethodReturnsVoid;
    private final List<String> argMethodNames;
    private final List<String> argMethodRetTypes;

    /*
     * Unchanged after initialization.
     */
    private CompilationUnit initCu; // CU after initial transformation

    /*
     * Changed across each transform.
     */
    private ClassOrInterfaceDeclaration clz;
    private String outClzName;

    public OutputTransformer(
            CompilationUnit origCu,
            String inClzName,
            String entryMethodName,
            boolean entryMethodReturnsVoid,
            List<String> argMethodNames) {
        super(origCu);
        this.inClzName = inClzName;
        this.entryMethodName = entryMethodName;
        this.entryMethodReturnsVoid = entryMethodReturnsVoid;
        this.argMethodNames = argMethodNames;
        this.argMethodRetTypes = computeArgMethodRetTypes();
    }

    /**
     * Do the full transformation, including filling all the holes,
     * removing annotations, adding main(), etc.
     */
    @Override
    public void transform() {
        if (initCu == null) {
            initTransform();
        }
        // Update cu and clz
        cu = initCu.clone();
        clz = cu.getClassByName(inClzName).get();
        // Rename class
        renameClass();

        // Replace holes as api with concrete code
        transformApi();

        if (Config.trackHoles) {
            // A constant to store the number of holes filled
            clz.addFieldWithInitializer("int",
                    Constants.FILLED_HOLES,
                    new IntegerLiteralExpr(String.valueOf(Data.getNumFilledHoles())),
                    Modifier.Keyword.PRIVATE,
                    Modifier.Keyword.STATIC,
                    Modifier.Keyword.FINAL);
            tryCatchTrackMethods();
        }
    }

    /**
     * Surround every {@link Constants#TRACK_METHOD} method call with
     * a try-catch so we can know a hole is reached even when an
     * exception is thrown before getting inside the track method.
     */
    // TODO: I did not find a general solution; for now I
    //  hardcode for a DivideByZero exception in SkJDK8248552
    //  only.
    private void tryCatchTrackMethods() {
        if (!inClzName.equals("SkJDK8248552")) {
            return;
        }
        Statement stmt = clz.getMethodsByName(entryMethodName).get(0)
                .findFirst(ExpressionStmt.class, s -> {
                    if (!s.getExpression().isAssignExpr()) {
                        return false;
                    }
                    AssignExpr e = s.getExpression().asAssignExpr();
                    return e.getTarget().isNameExpr() && e.getTarget().asNameExpr().getNameAsString().equals("s1")
                            && e.getOperator() == AssignExpr.Operator.ASSIGN;
                }).get();
        MethodCallExpr trackMethodCall = stmt.asExpressionStmt().getExpression().asAssignExpr().getValue().asMethodCallExpr();
        if (!trackMethodCall.getNameAsString().equals(Constants.TRACK_METHOD)) {
            // If this hole is not filled.
            return;
        }
        int holeId = (int) trackMethodCall.getArguments().get(1).asIntegerLiteralExpr().asNumber();
        TryStmt tryStmt = new TryStmt();
        tryStmt.setTryBlock(new BlockStmt().addStatement(stmt.clone()));
        CatchClause cc = new CatchClause(
                new Parameter(StaticJavaParser.parseClassOrInterfaceType("ArithmeticException"), "e"),
                StaticJavaParser.parseBlock(String.format("{%s(null, %d); throw e;}", Constants.TRACK_METHOD, holeId)));
        tryStmt.setCatchClauses(new NodeList<>(cc));
        stmt.replace(tryStmt);
    }

    /**
     * Rename class.
     */
    private void renameClass() {
        clz.setName(outClzName);
        // find usage in methods and change every one
        clz.accept(new RenameVisitor(inClzName, outClzName), null);
    }

    /**
     * Transform Sketchy apis.
     */
    private void transformApi() {
        cu.accept(new RemoveVisitor(), null);
        cu.accept(new HoleFiller(Config.trackHoles), null);
    }

    /**
     * Set outClzName.
     */
    public OutputTransformer setOutClzName(String outClzName) {
        this.outClzName = outClzName;
        return this;
    }

    /**
     * The common part across different generated programs, which we
     * only need to transform once.
     */
    private void initTransform() {
        initCu = origCu.clone();
        // Remove package declaration
        if (!Config.keepPkg) {
            initCu.removePackageDeclaration();
        }
        // Import CSUtil;
        initCu.addImport("org.csutil.checksum.WrappedChecksum");
        // Transform or add main()
        transformOrAddMain();
        if (Config.trackHoles) {
            addPiecesForTrackingHoles();
        }
    }

    private void addPiecesForTrackingHoles() {
        ClassOrInterfaceDeclaration initClz = initCu.getClassByName(inClzName).get();
        // A boolean array to track if holes are covered
        initClz.addFieldWithInitializer("boolean[]",
                Constants.TOTAL_HOLES,
                StaticJavaParser.parseExpression(String.format("new boolean[%s]", Data.getTotalNumHoles())),
                Modifier.Keyword.PRIVATE,
                Modifier.Keyword.STATIC,
                Modifier.Keyword.FINAL);
        // A method to do the tracking
        MethodDeclaration trackMethod = StaticJavaParser.parseMethodDeclaration(
                String.format("private static <T> T %s (T val, int id) {" +
                                // holeId starts with 1
                                "%s[id - 1] = true;" +
                                "return val;" +
                                "}",
                        Constants.TRACK_METHOD,
                        Constants.TOTAL_HOLES));
        initClz.addMember(trackMethod);
        // A method to write tracking results to file
        MethodDeclaration writeTrackingResultsMethod = StaticJavaParser.parseMethodDeclaration(
                String.format("private static void %s(String[] args) {" +
                                // Takes the second argument as the directory to output
                                "String dir = args.length < 2 ? \".\" : args[1];" +
                                "int numReached = 0;" +
                                "for (boolean b : %s) {" +
                                "  numReached += b ? 1 : 0;" +
                                "}" +
                                "String contents = \"reachedHoles,filledHoles,totalHoles\\n\" + numReached + \",\" + %s + \",\" + %s.length + \"\\n\";" +
                                "%s.createDir(dir);" +
                                "%s.writeToFile(dir, \"%s\", contents);" +
                                "}",
                        Constants.WRITE_TRACKING_RESULTS_METHOD,
                        Constants.TOTAL_HOLES,
                        Constants.FILLED_HOLES, Constants.TOTAL_HOLES,
                        Constants.IOUTIL_CLZ,
                        Constants.IOUTIL_CLZ, Config.trackHolesFile)
        );
        initClz.addMember(writeTrackingResultsMethod);
        // Invoke method writeTrackResults in main
        MethodDeclaration main = initClz.getMethodsByName("main").get(0);
        BlockStmt block = main.getBody().get();
        block.addStatement(StaticJavaParser.parseStatement(Constants.WRITE_TRACKING_RESULTS_METHOD + "(args);"));
    }

    private void transformOrAddMain() {
        ClassOrInterfaceDeclaration initClz = initCu.getClassByName(inClzName).get();

        // Add main0
        MethodDeclaration main0Method = genMain0();
        initClz.addMember(main0Method);

        // Add main
        MethodDeclaration mainMethod = JPUtil.getMain(initClz).orElse(null);
        if (mainMethod != null) {
            // Replace main if there is one
            initClz.replace(mainMethod, genMain());
        } else {
            // Add one if there is no main
            initClz.addMember(genMain());
        }
    }

    private List<String> computeArgMethodRetTypes() {
        List<String> types = new ArrayList<>();
        ClassOrInterfaceDeclaration origClz = origCu.getClassByName(inClzName).get();
        for (String name : argMethodNames) {
            types.add(origClz.getMethodsByName(name).get(0).getTypeAsString());
        }
        return types;
    }

    /**
     * Generate a main method which invokes main0 and print the return
     * value of main0.
     */
    private MethodDeclaration genMain() {
        MethodDeclaration method = StaticJavaParser
                .parseMethodDeclaration("public static void main(String[] args){}");
        method.setBody(genMainBody());
        return method;
    }

    private static BlockStmt genMainBody() {
        return StaticJavaParser.parseBlock(
                "{" +
                        "System.out.println(main0(args));" +
                        "}");
    }

    /**
     * Generate main0 method that invokes entry methods many times and
     * return a checksum encoded from execution.
     */
    private MethodDeclaration genMain0() {
        MethodDeclaration method = StaticJavaParser
                .parseMethodDeclaration("public static long main0(String[] args) {}");
        method.setBody(genMain0Body());
        return method;
    }

    private BlockStmt genMain0Body() {
        StringBuilder sb = new StringBuilder();
        // Number of iterations
        sb.append("{")
                .append("int N = ").append(Config.nInvocations).append(";")
                .append("if (args.length > 0) {")
                .append("N = Math.min(Integer.parseInt(args[0]), N);")
                .append("}");
        // Initiate checksum instance
        sb.append("WrappedChecksum cs = new WrappedChecksum();");
        // Main loop
        sb.append("for (int i = 0; i < N; ++i) {")
                .append("try {");
        // Create arguments for entry methods.
        String argVar = "arg";
        for (int i = 0; i < argMethodNames.size(); i++) {
            sb.append(argMethodRetTypes.get(i))
                    .append(" ")
                    .append(argVar).append(i + 1)
                    .append(" = ")
                    .append(argMethodNames.get(i)).append("();");
        }
        // Invoke the entry method
        StringBuilder entryMethodCall = new StringBuilder();
        entryMethodCall.append(entryMethodName).append("(");
        for (int i = 0; i < argMethodNames.size(); i++) {
            if (i != 0) {
                entryMethodCall.append(", ");
            }
            entryMethodCall.append(argVar).append(i + 1);
        }
        entryMethodCall.append(")");
        if (entryMethodReturnsVoid) {
            sb.append(entryMethodCall).append(";");
        } else {
            sb.append("cs.update(").append(entryMethodCall).append(");");
        }
        sb.append("}")
                .append("catch (Throwable e) {");
        // Catch block
        sb.append(String.format("if (e instanceof %s) {throw e;}", Constants.INVOKED_FROM_NOT_DRIVER_EXCEPTION_CLZ));
        sb.append("cs.update(e.getClass().getName());");
        sb.append("}")
                .append("}")
                // Update checksum with static fields
                .append("cs.updateStaticFieldsOfClass(").append(inClzName).append(".class);")
                .append("return cs.getValue();")
                .append("}");
        return StaticJavaParser.parseBlock(sb.toString());
    }
}
