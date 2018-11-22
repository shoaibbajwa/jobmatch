package com.workermatcher.workermatcher.controller;

import com.workermatcher.workermatcher.Dto.JobDto;
import com.workermatcher.workermatcher.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class WorkerController {
	@Autowired
	public WorkerController(WorkerService workerService) {
		this.workerService = workerService;

	}
	private WorkerService workerService;
	
	@GetMapping("/jobsearch/worker/{id}")
	public ResponseEntity<List<JobDto>> getJobsForWorker(@PathVariable String id)
	{
		List<JobDto> jobDtos = workerService.getJobsForWorker(Integer.parseInt(id));
		return new ResponseEntity<>(jobDtos, HttpStatus.OK);
	}
	

} 