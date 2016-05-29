package proteomeProject.report.svg;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.IonType;
import proteomeProject.utils.ProjectPaths;
import proteomeProject.utils.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Comparator;

import static proteomeProject.dataEntities.IonType.Type.B;

/**
 * Created by the7winds on 24.04.16.
 */
public final class BoundsAlignedSVG {

    private static final String HEIGHT = "450";
    private static final String WIDTH = "10000";

    private BoundsAlignedSVG() {
    }

    public static String buildBoundsAligned(Annotation annotation) {
        String file = "boundsAligned" + Utils.getSvgName(Utils.id());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width", WIDTH);
            SVG.setAttribute("height", HEIGHT);
            SVG.appendChild(getBoundsAligned(document, annotation));

            SVGTranscoder transcoder = new SVGTranscoder();

            Path path = ProjectPaths.getSvg().resolve(file);
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getBoundsAligned(Document document, Annotation annotation) {
        Element all = document.createElement("g");
        all.appendChild(AnnotationSVG.getElement(document, annotation));

        int begin = annotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingDouble(IonType::getNum))
                .get()
                .getNum();
        int end = annotation.getAnnotations().get(annotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingDouble(IonType::getNum))
                .get()
                .getNum();
        String p = annotation.getPeptide().getPeptide();
        p = annotation.getType() == B
                ? p.substring(begin, end)
                : StringUtils.reverse(p).substring(begin, end);

        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        appendix.setAttribute("transform", "translate(10, 320)");
        appendix.setAttribute("style", "font-family: arial");

        InfoSVG.InfoSVGBuilder infoSVGBuilder = new InfoSVG.InfoSVGBuilder(document);
        infoSVGBuilder.addLine("BOUNDS ALIGNED=BOTH");
        appendix.appendChild(infoSVGBuilder.build());

        Element cutSVG = AminoStringSVG.getElement(document, p);
        cutSVG.setAttribute("transform", "translate(10, 30)");
        appendix.appendChild(cutSVG);

        all.appendChild(appendix);

        return all;
    }

