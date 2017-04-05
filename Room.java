import java.lang.Math;
import java.util.*;

public class Room{
  private int height;
  private int width;
  private int startX;
  private int startY;
  private boolean secret;

  public Room(int w, int h, boolean secret){
    height=h;
    width=w;
    this.secret=secret;
  }

  public void setPosition(int sx, int sy){
    startX=sx;
    startY=sy;
  }
  public int getStartX(){
    return startX;
  }
  public int getStartY(){
    return startY;
  }public int getEndX(){
    return startX+width;
  }
  public int getEndY(){
    return startY+height;
  }

  public int getHeight(){
    return height;
  }
  public int getWidth(){
    return width;
  }

  public Room getNearestRoom(List<Room> rooms){
    Room nearest=null;
    for(Room room : rooms) if(nearest==null||this.distanceToRoom(room)<this.distanceToRoom(nearest)) nearest = room;
    return nearest;
  }
  public int distanceToRoom(Room room){
    //Find dx
    int dx;
    if((this.getStartX()>room.getStartX() && this.getStartX()<(room.getStartX()+room.getWidth())) || (this.getStartX()>room.getStartX() && this.getStartX()<(room.getStartX()+room.getWidth()))){ // If rooms x-overlap
      dx = 0;
    }
    else{
      int dxattempt=room.getStartX()-this.getStartX()+this.getWidth();
      dx = (dxattempt>0)?dxattempt:this.getStartX()-room.getStartX()+room.getWidth();
    }

    //Find dy
    int dy;
    if((this.getStartY()>room.getStartY() && this.getStartY()<(room.getStartY()+room.getWidth())) || (this.getStartY()>room.getStartY() && this.getStartY()<(room.getStartY()+room.getWidth()))){ // If rooms y-overlap
      dy=0;
    }
    else{
      int dyattempt=room.getStartY()-this.getStartY()+this.getWidth();
      dy = (dyattempt>0)?dyattempt:this.getStartY()-room.getStartY()+room.getWidth();
    }
    // Pythagoras
    return (int) Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
  }
}
