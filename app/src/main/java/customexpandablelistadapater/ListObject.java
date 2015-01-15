package customexpandablelistadapater;

import java.util.ArrayList;
import java.util.List;

public class ListObject {
    public String title; // use getters and setters instead
    public List<ListObject> children; // same as above

    public ListObject() {
        children = new ArrayList<ListObject>();
    }
}