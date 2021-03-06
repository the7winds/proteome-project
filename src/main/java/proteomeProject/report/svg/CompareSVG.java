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
 * Created by the7winds on 21.05.16.
 */
public class CompareSVG {

    private static final String WIDTH = "10000";
    private static final String HEIGHT = "350";

    public static String build(Annotation standard, Annotation variant) {
        String file = "compare" + Utils.getSvgName(Utils.id());

        Document document = SVGDOMImplementation.getDOMImplementation()
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        try {
            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width", WIDTH);
            SVG.setAttribute("height", HEIGHT);
            SVG.appendChild(getElement(document, standard, variant));

            SVGTranscoder transcoder = new SVGTranscoder();

            Writer writer = new FileWriter(ProjectPaths.getSvg().resolve(file).toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }

        return file;
    }

    private static Node getElement(Document document, Annotation standard, Annotation variant) {
        Element all = document.createElement("g");
        all.setAttribute("style", "font-family: arial;");

        Element stdInfo = InfoSVG.getElement(document, standard);
        stdInfo.setAttribute("transform", "translate(0, 10)");
        all.appendChild(stdInfo);

        Element varInfo = InfoSVG.getElement(document, variant);
        varInfo.setAttribute("transform", "translate(250, 10)");
        all.appendChild(varInfo);

        Element peptides = ModificationsSVG.getElement(document, variant);
        peptides.setAttribute("transform", "translate(0, 100)");
        all.appendChild(peptides);

        Element specs = getSpecs(document, standard, variant);
        specs.setAttribute("transform", String.format("translate(0, %d)", AminoSVG.height * 10));
        all.appendChild(specs);

        return all;
    }

    private static Element getSpecs(Document document, Annotation standard, Annotation variant) {
        SpectrumSVG.SpectrumSVGBuilder builder = new SpectrumSVG.SpectrumSVGBuilder(document);
        builder.addSpectrum(standard);
        builder.addSpectrum(variant);

        return builder.build();
    }
}
