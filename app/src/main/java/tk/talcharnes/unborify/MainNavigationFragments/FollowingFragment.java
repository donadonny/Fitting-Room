package tk.talcharnes.unborify.MainNavigationFragments;

import android.icu.util.ValueIterator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.bakerj.infinitecards.AnimationTransformer;
import com.bakerj.infinitecards.CardItem;
import com.bakerj.infinitecards.InfiniteCardView;
import com.bakerj.infinitecards.ZIndexTransformer;
import com.bakerj.infinitecards.transformer.DefaultCommonTransformer;
import com.bakerj.infinitecards.transformer.DefaultTransformerToBack;
import com.bakerj.infinitecards.transformer.DefaultTransformerToFront;
import com.bakerj.infinitecards.transformer.DefaultZIndexTransformerCommon;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tk.talcharnes.unborify.MyTransformation;
import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 9/29/17.
 */

public class FollowingFragment extends Fragment {

    private static final String TAG = FollowingFragment.class.getSimpleName();

    private View rootView;
    private InfiniteCardView mCardView;
    private BaseAdapter mAdapter1;
    private boolean mIsAdapter1 = true;
    private int[] resId = {R.mipmap.ic_launcher, R.drawable.bg_circle};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_following, container, false);

        Log.d(TAG, "Load Following Fragment");

        mCardView = rootView.findViewById(R.id.view);
        mAdapter1 = new MyAdapter(resId);
        mCardView.setAdapter(mAdapter1);
        mCardView.setCardAnimationListener(new InfiniteCardView.CardAnimationListener() {
            @Override
            public void onAnimationStart() {
                //Toast.makeText(getActivity(), "Animation Start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd() {
                //Toast.makeText(getActivity(), "Animation End", Toast.LENGTH_SHORT).show();
            }
        });
        initButton();
        return rootView;
    }

    private void initButton() {
        rootView.findViewById(R.id.pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsAdapter1) {
                    setStyle2();
                    mCardView.bringCardToFront(mAdapter1.getCount() - 1);
                }
            }
        });
        rootView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsAdapter1) {
                    setStyle2();
                } else {
                    setStyle3();
                }
                mCardView.bringCardToFront(1);
            }
        });
    }

    private void setStyle1() {
        mCardView.setClickable(true);
        mCardView.setAnimType(InfiniteCardView.ANIM_TYPE_FRONT);
        mCardView.setAnimInterpolator(new LinearInterpolator());
        mCardView.setTransformerToFront(new DefaultTransformerToFront());
        mCardView.setTransformerToBack(new DefaultTransformerToBack());
        mCardView.setZIndexTransformerToBack(new DefaultZIndexTransformerCommon());
    }

    private void setStyle2() {
        mCardView.setClickable(true);
        mCardView.setAnimType(InfiniteCardView.ANIM_TYPE_SWITCH);
        mCardView.setAnimInterpolator(new OvershootInterpolator(-18));
        mCardView.setTransformerToFront(new DefaultTransformerToFront());
        mCardView.setTransformerToBack(new AnimationTransformer() {
            @Override
            public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                int positionCount = fromPosition - toPosition;
                float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);
                view.setScaleX(scale);
                view.setScaleY(scale);
                if (fraction < 0.5) {
                    view.setRotationX(180 * fraction);
                } else {
                    view.setRotationX(180 * (1 - fraction));
                }
            }

            @Override
            public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                int positionCount = fromPosition - toPosition;
                float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);
                view.setTranslationY(-cardHeight * (0.8f - scale) * 0.5f - cardWidth * (0.02f *
                        fromPosition - 0.02f * fraction * positionCount));
            }
        });
        mCardView.setZIndexTransformerToBack(new ZIndexTransformer() {
            @Override
            public void transformAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                if (fraction < 0.4f) {
                    card.zIndex = 1f + 0.01f * fromPosition;
                } else {
                    card.zIndex = 1f + 0.01f * toPosition;
                }
            }

            @Override
            public void transformInterpolatedAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

            }
        });
    }

    private void setStyle3() {
        mCardView.setClickable(false);
        mCardView.setAnimType(InfiniteCardView.ANIM_TYPE_FRONT_TO_LAST);
        mCardView.setAnimInterpolator(new OvershootInterpolator(-8));
        mCardView.setTransformerToFront(new DefaultCommonTransformer());
        mCardView.setTransformerToBack(new AnimationTransformer() {
            @Override
            public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                int positionCount = fromPosition - toPosition;
                float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);
                view.setScaleX(scale);
                view.setScaleY(scale);
                if (fraction < 0.5) {
                    view.setTranslationX(cardWidth * fraction * 1.5f);
                    view.setRotationY(-45 * fraction);
                } else {
                    view.setTranslationX(cardWidth * 1.5f * (1f - fraction));
                    view.setRotationY(-45 * (1 - fraction));
                }
            }

            @Override
            public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                int positionCount = fromPosition - toPosition;
                float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);
                view.setTranslationY(-cardHeight * (0.8f - scale) * 0.5f - cardWidth * (0.02f *
                        fromPosition - 0.02f * fraction * positionCount));
            }
        });
        mCardView.setZIndexTransformerToBack(new ZIndexTransformer() {
            @Override
            public void transformAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                if (fraction < 0.5f) {
                    card.zIndex = 1f + 0.01f * fromPosition;
                } else {
                    card.zIndex = 1f + 0.01f * toPosition;
                }
            }

            @Override
            public void transformInterpolatedAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

            }
        });
    }

    private static class MyAdapter extends BaseAdapter {
        private int[] resIds = {};

        MyAdapter(int[] resIds) {
            this.resIds = resIds;
        }

        @Override
        public int getCount() {
            return resIds.length;
        }

        @Override
        public Integer getItem(int position) {
            return resIds[0];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .item_card, parent, false);
            }
            convertView.setBackgroundResource(resIds[position]);
            return convertView;
        }
    }
}