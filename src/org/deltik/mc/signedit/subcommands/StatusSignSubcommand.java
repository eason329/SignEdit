package org.deltik.mc.signedit.subcommands;

import org.bukkit.entity.Player;
import org.deltik.mc.signedit.*;
import org.deltik.mc.signedit.interactions.SignEditInteraction;
import org.deltik.mc.signedit.listeners.SignEditListener;

import javax.inject.Inject;

public class StatusSignSubcommand implements SignSubcommand {
    private final Player player;
    private final ChatComms comms;
    private final SignEditListener listener;
    private final SignTextClipboardManager clipboardManager;
    private final SignTextHistoryManager historyManager;

    @Inject
    public StatusSignSubcommand(
            Player player,
            ChatComms comms,
            SignEditListener listener,
            SignTextClipboardManager clipboardManager,
            SignTextHistoryManager historyManager
    ) {
        this.player = player;
        this.comms = comms;
        this.listener = listener;
        this.clipboardManager = clipboardManager;
        this.historyManager = historyManager;
    }

    @Override
    public SignEditInteraction execute() {
        reportHistory();
        reportPendingAction();
        reportClipboard();
        return null;
    }

    private void reportPendingAction() {
        if (!listener.isInteractionPending(player)) {
            comms.tellPlayer(comms.primary() + comms.strong() + "Pending Action:" + comms.reset() + " " +
                    comms.primaryDark() + "None");
        } else {
            SignEditInteraction interaction = listener.getPendingInteraction(player);
            comms.tellPlayer(comms.primary() + comms.strong() + "Pending Action:" + comms.reset() + " " +
                    interaction.getName());
            comms.tellPlayer(" " + comms.italic() + "Right-click a sign to apply the action!");
        }
    }

    private void reportHistory() {
        SignTextHistory history = historyManager.getHistory(player);
        int undosRemaining = 0;
        int redosRemaining = 0;

        if (history != null) {
            undosRemaining = history.undosRemaining();
            redosRemaining = history.redosRemaining();
        }

        comms.tellPlayer(comms.primary() + comms.strong() + "History:" + comms.reset() + " " +
                comms.primary() + "have " + comms.primaryLight() +
                undosRemaining +
                comms.primary() + " undos, " + comms.primaryLight() +
                redosRemaining +
                comms.primary() + " redos");
    }

    private void reportClipboard() {
        SignText clipboard = clipboardManager.getClipboard(player);
        if (clipboard == null) {
            comms.tellPlayer(comms.primary() + comms.strong() + "Clipboard Contents:" + comms.reset() + " " +
                    comms.primaryDark() + "None");
        } else {
            comms.tellPlayer(comms.primary() + comms.strong() + "Clipboard Contents:");
            comms.dumpSignTextLines(clipboard);
        }
    }
}
