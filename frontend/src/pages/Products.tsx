import { useEffect, useState } from "react";
import ProductCard from "../components/ProductCard";
import { loadCart, saveCart } from "../store/cart";
import { api } from "../api/client";

type Product = { id: number; name: string; description: string; priceCents: number; stock: number };

export default function Products() {
  const [products, setProducts] = useState<Product[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    api<Product[]>("/api/products")
      .then(setProducts)
      .catch(err => setError(err instanceof Error ? err.message : "Unable to load products"));
  }, []);

  function addToCart(p: Product) {
    const cart = loadCart();
    const item = cart.find(i => i.productId === p.id);
    if ((item?.quantity ?? 0) >= p.stock) {
      setError(`No more stock available for ${p.name}`);
      return;
    }
    setError("");
    if (item) item.quantity++;
    else cart.push({ productId: p.id, name: p.name, priceCents: p.priceCents, quantity: 1 });
    saveCart(cart);
    alert("Added to cart");
  }

  return (
    <div style={{ padding: 16, display: "grid", gap: 16 }}>
      {error && <p role="alert">{error}</p>}
      {products.map(p => (
        <ProductCard key={p.id} product={p} onAdd={() => addToCart(p)} />
      ))}
    </div>
  );
}
