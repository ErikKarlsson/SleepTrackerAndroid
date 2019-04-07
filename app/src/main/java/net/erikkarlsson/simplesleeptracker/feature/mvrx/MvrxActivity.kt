package net.erikkarlsson.simplesleeptracker.feature.mvrx

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity
import net.erikkarlsson.simplesleeptracker.R

class MvrxActivity : BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mvrx)

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, MvrxFragment.newInstance("123"), "MvrxFragment")
                    .disallowAddToBackStack()
                    .commit()
        }
    }
}
