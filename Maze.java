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

    public Maze(int x, int y, String input, int[][] square){
        height=y;
        width=x;
        grid=new int[x][y];
        int p = 0;

        int[] map = new int[x*y];

        String[] splitInput = input.split("~");
        SwingFrame.player = Integer.parseInt(splitInput[0].substring(7,8));

        System.out.println("I am player number " + SwingFrame.player);

        String[] rle = splitInput[1].split(",");
        String[] wizd = splitInput[2].split(",");

        int wizBit = 0;
        int wizX = 0;
        int wizY = 0;
        int wizType = 0;
        for(String s : wizd){
            switch(wizBit){
                case 0:
                wizX = Integer.parseInt(s);
                break;
                case 1:
                wizY = Integer.parseInt(s);
                break;
                case 2:
                wizType = Integer.parseInt(s);
                square[wizX][wizY] = wizType;
                wizBit = -1;
                break;
            }
            wizBit++;
        }

        for (int i = 0; i < rle.length - 1; i += 2) {
            int value = Integer.parseInt(rle[i]);
            for (int j = 0; j < Integer.parseInt(rle[i + 1]); j++) {
                if(p+1 > x*y){System.out.println("Error!!!!");break;}

                map[p++] = value;
            }
        }

        int q = 0;
        outer: for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                grid[i][j] = map[q++];
                if (q > p) break outer;
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
        final int numberOfRooms=128;
        //numberOfRooms=num;
        List<Room> rooms=new ArrayList<>();
        System.out.println("Making rooms...");
        for(int i=0;i<numberOfRooms;i++){
            rooms.add(new Room(rand(10,50),rand(10,50),false));
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
        List<Room> doneRooms=new ArrayList<>();
        for(int i=1;i<rooms.size();i++){
            doneRooms.add(rooms.get(i-1));
            Room room = rooms.get(i);
            Tunnel nextTunnel=new Tunnel(room,room.getNearestRoom(doneRooms),false);
            double dx=nextTunnel.getStartX()-nextTunnel.getEndX();
            double dy=nextTunnel.getStartY()-nextTunnel.getEndY();
            //System.out.println(nextTunnel.getStartX+" "+" "+i+" "+nextTunnel.getStartX());
            if(Math.abs(dy/dx)<1){
                double c=nextTunnel.getStartY()-(dy/dx)*nextTunnel.getStartX();
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
                double c=nextTunnel.getStartX()-(dx/dy)*nextTunnel.getStartY();
                if(nextTunnel.getStartY()<nextTunnel.getEndY()){
                    for(int y=nextTunnel.getStartY();y<nextTunnel.getEndY();y++){
                        grid[(int)((dx/dy)*y+c)][y]=2;
                        grid[(int)((dx/dy)*y+c+1)][y]=2;
                    }
                }else{
                    for(int y=nextTunnel.getStartY();y>nextTunnel.getEndY();y--){
                        grid[(int)((dx/dy)*y+c)][y]=2;
                        grid[(int)((dx/dy)*y+c+1)][y]=2;
                    }
                }
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
        //}
        //}
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
