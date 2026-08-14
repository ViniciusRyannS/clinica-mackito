# 01 — Current State Audit

> Status: **AUDITORIA INICIAL CONCLUÍDA EM 2026-08-09**
>
> Escopo: análise estática de todo o repositório, execução de testes/build e tentativa controlada de inicialização. Nenhum endpoint foi exercitado porque não havia MySQL disponível no ambiente.

## 1. Resumo executivo

A versão local é uma API REST monolítica em Java 17 e Spring Boot 3.4.5 para cadastro de usuários, pacientes, médicos e atendimentos. O código segue, em geral, o fluxo controller → service → repository → JPA, com autenticação stateless por JWT. O banco efetivamente configurado é MySQL; H2 está no classpath, mas sua configuração está comentada e não há profile de teste. O projeto compila e gera um JAR executável, porém a inicialização local não terminou sem um MySQL em `localhost:3306/clinica_db`. Não há testes ativos: o único arquivo de teste está integralmente comentado. Também não há HTML, CSS ou JavaScript nesta cópia. Os riscos mais relevantes são o segredo JWT fixo no código, credenciais MySQL versionadas, exposição pública de dados e criação de atendimento em nome de qualquer usuário, respostas que expõem entidades e tratamento incompleto de entradas e exceções.

## 2. Estrutura do repositório

- `pom.xml`: build Maven, Java 17, Spring Boot 3.4.5 e dependências.
- `mvnw`, `mvnw.cmd`, `.mvn/wrapper/`: Maven Wrapper 3.3.2, distribuição Maven 3.9.9.
- `src/main/java/com/mackito/clinica/`:
  - `ClinicaMackitoApplication`: ponto de entrada e component scan.
  - `config`: cadeia do Spring Security, CORS, encoder e `AuthenticationManager`.
  - `controller`: quatro controllers REST (`Auth`, `Paciente`, `Medico`, `Atendimento`).
  - `service`: regras simples de CRUD, montagem de DTOs e emissão/validação de JWT.
  - `repository`: quatro interfaces Spring Data JPA.
  - `model`: quatro entities e sete DTOs.
  - `security`: filtro JWT, `UserDetailsService` e utilitário do usuário corrente.
  - `exception`: handler apenas para falhas de Bean Validation.
- `src/main/resources/application.properties`: única configuração de runtime.
- `src/test/java`: um arquivo integralmente comentado; não há testes executáveis.
- `docs/spec`: documentação Spec-as-Code adicionada posteriormente e ainda não rastreada pelo Git no momento da auditoria.
- `target`: artefatos gerados, ignorados pelo Git.

Não existem `src/main/resources/static`, `templates`, `src/main/webapp`, migrations SQL, Docker/Compose, README, CI ou coleção Postman no estado local. O histórico possui sete commits acadêmicos; os créditos de nove integrantes permanecem em `SecurityConfig.java:17-28`.

## 3. Stack efetivamente utilizada

| Tecnologia/dependência | Versão declarada/resolvida | Onde é usada | Evidência | Observação |
|---|---|---|---|---|
| Java | 17 | Todo o projeto | `pom.xml` (`java.version`); build com Java 17.0.12 | Requisito local confirmado |
| Spring Boot | 3.4.5 | Bootstrap e autoconfiguração | parent do `pom.xml`; `ClinicaMackitoApplication` | JAR executável gerado |
| Spring Web | gerenciada pelo Boot | API REST/Tomcat | controllers; `spring-boot-starter-web` | Porta padrão 8080 |
| Spring Data JPA / Hibernate | Boot 3.4.5 / Hibernate 6.6.13.Final | Persistência | repositories e entities | Quatro repositories detectados no startup |
| Spring Security | gerenciada pelo Boot | JWT, rotas e BCrypt | `SecurityConfig`, pacote `security`, `AuthController` | Sem roles; authorities vazias |
| java-jwt | 4.4.0 | Criação e validação de JWT HMAC256 | `TokenService` | Segredo fixo no código |
| Bean Validation API | versão gerenciada | Anotações em `Paciente` e DTOs não usados | `Paciente`, `PacienteRequestDTO`, `MedicoRequestDTO` | Foi declarada a API diretamente; validação efetiva é parcial |
| MySQL Connector/J | 8.0.33 | Driver do datasource ativo | `pom.xml`, URL MySQL em `application.properties` | Startup tentou conexão MySQL |
| H2 | versão gerenciada, runtime | Não usado no estado atual | dependência no `pom.xml`; propriedades H2 comentadas | Não existe profile H2/teste |
| java-dotenv | 5.2.2 | Nenhum uso encontrado | busca no código sem referências | Dependência aparentemente abandonada |
| Spring Boot Test | gerenciada pelo Boot, test | Classpath de teste | `pom.xml` | Nenhum teste ativo |
| Jakarta Persistence API | 3.1.0 explícita | Anotações JPA | models | Redundante com o starter JPA |

