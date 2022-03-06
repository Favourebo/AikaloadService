package com.aikaload.pagination;

import com.aikaload.dto.PaginationRequest;
import com.aikaload.utils.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;


@Repository
@RequiredArgsConstructor
@Slf4j
public class PaginationUtil{

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${sql.version}")
    private String sqlVersion;

    /** The Constant BLANK. */
    private static final String BLANK = "";

    /** The Constant SPACE. */
    private static final String SPACE = " ";

    /** The Constant LIKE_PREFIX. */
    private static final String LIKE_PREFIX = " LIKE Lower('%";

    /** The Constant LIKE_SUFFIX. */
    private static final String LIKE_SUFFIX = "%') ";

    /** The Constant AND. */
    private static final String AND = " AND ";

    /** The Constant OR. */
    private static final String OR = " OR ";

    /** The Constant ORDER_BY. */
    private static final String ORDER_BY = " ORDER BY ";

    private static final String BRKT_OPN = " ( ";

    private static final String BRKT_CLS = " ) ";

    /** The Constant COMMA. */
    private static final String COMMA = " , ";

    /** The Constant PAGE_NO. */
    public static final String PAGE_NO = "start";

    /** The Constant PAGE_SIZE. */
    public static final String PAGE_SIZE = "length";

    /** The Constant MYSQL. */
    public static final String MYSQL = "MYSQL";

    /** The Constant MYSQL. */
    public static final String POSTGRES = "POSTGRES";

    public String buildPaginatedQuery(String baseQuery, PaginationRequest paginationRequest){
        StringBuilder sb = new StringBuilder("SELECT FILTERED_ORDERED_RESULTS.* FROM (SELECT BASE_INFO.* FROM ( #BASE_QUERY# ) BASE_INFO #WHERE_CLAUSE#  ) FILTERED_ORDERED_RESULTS #ORDER_CLAUSE# "+getLimitString(sqlVersion));
        String finalQuery = null;
        if(!CommonUtil.isObjectEmpty(paginationRequest)){
            finalQuery = sb.toString().replaceAll("#BASE_QUERY#", baseQuery)
                    .replaceAll("#WHERE_CLAUSE#", ((CommonUtil.isObjectEmpty(getFilterByClause(paginationRequest.getFilterBy(),paginationRequest.isGlobalSearch()))) ? "" : " WHERE ") + getFilterByClause(paginationRequest.getFilterBy(),paginationRequest.isGlobalSearch()))
                    .replaceAll("#ORDER_CLAUSE#", getOrderByClause(paginationRequest.getOrderBy()))
                    .replaceAll("#PAGE_NUMBER#",paginationRequest.getPageNumber().toString())
                    .replaceAll("#PAGE_SIZE#", paginationRequest.getPageSize().toString());
        }
        return (null == finalQuery) ?  baseQuery : finalQuery;
    }


    private String getLimitString(String sqlVersion){
        if(sqlVersion.equals(MYSQL))
            return "LIMIT #PAGE_NUMBER#, #PAGE_SIZE#";

        if(sqlVersion.equals(POSTGRES))
            return "LIMIT #PAGE_SIZE# OFFSET #PAGE_NUMBER#";

        return "";
    }





    private  String getFilterByClause(Map<String,String> filterBy, boolean globalSearch){
        StringBuilder fbsb = null;
          String charType = null;

        if(sqlVersion.equals(MYSQL))
            charType = "char";

        if(sqlVersion.equals(POSTGRES))
            charType = "varchar";

            if (!isFilterByEmpty(filterBy)) {
            Iterator<Map.Entry<String, String>> fbit = filterBy.entrySet().iterator();

            while (fbit.hasNext()) {
                Map.Entry<String, String> pair =  fbit.next();

                if(null == fbsb) {
                    fbsb = new StringBuilder();
                    fbsb.append(BRKT_OPN);

                    fbsb.append(SPACE)
                            .append(BRKT_OPN)
                            .append(String.format("Lower(cast(%s as %s))",pair.getKey(),charType))
                            .append(LIKE_PREFIX)
                            .append(pair.getValue())
                            .append(LIKE_SUFFIX)
                            .append(BRKT_CLS);

                } else {
                    fbsb.append(globalSearch ? OR : AND)
                            .append(BRKT_OPN)
                            .append(String.format("Lower(cast(%s as %s))",pair.getKey(),charType))
                            .append(LIKE_PREFIX)
                            .append(pair.getValue())
                            .append(LIKE_SUFFIX)
                            .append(BRKT_CLS);

                }
            }
            fbsb.append(BRKT_CLS);
        }
        return (null == fbsb) ? BLANK :   fbsb.toString();
    }


    /**
     * Gets the order by clause.
     *
     * @return the order by clause
     */
    public String getOrderByClause(Map<String, SortOrder> sortBy) {

        StringBuilder sbsb = null;

        if(!isSortByEmpty(sortBy)) {
            Iterator<Map.Entry<String, SortOrder>> sbit = sortBy.entrySet().iterator();

            while (sbit.hasNext()) {
                Map.Entry<String, SortOrder> pair =  sbit.next();
                if(null == sbsb) {
                    sbsb = new StringBuilder();
                    sbsb.append(ORDER_BY).append(pair.getKey()).append(SPACE).append(pair.getValue());
                } else {
                    sbsb.append(COMMA).append(pair.getKey()).append(SPACE).append(pair.getValue());
                }
            }
        }

        return (null == sbsb) ? BLANK : sbsb.toString();
    }



    private static boolean isFilterByEmpty(Map<String,String> filterBy) {
        if(null == filterBy  || filterBy.size() == 0) {
            return true;
        }
        return false;
    }

    private static boolean isSortByEmpty(Map<String, SortOrder> sortBy) {
        if(null == sortBy  || sortBy.size() == 0) {
            return true;
        }
        return false;
    }


    public List<Object[]> executePaginatedQuery(String baseQuery,PaginationRequest paginationRequest){
        String paginatedQuery = buildPaginatedQuery(baseQuery,paginationRequest);
        log.info(">>>>>paginatedQuery:::::"+paginatedQuery);
        Query query = entityManager.createNativeQuery(paginatedQuery);
        List<Object[]> resultSet = query.getResultList();
        return resultSet;
    }


    public List<Object[]> executeQuery(String query){
        log.info(">>>>>Query:::::"+query);
        Query q = entityManager.createNativeQuery(query);
        List<Object[]> resultSet = q.getResultList();
        return resultSet;
    }

}
