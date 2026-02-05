# Projeto: Testes Unitários para ViewModel

## Descrição
Desenvolver testes unitários para o `MyViewModel` utilizando a biblioteca `io.mockk` para mocking e `kotlinx.coroutines` para testes assíncronos. O objetivo é garantir que o `ViewModel` se comporte corretamente em diferentes cenários.

## Funcionalidades a serem testadas
- Verificar se os dados são carregados corretamente no estado de sucesso.
- Testar o comportamento no estado de erro.
- Assegurar que chamadas de funções específicas são feitas quando necessário.
- Testar a atualização do LiveData em resposta a eventos.

## Ambiente de Teste
- Linguagem: Kotlin
- Framework de Teste: JUnit
- Mocking: io.mockk
- Coroutines: kotlinx.coroutines.test

## Estrutura do Teste

```kotlin
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class MyViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MyViewModel
    private val repository: MyRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MyViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test successful data loading`() = runTest {
        // Arrange
        val expectedData = listOf("Item1", "Item2")
        coEvery { repository.getData() } returns expectedData

        // Act
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(expectedData, viewModel.data.value)
        coVerify { repository.getData() }
    }

    @Test
    fun `test data loading error`() = runTest {
        // Arrange
        val errorMessage = "Error loading data"
        coEvery { repository.getData() } throws Exception(errorMessage)

        // Act
        viewModel.loadData()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(errorMessage, viewModel.error.value)
        coVerify { repository.getData() }
    }
}