-- 移行元: Paradox テーブル Estoque（品目・在庫）, Entrada/Saida（入庫・出庫の移動履歴）
CREATE TABLE item (
    code        VARCHAR(20)    PRIMARY KEY,          -- Estoque.CodMerc
    description VARCHAR(100)   NOT NULL,             -- Estoque.Descricao
    unit        VARCHAR(10)    NOT NULL,             -- Estoque.Sigla
    quantity    NUMERIC(14, 3) NOT NULL DEFAULT 0,   -- Estoque.Quantidade
    min_stock   NUMERIC(14, 3) NOT NULL DEFAULT 0,   -- Estoque.EstoqueMin
    max_stock   NUMERIC(14, 3),                      -- Estoque.EstoqueMax
    CONSTRAINT item_quantity_non_negative CHECK (quantity >= 0)
);

CREATE TABLE stock_movement (
    id         BIGSERIAL      PRIMARY KEY,           -- Entrada/Saida.CodMov
    item_code  VARCHAR(20)    NOT NULL REFERENCES item (code),
    type       CHAR(1)        NOT NULL,              -- Tipo: 'E' = 入庫, 'S' = 出庫
    quantity   NUMERIC(14, 3) NOT NULL,              -- Quant
    reason     TEXT,                                 -- Motivo
    user_name  VARCHAR(50)    NOT NULL,              -- UserName
    moved_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),-- Data + Hora
    CONSTRAINT stock_movement_type CHECK (type IN ('E', 'S')),
    CONSTRAINT stock_movement_quantity_positive CHECK (quantity > 0)
);

CREATE INDEX stock_movement_item_code_idx ON stock_movement (item_code, moved_at DESC);
