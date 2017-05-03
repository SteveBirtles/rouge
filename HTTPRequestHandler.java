import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class HTTPRequestHandler extends AbstractHandler {

    private GameBoard board;

    public HTTPRequestHandler(GameBoard board)
    {
        this.board = board;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {                 
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        if (request.getRequestURI().equals("/favicon.ico")) return; 

        //DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        //Date dateobj = new Date();

        StringBuilder responseText = new StringBuilder();

        if (request.getRequestURI().startsWith("/map")) {

            System.out.println("Map request recieved from " + request.getRemoteAddr());

            responseText.append(request.getRemoteAddr());
            responseText.append("~");
            int lastValue = -1;
            int repeats = 0;
            int value = 0;

            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {                
                    value = board.maze.getGrid()[x][y];
                    if (value == lastValue) {
                        repeats += 1;
                    }
                    else {
                        if (repeats > 0) {
                            responseText.append(Integer.toString(lastValue) + ",");
                            responseText.append(Integer.toString(repeats) + ",");
                        }
                        lastValue = value;
                        repeats = 1;
                    }
                }
            }

            responseText.append("~");

            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {          
                    if(board.square[x][y] != 0){
                        String wiz = Integer.toString(x) + "," + Integer.toString(y) + ","
                            + Integer.toString(board.square[x][y]) + ",";
                        responseText.append(wiz);                     
                    }
                }

            }

        } else if (request.getRequestURI().startsWith("/move")) { // If client requested to move

            if (request.getQueryString() != null)
            {

                int lastx = -1;
                int lasty = 0;
                int playerx = 0;
                int playery = 0;
                int player = 0;

                for(String q : request.getQueryString().split("&"))
                {
                    if (q.contains("=")) 
                    {
                        String variable = q.split("=")[0];
                        String value = q.split("=")[1];

                        if (variable.equals("x")) playerx = Integer.parseInt(value);
                        if (variable.equals("y")) playery = Integer.parseInt(value);                    
                        if (variable.equals("player")) player = Integer.parseInt(value);                        
                    }
                    else               
                    {
                        System.out.println(" -> Invalid query string component (" + q + ")");
                    }
                }
                for (int x = 0; x < 512; x++) {
                    for (int y = 0; y < 512; y++) {          
                        if(board.square[x][y] == player){
                            lastx = x;
                            lasty = y;
                            break;
                        }
                    }
                }                        

                synchronized(board) {
                    if (board.square[playerx][playery] == 0) {
                        if (lastx > -1) {
                            board.square[lastx][lasty] = 0;
                            board.playerFlipped[player - 1] = lastx > playerx;
                        }
                        board.square[playerx][playery] = player;                           
                    }
                }

                for (int x = 0; x < 512; x++) {
                    for (int y = 0; y < 512; y++) {          
                        synchronized(board) {
                            if(board.square[x][y] != 0){
                                String wiz = Integer.toString(x) + "," + Integer.toString(y) + ","
                                    + Integer.toString(board.square[x][y]) + ",";
                                if (board.playerFlipped[board.square[x][y] - 1] ) {
                                    wiz += "1,";
                                } else {
                                    wiz +="0,";
                                }
                                responseText.append(wiz);                     
                            }
                        }
                    }
                }

            }
            else
            {
                responseText.append("No query string supplied");
            }

        }

        //System.out.println("Generated response OK, length: " + responseText.length());
        response.getWriter().println(responseText.toString());
        baseRequest.setHandled(true);

    }

    public static String getMyNetworkAdapter() throws SocketException
    {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) 
        {
            NetworkInterface iface = interfaces.nextElement();

            if (iface.isLoopback() || !iface.isUp()) continue; // filters out 127.0.0.1 and inactive interfaces

            Enumeration<InetAddress> addresses = iface.getInetAddresses();            

            if (iface.getDisplayName().startsWith("wlan0")) continue;

            while(addresses.hasMoreElements())             
            {                
                InetAddress addr = addresses.nextElement();
                if (!(addr instanceof Inet4Address)) continue;
                return (addr.getHostAddress());
            }
        }
        return null;
    }
}
