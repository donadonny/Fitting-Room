
package tk.talcharnes.unborify.PhotoCard;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.mindorks.placeholderview.SwipeDirection;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.mindorks.placeholderview.annotations.swipe.SwipingDirection;

import tk.talcharnes.unborify.R;

/**
 * Created by Khuram Chaudhry on 09/02/2017.
 *
 */

@Layout(R.layout.ad_card_view)
public class AdCard {

    private final String LOG_TAG = AdCard.class.getSimpleName();

    @View(R.id.adCard)
    CardView adCard;

    @View(R.id.indeterminateBar)
    ProgressBar progressBar;

    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private AdView mAdView;

    public AdCard(Context context, SwipePlaceHolderView swipeView) {
        mContext = context;
        mSwipeView = swipeView;
    }

    /**
     * This function sets up the Card View with an image, name, and the ratings.
     */
    @Resolve
    public void onResolved() {
        mAdView = adCard.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
            }

//    /**
//     * This function handles when the Card View is swiped right.
//     */
    @SwipeIn
    public void onSwipeIn() {
        //Log.d("EVENT", "onSwipedIn");
    }

//    /**
//     * This function handles when the Card View is swiped left.
//     */
    @SwipeOut
    public void onSwipedOut() {
        //Log.d("EVENT", "onSwipeOut");
    }

//    /**
//     * This function handles when the Card View is moving right.
//     */
    @SwipeInState
    public void onSwipeInState() {
        //Log.d("EVENT", "onSwipeInState");
    }

//    /**
//     * This function handles when the Card View is moving left.
//     */
    @SwipeOutState
    public void onSwipeOutState() {
        //Log.d("EVENT", "onSwipeOutState");
    }

//    /**
//     * Don't know what this does.
//     */
    @SwipeCancelState
    public void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

//    /**
//     * This function records the direction of user touches.
//     */
    @SwipingDirection
    public void onSwipingDirection(SwipeDirection direction) {
        Log.d(LOG_TAG, "SwipingDirection " + direction.name());
    }
}