import TUIO.TuioObject;

public class Connection {
    int aID;
    int bID;
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
        int x1 = Rapporto.WIDTH - a.getScreenX(Rapporto.WIDTH)+(int)((Rapporto.cos(t1)*Rapporto.OBJECTSIZE));
        int y1 = a.getScreenY(Rapporto.HEIGHT)+(int)((Rapporto.sin(t1)*Rapporto.OBJECTSIZE));
        int x2 = Rapporto.WIDTH - b.getScreenX(Rapporto.WIDTH)+(int)((Rapporto.cos(t2)*Rapporto.OBJECTSIZE));
        int y2 = b.getScreenY(Rapporto.HEIGHT)+(int)((Rapporto.sin(t2)*Rapporto.OBJECTSIZE));

        float distance = Rapporto.sqrt(Rapporto.pow(x2-x1, 2) + Rapporto.pow(y2-y1, 2));
        float opacity = 255*(40/distance);
        ctx.canvas.fill(20, opacity);
        ctx.canvas.noStroke();
        ctx.canvas.ellipse((x2+x1)/2, (y1+y2)/2, Rapporto.OBJECTSIZE, Rapporto.OBJECTSIZE);

        if (distance < 150) {
            ctx.canvas.fill(255, opacity*1.5f);
            ctx.canvas.text(getContent(), (x2+x1)/2, (y1+y2)/2);
        }




//        ctx.canvas.noFill();
//        ctx.canvas.stroke(255, 255-opacity);
//
//        float curvatureDitherA = Rapporto.CURVATURE+100*Rapporto.cos((float)(ctx.millis()/1000.0));
//        float curvatureDitherB = Rapporto.CURVATURE+100*Rapporto.sin((float)(ctx.millis()/1000.0));
//
//        int c1 = x1-(int)(curvatureDitherA*Rapporto.cos(t1));
//        int c2 = y1-(int)(curvatureDitherB*Rapporto.sin(t1));
//        int c3 = x2-(int)(curvatureDitherA*Rapporto.cos(t2));
//        int c4 = y2-(int)(curvatureDitherB*Rapporto.sin(t2));
//
//        ctx.canvas.beginShape();
//        // Control point for beginning
//        ctx.canvas.curveVertex(c1, c2);
//        // actual beginning of curve
//        ctx.canvas.curveVertex(x1, y1);
//        // control point for ending
//        ctx.canvas.curveVertex(x2, y2);
//        // actual ending
//        ctx.canvas.curveVertex(c3, c4);
//        ctx.canvas.endShape();

//        ctx.canvas.text(getContent(), c1-c3, c2-c4);
//        ctx.canvas.text(getContent(), (c1+c3)/2, (c2+c4)/2);
    }

    public int[] getActiveYears() {
        return yearsActive;
    }

    public Rapporto.CATEGORY getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }
}