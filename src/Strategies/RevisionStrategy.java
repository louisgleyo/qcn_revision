package Strategies;

import Algebra.RelationnalAlgebra;
import Beliefs.QualitativeConstraintNetwork;
import Empirical.Revision;


public abstract class RevisionStrategy {

    /**
     * Algebre relationnelle dans laquelle on effectue la révision
     */
    protected RelationnalAlgebra ra = RelationnalAlgebra.getInstance();

    /**
     * Renvoie la révision de psi par mu
     * @param psi psi
     * @param mu mu
     * @return la révision
     */
    public abstract Revision revise(QualitativeConstraintNetwork psi, QualitativeConstraintNetwork mu);

    /**
     * Permet d'obtenir le nom de la stratégie de révision
     * @return le nom
     */
    public abstract String getName();

}
