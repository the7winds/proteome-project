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
import proteomeProject.dataEntities.VariantsStandards;
import proteomeProject.utils.Chemicals;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

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

        Element peptides = getPeptides(document, standard, variant);
        peptides.setAttribute("transform", "translate(0, 100)");
        all.appendChild(peptides);

        Element specs = getSpecs(document, standard, variant);
        specs.setAttribute("transform", String.format("translate(0, %d)", AminoSVG.height * 9));
        all.appendChild(specs);

        return all;
    }

    private static Element getSpecs(Document document, Annotation standard, Annotation variant) {
        Element group = document.createElement("g");
        group.setAttribute("style", "font-size: 6px");

        Element stdLabel = document.createElement("text");
        stdLabel.appendChild(document.createTextNode(standard.getPeptide().getName()));
        stdLabel.setAttribute("transform", "translate(0, 25)");

        Element varLabel = document.createElement("text");
        varLabel.appendChild(document.createTextNode(variant.getPeptide().getName()));
        varLabel.setAttribute("transform", "translate(0, 125)");

        Element stdSpec = SpectrumSVG.getElement(document, standard);
        stdSpec.setAttribute("transform", "translate(50, 25)");

        Element varSpec = SpectrumSVG.getElement(document, variant);
        varSpec.setAttribute("transform", "translate(50, 125)");

        group.appendChild(stdLabel);
        group.appendChild(varLabel);
        group.appendChild(stdSpec);
        group.appendChild(varSpec);

        return group;
    }

    private static Element getPeptides(Document document, Annotation standard, Annotation variant) {
        Element group = document.createElement("g");
        group.setAttribute("style", "font-size: 6px");

        Element stdLabel = document.createElement("text");
        stdLabel.appendChild(document.createTextNode(standard.getPeptide().getName()));
        stdLabel.setAttribute("transform", String.format("translate(0, %d)", AminoSVG.height / 2));

        Element varLabel = document.createElement("text");
        varLabel.appendChild(document.createTextNode(variant.getPeptide().getName()));
        varLabel.setAttribute("transform", String.format("translate(0, %d)", 7 * AminoSVG.height / 4));

        Element stdPeptide = AminoStringSVG.getElement(document, standard.getPeptide().getPeptide());
        stdPeptide.setAttribute("transform", String.format("translate(%d, 0)", AminoSVG.width * 4));

        Element varPeptide = getVarAminoString(document, variant);
        varPeptide.setAttribute("transform", String.format("translate(%d, %d)", AminoSVG.width * 4, 5 * AminoSVG.height / 4));

        group.appendChild(stdLabel);
        group.appendChild(stdPeptide);
        group.appendChild(varLabel);
        group.appendChild(varPeptide);

        return group;
    }

    private static Element getVarAminoString(Document document, Annotation variant) {
        Element varAminoString = document.createElement("g");

        Map<Integer, VariantsStandards.MapAmino> modifications = VariantsStandards.getInstance().getModifications(variant.getPeptide());

        for (int i = 0; i < variant.getPeptide().getPeptide().length(); ++i) {
            Element aminoAcid = AminoSVG.getElement(document, Chemicals.AminoAcid.valueOf(variant.getPeptide().getPeptide().charAt(i)));
            aminoAcid.setAttribute("transform", String.format("translate(%d, 0)", modifications.get(i).getIdx() * AminoSVG.width));
            varAminoString.appendChild(aminoAcid);
        }

        return varAminoString;
    }
}
