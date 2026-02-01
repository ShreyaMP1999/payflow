export type CartItem = {
    productId: number;
    name: string;
    priceCents: number;
    quantity: number;
  };
  
  const KEY = "payflow_cart";
  
  export function loadCart(): CartItem[] {
    return JSON.parse(localStorage.getItem(KEY) || "[]");
  }
  
  export function saveCart(items: CartItem[]) {
    localStorage.setItem(KEY, JSON.stringify(items));
  }
  