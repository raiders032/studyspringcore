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



### 관심사의 분리

* 현재 코드의 문제점을 비유해보자면 배우가 공연도 하고 상대방을 캐스팅하는 다양한 책임을 가지고 있는 것이다.
* `배우`는 본인의 역할인 배역을 수행하는데 집중하고 역할에 맞는 배우를 지정하는 책임을 가지는 별도의 `공연 기획자`가 필요한 시점이다.



### AppConfig 생성

``` java
public class AppConfig {

    public MemberService memberService(){
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService(){
        return new OrderServiceImpl(new FixDiscountPolicy(), new MemoryMemberRepository());
    }

}
```

* AppConfig는 애플리케이션의 실제 동작에 필요한 **구현 객체를 생성**한다.
* AppConfig는 생성한 객체 인스턴스의 참조(레퍼런스)를 생성자를 통해서 주입(연결)해준다.



### OrderServiceImpl 생성자 주입

```java
public class OrderServiceImpl implements OrderService {

    private final DiscountPolicy discountPolicy;
    private final MemberRepository memberRepository;

    public OrderServiceImpl(DiscountPolicy discountPolicy, MemberRepository memberRepository) {
        this.discountPolicy = discountPolicy;
        this.memberRepository = memberRepository;
    }
  
}
```

* 설계의 변경으로  `OrderServiceImpl` 은 `FixDiscountPolicy` 를 의존하지 않는다
* `OrderServiceImpl` 의 생성자를 통해서 어떤 구현 객체을 주입할지는 오직 외부( AppConfig )에서 결정 한다.
* DIP 원칙을 준수하게 되었다!!



### AppConfig 리팩터링

```java
public class AppConfig {

    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    private DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }

    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }

    public OrderService orderService(){
        return new OrderServiceImpl(discountPolicy(), memberRepository());
    }

}
```



### 새로운 구조와 할인 정책 적용

* `FixDiscountPolicy` 를 `RateDiscountPolicy` 로 바꿔보자
* `AppConfig` 의 등장으로 애플리케이션을 크게 `사용 영역`과 객체를 생성하고 구성하는 `구성 영역`으로 분리되었다.

**고정 할인 정책 사용할 때 OrderApp출력 결과**

```bash
order = Order{memberId=1, itemName='spring', itemPrice=20000, discountPrice=1000}
order.calculatePrice = 19000
```

**새로운 할인 정책 사용**

* AppConfig만 수정하면 된다.
* `구성 영역`을 수정해서 할인 정책 역할을 담당하는 구현을 `FixDiscountPolicy` 에서 `RateDiscountPolicy` 로 변경했다
* `사용 영역`의 어떠한 코드도 수정하지 않았다
* 할인 정책을 바꾸기 위해서 `OrderServiceImpl` 의 코드를 수정할 필요가 없으므로 `OCP`를 준수한다.

```java
public class AppConfig {

    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    private DiscountPolicy discountPolicy() {
      	// RateDiscountPolicy로 정책을 변경
        return new RateDiscountPolicy();
    }

    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }

    public OrderService orderService(){
        return new OrderServiceImpl(discountPolicy(), memberRepository());
    }

}
```

**AppConfig 수정 후 비율 할인 정책 사용할 때 OrderApp출력 결과**

```bash
order = Order{memberId=1, itemName='spring', itemPrice=20000, discountPrice=2000}
order.calculatePrice = 18000
```



### 좋은 객체 지향 설계의 5가지 원칙의 적용

**SRP**

> 한 클래스는 하나의 책임만 가져야 한다.

* 클라이언트 객체는 직접 구현 객체를 생성하고, 연결하고, 실행하는 다양한 책임을 가지고 있다
*  SRP 단일 책임 원칙을 따르면서 관심사를 분리함
*  구현 객체를 생성하고 연결하는 책임은 AppConfig가 담당
*  클라이언트 객체는 실행하는 책임만 담당



**DIP 의존관계 역전 원칙**

> 프로그래머는 "추상화에 의존해야지, 구체화에 의존하면 안된다." 의존성 주입은 이 원칙을 따르는 방법 중 하나다

