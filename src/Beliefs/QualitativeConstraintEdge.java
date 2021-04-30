package Beliefs;

import Algebra.RelationnalAlgebra;
import Graph.Edge;
import Graph.Vertex;

import java.util.*;

public class QualitativeConstraintEdge extends Edge {

    /**
     * Algèbre dans laquelle est définie l'arc
     */
    private RelationnalAlgebra relationnalAlgebra = RelationnalAlgebra.getInstance();

    /**
     *  Ensemble des relations possibles entre les deux sommets
     */
    private Set<String> relationsSet;

    /**
     * Constructeur par défaut, l'ensemble des relaitons est alors "?" (toutes les relations de l'algèbre)
     * @param v1 le sommet qui représente la première variable
     * @param v2 le sommet qui représente la deuxième variable
     */
    public QualitativeConstraintEdge(Vertex v1,Vertex v2){
        super(v1,v2);
        relationsSet = relationnalAlgebra.getRelations();
    }

    /**
     * Constructeur
     * @param v1 le sommet qui représente la première variable
     * @param v2 le sommet qui représente la deuxième variable
     * @param relationsSet l'ensemble des relations possibles entre les deux variables
     */
    public QualitativeConstraintEdge(Vertex v1, Vertex v2, Set<String> relationsSet){
        super(v1,v2);
        this.relationsSet=relationsSet;
    }

    /**
     * Permet de modifier l'ensemble des relations possibles entre les deux variables représentées par les sommets
     * @param relationsList le nouvel ensemble de relations qui relie les deux sommets
     */
    public void setRelationSet(Set<String> relationsList){
        this.relationsSet=relationsList;
    }

    /**
     * Renvoie une copie de l'ensemble des relations possible entre les deux variables représentées par les sommets
     * @return une copie de l'ensemble des deux relations
     */
    public Set<String> relations(){
        return Set.copyOf(relationsSet);
    }

    /**
     * Vérifie si les relations du sommets sont bien définies dans l'algèbre
     */
    public void checkAlgebra(){
        RelationnalAlgebra ra = RelationnalAlgebra.getInstance();
        for(String relation : relationsSet){
            if(!ra.contains(relation)){
                throw new AssertionError("Le RCQ n'est pas défini dans cette algèbre");
            }
        }
    }

    /**
     * Enleve aléatoirement une relation possible entre les deux sommets
     */
    public void deleteRelationRandomly(){
        Random tirage = new Random();
        List<String> relationList = new ArrayList<>(relationsSet);
        int relationNum = tirage.nextInt(relationsSet.size());
        relationsSet.remove(relationList.get(relationNum));
    }

    /**
     * Pour vérifier que le Edge traité est bien un QualitativeConstraintEdge
     * @return True car c'est le cas
     */
    public boolean isQualitativeConstraintEdge(){
        return true;
    }


    /**
     * Début de la chaine de caractères correspondante pour GraphzViz
     * @return le début de la chaine de caractères correspondante
     */
    public String toGraphVizBeginning() {
        StringBuilder sb = new StringBuilder();
        sb.append(v1.getId()).append(" -> ").append(v2.getId()).append(" [label=\"{");
        boolean isEmpty = true;
        for(String relation : relationsSet){
            sb.append(relation).append(";");
            isEmpty = false;
        }
        if(!isEmpty){
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append("}\"");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof QualitativeConstraintEdge) {
            QualitativeConstraintEdge e = (QualitativeConstraintEdge) o;
            if (e.v1Id().equals(v1Id()) && e.v2Id().equals(v2Id())) {
                if (relationsSet.size() == e.relations().size()) {
                    for (String relation : relationsSet) {
                        if (!e.relations().contains(relation)) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
