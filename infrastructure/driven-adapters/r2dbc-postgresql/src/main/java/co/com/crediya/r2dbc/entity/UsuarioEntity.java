package co.com.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Table("usuario")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioEntity {
    @Id
    @Column("id")
    private Integer id;

    private String nombres;

    private String apellidos;

    @Column("fecha_nacimiento")
    private Date fechaNacimiento;

    private String email;

    @Column("documento_identidad")
    private String documentoIdentidad;

    private String telefono;

    private String direccion;

    private BigDecimal salarioBase;

    @Column("id_rol") // Mantener la columna para la clave foránea
    private Integer idRol; // Cambiar a Integer para la clave foránea

    // Audit fields
    @Column("created_by")
    private String createdBy;

    @Column("modified_by")
    private String modifiedBy;

    @Column("date_created")
    private LocalDateTime dateCreated;

    @Column("date_modified")
    private LocalDateTime dateModified;
}
