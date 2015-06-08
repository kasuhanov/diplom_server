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
        StringBuffer jb = new StringBuffer();
        if(request.getParameter("par").equals("addmark")) {
            String line = null;
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);
                JSONObject jsonObject = new JSONObject(jb.toString());
                DBconnector db= new DBconnector();
                db.add(jsonObject.getString("name"),jsonObject.getDouble("lat"),
                        jsonObject.getDouble("long"),jsonObject.getString("description"));
                out.println(jsonObject);
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        }  else {
            if(request.getParameter("par").equals("addimage")) {
                String line = null;
                try {
                    BufferedReader reader = request.getReader();
                    while ((line = reader.readLine()) != null)
                        jb.append(line);
                    JSONObject jsonObject = new JSONObject(jb.toString());
                    DBconnector db= new DBconnector();
                    db.addImage(jsonObject.getInt("id"), jsonObject.getString("image"));
                    out.println(jsonObject);
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            }else {
                out.println("parameter error");
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if(request.getParameter("par").equals("marks")) {
            DBconnector db = new DBconnector();
            out.println(db.getJson().toString());
        }  else {
            out.println("parameter error");

            //DBconnector db = new DBconnector();

            //System.out.println(db.getMarkComments(1).get(0));
            //System.out.println(db.getMarkComments(1).get(1));
            //System.out.println(db.getMarkComments(1).get(2));
            //System.out.println(db.getMarkComments(3).get(0));
            //if(db.addComment("usa",1,"sdfsdf"))System.out.println("usa SSSS");
            //if(db.addComment("usa",112,"comment"))System.out.println("usa 112 logged");
            //if(db.addComment("kusa",1,"comment"))System.out.println("kusa logged");

            //db.addUser("usa","pas");
            //if(db.login("usa","padfas"))System.out.println("usa logged");
            //if(db.login("usa","pas"))System.out.println("kusa logged");
        }
    }
}