# Mc2Discord Website

## Database structure
```postgresql
CREATE TABLE upload (
    id     UUID NOT NULL PRIMARY KEY,
    config TEXT NOT NULL,
    errors TEXT NOT NULL,
    env    TEXT NOT NULL
);
```