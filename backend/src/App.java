import controllers.ProductsController;
import webserver.WebServer;
import webserver.WebServerContext;


public class App {
    public static void main(String[] args) throws Exception {
        WebServer webserver = new WebServer();
        
        webserver.listen(8080);
        webserver.getRouter().get(
            "/products", (WebServerContext context) -> {
                ProductsController.findAll(context);
            });
        webserver.getRouter().post(
            "/bid/:productId", (WebServerContext context) -> {
                ProductsController.bid(context);
            });
    }
}
