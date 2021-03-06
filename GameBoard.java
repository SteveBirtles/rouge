import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.MouseInfo;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;

import java.util.Random;

public class GameBoard extends JPanel implements ActionListener {

    public int[][] square;
    public boolean[] playerFlipped;
    public Maze maze = null;

    private Timer timer;
    private long lastRequest;
    private BufferedImage[][] sprite;
    private int cursorX = 0, cursorY = 0;
    private double cameraX = 0, cameraY = 0;
    private int playerX = 0, playerY = 0;
    private int selectedX = -1, selectedY;
    private double mouseX, mouseY;
    private boolean showcoords;
    private int[] lastMoved = null;
    private boolean connectionEstablished = false;
    private boolean drawMap = false;
    private long lastMillis;
    private boolean turnEnd;
    private long moveWait;
    private boolean directionLeft = true;

    public GameBoard() 
    {
        lastRequest = System.currentTimeMillis();

        sprite = new BufferedImage[11][2];

        try
        {
            sprite[0][0]  =   ImageIO.read(new File("res/Background/dirt.png"));
            sprite[1][0]  =   ImageIO.read(new File("res/Wizards/black.png"));
            sprite[2][0]  =   ImageIO.read(new File("res/Wizards/blue.png"));
            sprite[3][0]  =   ImageIO.read(new File("res/Wizards/green.png"));
            sprite[4][0]  =   ImageIO.read(new File("res/Wizards/grey.png"));
            sprite[5][0]  =   ImageIO.read(new File("res/Wizards/orange.png"));
            sprite[6][0]  =   ImageIO.read(new File("res/Wizards/purple.png"));
            sprite[7][0]  =   ImageIO.read(new File("res/Wizards/red.png"));
            sprite[8][0]  =   ImageIO.read(new File("res/Wizards/white.png"));
            sprite[9][0]  =   ImageIO.read(new File("res/Background/tile.png"));
            sprite[10][0] =   ImageIO.read(new File("res/Background/tunnelFloor.png"));
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        for (int s = 0; s < 11; s++) {

            BufferedImage b = sprite[s][0];
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-b.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            sprite[s][1] = op.filter(b, null);

        }

        addKeyListener(new KeyboardyMcKeyboardFace());  
        addMouseListener(new MouseyMcMouseFace());
        setFocusable(true);

        setBackground(new Color(0,0,64)); 

        resetBoard();

        timer = new Timer(16, this);
        timer.start();                 

    }

    private void resetBoard()
    {
        square = new int[512][512];      
        playerFlipped = new boolean[8];

        if (SwingFrame.server == null) {
            maze = new Maze(512,512);   

            Random rnd = new Random();        

            for (int izywizy = 0; izywizy < 8; izywizy++) {
                int x = 0; 
                int y = 0;
                while (x == 0 || maze.getGrid()[x][y] != 1 || square[x][y] > 0) {
                    x = rnd.nextInt(50); 
                    y = rnd.nextInt(50);            
                }
                square[x][y] = izywizy + 1; 
            }

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {

        if (!connectionEstablished) {

            if (System.currentTimeMillis() - lastRequest > 1000) {
                requestMap();            
                lastRequest = System.currentTimeMillis();
            } 

        }
        else 
        {
            mouseX = MouseInfo.getPointerInfo().getLocation().getX() - this.getLocationOnScreen().getX();
            mouseY = MouseInfo.getPointerInfo().getLocation().getY() - this.getLocationOnScreen().getY();        

            if (mouseX >= 0 && mouseX < 1280 && mouseY >= 0 && mouseY < 1024) {
                cursorX = (int) (mouseX) / 64;
                cursorY = (int) (mouseY) / 64;
            }  
        }

        if (turnEnd) {            
            requestMove(playerX, playerY);            
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) { 

        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        if (SwingFrame.server != null) {

            if (!connectionEstablished) {

                g.setPaint(new Color(255,255,255));
                g.drawString("Waiting for server...", 100, 100);

                g.drawImage (sprite[1][0], (int)( -100 + (1.480 * ((System.currentTimeMillis() - lastRequest + 0  ) % 1000))), 200, this);
                g.drawImage (sprite[2][0], (int)( -100 + (1.480 * ((System.currentTimeMillis() - lastRequest + 100) % 1000))), 250, this);
                g.drawImage (sprite[3][0], (int)( -100 + (1.480 * ((System.currentTimeMillis() - lastRequest + 200) % 1000))), 300, this);
                g.drawImage (sprite[4][0], (int)( -100 + (1.480 * ((System.currentTimeMillis() - lastRequest + 300) % 1000))), 350, this);
                g.drawImage (sprite[5][0], (int)( -100 + (1.480 * ((System.currentTimeMillis() - lastRequest + 400) % 1000))), 400, this);
                g.drawImage (sprite[6][0], (int)( -100 + (1.480 * ((System.currentTimeMillis() - lastRequest + 500) % 1000))), 450, this);
                g.drawImage (sprite[7][0], (int)( -100 + (1.480 * ((System.currentTimeMillis() - lastRequest + 600) % 1000))), 500, this);
                g.drawImage (sprite[8][0], (int)( -100 + (1.480 * ((System.currentTimeMillis() - lastRequest + 700) % 1000))), 550, this);

            }
            else
            {

                int xNudge = (int) ((cameraX - (int) cameraX) * 64);
                int yNudge = (int) ((cameraY - (int) cameraY) * 64);

                for (int x = -1; x < 21; x++)
                {
                    for (int y = 0; y < 17; y++)
                    {

                        if (x + (int) cameraX >= 0 
                        && y + (int) cameraY >= 0 
                        && x + (int) cameraX < 512 
                        && y + (int) cameraY < 512) {

                            if (maze != null && maze.getGrid()[x + (int) cameraX][y + (int) cameraY] == 0)
                            {
                                g.drawImage (sprite[0][0], x * 64 - xNudge, y * 64 - yNudge, this);
                                //                         if (x == cursorX && y == cursorY)
                                //                             g.setPaint(new Color(180,180,64));                
                                //                         else
                                //                             g.setPaint(new Color(180,180,180));                
                            }
                            else if(maze != null && maze.getGrid()[x + (int) cameraX][y + (int) cameraY] ==1)
                            {
                                g.drawImage (sprite[9][0], x * 64 - xNudge, y * 64 - yNudge, this);

                                //g.drawImage (sprite[0], x * 64 - xNudge, y * 64 - yNudge, this);
                                //                         if (x == cursorX && y == cursorY)
                                //g.setPaint(new Color(64,64,64));  
                                //g.fillRect (x * 64 - xNudge, y * 64 - yNudge, 64, 64);              
                                //                         else
                                //                             g.setPaint(new Color(160,160,160));                
                            }else{
                                g.drawImage(sprite[10][0], x* 64 -xNudge,y*64 -yNudge, this);
                            }

                            //g.fillRect (x * 64 - xNudge, y * 64 - yNudge, 64, 64);              

                            if (showcoords)
                            {
                                g.setPaint(new Color(192,192,192));
                                //                     g.drawString(columns[x] + rows[y], 322 + x * 64, 142 + y * 64);
                            }

                            int here = 0;
                            here = square[x + (int) cameraX][y + (int) cameraY];

                            if (here > 0) {
                                int direction = playerFlipped[here - 1] ? 1 : 0;
                                g.drawImage (sprite[here][direction], x * 64 - xNudge, y * 64 - yNudge, this);
                            }

                            if (x == selectedX && y == selectedY)
                            {
                                g.drawImage (sprite[0][0], x * 64 - xNudge, y * 64 - yNudge, this);
                            }

                        }
                    }

                }

                long millis = 250 - System.currentTimeMillis() % 250;

                //g.setPaint(new Color(255,255,255));                
                //g.drawString("Turn Time Remaining: " + millis, 100, 100);

                if (lastMillis < millis) {
                    //g.setPaint(new Color(255,255,255));                
                    //g.fillRect (0, 0, 1024, 1024);
                    turnEnd = true;
                    moveWait = 0 ;
                } else
                {
                    moveWait += millis - lastMillis;
                    if (moveWait < 0) moveWait = 0;
                    turnEnd = false;
                }

                lastMillis = millis;
            }
        }

        if (SwingFrame.server == null || drawMap)
        {
            BufferedImage canvas = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
            Color color;
            for (int x = 0; x < 512; x++)
            {
                for (int y = 0; y < 512; y++)                
                {

                    if (x  >= 512 || y >= 512) continue;                 

                    if (maze != null && maze.getGrid()[x ][y] == 1)
                    {
                        color = new Color(64,64,64);
                    } else if(maze != null && maze.getGrid()[x ][y] == 2){
                        color = new Color(0,0,255);

                    }else{
                        color = new Color(255,255,255);

                    }

                    canvas.setRGB(x, y, color.getRGB());                       

                }
            }

            g.drawImage(canvas, null, null);

        }

    }

    public void requestMap()
    {

        if (SwingFrame.server == null) return;

        String theGrid = null;

        try
        {
            URL url = new URL( "http://" + SwingFrame.server + "/map");                        
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("HTTP GET URL: " + url + ", Response Code: " + responseCode);
            InputStream inputStream = con.getInputStream(); 
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)); 
            String line="";
            while(br.ready()){ 
                line = br.readLine(); 
                //line has the contents returned by the inputStream 
            }

            theGrid = line;        

            //theGrid = con.getResponseMessage();
            String[] gridyMcGridFace = theGrid.split("~");
            System.out.println("THE GRID " + gridyMcGridFace[0] + ": " + gridyMcGridFace[1].length() + ", " + gridyMcGridFace[2].length()); 
            connectionEstablished = true;
        }
        catch (Exception ex)
        {
            System.out.println("HTTP GET ERROR: " + ex.getMessage());
        }

        if (theGrid != null) maze = new Maze(512,512, theGrid, square);

        if (SwingFrame.player != 0) {
            for (int x = 0; x < 512; x++)
            {
                for (int y = 0; y < 512; y++)                
                {
                    if (square[x][y] == SwingFrame.player) {
                        cameraX = x - 10;
                        cameraY = y - 8;
                        playerX = x;
                        playerY = y;
                    }
                }

            }
        }

    }

    public void requestMove(int x, int y)
    {

        if (SwingFrame.server == null) return;

        try
        {
            URL url = new URL( "http://" + SwingFrame.server + "/move?x=" + x + "&y=" + y + "&player=" + SwingFrame.player);                        
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("HTTP GET URL: " + url + ", Response Code: " + responseCode);
            InputStream inputStream = con.getInputStream(); 
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)); 
            String line="";
            while(br.ready()){ 
                line = br.readLine(); 
            }

            for (int i = 0; i < 512; i++)
            {
                for (int j = 0; j < 512; j++)                
                {
                    square[i][j] = 0;
                }
            }

            System.out.println("Recieved :" + line);

            String[] wizd = line.split(",");
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
                    case 3:
                    playerFlipped[wizType - 1] = Integer.parseInt(s) == 1;
                    wizBit = -1;
                    break;
                }
                wizBit++;
            }

