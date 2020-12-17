package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    //== 주문에서 검색 ==//
    public List<Order> findAllByString(OrderSearch orderSearch) {
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

    //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

    //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

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

}
