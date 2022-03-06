package com.aikaload.service;

import com.aikaload.asyncservice.SmsService;
import com.aikaload.dto.PaginationRequest;
import com.aikaload.entity.*;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.enums.TruckEnum;
import com.aikaload.pagination.PaginationResult;
import com.aikaload.pagination.PaginationUtil;
import com.aikaload.pagination.SqlQuery;
import com.aikaload.repo.*;
import com.aikaload.utils.VariableUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import com.aikaload.dto.TruckRequest;
import com.aikaload.dto.Response;

import java.math.BigInteger;
import java.util.*;


@Service("truckService")
@AllArgsConstructor
@Log4j2
public class TruckService {
    private final TruckModelRepo truckModelRepo;
    private final TruckTypeRepo truckTypeRepo;
    private final UserAccountRepo userAccountRepo;
    private final TruckInfoRepo truckInfoRepo;
    private final TruckImagesRepo truckImagesRepo;
    private final UserService userService;
    private final PaginationUtil paginationUtil;
    private final SmsService smsService;


    /**
     * This method helps to create truck in the database
     *
     * @param truckRequest contains truck request information
     * @return ResponseEntity<Object>
     */
    public ResponseEntity<Object> createTruck(TruckRequest truckRequest){
        //Validate incoming request
        String validationResponse = validateIncomingRequest(truckRequest);

        if(!validationResponse.equals(ResponseEnum.OK.getCode())){
            //Invalid request error returned
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), validationResponse , truckRequest));
        }

        //Create TruckInfo Object
        TruckInfo truckInfo = new TruckInfo();

        //Get TruckModel Information for Id sent
        Optional<TruckModel> truckModel = truckModelRepo.findById(truckRequest.getTruckModelId());
        if(!truckModel.isPresent()){
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid truckModelId Sent" , truckRequest));
        }
        truckInfo.setTruckModel(truckModel.get());


        //Get TruckType Information for Id sent
        Optional<TruckType> truckType = truckTypeRepo.findById(truckRequest.getTruckTypeId());
        if(!truckType.isPresent()){
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid truckTypeId Sent" , truckRequest));
        }
        truckInfo.setTruckType(truckType.get());
        truckInfo.setVerified(false);
        truckInfo.setTruckYear(truckRequest.getTruckYear());
        truckInfo.setPlateNumber(truckRequest.getPlateNumber());
        truckInfo.setCreatedDate(new Date());
        truckInfo.setTruckStatus(TruckEnum.AVAILABLE.getCode());

        //Get UserAccount information for the Id sent
        Optional<UserAccount> createdBy = userAccountRepo.findById(truckRequest.getCreatedBy());
        if(!createdBy.isPresent()){
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid createdBy Id Sent" , truckRequest));
        }
        truckInfo.setUserAccount(createdBy.get());
        TruckInfo createdTruckInfo = truckInfoRepo.save(truckInfo);

        if(truckRequest.getTruckPictures() != null || truckRequest.getTruckPictures().length > 0) {
            //Create Truck Images
            for (String truckPictures : truckRequest.getTruckPictures()) {
                TruckImages truckImages = new TruckImages();
                truckImages.setTruckInfo(createdTruckInfo);
                truckImages.setTruckImageUrl(truckPictures);
                truckImagesRepo.save(truckImages);
            }
        }
        truckRequest.setTruckId(createdTruckInfo.getId());

        String msg = String.format("%s (%s) just added a %s %s to AikaPremium",createdBy.get().getCompanyName(),createdBy.get().getUsername(),truckModel.get().getName(),
                truckType.get().getName());
        smsService.sendAdminPushNotification(msg,VariableUtil.CREATED_TRUCK_SLACK_URL);


        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Truck was successfully created",truckRequest));
    }




    /**
     * This method helps to create truck in the database
     *
     * @param editRequest contains truck request information
     * @return ResponseEntity<Object>
     */
    public ResponseEntity<Object> editTruck(TruckRequest editRequest){

        //Validate incoming request
        String validationResponse = validateIncomingRequest(editRequest);

        if(!validationResponse.equals(ResponseEnum.OK.getCode())){
            //Invalid request error returned
            return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), validationResponse , editRequest));
        }

        //Get TruckInfo Object
        Optional<TruckInfo> editTruckInfo = truckInfoRepo.findById(editRequest.getTruckId());

        if(!editTruckInfo.isPresent())
        return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "An invalid truckId was sent" , editRequest));


        //Get TruckModel Information for Id sent
        if(editRequest.getTruckModelId() != 0) {
             Optional<TruckModel> truckModel = truckModelRepo.findById(editRequest.getTruckModelId());
             if (!truckModel.isPresent()) {
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid truckModelId Sent", editRequest));
             }
            editTruckInfo.get().setTruckModel(truckModel.get());
        }

        //Get TruckType Information for Id sent
        if(editRequest.getTruckTypeId() != 0){
            Optional<TruckType> truckType = truckTypeRepo.findById(editRequest.getTruckTypeId());
            if (!truckType.isPresent()) {
                return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Invalid truckTypeId Sent", editRequest));
            }
            editTruckInfo.get().setTruckType(truckType.get());
        }

        if(editRequest.getTruckYear() != null)    editTruckInfo.get().setTruckYear(editRequest.getTruckYear());
        if(editRequest.getPlateNumber() != null)  editTruckInfo.get().setPlateNumber(editRequest.getPlateNumber());
        if(StringUtils.isNotBlank(editRequest.getVerificationStatus())){
            if(editRequest.getVerificationStatus().equalsIgnoreCase("true")) editTruckInfo.get().setVerified(true);
            if(editRequest.getVerificationStatus().equalsIgnoreCase("false")) editTruckInfo.get().setVerified(false);
        }

        editTruckInfo.get().setLastModifiedDate(new Date());

        if(editRequest.getTruckStatus() == TruckEnum.UNAVAILABLE.getCode() || editRequest.getTruckStatus() == TruckEnum.AVAILABLE.getCode() || editRequest.getTruckStatus() == TruckEnum.REMOVED.getCode())
        editTruckInfo.get().setTruckStatus(editRequest.getTruckStatus());

        TruckInfo savedTruckInfo = truckInfoRepo.save(editTruckInfo.get());

        if(editRequest.getTruckPictures() != null || editRequest.getTruckPictures().length > 0) {
            //Delete current images in the database
             truckImagesRepo.deleteByTruckInfo(savedTruckInfo);

             //Edit Truck Images
            for (String truckPictures : editRequest.getTruckPictures()) {
                TruckImages truckImages = new TruckImages();
                truckImages.setTruckInfo(savedTruckInfo);
                truckImages.setTruckImageUrl(truckPictures);
                truckImagesRepo.save(truckImages);
            }
        }

        editRequest.setTruckId(savedTruckInfo.getId());
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), "Truck was successfully edited",editRequest));
    }





    /**
     * This method returns all truck models
     * @return ResponseEntity
     */
    public ResponseEntity<Object> getTruckModels() {
        //Fetch all truck models from the database
        List<TruckModel> truckModels = truckModelRepo.findAll();
        if(truckModels.isEmpty()){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(),"Empty result set", null));
        }

        List<Object> results = new ArrayList<>();
        for(TruckModel truckModel: truckModels) {
            Map<String, Object> result = new HashMap<>();
            result.put("id",truckModel.getId());
            result.put("modelName", truckModel.getName());
            results.add(result);
        }
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),results));
    }


    /**
     * This method returns all trucks
     * @return ResponseEntity
     */
    public ResponseEntity<Object> getTrucks(Integer pageNo, Integer pageSize, String sortBy) {

        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC,sortBy));

        //Fetch all truck types from the database
        Page<TruckInfo> truckInfos = truckInfoRepo.findAll(paging);

        if(truckInfos.isEmpty()){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(),"Empty result set", null));
        }

        PaginationResult paginationResult = new PaginationResult();
        Long totalRecord = truckInfos.getTotalElements();
        paginationResult.setRecordsTotal(totalRecord.intValue());
        paginationResult.setPageNumber(pageNo);
        paginationResult.setPageSize(pageSize);
        paginationResult.setResults(formatTruckListInGenericListFormat(truckInfos.getContent()));
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),paginationResult));
    }


    public ResponseEntity<Object> getTrucksWithoutPagination(){
        Iterator<TruckInfo> truckInfos = truckInfoRepo.findAll().iterator();
       if(!truckInfos.hasNext()){
           return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(),"Empty result set", null));
       }
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),truckInfos));
    }


    /**
     * This method returns all trucks
     * @return  ResponseEntity contains response
     */
    public ResponseEntity findTrucks(PaginationRequest paginationRequest) {

        List<Object[]> resultSet = paginationUtil.executePaginatedQuery(SqlQuery.FIND_TRUCK_QUERY,paginationRequest);

        if(resultSet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(), ResponseEnum.NO_RECORD_FOUND.getMessage(), paginationRequest));
        }

        //Declare variables for result
        int totalRecords = 0;
        int counter = 0;
        List<Object> result = new ArrayList<>();

        //Loop through result set
        for(Object[] object: resultSet){
            Map<String,Object> truck = returnTruckInMapFormat(null,object,VariableUtil.NATIVE);
            if(counter == 0)
                totalRecords = Integer.parseInt(object[11].toString());
            result.add(truck);
        }
        PaginationResult paginationResult = new PaginationResult();
        paginationResult.setRecordsTotal(totalRecords);
        paginationResult.setPageNumber(paginationRequest.getPageNumber());
        paginationResult.setPageSize(paginationRequest.getPageSize());
        paginationResult.setResults(result);
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(), ResponseEnum.OK.getMessage(), paginationResult));
    }


    /**
     * This method returns truck information based on the truckId
     * @param truckId Information needed to retrieve truck information
     * @return truck information
     */
    public ResponseEntity<Object> getTruckById(int truckId) {
        Optional<TruckInfo> truckInfo = truckInfoRepo.findById(truckId);

        if(!truckInfo.isPresent()){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(),"Empty result set", null));
        }
        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),returnTruckInMapFormat(truckInfo.get(),null,VariableUtil.ORM)));
    }


    public Map<String,Object> returnTruckInMapFormat(TruckInfo truckInfo,Object[] object, String type){
        Map<String, Object> result = new HashMap<>();
        if(type.equals(VariableUtil.ORM)) {
            result.put("id", truckInfo.getId());
            result.put("truckModelId", truckInfo.getTruckModel().getId());
            result.put("truckModelName", truckInfo.getTruckModel().getName());
            result.put("plateNumber", truckInfo.getPlateNumber());
            result.put("truckTypeId", truckInfo.getTruckType().getId());
            result.put("truckTypeName", truckInfo.getTruckType().getName());
            result.put("truckYear", truckInfo.getTruckYear());
            result.put("truckStatus", truckInfo.getTruckStatus());
            result.put("isVerified", truckInfo.isVerified());
            result.put("createdDate", truckInfo.getCreatedDate());
            result.put("createdBy", userService.returnUserAccountInMapFormat(truckInfo.getUserAccount()));
            List<TruckImages> truckImages = truckImagesRepo.findByTruckInfo(truckInfo);
            result.put("truckImages", truckImages);
        }else{
            result.put("id",object[0]);
            result.put("truckModelId",object[5]);
            result.put("truckModelName",object[6]);
            result.put("plateNumber",object[1]);
            result.put("truckTypeId",object[7]);
            result.put("truckTypeName",object[8]);
            result.put("truckYear",object[2]);
            result.put("truckStatus",object[4]);
            result.put("createdDate",object[3]);
            result.put("isVerified",object[16]);

            Map<String,Object> createdBy = new HashMap<>();
            Optional<UserAccount> userAccountOptional = userAccountRepo.findById(((BigInteger)object[9]).longValue());

          /** createdBy.put("userId",object[9]);
            createdBy.put("companyName",object[10]);
            createdBy.put("city",object[13]);
            createdBy.put("state",object[14]);
            createdBy.put("routes",object[15]);
            createdBy.put("userStatus",object[12]);**/
            result.put("createdBy",userService.returnUserAccountInMapFormat(userAccountOptional.get()));

            List<TruckImages> truckImages = truckImagesRepo.getByTruckId(Long.parseLong(object[0].toString()));
            result.put("truckImages", truckImages);
        }
        return result;
    }


    /**
     * This method helps to validate incoming request
     *
     * @param truckRequest contains information to be validated
     * @return String contains validation response
     */
    private String validateIncomingRequest(TruckRequest truckRequest){
        if(truckRequest == null){
            //Bounce request
            return "Request cannot be empty";
        }
        return ResponseEnum.OK.getCode();
    }

    public ResponseEntity<Object> getTruckByUserId(Long userId) {
        Optional<UserAccount> userAccount = userAccountRepo.findById(userId);

        if(!userAccount.isPresent())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"Invalid UserId sent", userId));

        log.info("Name:::::{}",userAccount.get().getCompanyName());
         List<TruckInfo> truckInfoList = truckInfoRepo.findByUserAccount(userAccount.get());

        if(truckInfoList.isEmpty()){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new Response(ResponseEnum.NO_RECORD_FOUND.getCode(),"Empty result set", null));
        }

        return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),formatTruckListInGenericListFormat(truckInfoList)));

    }


    private List<Object> formatTruckListInGenericListFormat(List<TruckInfo> truckInfos){
        List<Object> results = new ArrayList<>();
        for(TruckInfo truckInfo: truckInfos){
            Map<String, Object> result = returnTruckInMapFormat(truckInfo,null,VariableUtil.ORM);
            results.add(result);
        }
        return results;
    }
}
