package com.wisetasklecturer.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.wisetasklecturer.entities.*;
import com.wisetasklecturer.repositories.TasksRepository;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class LecturerServices {
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	TasksRepository tasksRepository;
	
	public PendingTasksToSet retrieveDaysToSet(int lecturerID) {
		User lecturerUser = requestUser(lecturerID);
		DataSource dataSource = context.getBean(DataSource.class);
		PendingTasksToSet pendingTasksToSet = context.getBean(PendingTasksToSet.class);
		String sql = "SELECT M.module_name, M.curriculum, A.assessment_type, "
				   + "A.assessment_weeks, A.assessment_id, S.week1_begin_date, A.assessment_weight "
				   + "FROM modules M, assessments A, entries E, users U, settings S"
				   + " WHERE E.entry_id = M.module_belongsTo_entry AND "
				   + "M.module_id = A.assessment_belongsTo_module AND "
				   + "U.user_id = E.user_id AND "
				   + "E.entry_id = S.entry_FK AND "
				   + "M.primary_lecturer LIKE ? AND "
				   + "A.areDatesSet = 0";
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, "%" + lecturerUser.getUsername() + "%");
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) {
				pendingTasksToSet.getPendingTasksToSetList()
											.add((PendingTaskToSet.builder()
												.moduleName(resultSet.getString(1))
												.curriculum(resultSet.getString(2))
												.assessmentType(resultSet.getString(3))
												.assessmentWeeks(resultSet.getString(4))
												.assessmentId(resultSet.getInt(5))
												.week1BeginDate(resultSet.getDate(6))
												.assessmentWeight(resultSet.getInt(7))
												.build()));
			}
			statement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pendingTasksToSet;
	}

	private User requestUser(int lecturerID) {
		RestTemplate bean = context.getBean(RestTemplate.class);
		return bean.getForObject("http://localhost:8028/users/" + lecturerID, User.class);
	} 
	
	public void updateSettedDates(String uploadDate, String deadlineDate, int assessmentID) {
		DataSource dataSource = context.getBean("dataSource", DataSource.class);
		Connection connection;
		try {
			connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(
					"UPDATE assessments SET assessment_upload_date = ? "
					+ ", assessment_deadline_date = ? "
					+ ", areDatesSet = 1 "
					+ "WHERE assessment_id = ?");
			statement.setDate(1, java.sql.Date.valueOf(uploadDate));
			statement.setDate(2, java.sql.Date.valueOf(deadlineDate));
			statement.setInt(3, assessmentID);
			statement.executeUpdate();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addTask(Task task) {
		tasksRepository.save(task);
	}
	
	public Assessment getAssessment(int assessmentId) {
		RestTemplate restTemplate = context.getBean(RestTemplate.class);
		Assessment assessment = restTemplate.getForObject("http://localhost:8026/admin/assessments/" + assessmentId, 
								  Assessment.class);
		return assessment;
	}
	
	public TasksToDo getTasksToDo(int lecturerID) {
		User lecturer = getLecturerBasedOnId(lecturerID);
		DataSource dataSource = context.getBean(DataSource.class);
		TasksToDo tasksToDo = context.getBean(TasksToDo.class);
		Connection connection;
		String sql = "SELECT T.task_description, T.date_to_send, "
				+ "M.module_name, M.curriculum, A.assessment_type, "
				+ "T.task_id "
				+ "FROM tasks T, users U, assessments A, modules M, entries E, settings S "
				+ "WHERE T.task_belongs_to_assessment = A.assessment_id "
				+ "AND A.assessment_belongsTo_module = M.module_id "
				+ "AND M.module_belongsTo_entry = E.entry_id "
				+ "AND S.entry_FK = E.entry_id "
				+ "AND E.user_id = U.user_id "
				+ "AND M.primary_lecturer LIKE ? "
				+ "AND T.isCompleted = 0";
		try {
			connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, "%" + lecturer.getUsername() + "%");
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) {
				tasksToDo.getTasksToDo().add(TaskToDo.builder()
						.taskDescription(resultSet.getString(1))
						.dateToSend(resultSet.getDate(2).toLocalDate())
						.moduleName(resultSet.getString(3))
						.curriculum(resultSet.getString(4))
						.assessmentType(resultSet.getString(5))
						.taskId(resultSet.getInt(6))
						.build());
			}
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tasksToDo;
	}
	
	private User getLecturerBasedOnId(int lecturerID) {
		RestTemplate restTemplate = context.getBean(RestTemplate.class);
		User lecturer = restTemplate.getForObject("http://localhost:8028/users/" + lecturerID, User.class);
		return lecturer;
	}

	public void changeTaskVisibility(int taskID) {
		Task task = tasksRepository.findById(taskID).get();
		task.setCompleted(true);
		tasksRepository.save(task);
	}
	
}
