package com.laboratorio.linkedinapiinterface.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 24/08/2024
 * @updated 24/08/2024
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LinkedInRegisterUploadRequest {
    private List<String> recipes;
    private String owner;
    private List<LinkedInServiceRelationships> serviceRelationships;

    public LinkedInRegisterUploadRequest(String owner) {
        this.recipes = new ArrayList<>();
        this.recipes.add("urn:li:digitalmediaRecipe:feedshare-image");
        this.owner = owner;
        this.serviceRelationships = new ArrayList<>();
        this.serviceRelationships.add(new LinkedInServiceRelationships());
    }
}