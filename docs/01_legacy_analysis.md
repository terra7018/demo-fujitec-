# 1. 移行元システムの分析（Pegasus 在庫管理）

Devin が `legacy/pegasus/` の Delphi コードを読んでまとめた分析です。

## 1.1 全体像

| 項目 | 内容 |
|---|---|
| 言語・フレームワーク | Delphi 5/6（VCL）、RX / LMD / Jedi などのサードパーティ部品 |
| DB | Paradox（BDE の `TTable` で直接アクセス） |
| 構成 | 2 層のクライアント/サーバー型。画面（Form）がデータモジュール `dmdados` のテーブルを直接操作する |
| 規模 | Pascal 28 ユニット、約 11,800 行（うちデータモジュール `UnitDmdados.pas` が約 2,100 行） |
| 画面定義 | `.dfm` はリポジトリに含まれていない。画面イメージは `legacy/TelasImg/` |

## 1.2 機能とユニットの対応

| 業務 | 主なユニット |
|---|---|
| 品目マスタ（登録・検索） | `unitCadMaterial`, `UnitLocProd`, `unitCadGrupo`, `unitCadSubgrupo`, `unitCadMarca`, `unitCadUnidade` |
| 仕入先マスタ | `UnitCadFornec`, `UnitLocFornec` |
| **入庫・出庫** | **`unitEntradaMat`, `unitSaidaMat`** ← 今回の移行対象 |
| 発注・支払い | `unitCompras`, `unitPagaPedido` |
| 帳票（在庫・発注・ラベル） | `UnitRelEstoque`, `unitRelCompras`, `unitRelEtiqueta`, `UnitQR*`（QuickReport） |
| 共通 | `unitPrincipal`（メニュー）, `UnitDmdados`（全テーブル定義）, `unitParametro`, `UnitCadSenha` |

依存関係は、すべての画面が `unitDmDados`（データアクセス）と `unitPrincipal`（メニュー）に依存する「ハブ型」です。業務ロジックは画面のボタンイベントの中に直接書かれています。

## 1.3 入庫・出庫のロジック（移行対象）

| ルール | 旧実装の場所 |
|---|---|
| 品目コードは必須。未登録コードはエラー | `dbCodMercExit`（両画面） |
| 入庫：履歴に区分 `E` で保存し、在庫数に加算 | `TFrmEntradaMat.BtnGravarClick` |
| 出庫：履歴に区分 `S` で保存し、在庫数から減算 | `TFrmSaidaMat.BtnGravarClick` |
| 出庫数が在庫数を超えたら警告し、在庫数に置き換える | `TFrmSaidaMat.dbQuantExit` |
| 登録日に当日を自動設定 | `BtnNovoClick` |
| 最低在庫を下回る品目を強調 | `unitCadMaterial`, `UnitDmdados`（在庫帳票） |

使用テーブル：`Estoque`（品目・在庫数）、`Entrada` / `Saida`（入出庫の履歴。項目は CodMov, CodMerc, Tipo, Quant, Motivo, Data, Hora, UserName）。

## 1.4 移行時に直すべき問題

1. **在庫更新がトランザクションになっていない**：履歴の `Post` と在庫数の `Post` が別々に確定します。途中で失敗すると、履歴と在庫数が合わなくなります。
2. **在庫チェックが画面の入力欄の離脱イベントだけ**：`dbQuantExit` を通らずに保存できるうえ、保存時には再チェックしません。
3. **同時更新に弱い**：2 台の端末が同時に出庫すると、両方が古い在庫数を読んで更新するため、在庫を超えて出庫できてしまいます。
4. **数量が浮動小数点（`TFloatField`）**：端数の誤差が出ます。
5. **ロジックが画面と一体**：テストが書けず、Web 化の際に再利用できません。
