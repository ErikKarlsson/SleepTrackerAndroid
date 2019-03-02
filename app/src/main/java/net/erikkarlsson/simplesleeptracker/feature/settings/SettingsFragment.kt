package net.erikkarlsson.simplesleeptracker.feature.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.android.support.AndroidSupportInjection
import net.erikkarlsson.simplesleeptracker.R
import net.erikkarlsson.simplesleeptracker.domain.SleepDetection
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var sleepDetection: SleepDetection

    var changeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        changeListener = object : SharedPreferences.OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
                val sleepDetectionSettings = listOf("prefs_sleep_detection_start_time",
                        "prefs_sleep_detection_stop_time", "prefs_sleep_detection_enabled")

                if (sleepDetectionSettings.contains(key)) {
                    sleepDetection.update()
                }
            }

        }

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(changeListener)

        preferenceManager.findPreference("privacyPolicy").setOnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/simplesleeptracker/"))
            startActivity(browserIntent)
            true
        }

        preferenceManager.findPreference("termsAndConditions").setOnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/simplesleeptracker/terms-and-conditions?authuser=0"))
            startActivity(browserIntent)
            true
        }

        preferenceManager.findPreference("thirdPartyLicenses").setOnPreferenceClickListener {
            startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(changeListener)
        changeListener = null
    }

    // Remove padding in Preference Screen - https://stackoverflow.com/questions/18509369/android-how-to-get-remove-margin-padding-in-preference-screen
    override fun onCreateAdapter(preferenceScreen: PreferenceScreen?): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {
            @SuppressLint("RestrictedApi")
            override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)
                val preference = getItem(position)
                if (preference is PreferenceCategory)
                    setZeroPaddingToLayoutChildren(holder.itemView)
                else
                    holder.itemView.findViewById<View?>(R.id.icon_frame)?.visibility = if (preference.icon == null) View.GONE else View.VISIBLE
            }
        }
    }

    private fun setZeroPaddingToLayoutChildren(view: View) {
        if (view !is ViewGroup)
            return
        val childCount = view.childCount
        for (i in 0 until childCount) {
            setZeroPaddingToLayoutChildren(view.getChildAt(i))
            view.setPaddingRelative(0, view.paddingTop, view.paddingEnd, view.paddingBottom)
        }
    }

}
