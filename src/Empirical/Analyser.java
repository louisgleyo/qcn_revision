package Empirical;

import Beliefs.QualitativeConstraintNetwork;
import Strategies.Conjecture;
import Strategies.ExactStrategy;
import Strategies.RevisionStrategy;

import java.util.Map;
import java.util.Set;

public class Analyser {

    /**
     * Psi (RCQ de gauche dans la révision)
     */
    private final QualitativeConstraintNetwork psi;

    /**
     * Mu (RCQ de droite dans la révison)
     */
    private final QualitativeConstraintNetwork mu;

    /**
     * La stratégie exacte, censée avoir trouvé la révision dont tous les scénarios sont de coût minmal avec psi
     */
    private final ExactStrategy reference;
    /**
     * La révision trouvée avec la stratégie exacte
     */
    private final Revision referenceResult;

    /**
     * La stratégie dont on veut tester les résultats, qui n'est pas forcément exacte
     */
    private final RevisionStrategy subject;
    /**
     * La révision trouvée avec la stratégie sujet
     */
    private final Revision resultToTest;

    public  Analyser(QualitativeConstraintNetwork psi, QualitativeConstraintNetwork mu, ExactStrategy reference, RevisionStrategy subject){
        this.mu = mu;
        this.psi = psi;
        this.reference = reference;
        referenceResult = reference.revise(psi,mu);

        this.subject = subject;
        resultToTest = subject.revise(psi,mu);
    }

    /**
     * Renvoie la stratégie référence, censée avoir trouvé la révision dont tous les scénarios sont de coût minmal avec psi
     * @return la stratégie référence
     */
    public ExactStrategy referenceStrategy(){
        return reference;
    }

    /**
     * La stratégie sujet, dont on veut tester les résultats, qui n'est pas forcément exacte
     * @return la stratégie sujet
     */
    public RevisionStrategy subjectStrategy(){
        return subject;
    }

    /**
     * La révision trouvée avec la stratégie sujet
     */
    public Revision subjectResult(){
        return resultToTest;
    }

    /**
     * Renvoie les résultats trouvés avec la stratégie de référence, c'est à dire la révision tels que tous les
     * scénarios de cette révision sont de distance minimale avec psi
     * @return les résultats trouvés avec la stratégie de référence
     */
    public Revision referenceResult(){
        return referenceResult;
    }

    /**
     * Renvoie la différence de temps de révision entre la stratégie de référence et la stratégie sujet
     * @return la différence
     */
    public long timeDifference(){
        return referenceResult.distance()-resultToTest.distance();
    }

    /**
     * Renvoie la distance entre les deux révisions trouvées avec la stratégie référence et la stratégie sujet
     * @return la distance
     */
    public int resultsDistance(){
        Set<QualitativeConstraintNetwork> referenceScenarios = referenceResult.toScenarios();
        Set<QualitativeConstraintNetwork> testScenarios = resultToTest.toScenarios();

        int i  = Integer.MAX_VALUE;
        for(QualitativeConstraintNetwork scenario : referenceScenarios){
            i = Math.min(i,scenario.heuristic(testScenarios));
        }
        return i;
    }

    /**
     * Permet de tester une conjecture la stratégie sujet
     * @param conjecture la conjecture a tester
     * @return true si la conjecture est respectée, false sinon
     */
    public boolean checkConjecture(Conjecture conjecture){
        return switch (conjecture) {
            case CORRECTION -> checkCorrection();
            case AGM_CORRECTION -> checkAGM1();
            case COMPLETNESS -> checkCompletness();
            default -> throw new AssertionError("Cette conjecture ne peut pas encore être testée");
        };
    }

    /**
     * Permet de tester la complétude de la stratégie sujet
     * @return true si la conjecture est respectée, false sinon
     */
    private boolean checkCompletness() {
        for(QualitativeConstraintNetwork referenceQCN : referenceResult.result()){
            boolean include = false;
            for(QualitativeConstraintNetwork testQCN : resultToTest.result()){
                if(testQCN.includes(referenceQCN)){
                    include = true;
                    break;
                }
            }
            if(!include){
                return false;
            }
        }
        return true;
    }

    /**
     * Permet de tester la correction de la stratégie sujet
     * @return true si la conjecture est respectée, false sinon
     */
    private boolean checkCorrection(){
        for(QualitativeConstraintNetwork testQCN : resultToTest.result()){
            boolean include = false;
            for(QualitativeConstraintNetwork referenceQCN : referenceResult.result()){
                if(referenceQCN.includes(testQCN)){
                    include = true;
                    break;
                }
            }
            if(!include){
                return false;
            }
        }
        return true;
    }

    /**
     * Permet de tester le postulat AGM-1 sur la stratégie sujet
     * @return true si la conjecture a été respectée, false sinon
     */
    private boolean checkAGM1(){
        return mu.includes(resultToTest.result());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PSI :\n");
        sb.append(psi.toGraphViz()).append("\n");
        sb.append("MU :\n");
        sb.append(mu.toGraphViz()).append("\n");
        sb.append("EXACT STRATEGY : ").append(reference.getName()).append("\n");
        for(QualitativeConstraintNetwork qcn : referenceResult.result()){
            sb.append(qcn.toGraphViz()).append('\n');
        }
        sb.append("DISTANCE : ").append(referenceResult.distance()).append("\n");
        sb.append("TESTED STRATEGY : ").append(subject.getName()).append("\n");
        for(QualitativeConstraintNetwork qcn : resultToTest.result()){
            sb.append(qcn.toGraphViz()).append('\n');
        }
        sb.append("DISTANCE :").append(resultToTest.distance()).append("\n");
        return sb.toString();
    }
}