    public static String buildPrecursorAligned(Annotation stdAnnotation, double zeroDiff) {
        String file = "precursorAligned" + Utils.getSvgName(Utils.id());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width", WIDTH);
            SVG.setAttribute("height", HEIGHT);
            SVG.appendChild(getPrecursorAligned(document, stdAnnotation, zeroDiff));

            SVGTranscoder transcoder = new SVGTranscoder();

            Path path = ProjectPaths.getSvg().resolve(file);
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getPrecursorAligned(Document document, Annotation stdAnnotation, double zeroDiff) {
        Element all = document.createElement("g");
        all.appendChild(AnnotationSVG.getElement(document, stdAnnotation));

        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        appendix.setAttribute("transform", "translate(10, 320)");
        appendix.setAttribute("style", "font-family: arial");

        InfoSVG.InfoSVGBuilder infoSVGBuilder = new InfoSVG.InfoSVGBuilder(document);;
        infoSVGBuilder.addLine("BOUNDS ALIGNED=PRECURSOR");
        infoSVGBuilder.addLine(String.format("ZERO DIFF=%f", zeroDiff));
        appendix.appendChild(infoSVGBuilder.build());

        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide().getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide().getPeptide());
        cut = cut.substring(0, stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        Element cutSVG = AminoStringSVG.getElement(document, cut);
        cutSVG.setAttribute("transform", "translate(0, 30)");
        appendix.appendChild(cutSVG);

        all.appendChild(appendix);

        return all;
    }

    public static String buildPrecursorAligned(Annotation stdAnnotation, double zeroDiff, int idx, double l, double r) {
        String file = "precursorAligned" + Utils.getSvgName(Utils.id());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width", WIDTH);
            SVG.setAttribute("height", HEIGHT);
            SVG.appendChild(getPrecursorAligned(document, stdAnnotation, zeroDiff, idx, l, r));

            SVGTranscoder transcoder = new SVGTranscoder();

            Path path = ProjectPaths.getSvg().resolve(file);
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getPrecursorAligned(Document document, Annotation stdAnnotation, double zeroDiff, int idx, double l, double r) {
        Element all = document.createElement("g");
        all.appendChild(AnnotationSVG.getElement(document, stdAnnotation));

        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        appendix.setAttribute("transform", "translate(10, 320)");
        appendix.setAttribute("style", "font-family: arial");

        InfoSVG.InfoSVGBuilder infoSVGBuilder = new InfoSVG.InfoSVGBuilder(document);
        infoSVGBuilder.addLine("BOUNDS ALIGNED=PRECURSOR");
        infoSVGBuilder.addLine(String.format("ZERO DIFF=%f", zeroDiff));
        appendix.appendChild(infoSVGBuilder.build());

        Element splitted = getSplitted(document, stdAnnotation.getType(), idx, 0, l, r);
        splitted.setAttribute("transform", "translate(0, 60)");
        appendix.appendChild(splitted);

        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide().getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide().getPeptide());
        cut = cut.substring(idx, stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        Element cutSVG = AminoStringSVG.getElement(document, cut);
        cutSVG.setAttribute("transform", "translate(0, 30)");
        appendix.appendChild(cutSVG);

        all.appendChild(appendix);

        return all;
    }

    private static Element getSplitted(Document document, IonType.Type type, int idx, double s, double l, double r) {
        Element splitted = document.createElement("g");

        Element line = document.createElement("polyline");
        line.setAttribute("points", "0,0 0,1 50,1 50,0 50,1 100,1 100,0");
        line.setAttribute("style", "fill: none; stroke: black; stroke-width: 0.5;");

        Element left = document.createElement("text");
        left.appendChild(document.createTextNode(Double.toString(l)));
        left.setAttribute("transform", "translate(25 5) rotate(90)");
        left.setAttribute("style", "font-size: 4px;");

        Element right = document.createElement("text");
        right.appendChild(document.createTextNode(Double.toString(r)));
        right.setAttribute("transform", "translate(75 5) rotate(90)");
        right.setAttribute("style", "font-size: 4px;");

        Element splitter = document.createElement("text");
        splitter.appendChild(document.createTextNode(Double.toString(s)));
        splitter.setAttribute("transform", "translate(50 5) rotate(90)");
        splitter.setAttribute("style", "font-size: 4px;");

        Element axis = document.createElement("g");
        axis.appendChild(line);
        axis.appendChild(left);
        axis.appendChild(splitter);
        axis.appendChild(right);

        axis.setAttribute("transform", "translate(10 10)");

        Element leftIon = document.createElement("text");
        leftIon.appendChild(document.createTextNode(String.format("%s%d", type.name(), idx - 1)));
        leftIon.setAttribute("transform", "translate(0 10)");
        leftIon.setAttribute("style", "font-size: 4px;");

        Element rightIon = document.createElement("text");
        rightIon.appendChild(document.createTextNode(String.format("%s%d", type.name(), idx)));
        rightIon.setAttribute("transform", "translate(115 10)");
        rightIon.setAttribute("style", "font-size: 4px;");

        splitted.appendChild(leftIon);
        splitted.appendChild(axis);
        splitted.appendChild(rightIon);

        return splitted;
    }

    public static String buildZeroAligned(Annotation stdAnnotation, double precursorDiff) {
        String file = "zeroAligned" + Utils.getSvgName(Utils.id());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width", WIDTH);
            SVG.setAttribute("height", HEIGHT);
            SVG.appendChild(getZeroAligned(document, stdAnnotation, precursorDiff));

            SVGTranscoder transcoder = new SVGTranscoder();

            Path path = ProjectPaths.getSvg().resolve(file);
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getZeroAligned(Document document, Annotation stdAnnotation, double precursorDiff) {
        Element all = document.createElement("g");
        all.appendChild(AnnotationSVG.getElement(document, stdAnnotation));

        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        appendix.setAttribute("transform", "translate(10, 320)");
        appendix.setAttribute("style", "font-family: arial");

        InfoSVG.InfoSVGBuilder infoSVGBuilder = new InfoSVG.InfoSVGBuilder(document);
        infoSVGBuilder.addLine("BOUNDS ALIGNED=ZERO");
        infoSVGBuilder.addLine(String.format("PRECURSOR DIFF=%f\n", precursorDiff));
        appendix.appendChild(infoSVGBuilder.build());

        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide().getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide().getPeptide());
        cut = cut.substring(stdAnnotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        Element cutSVG = AminoStringSVG.getElement(document, cut);
        cutSVG.setAttribute("transform", "translate(0, 30)");
        appendix.appendChild(cutSVG);

        all.appendChild(appendix);

        return all;
    }

    public static String buildZeroAligned(Annotation stdAnnotation, double precursorDiff, int splittedIdx, double l, double r) {
        String file = "zeroAligned" + Utils.getSvgName(Utils.id());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width", WIDTH);
            SVG.setAttribute("height", HEIGHT);
            SVG.appendChild(getZeroAligned(document, stdAnnotation, precursorDiff, splittedIdx, l, r));

            SVGTranscoder transcoder = new SVGTranscoder();

            Path path = ProjectPaths.getSvg().resolve(file);
            path.toFile().createNewFile();
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getZeroAligned(Document document, Annotation stdAnnotation, double precursorDiff, int splittedIdx, double l, double r) {
        Element all = document.createElement("g");
        all.appendChild(AnnotationSVG.getElement(document, stdAnnotation));

        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        appendix.setAttribute("transform", "translate(10, 320)");
        appendix.setAttribute("style", "font-family: arial");

        InfoSVG.InfoSVGBuilder infoSVGBuilder = new InfoSVG.InfoSVGBuilder(document);
        infoSVGBuilder.addLine("BOUNDS ALIGNED=ZERO");
        infoSVGBuilder.addLine(String.format("PRECURSOR DIFF=%f", precursorDiff));
        appendix.appendChild(infoSVGBuilder.build());

        Element splitted = getSplitted(document, stdAnnotation.getType(), splittedIdx, stdAnnotation.getSpectrum().getPrecursorMass(), l, r);
        splitted.setAttribute("transform", "translate(0, 60)");
        appendix.appendChild(splitted);

        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide().getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide().getPeptide());
        cut = cut.substring(stdAnnotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingInt(IonType::getNum)).get().getNum(), splittedIdx - 1); // idx -1 because string is indexed from zerp
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        Element cutSVG = AminoStringSVG.getElement(document, cut);
        cutSVG.setAttribute("transform", "translate(0, 30)");
        appendix.appendChild(cutSVG);

        all.appendChild(appendix);

        return all;
    }
}
