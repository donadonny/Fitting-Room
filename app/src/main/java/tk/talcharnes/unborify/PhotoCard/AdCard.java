
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
 */

@Layout(R.layout.ad_card_view)
public class AdCard {

    private final String LOG_TAG = AdCard.class.getSimpleName();

    @View(R.id.adCard)
    private CardView adCard;

    @View(R.id.indeterminateBar)
    private ProgressBar progressBar;

    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private int width;
    private int height;

    public AdCard(Context context, SwipePlaceHolderView swipeView) {
        mContext = context;
        mSwipeView = swipeView;
    }

    /**
     * This function sets up the Card View with an image, name, and the ratings.
     */
    @Resolve
    private void onResolved() {
        ViewTreeObserver vto = adCard.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adCard.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                width = pxToDp(adCard.getMeasuredWidth());
                height = pxToDp(adCard.getMeasuredHeight());

                Log.d(LOG_TAG, "Initial Width = " + width +
                        " ----------------------------------- Initial Height = " + height);

                width = (width < 80) ? 80 : (width > 1200) ? 1200 : (int) (width * .9);
                height = (height < 80) ? 80 : (height > 1200) ? 1200 : (int) (height * .9);

                Log.d(LOG_TAG, "Final Width = " + width +
                        " ----------------------------------- Final Height = " + height);

                CardView.LayoutParams params = new CardView.LayoutParams(
                        CardView.LayoutParams.MATCH_PARENT,
                        CardView.LayoutParams.MATCH_PARENT - 75, Gravity.TOP);

                NativeExpressAdView mAdView = new NativeExpressAdView(mContext);
                mAdView.setAdSize(new AdSize(width, height));
                mAdView.setAdUnitId("ca-app-pub-6667404740993831/9531692095");
                adCard.addView(mAdView, params);

                AdRequest request = new AdRequest.Builder().build();
                mAdView.loadAd(request);

                if (mAdView.isLoading()) {
                    progressBar.setVisibility(android.view.View.VISIBLE);
                } else {
                    progressBar.setVisibility(android.view.View.INVISIBLE);
                }

            }
        });
    }

    /**
     * This function handles when the Card View is swiped right.
     */
    @SwipeIn
    private void onSwipeIn() {
        //Log.d("EVENT", "onSwipedIn");
    }

    /**
     * This function handles when the Card View is swiped left.
     */
    @SwipeOut
    private void onSwipedOut() {
        //Log.d("EVENT", "onSwipeOut");
    }

    /**
     * This function handles when the Card View is moving right.
     */
    @SwipeInState
    private void onSwipeInState() {
        //Log.d("EVENT", "onSwipeInState");
    }

    /**
     * This function handles when the Card View is moving left.
     */
    @SwipeOutState
    private void onSwipeOutState() {
        //Log.d("EVENT", "onSwipeOutState");
    }

    /**
     * Don't know what this does.
     */
    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

    /**
     * This function records the direction of user touches.
     */
    @SwipingDirection
    private void onSwipingDirection(SwipeDirection direction) {
        Log.d(LOG_TAG, "SwipingDirection " + direction.name());
    }

    private int pxToDp(int px) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

}