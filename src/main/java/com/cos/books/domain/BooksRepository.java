package com.cos.books.domain;

import org.springframework.data.jpa.repository.JpaRepository;

//인터페이스로 꼭 만들어 줘야한다.
//@Repository 적어야 스프링 IOC에 빈으로 등록이 되지만
//JpaRepository를 extends하면 생략하능 함.
//JpaRepository는 CRUD 함수를 들고 있음.
public interface BooksRepository extends JpaRepository<Books, Long>{

}
