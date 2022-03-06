package com.aikaload.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Response {
	private String responseCode;
	private String responseMessage;
	private Object data;
}
