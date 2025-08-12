package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "content", nullable = false)
    private String content;
    
    // Default constructor
    public Message() {}
    
    // Constructor with content
    public Message(String content) {
        this.content = content;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}
