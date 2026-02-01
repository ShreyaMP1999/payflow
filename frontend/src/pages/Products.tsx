import { useEffect, useState } from "react";
import ProductCard from "../components/ProductCard";
import { loadCart, saveCart } from "../store/cart";

const API = import.meta.env.VITE_API_BASE_URL;

export default function Products() {
  const [products, setProducts] = useState<any[]>([]);

  useEffect(() => {
    fetch(`${API}/api/products`)
      .then(res => res.json())
      .then(setProducts);
  }, []);

  function addToCart(p: any) {
    const cart = loadCart();
    const item = cart.find(i => i.productId === p.id);
    if (item) item.quantity++;
    else cart.push({ productId: p.id, name: p.name, priceCents: p.priceCents, quantity: 1 });
    saveCart(cart);
    alert("Added to cart");
  }

  return (
    <div style={{ padding: 16, display: "grid", gap: 16 }}>
      {products.map(p => (
        <ProductCard key={p.id} product={p} onAdd={() => addToCart(p)} />
      ))}
    </div>
  );
}
