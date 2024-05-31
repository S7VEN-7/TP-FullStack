package controllers;

import java.util.List;

import com.google.gson.JsonObject;

import dao.ProductsDAO;
import models.Product;
import webserver.WebServerContext;

public class ProductsController {
    public static List<Product> findAll(WebServerContext context) {
        ProductsDAO prod = new ProductsDAO();
        context.getResponse().json(prod.findAll());
        return prod.findAll();
    }

    public static void bid(WebServerContext context) {
        try {
            ProductsDAO prod = new ProductsDAO();
            int productId = Integer.parseInt(context.getRequest().getParam("productId"));
            JsonObject newBid = prod.bid(productId);
            context.getResponse().json(newBid);

            JsonObject data = new JsonObject();
            data.addProperty("productId", productId);
            data.addProperty("newBid", newBid.get("newBid").getAsInt());
            context.getSSE().emit("bids", data.toString());
        } catch (Exception e) {
            context.getResponse().serverError("Unable to bid on product.");
        }
    }
}
