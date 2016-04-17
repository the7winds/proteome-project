package proteomeProject.report.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.annotation.Annotation;

import java.io.FileNotFoundException;

/**
 * Created by the7winds on 16.04.16.
 */
public class InfoSVG {

    private static final String CLASS = "info";

    public static Element getElement(Document document, Annotation annotation) throws FileNotFoundException {
        Element group = document.createElement("g");
        group.setAttribute("class", CLASS);

        Element text;
        text = document.createElement("text");
        text.appendChild(document.createTextNode(String.format("SCANS=%d\n", annotation.getSpectrum().getScans())));
        text.setAttribute("transform", "translate(0 10)");
        group.appendChild(text);

        text = document.createElement("text");
        text.appendChild(document.createTextNode(String.format("PEPTIDE=%s\n", annotation.getPeptide().getPeptide())));
        text.setAttribute("transform", "translate(0 20)");
        group.appendChild(text);

        text = document.createElement("text");
        text.appendChild(document.createTextNode(String.format("NAME=%s\n", annotation.getPeptide().getName())));
        text.setAttribute("transform", "translate(0 30)");
        group.appendChild(text);

        text = document.createElement("text");
        text.appendChild(document.createTextNode(String.format("PRECURSOR MASS=%f\n", annotation.getSpectrum().getPrecursorMass())));
        text.setAttribute("transform", "translate(0 40)");
        group.appendChild(text);

        text = document.createElement("text");
        text.appendChild(document.createTextNode(String.format("THEORETICAL MASS=%f\n", annotation.getPeptide().getTheoreticMass())));
        text.setAttribute("transform", "translate(0 50)");
        group.appendChild(text);

        text = document.createElement("text");
        text.appendChild(document.createTextNode(String.format("PRECURSOR MASS-THEORETICAL MASS=%f\n", annotation.getSpectrum().getPrecursorMass() - annotation.getPeptide().getTheoreticMass())));
        text.setAttribute("transform", "translate(0 60)");
        group.appendChild(text);

        if (annotation.getTag() != null) {
            if (annotation.getType() != null) {
                text = document.createElement("text");
                text.appendChild(document.createTextNode(String.format("TAG=%s FIRST=%s%d LAST=%s%d\n"
                        , annotation.getTag().getTag()
                        , annotation.getType().name(), annotation.getFirst()
                        , annotation.getType().name(), annotation.getLast())));
            } else {
                text = document.createElement("text");
                text.appendChild(document.createTextNode(String.format("TAG=%s\n", annotation.getTag().getTag())));
            }
            text.setAttribute("transform", "translate(0 70)");
            group.appendChild(text);
        }

        return group;
    }
}
