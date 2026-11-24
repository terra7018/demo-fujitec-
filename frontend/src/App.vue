<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { listItems, listMovements, registerMovement } from './api'
import { validateMovement } from './validation'

const items = ref([])
const movements = ref([])
const message = ref(null)
const errors = ref([])
const saving = ref(false)
const form = reactive({ type: 'IN', itemCode: '', quantity: '', reason: '', userName: '倉庫 太郎' })

const selectedItem = computed(() => items.value.find((i) => i.code === form.itemCode))
const fmt = (n) => Number(n).toLocaleString('ja-JP', { maximumFractionDigits: 3 })
const fmtTime = (t) => new Date(t).toLocaleString('ja-JP', { dateStyle: 'short', timeStyle: 'short' })
const itemName = (code) => items.value.find((i) => i.code === code)?.description ?? code

async function reload() {
  ;[items.value, movements.value] = await Promise.all([listItems(), listMovements()])
}

async function submit() {
  message.value = null
  errors.value = validateMovement(form, selectedItem.value)
  if (errors.value.length) return
  saving.value = true
  try {
    const res = await registerMovement({ ...form, quantity: Number(form.quantity) })
    message.value = `${form.type === 'IN' ? '入庫' : '出庫'}を登録しました（${itemName(res.itemCode)}：在庫 ${fmt(res.stockAfter)}${selectedItem.value.unit}）`
    form.quantity = ''
    form.reason = ''
    await reload()
  } catch (e) {
    errors.value = [e.message]
  } finally {
    saving.value = false
  }
}

onMounted(reload)
</script>

<template>
  <header>
    <h1>在庫管理</h1>
    <span class="sub">入庫・出庫（Delphi「Entrada / Saída de Material」からの移行版）</span>
  </header>

  <main>
    <section class="card">
      <h2>入出庫登録</h2>
      <form @submit.prevent="submit" data-testid="movement-form">
        <div class="toggle">
          <label :class="{ active: form.type === 'IN' }"><input type="radio" value="IN" v-model="form.type" />入庫</label>
          <label :class="{ active: form.type === 'OUT' }"><input type="radio" value="OUT" v-model="form.type" />出庫</label>
        </div>
        <label>品目
          <select v-model="form.itemCode" data-testid="item-select">
            <option value="" disabled>選択してください</option>
            <option v-for="i in items" :key="i.code" :value="i.code">{{ i.code }}　{{ i.description }}</option>
          </select>
        </label>
        <p v-if="selectedItem" class="hint">現在庫：{{ fmt(selectedItem.quantity) }}{{ selectedItem.unit }}（最低在庫 {{ fmt(selectedItem.minStock) }}）</p>
        <label>数量 <input v-model="form.quantity" type="number" step="0.001" min="0" data-testid="quantity" /></label>
        <label>担当者 <input v-model="form.userName" /></label>
        <label>理由・備考 <textarea v-model="form.reason" rows="2" /></label>
        <button type="submit" :disabled="saving">{{ form.type === 'IN' ? '入庫を登録' : '出庫を登録' }}</button>
        <ul v-if="errors.length" class="errors" data-testid="errors"><li v-for="e in errors" :key="e">{{ e }}</li></ul>
        <p v-if="message" class="ok" data-testid="message">{{ message }}</p>
      </form>
    </section>

    <section class="card wide">
      <h2>在庫一覧</h2>
      <table data-testid="items">
        <thead><tr><th>品目コード</th><th>品名</th><th class="num">在庫数</th><th class="num">最低在庫</th><th>状態</th></tr></thead>
        <tbody>
          <tr v-for="i in items" :key="i.code" :class="{ low: i.belowMinimum }">
            <td>{{ i.code }}</td><td>{{ i.description }}</td>
            <td class="num">{{ fmt(i.quantity) }} {{ i.unit }}</td><td class="num">{{ fmt(i.minStock) }}</td>
            <td><span v-if="i.belowMinimum" class="badge">要発注</span></td>
          </tr>
        </tbody>
      </table>

      <h2>入出庫履歴（最新100件）</h2>
      <table data-testid="movements">
        <thead><tr><th>日時</th><th>区分</th><th>品目</th><th class="num">数量</th><th>担当者</th><th>理由</th></tr></thead>
        <tbody>
          <tr v-for="m in movements" :key="m.id">
            <td>{{ fmtTime(m.movedAt) }}</td>
            <td><span :class="['kind', m.type]">{{ m.type === 'IN' ? '入庫' : '出庫' }}</span></td>
            <td>{{ itemName(m.itemCode) }}</td><td class="num">{{ fmt(m.quantity) }}</td>
            <td>{{ m.userName }}</td><td>{{ m.reason }}</td>
          </tr>
          <tr v-if="!movements.length"><td colspan="6" class="empty">まだ履歴はありません</td></tr>
        </tbody>
      </table>
    </section>
  </main>
</template>
