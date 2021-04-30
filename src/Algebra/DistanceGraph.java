package Algebra;

import Graph.*;
import Tools.MultiKeyHashMap;

import java.util.*;

public class DistanceGraph extends Graph {

    /**
     * Booléen pour savoir si toutes les distances ont déjà été calculées
     */
    private boolean setted = false;

    /**
     * Dictionnaire à double clé pour connaitre la distance entre deux relations
     */
    private MultiKeyHashMap<String,String,Integer> distanceMap =  new MultiKeyHashMap<>();

    /**
     * Constructeur du graphe de distances
     */
    public DistanceGraph() {
        super();
    }

    /**
     * Ajoute un arc au graphe
     * @param e l'arc à ajouter
     */
    public void addEdge(Edge e) {
        if(!e.isDistanceEdge()){
            throw new AssertionError("Vous devez mettre des DistanceRelationsEdge en paramètres");
        }
        super.addEdge(e);
    }

    /**
     * Permet d'intialiser le dictionnaire des distances si ce n'est pas déjà fait
     */
    public void setMap(){
        if(!setted) {
            for (String x : V.keySet()) {
                setMap(x);
            }
            setted = true;
        }
    }

    /**
     * Permet d'initaliser le graphe de distances pour un certain sommet si ce n'est pas déjà fait
     * @param x le graphe de distance
     */
    public void setMap(String x){
        assert (super.V.containsKey(x)) : "Ce noeud n'existe pas";

        for(String y : super.V.keySet()){
            if(y.equals(x)){
                distanceMap.put(x,y,0);
            } else {
                distanceMap.put(x,y,Integer.MAX_VALUE);
            }
        }

        int size = super.V.size();
        for (int i = 0; i < size; i++) {
            Map<String, Integer> dictTemp = new HashMap<>();

            for (String y : super.V.keySet()) {
                dictTemp.put(y,distanceMap.get(x,y));
                    for(String z : super.V.keySet()) {
                        Edge e2 = getEdge(z, y);
                        if (e2 == null) {
                            e2 = getEdge(y, z);
                        }
                        if (e2 != null) {
                            int oldValue = dictTemp.get(y);
                            dictTemp.remove(y);
                            if(distanceMap.get(x,z)==Integer.MAX_VALUE){
                                dictTemp.put(y,oldValue);
                            } else {
                                dictTemp.put(y, Math.min(distanceMap.get(x, z) + e2.getCost(), oldValue));
                            }
                        }
                    }
            }
            for(String y : dictTemp.keySet()){
                distanceMap.put(x,y,dictTemp.get(y));
            }
        }
    }

    /**
     * Renvoie la distance entre deux relations
     * @param x la première relation
     * @param y la deuxième distance
     * @return la distance entre les deux relations
     */
    public int getDistance(String x,String y){
        setMap(); // Définit les distance si ce n'est pas déjà fait
        return distanceMap.get(x,y);
    }

}
