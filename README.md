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

##### inner class 인 createMemberRequest 에서 사용자의 이름을 받아와 Member 객체의 member에 할당해준다.
##### 그리고 memberService 클래스에 접근하여 name 과 일치하는 회원 id 를 join 해준다. 
##### 마지막으로 회원 id를 return 해준다.

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
##### memberService 클래스에 update 메소드를 만들어주고 id, request를 통해 얻은 회원 name을 넘겨준다.
##### 그리고 memberService 에서 넘겨준 id와 일치하는 회원 정보를 findMember 에 저장해준다. 
##### 마지막으로 찾은 회원의 이름, id (findMember.getId(), findMember.getName()) 을 return 해준다.

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
    
    
##### entity 직접 반환하거나 노출하는 로직은 절대로 만들어서 안된다!
##### 예를 들어, Member entity에 직접 접근해서 @JsonIgnore 같은 annotation을 넣어주는 행위
##### 만약에 이런 행위를 할 시 양방향 연결이 이루어져 error detection 및 수정이 불가능해진다. 
##### @GetMapping("/api/v2/members") 로 끌어와서 memberservice의 findMembers를 통해 회원을 찾는다.
##### 그리고 회원 목록이 array로 넘어오기에 이를 stream.map에 ramda 를 이용해 memberDto class에 넘겨준다.
###### DTO(Data Transfer Object)는 VO(Value Object)로 바꿔 말할 수 있는데 계층간 데이터 교환을 위한 자바빈즈를 말합니다.
##### MemberDto 클래스에서 만들어진 회원 이름들을 collect(Collectors.toList()) 로 list로 만들어준다.
##### 마지막으로 결과를 담는 generic class Result<T> 객체를 선언하고 값을 보내준다.

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
    
##### 1. 각각의 entity 를 직접 참조해서 member, item, order 를 입력해준다.
##### 2. 다른 데이터들을 추가할 때 편리성을 위해 method 단위로 분리해준다.
##### 3. entity manager 의 persist 를 통해 바뀐 entity 정보를 편리하게 저장해준다.

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

##### order -> member / order -> address 는 지연로딩이다. 
##### 따라서 실제 entity 대신에 proxy 라는 임시 저장소를 사용한다. 
##### jackson 라이브러리는 기본적으로 이 proxy 객체를 json 으로 어떻게 생성해야 되는 지 모른다.
##### 손쉬운 방법으로는 Hibernate5Module 을 spring bean 으로 등록하여 해결가능하다. 
##### 하지만 결과적으로 이 또한 entity 직접 참조이므로 하지 않는 것이 좋다.

