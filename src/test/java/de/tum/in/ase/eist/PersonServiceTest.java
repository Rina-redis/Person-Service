package de.tum.in.ase.eist;

import de.tum.in.ase.eist.model.Person;
import de.tum.in.ase.eist.repository.PersonRepository;
import de.tum.in.ase.eist.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class PersonServiceTest {
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;

    @Test
    void testAddPerson() {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        personService.save(person);

        assertEquals(1, personRepository.findAll().size());
    }

    @Test
    void testDeletePerson() {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        person = personRepository.save(person);

        personService.delete(person);

        assertTrue(personRepository.findAll().isEmpty());
    }

    @Test
    void testAddParent() {
        Person child = new Person();
        child.setFirstName("Anna");
        child.setLastName("Smith");
        child.setBirthday(LocalDate.now());

        Person parent = new Person();
        parent.setFirstName("John");
        parent.setLastName("Doe");
        parent.setBirthday(LocalDate.of(1980, 1, 1));
        personRepository.save(child);
        personService.save(parent);

        // Act
        personService.addParent(child, parent);

        assertTrue(child.getParents().contains(parent), "The parent should be in the child's parent list.");
        assertEquals(1, child.getParents().size(), "Child should have exactly one parent.");
        assertEquals(2, personRepository.findAll().size(), "Child should have exactly one parent.");
    }

    @Test
    void testAddThreeParents() {
        Person child = new Person();
        child.setFirstName("Anna");
        child.setLastName("Smith");
        child.setBirthday(LocalDate.now());

        Person parent1 = new Person();
        parent1.setFirstName("John");
        parent1.setLastName("Doe");
        parent1.setBirthday(LocalDate.of(1980, 1, 1));

        Person parent2 = new Person();
        parent2.setFirstName("John");
        parent2.setLastName("Doe");
        parent2.setBirthday(LocalDate.of(1980, 1, 1));

        Person parent3 = new Person();
        parent3.setFirstName("John");
        parent3.setLastName("Doe");
        parent3.setBirthday(LocalDate.of(1980, 1, 1));


        personRepository.save(child);
        personService.save(parent1);
        personService.save(parent2);
        personService.save(parent3);


//       personService.addChild(parent1,child);
//       personService.addChild(parent2,child);
//
//       assertTrue(child.getParents().contains(parent1));
//       assertTrue(child.getParents().contains(parent2));
//
//       try {
//           personService.addChild(parent3,child);
//       }catch (ResponseStatusException e){
//           assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
//       }
//       assertEquals(4, personRepository.findAll().size());
//

        Person updatedChild = personService.addParent(child, parent1);
        updatedChild = personService.addParent(updatedChild, parent2);

        assertTrue(updatedChild.getParents().contains(parent1));
        assertTrue(updatedChild.getParents().contains(parent2));
        assertEquals(2, updatedChild.getParents().size(), "Child should have exactly two parents before adding the third.");

        try {
            personService.addParent(updatedChild, parent3);
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode(), "Expected status code 400 for adding a third parent");
        }

        // Verify that no additional changes were made to the database after the exception
        updatedChild = personRepository.findById(updatedChild.getId()).orElse(null);
        assertEquals(2, updatedChild.getParents().size(), "Child should still have exactly two parents after exception.");
        assertEquals(4, personRepository.findAll().size(), "Total persons in the database should remain unchanged.");
    }
}
