import java.util.Objects;

// Класс представляющий связь между пользователями
public class UserFriend {
    private int userId;
    private int friendId;

    // Конструктор для создания объекта UserFriend с заданными userId и friendId
    public UserFriend(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    // Геттеры и сеттеры для доступа к полям класса

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    // Переопределение метода toString для удобного вывода объекта в строковом
    // представлении
    @Override
    public String toString() {
        return "UserFriend{" +
                "userId=" + userId +
                ", friendId=" + friendId +
                '}';
    }

    // Переопределение методов equals и hashCode для корректного сравнения объектов
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserFriend that = (UserFriend) o;
        return userId == that.userId && friendId == that.friendId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }
}