## 4. Como a aplicação inicia

- Classe principal: `com.mackito.clinica.ClinicaMackitoApplication`.
- Porta: `8080`, padrão do Tomcat; não há `server.port` configurada.
- Profile/configuração: nenhum profile ativo; somente `application.properties` e profile `default`.
- Pré-requisitos observados: JDK 17; MySQL acessível em `localhost:3306`; database `clinica_db`; credenciais configuradas; acesso inicial à internet para baixar Maven/dependências.
- Testes: `./mvnw.cmd test` no Windows; `./mvnw test` em ambiente Unix.
- Build: `./mvnw.cmd package`.
- Execução esperada: `./mvnw.cmd spring-boot:run` ou `java -jar target/clinica-0.0.1-SNAPSHOT.jar`.
- Tentativa executada: `java -jar target/clinica-0.0.1-SNAPSHOT.jar`; confirmou Java 17, profile default, quatro repositories e Tomcat 8080, mas não concluiu em 20 segundos. O processo foi encerrado pela auditoria após `Communications link failure`/`Connection refused` ao MySQL local.

## 5. Configuração e ambientes

### `application.properties` / profiles

`src/main/resources/application.properties:11-17` ativa exclusivamente MySQL, com URL local, usuário `root`, senha literal, `ddl-auto=update`, dialeto `MySQL8Dialect` e SQL no log. As linhas 1-8 contêm uma alternativa H2 e log de Security comentados. Não há `application-test.properties`, variáveis interpoladas (`${...}`), `.env`, configuração externa documentada ou profile separado.

O startup alertou que `MySQL8Dialect` é obsoleto e desnecessário no Hibernate atual. A saída também apresentou logging DEBUG do Spring durante a tentativa; não há propriedade ativa no arquivo que explique esse nível, portanto a origem externa ao repositório ficou em aberto.

### Variáveis de ambiente detectadas

| Variável | Finalidade | Obrigatória? | Possui valor inseguro versionado? |
|---|---|---:|---:|
| Nenhuma | O código não consulta variáveis de ambiente | Não | Os valores equivalentes de datasource e JWT estão literais no repositório |

## 6. Banco de dados

- Banco configurado e efetivamente selecionado: MySQL em `jdbc:mysql://localhost:3306/clinica_db`; confirmado pela configuração e tentativa de conexão do Hikari/MySQL Connector.
- H2: dependência runtime presente, mas propriedades comentadas; não é selecionado em nenhum contexto encontrado.
- Estratégia de schema: Hibernate `spring.jpa.hibernate.ddl-auto=update`. Não há migrations ou schema versionado; o database precisa existir antes do startup.
- Entities/tabelas: `Paciente` (nome implícito), `Medico` (nome implícito), `Usuario` → `usuarios`, `Atendimento` → `atendimentos`.
- Relacionamentos: `Atendimento` possui três `@ManyToOne(fetch = LAZY)` obrigatórios para paciente, médico e usuário (`id_paciente`, `id_medico`, `id_usuario`). Não há coleções inversas nem cascades declarados.
- Restrições explícitas: e-mail de usuário único/não nulo; senha de usuário não nula; três FKs e data do atendimento não nulas. CPF, CRM e e-mails de paciente/médico não possuem unicidade de banco.
- Riscos: credencial administrativa literal; reprodução depende de MySQL preparado manualmente; schema mutável sem histórico; `ddl-auto=update` pode mascarar divergências; ausência de profile isolado impede teste de contexto independente; deleções podem falhar por FKs sem tratamento.

