package proteomeProject.report.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.IonType;

import java.util.List;
import java.util.Map;

import static proteomeProject.dataEntities.IonType.Type.B;

/**
 * Created by the7winds on 16.04.16.
 */
public class SpectrumSVG {

    private static final double SCALE = 0.5;

    public static Element getElement(Document document, Annotation annotation) {
        Element group = document.createElement("g");

        Element axis = getAxis(document, annotation);

        group.appendChild(axis);

        return group;
    }

    private static Element getAxis(Document document, Annotation annotation) {
        Element group = document.createElement("g");

        Element line = document.createElement("polyline");

        line.setAttribute("points", String.format("0,2 %f,2", scale(annotation.getSpectrum().getPrecursorMass())));
        line.setAttribute("style", "stroke: black;" +
                "stroke-width: 0.5");

        group.appendChild(line);

        for (Map.Entry<Double, List<IonType>> entry: annotation.getAnnotations().entrySet()) {
            Element peak = getPeak(document, entry.getKey());
            group.appendChild(peak);

            if (entry.getValue().size() > 0) {
                Element label = getLabel(document, annotation, entry.getKey(), entry.getValue());
                group.appendChild(label);
            }
        }

        return group;
    }

    private static Element getLabel(Document document, Annotation annotation, double pos, List<IonType> types) {
        double x = scale(pos);

        Element label = document.createElement("g");
        label.setAttribute("class", "label");
        label.setAttribute("transform", String.format("translate(%f 3)", x));
        label.setAttribute("style", "fill: black;" +
                "stroke: none;" +
                "font-size: 4px;");

        String text = "";
        for (IonType ionType : types) {
            text += String.format("\t(%s : %f)"
                    , ionType.toString()
                    , ionType.getType() == B
                            ? annotation.getPeptide().getbSpectrum()[ionType.getNum()]
                            : annotation.getPeptide().getySpectrum()[ionType.getNum()]);
        }

        Element note = document.createElement("text");
        note.appendChild(document.createTextNode(text));
        note.setAttribute("transform", "translate(0 5) rotate(90)");

        label.appendChild(note);

        return label;
    }

    private static Element getPeak(Document document, double pos) {
        Element peak = document.createElement("g");
        peak.setAttribute("class", "peak");

        double x = scale(pos);
        Element peakTick = document.createElement("polyline");
        peakTick.setAttribute("points", String.format("%f,0 %f,4", x, x));
        peakTick.setAttribute("style", "stroke: black;" +
                "strike-width: 0.5;");
        peak.appendChild(peakTick);

        Element peakNote = document.createElement("text");
        peakNote.appendChild(document.createTextNode(String.valueOf(pos)));
        peakNote.setAttribute("transform", String.format("translate(%f -2) rotate(-70)", x));
        peakNote.setAttribute("style", "fill: black;" +
                "stroke: none;" +
                "font-size: 3.5px;");

        peak.appendChild(peakNote);

        return peak;
    }

    private static double scale(double len) {
        return SCALE * len;
    }

    public static class SpectrumSVGBuilder {

        private final Element element;
        private final Document document;
        private int cnt = 0;

        public SpectrumSVGBuilder(Document document) {
            this.document = document;
            this.element = document.createElement("g");
        }

        public void addSpectrum(Annotation annotation) {
            addSpectrum(annotation, annotation.getPeptide().getName());
        }

        public Element build() {
            return element;
        }

        public void addSpectrum(Annotation annotation, String label) {
            Element name = document.createElement("text");
            name.appendChild(document.createTextNode(label));
            name.setAttribute("transform", String.format("translate(0, %d)", cnt * 90 + 5));
            name.setAttribute("style", "font-size: 6px;");

            Element spectrum = getElement(document, annotation);
            spectrum.setAttribute("transform", String.format("translate(50, %d)", cnt * 90));

            element.appendChild(name);
            element.appendChild(spectrum);

            cnt++;
        }
    }
}
