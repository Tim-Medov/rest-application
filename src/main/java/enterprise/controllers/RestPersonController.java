
package enterprise.controllers;

import enterprise.dto.PersonDto;
import enterprise.models.Person;
import enterprise.services.PersonService;
import enterprise.utils.PersonErrorResponse;
import enterprise.utils.PersonNotCreatedException;
import enterprise.utils.PersonNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RestPersonController {

    private PersonService personService;

    @Autowired
    public RestPersonController(PersonService personService) {
        this.personService = personService;
    }

    // http://localhost:8080/persons
    @GetMapping("/persons")
    public List<PersonDto> getAllPersonsDto() {
        return personService.findAll().stream().map(this::convertToPersonDto).collect(Collectors.toList());
    }

    // http://localhost:8080/persons/1
    @GetMapping("/persons/{id}")
    public PersonDto getPersonDto(@PathVariable("id") int id) {
        return convertToPersonDto(personService.findOne(id));
    }

    // http://localhost:8080/persons
    @PostMapping("/persons")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDto personDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {

                errorMessage.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }

            throw new PersonNotCreatedException(errorMessage.toString());
        }

        personService.save(convertToPerson(personDto));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Person convertToPerson(PersonDto personDto) {

        ModelMapper modelMapper = new ModelMapper();

        Person person = modelMapper.map(personDto, Person.class);

        person.setCreatedTime(LocalDateTime.now());

        return person;
    }

    private PersonDto convertToPersonDto(Person person) {

        ModelMapper modelMapper = new ModelMapper();

        PersonDto personDto = modelMapper.map(person, PersonDto.class);

        return personDto;
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException ex) {

        Date date = new Date();

        PersonErrorResponse personErrorResponse =
                new PersonErrorResponse("Person with this 'id' wasn't found", date);

        return new ResponseEntity<>(personErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException ex) {

        Date date = new Date();

        PersonErrorResponse personErrorResponse =
                new PersonErrorResponse(ex.getMessage(), date);

        return new ResponseEntity<>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
