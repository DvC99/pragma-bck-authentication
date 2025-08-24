package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Table("rol")
@AllArgsConstructor
@NoArgsConstructor
public class RolEntity {
    @Id
    @Column("id")
    private Integer id;

    private String nombre;

    private String descripcion;

    @Column("created_by")
    private String createdBy;

    @Column("modified_by")
    private String modifiedBy;

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_modified")
    private LocalDateTime dateModified;
}