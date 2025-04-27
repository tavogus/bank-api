CREATE TYPE transaction_type AS ENUM (
  'TRANSFER',
  'DEPOSIT',
  'WITHDRAWAL',
  'CREDIT_CARD',
  'DEBIT_CARD'
);

ALTER TABLE transactions
DROP CONSTRAINT transactions_type_check;

ALTER TABLE transactions
ALTER COLUMN type TYPE transaction_type
USING type::transaction_type;