* 새로운 할인 정책을 적용하려고 하니 클라이언트 코드도 함께 변경해야 했다. 
* 클라이언트 코드 `OrderServiceImpl` 는 DIP를 지키며 `DiscountPolicy` 추상화 인터페이스에 의존하는 것 같았지만, `FixDiscountPolicy` 구체화 구현 클래스에도 함께 의존했다.
* 클라이언트 코드가 `DiscountPolicy` 추상화 인터페이스에만 의존하도록 코드를 변경했다.
* 하지만 클라이언트 코드는 인터페이스만으로는 아무것도 실행할 수 없다.
*  `AppConfig`가 `FixDiscountPolicy` 객체 인스턴스를 클라이언트 코드 대신 생성해서 클라이언트 코드 에 의존관계를 주입했다. 
* 이렇게해서 `DIP` 원칙을 따르면서 문제도 해결했다.



**OCP**

> **소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀 있어야 한다**

* 다형성 사용하고 클라이언트가 DIP를 지킴
* 애플리케이션을 사용 영역과 구성 영역으로 나눔
* `AppConfig`가 의존관계를 `FixDiscountPolicy` 에서 `RateDiscountPolicy` 로 변경해서 클라이언트 코드에 주입하므로 클라이언트 코드는 변경하지 않아도 됨
* **소프트웨어 요소를 새롭게 확장해도 사용 역영의 변경은 닫혀 있다**



___



## IoC, DI, 그리고 컨테이너



### 제어의 역전 IoC(Inversion of Control)

* 기존 프로그램은 클라이언트 구현 객체가 스스로 필요한 서버 구현 객체를 생성하고, 연결하고, 실행했다. 한 마디로 구현 객체가 프로그램의 제어 흐름을 스스로 조종했다. 
* `AppConfig`가 등장한 이후에 구현 객체는 자신의 로직을 실행하는 역할만 담당한다. 
* 프로그램의 제어 흐름은 이제 AppConfig가 가져간다. 
  * 예를 들어서 `OrderServiceImpl` 은 필요한 인터페이스들을 호출하지만 어떤 구현 객체들이 실행될지 모른다.
* 프로그램에 대한 제어 흐름에 대한 권한은 모두 AppConfig가 가지고 있다. 심지어 OrderServiceImpl 도 AppConfig가 생성한다. 그리고 AppConfig는 OrderServiceImpl 이 아닌 OrderService 인터페이스의 다른 구현 객체를 생성하고 실행할 수 도 있다. 그런 사실도 모른체 OrderServiceImpl 은 묵묵히 자신의 로직을 실행할 뿐이다.

* 이렇듯 프로그램의 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것을 제어의 역전(IoC)이라 한다.



**프레임워크 vs 라이브러리**

* 프레임워크가 내가 작성한 코드를 제어하고, 대신 실행하면 그것은 프레임워크가 맞다. (JUnit)
* 반면에 내가 작성한 코드가 직접 제어의 흐름을 담당한다면 그것은 프레임워크가 아니라 라이브러리다.



### 의존관계 주입 DI(Dependency Injection)

* `OrderServiceImpl` 은 `DiscountPolicy` 인터페이스에 의존한다. 
* 실제 어떤 구현 객체가 사용될지는 모른다.
* 의존관계는 정적인 클래스 의존 관계와, 실행 시점에 결정되는 동적인 객체(인스턴스) 의존 관계 둘을 분리해서 생각해야 한다.

**정적인 클래스 의존관계**

* 클래스가 사용하는 import 코드만 보고 의존관계를 쉽게 판단할 수 있다. 
* 정적인 의존관계는 애플리케이션 을 실행하지 않아도 분석할 수 있다.
* `OrderServiceImpl` 은 `MemberRepository` , `DiscountPolicy` 에 의존한다는 것을 알 수 있다.

```java
import com.example.studyspringcore.discount.DiscountPolicy;
import com.example.studyspringcore.member.Member;
import com.example.studyspringcore.member.MemberRepository;

public class OrderServiceImpl implements OrderService {

    private final DiscountPolicy discountPolicy;
    private final MemberRepository memberRepository;

    public OrderServiceImpl(DiscountPolicy discountPolicy, MemberRepository memberRepository) {
        this.discountPolicy = discountPolicy;
        this.memberRepository = memberRepository;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member findMember = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(findMember, itemPrice);
        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
```



