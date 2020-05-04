package com.example.reatormongoquickdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@SpringBootApplication
public class ReatorMongoQuickDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReatorMongoQuickDemoApplication.class, args);
	}
}

@RestController
class CoffeeController{

	private final CoffeeService coffeeService;

	CoffeeController(CoffeeService coffeeService) {
		this.coffeeService = coffeeService;
	}

	@GetMapping("/coffees")
	public Flux<Coffee> getAllCoffess(){
		return coffeeService.getAllCoffees();
	}

	@GetMapping("/coffees/{id}")
	public Mono<Coffee> getACoffeeById(@PathVariable("id") String id){
		return coffeeService.getCoffeeById(id);
	}

	@GetMapping(value = "/coffees/{id}/orders", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<CoffeeOder> getCoffeeOrders(@PathVariable("id") String id){
		return coffeeService.getOrders(id);
	}



}

@Service
class CoffeeService{
	private CofeeRepository cofeeRepository;

	CoffeeService(CofeeRepository cofeeRepository) {
		this.cofeeRepository = cofeeRepository;
	}

	public Flux<Coffee> getAllCoffees(){
		return cofeeRepository.findAll();
	}

	Mono<Coffee> getCoffeeById(String id){
		return cofeeRepository.findById(id);
	}

	Flux<CoffeeOder> getOrders(String coffeeId){
		return Flux.<CoffeeOder>generate(s -> s.next(new CoffeeOder(coffeeId, Instant.now())))
				.delayElements(Duration.ofSeconds(4));
	}

}




// Load dummy data
@Component
class DataLoader{
	private CofeeRepository cofeeRepository;

	public DataLoader(CofeeRepository cofeeRepository) {
		this.cofeeRepository = cofeeRepository;
	}

	@PostConstruct
	private void load(){
		cofeeRepository.deleteAll().thenMany(
					Flux.just("Coffee_1", "Coffee_2", "Coffee_3", "Coffee_4")
					.map(name -> new Coffee(UUID.randomUUID().toString(), name))
					.flatMap(cofeeRepository::save)
				).thenMany(cofeeRepository.findAll())
				.subscribe(System.out::println);
	}
}

interface CofeeRepository extends ReactiveCrudRepository<Coffee, String> {}

@Data
@AllArgsConstructor
@NoArgsConstructor
class CoffeeOder {
	private String coffeeId;
	private Instant dateOrder;
}

@Document
@Data
@AllArgsConstructor
class Coffee{

	@Id
	private String id;

	private String name;
}