## 7. Modelo de domínio

| Entidade/conceito | Responsabilidade | Relacionamentos | Regras encontradas |
|---|---|---|---|
| `Usuario` | Credencial de autenticação e `UserDetails` | Referenciado por atendimento | E-mail único; senha persistida em BCrypt no cadastro; conta sempre ativa; nenhuma role |
| `Paciente` | Cadastro com nome, CPF, e-mail e telefone | Referenciado por atendimento | Nome 2–100; CPF exatamente 11 caracteres; campos obrigatórios; e-mail não valida formato |
| `Medico` | Cadastro com nome, CRM e especialidade | Referenciado por atendimento | Nenhuma validação na entity realmente recebida pelos endpoints |
| `Atendimento` | Vincula paciente, médico, usuário, data e sala | Muitos-para-um para os três cadastros | Ao criar, os três IDs devem existir; não há regra de data, conflito de agenda ou sala |

Não foram encontradas outras regras clínicas. `PacienteRequestDTO` e `MedicoRequestDTO` descrevem validações/campos adicionais, mas nenhum controller ou service os utiliza; portanto não representam o contrato efetivo.

## 8. Endpoints/API

Autenticação abaixo significa o que `SecurityConfig.java:41-45` efetivamente exige. Códigos de erro 500 são resultados prováveis de exceções sem handler, não contratos intencionais.

| Método | Rota | Controller | Autenticação | Entrada | Saída / códigos observáveis | Regra principal |
|---|---|---|---|---|---|---|
| POST | `/auth/cadastrar` | `AuthController.cadastrarUsuario` | Pública | Entity `Usuario` (`email`, `senha`) | 200 com entity `Usuario`; 500 provável em duplicidade/nulos | Aplica BCrypt e salva |
| POST | `/auth/login` | `AuthController.autenticar` | Pública | `AutenticacaoDTO` | 200 `TokenDTO`; 403 com texto para usuário/senha inválidos; 500 provável para entrada inválida | Consulta e-mail, compara BCrypt, autentica novamente e emite JWT de 2h |
| GET | `/pacientes` | `PacienteController.listarTodos` | Pública | — | 200 com lista de entities | Lista todos, incluindo CPF/e-mail/telefone |
| GET | `/pacientes/{id}` | `PacienteController.buscarPorId` | JWT | Path `id` | 200 `PacienteDTO`; 404 | Busca por ID |
| POST | `/pacientes` | `PacienteController.criarPaciente` | Pública | Entity `Paciente`, `@Valid` | 200 com entity; 400 por validação | Salva cadastro |
| PUT | `/pacientes/{id}` | `PacienteController.atualizarPaciente` | JWT | Path `id` + entity `Paciente`, `@Valid` | 200 com entity; 400; 500 se ausente | Substitui quatro campos |
| DELETE | `/pacientes/{id}` | `PacienteController.deletarPaciente` | JWT | Path `id` | 200 vazio; erro não padronizado possível | Exclui por ID |
| GET | `/medicos` | `MedicoController.listarTodos` | Pública | — | 200 com lista de entities | Lista todos |
| GET | `/medicos/{id}` | `MedicoController.buscarPorId` | JWT | Path `id` | 200 `MedicoDTO`; 404 | Busca por ID |
| POST | `/medicos` | `MedicoController.criarMedico` | JWT | Entity `Medico`, `@Valid` sem constraints | 200 com entity | Salva cadastro |
| PUT | `/medicos/{id}` | `MedicoController.atualizarMedico` | JWT | Path `id` + entity `Medico` | 200 com entity; 500 provável se ausente | Substitui nome, CRM e especialidade |
| DELETE | `/medicos/{id}` | `MedicoController.deletarMedico` | JWT | Path `id` | 200 vazio; erro não padronizado possível | Exclui por ID |
| POST | `/atendimentos` | `AtendimentoController.criar` | Pública | `AtendimentoRequestDTO` sem `@Valid` | 200 `AtendimentoDTO`; 500 se referência ausente/nula | Aceita inclusive `idUsuario` informado pelo cliente |
| GET | `/atendimentos` | `AtendimentoController.listarTodos` | Pública | — | 200 lista de DTOs | Lista todos os atendimentos |
| DELETE | `/atendimentos/{id}` | `AtendimentoController.cancelar` | JWT | Path `id` | 204; erro não padronizado possível | Exclui por ID |
| GET | `/atendimentos/paciente/{id}` | `AtendimentoController.listarPorPaciente` | Pública | Path `id` | 200 lista de DTOs, inclusive vazia | Filtra por paciente |
| GET | `/atendimentos/medico/{id}` | `AtendimentoController.listarPorMedico` | JWT | Path `id` | 200 lista de DTOs, inclusive vazia | Filtra por médico |

