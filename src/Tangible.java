import java.util.ArrayList;

public class Tangible {
    private int fiducialID;
    private Rapporto.CATEGORY cat;
    private boolean visible = false;
    private int nP = 0; // number of arcs
    private int selectedArc = 1;
    private int prevSelectedArc = 1;
    private ArrayList relevantPersons;

    private float easing = 0.12f;
    private int targetSizeBig = Rapporto.OBJECTSIZE-6;
    private int targetSizeSmall = (int)(targetSizeBig*0.41f);

    private float sBig = 0;
    private float sSmall = 0;

    public Tangible (int fid, Rapporto.CATEGORY category) {
        fiducialID = fid;
        cat = category;
    }

    public void resetSizes() {
        sBig = Rapporto.OBJECTSIZE/2;
        sSmall = Rapporto.OBJECTSIZE;
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

    public int getPrevSelectedArc() {
        return prevSelectedArc;
    }

    public int getMirroredX(int x) {
        return Rapporto.WIDTH - x;
    }

    public void computeSelectedArc(float angle) {
        float segmentlength = Rapporto.TWO_PI / getNumberOfArcs();
        int segment = -1;
        for (int i = 1; i <= getNumberOfArcs(); i++) {
            if (angle <= segmentlength*(float) i && angle > segmentlength*((float)i-1)) {
                if (i != selectedArc) {
                    prevSelectedArc = selectedArc;
                    resetSizes();
                }
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

    public void draw(Rapporto ctx, int _sX, int sY, float angle) {
        int sX = getMirroredX(_sX);


        relevantPersons = Rapporto.getRelevantPersons(cat);
        setNumberOfArcs(relevantPersons.size()); //TODO put in update methods
        float arcLength = Rapporto.TWO_PI / getNumberOfArcs();
//        int objSize = Rapporto.OBJECTSIZE;

        float dSizeB = targetSizeBig - sBig;
        sBig += dSizeB * easing;

        float dSizeS = targetSizeSmall - sSmall;
        sSmall += dSizeS * easing;

        ctx.canvas.beginDraw();

        ctx.canvas.textAlign(Rapporto.CENTER, Rapporto.CENTER);

        ctx.canvas.pushMatrix();
        ctx.canvas.translate(sX, sY);
        ctx.canvas.text(angle, 0,0);
        ctx.canvas.imageMode(Rapporto.CENTER);
        ctx.canvas.textAlign(Rapporto.CENTER, Rapporto.CENTER);
        ctx.canvas.stroke(255);

        for (int i = 0; i < relevantPersons.size(); i++) {
            Person current = Rapporto.persons.get(relevantPersons.get(i));
            ctx.canvas.pushMatrix();
            ctx.canvas.translate(
                    targetSizeBig*Rapporto.cos(arcLength*(i+1)-arcLength/2),
                    targetSizeBig*Rapporto.sin(arcLength*(i+1)-arcLength/2)
            );

            // draw selected person
            if (i+1 == selectedArc) {
                ctx.canvas.fill(ctx.getCategoryColor(cat));
                ctx.canvas.strokeWeight(6);
                ctx.canvas.ellipse(0, 0, sBig, sBig);

                ctx.canvas.fill(255);
                ctx.canvas.textFont(ctx.dinMedb);
                ctx.canvas.text(current.getName().replaceFirst(" ", "\n"), 0, -sBig*0.33f);
                for (int k = 0; k < Rapporto.connections.size(); k++) {
                    Connection currentC = Rapporto.connections.get(k);
                    if (currentC.hasConnection(0, current.getID())) {
                        ctx.canvas.textFont(ctx.dinMeds);
                        ctx.canvas.pushMatrix();
                        ctx.canvas.scale(sBig/targetSizeBig);
                        ctx.canvas.text(currentC.getContent(), -targetSizeBig/2, -targetSizeBig/2, targetSizeBig, targetSizeBig);
                        ctx.canvas.popMatrix();
                        break;
                    }
                }
                if(current.hasImage()) {
                    ctx.canvas.ellipse(-sBig/2, -sBig/3, sSmall, sSmall);
                    ctx.canvas.image(current.getPortrait(),
                            -sBig/2,
                            -sBig/3,
                            sSmall,
                            sSmall);
                }
            // draw other persons
            } else {
                ctx.canvas.strokeWeight(2);
                ctx.canvas.fill(ctx.getCategoryColor(cat));
                if (prevSelectedArc == i+1) {
                    ctx.canvas.ellipse(0, 0, sSmall, sSmall);
                } else {
                    ctx.canvas.ellipse(0, 0, targetSizeBig/2, targetSizeBig/2);
                }
                ctx.canvas.textFont(ctx.dinReg);
                ctx.canvas.fill(255);
                ctx.canvas.text(current.getName().replace(" ", "\n"), 0, 0);

//                if(current.hasImage()) {
//                    ctx.canvas.image(current.getPortrait(),
//                            0,
//                            -targetSizeBig/2,
//                            targetSizeBig/4,
//                            targetSizeBig/4);
//                }
            }

            ctx.canvas.fill(255);
            ctx.canvas.popMatrix();
        }
//        ctx.canvas.tint(255,170);
        ctx.canvas.rotate(angle);
        ctx.canvas.image(ctx.cursor, 0, 0, targetSizeBig, targetSizeBig);
        ctx.canvas.popMatrix();
        ctx.canvas.endDraw();
    }
}
