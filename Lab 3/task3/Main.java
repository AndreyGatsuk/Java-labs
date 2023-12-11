import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Main {
    private static final int MAX_COUNT_USER_FRIENDS = 100;
    private static final int MIN_USER_ID = 0;
    private static final int MAX_USER_ID = 9;

    private static ArrayList<UserFriend> userFriends = new ArrayList<>();

    public static void main(String[] args) {
        initUserFriends();

        // Получение потока userId
        getObservableUserId()
                // Преобразование userId в поток UserFriend через функцию getFriends
                .flatMap(userId -> getFriends(userId))
                // Подписка на поток UserFriend и вывод результатов на консоль
                .subscribe(userFriend -> System.out.println("Received: " + userFriend));
    }

    // Получение потока userId
    private static Observable<Integer> getObservableUserId() {
        // Использование множества для уникальных userId
        Set<Integer> userIdsSet = new HashSet<>();

        // Генерация 10 случайных userId
        for (int i = 0; i < 10; i++) {
            userIdsSet.add(getRandomUserId());
        }

        // Преобразование множества в массив и создание Observable из массива
        return Observable.fromArray(userIdsSet.toArray(Integer[]::new));
    }

    // Получение потока UserFriend по заданному userId
    private static Observable<UserFriend> getFriends(int userId) {
        // Фильтрация userFriends по userId и преобразование в Observable
        return Observable.fromIterable(userFriends)
                .filter(userFriend -> userFriend.getUserId() == userId);
    }

    // Инициализация массива userFriends случайными данными
    private static void initUserFriends() {
        // Использование множества для уникальных UserFriend
        Set<UserFriend> userFriendSet = new HashSet<>();

        // Генерация случайных данных для UserFriend
        for (int i = 0; i < MAX_COUNT_USER_FRIENDS; i++) {
            int newUserId = getRandomUserId();
            int newFriendId = getRandomUserId();

            // Проверка на совпадение userId и friendId
            if (newFriendId != newUserId) {
                userFriendSet.add(new UserFriend(newUserId, newFriendId));
            }
        }

        // Преобразование множества в список для дальнейшего использования
        userFriends = new ArrayList<>(userFriendSet);
    }

    // Генерация случайного userId
    private static int getRandomUserId() {
        return getRandomNumber(MIN_USER_ID, MAX_USER_ID);
    }

    // Генерация случайного числа в заданном диапазоне
    private static int getRandomNumber(int min, int max) {
        return (int) Math.round(Math.random() * (max - min) + min);
    }
}
