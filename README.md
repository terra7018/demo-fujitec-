# demo-fujitec

Delphi のクライアント/サーバー型業務システムを、Devin で Web アプリ（Vue.js + Java Spring Boot + PostgreSQL）に移行するデモです。

## 構成

| パス | 内容 |
|---|---|
| `legacy/` | 移行元。公開リポジトリ [MDsolucoesTI/Pegasus](https://github.com/MDsolucoesTI/Pegasus)（MIT ライセンス）の在庫管理システム。Delphi 5/6、DB は Paradox |
| `docs/` | Devin が作成した分析・移行仕様・検証結果 |
| `backend/` | Spring Boot 3（Java 17）+ PostgreSQL（Flyway） |
| `frontend/` | Vue.js 3 + Vite |

## 流れ

1. [移行元の分析](docs/01_legacy_analysis.md)：構成・依存関係・入出庫ロジック・直すべき問題
2. [移行仕様](docs/02_migration_spec.md)：要件・設計・タスク
3. 実装：`backend/`, `frontend/`
4. [検証結果](docs/03_verification.md)：旧ルールと同じ動きになるか、同時出庫で在庫がマイナスにならないか

## 起動

```bash
docker compose up -d db
cd backend && mvn spring-boot:run      # http://localhost:8080
cd frontend && npm install && npm run dev   # http://localhost:5173
```

移行元のコードは原作者の MIT ライセンス（`legacy/LICENSE`）に従って取り込んでいます。原本の README は `legacy/README.original.md` です。サンプルデータの品目は架空のものです。
