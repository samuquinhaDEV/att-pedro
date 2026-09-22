# Diagrama C4 Nível 2 (Contêineres)

Detalhamento da estrutura interna do sistema, separando a aplicação em uma interface web, uma API responsável pelo processamento e um banco de dados para armazenar as informações.

```mermaid
C4Container

title Diagrama C4 Nível 2 - Contêineres

Person(vendedor, "Vendedor", "Realiza pedidos de venda.")

Person(admin, "Administrador", "Administra os produtos.")

Person(estoque, "Equipe de Estoque", "Acompanha e atualiza o estoque.")

System_Boundary(sistema_vendas_boundary, "Sistema de Vendas e Estoque") {

    Container(frontend, "Aplicação Web (SPA)", "React + Vite", "Interface utilizada pelos funcionários para acessar e realizar as operações do sistema.")

    Container(api, "API do Sistema", "Node / Spring Boot", "Recebe as solicitações, processa as operações e controla as regras de vendas e estoque.")

    ContainerDb(database, "Banco de Dados", "MySQL 8", "Guarda as informações dos produtos, vendas realizadas e quantidade disponível no estoque.")

}

Rel(vendedor, frontend, "Utiliza pelo navegador", "HTTPS")

Rel(admin, frontend, "Utiliza pelo navegador", "HTTPS")

Rel(estoque, frontend, "Utiliza pelo navegador", "HTTPS")

Rel(frontend, api, "Envia requisições", "JSON/HTTPS")

Rel(api, database, "Consulta e armazena informações", "TCP/IP")
```
