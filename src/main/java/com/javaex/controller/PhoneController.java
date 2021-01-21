package com.javaex.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.javaex.dao.PhoneDao;
import com.javaex.vo.PhoneVo;

//***controller임을 명시하기(어노테이션)***
//컨트롤러는 주소임. 메소드 위에 주소인걸 @문법으로 붙임.
@Controller
@RequestMapping(value="/phone")
public class PhoneController {

	// 필드 : 사람 들어와서 실행할 때마다 new하면 dao가 필요없을 때도 올리니까 메모리공간을 많이 차지하고 
	// (시간이 지나면 알아서 사라지긴 하지만) 많은 사용자를 관리하기 힘듦.
	// 여기서 new 안 하고 필요하다고 표시만 함. 이러면 디스패쳐서블렛이 알아서 new해서 주소 넣어줌 --> 새로운 dao를 만드는 게 아니라 기존 걸 돌려씀.
	// 제어역전 : dao new하고 말고가 이거 역할이었는데 이제 디스패쳐서블렛이 하게 됨.
	// private PhoneDao pDao; --> phonebook4
	
	// 생성자
	// 메소드 겟셋
	/* 메소드 일반 (기능 1개씩 --> 기능마다 url 부여) */
	
	//리스트
	@RequestMapping(value="/list", method= {RequestMethod.GET, RequestMethod.POST})
	public String list(Model model) {
		System.out.println("list");
		
		//dao를 통해 리스트 가져옴
		PhoneDao phoneDao = new PhoneDao();
		List<PhoneVo> phoneList = phoneDao.getList();
		System.out.println(phoneList.toString());
		
		//*****view는 포워드시키려고 return시키고 model(데이터)은 attribute해서 넘김.
		//model --> data를 보내는 법 --> 담아 놓으면 됨 
		model.addAttribute("pList", phoneList); //"이름", 실제데이터(주소값)
		
		return "list"; //view resolver로 주소 단축
	}
	
	//등록폼
	@RequestMapping(value="/writeForm", method= {RequestMethod.GET, RequestMethod.POST})
	public String writeForm() {
		System.out.println("writeForm");
		
		return "/WEB-INF/views/writeForm.jsp";
	}
	
	//등록 (각 파라미터 꺼내기)
	@RequestMapping(value="/write", method= {RequestMethod.GET, RequestMethod.POST})
	//(("파라미터") 쓸 이름)
	public String write(@RequestParam("name") String name, @RequestParam("hp") String hp, @RequestParam("company") String company) { 
		System.out.println("write");
		
		PhoneVo pVo = new PhoneVo(name, hp, company);
		System.out.println(pVo.toString());
		
		PhoneDao pDao = new PhoneDao();
		pDao.phoneInsert(pVo);
		
		//리다이렉트 (view로 전달하고 ""안에는 url 씀)
		return "redirect:/phone/list";
	}
	
	//삭제 delete --> @RequestMapping 약식
	@RequestMapping(value="/delete2", method= {RequestMethod.GET, RequestMethod.POST})
	public String delete2(@RequestParam("personId") int id) {
		System.out.println("delete2");
		
		PhoneDao pDao = new PhoneDao();
		pDao.phoneDelete(id);
		
		return "redirect:/phone/list";
	}
	
	//삭제 delete --> @PathVariable
	//삭제 누르면 파라미터가 personId=n으로 가는데 그냥 가변적인 숫자 자체를 personId로 쓰고 싶은 거. 주소에서 찾아 씀.
	//숫자 하나정도 넘길 때 쓰는 게 좋음. 파라미터가 많으면 파라미터명도 같이 넘기는 게 명확함. 잘 쓰면 url이 깔끔해짐.
	//{담을 변수명} --> 고정값이 아니라는 뜻
	@RequestMapping(value="/delete/{personId}", method= {RequestMethod.GET, RequestMethod.POST})
	public String delete(@PathVariable("personId") int id) {
		System.out.println("delete");
		
		PhoneDao pDao = new PhoneDao();
		pDao.phoneDelete(id);
		
		return "redirect:/phone/list";
	}
	
	//수정폼 modifyForm
	@RequestMapping(value="/modifyForm", method= {RequestMethod.GET, RequestMethod.POST})
	public String modifyForm(@RequestParam("personId") int id, Model model) { 
		System.out.println("modifyForm");
		
		PhoneDao pDao = new PhoneDao();
		PhoneVo pVo = pDao.getPerson(id);
		
		//파라미터가 있어도 모델을 위처럼 파라미터랑 같이 쓰는데 순서 상관없음. 
		//id를 찾을 수 없다고 오류났는데 vo랑 dao에서는 personId로 쓰던 걸 수정폼.jsp에서는 id로 써놔서 그런 거였음.
		model.addAttribute("pVo", pVo);
		
		return "/WEB-INF/views/modifyForm.jsp";
		
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
	//디스패쳐서블렛에서 PhoneVo를 기본생성자로 new 한 후에 setter로 각 필드값을 넣음.
	@RequestMapping(value="/modify", method= {RequestMethod.GET, RequestMethod.POST})
	public String modify(@ModelAttribute PhoneVo pVo) {
		System.out.println("modify");
		
		//System.out.println(pVo.toString());
		//처음에 personId가 0으로 나와서 수정이 안 먹혔는데 기본생성자는 파라미터이름으로 setter를 찾기 때문에
		//파라미터명(id)과 필드명(personId)이 안 맞아서 값을 안 넣게 됨. idSetter로 찾는데 vo에는 이게 없는 거.
		//그래서 이름 바꿔서 맞춰야 됨. 나는 파라미터명을 personId로 바꿈.
		
		PhoneDao pDao = new PhoneDao();
		pDao.phoneUpdate(pVo);
		
		return "redirect:/phone/list";
	}
	
}
