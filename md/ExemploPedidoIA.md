# Pedido para IA: Gerar fluxo getJob (Android + Kotlin + Ktor) com MVVM e Clean Architecture

Use este arquivo como prompt para a IA. Ele descreve exatamente o que a IA deve gerar para implementar o método getJob, passando pelas camadas Data → Domain → Presentation, até a UI. Preencha os campos com TODO conforme seu contexto.

## Papel e Objetivo

- Papel da IA: Gerar código Android em Kotlin, usando Ktor para networking, aplicando MVVM e Clean Architecture.
- Objetivo: Implementar o fluxo para obter um Job via método getJob, mapear DTO para domínio e expor o dado na UI.

## Contexto e Modelo de Domínio

Use tipagem forte com inline classes e um data class para Job.

```kotlin
@JvmInline
value class JobId(val value: Long)

@JvmInline
value class Company(val name: String)

@JvmInline
value class Role(val name: String)

data class Salary(val amount: Double, val currency: String)

data class Job(
    val id: JobId,
    val company: Company,
    val role: Role,
    val salary: Salary
)
```

## Entradas (preencher antes de enviar à IA)

- Base URL da API: https://api.example.com            <!-- TODO: ajuste -->
- Endpoint getJob: GET /jobs/{id}                      <!-- TODO: ajuste -->
- Campos do DTO (se divergirem do exemplo):            <!-- TODO: liste -->
    - id: Long
    - company: String
    - role: String
    - salary_amount: Double
    - salary_currency: String
- Stack da UI: Jetpack Compose                         <!-- ou Views -->
- Mecanismo de DI: Hilt                                <!-- ou Koin/Manual -->

## Requisitos Funcionais

- Implementar JobRepository com método suspend fun getJob(jobId: JobId): Job.
- Usar Ktor Client para fazer GET no endpoint informado.
- Mapear a resposta (DTO) para o modelo de domínio Job (sem vazar DTO para outras camadas).
- Criar um UseCase GetJobUseCase que delega ao repositório.
- Expor estado para UI via ViewModel (StateFlow ou LiveData).
- Renderizar os dados na tela, mostrando id, company, role, salary.

## Requisitos Não Funcionais

- Separação de camadas conforme Clean Architecture: Data, Domain, Presentation.
- Tratamento de erros de rede (timeouts, HTTP 4xx/5xx) com mensagens amigáveis.
- Código idiomático Kotlin, corrotinas no ViewModel via viewModelScope.
- Testabilidade: estruturas adequadas para mock (MockEngine do Ktor no Data).

## Arquitetura Esperada (pastas/módulos)

- domain/
    - model/Job.kt, inline classes
    - repository/JobRepository.kt
    - usecase/GetJobUseCase.kt
- data/
    - remote/KtorClientProvider.kt
    - remote/dto/JobResponse.kt
    - mapper/JobMapper.kt
    - repository/JobRepositoryImpl.kt
- presentation/
    - job/JobUiState.kt
    - job/JobViewModel.kt
    - job/ui/JobScreen.kt (Compose) ou JobFragment.kt/Activity
- di/ AppModule.kt (Hilt) ou configuração equivalente

## Detalhes de Implementação (esperado)

- Dependências Gradle para Ktor (client-core, engine, content-negotiation, serialization-json).
- KtorClientProvider com ContentNegotiation + kotlinx.serialization e ignoreUnknownKeys = true.
- DTO serializável com @Serializable e @SerialName para campos divergentes.
- Mapper JobResponse.toDomain(): Job.
- ViewModel com:
    - StateFlow<JobUiState> ou LiveData<Job>
    - função fetchJob(jobId: JobId) que atualiza isLoading, job, error
- UI:
    - Com Compose: coletar state com collectAsStateWithLifecycle e desenhar estados (loading/sucesso/erro).
    - Com Views: observar Flow/Livedata e atualizar a UI.

## Tratamento de Erros

- Converter HttpRequestTimeoutException, ClientRequestException, ServerResponseException, IOException em mensagens claras.
- Garantir que a UI receba estado de erro e não quebre o app.
- Opcional: encapsular com um wrapper Result/Either.

## Testes (mínimos)

- Mapper DTO → Domain.
- Repositório com MockEngine (200 OK e erros).
- UseCase delegando para repositório.
- ViewModel: transições de estado (loading → sucesso/erro).

## Entregáveis (formato que a IA deve retornar)

- Blocos de código completos, prontos para colar, com imports.
- Trechos Gradle necessários.
- Estrutura de pastas sugerida (comentada).
- Instruções rápidas de uso na UI (como chamar fetchJob).
- Notas de configuração (onde ajustar baseUrl e endpoint).

## Exemplo de Saída Esperada (resumido)

- build.gradle (dependências Ktor + serialization)
- KtorClientProvider.kt
- JobResponse.kt
- JobMapper.kt (fun JobResponse.toDomain(): Job)
- JobRepository.kt (contrato)
- JobRepositoryImpl.kt (Ktor GET baseUrl/jobs/{id})
- GetJobUseCase.kt
- JobUiState.kt
- JobViewModel.kt (StateFlow + fetchJob)
- JobScreen.kt (Compose) ou Activity/Fragment equivalente
- AppModule.kt (Hilt) com provides para HttpClient, Repository, UseCase

## Estilo e Qualidade

- Kotlin idiomático, nomes claros, funções puras onde possível.
- Sem lógica de negócio na UI ou no repositório (apenas acesso a dados).
- Comentários concisos explicando pontos-chave.

## Solicitação Final à IA

Com base nas informações e requisitos acima, gere todo o código necessário para implementar o fluxo getJob em Android (Kotlin + Ktor) usando MVVM e Clean Architecture, incluindo:
- Dependências Gradle
- Código das camadas Data, Domain e Presentation
- Mapeamentos e tratamento de erros
- Exemplo de tela em Compose (ou Views)
- Configuração de DI (Hilt, se selecionado)
  Adapte nomes de pacotes/pastas conforme a estrutura indicada e utilize o baseUrl e endpoint fornecidos nas Entradas.