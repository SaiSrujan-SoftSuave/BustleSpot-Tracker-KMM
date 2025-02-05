package org.company.app.organisation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import org.company.app.mainnavigation.Graph

sealed class OrganisationScreen(val route:String){
    data object SelectionScreen: OrganisationScreen("selection_screen")
}

fun NavGraphBuilder.orgNavGraph(){
    navigation(
        startDestination = OrganisationScreen.SelectionScreen.route,
        route = Graph.ORGANISATION
    ){

    }
}
