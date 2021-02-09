package com.cos.books.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

// 단위 테스트 (DB와 관련된 Bean이 IoC에 등록되면 됨)

@Slf4j
@Transactional // 독립적으로 함수를 테스트 하기위해 트랜잭션을 관리한다.
@AutoConfigureTestDatabase(replace = Replace.ANY) // 가상의 DB로 테스트 한다.
@DataJpaTest // Repositor들을 다 IoC 컨네이너에 등록준다.
public class BookRepositoryUnitTest {

	@Autowired // DI
	private BooksRepository booksRepository;
	
	@Test
	public void save_테스트() {
		// given
		Books book = new Books(null, "책제목1", "책저자1");
		
		// when
		Books bookEntity = booksRepository.save(book);
	
		// then
		assertEquals("책제목1", bookEntity.getTitle());
	}
	
	@Test
	public void findAll_테스트() {
		// given
		booksRepository.saveAll(
				Arrays.asList(
						new Books(null, "스프링부트 따라하기", "코스"),
						new Books(null, "리엑트 따라하기", "코스")
				)
			);
		
		// when
		List<Books> bookEntitys = booksRepository.findAll();
		
		// then
		log.info("bookEntitys : " + bookEntitys.size() );
		assertNotEquals(0, bookEntitys.size());
		assertEquals(2, bookEntitys.size());
	}
}
