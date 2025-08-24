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
    @Column("salario_base")
    private BigDecimal salarioBase;
    @Column("id_rol")
    private Long idRol;
    @Column("nombre_rol")
    private String nombreRol;
    @Column("created_by")
    private String createdBy;
    @Column("modified_by")
    private String modifiedBy;
    @Column("date_created")
    private LocalDateTime dateCreated;
    @Column("date_modified")
    private LocalDateTime dateModified;
}