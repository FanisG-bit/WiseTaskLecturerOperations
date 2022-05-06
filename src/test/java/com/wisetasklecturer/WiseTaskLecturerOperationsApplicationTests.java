package com.wisetasklecturer;

import static org.hamcrest.CoreMatchers.is;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.wisetasklecturer.controllers.LecturerOperationsController;
import com.wisetasklecturer.entities.PendingTaskToSet;
import com.wisetasklecturer.entities.PendingTasksToSet;
import com.wisetasklecturer.entities.TaskToDo;
import com.wisetasklecturer.entities.TasksToDo;
import com.wisetasklecturer.services.LecturerServices;

@WebMvcTest(controllers = LecturerOperationsController.class)
class WiseTaskLecturerOperationsApplicationTests {

	@MockBean
	private LecturerServices lecturerServices;
	
	@Autowired
    private MockMvc mockMvc;
	
	@Test
	public void testRetrieveDaysToSet() throws Exception {
		LocalDate date = LocalDate.parse("2022-04-01");
		PendingTasksToSet pendingTasksToSet = new PendingTasksToSet();
		pendingTasksToSet.setPendingTasksToSetList(new ArrayList<PendingTaskToSet>());
		pendingTasksToSet.getPendingTasksToSetList().add(PendingTaskToSet.builder()
				 .moduleName("Continuous and Agile Software Engineering")
				 .curriculum("WM")
				 .assessmentType("ASSESSED_LAB")
				 .assessmentWeeks("00000100000000000")
				 .assessmentId(3050)
				 .week1BeginDate(Date.valueOf(date))
				 .assessmentWeight(10)
				 .build()
				 );
		pendingTasksToSet.getPendingTasksToSetList().add(PendingTaskToSet.builder()
				 .moduleName("Continuous and Agile Software Engineering")
				 .curriculum("WM")
				 .assessmentType("PROJECT")
				 .assessmentWeeks("00000001111111000")
				 .assessmentId(3051)
				 .week1BeginDate(Date.valueOf(date))
				 .assessmentWeight(40)
				 .build()
				 );
		Mockito.when(lecturerServices.retrieveDaysToSet(1)).thenReturn(pendingTasksToSet);
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/lecturers/retrieveDaysToSet/1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.pendingTasksToSetList.length()", is(2)))
				.andReturn();
		Assertions.assertEquals("application/json", response.getResponse().getContentType());
	}
	
	@Test
	public void testGetTasksToDo() throws Exception {
		TasksToDo tasksToDo = new TasksToDo();
		tasksToDo.setTasksToDo(new ArrayList<>());
		tasksToDo.getTasksToDo().add(TaskToDo.builder()
				.taskId(0)
				.taskDescription("Today is the last day to finish creating the assessment."
						+ "\\nAssessment Details:\\nType: PROJECT\\nWeight: 40%"
						+ "\\nModule Name: Continuous and Agile Software Engineering "
						+ "\\nModule Code:  CCP6418 \\nModule Curriculum: ASE."
						+ "\\nModule Moderator: null")
				.dateToSend(LocalDate.parse("2022-05-20"))
				.moduleName("Continuous and Agile Software Engineering")
				.curriculum("ASE")
				.assessmentType("PROJECT")
				.build());
		tasksToDo.getTasksToDo().add(TaskToDo.builder()
				.taskId(1)
				.taskDescription("Today is the last day for assessing the papers given for the assessment."
						+ "\\nAssessment Details:\\nType: PROJECT\\nWeight: 40%"
						+ "\\nModule Name: Continuous and Agile Software Engineering "
						+ "\\nModule Code:  CCP6418 \\nModule Curriculum: ASE"
						+ "\\nModule Moderator: null")
				.dateToSend(LocalDate.parse("2022-07-01"))
				.moduleName("Continuous and Agile Software Engineering")
				.curriculum("ASE")
				.assessmentType("PROJECT")
				.build());
		tasksToDo.getTasksToDo().add(TaskToDo.builder()
				.taskId(2)
				.taskDescription("Today the feedback of the assessment should be sent to the moderator."
						+ "\\nAssessment Details:\\nType: PROJECT\\nWeight: 40%"
						+ "\\nModule Name: Continuous and Agile Software Engineering "
						+ "\\nModule Code:  CCP6418 \\nModule Curriculum: ASE"
						+ "\\nModule Moderator: null")
				.dateToSend(LocalDate.parse("2022-07-01"))
				.moduleName("Continuous and Agile Software Engineering")
				.curriculum("ASE")
				.assessmentType("PROJECT")
				.build());
		Mockito.when(lecturerServices.getTasksToDo(1)).thenReturn(tasksToDo);
		MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get("/lecturers/getTasksToDo/1"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.tasksToDo.length()", is(3)))
				.andReturn();
		Assertions.assertEquals("application/json", response.getResponse().getContentType());
	}
	
}
