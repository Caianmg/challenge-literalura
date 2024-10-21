package com.alura.literalura.repository;

import com.alura.literalura.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByName(String name);

    List<Person> findByBirthYearLessThanEqualAndDeathYearIsNullOrDeathYearGreaterThanEqual(int birthYear, int deathYear);

}
