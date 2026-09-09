# Proposta — BioMapa

## 1. Visão do produto

```
Para   alunos do curso de biologia e estudantes da UFRN em geral
Que    desejam identificar, catalogar ou descobrir informações sobre a flora do campus
O      BioMapa é uma plataforma colaborativa de mapeamento botânico
Que    permite o compartilhamento de fotos, localização exata, descrições e propriedades medicinais das plantas
Diferente de   guias genéricos de biologia, cadernos de campo físicos ou aplicativos globais de natureza
Nosso produto  oferece um mapa interativo e exclusivo da UFRN, focado na realidade do campus e alimentado pela própria comunidade acadêmica
```

**Hipótese de valor:** acreditamos que estudantes da UFRN vão cadastrar e consultar plantas do campus com regularidade, porque hoje não existe um catálogo local, colaborativo e com foco em propriedades medicinais — só guias genéricos ou cadernos de campo individuais.

**Como saberemos:** acompanharemos, a partir da Sprint 2, quantas plantas cadastradas recebem consultas de outros usuários (não só do autor do cadastro). Se o uso ficar restrito a quem cadastra, a hipótese de valor colaborativo precisa ser revista.

---

## 2. MVP

| No MVP | Fora do MVP |
| --- | --- |
| Cadastro de plantas com foto e localização no mapa | Identificação automática de plantas por Inteligência Artificial |
| Formulário para descrição e propriedades medicinais | Sistema de gamificação, ranking ou perfis de usuário complexos |
| Mapa interativo exibindo os marcadores dentro da UFRN | Funcionamento offline (sincronização posterior) |
| Listagem e busca simples das plantas cadastradas | Moderação e aprovação formal por especialistas antes da publicação |
| Autenticação básica de usuários (login/senha) | Compartilhamento em redes sociais ou chat interno |

**O que fica de fora é decisão de engenharia.** Identificação automática por IA e moderação por especialistas exigiriam integrações e processos que não cabem no prazo da disciplina; o valor do MVP está em provar que a comunidade cadastra e consulta plantas, não em automatizar ou curar esse conteúdo.

---

## 3. Backlog inicial

| Prio | História | Critérios de aceitação | Sprint |
| --- | --- | --- | --- |
| P1 | Como estudante, quero cadastrar uma planta com foto, localização e propriedades médicas para compartilhar conhecimento com a comunidade | Formulário com campos obrigatórios validados; upload de foto funcionando; salva os dados no sistema | 1 |
| P1 | Como estudante, quero visualizar um mapa interativo do campus da UFRN para encontrar onde estão as plantas cadastradas | Mapa carrega na tela principal; marcadores de plantas aparecem nas coordenadas geográficas corretas | 1 |
| P1 | Como estudante, quero clicar em um marcador de planta no mapa para ler sua descrição e propriedades | Modal ou nova tela abre ao clicar no pino; exibe foto, descrição e propriedades médicas daquela planta | 2 |
| P2 | Como estudante, quero que minha localização atual seja capturada pelo GPS para não precisar digitar as coordenadas manualmente | Permissão de localização solicitada em contexto; pino cai automaticamente no local atual do usuário | 3 |
| P2 | Como estudante, quero buscar plantas por nome ou propriedade para encontrar espécies específicas sem precisar olhar o mapa inteiro | Barra de pesquisa funcional; filtra as plantas exibidas ou mostra os resultados em uma aba de lista | 3 |

---

## 4. Decisões técnicas

### 4.1 Plataforma-alvo

**Android.** O público de referência — estudantes da UFRN — é majoritariamente usuário Android no Brasil, e a equipe não tem acesso garantido a hardware Apple para desenvolvimento e testes de iOS. Interface em Compose Multiplatform, com o alvo desktop reservado para o ciclo rápido de desenvolvimento (Compose Hot Reload).

### 4.2 Backend

**Supabase (opção A).** O MVP precisa de três capacidades imediatas: autenticação básica (login/senha), armazenamento de fotos e um modelo de dados geoespacial simples (coordenadas de cada planta). O Supabase cobre as três em uma única camada gratuita e sem cartão de crédito — PostgreSQL para os dados estruturados, storage para as fotos e autenticação pronta — sem exigir que o grupo mantenha infraestrutura própria nesta fase do curso.

> Contraexemplo do que **não** justificou a escolha: preferência pessoal por alguma ferramenta. A decisão parte do que o MVP exige (auth + storage + dados geoespaciais), não de familiaridade prévia do grupo com o serviço.

---

## 5. Entidades principais do domínio

```
Usuário ──< Planta          cadastrada por um usuário, com foto e localização
              │
              └── Propriedades medicinais   descrição livre associada à planta
```

O domínio vive em `shared/`, módulo Kotlin comum importado pelas camadas `data` e `presentation` do app.

---

## 6. Equipe

| Nome | Matrícula |
| --- | --- |
| Álvaro Soares dos Santos | 20260001348 |
| Mário Luiz da Silva Junior | 20260072930 |
| Ian Mendes Fernandes do Nascimento | 20260072869 |
