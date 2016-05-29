package proteomeProject.alignment.condition;

import proteomeProject.annotation.Annotation;

import java.io.IOException;

/**
 * Created by the7winds on 28.05.16.
 */
public interface Condition {

    void addIf(Annotation annotation);
    void print(Annotation annotation);
    void printTxt(Annotation annotation);
    void printSvg(Annotation annotation);
    void makeReport() throws IOException;
}
