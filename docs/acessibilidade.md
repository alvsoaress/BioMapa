# Acessibilidade

Revisão feita na Sprint 1. Falta o teste manual com o TalkBack: anote o resultado em cada item e acrescente o que aparecer.

## O que foi corrigido

| Onde | Correção | Por quê | TalkBack (teste manual) |
| --- | --- | --- | --- |
| Home, Auth, Nova Descoberta, Detalhe | Título principal ("Olá, Explorador!", "BioMapa", "Nova Descoberta", "Detalhes da planta") marcado com `semantics { heading() }` | O leitor de tela permite pular entre cabeçalhos; sem a marcação o usuário precisa percorrer a tela item a item | |
| Home (lista) e Detalhe | Subtítulos "Plantas mapeadas" e "Propriedades medicinais" também marcados como cabeçalho | Mesma navegação por cabeçalhos dentro da tela | |
| Auth | Ícone do logo com `contentDescription = null` (antes "Logo") | É decorativo; o nome "BioMapa" logo abaixo já identifica a tela | |
| Auth e Nova Descoberta | Ícones dentro dos campos (e-mail, senha, localização, informação) com `contentDescription = null` | Os campos já têm rótulo visível; o ícone com descrição fazia o TalkBack repetir a informação | |
| Home | Ícones dos botões "Registrar Nova Planta" e "Sair da conta" com `contentDescription = null` | O texto do botão já descreve a ação; antes era lido "Adicionar, Registrar Nova Planta" | |
| Botão de tema | Descrição passou de "Tema: Claro" para "Alternar tema. Atual: claro" | Botão só com ícone precisa dizer o que faz, não só o estado | |
| Botão Voltar (Detalhe) | Mantida a descrição "Voltar" | Botão só com ícone, descrição já era clara | |
| Home (lista de plantas) | Itens clicáveis com `role = Role.Button` e rótulo de ação "Ver detalhes" | O TalkBack anuncia que o item é acionável e o que acontece ao tocar | |
| Mapa no Android (osmdroid) | Marcadores com margem transparente até 48×48dp | O ícone padrão tem 27×48dp, abaixo do alvo mínimo de toque | |
| Mapa no desktop | Raio de toque dos marcadores de 40px para 24dp (alvo de 48×48dp) | 40px dava cerca de 20dp de alvo em telas de alta densidade | |
| Botão de tema | Saiu da sobreposição no canto e foi para o cabeçalho de cada tela | Sobreposto, cobria conteúdo e ficava fora da ordem de leitura | |

## O que já estava certo

- Botões, campos e o botão de tema usam componentes do Material 3, que garantem alvo mínimo de 48×48dp.
- Não há linhas com interruptor ou checkbox, então não foi preciso usar `toggleable` com `Role.Switch`.

## Pendências conhecidas

- Os marcadores do mapa (osmdroid no Android e Canvas no desktop) não são alcançáveis pelo TalkBack. A lista de plantas da Home é a alternativa acessível.
- Mensagens de erro do formulário de login são `Text` soltos abaixo do campo; o ideal é usar `supportingText` para o TalkBack lê-las junto com o campo.

## Roteiro do teste manual com TalkBack

1. Ative o TalkBack e abra cada tela: Login, Home, Nova Descoberta e Detalhe.
2. Navegue por cabeçalhos (gesto de leitura "Cabeçalhos") e confira se o título principal é o primeiro.
3. Percorra todos os elementos com deslize para a direita e anote qualquer ícone lido sem necessidade ou botão sem descrição.
4. Na Home, confira se cada planta da lista é anunciada como botão com a ação "Ver detalhes".
5. Anote o resultado na coluna "TalkBack" da tabela acima.
