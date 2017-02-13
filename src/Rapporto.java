import processing.core.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import TUIO.*;
import processing.data.Table;
import processing.data.TableRow;
//import processing.opengl.PShader;

public class Rapporto extends PApplet {

//    public PShader blur;

    int colorLover = color(158, 53, 80);
    int colorBenefactor = color(104, 185, 154);
    int colorColleague = color(213, 227, 133);
    int colorFriend = color(176, 102, 57);

    public enum CATEGORY {
        FRIEND, BENEFACTOR, LOVER, RIVAL, MAIN, COLLEAGUE;

        public String getMoniker() {
            switch (this) {
                case FRIEND: return "Freund";
                case BENEFACTOR: return "Schirmherr";
                case LOVER: return "Liebschaft";
                case RIVAL: return "Rivale";
                case MAIN: return "Protagonist";
                case COLLEAGUE: return "Mitstreiter";
            }
            return "";
        }
    }

    public int getCategoryColor(CATEGORY cat) {
        switch (cat) {
            case BENEFACTOR: return colorBenefactor;
            case LOVER: return colorLover;
            case FRIEND: return colorFriend;
            case COLLEAGUE: return colorColleague;
        }
        return 0;
    }

    public static final int TIMEID = 4;
    public static final int CURVATURE = 1000;
    static final int WIDTH = 980;
    static final int HEIGHT = 980;
    private TuioProcessing tuioClient;
    private boolean callback = true;

    public static PGraphics mask;
    public PGraphics blurH;
    public static Map<Number, Person> persons = new HashMap<>();
    public static Map<Number, Tangible> tangibles = new HashMap<>();
    public static Map<Number, Connection> connections = new HashMap<>();
    public static Map<CATEGORY, Integer> nrOfConnections = new HashMap<>();

    private float cursor_size = 15;
    private float object_size = 60;
    private float table_size = 760;
    private float scale_factor = 1;
    public TangibleClock theTime = new TangibleClock(TIMEID);
    public static int currentTime = 58;
    public int[][] canvasPoints = {
            {0, 0},
            {WIDTH, 0},
            {WIDTH, HEIGHT},
            {0, HEIGHT}
    };

    public static int OBJECTSIZE = 197;
    public PImage cursor, bg, ornamentS, ornamentB, floralL, floralR;
    public PFont dinMeds, dinMedb, dinReg, minion;

    public PGraphics canvas;

    public boolean calibration = false;

    public static ArrayList getRelevantPersons(CATEGORY askedCat) {
        //TODO move to tuio update
        ArrayList<Integer> retArr = new ArrayList<>();
        for (Map.Entry<Number, Person> personEntry : persons.entrySet()) {
            Person p = personEntry.getValue();
            if (p.isCategory(askedCat) && p.isInTime(currentTime)) {
                retArr.add(p.getID());
            }
        }
        return retArr;
    }

    public boolean contains(int[] array, int e) {
        for (int i = 0; i<array.length; i++) {
            if (array[i] == e) return true;
        }
        return false;
    }

    public void getAvailableConnections() {
        for (CATEGORY cat : CATEGORY.values()) {
            nrOfConnections.put(cat, 0);
        }
        for (int i = 0; i < connections.size(); i++) {
            Connection current = connections.get(i);
            if (current.aID == 0) {
                if (contains(current.getActiveYears(), currentTime)) {
                    int old = nrOfConnections.get(current.getCategory());
                    nrOfConnections.put(current.getCategory(), old+1);
                }
            }
        }
        System.out.print(currentTime);
        System.out.println(nrOfConnections);
    }

    public void settings() {
        size(WIDTH, HEIGHT, P2D);
        scale_factor = HEIGHT/table_size;
    }

