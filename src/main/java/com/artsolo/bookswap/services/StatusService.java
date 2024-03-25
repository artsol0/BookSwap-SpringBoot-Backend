package com.artsolo.bookswap.services;

import com.artsolo.bookswap.models.Status;
import com.artsolo.bookswap.repositoryes.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public boolean addNewStatus(String status) {
        Status newStatus = new Status();
        newStatus.setStatus(status);
        newStatus = statusRepository.save(newStatus);
        return statusRepository.existsById(newStatus.getId());
    }

    public boolean deleteStatusById(Long id) {
        Optional<Status> status = statusRepository.findById(id);
        if (status.isPresent()) {
            statusRepository.deleteById(status.get().getId());
            return !statusRepository.existsById(status.get().getId());
        }
        return false;
    }

    public Status getStatusById(Long id) {
        return statusRepository.findById(id).orElse(null);
    }

    public List<Status> getAllStatuses() {return statusRepository.findAll();}
}