**동적인 객체 인스턴스 의존 관계**

* 애플리케이션 실행 시점에 실제 생성된 객체 인스턴스의 참조가 연결된 의존 관계다.
* 애플리케이션 실행 시점(런타임)에 외부에서 실제 구현 객체를 생성하고 클라이언트에 전달해서 클라이언트와 서버의 실제 의존관계가 연결 되는 것을 **의존관계 주입**이라 한다.
* 객체 인스턴스를 생성하고, 그 참조값을 전달해서 연결된다.
* 의존관계 주입을 사용하면 클라이언트 코드를 변경하지 않고, 클라이언트가 호출하는 대상의 타입 인스턴스를 변경할 수 있다.
* 의존관계 주입을 사용하면 정적인 클래스 의존관계를 변경하지 않고, 동적인 객체 인스턴스 의존관계를 쉽게 변경할 수 있다.



### IoC 컨테이너, DI 컨테이너

* AppConfig 처럼 객체를 생성하고 관리하면서 의존관계를 연결해 주는 것을 **IoC 컨테이너** 또는 **DI 컨테이너**라 한다.
* 의존관계 주입에 초점을 맞추어 최근에는 주로 DI 컨테이너라 한다.
* 또는 어샘블러, 오브젝트 팩토리 등으로 불리기도 한다.



## 스프링으로 전환하기

### AppConfig 스프링 기반으로 변경

```java
@Configuration
public class AppConfig {

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(discountPolicy(), memberRepository());
    }

}
```



**MemberApp에 스프링 컨테이너 적용**

```java
public class MemberApp {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        
        Member memberA = new Member(1L, "memberA", Grade.VIP);
        memberService.join(memberA);
        Member findMember = memberService.findMember(1L);
        
        System.out.println("new member = " + memberA.getName() );
        System.out.println("find member = " + findMember.getName());
    }
}
```



### 스프링 컨테이너

* `ApplicationContext` 를 `스프링 컨테이너`라 한다.
* 기존에는 개발자가 AppConfig 를 사용해서 직접 객체를 생성하고 DI를 했지만, 이제부터는 스프링 컨테이너를 통해서 사용한다.
* 스프링 컨테이너는 @Configuration 이 붙은 AppConfig 를 설정(구성) 정보로 사용한다. 
* 여기서 @Bean 이라 적힌 메서드를 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. 
* 스프링 컨테이너에 등록된 객체를 `스프링 빈`이라 한다.
  * @Bean 이 붙은 메서드의 명을 스프링 빈의 이름으로 사용한다. ( memberService , orderService )
* 스프링 컨테이너를 통해서 필요한 스프링 빈(객체)를 찾는다.
  * applicationContext.getBean() 메서드 를 사용해서 찾을 수 있다.
* 기존에는 개발자가 직접 자바코드로 모든 것을 했다면 이제부터는 스프링 컨테이너에 객체를 스프링 빈으로 등록하고, 스프링 컨테이너에서 스프링 빈을 찾아서 사용하도록 변경되었다.
  



## 스프링 컨테이너와 스프링 빈



### 스프링 컨테이너 생성

```java
  ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```

* `ApplicationContext` 를 스프링 컨테이너라 한다.
* 스프링 컨테이너는 XML 또는 애노테이션 기반의 자바 설정 클래스로 만들 수 있다.
* ApplicationContext : 인터페이스
* AnnotationConfigApplicationContext: 구현체

> 더 정확히는 스프링 컨테이너를 부를 때 `BeanFactory` , `ApplicationContext` 로 구분해서 이야기 한다. 이 부분은 뒤에서 설명하겠다. `BeanFactory` 를 직접 사용하는 경우는 거의 없으므로 일반적으로 `ApplicationContext` 를 스프링 컨테이너라 한다.



### 스프링 컨테이너 생성 과정

1. 스프링 컨테이너 생성
   * `new AnnotationConfigApplicationContext(AppConfig.class)`
   * 스프링 컨테이너를 생성할 때 구성 정보(AppConfig.class)를 넘겨주어야 한다.
2. 스프링 빈 등록
   * 스프링 컨테이너는 파라미터로 넘어온 설정 클래스 정보를 사용해서 스프링 빈을 등록한다.
