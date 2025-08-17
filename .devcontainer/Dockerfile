# Java 17 base (Spark 3.5.x-friendly), multi-arch image
FROM mcr.microsoft.com/devcontainers/java:1-17-bullseye


# --- OS deps (root) ---
USER root
RUN apt-get update \
    && apt-get install -y curl gzip ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# --- Install Apache Spark ---
ARG SPARK_VERSION=3.5.1
ARG HADOOP_FLAVOR=hadoop3
ENV SPARK_HOME=/opt/spark
ENV PATH="${SPARK_HOME}/bin:${PATH}"
RUN set -eux; \
    curl -fL --retry 5 --retry-all-errors "https://archive.apache.org/dist/spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-${HADOOP_FLAVOR}.tgz" -o /tmp/spark.tgz; \
    mkdir -p /opt; \
    tar -xzf /tmp/spark.tgz -C /opt; \
    ln -s "/opt/spark-${SPARK_VERSION}-bin-${HADOOP_FLAVOR}" "${SPARK_HOME}"; \
    rm -f /tmp/spark.tgz; \
    "${SPARK_HOME}/bin/spark-submit" --version

# --- Install Coursier + tools (as vscode user) ---
USER vscode
ENV PATH="/home/vscode/.local/share/coursier/bin:${PATH}"

# Coursier install (non-interactive, explicit versions, and build-time verification)
ARG TARGETARCH
ENV COURSIER_NO_TERM=1
RUN set -eux; \
    arch="${TARGETARCH:-$(uname -m)}"; \
    case "$arch" in \
    amd64|x86_64) CS_URL="https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz" ;; \
    arm64|aarch64) CS_URL="https://github.com/coursier/launchers/raw/master/cs-aarch64-pc-linux.gz" ;; \
    *) echo "Unsupported arch: $arch" && exit 1 ;; \
    esac; \
    curl -fL --retry 5 --retry-all-errors "$CS_URL" -o /home/vscode/cs.gz; \
    gunzip -f /home/vscode/cs.gz; \
    chmod +x /home/vscode/cs; \
    # ensure on PATH immediately
    mkdir -p /home/vscode/.local/share/coursier/bin; \
    ln -sf /home/vscode/cs /home/vscode/.local/share/coursier/bin/cs; \
    echo 'export PATH="$PATH:/home/vscode/.local/share/coursier/bin"' >> /home/vscode/.bashrc; \
    # install tools with explicit versions to avoid resolution quirks
    cs --version; \
    cs install sbt:1.10.5 scalafmt:3.8.3; \
    # verify during build so failures are visible here
    ~/.local/share/coursier/bin/sbt --version; \
    ~/.local/share/coursier/bin/scalafmt --version

# Optional working dir (Dev Containers will mount your repo here)
WORKDIR /workspaces
