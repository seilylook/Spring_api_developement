# Spring MVC 개발 - API 이용

## 2020 - 12 -09
### 회원 등록 API 구현
##### main/controller/MemberController

    @PostMapping("/api/v2/members")
        public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
    
            Member member = new Member();
            member.setName(request.getName());
    
            Long id = memberService.join(member);
            return new CreateMemberResponse(id);
    
        }

* inner class 인 createMemberRequest 에서 사용자의 이름을 받아와 Member 객체의 member에 할당해준다.
* 그리고 memberService 클래스에 접근하여 name 과 일치하는 회원 id 를 join 해준다. 
* 마지막으로 회원 id를 return 해준다.

---------
## 2020 - 12 -10
### 회원 정보 수정 기능 구현
#### main/java/jpabook.jpashop/api/MemberController
    @PutMapping("/api/v2/members/{id}")
        public UpdateMemberResponse updateMemberResponse (
                @PathVariable("id") Long id,
                @RequestBody @Valid UpdateMemberRequest request) {
    
            memberService.update(id, request.getName());
            Member findMember = memberService.findOne(id);
            return new UpdateMemberResponse(findMember.getId(), findMember.getName());
        }
    
        @Data
        static class UpdateMemberRequest {
    
            private String name;
    
        }
    
        @Data
        @AllArgsConstructor
        static class UpdateMemberResponse {
    
            private Long id;
            private String name;
    
        }
* memberService 클래스에 update 메소드를 만들어주고 id, request를 통해 얻은 회원 name을 넘겨준다.
* 그리고 memberService 에서 넘겨준 id와 일치하는 회원 정보를 findMember 에 저장해준다. 
* 마지막으로 찾은 회원의 이름, id (findMember.getId(), findMember.getName()) 을 return 해준다.

---------------
## 20202 - 12 - 11
### 회원 조회 기능 구현 
#### main/java/jpabook.jpashop/api/MemberController

    @GetMapping("/api/v2/members")
        public Result membersV2() {
            List<Member> findMembers = memberService.findMembers();
            List<MemberDto> collect = findMembers.stream().map(m -> new MemberDto(m.getName()))
                    .collect(Collectors.toList());
            return new Result(collect);
        }
    
        @Data
        @AllArgsConstructor
        static class Result<T> {
            private T data;
        }
    
        @Data
        @AllArgsConstructor
        static class MemberDto {
            private String name;
    
        }
    
    
* entity 직접 반환하거나 노출하는 로직은 절대로 만들어서 안된다!
* 예를 들어, Member entity에 직접 접근해서 @JsonIgnore 같은 annotation을 넣어주는 행위
* 만약에 이런 행위를 할 시 양방향 연결이 이루어져 error detection 및 수정이 불가능해진다. 
* @GetMapping("/api/v2/members") 로 끌어와서 memberservice의 findMembers를 통해 회원을 찾는다.
* 그리고 회원 목록이 array로 넘어오기에 이를 stream.map에 ramda 를 이용해 memberDto class에 넘겨준다.
    * DTO(Data Transfer Object)는 VO(Value Object)로 바꿔 말할 수 있는데 계층간 데이터 교환을 위한 자바빈즈를 말합니다.
* MemberDto 클래스에서 만들어진 회원 이름들을 collect(Collectors.toList()) 로 list로 만들어준다.
* 마지막으로 결과를 담는 generic class Result<T> 객체를 선언하고 값을 보내준다.

