# 3. 検証結果

| テスト | 内容 | 結果 |
|---|---|---|
| `StockMovementApiTest`（6 件） | R1〜R5, R8 を API 経由で確認（PostgreSQL 16 / Testcontainers） | 合格 |
| `ConcurrentIssueTest`（1 件） | 在庫 5 の品目に 20 件同時に出庫 → 5 件だけ成功し、在庫 0・履歴 5 件（R6, R7） | 合格 |
| `validation.test.js`（4 件） | 画面の入力チェック | 合格 |

実行方法：

```bash
cd backend && mvn test          # Docker が必要（Testcontainers）
cd frontend && npm install && npm test
```
