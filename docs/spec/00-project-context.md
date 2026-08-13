# 00 — Project Context

## Projeto

**Nome atual:** Clínica Mackito  
**Repositório:** https://github.com/ViniciusRyannS/clinica-mackito  
**Tipo:** Projeto Integrador acadêmico posteriormente revisitado para evolução técnica.

## Origem

A Clínica Mackito foi desenvolvida originalmente em grupo durante a graduação em Análise e Desenvolvimento de Sistemas.

A modernização atual é uma etapa posterior de estudo e portfólio. Ela deve permanecer claramente separada da autoria da versão acadêmica original.

## Objetivo da modernização

Transformar o repositório em um case honesto de evolução técnica de um desenvolvedor júnior, mostrando capacidade de:

- compreender código existente;
- identificar débito técnico;
- priorizar melhorias;
- trabalhar com API e regras de negócio;
- melhorar segurança e validação;
- escrever testes;
- organizar configuração;
- documentar decisões;
- realizar refatorações incrementais;
- usar IA/Codex como ferramenta de engenharia sem terceirizar decisões técnicas.

## Papel esperado no portfólio

**Projeto secundário forte de Back-end Java e case de evolução/refatoração.**

A principal narrativa não deve ser "criei um sistema perfeito", mas:

> Um projeto acadêmico que foi revisitado após novas experiências de estudo e desenvolvimento, passando por diagnóstico, priorização e melhorias incrementais documentadas.

## Domínio conhecido

O sistema representa uma clínica e, pelo estado público conhecido antes da auditoria local, possui conceitos relacionados a:

- autenticação;
- pacientes;
- médicos;
- atendimentos.

A auditoria local deve confirmar todos os fluxos e regras reais.

## Tecnologias conhecidas antes da auditoria

O `pom.xml` público indica, entre outras dependências:

- Java 17;
- Spring Boot;
- Spring Web;
- Spring Data JPA;
- Spring Security;
- JWT;
- Bean Validation/Jakarta Validation;
- MySQL Connector;
- H2;
- dotenv;
- testes do ecossistema Spring Boot.

**Importante:** presença no `pom.xml` não prova uso correto ou efetivo. A auditoria deve verificar o que realmente está configurado e utilizado.

O projeto também foi descrito pelo autor como contendo HTML, CSS e JavaScript. A auditoria local deve confirmar onde estão esses arquivos, como são utilizados e se fazem parte do mesmo fluxo de execução.

## Restrições

A modernização NÃO deve:

- esconder que o projeto nasceu na faculdade;
- apagar a participação do grupo;
- inventar funcionalidades;
- inventar métricas ou usuários;
- transformar o projeto em uma solução excessivamente complexa;
- reescrever tudo do zero sem justificativa;
- substituir código apenas por preferência estética;
- introduzir tecnologias que o desenvolvedor não consiga explicar;
- transformar o projeto em microserviços ou arquitetura distribuída sem necessidade real.

## Critério de sucesso

Ao final, um recrutador deve conseguir entender o projeto rapidamente e um tech lead deve perceber que:

1. o desenvolvedor entende a estrutura;
2. as principais decisões estão documentadas;
3. o projeto pode ser executado seguindo instruções claras;
4. os fluxos principais possuem validação/testes adequados;
5. configurações sensíveis estão tratadas corretamente;
6. a evolução entre versão acadêmica e versão revisitada é transparente;
7. o desenvolvedor consegue defender tecnicamente as mudanças.

## Estratégia de evolução

A modernização será incremental:

1. preservar baseline acadêmico/histórico;
2. auditar;
3. montar backlog;
4. aprovar prioridades;
5. executar pequenos ciclos;
6. validar cada ciclo;
7. documentar;
8. preparar README final e defesa para entrevistas.
