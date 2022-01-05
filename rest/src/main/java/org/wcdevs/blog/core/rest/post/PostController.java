package org.wcdevs.blog.core.rest.post;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import org.wcdevs.blog.core.rest.auth.AuthAttributeExtractor;
import org.wcdevs.blog.core.rest.auth.Role;
import org.wcdevs.blog.core.rest.auth.SecurityContextAuthChecker;

/**
 * Controller providing webservices to perform post-related requests.
 */
@RestController
@RequestMapping("post")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;
  private final CommentService commentService;
  private final AuthAttributeExtractor authAttributeExtractor;
  private final SecurityContextAuthChecker securityContextAuthChecker;

  @GetMapping
  public Page<PostDto> getPosts(Pageable pageable) {
    return postService.getPosts(pageable);
  }

  /**
   * Creates a new post.
   *
   * @param principal Principal.
   * @param postDto   Post info.
   *
   * @return The newly created post.
   */
  @PostMapping
  @PreAuthorize("hasAnyRole('AUTHOR')")
  public ResponseEntity<PostDto> createPost(@AuthenticationPrincipal Object principal,
                                            @Validated @RequestBody PostDto postDto) {
    var username = authAttributeExtractor.principalUsername(principal);
    postDto.setPublishedBy(username);
    postDto.setUpdatedBy(username);

    return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
  }

  /**
   * Returns a post info.
   *
   * @param postSlug Post info to retrieve the data for.
   *
   * @return The info for the post with the given {@code postSlug}.
   */
  @GetMapping("/{postSlug}")
  public ResponseEntity<PostDto> getPost(@PathVariable String postSlug) {
    return new ResponseEntity<>(postService.getPost(postSlug), HttpStatus.OK);
  }

  /**
   * Updates a post info. Only those attributes which are not null.
   *
   * @param postSlug  Slug of the post to be updated.
   * @param principal Auth principal
   * @param newDto    New info to be updated in the post.
   *
   * @return The newly updated post info.
   */
  @PatchMapping("/{postSlug}")
  @PreAuthorize("hasAnyRole('EDITOR', 'AUTHOR')")
  public ResponseEntity<PostDto> partialUpdatePost(@PathVariable String postSlug,
                                                   @AuthenticationPrincipal Object principal,
                                                   @Validated @RequestBody PartialPostDto newDto) {
    var username = authAttributeExtractor.principalUsername(principal);
    newDto.setUpdatedBy(username);

    return new ResponseEntity<>(postService.partialUpdate(postSlug, newDto), HttpStatus.OK);
  }

  /**
   * Updates a Post.
   *
   * @param postSlug  Slug of the post to be updated.
   * @param principal Auth principal.
   * @param newDto    DTO holding the post new data.
   *
   * @return The newly updated post info.
   */
  @PutMapping("/{postSlug}")
  @PreAuthorize("hasAnyRole('EDITOR', 'AUTHOR')")
  public ResponseEntity<PostDto> fullyUpdatePost(@PathVariable String postSlug,
                                                 @AuthenticationPrincipal Object principal,
                                                 @Validated @RequestBody PostDto newDto) {
    var username = authAttributeExtractor.principalUsername(principal);
    newDto.setUpdatedBy(username);

    return new ResponseEntity<>(postService.fullUpdate(postSlug, newDto), HttpStatus.OK);
  }

  /**
   * Deletes a post.
   *
   * @param postSlug  Slug of the post to be deleted.
   * @param principal Auth principal resolved by the framework.
   */
  @DeleteMapping("/{postSlug}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAnyRole('EDITOR', 'AUTHOR')")
  public void deletePost(@PathVariable String postSlug, @AuthenticationPrincipal Object principal) {
    if (securityContextAuthChecker.hasAnyRole(Role.EDITOR)) {
      postService.deletePost(postSlug);
      return;
    }
    var username = authAttributeExtractor.principalUsername(principal);
    postService.deletePost(postSlug, username);
  }

  /**
   * Creates a new comment.
   *
   * @param postSlug  Post slug.
   * @param principal Auth principal.
   * @param dto       Payload dto.
   *
   * @return Created comment info.
   */
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/{postSlug}/comment")
  public ResponseEntity<CommentDto> createComment(@PathVariable String postSlug,
                                                  @AuthenticationPrincipal Object principal,
                                                  @Validated @RequestBody CommentDto dto) {
    var username = authAttributeExtractor.principalUsername(principal);
    dto.setPublishedBy(username);

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
