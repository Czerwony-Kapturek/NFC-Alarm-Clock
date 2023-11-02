package com.nfcalarmclock.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import com.nfcalarmclock.R
import com.nfcalarmclock.alarm.db.NacAlarm
import com.nfcalarmclock.util.NacUtility.quickToast
import com.nfcalarmclock.util.NacUtility.toast

/**
 * NFC helper object.
 */
object NacNfc
{

	/**
	 * Add an NFC tag into an Intent.
	 */
	fun addTagToIntent(intent: Intent?, tag: Tag?)
	{
		// Check if the intent or tag are null
		if ((intent == null) || (tag == null))
		{
			return
		}

		// Add the tag to the intent
		intent.putExtra(NfcAdapter.EXTRA_TAG, tag)
	}

	/**
	 * Check if NFC exists on this device.
	 */
	@JvmStatic
	fun exists(context: Context): Boolean
	{
		return NfcAdapter.getDefaultAdapter(context) != null
	}

	/**
	 * Get an NFC tag from the given Intent.
	 *
	 * @return An NFC tag from the given Intent.
	 */
	@JvmStatic
	@Suppress("deprecation")
	fun getTag(intent: Intent?): Tag?
	{
		// Use the updated form of Intent.getParecelableExtra() if API >= 33
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
			{
				// Return the tag from the intent or null if the intent is null
				intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG,
					Tag::class.java)
			}
			else
			{
				if (intent != null)
				{
					intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?
				}
				else
				{
					null
				}
			}
	}

	/**
	 * Check if the NFC adapter is enabled.
	 *
	 * @return True if the NFC adapter is enabled, and False otherwise.
	 */
	@JvmStatic
	fun isEnabled(context: Context?): Boolean
	{
		val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

		return (nfcAdapter != null) && nfcAdapter.isEnabled
	}

	/**
	 * Parse NFC tag ID to a readable format.
	 */
	@JvmStatic
	fun parseId(nfcTag: Tag?): String?
	{
		// NFC tag is not defined
		if (nfcTag == null)
		{
			return null
		}

		// Check the ID of the NFC tag
		val srcId = nfcTag.id ?: return ""

		// Unable to find an ID on the NFC tag
		val id = StringBuilder()
		val buffer = CharArray(2)

		// Compile the NFC tag ID
		for (b in srcId)
		{
			buffer[0] = Character.forDigit(b.toInt() ushr 4 and 0x0F, 16)
			buffer[1] = Character.forDigit(b.toInt() and 0x0F, 16)
			id.append(buffer)
		}

		return id.toString()
	}

	/**
	 * @see .parseId
	 */
	fun parseId(intent: Intent?): String?
	{
		// Get the NFC tag from the intent
		val nfcTag = getTag(intent)

		return if (nfcTag != null)
			{
				// Get the ID from the NFC tag
				parseId(nfcTag)
			}
			else
			{
				null
			}
	}

	/**
	 * Prompt the user to enable NFC.
	 */
	@JvmStatic
	fun prompt(context: Context)
	{
		// NFC adapter does not exist
		if (!exists(context))
		{
			return
		}

		val settings = Intent(Settings.ACTION_NFC_SETTINGS)
		val message = context.getString(R.string.message_nfc_request)

		// Prompt the user to enable NFC
		toast(context, message)
		context.startActivity(settings)
	}

	/**
	 * Check if should use NFC.
	 *
	 * @return True if should use NFC, and False otherwise.
	 */
	@JvmStatic
	fun shouldUseNfc(context: Context, alarm: NacAlarm?): Boolean
	{
		return (alarm != null) && exists(context) && alarm.shouldUseNfc
	}

	/**
	 * @see .start
	 */
	@JvmStatic
	fun start(activity: Activity)
	{
		// Create an intent for an activity
		val intent = Intent(activity, activity.javaClass)
			.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

		// Start the activity with the intent
		start(activity, intent)
	}

	/**
	 * Enable NFC dispatch, so that the app can discover NFC tags.
	 */
	@JvmStatic
	fun start(activity: Activity, intent: Intent?)
	{
		val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)

		// NFC adapter is not present or not enabled
		if ((nfcAdapter == null) || !isEnabled(activity))
		{
			return
		}

		// Determine the pending intent flags
		var flags = 0

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
		{
			flags = flags or PendingIntent.FLAG_MUTABLE
		}

		// Create the pending intent
		val pending = PendingIntent.getActivity(activity, 0, intent, flags)

		// Enable NFC foreground dispatch
		try
		{
			nfcAdapter.enableForegroundDispatch(activity, pending, null, null)
		}
		catch (e: SecurityException)
		{
			// TODO: Create string for error message
			quickToast(activity, "Unable to scan NFC tags")
		}
	}

	/**
	 * Stop NFC dispatch, so the app does not waste battery when it does not
	 * need to discover NFC tags.
	 */
	@JvmStatic
	fun stop(context: Context)
	{
		val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

		// NFC adapter is not present or not enabled
		if ((nfcAdapter == null) || !isEnabled(context))
		{
			return
		}

		// Disable NFC foreground dispatch
		try
		{
			nfcAdapter.disableForegroundDispatch(context as Activity)
		}
		catch (ignored: IllegalStateException)
		{
		}
	}

	/**
	 * Check if an NFC tag was scanned/discovered and False otherwise.
	 *
	 * @return True if an NFC tag was scanned/discovered and False otherwise.
	 */
	@JvmStatic
	fun wasScanned(intent: Intent?): Boolean
	{
		// Check if intent is null
		if (intent == null)
		{
			return false
		}

		// Check the intent's action
		return if (intent.action.isNullOrEmpty())
			{
				false
			}
			else
			{
				(intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED)
					|| (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED)
					|| (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED)
			}
	}

}