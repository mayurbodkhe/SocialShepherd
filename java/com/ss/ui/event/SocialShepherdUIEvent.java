package com.ss.ui.event;

import java.util.Collection;
import com.ss.ui.domain.Transaction;
import com.ss.ui.view.SocialShepherdUIViewType;

/*
 * Event bus events used in Dashboard are listed here as inner classes.
 */
public abstract class SocialShepherdUIEvent {

    public static final class UserLoginRequestedEvent {
        private final String userName, password;

        public UserLoginRequestedEvent(final String userName,
                final String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }
    }

    public static class BrowserResizeEvent {

    }

    public static class UserLoggedOutEvent {

    }

    public static class NotificationsCountUpdatedEvent {
    }

    public static final class ReportsCountUpdatedEvent {
        private final int count;

        public ReportsCountUpdatedEvent(final int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }

    }

    public static final class TransactionReportEvent {
        private final Collection<Transaction> transactions;

        public TransactionReportEvent(final Collection<Transaction> transactions) {
            this.transactions = transactions;
        }

        public Collection<Transaction> getTransactions() {
            return transactions;
        }
    }

    public static final class PostViewChangeEvent {
        private final SocialShepherdUIViewType view;

        public PostViewChangeEvent(final SocialShepherdUIViewType view) {
            this.view = view;
        }

        public SocialShepherdUIViewType getView() {
            return view;
        }
    }

    public static class CloseOpenWindowsEvent {
    }

    public static class ProfileUpdatedEvent {
    }

}