3. 스프링 빈 의존관계 설정 - 준비
4. 스프링 빈 의존관계 설정 - 완료
   * 스프링 컨테이너는 설정 정보를 참고해서 의존관계를 주입(DI)한다.
   * 단순히 자바 코드를 호출하는 것 과는 차이가 있다.

### 컨테이너에 등록된 모든 빈 조회

* `ac.getBeanDefinitionNames()` : 스프링에 등록된 모든 빈 이름을 조회한다.
* `ac.getBean()` : 빈 이름으로 빈 객체(인스턴스)를 조회한다.
* ROLE_APPLICATION : 일반적으로 사용자가 정의한 빈 
* ROLE_INFRASTRUCTURE : 스프링이 내부에서 사용하는 빈

```java
class ApplicationContextInfoTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    void printAllBeans() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName);
            System.out.println("name: " + beanDefinitionName + " bean: " + bean);
        }
    }

    @Test
    @DisplayName("모든 빈 출력하기")
    void printAllApplicationBeans() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name: " + beanDefinitionName + " bean: " + bean);
            }
        }
    }
}
```



printAllApplicationBeans 출력결과

```bash
...
name: appConfig bean: com.example.studyspringcore.AppConfig$$EnhancerBySpringCGLIB$$b5cd41de@732f29af
name: memberRepository bean: com.example.studyspringcore.member.MemoryMemberRepository@d3957fe
name: discountPolicy bean: com.example.studyspringcore.discount.RateDiscountPolicy@6622fc65
name: memberService bean: com.example.studyspringcore.member.MemberServiceImpl@299321e2
name: orderService bean: com.example.studyspringcore.order.OrderServiceImpl@23fb172e
...
```



### 스프링 빈 조회 - 기본

* `ac.getBean(빈이름, 타입)`
* `ac.getBean(타입)`

> 구체 타입으로 조회하면 변경시 유연성이 떨어진다.

```java
public class ApplicationContextBasicFindTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("빈 이름으로 조회")
    void findBeanByName() {
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("빈 타입으로 조회")
    void findBeanByType() {
        MemberService memberService = ac.getBean(MemberService.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("구체 타입으로 조회")
    void findBeanByName2() {
        MemberService memberService = ac.getBean("memberService", MemberServiceImpl.class);
        assertThat(memberService).isInstanceOf(MemberServiceImpl.class);
    }

    @Test
    @DisplayName("빈 이름으로 조회 X")
    void findBeanByNameX() {
        assertThrows(NoSuchBeanDefinitionException.class,
                () -> ac.getBean("MemberService", MemberService.class));
    }
}
```



### 스프링 빈 조회 - 동일한 타입이 둘 이상

* 타입으로 조회시 같은 타입의 스프링 빈이 둘 이상이면 오류가 발생한다. 이때는 빈 이름을 지정하자.
*  `ac.getBeansOfType()` 을 사용하면 해당 타입의 모든 빈을 조회할 수 있다.

```java
public class ApplicationContextSameBeanFindTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SameBeanConfig.class);

    @Test
    @DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 중복 오류가 발생한다.")
    void findBeanByTypeDuplicate() {
        Assertions.assertThrows(NoUniqueBeanDefinitionException.class,
                () -> ac.getBean(MemberRepository.class)
        );
    }

    @Test
    @DisplayName("타입으로 조회시 같은 타입이 둘 이상 있으면, 이름을 지정한다.")
    void findBeanByNameAndType() {
        MemberRepository memberRepository = ac.getBean("memberRepository1", MemberRepository.class);
        assertThat(memberRepository).isInstanceOf(MemberRepository.class);
    }

    @Test
    @DisplayName("특정 타입 모두 조회하기")
    void findAllBeanByType(){
        Map<String, MemberRepository> beansOfType = ac.getBeansOfType(MemberRepository.class);
        for (String key : beansOfType.keySet()) {
            System.out.println("key :" + key + " value: " + beansOfType.get(key));
        }
        assertThat(beansOfType.size()).isEqualTo(2);
    }

    @Configuration
    static class SameBeanConfig {

        @Bean
        public MemberRepository memberRepository1() {
            return new MemoryMemberRepository();
        }

        @Bean
        public MemberRepository memberRepository2() {
            return new MemoryMemberRepository();
        }
    }

}
```



