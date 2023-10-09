package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TransactionService {

    static final String EMP_URL_PREFIX = "https://blockstream.info/api/block/000000000000000000076c036ff5119e5a5a74df77abf64203473364509f7732/txs/";

    private RestTemplate restTemplate = new RestTemplate();

    public void getEmployee(String id) throws JsonProcessingException {
        Map<String, Set<String>> map = new HashMap<>();

        for (int j = 0; j < 2875; j = j + 25) {

            ResponseEntity<String> resp = restTemplate.getForEntity(EMP_URL_PREFIX + j, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(resp.getBody());

            for (int i = 0; i < 25; i++) {
                JsonNode name = root.get(i);

                Set<String> set = map.getOrDefault(name.get("txid").toString(), new HashSet<>());
                set.add(name.get("vin").get(0).get("txid").toString());
                map.put(name.get("txid").toString(), set);
            }
        }

        Map<Integer, Set<String>> finalMap = new TreeMap<>(Comparator.reverseOrder());
        Map<String, Integer> tmpMap = new HashMap<>();

        map.forEach((s, strings) -> {
            recursiveCall(s, map, finalMap, tmpMap);
        });

        finalMap.forEach( (integer, strings) -> {
            strings.forEach(s -> {
                System.out.println("TxnID: " + s + " -> Ancestors: " + integer);
            });
        });
    }

    int recursiveCall(String id, Map<String, Set<String>> map, Map<Integer, Set<String>> finalMap, Map<String, Integer> tmpMap) {
        if (!map.containsKey(id)) {
            Set<String> set = finalMap.getOrDefault(0, new HashSet<>());
            set.add(id);
            finalMap.put(0, set);
            tmpMap.put(id, 0);
            return 0;
        } else if (map.get(id).isEmpty()) {
            Set<String> set = finalMap.getOrDefault(1, new HashSet<>());
            set.add(id);
            finalMap.put(1, set);
            tmpMap.put(id, 1);
            return 1;
        } else {
            AtomicInteger totalCount = new AtomicInteger(1);
            map.get(id).forEach(s -> {
                if (!tmpMap.containsKey(s))
                    totalCount.addAndGet(recursiveCall(s, map, finalMap, tmpMap));
                else
                    totalCount.addAndGet(tmpMap.get(s));
            });
            Set<String> set = finalMap.getOrDefault(totalCount.get(), new HashSet<>());
            set.add(id);
            finalMap.put(totalCount.get(), set);
            tmpMap.put(id, totalCount.get());
            return totalCount.get();
        }
    }
}
