package com.mldtech.nilapp.api.users.children.UserStreak.service;

import com.mldtech.nilapp.api.users.children.UserStreak.repository.UserStreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStreakService {
    private final UserStreakRepository userStreakRepository;



}
