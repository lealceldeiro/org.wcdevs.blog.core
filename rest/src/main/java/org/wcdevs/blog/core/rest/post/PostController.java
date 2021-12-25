package org.wcdevs.blog.core.rest.post;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.wcdevs.blog.core.common.comment.CommentService;
import org.wcdevs.blog.core.common.post.PostService;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
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
  private final CommentService commentService;

  @GetMapping("/")
  public ResponseEntity<Collection<PostDto>> getPosts() {
    return new ResponseEntity<>(postService.getPosts(), HttpStatus.OK);
  }

  @PostMapping("/")
  @PreAuthorize("hasAnyRole('EDITOR', 'AUTHOR')")
  public ResponseEntity<PostDto> createPost(@Validated @RequestBody PostDto postDto) {
    return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
  }

  @GetMapping("/{postSlug}")
  public ResponseEntity<PostDto> getPost(@PathVariable String postSlug) {
    return new ResponseEntity<>(postService.getPost(postSlug), HttpStatus.OK);
  }

  @PatchMapping("/{postSlug}")
  @PreAuthorize("hasAnyRole('EDITOR', 'AUTHOR')")
  public ResponseEntity<PostDto> partialUpdatePost(@PathVariable String postSlug,
                                                   @Validated @RequestBody PartialPostDto newDto) {
    return new ResponseEntity<>(postService.partialUpdate(postSlug, newDto), HttpStatus.OK);
  }

  @PutMapping("/{postSlug}")
  @PreAuthorize("hasAnyRole('EDITOR', 'AUTHOR')")
  public ResponseEntity<PostDto> fullyUpdatePost(@PathVariable String postSlug,
                                                 @Validated @RequestBody PostDto newDto) {
    return new ResponseEntity<>(postService.fullUpdate(postSlug, newDto), HttpStatus.OK);
  }

  @DeleteMapping("/{postSlug}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('EDITOR', 'AUTHOR')")
  public void deletePost(@PathVariable String postSlug) {
    postService.deletePost(postSlug);
  }

  @PostMapping("/{postSlug}/comment")
  public ResponseEntity<CommentDto> createComment(@PathVariable String postSlug,
                                                  @Validated @RequestBody CommentDto dto) {
    return new ResponseEntity<>(commentService.createComment(postSlug, dto), HttpStatus.CREATED);
  }

  @GetMapping("{postSlug}/comment/all")
  public ResponseEntity<Collection<CommentDto>> getAllPostComments(@PathVariable String postSlug) {
    return new ResponseEntity<>(commentService.getAllPostComments(postSlug), HttpStatus.OK);
  }

  @GetMapping("{postSlug}/comment/root")
  public ResponseEntity<Collection<CommentDto>> getRootPostComments(@PathVariable String postSlug) {
    return new ResponseEntity<>(commentService.getRootPostComments(postSlug), HttpStatus.OK);
  }
}
