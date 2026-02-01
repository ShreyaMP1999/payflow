import { useState } from "react";
import { useNavigate } from "react-router-dom";

const API = import.meta.env.VITE_API_BASE_URL;

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  async function submit(path: string) {
    const res = await fetch(`${API}/api/auth/${path}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    localStorage.setItem("payflow_token", data.token);
    navigate("/products");
  }

  return (
    <div style={{ padding: 16 }}>
      <h2>Login / Register</h2>
      <input placeholder="Email" onChange={e => setEmail(e.target.value)} />
      <input type="password" placeholder="Password" onChange={e => setPassword(e.target.value)} />
      <button onClick={() => submit("login")}>Login</button>
      <button onClick={() => submit("register")}>Register</button>
    </div>
  );
}
