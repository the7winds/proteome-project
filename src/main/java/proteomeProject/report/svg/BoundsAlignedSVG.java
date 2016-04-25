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

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

import static proteomeProject.dataEntities.IonType.Type.B;

/**
 * Created by the7winds on 24.04.16.
 */
public final class BoundsAlignedSVG {

    private final Collection<String> elements = new LinkedList<>();

    private BoundsAlignedSVG() {
    }

    private static final BoundsAlignedSVG INSTANCE = new BoundsAlignedSVG();

    public static BoundsAlignedSVG getInstance() {
        return INSTANCE;
    }

    public String build(Annotation annotation) {
        String file = Utils.getSvgName(Utils.newName());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width"
                    , String.valueOf(1.2 * Math.max(annotation.getSpectrum().getPrecursorMass()
                            , 0.5 * AminoSVG.width * annotation.getPeptide().getPeptide().length())));
            SVG.setAttribute("height", "500");
            SVG.appendChild(getBoundsAligned(document, annotation));

            SVGTranscoder transcoder = new SVGTranscoder();
            transcoder.addTranscodingHint(SVGTranscoder.KEY_XML_DECLARATION,
                    "<?xml-stylesheet type=\"text/css\" href=\"annotation.css\" ?>\n" +
                            "<?xml-stylesheet type=\"text/css\" href=\"amino.css\" ?>");
            Path path = ProjectPaths.getSvg().resolve(file);
            elements.add(file);
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getBoundsAligned(Document document, Annotation annotation) throws FileNotFoundException {
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
                : p.substring(end, begin);


        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        appendix.setAttribute("transform", "translate(0, 420)");

        Element status = document.createElement("text");
        status.setAttribute("class", "info");
        status.appendChild(document.createTextNode("ALIGNED BOUNDS=BOTH"));
        Element cutSVG = AminoStringSVG.getElement(document, p);
        cutSVG.setAttribute("class", "cut");

        appendix.appendChild(status);
        appendix.appendChild(cutSVG);

        all.appendChild(appendix);

        return all;
    }

    public String buildPrecursorAligned(Annotation stdAnnotation, double zeroDiff) {
        String file = Utils.getSvgName(Utils.newName());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width"
                    , String.valueOf(1.2 * Math.max(stdAnnotation.getSpectrum().getPrecursorMass()
                            , 0.5 * AminoSVG.width * stdAnnotation.getPeptide().getPeptide().length())));
            SVG.setAttribute("height", "500");
            SVG.appendChild(getPrecursorAligned(document, stdAnnotation, zeroDiff));

            SVGTranscoder transcoder = new SVGTranscoder();
            transcoder.addTranscodingHint(SVGTranscoder.KEY_XML_DECLARATION,
                    "<?xml-stylesheet type=\"text/css\" href=\"annotation.css\" ?>\n" +
                            "<?xml-stylesheet type=\"text/css\" href=\"amino.css\" ?>");
            Path path = ProjectPaths.getSvg().resolve(file);
            elements.add(file);
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getPrecursorAligned(Document document, Annotation stdAnnotation, double zeroDiff) throws FileNotFoundException {
        Element all = document.createElement("g");
        all.appendChild(AnnotationSVG.getElement(document, stdAnnotation));

        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        appendix.setAttribute("transform", "translate(0, 420)");

        Element status = document.createElement("text");
        status.setAttribute("class", "info");
        status.appendChild(document.createTextNode(String.format("ALIGNED BOUNDS=PRECURSOR\nZERO DIFF=%f\n", zeroDiff)));

        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide()
                .getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide()
                .getPeptide());
        cut = cut.substring(0, stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        Element cutSVG = AminoStringSVG.getElement(document, cut);
        cutSVG.setAttribute("class", "cut");

        appendix.appendChild(status);
        appendix.appendChild(cutSVG);

        all.appendChild(appendix);

        return all;
    }

    public String buildPrecursorAligned(Annotation stdAnnotation, double zeroDiff, int idx, double l, double r) {
        String file = Utils.getSvgName(Utils.newName());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width"
                    , String.valueOf(1.2 * Math.max(stdAnnotation.getSpectrum().getPrecursorMass()
                            , 0.5 * AminoSVG.width * stdAnnotation.getPeptide().getPeptide().length())));
            SVG.setAttribute("height", "500");
            SVG.appendChild(getPrecursorAligned(document, stdAnnotation, zeroDiff, idx, l, r));

