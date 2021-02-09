package com.cos.books.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.cos.books.domain.Books;
import com.cos.books.service.BooksService;

import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.extern.slf4j.Slf4j;

// 단위 테스트 (Controller, Filter, ControllerAdvice)

@Slf4j
@WebMvcTest
public class BooksControllerUnitTest {

	@Autowired
	private MockMvc mockMvc;
	
	// IoC환경에 Bean등록 됨. 가상의 서비스가 등록된 것임.
	// 왜 가상의 서비스를 만들었냐면 실제를 사용하면 레파지토리 까지 들고 와야하기 때문.
	@MockBean 
	private BooksService booksService;
	
	// BDDMockito 패턴 given, when, then
	@Test
	public void save_테스트() throws Exception {
		//given (테스트를 하기 위한 준비단계)
		Books books = new Books(null, "스프링 따라하기", "코스");
		String content = new ObjectMapper().
				writeValueAsString(books);
		log.info(content);
		// 스텁 : 가정하는것
		// 서비스는 가짜이다. 그래서 서비스가 실행되면 무엇이 리턴되어야 하는지 지정을 해주는 것이다.
		when(booksService.저장하기(books)).thenReturn(new Books(null, "스프링 따라하기", "코스"));
		
		//when :﻿테스트를 실행 하는 것 이다.
		ResultActions resultActions = mockMvc.perform(post("/books")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then : 검증
		resultActions
		.andExpect(status().isCreated())// http 결과를 기대한다.
		.andExpect(jsonPath("$.title").value("스프링 따라하기")) // $(전체).변수명 .value(기대하는 값) 
		.andDo(MockMvcResultHandlers.print()); // 기대값이후 행동 (출력하기)
	}
	
	@Test
	public void findAll_테스트() throws Exception {
		//given
		List<Books> booksList = new ArrayList<>();
		booksList.add(new Books(1L, "스프링부트 따라하기", "코스"));
		booksList.add(new Books(2L, "리엑트 따라하기", "코스"));
		when(booksService.모두가져오기()).thenReturn(booksList);
		
		//when
		ResultActions resultActions = mockMvc.perform(get("/books")
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		resultActions
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", Matchers.hasSize(2))) // 전체 데이터의 길이를 기대하는 문법
		.andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기"))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void findById_테스트() throws Exception{
		//given
		Long id = 1L;
		when(booksService.한건가져오기(id)).thenReturn(new Books(1L, "자바 공부하기", "쌀"));
		
		//when
		ResultActions resultActions = mockMvc.perform(get("/books/{id}",id)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
		resultActions
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.title").value("자바 공부하기"))
		.andDo(MockMvcResultHandlers.print());
	}
	
	@Test
	public void update_테스트() throws Exception{
		//given		
		Long id = 1L;
		Books books = new Books(null, "씨플플 따라하기", "코스");
		String content = new ObjectMapper().
				writeValueAsString(books); // json으로 파싱해준다.
		// stub : 가정하는것
		// 서비스는 가짜이다. 그래서 서비스가 실행되면 무엇이 리턴되어야 하는지 지정을 해주는 것이다.
		when(booksService.수정하기(id, books)).thenReturn(new Books(1L, "씨플플 따라하기", "코스"));
		

		//when :﻿테스트를 실행 하는 것 이다.
		ResultActions resultActions = mockMvc.perform(put("/books/{id}",id)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content)
				.accept(MediaType.APPLICATION_JSON_UTF8));
		
		//then
				resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("씨플플 따라하기"))
				.andDo(MockMvcResultHandlers.print());
		
	}
	
	@Test
	public void delete_테스트() throws Exception{
		//given		
		Long id = 1L;
		
		// 스텁 : 가정하는것
		// 서비스는 가짜이다. 그래서 서비스가 실행되면 무엇이 리턴되어야 하는지 지정을 해주는 것이다.
		when(booksService.삭제하기(id)).thenReturn("ok");
		

		//when :﻿테스트를 실행 하는 것 이다.
		ResultActions resultActions = mockMvc.perform(delete("/books/{id}",id)
				.accept(MediaType.TEXT_PLAIN));
		
		//then
				resultActions
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print());
				
		// TEXT_PLAIN값을 받기위해선 문자열 방식으로 받아줘야 한다
		MvcResult reqMvcResult = resultActions.andReturn();
		String result = reqMvcResult.getResponse().getContentAsString();
		
		assertEquals("ok", result);
		
	}
	
	
	
}
