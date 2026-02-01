import { loadCart, saveCart } from "../store/cart";

const API = import.meta.env.VITE_API_BASE_URL;

export default function Cart() {
  const cart = loadCart();

  async function checkout() {
    const token = localStorage.getItem("payflow_token");
    if (!token) {
      alert("Please login first");
      return;
    }

    const res = await fetch(`${API}/api/checkout/session`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({
        items: cart.map(i => ({ productId: i.productId, quantity: i.quantity }))
      })
    });

    const data = await res.json();
    window.location.href = data.checkoutUrl;
  }

  function clear() {
    saveCart([]);
    window.location.reload();
  }

  return (
    <div style={{ padding: 16 }}>
      <h2>Cart</h2>
      {cart.map(i => (
        <div key={i.productId}>
          {i.name} × {i.quantity}
        </div>
      ))}
      <button onClick={checkout}>Checkout</button>
      <button onClick={clear}>Clear</button>
    </div>
  );
}
