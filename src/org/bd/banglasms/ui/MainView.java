package org.bd.banglasms.ui;

import org.bd.banglasms.control.UIEvents;

/**
 * Main view shows the first typical view to user on application startup.
 * </p>
 * <h2> Events </h2>
 * <UL> It must support the following events.
 * <LI> {@link UIEvents#WRITE_MESSAGE}
 * <LI> {@link UIEvents#OPEN_INBOX}
 * <LI> {@link UIEvents#OPEN_SENT}
 * <LI> {@link UIEvents#OPEN_TEMPLATE}
 * <LI> {@link UIEvents#OPEN_DRAFTS}
 * <LI> {@link UIEvents#OPEN_HELP}
 * <LI> {@link UIEvents#OPEN_CREDIT}
 * <LI> {@link UIEvents#UPDATE}
 * <LI> {@link UIEvents#EXIT}
 * </UL>
 */
public interface MainView extends View{

}
