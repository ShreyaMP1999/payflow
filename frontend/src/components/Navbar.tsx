import { Link, useNavigate } from "react-router-dom";

export default function Navbar() {
  const navigate = useNavigate();
  const token = localStorage.getItem("payflow_token");

  function logout() {
    localStorage.removeItem("payflow_token");
    navigate("/login");
  }

  return (
    <nav style={{ padding: 12, borderBottom: "1px solid #ccc" }}>
      <Link to="/products">Products</Link>{" | "}
      <Link to="/cart">Cart</Link>{" | "}
      {token ? (
        <button onClick={logout}>Logout</button>
      ) : (
        <Link to="/login">Login</Link>
      )}
    </nav>
  );
}
