package com.wbrawner.twigs.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Summary(
    @SerialName("domains_being_blocked")
    val domainsBeingBlocked: String,
    @SerialName("dns_queries_today")
    val dnsQueriesToday: String,
    @SerialName("ads_blocked_today")
    val adsBlockedToday: String,
    @SerialName("ads_percentage_today")
    val adsPercentageToday: String,
    @SerialName("unique_domains")
    val uniqueDomains: String,
    @SerialName("queries_forwarded")
    val queriesForwarded: String,
    @SerialName("clients_ever_seen")
    val clientsEverSeen: String,
    @SerialName("unique_clients")
    val uniqueClients: String,
    @SerialName("dns_queries_all_types")
    val dnsQueriesAllTypes: String,
    @SerialName("queries_cached")
    val queriesCached: String,
    @SerialName("no_data_replies")
    val noDataReplies: String? = null,
    @SerialName("nx_domain_replies")
    val nxDomainReplies: String? = null,
    @SerialName("cname_replies")
    val cnameReplies: String? = null,
    @SerialName("in_replies")
    val ipReplies: String? = null,
    @SerialName("privacy_level")
    val privacyLevel: String,
    override val status: Status,
    @SerialName("gravity_last_updated")
    val gravity: Gravity? = null,
    val type: String? = null,
    val version: Int? = null
) : StatusProvider

@Serializable
enum class Status {
    @SerialName("enabled")
    ENABLED,
    @SerialName("disabled")
    DISABLED
}

@Serializable
data class Gravity(
    @SerialName("file_exists")
    val fileExists: Boolean,
    val absolute: Int,
    val relative: Relative
)

@Serializable
data class Relative(
    val days: String,
    val hours: String,
    val minutes: String
)

@Serializable
data class VersionResponse(val version: Int)

@Serializable
data class TopItemsResponse(
    @SerialName("top_queries") val topQueries: Map<String, String>,
    @SerialName("top_ads") val topAds: Map<String, Double>
)

@Serializable
data class StatusResponse(
    override val status: Status
) : StatusProvider

interface StatusProvider {
    val status: Status
}
