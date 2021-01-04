package hu.herolds.projects.moraleassistant.model;

import hu.herolds.projects.moraleassistant.model.enums.Language;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "joke")
public class Joke extends BaseEntity {
    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "sound_file_path")
    private URI soundFilePath;
}