### 스프링 빈 조회 - 상속 관계

* 부모 타입으로 조회하면, 자식 타입도 함께 조회한다.
* 모든 자바 객체의 최고 부모인 Object 타입으로 조회하면, 모든 스프링 빈을 조회한다.

```java
public class ApplicationContextExtendsFindTest {

    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

    @Test
    @DisplayName("부모 타입으로 조회시, 자식이 둘 이상이면 중복 오류가 발생한다.")
    void findBeanByTypeDuplicate() {
        Assertions.assertThrows(NoUniqueBeanDefinitionException.class,
                () -> ac.getBean(DiscountPolicy.class)
        );
    }

    @Test
    @DisplayName("부모 타입으로 조회시, 자식이 둘 이상이면 빈 이름을 지정하면 된다.")
    void findBeanByTypeAndName() {
        DiscountPolicy rateDiscountPolicy = ac.getBean("rateDiscountPolicy", DiscountPolicy.class);
        assertThat(rateDiscountPolicy).isInstanceOf(DiscountPolicy.class);
    }

    @Test
    @DisplayName("특정 하위 타입으로 조회하기")
    void findBeanBySubType() {
        DiscountPolicy rateDiscountPolicy = ac.getBean(RateDiscountPolicy.class);
        assertThat(rateDiscountPolicy).isInstanceOf(RateDiscountPolicy.class);
    }

    @Test
    @DisplayName("부모 타입으로 모두 조회하기")
    void findByParentType() {
        Map<String, DiscountPolicy> beansOfType = ac.getBeansOfType(DiscountPolicy.class);
        assertThat(beansOfType.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Object 타입으로 모두 조회하기")
    void findByObjectType() {
        Map<String, Object> beansOfType = ac.getBeansOfType(Object.class);
        for (String key : beansOfType.keySet()) {
            System.out.println("key: " + key + " value: " + beansOfType.get(key));
        }
    }

    @Configuration
    static class TestConfig {

        @Bean
        public DiscountPolicy rateDiscountPolicy() {
            return new RateDiscountPolicy();
        }

        @Bean
        public DiscountPolicy fixDiscountPolicy() {
            return new FixDiscountPolicy();
        }
    }
}
```



## BeanFactory와 ApplicationContext

![image-20210202140124622](./images/image-20210202140124622.png)

**BeanFactory**

* 스프링 컨테이너의 최상위 인터페이스다.
* 스프링 빈을 관리하고 조회하는 역할을 담당한다.
* getBean() 을 제공한다.
* 지금까지 우리가 사용했던 대부분의 기능은 BeanFactory가 제공하는 기능이다.



**ApplicationContext**

* BeanFactory 기능을 모두 상속받아서 제공한다.
* 빈을 관리하고 검색하는 기능을 BeanFactory가 제공해주는데, 그러면 둘의 차이가 뭘까?
* 애플리케이션을 개발할 때는 빈은 관리하고 조회하는 기능은 물론이고, 수 많은 부가기능이 필요하다.

 	![image-20210202140552465](./images/image-20210202140552465.png)



* 메시지소스를 활용한 국제화 기능
  * 예를 들어서 한국에서 들어오면 한국어로, 영어권에서 들어오면 영어로 출력 
* 환경변수
  * 로컬, 개발, 운영등을 구분해서 처리 
* 애플리케이션 이벤트
  * 이벤트를 발행하고 구독하는 모델을 편리하게 지원
* 편리한 리소스 조회
  * 파일, 클래스패스, 외부 등에서 리소스를 편리하게 조회

**정리**

* ApplicationContext는 BeanFactory의 기능을 상속받는다.
* ApplicationContext는 빈 관리기능 + 편리한 부가 기능을 제공한다.
* BeanFactory를 직접 사용할 일은 거의 없다. 부가기능이 포함된 ApplicationContext를 사용한다. 
* BeanFactory나 ApplicationContext를 스프링 컨테이너라 한다.



### 스프링 빈 설정 메타 정보 - BeanDefinition

