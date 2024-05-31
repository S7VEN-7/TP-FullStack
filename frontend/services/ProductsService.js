//Rappel sur l'utilisation de fetch
export async function findAll()
{
    const response = await fetch("http://localhost:8080/products");
    if (response.status === 200)
    {
        const data = await response.json();
        return data;
    }
    else
    {
        throw new Error("Unable to fetch products");
    }
}

export async function bid(idProduct) {
    const response = await fetch(`http://localhost:8080/bid/${idProduct}`, {
        method: "POST"
    });
    if (response.status === 200)
    {
        const data = await response.json();
        return data;
    }
    else
    {
        throw new Error("Unable to bid on the product");
    }
}