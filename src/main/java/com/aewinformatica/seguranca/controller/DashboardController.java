package com.aewinformatica.seguranca.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

	@GetMapping("/")
	public ModelAndView home(){
		
		ModelAndView mv = new ModelAndView("Dashboard");
//		ModelAndView mv = new ModelAndView("layout/LayoutPadrao");
		return mv;
	}
}
