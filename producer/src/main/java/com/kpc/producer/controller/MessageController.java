package com.kpc.producer.controller;

import com.kpc.common.schema.Message;
import com.kpc.producer.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/api")
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    private KafkaService kafkaService;

    public MessageController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @PostMapping(path = "/record", consumes = {"application/json"})
    public ResponseEntity createRecord(@RequestBody @NotNull Message message) {

        LOGGER.info("Creating a new message: {}", message.toString());
        try {
            kafkaService.addRecord(message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
