.PHONY: install-docker start-docker setup

install-docker:
	@if ! command -v docker >/dev/null 2>&1; then \
		echo "Installing Docker Desktop..."; \
		brew install --cask docker; \
	else \
		echo "Docker is already installed."; \
	fi

start-docker:
	@echo "Starting Docker Desktop..."
	@open -a Docker

setup: install-docker start-docker