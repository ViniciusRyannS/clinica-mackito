# 03 — Refactoring Backlog

## Regras

Este backlog registra melhorias propostas. **Item no backlog não significa item aprovado.** Nenhum item abaixo foi implementado nesta auditoria.

Prioridades: **P0** — bloqueador/risco crítico; **P1** — obrigatório para portfólio; **P2** — diferencial relevante; **P3** — futuro.

## Backlog proposto

| ID | Prioridade | Categoria | Problema / oportunidade | Evidência | Mudança proposta | Valor para portfólio/técnico | Risco | Status |
|---|---|---|---|---|---|---|---|---|
| RF-001 | P0 | Segurança/JWT | Segredo HMAC curto e versionado permite assinar tokens válidos | `TokenService.java:15` no baseline auditado | Externalizar segredo obrigatório, validar força, rotacionar valor e testar token válido/inválido/expirado | Demonstra gestão correta de segredo e autenticação | Alteração invalida tokens existentes; confirmar ambientes | CONCLUÍDO |
| RF-002 | P1 | Configuração/segurança | Datasource usava credenciais literais e não havia configuração reproduzível por ambiente | `application.properties:11-16` no baseline (valores omitidos); startup falhou sem MySQL | Externalizar URL/usuário/senha, usar usuário de privilégio mínimo, criar profiles local/test e documentar bootstrap | Execução clara e segura | Exige decidir banco local e destino de credenciais; rotacionar se usadas | EM_ANDAMENTO |
| RF-003 | P1 | Autorização/privacidade | Pacientes/atendimentos eram públicos e POST aceitava qualquer `idUsuario` | Baseline de `SecurityConfig`, `AtendimentoRequestDTO` e `AtendimentoService` | Proteger dados, criar roles, derivar registrador do contexto e depois adicionar ownership | Mostra autorização além de “ter JWT” | Matriz administrativa concluída; endpoints próprios ainda faltam | EM_ANDAMENTO |
| RF-004 | P1 | API/segurança | Cadastro recebia/retornava `Usuario`, incluindo hash BCrypt serializável | Baseline de `AuthController` e `Usuario` | Criar DTOs mínimos, nunca retornar senha/hash e responder 201 | Contrato seguro e explícito | Cadastro público é fixo como PACIENTE | CONCLUÍDO |
| RF-005 | P1 | Autenticação/erros | Login duplica verificação, enumera usuários e JWT inválido pode gerar 500 | `AuthController.java:43-56`; `SecurityFilter.java:40` | Centralizar autenticação no manager, uniformizar 401 e capturar falhas de token no filtro | Fluxo previsível e testável | Não vazar motivo específico; preservar clientes que esperam 403 exige decisão | PROPOSTO |
| RF-006 | P1 | Validação | Auth, médico e atendimento não tinham validação efetiva; DTOs validados de paciente/médico estavam sem uso | Baseline de `AutenticacaoDTO`, `Medico` e `AtendimentoRequestDTO` | Definir contratos reais, aplicar constraints/`@Valid` e validar IDs/data/sala/e-mails | Evita 500 e protege invariantes de entrada | Regras de unicidade continuam em RF-012 | CONCLUÍDO |
| RF-007 | P1 | Erros/API | Handler cobria apenas Bean Validation; ausências e integridade geravam respostas inconsistentes/500 | Baseline de `GlobalExceptionHandler` e services | Criar exceção focada e handler uniforme para 400/404/409 | API defensável e previsível | Novas categorias devem manter o contrato | CONCLUÍDO |
| RF-008 | P1 | Testes | Nenhum teste estava ativo; build verde não verificava comportamento | teste integralmente comentado no baseline; Surefire executava zero testes | Criar baseline de contexto com profile isolado e testes de auth, segurança, validação e fluxos CRUD prioritários | Principal evidência de evolução técnica | Suíte possui 10 testes; auth/roles/CRUD feliz ainda faltam | EM_ANDAMENTO |
| RF-009 | P1 | Execução/documentação | Não há README, bootstrap do MySQL nem exemplos de configuração/comandos | raiz do repositório; falha de startup por conexão recusada | Documentar JDK, comandos, variáveis, banco, endpoints e limitações após decisões de configuração | Recrutador consegue executar e avaliar | Documentação deve refletir apenas comportamento validado | PROPOSTO |
| RF-010 | P2 | Contrato API | Paciente/médico alternam entity e DTO; POST usa 200 e deletes têm status distintos | controllers dos três domínios | Adotar DTOs de entrada/saída por endpoint e status 201/204/404 consistentes, em pequenos ciclos | Reduz acoplamento JPA/API | Breaking change potencial para front-end ausente | PROPOSTO |
| RF-011 | P2 | Persistência | `ddl-auto=update` não oferece histórico reproduzível do schema | `application.properties:15`; ausência de migrations | Após estabilizar modelo, introduzir migrations pequenas e usar estratégia de schema adequada por profile | Reprodutibilidade e disciplina de banco | Baseline deve refletir schema real; requer acesso ao MySQL original | PROPOSTO |
| RF-012 | P2 | Domínio/dados | Não há constraints de unicidade para CPF/CRM nem decisão documentada | `Paciente`, `Medico`, schema gerado | Confirmar requisitos e, somente se aprovados, validar e criar constraints/migration | Integridade de dados baseada em regra real | Dados existentes podem conter duplicatas | PROPOSTO |
| RF-013 | P2 | CORS/configuração | CORS permite qualquer origem/método/header e usa DSL obsoleta | `SecurityConfig.java:48,63-70`; warnings do build | Configurar origens por ambiente e migrar para DSL atual | Segurança de integração e build sem API obsoleta | Precisa conhecer origem do front-end | PROPOSTO |
| RF-014 | P2 | Manutenção | Mapeamento de atendimento é repetido quatro vezes | `AtendimentoService.java:54-62,66-75,86-94,100-108` | Extrair método privado de conversão, sem camada arquitetural nova | Mudança pequena, reduz divergência | Baixo | PROPOSTO |
| RF-015 | P2 | Dependências | H2 não tem profile; dotenv e persistence API explícita aparentam redundância | `pom.xml`; nenhuma referência a dotenv; propriedades H2 comentadas | Confirmar estratégia de teste e remover somente dependências comprovadamente redundantes | Build e intenção mais claros | H2 pode ser escolhido para testes; decidir antes de remover | PROPOSTO |
| RF-016 | P3 | Código morto | `UsuarioService` e teste estão comentados; request DTOs e `SecurityUtil` sem uso; `MedicoDTO.email` nunca preenchido | arquivos/classes citados | Excluir ou integrar cada artefato após confirmar intenção, em commit isolado | Reduz ruído para leitura | Pode apagar intenção acadêmica ainda útil; preservar histórico Git | PROPOSTO |
| RF-017 | P3 | Legibilidade | Injeção por campo, indentação inconsistente e comentários óbvios/desatualizados | controllers/services/config | Migrar gradualmente para construtor e formatar apenas arquivos tocados | Facilita testes/leitura sem refatoração massiva | Não misturar com correções funcionais | PROPOSTO |
| RF-018 | P3 | Configuração/JPA | Dialeto MySQL explícito está obsoleto e `show-sql=true` é global | `application.properties:16-17`; warnings do startup | Deixar autodetecção e mover SQL logging para profile de desenvolvimento | Menos warnings/ruído | Validar contra versão MySQL escolhida | PROPOSTO |

## Contagem

- P0: 1
- P1: 8
- P2: 6
- P3: 3
- Total: 18 itens `PROPOSTO`

## Ordem sugerida para revisão humana

Esta ordem é uma proposta de discussão, não um plano aprovado:

1. Deliberar RF-001 a RF-003: segredo, ambientes e matriz de acesso/identidade.
2. Deliberar RF-004 a RF-007: contratos de autenticação, validação e erros.
3. Aprovar uma fatia mínima de RF-008 antes de mudanças comportamentais.
4. Tratar RF-009 e RF-010 em paralelo apenas quando contratos estiverem definidos.
5. Avaliar persistência e qualidade (RF-011 a RF-018) em ciclos pequenos.

## Itens deliberadamente adiados

- Microserviços, mensageria, Redis, Kubernetes, CQRS/Event Sourcing, DDD/Clean/Hexagonal completos: não resolvem evidências prioritárias desta auditoria.
- Roles complexas: aguardar confirmação dos perfis reais; autenticação atual não prova requisito de RBAC.
- Regras clínicas de agenda, CPF/CRM e cobrança: aguardar decisão humana; o código atual não comprova esses requisitos.
- Front-end novo: primeiro localizar/confirmar o front-end acadêmico mencionado e estabilizar contratos da API.
