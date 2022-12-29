
package enterprise.services;

import enterprise.models.Person;
import enterprise.repositories.PersonRepository;
import enterprise.utils.PersonNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person findOne(int id) {

        Optional<Person> optionalPerson = personRepository.findById(id);

        return  optionalPerson.orElseThrow(PersonNotFoundException::new);
    }

    public void save(Person person) {
        personRepository.save(person);
    }
}
