package proteomeProject.report.svg;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import proteomeProject.annotation.Annotation;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by the7winds on 22.05.16.
 */
public class ReverseSVG {

    private static final String WIDTH = "10000";
    private static final String HEIGHT = "350";

    public static String build(Annotation annotation, Annotation reverse) {
        String file = "reverse" + Utils.getSvgName(Utils.id());

        Document document = SVGDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        try {
            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width", WIDTH);
            SVG.setAttribute("height", HEIGHT);
            SVG.appendChild(getElement(document, annotation, reverse));

            SVGTranscoder transcoder = new SVGTranscoder();

            Writer writer = new FileWriter(ProjectPaths.getSvg().resolve(file).toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }

        return file;
    }

    private static Node getElement(Document document, Annotation annotation, Annotation reversed) {
        Element all = document.createElement("g");
        all.setAttribute("style", "font-family: arial;");

        Element stdInfo = InfoSVG.getElement(document, annotation);
        stdInfo.setAttribute("transform", "translate(0, 0)");
        all.appendChild(stdInfo);

        Element aminoString = AminoStringSVG.getElement(document, annotation.getPeptide().getPeptide());
        aminoString.setAttribute("transform", "translate(0, 100)");
        all.appendChild(aminoString);

        /*
        Element peptides = ModificationsSVG.getElement(document, standard, variant);
        peptides.setAttribute("transform", "translate(0, 100)");
        all.appendChild(peptides);
        */

        Element specs = getSpecs(document, annotation, reversed);
        specs.setAttribute("transform", String.format("translate(0, %d)", 150 + 2 * AminoSVG.height));
        all.appendChild(specs);

        return all;
    }

    private static Element getSpecs(Document document, Annotation annotation, Annotation reverse) {
        SpectrumSVG.SpectrumSVGBuilder builder = new SpectrumSVG.SpectrumSVGBuilder(document);
        builder.addSpectrum(annotation, "original");
        builder.addSpectrum(reverse, "reverse");

        return builder.build();
    }
}
