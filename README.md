# Clínica Mackito

Projeto Integrador acadêmico desenvolvido originalmente em grupo e posteriormente revisitado para estudo, portfólio e evolução técnica incremental.

## Requisitos locais

- JDK 17
- MySQL 8
- Maven Wrapper incluído no repositório

Nenhuma extensão de editor é obrigatória. No VS Code, **Extension Pack for Java** e **Test Runner for Java** são opcionais e facilitam executar/debugar testes pela interface. Os comandos Maven continuam sendo a validação reproduzível do projeto.

## Configuração segura

A aplicação não contém credenciais ou segredo JWT padrão. Antes de executá-la no PowerShell, defina valores do seu próprio ambiente:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/clinica_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:DB_USERNAME = '<seu-usuario-local>'
$env:DB_PASSWORD = '<sua-senha-local>'
$env:JWT_SECRET = '<segredo-aleatorio-com-pelo-menos-32-caracteres>'
```

Não copie valores reais para arquivos versionados. O usuário MySQL deve ter acesso somente ao database da aplicação e não deve ser `root`.

O database vazio precisa existir antes da primeira inicialização:

```sql
CREATE DATABASE clinica_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Executar

```powershell
.\mvnw.cmd spring-boot:run
```

A API usa a porta `8080` por padrão. O Flyway executa as migrations de `src/main/resources/db/migration`, e o Hibernate usa `ddl-auto=validate` para conferir o mapeamento sem alterar o schema.

Esta migration inicial foi criada para um database novo. Se existir um schema acadêmico antigo, faça backup e não aponte a aplicação modernizada diretamente para ele: a estratégia de migração dos dados deverá ser avaliada separadamente.

## Testes

```powershell
.\mvnw.cmd test
```

Os testes usam o profile `test` e um banco H2 em memória. Portanto, não exigem MySQL nem credenciais reais. A suíte inicial cobre:

- carregamento do contexto Spring/JPA;
- emissão e validação de JWT;
- rejeição de segredo JWT fraco;
- rejeição de token assinado com outro segredo;
- bloqueio anônimo das listagens de pacientes e atendimentos.
- validação HTTP de usuários, pacientes e atendimentos;
- resposta `404` uniforme para recurso inexistente.

O Postman é indicado para explorar e demonstrar a API manualmente, mas não substitui os testes automatizados: a suíte Maven é repetível e detecta regressões sem depender de cliques ou de uma coleção local.

A coleção inicial e o ambiente sem credenciais estão em [`postman/`](postman/README.md).

## Respostas de erro

Entradas inválidas retornam `400 Bad Request` com um contrato previsível:

```json
{
  "timestamp": "2026-08-13T02:12:51",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Campos inválidos",
  "path": "/pacientes",
  "campos": {
    "email": "O email deve possuir um formato válido"
  }
}
```

Recursos inexistentes retornam `404 Not Found` no mesmo formato, sem o objeto `campos`. Violações de restrições persistidas retornam `409 Conflict` sem expor detalhes internos do banco.

## Perfis e autorização

A aplicação possui quatro perfis: `ADMIN`, `RECEPCAO`, `MEDICO` e `PACIENTE`.

Nesta etapa, `ADMIN` e `RECEPCAO` podem acessar os endpoints administrativos de pacientes, médicos e atendimentos. `MEDICO` e `PACIENTE` ficam bloqueados nesses endpoints gerais até existirem rotas que garantam acesso somente aos próprios dados.

O cadastro público em `POST /auth/cadastrar` sempre cria uma conta `PACIENTE`; qualquer campo `perfil` enviado pelo cliente é ignorado. A resposta contém somente `id`, `email` e `perfil`, nunca senha ou hash.

Ao criar um atendimento, o usuário responsável é obtido da autenticação corrente. O cliente não envia mais `idUsuario`.

## Unicidade

O banco e a aplicação impedem duplicidade de:

- e-mail de conta;
- CPF de paciente;
- e-mail de paciente;
- CRM de médico.

Conflitos conhecidos retornam `409 Conflict` com mensagem segura. As constraints do banco permanecem como proteção definitiva contra requisições concorrentes.
