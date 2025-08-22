package ai.elimu.chat.util

import ai.elimu.chat.util.Constants.EMOJI_UNICODES

fun getRandomEmoji(): String {
    val randomIndex = (Math.random() * EMOJI_UNICODES.size).toInt()
    val unicode = EMOJI_UNICODES[randomIndex]

    /**
     * See http://apps.timwhitlock.info/emoji/tables/unicode
     * @param unicode Example: "U+1F601" --> "0x1F601"
     * @return
     */
    val emoji = String(Character.toChars(unicode))
    return emoji
}