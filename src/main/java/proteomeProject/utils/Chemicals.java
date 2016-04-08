package proteomeProject.utils;

/**
 * Created by the7winds on 23.03.16.
 */

/**
 * The enum describes chemical substances which can be used in evaluation,
 * for example H2O. There are sub-enum which describes 20 amino acids.
 */

public enum Chemicals {
    H2O(18.01528),
    NH3(17.031);

    /**
     * The enum describes 20 amino acids
     */

    public enum AminoAcid {
        A(71.037114),
        R(156.10111),
        N(114.04293),
        D(115.02694),
        C(103.00919),
        E(129.04259),
        Q(128.05858),
        G(57.021464),
        P(97.052764),
        S(87.032029),
        Y(163.06333),
        H(137.05891),
        I(113.08406),
        L(113.08406),
        K(128.09496),
        M(131.04048),
        F(147.06841),
        T(101.04768),
        W(186.07931),
        V(99.068414);

        private final double averageMass;

        AminoAcid(double averageMass) {
            this.averageMass = averageMass;
        }

        public double getAverageMass() {
            return averageMass;
        }

        public static double getMass(char c) {
            return valueOf(Character.toString(c)).getAverageMass();
        }

    }

    private final double mass;

    Chemicals(double mass) {
        this.mass = mass;
    }

    public double getMass() {
        return mass;
    }
}