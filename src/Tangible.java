import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Map;

public class Tangible {
    private int fiducialID;
    private Rapporto.CATEGORY cat;
    private boolean visible = false;
    private PApplet ctx;
    private int nP = 0; // number of arcs
    private int selectedArc = 1;
    private ArrayList relevantPersons;

    public Tangible (PApplet context, int fid, Rapporto.CATEGORY category) {
        ctx = context;
        fiducialID = fid;
        cat = category;
    }

    public void setVisible() {
        visible = true;
    }
    public void setInvisible() {
        visible = false;
    }
    public boolean isVisible() {
        return visible;
    }
    public Rapporto.CATEGORY getCategory() {
        return cat;
    }

    public int getSelectedPerson() {
//        return 0;
        return (int) relevantPersons.get(getSelectedArc()-1);
    }

    public int getSelectedArc() {
        return selectedArc;
    }

    public void computeSelectedArc(float angle) {
        float segmentlength = Rapporto.TWO_PI / getNumberOfArcs();
        int segment = -1;
        for (int i = 1; i <= getNumberOfArcs(); i++) {
            if (angle <= segmentlength*(float) i && angle > segmentlength*((float)i-1)) {
                segment = i;
                break;
            }
        }
        selectedArc = segment;
    }

    public int getNumberOfArcs() {
        return nP;
    }
    public void setNumberOfArcs(int n) {
        nP = n;
    }

    public void draw(int sX, int sY, float angle) {
        relevantPersons = Rapporto.getRelevantPersons(cat);
        setNumberOfArcs(relevantPersons.size()); //TODO put in update methods
        float arcLength = Rapporto.TWO_PI / getNumberOfArcs();

        Rapporto.canvas.beginDraw();
        Rapporto.canvas.stroke(255);
        Rapporto.canvas.stroke(0);

        for (int i = 0; i < relevantPersons.size(); i++) {
            Rapporto.canvas.text("["+i+"]arc " + selectedArc + " points to " + getSelectedPerson(), sX, sY-45);
            Person current = Rapporto.persons.get(relevantPersons.get(i));
            if (i+1 == selectedArc) {
                Rapporto.canvas.fill(60,200,70);
                Rapporto.canvas.text(current.getID()+": "+current.getName(), sX, sY+(20*i));
            } else {
                Rapporto.canvas.fill(120,30,70);
                Rapporto.canvas.text(current.getID()+": "+current.getName(), sX, sY+(20*i));
            }
//        Rapporto.canvas.text(Rapporto.persons.get(relevantPersons.get(i)).getName(), sX, sY+(20*i));
//        Rapporto.canvas.arc(sX, sY, 100, 100, arcLength*i, arcLength*(i+1), Rapporto.PIE);
        }

        Rapporto.canvas.fill(0,0,0);
        Rapporto.canvas.pushMatrix();
        Rapporto.canvas.translate(sX-30,sY);
        Rapporto.canvas.rotate(angle);
        Rapporto.canvas.line(0, 0, -30, 40);
        Rapporto.canvas.popMatrix();
        Rapporto.canvas.ellipse(sX-30, sY, 20, 20);
        Rapporto.canvas.fill(255, 40, 255);
        Rapporto.canvas.text(cat+": "+fiducialID, sX, sY-20);
        Rapporto.canvas.endDraw();



    }
}
