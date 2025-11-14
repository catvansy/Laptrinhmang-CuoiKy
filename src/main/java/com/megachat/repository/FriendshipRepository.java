package com.megachat.repository;

import com.megachat.model.Friendship;
import com.megachat.model.FriendshipStatus;
import com.megachat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findByRequesterAndStatus(User requester, FriendshipStatus status);

    List<Friendship> findByReceiverAndStatus(User receiver, FriendshipStatus status);

    Optional<Friendship> findByIdAndReceiver(Long id, User receiver);

    @Query("SELECT COUNT(f) > 0 FROM Friendship f " +
           "WHERE ((f.requester = :user1 AND f.receiver = :user2) " +
           "OR (f.requester = :user2 AND f.receiver = :user1)) " +
           "AND f.status IN :statuses")
    boolean existsActiveFriendship(
        @Param("user1") User user1,
        @Param("user2") User user2,
        @Param("statuses") Collection<FriendshipStatus> statuses
    );

    @Query("SELECT f FROM Friendship f " +
           "WHERE (f.requester = :user OR f.receiver = :user) " +
           "AND f.status = com.megachat.model.FriendshipStatus.ACCEPTED")
    List<Friendship> findAcceptedFriendships(@Param("user") User user);

    @Query("SELECT COUNT(f) > 0 FROM Friendship f " +
           "WHERE ((f.requester = :user1 AND f.receiver = :user2) " +
           "OR (f.requester = :user2 AND f.receiver = :user1)) " +
           "AND f.status = com.megachat.model.FriendshipStatus.ACCEPTED")
    boolean existsAcceptedFriendship(
        @Param("user1") User user1,
        @Param("user2") User user2
    );
}


