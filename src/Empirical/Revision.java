package Empirical;

import Beliefs.QualitativeConstraintNetwork;

import java.util.HashSet;
import java.util.Set;

public class Revision {

    private final int distance;
    private final long time;
    private final Set<QualitativeConstraintNetwork> qcnSet;

    /**
     * Constructeur de la Revision
     * @param time temps mis pour trouver le résultat
     * @param distance distance avec la révision exacte de coût minimal
     * @param qcnSet l'ensemble des scénarios qui forme la révision trouvée
     */
    public Revision(long time, int distance, Set<QualitativeConstraintNetwork> qcnSet){
        this.time = time;
        this.distance = distance;
        this.qcnSet = qcnSet;
    }

    /**
     * Renvoie la distance avec la révision exacte de coût minimal
     * @return la distance
     */
    public int distance() {
        return distance;
    }

    /**
     * Renvoie le temps mis pour trouver ce résultat
     * @return le temps
     */
    public long time(){
        return time;
    }

    /**
     * Renvoie les résulttats sous la forme d'un ensemble de RCQ
     * @return les résultats
     */
    public Set<QualitativeConstraintNetwork> result(){
        return qcnSet;
    }

    /**
     * Renvoie les résultats sous la forme d'un ensemble de sécnarios
     * @return les résultats
     */
    public Set<QualitativeConstraintNetwork> toScenarios() {
        Set<QualitativeConstraintNetwork> scenariosSet = new HashSet<>();
        for(QualitativeConstraintNetwork qcn : qcnSet){
            scenariosSet.addAll(qcn.toScenarios());
        }
        return scenariosSet;
    }

}
