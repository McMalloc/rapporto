public class Person {
    private String name;
    private int id;
    private int[] yearsActive;
    private Rapporto.CATEGORY category;

    public Person(String _name, Rapporto.CATEGORY _category, int _id, int[] _yearsActive) {
        name = _name;
        id = _id;
        yearsActive = _yearsActive;
        category = _category;
    }

    public boolean isCategory(Rapporto.CATEGORY askedCat) {
        return category == askedCat;
    }
    public boolean isInTime(int year) {
        for (int i = 0; i<yearsActive.length; i++) {
            if (year == yearsActive[i]) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }
    public int getID() {
        return id;
    }
}
