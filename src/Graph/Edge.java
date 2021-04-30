package Graph;

import Graph.*;

import java.util.Set;

/**
 * @author mihneadb
 */
public class Edge {

    protected String label;
    protected Vertex v1, v2;

    public Edge(Vertex v1, Vertex v2, String label) {
        this.v1 = v1;
        this.v2 = v2;
        this.label = label;
    }

    public Edge(Edge e) {
        this.v1 = e.getV1();
        this.v2 = e.getV2();
        this.label = e.getLabel();
    }

    public Edge(Vertex v1, Vertex v2) {
        this(v1, v2, "");
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setV1(Vertex v1) {
        this.v1 = v1;
    }

    public void setV2(Vertex v2) {
        this.v2 = v2;
    }

    public String v1Id(){
        return v1.getId();
    }

    public String v2Id(){
        return v2.getId();
    }

    public Vertex getV1() {
        return v1;
    }

    public Vertex getV2() {
        return v2;
    }

    public boolean isQualitativeConstraintEdge(){
        return false;
    }

    public boolean isDistanceEdge(){
        return false;
    }

    public void setCost(int cost){}

    public int getCost(){
        return 0;
    }

    public String toGraphVizBeginning() {
        return v1.getId() + " -> " +  v2.getId()+ " [label=\""+label+"\"";
    }

    public String toGraphViz(){
        return this.toGraphVizBeginning()+"]";
    }

    /**
     * Fonctions réservées aux QualitativeConstraintEdge
     */

    public void setRelationSet(Set<String> relationSet) {}

    public Set<String> relations(){ return null; }

    public void checkAlgebra(){}

    public void deleteRelationRandomly(){}

}