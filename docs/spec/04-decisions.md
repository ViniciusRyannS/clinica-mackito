# 04 — Technical Decision Log

Este arquivo registra decisões técnicas relevantes tomadas durante a modernização.

Não é necessário criar uma ADR para cada mudança pequena. Registre apenas decisões que um entrevistador ou outro desenvolvedor poderia perguntar: "por que vocês fizeram assim?".

---

## ADR-000 — Processo de modernização incremental

**Status:** Aceita  
**Contexto:** O projeto nasceu como trabalho acadêmico em grupo e está sendo revisitado posteriormente para estudo e portfólio.

**Decisão:** A modernização será incremental, baseada em auditoria e backlog aprovado, preservando o histórico e evitando reescrita total sem justificativa.

**Alternativas consideradas:**
1. reescrever toda a aplicação do zero;
2. manter o projeto congelado;
3. evoluir incrementalmente.

**Motivo:** A terceira alternativa demonstra melhor capacidade de leitura de código existente, priorização e refatoração, além de preservar a história real do projeto.

**Consequências:**
- algumas estruturas legadas podem permanecer;
- mudanças serão menores e mais explicáveis;
- o histórico acadêmico continua visível;
- evolução pode ser discutida em entrevistas.

---

## ADR-001 — MySQL no runtime e H2 nos testes iniciais

**Status:** Aceita  
**Contexto:** A aplicação acadêmica já estava configurada para MySQL, mas dependia de credenciais literais e não iniciava sem um servidor local. H2 já existia no `pom.xml`, porém não era configurado. A modernização precisa preservar uma tecnologia relevante de produção e, ao mesmo tempo, oferecer testes rápidos e reproduzíveis.

**Opções consideradas:**
1. usar H2 em todos os ambientes;
2. usar MySQL também em cada teste local;
3. manter MySQL no runtime e usar H2 somente no profile `test`, acrescentando posteriormente testes de compatibilidade com MySQL quando necessário.

**Decisão:** Manter MySQL como banco da aplicação e configurar H2 em memória exclusivamente no profile `test`. URL, usuário e senha do MySQL passam a ser obrigatoriamente externos. Nesta primeira etapa, Hibernate cria e remove o schema de teste. Testcontainers/MySQL fica como evolução posterior, caso a compatibilidade específica do banco precise ser comprovada.

**Motivo:** MySQL preserva a stack e o aprendizado do projeto; H2 reduz a barreira para executar testes unitários/de integração no Maven. A combinação é proporcional ao portfólio e não exige Docker no primeiro ciclo.

