### 새로운 할인 정책 개발
* 기존의 고정 할인 정책을 비율 할인 정책으로 바꿔보자
* DiscountPolicy를 구현한 RateDiscountPolicy를 작성
* RateDiscountPolicy의 Test 코드 작성



### 새로운 할인 정책 적용의 문제점

* 할인 정책을 변경하려면 클라이언트인 `OrderServiceImpl` 코드를 고쳐야 한다.
* 역할과 구현을 충실히 분리함
* 다형성고 활용하고, 인터페이스와 구현 객체를 분리하였다.
* 하지만 OCP, DIP 같은 객체지향 설계 원칙을 준수하지 못했다.
* DIP
  * `OrderServiceImpl` 는 `DiscountPolicy` 만 의존하는게 아니라 `FixDiscountPolicy`, `RateDiscountPolicy`  에도 의존함으로 DIP를 준수하지 못한다.
  * 즉 `OrderServiceImpl` 가 구체화의 의존한다.
* OCP
  * 할인 정책을 바꾸기 위해서 `OrderServiceImpl` 의 코드를 수정해야하므로 OCP를 준수하지 못한다.

```java
public class OrderServiceImpl implements OrderService {

  	//private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();  // 비율 할인 정책으로 변경 보다시피 코드를 변경해야 한다.
    private final MemberRepository memberRepository = new MemoryMemberRepository();

}
```



### 어떻게 문제를 해결할 수 있을가?

1. 인터페이스만 의존하도록 설계를 변경하자!
   * 구현체가 없는데 어떻게 코드를 실행할 수 있지?
2. 누군가가 클라이언트인 `OrderServiceImpl` 에 `DiscountPolicy` 의 구현 객체를 대신 생성하고 주입해주어야 한다.

