package com.puppycrawl.tools.checkstyle;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.xpath.XpathQueryGenerator;
import static sketchy.Sketchy.*;
import sketchy.annotation.*;
import org.csutil.checksum.WrappedChecksum;

public final class SuppressionsStringPrinterm106TemplateGEN1 {

    private static final Pattern VALID_SUPPRESSION_LINE_COLUMN_NUMBER_REGEX = Pattern.compile("^([0-9]+):([0-9]+)$");

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private SuppressionsStringPrinterm106TemplateGEN1() {
    }

    public static String printSuppressions(File file, String suppressionLineColumnNumber, int tabWidth) throws IOException, CheckstyleException {
        final Matcher matcher = VALID_SUPPRESSION_LINE_COLUMN_NUMBER_REGEX.matcher(suppressionLineColumnNumber);
        if (!matcher.matches()) {
            final String exceptionMsg = String.format(Locale.ROOT, "%s does not match valid format 'line:column'.", suppressionLineColumnNumber);
            throw new IllegalStateException(exceptionMsg);
        }
        final FileText fileText = new FileText(file.getAbsoluteFile(), System.getProperty("file.encoding", StandardCharsets.UTF_8.name()));
        final DetailAST detailAST = JavaParser.parseFileText(fileText, JavaParser.Options.WITH_COMMENTS);
        final int lineNumber = Integer.parseInt(matcher.group(1));
        final int columnNumber = Integer.parseInt(matcher.group(2));
        return generate(fileText, detailAST, lineNumber, columnNumber, tabWidth);
    }

    private static String generate(FileText fileText, DetailAST detailAST, int lineNumber, int columnNumber, int tabWidth) {
        final XpathQueryGenerator queryGenerator = new XpathQueryGenerator(detailAST, lineNumber, columnNumber, fileText, tabWidth);
        final List<String> suppressions = queryGenerator.generate();
        return suppressions.stream().collect(Collectors.joining(LINE_SEPARATOR, "", LINE_SEPARATOR));
    }

    public static File nonPrim1() {
        return null;
    }

    public static String nonPrim2() {
        return null;
    }

    public static int intArg3() {
        return -922089966;
    }

    public static long main0(String[] args) {
        int N = 100000;
        if (args.length > 0) {
            N = Math.min(Integer.parseInt(args[0]), N);
        }
        WrappedChecksum cs = new WrappedChecksum();
        for (int i = 0; i < N; ++i) {
            try {
                File arg1 = nonPrim1();
                String arg2 = nonPrim2();
                int arg3 = intArg3();
                cs.update(printSuppressions(arg1, arg2, arg3));
            } catch (Throwable e) {
                cs.update(e.getClass().getName());
            }
        }
        cs.updateStaticFieldsOfClass(SuppressionsStringPrinterm106TemplateGEN1.class);
        return cs.getValue();
    }

    public static void main(String[] args) {
        System.out.println(main0(args));
    }
}
