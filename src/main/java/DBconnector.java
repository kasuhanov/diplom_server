import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DBconnector {
    private Connection dbcon = null;
    private Statement statement = null;
    public DBconnector(){
        try{
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (Exception e) {
            System.out.println("Error: connector driver is missing");
        }
    }
    public static  Connection getDBConnection() throws SQLException {
        Connection dbConnection = null;
        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "89617898797q");
        return dbConnection;
    }
    public void add(String name, double Lat, double Long, String description)throws SQLException{
        dbcon = this.getDBConnection();
        int ID =getLastID();
        if(ID==0){return;}else ID++;
        //String selectTableSQL = "SELECT max(id)  FROM jlab.diplom";

        int checked =1;
        statement = dbcon.createStatement();
        String insertTableSQL = "INSERT INTO JLAB.diplom"
                + "( mark_id,name, lat,long, description , checked) " + "VALUES"
                + "('"+ID+"', '"+name+"', '"+Lat+"', '"+Long+"', '"+description+"', '"+checked+"')";
                //+ "('"+ID+"', '"+name+"', '"+Lat+"', '"+Long+"', '"+description+"', '"+checked+"')";
        statement.executeUpdate(insertTableSQL);

    }
    public  JSONArray convertToJSON(ResultSet resultSet) throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int total_rows = resultSet.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                        .toLowerCase(), resultSet.getObject(i + 1));
            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }
    public  JSONArray convert2JSON(ResultSet resultSet) throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int total_rows = resultSet.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();
            obj.put("name", resultSet.getObject(1));
            obj.put("description", resultSet.getObject(4));
            obj.put("lat", resultSet.getObject(2));
            obj.put("long", resultSet.getObject(3));
            obj.put("id", resultSet.getInt("mark_id"));
            jsonArray.put(obj);
        }
        return jsonArray;
    }
    public JSONArray getJson() {
        String selectTableSQL = "SELECT * FROM jlab.diplom WHERE checked = 1";
        JSONArray jsonArray = new JSONArray();
        try {
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            return this.convert2JSON(rs);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return jsonArray;
        }

    }
    public int getLastID() {
        String selectTableSQL = "SELECT max(mark_id)  FROM jlab.diplom";

        try {
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if(rs.next()) {
                return rs.getInt(1);
            } else return 0;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }

    }
    public void addImage(int id, String image)throws SQLException{
        dbcon = this.getDBConnection();
        statement = dbcon.createStatement();
        String insertTableSQL = "INSERT INTO JLAB.image_table"
                + "( mark_id, image) " + "VALUES"
                + "('"+id+"', '"+image+"')";
        //+ "('"+ID+"', '"+name+"', '"+Lat+"', '"+Long+"', '"+description+"', '"+checked+"')";
        statement.executeUpdate(insertTableSQL);
    }
    public void addUser(String Login, String Password){
        try {

                dbcon = this.getDBConnection();
                statement = dbcon.createStatement();
                String insertTableSQL = "INSERT INTO JLAB.user_table"
                        + "( login, password) " + "VALUES"
                        + "('"+Login+"', '"+Password+"')";
                statement.executeUpdate(insertTableSQL);

        }catch (Exception e){
            System.out.println(e.getMessage());

        }
    }
    public boolean login(String Login, String Password){
        String selectTableSQL = "SELECT *  FROM jlab.user_table WHERE login ='"+Login+"' AND password='"+Password+"'";

        try {
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if(rs.next()) {
                return true;
            } else return false;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean addComment(String login, int markId, String comment){
        String selectTableSQL = "SELECT *  FROM jlab.user_table WHERE login ='"+login+"'";

        try {
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if(rs.next()) {
                selectTableSQL = "SELECT *  FROM jlab.diplom WHERE mark_id ='"+markId+"'";
                dbcon = this.getDBConnection();
                statement = dbcon.createStatement();
                ResultSet rs2 = statement.executeQuery(selectTableSQL);
                if(rs2.next()) {
                    int ID=getCommentId(comment);
                    if(ID==0)return false;
                    dbcon = this.getDBConnection();
                    statement = dbcon.createStatement();
                    String insertTableSQL = "INSERT INTO JLAB.comment_link"
                        + "( mark_id, login, comment_id) " + "VALUES"
                        + "('"+markId+"', '"+login+"', '"+ID+"')";
                    statement.executeUpdate(insertTableSQL);
                    return true;
                } else return false;
            } else return false;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public int getCommentId(String comment){
        String selectTableSQL = "SELECT max(comment_id)  FROM jlab.comment";

        try {
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if(rs.next()) {
                int ID = rs.getInt(1);ID++;
                dbcon = this.getDBConnection();
                statement = dbcon.createStatement();
                String insertTableSQL = "INSERT INTO JLAB.comment"
                        + "( comment_id,comment) " + "VALUES"
                        + "('"+ID+"','"+comment+"')";
                statement.executeUpdate(insertTableSQL);

                return rs.getInt(1);
            } else return 0;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }
    }
    public List<String> getMarkComments(int markId){
        List<String> comments= new ArrayList<String>();
        String selectTableSQL = "SELECT  comment_id FROM jlab.comment_link";
        int i=0;
        try {
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                if (rs.getInt(1)!=0){
                    selectTableSQL = "SELECT  comment FROM jlab.comment WHERE comment_id='"+rs.getInt(1)+"'";
                    dbcon = this.getDBConnection();
                    statement = dbcon.createStatement();
                    ResultSet rs2 = statement.executeQuery(selectTableSQL);
                    if (rs2.next()){

                        comments.add(i,rs2.getString(1));
                        i++;
                    }
                }
            } // return comments;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        return comments;
    }
}
