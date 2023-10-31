package com.nfcalarmclock.view.dayofweek

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import com.nfcalarmclock.R
import com.nfcalarmclock.shared.NacSharedPreferences
import com.nfcalarmclock.util.NacCalendar
import java.util.EnumSet

/**
 * Preference that displays the day of week dialog.
 */
class NacDayOfWeekPreference @JvmOverloads constructor(

	/**
	 * Context.
	 */
	context: Context,

	/**
	 * Attribute set.
	 */
	attrs: AttributeSet? = null,

	/**
	 * Default style.
	 */
	style: Int = 0

	// Constructor
) : Preference(context, attrs, style),

	// Interface
	NacDayOfWeekDialog.OnDaysOfWeekSelectedListener
{

	/**
	 * Value of days.
	 */
	private var dayOfWeekValue = 0

	/**
	 * Shared preferences.
	 */
	private val sharedPreferences: NacSharedPreferences = NacSharedPreferences(context)

	/**
	 * Constructor.
	 */
	init
	{
		layoutResource = R.layout.nac_preference
	}

	/**
	 * Get the summary text.
	 *
	 * @return The summary text.
	 */
	override fun getSummary(): CharSequence?
	{
		return sharedPreferences.daysSummary
	}

	/**
	 * Called when the days of week are selected.
	 */
	override fun onDaysOfWeekSelected(selectedDays: EnumSet<NacCalendar.Day>)
	{
		// Set the day of week value
		dayOfWeekValue = NacCalendar.Days.daysToValue(selectedDays)

		// Reevaluate the summary
		summary = this.summary

		// Persist the value
		persistInt(dayOfWeekValue)
	}

	/**
	 * Get the default value.
	 *
	 * @return The default value.
	 */
	override fun onGetDefaultValue(a: TypedArray, index: Int): Any
	{
		// Calculate the default value
		val default = NacCalendar.Days.daysToValue(
			EnumSet.of(NacCalendar.Day.MONDAY, NacCalendar.Day.TUESDAY,
				NacCalendar.Day.WEDNESDAY, NacCalendar.Day.THURSDAY,
				NacCalendar.Day.FRIDAY))

		// Get the default value
		return a.getInteger(index, default)
	}

	/**
	 * Set the initial preference value.
	 */
	override fun onSetInitialValue(defaultValue: Any?)
	{
		// Check if the default value is null
		if (defaultValue == null)
		{
			dayOfWeekValue = getPersistedInt(dayOfWeekValue)
		}
		// Convert the default value
		else
		{
			dayOfWeekValue = defaultValue as Int
			persistInt(dayOfWeekValue)
		}
	}

	/**
	 * Show the start week on dialog.
	 */
	fun showDialog(manager: FragmentManager)
	{
		// Create the dialog
		val dialog = NacDayOfWeekDialog()

		// Setup the dialog
		dialog.defaultDayOfWeekValues = dayOfWeekValue
		dialog.onDaysOfWeekSelectedListener = this

		// Show the dialog
		dialog.show(manager, NacDayOfWeekDialog.TAG)
	}

}