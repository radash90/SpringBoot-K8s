package com.example.demo.controller;

import com.example.demo.entity.Message;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @PostMapping("/message")
    public String saveMessage(@RequestBody String content) {
        messageService.saveMessage(content);
        return "Message saved successfully";
    }

    @DeleteMapping("/message/{id}")
    public String deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return "Message deleted successfully";
    }

}
