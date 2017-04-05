import org.eclipse.jetty.server.Server;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import javax.imageio.ImageIO;

public class SwingFrame extends JFrame 
{

    public static String server = null;

    public SwingFrame() 
    {

        if (SwingFrame.server == null) {
            this.setSize(1024, 1024);
        }
        else {
            this.setSize(1280, 1024);
        }            

        this.setUndecorated(true);
        this.setDefaultCloseOperation(3);
        this.setTitle("Pi Swing Chess");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);       
        this.setVisible(true);

        BufferedImage cursorImg = null;
        try { cursorImg = ImageIO.read(new File("cursor.png")); }       
        catch (Exception ex) { System.out.println(ex.getMessage()); }

        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        this.getContentPane().setCursor(blankCursor);

        GameBoard board = new GameBoard();
        this.add(board);

        if (SwingFrame.server == null) {        
            try
            {
                Server server = new Server(8080);
                server.setHandler(new HTTPRequestHandler(board));
                server.start();
                System.out.println("Server is live on " + HTTPRequestHandler.getMyNetworkAdapter());
            }
            catch (Exception ex)
            {
                System.out.println("Sever creation failed: " + ex.getMessage());
            }
        }

    }

    public static void main(String[] args)
    {

        if (args.length > 0) {
            server = args[0];
        }

        SwingUtilities.invokeLater(new Runnable() 
            {
                public void run() 
                {
                    SwingFrame game = new SwingFrame();
                    game.setVisible(true);
                }
            });                
    } 
}