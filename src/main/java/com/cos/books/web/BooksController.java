package com.cos.books.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import com.cos.books.domain.Books;
import com.cos.books.service.BooksService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class BooksController {

	private final BooksService booksService;

	// 무엇을 리턴할지 모른다면 <?>를 하면 편하다.
	@PostMapping("/books")
	public ResponseEntity<?> save(@RequestBody Books books) {
		//<>를 비워도 되는 이유
		//마치 List<Books> books = new ArrayList<>(); 와 같은 이치이다. 
		
		return new ResponseEntity<>(booksService.저장하기(books), HttpStatus.CREATED); // 201
	}

	@GetMapping("/books")
	public ResponseEntity<?> findAll() {
		return new ResponseEntity<>(booksService.모두가져오기(), HttpStatus.OK); // 201
	}
	
	@GetMapping("/books/{id}")
	public ResponseEntity<?> findById(@PathVariable Long id){
		return new ResponseEntity<>(booksService.한건가져오기(id), HttpStatus.OK);
	}
	
	@PutMapping("/books/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Books book){
		return new ResponseEntity<>(booksService.수정하기(id, book), HttpStatus.OK);
	}
	
	@DeleteMapping("/books/{id}")
	public ResponseEntity<?> deleteById(@PathVariable Long id){
		return new ResponseEntity<>(booksService.삭제하기(id), HttpStatus.OK);
	}
	
}
