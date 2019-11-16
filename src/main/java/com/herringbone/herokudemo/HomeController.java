package com.herringbone.herokudemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    RecordRepository recordRepository;

    @GetMapping
    public String home(ModelMap model) {
        List<Record> records = recordRepository.findAll();
        model.addAttribute("records", records);
        model.addAttribute("insertRecord", new Record());
        return "home";
    }

    @PostMapping
    public String insertData(ModelMap model ,
                   @ModelAttribute("insertRecord") @Valid  Record record,
                   BindingResult result) {
        if (!result.hasErrors()) {
            recordRepository.save(record);
        }
        return home(model);
    }
}
