package road_friend.road_friend_server.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import road_friend.road_friend_server.Api.BusStopService;
import road_friend.road_friend_server.Dto.BusStopDto;
import road_friend.road_friend_server.Dto.ReviewDto;
import road_friend.road_friend_server.Repository.BusStopRepository;
import road_friend.road_friend_server.Repository.ReviewRepository;
import road_friend.road_friend_server.domain.BusStop;
import road_friend.road_friend_server.domain.Member;
import road_friend.road_friend_server.domain.Review;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bus-stops")
public class BusStopController {

    private final BusStopService busStopService;
    private final BusStopRepository busStopRepository;
    private final ReviewRepository reviewRepository;


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
    public ReviewDto createReview(@PathVariable("busStopId")Long busStopId, @RequestBody ReviewDto dto, Authentication authentication){
        Member member = (Member) authentication.getPrincipal();
        Review review = new Review();
        review.setBusStop(busStopRepository.findOne(busStopId));
        review.setAuthor(member);
        review.setContent(dto.getContent());
        review.setDayTags(dto.getDayTags());
        review.setCategoryTags(dto.getCategoryTags());
        review.setImageUrl(dto.getImageUrl());
        review.setIsAnonymous(dto.getIsAnonymous());
        review.setLikeCount(0);
        review.setTimeTags(dto.getTimeTags());
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
    public List<ReviewDto> getReviews(@PathVariable("busStopId") Long busStopId){
        List<Review> reviews = reviewRepository.getReviewsByBusStop(busStopId);

        List<ReviewDto> dtos = new ArrayList<>();

        for(Review review : reviews){
            ReviewDto dto = new ReviewDto();
            dto.setBusStopId(busStopId);
            dto.setContent(review.getContent());
            dto.setDayTags(review.getDayTags());
            dto.setCreatedAt(review.getCreatedAt());
            dto.setImageUrl(review.getImageUrl());
            dto.setLikeCount(review.getLikeCount());
            dto.setTimeTags(review.getTimeTags());
            dto.setIsAnonymous(review.getIsAnonymous());
            dto.setAuthorId(review.getAuthor().getId());
            dto.setId(review.getId());
            dto.setCategoryTags(review.getCategoryTags());
            dto.setAuthorNickName(review.getAuthor().getNickname());
            dto.setBusStopName(review.getBusStop().getName());

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



}
