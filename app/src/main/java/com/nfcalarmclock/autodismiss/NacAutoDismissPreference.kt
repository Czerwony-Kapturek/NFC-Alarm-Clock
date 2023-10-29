package com.nfcalarmclock.autodismiss

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import com.nfcalarmclock.R
import com.nfcalarmclock.shared.NacSharedConstants
import com.nfcalarmclock.shared.NacSharedDefaults
import com.nfcalarmclock.shared.NacSharedPreferences
import com.nfcalarmclock.view.dialog.NacScrollablePickerDialogFragment.OnScrollablePickerOptionSelectedListener

/**
 * Preference that displays how long before an alarm is auto dismissed.
 */
class NacAutoDismissPreference @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	style: Int = 0
) : Preference(context, attrs, style),
	OnScrollablePickerOptionSelectedListener
{

	/**
	 * Preference value.
	 */
	private var autoDismissIndex = 0

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
		val cons = NacSharedConstants(context)
		val value = autoDismissIndex
		return NacSharedPreferences.getAutoDismissSummary(cons, value)
	}

	/**
	 * Get the default value.
	 *
	 * @return The default value.
	 */
	override fun onGetDefaultValue(a: TypedArray, index: Int): Any
	{
		val defs = NacSharedDefaults(context)

		return a.getInteger(index, defs.autoDismissIndex)
	}

	/**
	 * Save the selected value from the scrollable picker.
	 */
	override fun onScrollablePickerOptionSelected(index: Int)
	{
		// Set the auto dismiss index
		autoDismissIndex = index

		// Persist the index
		persistInt(index)

		// Notify of a change
		notifyChanged()
	}

	/**
	 * Set the initial preference value.
	 */
	override fun onSetInitialValue(defaultValue: Any?)
	{
		// Check if the default value is null
		if (defaultValue == null)
		{
			autoDismissIndex = getPersistedInt(autoDismissIndex)
		}
		// Convert the default value
		else
		{
			autoDismissIndex = defaultValue as Int

			persistInt(autoDismissIndex)
		}
	}

	/**
	 * Show the auto dismiss dialog.
	 */
	fun showDialog(manager: FragmentManager)
	{
		// Create the dialog
		val dialog = NacAutoDismissDialog()

		// Setup the dialog
		dialog.defaultScrollablePickerIndex = autoDismissIndex
		dialog.onScrollablePickerOptionSelectedListener = this

		// Show the dialog
		dialog.show(manager, NacAutoDismissDialog.TAG)
	}

}