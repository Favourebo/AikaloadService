package com.aikaload.pagination;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * The Class PaginationResult.
 *
 * @author EBO FAVOUR
 */
@Getter
@Setter
public class PaginationResult {

    /** The page number. */
    private int pageNumber;

    /** The page size. */
    private int pageSize;

    /** The records total. */
    private int recordsTotal;

    /** The list of data objects. */
    List<Object> results;
}
