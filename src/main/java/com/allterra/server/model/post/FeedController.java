package com.allterra.server.model.post;

import com.allterra.server.model.post.dto.FeedPageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for the community feed.
 */
@Slf4j
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public final class FeedController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<FeedPageResponseDto> getFeed(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(required = false) final PostAudience audience
    ) {
        log.info("Get feed page {} size {} audience {}", page, size, audience);
        return ResponseEntity.ok(postService.getFeed(page, size, audience));
    }
}
