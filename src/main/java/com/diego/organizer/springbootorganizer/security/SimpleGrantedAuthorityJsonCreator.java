package com.diego.organizer.springbootorganizer.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SimpleGrantedAuthorityJsonCreator {

    @JsonCreator
    public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {} // indicamos que el constructor de la clase SimpleGrantedAuthority recibe un parámetro llamado authority
}
