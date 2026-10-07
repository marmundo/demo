-- Este script roda a cada inicialização. Como o H2 é em memória, a tabela começa vazia
-- a cada execução; o MERGE (em vez de INSERT) também evita duplicar se o banco for persistente.
-- Carga inicial das prioridades. MERGE ... KEY(nome) é idempotente (H2).
MERGE INTO prioridades (nome) KEY (nome) VALUES ('BAIXA');
MERGE INTO prioridades (nome) KEY (nome) VALUES ('MEDIA');
MERGE INTO prioridades (nome) KEY (nome) VALUES ('ALTA');
