package proteomeProject.report.svg;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.annotation.Annotation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by the7winds on 16.04.16.
 */
public class AnnotationSVG {

    private static final String svgNs = SVGDOMImplementation.SVG_NAMESPACE_URI;

    private final Annotation annotation;
    private final File file;
    private final Document document;

    public AnnotationSVG(File file, Annotation annotation) {
        this.file = file;
        this.annotation = annotation;
        document = SVGDOMImplementation.getDOMImplementation()
                .createDocument(svgNs, "svg", null);
    }

    public void build() throws IOException, TranscoderException {
        Element svgRoot = document.getDocumentElement();

        svgRoot.setAttribute("width"
                , String.valueOf(0.6 * Math.max(annotation.getSpectrum().getPrecursorMass()
                , AminoSVG.width * annotation.getPeptide().getPeptide().length())));
        svgRoot.setAttribute("height", "400");

        Element info = InfoSVG.getElement(document, annotation);
        Element alignment = AlignmentSVG.getElement(document, annotation);
        Element spectrum = SpectrumSVG.getElement(document, annotation);

        svgRoot.appendChild(info);
        svgRoot.appendChild(alignment);
        svgRoot.appendChild(spectrum);

        SVGTranscoder transcoder = new SVGTranscoder();
        transcoder.addTranscodingHint(SVGTranscoder.KEY_XML_DECLARATION,
                "<?xml-stylesheet type=\"text/css\" href=\"report.css\" ?>\n" +
                "<?xml-stylesheet type=\"text/css\" href=\"amino.css\" ?>");
        Writer writer = new FileWriter(file);
        transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
    }

}
