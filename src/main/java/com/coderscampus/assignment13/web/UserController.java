package com.coderscampus.assignment13.web;

import java.util.Arrays;
import java.util.Set;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String home() {
		System.out.println("Home controller method called");
		return "index";
	}

	@GetMapping("/register")
	public String getCreateUser(ModelMap model) {

		model.put("user", new User());

		return "register";
	}

	@PostMapping("/register")
    public String postCreateUser(@ModelAttribute("user") User user, BindingResult result, ModelMap model) {
    List<User> existingUsers = userService.findByUsername(user.getUsername());
    if (!existingUsers.isEmpty()) {
        // Adds an error message to the model if the username already exists
        model.addAttribute("errorMessage", "Username already exists. Please choose a different one.");
        return "register"; // Return back to the registration page
    }
    userService.saveUser(user);
    return "redirect:/users";
}


	@GetMapping("/users")
	public String getAllUsers(ModelMap model) {
		Set<User> users = userService.findAll();

		model.put("users", users);
		if (users.size() != 1) {
			model.put("user", users.iterator().next());
		}

		return "users";
	}

	@GetMapping("/user_details/{userId}")
	public String getOneUser(ModelMap model, @PathVariable Long userId) {
		User user = userService.findById(userId);
		if (user == null) {
			return "redirect:/users";
		}
		model.put("users", Arrays.asList(user));
		model.put("user", user);
		boolean displayUpdateForm = false;
		model.addAttribute("displayUpdateForm", displayUpdateForm);
		return "user_details";
	}

	@PostMapping("/user_details/{userId}/update")
	public String updateUserDetailsAndAddress(@PathVariable Long userId, @ModelAttribute User user, BindingResult result,
			ModelMap model) {
		if (result.hasErrors()) {
			// Handle errors when validation messages are added to the BindingResult
			return "user_details"; 
		}
		// Optional: Fetch the existing user to merge updates if necessary
		User existingUser = userService.findById(userId);
		if (existingUser == null) {
			// Handle case where user does not exist
			return "redirect:/users";
		}

		userService.updateUserAndAddress(user); 
		return "redirect:/user_details/" + userId;
	}	


	@GetMapping("/update_user/{userId}")
	public String showUpdateForm(@PathVariable Long userId, ModelMap model) {
		User user = userService.findById(userId);
		if (user == null) {
			return "redirect:/users";
		}
		model.put("user", user);
		model.addAttribute("displayUpdateForm", true);
		return "user_details";
	}

	@PostMapping("/user_details/{userId}")
	public String postOneUser(@ModelAttribute User user) {
		userService.saveUser(user);
		return "redirect:/users/" + user.getUserId();
	}

	@PostMapping("/users_details/{userId}/delete")
	public String deleteOneUser(@PathVariable Long userId) {
		userService.delete(userId);
		return "redirect:/users";
	}
}

