package com.wisetasklecturer.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.wisetasklecturer.entities.Task;

/**
 * @author Theofanis Gkoufas
 *
 */
@Repository
public interface TasksRepository extends CrudRepository<Task, Integer> {

}
