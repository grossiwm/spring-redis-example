package com.springredis.springredis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductService {

    static final private String LIST_NAME = "products";

    final ObjectMapper objectMapper=new ObjectMapper();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Jedis jedis;

    @Transactional
    public void addProduct(Product product) throws JsonProcessingException {
        productRepository.save(product);
        String productString = objectMapper.writeValueAsString(product);
        jedis.lpush(LIST_NAME, productString);
    }

    public List<Product> listProducts() {
        return jedis.lrange(LIST_NAME, 0, jedis.llen(LIST_NAME) - 1)
                .stream().map(ps -> {
                    try {
                        return objectMapper.readValue(ps, Product.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
    }

}
