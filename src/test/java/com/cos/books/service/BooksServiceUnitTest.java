package com.cos.books.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.cos.books.domain.Books;
import com.cos.books.domain.BooksRepository;

// 단위 테스트 (서비스와 관련된 것들만 메모리(IoC)에 띄우면 됨.)

// 가상의 공간에서 단위 테스트를 합니다.
@ExtendWith(MockitoExtension.class)
public class BooksServiceUnitTest {

	// BooksServiceUnitTest속 가상의 Mock공간에 있는 모든 필드를 주입 받습니다.
	@InjectMocks 
	private BooksService booksService;
	
	// 가상의 Mock의 공간에 BooksRepository를 등록합니다.
	@Mock
	private BooksRepository booksRepository;
	
	@Test
	public void 저장하기_테스트() {

		// BODMocikto 방식
		// given
		Books books = new Books();
		books.setTitle("책제목1");
		books.setAuthor("책저자1");
	
		// stub - 동작 지정
		when(booksRepository.save(books)).thenReturn(books);
		
		// test execute
		Books bookEntity = booksService.저장하기(books);
		
		// then
		assertEquals(bookEntity, books);
	}
	
}
