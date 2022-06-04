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

/**
 * The rest controller (resource class) that contains url-endpoints for all the operations
 * that can be performed by user of account type LECTURER.
 * @author Theofanis Gkoufas
 *
 */
@RestController
@CrossOrigin(origins = {"http://localhost:8025", "http://localhost:8026", "http://localhost:8028"})
@RequestMapping(value = "/lecturers")
public class LecturerOperationsController {
	
	@Autowired
	LecturerServices lecturerServices;
	
	/**
	 * Retrieves the tasks (basically the assessments) for which the lecturer should set the upload
	 * and the deadline dates.
	 * @param lecturerID The id of the lecturer.
	 * @return The tasks for which the lecturer should set the upload/deadline dates.
	 */
	@RequestMapping(value = "/retrieveDaysToSet/{id}", method = RequestMethod.GET)
	public PendingTasksToSet retrieveDaysToSet(@PathVariable(name = "id") int lecturerID) {
		return lecturerServices.retrieveDaysToSet(lecturerID);
	}
	
	/**
	 * Inserts the upload/deadline dates that are set by the lecturer for a particular assessment.
	 * @param requiredData A map that should include two elements namely; uploadDate and deadlineDate.
	 */
	@RequestMapping(value = "/uploadSettedDates", method = RequestMethod.PUT)
	public void uploadSettedDates(@RequestBody Map<String, Object> requiredData) {
		lecturerServices.updateSettedDates((String) requiredData.get("uploadDate"),
										   (String) requiredData.get("deadlineDate"),
										   Integer.parseInt((String)requiredData.get("assessmentID")));
	}
	
	/**
	 * Creates a task given information about; the assessment id which associates with
	 * this task, the task description, and the date that this task should be sent (as
	 * an email) to the corresponding lecturer.
	 * @param taskDetailsAndAsmntId A map containing that should include elements in regards
	 * to; assessmentID, taskDescription, and dateToSend.
	 */
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
	
	/**
	 * Retrieves the to-do tasks that belong to a lecturer.
	 * @param lecturerID The primary key of the lecturer.
	 * @return A TasksToDo object containing a list of to-do tasks.
	 */
	@RequestMapping(value = "/getTasksToDo/{id}", method = RequestMethod.GET)
	public TasksToDo getTasksToDo(@PathVariable(name = "id") int lecturerID) {
		return lecturerServices.getTasksToDo(lecturerID);
	}
	
	/**
	 * Changes the visibility of a task (to-do task in particular). Basically,
	 * the boolean attribute isCompleted is changes from false to true.
	 * @param taskID The id of the task whose completion status should change.
	 */
	@RequestMapping(value = "/changeTaskVisibility/{taskID}", method = RequestMethod.PUT)
	public void changeTaskVisibility(@PathVariable(name = "taskID") int taskID) {
		lecturerServices.changeTaskVisibility(taskID);
	}

}
