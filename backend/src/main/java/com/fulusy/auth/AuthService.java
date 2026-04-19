package com.fulusy.auth;

import com.fulusy.common.exception.BadRequestException;
import com.fulusy.common.exception.UnauthorizedException;
import com.fulusy.common.security.JwtService;
import com.fulusy.common.security.PasswordService;
import com.fulusy.user.GamificationState;
import com.fulusy.user.PenaltyState;
import com.fulusy.user.User;
import com.fulusy.user.UserSettings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthService {

    @Inject PasswordService passwordService;
    @Inject JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (User.findByEmail(req.email()) != null) {
            throw new BadRequestException("Email already registered");
        }

        if ("fixed".equals(req.incomeMode()) || "hybrid".equals(req.incomeMode())) {
            if (req.fixedIncomeAmount() == null || req.fixedIncomeDay() == null) {
                throw new BadRequestException("Fixed income amount and day required for fixed/hybrid modes");
            }
        }

        User user = new User();
        user.email = req.email();
        user.passwordHash = passwordService.hash(req.password());
        user.name = req.name();
        user.startingBalance = req.startingBalance();
        user.incomeMode = req.incomeMode();
        user.fixedIncomeAmount = req.fixedIncomeAmount();
        user.fixedIncomeDay = req.fixedIncomeDay();
        user.language = req.language() != null ? req.language() : "ar";
        user.persist();

        UserSettings settings = new UserSettings();
        settings.userId = user.id;
        settings.persist();

        GamificationState gamification = new GamificationState();
        gamification.userId = user.id;
        gamification.persist();

        PenaltyState penalty = new PenaltyState();
        penalty.userId = user.id;
        penalty.persist();

        String token = jwtService.issueToken(user.id, user.email);
        return new AuthResponse(token, user.id, user.email, user.name, user.language);
    }

    public AuthResponse login(LoginRequest req) {
        User user = User.findByEmail(req.email());
        if (user == null || !passwordService.verify(req.password(), user.passwordHash)) {
            throw new UnauthorizedException("Invalid email or password");
        }
        String token = jwtService.issueToken(user.id, user.email);
        return new AuthResponse(token, user.id, user.email, user.name, user.language);
    }
}