Inconsistências: POSTs retornam 200 em vez de 201; deletes de paciente/médico retornam 200 enquanto atendimento retorna 204; listas retornam entities em dois domínios e DTOs em outro; não há `Location`, paginação ou contrato uniforme de erro. Não foram encontrados endpoints duplicados, mas as políticas de acesso a coleções e itens individuais são inconsistentes.

## 9. Fluxos principais e arquitetura atual

### 9.1 Autenticação

1. `POST /auth/cadastrar` recebe diretamente `Usuario`, aplica `BCryptPasswordEncoder` e salva via `UsuarioRepository`.
2. `POST /auth/login` consulta e-mail e compara senha no controller; depois chama também o `AuthenticationManager`, que usa `UsuarioDetailsServiceImpl` e BCrypt.
3. `TokenService` cria JWT HMAC256 com subject igual ao e-mail e expiração de duas horas, usando segredo fixo e offset `-03:00`.
4. Em requests fora de `/auth`, `SecurityFilter` extrai `Bearer`, valida o token, recarrega o usuário e popula o `SecurityContext`.
5. `SecurityConfig` decide entre `permitAll` e `authenticated`; não há autorização por role/ownership.

### 9.2 Pacientes e médicos

Os controllers delegam CRUD simples aos services, que usam `JpaRepository`. Busca individual converte para DTO no controller; listagem/criação/atualização expõem a entity. Atualizações carregam o registro e copiam campos. Não há verificação de duplicidade de CPF/CRM/e-mail nem regras além das constraints de `Paciente`.

### 9.3 Atendimentos

`AtendimentoService.salvar` consulta separadamente médico, paciente e usuário; se qualquer referência faltar lança `RuntimeException`. Monta a entity, salva e converte para DTO. As três listagens repetem manualmente o mesmo mapeamento. O usuário responsável vem de `idUsuario` do body, embora exista `SecurityUtil` capaz de consultar o usuário autenticado; esse utilitário não é usado.

### 9.4 Fluxo arquitetural

O fluxo predominante é `Request → SecurityFilter/SecurityConfig → Controller → Service → JpaRepository → MySQL`. A separação existe, mas `AuthController` concentra consulta, verificação de senha, autenticação, hash e persistência. Controllers também escolhem conversões e expõem entities, enquanto services misturam regra e mapeamento de DTO. A estrutura é suficiente para o tamanho atual e não justifica uma reescrita arquitetural completa.

## 10. Segurança

