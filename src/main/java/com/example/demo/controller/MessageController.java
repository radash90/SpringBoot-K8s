package com.example.demo.controller;

import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * GET endpoint that returns a message string from the database
     * @return String message from the database
     */
    @GetMapping("/message")
    public String getMessage() {
        return messageService.getMessage();
    }
}
