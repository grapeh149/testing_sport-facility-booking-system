package group6.it.ou.sportfacilitybooking.dto;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String timestamp;
    private Object error;

    public ApiResponse() {}

    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    // Getters & Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Object getError() { return error; }
    public void setError(Object error) { this.error = error; }
}
