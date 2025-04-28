# careQ
📢 병원 대기, 온라인으로 번호표 뽑으세요 <br>
🌐 <a href="http://52.79.50.241:8083/">careQ 배포 사이트 바로가기</a>

## 서비스 소개  
**careQ**는 환자들이 병원 대기 시간을 최소화하고 효율적으로 관리할 수 있도록 **온라인으로 번호표를 뽑을 수 있는 서비스**입니다. 
우리의 기획 의도는 환자들에게 편의성과 접근 용이성을 제공하는 동시에, 병원 직원들에게는 대기 시간을 관리하고 환자를 효율적으로 처리할 수 있는 시스템을 제공하는 것입니다. 

**careQ**는 환자 경험과 병원 운영의 효율성을 동시에 고려하여 설계되었습니다.  
지금부터, **careQ**와 함께 기다리지 말고 원격 줄서기를 해보세요!

### ✨ What?  
1. **병원 줄서기!** careQ를 통해 온라인으로 번호표를 뽑고, 현재 대기 현황을 실시간으로 파악하세요.
2. **병원 예약!** 편리한 예약 시스템을 통해 원하는 시간에 병원 방문을 예약하세요.
3. **실시간 채팅!** 병원 관리자와의 실시간 채팅을 통해 질문이나 요청을 쉽게 전달하세요.
4. **인근 약국 조회!** 주변 약국을 찾아보세요.

## 개발기간
- 2023년 09월 24일 ~ 2023년 10월 22일
- 2023년 12월 3일 ~ 2024년 02월 08일  

## 팀원  
- 박채원, 정다인 
<img width="1152" alt="스크린샷 2024-02-09 오후 6 26 42" src="https://github.com/careQ/careQ/assets/82140052/08767c30-1156-4a9f-b908-41af7f8d60d8">

## 🔧 Tools 

<div align=center> 
  <img src="https://img.shields.io/badge/JAVA 17-407999?style=for-the-badge&logo=JAVA 17&logoColor=white">
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=for-the-badge&logo=Spring Security&logoColor=white"> 
	<img src="https://img.shields.io/badge/OAuth-43853D?style=flat-square&logo=OAuth&logoColor=white"/>
  <img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">
  <img src="https://img.shields.io/badge/lombok-C02E18?style=for-the-badge&logo=lombok&logoColor=white">
  <br>  

  <img src="https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariaDB&logoColor=white"/>
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/SpringDataJPA-53B421?style=for-the-badge&logo=SpringDataJPA&logoColor=white">  
	<img src="https://img.shields.io/badge/JUnit5-25A162?style=flat-square&logo=JUnit5&logoColor=white"/>
  <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
  <br>  
  <img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
	<img src="https://img.shields.io/badge/Bootstrapap-7952B3?style=flat-square&logo=bootstrap&logoColor=white"/>
  <img src="https://img.shields.io/badge/Tailwind-06B6D4?style=for-the-badge&logo=Tailwind&logoColor=white">
  <img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">

  <br>  
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=black">
	<img src="https://img.shields.io/badge/AWS%20EC2-232F3E?style=flat-square&logo=AmazonAWS&logoColor=white"/>
	<img src="https://img.shields.io/badge/Jenkins-D24939?style=flat-square&logo=Jenkins&logoColor=white"/>

  <br>  
</div>  

## 요구사항 명세  
<img width="1152" alt="스크린샷 2024-02-13 오후 6 11 14" src="https://github.com/careQ/careQ/assets/82140052/2efd5694-a339-40ca-ae00-ba899d871636">  

## 아키텍처  
![careQ diagram](https://github.com/careQ/careQ/assets/82140052/ea9d0a70-0b6a-41d4-aa27-e8cf4e46717b)

## 테이블 (다이어그램)  
<img width="1152" alt="스크린샷 2024-02-13 오후 6 42 23" src="https://github.com/careQ/careQ/assets/82140052/2faeec01-bfbf-40ae-86c8-45cb831adc02">  

## API 명세  
https://bouncy-mambo-779.notion.site/API-986636c07afd4471972c6c6801d61a0c?pvs=4  

## 프로토타입    
<img width="1152" alt="스크린샷 2024-02-13 오후 6 18 55" src="https://github.com/careQ/careQ/assets/82140052/82356f9e-ee45-483f-a3fe-ac4e964415cd">

##  💻  프로젝트 설명 (주요 기능)
### 1. 회원가입/로그인(관리자, 일반회원)  
- 스프링 시큐리티 적용 및 oauth 소셜(googole, naver, kakao) 로그인  
![로그인:회원가입](https://github.com/careQ/careQ/assets/82140052/111918ee-e5a6-4d7b-aa63-19de6a09400b)  
![관리자로그인](https://github.com/careQ/careQ/assets/82140052/1a64e212-a4a9-412e-abe3-558a17138478)  

### 2. 줄서기 등록 및 대기현황 파악
- 병원과 진료과목 선택 후, 줄서기 등록 및 실시간 대기인원 및 대기시간 파악  
- 관리자 줄서기 상태 처리        
![줄서기](https://github.com/careQ/careQ/assets/82140052/f29a31a2-76a9-45e4-82f5-2f73be193c1a)  

### 2. 병원 예약 시스템
- 병원 예약(중복 예약 및 당일 예약 방지) 등록 및 취소  
- 관리자 예약 확정 및 삭제 처리  
![예약](https://github.com/careQ/careQ/assets/82140052/689d8919-d1b7-4b86-976d-9d7ff16ffe57)  

### 3. 실시간 채팅
- 병원 관리자와 회원의 실시간 채팅   
![채팅](https://github.com/careQ/careQ/assets/82140052/29f4395c-7a42-467b-9d5b-1216c668e0f4)  

### 4. 인근 약국 조회  
- 전국 병/의원 공공 데이터 및 카카오 map API를 활용하여 인근 약국 조회  
![약국검색](https://github.com/careQ/careQ/assets/82140052/29d8897b-7bed-4f9b-b77f-14678bc7e772)  

### 5. 마이페이지 
- 아이디/비밀번호 변경
- 진료 목록 확인  
![마이페이지](https://github.com/careQ/careQ/assets/82140052/bd0c8bf2-1753-4c3d-8c6b-3fbd1247da2b)  

### 6. 관리자 페이지 
- 줄서기 및 예약 상태 관리
- 실시간 채팅  
<img width="827" alt="스크린샷 2024-02-13 오후 6 58 37" src="https://github.com/careQ/careQ/assets/82140052/0e37d152-6067-43be-a16d-b93b539919c5">  
