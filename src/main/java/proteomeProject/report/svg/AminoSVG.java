package proteomeProject.report.svg;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.utils.Chemicals;

/**
 * Created by the7winds on 16.04.16.
 */
class AminoSVG {

    static final int width = 20;
    static final int height = 20;

    public static Element getElement(Document document, Chemicals.AminoAcid aminoAcid) {
        Element group = document.createElement("g");
        Element rect = document.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");

        // create container
        rect.setAttribute("width", Integer.toString(width));
        rect.setAttribute("height", Integer.toString(height));
        rect.setAttribute("style", String.format("stroke: black;" +
                "stroke-width: 0.5;" +
                "fill: rgb(%d, %d, %d)"
                , (int) aminoAcid.getAverageMass() * 131 % 256
                , (int) aminoAcid.getAverageMass() * 231 % 256
                , (int) aminoAcid.getAverageMass() * 337 % 256));
        group.appendChild(rect);

        // add acid's name
        Element name = document.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
        name.appendChild(document.createTextNode(aminoAcid.name()));
        name.setAttribute("transform", "translate(6, 14)");
        name.setAttribute("style", "font-family: arial;" +
                "fill: white;" +
                "font-weight: bold;" +
                "font-size: 10px;");
        group.appendChild(name);

        return group;
    }
}
