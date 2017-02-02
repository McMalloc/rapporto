public class Connection {
    private int aID;
    private int bID;
    private Rapporto.CATEGORY category;
    private String content;
    private int id;
    private int[] yearsActive;

    public Connection(int _id, int a, int b, Rapporto.CATEGORY _category, int[] _yearsActive, String _content) {
        content = _content;
        id = _id;
        aID = a;
        bID = b;
        yearsActive = _yearsActive;
        category = _category;
    }

    public boolean hasConnection(int idX, int idY) {
        boolean matchingYear = false;
        for (int i = 0; i < yearsActive.length; i++) {
            matchingYear = Rapporto.currentTime == yearsActive[i];
            if (matchingYear) break;
        }
        return ((idX == aID && idY == bID) || (idY == aID && idX == bID)) && matchingYear;
    }

    public String getContent() {
        return content;
    }
}
