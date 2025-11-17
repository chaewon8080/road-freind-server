package road_friend.road_friend_server.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import road_friend.road_friend_server.Dto.BoardDto;
import road_friend.road_friend_server.Dto.PostCreateDto;
import road_friend.road_friend_server.Dto.PostResponseDto;
import road_friend.road_friend_server.Repository.BoardRepository;
import road_friend.road_friend_server.Repository.PostRepository;
import road_friend.road_friend_server.domain.Board;
import road_friend.road_friend_server.domain.Member;
import road_friend.road_friend_server.domain.Post;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;

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
    public BoardDto getBoard(@PathVariable Long boardId) {
         Board board = boardRepository.findOne(boardId);

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
}