# Image de base : JRE 17 uniquement
FROM eclipse-temurin:17-jre

# Répertoire de travail
WORKDIR /app

# Copie du jar produit par Maven
COPY target/*.jar app.jar

# Port d'écoute de l'application
EXPOSE 8089

# Commande de démarrage (forme exec)
ENTRYPOINT ["java", "-jar", "app.jar"]
