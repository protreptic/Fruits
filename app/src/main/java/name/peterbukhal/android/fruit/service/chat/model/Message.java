package name.peterbukhal.android.fruit.service.chat.model;

import java.util.UUID;

public final class Message {

    private String id;
    private long createdAt;
    private int status;
    private String author;
    private String message;

    public Message(String author, String message) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.status = 0;
        this.author = author;
        this.message = message;
    }

    public String getAuthor() {
        return author.substring(0, 8);
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", status=" + status +
                ", author='" + author + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        return createdAt == message.createdAt;

    }

    @Override
    public int hashCode() {
        return (int) (createdAt ^ (createdAt >>> 32));
    }
}
