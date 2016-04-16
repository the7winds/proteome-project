package proteomeProject.dataEntities;

import proteomeProject.utils.Utils;

import java.util.Arrays;

/**
 * Created by the7winds on 14.04.16.
 */
public class Peptide {

    private final String peptide;
    private final double theoreticMass;
    private final double[] bSpectrum;
    private final double[] ySpectrum;

    public Peptide(String peptide) {
        this.peptide = Utils.getRealPeptideString(peptide);
        bSpectrum = Utils.getTheoreticSpectrum(peptide, IonType.Type.B);
        ySpectrum = Utils.getTheoreticSpectrum(peptide, IonType.Type.Y);
        theoreticMass = ySpectrum[ySpectrum.length - 1];
    }

    public Peptide(Peptide peptide) {
        this.peptide = peptide.getPeptide();
        theoreticMass = peptide.getTheoreticMass();
        bSpectrum = Arrays.copyOf(peptide.getbSpectrum(), peptide.getbSpectrum().length);
        ySpectrum = Arrays.copyOf(peptide.getySpectrum(), peptide.getySpectrum().length);
    }

    public String getPeptide() {
        return peptide;
    }

    public double[] getbSpectrum() {
        return bSpectrum;
    }

    public double[] getySpectrum() {
        return ySpectrum;
    }

    public double getTheoreticMass() {
        return theoreticMass;
    }
}
