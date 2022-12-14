package com.booking.roles.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    private Long id;

    @JsonProperty
    @NotBlank(message = "Role must be provided")
    @Column(nullable = false, unique = true)
    @ApiModelProperty(name = "name", value = "Name of role (must be unique)", required = true, example = "Customer", position = 2)
    private String name;

    public Role() {
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}