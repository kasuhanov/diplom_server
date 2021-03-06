import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    public void close() {
        try {
            statement.close();
            dbcon.close();
        } catch (SQLException e) {
            System.out.println("Error: Problem with closing database");
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
        resultSet.close();
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
                rs.close();
                return ID;
            } else {rs.close();return 0;}
        }catch (Exception e){
            System.out.println(e.getMessage());
            return 0;
        }
    }
    private List<String>  users=new ArrayList<String>();
    private List<String>  comm=new ArrayList<String>();
    private int iii=0;
    public Map<String,String> getMarkComments(int markId){
        Map<String,String> comments= new HashMap<String, String>();
        String selectTableSQL = "SELECT  comment_id,login FROM jlab.comment_link WHERE mark_id='"+markId+"'";
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

                        comments.put(rs.getString(2), rs2.getString(1));

                        comm.add( rs2.getString(1));
                        users.add( rs.getString(2));
                        iii++;
                        i++;
                    }
                    rs2.close();
                }
            }
            rs.close();
            // return comments;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
        return comments;
    }
    public JSONArray getJsonComments(Map<String,String> map){
        JSONArray jsonArray = new JSONArray();
        JSONObject obj = new JSONObject();
        obj.put("comments",comm);
        obj.put("users",users);
        //for(int i=0;i<iii;i++) {
        //    obj.put(comm.get(i), users.get(i));
        //}
        //obj.put("map",map);
        jsonArray.put(obj);
        return jsonArray;
    }
    public JSONArray getCountries(){
        String selectTableSQL = "SELECT * FROM jlab.country";
        JSONArray jsonArray = new JSONArray();
        try{
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getObject(1));
                obj.put("country", rs.getObject(2));
                jsonArray.put(obj);
            }
            return jsonArray;
        }catch (Exception e){
            return jsonArray;
        }

    }
    public JSONArray getHotels(int country_id){
        String selectTableSQL = "SELECT * FROM jlab.hotel_country WHERE country_id ="+country_id;
        JSONArray jsonArray = new JSONArray();
        try{
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                System.out.println(rs.getInt(1));
                jsonArray.put(getHotelByID(rs.getInt(1)));

            }
            return jsonArray;
        }catch (Exception e){
            return jsonArray;
        }

    }
    public JSONObject getHotelByID(int id){
        String selectTableSQL = "SELECT *  FROM jlab.hotel WHERE hotel_id ="+id;
        JSONArray jsonArray = new JSONArray();
        JSONObject obj = new JSONObject();
        try {
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) {


                    obj.put("id", rs.getObject(1));
                    obj.put("name", rs.getObject(2));
                    obj.put("description", rs.getObject(3));
                    obj.put("star", rs.getObject(4));
            }
            return obj;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return obj;
        }

    }
    public JSONArray getRooms(int hotel_id){
        String selectTableSQL = "SELECT * FROM jlab.hotel_room WHERE hotel_id ="+hotel_id;
        JSONArray jsonArray = new JSONArray();
        try{
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            while (rs.next()) {
                System.out.println(rs.getInt(1));
                jsonArray.put(getRoomByID(rs.getInt(1)));

            }
            return jsonArray;
        }catch (Exception e){
            return jsonArray;
        }

    }
    public JSONObject getRoomByID(int id){
        String selectTableSQL = "SELECT *  FROM jlab.room WHERE room_id ="+id;
        JSONArray jsonArray = new JSONArray();
        JSONObject obj = new JSONObject();
        try {
            dbcon = this.getDBConnection();
            statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery(selectTableSQL);
            if (rs.next()) {


                obj.put("id", rs.getObject(1));
                obj.put("type", rs.getObject(2));
                obj.put("date_start", rs.getObject(3));
                obj.put("date_long", rs.getObject(4));
                obj.put("description", rs.getObject(5));
            }
            return obj;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return obj;
        }

    }

}
