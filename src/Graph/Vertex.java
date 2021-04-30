package Graph;
import java.util.regex.*;

/**
 * @author mihneadb
 */

public class Vertex {

    private String id;
    private String label;

    public Vertex(String id) {
        setId(id);
        setLabel(id);
    }

    public Vertex(String id,String label) {
        setId(id);
        setLabel(label);
    }

    public Vertex(Vertex v){
        this.id = v.getId();
        this.label = v.getLabel();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        checkId(id);
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    private void checkId(String id){
        Pattern p = Pattern.compile("^[0-9a-zA-Z]+$");
        Matcher matcher = p.matcher(id);
        if ((!matcher.matches())) throw new AssertionError("L'id ne doit contenir que des chiffres et des lettres");
    }

    public boolean equals(Vertex v) {
        return v.getId().equals(this.getId());
    }

    public String toGraphViz() {
        return id+" [label=\""+label+"\"]";
    }
}