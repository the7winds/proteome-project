package proteomeProject.report.svg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import proteomeProject.annotation.Annotation;

import java.io.FileNotFoundException;

/**
 * Created by the7winds on 16.04.16.
 */
public class InfoSVG {

    public static class InfoSVGBuilder {

        private Document document;
        private Element group;
        private int cnt = 0;

        public InfoSVGBuilder(Document document) {
            this.document = document;
            group = document.createElement("g");
            group.setAttribute("style", "font-size: 8px;");
        }

        public void addLine(String str) {
            Element text;
            text = document.createElement("text");
            text.appendChild(document.createTextNode(str));
            text.setAttribute("transform", String.format("translate(0 %d)", cnt * 10));
            group.appendChild(text);
            cnt++;
        }

        public Element build() {
            return group;
        }
    }

    public static Element getElement(Document document, Annotation annotation) throws FileNotFoundException {
        InfoSVGBuilder infoSVGBuilder = new InfoSVGBuilder(document);

        infoSVGBuilder.addLine(String.format("SCANS=%d\n", annotation.getSpectrum().getScans()));
        infoSVGBuilder.addLine(String.format("PEPTIDE=%s\n", annotation.getPeptide().getPeptide()));
        infoSVGBuilder.addLine(String.format("NAME=%s\n", annotation.getPeptide().getName()));
        infoSVGBuilder.addLine(String.format("PRECURSOR MASS=%f\n", annotation.getSpectrum().getPrecursorMass()));
        infoSVGBuilder.addLine(String.format("THEORETICAL MASS=%f\n", annotation.getPeptide().getTheoreticMass()));
        infoSVGBuilder.addLine(String.format("PRECURSOR MASS-THEORETICAL MASS=%f\n", annotation.getSpectrum().getPrecursorMass() - annotation.getPeptide().getTheoreticMass()));

        if (annotation.getTag() != null) {
            if (annotation.getType() != null) {
                infoSVGBuilder.addLine(String.format("TAG=%s FIRST=%s%d LAST=%s%d\n"
                        , annotation.getTag().getTag()
                        , annotation.getType().name(), annotation.getFirst()
                        , annotation.getType().name(), annotation.getLast()));
            } else {
                infoSVGBuilder.addLine(String.format("TAG=%s\n", annotation.getTag().getTag()));
            }
        }

        return infoSVGBuilder.build();
    }
}
