package proteomeProject.dataEntities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by the7winds on 18.04.16.
 */
public class SpectrumWrapper {

    private static SpectrumWrapper INSTANCE;
    private final Map<File, Map<Integer, Spectrum>> scansToSpectrum;
    private final Collection<Spectrum> all;

    private SpectrumWrapper(Collection<Path> paths) throws IOException {
        scansToSpectrum = new HashMap<>();
        all = new LinkedList<>();

        for (Path path : paths) {
            scansToSpectrum.putIfAbsent(path.toFile(), new HashMap<>());
            BufferedReader reader = new BufferedReader(new FileReader(path.toFile()));

            while (reader.ready()) {
                while (reader.ready() && !reader.readLine().equals("BEGIN IONS")) ;

                if (reader.ready()) {
                    reader.readLine();  // skip id
                    final int scans = Integer.valueOf(reader.readLine().split("=")[1]);   // scans

                    reader.readLine();  // skip activation
                    reader.readLine();  // skip precursor_mz
                    final int precursorCharge = Integer.valueOf(reader.readLine().split("=")[1]);   // precursor_charge
                    final double precursorMass = Double.valueOf(reader.readLine().split("=")[1]);   // precursor_mass
                    final SortedSet<Double> peaks = new TreeSet<>();

                    // adds min and max spectrum bounds
                    peaks.add(0d);
                    peaks.add(precursorMass);

                    for (String line = reader.readLine(); !line.equals("END IONS"); line = reader.readLine()) {
                        peaks.add(Double.valueOf(line.split("\\s+")[0]));  // peak
                    }

                    Spectrum spectrum = new Spectrum(scans, precursorCharge, precursorMass, peaks);
                    spectrum.setSpecFile(path.toFile());
                    scansToSpectrum.get(path.toFile()).put(scans, spectrum);
                }
            }
            all.addAll(scansToSpectrum.get(path.toFile()).values());
        }
    }

    public static void init(Collection<Path> paths) throws IOException {
        if (INSTANCE == null) {
            INSTANCE = new SpectrumWrapper(paths);
        }
    }

    public static SpectrumWrapper getInstance() {
        return INSTANCE;
    }

    public Spectrum findSpectrumByScans(File file, int scans) {
        return scansToSpectrum.get(file).get(scans);
    }

    public Collection<Spectrum> getAllSpectrums() {
        return all;
    }
}
