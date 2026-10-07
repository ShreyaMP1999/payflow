import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api/client";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function submit(path: string) {
    setError("");
    setBusy(true);
    try {
      const data = await api<{ token: string }>(`/api/auth/${path}`, {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });
      if (!data.token) throw new Error("Login did not return a token");
      localStorage.setItem("payflow_token", data.token);
      navigate("/products");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to sign in");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div style={{ padding: 16 }}>
      <h2>Login / Register</h2>
      <input aria-label="Email" type="email" autoComplete="email" placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
      <input aria-label="Password" type="password" autoComplete="current-password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
      <button disabled={busy || !email || !password} onClick={() => submit("login")}>Login</button>
      <button disabled={busy || !email || !password} onClick={() => submit("register")}>Register</button>
      {error && <p role="alert">{error}</p>}
    </div>
  );
}
