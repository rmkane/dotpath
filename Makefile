MVN := mvn
DOCS_DIR := target/reports/apidocs

.PHONY: all clean compile test verify deps update format checkstyle site help docs docs-jar open-docs

.DEFAULT_GOAL := all

all: format verify docs  # Alias for verify

clean: # Clean build artifacts
	$(MVN) clean

compile: # Compile the code
	$(MVN) compile

test: # Run tests
	$(MVN) test

verify: # Compile, run tests, and verify
	$(MVN) verify

deps: # Show dependency tree
	$(MVN) dependency:tree

update: # Check for dependency updates
	$(MVN) versions:display-dependency-updates

format: # Format code using Google Java Format via Spotless
	$(MVN) spotless:apply

checkstyle: # Check code style
	$(MVN) checkstyle:check

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

help: # Show help message
	@echo "Available targets:"
	@echo
	@grep -E '^[a-zA-Z_-]+:.*?# .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?# "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'
	