    public void setup() {
        tuioClient  = new TuioProcessing(this);
        canvas =  createGraphics(WIDTH, HEIGHT, P2D);
//        blurH = createGraphics(400, 400, P2D);
        mask = createGraphics(OBJECTSIZE/2, OBJECTSIZE/2, P2D);
//        blur = loadShader("data\\blur.glsl");
//        blur.set("sigma", 15.0f);
//        blur.set("horizontalPass", 1);
        dinMedb = loadFont("data\\fonts\\DIN-Medium-21.vlw");
        dinMeds = loadFont("data\\fonts\\DIN-Medium-23.vlw");
        dinReg = loadFont("data\\fonts\\DIN-Regular-21.vlw");
        minion = loadFont("data\\fonts\\MinionPro-Semibold-48.vlw");

        bg = loadImage("data\\sprites\\bg.png");
        floralL = loadImage("data\\sprites\\floralL.png");
        floralR = loadImage("data\\sprites\\floralR.png");
        ornamentB = loadImage("data\\sprites\\ornamentB.png");
        ornamentS = loadImage("data\\sprites\\ornamentS.png");
        cursor = loadImage("data\\sprites\\cursor.png");

        frameRate(60);
        for (CATEGORY cat : CATEGORY.values()) {
            nrOfConnections.put(cat, 0);
        }

        Table tangiblesTSV;
        tangiblesTSV = loadTable("data\\tangibles.tsv", "header, tsv");
        for (TableRow row : tangiblesTSV.rows()) {
            int id = row.getInt("id");
            CATEGORY cat = CATEGORY.valueOf(row.getString("cat"));
            tangibles.put(id, new Tangible(id, cat));
        }

        Table connectionsTSV;
        connectionsTSV = loadTable("data\\connections.tsv", "header, tsv");
        for (TableRow row : connectionsTSV.rows()) {
            int id = row.getInt("id");
            CATEGORY cat = CATEGORY.valueOf(row.getString("cat"));
            int a = Integer.parseInt(row.getString("idA"));
            int b = Integer.parseInt(row.getString("idB"));
            int[] yearsActive = stringToIntArr(row.getString("years"));

            String content = row.getString("content");
            connections.put(id, new Connection(
                    id,
                    a,
                    b,
                    cat,
                    yearsActive,
                    content
            ));
        }

        Table personsTSV;
        personsTSV = loadTable("data\\persons.tsv", "header, tsv");
        for (TableRow row : personsTSV.rows()) {
            int id = row.getInt("id");
            CATEGORY cat = CATEGORY.valueOf(row.getString("cat"));
            int[] yearsActive = stringToIntArr(row.getString("years"));
            String name = row.getString("name");
            persons.put(id, new Person(
                    name,
                    cat,
                    id,
                    yearsActive,
                    loadImage("data\\portraits\\p_"+ id +".png")));
        }

        ellipseMode(CENTER);

//        mask.beginDraw();
//        mask.fill(255);
//        mask.ellipse(OBJECTSIZE/4, OBJECTSIZE/4, OBJECTSIZE/2, OBJECTSIZE/2);
//        mask.endDraw();
    }

    public static void main(String[] args)  {
        PApplet.main(new String[]{"Rapporto"});
    }

