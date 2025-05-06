# RMI Chat Rooms

## Como rodar o projeto

1. **Compilar os arquivos Java**  
    Execute o seguinte comando para compilar os arquivos Java e gerar os arquivos `.class` na pasta `bin`:

    ```bash
    javac -d bin -cp "lib/flatlaf-3.6.jar" src/*.java
    ```

2. **Executar o servidor**  
    Inicie o servidor de chat com o comando:

    ```bash
    java -cp "bin;lib/flatlaf-3.6.jar" ServerChat
    ```

3. **Executar o cliente**  
    Inicie o cliente de chat com o comando:

    ```bash
    java -cp "bin;lib/flatlaf-3.6.jar" UserChat
    ```

## Estrutura do Projeto

- `src/`: Contém os arquivos-fonte do projeto.
- `bin/`: Diretório onde os arquivos compilados serão armazenados.
- `lib/`: Contém a biblioteca `flatlaf-3.6.jar` (Look and Feel para deixar a interface do swing mais bonita).

## Observações

- Certifique-se de que o servidor esteja em execução antes de iniciar o cliente.
- Caso esteja utilizando um sistema operacional diferente, ajuste os separadores de caminho (`;` no Windows, `:` no Linux/Mac) nos comandos acima.
