package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/sample-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // order 가 2개
        // N + 1 문제가 발생한다.
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        // 결과가 2개면 두번의 반복이 수행
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(toList());

        return result;
    }

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




}
