package Algebra;

import Graph.Vertex;
import Tools.MultiKeyHashMap;

import javax.annotation.processing.FilerException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WARNING : Comportement des opérateurs non-gérés en cas d'ensemble vide
 */

public class RelationnalAlgebra {

    /**
     * Ensemble des relations, représenté par une String
     */
    private Set<String> relationList = new HashSet<>();

    /**
     * Dictionnaire qui permet de récupérer l'inverse d'une relation
     */
    private Map<String,String> reverseMap = new HashMap<>();

    /**
     * Dictionnaire de composition
     */
    private MultiKeyHashMap<String,String,Set<String>> compositionMap = new MultiKeyHashMap<>();

    /**
     * Graphe de distance qui permet de connaitre la distance entre deux relations
     */
    private DistanceGraph distanceGraph = new DistanceGraph();

    /**
     * Constructeur privé pour le Singleton
     */
    private RelationnalAlgebra(){}

    /**
     * Instance du singleton
     */
    private static RelationnalAlgebra INSTANCE = null;

    /**
     * Pour récupérer une instance de l'algèbre relationnel
     * @return l'instance
     */
    public static RelationnalAlgebra getInstance(){
        if(INSTANCE==null){
            INSTANCE = new RelationnalAlgebra();
        }
        return INSTANCE;
    }

    /**
     * Permet de récupérer toutes les relations d'une algèbre
     * @return une copie de la liste des relations
     */
    public Set<String> getRelations(){
        return new HashSet<>(relationList);
    }

    /**
     * Initialisation de l'algèbre à parti d'un fichier
     * @param filename le nom du fichier
     * @throws IOException si le fichier n'est pas aux normes
     */
    public void initialize(String filename) throws IOException {

        Map<Integer,String> relationMap = new HashMap<>();

        BufferedReader in = new BufferedReader(new FileReader(filename));
        Pattern p = Pattern.compile("^[0-9a-zA-Z]+$");
        Matcher matcher;


        String line = in.readLine();
        String equalsString = new String(line);
        line = in.readLine();
        String[] parts = line.split("\t");
        int size =0;
        for(String relation : parts){
            matcher = p.matcher(relation);
            if (matcher.matches() && !relationMap.containsValue(relation)) {
                relationMap.put(size++,relation);
            } else {
                throw new AssertionError("Le nom de variable ne doit contenir que des chiffres et des lettres"+
                        "et deux variable ne peuvent pas avoir le même nom.");
            }
        }

        String[] partsList ;
        Set<String> compositionList;
        for(int i=0;i<size;i++){
            line = in.readLine();
            Map<String,Set<String>> compositionParticularMap = new HashMap<>();
            parts = line.split("\t");
            if(parts.length!=size){
                throw new FilerException("Tableau de composition de mauvaise taille");
            }
            for(int j = 0;j<size;j++){
                String liste = parts[j];
                if(liste.equals("*")){
                    compositionList = new HashSet<>(relationMap.values());
                } else {
                    partsList = liste.split(",");
                    for(String relation : partsList){
                        if(!relationMap.containsValue(relation)){
                            throw new FilerException("Composition avec des variables non définies");
                        }
                    }
                    compositionList = new HashSet<>(Arrays.asList(partsList));
                }
                compositionMap.put(relationMap.get(i),relationMap.get(j),compositionList);
            }
        }

        relationList = new HashSet<>(relationMap.values());

        Map<String,Vertex> vertexMap = new HashMap<>();
        for(String s : relationList){
            Vertex vs = new Vertex(s);
            vertexMap.put(s,vs);
            distanceGraph.addVertex(vs);
        }

        while((line = in.readLine())!=null){
            String[] informationsEdge = line.split("\t");
            if(informationsEdge.length==2){
                if(relationList.contains(informationsEdge[0])&&relationList.contains(informationsEdge[1])){
                    DistanceEdge e = new DistanceEdge(vertexMap.get(informationsEdge[0]),vertexMap.get(informationsEdge[1]),1);
                    distanceGraph.addEdge(e);
                } else {
                    throw new FilerException("Problème de format fichier pour le graphe de distance");
                }
            } else if (informationsEdge.length==3){
                if(relationList.contains(informationsEdge[0])&&relationList.contains(informationsEdge[1])){
                    DistanceEdge e = new DistanceEdge(vertexMap.get(informationsEdge[0]),vertexMap.get(informationsEdge[1]),Integer.parseInt(informationsEdge[2]));
                    distanceGraph.addEdge(e);
                } else {
                    throw new FilerException("Problème de format fichier pour le graphe de distance");
                }
            }
        }

        in.close();

        for(String relation : relationMap.values()){
            Map<String, Set<String>> compositionReducedMap =  compositionMap.get(relation);
            for(String s : compositionReducedMap.keySet()){
                if(compositionReducedMap.get(s).contains(equalsString)){
                    reverseMap.put(relation,s);
                    reverseMap.put(s,relation);
                }
            }
        }
    }


