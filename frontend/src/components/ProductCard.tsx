type Props = {
    product: {
      id: number;
      name: string;
      description: string;
      priceCents: number;
    };
    onAdd: () => void;
  };
  
  export default function ProductCard({ product, onAdd }: Props) {
    return (
      <div style={{ border: "1px solid #ddd", padding: 16 }}>
        <h3>{product.name}</h3>
        <p>{product.description}</p>
        <p>${(product.priceCents / 100).toFixed(2)}</p>
        <button onClick={onAdd}>Add to Cart</button>
      </div>
    );
  }
  