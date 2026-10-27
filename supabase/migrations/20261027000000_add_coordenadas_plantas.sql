-- Coordenadas usadas pelo mapa e pelo seletor de local do cadastro.
alter table public.plantas
  add column if not exists latitude double precision,
  add column if not exists longitude double precision;

-- Faz a API (PostgREST) enxergar as novas colunas sem esperar o recarregamento do cache.
notify pgrst, 'reload schema';
