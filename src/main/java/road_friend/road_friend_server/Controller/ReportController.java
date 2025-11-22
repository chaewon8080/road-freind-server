package road_friend.road_friend_server.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import road_friend.road_friend_server.Dto.CommentResponseDto;
import road_friend.road_friend_server.Dto.PostResponseDto;
import road_friend.road_friend_server.Dto.ReportDto;
import road_friend.road_friend_server.Dto.ReviewDto;
import road_friend.road_friend_server.Repository.*;
import road_friend.road_friend_server.domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostLikeRepository postLikeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;



    //모든 신고내역 조회
    @GetMapping("/reports")
    public List<ReportDto> getAll(){

        List<Report> reports = reportRepository.findAll();

        List<ReportDto> dtos = new ArrayList<>();

        for(Report report : reports){

            ReportDto dto = new ReportDto();
            dto.setType(report.getType());
            dto.setReason(report.getReason());
            dto.setReporterEmail(report.getReporterEmail());
            dto.setTargetId(report.getTargetId());

            dtos.add(dto);



        }

        return dtos;


    }

    //신고 만들기
    @PostMapping("/create-report")
    public ReportDto createReport(@RequestBody ReportDto dto){

        Report report = new Report();

        report.setType(dto.getType());
        report.setReason(dto.getReason());
        report.setReporterEmail(dto.getReporterEmail());
        report.setTargetId(dto.getTargetId());

        reportRepository.save(report);

        ReportDto dto1 = new ReportDto();
        dto1.setType(report.getType());
        dto1.setReason(report.getReason());
        dto1.setReporterEmail(report.getReporterEmail());
        dto1.setTargetId(report.getTargetId());

        return dto1;


    }

    //신고 승인
    @Transactional
    @PostMapping("/reports/{reportId}/approve")
    public Map<String, Object> approveReport(@PathVariable Long reportId){
        Report report = reportRepository.findOne(reportId);

        if(report.getType().equals(ReportType.POST)){
            //게시글에 달린 댓글 삭제
            List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(report.getTargetId());

            for(Comment comment : comments){
                // 좋아요 먼저 삭제
                List<CommentLike> likes = commentLikeRepository.findAllByCommentId(comment.getId());
                for (CommentLike like : likes) {
                    commentLikeRepository.delete(like);
                }

                //댓글삭제
                commentRepository.delete(comment);




            }

            // 게시글 좋아요 삭제
            List<PostLike> likes = postLikeRepository.findAllByPostId(report.getTargetId());

            for (PostLike like : likes) {
                postLikeRepository.delete(like);
            }

            // 게시글 삭제
            postRepository.delete(postRepository.findOne(report.getTargetId()));

        }
        else if(report.getType().equals(ReportType.COMMENT)){

            // 좋아요 먼저 삭제
            List<CommentLike> likes = commentLikeRepository.findAllByCommentId(report.getTargetId());
            for (CommentLike like : likes) {
                commentLikeRepository.delete(like);
            }

            commentRepository.delete(commentRepository.findOne(report.getTargetId()));

        }
        else{

            // 좋아요 먼저 삭제
            List<ReviewLike> likes = reviewLikeRepository.findAllByReviewId(report.getTargetId());
            for (ReviewLike like : likes) {
                reviewLikeRepository.delete(like);
            }

            // 리뷰 삭제
            reviewRepository.delete(reviewRepository.findOne(report.getTargetId()));




        }

        reportRepository.delete(report);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "success");

        return response;




    }

    //신고 거부
    @Transactional
    @PostMapping("/reports/{reportId}/reject")
    public void rejectReport(@PathVariable Long reportId) {
        Report report = reportRepository.findOne(reportId);

        // 신고만 삭제
        reportRepository.delete(report);
    }


    @GetMapping("/reports/{reportId}/posts/{postId}")
    public PostResponseDto getPostDetail(@PathVariable Long postId){


        Post post = postRepository.findOne(postId);

        PostResponseDto responseDto= new PostResponseDto();
        responseDto.setCreatedAt(post.getCreatedAt());
        responseDto.setImageUrl(post.getImageUrl());
        responseDto.setCategory(post.getCategory());
        responseDto.setBoardId(post.getBoard().getId());
        responseDto.setId(post.getId());
        responseDto.setArrivalTag(post.getArrivalTag());
        responseDto.setDepartureTag(post.getDepartureTag());
        responseDto.setLikeCount(post.getLikeCount());
        responseDto.setIsAnonymous(post.getIsAnonymous());
        responseDto.setAuthorId(post.getAuthor().getId());
        responseDto.setAuthorNickName(post.getIsAnonymous() ? "익명" : post.getAuthor().getNickname());
        responseDto.setContent(post.getContent());
        responseDto.setTimeTag(post.getTimeTag());
        responseDto.setTitle(post.getTitle());

        return responseDto;

    }

    @GetMapping("/reports/{reportId}/reviews/{reviewId}")
    public ReviewDto getReviewDetail(@PathVariable Long reviewId){


        Review review = reviewRepository.findOne(reviewId);

        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setBusStopId(review.getBusStop().getId());
        dto.setBusStopName(review.getBusStop().getName());
        dto.setAuthorId(review.getAuthor().getId());
        dto.setAuthorNickName(review.getAuthor().getNickname());
        dto.setContent(review.getContent());
        dto.setDayTags(review.getDayTags());
        dto.setTimeTags(review.getTimeTags());
        dto.setCategoryTags(review.getCategoryTags());
        dto.setImageUrl(review.getImageUrl());
        dto.setLikeCount(review.getLikeCount());
        dto.setIsAnonymous(review.getIsAnonymous());
        dto.setCreatedAt(review.getCreatedAt());

        return dto;

    }

    @GetMapping("/reports/{reportId}/comments/{commentId}")
    public CommentResponseDto getCommentDetail(@PathVariable Long commentId){
        Comment comment = commentRepository.findOne(commentId);

        CommentResponseDto dto = new CommentResponseDto();

        dto.setContent(comment.getContent());
        dto.setId(comment.getId());
        dto.setAuthorId(comment.getAuthor().getId());
        dto.setIsAnonymous(comment.getIsAnonymous());
        dto.setAuthorNickName(comment.getAuthor().getNickname());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(comment.getCreatedAt());

        return dto;

    }





}
