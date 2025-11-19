package road_friend.road_friend_server.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import road_friend.road_friend_server.Dto.*;
import road_friend.road_friend_server.Repository.BoardRepository;
import road_friend.road_friend_server.Repository.CommentRepository;
import road_friend.road_friend_server.Repository.PostRepository;
import road_friend.road_friend_server.domain.Board;
import road_friend.road_friend_server.domain.Comment;
import road_friend.road_friend_server.domain.Member;
import road_friend.road_friend_server.domain.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    // 전체 게시판 목록 조회
    @GetMapping
    public List<BoardDto> getBoards() {
        List<Board> boards =  boardRepository.findAll();
        List<BoardDto> boardsDto = new ArrayList<>();

        for(Board board : boards){

            BoardDto dto = new BoardDto();
            dto.setId(board.getId());
            dto.setDeparture(board.getDeparture());
            dto.setArrival(board.getArrival());

            List<Post> posts = postRepository.getPostsByBoard(board.getId());

            List<PostResponseDto> postDtos = new ArrayList<>();
            for(Post post : posts){

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
                responseDto.setAuthorNickName(post.getAuthor().getNickname());
                responseDto.setContent(post.getContent());
                responseDto.setTimeTag(post.getTimeTag());
                responseDto.setTitle(post.getTitle());

                postDtos.add(responseDto);


            }

            dto.setPosts(postDtos);
            boardsDto.add(dto);

        }

        return boardsDto;
    }

    // 특정 게시판 상세
    @GetMapping("/{boardId}")
    public BoardDto getBoard(
            @PathVariable Long boardId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String arrival,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        Board board = boardRepository.findOne(boardId);

        List<Post> posts = postRepository.getPostsByBoard(boardId);

        // 카테고리 필터
        if (category != null) {
            List<Post> filtered = new ArrayList<>();
            for (Post p : posts) {
                if (category.equals(p.getCategory())) {
                    filtered.add(p);
                }
            }
            posts = filtered;
        }

        // 출발 지역 필터
        if (departure != null && !departure.isEmpty()) {
            List<Post> filtered = new ArrayList<>();
            for (Post p : posts) {
                if (p.getDepartureTag() != null && p.getDepartureTag().contains(departure)) {
                    filtered.add(p);
                }
            }
            posts = filtered;
        }

        // 도착 지역 필터
        if (arrival != null && !arrival.isEmpty()) {
            List<Post> filtered = new ArrayList<>();
            for (Post p : posts) {
                if (p.getArrivalTag() != null && p.getArrivalTag().contains(arrival)) {
                    filtered.add(p);
                }
            }
            posts = filtered;
        }

        // 출발 시간 필터
        if (time != null && !time.isEmpty()) {
            List<Post> filtered = new ArrayList<>();
            for (Post p : posts) {
                if (p.getTimeTag() != null && p.getTimeTag().equals(time)) {
                    filtered.add(p);
                }
            }
            posts = filtered;
        }

        // 키워드 검색 (제목 + 내용)
        if (keyword != null && !keyword.isEmpty()) {
            List<Post> filtered = new ArrayList<>();
            for (Post p : posts) {
                boolean matchTitle = p.getTitle() != null && p.getTitle().contains(keyword);
                boolean matchContent = p.getContent() != null && p.getContent().contains(keyword);

                if (matchTitle || matchContent) {
                    filtered.add(p);
                }
            }
            posts = filtered;
        }

        // 정렬
        if (sort.equals("likes")) {
            posts.sort((a, b) -> b.getLikeCount() - a.getLikeCount());
        } else {
            posts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }

        // DTO 변환
        BoardDto dto = new BoardDto();
        dto.setId(board.getId());
        dto.setDeparture(board.getDeparture());
        dto.setArrival(board.getArrival());

        List<PostResponseDto> list = new ArrayList<>();
        for (Post p : posts) {
            PostResponseDto res = new PostResponseDto();
            res.setId(p.getId());
            res.setTitle(p.getTitle());
            res.setContent(p.getContent());
            res.setCategory(p.getCategory());
            res.setDepartureTag(p.getDepartureTag());
            res.setArrivalTag(p.getArrivalTag());
            res.setTimeTag(p.getTimeTag());
            res.setLikeCount(p.getLikeCount());
            res.setCreatedAt(p.getCreatedAt());
            res.setAuthorId(p.getAuthor().getId());
            res.setAuthorNickName(p.getAuthor().getNickname());
            res.setCommentCount(p.getComments().size());
            list.add(res);
        }

        dto.setPosts(list);
        return dto;
    }





    //특정 게시판에 글쓰기
    @PostMapping("/{boardId}/posts")
    public PostResponseDto createPost(@PathVariable Long boardId, @RequestBody PostCreateDto dto, Authentication authentication){
        Member member = (Member) authentication.getPrincipal();


        Post post = new Post();
        post.setBoard(boardRepository.findOne(boardId));
        post.setAuthor(member);
        post.setContent(dto.getContent());
        post.setCategory(dto.getCategory());
        post.setArrivalTag(dto.getArrivalTag());
        post.setDepartureTag(dto.getDepartureTag());
        post.setIsAnonymous(dto.getIsAnonymous());
        post.setLikeCount(0);
        post.setTimeTag(dto.getTimeTag());
        post.setTitle(dto.getTitle());
        post.setImageUrl(dto.getImageUrl());

        postRepository.savePost(post);

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
        responseDto.setAuthorNickName(post.getAuthor().getNickname());
        responseDto.setContent(post.getContent());
        responseDto.setTimeTag(post.getTimeTag());
        responseDto.setTitle(post.getTitle());

        return responseDto;

    }

    //특정게시물상세
    @GetMapping("/{boardId}/post/{postId}")
    public PostResponseDto getPostDetail(
            @PathVariable Long boardId,
            @PathVariable Long postId
    ) {

        Post post = postRepository.findByIdAndBoardId(postId,boardId);

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

        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);




        for(Comment comment : comments){

            CommentResponseDto dto = new CommentResponseDto();
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setCreatedAt(comment.getCreatedAt());
            dto.setAuthorNickName(comment.getIsAnonymous() ? "익명" : comment.getAuthor().getNickname());
            dto.setIsAnonymous(comment.getIsAnonymous());
            dto.setContent(comment.getContent());
            dto.setLikeCount(comment.getLikeCount());
            dto.setId(comment.getId());

            commentResponseDtos.add(dto);

        }

        responseDto.setComments(commentResponseDtos);


        return responseDto;


    }

    //댓글쓰기
    @PostMapping("/{boardId}/post/{postId}/comment")
    public CommentResponseDto createComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @RequestBody CommentCreateDto request,

            Authentication authentication) {

        Member member = (Member) authentication.getPrincipal();
        Post post =postRepository.findByIdAndBoardId(postId,boardId);

        Comment comment = new Comment();
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(member);
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setIsAnonymous(request.getIsAnonymous());
        comment.setLikeCount(0);

        post.getComments().add(comment);

        commentRepository.save(comment);

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

    //특정 댓글 조회

    @GetMapping("/{boardId}/post/{postId}/comment/{commentId}")
    public CommentResponseDto createComment(
            @PathVariable Long boardId,
            @PathVariable Long postId,
            @PathVariable Long commentId,


            Authentication authentication) {

        Member member = (Member) authentication.getPrincipal();
        Post post =postRepository.findByIdAndBoardId(postId,boardId);

        Comment comment = commentRepository.findById(commentId);

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