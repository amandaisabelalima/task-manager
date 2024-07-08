# Task Management API

## Descrição

Essa aplicação é uma API de gerenciamento de tarefas que permite aos usuários:
- Criar uma nova tarefa.
- Listar todas as tarefas.
- Atualizar uma tarefa existente.
- Deletar uma tarefa.
- Buscar tarefas por status (concluída/não concluída).

---

## Endpoints da API

```markdown
POST /api/tasks - Criar uma tarefa
GET /api/tasks - Buscar Tarefas
GET /api/tasks/status - Buscar Tarefas por Status
PUT /api/tasks - Atualizar Tarefa
DELETE /api/tasks/{id} - Deletar Tarefa
```

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

----


## Observações sobre os entregáveis

- A API com todos os endpoints está completa e funcional


- Os arquivos anexados em uma tarefa são salvos na AWS S3 e na listagem das tarefas é exibido os links para download


- O fluxo de autenticação e autorização está todo comentado porque estava dando alguns problemas que acabou levando muito tempo e com isso ficou pendente de finalização


- Pelo tempo gasto no problema da autenticação os testes também ficaram incompletos

