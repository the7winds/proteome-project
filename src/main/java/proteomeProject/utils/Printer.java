package proteomeProject.utils;

import java.io.PrintStream;

/**
 * Created by the7winds on 30.03.16.
 */
public interface Printer {

    void setUpOutput(PrintStream printStream);

    void print(Object object);
}
