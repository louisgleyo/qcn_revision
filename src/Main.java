import Algebra.RelationnalAlgebra;
import Beliefs.QualitativeConstraintNetwork;
import Empirical.Analyser;
import Empirical.Generator;
import Empirical.Revision;
import Strategies.*;

import java.io.IOException;
import java.util.*;

public class Main {


    public static void testComposition(){
        RelationnalAlgebra ra = RelationnalAlgebra.getInstance();
        Set<String> relations1 = new HashSet<>();
        relations1.add("TPP");
        Set<String> relations2 = new HashSet<>();
        relations2.add("DC");
        System.out.println(ra.compose(relations2,relations1));
        System.out.println(ra.compose(relations1,relations2));
    }

    public static void testGenerate(){
        RelationnalAlgebra ra = RelationnalAlgebra.getInstance();

        Generator g = new Generator();
        ExactStrategy bbStrategy = new BranchAndBoundStrategy();
        RevisionStrategy revisionStrategy = new RelaxStrategy();
        g.generate(10,4,25);
        Map<Conjecture,Analyser> counterExamples = g.checkConjectures(bbStrategy,revisionStrategy,
                Conjecture.AGM_CORRECTION,Conjecture.COMPLETNESS,Conjecture.CORRECTION);
        for(Conjecture conjecture : counterExamples.keySet()){
            System.out.println(conjecture);
            System.out.println("_______________________________________");
            System.out.println(counterExamples.get(conjecture));
        }

    }

    public static void testExactAlgo(){
        Generator g  = new Generator();
        List<QualitativeConstraintNetwork> qcnList = new ArrayList<>(g.generate(14,4,25));
        ExactStrategy exactStrategy = new BranchAndBoundStrategy();
        System.out.println(qcnList.get(0).toGraphViz());
        System.out.println(qcnList.get(1).toGraphViz());

        Revision result  = exactStrategy.revise(qcnList.get(0),qcnList.get(1));
        System.out.println(result.distance());
        for(QualitativeConstraintNetwork qcn : result.result()){
            System.out.println(qcn.toGraphViz());
        }
    }

    public static void testRelaxAlgo(){
        Generator g  = new Generator();
        List<QualitativeConstraintNetwork> qcnList = new ArrayList<>(g.generate(2,3,15));

        RelaxStrategy strategy = new RelaxStrategy();
        strategy.revise(qcnList.get(0),qcnList.get(1));

    }

    public static void main(String[] args) throws IOException {

        RelationnalAlgebra ra = RelationnalAlgebra.getInstance();
        ra.initialize("rcc8");

        testGenerate();
        //testComposition();
        //testRelaxAlgo();
        //testExactAlgo();
    }
}
