export async function post(path: string): Promise<void> {
  const res = await fetch(`/api${path}`, { method: "POST" });
  if (!res.ok) throw new Error(await res.text());
}

export async function getJson<T>(path: string): Promise<T> {
  const res = await fetch(`/api${path}`);
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}