            SVGTranscoder transcoder = new SVGTranscoder();
            transcoder.addTranscodingHint(SVGTranscoder.KEY_XML_DECLARATION,
                    "<?xml-stylesheet type=\"text/css\" href=\"annotation.css\" ?>\n" +
                            "<?xml-stylesheet type=\"text/css\" href=\"amino.css\" ?>");
            Path path = ProjectPaths.getSvg().resolve(file);
            elements.add(file);
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (TranscoderException | IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getPrecursorAligned(Document document, Annotation stdAnnotation, double zeroDiff, int idx, double l, double r) throws FileNotFoundException {
        Element all = document.createElement("g");
        all.appendChild(AnnotationSVG.getElement(document, stdAnnotation));

        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        appendix.setAttribute("transform", "translate(0, 420)");
        
        Element status = document.createElement("text");
        status.setAttribute("class", "info");
        status.appendChild(
                document.createTextNode(
                        String.format("BOUNDS ALIGNED=PRECURSOR\nZERO DIFF=%f\n", zeroDiff)));
        Element splitted = getSplitted(document, stdAnnotation.getType(), idx, 0, l, r);

        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide()
                .getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide()
                .getPeptide());
        cut = cut.substring(idx, stdAnnotation.getAnnotations().get(stdAnnotation.getSpectrum().getPrecursorMass()).stream()
                .max(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        Element cutSVG = AminoStringSVG.getElement(document, cut);
        cutSVG.setAttribute("class", "cut");

        appendix.appendChild(status);
        appendix.appendChild(splitted);
        appendix.appendChild(cutSVG);

        all.appendChild(appendix);

        return all;
    }

    private static Element getSplitted(Document document, IonType.Type type, int idx, double s, double l, double r) {
        Element splitted = document.createElement("g");
        splitted.setAttribute("class", "splitted");

        Element line = document.createElement("polyline");
        line.setAttribute("points", "0,0 0,1 50,1 50,0 50,1 100,1 100,0");

        Element left = document.createElement("text");
        left.appendChild(document.createTextNode(Double.toString(l)));
        left.setAttribute("transform", "translate(25 5) rotate(90)");
        left.setAttribute("class", "label");

        Element right = document.createElement("text");
        right.appendChild(document.createTextNode(Double.toString(r)));
        right.setAttribute("transform", "translate(75 5) rotate(90)");
        right.setAttribute("class", "label");

        Element splitter = document.createElement("text");
        splitter.setAttribute("class", "label");
        splitter.appendChild(document.createTextNode(Double.toString(s)));
        splitter.setAttribute("transform", "translate(50 5) rotate(90)");

        Element axis = document.createElement("g");
        axis.setAttribute("class", "axis");
        axis.appendChild(line);
        axis.appendChild(left);
        axis.appendChild(splitter);
        axis.appendChild(right);

        axis.setAttribute("transform", "translate(20 10)");

        Element leftIon = document.createElement("text");
        leftIon.appendChild(document.createTextNode(String.format("%s%d", type.name(), idx - 1)));
        leftIon.setAttribute("transform", "translate(10 10)");
        leftIon.setAttribute("class", "label");

        Element rightIon = document.createElement("text");
        rightIon.appendChild(document.createTextNode(String.format("%s%d", type.name(), idx)));
        rightIon.setAttribute("transform", "translate(125 10)");
        rightIon.setAttribute("class", "label");

        splitted.appendChild(leftIon);
        splitted.appendChild(axis);
        splitted.appendChild(rightIon);

        return splitted;
    }

    public String buildZeroAligned(Annotation stdAnnotation, double precursorDiff) {
        String file = Utils.getSvgName(Utils.newName());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width"
                    , String.valueOf(1.2 * Math.max(stdAnnotation.getSpectrum().getPrecursorMass()
                            , 0.5 * AminoSVG.width * stdAnnotation.getPeptide().getPeptide().length())));
            SVG.setAttribute("height", "500");
            SVG.appendChild(getZeroAligned(document, stdAnnotation, precursorDiff));

            SVGTranscoder transcoder = new SVGTranscoder();
            transcoder.addTranscodingHint(SVGTranscoder.KEY_XML_DECLARATION,
                    "<?xml-stylesheet type=\"text/css\" href=\"annotation.css\" ?>\n" +
                            "<?xml-stylesheet type=\"text/css\" href=\"amino.css\" ?>");
            Path path = ProjectPaths.getSvg().resolve(file);
            elements.add(file);
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getZeroAligned(Document document, Annotation stdAnnotation, double precursorDiff) throws FileNotFoundException {
        Element all = document.createElement("g");
        all.appendChild(AnnotationSVG.getElement(document, stdAnnotation));

        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        appendix.setAttribute("transform", "translate(0, 420)");
        
        Element status = document.createElement("text");
        status.setAttribute("class", "info");
        status.appendChild(document.createTextNode(String.format("ALIGNED BOUNDS=ZERO\nPRECURSOR DIFF=%f\n", precursorDiff)));

        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide()
                .getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide()
                .getPeptide());
        cut = cut.substring(stdAnnotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingInt(IonType::getNum)).get().getNum());
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        Element cutSVG = AminoStringSVG.getElement(document, cut);
        cutSVG.setAttribute("class", "cut");

        appendix.appendChild(status);
        appendix.appendChild(cutSVG);

        all.appendChild(appendix);

        return all;
    }

