/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.rangi.nanodet.fragment.trending;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.rangi.nanodet.R;
import com.rangi.nanodet.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.rangi.nanodet.adapter.base.delegate.SimpleDelegateAdapter;
import com.rangi.nanodet.adapter.entity.NewInfo;
import com.rangi.nanodet.core.BaseFragment;
import com.rangi.nanodet.databinding.FragmentTrendingBinding;
import com.rangi.nanodet.utils.DemoDataProvider;
import com.rangi.nanodet.utils.Utils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import me.samlss.broccoli.Broccoli;

/**
 * @author xuexiang
 * @since 2019-10-30 00:19
 */
@Page(anim = CoreAnim.none)
public class TrendingFragment extends BaseFragment<FragmentTrendingBinding> {

    private SimpleDelegateAdapter<NewInfo> mNewsAdapter;

    @NonNull
    @Override
    protected FragmentTrendingBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentTrendingBinding.inflate(inflater, container, false);
    }

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        binding.recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        //学习
        mNewsAdapter = new BroccoliSimpleDelegateAdapter<NewInfo>(R.layout.adapter_news_linkman_view_list_item, new LinearLayoutHelper(), DemoDataProvider.getEmptyNewInfo()) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, NewInfo model, int position) {
                if (model != null) {
//                    holder.text(R.id.tv_user_name, model.getUserName());
                    holder.text(R.id.linkname, model.getTitle());
//                    holder.text(R.id.tv_summary, model.getSummary());
                    holder.image(R.id.link_image, model.getImageId());
//                    holder.click(R.id.link_view, v -> Utils.goWeb(getContext(), model.getDetailUrl()));
                }

            }

            @Override
            protected void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.linkname),
                        holder.findView(R.id.link_image)
                );
            }

        };
        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
        delegateAdapter.addAdapter(mNewsAdapter);

        binding.recyclerView.setAdapter(delegateAdapter);
        mNewsAdapter.refresh(DemoDataProvider.getDemoTrendingInfos());
    }
//    @Override
//    protected void initListeners() {
//        //下拉刷新
//        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
//            // TODO: 2020-02-25 这里只是模拟了网络请求
//            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.refresh(DemoDataProvider.getDemoTrendingInfos());
//                refreshLayout.finishRefresh();
//            }, 1000);
//        });
//        //上拉加载
//        binding.refreshLayout.setOnLoadMoreListener(refreshLayout -> {
//            // TODO: 2020-02-25 这里只是模拟了网络请求
//            refreshLayout.getLayout().postDelayed(() -> {
//                mNewsAdapter.loadMore(DemoDataProvider.getDemoTrendingInfos());
//                refreshLayout.finishLoadMore();
//            }, 1000);
//        });
//        binding.refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
//    }
}
