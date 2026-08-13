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

Os endpoints de pacientes, médicos e horários exigem `ADMIN` ou `RECEPCAO`. Preencha `staffToken` com o token de uma conta interna local.

Em database vazio, configure `ADMIN_INITIAL_EMAIL` e `ADMIN_INITIAL_PASSWORD` somente no primeiro startup. Faça login com essa conta, copie o token para `staffToken` e use a pasta **Usuários internos** para criar recepção ou médico. A coleção não fornece credenciais padrão nem permite criar `ADMIN` pelo cadastro público.

## Observações

- `idUsuario` não deve ser enviado ao criar atendimento.
- Use datas atuais ou futuras em `dataAtendimento` (`AAAA-MM-DD`).
- CPF, e-mail de paciente, e-mail de conta e CRM devem ser únicos.
- A pasta “Exemplos de erro” ajuda a visualizar o contrato `400` e o bloqueio anônimo.