    /**
     * Vérifie si une relation est définie dans l'algèbre
     * @param relation la relation à vérifier
     * @return true si la relation est définie, false sinon
     */
    public boolean contains(String relation) {
        return relationList.contains(relation);
    }

    /**
     * Opérateur d'inversion pour un ensemble de relations
     * @param relationsSet l'ensemble de relations à inverser
     * @return l'inverse de l'ensemble de relations
     */
    public Set<String> reverse(Set<String> relationsSet){
        Set<String> reverse = new HashSet<>();
        for(String relation : relationsSet){
            reverse.add(reverse(relation));
        }
        return reverse;
    }

    /**
     * Opérateur d'inversion pour une relation
     * @param relation la relation à inverser
     * @return l'inverse de la relation
     */
    public String reverse(String relation){
        if(relationList.contains(relation)){
            return reverseMap.get(relation);
        } else {
            throw new AssertionError("Relation inconnue dans cet algèbre");
        }
    }

    /**
     * Opérateur de composition sur deux ensembles de relations
     * @param relations1 le premier ensemble de relations
     * @param relations2 le deuxième ensemble de relations
     * @return la composition
     */
    public Set<String> compose(Set<String> relations1, Set<String> relations2){
        Set<String> composition = new HashSet<>();
        for(String relation1 : relations1){
            for(String relation2 : relations2){
                composition.addAll(compose(relation1,relation2));
            }
        }
        return composition;
    }

    /**
     * Opérateur de composition entre deux relations
     * @param id1 la premiere relation
     * @param id2 la deuxieme relation
     * @return la composition
     */
    public Set<String> compose(String id1, String id2){
        if(relationList.contains(id1)&&relationList.contains(id2)){
            return new HashSet<>(compositionMap.get(id1,id2));
        } else {
            throw new AssertionError("Relation inconnue dans cet algèbre");
        }
    }

    /**
     * Opérateur d'union
     * @param relationsList la liste des différents ensembles de relations dont il faut faire l'union
     * @return l'union
     */
    public Set<String> union(Set<String>... relationsList){
        Set<String> firstSet = new HashSet<>(relationsList[0]);
        for(Set<String> set : relationsList){
            if(set!=null) {
                Set<String> copySet = new HashSet<>(set);
                firstSet.addAll(copySet);
            }
        }
        return firstSet;
    }

    /**
     * Opérateur d'intersection
     * @param relationsList la liste des différents ensembles de relations dont il faut faire l'intersection
     * @return l'intersection
     */
    public Set<String> intersection(Set<String>... relationsList){
        if(relationsList[0]!=null) {
            Set<String> firstSet = new HashSet<>(relationsList[0]);
            for (Set<String> set : relationsList) {
                if (set != null) {
                    Set<String> copySet = new HashSet<>(set);
                    firstSet.retainAll(copySet);
                }
            }
            return firstSet;
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Opérateur de relaxation
     * @param relationSetExtern l'ensemble de relations à relacher
     * @return le relâchement de l'ensemble des relations
     */
    public Set<String> relax(Set<String> relationSetExtern){
        if(relationSetExtern!=null) {
            Set<String> newSet = new HashSet<>(relationSetExtern);
            for (String x : relationSetExtern) {
                for (String y : relationList) {
                    if (distanceGraph.getDistance(x, y) == 1) {
                        newSet.add(y);
                    }
                }
            }
            return newSet;
        } else {
            throw new AssertionError("Vous demandez l'inverse d'une relation vide");
        }
    }

    /**
     * Opérateur de distance
     * @param relationsSet1 le premier ensemble à comparer
     * @param relationsSet2 le deuxième ensemble à comparer
     * @return le minimum de chaque distance deux à deux
     */
    public int distance(Set<String> relationsSet1, Set<String> relationsSet2){
        int i = Integer.MAX_VALUE;
        for(String x : relationsSet1){
            for(String y : relationsSet2){
                i = Math.min(i,distance(x,y));
                if(i==0){
                    break;
                }
            }
        }
        return i;
    }

    /**
     * Donne la distance entre deux relations
     * @param x la première relation
     * @param y la deuxième relation
     * @return la distance entre ses deux relations dans le graphe de distance
     */
    public int distance(String x,String y){
        return distanceGraph.getDistance(x,y);
    }

}
