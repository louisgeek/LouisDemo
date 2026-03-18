.PHONY: help build clean test release install run

help:
	@echo "Available commands:"
	@echo "  make build      - Build debug APK"
	@echo "  make clean      - Clean project"
	@echo "  make test       - Run tests"
	@echo "  make release    - Build release APK"
	@echo "  make install    - Install debug APK"
	@echo "  make run        - Build and install"

build:
	./gradlew assembleDebug

clean:
	./gradlew clean

test:
	./gradlew test

release:
	./gradlew assembleRelease

install:
	./gradlew installDebug

run: build install

lint:
	./gradlew lint

format:
	./gradlew ktlintFormat
