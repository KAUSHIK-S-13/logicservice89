package com.coherent.unnamed.logic.controller;

import com.coherent.unnamed.logic.Response.BaseResponse;
import com.coherent.unnamed.logic.dto.AttendenceDTO;
import com.coherent.unnamed.logic.dto.DetailsDTO;
import com.coherent.unnamed.logic.dto.TimeLogsDTO;
import com.coherent.unnamed.logic.service.LogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value ="/unnamed-logic-service")
public class LogicController {
	
	@Autowired
	private LogicService logicService;


	@PostMapping(value ="/data/registerattendance")
	public BaseResponse saveTimeLogs(@RequestBody TimeLogsDTO timeLogsDTO){
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setData(logicService.saveTimeLogs(timeLogsDTO));
		return baseResponse;
	}


	@PostMapping(value = "/data/verifylocation")
	public BaseResponse verifyLocation(@RequestParam String longitude, @RequestParam String latitude){
		 String response=logicService.verifyLocation(longitude,latitude);
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setData(response);
		return baseResponse;
	}


	@GetMapping(value = "/data/listbydaysbymonth")
	public BaseResponse<List<AttendenceDTO>> listByDayMonth(@RequestParam int year,@RequestParam int month) {
		BaseResponse<List<AttendenceDTO>> baseResponse = null;
		baseResponse = BaseResponse.<List<AttendenceDTO>>builder().Data(logicService.listByDayMonth(year, month)).build();
		return baseResponse;
	}

	@GetMapping(value = "/data/listbydate")
	public BaseResponse<DetailsDTO> listByDate(@RequestParam int date, @RequestParam int user_id_fk) {
		BaseResponse<DetailsDTO> baseResponse = null;
		baseResponse = BaseResponse.<DetailsDTO>builder().Data(logicService.listByDate(date, user_id_fk)).build();
		return baseResponse;
	}




}
