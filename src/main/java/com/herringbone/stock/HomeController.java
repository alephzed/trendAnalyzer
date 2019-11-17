package com.herringbone.stock;

import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.repository.TickerRepository;
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
    TickerRepository recordRepository;

    @GetMapping
    public String home(ModelMap model) {
        List<Ticker> tickers = recordRepository.findAll();
        model.addAttribute("records", tickers);
        model.addAttribute("insertRecord", new Ticker());
        return "home";
    }

    @PostMapping
    public String insertData(ModelMap model ,
                   @ModelAttribute("insertRecord") @Valid Ticker ticker,
                   BindingResult result) {
        if (!result.hasErrors()) {
            recordRepository.save(ticker);
        }
        return home(model);
    }
}
