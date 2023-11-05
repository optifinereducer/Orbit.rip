package rip.warzone.anticheat.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnumClientType implements ClientType {

    COSMIC_CLIENT(false, "CosmicClient"),
    CHEAT_BREAKER(false, "CheatBreaker"),
    Lunar_Client(false, "Lunar-Client"),
    VANILLA(false, "Vanilla"),
    FORGE(false, "Forge-Client"),
    OCMC(false, "OCMC-Client", "OCMC"),
    CRYSTALWARE(true, "Crystalware", "CRYSTAL|6LAKS0TRIES"),
    CRYSTALWARE1(true, "Crystalware", "CRYSTAL|KZ1LM9TO"),
    Vapev3(true, "Vape v3", "1946203560"),
    NoNameCLient(true, "NoNameCLient", "lmaohax"),
    bspkrsCore_Client(true, "bspkrsCore Client", "0SO1Lk2KASxzsd"),
    bspkrsCoreClient(true, "bspkrsCore Client", "customGuiOpenBspkrs"),
    Vape(true, "Vape", "cock"),
    Moon(true, "Moon", "moon:exempt");



    private final boolean hacked;
    private final String name;
    private String payloadTag;

    EnumClientType(boolean hacked, String name) {
        this.hacked=hacked;
        this.name=name;
    }

}
