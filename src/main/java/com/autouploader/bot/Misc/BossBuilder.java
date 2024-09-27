package com.autouploader.bot.Misc;

import com.autouploader.bot.Models.Boss;
import com.autouploader.bot.Models.LogsListener.TypeOfRecording;

public class BossBuilder {
    private StringBuilder wingContent;
    private Boss boss;

    public BossBuilder(Boss boss) {
        this.boss = boss;
        this.wingContent = new StringBuilder();
    }

    public String build(TypeOfRecording typeOfRecording) {
        if (typeOfRecording == TypeOfRecording.SINGLE) {
            appendSuccessIndicator();
            wingContent.append(" [").append(boss.getFightName());
        } else {
            appendBossEmoji();
            wingContent.append(" [").append(boss.getFightName());
        }

        appendDifficultyIndicators();
        
        wingContent.append("](")
            .append(boss.getPermalink())
            .append(") - ")
            .append(formatDuration(boss.getDuration()))
            .append("\n");

        return wingContent.toString();
    }

    private void appendSuccessIndicator() {
        wingContent.append(boss.getSuccess() ? ":white_check_mark:" : ":x:");
    }

    private void appendBossEmoji() {
        if (boss.getEmoji() != null) {
            wingContent.append(boss.getEmoji()).append(" ");
        }
    }

    private void appendDifficultyIndicators() {
        if (boss.getLogBossIsCm() && !boss.getLogBossIsLcm()) {
            wingContent.append(" (CM)");
        } else if (boss.getLogBossIsLcm()) {
            wingContent.append(" (LCM)");
        }
    }

    private String formatDuration(String duration) {
        return duration.replace("00m ", "");
    }
}
