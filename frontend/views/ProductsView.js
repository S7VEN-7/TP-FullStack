import { bid, findAll } from "../services/ProductsService.js";

export class ProductsView {

    async #displayProduct(product) {
        const products = document.querySelector(".products");
        const productElement = document.createElement("div");
        
        productElement.classList.add("product");
        products.append(productElement);
        
        productElement.dataset.id = product.id;

        let productName = document.createElement("p");
        productName.innerHTML = product.name;
        productName.classList.add("product-name");
        productElement.append(productName);

        let productOwner = document.createElement("p");
        productOwner.innerHTML = product.owner;
        productOwner.classList.add("product-owner");
        productElement.append(productOwner);

        let productBid = document.createElement("p");
        productBid.innerHTML = product.bid;
        productBid.classList.add("product-bid");
        productElement.append(productBid);

        let bidButton = document.createElement("button");
        bidButton.innerHTML = "Enchérir";
        
        bidButton.addEventListener("click", async (event) => {
            let productId = productElement.dataset.id;
            let newProduct = await bid(productId);
            productBid.innerHTML = newProduct.newBid;
        });
        
        bidButton.classList.add("bid-button");
        productElement.append(bidButton);
    }

    async displayProducts() {
        let products = await findAll();
        products.forEach(async (product) => {
            await this.#displayProduct(product);
        });
    }

    updateBid(data) {
        console.log(data);
        let productId = data.productId;
        let newBid = data.newBid;
        let productElement = document.querySelector(`.product[data-id="${productId}"]`);
        let productBid = productElement.querySelector(".product-bid");
        productBid.innerHTML = newBid;
    }
}