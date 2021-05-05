package study.querydsl.entity;


import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydlBaicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10, teamA);
        Member member2 = new Member("member2",20, teamA);

        Member member3 = new Member("member3",30, teamB);
        Member member4 = new Member("member4",40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startJPQL(){

        // memberr1을 찾아라
        String qlString =
                "select m from Member m " +
                "where m.username =:username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl(){
        // 별칭을 사용 ( 같은 테이블을 참고할때만 사용 권장 )
        // QMember m = new QMember("m");
        
        /*
        QMember m = QMember.member;
        Member findMember = queryFactory.select(m)
                .from(m)
                .where(m.username.eq("member1")) // 파라미터 바인딩 처리
                .fetchOne();
        */

        /* statc import (권장) */
        Member findMember = queryFactory.select(member)
                .from(member)
                .where(member.username.eq("member1")) // 파라미터 바인딩 처리
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void searchAndParam() {
        Member findMember = queryFactory
                .selectFrom(member)
                .where(
                        member.username.eq("member1")
                        , member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");

    }

    @Test
    public void resultFetch(){
/*
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        Member fetchOne = queryFactory
                .select(member)
                .fetchOne();

        Member fetchFirst = queryFactory
                .selectFrom(QMember.member)
                .fetchFirst();
*/

        // 페징
/*
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        results.getTotal();
        List<Member> content = results.getResults();
*/

        long total = queryFactory
                .selectFrom(member)
                .fetchCount();


    }
}
