package com.javaex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.javaex.dao.PhoneDao;
import com.javaex.vo.PhoneVo;

@Controller
@RequestMapping(value="/phone")
public class PhoneController {

	// 필드 : 사람 들어와서 실행할 때마다 new하면 dao가 필요없을 때도 올리니까 메모리공간을 많이 차지하고 
	// (시간이 지나면 알아서 사라지긴 하지만) 많은 사용자를 관리하기 힘듦.
	// 여기서 new 안 하고 필요하다고 표시만 함. 이러면 디스패쳐서블렛이 알아서 new해서 주소 넣어줌 --> 새로운 dao를 만드는 게 아니라 기존 걸 돌려씀.
	// 제어역전 : dao new하고 말고가 이거 역할이었는데 이제 디스패쳐서블렛이 하게 됨.
	// private PhoneDao pDao; --> phonebook4
	
	//DB와 dao 간 커넥션을 매번 만드는 데는 리소스가 너무 많이 듦. 
	//그래서 커넥션 몇개 미리 만들어서 커넥션풀로 관리하면서 갖고 있는 커넥션으로 돌려 쓰게 만들어줌 --> data source 라이브러리 추가
	
	//필드 --> @Authwired(자동으로 new하는 관리대상에 들어가게 함) + (이 dao를 관리하도록 함) --> dao 새로 선언X
	@Autowired
	private PhoneDao phoneDao;
	
	// 생성자
	// 메소드 겟셋
	/* 메소드 일반 (기능 1개씩 --> 기능마다 url 부여) */
	
	//리스트
	@RequestMapping(value="/list", method= {RequestMethod.GET, RequestMethod.POST})
	public String list(Model model) {
		System.out.println("list");
		
		//dao를 통해 리스트 가져옴
		List<PhoneVo> phoneList = phoneDao.getList();
		System.out.println(phoneList.toString());
		
		model.addAttribute("pList", phoneList); 
		
		return "list"; 
	}
	
	//등록폼
	@RequestMapping(value="/writeForm", method= {RequestMethod.GET, RequestMethod.POST})
	public String writeForm() {
		System.out.println("writeForm");
		
		return "writeForm";
	}
	
	//등록 (각 파라미터 꺼내기)
	@RequestMapping(value="/write", method= {RequestMethod.GET, RequestMethod.POST})
	public String write(@RequestParam("name") String name, @RequestParam("hp") String hp, @RequestParam("company") String company) { 
		System.out.println("write");
		
		PhoneVo pVo = new PhoneVo(name, hp, company);
		System.out.println(pVo.toString());
		
		phoneDao.phoneInsert(pVo);
		
		return "redirect:/phone/list";
	}
	
	//삭제 delete --> @RequestMapping 약식
	@RequestMapping(value="/delete2", method= {RequestMethod.GET, RequestMethod.POST})
	public String delete2(@RequestParam("personId") int id) {
		System.out.println("delete2");
		
		phoneDao.phoneDelete(id);
		
		return "redirect:/phone/list";
	}
	
	//삭제 delete --> @PathVariable
	@RequestMapping(value="/delete/{personId}", method= {RequestMethod.GET, RequestMethod.POST})
	public String delete(@PathVariable("personId") int id) {
		System.out.println("delete");
		
		phoneDao.phoneDelete(id);
		
		return "redirect:/phone/list";
	}
	
	//수정폼 modifyForm
	@RequestMapping(value="/modifyForm", method= {RequestMethod.GET, RequestMethod.POST})
	public String modifyForm(@RequestParam("personId") int id, Model model) { 
		System.out.println("modifyForm");
		
		PhoneVo pVo = phoneDao.getPerson(id);
		
		model.addAttribute("pVo", pVo);
		
		return "modifyForm";
		
		/* 해설
		먼저 html 가져오고 
		html + 정보 --> DB 접근
		*/
		
	}
	
	/*
	//수정 modify
	@RequestMapping(value="/modify", method= {RequestMethod.GET, RequestMethod.POST})
	public String modify(@RequestParam("id") int id, 
				@RequestParam("name") String name, 
				@RequestParam("hp") String hp, 
				@RequestParam("company") String company) {
		System.out.println("modify");
		
		PhoneVo pVo = new PhoneVo(id, name, hp, company);
		PhoneDao pDao = new PhoneDao();
		pDao.phoneUpdate(pVo);
		
		return "redirect:/phone/list";
	}
	*/
	
	//수정 modify --> 자동으로 파라미터 다 받아서 vo에 넣게 하기 --> @ModelAttribute(이거 생략하고 PHoneVo pVo만 써도 됨)
	@RequestMapping(value="/modify", method= {RequestMethod.GET, RequestMethod.POST})
	public String modify(@ModelAttribute PhoneVo pVo) {
		System.out.println("modify");
		
		phoneDao.phoneUpdate(pVo);
		
		return "redirect:/phone/list";
	}
	
}
