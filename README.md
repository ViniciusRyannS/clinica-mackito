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

O database precisa existir antes da primeira inicialização:

```sql
CREATE DATABASE clinica_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Executar

```powershell
.\mvnw.cmd spring-boot:run
```

A API usa a porta `8080` por padrão. O Hibernate ainda atualiza o schema com `ddl-auto=update`; migrations versionadas permanecem como melhoria planejada.

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

O Postman é indicado para explorar e demonstrar a API manualmente, mas não substitui os testes automatizados: a suíte Maven é repetível e detecta regressões sem depender de cliques ou de uma coleção local.
