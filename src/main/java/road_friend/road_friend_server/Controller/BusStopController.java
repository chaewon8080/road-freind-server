package road_friend.road_friend_server.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import road_friend.road_friend_server.Api.BusStopService;
import road_friend.road_friend_server.Dto.BusStopDto;
import road_friend.road_friend_server.Dto.ReviewDto;
import road_friend.road_friend_server.Dto.ReviewLikeDto;
import road_friend.road_friend_server.Repository.BusStopRepository;
import road_friend.road_friend_server.Repository.ReviewLikeRepository;
import road_friend.road_friend_server.Repository.ReviewRepository;
import road_friend.road_friend_server.domain.BusStop;
import road_friend.road_friend_server.domain.Member;
import road_friend.road_friend_server.domain.Review;
import road_friend.road_friend_server.domain.ReviewLike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bus-stops")
public class BusStopController {

    private final BusStopService busStopService;
    private final BusStopRepository busStopRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    ObjectMapper mapper = new ObjectMapper();
    ObjectMapper objectMapper = new ObjectMapper();




    @GetMapping("/nearby")
    public List<BusStopDto> getNearbyStops(
            @RequestParam double lat,
            @RequestParam double lng) {
        return busStopRepository.findNearestStops(lat, lng);
    }

    @GetMapping("/search")
    public List<BusStopDto> searchBusStops(@RequestParam String keyword){
        List<BusStop> busStops = busStopRepository.searchBusStops(keyword);
        List<BusStopDto> dtos = new ArrayList<>();

        for(BusStop busStop : busStops){
            BusStopDto dto = new BusStopDto();
            dto.setReviewCount(busStop.getReviews().size());
            dto.setId(busStop.getId());
            dto.setName(busStop.getName());
            dto.setAddress(busStop.getAddress());
            dto.setMobileNumber(busStop.getMobileNumber());
            dto.setLatitude(busStop.getLatitude());
            dto.setLongitude(busStop.getLongitude());
            dto.setNumber(busStop.getNumber());

            dtos.add(dto);


        }

        return dtos;

    }


    //특정 버스 정류장 리뷰 작성
    @PostMapping("/{busStopId}")
    public ReviewDto createReview(@PathVariable("busStopId")Long busStopId, @RequestBody ReviewDto dto, Authentication authentication) throws JsonProcessingException {
        Member member = (Member) authentication.getPrincipal();
        Review review = new Review();
        review.setBusStop(busStopRepository.findOne(busStopId));
        review.setAuthor(member);
        review.setContent(dto.getContent());
        review.setDayTags(mapper.writeValueAsString(dto.getDayTags()));
        review.setCategoryTags(mapper.writeValueAsString(dto.getCategoryTags()));
        review.setImageUrl(dto.getImageUrl());
        review.setIsAnonymous(dto.getIsAnonymous());
        review.setLikeCount(0);
        review.setTimeTags(mapper.writeValueAsString(dto.getTimeTags()));
        review.setCreatedAt(dto.getCreatedAt());

        reviewRepository.saveReview(review);
        dto.setId(review.getId());
        dto.setAuthorId(member.getId());
        dto.setBusStopId(busStopId);
        dto.setAuthorNickName(member.getNickname());
        dto.setBusStopName(review.getBusStop().getName());

        return dto;


    }

