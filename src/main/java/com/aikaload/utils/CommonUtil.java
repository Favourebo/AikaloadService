package com.aikaload.utils;

import com.aikaload.enums.SettlementEnum;
import lombok.extern.log4j.Log4j2;
import org.codehaus.jackson.map.ObjectMapper;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
public class CommonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final DecimalFormat df = new DecimalFormat("#,###.00");

    public static String formatAmount(BigDecimal amount){
        return df.format(amount);
    }

    public static Date getExpirationDate(Date subscriptionDate, int duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(subscriptionDate);
        cal.add(Calendar.DATE, duration);
        java.util.Date expirationDate = cal.getTime();
        return expirationDate;
    }

    public static Date formatDate(String date) {
        Date xDate = null;
        try {
            xDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
        } catch (Exception e) {
            log.error(">>>>An error occurred while trying to format date:::"+date);
        }
        return xDate;
    }

    /**
     * Checks if is object empty.
     *
     * @param object the object
     * @return true, if is object empty
     */
    public static boolean isObjectEmpty(Object object) {
        if(object == null) return true;
        else if(object instanceof String) {
            if (((String)object).trim().length() == 0) {
                return true;
            }
        } else if(object instanceof Collection) {
            return isCollectionEmpty((Collection<?>)object);
        }
        return false;
    }

    /**
     * Checks if is collection empty.
     *
     * @param collection the collection
     * @return true, if is collection empty
     */
    private static boolean isCollectionEmpty(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    public static String returnToken(){
       return UUID.randomUUID().toString()+new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
    }

    public static String getCode(){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((1000 + rnd.nextInt(9000)) + "S");
        for (int i = 0; i < 4; i++)
            sb.append(chars[rnd.nextInt(chars.length)]);

        return sb.toString();
    }

    public static boolean resolveCodeStatus(String codeType, String status) {
        if (codeType.equals(VariableUtil.LOAD_CODE)) {
            if (status.equals(SettlementEnum.FRESH.getMessage()))
                return false;
            if (status.equals(SettlementEnum.START_SETTLEMENT_REQUEST.getMessage()) || status.equals(SettlementEnum.END_SETTLEMENT_REQUEST.getMessage()))
                return true;
        } else if (codeType.equals(VariableUtil.COMPLETION_CODE)) {
            if (status.equals(SettlementEnum.FRESH.getMessage()) || status.equals(SettlementEnum.START_SETTLEMENT_REQUEST.getMessage()))
                return false;
            if (status.equals(SettlementEnum.END_SETTLEMENT_REQUEST.getMessage()))
                return true;
        }
        return false;
    }

    public static String convertToJson(String object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static  List<Object> getPhoneNumberAndCountryId(String xphoneNumber){
        List<Object> results = new ArrayList<>();
        Map<String,Object> result = new HashMap<>();

        if(xphoneNumber.startsWith("+")){
            String countryId = xphoneNumber.substring(0,4);
            result.put("countryId",countryId);
            result.put("phoneNumber",xphoneNumber.replace(countryId,""));
            results.add(result);
        }else{
            result.put("countryId","+234");
            result.put("phoneNumber",xphoneNumber);
            results.add(result);
        }
        return results;
    }


}
