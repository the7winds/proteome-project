package proteomeProject.report.html;

import proteomeProject.utils.ProjectPaths;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Created by the7winds on 17.04.16.
 */
public class HtmlAlignmentReport {

    public static void makeHtmlReport(String name, Collection<String> svgPaths) throws IOException {
        java.lang.String svgPathsString = svgPaths.stream()
                .map(s -> "\"" + "svg/" + s + "\"")
                .collect(Collectors.joining(","));
        svgPathsString = java.lang.String.format("var svgPaths = [ %s ];", svgPathsString);

        try (PrintStream js = new PrintStream(ProjectPaths.getReport().resolve(name + ".js").toFile())) {
            js.println(svgPathsString);
            Files.lines(Paths.get("src/main/resources/report/template.js"))
                    .forEach(js::println);
        }

        try (PrintStream html = new PrintStream(ProjectPaths.getReport().resolve(name + ".html").toFile())) {
            Files.lines(Paths.get("src/main/resources/report/template.html"))
                    .forEach(s -> {
                        html.println(s);
                        if (s.equals("<!--script-->")) {
                            html.printf("<script type=\"text/javascript\" src=\"%s\">" +
                                    "</script>", name + ".js");
                        }
                    });
        }
    }
}
