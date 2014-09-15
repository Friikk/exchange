/*
 * This file is part of Bitsquare.
 *
 * Bitsquare is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bitsquare is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bitsquare. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bitsquare.gui;

import io.bitsquare.persistence.Persistence;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationManager {
    private static final Logger log = LoggerFactory.getLogger(NavigationManager.class);
    private Persistence persistence;

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Interface
    ///////////////////////////////////////////////////////////////////////////////////////////

    public interface NavigationListener {
        void onNavigationRequested(NavigationItem... navigationItems);
    }

    private List<NavigationListener> listeners = new ArrayList<>();
    private NavigationItem[] previousMainNavigationItems;
    private NavigationItem[] currentNavigationItems;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public NavigationManager(Persistence persistence) {
        this.persistence = persistence;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Public methods
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void navigationTo(NavigationItem... navigationItems) {
        previousMainNavigationItems = currentNavigationItems;
        currentNavigationItems = navigationItems;

        persistence.write(this, "navigationItems", navigationItems);

        listeners.stream().forEach((e) -> e.onNavigationRequested(navigationItems));
    }

    public void navigateToLastStoredItem() {
        NavigationItem[] navigationItems = (NavigationItem[]) persistence.read(this, "navigationItems");
        if (navigationItems == null || navigationItems.length == 0)
            navigationItems = new NavigationItem[]{NavigationItem.HOME};

        navigationTo(navigationItems);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    // Listeners
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void addListener(NavigationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NavigationListener listener) {
        listeners.remove(listener);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////////////////////

    public NavigationItem[] getPreviousMainNavigationItems() {
        return previousMainNavigationItems;
    }

    public NavigationItem[] getCurrentNavigationItems() {
        return currentNavigationItems;
    }

}
