package com.coherent.unnamed.logic.service.impl;

import com.coherent.unnamed.logic.Constants.Constants;
import com.coherent.unnamed.logic.Exception.CustomException;
import com.coherent.unnamed.logic.dto.AttendenceDTO;
import com.coherent.unnamed.logic.dto.DetailsDTO;
import com.coherent.unnamed.logic.dto.LoggedDetailsDTO;
import com.coherent.unnamed.logic.dto.TimeLogsDTO;
import com.coherent.unnamed.logic.model.Attendance;
import com.coherent.unnamed.logic.model.TimeLogs;
import com.coherent.unnamed.logic.repository.AttendanceRepository;
import com.coherent.unnamed.logic.repository.TimeLogsRepository;
import com.coherent.unnamed.logic.service.LogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@EnableScheduling
public class LogicServiceImpl implements LogicService {

	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private TimeLogsRepository timeLogsRepository;

	@Scheduled(fixedRate = 10000)
	public void add() {
		List<TimeLogs> timeLogs = timeLogsRepository.findAll();
		List<Long> ids = new ArrayList<>();
		Set<Long> originalId = new HashSet<>();
		timeLogs.stream().forEach(timeLog -> {
			Long userId = timeLog.getUserIdFk();
			ids.add(userId);
		});
		originalId.addAll(ids);
		Date date  = new Date(System.currentTimeMillis()-24*60*60*1000);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String strDate= formatter.format(date);
		originalId.stream().forEach(id -> {
			final Long[] value = {null};
			final Long[] value1 = {null};
			/*List<Long> hourList = new ArrayList<>;*/
			List<TimeLogs> timeLogsPunchIn= timeLogsRepository.findByCreatedAtAndUserIdFkAndIsLogged(strDate,id,1);
			timeLogsPunchIn.stream().forEach(timeLogs1 -> {
				Date loginDate = null;
				loginDate = timeLogs1.getCreatedAt();
				long diffOfHours=loginDate.getTime();
				long changeToHours=diffOfHours / (60 * 60 * 1000) % 24;
			});
			List<TimeLogs> timeLogsPunchOut= timeLogsRepository.findByCreatedAtAndUserIdFkAndIsLogged(strDate,id,0);
			timeLogsPunchOut.stream().forEach(timeLogs2 -> {
				    Date logoutDate = null;
					logoutDate = timeLogs2.getCreatedAt();
					long diffOfHours2 = logoutDate.getTime();
					long changeToHours2 = diffOfHours2 / (60 * 60 * 1000) % 24;
					value1[0] = changeToHours2;
			});
			long diff = value1[0] - value[0];
			Attendance attendance = new Attendance();
			Timestamp timeStamp1 = new Timestamp(System.currentTimeMillis()-24*60*60*1000);
			attendance.setHours(diff);
			if (diff > 6) {
				attendance.setIsPresent("1");
			} else {
				attendance.setIsPresent("0");
			}
			attendance.setUserIdFk(id);
			attendance.setActive(true);
			attendance.setDeletedFlag(false);
			attendance.setCreatedBy("USER");
			attendance.setCreatedAt(timeStamp1);
			attendance.setModifiedAt(timeStamp1);
			attendance.setModifiedBy("NULL");
			attendanceRepository.save(attendance);
		});
	}

/*	public void updateStatus(TimeLogs timeLogs){
		if (timeLogs!=null){
			timeLogs.setStatus(false);
		}
	}*/

	@Override
	public String saveTimeLogs(TimeLogsDTO timeLogsDTO) {
		if (timeLogsDTO != null) {
			Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
			TimeLogs timeLogs = new TimeLogs();
			timeLogs.setLongitude(timeLogsDTO.getLongitude());
			timeLogs.setLatitude(timeLogsDTO.getLatitude());
			timeLogs.setIsLogged(timeLogsDTO.getIsLogged());
			timeLogs.setUserIdFk(timeLogsDTO.getUserIdFk());
			timeLogs.setActive(true);
			timeLogs.setDeletedFlag(false);
			timeLogs.setCreatedAt(timeStamp);
			timeLogs.setCreatedBy("USER");
			timeLogs.setModifiedAt(timeStamp);
			timeLogs.setModifiedBy("USER");
			timeLogsRepository.save(timeLogs);
			return Constants.SUCCESS;
		} else {
			throw new CustomException(Constants.ERROR_CODE, Constants.ERROR);
		}
	}


