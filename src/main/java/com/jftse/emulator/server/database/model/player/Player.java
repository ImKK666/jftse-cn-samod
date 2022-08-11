package com.jftse.emulator.server.database.model.player;

import com.jftse.emulator.common.model.AbstractBaseModel;
import com.jftse.emulator.server.database.model.account.Account;
import com.jftse.emulator.server.database.model.challenge.ChallengeProgress;
import com.jftse.emulator.server.database.model.guild.GuildMember;
import com.jftse.emulator.server.database.model.pocket.Pocket;
import com.jftse.emulator.server.database.model.tutorial.TutorialProgress;
import com.jftse.emulator.server.database.model.pocket.PlayerPocket;
import com.jftse.emulator.server.database.repository.pocket.PlayerPocketRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.Fetch;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Audited
@Entity
@Log4j2
public class Player extends AbstractBaseModel {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "pocket_id", referencedColumnName = "id")
    private Pocket pocket;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pocket")
    private List<PlayerPocket> playerPocketList;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "clothEquipment_id", referencedColumnName = "id")
    private ClothEquipment clothEquipment;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "quickSlotEquipment_id", referencedColumnName = "id")
    private QuickSlotEquipment quickSlotEquipment;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "toolSlotEquipment_id", referencedColumnName = "id")
    private ToolSlotEquipment toolSlotEquipment;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "specialSlotEquipment_id", referencedColumnName = "id")
    private SpecialSlotEquipment specialSlotEquipment;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "cardSlotEquipment_id", referencedColumnName = "id")
    private CardSlotEquipment cardSlotEquipment;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "playerStatistic_id", referencedColumnName = "id")
    private PlayerStatistic playerStatistic;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "player")
    private List<ChallengeProgress> challengeProgressList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "player")
    private List<TutorialProgress> tutorialProgressList;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "player")
    private GuildMember guildMember;

    private Boolean firstPlayer = false;
    private Boolean alreadyCreated = false;
    private String name = "";
    private Byte level = 1;
    private Integer expPoints = 0;
    private Boolean nameChangeAllowed = false;
    private Integer gold = 0;
    private Byte playerType;

    private Byte strength = 0;
    private Byte stamina = 0;
    private Byte dexterity = 0;
    private Byte willpower = 0;
    private Byte statusPoints = 0;

    public Boolean haveNameChangeItem() {
        log.debug("PlayerItemCount="+playerPocketList.size());
        for (PlayerPocket playerPocket : playerPocketList) {
            log.debug(String.format("Cat=%s,Index=%d,Count=%d",
                    playerPocket.getCategory(),
                    playerPocket.getItemIndex(),
                    playerPocket.getItemCount()));
            if (playerPocket.getCategory().equals("SPECIAL")
                    && playerPocket.getItemIndex() == 4
                    && playerPocket.getItemCount() > 0) {
                log.debug("return true;");
                return true;
            }
        }
        return false;
    }
}
