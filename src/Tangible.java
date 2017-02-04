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

        PGraphics mask, annulus;

        mask = ctx.createGraphics(objSize, objSize, Rapporto.P2D);
        annulus = ctx.createGraphics(objSize, objSize);

        mask.beginDraw();
        mask.noStroke();
        mask.fill(0, 0);
        mask.rect(0, 0, objSize, objSize);
        mask.fill(255, 0);
        mask.ellipse(objSize/2, objSize/2, 150, 150);
        mask.endDraw();

        annulus.beginDraw();
        annulus.noStroke();
        annulus.textAlign(Rapporto.CENTER);
        annulus.textSize(18);
        for (int i = 0; i < relevantPersons.size(); i++) {
            Person current = Rapporto.persons.get(relevantPersons.get(i));
            annulus.pushMatrix();
            annulus.translate(objSize/2, objSize/2);
            annulus.rotate(i*arcLength);
//            ctx.canvas.rotate(arcLength);
            if (i+1 == selectedArc) {
                annulus.fill(60,200,70);
            } else {
                annulus.fill(120,30,70);

            }
            annulus.arc(0, 0, objSize, objSize, 0, arcLength, Rapporto.PIE);
            annulus.rotate((float)(arcLength*-0.5));
            annulus.fill(0);
            annulus.text(current.getID()+": "+current.getName().replace(" ", "\n"), 0, 100);
            annulus.popMatrix();
        }
        annulus.endDraw();
//        annulus.mask(mask);

        // draw angle indicator
        ctx.canvas.beginDraw();
        ctx.canvas.pushMatrix();
        ctx.canvas.translate(sX,sY);
        ctx.canvas.text("arc " + selectedArc + " points to " + getSelectedPerson(), 0, objSize/2+(20));
//        ctx.canvas.tint(255,170);
        ctx.canvas.image(annulus, -objSize/2, -objSize/2);
        ctx.canvas.rotate(angle);
        ctx.canvas.line(0, 0, 30, 0);
        ctx.canvas.popMatrix();
        ctx.canvas.endDraw();
    }
}
