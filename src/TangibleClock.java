import processing.core.PApplet;

public class TangibleClock extends Tangible{

    private int[] years = {55,56,57,58,59,60,61,62,63,64,65,66,67};
    private float arcLength = Rapporto.TWO_PI / getNumberOfArcs();

    public TangibleClock(PApplet context, int fid) {
        super(context, fid, null);
        setNumberOfArcs(years.length);
    }

    public int getCurrentYear() {
        return years[getSelectedArc()-1];
    }

    public void draw(int sX, int sY, float angle) {
        Rapporto.canvas.beginDraw();
        Rapporto.canvas.fill(0);
        Rapporto.canvas.rect(sX-10, sY-10, 200, 100);
        Rapporto.canvas.fill(255);
        Rapporto.canvas.text("'"+years[getSelectedArc()-1], sX, sY);
        Rapporto.canvas.endDraw();
    }
}
