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
import static jattack.Boom.*;
import jattack.annotation.*;

public final class SuppressionsStringPrinterm106Template {

    private static final Pattern VALID_SUPPRESSION_LINE_COLUMN_NUMBER_REGEX = Pattern.compile("^([0-9]+):([0-9]+)$");

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private SuppressionsStringPrinterm106Template() {
    }

    @jattack.annotation.Entry()
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

    @Argument(1)
    public static File nonPrim1() {
        return null;
    }

    @Argument(2)
    public static String nonPrim2() {
        return null;
    }

    @Argument(3)
    public static int intArg3() {
        return intVal().eval();
    }
}
