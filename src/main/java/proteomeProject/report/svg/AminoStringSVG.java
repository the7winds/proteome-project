package proteomeProject.report.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.utils.Chemicals;

/**
 * Created by the7winds on 16.04.16.
 */
class AminoStringSVG {

    public static Element getElement(Document document, String aminoString, Boolean forward) {
        Element group = document.createElement("g");

        char[] aminos = aminoString.toCharArray();

        for (int i = 0; i < aminos.length; ++i) {
            Chemicals.AminoAcid aminoAcid = Chemicals.AminoAcid.valueOf(aminos[i]);

            Element aminoSVG = forward != null
                    ? AminoSVG.getElement(document, aminoAcid, forward ? i + 1 : aminos.length - i)
                    : AminoSVG.getElement(document, aminoAcid, i + 1, aminos.length - i);

            aminoSVG.setAttribute("transform", String.format("translate(%d %d)", i * AminoSVG.width, 0));
            group.appendChild(aminoSVG);
        }

        return group;
    }

    public static Element getElement(Document document, String aminoString) {
        Element group = document.createElement("g");

        char[] aminos = aminoString.toCharArray();

        for (int i = 0; i < aminos.length; ++i) {
            Chemicals.AminoAcid aminoAcid = Chemicals.AminoAcid.valueOf(aminos[i]);
            Element aminoSVG = AminoSVG.getElement(document, aminoAcid);
            aminoSVG.setAttribute("transform", String.format("translate(%d %d)", i * AminoSVG.width, 0));
            group.appendChild(aminoSVG);
        }

        return group;
    }
}
