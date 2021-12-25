package org.wcdevs.blog.core.rest.comment;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

/**
 * Controller providing webservices to perform comment-related requests.
 */
@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentController {
  private final CommentService commentService;

  @GetMapping("/{commentAnchor}")
  public ResponseEntity<CommentDto> getComment(@PathVariable String commentAnchor) {
    return new ResponseEntity<>(commentService.getComment(commentAnchor), HttpStatus.OK);
  }

  @GetMapping("/children/{parentAnchor}")
  public ResponseEntity<Collection<CommentDto>> getChildren(@PathVariable String parentAnchor) {
    return new ResponseEntity<>(commentService.getParentCommentChildren(parentAnchor),
                                HttpStatus.OK);
  }

  @PutMapping("/{commentAnchor}")
  public ResponseEntity<CommentDto> updateComment(@PathVariable String commentAnchor,
                                                  @Validated @RequestBody PartialCommentDto dto) {
    return new ResponseEntity<>(commentService.updateComment(commentAnchor, dto), HttpStatus.OK);
  }

  @DeleteMapping("/{commentAnchor}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteComment(@PathVariable String commentAnchor) {
    commentService.deleteComment(commentAnchor);
  }
}
