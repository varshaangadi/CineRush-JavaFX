package src.model;

public class Booking {

    private String movieTitle;
    private String showTime;
    private String seatNo;
    private String username;
    private double totalAmount;

    public Booking(
            String movieTitle,
            String showTime,
            String seatNo,
            String username,
            double totalAmount) {

        this.movieTitle = movieTitle;
        this.showTime = showTime;
        this.seatNo = seatNo;
        this.username = username;
        this.totalAmount = totalAmount;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getShowTime() {
        return showTime;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public String getUsername() {
        return username;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
