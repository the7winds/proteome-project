package proteomeProject.report.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.IonType;

import java.util.List;
import java.util.Map;

/**
 * Created by the7winds on 16.04.16.
 */
public class SpectrumSVG {

    private static final double SCALE = 0.5;
    private static final String CLASS = "spectrum";

    public static Element getElement(Document document, Annotation annotation) {
        Element group = document.createElement("g");
        group.setAttribute("class", CLASS);

        Element axis = getAxis(document, annotation);

        group.appendChild(axis);

        return group;
    }

    private static Element getAxis(Document document, Annotation annotation) {
        Element group = document.createElement("g");
        group.setAttribute("class", "axis");

        Element line = document.createElement("polyline");

        line.setAttribute("points", String.format("0,2 %f,2", scale(annotation.getSpectrum().getPrecursorMass())));
        // line.setAttribute("stroke", "black");
        // line.setAttribute("stroke-width", "1");

        group.appendChild(line);

        for (Map.Entry<Double, List<IonType>> entry: annotation.getAnnotations().entrySet()) {
            Element peak = getPeaK(document, entry.getKey());
            group.appendChild(peak);

            if (entry.getValue().size() > 0) {
                Element label = getLabel(document, entry.getKey(), entry.getValue());
                group.appendChild(label);
            }
        }

        return group;
    }

    private static Element getLabel(Document document, double pos, List<IonType> types) {
        double x = scale(pos);

        Element label = document.createElement("g");
        label.setAttribute("class", "label");
        label.setAttribute("transform", String.format("translate(%f 3)", x));

        String text = "";
        for (IonType ionType : types) {
            text += "\t" + ionType.toString();
        }

        Element note = document.createElement("text");
        note.appendChild(document.createTextNode(text));
        note.setAttribute("transform", "translate(0 20) rotate(90)");


        Element pointer = document.createElement("polyline");
        pointer.setAttribute("points", "0,5 0,20");

        label.appendChild(note);
        label.appendChild(pointer);

        return label;
    }

    private static Element getPeaK(Document document, double pos) {
        Element peak = document.createElement("g");
        peak.setAttribute("class", "peak");

        double x = scale(pos);
        Element peakTick = document.createElement("polyline");
        peakTick.setAttribute("points", String.format("%f,0 %f,4", x, x));

        peak.appendChild(peakTick);

        Element peakNote = document.createElement("text");
        peakNote.appendChild(document.createTextNode(String.valueOf(x)));
        peakNote.setAttribute("class", "peakNote");
        peakNote.setAttribute("transform", String.format("translate(%f -2) rotate(-70)", x));

        peak.appendChild(peakNote);

        return peak;
    }

    private static double scale(double len) {
        return SCALE * len;
    }
}
