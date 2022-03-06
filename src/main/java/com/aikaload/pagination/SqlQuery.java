package com.aikaload.pagination;

public class SqlQuery {
    public static final String FIND_JOB_QUERY= "select j.id,j.job_reference_number as jobReferenceNumber,j.delivery_address_state as dropOffAddressState,j.delivery_address_city as dropOffAddressCity,j.pick_up_address_state as pickUpAddressState,j.pick_up_address_city as pickUpAddressCity,l.id as materialTypeId,l.name as materialTypeName,t.id as truckTypeId,\n" +
            "t.truck_name as truckTypeName,j.job_summary as jobSummary,j.date_created as createdDate,j.fixed_pick_up_date as pickUpDate, j.truck_no as truckNo,u.id as userId, u.company_name as companyName, (SELECT COUNT(1) FROM job_info) AS totalRecords,u.user_status as userStatus,j.job_status as jobStatus,ll.id as loadLevelId,ll.name as loadLevelName,u.user_profile_url as userProfileUrl,j.pick_up_address as pickUpAddress,j.delivery_address as deliveryAddress from job_info j \n" +
            "left join load_category l on j.load_category_id = l.id\n" +
            "left join truck_type t on j.truck_type_id = t.id\n" +
            "left join load_level ll on j.load_level_id = ll.id\n" +
            "left join user_account u on j.user_account_id = u.id";

    public static final String FIND_TRUCK_QUERY="select t.id,t.destination_city as plateNumber,t.destination_state as truckYear,t.created_date as createdDate,t.truck_status as truckStatus, tm.id as truckModelId, tm.name as truckModelName,\n" +
            "tt.id as truckTypeId,tt.truck_name as truckTypeName,u.id as userId, u.company_name as companyName, (SELECT COUNT(1) FROM truck_info) AS totalRecords,u.user_status as userStatus,u.city,u.state,u.routes,t.is_verified from truck_info t\n" +
            "left join truck_model tm on t.truck_model_id = tm.id\n" +
            "left join truck_type tt on t.truck_type_id = tt.id\n" +
            "left join user_account u on t.user_account_id = u.id";

    public static final String SETTLEMENT_HISTORY_QUERY = "select l.completion_code,l.load_code,l.truck_id,l.admin_status,l.created_date,a.job_info_id,a.offer_amount from load_completion_code_manager l join assign_job_info a on l.assign_job_id = a.id where assigned_to=%s";
}
