package dev.smartshub.shpets.api.pet.template;

public record EquipmentData(
    String helmet,
    String chestplate,
    String leggings,
    String boots,
    String hand,
    String offHand,
    String headValue,
    boolean showArms,
    boolean playerEquipment,
    boolean headOverride
) {}