package com.aikaload.dto;

import com.aikaload.pagination.SortOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;


@Getter
@Setter
public class PaginationRequest {
    /**
     * Where record should start reading from
     */
    private Integer pageNumber;

    /**
     * Max number of request that should be returned per call
     */
    private Integer pageSize;


    /**
     * Filter by the following, if its left empty, it returns everything in the database without filter
     */
    private Map<String,String> filterBy;

    /**
     * order by the following, if its left empty, it returns everything in the database without filter
     */
    private Map<String, SortOrder> orderBy;


    /**
     * Global Search
     */
    @ApiModelProperty(name =  "globalSearch", dataType = "Boolean", value = "could be TRUE(for OR statement) or FALSE(for AND statement)", example = "TRUE", required = true)
    private boolean globalSearch;
}
