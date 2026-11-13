-- Which accounts a token subject (JWT "sub") may access. Authorization source of truth (D3).
CREATE TABLE account_access (
    subject    VARCHAR(128) NOT NULL,
    account_id VARCHAR(64)  NOT NULL,
    PRIMARY KEY (subject, account_id)
);
