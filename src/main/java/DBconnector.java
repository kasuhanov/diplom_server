import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;


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
        int ID =3;
        int checked =1;
        statement = dbcon.createStatement();
        String insertTableSQL = "INSERT INTO JLAB.diplom"
                + "( name, lat,long, description , checked) " + "VALUES"
                + "('"+name+"', '"+Lat+"', '"+Long+"', '"+description+"', '"+checked+"')";
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
}
