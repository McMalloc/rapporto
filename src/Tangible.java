import processing.core.PGraphics;
import java.util.ArrayList;

public class Tangible {
    private int fiducialID;
    private Rapporto.CATEGORY cat;
    private boolean visible = false;
    private int nP = 0; // number of arcs
    private int selectedArc = 1;
    private ArrayList relevantPersons;

    public Tangible (int fid, Rapporto.CATEGORY category) {
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
        int selectedArc = getSelectedArc();
        if (relevantPersons.size() == 0) return -1;
        if (selectedArc < 1 || selectedArc > relevantPersons.size()) return -1;
        return (int) relevantPersons.get(selectedArc-1);
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

    public void draw(Rapporto ctx, int sX, int sY, float angle) {
        relevantPersons = Rapporto.getRelevantPersons(cat);
        setNumberOfArcs(relevantPersons.size()); //TODO put in update methods
        float arcLength = Rapporto.TWO_PI / getNumberOfArcs();
        int objSize = Rapporto.OBJECTSIZE;

        ctx.canvas.beginDraw();

        ctx.canvas.noStroke();
        ctx.canvas.textAlign(Rapporto.CENTER);
        ctx.canvas.textSize(18);
        for (int i = 0; i < relevantPersons.size(); i++) {
            Person current = Rapporto.persons.get(relevantPersons.get(i));
            ctx.canvas.pushMatrix();
            ctx.canvas.translate(sX, sY);
            ctx.canvas.rotate(i*arcLength);
//            ctx.canvas.rotate(arcLength);
            if (i+1 == selectedArc) {
                ctx.canvas.fill(60,200,70);
                for (int k = 0; k < Rapporto.connections.size(); k++) {
                    Connection currentC = Rapporto.connections.get(k);
                    if (currentC.hasConnection(0, current.getID())) {
                        ctx.canvas.text(currentC.getContent(), 0, 200, 200, 400);
                    }
                }
            } else {
                ctx.canvas.fill(120,30,70);
            }
            ctx.canvas.arc(0, 0, objSize, objSize, 0, arcLength, Rapporto.PIE);
            ctx.canvas.rotate((float)(arcLength*-0.5));
            ctx.canvas.fill(255);
            ctx.canvas.text(current.getID()+": "+current.getName().replace(" ", "\n"), 0, 0+100);
            if(current.hasImage()) {
                ctx.canvas.image(current.getPortrait(), -100, -100, 150, 150);
            }
            ctx.canvas.popMatrix();
        }

        ctx.canvas.pushMatrix();
        ctx.canvas.translate(sX,sY);
        ctx.canvas.text("arc " + selectedArc + " points to " + getSelectedPerson(), 0, objSize/2+(20));
//        ctx.canvas.tint(255,170);
        ctx.canvas.rotate(angle);
        ctx.canvas.line(0, 0, 300, 0);
        ctx.canvas.popMatrix();
        ctx.canvas.endDraw();
    }
}
