package com.herringbone.stock.controller;

import com.herringbone.stock.service.BucketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/buckets")
public class BucketController {

    private final BucketService bucketService;

    public BucketController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @GetMapping(value="/{symbol}")
    public @ResponseBody
    Map getBuckets(@PathVariable("symbol") String symbol) {
        return bucketService.getCurrentBuckets(symbol);
    }
}
