package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.CommentService;
import kg.peaksoft.taskTrackerb6.dto.request.CommentRequest;
import kg.peaksoft.taskTrackerb6.dto.response.CommentResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Comment API", description = "All endpoints of comment")
public class CommentApi {

    private final CommentService commentService;

    @Operation(summary = "Save comment", description = "Save new comment")
    @PostMapping("card/{id}")
    public CommentResponse saveComment(@PathVariable Long id,
                                       @RequestBody CommentRequest request){
        return commentService.saveComment(id, request);
    }

    @Operation(summary = "Edit comment", description = "Edit comment by id")
    @PutMapping("{id}")
    public CommentResponse updateComment(@PathVariable Long id,
                                         @RequestBody CommentRequest request){
        return commentService.editComment(id, request);
    }

    @Operation(summary = "Delete comment", description = "Delete comment by id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteComment(@PathVariable Long id){
        return commentService.deleteComment(id);
    }

    @Operation(summary = "Get all comments", description = "Get all comments by card id")
    @GetMapping("card/{id}")
    public List<CommentResponse> findAllCommentsByCardId(@PathVariable Long id){
        return commentService.findAllComments(id);
    }
}
