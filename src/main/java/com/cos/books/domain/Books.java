package com.cos.books.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity // 서버 실행 시 ORM => 테이블이 h2 DB에 생성됨.
public class Books {
	@Id //pk를 해당 변수로 하겠다.
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 해당 데이터베이스 번호 증가 전략을 따라가겠다.
	private Long id;
	
	private String title;
	private String author;
	
}
