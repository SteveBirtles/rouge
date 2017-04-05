import java.util.*;

public class Maze{
    private int height;
    private int width;
    private int[][] grid;

    public Maze(int x,int y){
        height=y;
        width=x;
        grid=new int[x][y];
        generate(0);
    }

    public Maze(int x,int y,String input){
        height=y;
        width=x;
        grid=new int[x][y];
        generate(0);
        int p=0;

        char[] map = input.toCharArray();

        for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                grid[i][j] = map[p] - 48;
                p++;
            }
        }
    }

    public int rand(int a, int b){
        return a+(int)Math.floor(Math.random()*(b-a));
    }

    public int[][] getGrid(){
        return grid;
    }

    public void generate(int num){
        for(int i=0;i<width;i++){
            for(int j=0;j<width;j++){
                grid[i][j]=0;
            }
        }
        int numberOfRooms=rand(200,900);
        //numberOfRooms=num;
        List<Room> rooms=new ArrayList<>();
        System.out.println("Making rooms...");
        for(int i=0;i<numberOfRooms;i++){
            rooms.add(new Room(rand(5,30),rand(5,30),false));
        }
        List<Room> placedRooms=new ArrayList<>();
        System.out.println("Placing rooms...");
        for(Room room:rooms){
            boolean placed=false;
            while(!placed){
                placed=true;
                int placementX=rand(0,width-room.getWidth());
                int placementY=rand(0,height-room.getHeight());
                room.setPosition(placementX,placementY);
                for(Room placedRoom:placedRooms){
                    for(int i=placedRoom.getStartY();i<placedRoom.getEndY();i++){
                        for(int j=placedRoom.getStartX();j<placedRoom.getEndX();j++){
                            if((i==room.getStartX()&&j==room.getStartY())||(i==room.getEndX()&&j==room.getStartY())||(i==room.getStartX()&&j==room.getEndY())||(i==room.getEndX()&&j==room.getEndY())){
                                placed=false;
                                //break;
                            }
                        }
                    }
                }
            }
            placedRooms.add(room);
            System.out.println("Made room "+placedRooms.size());

        }
        System.out.println("Making tunnels..");

        List<Integer> roomsToRemove=new ArrayList<>();
        for(int i=1;i<rooms.size();i++){
          Tunnel nextTunnel=new Tunnel(rooms.get(i-1),rooms.get(i),false);
          double dx=nextTunnel.getStartX()-nextTunnel.getEndX();
          double dy=nextTunnel.getStartY()-nextTunnel.getEndY();
          //System.out.println(nextTunnel.getStartX+" "+" "+i+" "+nextTunnel.getStartX());
          double c=nextTunnel.getStartY()-(dy/dx)*nextTunnel.getStartX();
          if(Math.abs(dy/dx)<1){
            if(nextTunnel.getStartX()<nextTunnel.getEndX()){
              for(int x=nextTunnel.getStartX();x<nextTunnel.getEndX();x++){
                grid[x][(int)((dy/dx)*x+c)]=2;
                grid[x][(int)((dy/dx)*x+c+1)]=2;
              }
            }else{
              for(int x=nextTunnel.getStartX();x>nextTunnel.getEndX();x--){
                grid[x][(int)((dy/dx)*x+c)]=2;
                grid[x][(int)((dy/dx)*x+c+1)]=2;
              }
            }
            if(roomsToRemove.contains(i-1)){
              if(roomsToRemove.contains(i)){
                roomsToRemove.remove((Integer)(i));
              }
              roomsToRemove.remove((Integer)(i-1));
            }
          }else{
            roomsToRemove.add(i);
          }
        }
        for(int i=roomsToRemove.size();i>0;i--){
          //rooms.remove(rooms.get(i-1));
        }

        /*List<Integer> roomsToRemove=new ArrayList<>();
        for(int i=1;i<rooms.size();i++){
            Tunnel nextTunnel=new Tunnel(rooms.get(i-1),rooms.get(i),false);
            double dx=nextTunnel.getStartX()-nextTunnel.getEndX();
            double dy=nextTunnel.getStartY()-nextTunnel.getEndY();
            //System.out.println(nextTunnel.getStartX+" "+" "+i+" "+nextTunnel.getStartX());
            double c=nextTunnel.getStartY()-(dy/dx)*nextTunnel.getStartX();
            if(Math.abs(dy/dx)<1){
                if(nextTunnel.getStartX()<nextTunnel.getEndX()){
                    for(int x=nextTunnel.getStartX();x<nextTunnel.getEndX();x++){
                        grid[x][(int)((dy/dx)*x+c)]=1;
                        if ((int)((dy/dx)*x+c+1) < 1024) grid[x][(int)((dy/dx)*x+c+1)]=1;
                    }
                }else{
                    for(int x=nextTunnel.getStartX();x>nextTunnel.getEndX();x--){
                        grid[x][(int)((dy/dx)*x+c)]=1;
                        if ((int)((dy/dx)*x+c+1) < 1024) grid[x][(int)((dy/dx)*x+c+1)]=1;
                    }
                }
            }else{ // Steep gradient (treat var x as y)
                if(nextTunnel.getStartX()<nextTunnel.getEndX()){
                    for(int x=nextTunnel.getStartX();x<nextTunnel.getEndX();x++){
                        grid[(int)((dx/dy)*x+c)][x]=1;
                        if ((int)((dx/dy)*x+c+1) < 1024) grid[(int)((dx/dy)*x+c+1)][x]=1;
                    }
                }else{
                    for(int x=nextTunnel.getStartX();x>nextTunnel.getEndX();x--){
                        grid[x][(int)((dx/dy)*x+c)]=1;
                        if ((int)((dx/dy)*x+c+1) < 1024) grid[x][(int)((dx/dy)*x+c+1)]=1;
                    }
                }
                /*if(roomsToRemove.contains(i-1)){
                if(roomsToRemove.contains(i)){
                roomsToRemove.remove((Integer)(i));
                }
                roomsToRemove.remove((Integer)(i-1));
                }*/
            }
        }
        /*for(int i=roomsToRemove.size();i>0;i--){
        //rooms.remove(rooms.get(i-1));
        }*/
        for(Room room:rooms){
            for(int i=room.getStartY();i<room.getEndY();i++){
                for(int j=room.getStartX();j<room.getEndX();j++){
                    grid[j][i]=1;
                }
            }
        }
    }

}
