package com.sk89q.worldguard.util.profile.resolver;

import com.sk89q.worldguard.util.profile.Profile;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;

import com.sk89q.worldguard.util.profile.resolver.SingleRequestService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

public class SpongePlayerService extends SingleRequestService {
    private static final SpongePlayerService INSTANCE = new SpongePlayerService();
    private UserStorageService uss;
    private SpongePlayerService() {
        uss = Sponge.getServiceManager().provide(UserStorageService.class).get();
    }

    public int getIdealRequestLimit() {
        return 2147483647;
    }

    @Nullable
    public Profile findByName(String name) throws IOException, InterruptedException {
        Optional<User> pU =uss.get(name);
        return pU.map(user -> new Profile(user.getUniqueId(), user.getName())).orElse(null);
    }

    public static SpongePlayerService getInstance() {
        return INSTANCE;
    }
}

