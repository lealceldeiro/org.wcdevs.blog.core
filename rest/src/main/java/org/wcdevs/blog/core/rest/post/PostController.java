package org.wcdevs.blog.core.rest.post;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wcdevs.blog.core.common.post.PostService;
import org.wcdevs.blog.core.persistence.post.PartialPostDto;
import org.wcdevs.blog.core.persistence.post.PostDto;

/**
 * Controller providing webservices to perform post-related requests.
 */
@RestController
@RequestMapping("post")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;

  @GetMapping("/")
  public ResponseEntity<List<PostDto>> getPosts() {
    return new ResponseEntity<>(postService.getPosts(), HttpStatus.OK);
  }

  @PostMapping("/")
  public ResponseEntity<PostDto> createPost(@Validated @RequestBody PostDto postDto) {
    return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
  }

  @GetMapping("/{postSlug}")
  public ResponseEntity<PostDto> getPost(@PathVariable String postSlug) {
    return new ResponseEntity<>(postService.getPost(postSlug), HttpStatus.OK);
  }

  @PatchMapping("/{postSlug}")
  public ResponseEntity<PostDto> partialUpdatePost(@PathVariable String postSlug,
                                                   @Validated @RequestBody PartialPostDto newDto) {
    return new ResponseEntity<>(postService.partialUpdate(postSlug, newDto), HttpStatus.OK);
  }

  @PutMapping("/{postSlug}")
  public ResponseEntity<PostDto> fullyUpdatePost(@PathVariable String postSlug,
                                                 @Validated @RequestBody PostDto newDto) {
    return new ResponseEntity<>(postService.fullUpdate(postSlug, newDto), HttpStatus.OK);
  }

  @DeleteMapping("/{postSlug}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePost(@PathVariable String postSlug) {
    postService.deletePost(postSlug);
  }
}
