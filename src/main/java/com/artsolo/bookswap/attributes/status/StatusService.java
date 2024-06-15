package com.artsolo.bookswap.attributes.status;

import com.artsolo.bookswap.exceptions.NoDataFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public Status addNewStatus(String status) {
        Status newStatus = Status.builder().status(status).build();
        return statusRepository.save(newStatus);
    }

    public boolean deleteStatus(Status status) {
        statusRepository.deleteById(status.getId());
        return !statusRepository.existsById(status.getId());
    }

    public Status getStatusById(Long id) {
        return statusRepository.findById(id).orElseThrow(() -> new NoDataFoundException("Status", id));
    }

    public List<Status> getAllStatuses() {return statusRepository.findAll();}
}
