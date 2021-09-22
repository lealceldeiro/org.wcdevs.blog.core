package com.wcdevs.blog.core.rest;

import com.wcdevs.blog.core.common.post.PostService;
import com.wcdevs.blog.core.persistence.post.PartialPostDto;
import com.wcdevs.blog.core.persistence.post.PostDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller providing webservices to perform post-related requests.
 */
@RestController
@RequestMapping("post")
public class PostController {
  private final PostService postService;

  public PostController(final PostService postService) {
    this.postService = postService;
  }

  @PostMapping("/")
  public ResponseEntity<PartialPostDto> createPost(@Validated @RequestBody PostDto postDto) {
    return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
  }

  @GetMapping("/{postSlug}")
  public ResponseEntity<PostDto> getPost(@PathVariable String postSlug) {
    return new ResponseEntity<>(postService.getPost(postSlug), HttpStatus.OK);
  }

  @PutMapping("/{postSlug}")
  public ResponseEntity<PartialPostDto> updatePost(@PathVariable String postSlug,
                                                   @Validated @RequestBody PartialPostDto newDto) {
    return new ResponseEntity<>(postService.updatePost(postSlug, newDto), HttpStatus.OK);
  }

  @DeleteMapping("/{postSlug}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePost(@PathVariable String postSlug) {
    postService.deletePost(postSlug);
  }
}