| ID | Severidade | Evidência | Impacto | Correção sugerida |
|---|---|---|---|---|
| SEC-001 | Crítica | No baseline auditado, `TokenService.java:15` continha um segredo JWT literal (valor omitido) | Qualquer pessoa com o código poderia assinar tokens aceitos pela aplicação | Externalizar, exigir segredo forte por ambiente e rotacionar o valor comprometido |
| SEC-002 | Alta | No baseline, `application.properties:12-13` continha usuário privilegiado e senha literais (valores omitidos) | Credencial versionada e uso de usuário privilegiado; risco maior se reutilizada fora do local | Remover/rotacionar, usar variáveis e usuário de privilégio mínimo |
| SEC-003 | Alta | `SecurityConfig.java:42-44`; `AtendimentoRequestDTO.idUsuario` | Cliente anônimo pode criar atendimento atribuído a qualquer usuário; listas públicas expõem dados pessoais e agenda | Rever matriz pública/protegida e obter usuário do contexto autenticado |
| SEC-004 | Alta | `AuthController.cadastrarUsuario` retorna `Usuario`; `Usuario.getSenha()` é serializável | Resposta expõe hash BCrypt e contrato acopla credencial à entity | Usar DTO de entrada/saída sem senha e status apropriado |
| SEC-005 | Média | `SecurityFilter.java:40` não captura exceção de JWT inválido/expirado | Token malformado pode virar erro 500 em vez de 401 consistente | Tratar falha no filtro e responder 401 sem detalhes sensíveis |
| SEC-006 | Média | `SecurityConfig.java:65-67` permite qualquer origem, método e header | Política CORS é mais ampla que o necessário e não diferencia ambiente | Restringir às origens/métodos realmente usados |
| SEC-007 | Média | Login distingue “usuário não encontrado” de “senha inválida” | Facilita enumeração de contas | Usar mensagem/status uniforme e fluxo único do AuthenticationManager |
| SEC-008 | Baixa | `Usuario.getAuthorities()` sempre vazio e não há roles | Qualquer usuário autenticado possui o mesmo acesso | Manter somente se for decisão de escopo; documentar ou criar perfis apenas se houver requisito |

CSRF está desabilitado, coerente com uma API stateless por Bearer token, desde que autenticação não migre para cookie. BCrypt é um ponto positivo. Não há segredo por variável nem evidência de TLS/configuração de proxy neste repositório.

## 11. Tratamento de erros

- Exception customizada: nenhuma.
- Handler global: `GlobalExceptionHandler` trata somente `MethodArgumentNotValidException` e retorna timestamp, status, mensagem e mapa de campos.
- Ausências: `RuntimeException`, `NoSuchElementException`, falhas de integridade/duplicidade, deleção inexistente, autenticação e JWT não possuem contrato global.
- Efeito: diferentes fluxos podem produzir 400, 403, 404, 500, corpo textual, mapa customizado ou corpo padrão do Spring.
- `AtendimentoService.salvar` agrega três ausências em uma única mensagem e retorna 500; updates ausentes também tendem a 500.

## 12. Validação

- `Paciente` possui `@NotBlank` e `@Size`; os POST/PUT usam `@Valid`, portanto essas constraints são efetivas.
- E-mail de paciente só é não vazio; não possui `@Email`. CPF verifica comprimento, não dígitos nem validade.
- `Medico` não possui constraints. O `@Valid` do controller não valida nenhum campo.
- `Usuario` e `AutenticacaoDTO` não possuem constraints; seus `@Valid` também não produzem validação de campos.
- `AtendimentoRequestDTO` não possui constraints e nem `@Valid`; IDs, data e sala podem chegar nulos. A data também pode estar no passado.
- `PacienteRequestDTO` e `MedicoRequestDTO` têm constraints mais completas, porém são código não utilizado. O DTO de médico contém telefone, e-mail, senha e valor da consulta que nem existem em `Medico`, sinal de contrato abandonado.
- Não há validação explícita de unicidade de CPF, CRM ou e-mails de paciente/médico.

## 13. Testes

- Arquivo existente: `src/test/java/com/mackito/clinica/ClinicaMackitoApplicationTests.java`.
- Estado: todas as linhas, inclusive package, classe e `contextLoads`, estão comentadas.
- Tipos de teste ativos: nenhum unitário, integração, MVC, repository ou segurança.
- `./mvnw.cmd test`: `BUILD SUCCESS`, compila 31 fontes e o arquivo de teste comentado, mas Surefire não executa testes. Sucesso de build não valida comportamento.
- Cobertura: não há plugin/ferramenta; nenhum percentual foi inferido.
- Lacunas principais: cadastro/login/JWT; matriz de autorização; CRUD e erros; validação; regras e consultas de atendimento; persistência/mapeamentos; carregamento do contexto.

