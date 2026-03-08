FROM eclipse-temurin:17-jdk

# Install sbt
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fL https://github.com/sbt/sbt/releases/download/v1.9.7/sbt-1.9.7.tgz | tar -xz -C /usr/local && \
    ln -s /usr/local/sbt/bin/sbt /usr/bin/sbt && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY build.sbt ./

# If you have a project folder for plugins/build properties, you can uncomment the following line:
# COPY project ./project --- UNCOMMENT THIS LINE ---

# Fetch dependencies
RUN sbt update

COPY src ./src

RUN sbt compile

CMD ["sbt", "run"]