    public void draw() {
        ArrayList<TuioObject> tuioObjectList = tuioClient.getTuioObjectList();
        int visibleTangibles = tuioObjectList.size();

        background(0);
        canvas.beginDraw();
        if (calibration) {
            canvas.background(0,255,0);
        } else {
            canvas.background(0);
        }
        canvas.imageMode(CORNER);
        canvas.image(bg, 0, 0);
        canvas.endDraw();

        for (int i = 0; i < visibleTangibles; i++) {
            TuioObject tobj = tuioObjectList.get(i);
            if (tobj.getSymbolID() == TIMEID) {
                theTime.draw(this, tobj.getScreenX(width), tobj.getScreenY(height), tobj.getAngle());
            } else {
                try {
                    tangibles
                            .get(tobj.getSymbolID())
                            .draw(
                                    this,
                                    tobj.getScreenX(width),
                                    tobj.getScreenY(height),
                                    tobj.getAngle()
                            );
                } catch (Exception e) {
                    System.out.println("Couldn't get tangible with index "+i+" with exception: " + e);
                }
            }
        }

        for (int i = 0; i<visibleTangibles; i++) {
            TuioObject a = tuioObjectList.get(i);
//            if (i==1) break;
            for (int j = 0; j<visibleTangibles; j++) {
                // TEST CODE
//                if (j==1) {
                    TuioObject b = tuioObjectList.get(j);
//
//                    canvas.beginDraw();
//                    canvas.noFill();
//                    canvas.stroke(255,200,0);
//                    Tangible at = tangibles.get(a.getSymbolID());
//                    Tangible bt = tangibles.get(b.getSymbolID());
//                    Connection current = connections.get(4);
//                    current.draw(this, a, b);
//                    canvas.endDraw();
//                }


                if (a.getSymbolID() != TIMEID && b.getSymbolID() != TIMEID) {
                    Tangible at = tangibles.get(a.getSymbolID());
                    Tangible bt = tangibles.get(b.getSymbolID());

                    canvas.beginDraw();
                    canvas.noFill();
                    canvas.stroke(255,200,0);
                    try {
                        for (int k = 0; k < connections.size(); k++) {
                            Connection current = connections.get(k);
                            if (at != null && bt != null &
                                    current.hasConnection(at.getSelectedPerson(), bt.getSelectedPerson())
                                    ) {
                                current.draw(this, a, b);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    canvas.endDraw();
                }
            }
        }
//        tint(255, 170);

        drawFixedGui();
        beginShape();
        texture(canvas);
        vertex(canvasPoints[0][0], canvasPoints[0][1], 0, 0);
        vertex(canvasPoints[1][0], canvasPoints[1][1], 980, 0);
        vertex(canvasPoints[2][0], canvasPoints[2][1], 980, 980);
        vertex(canvasPoints[3][0], canvasPoints[3][1], 0, 980);
        endShape();

//        image(canvas, 0, 0);

        fill(0,255,0);
        text((int)frameRate, WIDTH-40, 40);
        if (calibration) {
            text("CALIBRATION", WIDTH/2, HEIGHT/2);
        }
    }

    public void drawFixedGui() {
        canvas.beginDraw();
        int cardWidth = 18;
        int cardHeight = 32;

        canvas.imageMode(CORNER);
        canvas.image(floralL, WIDTH*0.23f, HEIGHT*0.75f);
        canvas.image(floralR, WIDTH*0.59f, HEIGHT*0.75f);
        canvas.textAlign(CENTER, TOP);

        canvas.fill(255);
        canvas.textFont(minion);
        canvas.text("17"+ theTime.getCurrentYear(), WIDTH/2, HEIGHT*0.75f);
        canvas.strokeWeight(1);
        canvas.textFont(dinReg);

        drawCardIndicator(CATEGORY.BENEFACTOR, 0, cardHeight, cardWidth);
        drawCardIndicator(CATEGORY.FRIEND, 1, cardHeight, cardWidth);
        drawCardIndicator(CATEGORY.COLLEAGUE, 2, cardHeight, cardWidth);
        drawCardIndicator(CATEGORY.LOVER, 3, cardHeight, cardWidth);

        canvas.endDraw();
    }

    public void drawCardIndicator(CATEGORY category, int position, int cardHeight, int cardWidth) {
        int margin = (WIDTH-353*2-4*cardWidth)/3;
        if (nrOfConnections.get(category) > 0) {
            canvas.fill(getCategoryColor(category));
            canvas.stroke(255);
            canvas.rect(353+margin*position+cardWidth*position, 840, cardWidth, cardHeight, 2);
            canvas.fill(255);
            canvas.text(category.getMoniker(), 353+margin*position+cardWidth*position, 840+cardHeight+15);
        } else {
            canvas.fill(getCategoryColor(category), 70);
            canvas.stroke(255, 40);
            canvas.rect(353+margin*position+cardWidth*position, 840, cardWidth, cardHeight, 2);
            canvas.fill(255, 70);
            canvas.text(category.getMoniker(), 353+margin*position+cardWidth*position, 840+cardHeight+15);
        }
    }

    public void keyPressed() {
        switch (key) {
            case 'c':
                calibration = true;
                break;
        }
    }
    public void keyReleased() {
        switch (key) {
            case 'c':
                calibration = false;
                break;
        }
    }
    public void mouseClicked() {
        if (calibration) {
            if ((mouseX <= WIDTH/2) && (mouseY <= HEIGHT/2)) {
                System.out.println("oben links");
                canvasPoints[0][0] = mouseX;
                canvasPoints[0][1] = mouseY;
            } else if ((mouseX > WIDTH/2) && (mouseY <= HEIGHT/2)) {
                System.out.println("oben rechts");
                canvasPoints[1][0] = mouseX;
                canvasPoints[1][1] = mouseY;
            }if ((mouseX > WIDTH/2) && (mouseY > HEIGHT/2)) {
                System.out.println("unten rechts");
                canvasPoints[2][0] = mouseX;
                canvasPoints[2][1] = mouseY;
            } else if ((mouseX <= WIDTH/2) && (mouseY > HEIGHT/2)) {
                System.out.println("unten links");
                canvasPoints[3][0] = mouseX;
                canvasPoints[3][1] = mouseY;
            }
        }
    }

    // called when a cursor is added to the scene
    public void addTuioObject(TuioObject tobj) {
        System.out.println("added");
        tangibles.get(tobj.getSymbolID()).setVisible();
        //redraw();
    }

    public void updateTuioObject(TuioObject tobj) {
        if (tobj.getSymbolID() == TIMEID) {
            theTime.computeSelectedArc(tobj.getAngle());
            currentTime = theTime.getCurrentYear();
            getAvailableConnections();
        } else {
            tangibles.get(tobj.getSymbolID()).computeSelectedArc(tobj.getAngle());
        }
    }

    // called when a cursor is removed from the scene
    public void removeTuioObject(TuioObject tobj) {
        tangibles.get(tobj.getSymbolID()).setInvisible();
    }
    // --------------------------------------------------------------
// called at the end of each TUIO frame
    public void refresh(TuioTime frameTime) {
        if (false) println("frame #"+frameTime.getFrameID()+" ("+frameTime.getTotalMilliseconds()+")");
        if (callback) redraw();
    }

    //Helper functions
    public int[] stringToIntArr(String input) {
        String[] strArr = input.split(",");
        int[] outputArr = new int[strArr.length];
        for (int i = 0; i<outputArr.length; i++) {
            outputArr[i] = Integer.parseInt(strArr[i]);
        }
        return outputArr;
    }
}