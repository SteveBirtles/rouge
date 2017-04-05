public class Tunnel{
  private int startX;
  private int startY;
  private int endX;
  private int endY;
  private boolean secret;

  public Tunnel(Room a, Room b, boolean secret){
    startX=(int)(a.getStartX()+(a.getWidth()/2.0));
    startY=(int)(a.getStartY()+(a.getHeight()/2.0));
    endX=(int)(b.getStartX()+(b.getWidth()/2.0));
    endY=(int)(b.getStartY()+(b.getHeight()/2.0));
    this.secret=secret;
  }
  public int getStartX(){
    return startX;
  }
  public int getStartY(){
    return startY;
  }public int getEndX(){
    return endX;
  }
  public int getEndY(){
    return endY;
  }

}
