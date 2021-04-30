package Empirical;

import Beliefs.QualitativeConstraintNetwork;
import Strategies.Conjecture;
import Strategies.ExactStrategy;
import Strategies.RevisionStrategy;

import java.util.*;

public class Generator {

    /**
     * Dictionnaire où chaque clé est une conjecture et chaque valeur un objet Analyser qui montre que la stratégie
     * ne respecte pas la conjecture. Si la conjecture est respectée, contient null
     */
    Map<Conjecture,Analyser> counterExamples = new HashMap<>();

    /**
     * Ensemble de RCQ sur lesquels on va faire des révisions pour la stratégie référence et la stratégie sujet
     */
    Set<QualitativeConstraintNetwork> beliefsSet = new HashSet<>();

    /**
     * Génère un ensemble de RCQ qui peut va servir à effectuer les tests. Stockés dans beliefsSet et renvoyé
     * @param numberOfQCN le nombre de QCN qu'on veut créer
     * @param QCNSize le nombre de sommets (variables) des QCN
     * @param nbrIterations le nombre de relations qu'on veut retirer aléatoirement à chaque RCQ "plein" pour
     *                      obtenir un nouveau RCQ aléatoire
     * @return les RCQ générés (attention, sans encapsulation des données)
     */
    public Set<QualitativeConstraintNetwork> generate(int numberOfQCN, int QCNSize, int nbrIterations){
        Set<QualitativeConstraintNetwork> qcnSet = new HashSet<>();
        for(int i=0;i<numberOfQCN;i++){
            QualitativeConstraintNetwork qcn = new QualitativeConstraintNetwork(QCNSize);
            try {
                qcn.deleteRelationRandomly(nbrIterations);
                qcnSet.add(qcn);
            } catch (Exception e) {
                i--;
            }
        }
        beliefsSet = qcnSet;
        return qcnSet;
    }

    /**
     * Teste des conjectures sur l'ensemble des stratégies pour les RCQ générés précédemment
     * @param reference la stratégie qui produit un résultat exact et de coût minimal qui va servir de référence pour la comparaison
     * @param subject la stratégie, parfois approximative, pour réviser
     * @param conjectures les conjectures qu'on veut tester
     * @return le dictionnaire qui contient pour chaque conjecturere null si la conjecture a été respectée,
     * et un objet Analyser s'il permet de montrer que la stratégie testée ne respecte pas la conjecture.
     */
    public Map<Conjecture,Analyser> checkConjectures(ExactStrategy reference, RevisionStrategy subject,
                                                     Conjecture... conjectures){
        int counter = 0;
        List<Conjecture> disprovedConjecture = new ArrayList<>();
        for(Conjecture conjecture : conjectures){
            counterExamples.put(conjecture,null);
        }
        for(QualitativeConstraintNetwork psi : beliefsSet){
            for(QualitativeConstraintNetwork mu : beliefsSet){
                if(psi!=mu) {
                    if (counter == conjectures.length) break;
                    Analyser analyser = new Analyser(psi, mu, reference, subject);
                    for (Conjecture conjecture : conjectures) {
                        if (!disprovedConjecture.contains(conjecture)) {
                            if (!analyser.checkConjecture(conjecture)) {
                                counterExamples.put(conjecture, analyser);
                                disprovedConjecture.add(conjecture);
                                counter++;
                            }
                        }
                    }
                }
            }
            if(counter == conjectures.length) break;
        }
        return counterExamples;
    }

}
