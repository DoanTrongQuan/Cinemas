package org.example.project_cinemas_java.payload.dto.scheduledtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleByDayDTO {
    private String startAt;
    private Integer capacity;
}
