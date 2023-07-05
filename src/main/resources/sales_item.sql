DROP TABLE sales_item;
CREATE TABLE sales_item (
    id integer primary key autoincrement,
    title text,
    description text,
    image_url text,
    min_price_wanted integer,
    status text,
    writer text,
    password text
);

INSERT INTO sales_item (title, description, min_price_wanted, status, writer, password)
VALUES ('애플워치 팜', '애플워치 싸게 팔아요', 100000, '판매중', 'cat', 'cat1234'),
       ('맥북 프로 팔아요', '새거 같은 맥북 프로', 1000000, '판매중', 'dog', 'dog1234'),
       ('자전거 팔아요', '바퀴만 사라져서 팔아요', 50000, '판매중', 'lion', 'lion1234'),
       ('마우스 팝니다', '로지텍 마우스 팔아요! PC 방 그 마우스!', 5000, '판매중', 'elephant', 'elephant1234'),
       ('모니터... 사가요', '분조장으로 박살난 모니터 사실 분~', 1000, '판매중', 'alex', 'alex1234'),
       ('부서진 키보드 사실 분', '개발하다... 그만.. 샷건을....', 5000, '판매중', 'kain', 'kain1234'),
       ('양말 세트 팔아요', '신던거 아니에요...', 10000, '판매중', 'socks', 'socks1234'),
       ('한정판!!!', '한정판 제가 먹던 과자 팔아요', 5000, '판매중', 'snack', 'snack1234'),
       ('아이폰 13', '아이폰 13 고쳐 쓰실 분 사가세요', 1500000, '판매중', 'iphone', 'iphone1234'),
       ('롤렉스 시계 팝니다', '태국에서 산 짝퉁 아니에요', 10000000, '판매중', 'rolex', 'rolex1234'),
       ('PS5 급처!!', '와이프가 알았어요....', 400000, '판매중', 'ps', 'ps1234'),
       ('아이패드 프로 팔아요', '언제 산건지는 몰라요', 500000, '판매중', 'ipad', 'ipad1234'),
       ('닌텐도 스위치 + 동물의 숲', '닌텐도 스위치랑 동물의 숲 세트로 팔아요', 300000, '판매중', 'nintendo', 'nintendo1234'),
       ('스팸 세트', '선물 받은거 아니에요...', 20000, '판매중', 'spam', 'spam1234'),
       ('골프채 팜', '아빠꺼 몰래 팔아요', 50000, '판매중', 'golf', 'golf1234');