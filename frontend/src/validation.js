export function validateMovement(form, item) {
  const errors = []
  if (!form.itemCode) errors.push('品目を選択してください')
  if (!form.userName?.trim()) errors.push('担当者を入力してください')
  const qty = Number(form.quantity)
  if (!form.quantity || Number.isNaN(qty) || qty <= 0) {
    errors.push('数量は0より大きい値を入力してください')
  } else if (form.type === 'OUT' && item && qty > Number(item.quantity)) {
    errors.push(`在庫が不足しています（在庫 ${Number(item.quantity)}${item.unit}）`)
  }
  return errors
}
