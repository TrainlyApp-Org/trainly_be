package com.trainly.backend.repository;

import com.trainly.backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Profile findByUsername(String username);

    @Query("""
            select p from Profile p
            where :query = ''
               or lower(coalesce(p.username, '')) like lower(concat('%', :query, '%'))
               or lower(coalesce(p.fullName, '')) like lower(concat('%', :query, '%'))
               or cast(p.id as string) like concat('%', :query, '%')
            """)
    Page<Profile> search(@Param("query") String query, Pageable pageable);

    long countByPremiumTrue();

}
