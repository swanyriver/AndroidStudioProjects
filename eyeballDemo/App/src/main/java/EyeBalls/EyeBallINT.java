package EyeBalls;

/**
 * Created by Brandon on 12/9/13.
 */
public interface EyeBallINT {
    void focusHere(int x, int y);
    void focusHere(int RequestCode);
    double distanceToNewPupilCenter(int newCords[]);
    void findCenterInWindow();
    public int getPupilCenterX();


    public int getPupilCenterY();



    public int getmEyeballCenterWindowX();
    public int getmEyeballCenterWindowY();

    public int getHeight();
    public int getWidth();
}
