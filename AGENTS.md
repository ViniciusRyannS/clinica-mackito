# AGENTS.md — Clínica Mackito

## 1. Objetivo do trabalho

Este repositório é um projeto acadêmico legado que está sendo revisitado para fins de aprendizado, portfólio e preparação para entrevistas técnicas.

O objetivo NÃO é reescrever o sistema do zero nem transformá-lo artificialmente em uma arquitetura de nível sênior.

O objetivo é:
- compreender o estado atual;
- identificar problemas reais;
- preservar o que funciona;
- corrigir riscos e débitos técnicos relevantes;
- melhorar qualidade, segurança, testes e documentação;
- evoluir o projeto de forma incremental;
- manter uma história de evolução técnica que possa ser explicada pelo desenvolvedor em entrevistas.

## 2. Contexto de autoria

A Clínica Mackito foi originalmente desenvolvida como Projeto Integrador em grupo durante a graduação.

Regras obrigatórias:
- nunca atribua ao mantenedor atual trabalho original que não possa ser comprovado;
- nunca apague ou reescreva o histórico para esconder a origem acadêmica/coletiva;
- diferencie claramente "versão acadêmica original" de "melhorias posteriores";
- não invente requisitos, experiências profissionais, usuários reais, métricas de produção ou resultados de negócio;
- preserve créditos e autoria existentes no histórico Git.

## 3. Fonte de verdade

Antes de qualquer tarefa relevante:
1. leia este `AGENTS.md`;
2. leia os arquivos em `docs/spec/`;
3. inspecione o código relacionado à tarefa;
4. não trate suposições como fatos.

Se código e documentação divergirem:
- registre a divergência;
- considere o código executável como evidência do estado atual;
- não altere silenciosamente a documentação ou o comportamento sem explicar.

## 4. Fluxo Spec-as-Code

O trabalho deve seguir estas fases:

1. Diagnóstico
2. Proposta
3. Aprovação humana
4. Implementação pequena
5. Validação/testes
6. Atualização da documentação
7. Commit pequeno e explicável

Durante tarefas explicitamente marcadas como AUDITORIA ou DIAGNÓSTICO:
- NÃO altere código de produção;
- NÃO altere regras de negócio;
- NÃO adicione dependências;
- NÃO faça refatorações;
- limite mudanças aos arquivos de documentação autorizados no prompt.

Durante tarefas de IMPLEMENTAÇÃO:
- implemente somente itens já aprovados;
- não aumente o escopo por iniciativa própria;
- se descobrir outro problema, registre como proposta/backlog em vez de corrigi-lo escondido.

## 5. Princípios de engenharia

Priorize:
- simplicidade;
- legibilidade;
- comportamento previsível;
- responsabilidades claras;
- validação de entrada;
- tratamento consistente de erros;
- segurança básica correta;
- configuração reproduzível;
- testes dos fluxos relevantes;
- documentação compatível com o código.

Evite overengineering.

Não introduza sem necessidade comprovada:
- microserviços;
- Kafka/RabbitMQ;
- Redis;
- Kubernetes;
- arquitetura distribuída;
- CQRS/Event Sourcing;
- abstrações genéricas sem uso real;
- padrões complexos apenas para "parecer profissional";
- dependências que não resolvam um problema concreto.

Arquiteturas como Clean Architecture, Hexagonal ou DDD só devem ser propostas quando houver justificativa concreta e proporcional ao escopo.

## 6. Compatibilidade e comportamento

Por padrão:
- preserve contratos existentes;
- preserve regras de negócio conhecidas;
- evite breaking changes;
- não renomeie grandes áreas do projeto sem necessidade;
- não substitua tecnologia apenas por preferência pessoal.

Qualquer alteração de comportamento deve ser explicitamente apontada antes da implementação.

## 7. Segurança

Nunca:
- versione segredos reais;
- exponha senha, token, chave JWT ou credencial;
- crie credenciais "temporárias" inseguras e as commite;
- ignore falhas de autenticação/autorização encontradas.

Ao detectar risco de segurança:
- registre evidência;
- explique impacto;
- classifique prioridade;
- proponha correção proporcional ao projeto.

## 8. Banco de dados

Antes de alterar persistência:
- identifique qual banco/configuração é realmente usado em cada ambiente;
- verifique H2, MySQL, profiles e propriedades existentes;
- não assuma que uma dependência presente no `pom.xml` está efetivamente em uso;
- não execute operações destrutivas sem autorização explícita;
- mudanças de schema devem ser documentadas.

## 9. Testes e validação

Para cada implementação:
- identifique o comportamento esperado;
- adicione/ajuste testes quando fizer sentido;
- execute os testes relevantes;
- execute build quando possível;
- informe exatamente o que foi validado;
- nunca diga que algo "funciona" se não foi executado ou comprovado.

## 10. Dependências

Antes de adicionar uma dependência:
- explique qual problema ela resolve;
- verifique se o projeto já possui uma solução;
- prefira recursos do ecossistema Spring/Java já utilizados;
- evite dependência apenas por conveniência.

## 11. Tamanho das mudanças

Prefira mudanças pequenas e revisáveis.

Uma tarefa deve, idealmente, resolver um problema principal.

Evite:
- refatoração massiva;
- alteração de dezenas de arquivos sem necessidade;
- misturar documentação, feature, segurança e limpeza geral numa única entrega.

## 12. Documentação de decisão

Quando houver decisão técnica não trivial:
- registre em `docs/spec/04-decisions.md`;
- inclua contexto;
- alternativas consideradas;
- decisão;
- consequência;
- status.

## 13. Backlog

Problemas encontrados fora do escopo atual devem ir para:
`docs/spec/03-refactoring-backlog.md`

Classificação:
- P0 — bloqueador/segurança crítica;
- P1 — obrigatório para a versão de portfólio;
- P2 — diferencial relevante;
- P3 — melhoria futura.

## 14. Comunicação

Ao finalizar uma tarefa, informe:
1. o que encontrou;
2. o que alterou;
3. por que alterou;
4. arquivos afetados;
5. testes/comandos executados;
6. riscos ou pendências;
7. sugestão de próximo passo;
8. sugestão de mensagem de commit.

O desenvolvedor precisa conseguir explicar a mudança em uma entrevista. Portanto, use linguagem técnica clara e não esconda decisões importantes atrás de automação.
