package com.warhammer.wartale.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class BaseMasteryComponent implements Component<EntityStore> {

    protected int experience;


    protected String masteryName;

    protected BaseMasteryComponent(String masterName) {
        this.experience = 0;
        this.masteryName = masterName;
    }

    protected BaseMasteryComponent() {
        this.masteryName = "";
        this.experience = 0;
    }


    protected BaseMasteryComponent(BaseMasteryComponent other) {
        this.experience = other.experience;
        this.masteryName = other.masteryName;
    }


    public int getExperience() {
        return this.experience;
    }


    public void setExperience(int gainedExperience) {
        this.experience = gainedExperience;
    }

    public void addExperience(int gainedExperience) {
        this.experience += gainedExperience;
    }


    @NullableDecl
    @Override
    public abstract Component<EntityStore> clone();


    @Override
    public abstract String toString();


    public abstract String getMasteryName();


    public abstract String getLevelUpgradeColor();


    public abstract String getLevelUpgradeSecondaryColor();


    public abstract String getLevelUpgradeIcon();
}
