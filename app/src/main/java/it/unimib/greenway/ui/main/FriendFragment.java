package it.unimib.greenway.ui.main;

import static it.unimib.greenway.util.Constants.ERROR_RETRIEVING_CHALLENGE;
import static it.unimib.greenway.util.Constants.ERROR_RETRIEVING_USER_INFO;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.greenway.R;
import it.unimib.greenway.adapter.ChallengeRecyclerViewAdapter;
import it.unimib.greenway.data.repository.challenge.IChallengeRepositoryWithLiveData;
import it.unimib.greenway.data.repository.user.IUserRepository;
import it.unimib.greenway.model.Challenge;
import it.unimib.greenway.model.Result;
import it.unimib.greenway.model.User;
import it.unimib.greenway.ui.UserViewModel;
import it.unimib.greenway.ui.UserViewModelFactory;
import it.unimib.greenway.util.ServiceLocator;


public class FriendFragment extends Fragment {

    private List<Challenge> challengeList;
    private RecyclerView recyclerViewChallenge;
    private ChallengeRecyclerViewAdapter challengeRecyclerViewAdapter;
    private ChallengeViewModel challengeViewModel;
    private UserViewModel userViewModel;
    private RecyclerView.LayoutManager layoutManager;

    private User friend;

    private ImageButton backButton;


    public FriendFragment() {
        // Required empty public constructor
    }

    public static FriendFragment newInstance(String param1, String param2) {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        challengeList = new ArrayList<>();

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                this,
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        IChallengeRepositoryWithLiveData challengeRepositoryWithLiveData =
                ServiceLocator.getInstance().getChallengeRepository(
                        requireActivity().getApplication()
                );
        challengeViewModel = new ViewModelProvider(this, new ChallengeViewModelFactory(challengeRepositoryWithLiveData)).get(ChallengeViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        backButton = requireActivity().findViewById(R.id.backButton);
        backButton.setVisibility(View.VISIBLE);


        Bundle bundle = getArguments();
        if (bundle != null) {
            friend = bundle.getParcelable("friend");
        }

        return inflater.inflate(R.layout.fragment_friend, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewChallenge = view.findViewById(R.id.recyclerViewFriendChallenge);
        layoutManager = new LinearLayoutManager(requireActivity());
    }

    @Override
    public void onResume() {
        super.onResume();

        userViewModel.getUserDataMutableLiveData(friend.getUserId()).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccessUser()) {
                User user = ((Result.UserResponseSuccess) result).getData();


                challengeRecyclerViewAdapter = new ChallengeRecyclerViewAdapter(challengeList,
                        requireActivity().getApplication(), user);
                recyclerViewChallenge.setAdapter(challengeRecyclerViewAdapter);
                recyclerViewChallenge.setLayoutManager(layoutManager);
                challengeViewModel.getChallengeMutableLiveData().observe(getViewLifecycleOwner(),
                        result2 -> {
                            if (result2.isSuccessChallenge()) {
                                this.challengeList.clear();
                                this.challengeList.addAll(((Result.ChallengeResponseSuccess) result2).getData().getChallenges());
                                challengeRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) result2).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });


            } else if (result.isError()) {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        getErrorMessage(((Result.Error) result).getMessage()),
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case ERROR_RETRIEVING_CHALLENGE:
                return requireActivity().getString(R.string.error_retrieving_challenge);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }
}