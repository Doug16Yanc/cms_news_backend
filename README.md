# Geld Fokus

Este projeto é um trabalho da disciplina Programação Web II. Se trata de um site de notícias com a temática financeira cujo nome significa "focar em dinheiro", onde jornalista e editor ter perimissão para postar  notícias e gerenciá-las, cada qual com limitações. Ele também faz uso da API __________ para exibir dados e gráficos em cardes laterais e o usuário ficar por dentro das mudanças na bolsa de valores.
Plataforma de gerenciamento de notícias desenvolvida com foco em desempenho, organização de código e escalabilidade.

## Funcionalidades
- O sistema permite que o usuário comum navegue pelas notícias e as leia.
- Jornalista e editor podem adicionar e gerenciar notícias.


## Regras de Negócio
- O editor pode gerenciar as notícias de todos os jornalistas.
- Um jornalista pode gerenciar apenas as suas notícas.


## Tecnologias Utilizadas

- **Backend:** Kotlin com Spring Boot  
- **Banco de Dados:** MongoDB 
- **Frontend:** Framework Angular 20  
- **Estilos:** SCSS
- **Estrutura de páginas:** HTML5


## Requisitos

Para execução do projeto, é necessário ter instalados no ambiente de desenvolvimento:

- [Node.js](https://nodejs.org/)  
- [Angular CLI](https://angular.io/cli)
- [Docker](https://www.docker.com/)
---

## Estrutura do projeto
### Frontend
```bash
    src/
    ├── app/
    │   ├── core/
    │   │   ├── @types/
    │   │   │   ├── Article
    │   │   │   ├── Category
    │   │   │   ├── Pagination
    │   │   │   ├── Stock
    │   │   │   ├── Tag
    │   │   │   ├── Toast
    │   │   │   ├── User
    │   │   │   └── UserRegister
    │   │   ├── enumeration
    │   │   ├── guards
    │   │   ├── interceptors
    │   │   └── services
    │
    │   ├── features/
    │   │   ├── layouts/
    │   │   │   ├── default-layout
    │   │   │   └── empty-layout
    │   │   └── pages/
    │   │       ├── authentication
    │   │       ├── common
    │   │       ├── editor
    │   │       ├── journalist
    │   │       └── visitor
    │
    │   ├── shared/
    │   │   ├── components/
    │   │   │   ├── footer
    │   │   │   ├── manage-navbar
    │   │   │   ├── navbar
    │   │   │   ├── news-card
    │   │   │   ├── news-carousel
    │   │   │   ├── search-news
    │   │   │   ├── stock-chart
    │   │   │   ├── stock-widget
    │   │   │   ├── stocks-sidebar
    │   │   │   ├── tab
    │   │   │   ├── title-section
    │   │   │   ├── toast
    │   │   │   ├── toast-container
    │   │   │   └── user-menu.component
    │   │   └── sections/
    │   │       ├── all-news-section
    │   │       ├── comments-section
    │   │       └── latest-news-section
    │
    │   ├── app.config.ts
    │   ├── app.html
    │   ├── app.routes.ts
    │   ├── app.scss
    │   └── app.ts
    │
    ├── assets/
    ├── _mixins.scss
    ├── index.html
    ├── main.ts
    └── styles.scss
│
├── .editorconfig
├── .gitignore
├── README.md
├── angular.json
├── eslint.config.js
├── package-lock.json
├── package.json
├── tsconfig.app.json
├── tsconfig.json
├── tsconfig.spec.json

```

### Backend

```bash
   src
    └── main
        ├── kotlin
        │   └── douglas
        │       └── cms_news_backend
        │           ├── CmsNewsBackendApplication.kt
        │           ├── config
        │           │   ├── CloudinaryConfig.kt
        │           │   ├── cors
        │           │   ├── rest
        │           │   └── security
        │           ├── controller
        │           ├── dto
        │           ├── exception
        │           │   ├── global
        │           │   └── local
        │           ├── initializer
        │           ├── mapper
        │           ├── model
        │           ├── repository
        │           ├── service
        │           └── utils
        └── resources
    └── test
        └── kotlin
            └── douglas
                └── cms_news_backend
   ├── .env
   ├── build.gradle
   ├── docker-compose.yml
   ├── Dockerfile
   ├── gradle
   ├── gradle.properties
   ├── gradlew
   ├── gradlew.bat
   ├── settings.gradle
```

## Configuração e Execução
**Certifique-se de ter instalado todas as tecnologias citadas anteriormente**

Instalar as dependências do projeto listadas no `package.json`:
```bash
npm install
```

Compilar e executar o projeto Angular localmente, abrindo no navegador:
```bash
ng s -o
```

Caso não queria abrir diretamente no navegador, basta remover o `-o` do final:
```bash
ng s
```

O banco de dados MongoDB é executado em container. Para iniciar digite:  
```bash
docker-compose up -d
```