**Consequências:**
- executar a aplicação exige `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `JWT_SECRET`;
- `mvnw test` não depende de MySQL local nem de credenciais reais;
- diferenças entre H2 e MySQL continuam possíveis e deverão ser cobertas seletivamente antes de mudanças relevantes de schema/query;
- nenhuma credencial operacional ou segredo JWT possui fallback versionado.

**Evidências/arquivos relacionados:**  
- `src/main/resources/application.properties`
- `src/test/resources/application-test.properties`
- `src/test/java/com/mackito/clinica/ClinicaMackitoApplicationTests.java`

---

## ADR-002 — Estratégia inicial de testes

**Status:** Aceita  
**Contexto:** O único teste acadêmico estava integralmente comentado. O projeto já possuía `spring-boot-starter-test`, que inclui JUnit 5, AssertJ, Mockito e infraestrutura Spring Test.

**Opções consideradas:**
1. adicionar outro framework de testes;
2. iniciar com a stack oficial já disponível;
3. testar apenas manualmente no Postman.

**Decisão:** Usar JUnit 5 para testes Java, Spring Boot Test para contexto, MockMvc para contratos HTTP/segurança e H2 no profile de testes. Postman permanece útil para exploração e demonstração manual, mas o Maven será a validação reproduzível.

**Motivo:** Evita dependência desnecessária, integra-se naturalmente ao Spring Boot e permite explicar níveis de teste em entrevistas.

**Consequências:**
- nenhuma extensão de editor é obrigatória;
- testes de contexto são mais lentos que testes unitários e devem ser usados seletivamente;
- a suíte inicial protege carregamento, JWT e bloqueio anônimo de dados sensíveis;
- próximos ciclos devem acrescentar validação, erros, autenticação e autorização por perfil.

**Evidências/arquivos relacionados:**  
- `pom.xml`
- `src/test/java/com/mackito/clinica/service/TokenServiceTest.java`
- `src/test/java/com/mackito/clinica/security/SecurityConfigIntegrationTest.java`

---

## ADR-003 — Perfis, cadastro público e identidade do atendimento

**Status:** Aceita

**Contexto:** A versão acadêmica autenticava usuários sem authorities, permitia que o cliente informasse `idUsuario` ao criar atendimento e não vinculava uma conta aos cadastros de paciente ou médico. A evolução confirmou os perfis `ADMIN`, `RECEPCAO`, `MEDICO` e `PACIENTE`, com a recepção responsável também por horários.

**Opções consideradas:**
1. manter todos os usuários com o mesmo acesso;
2. aceitar o perfil enviado no cadastro público;
3. adotar roles com menor privilégio, cadastro público fixo como paciente e vínculos opcionais e únicos da conta com paciente/médico.

**Decisão:** Adotar `PerfilUsuario` persistido como texto. O cadastro público cria exclusivamente `PACIENTE`, independentemente de campos extras enviados. Contas internas serão criadas em fluxo administrativo posterior. `Usuario` poderá possuir vínculo um-para-um com `Paciente` ou `Medico`. O usuário registrador do atendimento será obtido do contexto autenticado, e não do body.

**Matriz inicial:**

| Recurso | ADMIN | RECEPCAO | MEDICO | PACIENTE |
|---|---|---|---|---|
| Pacientes, médicos e atendimentos administrativos | permitido | permitido | negado | negado |
| Dados/agenda próprios | futuro endpoint com ownership | n/a | planejado | planejado |
| Cadastro público | n/a | n/a | n/a | cria conta PACIENTE |

**Motivo:** Evita escalação de privilégio e exposição cruzada enquanto os endpoints de ownership ainda não existem. É preferível negar temporariamente a médicos/pacientes do que liberar coleções completas.

**Consequências:**
- JWT passa a carregar o claim informativo `perfil`, mas a autorização usa as authorities recarregadas do banco;
- alterações de perfil têm efeito sem esperar o token expirar;
- migrations precisarão atribuir perfil seguro aos registros existentes e criar os vínculos;
- endpoints próprios de médico/paciente serão um ciclo separado.

**Evidências/arquivos relacionados:**
- `src/main/java/com/mackito/clinica/model/PerfilUsuario.java`
- `src/main/java/com/mackito/clinica/model/Usuario.java`
- `src/main/java/com/mackito/clinica/config/SecurityConfig.java`
- `src/main/java/com/mackito/clinica/service/AtendimentoService.java`

---

## ADR-004 — Flyway como fonte do schema MySQL

**Status:** Aceita

**Contexto:** `ddl-auto=update` alterava o schema implicitamente e não deixava histórico. O database acadêmico original não foi preservado, portanto não há dados locais conhecidos a migrar.

**Opções consideradas:**
1. continuar com `ddl-auto=update`;
2. manter um arquivo SQL manual sem controle de execução;
3. usar Flyway com migrations versionadas e Hibernate em modo `validate`.

**Decisão:** Flyway passa a criar e evoluir o MySQL a partir de `V1__criar_schema_inicial.sql`. O runtime usa `ddl-auto=validate`. O profile `test` mantém Flyway desligado e Hibernate `create-drop` sobre H2, até ser acrescentado um teste específico de migrations/MySQL.

**Motivo:** A evolução do schema fica auditável, repetível e explicável sem adicionar infraestrutura complexa.

**Consequências:**
- a migration V1 pressupõe database vazio;
- schema legado, se reaparecer, exigirá backup e estratégia própria, sem `baseline-on-migrate` automático;
- CPF, e-mail de paciente, e-mail de conta e CRM são únicos no banco;
- diferenças MySQL/H2 ainda exigem validação específica futura.

**Evidências/arquivos relacionados:**
- `pom.xml`
- `src/main/resources/application.properties`
- `src/main/resources/db/migration/V1__criar_schema_inicial.sql`

---

## Modelo para próximas decisões

### ADR-XXX — Título

**Status:** Proposta | Aceita | Substituída | Rejeitada

**Contexto:**  
Qual problema ou decisão existe?

**Opções consideradas:**
1. ...
2. ...

**Decisão:**  
O que foi escolhido?

**Motivo:**  
Por quê?

**Consequências:**  
Quais trade-offs essa decisão cria?

**Evidências/arquivos relacionados:**  
- `caminho/do/arquivo`
