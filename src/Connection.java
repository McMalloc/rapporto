import TUIO.TuioObject;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Connection {
    private int aID;
    private int bID;
    private Rapporto.CATEGORY category;
    private String content;
    private int id;
    private int[] yearsActive;

    public Connection(int id, int a, int b, Rapporto.CATEGORY _category, int[] yearsActive, String content) {
        this.content = content;
        this.id = id;
        aID = a;
        bID = b;
        this.yearsActive = yearsActive;
        category = _category;
    }

    public boolean hasConnection(int idX, int idY) {
        boolean matchingYear = false;
        for (int i = 0; i < yearsActive.length; i++) {
            matchingYear = Rapporto.currentTime == yearsActive[i];
            if (matchingYear) break;
        }
        return ((idX == aID && idY == bID)) && matchingYear;
    }

    public void draw(Rapporto ctx, TuioObject a, TuioObject b) {
        float t1 = a.getAngle();
        float t2 = b.getAngle();
        int x1 = a.getScreenX(Rapporto.WIDTH)+(int)((Rapporto.cos(t1)*Rapporto.OBJECTSIZE/2));
        int y1 = a.getScreenY(Rapporto.HEIGHT)+(int)((Rapporto.sin(t1)*Rapporto.OBJECTSIZE/2));
        int x2 = b.getScreenX(Rapporto.WIDTH)+(int)((Rapporto.cos(t1)*Rapporto.OBJECTSIZE/2));
        int y2 = b.getScreenY(Rapporto.HEIGHT)+(int)((Rapporto.sin(t1)*Rapporto.OBJECTSIZE/2));
        float curvatureDitherA = Rapporto.CURVATURE+100*Rapporto.cos((float)(ctx.millis()/1000.0));
        float curvatureDitherB = Rapporto.CURVATURE+100*Rapporto.sin((float)(ctx.millis()/1000.0));

        ctx.canvas.stroke(200,0,255);
        ctx.canvas.beginShape();
        // Control point for beginning
        ctx.canvas.curveVertex(
                x1-curvatureDitherA*Rapporto.cos(t1),
                y1-curvatureDitherB*Rapporto.sin(t1)
        );
        // actual beginning of curve
        ctx.canvas.curveVertex(x1, y1);
        // control point for ending
        ctx.canvas.curveVertex(x2, y2);
        // actual ending
        ctx.canvas.curveVertex(
                x2-curvatureDitherA*Rapporto.cos(t2),
                y2-curvatureDitherB*Rapporto.sin(t2)
        );

        ctx.canvas.endShape();
    }

    public String getContent() {
        return content;
    }
}