package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Integer>{
    Member findByEmailAndPassword(String email, String password);
}
