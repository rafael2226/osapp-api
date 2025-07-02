# OSAPP-API: Sistema de Gestão de Ordens de Serviço

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![Linguagem](https://img.shields.io/badge/linguagem-Java%2017-blue)
![Framework](https://img.shields.io/badge/framework-Spring%20Boot%203-green)

---

## 📝 Sobre o Projeto

**OSAPP-API** é uma API RESTful completa desenvolvida em Java com Spring Boot para o gerenciamento de um sistema de Ordens de Serviço (OS). O projeto foi desenhado para simular as operações de uma pequena empresa de serviço, cobrindo todo o ciclo de vida de um serviço: desde o cadastro de clientes e funcionários, passando pela criação e gestão de serviços, até o faturamento final.


---

## ✨ Funcionalidades Principais

* **Gestão de Perfis e Acesso:**
    * [x] Cadastro de usuários com separação de papéis (Cliente, Funcionário, Admin).
    * [x] Fluxo de cadastro em 2 etapas: criação da conta de acesso e posterior preenchimento do perfil.
    * [x] Modelo preparado para integração com Spring Security (`UserDetails`).

* **Gestão de Ordens de Serviço:**
    * [x] CRUD completo para Ordens de Serviço.
    * [x] Lógica de negócio para cálculo automático de valores.
    * [x] Gerenciamento de itens aninhados (adicionar, atualizar, remover).
    * [x] Ações de negócio como mudança de status e clonagem de OS.
    * [x] Sistema de busca avançada e paginada com múltiplos filtros (por cliente, funcionário, data, etc.).

* **Gestão de Faturamento:**
    * [x] Geração de faturas a partir de uma ou mais Ordens de Serviço concluídas.
    * [x] Ações de negócio para registrar pagamentos e cancelar faturas.
    * [x] Preservação do histórico, sem deleção física de registros financeiros.

* **Cadastros Base:**
    * [x] CRUD completo para Clientes, Funcionários, Endereços e um Catálogo de Serviços.
    * [x] Modelo de precificação flexível no catálogo de serviços.

---

## 🛠️ Arquitetura e Tecnologias

O projeto foi construído utilizando as seguintes tecnologias e padrões de arquitetura:

* **Tecnologias:**
    * **Java 17**
    * **Spring Boot 3**
    * **Spring Data JPA** com Hibernate
    * **Spring Validation** para validação de dados de entrada
    * **PostgreSQL** como banco de dados
    * **Maven** para gerenciamento de dependências
    * **Lombok** para redução de código boilerplate

* **Arquitetura:**
    * **API RESTful** com arquitetura em camadas (Controller, Service, Repository).
    * Modelo de dados de **"Conta de Acesso e Perfil"**: Separação clara entre dados de autenticação (`Usuario`) e dados pessoais (`Perfil`), usando Composição em vez de Herança complexa.
    * Uso do padrão **DTO (Data Transfer Object)** para comunicação com a API, com um DTO único por entidade e classes aninhadas para respostas resumidas, evitando referências circulares.
    * Tratamento de exceções centralizado com **`@RestControllerAdvice`** para fornecer mensagens de erro amigáveis e padronizadas.
    * Buscas avançadas e dinâmicas com **JPA Specifications**.

---

## 🚀 Coleção do Postman

Para facilitar os testes e a exploração da API, uma coleção completa do Postman foi criada. Ela está organizada por fluxo de negócio e inclui testes para cenários de sucesso e de erro.

**[ 🔗 Clique aqui para acessar a Coleção do Postman Online ](https://rafael-4827870.postman.co/workspace/Rafael's-Workspace~e6137b0b-f094-49f4-8ca0-16fdb4176e7d/collection/46194672-744c46be-2b29-4c41-86e3-5c826bfe3dde?action=share&creator=46194672&active-environment=46194672-6b2e6dd1-98d1-44fd-92ac-d43a258f5cdb)**



---

## ▶️ Como Executar o Projeto

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/rafael2226/osapp-api.git](https://github.com/rafael2226/osapp-api.git)
    ```
2.  **Configure o Banco de Dados:**
    * Abra o arquivo `src/main/resources/application.properties`.
    * Altere as propriedades `spring.datasource.url`, `spring.datasource.username` e `spring.datasource.password` com as credenciais do seu banco de dados PostgreSQL.

3.  **Execute a Aplicação:**
    * Utilize sua IDE (como o IntelliJ ou Eclipse) para rodar a classe principal `OsappApplication`.
    * Ou, pela linha de comando, na raiz do projeto, execute:
        ```bash
        mvn spring-boot:run
        ```
4.  **Acesse a API:**
    * A API estará disponível em `http://localhost:5050`.
    * Você pode acessar o endpoint raiz para verificar se está funcionando: `http://localhost:5050/api/v1/`

---

## ✒️ Autor

**Rafael Leal**