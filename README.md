# EcoScore Engine — API RESTful (Java · Spring Boot)

API do **EcoScore Engine**, plataforma da **GaiaTech** que transforma ações sustentáveis em pontos, missões, níveis, créditos e saque em R$. A API também oferece um módulo B2B para empresas que patrocinam campanhas ESG.

FIAP · Challenge 2026 (SoulUp) · Domain Driven Design Using Java · Sprint 4 · Turma 1TDSPV

| Item | Link |
|---|---|
| API publicada (Render) | `https://<preencher-apos-deploy>.onrender.com` |
| Verificação de saúde | `https://<preencher-apos-deploy>.onrender.com/health` |
| Repositório GitHub | `<preencher>` |
| Front-end (Vercel) | `<preencher>` |

## Conteúdo do repositório (entrega da Sprint 4)

| Item exigido | Onde está |
|---|---|
| Código-fonte Java atualizado | `src/`, `pom.xml`, `mvnw`, `Dockerfile` |
| Pasta do projeto em .zip | `entrega/EcoScoreEngine-API.zip` |
| Documentação final em PDF | `docs/Documentacao_EcoScoreEngine_Sprint4_Java.pdf` |
| Link do deploy na nuvem | tabela de links no topo deste README |
| MER e diagramas de classes | `docs/diagramas/` (PNG e SVG) |

## Tecnologias

- Java 21 · Spring Boot 4.1 (Spring MVC, Bean Validation)
- JDBC com os padrões **DAO** (`@Repository`) e **Connection Factory**, com o pool de conexões do Spring (HikariCP)
- **ModelMapper** para converter DTO ↔ Model (mesma biblioteca usada em aula)
- Oracle Database (driver `ojdbc17`)
- BCrypt (`spring-security-crypto`) para o hash das senhas
- Maven Wrapper · Docker · Render

## Arquitetura (camadas)

```
Requisição HTTP
  → CorsFilter                          (config)
  → *Resource   (@RestController)       endpoints REST, status HTTP; converte DTO <-> Model com o ModelMapper
  → *Bo         (@Service)              regras de negócio, controle de acesso e transações (commit/rollback)
  → *Dao        (@Repository)           SQL com PreparedStatement, try-with-resources e montagem dos objetos
  → ConnectionFactory (@Component)      fornece a Connection com o Oracle
  → Oracle
Falhas → exceções (EntidadeNaoEncontradaException etc.) → ApiExceptionHandler → ErrorResponseDto {erro, detalhe}
```

**Transações:** as operações que gravam em mais de uma tabela (cadastro, registro e validação de ação, conversão e saque) seguem este padrão no BO:

```java
Connection conexao = connectionFactory.getConnection();
try {
    conexao.setAutoCommit(false);
    // ... chama os DAOs passando a mesma conexao ...
    conexao.commit();
} catch (Exception e) {
    conexao.rollback();
    throw e;
} finally {
    conexao.close();
}
```

## Estrutura de pastas

```
src/main/java/br/com/soulprime/
├── EcoScoreEngineApiApplication.java
├── factory/      ConnectionFactory
├── config/       CorsFilter, ModelMapperConfig
├── model/        15 classes, uma para cada tabela do MER, com associações por objeto
│   └── enums/    Perfil, StatusUsuario, StatusValidacao, TipoEvidencia, ...
├── dao/          UsuarioDao, AcaoDao, RegistroAcaoDao, CarteiraDao, CampanhaEsgDao, ... (14 DAOs)
├── bo/           AuthBo, UsuarioBo, RegistroAcaoBo, GamificacaoBo, CarteiraBo, EmpresaBo, CampanhaBo, ...
├── dto/          XxxRequestDto (entrada, com Bean Validation), XxxResponseDto (saída) e ErrorResponseDto
├── resource/     AuthResource, UsuarioResource, RegistroAcaoResource, CarteiraResource, ... (10 Resources)
└── exception/    EntidadeNaoEncontradaException, RegraNegocioException, ... e ApiExceptionHandler
src/main/resources/application.properties   (lê as credenciais das variáveis de ambiente)
Dockerfile · .env.example · pom.xml · mvnw
```

## Como executar localmente

**Pré-requisitos:**
- JDK 21 ou superior;
- acesso ao Oracle com as tabelas criadas pelos scripts de Building Relational Database (`00_EcoScore_DDL.sql` e `01_EcoScore_Carga_Inicial.sql`).

**Passos:**
1. Copie `.env.example` para `.env` e preencha `DB_URL`, `DB_USER` e `DB_PASSWORD`. O `.env` nunca vai para o GitHub.
2. Rode:
   ```bash
   set -a; . ./.env; set +a
   ./mvnw spring-boot:run
   ```
   No Windows (PowerShell), defina as variáveis com `$env:DB_URL="..."` e depois rode `mvnw.cmd spring-boot:run`.
