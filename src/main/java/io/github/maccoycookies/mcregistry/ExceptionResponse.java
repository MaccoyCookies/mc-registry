package io.github.maccoycookies.mcregistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author Maccoy
 * @date 2024/4/27 17:19
 * Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {

    public HttpStatus httpStatus;
    public String message;

}
