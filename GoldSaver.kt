import basemod.BaseMod
import basemod.interfaces.PostDeathSubscriber
import basemod.interfaces.StartGameSubscriber
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer
import com.megacrit.cardcrawl.core.CardCrawlGame
import com.megacrit.cardcrawl.dungeons.AbstractDungeon
import java.io.File

@SpireInitializer
class TestMod() : StartGameSubscriber, PostDeathSubscriber {

    companion object {
        @JvmStatic
        fun initialize() {
            TestMod()
        }
    }

    init {
        BaseMod.subscribe(this)
    }

    override fun receiveStartGame() {
        println("RECEIVE_PRE_START_GAME")
        setup()
    }

    override fun receivePostDeath() {
        println("RECEIVE_POST_DEATH")
        saveGold()
    }

    private fun saveGold() {
        println("Saving ${AbstractDungeon.player.gold} gold.")
        val filepath = "${System.getProperty("user.dir")}\\gold.txt"
        val file = File(filepath)
        file.writeText(AbstractDungeon.player.gold.toString())
    }

    private fun loadGold(): Int? {
        val filepath = "${System.getProperty("user.dir")}\\gold.txt"
        val file = File(filepath)
        return if (file.exists()) {
            if (file.readText() == "none") {
                null
            } else {
                file.readText().toInt()
            }
        } else {
            file.writeText("none")
            null
        }

    }

    private fun clearGold() {
        val filepath = "${System.getProperty("user.dir")}\\gold.txt"
        val file = File(filepath)
        file.writeText("none")
    }

    private fun setup() {
        loadGold()?.let {
            val goldToGain = it - AbstractDungeon.player.gold
            if (goldToGain > 0) {
                println("Adding $goldToGain gold.")
                CardCrawlGame.sound.play("GOLD_JINGLE")
                AbstractDungeon.player.gainGold(goldToGain)
                CardCrawlGame.goldGained -= goldToGain
            }
            clearGold()
        }
    }
}