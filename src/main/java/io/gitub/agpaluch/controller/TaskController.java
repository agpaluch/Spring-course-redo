package io.gitub.agpaluch.controller;


import io.gitub.agpaluch.model.Task;
import io.gitub.agpaluch.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;


@Controller
@ResponseBody
@RequestMapping("/tasks")
class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository repository;

    TaskController(final TaskRepository repository) {
        this.repository = repository;
    }


    @RequestMapping(method = RequestMethod.GET, params = {"!sort", "!page", "!size"})
    ResponseEntity<List<Task>> readAllTasks(){
        logger.warn("Exposing all the tasks!");
        return ResponseEntity.ok(repository.findAll());
    }


    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.info("Custom pageable.");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<Task> readTask(@PathVariable int id){
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<Task> updateTask(@RequestBody @Valid Task toCreate){

        Task savedTask = repository.save(toCreate);
        return ResponseEntity.created(URI.create("/"+savedTask.getId())).body(savedTask);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        if (!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        toUpdate.setId(id);
        repository.save(toUpdate);
        return ResponseEntity.noContent().build();

    }



}
