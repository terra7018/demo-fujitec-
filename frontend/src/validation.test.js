import { describe, expect, it } from 'vitest'
import { validateMovement } from './validation'

const item = { code: 'A', quantity: 5, unit: '個' }
const base = { type: 'OUT', itemCode: 'A', quantity: '3', userName: '担当' }

describe('validateMovement', () => {
  it('正しい入力ではエラーなし', () => {
    expect(validateMovement(base, item)).toEqual([])
  })
  it('在庫を超える出庫はエラー', () => {
    expect(validateMovement({ ...base, quantity: '6' }, item)).toEqual(['在庫が不足しています（在庫 5個）'])
  })
  it('入庫は在庫数に関係なく登録できる', () => {
    expect(validateMovement({ ...base, type: 'IN', quantity: '100' }, item)).toEqual([])
  })
  it('数量0・品目未選択・担当者なしはエラー', () => {
    expect(validateMovement({ type: 'IN', itemCode: '', quantity: '0', userName: ' ' }, undefined)).toHaveLength(3)
  })
})
