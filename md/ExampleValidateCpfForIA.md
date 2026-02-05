# Prompt para IA: Validação de CPF em Kotlin com Jetpack Compose e MVVM

## Objetivo
Criar um projeto em Kotlin utilizando Jetpack Compose e o padrão MVVM para validar um CPF em tempo real enquanto o usuário digita, controlando o estado na ViewModel.

## Detalhes do Projeto

1. **ViewModel**: 
   - Deve gerenciar o estado do CPF e sua validade.
   - Deve fornecer uma função para atualizar o CPF e validar sua conformidade.
   - A validação deve verificar se o CPF possui 11 dígitos numéricos.

2. **Composable Function**:
   - Deve conter um campo de texto para entrada do CPF.
   - Deve exibir uma mensagem indicando se o CPF é válido ou inválido.
   - A cor do texto de mensagem deve mudar dependendo da validade do CPF.

3. **MainActivity**:
   - Deve configurar o ambiente Compose.
   - Deve exibir a tela que contém o campo de entrada e a mensagem de validação.

## Exemplo de Código

### ViewModel
```kotlin
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CpfViewModel : ViewModel() {
    var cpf by mutableStateOf("")
        private set

    var isValidCpf by mutableStateOf(false)
        private set

    fun onCpfChange(newCpf: String) {
        cpf = newCpf
        isValidCpf = validateCpf(newCpf)
    }

    private fun validateCpf(cpf: String): Boolean {
        return cpf.length == 11 && cpf.all { it.isDigit() }
    }
}
```

### Composable
```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CpfScreen(viewModel: CpfViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = viewModel.cpf,
            onValueChange = { viewModel.onCpfChange(it) },
            label = { Text("Digite seu CPF") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (viewModel.isValidCpf) "CPF Válido" else "CPF Inválido",
            color = if (viewModel.isValidCpf) MaterialTheme.colors.primary else MaterialTheme.colors.error
        )
    }
}
```

### MainActivity
```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    CpfScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        CpfScreen()
    }
}
```

## Considerações Finais
- Este exemplo é uma implementação básica e a lógica de validação do CPF pode ser aprimorada para incluir verificações de dígitos verificadores e outras regras de validação específicas.