import { loadCart, saveCart } from "../store/cart";
import { useState } from "react";
import { api, getToken } from "../api/client";

export default function Cart() {
  const [cart, setCart] = useState(loadCart);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function checkout() {
    if (!getToken()) {
      setError("Please login first");
      return;
    }

    setError("");
    setBusy(true);
    try {
      const data = await api<{ checkoutUrl: string }>("/api/checkout/session", {
        method: "POST",
        body: JSON.stringify({
          items: cart.map(i => ({ productId: i.productId, quantity: i.quantity })),
        }),
      });
      if (!data.checkoutUrl) throw new Error("Checkout did not return a payment URL");
      window.location.href = data.checkoutUrl;
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to start checkout");
      setBusy(false);
    }
  }

  function clear() {
    saveCart([]);
    setCart([]);
    setError("");
  }

  return (
    <div style={{ padding: 16 }}>
      <h2>Cart</h2>
      {cart.map(i => (
        <div key={i.productId}>
          {i.name} × {i.quantity}
        </div>
      ))}
      {cart.length === 0 && <p>Your cart is empty.</p>}
      <button disabled={busy || cart.length === 0} onClick={checkout}>{busy ? "Opening checkout..." : "Checkout"}</button>
      <button disabled={busy || cart.length === 0} onClick={clear}>Clear</button>
      {error && <p role="alert">{error}</p>}
    </div>
  );
}
