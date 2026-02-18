# 📌 TaskList - Fullstack Application

Projeto desenvolvido como parte de um teste técnico, demonstrando arquitetura limpa, boas práticas de desenvolvimento backend e frontend, autenticação segura com JWT e organização modular escalável.

---

# 🏗 Visão Geral da Arquitetura

A aplicação foi construída seguindo princípios de **Clean Architecture (Arquitetura Limpa)** e **Ports & Adapters (Arquitetura Hexagonal)** no backend, promovendo alta coesão, baixo acoplamento e testabilidade.

## 🔹 Backend

A estrutura é dividida em três camadas principais:

### 1️⃣ Application (Regra de Negócio)

Responsável por conter:

* Entidades de domínio
* Interfaces de entrada (use cases)
* Interfaces de saída (ports)
* Serviços com regras de negócio

Essa camada não depende de frameworks externos.

### 2️⃣ Adapters

* Controllers REST (input)
* Implementações de repositórios (output)
* Entidades JPA
* DTOs de request/response

Aqui ocorre a adaptação entre o mundo externo (HTTP, banco de dados) e o domínio.

### 3️⃣ Config

* Configuração de segurança (JWT)
* Filtros de autenticação
* Configuração de CORS
* Tratamento global de exceções
* Swagger/OpenAPI

---

## 🔹 Frontend

Frontend desenvolvido com Vue + Vuetify, estruturado de forma modular e orientado a componentes.

Principais características:

* Autenticação com JWT
* Gerenciamento de listas e tasks
* Comunicação com backend via API REST
* Organização por views, components e services

---

# 🧰 Stack Tecnológica

## 🔹 Backend

* **Java 17** – Versão LTS moderna e estável
* **Spring Boot** – Framework robusto para APIs REST
* **Spring Security** – Implementação de autenticação e autorização
* **JWT (JSON Web Token)** – Autenticação stateless
* **JPA / Hibernate** – Persistência ORM
* **PostgreSQL** – Banco relacional robusto e amplamente utilizado
* **JUnit + Mockito** – Testes unitários e mocks
* **Jacoco** – Análise de cobertura de testes
* **Swagger/OpenAPI** – Documentação automática da API
* **Docker / Docker Compose** – Padronização do ambiente

### Justificativa das Escolhas

* Spring Boot acelera desenvolvimento mantendo organização.
* JWT permite autenticação stateless escalável.
* PostgreSQL garante confiabilidade e compatibilidade com produção.
* Clean Architecture facilita manutenção e evolução.

---

## 🔹 Frontend

* **Vue 3** – Framework progressivo e reativo
* **Vuetify** – Biblioteca UI baseada em Material Design
* **Axios** – Cliente HTTP para integração com API
* **TypeScript**

### Justificativa

* Vue oferece curva de aprendizado suave e boa organização.
* Vuetify acelera desenvolvimento visual.
* Axios simplifica comunicação HTTP.

---

# 🚀 Como Rodar Localmente

## 🔹 Pré-requisitos

* Java 17+
* Node 18+
* Docker e Docker Compose
* Maven

---

## 🔹 Backend

### 1️⃣ Clonar o repositório

```bash
git clone <repo-url>
cd backend
```

### 2️⃣ Subir banco com Docker

```bash
docker-compose up -d
```

### 3️⃣ Rodar aplicação

```bash
mvn clean install
mvn spring-boot:run
```

A API estará disponível em:

```
http://localhost:8080
```

Swagger:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔹 Frontend

```bash
cd frontend
npm install
npm run dev
```

A aplicação estará disponível em:

```
http://localhost:5173
```

---

# 🧪 Como Rodar os Testes

## 🔹 Backend

```bash
mvn test
```

Para gerar relatório de cobertura:

```bash
mvn clean verify
```

Relatório Jacoco:

```
target/site/jacoco/index.html
```

---

## 🔹 Frontend (se aplicável)

```bash
npm run test
```

---

# 📂 Estrutura de Pastas Detalhada

## Backend

```
src/main/java
 ├── application
 │   ├── core
 │   │   ├── domains
 │   │   └── services
 │   ├── ports
 │   │   ├── input
 │   │   └── output
 │   └── usecases
 │
 ├── adapters
 │   ├── input
 │   │   ├── controllers
 │   │   └── dtos
 │   └── output
 │       ├── entities
 │       └── repositories
 │
 └── config
```

## Frontend

```
src
 ├── components
 ├── views
 ├── services
 ├── router
 └── assets
```

---

# 🧠 Decisões Técnicas Aprofundadas

## 1️⃣ Arquitetura Hexagonal

Permite independência do framework, facilitando testes unitários e manutenção.

## 2️⃣ Separação entre Domínio e Persistência

As entidades de domínio não dependem de JPA, promovendo isolamento da regra de negócio.

## 3️⃣ Autenticação Stateless

JWT foi escolhido para permitir escalabilidade horizontal sem necessidade de sessão no servidor.

## 4️⃣ Refresh Token

Implementado para melhorar segurança e experiência do usuário.

## 5️⃣ Testabilidade

Uso de portas e mocks permite testes isolados de regra de negócio.

---

# 📈 Melhorias e Roadmap

## 🔹 Backend

* Implementar testes de integração
* Adicionar Testcontainers
* Implementar Domain Events
* Adicionar observabilidade (Micrometer + Prometheus)
* Implementar cache (Redis)
* CI/CD pipeline

## 🔹 Frontend

* Implementar gerenciamento global de estado (Pinia)
* Melhorar UX com estados de loading padronizados

## 🔹 Arquitetura

* Evoluir para arquitetura orientada a eventos
* Implementar versionamento de API
* Preparar para microsserviços

---

# 🎯 Considerações Finais

Este projeto demonstra:

* Aplicação de princípios de Clean Architecture
* Separação clara de responsabilidades
* Implementação segura de autenticação
* Organização modular e testável
* Preparação para escalabilidade

