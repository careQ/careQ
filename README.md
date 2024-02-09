# careQ
📢 병원 대기, 온라인으로 번호표 뽑으세요

## 서비스 소개  
**careQ**는 환자들이 병원 대기 시간을 최소화하고 효율적으로 관리할 수 있도록 **온라인으로 번호표를 뽑을 수 있는 서비스**입니다. 
우리의 기획 의도는 환자들에게 편리함과 편안함을 제공하는 동시에, 병원 직원들에게는 대기 시간을 관리하고 환자를 효율적으로 처리할 수 있는 시스템을 제공하는 것입니다. 

**careQ**는 환자 경험과 병원 운영의 효율성을 동시에 고려하여 설계되었습니다.  
지금부터, **careQ**와 함께 기다리지 말고 원격 줄서기를 해보세요!

### ✨ What?  
1. **병원 줄서기!** careQ를 통해 온라인으로 번호표를 뽑고, 현재 대기 현황을 실시간으로 파악하세요.
2. **병원 예약!** 편리한 예약 시스템을 통해 원하는 시간에 병원 방문을 예약하세요.
3. **실시간 채팅!** 병원 관리자와의 실시간 채팅을 통해 질문이나 요청을 쉽게 전달하세요.
4. **인근 약국 조회!** 주변 약국 정보를 확인하여 필요한 약품을 쉽게 찾아보세요.

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

## 테이블 (다이어그램)  
  
<img width="1152" alt="스크린샷 2024-02-09 오후 5 48 19" src="https://github.com/careQ/careQ/assets/82140052/5ea25075-57bb-4517-8b2b-905816b0a6df">  

## 아키텍처  
![careQ diagram](https://github.com/careQ/careQ/assets/82140052/ea9d0a70-0b6a-41d4-aa27-e8cf4e46717b)

##  💻  프로젝트 설명 (주요 기능)
### 1. 회원가입/로그인(관리자, 일반회원)  
- 스프링 시큐리티 적용 및 oauth 소셜(googole, naver, kakao) 로그인
<img width="1251" alt="스크린샷 2024-02-09 오후 5 25 05" src="https://github.com/careQ/careQ/assets/82140052/c0cd151b-6b39-4e43-a65e-7247d80b829b">   
<img width="1251" alt="스크린샷 2024-02-09 오후 5 24 01" src="https://github.com/careQ/careQ/assets/82140052/0e3f175a-017a-48b3-8304-abca5e6dc218">    

### 2. 줄서기 등록 및 대기현황 파악
- 병원과 진료과목 선택 후, 줄서기 등록 및 실시간 대기인원 및 대기시간 파악  
- 관리자 줄서기 상태 처리        
<img width="1251" alt="스크린샷 2024-02-09 오후 5 32 15" src="https://github.com/careQ/careQ/assets/82140052/30613d61-bd8f-4cca-ac5a-5ec0033be13d">
<img width="1251" alt="스크린샷 2024-02-09 오후 5 31 56" src="https://github.com/careQ/careQ/assets/82140052/a8c28bbe-171f-4c66-bb8d-150c54417534">  
<img width="1251" alt="스크린샷 2024-02-09 오후 5 35 17" src="https://github.com/careQ/careQ/assets/82140052/697c7cec-7344-48b1-84e8-7d9028a52b90">   

### 2. 병원 예약 시스템
- 병원 예약(중복 예약 및 당일 예약 방지) 등록 및 취소  
- 관리자 예약 확정 및 삭제 처리  
<img width="1251" alt="스크린샷 2024-02-09 오후 5 37 06" src="https://github.com/careQ/careQ/assets/82140052/fa825569-1aaf-44c6-a0e8-0caa3a344fe1">  
<img width="1251" alt="스크린샷 2024-02-09 오후 5 37 17" src="https://github.com/careQ/careQ/assets/82140052/a6debca1-40fd-4260-a335-923fd1723af1">  
<img width="1251" alt="스크린샷 2024-02-09 오후 5 38 29" src="https://github.com/careQ/careQ/assets/82140052/d77000bc-6cfd-4e6c-81f3-9dbe51040286">  
<img width="1251" alt="스크린샷 2024-02-09 오후 5 39 20" src="https://github.com/careQ/careQ/assets/82140052/7a5ce6e4-a9e1-4985-aac2-8173ea116236">  

### 3. 실시간 채팅
- 병원 관리자와 회원의 실시간 채팅   
<img width="1251" alt="스크린샷 2024-02-09 오후 5 43 36" src="https://github.com/careQ/careQ/assets/82140052/534ed4a9-7b72-46ac-b4a5-533e6658dc92">  
<img width="1251" alt="스크린샷 2024-02-09 오후 5 43 59" src="https://github.com/careQ/careQ/assets/82140052/9cac6c95-46b8-4a10-9ee2-32aba187d033">  

### 4. 인근 약국 조회  
- 전국 병/의원 공공 데이터 및 카카오 map API를 활용하여 인근 약국 조회  
<img width="1251" alt="스크린샷 2024-02-09 오후 5 45 23" src="https://github.com/careQ/careQ/assets/82140052/10ddf7e7-35ea-46f1-9dd8-1c13741c35e1">

### 5. 마이페이지 
- 아이디/비밀번호 변경
- 진료 목록 확인  
<img width="1251" alt="스크린샷 2024-02-09 오후 5 46 03" src="https://github.com/careQ/careQ/assets/82140052/4d483eef-21c2-46cf-ae18-2e5418c6dc5a">  

### 6. 관리자 페이지 
- 줄서기 및 예약 상태 관리
- 실시간 채팅  
<img width="1251" alt="스크린샷 2024-02-09 오후 5 50 35" src="https://github.com/careQ/careQ/assets/82140052/2400dbd5-a90a-4333-851a-1280632b05d6">  
