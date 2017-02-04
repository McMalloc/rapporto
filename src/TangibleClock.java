import processing.core.PApplet;

public class TangibleClock extends Tangible{

    private int[] years = {55,56,57,58,59,60,61,62,63,64,65,66,67};
    private float arcLength = Rapporto.TWO_PI / getNumberOfArcs();

    public TangibleClock(int fid) {
        super(fid, null);
        setNumberOfArcs(years.length);
    }

    public int getCurrentYear() {
        return years[getSelectedArc()-1];
    }

    public void draw(Rapporto ctx, int sX, int sY, float angle) {
        ctx.canvas.beginDraw();
        ctx.canvas.fill(0);
        ctx.canvas.rect(sX-10, sY-10, 200, 100);
        ctx.canvas.fill(255);
        ctx.canvas.text("'"+years[getSelectedArc()-1], sX, sY);
        ctx.canvas.endDraw();
    }
}