3. Abra `http://localhost:8080/health`. A resposta esperada é `{"status":"UP","banco":"UP"}`.

**Pela IDE** (IntelliJ ou Eclipse): importe como projeto **Maven**, configure as 3 variáveis de ambiente na *Run Configuration* e execute `EcoScoreEngineApiApplication`.

**Com Docker:**
```bash
docker build -t ecoscore-api .
docker run -p 8080:8080 --env-file .env ecoscore-api
```

## Identificação do usuário e perfis

- `POST /auth/login` devolve os dados do usuário, entre eles o `id` e o `perfil`.
- Nas rotas protegidas, o Front envia o id do usuário logado no header **`X-Usuario-Id`**.
- A API confere se a conta está **ATIVA** e se o perfil pode executar a operação:

| Perfil | Pode |
|---|---|
| `PARTICIPANTE` | Registrar ações, consultar o painel, os desafios e o histórico, converter pontos e sacar |
| `VALIDADOR` | Aprovar ou negar registros pendentes, gerenciar o catálogo, ativar empresas e campanhas, desbloquear contas |
| `EMPRESA` | Criar campanhas ESG e ver o Dashboard ESG das próprias campanhas (somente dados agregados) |

## Endpoints

**Formato dos erros:** `{"erro": "Not Found", "detalhe": "Acao 999 nao encontrada."}`. Os erros de validação (400) devolvem um mapa campo → mensagem, ex.: `{"email": "E-mail invalido."}`.

**Códigos de status usados:**
- `200` OK · `201` Created (com header `Location`) · `204` No Content
- `400` dados inválidos · `401` não identificado · `403` sem permissão · `404` não encontrado · `405` método não suportado
- `409` conflito ou duplicidade · `422` regra de negócio · `423` conta bloqueada
- `500` erro interno · `503` banco indisponível

| Verbo | URI | Acesso | Descrição | Status |
|---|---|---|---|---|
| GET | `/` | público | Informações da API | 200 |
| GET | `/health` | público | Saúde da API e do banco | 200, 503 |
| POST | `/auth/login` | público | Login (bloqueia após 5 tentativas erradas) | 200, 400, 401, 403, 423 |
| POST | `/auth/cadastro` | público | Cadastro de participante | 201, 400, 409 |
| GET | `/usuarios` | validador | Lista de usuários (`?perfil=&status=`) | 200, 401, 403 |
| GET | `/usuarios/{id}` | próprio/validador | Dados do usuário | 200, 401, 403, 404 |
| PUT | `/usuarios/{id}` | próprio/validador | Edição do perfil | 200, 400, 403, 404, 409 |
| DELETE | `/usuarios/{id}` | próprio/validador | Exclusão lógica (status INATIVO) | 204, 403, 404, 422 |
| PATCH | `/usuarios/{id}/desbloqueio` | validador | Desbloqueio de conta SUSPENSA | 200, 403, 404, 422 |
| GET | `/usuarios/{id}/painel` | próprio/validador | Nível, missão em andamento, carteira, CO₂ e posição no ranking | 200, 403, 404, 422 |
| GET | `/usuarios/{id}/desafios` | próprio/validador | Progresso nos desafios temáticos | 200, 403, 404 |
| GET | `/usuarios/{id}/registros` | próprio/validador | Histórico de registros (`?status=`) | 200, 403 |
| GET | `/usuarios/{id}/carteira` | próprio/validador | Saldo de créditos e valor em R$ | 200, 403, 404 |
| POST | `/usuarios/{id}/conversoes` | próprio participante | Converte pontos em créditos (100 pts = 1 crédito) | 201, 403, 422 |
| GET | `/usuarios/{id}/conversoes` | próprio/validador | Histórico de conversões | 200, 403 |
| POST | `/usuarios/{id}/saques` | próprio participante | Saque (mínimo de 10 créditos; 10 créditos = R$ 1,00) | 201, 400, 403, 422 |
| GET | `/usuarios/{id}/saques` | próprio/validador | Histórico de saques | 200, 403 |
| POST | `/registros` | participante | Registra uma ação sustentável | 201, 400, 403, 404, 422 |
| GET | `/registros` | logado | Lista de registros (`?status=PENDENTE&usuarioId=`) | 200, 401, 403 |
| GET | `/registros/{id}` | dono/validador | Detalhe do registro | 200, 403, 404 |
| PATCH | `/registros/{id}/validacao` | validador | Aprova ou nega um registro pendente | 200, 400, 403, 404, 422 |
| GET | `/acoes` | público | Catálogo (`?status=ATIVA\|INATIVA\|TODAS&categoriaId=`) | 200, 400 |
| GET | `/acoes/{id}` | público | Detalhe da ação | 200, 404 |
| POST | `/acoes` | validador | Cria uma ação | 201, 400, 403, 404, 409 |
| PUT | `/acoes/{id}` | validador | Edita uma ação | 200, 400, 403, 404, 409 |
| DELETE | `/acoes/{id}` | validador | Exclusão lógica (status INATIVA) | 204, 403, 404, 422 |
| GET | `/categorias` · `/categorias/{id}` | público | Categorias | 200, 404 |
| POST | `/categorias` | validador | Cria uma categoria | 201, 400, 403, 409 |
| PUT | `/categorias/{id}` | validador | Edita uma categoria | 200, 403, 404, 409 |
| DELETE | `/categorias/{id}` | validador | Exclui (somente sem ações vinculadas) | 204, 403, 404, 409 |
| GET | `/missoes` · `/missoes/{id}` | público | Desafios temáticos | 200, 404 |
| POST | `/missoes` | validador | Cria um desafio | 201, 400, 403, 404, 409 |
| PUT | `/missoes/{id}` | validador | Edita um desafio | 200, 403, 404, 409 |
| DELETE | `/missoes/{id}` | validador | Exclusão lógica (status INATIVA) | 204, 403, 404, 422 |
| GET | `/niveis` | público | Níveis e bônus | 200 |
| GET | `/ranking` | público | Ranking pelos pontos acumulados (`?limite=10`) | 200 |
| POST | `/empresas` | público | Cadastro B2B: empresa PENDENTE e representante | 201, 400, 409 |
| GET | `/empresas` | validador | Lista de empresas (`?status=PENDENTE`) | 200, 403 |
| GET | `/empresas/{id}` | dona/validador | Dados da empresa | 200, 403, 404 |
| PUT | `/empresas/{id}` | dona/validador | Edição (o CNPJ não pode mudar) | 200, 400, 403, 409, 422 |
| PATCH | `/empresas/{id}/status` | validador | Ativação, suspensão ou inativação | 200, 400, 403, 404, 422 |
| GET | `/campanhas` | público/empresa/validador | Campanhas (público vê só as ATIVAS) | 200 |
| GET | `/campanhas/{id}` | público se ATIVA | Detalhe da campanha | 200, 401, 403, 404 |
| POST | `/campanhas` | empresa | Cria campanha (status PENDENTE) | 201, 400, 403, 404, 409, 422 |
| PUT | `/campanhas/{id}` | empresa dona | Edita campanha PENDENTE | 200, 400, 403, 409, 422 |
| DELETE | `/campanhas/{id}` | dona/validador | Cancelamento (status CANCELADA) | 204, 403, 404, 422 |
| PATCH | `/campanhas/{id}/status` | validador | Aprovação, encerramento ou cancelamento | 200, 400, 403, 404, 422 |
| GET | `/campanhas/{id}/metricas` | dona/validador | Métricas ESG agregadas | 200, 403, 404 |
| GET | `/dashboard/esg` | empresa/validador | Indicadores agregados das campanhas | 200, 401, 403 |

