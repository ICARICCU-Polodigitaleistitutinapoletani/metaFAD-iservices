package com.gruppometa.poloigitale.services.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gruppometa.poloigitale.services.jobs.UnimarcImportJob;
import com.gruppometa.poloigitale.services.objects.Message;


@RestController
@RequestMapping("/jobs")
public class JobController {

    protected static Logger logger = LoggerFactory.getLogger(JobController.class);
	@Autowired
	protected UnimarcImportJob unimarcJob;
	
	@RequestMapping("/import/start")
    public Message startImport(@RequestParam(value="filename") String filename,
    		@RequestParam(value="directory") String directory,
    		@RequestParam(value="rows", defaultValue="0") int rows,
    		@RequestParam(value="offset", defaultValue="0") int offset,
    		@RequestParam(value="profile",defaultValue="na") String profile,
            @RequestParam(value="clear",defaultValue="true") boolean clear,
            @RequestParam(value="id",required = false) String id
    		) {
	    logger.info("Start job "+filename+" ->"+directory);
		unimarcJob.run(filename,directory,rows,offset,profile, clear,id);
        Message mess = new Message();
        mess.setMessage(filename+"->"+directory);
        return mess;
    }
	@RequestMapping("/import/status")
    public Message statusImport() {
        return unimarcJob.getStatus();
    }
	@RequestMapping("/import/stop")
    public Message stopImport() {
        unimarcJob.stop();
        return unimarcJob.getStatus();
    }
	
}
