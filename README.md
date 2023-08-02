# 멋쟁이 사자처럼 백엔트 스쿨 5기 - LEEHEEJUN
# ♻️멋사마켓♻️
Spring Boot Mini Project - 중고 거래 플랫폼 api

## 📒프로젝트 개요
중고 거래 판매자가 물품을 등록하고, 댓글을 통해 구매자와 소통, 구매 제안과 수락 할 수 있는 기능을 구현

## 🗓️ 프로젝트 기간
2023-07-26 ~ 2023-08-02

## 🧑🏻‍💻 개발환경
- `java version 17`
- `Spring Boot 3.1.1`
- `SQLite`
- `IntelliJ`
- `MacOS`

## ⚙️ 구현 기능
### DB ERD
<img src="https://github.com/likelion-backend-5th/Project_1_LeeHeeJun/assets/64578367/2231d3b3-ea24-4bf5-8681-648076c2b3d6" width="500" height="500">

### 회원 가입, 로그인, 인증 및 JWT 발급
 기능 | HTTP METHOD | URL
 --- | ----------- | ---
 회원 가입 | POST | /auth/register
 로그인 | GET | /auth/login

#### 회원 가입, 로그인
- 회원 가입
  - 사용자가 입력한 username 과 password 및 기타 정보를 바탕으로 회원 가입
  - password 와 passwordcheck 항목을 비교해 사용하고하 하는 password 재확인
  - UserDetails 를 구현한 구현체를 활용해 사용자 생성
  - 생성 전 usernaem, email, password 중복 검사
  - password 는 보안을 위해 encoding 후 저장
  - 인증이 불필요한 요청 (token 유효성 검사에서 유효하지 않는 token 에 대해 예외를 발생시키기 때문에 ignore 을 통해 filter 에서 제외)
- 로그인 및 JWT 발급
  - 사용자가 입력한 username 과 password 를 바탕으로 로그인 진행
  - username 을 통해 사용자 조회
  - 조회된 사용자의 비밀번호 검증
  - 로그인이 완료된 사용자의 username 을 포함하는 JWT 발급
#### 인증
- 서버에서 발급한 JWT 의 유효성을 jwtTokenFilter 를 통해 검증
- jwtTokenFilter 에서 발생하는 예외를 처리하기 위해 jwtExceptionFilter 를 jwtTokenFilter 앞에 배치
- 유효한 JWT 에서 username 을 추출해 SecurityContext 에 저장 후 ContextHolder 에 담는다 (추후 인증된 사용자의 정보가 필요한 경우 활용)
- 기본적으로 ContextHolder 는 Thread 내에서 전역적으로 사용이 가능
- JWT 유효성 검증하는 과정에서 발생하는 예외를 커스텀해서 처리하니 예외가 발생하면 그대로 요청이 중단됨
- 회원 가입, 로그인과 같은 인증이 불필요한 요청에 대해서 permitAll 처리를 해주었지만 permitAll 한 요청은 인증이 되지 않아도 resource 를 제공받을 수 있다는 의미지 Filter 를 무시하는 것이 아니기 때문에 로그인과 회원 가입 시에도 Token 이 존재하지 않으면 요청이 중단됨.
- 인증이 불필요한 요청에 대해 ignore 처리를 해주었지만 Filter 를 Component 어노테이션을 활용해 등록하게 되면서 ApplicationFilterChain 과 SecurityFilterChain 양쪽에 등록이 되면서 SecurityFilterChain 에서는 무시를 하지만 ApplicationFilterChain 에서 걸리게 됨.
- ApplicationFilterChain 에 등록된 Filter 를 사용하지 않게 설정하는 것으로 해결
  
### 중고 물품
 기능 | HTTP METHOD | URL
 --- | ----------- | ---
 물품 등록 | POST | /items
 물품 조회 | GET | /items/{itemId}
 물품 전체 조회 | GET | /items?page={page}&limit={limit}
 물품 수정 | PUT | /items/{itemId}
 물품 이미지 등록 및 수정 | PUT | /items/{itemId}/image
 물품 삭제 | DELETE | /items/{itemId}

