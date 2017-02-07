import processing.core.PImage;

public class Person {
    private String name;
    private int id;
    private int[] yearsActive;
    private Rapporto.CATEGORY category;
    private PImage portrait = null;
    private boolean hasImage = false;

    public Person(String name, Rapporto.CATEGORY category, int id, int[] yearsActive, PImage portrait) {
        this.name = name;
        this.id = id;
        this.yearsActive = yearsActive;
        this.category = category;
        if (portrait != null) {
            this.portrait = portrait;
            hasImage = true;
        }
    }

    public boolean hasImage() {
        return hasImage;
    }

    public PImage getPortrait() {
        return portrait;
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
