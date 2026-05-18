---
phase: 06
plan: 06
subsystem: backend
tags: [content, likes, engagement, dto]
requires: [06-01, 06-02]
provides: [like-toggle, userLiked-status]
affects: [ContentController, ContentResponse]
key-decisions:
  - Like toggle uses atomic GREATEST(0, likes + delta) to prevent negative counts
  - Only purchased content can be liked (D-LIKE-04)
  - userLiked field populated via toBuilder for fluent modification
metrics:
  duration: 10 minutes
  tasks_completed: 2
  files_modified: 2
---

# Phase 6 Plan 06: Like/Unlike Content with Engagement Counts Summary

Implemented like toggle functionality with atomic count updates, per-user tracking via content_likes table, and added userLiked status indicator to ContentResponse.

## Changes Made

### Task 01: Add userLiked field to ContentResponse

**Files Modified:**
- `drink-mall-api/src/main/java/com/drinkmall/dto/ContentResponse.java`

**Implementation:**
- Added `Boolean userLiked` field to track current user's like status per content (D-LIKE-02)
- Enabled `toBuilder = true` on @Builder annotation for fluent modification

### Task 02: Add like toggle endpoint and userLiked population to ContentController

**Files Modified:**
- `drink-mall-api/src/main/java/com/drinkmall/controller/ContentController.java`
- `drink-mall-api/src/main/java/com/drinkmall/dto/ContentResponse.java`

**Implementation:**
- Added `ContentLikeMapper` injection for like tracking
- Added `hasUserLiked()` helper method to check per-user like status
- Added `POST /{id}/like` endpoint for toggling like/unlike (D-LIKE-01)
- Added `updateLikeCount()` helper with atomic GREATEST(0, ...) SQL (D-LIKE-03, T-6-06)
- Added `getLikeCount()` helper to retrieve current count
- Enforced purchase requirement before liking (D-LIKE-04)
- Updated `videoResponse()` and `articleResponse()` to populate userLiked field

## Technical Details

### Like Toggle Endpoint (D-LIKE-01, D-LIKE-04)

```java
@PostMapping("/{id}/like")
@SaCheckLogin
@Transactional
public Result<Map<String, Object>> toggleLike(
        @PathVariable Long id,
        @RequestParam(defaultValue = TYPE_VIDEO) String type) {
    Long userId = StpUtil.getLoginIdAsLong();

    // Only purchased content can be liked
    if (!contentPurchaseService.hasPurchased(userId, type, id)) {
        throw new BusinessException(400, "请先购买内容后再点赞");
    }
    // ... toggle logic
}
```

### Atomic Count Update (D-LIKE-03, T-6-06)

```java
private void updateLikeCount(String type, Long contentId, int delta) {
    if (TYPE_ARTICLE.equals(type)) {
        helpArticleMapper.update(null, new LambdaUpdateWrapper<HelpArticle>()
                .eq(HelpArticle::getId, contentId)
                .setSql("likes = GREATEST(0, likes + " + delta + ")"));
    } else {
        videoMapper.update(null, new LambdaUpdateWrapper<Video>()
                .eq(Video::getId, contentId)
                .setSql("likes = GREATEST(0, likes + " + delta + ")"));
    }
}
```

The `GREATEST(0, ...)` ensures counts never go negative even with race conditions.

### UserLiked Population

```java
private ContentResponse videoResponse(Video video, Long userId) {
    // ...
    boolean userLiked = hasUserLiked(userId, TYPE_VIDEO, video.getId());
    return ContentResponse.fromVideo(video, purchased, price, access, payMethods)
            .toBuilder()
            .userLiked(userLiked)
            .build();
}
```

## Deviations from Plan

None - plan executed exactly as written.

## Verification

- [x] `mvn compile -q` succeeds
- [x] ContentController has `POST /{id}/like` endpoint
- [x] Endpoint requires login (@SaCheckLogin)
- [x] Only purchased content can be liked
- [x] Likes update atomically using setSql with GREATEST
- [x] ContentResponse includes userLiked field populated from hasUserLiked check
- [x] toBuilder enabled on ContentResponse for fluent modification

## Commits

| Commit | Message |
|--------|---------|
| 8c82f6b | feat(06-06): add userLiked field to ContentResponse |
| 961d161 | feat(06-06): add like toggle endpoint and userLiked population |

## Self-Check: PASSED

- [x] ContentResponse.java exists and compiles
- [x] ContentController.java exists and compiles
- [x] Commits 8c82f6b and 961d161 exist in git log
