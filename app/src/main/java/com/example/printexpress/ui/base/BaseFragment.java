package com.example.printexpress.ui.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.printexpress.data.Async;
import com.example.printexpress.data.SessionManager;
import com.example.printexpress.data.repository.Repositories;

/** Parent of the tab fragments (Home, Browse, Orders, Profile). */
public abstract class BaseFragment extends Fragment {

    private Repositories repositories;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        repositories = Repositories.get(context);
    }

    /** Safe to use inside background tasks (does not need the screen). */
    protected Repositories repo() {
        return repositories;
    }

    protected SessionManager session() {
        return new SessionManager(requireContext());
    }

    protected long userId() {
        return session().getUserId();
    }

    /** Runs database work in the background; ignores the answer if the user already left this tab. */
    protected <T> void request(Async.Task<T> task, Async.OnSuccess<T> onSuccess, Async.OnError onError) {
        Async.run(task,
                result -> {
                    if (isAdded() && getView() != null) onSuccess.onSuccess(result);
                },
                message -> {
                    if (isAdded() && getView() != null) onError.onError(message);
                });
    }

    protected void showMessage(String message) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showMessage(message);
        }
    }
}