## 14. Front-end / assets

Não existem arquivos `.html`, `.css` ou `.js`, nem diretórios `static`, `templates` ou `webapp` no repositório local ou na lista de arquivos rastreados. O startup confirmou que não encontrou document roots. Portanto, a descrição prévia de front-end diverge desta versão clonada; não é possível confirmar páginas nem comunicação com o back-end. Pode ter existido fora deste repositório/branch, mas isso exige confirmação humana.

## 15. Build e qualidade

- Primeira tentativa de `./mvnw.cmd test`: falhou porque o sandbox impediu download do parent POM; não foi falha do projeto. Após autorização de rede: `BUILD SUCCESS` em 12,776 s, zero testes ativos.
- `./mvnw.cmd package`: `BUILD SUCCESS` em 13,789 s; gerou `target/clinica-0.0.1-SNAPSHOT.jar`.
- Warnings: uso de `http.cors().and()` obsoleto em `SecurityConfig.java:48`; `MySQL8Dialect` obsoleto/desnecessário no startup.
- Código morto/aparentemente abandonado: `UsuarioService.java` inteiro comentado; `PacienteRequestDTO` e `MedicoRequestDTO` sem referências; campo `MedicoDTO.email` nunca preenchido; `SecurityUtil` sem uso; teste inteiro comentado.
- Dependências aparentemente redundantes: `java-dotenv` sem uso; `jakarta.persistence-api` explícita já fornecida pelo starter JPA; H2 sem profile/configuração ativa; `spring-boot-starter` é transitivo dos demais starters.
- Duplicação: conversão `Atendimento → AtendimentoDTO` repetida quatro vezes em `AtendimentoService`; CRUD de paciente/médico é estruturalmente semelhante, mas ainda pequeno e não justifica abstração genérica.
- Nomenclatura/estilo: injeção por campo, indentação inconsistente e comentários de implementação óbvios/desatualizados. São problemas secundários diante de segurança, execução e testes.
- Versionamento: `AGENTS.md` e `docs/` estão não rastreados; `target/` é corretamente ignorado. Nenhum artefato sensível adicional foi encontrado.

## 16. Pontos fortes atuais

1. Separação reconhecível em controller/service/repository e packages pequenos, facilitando leitura e evolução incremental.
2. Uso de Spring Data `JpaRepository` e derived queries (`findByPacienteId`, `findByMedicoId`) simples e proporcional ao domínio.
3. Senhas novas passam por BCrypt, e o modelo `Usuario` integra corretamente o contrato básico `UserDetails`.
4. JWT possui subject e expiração de duas horas; o filtro recarrega o usuário antes de autenticar o request, em vez de confiar apenas em dados arbitrários do token.
5. `Atendimento` modela FKs obrigatórias e associações LAZY, e a API de atendimento usa DTO de saída para evitar serializar proxies/relacionamentos inteiros.
6. Busca individual de paciente/médico retorna 404 quando ausente, em vez de responder 200 com nulo.
7. Há um handler de Bean Validation com erros por campo, base aproveitável para padronização posterior.
8. Maven Wrapper e Java fixado em 17 tornam a cadeia de build identificável; o projeto compila com o toolchain declarado.

## 17. Débitos e riscos atuais

