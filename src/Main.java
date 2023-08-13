import com.epicbot.api.shared.APIContext;
import com.epicbot.api.shared.GameType;
import com.epicbot.api.shared.entity.*;
import com.epicbot.api.shared.event.ChatMessageEvent;
import com.epicbot.api.shared.methods.ITabsAPI;
import com.epicbot.api.shared.model.Area;
import com.epicbot.api.shared.model.Skill;
import com.epicbot.api.shared.model.Spell;
import com.epicbot.api.shared.model.Tile;
import com.epicbot.api.shared.script.LoopScript;
import com.epicbot.api.shared.script.ScriptManifest;
import com.epicbot.api.shared.util.Random;
import com.epicbot.api.shared.util.paint.frame.PaintFrame;
import com.epicbot.api.shared.util.time.Time;
import sun.security.action.GetBooleanAction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


@ScriptManifest(name = "Recoil FACTORY", gameType = GameType.OS)
public class main extends LoopScript {


    public boolean needsToBank() {
        return !getAPIContext().inventory().contains("Cosmic rune") || !getAPIContext().inventory().contains("Sapphire ring");
    }

    public boolean needsToOpenBank() {
        return !getAPIContext().bank().isOpen();
    }

    public void openBank() {

        if (getAPIContext().magic().isSpellSelected()) {
            getAPIContext().mouse().click(450, 265);

        } else {
            getAPIContext().bank().open();
        }


    }
    public boolean shouldLogOut() {
        return getAPIContext().bank().getCount("Sapphire ring") == 0 || getAPIContext().inventory().getCount("Cosmic rune") == 0;
    }

    public void logOut() {
        if (getAPIContext().bank().isOpen()) {
            getAPIContext().bank().close(); }
        getAPIContext().game().logout();
        getAPIContext().script().stop("Das it bro, was it worth it?");



    }

    public boolean doesNeedToDeposit() {
        return getAPIContext().inventory().getCount("Ring of recoil") == 27;
    }

    public void depositRings() {

        getAPIContext().bank().depositAllExcept("Cosmic rune");
    }

    public boolean needsWithdrawSapphirerings() {
        return getAPIContext().inventory().getCount("Ring of recoil") == 0 && getAPIContext().inventory().getCount("Sapphire ring") == 0;

    }

    public void withdrawSapphireRing() {
        getAPIContext().bank().withdrawAll("Sapphire ring");
    }


    public boolean needsToEnchant() {
        return getAPIContext().inventory().contains("Cosmic Rune") && getAPIContext().inventory().contains("Sapphire ring");

    }

    public void enchantsRings() {
        int count = getAPIContext().inventory().getCount(true, "Ring of recoil");
        if (!getAPIContext().magic().isSpellSelected()) {
            if (getAPIContext().magic().cast(Spell.Modern.LEVEL_1_ENCHANT)) {
                Time.sleep(500, 1000, () -> getAPIContext().magic().isSpellSelected());
            }
        } else {
            ItemWidget ring = getAPIContext().inventory().getItem("Sapphire ring");
            if (ring != null) {
                if (ring.interact()) {
                    Time.sleep(500, 1000, () -> getAPIContext().inventory().getCount(true, "Ring of recoil") > count);
                }
            }
        }
    }

    public final String formatTime(long ms) {
        long s = ms / 1000, m = s / 60, h = m / 60, d = h / 24;
        s %= 60;
        m %= 60;
        h %= 24;

        return d > 0 ? String.format("%02d:%02d:%02d:%02d", d, h, m, s) :
                h > 0 ? String.format("%02d:%02d:%02d", h, m, s) :
                        String.format("%02d:%02d", m, s);
    }


    @Override
    public boolean onStart(String... strings) {
        System.out.println("Lets enchant some rings");
        return true;


    }




    @Override

    protected int loop() {
        if (!getAPIContext().client().isLoggedIn()) {
            System.out.println("hold up till you are logged in mayne");
            return 300;
        }
        if (needsToBank() && needsToOpenBank()) {
            System.out.println("Opening bank");
            openBank();
            return 300;
        }
        if (getAPIContext().bank().isOpen()) {
            if (doesNeedToDeposit()) {
                System.out.println("Depositing rings");
                depositRings();
                return 200;
            }
            if (shouldLogOut()) {
                logOut();
            }
            if (needsWithdrawSapphirerings()) {
                System.out.println("Withdrawin the rings");
                withdrawSapphireRing();
                return 1000;
            } else {
                getAPIContext().bank().close();
                return 200;
            }
        }

        if (needsToEnchant()) {
            System.out.println("Enchanting rings");
            enchantsRings();
            return 200;
        }
        if (needsToBank()) {
            System.out.println("Bankin");
            openBank();
            return 200;
        }
        if (doesNeedToDeposit()) {
            depositRings();
            return 200;
        }
        return 200;
    }


}