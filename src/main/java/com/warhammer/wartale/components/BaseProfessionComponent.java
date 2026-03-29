package com.warhammer.wartale.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.professions.KillProfessionComponent;
import com.warhammer.wartale.dto.LevelTable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class BaseProfessionComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, KillProfessionComponent> TYPE;
    protected int experience;

    public static ComponentType<EntityStore, KillProfessionComponent> getComponentType() {
        return TYPE;
    }

    protected BaseProfessionComponent() {
        this.experience = 0;
    }

    protected BaseProfessionComponent(BaseProfessionComponent other) {
        this.experience = other.experience;
    }

    public int getExperience() {
        return this.experience;
    }

    public void setExperience(int gainedExperience) {
        this.experience = gainedExperience;
    }

    public int getLevel() {
        return LevelTable.getLevel(this.experience);
    }

    public int getMaxExperienceForLevel() {
        return LevelTable.getMaxExperienceForLevel(this.getLevel());
    }

    public int getExperienceToNextLevel() {
        return LevelTable.getExperienceToNextLevel(experience);
    }

    public boolean isMaxLevel() {
        return this.getLevel() >= LevelTable.getMaxLevel();
    }

    public boolean isLevelUp(int gainedExperience) {
        return this.getExperienceToNextLevel() <= gainedExperience;
    }

    public void addExperience(int gainedExperience) {
        this.experience += gainedExperience;
    }

    @NullableDecl
    @Override
    public abstract Component<EntityStore> clone();

    @NullableDecl
    @Override
    public abstract String toString();

    public abstract String getProfessionName();
}