            if (square[playerX][playerY] != SwingFrame.player)
            {            
                for (int xx = 0; xx < 512; xx++)
                {
                    for (int yy = 0; yy < 512; yy++)                
                    {
                        if (square[xx][yy] == SwingFrame.player) {
                            cameraX = xx - 10;
                            cameraY = yy - 8;
                            playerX = xx;
                            playerY = yy;
                        }
                    }

                }
            }

        }
        catch (Exception ex)
        {
            System.out.println("HTTP GET ERROR: " + ex.getMessage());
        }

    }

    class KeyboardyMcKeyboardFace extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            int keycode = e.getKeyCode();

            if (keycode == 'm' || keycode == 'M') 
            {
                drawMap = true;
            }

            if (keycode == 'n' || keycode == 'N') 
            {
                drawMap = false;        
            }

            if (moveWait == 0) {

                if (keycode == 'w' || keycode == 'W') 
                {
                    if (playerY > 0 && maze.getGrid()[playerX][playerY - 1] != 0
                    && square[playerX][playerY - 1] == 0) {                    
                        square[playerX][playerY] = 0;
                        square[playerX][--playerY] = SwingFrame.player;                    
                        cameraY -= 1;  
                        moveWait = 125;
                    }
                }
                if (keycode == 's' || keycode == 'S')
                {
                    if (playerY < 510 && maze.getGrid()[playerX][playerY + 1] != 0
                    && square[playerX][playerY + 1] == 0) {
                        square[playerX][playerY] = 0;
                        square[playerX][++playerY] = SwingFrame.player;
                        cameraY += 1;
                        moveWait = 125;
                    }
                }            
                if (keycode == 'a' || keycode == 'A') 
                {
                    if (playerX > 0 && maze.getGrid()[playerX - 1][playerY] != 0
                    && square[playerX - 1][playerY] == 0) {
                        square[playerX][playerY] = 0;
                        square[--playerX][playerY] = SwingFrame.player;
                        cameraX -= 1;
                        moveWait = 125;
                    }
                }
                if (keycode == 'd' || keycode == 'D') 
                {
                    if (playerX < 512 && maze.getGrid()[playerX + 1][playerY] != 0
                    && square[playerX + 1][playerY] == 0) {
                        square[playerX][playerY] = 0;
                        square[++playerX][playerY] = SwingFrame.player;   
                        cameraX += 1;
                        moveWait = 125;
                    }
                }       
            }

            if (keycode == 'r' || keycode == 'R') 
            {
                resetBoard();
            }            

            if (keycode == KeyEvent.VK_ESCAPE) 
            {
                System.exit(0);
                return;
            }            

        }
    }

    class MouseyMcMouseFace implements MouseListener
    {
        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}        

    }

}
