package proteomeProject.report.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.annotation.Annotation;
import proteomeProject.dataEntities.Peptide;
import proteomeProject.dataEntities.VariantsStandards;
import proteomeProject.utils.Chemicals;

import java.util.Map;

/**
 * Created by the7winds on 22.05.16.
 */
public class ModificationsSVG {

    public static Element getElement(Document document, Annotation variant) {
        Peptide standard = VariantsStandards.getInstance().getStandard(variant.getPeptide().getName());

        if (standard != null) {
            Element group = document.createElement("g");
            group.setAttribute("style", "font-size: 6px");

            Element stdLabel = document.createElement("text");
            stdLabel.appendChild(document.createTextNode(standard.getName()));
            stdLabel.setAttribute("transform", String.format("translate(0, %d)", AminoSVG.height / 2));

            Element varLabel = document.createElement("text");
            varLabel.appendChild(document.createTextNode(variant.getPeptide().getName()));
            varLabel.setAttribute("transform", String.format("translate(0, %d)", 7 * AminoSVG.height / 4));

            Element stdPeptide = AminoStringSVG.getElement(document, standard.getPeptide());
            stdPeptide.setAttribute("transform", String.format("translate(%d, 0)", AminoSVG.width * 4));

            Element varPeptide = getVarAminoString(document, variant);
            varPeptide.setAttribute("transform", String.format("translate(%d, %d)", AminoSVG.width * 4, 5 * AminoSVG.height / 4));

            group.appendChild(stdLabel);
            group.appendChild(stdPeptide);
            group.appendChild(varLabel);
            group.appendChild(varPeptide);

            return group;
        }

        return null;
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
