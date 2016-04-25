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

    public static void build(String file, Annotation annotation) {
        Document document = SVGDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        try {
            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width"
                    , String.valueOf(1.2 * Math.max(annotation.getSpectrum().getPrecursorMass()
                        , 0.5 * AminoSVG.width * annotation.getPeptide().getPeptide().length())));
            SVG.setAttribute("height", "400");
            SVG.appendChild(getElement(document, annotation));

            SVGTranscoder transcoder = new SVGTranscoder();
            transcoder.addTranscodingHint(SVGTranscoder.KEY_XML_DECLARATION,
                    "<?xml-stylesheet type=\"text/css\" href=\"annotation.css\" ?>\n" +
                            "<?xml-stylesheet type=\"text/css\" href=\"amino.css\" ?>");

            Writer writer = new FileWriter(ProjectPaths.getSvg().resolve(file).toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
    }

    static Element getElement(Document document, Annotation annotation) throws FileNotFoundException {
        Element annotationSVG = document.createElement("g");

        annotationSVG.setAttribute("class", "annotation");

        Element infoSVG = InfoSVG.getElement(document, annotation);
        Element alignmentSVG = AlignmentSVG.getElement(document, annotation);
        Element spectrumSVG = SpectrumSVG.getElement(document, annotation);

        annotationSVG.appendChild(infoSVG);
        annotationSVG.appendChild(alignmentSVG);
        annotationSVG.appendChild(spectrumSVG);

        return annotationSVG;
    }
}
