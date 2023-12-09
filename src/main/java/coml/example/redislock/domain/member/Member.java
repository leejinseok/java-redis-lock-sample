package coml.example.redislock.domain.member;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nickname;

    public static Member of(String nickname) {
        Member member = new Member();
        member.nickname = nickname;
        return member;
    }

}