**Exemplo:** registrar uma ação.
```http
POST /registros
X-Usuario-Id: 1
Content-Type: application/json

{ "acaoId": 4, "tipoEvidencia": "COMPROVADA", "evidencia": "Foto da muda plantada" }
```

## Regras de negócio aplicadas

As regras completas estão em `docs/regras-negocio.md`.

- **Pontos:** `TRUNC((impacto × 10 × 1/repetições no dia × confiabilidade) × bônus do nível)`.
- **Confiabilidade:** 1,0 para ação comprovada (creditada na hora); 0,7 para parcial e 0,4 sem comprovação, ambas aguardando o validador.
- **Limite:** no máximo 2 registros por dia da mesma ação.
- **Missões e níveis:** a cada 3 ações creditadas, 1 missão concluída. Os níveis vão por missões (0–4, 5–9, 10–19, 20+), com bônus ×1,0 / ×1,1 / ×1,2 / ×1,3. O nível nunca diminui.
- **Conversão e saque:** 100 pontos = 1 crédito; 10 créditos = R$ 1,00; saque mínimo de 10 créditos.
- **Transações:** conversão, saque, registro e validação acontecem numa única transação, com `SELECT ... FOR UPDATE`.

## Usuários de teste (carga inicial)

A senha de todos é `Eco@2026`.

| Perfil | E-mail |
|---|---|
| PARTICIPANTE | ana.lima@email.com |
| VALIDADOR | patricia.andrade@gaiatech.com.br |
| EMPRESA | marcos.duarte@ecoverde.com.br |

## Integrantes — GaiaTech (1TDSPV)

| Nome | RM |
|---|---|
| Leonardo Afonço Sousa | RM573982 |
| Matheus Jones Sousa de Freitas | RM573559 |
| Syang Cristina Gomes Souza | RM569259 |
| Joao Machado Nascimento | RM572364 |
| Vitor Gonçalves de Souza | RM569023 |
