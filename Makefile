MVN := mvn
DOCS_DIR := target/reports/apidocs

.PHONY: all clean compile test verify deploy deps update format lint site help docs docs-jar open-docs setup-hooks

.DEFAULT_GOAL := all

all: setup-hooks lint verify docs  # Alias for verify

clean: # Clean build artifacts
	$(MVN) clean

compile: # Compile the code
	$(MVN) compile

test: # Run tests
	$(MVN) test

verify: # Compile, run tests, and verify
	$(MVN) verify

deploy-github: # Deploy the package to the GitHub repository
	$(MVN) deploy -P github

deploy-central: # Deploy the package to the Maven Central repository
	$(MVN) deploy -P central

deps: # Show dependency tree
	$(MVN) dependency:tree

update: # Check for dependency updates
	$(MVN) versions:display-dependency-updates

format: # Format code using Google Java Format via Spotless
	$(MVN) spotless:apply

lint: # Check code style
	$(MVN) spotless:check

site: # Generate site documentation
	$(MVN) site

docs: # Generate Javadoc documentation
	$(MVN) javadoc:javadoc
	@echo "Documentation generated in $(DOCS_DIR)/"

docs-jar: # Generate Javadoc JAR
	$(MVN) javadoc:jar
	@echo "Javadoc JAR generated in target/"

open-docs: docs # Generate and open documentation in default browser
ifeq ($(shell uname), Darwin)
	open $(DOCS_DIR)/index.html
else
	xdg-open $(DOCS_DIR)/index.html 2>/dev/null || echo "Could not open browser automatically. Please open $(DOCS_DIR)/index.html manually."
endif

setup-hooks: # Install git hooks
	@echo "Installing git hooks..."
	@rm -f .git/hooks/pre-commit
	@ln -sf ../../.githooks/pre-commit .git/hooks/pre-commit
	@chmod +x .githooks/pre-commit
	@echo "Git hooks installed successfully!"

help: # Show help message
	@echo "Available targets:"
	@echo
	@grep -E '^[a-zA-Z_-]+:.*?# .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?# "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'