    public String buildZeroAligned(Annotation stdAnnotation, double precursorDiff, int idx, double l, double r) {
        String file = Utils.getSvgName(Utils.newName());
        try {
            Document document = SVGDOMImplementation.getDOMImplementation()
                    .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);

            Element SVG = document.getDocumentElement();
            SVG.setAttribute("width"
                    , String.valueOf(1.2 * Math.max(stdAnnotation.getSpectrum().getPrecursorMass()
                        , 0.5 * AminoSVG.width * stdAnnotation.getPeptide().getPeptide().length())));
            SVG.setAttribute("height", "500");
            SVG.appendChild(getZeroAligned(document, stdAnnotation, precursorDiff, idx, l, r));

            SVGTranscoder transcoder = new SVGTranscoder();
            transcoder.addTranscodingHint(SVGTranscoder.KEY_XML_DECLARATION,
                    "<?xml-stylesheet type=\"text/css\" href=\"annotation.css\" ?>\n" +
                            "<?xml-stylesheet type=\"text/css\" href=\"amino.css\" ?>");
            Path path = ProjectPaths.getSvg().resolve(file);
            elements.add(file);
            path.toFile().createNewFile();
            Writer writer = new FileWriter(path.toFile());
            transcoder.transcode(new TranscoderInput(document), new TranscoderOutput(writer));
        } catch (IOException | TranscoderException e) {
            e.printStackTrace();
        }
        return file;
    }

    private static Element getZeroAligned(Document document, Annotation stdAnnotation, double precursorDiff, int idx, double l, double r) throws FileNotFoundException {
        Element all = document.createElement("g");
        all.appendChild(AnnotationSVG.getElement(document, stdAnnotation));
        
        Element appendix = document.createElement("g");
        appendix.setAttribute("class", "appendix");
        
        Element status = document.createElement("text");
        status.setAttribute("class", "info");
        status.appendChild(
                document.createTextNode(
                        String.format("BOUNDS ALIGNED=PRECURSOR\nZERO DIFF=%f\n", precursorDiff)));

        Element splitted = getSplitted(document, stdAnnotation.getType(), idx, 0, l, r);

        String cut = stdAnnotation.getType() == B
                ? stdAnnotation.getPeptide()
                .getPeptide()
                : StringUtils.reverse(stdAnnotation.getPeptide()
                .getPeptide());
        cut = cut.substring(stdAnnotation.getAnnotations().get(0d).stream()
                .min(Comparator.comparingInt(IonType::getNum)).get().getNum(), idx);
        cut = stdAnnotation.getType() == B
                ? cut
                : StringUtils.reverse(cut);
        Element cutSVG = AminoStringSVG.getElement(document, cut);
        cutSVG.setAttribute("class", "cut");

        appendix.appendChild(status);
        appendix.appendChild(splitted);
        appendix.appendChild(cutSVG);
        all.appendChild(appendix);

        return all;
    }

    public Collection<String> getElements() {
        return elements;
    }
}
