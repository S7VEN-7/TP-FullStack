package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import database.PolyBayDatabase;
import models.Product;

public class ProductsDAO {

    private PolyBayDatabase database;

    public ProductsDAO() {
        try{
            this.database = new PolyBayDatabase();
        } catch (SQLException e) {
            System.err.println("Impossible de se connecter à la base de données");
        }
    }

    public ArrayList<Product> findAll() {
        try {
            PreparedStatement statement = database.prepareStatement("SELECT * FROM product;");
            ResultSet resultSet = statement.executeQuery();
            
            ArrayList<Product> products = new ArrayList<Product>();
            while (resultSet.next()) {
                products.add(new Product(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("owner"), resultSet.getFloat("bid")));
            }
            return products;
        } catch (SQLException exception) {
            System.err.println("Unable to fetch products.");
            exception.printStackTrace();
            return new ArrayList<Product>();
        }
    }

    public JsonObject bid(int idProduct) {
        JsonObject newBid_json = null;
        try {
            PreparedStatement statement = database.prepareStatement("UPDATE product SET bid = bid + 50 WHERE id = ?;");
            statement.setInt(1, idProduct);
            statement.executeUpdate();

            statement = database.prepareStatement("SELECT bid FROM product WHERE id = ?;");
            statement.setInt(1, idProduct);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                newBid_json = new JsonObject();
                newBid_json.addProperty("newBid", resultSet.getInt("bid"));
            }
            
        } catch (SQLException exception) {
            System.err.println("Unable to bid on product.");
            exception.printStackTrace();
        }

        return newBid_json;
    }
}
