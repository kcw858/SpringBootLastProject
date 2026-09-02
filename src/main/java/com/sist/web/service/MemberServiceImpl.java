package com.sist.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sist.web.mapper.AuthorityMapper;
import com.sist.web.mapper.MemberMapper;
import com.sist.web.vo.AuthorityVO;
import com.sist.web.vo.MemberVO;

import lombok.RequiredArgsConstructor;
/*
 *  User ---- Controller ------ Service ------ Repository ------- DB
 *  	  |
 *  	Security
 *  
 *  => 의존성이 낮은 프로그램
 *     ---- 결합성
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
	private final MemberMapper mMapper;
	private final AuthorityMapper aMapper;
	
	@Override
	public MemberVO findByUsername(String username) {

		return mMapper.findByUsername(username);
	}
	@Override
	public List<AuthorityVO> getAuthorityData(int member_id) {

		return aMapper.getAuthorityData(member_id);
	}
}
