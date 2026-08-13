# 05 — Interview Notes

> Este documento será atualizado ao longo da modernização. O objetivo é garantir que o desenvolvedor consiga explicar o projeto sem depender da IA.

## 1. Pitch de 30 segundos

**Pendente.**

Estrutura futura:

> "A Clínica Mackito nasceu como [...]. O sistema [...]. Depois de [...], revisitei o projeto e [...]. Meu foco na modernização foi [...]."

## 2. O que existia na versão acadêmica

- [ ] Confirmar funcionalidades.
- [ ] Confirmar arquitetura.
- [ ] Confirmar participação/autoria sem exagerar.
- [ ] Confirmar tecnologias realmente usadas.

## 3. O que foi melhorado posteriormente

| Melhoria | Problema anterior | Minha decisão | Como implementei | Como validei |
|---|---|---|---|---|
| Configuração segura | JWT e MySQL possuíam valores literais | Exigir configuração externa sem fallback inseguro | Placeholders em `application.properties` e injeção do JWT no `TokenService` | Testes JWT e busca por valores antigos |
| Estratégia inicial de banco/testes | A aplicação dependia de MySQL até para carregar contexto | Preservar MySQL no runtime e usar H2 somente em testes | Profile `test` com H2 em memória | Teste de contexto Spring/JPA |
| Proteção de dados | Listagens de pacientes e atendimentos eram públicas | Manter públicos somente cadastro e login nesta etapa | Matriz inicial em `SecurityConfig` | MockMvc confirma bloqueio anônimo |
| Validação e erros | Entradas inválidas e recursos ausentes podiam gerar respostas inconsistentes/500 | Criar contrato pequeno de erro e validar somente regras confirmadas | Bean Validation, `ApiError`, handler global e exceção de recurso ausente | Quatro testes MockMvc validam 400 e 404 |
| Perfis e identidade | Todo usuário tinha o mesmo acesso e o cliente escolhia `idUsuario` do atendimento | Aplicar menor privilégio e derivar o registrador da autenticação | Enum de perfil, authorities, claim JWT, DTO seguro e matriz no Spring Security | 15 testes, incluindo permissões e cadastro sem escalação |
| Schema e unicidade | Hibernate alterava o banco implicitamente e cadastros podiam duplicar identificadores | Versionar schema e proteger nas camadas de aplicação/banco | Flyway V1, `ddl-auto=validate`, checks e constraints únicas | 16 testes; e-mail duplicado retorna 409 |
| Contrato manual | Não havia coleção reproduzível para demonstrar a API | Versionar exemplos sem credenciais e manter Maven como validação principal | Coleção/ambiente Postman com scripts e variáveis vazias | JSON validado e fluxos alinhados aos endpoints atuais |
| Primeiro administrador | Cadastro público não pode escolher perfil, mas um banco novo precisa de admin | Bootstrap opt-in sem senha padrão e endpoint interno protegido | Variáveis temporárias, BCrypt e `POST /usuarios` exclusivo de ADMIN | 22 testes, incluindo bootstrap e bloqueio da recepção |

## 4. Fluxos que preciso explicar de ponta a ponta

### Autenticação/JWT
- [ ] Consigo explicar onde o token é criado.
- [ ] Consigo explicar como é validado.
- [ ] Consigo explicar como Spring Security entra no fluxo.
- [ ] Consigo explicar senha/hash.
- [ ] Consigo explicar endpoints públicos e protegidos.

### Requisição → Controller → Service → Repository
- [ ] Consigo escolher um endpoint real e percorrer o fluxo completo.

### Persistência/JPA
- [ ] Consigo explicar as principais entidades.
- [ ] Consigo explicar relacionamentos.
- [ ] Consigo explicar o banco usado localmente.

### Validações e erros
- [ ] Consigo explicar o que ocorre com entrada inválida.
- [ ] Consigo explicar exceptions e status HTTP.

### Testes
- [x] Consigo explicar o que cada teste inicial garante: contexto, JWT e bloqueio anônimo.

## 5. Perguntas que um tech lead pode fazer

1. Por que você decidiu revisitar esse projeto?
2. O que você mudaria na versão acadêmica e por quê?
3. Por que Spring Boot?
4. Como a autenticação funciona?
5. Por que JWT?
6. Quais riscos de segurança você encontrou?
7. Como os dados chegam até o banco?
8. Onde ficam as regras de negócio?
9. O que você testou?
10. Qual foi a melhoria de maior impacto?
11. O que você deliberadamente decidiu NÃO implementar?
12. O que faria se o projeto precisasse atender usuários reais?
13. O que foi feito pelo grupo originalmente?
14. O que você alterou depois?
15. Como você utilizou Codex/IA?
16. Que decisões foram suas e como você validou as sugestões da IA?

## 6. Como explicar o uso de IA

Mensagem-base:

> Usei o Codex como ferramenta de análise e implementação assistida dentro de um processo Spec-as-Code. Primeiro documentei o estado atual e defini o backlog. As mudanças eram feitas em escopo pequeno, revisadas e testadas. Meu objetivo não era pedir para a IA "refazer o projeto", mas usá-la para acelerar tarefas enquanto eu mantinha as decisões, prioridades e entendimento técnico.

Não decorar literalmente. Adaptar ao que realmente foi feito.

## 7. Vocabulário/conceitos que preciso estudar

| Conceito encontrado | Consigo explicar? | Revisar |
|---|---:|---|
| Spring Boot | ⬜ | |
| Injeção de dependência | ⬜ | |
| Controller/Service/Repository | ⬜ | |
| JPA/Hibernate | ⬜ | |
| Spring Security | ⬜ | |
| JWT | ⬜ | |
| Bean Validation | ⬜ | |
| DTO | ⬜ | |
| HTTP status | ⬜ | |
| Testes unitários/integração | ⬜ | |

## 8. Limitações conhecidas

- Roles de recepção, médico e administrador ainda não foram modeladas.
- A identidade associada ao atendimento ainda vem do body e será revista junto com autorização/ownership.
- Validações e respostas de erro ainda não estão uniformes.
- H2 dá feedback rápido, mas não comprova toda compatibilidade específica com MySQL.
- O front-end acadêmico não foi preservado e uma nova interface ainda será projetada.
- O catálogo público de serviços e o assistente “Doutor Rogério” são requisitos futuros, não funcionalidades existentes.

Ser capaz de falar sobre limitações sem vergonha é parte da defesa técnica do projeto.
