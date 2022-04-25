package com.wisetasklecturer.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.wisetasklecturer.entities.Task;

@Repository
public interface TasksRepository extends CrudRepository<Task, Integer> {

}
