package cyberminds.backend.model.user;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {

    RIDER("USER"),
    ADMIN("ADMIN");

    public final String role;

    Role(String status){
        this.role = status;
    }

    @JsonValue
    public String getStatus() {
        return role;
    }
}