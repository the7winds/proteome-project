package proteomeProject.report.svg;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.utils.Chemicals;

/**
 * Created by the7winds on 16.04.16.
 */
class AminoSVG {

    private static final String CLASS = "amino";
    static final int width = 20;
    static final int height = 20;

    public static Element getElement(Document document, Chemicals.AminoAcid aminoAcid) {
        Element group = document.createElement("g");
        group.setAttribute("class", String.format("%s %s", CLASS, aminoAcid.name()));

        Element rect = document.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");

        // create container
        rect.setAttribute("width", Integer.toString(width));
        rect.setAttribute("height", Integer.toString(height));
        group.appendChild(rect);

        // add acid's name
        Element name = document.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
        name.appendChild(document.createTextNode(aminoAcid.name()));
        group.appendChild(name);

        return group;
    }
}
