package library.services;

import library.models.Book;
import library.models.Person;
import library.repositories.BooksRepositories;
import library.repositories.PeopleRepositories;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeopleService {

    private final PeopleRepositories peopleRepositories;
    private final BooksRepositories booksRepositories;

    public List<Person> findAll() {
        return peopleRepositories.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepositories.findById(id);
        return foundPerson.orElse(null);
    }

    public Person findOne(String name) {
        Person foundPerson = peopleRepositories.findByFullName(name);
        return foundPerson;
    }

    @Transactional
    public void save(Person person) {
        peopleRepositories.save(person);
    }

    @Transactional
    public void update(int id, Person personUpdated) {
        personUpdated.setId(id);
        peopleRepositories.save(personUpdated);
    }

    @Transactional
    public void delete(int id) {
        peopleRepositories.deleteById(id);
    }

    @Transactional
    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> person = peopleRepositories.findById(id);
        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());
            person.get().getBooks().forEach(book -> {
                long p1 = book.getTakenAt().getTime();
                long p2 = new Date().getTime();
                long diffMillies = Math.abs(p1 - p2);
                if (864000000 < diffMillies) {
                    book.setOverdue(true);
                }
            });
            return person.get().getBooks();
        }
        return Collections.emptyList();
    }
}
