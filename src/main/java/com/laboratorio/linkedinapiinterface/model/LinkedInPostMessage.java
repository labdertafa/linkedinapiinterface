package com.laboratorio.linkedinapiinterface.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 24/08/2024
 * @updated 24/08/2024
 */

@Getter @Setter @AllArgsConstructor
public class LinkedInPostMessage {
    private String author;
    private String lifecycleState;
    private LinkedInSpecificContent specificContent;
    private LinkedInVisibility visibility;

    public LinkedInPostMessage(String author, LinkedInSpecificContent specificContent) {
        this.author = author;
        this.lifecycleState = "PUBLISHED";
        this.specificContent = specificContent;
        this.visibility = new LinkedInVisibility("PUBLIC");
    }
}