import { SSEClient } from './lib/sse-client.js';
import { ProductsView } from './views/ProductsView.js';

function run() {
    const productsView = new ProductsView();
    productsView.displayProducts();

    const mySSEClient = new SSEClient("localhost:8080");
    mySSEClient.connect();

    mySSEClient.subscribe("bids", (data) => {
        productsView.updateBid(data);
    });
}

run();