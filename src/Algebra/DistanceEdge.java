package Algebra;

import Graph.Edge;
import Graph.Vertex;

public class DistanceEdge extends Edge {

    public int cost;

    /**
     * Constructeur
     * @param v1 le premier sommet
     * @param v2 le deuxième le deuxième sommet
     * @param cost le coût pour aller de l'un à l'autre
     */
    public DistanceEdge(Vertex v1, Vertex v2, int cost) {
        super(v1, v2);
        assert cost>0 : "Les coûts doivent être positifs";
        this.cost = cost;
    }

    /**
     * Permet d'initialiser un cout
     * @param cost le coût, qui doit être positif comme il s'agit de distances
     */
    public void setCost(int cost){
        assert cost>0 : "Les coûts doivent être positifs dans un graphe de distance";
        this.cost = cost;


    }

    /**
     * Renvoie le coût du vecteur
     * @return le coût
     */
    public int getCost(){
        return cost;
    }

    /**
     * Permet de savoir si l'arc est bien un arc pour un graphe de distance
     * @return true comme c'est le cas
     */
    public boolean isDistanceEdge(){
        return true;
    }

    @Override
    public String toGraphViz() {
        return super.toGraphVizBeginning()+",dir=\"none\"]";
    }
}
