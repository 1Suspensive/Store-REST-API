package com.suspensive.store.models.entities;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "roles")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private RolesEnum rolesEnum;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "roles_permissions",joinColumns = @JoinColumn(name="role_id"), inverseJoinColumns = @JoinColumn(name="permission_id"))
    private Set<PermissionEntity> permissions;
    
}
