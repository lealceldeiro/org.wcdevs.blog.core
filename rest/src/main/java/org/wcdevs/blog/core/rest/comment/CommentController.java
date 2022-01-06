package org.wcdevs.blog.core.rest.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wcdevs.blog.core.common.comment.CommentService;
import org.wcdevs.blog.core.persistence.comment.CommentDto;
import org.wcdevs.blog.core.persistence.comment.PartialCommentDto;
import org.wcdevs.blog.core.rest.auth.AuthAttributeExtractor;
import org.wcdevs.blog.core.rest.auth.Role;
import org.wcdevs.blog.core.rest.auth.SecurityContextAuthChecker;

/**
 * Controller providing webservices to perform comment-related requests.
 */
@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;
  private final AuthAttributeExtractor authAttributeExtractor;
  private final SecurityContextAuthChecker securityContextAuthChecker;

  @GetMapping("/{commentAnchor}")
  public CommentDto getComment(@PathVariable String commentAnchor) {
    return commentService.getComment(commentAnchor);
  }

  @GetMapping("/children/{parentAnchor}")
  public Page<CommentDto> getChildren(@PathVariable String parentAnchor, Pageable pageable) {
    return commentService.getParentCommentChildren(parentAnchor, pageable);
  }

  /**
   * Updates a comment.
   *
   * @param commentAnchor Anchor of the comment to be updated.
   * @param principal     Authentication principal, automatically resolved by Spring.
   * @param dto           Payload with the new content to use to update the comment.
   *
   * @return The updated comment info.
   */
  @PutMapping("/{commentAnchor}")
  @PreAuthorize("isAuthenticated()")
  public CommentDto updateComment(@PathVariable String commentAnchor,
                                  @AuthenticationPrincipal Object principal,
                                  @Validated @RequestBody PartialCommentDto dto) {
    var username = authAttributeExtractor.principalUsername(principal);
    return commentService.updateComment(commentAnchor, dto, username);
  }

  /**
   * Deletes a comment.
   *
   * @param commentAnchor Anchor of the comment to be deleted.
   * @param principal     Auth principal object, resolved by the framework.
   */
  @DeleteMapping("/{commentAnchor}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("isAuthenticated()")
  public void deleteComment(@PathVariable String commentAnchor,
                            @AuthenticationPrincipal Object principal) {
    if (securityContextAuthChecker.hasAnyRole(Role.EDITOR)) {
      commentService.deleteComment(commentAnchor);
      return;
    }
    var username = authAttributeExtractor.principalUsername(principal);
    commentService.deleteComment(commentAnchor, username);
  }
}
