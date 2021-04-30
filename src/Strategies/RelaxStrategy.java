package Strategies;
import Beliefs.QualitativeConstraintNetwork;
import Empirical.Revision;
import Graph.Edge;
import java.util.*;
import Tools.*;

public class RelaxStrategy extends RevisionStrategy {

    private MultiKeyHashMap<String,String,Set<String>> lockedConstraints;
    private QualitativeConstraintNetwork epsilon;

    @Override
    public Revision revise(QualitativeConstraintNetwork psi, QualitativeConstraintNetwork mu) {
        lockedConstraints = new MultiKeyHashMap<>();
        long toc = System.currentTimeMillis();

        /* IMPRESSION ALGO APPROXIMATIF
        System.out.println("\n################################################");

        System.out.println("Mu :\n");

        for(Edge e : mu.getE()){
            System.out.println(e.toGraphViz());
        }
        System.out.println("\n################################################");
        /**/

        epsilon  = new QualitativeConstraintNetwork(psi);
        Collection<Edge> edges =mu.getHalfEdges();
        for(Edge e : edges) {

            /* IMPRESSION ALGO APPROXIMATIF
            System.out.println("Epsilon :\n");
            for(Edge e2 : epsilon.getE()){
                if(e.v1Id().equals(e2.v1Id())&&e.v2Id().equals(e2.v2Id())){
                    System.out.println(e2.toGraphViz()+" : va être modifié");
                } else {
                    System.out.println(e2.toGraphViz());
                }
            }
            /**/

            String x = e.v1Id();
            String y = e.v2Id();


            Set<String> c0 = epsilon.getRelations(x, y);
            Set<String> cMuXY = mu.getRelations(x, y);
            Set<String> c1 = ra.intersection(c0, cMuXY);
            while (c1.isEmpty()) {
                c0 = ra.relax(c0);
                c1 = ra.intersection(c0, cMuXY);
            }


            epsilon.changeRelations(x, y, c1);

            /* IMPRESSION ALGO APPROXIMATIF

            System.out.println("Après relâchement et intersection avec c1 :\n"+epsilon.getEdge(x,y).toGraphViz());

             /**/

            lockedConstraints.put(x, y, cMuXY);
            lockedConstraints.put(y,x,ra.reverse(cMuXY));

            epsilon.propagateRepair(lockedConstraints);

            /* IMPRESSION ALGO APPROXIMATIF
            System.out.println("\n***********************************************");
            /**/

        }

        Set<QualitativeConstraintNetwork> qcnResult = new HashSet<>();
        qcnResult.add(epsilon);

        long tic = System.currentTimeMillis();

        return new Revision(tic-toc,psi.distance(epsilon.toScenarios()),qcnResult);
    }


    @Override
    public String getName(){
        return "RelaxStrategy";
    }

}
