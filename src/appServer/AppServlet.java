package appServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AppServlet
 */
@WebServlet("/AppServlet")
public class AppServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AppServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	    
	    Client client = new Client();
	    
	    String method = request.getParameter("method");
	    PrintWriter out = response.getWriter();
	        
	    
	    if (method.equals("connect")) {
	        try {
                out.write(client.sendRequest());
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                System.err.println(e.getMessage());
            }
	    }
	    else if (method.equals("getToken")) {
	        String code = request.getParameter("code");
	        out.write(client.accessToken(code));
	    }
	    else if (method.equals("getAccInfo")) {
            String token = request.getParameter("token");
            String acc_id = request.getParameter("acc_id");
            out.write(client.getAccountInfo(token, acc_id));
        }
	    else if (method.equals("upload")) {
            String token = request.getParameter("token");
            String pathToFile = request.getParameter("pathToFile");
            out.write(client.uploadFile(token, pathToFile));
        }
	    else if(method.equals("delete")) {
	        String token = request.getParameter("token");
            String pathToDBFile = request.getParameter("pathToDBFile");
            out.write(client.deleteFile(token, pathToDBFile));
	    }
	    
	    out.flush();
	    out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	

    

}
