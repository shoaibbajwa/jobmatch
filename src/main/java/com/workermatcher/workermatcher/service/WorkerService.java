package com.workermatcher.workermatcher.service;

import com.workermatcher.workermatcher.Dto.AvailabilityDto;
import com.workermatcher.workermatcher.Dto.JobDto;
import com.workermatcher.workermatcher.Dto.WorkerDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class WorkerService {

	private static final int SKILL_MATCH_TOLERANCE = 75;
    private static final int DISTANCE_TOLERANCE = 10;
    private static final int DISTANCE_INCREMENTS = 5;

	public List<JobDto> getJobsForWorker(int workerId) {
		List<JobDto> jobDtos = new ArrayList<>();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<WorkerDto>> response = restTemplate.exchange(
				"http://test.swipejobs.com/api/workers",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<WorkerDto>>(){});
		List<WorkerDto> workers = response.getBody();

		WorkerDto workerDto = workers.stream()
									.filter(worker -> worker.getUserId() == workerId)
									.findAny()
									.orElse(null);

		if (workerDto == null)
			return null;

		boolean[] workerAvailability = {false, false, false, false, false, false, false};
		for (AvailabilityDto availabilityDto: workerDto.getAvailability()) {
		    workerAvailability[availabilityDto.getDayIndex() - 1] = true;
        }

		ResponseEntity<List<JobDto>> JobsResponse = restTemplate.exchange(
				"http://test.swipejobs.com/api/jobs",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<JobDto>>(){});
		List<JobDto> jobs = JobsResponse.getBody();

        List<JobDto> filteredJobs = jobs.stream()
                                        .filter(job -> {
                                            if (job.getDriverLicenseRequired() && !workerDto.getHasDriversLicense())
                                                return false;

                                            calculateSkillMatch(job, workerDto);
                                            if (job.getSkillMatch() < SKILL_MATCH_TOLERANCE)
                                                return false;

                                            if (!workerAvailable(job, workerAvailability))
                                                return false;

                                            return true;
                                        })
                                        .collect(Collectors.toList());

        List<JobDto> distanceBasedFilteredJobs = null;

        int distanceFilter = workerDto.getJobSearchAddress().getMaxJobDistance();

        while(distanceFilter < workerDto.getJobSearchAddress().getMaxJobDistance() + DISTANCE_TOLERANCE) {
            distanceBasedFilteredJobs = getDistanceBasedFilter(filteredJobs, workerDto, distanceFilter);
            if (distanceBasedFilteredJobs.size() > 2)
                break;

            distanceFilter += DISTANCE_INCREMENTS;
        }

        distanceBasedFilteredJobs = distanceBasedFilteredJobs.stream()
                .sorted((job1, job2) -> {
                    if (job1.isSameDayAvail() && !job2.isSameDayAvail())
                        return -1;
                    if (!job1.isSameDayAvail() && job2.isSameDayAvail())
                        return 1;

                    return job1.getSkillMatch().compareTo(job2.getSkillMatch()) * -1;
                })
                .collect(Collectors.toList());

        int subListLastIndex = distanceBasedFilteredJobs.size() < 3 ? distanceBasedFilteredJobs.size() : 3;

        return distanceBasedFilteredJobs.subList(0, subListLastIndex);
	}

    private boolean workerAvailable(JobDto job, boolean[] workerAvailability) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(job.getStartDate());
        int day = cal.get(Calendar.DAY_OF_WEEK) - 2 > 0 ? cal.get(Calendar.DAY_OF_WEEK) - 2 : 6;
        if (!workerAvailability[day]) {
            if (!workerAvailability[day + 1])
                return false;

            job.setSameDayAvail(false);
        }
        return true;
    }

    private void calculateSkillMatch(JobDto job, WorkerDto workerDto) {
        int totalCertRequired = job.getRequiredCertificates().size();
        int workerHasCerts = 0;
        for (String requiredCert : job.getRequiredCertificates()) {
            if (workerDto.getCertificates().contains(requiredCert))
                workerHasCerts++;
        }
        job.setSkillMatch(workerHasCerts/totalCertRequired * 100);
    }

    private List<JobDto> getDistanceBasedFilter(List<JobDto> list, WorkerDto workerDto, Integer maxDistance) {
	    return list.stream()
                .filter(job -> {

                    if (distance(Double.parseDouble(job.getLocation().getLatitude()),
                            Double.parseDouble(job.getLocation().getLongitude()),
                            Double.parseDouble(workerDto.getJobSearchAddress().getLatitude()),
                            Double.parseDouble(workerDto.getJobSearchAddress().getLongitude()),
                            workerDto.getJobSearchAddress().getUnit()) > maxDistance)
                        return false;

                    return true;
                })
                .collect(Collectors.toList());
    }

    private double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit.equals("km")) {
            dist = dist * 1.609344;
        } else if (unit.equals("N")) {
            dist = dist * 0.8684;
        }

        return (dist);

    }
    private double deg2rad(double deg) {

        return (deg * Math.PI / 180.0);

    }
    private double rad2deg(double rad) {

        return (rad * 180.0 / Math.PI);

    }
}