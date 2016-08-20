package school.camera.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import school.camera.event.schedule.SchedulerCaptureJob;
import school.camera.event.schedule.SchedulerRecordJob;
import school.camera.persistence.model.Camera;
import school.camera.persistence.service.ICameraService;

import javax.annotation.PostConstruct;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@ComponentScan(basePackages = {"com.gcs.infras.schedule"})
@Service
public class QuartzConfiguration {
  private static final Logger LOGGER = LoggerFactory.getLogger(QuartzConfiguration.class);

  private Scheduler scheduler;


  @Autowired
  private ICameraService cameraService;
//
//  @Autowired
//  private ICameraRepo cameraRepo;

  /**
   * Create new Scheduler bean.
   * 
   */

  @PostConstruct
  public void schedulerFactoryBean() {
    LOGGER.info("Creating scheduler..................................");
		 try {
		 this.scheduler = new StdSchedulerFactory().getScheduler();
		 scheduler.start();
		 
		// // add trigger from db.
		// // scheduleInitializer();
		// // create schedule to run check current view streaming.
		// JobKey jobKey = new JobKey("interval", "stream");
		// JobDetail schedulerMasterJobDetail =
		// JobBuilder.newJob(ActivityJob.class).withIdentity(jobKey).storeDurably(false).build();
		//
		// Trigger schedulerMasterTrigger =
		// TriggerBuilder.newTrigger().withIdentity("interval",
		// "stream").startNow()
		// .withSchedule(SimpleScheduleBuilder.simpleSchedule()
		// .withIntervalInSeconds((int)
		// Constant.TIME_VIEW_INTERVAL).repeatForever())
		// .build();
		// schedulerMasterJobDetail.getJobDataMap().put(ActivityJob.SERVICE,
		// cameraService);
		// LOGGER.info("ADD streaming trigger.");
		//
		// try {
		// if (scheduler != null && !scheduler.checkExists(jobKey)) {
		// this.scheduler.scheduleJob(schedulerMasterJobDetail,
		// schedulerMasterTrigger);
		// } else if (scheduler.checkExists(jobKey)) {
		// LOGGER.info("reschedual jobKey exit");
		// this.scheduler.deleteJob(jobKey);
		// this.scheduler.scheduleJob(schedulerMasterJobDetail,
		// schedulerMasterTrigger);
		// }
		// } catch (SchedulerException se) {
		// LOGGER.error("Can't schedule scheduler master.", se);
		// throw se;
		// }
		// LOGGER.info("create capture
		// Trigger.............................END");
		//
		 } catch (SchedulerException e) {
		 LOGGER.error("Create scheduler error.");
		 }
    LOGGER.info("Created scheduler..................................");
  }

  /**
   * create new record trigger then add to scheduler.
   * 
   * @param seconds .
   * @param id .
   * @throws SchedulerException .
   */
  public void createCaptureTrigger(Camera camera) throws SchedulerException {
    LOGGER.info("create  record Trigger.............................START");
    JobKey jobKey = new JobKey(Long.toString(camera.getCameraid()), "capture");
    JobDetail schedulerMasterJobDetail = JobBuilder.newJob(SchedulerCaptureJob.class)
        .withIdentity(jobKey).storeDurably(false).build();

    Trigger schedulerMasterTrigger = null;
    if (camera.isCapture() == true) {
      schedulerMasterTrigger =
          TriggerBuilder.newTrigger().withIdentity(Long.toString(camera.getCameraid()), "capture")
              .startNow()
              .withSchedule(
                  SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(camera.getCaptureTime()).repeatForever())
              .build();
    }

    schedulerMasterJobDetail.getJobDataMap().put(SchedulerCaptureJob.SERVICE, cameraService);
    LOGGER.info("capture trigger.");
    try {
      if (scheduler != null && !scheduler.checkExists(jobKey)) {
        this.scheduler.scheduleJob(schedulerMasterJobDetail, schedulerMasterTrigger);
      } else if (scheduler.checkExists(jobKey)) {
        LOGGER.info("reschedual jobKey exit");
        this.scheduler.deleteJob(jobKey);
        this.scheduler.scheduleJob(schedulerMasterJobDetail, schedulerMasterTrigger);
      }
    } catch (SchedulerException se) {
      LOGGER.error("Can't schedule scheduler master.", se);
      throw se;
    }
    LOGGER.info("create record Trigger.............................END");

  }
  
  /**
   * create new record trigger then add to scheduler.
   * 
   * @param seconds .
   * @param id .
   * @throws SchedulerException .
   */
  public void createRecordTrigger(Camera camera) throws SchedulerException {
    LOGGER.info("create  record Trigger.............................START");
    JobKey jobKey = new JobKey(Long.toString(camera.getCameraid()), "record");
    JobDetail schedulerMasterJobDetail = JobBuilder.newJob(SchedulerRecordJob.class)
        .withIdentity(jobKey).storeDurably(false).build();

    Trigger schedulerMasterTrigger = null;
    if (camera.isRecord() == true) {
      schedulerMasterTrigger =
          TriggerBuilder.newTrigger().withIdentity(Long.toString(camera.getCameraid()), "record")
              .startNow()
              .withSchedule(
                  SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(24).repeatForever())
              .build();
    }

    schedulerMasterJobDetail.getJobDataMap().put(SchedulerRecordJob.SERVICE, cameraService);
    LOGGER.info("record capture trigger.");
    try {
      if (scheduler != null && !scheduler.checkExists(jobKey)) {
        this.scheduler.scheduleJob(schedulerMasterJobDetail, schedulerMasterTrigger);
      } else if (scheduler.checkExists(jobKey)) {
        LOGGER.info("reschedual jobKey exit");
        this.scheduler.deleteJob(jobKey);
        this.scheduler.scheduleJob(schedulerMasterJobDetail, schedulerMasterTrigger);
      }
    } catch (SchedulerException se) {
      LOGGER.error("Can't schedule scheduler master.", se);
      throw se;
    }
    LOGGER.info("create record Trigger.............................END");

  }
//
//  /**
//   * create schedule when restart server.
//   */
//  public void scheduleInitializer() {
//    LOGGER.info(" Initializer schedule from db  ========>start");
//    List<Camera> cameras = cameraRepo.findByCaptureEnableOrRecordEnable(true, true);
//    for (Camera camera : cameras) {
//      if (camera.isCaptureEnable() == true && camera.getCaptureSchedual() != 0) {
//        try {
//          createCaptureTrigger(camera.getCaptureSchedual(), camera.getCameraId());
//        } catch (SchedulerException e) {
//          LOGGER.error("Error when create capture SchedulerException", e);
//          throw new RuntimeException(e);
//        }
//      } else if (camera.isRecordEnable() == true && camera.getRecordSchedual() != null) {
//        try {
//          // edit this one.
//          createRecordTrigger(camera);
//        } catch (SchedulerException e) {
//          LOGGER.error("Error when create record SchedulerException", e);
//          throw new RuntimeException(e);
//        }
//      }
//
//    }
//    LOGGER.info(" Initializer schedule from db  ========>end");
//  }
//
//  /**
//   * delete job key.
//   * 
//   * @param id camera id.
//   * @throws SchedulerException .
//   */
  public void deleteJob(Long id, String group) throws SchedulerException {
    LOGGER.info("DELETE JOB.............................START");
    JobKey jobKey = new JobKey(Long.toString(id), group);
    if (scheduler.checkExists(jobKey)) {
      LOGGER.info("DELETE JOB");
      this.scheduler.deleteJob(jobKey);
    } else {
      LOGGER.info("JOB KEY IS NOT EXIT");


    }
  }


}