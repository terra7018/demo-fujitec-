async function request(path, options = {}) {
  const res = await fetch(path, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  const body = res.status === 204 ? null : await res.json()
  if (!res.ok) {
    throw new Error(body?.detail ?? `エラーが発生しました（${res.status}）`)
  }
  return body
}

export const listItems = () => request('/api/items')
export const listMovements = (itemCode) =>
  request(`/api/movements${itemCode ? `?itemCode=${encodeURIComponent(itemCode)}` : ''}`)
export const registerMovement = (movement) =>
  request('/api/movements', { method: 'POST', body: JSON.stringify(movement) })
