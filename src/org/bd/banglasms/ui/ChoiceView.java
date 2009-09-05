package org.bd.banglasms.ui;


/**
 * This view shows a list choices to user.
 * <h2> Events </h2>
 * <UL> It must support the following events.
 * <LI> {@link #EVENT_CHOICE_SELECTED}
 * <LI> {@link #EVENT_CHOICE_CANCELLED}
 * </UL>
 */
public interface ChoiceView extends View {
	/**
	 * This event will be notified when user will choose an option.
	 */
	public static final String EVENT_CHOICE_SELECTED = "event_choice_selected";

	/**
	 * This event will be notified when user will cancel the choice list.
	 */
	public static final String EVENT_CHOICE_CANCELLED = "event_choice_cancelled";

	/**
	 * Sets the options for user
	 * @param options option texts for user
	 */
	public void setOptions(String[] options);

	/**
	 * Returns the index of the selected Option or -1 if none selected.
	 * @return index of the selected Option or -1 if none selected
	 */
	public int getSelectedOption();

	/**
	 * Sets the option at given index selected.
	 * @param index index of the option to be selected
	 * @throws IllegalArgumentException if index is not valid
	 */
	public void setSelectedOption(int index);
}
