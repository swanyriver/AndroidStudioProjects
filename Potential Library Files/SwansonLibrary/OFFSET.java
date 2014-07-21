package SwansonLibrary;

import android.graphics.Point;

public class OFFSET{
    private Point offset;

    public OFFSET(int x, int y, int x2, int y2){
        setoffset(new Point(x,y), new Point(x2,y2));
    }
    public OFFSET(Point originalPoint, Point offset) {
        setoffset(originalPoint,offset);
    }
    private void setoffset(Point originalPoint, Point offset){
        this.offset = new Point(originalPoint.x-offset.x,originalPoint.y-offset.y);
    }

    public Point get(int x, int y){return get(new Point(x,y));}
    public Point get(Point originalPoint){
        return new Point(originalPoint.x+offset.x,originalPoint.y+offset.y);
    }
}
