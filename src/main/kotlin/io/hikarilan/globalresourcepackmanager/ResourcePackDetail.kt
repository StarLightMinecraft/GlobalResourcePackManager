package io.hikarilan.globalresourcepackmanager

import net.kyori.adventure.text.Component

/**
 * @param url The URL from which the client will download the resource
 *     pack. The string must contain only US-ASCII characters and should
 *     be encoded as per RFC 1738.
 * @param hash A 40 character hexadecimal and lowercase SHA-1 digest of
 *     the resource pack file.
 * @param required Marks if the resource pack should be required by the client
 * @param resourcePackPrompt A Prompt to be displayed in the client request
 */
data class ResourcePackDetail(
    val url: String,
    val hash: String,
    val required: Boolean,
    val resourcePackPrompt: Component?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ResourcePackDetail) return false

        if (hash != other.hash) return false

        return true
    }

    override fun hashCode(): Int {
        return hash.hashCode()
    }
}