| ID | Categoria | Problema | Evidência | Impacto | Prioridade sugerida |
|---|---|---|---|---|---|
| CS-001 | Segurança | Segredo JWT comprometido no fonte | `TokenService.java:15` | Falsificação de autenticação | P0 |
| CS-002 | Configuração/segurança | Credenciais MySQL literais e ambiente não reproduzível | `application.properties:11-16` | Exposição e startup dependente de setup manual | P1 |
| CS-003 | Autorização/privacidade | Rotas sensíveis públicas e usuário de atendimento controlado pelo cliente | `SecurityConfig.java:42-44`; `AtendimentoRequestDTO.java:8` | Acesso a PII e atribuição indevida | P1 |
| CS-004 | Testes | Nenhum teste executável | `ClinicaMackitoApplicationTests.java` comentado | Regressões sem detecção | P1 |
| CS-005 | API/segurança | Cadastro retorna entity com hash de senha | `AuthController.java:27-30`; `Usuario.java:39-41` | Exposição desnecessária e contrato inseguro | P1 |
| CS-006 | Validação | DTOs efetivos de auth/atendimento e entity médico sem constraints | Arquivos correspondentes | Entrada inválida chega à persistência/gera 500 | P1 |
| CS-007 | Erros/API | Exceções importantes não são tratadas | `GlobalExceptionHandler` cobre só validação | Status/corpos imprevisíveis | P1 |
| CS-008 | Contrato API | Entities e status HTTP usados de forma inconsistente | Controllers de paciente/médico | Acoplamento e contrato difícil de testar | P2 |
| CS-009 | Banco | `ddl-auto=update` sem migrations | `application.properties:15`; ausência de SQL | Schema não auditável/repetível | P2 |
| CS-010 | Manutenção | Código/dependências não usados e mapeamento duplicado | DTOs request, `UsuarioService`, dotenv, `AtendimentoService` | Ruído e custo de mudança | P2/P3 |

## 18. Perguntas em aberto

1. As credenciais literais antigas e o segredo JWT já foram usados fora de ambiente local? Se sim, ambos precisam ser rotacionados, não apenas removidos do estado atual.
2. Quais rotas deveriam ser públicas por requisito acadêmico? Em especial, cadastro/listagem de pacientes e criação/listagem de atendimentos.
3. O usuário associado ao atendimento representa quem cadastrou, o paciente autenticado ou outro papel?
4. O projeto pretendia possuir perfis (administrativo, médico, recepção), ou autenticação sem roles era deliberada?
5. Onde está o front-end mencionado: outro repositório, branch ou arquivos nunca versionados?
6. Qual versão de MySQL era usada e existe um dump/schema/dados de exemplo fora do repositório?
7. CPF, CRM e e-mails de paciente/médico devem ser únicos? Quais validações são requisitos reais?
8. Os campos extras de `MedicoRequestDTO` representam requisito abandonado ou implementação incompleta?
9. A data do atendimento pode estar no passado e existe regra de conflito de agenda/sala?
10. Por que logging DEBUG apareceu no ambiente de execução embora a propriedade correspondente esteja comentada? Há variável/configuração global local?

## 19. Conclusão da auditoria

Vale preservar a estrutura monolítica simples, o fluxo em camadas, Spring Data, BCrypt, associações JPA e DTO de atendimento. Antes de ampliar funcionalidades, a proposta é corrigir o segredo JWT, tornar configuração/banco reproduzíveis, definir a matriz de acesso e identidade do atendimento, estabilizar validação/erros e criar testes para os fluxos atuais. Migrations, uniformização completa de DTOs e limpezas podem vir depois. Microserviços, mensageria ou uma arquitetura completa adicional não resolvem os riscos observados e não são recomendados para este escopo.

## 20. Evolução posterior à auditoria

Em 2026-08-12 foi concluído o primeiro ciclo incremental, sem reescrever o baseline:

- segredo JWT e credenciais de datasource passaram a ser configuração externa obrigatória;
- o segredo JWT passou a exigir pelo menos 32 caracteres;
- pacientes, médicos e atendimentos passaram a exigir autenticação; apenas login e cadastro permanecem públicos;
- MySQL foi preservado no runtime e H2 foi configurado somente no profile `test`;
- a API isolada de Bean Validation foi substituída pelo starter que fornece Hibernate Validator;
- foi criada uma suíte inicial com seis testes para contexto, JWT e bloqueio anônimo de dados sensíveis.