#### 등록, 조회, 수정, 삭제, 이미지 등록
- 등록
  - 입력된 물품 등록, 제목 / 설명 / 가격 기반으로 물품 등록
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
- 조회
  - 물품 ID 를 기반으로 단일 물품 조회
  - 전체 물품 조회 ( 페이지 단위 조회 )
- 수정
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
- 삭제
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용 
- 이미지 등록
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
  - 등록된 물품 정보에 이미지 첨부 가능
  - 이미지는 프로젝트의 src/main/resources/images/ 에 TimeStamp Data_writer.png 형태로 저장된다
  - 기본 static-path 는 /static/images/**
  - 현재 security 적용 후 정적 자원 접근에 대한 이슈가 발생
  - 정적 자원 접근 요청 설정을 permitAll, igore 을 통해 filter 에 걸리지 않게 설정해도 정적 자원에 접근이 불가한 상황
- 추가적으로 진행할 사항
  - validation 진행
  - 물품에 관한 서비스 요청 중 발생할 수 있는 예외를 조금 더 명확하게 커스텀을 진행해 처리
  - 모든 작성자 검증에서 itemEntity, userEntity 모두를 조회한 후 서로 연관관계가 있는지 확인하고 있는 상황, 이는 예외가 발생했을 때
  - 세세한 예외처리가 가능하지만 DB 조회를 할 때 join 과 where 절을 이용해 처리도 가능함

### 댓글
기능 | HTTP METHOD | URL
 --- | ----------- | ---
 댓글 등록 | POST | /items/{itemId}/comments
 댓글 전체 조회 | GET | items/{itemId}/comments?page={page}&limit={limit}
 댓글 수정 | PUT | /items/{itemId}/comments/{commentId}
 답글 등록 및 수정 | PUT | /items/{itemId}/comments/{commentId}/reply
 댓글 삭제 | DELETE | /items/{itmeId}/comments/{commentId}
 
#### 등록, 조회, 수정, 삭제, 답글 등록 및 수정
- 등록
  - 물품 ID 를 기반으로 사용자가 입력한 댓글 내용을 바탕으로 댓글 등록
  - 물품 ID 기반으로 해당 물품이 존재하는지 확인
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
- 조회
  - 물품 ID 를 기반으로 해당 물품이 존재하는지 확인 
  - 물품 ID 를 기반으로 해당 물품과 연관관계가 있는 전체 댓글 조회 ( 페이지 단위 조회 )
- 수정
  - 물품 ID 를 기반으로 해당 물품이 존재하는지 확인
  - 해당 댓글 ID 를 기반으로 존재하는 댓글인지 확인
  - 물품 ID 와 댓글 ID 로 조회한 Entity 가 서로 연관관계가 있는지 확인
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
- 답글 등록 및 수정
  - 물품 ID 를 기반으로 해당 물품이 존재하는지 확인
  - 해당 댓글 ID 를 기반으로 존재하는 댓글인지 확인
  - 물품 ID 와 댓글 ID 로 조회한 Entity 가 서로 연관관계가 있는지 확인
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
- 삭제
  - 물품 ID 를 기반으로 해당 물품이 존재하는지 확인
  - 해당 댓글 ID 를 기반으로 존재하는 댓글인지 확인
  - 물품 ID 와 댓글 ID 로 조회한 Entity 가 서로 연관관계가 있는지 확인
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
- 추가적으로 진행할 사항
  - validation 진행
  - 댓글에 관한 서비스 요청 중 발생할 수 있는 예외를 조금 더 명확하게 커스텀을 진행해 처리
  - 답글 삭제 기능

### 제안
기능 | HTTP METHOD | URL
 --- | ----------- | ---
 제안 등록 | POST | /items/{itemId}/proposals
 제안 전체 조회 | GET | /items/{itemId}/proposals?page={page}&limit={limit}
 제안 삭제 | DELETE | /items/{itmeId}/proposals/{proposalId}
 제안 수정, 수락, 거절, 구매 확정 | PUT | /items/{itemId}/proposals/{proposalId}

#### 등록, 조회, 수정, 삭제, 제안 수락 / 거절 / 구매 확정
- 등록
  - 물품 ID 를 기반으로 사용자가 입력한 제안 가격을 바탕으로 제안 생성
  - 물품 ID 기반으로 해당 물품이 존재하는지 확인
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
- 조회
  - 물품 ID 를 기반으로 해당 물품이 존재하는지 확인
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
  - 물품 ID 와 Token 에서 추출한 사용자 정보를 기반으로 물품 등록자의 조회 요청인지 제안 등록자의 조회 요청인지 분류
  - 물품 등록자의 경우 해당 물품 ID 기반으로 전체 제안 조회 ( 페이지 단위 조회 )
  - 제안 등록자의 경우 해당 물품 ID 와 작성자 정보 기반으로 제안 작성자의 제안만 조회 ( 페이지 단위 조회 )
- 제안 수정, 수락, 거절, 구매 확정
  - Validator 와 Custom Annotation 을 활용해 status 값이 {수락, 거절, 확정, null} 만 가능하도록 유효성 검사
  - 물품 ID 기반으로 해당 물품이 존재하는지 확인
  - requestBody 의 status 와 suggestPrice 항목의 null 여부를 체크해 제안의 가격 수정 요청인지 제안 수락, 거절, 구매확정 요청인지 분류
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
  - 가격 수정 요청의 경우 (사용자가 suggestPrice 항목만 포함한 경우)
    - 제안 ID 를 기반으로 제안 조회
    - 물품과 제안이 연관관계가 있는지 확인
    - Token 에서 추출한 사용자 정보를 기반으로 사용자 조회
    - 제안을 작성한 작성자인지 확인
  - 가격 수정 요청이 아닌 경우 (사용자가 status 항목만 포함한 경우)
    - 해당 요청의 RequestBody 의 Status 가 "확정" 인지 확인
    - "확정"이 아니라면 "수락" 혹은 "거절" --> 물품 등록자의 요청
      - 물품 ID 와 작성자 정보를 기반으로 작성자 정보가 일치하는지 확인
      - 물품 ID 와 제안 ID 를 기반으로 제안 조회
      - 해당 제안이 "제안" 상태이고 아직 해당 물품에 "수락" 상태인 제안이 없는지 확인 ( 구매 확정이 이루어지면 확정된 제안은 "확정" 상태가 되고 그 외에는 "거절" 상태가 되기에 "수락" 상태가 있는지만 확인 )
    - "확정"이라면 구매확정을 위한 요청 --> 제안 등록자의 요청
      - 제안 ID 와 작성자 정보를 기반으로 작성자 정보가 일치하는지 확인
      - 물품 ID 와 제안 ID 를 기반으로 제안 조회
      - 해당 제안의 상태가 "수락" 상태면 "확정" 상태로 변경, 그외 제안 "거절" 상태로 변경 및 물품의 상태를 "판매 완료" 로 변경
- 제안 삭제
  - 물품 ID 를 기반으로 해당 물품이 존재하는지 확인
  - 인증이 필요한 요청
  - Bearer Token 을 포함해 해당 토큰의 유효성을 검증하고 Token 에서 사용자 정보를 추출해 사용
  - 제안 ID 와 작성자 정보를 기반으로 작성자 정보가 일치하는지 확인
  - 물품 ID 와 제안 ID 기반으로 제안 조회
- 추가적으로 진행할 사항
  - 위의 내용과 동일

## 추가 정보
[Application.yaml & Postman Collection](https://www.notion.so/Spring-13646b407a5140c6b05c86400d94b63a)



 

