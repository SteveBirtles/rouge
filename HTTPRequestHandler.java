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

        for (int x = 0; x < 1024; x++) {
            for (int y = 0; y < 1024; y++) {                
                responseText.append(Integer.toString(board.maze.getGrid()[x][y]));
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
