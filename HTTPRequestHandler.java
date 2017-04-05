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
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
        if (request.getRequestURI().equals("/favicon.ico")) return; // SKIP FAVICON REQUESTS;

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date dateobj = new Date();
        System.out.println("Request recieved from " + request.getRemoteAddr());

        StringBuilder responseText = new StringBuilder();
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
                        //System.out.print(Integer.toString(value) + ","+Integer.toString(repeats)+",");
                    }
                    lastValue = value;
                    repeats = 1;
                }
            }
            //responseText.append(Integer.toString(value) + ",");
            //responseText.append(Integer.toString(repeats) + ",");
            //System.out.print(Integer.toString(value) + ","+Integer.toString(repeats));
        }

        responseText.append("~");

        for (int x = 0; x < 512; x++) {
            for (int y = 0; y < 512; y++) {          
                if(board.square[x][y] != 0){
                    String wiz = Integer.toString(x) + "," + Integer.toString(y) + ","
                        + Integer.toString(board.square[x][y]) + ",";
                    //System.out.println(wiz);
                    responseText.append(wiz);                     
                }
            }

        }

        System.out.println("Generated response OK, length: " + responseText.length());
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
