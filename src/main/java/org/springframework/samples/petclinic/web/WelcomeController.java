package org.springframework.samples.petclinic.web;

import org.springframework.samples.petclinic.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class WelcomeController {


	  @GetMapping({"/","/welcome"})
	  public String welcome(Map<String, Object> model) {

          String[] names = {"Christina Dsubanko", "Francisco Javier Vázquez", "Michael Hirsch", "Kim Taekyung", "Pablo Delfín López", "Alejandro Carrasco", "Florian Gamillscheg"};
          List<Person> persons = new ArrayList<Person>();

          for(String name : names) {
              String[] fullName = name.split(" ");
              if(fullName.length==3){
                Person person = new Person();
                String firstNameComposed = fullName[0] + " " + fullName[1];
                person.setFirstName(firstNameComposed);
                person.setLastName(fullName[2]);
                persons.add(person);
              }else{
                Person person = new Person();
                person.setFirstName(fullName[0]);
                person.setLastName(fullName[1]);
                persons.add(person);
              }
          }

          model.put("persons", persons);
          model.put("title", "Our Projectname");
          model.put("group", "Ling-3");

          return "welcome";
	  }
}
