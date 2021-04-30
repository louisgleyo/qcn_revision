package Strategies;

import Beliefs.QualitativeConstraintNetwork;
import Empirical.Revision;
import Graph.Edge;

import java.util.*;

public class BranchAndBoundStrategy extends ExactStrategy {

    List<Edge> edgesList;
    Set<QualitativeConstraintNetwork> buffer ;
    private int distance ;

    @Override
    public Revision revise(QualitativeConstraintNetwork psi, QualitativeConstraintNetwork mu) {
        Set<QualitativeConstraintNetwork> psiScenarios = psi.toScenarios();
        edgesList = new ArrayList<>();
        distance = Integer.MAX_VALUE;
        buffer = new HashSet<>();

        long toc = System.currentTimeMillis();

        edgesList = new ArrayList<>(mu.getHalfEdges());
        DFS(psiScenarios,psi,mu,0);

        long tic = System.currentTimeMillis();

        return new Revision(tic-toc,distance,buffer);
    }

    /**
     * Algorithme récursif de Branch and bound en profondeur
     * @param psiScenarios les scénarios de psi (pour la vérification quand on a un scénario de mu)
     * @param psi psi
     * @param mu mu
     * @param deepness la profondeur de l'exploration, qui permet de décider quel arc il faut contraindre
     */
    public void DFS(Set<QualitativeConstraintNetwork> psiScenarios, QualitativeConstraintNetwork psi, QualitativeConstraintNetwork mu,int deepness){

        mu.complete();


        int heuristic = mu.heuristic(psi);

        if(heuristic<=distance) {

            if(deepness== edgesList.size()){
                int realDistance = mu.distance(psiScenarios);
                if (realDistance < distance) {
                    distance = realDistance;
                    buffer.clear();
                    buffer.add(mu);
                } else if(realDistance==distance) {
                    buffer.add(mu);
                }
            } else {

                Edge e = edgesList.get(deepness);
                Set<String> r = e.relations();

                QualitativeConstraintNetwork newMu = new QualitativeConstraintNetwork(mu);
                if (r.size() > 1) {
                    for (String s : r) {
                        newMu = new QualitativeConstraintNetwork(mu);
                        PropagationStatus status = newMu.contract(new HashSet<String>(Collections.singleton(s)), e.v1Id(), e.v2Id());
                        if(status!=PropagationStatus.INCONSISTENT){
                            DFS(psiScenarios,psi,newMu, deepness + 1);
                        }
                    }
                }
                DFS(psiScenarios,psi,newMu, deepness + 1);
            }
        }
    }

    @Override
    public String getName(){
        return "BranchAndBoundStrategy";
    }

}
