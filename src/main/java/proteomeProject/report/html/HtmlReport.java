package proteomeProject.report.html;

import org.apache.batik.transcoder.TranscoderException;
import proteomeProject.annotation.Annotation;
import proteomeProject.report.svg.AnnotationSVG;
import proteomeProject.utils.ProjectPaths;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * Created by the7winds on 17.04.16.
 */
public class HtmlReport {

    private static final String DIR = "report";
    private static final String SVG = "svg";

    public static void makeHtmlReport(String name, Collection<Annotation> annotations) throws IOException, TranscoderException {
        Path reportDir = ProjectPaths.getOutput().resolve(DIR);
        Path svgDir = reportDir.resolve(SVG);

        svgDir.toFile().mkdirs();

        StringBuilder stringBuilder = new StringBuilder();
        for (Annotation annotation : annotations) {
            String filename = annotation.toString() + ".svg";
            File svgFile = svgDir.resolve(filename).toFile();
            svgFile.createNewFile();
            new AnnotationSVG(svgFile, annotation).build();
            String svgName = "\"" + "svg/" + filename + "\", ";
            stringBuilder.append(svgName);
        }
        String svgPaths = stringBuilder.toString();
        svgPaths = "var svgPaths = [" + (svgPaths.length() > 0
                ? svgPaths.substring(0, svgPaths.length() - 2)
                : "") + "];";

        try (PrintStream js = new PrintStream(reportDir.resolve(name + ".js").toFile())) {
            js.println(svgPaths);
            Files.lines(Paths.get("src/main/resources/report/template.js"))
                    .forEach(js::println);
        }

        try (PrintStream html = new PrintStream(reportDir.resolve(name + ".html").toFile())) {
            Files.lines(Paths.get("src/main/resources/report/template.html"))
                    .forEach(s -> {
                        html.println(s);
                        if (s.equals("<!--script-->")) {
                            html.printf("<script type=\"text/javascript\" src=\"%s\">" +
                                    "</script>", name + ".js");
                        }
                    });
        }

        Files.copy(Paths.get("src/main/resources/report/amino.css"), svgDir.resolve("amino.css"), REPLACE_EXISTING);
        Files.copy(Paths.get("src/main/resources/report/alignment.css"), svgDir.resolve("alignment.css"), REPLACE_EXISTING);
    }
}
