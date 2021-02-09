package com.cos.books.web;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.cos.books.domain.Books;
import com.cos.books.domain.BooksRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
// ADOC을 위한 어노테이션
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class BooksControllerIntegreTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BooksRepository booksRepository;

	// JPA는 EntityManager의 구현체 이다.
	@Autowired
	private EntityManager entityManager;

	protected RestDocumentationResultHandler document;

	@BeforeEach
	private void setup(WebApplicationContext webApplicationContext,
			RestDocumentationContextProvider restDocumentation) {
		this.document = document("{class-name}/{method-name}",
				Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
				Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));

		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.addFilter(new CharacterEncodingFilter(StandardCharsets.UTF_8.name(), true))
				.apply(documentationConfiguration(restDocumentation)).alwaysDo(document).build();
	}

	// 각각의 함수가 실행되기 직전에 실행되도록 하는 어노테이션 이다.
	// 이 함수를 통해 Auto_increment 값을 초기화 할 수 있습니다.
	@BeforeEach
	public void init() {
		entityManager.createNativeQuery("ALTER TABLE books AUTO_INCREMENT = 1").executeUpdate();
	}

	// BDDMockito 패턴 given, when, then
	@Test
	public void save_테스트() throws Exception {
		// given (테스트를 하기 위한 준비단계)
		Books books = new Books(null, "스프링 따라하기", "코스");
		String content = new ObjectMapper().writeValueAsString(books);

		// when :﻿테스트를 실행 하는 것 이다.
		ResultActions resultActions = mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(content).accept(MediaType.APPLICATION_JSON_UTF8));

		// then : 검증
		resultActions.andExpect(status().isCreated())// http 결과를 기대한다.
				.andExpect(jsonPath("$.title").value("스프링 따라하기")) // $(전체).변수명 .value(기대하는 값)
				.andDo(MockMvcResultHandlers.print())// 기대값이후 행동 (출력하기)
				.andDo(document); // API문서 만들기

	}

	@Test
	public void findAll_테스트() throws Exception {
		// given
		List<Books> booksList = new ArrayList<>();
		booksList.add(new Books(1L, "스프링부트 따라하기", "코스"));
		booksList.add(new Books(2L, "리엑트 따라하기", "코스"));
		booksRepository.saveAll(booksList); // 한 번에 Insert하는 방법

		// when
		ResultActions resultActions = mockMvc.perform(get("/books").accept(MediaType.APPLICATION_JSON_UTF8));

		resultActions.andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.hasSize(2))) // 전체 데이터의 길이를 기대하는 문법
				.andExpect(jsonPath("$.[0].title").value("스프링부트 따라하기")).andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void findById_테스트() throws Exception {
		// given
		Long id = 2L;

		List<Books> booksList = new ArrayList<>();
		booksList.add(new Books(1L, "스프링부트 따라하기", "코스"));
		booksList.add(new Books(2L, "리엑트 따라하기", "코스"));
		booksList.add(new Books(3L, "Junit 따라하기", "코스"));
		booksRepository.saveAll(booksList); // 한 번에 Insert하는 방법

		// when
		ResultActions resultActions = mockMvc.perform(get("/books/{id}", id).accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultActions.andExpect(status().isOk()).andExpect(jsonPath("$.title").value("리엑트 따라하기"))
				.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void update_테스트() throws Exception {
		// given
		Long id = 1L;
		List<Books> booksList = new ArrayList<>();
		booksList.add(new Books(1L, "스프링부트 따라하기", "코스"));
		booksList.add(new Books(2L, "리엑트 따라하기", "코스"));
		booksList.add(new Books(3L, "Junit 따라하기", "코스"));
		booksRepository.saveAll(booksList); // 한 번에 Insert하는 방법

		Books books = new Books(null, "씨플플 따라하기", "코스");
		String content = new ObjectMapper().writeValueAsString(books); // json으로 파싱해준다.

		// when :﻿테스트를 실행 하는 것 이다.
		ResultActions resultActions = mockMvc.perform(put("/books/{id}", id)
				.contentType(MediaType.APPLICATION_JSON_UTF8).content(content).accept(MediaType.APPLICATION_JSON_UTF8));

		// then
		resultActions.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.title").value("씨플플 따라하기")).andDo(MockMvcResultHandlers.print())
				.andDo(document.document(
						requestFields(
								fieldWithPath("id").description("번호"), 
								fieldWithPath("title").description("제목"),
								fieldWithPath("author").description("저자")
								),
						responseFields(
								fieldWithPath("id").description("번호"), 
								fieldWithPath("title").description("제목"),
								fieldWithPath("author").description("저자")
								)
						));
	}

	@Test
	public void delete_테스트() throws Exception {
		// given
		Long id = 1L;
		List<Books> booksList = new ArrayList<>();
		booksList.add(new Books(1L, "스프링부트 따라하기", "코스"));
		booksList.add(new Books(2L, "리엑트 따라하기", "코스"));
		booksList.add(new Books(3L, "Junit 따라하기", "코스"));
		booksRepository.saveAll(booksList); // 한 번에 Insert하는 방법

		// when :﻿테스트를 실행 하는 것 이다.
		ResultActions resultActions = mockMvc.perform(delete("/books/{id}", id).accept(MediaType.TEXT_PLAIN));

		// then
		resultActions.andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());

		// TEXT_PLAIN값을 받기위해선 문자열 방식으로 받아줘야 한다
		MvcResult reqMvcResult = resultActions.andReturn();
		String result = reqMvcResult.getResponse().getContentAsString();

		assertEquals("ok", result);

	}

}
