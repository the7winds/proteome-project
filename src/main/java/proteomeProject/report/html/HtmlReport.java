package proteomeProject.report.html;

import j2html.tags.ContainerTag;
import org.apache.batik.transcoder.TranscoderException;
import proteomeProject.annotation.Annotation;
import proteomeProject.report.svg.AnnotationSVG;
import proteomeProject.utils.ProjectPaths;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static j2html.TagCreator.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * Created by the7winds on 17.04.16.
 */
public class HtmlReport {

    private static final String DIR = "report";
    private static final String SVG = "svg";

    public static void makeHtmlReport(String name, Collection<Annotation> annotations) throws IOException, TranscoderException {
        Path reportDir = ProjectPaths.Output
                .getOutput()
                .resolve(DIR);
        Path report = reportDir.resolve(name);
        Path svgDir = reportDir.resolve(SVG);

        svgDir.toFile().mkdirs();

        ContainerTag body = body();
        for (Annotation annotation : annotations) {
            String filename = annotation.toString() + ".svg";
            File svgFile = svgDir.resolve(filename).toFile();
            svgFile.createNewFile();
            new AnnotationSVG(svgFile, annotation).build();

            ContainerTag div = div();

            ContainerTag object = object();
            object.setAttribute("type", "image/svg+xml");
            object.setAttribute("data", svgDir.resolve(filename).toString());

            div.with(object);
            body.with(div);
        }

        try (FileWriter fileWriter = new FileWriter(report.toFile())) {
            fileWriter.write(html().with(body).render());
        }

        Files.copy(Paths.get("src/main/resources/report/amino.css"), svgDir.resolve("amino.css"), REPLACE_EXISTING);
        Files.copy(Paths.get("src/main/resources/report/report.css"), svgDir.resolve("report.css"), REPLACE_EXISTING);
    }
}
