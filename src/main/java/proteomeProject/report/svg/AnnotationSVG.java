package proteomeProject.report.svg;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.annotation.Annotation;
import proteomeProject.utils.ProjectPaths;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by the7winds on 16.04.16.
 */
public final class AnnotationSVG {

    private static final String WIDTH = "10000";
    private static final String HEIGHT = "350";

    public static void build(String file, Annotation annotation) {
        Document document = SVGDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        try {
            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width", WIDTH);
            SVG.setAttribute("height", HEIGHT);
            SVG.appendChild(getElement(document, annotation));

            SVGTranscoder transcoder = new SVGTranscoder();

            Writer writer = new FileWriter(ProjectPaths.getSvg().resolve(file).toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
    }

    static Element getElement(Document document, Annotation annotation) throws FileNotFoundException {
        Element annotationSVG = document.createElement("g");

        annotationSVG.setAttribute("class", "annotation");
        annotationSVG.setAttribute("style", "font-family: arial");

        Element infoSVG = InfoSVG.getElement(document, annotation);
        infoSVG.setAttribute("transform", "translate(10, 10)");

        Element alignmentSVG = AlignmentSVG.getElement(document, annotation);
        alignmentSVG.setAttribute("transform", "translate(10, 100)");

        Element spectrumSVG = SpectrumSVG.getElement(document, annotation);
        spectrumSVG.setAttribute("transform", "translate(20, 200)");

        annotationSVG.appendChild(infoSVG);
        annotationSVG.appendChild(alignmentSVG);
        annotationSVG.appendChild(spectrumSVG);

        return annotationSVG;
    }
}
