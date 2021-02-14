package com.agjk.repodepot.model.data

data class RateLimit(
    val rate: Rate? = Rate(),
    val resources: Resources? = Resources()
) {
    data class Rate(
        val limit: Int? = 0,
        val remaining: Int? = 0,
        val reset: Int? = 0,
        val used: Int? = 0
    )

    data class Resources(
        val core: Core? = Core(),
        val graphql: Graphql? = Graphql(),
        val integration_manifest: IntegrationManifest? = IntegrationManifest(),
        val search: Search? = Search()
    ) {
        data class Core(
            val limit: Int? = 0,
            val remaining: Int? = 0,
            val reset: Int? = 0,
            val used: Int? = 0
        )

        data class Graphql(
            val limit: Int? = 0,
            val remaining: Int? = 0,
            val reset: Int? = 0,
            val used: Int? = 0
        )

        data class IntegrationManifest(
            val limit: Int? = 0,
            val remaining: Int? = 0,
            val reset: Int? = 0,
            val used: Int? = 0
        )

        data class Search(
            val limit: Int? = 0,
            val remaining: Int? = 0,
            val reset: Int? = 0,
            val used: Int? = 0
        )
    }
}