package dev.lrxh.neptune.kit;

import lombok.Getter;

@SuppressWarnings("unused")
@Getter
public enum Rules {
    BUILD("Allow/Deny Players to place blocks.", "Build") {
        @Override
        public void execute(Kit kit, boolean value) {
            kit.setBuild(value);
        }

        @Override
        public boolean enabled(Kit kit) {
            return kit.isBuild();
        }
    },
    HUNGER("Allow/Deny Players losing Hunger", "Hunger") {
        @Override
        public void execute(Kit kit, boolean value) {
            kit.setHunger(value);
        }

        @Override
        public boolean enabled(Kit kit) {
            return kit.isHunger();
        }
    },
    SUMO("Allow/Deny Sumo", "Sumo") {
        @Override
        public void execute(Kit kit, boolean value) {
            kit.setSumo(value);
        }

        @Override
        public boolean enabled(Kit kit) {
            return kit.isSumo();
        }
    },
    FALL_DAMAGE("Allow/Deny Player to take Fall Damage", "Fall Damage") {
        @Override
        public void execute(Kit kit, boolean value) {
            kit.setFallDamage(value);
        }

        @Override
        public boolean enabled(Kit kit) {
            return kit.isFallDamage();
        }
    },
    DENY_MOVEMENT("Allow/Deny Movement on match start", "Deny Movement") {
        @Override
        public void execute(Kit kit, boolean value) {
            kit.setDenyMovement(value);
        }

        @Override
        public boolean enabled(Kit kit) {
            return kit.isDenyMovement();
        }
    },
    BOXING("Allow/Deny Boxing", "Boxing") {
        @Override
        public void execute(Kit kit, boolean value) {
            kit.setBoxing(value);
        }

        @Override
        public boolean enabled(Kit kit) {
            return kit.isBoxing();
        }
    },
    ALLOW_ARENA_BREAK("Allow Players to break blocks from the arena", "Arena Break") {
        @Override
        public void execute(Kit kit, boolean value) {
            kit.setArenaBreak(value);
        }

        @Override
        public boolean enabled(Kit kit) {
            return kit.isArenaBreak();
        }
    },
    DAMAGE("Allow/Deny Players to take Damage", "Damage") {
        @Override
        public void execute(Kit kit, boolean value) {
            kit.setDamage(value);
        }

        @Override
        public boolean enabled(Kit kit) {
            return kit.isDamage();
        }
    };

    private final String description;
    private final String name;

    Rules(String description, String name) {
        this.description = description;
        this.name = name;
    }

    public abstract void execute(Kit kit, boolean value);

    public abstract boolean enabled(Kit kit);
}
