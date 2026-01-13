import { useEffect, useMemo, useRef, useState } from "react";
import { getJson, post } from "./api";

type TradeEvent = {
  id: number;
  env: string;
  eventType: "INTENT" | "APPROVED" | "REJECTED" | "EXECUTED";
  symbol: string;
  side: string;
  amount?: number | null;
  confidence?: number | null;
  rationale?: string | null;
  reason?: string | null;
  payload?: string | null;
  createdAt: string;
};

export default function App() {
  const [events, setEvents] = useState<TradeEvent[]>([]);
  const [kill, setKill] = useState<boolean>(false);
  const [terminal, setTerminal] = useState<string[]>([]);
  const [error, setError] = useState<string>("");

  const terminalRef = useRef<HTMLDivElement | null>(null);

  async function refresh() {
    setError("");
    try {
      const [ev, ks] = await Promise.all([
        getJson<TradeEvent[]>("/events/latest"),
        getJson<boolean>("/killswitch"),
      ]);
      setEvents(ev);
      setKill(ks);
    } catch (e: any) {
      setError(e?.message ?? String(e));
    }
  }

  useEffect(() => { refresh(); }, []);

  useEffect(() => {
    const es = new EventSource("/api/stream/events");
    es.addEventListener("tradeExecuted", (evt) => {
      const line = typeof (evt as any).data === "string" ? (evt as any).data : JSON.stringify((evt as any).data);
      setTerminal((t) => [...t.slice(-200), `[EXECUTED] ${line}`]);
    });
    return () => es.close();
  }, []);

  useEffect(() => {
    terminalRef.current?.scrollTo({ top: terminalRef.current.scrollHeight });
  }, [terminal]);

  async function enableKill() {
    setError("");
    try { await post("/killswitch/enable"); await refresh(); }
    catch (e: any) { setError(e?.message ?? String(e)); }
  }

  async function disableKill() {
    setError("");
    try { await post("/killswitch/disable"); await refresh(); }
    catch (e: any) { setError(e?.message ?? String(e)); }
  }

  const latestSummary = useMemo(() => {
    const last = events[0];
    if (!last) return "No events yet (wait ~5s for the first intent).";
    return `Latest: ${last.eventType} ${last.side} ${last.symbol} amount=${last.amount ?? "-"} conf=${last.confidence ?? "-"}`;
  }, [events]);

  return (
    <div style={{ padding: 24, fontFamily: "system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif" }}>
      <h1>Crypto Trader (Agentic)</h1>
      <p style={{ maxWidth: 900 }}>
        Multi-agent pipeline: Market → Analyst (AI) → Risk (deterministic) → Executor (tool calling).
        By default, orders are NOOP.
      </p>

      <div style={{ display: "flex", gap: 12, alignItems: "center", marginBottom: 12 }}>
        <button onClick={refresh}>Refresh</button>
        <span><b>Kill switch:</b> {String(kill)}</span>
        {!kill ? <button onClick={enableKill}>Enable</button> : <button onClick={disableKill}>Disable</button>}
        <span style={{ opacity: 0.75 }}>{latestSummary}</span>
      </div>

      {error && <pre style={{ color: "crimson", whiteSpace: "pre-wrap" }}>{error}</pre>}

      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 16 }}>
        <div>
          <h2>Latest Events (DB)</h2>
          <div style={{ display: "grid", gap: 8 }}>
            {events.map(e => (
              <div key={e.id} style={{ border: "1px solid #ddd", borderRadius: 10, padding: 12 }}>
                <div><b>{e.eventType}</b> — {e.side} {e.symbol}</div>
                <div style={{ opacity: 0.8 }}>
                  amount={e.amount ?? "-"} conf={e.confidence ?? "-"} at {e.createdAt}
                </div>
                {e.rationale ? <div><b>Rationale:</b> {e.rationale}</div> : null}
                {e.reason ? <div><b>Reason:</b> {e.reason}</div> : null}
              </div>
            ))}
          </div>
        </div>

        <div>
          <h2>Terminal (SSE: tradeExecuted)</h2>
          <div ref={terminalRef} style={{ border: "1px solid #ddd", borderRadius: 10, padding: 12, height: 480, overflow: "auto", background: "#fafafa" }}>
            {terminal.length === 0 ? <div style={{ opacity: 0.7 }}>Waiting for executed events...</div> : null}
            {terminal.map((l, i) => (
              <div key={i} style={{ fontFamily: "ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace", fontSize: 12 }}>
                {l}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
