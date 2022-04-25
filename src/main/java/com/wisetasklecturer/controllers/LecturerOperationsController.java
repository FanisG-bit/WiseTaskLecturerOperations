package com.wisetasklecturer.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.wisetasklecturer.entities.*;
import com.wisetasklecturer.services.LecturerServices;

@RestController
@CrossOrigin(origins = {"http://localhost:8025", "http://localhost:8026", "http://localhost:8028"})
@RequestMapping(value = "/lecturers")
public class LecturerOperationsController {
	
	@Autowired
	LecturerServices lecturerServices;
	
	@RequestMapping(value = "/retrieveDaysToSet/{id}", method = RequestMethod.GET)
	public PendingTasksToSet retrieveDaysToSet(@PathVariable(name = "id") int lecturerID) {
		return lecturerServices.retrieveDaysToSet(lecturerID);
	}
	
	@RequestMapping(value = "/uploadSettedDates", method = RequestMethod.PUT)
	public void uploadSettedDates(@RequestBody Map<String, Object> requiredData) {
		lecturerServices.updateSettedDates((String) requiredData.get("uploadDate"),
										   (String) requiredData.get("deadlineDate"),
										   Integer.parseInt((String)requiredData.get("assessmentID")));
	}
	
	@RequestMapping(value = "/tasks", method = RequestMethod.POST)
	public void addTask(@RequestBody Map<String, Object> taskDetailsAndAsmntId) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Assessment assessment =
				lecturerServices.getAssessment((int) taskDetailsAndAsmntId.get("assessmentID"));
		Task newTask = Task.builder()
				.taskDescription((String) taskDetailsAndAsmntId.get("taskDescription"))
				.dateToSend(LocalDate.parse((String)taskDetailsAndAsmntId.get("dateToSend"), dtf))
				.taskBelongsToAssessment(assessment)
				.emailAddressToSend(null)
				.build();
		lecturerServices.addTask(newTask);
	}
	
	@RequestMapping(value = "/getTasksToDo/{id}", method = RequestMethod.GET)
	public TasksToDo getTasksToDo(@PathVariable(name = "id") int lecturerID) {
		return lecturerServices.getTasksToDo(lecturerID);
	}
	
	@RequestMapping(value = "/changeTaskVisibility/{taskID}", method = RequestMethod.PUT)
	public void changeTaskVisibility(@PathVariable(name = "taskID") int taskID) {
		lecturerServices.changeTaskVisibility(taskID);
	}

}
