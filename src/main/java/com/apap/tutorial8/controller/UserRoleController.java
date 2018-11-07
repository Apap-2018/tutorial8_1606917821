package com.apap.tutorial8.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.apap.tutorial8.model.PasswordModel;
import com.apap.tutorial8.model.UserRoleModel;
import com.apap.tutorial8.service.UserRoleService;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;
	
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	private String addUserSubmit(@ModelAttribute UserRoleModel user, RedirectAttributes redirectAttributes) {
		String output = "";
		if (user.getPassword().matches("(?=.*[0-9])(?=.*[a-zA-Z]).{8,}")) {
			userService.addUser(user);
			output = "User berhasil ditambahkan!";
		}
		else {
			output = "Error! password minimal 8 karakter, mengandung angka dan huruf";
		}
		redirectAttributes.addFlashAttribute("output", output);
		return ("redirect:/");
	}
	
	
	@RequestMapping(value = "/updatePassword", method = RequestMethod.GET)
	private String updatePass() {
		return "updatePassword";
		
	}
	
	@RequestMapping (value = "/submitPassword", method = RequestMethod.POST)
	public String updatePassSubmit (@ModelAttribute PasswordModel password, RedirectAttributes redirectAttributes) {
		String output = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserRoleModel user = userService.getUserByUsername(auth.getName());
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if (password.getKonfirmasiPass().equals(password.getPasswordBaru())) {
			if (passwordEncoder.matches(password.getPasswordLama(), user.getPassword())) {
				if(password.getPasswordBaru().matches("(?=.*[0-9])(?=.*[a-zA-Z]).{8,}")) {
					userService.updatePasswordUser(user, password.getPasswordBaru());
					output = "Password berhasil diupdate";
				}
				else {
					output = "Error! Password minimal 8 karakter, mengandung angka dan huruf";
				}
			}
			else {
				output = "Error! Password lama salah";
			}
		}
		else {
			output = "Error! Password tidak sesuai.";
		}
		redirectAttributes.addFlashAttribute("output",output);
		return ("redirect:/user/updatePassword");			
	}

}