    //특정 버스 정류장 리뷰 조회
    @GetMapping("/{busStopId}")
    public List<ReviewDto> getReviews(@PathVariable("busStopId") Long busStopId, @RequestParam(defaultValue = "latest") String sort, @RequestParam(required = false) List<String> tags, @RequestParam(required = false) String keyword
    , Authentication authentication) throws JsonProcessingException {

        Member member = (Member) authentication.getPrincipal();

        List<Review> reviews = reviewRepository.getReviewsByBusStop(busStopId);


        //키워드 검색
        if (keyword != null && !keyword.isEmpty()) {
            List<Review> keywordMatchReviews = new ArrayList<>();

            for (Review review : reviews) {
                // 내용이 null일 수도 있으니까 null 체크
                if (review.getContent() != null && review.getContent().contains(keyword)) {
                    keywordMatchReviews.add(review);
                }
            }

            reviews = keywordMatchReviews;
        }

        if(tags != null && !tags.isEmpty()){

            for (String tag : tags) {

                // 요일 태그
                if (tag.contains("요일")) {
                    List<Review> dayMatchReviews = new ArrayList<>();

                    for (Review review : reviews) {
                        boolean found = false;

                        String[] dayTags = objectMapper.readValue(review.getDayTags(), String[].class);
                        if (dayTags == null) continue;  // null이면 skip


                        for( String t : objectMapper.readValue(review.getDayTags(), String[].class)){

                            if(t.equals(tag)){

                                found = true;
                                break;
                            }

                        }

                        if(found){

                            dayMatchReviews.add(review);


                        }

                    }
                    reviews = dayMatchReviews;
                    continue;

                }

                // 시간 태그라면
                if (tag.matches("^\\d{1,2}:00$")) {
                    List<Review> timeMatchReviews = new ArrayList<>();

                    for (Review r : reviews) {
                        boolean found = false;

                        String[] timeTags = objectMapper.readValue(r.getTimeTags(), String[].class);
                        if (timeTags == null) continue;

                        for (String t : objectMapper.readValue(r.getTimeTags(), String[].class)) {
                            if (t.equals(tag)) {
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            timeMatchReviews.add(r);
                        }
                    }

                    reviews = timeMatchReviews;
                    continue;
                }

                // 카테고리 태그
                List<Review> categoryMatchReviews = new ArrayList<>();

                for (Review r : reviews) {
                    boolean found = false;

                    String[] categoryTags = objectMapper.readValue(r.getCategoryTags(), String[].class);
                    if (categoryTags == null) continue;

                    for (String t : objectMapper.readValue(r.getCategoryTags(), String[].class)) {
                        if (t.equals(tag)) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        categoryMatchReviews.add(r);
                    }
                }

                reviews = categoryMatchReviews;
            }






        }


        //좋아요 정렬
        if(sort.equals("likes")){
            reviews.sort((review1, review2) -> review2.getLikeCount() - review1.getLikeCount());
        }else{
            reviews.sort((review1, review2) -> review2.getCreatedAt().compareTo(review1.getCreatedAt()));
        }

        List<ReviewDto> dtos = new ArrayList<>();

        for(Review review : reviews){
            ReviewDto dto = new ReviewDto();
            dto.setBusStopId(busStopId);
            dto.setContent(review.getContent());
            dto.setDayTags(objectMapper.readValue(review.getDayTags(), String[].class));
            dto.setCreatedAt(review.getCreatedAt());
            dto.setImageUrl(review.getImageUrl());
            dto.setLikeCount(review.getLikeCount());
            dto.setTimeTags(objectMapper.readValue(review.getTimeTags(), String[].class));
            dto.setIsAnonymous(review.getIsAnonymous());
            dto.setAuthorId(review.getAuthor().getId());
            dto.setId(review.getId());
            dto.setCategoryTags(objectMapper.readValue(review.getCategoryTags(), String[].class));
            dto.setAuthorNickName(review.getAuthor().getNickname());
            dto.setBusStopName(review.getBusStop().getName());

            //좋아요 이미 눌렀는지
            ReviewLike existing = reviewLikeRepository.findByMemberAndReview(member.getId(),review.getId());

            if(existing == null){
                dto.setLiked(false);


            }
            else{

                dto.setLiked(true);


            }


            dtos.add(dto);

        }

        return dtos;



    }

    //버스정류장 이름 조회
    @GetMapping("name/{busStopId}")
    public BusStopDto getBusStopName(@PathVariable("busStopId") Long busStopId){
        BusStop busStop = busStopRepository.findOne(busStopId);

        BusStopDto dto = new BusStopDto();
        dto.setName(busStop.getName());

        return dto;
    }

    //좋아요 누르기와 취소
    @Transactional
    @PostMapping("/{reviewId}/like")
    public ReviewLikeDto toggleLike(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        Member member = (Member) authentication.getPrincipal();

        Review review = reviewRepository.findOne(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("리뷰가 존재하지 않습니다.");
        }

        // 좋아요 이미 눌렀는지 확인
        ReviewLike existing = reviewLikeRepository.findByMemberAndReview(member.getId(), reviewId);

        boolean liked;   // 현재 좋아요 상태

        if (existing == null) {
            // 좋아요 신규 등록
            ReviewLike like = new ReviewLike();
            like.setMember(member);
            like.setReview(review);
            reviewLikeRepository.save(like);

            // 리뷰의 좋아요 수 증가
            review.setLikeCount(review.getLikeCount() + 1);
            liked = true;

        } else {
            // 좋아요 취소
            reviewLikeRepository.delete(existing);

            // 리뷰 좋아요 수 감소
            review.setLikeCount(review.getLikeCount() - 1);
            liked = false;
        }

        // 응답 반환
        ReviewLikeDto dto = new ReviewLikeDto();
        dto.setLiked(liked);
        dto.setLikeCount(review.getLikeCount());

        return dto;
    }

    //특정 리뷰 불러오기
    @GetMapping("/reviews/{reviewId}")
    public ReviewDto getReview(@PathVariable Long reviewId) throws JsonProcessingException {
        Review review = reviewRepository.findOne(reviewId);

        if (review == null) {
            throw new IllegalArgumentException("리뷰가 존재하지 않습니다.");
        }

        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        dto.setBusStopId(review.getBusStop().getId());
        dto.setBusStopName(review.getBusStop().getName());
        dto.setAuthorId(review.getAuthor().getId());
        dto.setAuthorNickName(review.getAuthor().getNickname());
        dto.setContent(review.getContent());
        dto.setDayTags(objectMapper.readValue(review.getDayTags(), String[].class));
        dto.setTimeTags(objectMapper.readValue(review.getTimeTags(), String[].class));
        dto.setCategoryTags(objectMapper.readValue(review.getCategoryTags(), String[].class));
        dto.setImageUrl(review.getImageUrl());
        dto.setLikeCount(review.getLikeCount());
        dto.setIsAnonymous(review.getIsAnonymous());
        dto.setCreatedAt(review.getCreatedAt());

        return dto;
    }



    //리뷰 수정
    @Transactional
    @PutMapping("/{reviewId}")
    public ReviewDto updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDto dto,
            Authentication authentication
    ) throws JsonProcessingException {
        Member member = (Member) authentication.getPrincipal();

        Review review = reviewRepository.findOne(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("리뷰가 존재하지 않습니다.");
        }

        // 작성자 아닌 경우
        if (!review.getAuthor().getId().equals(member.getId())) {
            throw new IllegalStateException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        // 수정 가능한 필드들 업데이트
        review.setContent(dto.getContent());
        review.setDayTags(mapper.writeValueAsString(dto.getDayTags()));
        review.setTimeTags(mapper.writeValueAsString(dto.getTimeTags()));
        review.setCategoryTags(mapper.writeValueAsString(dto.getCategoryTags()));
        review.setImageUrl(dto.getImageUrl());

        // response 생성
        ReviewDto response = new ReviewDto();
        response.setId(review.getId());
        response.setContent(review.getContent());
        response.setDayTags(objectMapper.readValue(review.getDayTags(), String[].class));
        response.setTimeTags(objectMapper.readValue(review.getTimeTags(), String[].class));
        response.setCategoryTags(objectMapper.readValue(review.getCategoryTags(), String[].class));
        response.setImageUrl(review.getImageUrl());
        response.setCreatedAt(review.getCreatedAt());
        response.setAuthorId(member.getId());
        response.setAuthorNickName(member.getNickname());
        response.setBusStopId(review.getBusStop().getId());
        response.setBusStopName(review.getBusStop().getName());

        return response;
    }



    @Transactional
    @DeleteMapping("/{reviewId}")
    public Map<String, Object> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        Member member = (Member) authentication.getPrincipal();

        Review review = reviewRepository.findOne(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("리뷰가 존재하지 않습니다.");
        }

        if (!review.getAuthor().getId().equals(member.getId())) {
            throw new IllegalStateException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        // 좋아요 먼저 삭제
        List<ReviewLike> likes = reviewLikeRepository.findAllByReviewId(reviewId);
        for (ReviewLike like : likes) {
            reviewLikeRepository.delete(like);
        }

        // 리뷰 삭제
        reviewRepository.delete(review);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "success");

        return response;
    }




}
