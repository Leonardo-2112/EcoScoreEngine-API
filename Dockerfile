# ---------- Etapa 1: build (JDK 21 + Maven Wrapper) ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src/ src/
RUN ./mvnw -B -q package -DskipTests

# ---------- Etapa 2: execucao (somente JRE 21) ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV TZ=America/Sao_Paulo
COPY --from=build /app/target/ecoscore-api.jar app.jar
# O Render/Railway informam a porta na variavel PORT (padrao 8080)
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Xmx384m -jar app.jar"]
