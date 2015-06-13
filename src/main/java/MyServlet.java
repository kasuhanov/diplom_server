import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


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
                db.close();
                out.println(jsonObject);
                return;
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        }
        if(request.getParameter("par").equals("addcomment")) {
            String line = null;
            try {
                BufferedReader reader = request.getReader();

                while ((line = reader.readLine()) != null)
                    jb.append(line);
                JSONObject jsonObject = new JSONObject(jb.toString());
                DBconnector db= new DBconnector();

                if(db.addComment(jsonObject.getString("login"), (int) jsonObject.getLong("id"),
                        jsonObject.getString("comment"))){
                    //out.print("comment added");
                }//else out.println("failed");
                db.close();
                return;
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        }
        if(request.getParameter("par").equals("login")) {
            String line = null;
            try {
                BufferedReader reader = request.getReader();

                while ((line = reader.readLine()) != null)
                    jb.append(line);
                JSONObject jsonObject = new JSONObject(jb.toString());
                DBconnector db= new DBconnector();

                if(db.login(jsonObject.getString("login"), jsonObject.getString("password"))){
                    out.print("logged");
                }else out.println("failed");
                db.close();
                return;
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        }
        if(request.getParameter("par").equals("addimage")) {
            String line = null;
            try {
                BufferedReader reader = request.getReader();

                while ((line = reader.readLine()) != null)
                    jb.append(line);
                //JSONObject jsonObject = new JSONObject(jb.toString());
                //DBconnector db= new DBconnector();
                System.out.println(jb.toString());
                    out.println(jb.toString());
                return;
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if((request.getParameter("par").equals("comments"))) {
            DBconnector db = new DBconnector();
            if(request.getIntHeader("id")!=-1)
                out.println(db.getJsonComments(db.getMarkComments(request.getIntHeader("id"))));
            //out.println(request.getIntHeader("id"));
            db.close();
            return;
        }
        if(request.getParameter("par").equals("marks")) {
            DBconnector db = new DBconnector();
            out.println(db.getJson().toString());
            db.close();
            return;
        }
        if(request.getParameter("par").equals("countries")) {
            DBconnector db = new DBconnector();
            out.println(db.getCountries().toString());
            db.close();
            return;
        }
        if(request.getParameter("par").equals("hotel")) {
            DBconnector db = new DBconnector();
                out.println(db.getHotels(Integer.parseInt(request.getHeader("country"))).toString());
            db.close();
            return;
        }
        if(request.getParameter("par").equals("room")) {
            DBconnector db = new DBconnector();
            out.println(db.getRooms(Integer.parseInt(request.getHeader("hotel"))).toString());
            db.close();
            return;
        }
    }
}