* 스프링은 어떻게 이런 다양한 설정 형식을 지원하는 것일까? 그 중심에는 BeanDefinition 이라는 추상화 가 있다.
* 쉽게 이야기해서 **역할과 구현을 개념적으로 나눈 것**이다!
  * XML을 읽어서 BeanDefinition을 만들면 된다.
  * 자바 코드를 읽어서 BeanDefinition을 만들면 된다.
  * 스프링 컨테이너는 자바 코드인지, XML인지 몰라도 된다. 오직 BeanDefinition만 알면 된다.
* BeanDefinition 을 빈 설정 메타정보라 한다.
  * @Bean , <bean> 당 각각 하나씩 메타 정보가 생성된다.
* 스프링 컨테이너는 이 메타정보를 기반으로 스프링 빈을 생성한다.

![image-20210202141709754](/Users/YT/Desktop/project/studyspringcore/images/image-20210202141709754.png)

* AnnotationConfigApplicationContext 는 AnnotatedBeanDefinitionReader 를 사용해서 AppConfig.class 를 읽고 BeanDefinition 을 생성한다
* GenericXmlApplicationContext 는 XmlBeanDefinitionReader 를 사용해서 appConfig.xml 설정 정보를 읽고 BeanDefinition 을 생성한다.
* 새로운 형식의 설정 정보가 추가되면, XxxBeanDefinitionReader를 만들어서 BeanDefinition 을 생성 하면 된다.



## 싱글톤 컨테이너

## 

### 웹 애플리케이션과 싱글톤

> 스프링은 태생이 기업용 온라인 서비스 기술을 지원하기 위해 탄생했다.  따라서 스프링 애플리케이션의 대부분은 웹 애플리케이션이다. 보통 여러 고객이 동시에 웹 애플리케이션의 요청을 한다.



**스프링 없는 순수한 DI 컨테이너 테스트**

![image-20210202143347673](./images/image-20210202143347673.png)



**문제점**

1. 요청을 할 때 마다 객체를 새로 생성한다.
2. 고객 트래픽이 초당 100이 나오면 초당 100개 객체가 생성되고 소멸된다
   * 메모리 낭비가 심하다.



**해결법**

* 해당 객체가 딱 1개만 생성되고, 공유하도록 설계하면 된다 -> 싱글톤 패턴



## 싱글톤 패턴

* 클래스의 인스턴스가 딱 1개만 생성되는 것을 보장하는 디자인 패턴이다.
  * private 생성자를 사용해서 외부에서 임의로 new 키워드를 사용하지 못하도록 막아야 한다.

```java
public class SingletonService {

    private static final SingletonService instance = new SingletonService();


    private SingletonService() {
    }

    public static SingletonService getInstance(){
        return instance;
    }
}
```

* static 영역에 객체 instance를 미리 하나 생성해서 올려둔다.
2. 이 객체 인스턴스가 필요하면 오직 getInstance() 메서드를 통해서만 조회할 수 있다. 이 메서드를 호 출하면 항상 같은 인스턴스를 반환한다.
3. 딱 1개의 객체 인스턴스만 존재해야 하므로, 생성자를 private으로 막아서 혹시라도 외부에서 new 키워 드로 객체 인스턴스가 생성되는 것을 막는다.

**테스트 코드**

![image-20210202144957338](./images/image-20210202144957338.png)

싱글톤 패턴을 적용하면 고객의 요청이 올 때 마다 객체를 생성하는 것이 아니라, 이미 만들어진 객체를 공유 해서 효율적으로 사용할 수 있다. 하지만 싱글톤 패턴은 다음과 같은 수 많은 문제점들을 가지고 있다.

싱글톤 패턴 문제점

* 싱글톤 패턴을 구현하는 코드 자체가 많이 들어간다.
* 의존관계상 클라이언트가 구체 클래스에 의존한다. DIP를 위반한다. 
* 클라이언트가 구체 클래스에 의존해서 OCP 원칙을 위반할 가능성이 높다. 테스트하기 어렵다.
* 내부 속성을 변경하거나 초기화 하기 어렵다.
* private 생성자로 자식 클래스를 만들기 어렵다.
* 결론적으로 유연성이 떨어진다.
* 안티패턴으로 불리기도 한다.



### 싱글톤 컨테이너

