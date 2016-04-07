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
        A(71.0788),
        R(156.1875),
        N(114.1038),
        D(115.0886),
        C(103.1388),
        E(129.1155),
        Q(128.1307),
        G(57.0519),
        P(97.1167),
        S(87.0782),
        Y(163.1760),
        H(137.1411),
        I(113.1594),
        L(113.1594),
        K(128.1741),
        M(131.1926),
        F(147.1766),
        T(101.1051),
        W(186.2132),
        V(99.1326);

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