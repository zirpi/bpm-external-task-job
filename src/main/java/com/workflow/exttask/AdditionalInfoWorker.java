package com.workflow.exttask;

import java.io.IOException;
import java.net.URI;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Desktop;

import org.camunda.bpm.client.ExternalTaskClient;

public class AdditionalInfoWorker {

    private final static Logger LOGGER = Logger.getLogger(AdditionalInfoWorker.class.getName());

    public static void main(String[] args) throws IOException {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // long polling timeout
                .build();

        client.subscribe("LoadAdditionalData")

                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    if (externalTask.getBusinessKey() == null) return;
                    try {
                        String variables = externalTask.getAllVariables().toString();
                        LOGGER.info("Task " + externalTask.getBusinessKey() + "  Variables: " + variables);

                        externalTaskService.complete(externalTask);
                    } catch (Exception ex){
                        LOGGER.log(Level.WARNING, ex.getMessage() + " " + ex.getStackTrace());
                    }
                })
                .open();

    }
}