* 스프링 컨테이너는 싱글톤 패턴의 문제점을 해결하면서, 객체 인스턴스를 싱글톤(1개만 생성)으로 관리한다.
* 스프링 컨테이너는 싱글톤 컨테이너 역할을 한다. 이렇게 싱글톤 객체를 생성하고 관리하는 기능을 `싱글톤 레지스트리`라 한다.
* DIP, OCP, 테스트, private 생성자로 부터 자유롭게 싱글톤을 사용할 수 있다.
* 스프링 컨테이너 덕분에 고객의 요청이 올 때 마다 객체를 생성하는 것이 아니라, 이미 만들어진 객체를 공유 해서 효율적으로 재사용할 수 있다.
* 스프링의 기본 빈 등록 방식은 싱글톤이지만, 싱글톤 방식만 지원하는 것은 아니다. 요청할 때 마다 새 로운 객체를 생성해서 반환하는 기능도 제공한다.

**테스트 코드**

* 호출할 때 마다 같은 객체를 반환하는지 확인해보자

```java
@Test
@DisplayName("스프링 컨테이너와 싱글톤")
void springContainer(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberService memberService1 = applicationContext.getBean("memberService", MemberService.class);
        MemberService memberService2 = applicationContext.getBean("memberService", MemberService.class);

        //참조값이 같은 것을 확인
        assertThat(memberService1).isSameAs(memberService2);

}
```



### 싱글톤 방식의 주의점

* 여러 클라이언트가 하나의 같은 객체 인스턴스를 공유하기 때문에 싱글톤 객체는 상태를 유지하게 설계하면 안된다.
* stateless하게 설계해야 한다.
  * 특정 클라이언트에 의존적인 필드가 있으면 안된다.

  * 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다.
  * 가급적 읽기만 가능해야 한다.
  * 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다.

**테스트**

* `StatefulService` 와 `StatefulServiceTest` 작성
* stateful 한 싱글톤 객체를 사용할 때 발생하는 문제점
* `StatefulService` 의 `price` 필드는 공유되는 필드인데, 특정 클라이언트가 값을 변경한다.

```java
public class StatefulService {

    private int price;

    public void order(String name, int price) {
        System.out.println("name : " + name + " price : " + price);
        this.price = price;
    }

    public int getPrice() {
        return price;
    }
}

```

```java
class StatefulServiceTest {

    @Test
    @DisplayName("stateful 싱글톤")
    void statefulServiceSingleton(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestConfig.class);
        StatefulService statefulService = applicationContext.getBean("statefulService", StatefulService.class);

        // member1 주문
        statefulService.order("member1", 10000);
        // member2 주문
        statefulService.order("member2", 20000);

        // member1 주문 금액 조회 10000원을 예상하지만 20000원이 조회됨
        int price = statefulService.getPrice();
        Assertions.assertThat(price).isEqualTo(20000);
    }

    static class TestConfig{
        @Bean
        public StatefulService statefulService(){
            return new StatefulService();
        }
    }
}
```



### @Configuration과 싱글톤

```java
@Configuration
public class AppConfig {

    @Bean
    public MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy() {
        return new RateDiscountPolicy();
    }

    @Bean
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl(discountPolicy(), memberRepository());
    }

}
```

* memberService 빈을 만드는 코드를 보면 memberRepository() 를 호출한다. 
  * 이 메서드를 호출하면 new MemoryMemberRepository() 를 호출한다. 
* orderService 빈을 만드는 코드도 동일하게 memberRepository() 를 호출한다. 
  * 이 메서드를 호출하면 new MemoryMemberRepository() 를 호출한다.
* new MemoryMemberRepository() 가 2번 호출되면서 싱글톤이 지켜지지 않는것 같다 테스트를 해보자



**테스트**

```java
@Test
void singleton(){
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

        MemberServiceImpl memberService = applicationContext.getBean("memberService", MemberServiceImpl.class);
        OrderServiceImpl orderService = applicationContext.getBean("orderService", OrderServiceImpl.class);
        MemberRepository memberRepository = applicationContext.getBean("memberRepository", MemberRepository.class);

        assertThat(memberService.getMemberRepository()).isSameAs(memberRepository);
        assertThat(orderService.getMemberRepository()).isSameAs(memberRepository);

}
```

* 테스트 결과 같은 객체를 참조하고 있다. 
  * 즉 싱글톤이 지켜졌다. 어떻게 해결한 것일까?





