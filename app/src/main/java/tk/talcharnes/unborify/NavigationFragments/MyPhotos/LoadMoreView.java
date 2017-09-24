package tk.talcharnes.unborify.NavigationFragments.MyPhotos;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mindorks.placeholderview.InfinitePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.infinite.LoadMore;

import java.util.List;

import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;

/**
 * Created by khuramchaudhry on 9/21/17.
 */

@Layout(R.layout.load_more_view)
public class LoadMoreView {

    public static final int LOAD_VIEW_SET_COUNT = 8;

    private InfinitePlaceHolderView mLoadMoreView;
    private List<Photo> mFeedList;
    private String mUserId, mUserName;

    public LoadMoreView(InfinitePlaceHolderView loadMoreView, List<Photo> feedList, String userId,
                        String userName) {
        this.mLoadMoreView = loadMoreView;
        this.mFeedList = feedList;
        mUserId = userId;
        mUserName = userName;
    }

    @LoadMore
    private void onLoadMore() {
        Log.d("DEBUG", "onLoadMore");
        new ForcedWaitedLoading();
    }

    private class ForcedWaitedLoading implements Runnable {

        public ForcedWaitedLoading() {
            new Thread(this).start();
        }

        @Override
        public void run() {

            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    int count = mLoadMoreView.getViewCount();
                    Log.d("DEBUG", "count " + count);
                    for (int i = count - 1;
                         i < (count - 1 + LoadMoreView.LOAD_VIEW_SET_COUNT) && mFeedList.size() > i;
                         i++) {
                        mLoadMoreView.addView(new PhotoView(mLoadMoreView.getContext(),
                                mFeedList.get(i), mUserId, mUserName, mLoadMoreView));

                        if (i == mFeedList.size() - 1) {
                            mLoadMoreView.noMoreToLoad();
                            break;
                        }
                    }
                    mLoadMoreView.loadingDone();
                }
            });
        }
    }
}