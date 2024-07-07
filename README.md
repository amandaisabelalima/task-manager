# Task Management API

## Descrição

Este projeto é uma API de gerenciamento de tarefas que permite aos usuários:
- Criar uma nova tarefa.
- Listar todas as tarefas.
- Atualizar uma tarefa existente.
- Deletar uma tarefa.
- Buscar tarefas por status (concluída/não concluída).

---
## Endpoints da API

### Criar Tarefa

**Endpoint:** POST /api/tasks

**Descrição:** Cria uma nova tarefa com anexos.


### Buscar Tarefas 

**Endpoint:** GET /api/tasks

**Descrição:** Busca todas as tarefas ativas.


### Buscar Tarefas por Status

**Endpoint:** GET /api/tasks/status?status={status}

**Descrição:** Busca tarefas por um status específico: COMPLETED ou NOT_COMPLETED.


### Atualizar Tarefa

**Endpoint:** PUT /api/tasks

**Descrição:** Altera os dados de uma tarefa. Possíveis alterações: título, descrição e status.


### Deletar Tarefa

**Endpoint:** DELETE /api/tasks/{id}

**Descrição:** Deleta uma tarefa com base no ID.

---

## Configuração com Docker Compose

Este projeto utiliza Docker Compose para configurar os serviços necessários, incluindo MongoDB, Mongo Express e LocalStack.

### Instruções

1 - **Clone o repositório:**

```sh
git clone https://github.com/amandaisabelalima/task-manager.git
cd task-manager
```

2 - **Inicie os serviços com Docker Compose:**

```sh
docker-compose up -d
```

3 - Para dar start na app pelo intellij, deve abrir o terminal e executar 
```sh
./gradlew bootRun
```

4 - **Para acessar a aplicação:**

- O Swagger da aplicação fica disponível em `http://localhost:8082/webjars/swagger-ui/index.html`


- O Mongo Express, para visualizar a base de dados fica disponível em `http://localhost:8081/db/tasks-manager/tasks`


- Bucket criado na S3 AWS, para visualizar todos os arquivos feitos upload fica disponível em: `http://localhost:4566/task-manager-bucket`