	@Override
	public String verifyLocation(String longitude, String latitude) {
		if (Objects.equals(longitude, Constants.LONGITUDE) && Objects.equals(latitude, Constants.LATITUDE)) {
			return Constants.IN_RANGE;
		} else {
			throw new CustomException(Constants.ERROR_CODE, Constants.NOT_IN_RANGE);
		}
	}


	@Override
	public List<AttendenceDTO> listByDayMonth(int year, int month) {
		List<AttendenceDTO> attendenceDTOList = new ArrayList<>();
		List<Attendance> attendances = attendanceRepository.findAllByCreatedAt(year, month);
		attendances.forEach(data -> {
			AttendenceDTO attendenceDTO = new AttendenceDTO();
			attendenceDTO.setCreatedAt(data.getCreatedAt());
			attendenceDTO.setIsPresent(data.getIsPresent());
			attendenceDTOList.add(attendenceDTO);
		});
		return attendenceDTOList;
	}


	@Override
	public DetailsDTO listByDate(int date, int user_id_fk) {
		List<LoggedDetailsDTO> loggedDetailsDTOList=new ArrayList<>();
		Attendance attendances = attendanceRepository.findByCreatedAtDate(date, user_id_fk);
		    DetailsDTO detailsDTO = new DetailsDTO();
			detailsDTO.setDate(date);
			detailsDTO.setHours(attendances.getHours());
		List<TimeLogs> timeLogs = timeLogsRepository.findAllByCreatedAtDate(date, user_id_fk);
		timeLogs.forEach(data -> {
			LoggedDetailsDTO loggedDetailsDTO=new LoggedDetailsDTO();
			loggedDetailsDTO.setCreatedAt(data.getCreatedAt());
			loggedDetailsDTO.setCreatedBy(data.getCreatedBy());
			loggedDetailsDTO.setIsLogged(data.getIsLogged());
			loggedDetailsDTOList.add(loggedDetailsDTO);
		});
		detailsDTO.setLogs(loggedDetailsDTOList);
		return detailsDTO;
	}
}



/*public void updateStatus(TimeLogs timeLogs){
		if (timeLogs!=null){
			timeLogs.setStatus(false);
		}
	}*/
	/*List<TimeLogs> timeLogsPunchIn= timeLogsRepository.findByCreatedAtAndUserIdFkAndIsLogged(strDate,id,1);
			timeLogsPunchIn.stream().forEach(data ->{
				if(data.getIsLogged()==1)
				{
					Date loginDate = null;
					loginDate=data.getCreatedAt();
				}
				else {
					Date logoutDate = null;
					logoutDate=data.getCreatedAt();
				}
				long diff = logoutDate.getTime() - loginDate.getTime();
			});*/
			/*List<TimeLogs> timeLogsPunchIn= timeLogsRepository.findByCreatedAtAndUserIdFkAndIsLogged(strDate,id,1);
			LongStream val=timeLogsPunchIn.stream().filter(data1 -> data1.getIsLogged()==1).mapToLong(data1 -> {
				Date login=data1.getCreatedAt();
				return login.getTime();
			});
			List<TimeLogs> timeLogsPunchOut= timeLogsRepository.findByCreatedAtAndUserIdFkAndIsLogged(strDate,id,0);
			LongStream val1=timeLogsPunchOut.stream().filter(data1 -> data1.getIsLogged()==0).mapToLong(data1 -> {
				Date logOut=data1.getCreatedAt();
				return logOut.getTime();
			});*/
		/*	if(timeLogsPunchOut==null)
			{
				attendance.setModifiedBy("System");
			}
			else
			{
				attendance.setModifiedBy("USER");
			}*/


		/*  timeLogs1.stream().filter(data -> data.getIsLogged() == 1).forEach(data ->{
             Date loginDate = data.getCreatedAt();

		  });
		  timeLogs1.stream().filter(data -> data.getIsLogged() == 0).forEach(data ->{
			  Date logoutDate = data.getCreatedAt();

		  });
		  long diff = logoutDate.getTime() - loginDate.getTime();*/











