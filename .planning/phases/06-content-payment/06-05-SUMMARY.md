---
phase: 06
plan: 05
subsystem: backend
tags: [dto, content, preview, likes]
requires: [06-01, 06-02]
provides: [preview-field, likes-count]
affects: [ContentResponse]
key-decisions:
  - Preview only shown for unpurchased paid articles (first 150 chars with "..." suffix)
  - Videos return null preview since content is not text
  - Likes default to 0 if null in entity
metrics:
  duration: 5 minutes
  tasks_completed: 2
  files_modified: 1
---

# Phase 6 Plan 05: Content Preview Before Purchase Summary

Added teaser/preview content field and likes count to ContentResponse, enabling users to preview content before purchasing.

## Changes Made

### Task 01: Add preview field to ContentResponse

**Files Modified:**
- `drink-mall-api/src/main/java/com/drinkmall/dto/ContentResponse.java`

**Implementation:**
- Added `preview` field to ContentResponse DTO
- Implemented preview logic in `fromArticle()` method:
  - First 150 characters of content for unpurchased paid articles
  - Appends "..." suffix if content exceeds 150 characters
  - Preview only shown when `!access.isCanView()` and content is paid
- For videos, preview is null (video content is not text)

### Task 02: Add likes count to ContentResponse

**Files Modified:**
- `drink-mall-api/src/main/java/com/drinkmall/dto/ContentResponse.java`

**Implementation:**
- Added `likes` field to ContentResponse DTO
- Updated `fromVideo()` to include likes from Video entity (default 0 if null)
- Updated `fromArticle()` to include likes from HelpArticle entity (default 0 if null)

## Technical Details

### Preview Logic (D-PREVIEW-01)

```java
String preview = null;
if (!access.isCanView() && Boolean.TRUE.equals(article.getPaid()) && article.getContent() != null) {
    preview = article.getContent().length() > 150
            ? article.getContent().substring(0, 150) + "..."
            : article.getContent();
}
```

The preview is only populated when:
1. User cannot view the full content (`!access.isCanView()`)
2. Content is paid (`article.getPaid()` is true)
3. Content is not null

### Likes Count (D-LIKE-03)

```java
.likes(article.getLikes() != null ? article.getLikes() : 0)
```

Handles null values gracefully by defaulting to 0.

## Deviations from Plan

None - plan executed exactly as written.

## Verification

- [x] `mvn compile -q` succeeds
- [x] ContentResponse contains `preview` field
- [x] ContentResponse contains `likes` field
- [x] Preview is first 150 chars with "..." suffix for longer content
- [x] Preview only shown for unpurchased paid content

## Commits

| Commit | Message |
|--------|---------|
| 77aee68 | feat(06-05): add preview and likes fields to ContentResponse |

## Self-Check: PASSED

- [x] ContentResponse.java exists and compiles
- [x] Commit 77aee68 exists in git log