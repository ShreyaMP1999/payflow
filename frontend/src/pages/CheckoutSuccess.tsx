  import { Link } from "react-router-dom";

  export default function CheckoutSuccess() {
    return (
      <div style={{ padding: 16 }}>
        <h2>Checkout status</h2>
        <p>Your payment status has not been verified here.</p>
        <Link to="/products">Continue shopping</Link>
      </div>
    );
  }
  
