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

    public void draw(Rapporto ctx, int _sX, int sY, float angle) {
        int selectedArc = getSelectedArc();
        int sX = getMirroredX(_sX);
        int objSize = Rapporto.OBJECTSIZE;

        ctx.canvas.beginDraw();
        ctx.canvas.fill(255);
        ctx.canvas.pushMatrix();
        ctx.canvas.translate(sX, sY);
        ctx.canvas.textAlign(Rapporto.CENTER);
        ctx.canvas.text("17"+years[selectedArc-1], 0, -100);

        if (selectedArc != 1) {
            ctx.canvas.text("17"+years[selectedArc-2], -75, -75);
        }

        if (selectedArc < years.length) {
            ctx.canvas.text("17"+years[selectedArc], 75, -75);
        }

        ctx.canvas.popMatrix();
        ctx.canvas.endDraw();
    }
}
