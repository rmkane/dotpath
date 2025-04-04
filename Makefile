MVN := mvn

.PHONY: all clean compile test verify run deps update format checkstyle site help

.DEFAULT_GOAL := verify

all: verify # Alias for verify

clean: # Clean build artifacts
	$(MVN) clean

compile: # Compile the code
	$(MVN) compile

test: # Run tests
	$(MVN) test

verify: # Compile, run tests, and verify
	$(MVN) verify

run: # Run the application
	$(MVN) exec:java -Dexec.mainClass="org.example.reflection.ReflectionUtils"

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

help: # Show help message
	@echo "Available targets:"
	@echo
	@grep -E '^[a-zA-Z_-]+:.*?# .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?# "}; {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}'
	