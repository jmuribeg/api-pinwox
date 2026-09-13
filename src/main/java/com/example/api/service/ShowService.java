package com.example.api.service;

import com.example.api.dto.CreateShowRequest;
import com.example.api.model.Show;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ShowService {

    private final Map<Long, Show> store = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    public ShowService() {
        create(new CreateShowRequest("Breaking Bad", "Drama"));
        create(new CreateShowRequest("Dark", "Sci-Fi"));
    }

    public List<Show> findAll() {
        return new ArrayList<>(store.values());
    }

    public Show findById(Long id) {
        Show show = store.get(id);
        if (show == null) {
            throw new IllegalArgumentException("show not found");
        }
        return show;
    }

    public Show create(CreateShowRequest request) {
        long id = sequence.incrementAndGet();
        Show show = new Show(id, request.name().trim(), request.genre().trim());
        store.put(id, show);
        return show;
    }
}
