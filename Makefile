.PHONY: all install uninstall clean dvi dist test tests run help

PROJECT_ROOT := $(CURDIR)
GRADLEW := $(PROJECT_ROOT)/gradlew
APP_NAME := simple-navigator
INSTALL_ROOT ?= $(PROJECT_ROOT)/install
INSTALL_DIR := $(INSTALL_ROOT)/$(APP_NAME)
DOC_DIR := $(PROJECT_ROOT)/artifacts/dvi
DIST_DIR := $(PROJECT_ROOT)/artifacts/dist
SOURCE_DIST := $(DIST_DIR)/$(APP_NAME)-src.tar.gz
COVERAGE_REPORT := $(PROJECT_ROOT)/build/reports/kover/html/index.html

all:
	$(GRADLEW) assemble

install: all
	@if [ "$(realpath $(INSTALL_ROOT))" = "$(realpath $(PROJECT_ROOT)/build)" ]; then \
		echo "INSTALL_ROOT must not point to the Gradle build directory"; \
		exit 1; \
	fi
	$(GRADLEW) installDist
	rm -rf "$(INSTALL_DIR)"
	mkdir -p "$(INSTALL_ROOT)"
	cp -R "$(PROJECT_ROOT)/build/install/$(APP_NAME)" "$(INSTALL_DIR)"

uninstall:
	rm -rf "$(INSTALL_DIR)"

clean:
	$(GRADLEW) clean
	rm -rf "$(PROJECT_ROOT)/artifacts" "$(PROJECT_ROOT)/install"

dvi:
	rm -rf "$(DOC_DIR)"
	mkdir -p "$(DOC_DIR)"
	cp "$(PROJECT_ROOT)/README.md" "$(DOC_DIR)/README.md"
	cp "$(PROJECT_ROOT)/README_RUS.md" "$(DOC_DIR)/README_RUS.md"

dist: all dvi
	rm -rf "$(DIST_DIR)"
	mkdir -p "$(DIST_DIR)"
	$(GRADLEW) distZip distTar
	cp "$(PROJECT_ROOT)"/build/distributions/*.zip "$(DIST_DIR)/"
	cp "$(PROJECT_ROOT)"/build/distributions/*.tar "$(DIST_DIR)/"
	tar --exclude-vcs \
		--exclude='./artifacts' \
		--exclude='./build' \
		--exclude='./install' \
		--exclude='./.gradle' \
		--exclude='./.kotlin' \
		--exclude='./.idea' \
		-czf "$(SOURCE_DIST)" \
		-C "$(PROJECT_ROOT)" .

test:
	$(GRADLEW) test

tests: test
	$(GRADLEW) koverHtmlReport
	@printf '%s\n' 'Coverage report: file://$(COVERAGE_REPORT)'
	@if command -v xdg-open >/dev/null 2>&1; then \
		xdg-open "$(COVERAGE_REPORT)" >/dev/null 2>&1 || true; \
	elif command -v open >/dev/null 2>&1; then \
		open "$(COVERAGE_REPORT)" >/dev/null 2>&1 || true; \
	elif command -v cmd.exe >/dev/null 2>&1; then \
		cmd.exe /C start "" "$(COVERAGE_REPORT)" >/dev/null 2>&1 || true; \
	fi

run:
	$(GRADLEW) run

help:
	@printf '%s\n' \
		'Available targets:' \
		'  all        Build the project via Gradle' \
		'  install    Install the app into INSTALL_ROOT (default: ./install)' \
		'  uninstall  Remove the installed app from INSTALL_ROOT' \
		'  clean      Remove Gradle build outputs and generated artifacts' \
		'  dvi        Copy project README files into artifacts/dvi' \
		'  dist       Collect binary distributions and create a source tarball' \
		'  test       Run the Gradle test suite' \
		'  tests      Run tests, generate coverage, and open the HTML report' \
		'  run        Run the console application via Gradle' \
		'  help       Show this help message'