----------
## 2020-12-12
### API 조회용 샘플 데이터 입력
#### 우선 이런식으로 하드코딩하는 방식은 실무에서는 사용 불가하다. 실습임을 감안하자.

    package jpabook.jpashop;
    
    import jpabook.jpashop.api.MemberApiController;
    import jpabook.jpashop.domain.*;
    import jpabook.jpashop.domain.item.Book;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;
    import org.springframework.transaction.annotation.Transactional;
    
    import javax.annotation.PostConstruct;
    import javax.persistence.EntityManager;
    
    /*
    * 총 주문 2개
    * userA
    * JPA1 BOOK
    * JPA2 BOOK
    *
    * userB
    * SPRING1 BOOK
    * SPRING2 BOOK
    * */
    
    @Component
    @RequiredArgsConstructor
    public class InitDb {
    
        private final InitService initService;
    
        @PostConstruct
        public void init() {
            initService.dbInit1();
            initService.dbInit2();
        }
    
        @Component
        @Transactional
        @RequiredArgsConstructor
        static class InitService {
    
            private final EntityManager em;
            public void dbInit1() {
                Member member = createMember("userA", "서울", "1", "1111");
                em.persist(member);
    
                Book book1 = createBook("JPA1 Book", 10000, "1111");
                em.persist(book1);
    
                Book book2 = createBook("JPA2 Book", 20000, "2222");
                em.persist(book2);
    
                Delivery delivery = createDelivery(member);
    
                OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
                OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
                Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
                em.persist(order);
            }
    
            public void dbInit2() {
                Member member = createMember("userB", "수원", "2", "2222");
                em.persist(member);
    
                Book book1 = createBook("Spring Book1", 15000, "3333");
                em.persist(book1);
    
                Book book2 = createBook("Spring Book2", 25000, "4444");
                em.persist(book2);
    
                Delivery delivery = createDelivery(member);
    
                OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
                OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
                Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
                em.persist(order);
            }
    
            private Member createMember(String name, String city, String street, String zipcode) {
                Member member = new Member();
                member.setName(name);
                member.setAddress(new Address(city, street, zipcode));
                return member;
            }
    
            private Book createBook(String name, int price, String isbn) {
                Book book1 = new Book();
                book1.setName(name);
                book1.setPrice(price);
                book1.setStockQuantity(100);
                book1.setIsbn(isbn);
                return book1;
            }
    
            private Delivery createDelivery(Member member) {
                Delivery delivery = new Delivery();
                delivery.setAddress(member.getAddress());
                return delivery;
            }
        }
    }
    
* 각각의 entity 를 직접 참조해서 member, item, order 를 입력해준다.
* 다른 데이터들을 추가할 때 편리성을 위해 method 단위로 분리해준다.
* entity manager 의 persist 를 통해 바뀐 entity 정보를 편리하게 저장해준다.

---------------

## 2020 - 12 -13
### Module 을 사용한 Entity 직접 노출 (사용하면 안되는 이유)
    package jpabook.jpashop.api;
    
    import jpabook.jpashop.domain.Order;
    import jpabook.jpashop.repository.OrderRepository;
    import jpabook.jpashop.repository.OrderSearch;
    import lombok.RequiredArgsConstructor;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import java.util.List;
    
    /*
    * xToOne(ManyToOne, OneToOne)ß
    * order
    * order -> member (many to one)
    * order -> delivery (one to one)
    * order -> orderItem (one to many)
    * */
    @RestController
    @RequiredArgsConstructor
    public class OrderSimpleApiController {
    
        private final OrderRepository orderRepository;
    
        @GetMapping("/api/v1/sample-orders")
        public List<Order> ordersV1() {
            List<Order> all = orderRepository.findAllByString(new OrderSearch());
            return all;
        }
    
    
    
    }

* order -> member / order -> address 는 지연로딩이다. 
* 따라서 실제 entity 대신에 proxy 라는 임시 저장소를 사용한다. 
* jackson 라이브러리는 기본적으로 이 proxy 객체를 json 으로 어떻게 생성해야 되는 지 모른다.
* 손쉬운 방법으로는 Hibernate5Module 을 spring bean 으로 등록하여 해결가능하다. 
* 하지만 결과적으로 이 또한 entity 직접 참조이므로 하지 않는 것이 좋다.

