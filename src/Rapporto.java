import processing.core.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import TUIO.*;
import processing.data.Table;
import processing.data.TableRow;

public class Rapporto extends PApplet {

    public enum CATEGORY {
        FRIEND, BENEFACTOR, LOVER, RIVAL, MAIN
    }

    public static final int TIMEID = 4;
    public static final int CURVATURE = 1000;
    private static final int WIDTH = 1400;
    private static final int HEIGHT = 900;
    private TuioProcessing tuioClient;
    private boolean callback = true;
    public static Map<Number, Person> persons = new HashMap<>();
    public static Map<Number, Tangible> tangibles = new HashMap<>();
    public static Map<Number, Connection> connections = new HashMap<>();

    private float cursor_size = 15;
    private float object_size = 60;
    private float table_size = 760;
    private float scale_factor = 1;
    public TangibleClock theTime = new TangibleClock(this, TIMEID);
    public static int currentTime = 55;

    public static PGraphics canvas;

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

    public void settings() {
        size(WIDTH, HEIGHT, P2D);
        scale_factor = HEIGHT/table_size;
    }

    public void setup() {
        tuioClient  = new TuioProcessing(this);

        canvas =  createGraphics(WIDTH, HEIGHT);

        frameRate(30);
        Table tangiblesTSV;
        tangiblesTSV = loadTable("data\\tangibles.tsv", "header, tsv");
        for (TableRow row : tangiblesTSV.rows()) {
            int id = row.getInt("id");
            CATEGORY cat = CATEGORY.valueOf(row.getString("cat"));
            tangibles.put(id, new Tangible(this, id, cat));
        }

        Table personsTSV;
        personsTSV = loadTable("data\\persons.tsv", "header, tsv");
        for (TableRow row : personsTSV.rows()) {
            int id = row.getInt("id");
            CATEGORY cat = CATEGORY.valueOf(row.getString("cat"));
            int[] yearsActive = stringToIntArr(row.getString("years"));
            String name = row.getString("name");
            persons.put(id, new Person(name, cat, id, yearsActive));
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

        ellipseMode(CENTER);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{"Rapporto"});
    }

    public void draw() {
        ArrayList<TuioObject> tuioObjectList = tuioClient.getTuioObjectList();
        int visibleTangibles = tuioObjectList.size();

        Rapporto.canvas.beginDraw();
        Rapporto.canvas.background(255);
        Rapporto.canvas.endDraw();

        for (int i = 0; i < visibleTangibles; i++) {
            TuioObject tobj = tuioObjectList.get(i);
            if (tobj.getSymbolID() == TIMEID) {
                theTime.draw(tobj.getScreenX(width), tobj.getScreenY(height), tobj.getAngle());
            } else {
                tangibles
                        .get(tobj.getSymbolID())
                        .draw(
                                tobj.getScreenX(width),
                                tobj.getScreenY(height),
                                tobj.getAngle()
                        );
            }
        }

        for (int i = 0; i<visibleTangibles; i++) {
            TuioObject a = tuioObjectList.get(i);
            for (int j = 0; j<visibleTangibles; j++) {
                TuioObject b = tuioObjectList.get(j);

                if (a.getSymbolID() != TIMEID && b.getSymbolID() != TIMEID) {
                    Tangible at = tangibles.get(a.getSymbolID());
                    Tangible bt = tangibles.get(b.getSymbolID());

                    canvas.beginDraw();
                    canvas.noFill();
                    canvas.stroke(255,200,0);
                    for (int k = 0; k < connections.size(); k++) {
                        Connection current = connections.get(k);
                        if (current.hasConnection(at.getSelectedPerson(), bt.getSelectedPerson())) {
                            canvas.stroke(0,0,255);
                            int x1 = a.getScreenX(WIDTH);
                            int y1 = a.getScreenY(HEIGHT);
                            int x2 = b.getScreenX(WIDTH);
                            int y2 = b.getScreenY(HEIGHT);
                            float t1 = a.getAngle();
                            float t2 = b.getAngle();
                            float curvatureDitherA = CURVATURE+100*cos((float)(millis()/1000.0));
                            float curvatureDitherB = CURVATURE+100*sin((float)(millis()/1000.0));
                            canvas.beginShape();
                            // Control point for beginning
                            canvas.curveVertex(
                                    x1-curvatureDitherA*cos(t1),
                                    y1-curvatureDitherB*sin(t1)
                            );
                            // actual beginning of curve
                            canvas.curveVertex(x1, y1);
                            // control point for ending
                            canvas.curveVertex(x2, y2);
                            // actual ending
                            canvas.curveVertex(
                                    x2-curvatureDitherA*cos(t2),
                                    y2-curvatureDitherB*sin(t2)
                            );

                            canvas.endShape();
                        }
                    }
                    canvas.endDraw();
                }
            }
        }
//        tint(255, 190);
        image(canvas, 0, 0);
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