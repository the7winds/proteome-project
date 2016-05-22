package proteomeProject.report.svg;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.annotation.Annotation;

import static proteomeProject.dataEntities.IonType.Type.B;
import static proteomeProject.dataEntities.IonType.Type.Y;

/**
 * Created by the7winds on 16.04.16.
 */
public class AlignmentSVG {

    public static Element getElement(Document document, Annotation annotation) {
        Element group = document.createElement("g");

        Element peptide = AminoStringSVG.getElement(document, annotation.getPeptide().getPeptide());
        peptide.setAttribute("transform", "translate(0, 10)");
        group.appendChild(peptide);

        if (annotation.getTag() != null) {
            Element nums = getNumeration(document, annotation);

            Element arrow = getArrow(document, annotation);
            Element tag = AminoStringSVG.getElement(document,
                    annotation.getType() == B
                            ? annotation.getTag().getTag()
                            : StringUtils.reverse(annotation.getTag().getTag()));

            if (annotation.getType() == B) {
                tag.setAttribute("transform", String.format("translate(%d %d)"
                        , annotation.getFirst() * AminoSVG.width
                        , 10 + AminoSVG.height));
                arrow.setAttribute("transform", String.format("translate(%d %f)"
                        , annotation.getFirst() * AminoSVG.width
                        , 10 + 2.2 * AminoSVG.height));
            } else {
                tag.setAttribute("transform", String.format("translate(%d %d)"
                        , (annotation.getPeptide().getPeptide().length() - annotation.getFirst() - annotation.getTag().getTag().length()) * AminoSVG.width
                        , 10 + AminoSVG.height));
                arrow.setAttribute("transform", String.format("translate(%d %f)"
                        , (annotation.getPeptide().getPeptide().length() - annotation.getFirst() - annotation.getTag().getTag().length()) * AminoSVG.width
                        , 10 + 2.2 * AminoSVG.height));
            }

            group.appendChild(nums);
            group.appendChild(tag);
            group.appendChild(arrow);
        }

        return group;
    }

    private static Element getNumeration(Document document, Annotation annotation) {
        Element group = document.createElement("g");

        for (int i = 0; i < annotation.getPeptide().getPeptide().length(); ++i) {
            Element num = document.createElement("text");
            num.appendChild(document.createTextNode(Integer.toString(annotation.getType() == B
                    ? i + 1
                    : annotation.getPeptide().getPeptide().length() - i)));
            num.setAttribute("style", "font-size: 6px;");
            num.setAttribute("transform", String.format("translate(%d, 0)", i * AminoSVG.width + AminoSVG.width / 3));
            group.appendChild(num);
        }

        return group;
    }

    private static Element getArrow(Document document, Annotation annotation) {
        Element group = document.createElement("g");
        group.setAttribute("class", "arrow");

        Element line = document.createElement("polyline");
        Element arrowHead = document.createElement("polygon");

        if (annotation.getType() == Y) {
            arrowHead.setAttribute("points", "0,2 4,4 4,0");
        } else {
            double d = annotation.getTag().getTag().length() * AminoSVG.width;
            arrowHead.setAttribute("points", String.format("%f,2 %f,0 %f,4", d, d - 4, d - 4));
        }

        line.setAttribute("points", String.format("0,2 %d,2", annotation.getTag().getTag().length() * AminoSVG.width));

        Element stroke1 = document.createElement("polyline");
        stroke1.setAttribute("points", String.format("0,%d 0,%d"
                , -AminoSVG.height
                , AminoSVG.height / 3));

        Element stroke2 = document.createElement("polyline");
        stroke2.setAttribute("points", String.format("%d,%d %d,%d"
                , annotation.getTag().getTag().length() * AminoSVG.width
                , -AminoSVG.height
                , annotation.getTag().getTag().length() * AminoSVG.width
                , AminoSVG.height / 3));

        group.appendChild(line);
        group.appendChild(arrowHead);
        group.appendChild(stroke1);
        group.appendChild(stroke2);
        group.setAttribute("style", "stroke: black; stroke-width: 0.5px;");
        return group;
    }
}
