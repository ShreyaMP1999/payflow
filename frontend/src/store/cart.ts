export type CartItem = {
    productId: number;
    name: string;
    priceCents: number;
    quantity: number;
  };
  
  const KEY = "payflow_cart";
  
  export function loadCart(): CartItem[] {
    try {
      const stored: unknown = JSON.parse(localStorage.getItem(KEY) || "[]");
      if (!Array.isArray(stored)) return [];
      return stored.filter((item): item is CartItem =>
        item !== null && typeof item === "object" &&
        Number.isSafeInteger(item.productId) && item.productId > 0 &&
        typeof item.name === "string" &&
        Number.isSafeInteger(item.priceCents) && item.priceCents >= 0 &&
        Number.isSafeInteger(item.quantity) && item.quantity > 0
      );
    } catch {
      return [];
    }
  }
  
  export function saveCart(items: CartItem[]) {
    localStorage.setItem(KEY, JSON.stringify(items));
  }
  