-------------
## 2020 - 12 - 14
### 간단한 주문 조회: entity 를 DTO 로 변경. 이를 통해 entity spec 을 유지해준다. 
    @GetMapping("/api/v2/simple-orders")
        public List<SimpleOrderDto> ordersV2() {
            // order 가 2개
            // N + 1 문제가 발생한다.
            List<Order> orders = orderRepository.findAllByString(new OrderSearch());
    
            // 결과가 2개면 두번의 반복이 수행
            List<SimpleOrderDto> result = orders.stream()
                    .map(o -> new SimpleOrderDto(o))
                    .collect(Collectors.toList());
    
            return result;
        }
    
        @Data
        static class SimpleOrderDto {
            private Long orderId;
            private String name;
            private LocalDateTime orderDate;
            private OrderStatus orderStatus;
            private Address address;
    
            public SimpleOrderDto(Order order) {
                orderId = order.getId();
                name = order.getMember().getName(); // LAZY 초기화
                orderDate = order.getOrderDate();
                orderStatus = order.getStatus();
                address = order.getDelivery().getAddress();
            }
        }
* entity 를 직접 노출하지 않고 dto 를 통해 로직을 짜게되면 
* 예를 들어 entity 변수명을 바꾸더라도 contoller 에서 사용할 때 에러가 발생해 쉽게 캐치할 수 있다.
* 하지만 앞서 v1, v2 둘 다 lazy loading 으로 인한 불필요한 query 호출이 발생하는 것이다. 
* entity 를 DTO 로 변환하는 것이 일반적이고 안정적인 방법이다.
* 쿼리가 총 N + 1 번 실행된다.
    * order 조회 1번 (order 조회 결과 수가 N 개가 된다.)
    * order -> member 지연 로딩 조회 N 번
    * order -> delivery 지연 로딩 조회 N 번
    * 예) order 의 결과가 4개면 worst case 1 + 4 -> 4번 실행된다.
        * 지연 로딩 (LAZY Loading)은 영속성 context 에서 조회하므로, 이미 조회된 경우 쿼리를 생략한다. 

---------------

## 2020 - 12 - 17
### fetch & join 을 이용한 최적화
#### /api/OrderSimpleApiController
    @GetMapping("/api/v3/simple-orders")
        public List<SimpleOrderDto> orderV3() {
            List<Order> orders = orderRepository.findAllWithMemberDelivery();
            // 실무에서 대부분의 문제는 n+1 문제로 인해 발생한다.
            // 기본적으로 lazy로 깔고 내가 필요한것만 fetch join으로 묶어서 디비에서 한방에 가져온다면
            // n+1문제가 발생하지 않게 된다.
            List<SimpleOrderDto> result = orders.stream()
                    .map(o -> new SimpleOrderDto(o))
                    .collect(toList());
    
            return result;
        }
        // v2와 v3는 기능적으로는 똑같다.
        // 하지만 query 호출을 보게 된다면 왜 fetch join을 사용해야 되는지 알 수 있다.
        // fetch 라는 JPA 명령어를 통해 간단하게 해결할 수 있는 것이다.
        // fetch join 으로 order -> member, order -> delivery 는 이미 조회된 상태 이므로
        // lazy loading 이 일어나지 않는다.

