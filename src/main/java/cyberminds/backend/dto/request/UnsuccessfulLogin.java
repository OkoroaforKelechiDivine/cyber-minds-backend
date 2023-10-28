package cyberminds.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnsuccessfulLogin {

    private LocalDateTime timestamp;

    private String message;

    private String requestType;

    private String path;
}
