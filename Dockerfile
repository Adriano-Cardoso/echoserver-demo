# Build Stage: Usar Maven para construir o projeto
FROM maven:3.8.4-openjdk-17 AS build

# Diretório de trabalho dentro da imagem
WORKDIR /app

# Copiar todo o código fonte para dentro do container
COPY . .

# Rodar o Maven para compilar o projeto e gerar o arquivo JAR
RUN mvn clean install

# Final Stage: Imagem final com o OpenJDK 17
FROM openjdk:17-jdk-slim

# Metadados do autor
LABEL authors="adrianogoulart"

# Diretório de trabalho dentro do container
WORKDIR /app

# Copiar o arquivo JAR gerado pela etapa anterior para a imagem final
COPY --from=build /app/target/echo-server-1.0-SNAPSHOT.jar /app/echo-server.jar

# Expor a porta 8081 para a aplicação
EXPOSE 8081

# Definir o comando para rodar a aplicação no container
ENTRYPOINT ["java", "-jar", "echo-server.jar"]

