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
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
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
