package ua.patronum.quicklink.mvc.home;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.patronum.quicklink.restapi.url.CreateUrlRequest;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mvc/home")
public class MvcHomeController {

    private final MvcHomeService service;

    @GetMapping()
    public String showHomePage(Model model) {
        return service.showHomePage(model);
    }

    @GetMapping("/active")
    public String showAllActiveUrl(Model model) {
        return service.showAllActiveUrl(model);
    }

    @GetMapping("/user/list")
    public String showAllUserURL(Model model) {
        return service.showAllUserURL(model);
    }

    @GetMapping("/user/list/active")
    public String showAllUserActiveURL(Model model) {
        return service.showAllUserActiveURL(model);
    }

    @PostMapping("/create")
    public String create(CreateUrlRequest request, Model model) {
        return service.create(request, model);
    }

    @PostMapping("/user/list/delete/{id}")
    public String delete(@PathVariable Long id) {
        return service.delete(id);
    }

    @PostMapping("/redirect/{id}")
    public String redirect(@PathVariable Long id) {
        return service.redirect(id);
    }

    @GetMapping("/redirect/**")
    public String getRedirect(Model model){
        return service.getRedirect(model);
    }

    @PostMapping("/user/add/{id}")
    public String addThirtyDays(@PathVariable Long id){
        return service.getExtensionTime(id);
    }
}