* 실무에서의 대부분 문제는 n + 1 문제로 인해 발생한다.
* 기본적으로 lazy 를 깔고 내가 필요한 것만 fetch join으로 묶어 db에서 한번에 가져온다면
* n + 1 문제가 발생하지 않게 된다.
* v2와 v3는 기능적으로는 똑같다.
* 하지만 query 호출을 보게 된다면 왜 fetch join을 사용해야 되는지 알 수 있다.
* fetch 라는 JPA 명령어를 통해 간단하게 해결할 수 있는 것이다.
* fetch join 으로 order -> member, order -> delivery 는 이미 조회된 상태 이므로
* lazy loading 이 일어나지 않는다.

    public List<Order> findAllWithMemberDelivery() {
            return em.createQuery(
    
        "select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
                    .getResultList();
    
            // order 를 가져올 때 객체 그래프까지 한번에 가져오고 싶어서 사용한다.
            // 이렇게 하면 order를 조회하는데 sql 쪽에서는 join 이면서
            // select 를 절에서 같이 한번에 가져오게 된다.
            // 객체의 값을 채워서 다같이 가져와 버린다.
            // 이를 바로 패치 조인 이라고 한다.
            // 기술적으로는 join 을 sql 에 던져주는데 이는 sql 에는 없다.
            // jpa 에 있는 fetch 를 사용해서 가져오게 되는 것이다.
        }

* v2와 v3는 기능적으로는 똑같다.
* 하지만 query 호출을 보게 된다면 왜 fetch join을 사용해야 되는지 알 수 있다.
* fetch 라는 JPA 명령어를 통해 간단하게 해결할 수 있는 것이다.
* fetch join 으로 order -> member, order -> delivery 는 이미 조회된 상태 이므로
* lazy loading 이 일어나지 않는다.

-------------

## 2020 - 12 - 19
### 간단한 주문 조회 V4: JPA에서 DTO로 바로 조회
#### jpabook/jpashop/repository/order.repository/OrderSimpleQueryDto
    package jpabook.jpashop.repository.order.simplequery;
    
    import jpabook.jpashop.domain.Address;
    import jpabook.jpashop.domain.Order;
    import jpabook.jpashop.domain.OrderStatus;
    import lombok.Data;
    
    import java.time.LocalDateTime;
    
    @Data
    public class OrderSimpleQueryDto {
    
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
    
        public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address) {
            this.orderId = orderId;
            this.name = name;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
            this.address = address;
        }
    }
##### 이는 조회 전용 repository

#### jpabook/jpashop/repository/order.repository/OrderSimpleQueryRepository
    package jpabook.jpashop.repository.order.simplequery;
    
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Repository;
    
    import javax.persistence.EntityManager;
    import java.util.List;
    
    @Repository
    @RequiredArgsConstructor
    public class OrderSimpleQueryRepository {
        private final EntityManager em;
    
        public List<OrderSimpleQueryDto> findOrderDtos() {
            return em.createQuery(
                    "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
    
                            " from Order o" +
                            " join o.member m" +
                            " join o.delivery d", OrderSimpleQueryDto.class)
                    .getResultList();
        }
    }

* 일반적인 SQL 사용할 때 처럼 원하는 값을 선택해서 조회
* new 명령어를 사용해서 JPQL 의 결과를 DTO로 즉시 변환
* SELECT 절에서 원하는 데이터를 직접 선택하므로 DB -> 애플리케이션 네트워크 용량 최적화 
* repository 재사용성 떨어진다. API 스펙에 맞춘 코드가 repository 에 들어가는 단점이 있다. 

#### jpabook/jpashop/api/OrderSimpleApiController
    @GetMapping("/api/v4/simple-orders")
        public List<OrderSimpleQueryDto> orderV4() {
            // jpa 에서 dto 로 바로 꺼내올 수 있는 기능을 구현하자.
            return orderSimpleQueryRepository.findOrderDtos();
            // 이렇게 해놓으면 select 절에서 호출값이 줄어든다.
            // 이것이 가능한 이유는 query 를 직접 작성했기에 가능한 것이다.
            // 그렇다고 v4가 v3 무작정 좋은 것은 아니다.
            // v3는 외부의 상태를 건드리지 않고 자유롭게 변경할 수 있다.
            // 이에 반해 v4는 dto로 조회하기 때문에 변경이 어렵다.
            // repository는 entity 를 조회하는데 써야한다.
            // 그래서 v3 사용해도 무방하다고 생각 가능
            // v4의 단점을 보완하고자 새로운 repository를 만들어준다.
        }
* 이렇게 해놓으면 select 절에서 호출값이 줄어든다.
* 이것이 가능한 이유는 query 를 직접 작성했기에 가능한 것이다.
* 그렇다고 v4가 v3 무작정 좋은 것은 아니다.
* v3는 외부의 상태를 건드리지 않고 자유롭게 변경할 수 있다.
* 이에 반해 v4는 dto로 조회하기 때문에 변경이 어렵다.
* repository는 entity 를 조회하는데 써야한다.
* 그래서 v3 사용해도 무방하다고 생각 가능
* v4의 단점을 보완하고자 새로운 repository를 만들어준다.


