package org.jetbrains.dokka.webhelp.location

import org.jetbrains.dokka.base.resolvers.local.DokkaLocationProvider
import org.jetbrains.dokka.base.resolvers.local.LocationProviderFactory
import org.jetbrains.dokka.links.DRI
import org.jetbrains.dokka.model.DisplaySourceSet
import org.jetbrains.dokka.pages.PageNode
import org.jetbrains.dokka.pages.RootPageNode
import org.jetbrains.dokka.plugability.DokkaContext

class WebhelpLocationProviderFactory(private val context: DokkaContext) : LocationProviderFactory {
    override fun getLocationProvider(pageNode: RootPageNode) = WebhelpLocationProvider(pageNode, context)
}

class WebhelpLocationProvider(
    pageGraphRoot: RootPageNode,
    dokkaContext: DokkaContext
) : DokkaLocationProvider(pageGraphRoot, dokkaContext, ".xml") {
    override fun resolve(node: PageNode, context: PageNode?, skipExtension: Boolean): String =
        super.resolve(node, context, skipExtension).replace("/", ".")

    override fun resolve(dri: DRI, sourceSets: Set<DisplaySourceSet>, context: PageNode?): String? =
        super.resolve(dri, sourceSets, context)?.replace("/", ".")
}