Ainda permanecem em aberto a autorização por roles/ownership, DTO seguro de cadastro, validações completas, tratamento uniforme de erros, migrations e catálogo público de serviços.

Em 2026-08-13 foi concluído o segundo ciclo incremental:

- entradas de autenticação, usuário, paciente, médico e atendimento receberam validações efetivas;
- CPF passou a aceitar somente 11 dígitos e e-mails passaram a validar formato;
- IDs de atendimento devem ser positivos, sala é obrigatória e data não pode estar no passado;
- foi criado um contrato uniforme de erro com status, mensagem, path e erros por campo;
- recursos ausentes em atualização/exclusão passaram a produzir `404`, e conflitos de integridade produzem `409` sem detalhes internos;
- quatro testes HTTP foram adicionados, elevando a suíte para dez testes aprovados.

Regras de unicidade de CPF/CRM, roles/ownership e DTO seguro de cadastro continuam para ciclos próprios.

Em 2026-08-13 foi iniciado o ciclo de perfis e identidade:

- foram modelados `ADMIN`, `RECEPCAO`, `MEDICO` e `PACIENTE`;
- o cadastro público passou a criar exclusivamente `PACIENTE` e retornar DTO sem senha/hash;
- `Usuario` recebeu vínculos opcionais e únicos com `Paciente` e `Medico`;
- o JWT passou a incluir o claim `perfil`, enquanto a autorização usa authorities recarregadas do banco;
- endpoints administrativos de pacientes, médicos e atendimentos passaram a aceitar somente `ADMIN` e `RECEPCAO`;
- `idUsuario` foi removido do request de atendimento e o registrador passou a vir do contexto autenticado;
- a suíte passou a 15 testes, incluindo matriz inicial de autorização e proteção contra escalação no cadastro público.

Endpoints de dados próprios para médico/paciente, criação administrativa de contas internas e migrations permanecem como próximos passos.

Ainda em 2026-08-13, o schema MySQL passou a ser versionado pelo Flyway:

- `V1__criar_schema_inicial.sql` cria as quatro tabelas, FKs, perfis e vínculos;
- o runtime passou a `ddl-auto=validate`;
- e-mail de conta, CPF/e-mail de paciente e CRM receberam constraints únicas;
- conflitos conhecidos retornam 409 antes da persistência, mantendo a constraint como garantia concorrente;
- a suíte passou a 16 testes com o cenário de e-mail de conta duplicado.

Também foi adicionada uma coleção Postman inicial, com fluxos públicos, administrativos e exemplos de erro. O ambiente versionado não contém credenciais ou tokens preenchidos. O bootstrap da primeira conta interna permanece pendente e está explicitamente documentado.

O SQL acadêmico recuperado posteriormente confirmou incompatibilidade com o schema modernizado: nomes de tabelas/colunas divergentes e inserts com senha em texto puro. O database acadêmico deve ser preservado; a demo modernizada deve usar database novo.

Foi implementado bootstrap opt-in da primeira conta `ADMIN`, sem credencial padrão, além de `POST /usuarios` exclusivo de administrador para criar `RECEPCAO`, `ADMIN` adicional e `MEDICO` vinculado. A suíte passou a 22 testes.

Na sequência, foi concluído o ownership de dados pessoais:

- pacientes completam e consultam o próprio perfil em `/me/paciente`, sem poder escolher outro e-mail;
- médicos consultam o cadastro previamente vinculado em `/me/medico`;
- pacientes e médicos consultam somente os próprios atendimentos em `/me/atendimentos`;
- endpoints administrativos completos permanecem restritos a `ADMIN` e `RECEPCAO`;
- cinco testes de integração cobrem vínculo, duplicidade, separação de dados e autorização por perfil.

Foi adotada a solicitação de agendamento com confirmação humana: o paciente escolhe médico e data preferida, acompanha o próprio pedido e não cria atendimento diretamente. `ADMIN`/`RECEPCAO` confirma com sala ou rejeita com motivo. A migration V2 versiona a nova tabela e quatro testes cobrem o fluxo e suas permissões.
