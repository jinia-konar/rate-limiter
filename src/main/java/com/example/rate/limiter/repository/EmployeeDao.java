package com.example.rate.limiter.repository;

import com.example.rate.limiter.entity.EmployeeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class EmployeeDao {

  @Autowired
  @Qualifier(value = "rate-limiter")
  RedisTemplate<String, String> redisTemplate;

  @Autowired private EmployeeRepository repository;

  @SuppressWarnings("unchecked")
  public Set<String> rateLimiter(String id) {
    String key = "rate-" + id;
    long currentTime = System.currentTimeMillis();
    long slidingWindowTime = 60000L; //1 min

    List<Object> valOuter =  redisTemplate.execute(new SessionCallback<List<Object>>() {
      @Override
      public List<Object> execute(@NotNull RedisOperations operations) throws DataAccessException {
        operations.multi();
        operations.opsForZSet().removeRangeByScore(key, 0, currentTime - slidingWindowTime);
        operations.opsForZSet().add(key, Long.toString(currentTime), currentTime);
        operations.expire(key, currentTime + slidingWindowTime, TimeUnit.MICROSECONDS);
        redisTemplate.opsForZSet().range(key, 0, -1);
        return operations.exec();
      }
    });
    log.info("EmployeeDao: rateLimiter: {}", valOuter);

    return valOuter != null ? (Set<String>) valOuter.get(valOuter.size()-1) : Collections.emptySet() ;
  }

  public EmployeeEntity saveAndFlush(EmployeeEntity entity) {
    return repository.saveAndFlush(entity);
  }

  public Optional<EmployeeEntity> fetchEmployee(Long id) {
    return repository.findById(id);
  }
}


