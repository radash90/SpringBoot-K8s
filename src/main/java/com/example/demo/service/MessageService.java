package com.example.demo.service;

import com.example.demo.entity.Message;
import com.example.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    /**
     * Get the first message from the database
     * @return String content of the first message, or default message if none exists
     */
    public String getMessage() {
        List<Message> messages = messageRepository.findAll();
        if (!messages.isEmpty()) {
            return messages.get(0).getContent();
        }
        return "Hello from Spring Boot! No messages found in database.";
    }
    
    /**
     * Save a new message to the database
     * @param content The message content to save
     * @return The saved message
     */
    public Message saveMessage(String content) {
        Message message = new Message(content);
        return messageRepository.save(message);
    }

    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }
}
