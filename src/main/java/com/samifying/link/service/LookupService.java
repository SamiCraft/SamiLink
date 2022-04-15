package com.samifying.link.service;

import com.samifying.link.AppConstants;
import com.samifying.link.model.AccountModel;
import com.samifying.link.model.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
public class LookupService {

    private final DataService dataService;
    private final MojangService mojangService;

    public UserModel findUserByMinecraftUsername(String username) {
        AccountModel account;
        try {
            account = mojangService.getAccountByUsername(username);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return dataService.getUserByUUID(account.getId(), 426156903555399680L, Long.valueOf(AppConstants.GUILD_ID));
    }
}
