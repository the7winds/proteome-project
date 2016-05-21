package proteomeProject.dataEntities;

import proteomeProject.utils.Utils;

import java.util.Arrays;

/**
 * Created by the7winds on 14.04.16.
 */
public final class Peptide {

    private final String name;
    private final String peptide;
    private final double theoreticMass;
    private final double[] bSpectrum;
    private final double[] ySpectrum;
    private final double[] shiftedBSpectrum;
    private final double[] shiftedYSpectrum;

    public Peptide(String name, String peptide) {
        this.name = name;
        this.peptide = Utils.getRealPeptideString(peptide);
        bSpectrum = Utils.getTheoreticSpectrum(peptide, IonType.Type.B);
        ySpectrum = Utils.getTheoreticSpectrum(peptide, IonType.Type.Y);
        shiftedBSpectrum = Arrays.copyOf(bSpectrum, bSpectrum.length);
        shiftedYSpectrum = Arrays.copyOf(ySpectrum, ySpectrum.length);
        theoreticMass = ySpectrum[ySpectrum.length - 1];
    }

    public Peptide(Peptide peptide) {
        this.name = peptide.name;
        this.peptide = peptide.peptide;
        theoreticMass = peptide.theoreticMass;
        bSpectrum = Arrays.copyOf(peptide.bSpectrum, peptide.bSpectrum.length);
        ySpectrum = Arrays.copyOf(peptide.ySpectrum, peptide.ySpectrum.length);
        shiftedBSpectrum = Arrays.copyOf(bSpectrum, bSpectrum.length);
        shiftedYSpectrum = Arrays.copyOf(ySpectrum, ySpectrum.length);
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

    public String getName() {
        return name;
    }

    public void shiftBSpectrum(double delta) {
        for (int i = 0; i < shiftedBSpectrum.length; ++i) {
            shiftedBSpectrum[i] = bSpectrum[i] + delta;
        }
    }

    public void shiftYSpectrum(double delta) {
        for (int i = 0; i < shiftedYSpectrum.length; ++i) {
            shiftedYSpectrum[i] = ySpectrum[i] + delta;
        }
    }

    public double[] getShiftedBSpectrum() {
        return shiftedBSpectrum;
    }

    public double[] getShiftedYSpectrum() {
        return shiftedYSpectrum;
    }
}
