package com.example.printexpress.ui.profile;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.example.printexpress.R;
import com.example.printexpress.data.model.AppNotification;
import com.example.printexpress.databinding.ItemNotificationBinding;
import com.example.printexpress.ui.base.BaseListActivity;
import com.example.printexpress.util.Formatter;
import com.example.printexpress.util.IconMapper;

/** All alerts: order updates, promotions and support replies. */
public class NotificationsActivity extends BaseListActivity<AppNotification> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b.toolbar.inflateMenu(R.menu.menu_notifications);
        b.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_read_all) {
                request(() -> { repo.notifications.markAllRead(userId()); return true; }, r -> {
                    for (AppNotification n : adapter.getItems()) n.setRead(true);
                    adapter.notifyDataSetChanged();
                });
                return true;
            }
            return false;
        });
    }

    @Override
    protected String getScreenTitle() {
        return getString(R.string.notifications);
    }

    @Override
    protected int getItemLayout() {
        return R.layout.item_notification;
    }

    @Override
    protected void bindItem(View itemView, AppNotification n, int position) {
        ItemNotificationBinding ib = ItemNotificationBinding.bind(itemView);
        ib.imageType.setImageResource(IconMapper.notificationIcon(n.getType()));
        ib.textTitle.setText(n.getTitle());
        ib.textTitle.setTypeface(null, n.isRead() ? Typeface.NORMAL : Typeface.BOLD);
        ib.textMessage.setText(n.getMessage());
        ib.textTime.setText(Formatter.dateTime(n.getCreatedAt()));
        ib.dotUnread.setVisibility(n.isRead() ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    protected void onItemClick(AppNotification n, int position) {
        if (n.isRead()) return;
        request(() -> { repo.notifications.markRead(userId(), n.getNotificationId()); return true; }, r -> {
            n.setRead(true);
            adapter.notifyItemChanged(position);
        });
    }

    @Override
    protected void loadItems() {
        request(() -> repo.notifications.getNotifications(userId()), false, this::showItems, this::showError);
    }

    @Override
    protected int getEmptyIcon() {
        return R.drawable.ic_bell;
    }

    @Override
    protected String getEmptyTitle() {
        return "You're all caught up";
    }

    @Override
    protected String getEmptyMessage() {
        return "Order updates and offers will appear here";
    }
}
