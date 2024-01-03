import android.content.Context
import android.view.LayoutInflater
import androidx.annotation.IdRes
import com.example.servivet.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationHelper {

    fun showBadge(context: Context, bottomNavigationView: BottomNavigationView, @IdRes itemId: Int) {
        removeBadge(bottomNavigationView, itemId)
        val itemView = bottomNavigationView.findViewById<BottomNavigationItemView>(itemId)
        val badge = LayoutInflater.from(context).inflate(R.layout.view_custom_indicator, bottomNavigationView, false)
        itemView.addView(badge)
    }

    fun removeBadge(bottomNavigationView: BottomNavigationView, @IdRes itemId: Int) {
        val itemView = bottomNavigationView.findViewById<BottomNavigationItemView>(itemId)

        if (itemView.childCount == 3) {
           // itemView.removeViewAt(2)
            itemView.removeViewAt(1)
        }
    }
}