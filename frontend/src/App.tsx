import { useEffect, useState } from "react";
import { getJson, postJson } from "./api";

type TradeEvent = {
  id: number;
  env: string;
  symbol: string;
  decision: string;
  riskAllowed: boolean;
  reason?: string | null;
  createdAt: string;
};

export default function App() {
  const [events, setEvents] = useState<TradeEvent[]>([]);
  const [error, setError] = useState<string>("");

  async function refresh() {
    setError("");
    try {
      setEvents(await getJson<TradeEvent[]>("/agent/logs"));
    } catch (e: any) {
      setError(e?.message ?? String(e));
    }
  }

  useEffect(() => { refresh(); }, []);

  async function start() {
    setError("");
    try {
      await postJson("/agent/start", {});
      await refresh();
    } catch (e: any) {
      setError(e?.message ?? String(e));
    }
  }

  async function stop() {
    setError("");
    try {
      await postJson("/agent/stop", {});
      await refresh();
    } catch (e: any) {
      setError(e?.message ?? String(e));
    }
  }

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif" }}>
      <h1>Crypto Trader (Starter)</h1>
      <p style={{ maxWidth: 760 }}>
        This starter runs a scheduled agent loop and stores events in Postgres (Flyway + JPA).
        Trading is disabled by default (only HOLD decisions are allowed) until you implement risk limits + real execution.
      </p>

      <div style={{ display: "flex", gap: 12, marginBottom: 16 }}>
        <button onClick={start}>Start Agent</button>
        <button onClick={stop}>Stop Agent</button>
        <button onClick={refresh}>Refresh</button>
      </div>

      {error && (
        <pre style={{ color: "crimson", whiteSpace: "pre-wrap" }}>{error}</pre>
      )}

      <h2>Trade Events (latest 200)</h2>
      <div style={{ display: "grid", gap: 8 }}>
        {events.map(e => (
          <div key={e.id} style={{ border: "1px solid #ddd", borderRadius: 8, padding: 12 }}>
            <div><b>ID:</b> {e.id} &nbsp; <b>Env:</b> {e.env} &nbsp; <b>Symbol:</b> {e.symbol}</div>
            <div><b>Decision:</b> {e.decision} &nbsp; <b>Allowed:</b> {String(e.riskAllowed)}</div>
            {e.reason ? <div><b>Reason:</b> {e.reason}</div> : null}
            <div style={{ opacity: 0.8 }}><b>Time:</b> {e.createdAt}</div>
          </div>
        ))}
        {events.length === 0 && <div style={{ opacity: 0.7 }}>No events yet. Start the agent to create events.</div>}
      </div>
    </div>
  );
}
