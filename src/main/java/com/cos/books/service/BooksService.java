package com.cos.books.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.cos.books.domain.Books;
import com.cos.books.domain.BooksRepository;

import lombok.RequiredArgsConstructor;

// 기능을 정의할 수 있고, 트랜잭션(CRUD)을 관리할 수 있음.

@RequiredArgsConstructor
@Service
public class BooksService {

	private final BooksRepository booksRepository;
	
	@Transactional // 서비스 함수가 종료될 때 commit할지 rollback할지 트랜잭션 관리 하겠다.
	public Books 저장하기(Books book) {
		return booksRepository.save(book);
	}
	
	// JPA에는 내부에 변경감지 기능을 비활성화.
	// update시의 정합성을 유지해줌.
	// insert의 유령데이터현상(팬텀현상)은 못 막음.
	@Transactional(readOnly = true) 
	public Books 한건가져오기(Long id) {
		// Repo에서 값을 불러오는 것이 실패할 수도있기에 예외 처리를 해줘야한다.
		// 그래서 람다표현식으로 예외 처리를 표현 해준다.
		// 람다 표현식을 사용하는 이유는 타입을 정해주지 않아도 되고 코드가 깔끔해지기 때문이다.
		return booksRepository.findById(id)
				.orElseThrow(()->new IllegalArgumentException("id를 입력해주세요!!"));
	}
	
	@Transactional(readOnly = true)
	public List<Books> 모두가져오기(){
		return booksRepository.findAll();
	}
	
	@Transactional
	public Books 수정하기(Long id, Books book) {
		// 더티 체킹으로 update하기
		Books bookEntity = booksRepository.findById(id)
				.orElseThrow(()->new IllegalArgumentException("id를 입력해주세요!!")); //영속화 (book 오브젝트) -> 영속성 컨텍스트 보관
		
		// 영속화 :  DB를 자바로 가지고 와서 오브젝트에 저장하는 것.
		bookEntity.setTitle(book.getTitle());
		bookEntity.setAuthor(book.getAuthor());
		
		return bookEntity;
	}//더티 체킹: 함수 종료 => 트랜잭션 종료 => 영속화 되어있는 데이터를 DB로 갱신(flush(비어낸다)) => DB에 commit이 된다
	
	@Transactional
	public String 삭제하기(Long id) {
		// 오류가 터지면 익셉션을 타니까 따로 만들어 주지 않아도 된다.
		booksRepository.deleteById(id);
		
		// 삭제에 성공한다면 Ok가 리턴된다.
		return "ok";
	}
}
