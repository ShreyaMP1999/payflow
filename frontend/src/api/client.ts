const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export function getToken(): string | null {
  return localStorage.getItem("payflow_token");
}

export async function api<T>(path: string, opts: RequestInit = {}): Promise<T> {
  const headers = new Headers(opts.headers);
  if (!headers.has("Content-Type")) headers.set("Content-Type", "application/json");
  const token = getToken();
  if (token) headers.set("Authorization", `Bearer ${token}`);

  const res = await fetch(`${API_BASE}${path}`, {
    ...opts,
    headers,
  });

  if (!res.ok) {
    const text = await res.text();
    let message = text || `Request failed: ${res.status}`;
    try {
      const error = JSON.parse(text);
      if (typeof error.message === "string") message = error.message;
    } catch {
      // Non-JSON errors can come from proxies or the security filters.
    }
    throw new Error(message);
  }

  return res.json() as Promise<T>;
}
