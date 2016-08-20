package school.camera.event.schedule;

import java.io.IOException;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import school.camera.persistence.service.ICameraService;

@DisallowConcurrentExecution
public class SchedulerRecordJob implements Job {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerCaptureJob.class);
	public static final String SERVICE = "service";
	private Long cameraId;

	private ICameraService cameraService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.info("============== BEGIN RUN SCHEDULER CAPTURE  ==============");
		JobKey jobkey = context.getJobDetail().getKey();
		this.cameraId = Long.parseLong(jobkey.getName());
		LOGGER.info("============== CAMERA id ============== {}", cameraId);
		this.cameraService = (ICameraService) context.getJobDetail().getJobDataMap().get(SERVICE);
		if (cameraService != null) {
			LOGGER.info("============== SCHEDULER RECORD RUNNING  ==============");
			// CameraDto cameraDto = new CameraDto();
			// cameraDto.setCameraId(cameraId);
			try {
				cameraService.record(cameraId);
			} catch (IOException e) {
				LOGGER.error("  IOException  {}", e);
			} catch (InterruptedException e) {
				LOGGER.error("  InterruptedException  {}", e);
			}
		}

		LOGGER.info("============== END RUN SCHEDULER RECORD  ==============");

	}

}
