# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

---

## [Unreleased]

### Added
- Option to export search results to CSV
- Keyboard shortcuts for quick navigation

### Fixed
- Widget panel alignment issue on smaller screens
- Diacritics recognition/distinction in file names

---

## [v1.0.0] - 2025-05-19

### Added
- Query-aware widget system (based on the introduced queries)
- Caching layer using `Proxy` pattern
- Spelling corrector that learns from previous searches (`Strategy` pattern)
- `PDF`, `DOCX` and `Image` file indexing and support
- Image preview widget with auto-launch
- `DOCX` preview widget to open files in `MS Word`
- Metadata summaries in status bar: file type, modified month and file size

### Changed
- Introduced `SearchControllerFacade` using `Facade` pattern for logic simplification
- Refactored all widget logic into a single `WidgetFactory`
- Improved `StatusBox` with multiline display 

### Removed
- `SearchService` class and other unused components to simplify architecture and reduce complexity

### Fixed
- Corrected behavior with spelling suggestions when handling invalid query terms

### Security
- Added basic pre-commit git hook to block files containing `TODO` from being committed
