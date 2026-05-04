package com.jlanzasg.novabank.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The type Cliente.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString (exclude = "cuentas")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name = "clientes")
public class Cliente {

    @Setter
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column (name = "dni", unique = true, nullable = false)
    private String dni;

    @Column (name = "nombre", nullable = false, length = 20)
    private String nombre;

    @Column (name = "apellidos", nullable = false)
    private String apellidos;

    @Column (name = "email", unique = true, nullable = false)
    private String email;

    @Column (name = "telefono", unique = true, nullable = false)
    private String telefono;

    @Builder.Default
    @Column (name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Builder.Default
    @OneToMany (mappedBy = "cliente", cascade = CascadeType.ALL,  orphanRemoval = true)
    private Set<Cuenta> cuentas = new LinkedHashSet<>();

    /**
     * Add cuenta.
     *
     * @param cuenta the cuenta
     */
    public void addCuenta(Cuenta cuenta) {
        cuentas.add(cuenta);
        cuenta.setCliente(this);
    }
}