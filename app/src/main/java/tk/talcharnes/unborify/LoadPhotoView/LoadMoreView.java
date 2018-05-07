package tk.talcharnes.unborify.LoadPhotoView;

import android.os.Handler;
import android.os.Looper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.InfinitePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.infinite.LoadMore;
import java.util.List;
import tk.talcharnes.unborify.Models.PhotoModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 9/21/17.
 * This class sets the load operation to load more photos from the list.
 */

@Layout(R.layout.load_more_view)
public class LoadMoreView {

   // public static String TAG = LoadMoreView.class.getSimpleName();
    public static final int LOAD_VIEW_SET_COUNT = 8;

    private InfinitePlaceHolderView mLoadMoreView;
    private List<PhotoModel> mFeedList;
    private List<String> mFeedList2;
    private String mUserId, mUserName;
    private boolean canEditPhotoList = false;

    public LoadMoreView(InfinitePlaceHolderView loadMoreView, List<PhotoModel> feedList, String userId,
                        String userName, boolean canEditPhotoList) {
        this.mLoadMoreView = loadMoreView;
        this.mFeedList = feedList;
        mUserId = userId;
        mUserName = userName;
        this.canEditPhotoList = canEditPhotoList;
    }

    public LoadMoreView(InfinitePlaceHolderView loadMoreView, List<String> feedList2, String userId,
                        String userName) {
        this.mLoadMoreView = loadMoreView;
        this.mFeedList2 = feedList2;
        mUserId = userId;
        mUserName = userName;
    }

    @LoadMore
    public void onLoadMore() {
        new ForcedWaitedLoading();
    }

    private class ForcedWaitedLoading implements Runnable {

        ForcedWaitedLoading() {
            new Thread(this).start();
        }

        @Override
        public void run() {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    int size = (mFeedList == null) ? mFeedList2.size() : mFeedList.size();
                    int count = mLoadMoreView.getViewCount();
                    for (int i = count - 1;
                         i < (count - 1 + LoadMoreView.LOAD_VIEW_SET_COUNT) && size > i; i++) {
                        if(mFeedList == null) {
                            DatabaseContants.getPhotoRef(mFeedList2.get(i))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        PhotoModel photoModel = dataSnapshot.getValue(PhotoModel.class);
                                        mLoadMoreView.addView(new PhotoView(mLoadMoreView.getContext(),
                                                photoModel, mUserId, mUserName, mLoadMoreView,
                                                canEditPhotoList));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    //DatabaseContants.logDatabaseError(TAG, databaseError);
                                }
                            });
                        } else {
                            mLoadMoreView.addView(new PhotoView(mLoadMoreView.getContext(),
                                    mFeedList.get(i), mUserId, mUserName, mLoadMoreView,
                                    false));
                        }

                        if (i == size - 1) {
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