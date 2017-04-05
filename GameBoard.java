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

import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.util.ArrayList;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Random;

public class GameBoard extends JPanel implements ActionListener {

    private Timer timer;

    private BufferedImage[] sprite;
    private int[][] square;
    private Maze maze;
    private int cursorX = 0, cursorY = 0;
    private double cameraX = 0, cameraY = 0;   
    private int selectedX = -1, selectedY;
    private double mouseX, mouseY;
    private boolean showcoords;
    private int[] lastMoved = null;

    public GameBoard() 
    {
        sprite = new BufferedImage[9];

        try
        {
            sprite[0] =   ImageIO.read(new File("selected.png"));
            sprite[1] =   ImageIO.read(new File("res/Wizards/black.png"));
            sprite[2] =   ImageIO.read(new File("res/Wizards/blue.png"));
            sprite[3] =   ImageIO.read(new File("res/Wizards/green.png"));
            sprite[4] =   ImageIO.read(new File("res/Wizards/grey.png"));
            sprite[5] =   ImageIO.read(new File("res/Wizards/orange.png"));
            sprite[6] =   ImageIO.read(new File("res/Wizards/purple.png"));
            sprite[7] =   ImageIO.read(new File("res/Wizards/red.png"));
            sprite[8] =   ImageIO.read(new File("res/Wizards/white.png"));
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
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
        square = new int[1024][1024];        

        maze = new Maze(1024,1024);

        Random rnd = new Random();        

        for (int izywizy = 0; izywizy < 10000; izywizy++) {
            int x = 0; 
            int y = 0;
            while (x == 0 || maze.getGrid()[x][y] != 1 || square[x][y] > 0) {
                x = rnd.nextInt(1024); 
                y = rnd.nextInt(1024);            
            }
            square[x][y] = rnd.nextInt(8) + 1; 

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        mouseX = MouseInfo.getPointerInfo().getLocation().getX() - this.getLocationOnScreen().getX();
        mouseY = MouseInfo.getPointerInfo().getLocation().getY() - this.getLocationOnScreen().getY();        

        if (mouseX >= 0 && mouseX < 1280 && mouseY >= 0 && mouseY < 1024) {
            cursorX = (int) (mouseX) / 64;
            cursorY = (int) (mouseY) / 64;
        }        

        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) { 

        //MoveChecker m = new MoveChecker();

        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;

        int xNudge = (int) ((cameraX - (int) cameraX) * 64);
        int yNudge = (int) ((cameraY - (int) cameraY) * 64);

        for (int x = -1; x < 21; x++)
        {
            for (int y = 0; y < 17; y++)
            {

                if (x + (int) cameraX >= 0 
                && y + (int) cameraY >= 0 
                && x + (int) cameraX < 1024 
                && y + (int) cameraY < 1024) {

                    if (maze.getGrid()[x + (int) cameraX][y + (int) cameraY] == 1)
                    {
                        if (x == cursorX && y == cursorY)
                            g.setPaint(new Color(180,180,64));                
                        else
                            g.setPaint(new Color(180,180,180));                
                    }
                    else
                    {
                        if (x == cursorX && y == cursorY)
                            g.setPaint(new Color(160,160,48));                
                        else
                            g.setPaint(new Color(160,160,160));                
                    }

                    g.fillRect (x * 64 - xNudge, y * 64 - yNudge, 64, 64);              

                    if (showcoords)
                    {
                        g.setPaint(new Color(192,192,192));
                        //                     g.drawString(columns[x] + rows[y], 322 + x * 64, 142 + y * 64);
                    }

                    int here = 0;

                    here = square[x + (int) cameraX][y + (int) cameraY];

                    if (here > 0) g.drawImage (sprite[here], x * 64 - xNudge, y * 64 - yNudge, this);

                    if (x == selectedX && y == selectedY)
                    {
                        g.drawImage (sprite[0], x * 64 - xNudge, y * 64 - yNudge, this);
                    }

                }
            }

        }
        g.setPaint(new Color(255,255,255));
        g.drawString(Double.toString(cameraX) + ", " + Double.toString(cameraY), 100, 100);

        cameraX += 0.25;

        if (cameraX > 1100) {
            cameraX -= 1100;
            cameraY = 1000;
            if (cameraY > 1100) {
                cameraY = 0;
            }
        }            

    }

    public void forceSync()
    {
        URL url;
        HttpURLConnection con;
        int responseCode;

        if (SwingFrame.opponent == null) return;
        {
            for (int x = 0; x < 8; x++)
            {
                for (int y = 0; y < 8; y++)
                {
                    String position = "";

                    try
                    {
                        //                         url = new URL( "http://" + SwingFrame.opponent + "/set?position=" + position + "&value=" + square[y][x] + "&unmoved=" + unmoved[y][x]);                        
                        //                         con = (HttpURLConnection) url.openConnection();
                        //                         con.setRequestMethod("GET");
                        //                         responseCode = con.getResponseCode();
                        //                         System.out.println("HTTP GET URL: " + url + ", Response Code: " + responseCode);
                    }
                    catch (Exception ex)
                    {
                        //                         System.out.println("HTTP GET ERROR: " + ex.getMessage());
                    }

                }

            }
        }

    }

    public void processClick()
    {
        if (selectedX >= 0)
        {
            if (selectedX != cursorX || selectedY != cursorY)
            {

                //                 if (moves[cursorY][cursorX] >= 0)
                //                 {
                //                     square[cursorY][cursorX] = square[selectedY][selectedX];
                //                     square[selectedY][selectedX] = 0;     
                //                     unmoved[selectedY][selectedX] = false;
                //                     if (moves[cursorY][cursorX] == 6)
                //                     {
                //                         if (square[cursorY][cursorX] == 1) square[cursorY + 1][cursorX] = 0;
                //                         if (square[cursorY][cursorX] == 7) square[cursorY - 1][cursorX] = 0;
                //                     }
                //                     lastMoved = new int[]{selectedX, selectedY, cursorX, cursorY};
                // 
                //                     if (SwingFrame.opponent != null)
                //                     {                        
                //                         String start = columns[selectedX] + rows[selectedY];
                //                         String end = columns[cursorX] + rows[cursorY];
                // 
                //                         try
                //                         {
                //                             URL url = new URL( "http://" + SwingFrame.opponent + "/move?start=" + start + "&end=" + end );                        
                //                             HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //                             con.setRequestMethod("GET");
                //                             int responseCode = con.getResponseCode();
                //                             System.out.println("HTTP GET URL: " + url + ", Response Code: " + responseCode);
                // 
                //                             if (moves[cursorY][cursorX] == 6)
                //                             {
                //                                 String position = null;
                //                                 if (square[cursorY][cursorX] == 1) position = columns[cursorX] + rows[cursorY + 1];
                //                                 if (square[cursorY][cursorX] == 7) position = columns[cursorX] + rows[cursorY - 1];                                
                // 
                //                                 new URL( "http://" + SwingFrame.opponent + "/move?position=" + position + "&value=0&unmoved=false" );
                //                                 con = (HttpURLConnection) url.openConnection();
                //                                 con.setRequestMethod("GET");
                //                                 responseCode = con.getResponseCode();
                //                                 System.out.println("HTTP GET URL: " + url + ", Response Code: " + responseCode);
                //                             }
                // 
                //                         }
                //                         catch (Exception ex)
                //                         {
                //                             System.out.println("HTTP GET ERROR: " + ex.getMessage());
                //                         }
                //                     }
                //                 }
            }
            selectedX = -1;
        }
        else
        {
            if (square[cursorY][cursorX] > 0)
            {
                selectedX = cursorX;
                selectedY = cursorY;                
            }
        }

    }

    class KeyboardyMcKeyboardFace extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int keycode = e.getKeyCode();

            if (keycode == 'w' || keycode == 'W') 
            {
                cameraY -= 1;
                if (cameraY < -16) cameraY = -16;                
            }
            if (keycode == 's' || keycode == 'S')
            {
                cameraY += 1;
                if (cameraY > 1024) cameraY = 1024;
            }            
            if (keycode == 'a' || keycode == 'A') 
            {
                cameraX -= 1;
                if (cameraX < -20) cameraX = -20;
            }
            if (keycode == 'd' || keycode == 'D') 
            {
                cameraX += 1;
                if (cameraX > 1024) cameraX = 1024;
            }            

            if (keycode == 'r' || keycode == 'R') 
            {
                resetBoard();
            }            

            if (keycode == 'q' || keycode == 'Q') 
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
            processClick();
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

