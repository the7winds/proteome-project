package proteomeProject.alignment;

import proteomeProject.alignment.condition.Condition;
import proteomeProject.annotation.Annotation;

import java.io.IOException;

/**
 * Created by the7winds on 29.05.16.
 */
public class ConditionTask {

    protected final Condition condition;

    ConditionTask(Condition condition) {
        this.condition = condition;
    }

    public void eval(Annotation annotation) {
        condition.addIf(annotation);
        condition.print(annotation);
    }

    public void makeReport() throws IOException {
        condition.makeReport();
    }
}
