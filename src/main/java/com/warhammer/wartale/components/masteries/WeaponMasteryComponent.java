package com.warhammer.wartale.components.masteries;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.masteryCore.MasteryCalculations;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.Map;

public abstract class WeaponMasteryComponent extends BaseMasteryComponent {
    protected WeaponMasteryComponent() {
        super();
    }

    protected WeaponMasteryComponent(String masterName) {
        super(masterName);
    }


    protected WeaponMasteryComponent(WeaponMasteryComponent other) {
        super(other);
    }

    @NullableDecl
    @Override
    public abstract Component<EntityStore> clone();


    @Override
    public String toString() {
        return this.masteryName + "{level=" + MasteryCalculations.getLevel(this.experience) +
                ", Experience=" + this.experience +
                ", toNext=" + MasteryCalculations.getTotalExperienceForLevel(MasteryCalculations.getLevel(this.experience)) + "}";
    }


    @Override
    public String getMasteryName() {
        return this.masteryName;
    }


    @Override
    public String getLevelUpgradeColor() {
        return "#c300ff";
    }


    @Override
    public String getLevelUpgradeSecondaryColor() {
        return "#8b32ff";
    }


    @Override
    public String getLevelUpgradeIcon() {
        return "Weapon_Boltpistol";
    }

    public abstract Map<String, Integer> getWeaponTierMap();
}
