package it.unimib.greenway.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.greenway.model.Result;
import it.unimib.greenway.model.StatusChallenge;
import it.unimib.greenway.model.User;

public interface IUserRepository {
    MutableLiveData<Result> getGoogleUser(String idToken, List<StatusChallenge> statusChallengeList);
    void signInWithGoogle(String token, List<StatusChallenge> statusChallengeList);
    MutableLiveData<Result> registerUser(String nome, String cognome, String email, String password, List<StatusChallenge> statusChallengeList);
    void signUp(String nome, String cognome, String email, String password, List<StatusChallenge> statusChallengeList);
    MutableLiveData<Result> loginUser(String email, String password);
    void login(String email, String password);

    MutableLiveData<Result> getUserData(String email, String password);
    void getUserCredential(String email, String password);
    MutableLiveData<Result> getUser(String idToken);
     User getLoggedUser();

     MutableLiveData<Result> updateCo2Saved(String idToken, String transportType, double co2Saved, double kmTravel, double co2Consumed);
    MutableLiveData<Result> updateCo2Car(String idToken, double co2Car);

    MutableLiveData<Result> logout();
    MutableLiveData<Result> changePassword(String token, String newPw, String oldPw);
    MutableLiveData<Result> changePhoto(String token, String imageBitmap);

    MutableLiveData<Result> getFriends(String idToken);
    MutableLiveData<Result> addFriend(String idToken, String friendId);
    MutableLiveData<Result> removeFriend(String idToken, String friendId);
    MutableLiveData<Result> getAllUsers(String idToken);
}
