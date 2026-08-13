# Coleção Postman

Importe no Postman:

1. `Clinica-Mackito.postman_collection.json`;
2. `Clinica-Mackito-Local.postman_environment.json`.

Selecione o ambiente **Clínica Mackito - Local** e preencha somente valores locais. Não salve nem exporte senhas ou tokens preenchidos de volta para o repositório.

## Fluxo público disponível

1. Execute **Cadastrar paciente**.
2. Preencha `loginEmail` e `loginSenha` com uma conta local.
3. Execute **Login**; o script salva o JWT em `token`.

## Fluxo administrativo

Os endpoints de pacientes, médicos e horários exigem `ADMIN` ou `RECEPCAO`. Por enquanto, preencha `staffToken` com o token de uma conta interna existente no banco local.

O bootstrap seguro da primeira conta `ADMIN` e o endpoint administrativo para criar contas internas ainda serão implementados. A coleção não fornece credenciais padrão nem permite criar `ADMIN` pelo cadastro público.

## Observações

- `idUsuario` não deve ser enviado ao criar atendimento.
- Use datas atuais ou futuras em `dataAtendimento` (`AAAA-MM-DD`).
- CPF, e-mail de paciente, e-mail de conta e CRM devem ser únicos.
- A pasta “Exemplos de erro” ajuda a visualizar o contrato `400` e o bloqueio anônimo.
