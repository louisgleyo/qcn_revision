package Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * @author mihneadb
 */

public class Graph {

    public Map<String,Vertex> V = new HashMap<>();
    public Collection<Edge> E = new ArrayList<Edge>();

    public Graph(Graph g){
        for(Vertex v : g.V.values()){
            V.put(v.getId(),new Vertex(v));
        }
        for(Edge e : g.E){
            E.add(new Edge(e));
        }
    }

    public Graph() {

    }

    public void addVertex(Vertex v) {
        if(!V.containsKey(v.getId())) {
            V.put(v.getId(), v);
        } else throw new AssertionError("Ce sommet existe déjà");
    }

    public void addEdge(Edge e) {
        if(V.containsKey(e.v1Id())&&V.containsKey(e.v2Id())) {
            E.add(e);
        } else throw new AssertionError("L'edge ne fait pas référence à des vertex");
    }

    public Edge getEdge(String x,String y){
        for(Edge e : E){
            if(e.v1Id().equals(x)&&e.v2Id().equals(y)){
                return e;
            }
        }
        return null;
    }

    public void removeVertex(Vertex v) {
        V.remove(v.getId());
    }

    public void removeEdge(Edge e) {
        E.remove(e);
    }

    public ArrayList<Vertex> getAdjacentVertices(Vertex v) {
        ArrayList<Vertex> adj = new ArrayList<Vertex>();
        for (Edge e: E) {
            if (e.getV1() == v) {
                adj.add(e.getV2());
                continue;
            }
            if (e.getV2() == v) {
                adj.add(e.getV1());
            }
        }
        return adj;
    }

    public ArrayList<Edge> getAdjacentEdges(Vertex v) {
        ArrayList<Edge> adj = new ArrayList<Edge>();
        for (Edge e: E) {
            if (e.getV1().equals(v) || e.getV2().equals(v)) {
                adj.add(e);
            }
        }
        Edge[] a = null;
        return adj;
    }

    public String toGraphViz() {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n" +
                "digraph world {\n" +
                "\n" +
                "\tgraph [rankdir=LR,pad=\"0.5\",nodesep=\"1\", ranksep=\"1\"]\n" +
                "\n" +
                "\tnode [shape=circle];\n");
        for (Vertex vertex: V.values()){
            sb.append("\t").append(vertex.toGraphViz()).append(";\n");
        }
        for (Edge edge : E){
            sb.append("\t").append(edge.toGraphViz()).append(";\n");
        }
        sb.append("\n}\n" +
                "@enduml");

        return sb.toString();
    }

    public Map<String,Vertex> getV(){
        return V;
    }

    public Collection<Edge> getE(){
        return E;
    }

}