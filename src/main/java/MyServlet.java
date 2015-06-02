import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/")
public class MyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        //out.println(request.getParameter("name"));
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
            //out.println(jb.toString());
        } catch (Exception e) { /*report an error*/ }

        try {
            JSONObject jsonObject = new JSONObject(jb.toString());
            out.println(jsonObject);
        } catch (Exception e) {
            // crash and burn
            out.println(e.getMessage());
        }


    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","Cyrill");
        jsonObject.put("num",new Integer(22));
        JSONArray arr = new JSONArray();
        arr.put(jsonObject);
        DBconnector db= new DBconnector();
        //System.out.println("hello");
        out.println(db.getJson().toString());
        //out.println(arr);
    }
}