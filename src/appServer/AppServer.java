package appServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;


public class AppServer {

    public static void main(String[] args) throws Exception {
        
        WebAppContext context = new WebAppContext();
        context.setDescriptor("WebContent/WEB-INF/web.xml");
        context.setResourceBase("WebContent/");
        context.setContextPath("/MyDBoxClient");
        
        
        //ServletHandler handler = new ServletHandler();
        //add all servlet to use to the handler, the second argument is the path (e.g. http://localhost:8080/searchPublication)
       //handler.addServletWithMapping("AppServlet", "/app");
       // handler.addServletWithMapping(UserProfile.class, "/getProfile");
       // handler.addServletWithMapping(CreateUserProfile.class, "/createProfile");
        //Create a new Server, add the handler to it and start
        Server server = new Server(8080);
        context.addServlet(AppServlet.class, "/app");
        server.setHandler(context);
        
        server.start();
        
        //this dumps a lot of debug output to the console.
        server.dumpStdErr();
        server.join();
      
      }
}
