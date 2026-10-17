# BioMapa 🌿

Plataforma colaborativa de mapeamento botânico do campus da UFRN. Estudantes cadastram plantas com foto, localização no mapa, descrição e propriedades medicinais, e consultam o que a comunidade já registrou.

Projeto da disciplina de Sistemas Móveis (2026.2), em Kotlin Multiplatform com Compose Multiplatform. O alvo principal é Android; o alvo desktop é usado para desenvolvimento rápido com Hot Reload.

## Vídeos das sprints

| Sprint | Entrega | Vídeo |
| --- | --- | --- |
| Sprint 0 | Proposta e apresentação do MVP | [Assistir no YouTube](https://youtu.be/i0fONSxesc8) |
| Sprint 1 | Telas do MVP, navegação tipada, tema e CI | [Assistir no Google Drive](https://drive.google.com/file/d/1xZqimgUg3yWvKZNFqtcjXLqjiGIVT8Mo/view?usp=sharing) |

## O que já foi entregue

**Sprint 1**
- Telas: Login, Home com mapa das plantas, Nova Descoberta (cadastro de planta) e Detalhe da planta
- Navegação com Navigation Compose e rotas tipadas (`@Serializable`), com passagem de ID para o detalhe e deep link `biomapa://planta`
- Tema Material 3 claro e escuro
- CI no GitHub Actions: compilação, ktlint e detekt

## Documentação

- [Proposta do produto](docs/proposta.md): visão, MVP, backlog e decisões técnicas
- [Uso de IA](docs/uso-de-ia.md)

## Estrutura do projeto

- [`/shared`](./shared/src): código compartilhado entre as plataformas (telas, navegação, tema e domínio)
  - [`commonMain`](./shared/src/commonMain/kotlin): código comum a todos os alvos
- [`/androidApp`](./androidApp): ponto de entrada do app Android
- [`/desktopApp`](./desktopApp): ponto de entrada do app desktop (JVM)
- [`/iosApp`](./iosApp/iosApp): ponto de entrada do app iOS

## Como rodar

- Android: `./gradlew :androidApp:assembleDebug`
- Desktop com Hot Reload: `./gradlew :desktopApp:hotRun --auto`
- Desktop padrão: `./gradlew :desktopApp:run`
- iOS: abrir a pasta [`/iosApp`](./iosApp) no Xcode e rodar por lá

## Testes e qualidade

- Testes Android: `./gradlew :shared:testAndroidHostTest`
- Testes desktop: `./gradlew :shared:jvmTest`
- Lint e análise estática: `./gradlew ktlintCheck detekt`

## Equipe

| Nome | Papel |
| --- | --- |
| Álvaro Soares dos Santos | Product Owner e desenvolvedor |
| Mário Luiz da Silva Junior | Product Owner e desenvolvedor |
| Ian Mendes Fernandes do Nascimento | Product Owner e desenvolvedor |
