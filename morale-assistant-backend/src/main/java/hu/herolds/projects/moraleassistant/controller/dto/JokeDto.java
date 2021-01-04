package hu.herolds.projects.moraleassistant.controller.dto;

import hu.herolds.projects.moraleassistant.model.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JokeDto {
    private Long id;

    @NotBlank
    private String text;

    @NotNull
    private Language language;

    @Null
    private String path;

    @Null
    private byte[] soundFile;
}
