# Recipe Crawling Spring

레시피 정보를 크롤링하여 MongoDB에 저장하는 Spring Boot 애플리케이션

## 기술 스택

- Java 17
- Spring Boot 3.5.3
- MongoDB
- Jsoup 1.21.1 (웹 크롤링)

## 크롤링 대상 사이트

| 사이트 | Collection Name | URL |
|--------|----------------|-----|
| 오키친 | okitchen-data | https://www.okitchen.co.kr |
| 만개의레시피 | recipe-10000-data | https://www.10000recipe.com |
| 메뉴판닷컴 | menupan-data | https://www.menupan.com |
| 삼양 | samyang-data | https://m.serveq.co.kr |
| 한식진흥원 | hansik-data | https://www.hansik.or.kr |

## 환경 설정

### application.yml

각 사이트별 URL과 MongoDB Collection 이름은 `application.yml`에 정의

## 구현 내용

- 다양한 레시피 사이트 크롤링
- MongoDB에 레시피 데이터 저장
- 사이트별 인덱스 URL 관리
- CSS Selector 기반 데이터 추출

## trigger 관련

- 실행 시 Spring web(was)가 돌아가므로 해당 엔드포인트에 요청 가능
- 로컬에서 사용하는 경우 해당 url로 curl 요청 혹은 기타 방법 사용 가능
- 필자의 경우 인텔리제이 내장 기능 + .http 사용

## 참고

- Jsoup: https://github.com/jhy/jsoup