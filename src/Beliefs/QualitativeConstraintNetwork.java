package Beliefs;

import Algebra.RelationnalAlgebra;
import Exceptions.QCNException;
import Graph.*;
import Strategies.PropagationStatus;
import Tools.MultiKeyHashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class QualitativeConstraintNetwork extends Graph {

    // Pour debugguer les fonctions de Dufour-Lussier - Ne pas prendre en compte
    private int counter = 0;

    /**
     * Champ pour savoir si les arcs ont été fait dans les deux sens pour les deux sommets
     */
    public boolean complete=false;

    /**
     * Algèbre dans laquelle est défini le RCQ
     */
    private final RelationnalAlgebra ra = RelationnalAlgebra.getInstance();

    /**
     * Constructeur d'un RCQ plein (tous les sommets sont liés par un "?",
     * ce qui veut dire qu'ils ont potentiellement toutes les relations possibles)
     * @param nbrVertex le nombre de sommets
     */
    public QualitativeConstraintNetwork(int nbrVertex){
        super();
        assert (nbrVertex<=26&&nbrVertex>2) : "Le nombre de variables du QCN doit être compris entre 3 et 26";
        for(int i = 65;i<65+nbrVertex;i++){
            String variableName = Character.toString((char) i);
            V.put(variableName, new Vertex(variableName));
        }
        for(String x : V.keySet()){
            for(String y : V.keySet()){
                if(x.compareTo(y)<0){
                    addEdge(x,y,ra.getRelations());
                }
            }
        }
        complete();
    }

    /**
     * Constructeur par copie profonde pour les arcs, par copie de surface (copie de pointeurs) pour les sommets
     * @param g le RCQ à copier
     */
    public QualitativeConstraintNetwork(QualitativeConstraintNetwork g) {
        this.complete = g.complete;
        this.counter = g.counter;
        for(Vertex v : g.V.values()){
            V.put(v.getId(),v);
        }
        for(Edge e : g.E){
            Vertex vX = V.get(e.v1Id());
            Vertex vY = V.get(e.v2Id());
            QualitativeConstraintEdge qce = new QualitativeConstraintEdge(vX,vY,e.relations());
            E.add(qce);
        }
    }

    /**
     * Constructeur par lecture de fichier
     * @param filename le nom du fichier
     * @throws IOException si le fichier n'est pas au bon format
     */
    public QualitativeConstraintNetwork(String filename) throws IOException {

        Map<Integer,String> relationMap = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;

        while((line=in.readLine())!=null){
            String[] parts = line.split("\t");
            if(parts.length<2){
                throw new AssertionError("Une arrête est mal définie");
            } else {
                if(!parts[0].matches("^[0-9a-zA-Z]+$")||!parts[1].matches("^[0-9a-zA-Z]+$")){
                    throw new AssertionError("Variables mal nommées (chiffres et lettres sans espace svp");
                } else {
                    Vertex v1;
                    Vertex v2;
                    if(V.containsKey(parts[0])){
                        v1 = V.get(parts[0]);
                    } else {
                        v1 = new Vertex(parts[0]);
                        this.addVertex(v1);
                    }
                    if(V.containsKey(parts[1])){
                        v2 = V.get(parts[1]);
                    } else {
                        v2 = new Vertex(parts[1]);
                        this.addVertex(v2);
                    }
                    if(parts.length>2){
                        Set<String> relationSet = new HashSet<>(Arrays.asList(parts).subList(2, parts.length));
                        QualitativeConstraintEdge qce = new QualitativeConstraintEdge(v1,v2,relationSet);
                        this.addEdge(qce);
                    }
                }
            }
        }
    }

    /**
     * Pour ajouter un sommet, ne pas utiliser en principe
     * @param v un sommet
     */
    public void addVertex(Vertex v){
        super.addVertex(v);
        complete=false;
    }

    /**
     * Ajoute un QualitativeConstraintEdge. Attention, ne gère pas le cas où la variable donnée n'est pas dans le RCQ
     * @param x la variable de départ (l'id du sommet)
     * @param y la variable d'arrivée (l'id du sommet)
     * @param relations les relations qui peuvent lier les deux variables
     */
    public void addEdge(String x, String y, Set<String> relations){
        Vertex v1 = V.get(x);
        Vertex v2 = V.get(y);
        Edge e = new QualitativeConstraintEdge(v1,v2,relations);
        addEdge(e);
    }

    /**
     * Ajoute un QualitativeConstraintEdge
     * @param e l'arrête à ajouter
     */
    public void addEdge(Edge e){
        if(!e.isQualitativeConstraintEdge()) {
            throw new AssertionError("Vous êtes contraint d'ajouter un QualitativeConstraintEdge");
        }
        if(e.v1Id().equals(e.v2Id())){

            throw new AssertionError("Un même sommet ne peut pas être source et destination d'une arrête :"+e.toGraphViz());
        }
        for(Edge edge : E){
            if(edge.v1Id().equals(e.v1Id())&&edge.v2Id().equals(e.v2Id())){
                throw new AssertionError("Il existe déjà une arrête entre ces deux sommets");
            } else if (edge.v1Id().equals(e.v2Id())&&edge.v2Id().equals(e.v1Id())) {
                throw new AssertionError("Il existe déjà une arrête entre ces deux sommets");
            }
        }
        super.addEdge(e);
    }

    /**
     * Renvoie les relations d'un arc
     * @param x la variable de départ
     * @param y la variable d'arrivée
     * @return les relations de (x,y)
     */
    public Set<String> getRelations(String x, String y){
        return getEdge(x,y).relations();
    }

    /**
     * Change les relations d'un arc (x,y)
     * @param x_y l'arc (x,y)
     * @param relations les nouvelles relations
     */
    public void changeRelations(Edge x_y,Set<String> relations){
        changeRelations(x_y.v1Id(),x_y.v2Id(),relations);
    }

    /**
     * Remplace les relations de (x,y) ( et du coup de (y,x) qui prend les relations inverses de (x,y) )
     * @param x x (par identifiant)
     * @param y y (par identifiant)
     * @param relations les nouvelles relations de (x,y)
     */
    public void changeRelations(String x, String y,Set<String> relations){
        Edge e = getEdge(x,y);
        e.setRelationSet(relations);
        Edge inverse = getEdge(y,x);
        inverse.setRelationSet(ra.reverse(relations));
    }

    /**
     * A TESTER
     * Opérateur de relaxation sur (x,y)
     * @param set les relations avec lesquelles relaxer
     * @param x x (par identifiant)
     * @param y y (par identifiant)
     * @return le statut de la propagation effectuée après la relaxation
     */
    public PropagationStatus relax(Set<String> set, String x, String y) {
        complete();

        Edge e = getEdge(x,y);
        Set<String> relations = e.relations();
        Set<String> newRelations = ra.union(relations, set);
        changeRelations(e,newRelations);
        return propagateConstraints();
    }

    /**
     * Opérateur de relaxation sur (x,y)
     * @param set les relations avec lesquelles contracter
     * @param x x (par identifiant)
     * @param y y (par identifiant)
     * @return le statut de la propagation effectuée après la contraction
     */
    public PropagationStatus contract(Set<String> set, String x, String y) {
        complete();

        Edge e = getEdge(x,y);
        Set<String> relations = e.relations();
        Set<String> newRelations = ra.intersection(relations, set);
        changeRelations(e,newRelations);
        PropagationStatus statut = propagateConstraints();
        return statut;
    }

    /**
     * Supprime aléatoirement n relations puis refait une clotûre algébrique.
     * On fait n fois :
     * - Choisir un arc aléatoirement
     * - Supprimer aléatoirement une des relations de cet arc
     * - Clôture algébrique
     * @param n le nombre de relations à supprimer
     * @throws QCNException Si le scénario obtenu est incohérent, on renvoie une exception
     */
    public void deleteRelationRandomly(int n) throws QCNException {
        complete();
        Random tirage = new Random();
        List<Edge> edgeList = new ArrayList<Edge>(E);
        for(int i=0;i<n;i++){
            int edgeNum = tirage.nextInt(edgeList.size());
            Edge e = edgeList.get(edgeNum);
            e.deleteRelationRandomly();
            Edge inverse = getEdge(e.v2Id(),e.v1Id());
            inverse.setRelationSet(ra.reverse(e.relations()));
            if(propagateConstraints()==PropagationStatus.INCONSISTENT){
                throw new QCNException("Incohérent");
            }
        }
    }

    /**
     * Permet de retirer l'arc (x,y)
     * @param x x (identifiant du vecteur)
     * @param y y (identifiant du vecteur)
     */
    public void removeEdge(String x,String y){
        E.removeIf(e -> e.v1Id().equals(x) && e.v2Id().equals(y));
    }

    /**
     * Permet de récuperer l'arc (x,y)
     * @param x x (identifiant du vecteur)
     * @param y y (identifiant du vecteur)
     * @return (x,y) ou null si l'arc n'est pas défini
     */
    public Edge getEdge(String x, String y) {
        for(Edge e : E){
            if(e.v1Id().equals(x)&&e.v2Id().equals(y)){
                return e;
            }
        }
        return null;
    }

    /**
     * Permet de vérifier si le RCQ est bien défini dans l'algèbre relationnelle défini en singleton
     */
    public void checkAlgebra(){
        for(Edge e : E){
            e.checkAlgebra();
        }
    }

    /**
     * Vérifie si le RCQ courant est en fait un scénario
     * @return true si c'est un scénario, false sinon
     */
    public boolean isScenario(){
        if((V.size()*(V.size()-1))/2!=E.size()){
            return false;
        } else {
            for(Edge e : E){
                if(e.relations().size()!=1){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Fait en sorte que les relations soient définies entre chaque couple de variable (x,y)
     * Si (x,y) est défini et pas (y,x), alors (y,x) prend les relations inverses de (x,y)
     */
    public void complete() {
        if(!complete) {
            Collection<Edge> newE = new ArrayList<Edge>();
            for (Edge e : E) {
                newE.add(new QualitativeConstraintEdge(e.getV2(), e.getV1(), ra.reverse(e.relations())));
            }
            E.addAll(newE);

            for (Vertex v1 : V.values()) {
                for (Vertex v2 : V.values()) {
                    if (!v1.equals(v2) && getEdge(v1.getId(), v2.getId()) == null) {
                        QualitativeConstraintEdge edge1 = new QualitativeConstraintEdge(v1, v2);
                        E.add(edge1);
                        QualitativeConstraintEdge edge2 = new QualitativeConstraintEdge(v2, v1);
                        E.add(edge2);
                    }
                }
            }
            complete=true;
        }
    }

    /**
     * Permet d'obtenir la totalité des arcs (x,y) tel que x < y.
     * Attention il s'agit des vrais arcs. Donc il ne faut pas les manipuler
     * @return les arcs tels que x < y.
     */
    public Queue<Edge> getHalfEdges(){
        Queue<Edge> edges = new ArrayDeque<Edge>();
        for(String x : V.keySet()){
            for(String y : V.keySet()){
                if(x.compareTo(y)<0) {
                    edges.add(getEdge(x,y));
                }
            }
        }
        return edges;
    }

    /**
     * Algorithme (approximatif) de propagation de contraintes de Dufour-Lussier
     * Risque de boucle infinie
     * @param C_Locked un dictionnaire a double clé qui contient la liste des relations autorisées
     *                 pour chaque arc (x,y)
     * @return le statut de fin de la propoagation
     */
    public PropagationStatus propagateRepair(MultiKeyHashMap<String,String,Set<String>> C_Locked){
        complete();
        Queue<Edge> edges =getHalfEdges();

        while(!edges.isEmpty()) {

            // ------------------------------------------------------------------------------------------
            // Pour empêcher les boucles infinies dans l'algo de Dufour-Lussier, ne pas prendre en compte
            counter++;
            if(counter>3000){
                throw new AssertionError("Boucle infinie");
            }
            // ------------------------------------------------------------------------------------------

            /* IMPRESSION ALGO APPROXIMATIF
            System.out.println("_______________________________________________");
            /**/

            Edge x_y = edges.peek();
            edges.remove();
            assert x_y != null;

            String x = x_y.v1Id();
            String y = x_y.v2Id();

            for (String z : getV().keySet()) {
                if((!z.equals(x))&&(!z.equals(y))) {
                    /* IMPRESSION ALGO APPROXIMATIF
                    System.out.println("\t...........................................");
                    /**/
                    Edge y_z = getEdge(y, z);
                    Edge x_z = getEdge(x, z);
                    Edge z_y = getEdge(z, y);
                    Edge z_x = getEdge(z, x);

                    /* IMPRESSION ALGO APPROXIMATIF

                    System.out.println("\tCxy : "+x_y.toGraphViz());
                    System.out.println("\tCyz : "+y_z.toGraphViz());
                    System.out.println("\tCxz : "+x_z.toGraphViz());
                    System.out.println("\tCxzL : "+C_Locked.get(x,z));
                    /**/

                    PropagationStatus r1 = fixConstraintRelaxedly(x_y, y_z, x_z,C_Locked.get(x,z));

                    if (r1 == PropagationStatus.CONTRACTED ) {
                        if(!edges.contains(x_z)) {
                            edges.add(x_z);
                        }
                    } else if (r1 == PropagationStatus.RELAXED) {
                        if (!edges.contains(x_y)) {
                            edges.add(x_y);
                        }
                    } else if (r1 == PropagationStatus.RELAX_NEEDED) {
                        if(!edges.contains(z_x)) {
                            edges.add(z_x);
                        }
                        //if(!edges.contains(x_y)) {
                        //    edges.add(y_z);
                        //}
                    } else if (r1 == PropagationStatus.INCONSISTENT) {
                        return PropagationStatus.INCONSISTENT;
                    }

                    /* IMPRESSION ALGO APPROXIMATIF
                    System.out.println("\tCzx : "+z_x.toGraphViz());
                    System.out.println("\tCxy : "+x_y.toGraphViz());
                    System.out.println("\tCzy : "+z_y.toGraphViz());
                    System.out.println("\tCzyL : "+C_Locked.get(z,y));
                    /**/
                    PropagationStatus r2 = fixConstraintRelaxedly(z_x, x_y, z_y,C_Locked.get(z,y));
                    if (r2 == PropagationStatus.CONTRACTED ) {
                        if (!edges.contains(z_y)) {
                            edges.add(z_y);
                        }
                    } else if (r2 == PropagationStatus.RELAXED ){
                        if(!edges.contains(x_y)) {
                            edges.add(x_y);
                        }
                    } else if (r2 == PropagationStatus.RELAX_NEEDED) {
                        if(!edges.contains(z_x)) {
                            edges.add(z_x);
                        }
                        //if(!edges.contains(x_y)) {
                        //    edges.add(x_y);
                        //}
                    } else if (r2 == PropagationStatus.INCONSISTENT) {
                            return PropagationStatus.INCONSISTENT;
                    }
                }
            }
        }
        return PropagationStatus.SUCCESS;
    }

    /**
     * Algorithme (approximatif) de fixation des contraintes avec relaxation de Dufour Lussier.
     * Pour pouvoir mieux comprendre le fonctionne de l'algorithme, je fais des impressions dans le terminal
     * @param x_y Cxy
     * @param y_z Cyz
     * @param x_z Cxz
     * @param x_z_locked CxzL
     * @return le statut de propagation
     */
    public PropagationStatus fixConstraintRelaxedly(Edge x_y,Edge y_z, Edge x_z, Set<String> x_z_locked){
        Set<String> r_x_z = x_z.relations();
        Set<String> r_y_z = y_z.relations();
        Set<String> r_x_y = x_y.relations();

        System.out.println(x_z.toGraphViz());
        if(x_z_locked==null){
            x_z_locked = ra.getRelations();
        }
        // La composition est inversée parce que je ne lis pas le tableau de composition dans le même sens
        // que M. Dufour-Lussier
        Set<String> r_x_z_prime = ra.compose(r_x_y,r_y_z);
        System.out.println("Cxz  : "+r_x_z);
        System.out.println("CxzL : "+x_z_locked);
        System.out.println("Cxz' : "+r_x_z_prime);

        if (r_x_z_prime.containsAll(r_x_z)&&x_z_locked.containsAll(r_x_z)) {
            /* IMPRESSION ALGO APPROXIMATIF
            System.out.println("\t\tAucun changement requis\n");
            /**/
            System.out.println("________________");
            return PropagationStatus.NO_CHANGE_NEEDED;
        }
        if(ra.intersection(r_x_z_prime,x_z_locked).isEmpty()){
            changeRelations(x_y,ra.relax(r_x_y));
            //changeRelations(y_z,ra.relax(r_y_z));
            return PropagationStatus.RELAX_NEEDED;
        }
        Set<String> r_x_z_second = ra.intersection(r_x_z, r_x_z_prime, x_z_locked);
        System.out.println("Cxz'' : "+r_x_z_second);
        if (!r_x_z_second.isEmpty()) {
            //System.out.println(r_x_z);
            //System.out.println(r_x_z_prime);
            //System.out.println(x_z_locked);
            //System.out.print(r_x_z_second);
            changeRelations(x_z,r_x_z_second);
            /* IMPRESSION ALGO APPROXIMATIF
            System.out.println("\t\tContracté : "+x_z.toGraphViz()+"\n");
            /**/
            System.out.println("________________");
            return PropagationStatus.CONTRACTED;
        }

        System.out.println("Relax(Cxz) : "+ra.relax(r_x_z));
        changeRelations(x_z, ra.relax(r_x_z));
        System.out.println("________________");
        return PropagationStatus.RELAXED;

        /*

        if(x_z_locked==null){
            x_z_locked = ra.getRelations();
        }


        Set<String> r_x_z_relaxed = r_x_z;
        do {

            System.out.println(r_x_z_relaxed);
            System.out.println(x_z_locked);
            System.out.println(r_x_z_prime);
            System.out.println("______________________");
            r_x_z_relaxed =ra.relax(r_x_z_relaxed);
            Set<String> r_x_z_temp = ra.intersection(r_x_z_relaxed, x_z_locked, r_x_z_prime);
            if (!r_x_z_temp.isEmpty()) {
                changeRelations(x_z, r_x_z_temp);

                IMPRESSION ALGO APPROXIMATIF
                System.out.println("\t\tRelaxé : " + x_z.toGraphViz() + "\n");

                return PropagationStatus.RELAXED;
            }
        } while(true);
        */

        /*
        IMPRESSION ALGO APPROXIMATIF
        System.out.println("Inconsistant");
        */
        //return PropagationStatus.INCONSISTENT;
    }

    /**
     * Algorithme de propagation de contraintes de Vilain et Kautz
     * @return le statut de la propagation
     */
    public PropagationStatus propagateConstraints(){
        complete();

        Queue<Edge> edges = getHalfEdges();

        while(!edges.isEmpty()) {
            Edge x_y = edges.peek();
            edges.remove();
            String x = x_y.v1Id();
            String y = x_y.v2Id();

            for (String z : getV().keySet()) {
                if((!z.equals(x))&&(!z.equals(y))) {
                    Edge y_z = getEdge(y, z);
                    Edge x_z = getEdge(x, z);
                    Edge z_y = getEdge(z, y);
                    Edge z_x = getEdge(z, x);

                    PropagationStatus r1 = fixConstraint(x_y, y_z, x_z);
                    if (r1 == PropagationStatus.CONTRACTED) {
                        edges.add(x_z);
                    } else if (r1 == PropagationStatus.INCONSISTENT) {
                        return PropagationStatus.INCONSISTENT;
                    }

                    PropagationStatus r2 = fixConstraint(z_x, x_y, z_y);
                    if (r2 == PropagationStatus.CONTRACTED) {
                        edges.add(z_y);
                    } else if (r2 == PropagationStatus.INCONSISTENT) {
                        return PropagationStatus.INCONSISTENT;
                    }
                }
            }
        }
        return PropagationStatus.SUCCESS;
    }

    /**
     * Algorithme de Vilain et Kautz (1986) pour fixer une contrainte
     * @param x_y Cxy
     * @param y_z Cyz
     * @param x_z Cxz
     * @return le statut de la propagation de la contrainte
     */
    public PropagationStatus fixConstraint(Edge x_y,Edge y_z, Edge x_z) {
        Set<String> r_x_z = x_z.relations();
        Set<String> r_y_z = y_z.relations();
        Set<String> r_x_y = x_y.relations();

        Set<String> r_x_z_prime = ra.compose(r_x_y,r_y_z);
        if (r_x_z_prime.containsAll(r_x_z)) {
            return PropagationStatus.NO_CHANGE_NEEDED;
        } else {

            Set<String> r_x_z_second = ra.intersection(r_x_z, r_x_z_prime);
            if (r_x_z_second.isEmpty()) {
                return PropagationStatus.INCONSISTENT;
            } else {
                x_z.setRelationSet(r_x_z_second);
                Edge eZX = getEdge(x_z.v2Id(),x_z.v1Id());
                eZX.setRelationSet(ra.reverse(r_x_z_second));
                return PropagationStatus.CONTRACTED;
            }
        }
    }


    /**
     * Génère les scénarios
     * @return la liste des scénarios inclu dans le RCQ
     */
    public Set<QualitativeConstraintNetwork> toScenarios(){
        return new HashSet<>(toScenarios(0));
    }

    /**
     * Fonction auxiliaire de toScenarios(), qui permet de générer tous les scénarios par exploration DFS
     * @param i le numéro de l'arc à contraindre pour générer le scénarios (ordre arbitraire)
     * @return les scénarios une fois l'arc numéro i contraint à une relation
     */
    private Set<QualitativeConstraintNetwork> toScenarios(int i){
        Set<QualitativeConstraintNetwork> listScenarios = new HashSet<>();
        if(i<E.size()){
            List<Edge> edgeList = new ArrayList<Edge>(E);
            Edge e = edgeList.get(i);
            Set<String> relations = e.relations();
            for(String s : relations){
                QualitativeConstraintNetwork succ = new QualitativeConstraintNetwork(this);
                if(relations.size()>1) {
                    PropagationStatus status = succ.contract(new HashSet<String>(Collections.singleton(s)), e.v1Id(), e.v2Id());
                    if (status != PropagationStatus.INCONSISTENT) {
                         listScenarios.addAll(succ.toScenarios(i+1));
                    }
                } else {
                    listScenarios.addAll(succ.toScenarios(i+1));
                }
            }
        } else {
            listScenarios.add(this);
        }
        return listScenarios;
    }

    /**
     * Donne le minimum des distances obtenues avec l'heuristique pour chaque qcn de l'ensemble passé en paramètres
     * @param qcnSet l'ensemble des RCQ à tester
     * @return le minimum des distances
     */
    public int heuristic(Set<QualitativeConstraintNetwork> qcnSet){
        int min = Integer.MAX_VALUE;
        for (QualitativeConstraintNetwork qcn : qcnSet){
            min = Math.min(min,heuristic(qcn));
        }
        return min;
    }

    /**
     * Donne l'heuristique de la distance
     * Il est à noter que si les deux RCQ sont des scénarios, l'heuristique est égal à la vraie distance
     * @param qcn le RCQ dont on doit connaitre l'heuristique (par rapport au RCQ courant)
     * @return l'heuristique
     */
    public int heuristic(QualitativeConstraintNetwork qcn){
        int distance = 0;
        for(Edge e1 : E){
            Edge e2 = qcn.getEdge(e1.v1Id(),e1.v2Id());
            int distance_to_add = ra.distance(e1.relations(),e2.relations());
            distance = distance + distance_to_add;
        }

        return distance;
    }

    /**
     * Calcule la distance avec un ensemble de RCQ, c'est à dire la distance minimale avec au moins un RCQ
     * @param qcnSet l'ensemble des RCQ à tester
     * @return la distance avec un ensemble de RCQ.
     */
    public int distance(Set<QualitativeConstraintNetwork> qcnSet){
        Set<QualitativeConstraintNetwork> scenarios = this.toScenarios();
        Set<QualitativeConstraintNetwork> scenariosParam = new HashSet<>();
        for(QualitativeConstraintNetwork qcn : qcnSet){
            scenariosParam.addAll(qcn.toScenarios());
        }
        int min = Integer.MAX_VALUE;
        for(QualitativeConstraintNetwork scenario : scenarios){
            min = Math.min(min,scenario.heuristic(scenariosParam));
        }
        return min;
    }

    /**
     * Vérifie si le RCQ donné en paramètre est inclu dans l'objet courant, c'est à dire si tous les scénarios
     * consistants que l'on peut générer à partir du RCQ sont inclus dans ceux du RCQ courant.
     * @param qcn le RCQ testé
     * @return true s'il y a inclusion, false sinon
     */
    public boolean includes(QualitativeConstraintNetwork qcn){
        Set<QualitativeConstraintNetwork> englobe = new HashSet<>();
        englobe.add(qcn);
        return includes(englobe);
    }

    /**
     * Vérifie si l'ensemble de RCQ donné en paramètre est inclu dans l'objet courant.
     * Autrement dit, on vérifie que si on génère les scénarios associés aux RCQ passés en paramètres, ils
     * se trouvent tous dans les scénarios qu'on peut générer avec le RCQ courant
     * @param qcnSet l'ensemble de RCQ testés
     * @return true s'il y a inclusion, false sinon
     */
    public boolean includes(Set<QualitativeConstraintNetwork> qcnSet){
        Set<QualitativeConstraintNetwork> scenariosThis = toScenarios();
        Set<QualitativeConstraintNetwork> scenariosOther = new HashSet<>();
        for(QualitativeConstraintNetwork qcn : qcnSet){
            scenariosOther.addAll(qcn.toScenarios());
        }
        for(QualitativeConstraintNetwork scenarioOther : scenariosOther){
            boolean include = false;
            for(QualitativeConstraintNetwork scenarioThis : scenariosThis){
                include = scenarioThis.equals(scenarioOther);
                if(include){
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
     * Permet de vérifier l'égalité avec un autre QualitativeConstraintNetwork
     * @param qcn le RCQ a tester
     * @return true si les RCQ sont identiques, false sinon
     */
    public boolean equals(QualitativeConstraintNetwork qcn){
        for(String x : V.keySet()){
            if(!qcn.V.containsKey(x)){
                return false;
            }
        }
        for(String x : qcn.V.keySet()){
            if(!V.containsKey(x)){
                return false;
            }
        }
        for(String x : V.keySet()){
            for(String y : V.keySet()){
                if(!x.equals(y)) {
                    if(!getEdge(x,y).equals(qcn.getEdge(x,y))){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
