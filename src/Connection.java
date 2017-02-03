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

    public String getContent() {
        return content;
    }
}