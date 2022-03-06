package com.aikaload.service;

import com.aikaload.asyncservice.PushNotificationService;
import com.aikaload.asyncservice.SmsService;
import com.aikaload.dto.Response;
import com.aikaload.dto.ReviewRequest;
import com.aikaload.entity.UserAccount;
import com.aikaload.entity.UserReview;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.repo.AssignJobInfoRepo;
import com.aikaload.repo.UserAccountRepo;
import com.aikaload.repo.UserRatingLogRepo;
import com.aikaload.repo.UserReviewRepo;
import com.aikaload.utils.SmsUtils;
import com.aikaload.utils.VariableUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.util.*;


@Service("ReviewService")
@Log4j2
@RequiredArgsConstructor
public class ReviewService {
    private final UserAccountRepo userAccountRepo;
    private final UserReviewRepo userReviewRepo;
    private final PushNotificationService pushNotificationService;
    private final UserRatingLogRepo userRatingLogRepo;
    private final AssignJobInfoRepo assignJobInfoRepo;
    private final SmsService smsService;



    /**
     * This method helps a user write reviews
     * @param reviewRequest contains job information
     * @return  ResponseEntity contains response
     */
    public ResponseEntity writeReviews(ReviewRequest reviewRequest) {
        try {
            log.info("\n\n\n");
            log.info(">>>>>>>New Review request from calling client::::" + reviewRequest.toString());

            //Terminate request and return response back to calling client
            if (reviewRequest == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Request cannot be empty!", reviewRequest));

            Optional<UserAccount> reviewee = userAccountRepo.findById(reviewRequest.getRevieweeId());
            if (!reviewee.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid revieweeId sent", reviewRequest));

            Optional<UserAccount> reviewer = userAccountRepo.findById(reviewRequest.getReviewerId());
            if (!reviewer.isPresent())
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid reviewerId sent", reviewRequest));

            if (reviewRequest.getRevieweeId().equals(reviewRequest.getReviewerId()))
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "reviewerId cannot be the same with revieweeId", reviewRequest));

            //Populate UserReview Object
            userReviewRepo.save(new UserReview(reviewRequest.getRevieweeId(), reviewRequest.getReviewerId(), reviewRequest.getComment(), new Date()));

            //Send SMS
            SmsUtils smsUtils = new SmsUtils("Aikaload Review Message",String.format(VariableUtil.PROFILE_REVIEW,reviewee.get().getCompanyName(),
                    reviewer.get().getCompanyName()),reviewee.get().getMobilePhone(),"234");
            smsService.sendSms(smsUtils,reviewee.get().getId(),reviewee.get().isSmsNotification(),reviewee.get().isWhatsappNotification(),reviewee.get().getWalletBalance());


            String msg = String.format("%s reviewed the %s saying (%s)",reviewer.get().getCompanyName(),reviewee.get().getCompanyName(),reviewRequest.getComment());
            smsService.sendAdminPushNotification(msg,VariableUtil.REVIEW_SLACK_URL);

            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Review successfully submitted", reviewRequest));
        } catch (Exception e) {
            log.error("<<<<<<An error occurred while trying to edit job:::" + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), "Unable to process your request,try again later ", reviewRequest));
    }






        /**
         * This method helps to get users reviews and ratings
         * @param userId unique identifier for a user
         * @return ResponseEntity contains response
         */
        public ResponseEntity getReviewsAndAggregateRatingByUserId(Long userId) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            try {
                Map<String, Object> result = new HashMap<>();

                //Call User-Review table
                List<UserReview> userReviewList = userReviewRepo.findByRevieweeId(userId);

                if(!userReviewList.isEmpty()){
                    List<Object> reviews = new ArrayList<>();
                    for(UserReview userReview: userReviewList){
                        Map<String,Object> review  = new HashMap<>();
                        Optional<UserAccount> user = userAccountRepo.findById(userReview.getReviewerId());
                        review.put("comment",userReview.getComment());
                        review.put("reviewerInfo",UserService.returnUserAccountInMapFormat(user.get()));
                        review.put("createDate",userReview.getCreatedDate());
                        reviews.add(review);
                    }
                    result.put("reviews", reviews);
                }

                //Get Completed Job Number
                Optional<UserAccount> truckOwner = userAccountRepo.findById(userId);
                if(truckOwner.isPresent()) {
                    if (truckOwner.get().getUserRole().getUserRoleName().equalsIgnoreCase(VariableUtil.TRUCK_OWNER)) {
                        int closedJobCount = assignJobInfoRepo.countByAssignedToAndIsTaskClosed(truckOwner.get(), true);
                        result.put("closedJobCount",closedJobCount);
                    }
                }

                //Call
                Double averageRatings = userRatingLogRepo.getAverageRatingsByUserId(userId);
                if (averageRatings != null)
                    result.put("averageRatings", decimalFormat.format(averageRatings));

                if(!result.isEmpty())
                    return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Successful", result));

                else
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), "No record was found", userId));
            }catch(Exception e){
                log.error("An error occurred while trying to retrieve reviews and aggregate ratings:::{}",e);
            }
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), "An error occurred while trying to retrieve information", userId));
        }


        public ResponseEntity deleteReview(Long reviewId){
            if(reviewId == null)
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid reviewId!", reviewId));

            userReviewRepo.deleteById(reviewId);

            return ResponseEntity.status(HttpStatus.OK).body(new Response(ResponseEnum.OK.getCode(), "Review successfull removed", reviewId));
        }

}
