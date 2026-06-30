//package src;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.*;
import java.io.*;

import src.database.MovieDAO;
import src.model.Movie;

import src.database.BookingDAO;

import src.database.UserDAO;
import src.model.User;

import src.api.TMDBService;

import java.awt.Desktop;
import java.net.URI;

public class MovieTicketBooking extends Application {

    // ── Movie Data ─────────────────────────────────────────────────────────

    // ── All Movies (Now Showing + filtered sets) ───────────────────────────
    static ArrayList<Movie> ALL_MOVIES = MovieDAO.getAllMovies();

    // ── Seat Config ────────────────────────────────────────────────────────
    static final String[] ROWS = { "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M" };
    static final String[] SEAT_TYPES = { "PREMIUM", "PREMIUM", "PREMIUM", "PREMIUM", "ACCESSIBLE", "STANDARD",
            "STANDARD", "STANDARD", "STANDARD", "VALUE", "VALUE", "VALUE" };
    static final int[] PRICES = { 350, 350, 350, 350, 300, 220, 220, 220, 220, 150, 150, 150 };
    static final int COLS = 14;

    // ── State ──────────────────────────────────────────────────────────────
    Map<String, Button> seatButtons = new HashMap<>();
    Set<String> bookedSeats = new HashSet<>();
    Set<String> selectedSeats = new LinkedHashSet<>();
    Movie currentMovie = null;
    String currentTime = "";
    String activeFilter = "All"; // track active language filter
    String searchText = "";
    Stage primaryStage;
    // Map<String, String> users = new HashMap<>();
    // String loggedInUser = "";
    User loggedInUser = null;

    // final String USER_FILE = "users.txt";
    // final String BOOKING_FILE = "bookings.txt";

    // ══════════════════════════════════════════════════════════════════════
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("🎬 CineRush — Movie Ticket Booking");
        // loadUsers();
        showLoginScreen();
        stage.show();
    }

    // ══════════════════════════════════════════════════════════════════════
    // HOME SCREEN
    // ══════════════════════════════════════════════════════════════════════
    void showHomeScreen() {
        // Header — only Movies tab
        Label logo = new Label("🎬 CineRush");
        logo.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 26));
        logo.setTextFill(Color.web("#f59e0b"));

        Label moviesTab = new Label("Movies");
        Button myBookingsBtn = styledBtn(
                "My Bookings",
                "#22d3ee",
                "#000000");

        Button logoutBtn = styledBtn(
                "Logout",
                "#ef4444",
                "#ffffff");

        Button adminBtn = styledBtn(
                "Admin Panel",
                "#f59e0b",
                "#000000");

        adminBtn.setOnAction(e -> {
            showAdminPanel();
        });
        if (!loggedInUser.getRole().equals("ADMIN")) {

            adminBtn.setVisible(false);

            adminBtn.setManaged(false);
        }

        logoutBtn.setOnAction(e -> {

            loggedInUser = null;

            showLoginScreen();
        });

        myBookingsBtn.setOnAction(e -> {
            showMyBookingsScreen();
        });
        moviesTab.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        moviesTab.setTextFill(Color.WHITE);
        moviesTab.setStyle("-fx-border-color: #f59e0b; -fx-border-width: 0 0 2 0; -fx-padding: 0 0 4 0;");

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(
                30,
                logo,
                moviesTab,
                spacer,
                myBookingsBtn,
                adminBtn,
                logoutBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #333333; -fx-border-width: 0 0 1 0;");

        // Filter row — now functional
        String[] filterLabels = { "All", "English", "Hindi", "Tamil", "Malayalam", "New Releases" };
        HBox filterRow = new HBox(10);
        filterRow.setPadding(new Insets(10, 20, 10, 20));
        filterRow.setStyle("-fx-background-color: #111111;");
        filterRow.setAlignment(Pos.CENTER_LEFT);

        // Movie Grid (will be rebuilt on filter change)
        FlowPane movieGrid = new FlowPane();
        movieGrid.setHgap(24);
        movieGrid.setVgap(28);

        movieGrid.setPadding(new Insets(0, 24, 24, 24));
        movieGrid.setStyle("-fx-background-color: #111111;");

        Label browseLabel = new Label("Now Showing");
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search movies...");
        searchField.setPrefWidth(250);

        searchField.setStyle(

                "-fx-background-color: #1a1a2e;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #777777;" +
                        "-fx-background-radius: 14;" +
                        "-fx-font-size: 14;" +
                        "-fx-padding: 12;");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            searchText = newVal.toLowerCase();
            rebuildMovieGrid(movieGrid, activeFilter, browseLabel);
        });
        browseLabel.setFont(Font.font("Arial Black", FontWeight.BOLD, 18));
        browseLabel.setTextFill(Color.WHITE);
        browseLabel.setPadding(new Insets(16, 24, 8, 24));

        // Populate grid based on active filter
        rebuildMovieGrid(movieGrid, activeFilter, browseLabel);

        // Build filter chips with click handlers
        for (String f : filterLabels) {
            Label chip = new Label(f.equals("All") ? "⚙ All ▾" : f);
            boolean isActive = f.equals(activeFilter) || (f.equals("All") && activeFilter.equals("All"));
            if (isActive) {
                chip.setStyle(
                        "-fx-background-color: #f59e0b; -fx-text-fill: #000000; -fx-border-color: #f59e0b; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14 5 14; -fx-font-size: 12; -fx-font-weight: bold; -fx-cursor: hand;");
            } else {
                chip.setStyle(
                        "-fx-background-color: #1a1a2e; -fx-text-fill: #cccccc; -fx-border-color: #444444; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14 5 14; -fx-font-size: 12; -fx-cursor: hand;");
            }
            chip.setOnMouseClicked(e -> {
                String selected = f.equals("All") ? "All" : f;
                activeFilter = selected;
                // Rebuild grid
                rebuildMovieGrid(movieGrid, activeFilter, browseLabel);
                // Update chip styles
                for (javafx.scene.Node node : filterRow.getChildren()) {
                    if (node instanceof Label) {
                        Label c = (Label) node;
                        String cText = c.getText().replace("⚙ ", "").replace(" ▾", "");
                        boolean active = cText.equals(selected) || (cText.equals("All") && selected.equals("All"));
                        if (active) {
                            c.setStyle(
                                    "-fx-background-color: #f59e0b; -fx-text-fill: #000000; -fx-border-color: #f59e0b; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14 5 14; -fx-font-size: 12; -fx-font-weight: bold; -fx-cursor: hand;");
                        } else {
                            c.setStyle(
                                    "-fx-background-color: #1a1a2e; -fx-text-fill: #cccccc; -fx-border-color: #444444; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14 5 14; -fx-font-size: 12; -fx-cursor: hand;");
                        }
                    }
                }
            });
            filterRow.getChildren().add(chip);
        }

        ScrollPane scrollPane = new ScrollPane(movieGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #111111; -fx-background-color: #111111; -fx-border-color: transparent;");

        VBox root = new VBox(header, filterRow, browseLabel, searchField, scrollPane);
        root.setStyle("-fx-background-color: #111111;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 900, 620);
        primaryStage.setScene(scene);
    }

    /** Rebuild movie grid based on filter */
    void rebuildMovieGrid(FlowPane grid, String filter, Label browseLabel) {
        grid.getChildren().clear();
        String label = "Now Showing";
        if (!filter.equals("All") && !filter.equals("New Releases")) {
            label = filter + " Movies";
        } else if (filter.equals("New Releases")) {
            label = "New Releases";
        }
        browseLabel.setText(label);

        for (Movie m : ALL_MOVIES) {
            boolean show = false;

            if (filter.equals("All")) {
                show = true;
            } else if (filter.equals("New Releases")) {
                show = m.isNewRelease();
            } else {
                if (m.getLanguage()
                        .toLowerCase()
                        .contains(filter.toLowerCase())) {
                    show = true;
                }
            }
            boolean matchesSearch = m.getTitle().toLowerCase().contains(searchText);

            if (show && matchesSearch) {
                grid.getChildren().add(createMovieCard(m));
            }
        }
    }

    VBox createMovieCard(Movie m) {
        ImageView img = new ImageView();
        img.setFitWidth(210);

        img.setFitHeight(300);
        img.setPreserveRatio(false);
        img.setSmooth(true);
        img.setCache(true);

        Rectangle clip = new Rectangle(
                210,
                300);

        clip.setArcWidth(20);
        clip.setArcHeight(20);
        img.setClip(clip);

        try {
            if (m.getPosterUrl() != null &&
                    !m.getPosterUrl().isEmpty()) {

                img.setImage(
                        new Image(
                                m.getPosterUrl(),
                                210,
                                300,
                                false,
                                true,
                                true));
            }
        } catch (Exception e) {
            img.setImage(
                    new Image(
                            "C:/Users/Mahesh Angadi/Downloads/coursera/COLLEGE/oosd/MovieTicketBookings/images/poster.jpg",
                            210, 300, false, true, true));
        }
        img.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 4);");

        Label genreTag = new Label(m.getGenre());
        genreTag.setStyle("-fx-background-color: " + "#f59e0b"
                + "; -fx-text-fill: white; -fx-font-size: 10; -fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 20;");

        StackPane thumbPane = new StackPane(img, genreTag);
        StackPane.setAlignment(genreTag, Pos.BOTTOM_LEFT);
        StackPane.setMargin(genreTag, new Insets(0, 0, 8, 8));
        thumbPane.setPrefSize(210, 300);
        thumbPane.setStyle("-fx-background-color: #2a2a2a;");

        Label titleLbl = new Label(m.getTitle());
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(190);
        titleLbl.setMinHeight(55);

        Label metaLbl = new Label(m.getRating() + " | " + m.getLanguage().split(",")[0]);
        metaLbl.setTextFill(Color.web("#888888"));
        metaLbl.setFont(Font.font("Arial", 11));

        VBox info = new VBox(6, titleLbl, metaLbl);
        info.setPadding(new Insets(10, 12, 12, 12));

        VBox card = new VBox(thumbPane, info);
        card.setStyle(

                "-fx-background-color: #141414;" +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;" +
                        "-fx-border-color: #2a2a2a;" +
                        "-fx-border-width: 1;");
        card.setPrefWidth(210);

        card.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
            st.setToX(1.04);
            st.setToY(1.04);
            st.play();
            card.setStyle(
                    "-fx-background-color: #1b1b1b;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-radius: 18;" +
                            "-fx-border-color: #f59e0b;" +
                            "-fx-border-width: 2;");
        });

        card.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
            card.setStyle(
                    "-fx-background-color: #141414;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-radius: 18;" +
                            "-fx-border-color: #2a2a2a;" +
                            "-fx-border-width: 1;");
        });
        card.setOnMouseClicked(e -> showMovieDetail(m));

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    // MOVIE DETAIL SCREEN
    // ══════════════════════════════════════════════════════════════════════
    void showMovieDetail(Movie m) {
        currentMovie = m;

        Button backBtn = styledBtn("← Back", "#444444", "#ffffff");
        backBtn.setOnAction(e -> showHomeScreen());

        ImageView poster = new ImageView();
        poster.setFitWidth(240);
        poster.setFitHeight(340);
        poster.setSmooth(true);
        poster.setCache(true);
        Rectangle posterClip = new Rectangle(
                240,
                340);
        posterClip.setArcWidth(24);
        posterClip.setArcHeight(24);
        poster.setClip(posterClip);
        poster.setPreserveRatio(false);
        try {

            if (m.getPosterUrl() != null &&
                    !m.getPosterUrl().isEmpty()) {

                poster.setImage(
                        new Image(
                                m.getPosterUrl(),
                                240,
                                340,
                                false,
                                true,
                                true));
            }

        } catch (Exception e) {

            poster.setImage(
                    new Image(
                            "C:/Users/Mahesh Angadi/Downloads/coursera/COLLEGE/oosd/MovieTicketBookings/images/poster.jpg",
                            240, 340, false, true, true));
        }
        poster.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 20, 0, 0, 8);");

        Label genrePill = new Label("  " + m.getGenre() + "  ");
        genrePill.setStyle("-fx-background-color: " + "#f59e0b"
                + "; -fx-text-fill: white; -fx-font-size: 11; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 4 12 4 12;");

        Label titleLbl = new Label(m.getTitle());
        titleLbl.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 34));
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(500);

        Label subLbl = new Label(m.getRating() + "  |  " + m.getLanguage() + "  |  " + m.getDuration());
        subLbl.setTextFill(Color.web("#aaaaaa"));
        subLbl.setFont(Font.font("Arial", 13));

        Label descLbl = new Label(m.getDescription());
        descLbl.setTextFill(Color.web("#cccccc"));
        descLbl.setFont(Font.font("Arial", 13));
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(560);

        Label ratingLbl = new Label("★★★★☆  8.2 / 10");
        ratingLbl.setStyle("-fx-text-fill: " + "#f59e0b" + "; -fx-font-size: 16; -fx-font-weight: bold;");

        Button trailerBtn = styledBtn(
                "▶ Watch Trailer",
                "#e11d48",
                "#ffffff");

        trailerBtn.setOnAction(e -> {

            try {

                String searchUrl =

                        "https://www.youtube.com/results?search_query=" +

                                m.getTitle().replace(" ", "+") +

                                "+official+trailer";

                Desktop.getDesktop().browse(

                        new URI(searchUrl));

            } catch (Exception ex) {

                ex.printStackTrace();
            }
        });

        VBox metaBox = new VBox(10, genrePill, titleLbl, subLbl, descLbl, ratingLbl, trailerBtn);
        metaBox.setAlignment(Pos.TOP_LEFT);

        HBox heroBox = new HBox(48, poster, metaBox);
        heroBox.setAlignment(Pos.CENTER_LEFT);
        heroBox.setPadding(new Insets(20, 28, 24, 28));
        heroBox.setStyle("-fx-background-color: linear-gradient(to bottom, " + "#f59e0b" + "88, #0a0a0a);");

        VBox headerArea = new VBox(0);
        HBox topBar = new HBox(backBtn);
        topBar.setPadding(new Insets(12, 28, 0, 28));
        topBar.setStyle("-fx-background-color: " + "#f59e0b" + "88;");
        headerArea.getChildren().addAll(topBar, heroBox);

        Label showLabel = new Label("🕐  Select Showtime");
        showLabel.setFont(Font.font("Arial Black", FontWeight.BOLD, 16));
        showLabel.setTextFill(Color.WHITE);

        HBox timeBox = new HBox(12);
        for (String t : new String[] { "10:00 AM", "1:30 PM", "4:45 PM", "8:00 PM", "11:30 PM" }) {
            Button tb = new Button(t);
            tb.setStyle(
                    "-fx-background-color: #1a1a2e; -fx-text-fill: #22d3ee; -fx-border-color: #22d3ee; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 18 10 18; -fx-font-weight: bold; -fx-font-size: 13; -fx-cursor: hand;");
            tb.setOnMouseEntered(e -> tb.setStyle(
                    "-fx-background-color: #22d3ee; -fx-text-fill: #000000; -fx-border-color: #22d3ee; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 18 10 18; -fx-font-weight: bold; -fx-font-size: 13; -fx-cursor: hand;"));
            tb.setOnMouseExited(e -> tb.setStyle(
                    "-fx-background-color: #1a1a2e; -fx-text-fill: #22d3ee; -fx-border-color: #22d3ee; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 18 10 18; -fx-font-weight: bold; -fx-font-size: 13; -fx-cursor: hand;"));
            tb.setOnAction(e -> {
                currentTime = t;
                showSeatScreen();
            });
            timeBox.getChildren().add(tb);
        }

        VBox showSection = new VBox(12, showLabel, timeBox);
        showSection.setPadding(new Insets(20, 28, 16, 28));
        showSection.setStyle("-fx-background-color: #0d0d0d;");

        Label priceLabel = new Label("💺  Seat Prices");
        priceLabel.setFont(Font.font("Arial Black", FontWeight.BOLD, 16));
        priceLabel.setTextFill(Color.WHITE);

        HBox priceBox = new HBox(16);
        String[][] priceInfo = { { "#22d3ee", "PREMIUM", "₹350" }, { "#a855f7", "STANDARD", "₹220" },
                { "#22c55e", "VALUE", "₹150" }, { "#a78bfa", "ACCESSIBLE", "₹300" } };
        for (String[] p : priceInfo) {
            Rectangle sq = new Rectangle(22, 22, Color.web(p[0]));
            sq.setArcWidth(5);
            sq.setArcHeight(5);
            Label typeLbl = new Label(p[1]);
            typeLbl.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11; -fx-font-weight: bold;");
            Label amtLbl = new Label(p[2]);
            amtLbl.setStyle("-fx-text-fill: " + p[0] + "; -fx-font-size: 18; -fx-font-weight: bold;");
            VBox priceMeta = new VBox(2, typeLbl, amtLbl);
            HBox pCard = new HBox(10, sq, priceMeta);
            pCard.setAlignment(Pos.CENTER_LEFT);
            pCard.setStyle(
                    "-fx-background-color: #1a1a1a; -fx-border-color: #333333; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12 16 12 16;");
            priceBox.getChildren().add(pCard);
        }

        VBox priceSection = new VBox(12, priceLabel, priceBox);
        priceSection.setPadding(new Insets(16, 28, 28, 28));
        priceSection.setStyle("-fx-background-color: #0d0d0d;");

        VBox root = new VBox(headerArea, showSection, priceSection);
        root.setStyle("-fx-background-color: #0d0d0d;");

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0d0d0d; -fx-background-color: #0d0d0d;");

        Scene scene = new Scene(sp, 900, 620);
        primaryStage.setScene(scene);
    }

    // ══════════════════════════════════════════════════════════════════════
    // SEAT SELECTION SCREENs
    // ══════════════════════════════════════════════════════════════════════
    void showSeatScreen() {
        seatButtons.clear();
        selectedSeats.clear();

        // bookedSeats.clear();

        bookedSeats = new HashSet<>(
                BookingDAO.getBookedSeats(
                        currentMovie.getTitle(),
                        currentTime));

        Button backBtn = styledBtn("← Back", "#333333", "#ffffff");
        backBtn.setOnAction(e -> showMovieDetail(currentMovie));

        Label movieLbl = new Label(currentMovie.getTitle());
        movieLbl.setFont(Font.font("Arial Black", FontWeight.BOLD, 15));
        movieLbl.setTextFill(Color.WHITE);
        Label subLbl = new Label("🕐 " + currentTime + "   |   Screen 5");
        subLbl.setTextFill(Color.web("#888888"));
        subLbl.setFont(Font.font("Arial", 12));
        VBox movieInfo = new VBox(3, movieLbl, subLbl);

        HBox topBar = new HBox(16, backBtn, movieInfo);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(14, 24, 14, 24));
        topBar.setStyle("-fx-background-color: #111111; -fx-border-color: #222222; -fx-border-width: 0 0 1 0;");

        Label screenLbl = new Label("— — —  S C R E E N  — — —");
        screenLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        screenLbl.setTextFill(Color.web("#22d3ee"));
        Rectangle glow = new Rectangle(600, 5, Color.web("#22d3ee"));
        glow.setArcWidth(5);
        glow.setArcHeight(5);
        glow.setEffect(new Glow(0.9));
        VBox screenArea = new VBox(4, screenLbl, glow);
        screenArea.setAlignment(Pos.CENTER);
        screenArea.setPadding(new Insets(16, 0, 12, 0));

        GridPane seatGrid = new GridPane();
        seatGrid.setHgap(6);
        seatGrid.setVgap(6);
        seatGrid.setAlignment(Pos.CENTER);
        seatGrid.setPadding(new Insets(0, 24, 16, 24));

        Label totalLbl = new Label("Total: ₹0");
        Label seatsLbl = new Label("No seats selected");

        for (int r = 0; r < ROWS.length; r++) {
            String row = ROWS[r];
            String type = SEAT_TYPES[r];
            int price = PRICES[r];
            boolean isAccess = type.equals("ACCESSIBLE");

            Label rowLblL = new Label(row);
            rowLblL.setStyle("-fx-text-fill: #666666; -fx-font-size: 11; -fx-font-weight: bold;");
            rowLblL.setMinWidth(18);
            rowLblL.setAlignment(Pos.CENTER_RIGHT);
            seatGrid.add(rowLblL, 0, r);

            for (int c = 1; c <= COLS; c++) {
                String seatId = row + c;
                boolean isBooked = bookedSeats.contains(seatId);
                String[] colors = getSeatColors(type);

                Button seatBtn = new Button(isAccess ? "♿" : "");
                seatBtn.setPrefSize(30, 26);
                seatBtn.setFont(Font.font("Arial", 9));
                seatBtn.setStyle(seatStyle(isBooked ? colors[2] : colors[0], isBooked));

                if (!isBooked) {
                    seatBtn.setOnAction(e -> {
                        if (selectedSeats.contains(seatId)) {
                            selectedSeats.remove(seatId);
                            seatBtn.setStyle(seatStyle(colors[0], false));
                        } else {
                            selectedSeats.add(seatId);
                            seatBtn.setStyle(seatStyle(colors[1], false));
                        }
                        updateBookingBar(totalLbl, seatsLbl);
                    });
                    seatBtn.setOnMouseEntered(e -> {
                        if (!selectedSeats.contains(seatId))
                            seatBtn.setStyle(seatStyle("#ffffff", false));
                    });
                    seatBtn.setOnMouseExited(e -> {
                        if (!selectedSeats.contains(seatId))
                            seatBtn.setStyle(seatStyle(colors[0], false));
                    });
                }

                seatButtons.put(seatId, seatBtn);
                seatGrid.add(seatBtn, c, r);
            }

            Label rowLblR = new Label(row);
            rowLblR.setStyle("-fx-text-fill: #666666; -fx-font-size: 11; -fx-font-weight: bold;");
            rowLblR.setMinWidth(18);
            seatGrid.add(rowLblR, COLS + 1, r);
        }

        HBox legend = new HBox(14);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(12, 0, 12, 0));
        String[][] legendItems = { { "#22d3ee", "PREMIUM ₹350" }, { "#a855f7", "STANDARD ₹220" },
                { "#22c55e", "VALUE ₹150" }, { "#a78bfa", "ACCESSIBLE ₹300" }, { "#f59e0b", "Selected" },
                { "#dc2626", "Booked" } };
        for (String[] li : legendItems) {
            Rectangle dot = new Rectangle(13, 13, Color.web(li[0]));
            dot.setArcWidth(3);
            dot.setArcHeight(3);
            Label lbl = new Label(li[1]);
            lbl.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11;");
            HBox item = new HBox(5, dot, lbl);
            item.setAlignment(Pos.CENTER_LEFT);
            legend.getChildren().add(item);
        }

        ScrollPane gridScroll = new ScrollPane(seatGrid);
        gridScroll.setFitToWidth(true);
        gridScroll.setStyle("-fx-background: #0d0d0d; -fx-background-color: #0d0d0d;");

        VBox seatContent = new VBox(screenArea, gridScroll, legend);
        seatContent.setStyle("-fx-background-color: #0d0d0d;");
        VBox.setVgrow(gridScroll, Priority.ALWAYS);

        totalLbl.setFont(Font.font("Arial Black", FontWeight.BOLD, 16));
        totalLbl.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 18; -fx-font-weight: bold;");
        seatsLbl.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12;");

        Button proceedBtn = new Button("Proceed to Payment →");
        proceedBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #22d3ee, #a855f7); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 12 28 12 28; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
        proceedBtn.setOnAction(e -> {
            if (selectedSeats.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Please select at least one seat.", ButtonType.OK);
                a.showAndWait();
                return;
            }
            showPaymentScreen();
        });

        VBox leftInfo = new VBox(4, seatsLbl, totalLbl);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox bookingBar = new HBox();
        bookingBar.getChildren().addAll(leftInfo, spacer, proceedBtn);
        bookingBar.setAlignment(Pos.CENTER);
        bookingBar.setPadding(new Insets(14, 24, 14, 24));
        bookingBar.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #22d3ee; -fx-border-width: 2 0 0 0;");

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(seatContent);
        root.setBottom(bookingBar);
        root.setStyle("-fx-background-color: #0d0d0d;");

        Scene scene = new Scene(root, 900, 620);
        primaryStage.setScene(scene);
    }

    String[] getSeatColors(String type) {
        switch (type) {
            case "PREMIUM":
                return new String[] { "#22d3ee", "#f59e0b", "#dc2626" };
            case "STANDARD":
                return new String[] { "#a855f7", "#f59e0b", "#dc2626" };
            case "VALUE":
                return new String[] { "#22c55e", "#f59e0b", "#dc2626" };
            case "ACCESSIBLE":
                return new String[] { "#a78bfa", "#f59e0b", "#dc2626" };
            default:
                return new String[] { "#888888", "#f59e0b", "#dc2626" };
        }
    }

    String seatStyle(String color, boolean disabled) {
        return "-fx-background-color: " + color
                + "; -fx-border-color: transparent; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: "
                + (disabled ? "default" : "hand") + "; -fx-opacity: " + (disabled ? "0.45" : "1.0") + ";";
    }

    void updateBookingBar(Label totalLbl, Label seatsLbl) {
        int total = 0;
        for (String sid : selectedSeats) {
            String row = sid.replaceAll("\\d", "");
            for (int i = 0; i < ROWS.length; i++) {
                if (ROWS[i].equals(row)) {
                    total += PRICES[i];
                    break;
                }
            }
        }
        totalLbl.setText("Total: ₹" + total);
        seatsLbl.setText(selectedSeats.isEmpty() ? "No seats selected"
                : selectedSeats.size() + " seat(s): " + String.join(", ", selectedSeats));
    }

    // ══════════════════════════════════════════════════════════════════════
    // PAYMENT SCREEN
    // ══════════════════════════════════════════════════════════════════════
    void showPaymentScreen() {
        int total = 0;
        for (String sid : selectedSeats) {
            String row = sid.replaceAll("\\d", "");
            for (int i = 0; i < ROWS.length; i++) {
                if (ROWS[i].equals(row)) {
                    total += PRICES[i];
                    break;
                }
            }
        }
        final int finalTotal = total;
        int gst = (int) (finalTotal * 0.18);
        int convFee = 100;
        int ticketBase = finalTotal - gst - convFee;

        Label payTitle = new Label("🔐 Secure Checkout");
        payTitle.setFont(Font.font("Arial Black", FontWeight.BOLD, 20));
        payTitle.setTextFill(Color.WHITE);

        Label banner = new Label("  Fill the form below to complete your purchase.  ");
        banner.setStyle(
                "-fx-background-color: #1a2a3a; -fx-text-fill: #7dd3fc; -fx-font-size: 12; -fx-padding: 8 14 8 14; -fx-background-radius: 8;");

        Label sec1 = sectionLabel("1 · Your Information");
        TextField firstName = payField("First Name");
        TextField lastName = payField("Last Name");
        TextField email = payField("Email Address");
        TextField phone = payField("Phone Number");
        HBox nameRow = new HBox(12, firstName, lastName);

        Label sec2 = sectionLabel("2 · Payment Method");

        ToggleGroup methodGroup = new ToggleGroup();
        ToggleButton cardBtn = methodToggle("💳 Card", methodGroup);
        ToggleButton upiBtn = methodToggle("📱 UPI", methodGroup);
        ToggleButton netBtn = methodToggle("🏦 Net Banking", methodGroup);
        cardBtn.setSelected(true);
        HBox methodRow = new HBox(8, cardBtn, upiBtn, netBtn);

        TextField cardNum = payField("Card Number");
        cardNum.setPrefWidth(Integer.MAX_VALUE);
        TextField expiry = payField("MM / YY");
        TextField cvv = payField("CVV");
        HBox cardRow = new HBox(12, expiry, cvv);

        TextField upiId = payField("yourname@upi");
        upiId.setPrefWidth(Integer.MAX_VALUE);

        ComboBox<String> bankSelect = new ComboBox<>();
        bankSelect.getItems().addAll("SBI Bank", "HDFC Bank", "ICICI Bank", "Axis Bank", "Kotak Bank");
        bankSelect.setValue("SBI Bank");
        bankSelect.setStyle(
                "-fx-background-color: #1a1a2e; -fx-text-fill: white; -fx-border-color: #333333; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13; -fx-padding: 8;");
        bankSelect.setMaxWidth(Double.MAX_VALUE);

        StackPane cardPane = new StackPane(new VBox(10, cardNum, cardRow));
        StackPane upiPane = new StackPane(upiId);
        StackPane netPane = new StackPane(bankSelect);
        upiPane.setVisible(false);
        netPane.setVisible(false);
        StackPane methodContent = new StackPane(cardPane, upiPane, netPane);

        if (loggedInUser != null) {

            firstName.setText(loggedInUser.getUsername());

            email.setText(loggedInUser.getEmail());

            phone.setText(loggedInUser.getPhone());
        }

        cardBtn.setOnAction(e -> {
            cardPane.setVisible(true);
            upiPane.setVisible(false);
            netPane.setVisible(false);
        });
        upiBtn.setOnAction(e -> {
            cardPane.setVisible(false);
            upiPane.setVisible(true);
            netPane.setVisible(false);
        });
        netBtn.setOnAction(e -> {
            cardPane.setVisible(false);
            upiPane.setVisible(false);
            netPane.setVisible(true);
        });

        Button payNowBtn = new Button("🔒  Pay ₹" + finalTotal + " Now");
        payNowBtn.setMaxWidth(Double.MAX_VALUE);
        payNowBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #22c55e, #16a34a); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15; -fx-padding: 13; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");

        Label secureNote = new Label("🛡 Secured by RazorPay • PCI-DSS Compliant");
        secureNote.setStyle("-fx-text-fill: #555555; -fx-font-size: 11;");
        secureNote.setAlignment(Pos.CENTER);

        payNowBtn.setOnAction(e -> {
            payNowBtn.setDisable(true);
            payNowBtn.setText("⏳ Processing Payment...");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            // pause.setOnFinished(ev -> showTicketScreen(finalTotal));
            pause.setOnFinished(ev -> {

                for (String seat : selectedSeats) {

                    BookingDAO.addBooking(

                            currentMovie.getTitle(),
                            currentTime,
                            seat,
                            loggedInUser.getUsername(),
                            getSeatPrice(seat));
                }
                bookedSeats.addAll(selectedSeats);

                showTicketScreen(finalTotal);

            });
            pause.play();
        });

        VBox leftPanel = new VBox(12, payTitle, banner, sec1, nameRow, email, phone, sec2, methodRow, methodContent,
                payNowBtn, secureNote);
        leftPanel.setPadding(new Insets(30, 30, 30, 30));
        leftPanel.setStyle("-fx-background-color: #0f0f1a;");
        leftPanel.setPrefWidth(520);

        ImageView posterImg = new ImageView();
        posterImg.setFitWidth(240);
        posterImg.setFitHeight(150);
        posterImg.setPreserveRatio(false);
        try {
            posterImg.setImage(new Image(currentMovie.getPosterUrl(), 240, 150, false, true, true));
        } catch (Exception ignored) {
        }

        Label ordTitle = new Label("🎟 Purchase Details");
        ordTitle.setFont(Font.font("Arial Black", FontWeight.BOLD, 15));
        ordTitle.setTextFill(Color.web("#f59e0b"));

        Label ordMovie = new Label(currentMovie.getTitle());
        ordMovie.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ordMovie.setTextFill(Color.WHITE);
        ordMovie.setWrapText(true);

        Label ordTime = orderMeta("🕐 " + currentTime + "  |  Screen 5");
        Label ordSeats = orderMeta("💺 " + String.join(", ", selectedSeats));

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #333333;");
        Label ordBase = orderRow("Net Ticket(s) Price", "₹" + ticketBase);
        Label ordGst = orderRow("GST (18%)", "₹" + gst);
        Label ordConv = orderRow("Convenience Fee", "₹" + convFee);
        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #333333;");

        Label ordTotal = new Label("Total:  ₹" + finalTotal);
        ordTotal.setFont(Font.font("Arial Black", FontWeight.BOLD, 18));
        ordTotal.setTextFill(Color.web("#f59e0b"));

        Label features = new Label(
                "✅ Access Online\n✅ Nearby Location\n✅ Mobile Transfer Available\n✅ Multi-Screen Available");
        features.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12; -fx-line-spacing: 4;");

        VBox rightPanel = new VBox(12, ordTitle, posterImg, ordMovie, ordTime, ordSeats, sep1, ordBase, ordGst, ordConv,
                sep2, ordTotal, features);
        rightPanel.setPadding(new Insets(30, 20, 30, 20));
        rightPanel.setStyle("-fx-background-color: #0a0a0a; -fx-border-color: #222222; -fx-border-width: 0 0 0 1;");
        rightPanel.setPrefWidth(280);

        HBox root = new HBox(leftPanel, rightPanel);
        root.setStyle("-fx-background-color: #0f0f1a;");
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        ScrollPane sp = new ScrollPane(root);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #0f0f1a; -fx-background-color: #0f0f1a;");

        Scene scene = new Scene(sp, 900, 620);
        primaryStage.setScene(scene);
    }

    // ══════════════════════════════════════════════════════════════════════
    // TICKET / CONFIRMATION SCREEN
    // ══════════════════════════════════════════════════════════════════════
    void showTicketScreen(int total) {
        String bookingId = "TRK" + Integer.toHexString((int) (Math.random() * 0xFFFFFF)).toUpperCase();
        String qrCode = "QR-" + Long.toHexString(System.currentTimeMillis()).toUpperCase().substring(4);
        int gst = (int) (total * 0.18);
        int convFee = 100;
        int tickBase = total - gst - convFee;

        VBox screen = new VBox();
        screen.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a2e, #16213e, #0f3460);");
        screen.setAlignment(Pos.CENTER);
        screen.setPadding(new Insets(30));

        VBox ticket = new VBox();
        ticket.setPrefWidth(330);
        ticket.setMaxWidth(330);
        ticket.setStyle(
                "-fx-background-color: white; -fx-border-radius: 18; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 40, 0, 0, 10);");

        Label shareRow = new Label("📤  SHARE YOUR TICKET");
        shareRow.setStyle("-fx-text-fill: #7dd3fc; -fx-font-size: 12; -fx-font-weight: bold;");

        ImageView tPoster = new ImageView();
        tPoster.setFitWidth(100);
        tPoster.setFitHeight(130);
        tPoster.setPreserveRatio(false);
        try {
            tPoster.setImage(new Image(currentMovie.getPosterUrl(), 100, 130, false, true, true));
        } catch (Exception ignored) {
        }
        tPoster.setStyle(
                "-fx-border-color: #f59e0b; -fx-border-width: 3; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label tMovieName = new Label(currentMovie.getTitle());
        tMovieName.setFont(Font.font("Arial Black", FontWeight.BOLD, 14));
        tMovieName.setTextFill(Color.WHITE);
        tMovieName.setWrapText(true);
        tMovieName.setMaxWidth(240);

        Label tLanguage = new Label(currentMovie.getLanguage().split(",")[0] + ", 2D");
        tLanguage.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12;");
        Label tDate = new Label("📅 " + java.time.LocalDate.now() + "  |  🕐 " + currentTime);
        tDate.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11;");
        Label tVenue = new Label("📍 PVR: Elan Miracle, Sec 84  |  Screen 5");
        tVenue.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11;");
        tVenue.setWrapText(true);

        VBox topSection = new VBox(8, shareRow, tPoster, tMovieName, tLanguage, tDate, tVenue);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(20, 16, 16, 16));
        topSection.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #1a1a2e, #0f3460); -fx-border-radius: 18 18 0 0; -fx-background-radius: 18 18 0 0;");

        Label dash = new Label("- - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        dash.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11;");
        HBox dashes = new HBox(dash);
        dashes.setAlignment(Pos.CENTER);
        dashes.setPadding(new Insets(2, 0, 2, 0));

        Label seatCountLbl = new Label(selectedSeats.size() + " Ticket(s)");
        seatCountLbl.setStyle("-fx-text-fill: #111111; -fx-font-weight: bold; -fx-font-size: 13;");
        Label screenLbl2 = new Label("SCREEN 5");
        screenLbl2.setStyle("-fx-text-fill: #888888; -fx-font-size: 11;");
        Label screenIdLbl = new Label(qrCode);
        screenIdLbl.setStyle("-fx-text-fill: #888888; -fx-font-size: 10;");

        GridPane qrGrid = new GridPane();
        qrGrid.setHgap(1);
        qrGrid.setVgap(1);
        Random rng = new Random(bookingId.hashCode());
        for (int r2 = 0; r2 < 10; r2++) {
            for (int c2 = 0; c2 < 10; c2++) {
                Rectangle cell = new Rectangle(6, 6, rng.nextBoolean() ? Color.BLACK : Color.WHITE);
                cell.setArcWidth(1);
                cell.setArcHeight(1);
                qrGrid.add(cell, c2, r2);
            }
        }
        VBox qrBox = new VBox(8, qrGrid, screenIdLbl);
        qrBox.setAlignment(Pos.CENTER);
        qrBox.setStyle(
                "-fx-background-color: #f9f9f9; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12;");

        Label bookIdLbl = new Label("BOOKING ID   " + bookingId);
        bookIdLbl.setStyle(
                "-fx-background-color: #f0f0f0; -fx-text-fill: #111111; -fx-font-size: 11; -fx-font-weight: bold; -fx-padding: 8 12 8 12; -fx-background-radius: 8;");

        Label cancelNote = new Label("Cancellation not available for this venue");
        cancelNote.setStyle("-fx-text-fill: #999999; -fx-font-size: 10;");

        Separator s1 = new Separator();
        Label r1 = ticketAmtRow("Net Ticket(s) Price", "₹" + tickBase);
        Label r2 = ticketAmtRow("GST", "₹" + gst);
        Label r3 = ticketAmtRow("Convenience Fees", "₹" + convFee);
        Separator s2 = new Separator();
        Label r4 = new Label("Total Amount:   ₹" + total);
        r4.setFont(Font.font("Arial Black", FontWeight.BOLD, 15));
        r4.setTextFill(Color.web("#111111"));

        VBox bodySection = new VBox(8, seatCountLbl, screenLbl2, qrBox, bookIdLbl, cancelNote, s1, r1, r2, r3, s2, r4);
        bodySection.setPadding(new Insets(14, 18, 16, 18));
        bodySection.setStyle("-fx-background-color: white;");

        Label successBadge = new Label("  ✅  Payment Successful!  ");
        successBadge.setFont(Font.font("Arial Black", FontWeight.BOLD, 14));
        successBadge.setStyle(
                "-fx-background-color: #22c55e; -fx-text-fill: white; -fx-padding: 12; -fx-alignment: center;");
        successBadge.setMaxWidth(Double.MAX_VALUE);

        Button homeBtn = new Button("🏠  Back to Home");
        homeBtn.setMaxWidth(Double.MAX_VALUE);
        homeBtn.setStyle(
                "-fx-background-color: #1a1a2e; -fx-text-fill: #7dd3fc; -fx-font-weight: bold; -fx-font-size: 13; -fx-padding: 12; -fx-border-radius: 0 0 18 18; -fx-background-radius: 0 0 18 18; -fx-cursor: hand;");
        homeBtn.setOnAction(e -> {

            selectedSeats.clear();
            showHomeScreen();
        });

        ticket.getChildren().addAll(topSection, dashes, bodySection, successBadge, homeBtn);

        ticket.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(600), ticket);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        screen.getChildren().add(ticket);

        ScrollPane sp = new ScrollPane(screen);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Scene scene = new Scene(sp, 900, 620);
        primaryStage.setScene(scene);
    }

    // ══════════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════════
    Button styledBtn(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg
                + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand; -fx-font-size: 13;");
        return b;
    }

    TextField payField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
                "-fx-background-color: #1a1a2e; -fx-border-color: #333333; -fx-text-fill: white; -fx-prompt-text-fill: #666666; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 14 10 14; -fx-font-size: 13;");
        tf.setPrefWidth(Integer.MAX_VALUE);
        HBox.setHgrow(tf, Priority.ALWAYS);
        return tf;
    }

    Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-font-size: 12; -fx-padding: 6 0 0 0;");
        return l;
    }

    Label orderMeta(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #888888; -fx-font-size: 11;");
        l.setWrapText(true);
        return l;
    }

    Label orderRow(String label, String value) {
        Label l = new Label(label + "  ..........  " + value);
        l.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13;");
        return l;
    }

    Label ticketAmtRow(String label, String value) {
        Label l = new Label(label + "  .....  " + value);
        l.setStyle("-fx-text-fill: #555555; -fx-font-size: 12;");
        return l;
    }

    ToggleButton methodToggle(String text, ToggleGroup group) {
        ToggleButton tb = new ToggleButton(text);
        tb.setToggleGroup(group);
        tb.setStyle(
                "-fx-background-color: #1a1a2e; -fx-text-fill: #aaaaaa; -fx-border-color: #333333; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 9 16 9 16; -fx-font-size: 13; -fx-cursor: hand;");
        tb.selectedProperty().addListener((obs, o, selected) -> {
            if (selected)
                tb.setStyle(
                        "-fx-background-color: #22d3ee22; -fx-text-fill: #22d3ee; -fx-border-color: #22d3ee; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 9 16 9 16; -fx-font-size: 13; -fx-cursor: hand;");
            else
                tb.setStyle(
                        "-fx-background-color: #1a1a2e; -fx-text-fill: #aaaaaa; -fx-border-color: #333333; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 9 16 9 16; -fx-font-size: 13; -fx-cursor: hand;");
        });
        return tb;
    }

    void showLoginScreen() {

        Label title = new Label("🎬 CineRush");
        title.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 32));
        title.setTextFill(Color.web("#f59e0b"));

        Label subtitle = new Label("Login to continue");
        subtitle.setTextFill(Color.WHITE);
        subtitle.setFont(Font.font("Arial", 16));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");

        String fieldStyle = "-fx-background-color: #1a1a2e;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #777777;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 13;";

        usernameField.setStyle(fieldStyle);
        passwordField.setStyle(fieldStyle);

        emailField.setStyle(fieldStyle);
        phoneField.setStyle(fieldStyle);

        Button loginBtn = new Button("Login");
        Button signupBtn = new Button("Signup");

        String btnStyle = "-fx-background-color: #22d3ee;" +
                "-fx-text-fill: black;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 20 10 20;" +
                "-fx-cursor: hand;";

        loginBtn.setStyle(btnStyle);
        signupBtn.setStyle(btnStyle);

        Label message = new Label();
        message.setTextFill(Color.RED);

        loginBtn.setOnAction(e -> {

            String username = usernameField.getText();
            String password = passwordField.getText();

            User user = UserDAO.loginUser(
                    username,
                    password);

            if (user != null) {

                loggedInUser = user;

                showHomeScreen();

            } else {

                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "Invalid Username or Password");

                alert.showAndWait();
            }
        });

        signupBtn.setOnAction(e -> {

            boolean success = UserDAO.registerUser(

                    usernameField.getText(),
                    passwordField.getText(),
                    emailField.getText(),
                    phoneField.getText());

            if (success) {

                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION,
                        "Registration Successful!");

                alert.showAndWait();

                showLoginScreen();

            } else {
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "Username already exists!");

                alert.showAndWait();
            }
        });

        VBox root = new VBox(
                15,
                title,
                subtitle,
                usernameField,
                passwordField,
                emailField,
                phoneField,
                loginBtn,
                signupBtn,
                message);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #0f172a, #111827);");

        Scene scene = new Scene(root, 900, 620);

        primaryStage.setScene(scene);
    }

    int calculateTotal() {

        int total = 0;

        for (String sid : selectedSeats) {

            String row = sid.replaceAll("\\d", "");

            for (int i = 0; i < ROWS.length; i++) {

                if (ROWS[i].equals(row)) {
                    total += PRICES[i];
                    break;
                }
            }
        }

        return total;
    }

    int getSeatPrice(String seatId) {

        String row = seatId.replaceAll("\\d", "");

        for (int i = 0; i < ROWS.length; i++) {

            if (ROWS[i].equals(row)) {

                return PRICES[i];
            }
        }

        return 0;
    }

    void showMyBookingsScreen() {

        if (loggedInUser == null) {

            showLoginScreen();

            return;
        }

        Label title = new Label("🎟 My Bookings");

        title.setFont(
                Font.font(
                        "Arial Black",
                        FontWeight.BOLD,
                        24));

        title.setTextFill(Color.WHITE);

        VBox bookingsBox = new VBox(18);

        bookingsBox.setPadding(
                new Insets(20));

        ArrayList<String> bookings = BookingDAO.getBookingsByUser(
                loggedInUser.getUsername());

        if (bookings.isEmpty()) {

            Label empty = new Label("No bookings yet.");

            empty.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 16;");

            bookingsBox.getChildren()
                    .add(empty);

        } else {

            for (String b : bookings) {

                VBox card = new VBox(8);

                card.setPadding(
                        new Insets(18));

                card.setStyle(
                        "-fx-background-color: #1a1a2e;" +
                                "-fx-background-radius: 16;" +
                                "-fx-border-color: #22d3ee;" +
                                "-fx-border-radius: 16;");

                Label lbl = new Label(b);

                lbl.setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-font-size: 14;");

                card.getChildren().add(lbl);

                bookingsBox.getChildren()
                        .add(card);
            }
        }

        Button backBtn = styledBtn(
                "← Back",
                "#333333",
                "#ffffff");

        backBtn.setOnAction(e -> {
            showHomeScreen();
        });

        VBox root = new VBox(
                20,
                backBtn,
                title,
                bookingsBox);

        root.setPadding(
                new Insets(20));

        root.setStyle(
                "-fx-background-color: #111111;");

        ScrollPane sp = new ScrollPane(root);

        sp.setFitToWidth(true);

        Scene scene = new Scene(sp, 900, 620);

        primaryStage.setScene(scene);
    }

    void showAdminPanel() {
        if (loggedInUser == null ||
                !loggedInUser.getRole().equals("ADMIN")) {

            showHomeScreen();
            return;
        }
        Label title = new Label("🛠 Admin Panel");

        title.setFont(
                Font.font(
                        "Arial Black",
                        FontWeight.BOLD,
                        28));

        title.setTextFill(Color.WHITE);

        Button addMovieBtn = styledBtn(
                "Add Movie",
                "#22c55e",
                "#ffffff");

        Button editMovieBtn = styledBtn(
                "Edit Movie",
                "#3b82f6",
                "#ffffff");

        Button deleteMovieBtn = styledBtn(
                "Delete Movie",
                "#ef4444",
                "#ffffff");

        Button importBtn = styledBtn(
                "Import Latest Movies",
                "#22d3ee",
                "#000000");

        Button fixPostersBtn = styledBtn(

                "Fix Missing Posters",
                "#f59e0b",
                "#000000");

        fixPostersBtn.setOnAction(e -> {
            TMDBService.updateMissingPosters();
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Missing posters updated successfully!");
            alert.showAndWait();
        });

        addMovieBtn.setMaxWidth(Double.MAX_VALUE);
        editMovieBtn.setMaxWidth(Double.MAX_VALUE);
        deleteMovieBtn.setMaxWidth(Double.MAX_VALUE);

        addMovieBtn.setPrefHeight(55);
        editMovieBtn.setPrefHeight(55);
        deleteMovieBtn.setPrefHeight(55);

        importBtn.setOnAction(e -> {

            TMDBService.importNowPlayingMovies();
            ALL_MOVIES = MovieDAO.getAllMovies();
            showHomeScreen();
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Movies Imported Successfully!");
            alert.showAndWait();
        });

        addMovieBtn.setOnAction(e -> {
            showAddMovieScreen();
        });

        VBox root = new VBox(
                20,
                title,
                addMovieBtn,
                editMovieBtn,
                deleteMovieBtn,
                fixPostersBtn,
                importBtn);

        root.setPadding(new Insets(30));

        root.setAlignment(Pos.TOP_CENTER);

        root.setStyle(
                "-fx-background-color: #111111;");

        Scene scene = new Scene(root, 900, 620);

        primaryStage.setScene(scene);

    }

    void showAddMovieScreen() {
        Label title = new Label("➕ Add Movie");

        title.setFont(
                Font.font(
                        "Arial Black",
                        FontWeight.BOLD,
                        26));

        title.setTextFill(Color.WHITE);

        TextField titleField = new TextField();

        titleField.setPromptText(
                "Movie Title");

        TextField genreField = new TextField();

        genreField.setPromptText(
                "Genre");

        TextField languageField = new TextField();

        languageField.setPromptText(
                "Language");

        TextField durationField = new TextField();

        durationField.setPromptText(
                "Duration");

        TextField ratingField = new TextField();

        ratingField.setPromptText(
                "Rating");

        TextField imdbField = new TextField();

        imdbField.setPromptText(
                "IMDb");

        TextArea descriptionField = new TextArea();

        descriptionField.setPromptText(
                "Description");

        TextField posterField = new TextField();

        posterField.setPromptText(
                "Poster URL");

        TextField trailerField = new TextField();

        trailerField.setPromptText(
                "Trailer URL");

        DatePicker releaseDateField = new DatePicker();

        CheckBox newReleaseBox = new CheckBox("New Release");

        newReleaseBox.setTextFill(
                Color.WHITE);

        String fieldStyle =

                "-fx-background-color: #1a1a2e;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #888;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 10;";

        titleField.setStyle(fieldStyle);
        genreField.setStyle(fieldStyle);
        languageField.setStyle(fieldStyle);
        durationField.setStyle(fieldStyle);
        ratingField.setStyle(fieldStyle);
        imdbField.setStyle(fieldStyle);
        descriptionField.setStyle(fieldStyle);
        posterField.setStyle(fieldStyle);
        trailerField.setStyle(fieldStyle);

        Button addBtn = styledBtn(
                "Add Movie",
                "#22c55e",
                "#ffffff");

        Button backBtn = styledBtn(
                "← Back",
                "#333333",
                "#ffffff");

        addBtn.setOnAction(e -> {

            try {

                Movie movie = new Movie(0,
                        titleField.getText(),

                        genreField.getText(),

                        languageField.getText(),

                        durationField.getText(),

                        ratingField.getText(),

                        Double.parseDouble(
                                imdbField.getText()),

                        descriptionField.getText(),

                        posterField.getText(),

                        trailerField.getText(),

                        releaseDateField
                                .getValue()
                                .toString(),

                        newReleaseBox.isSelected());

                MovieDAO.addMovie(movie);

                Alert alert = new Alert(
                        Alert.AlertType.INFORMATION,
                        "Movie Added Successfully!");

                alert.showAndWait();
                showAdminPanel();

            } catch (Exception ex) {

                ex.printStackTrace();

                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        "Invalid Movie Data");

                alert.showAndWait();
            }
        });

        backBtn.setOnAction(e -> {
            showAdminPanel();
        });

        VBox root = new VBox(

                15,

                backBtn,
                title,

                titleField,
                genreField,
                languageField,
                durationField,
                ratingField,
                imdbField,
                descriptionField,
                posterField,
                trailerField,
                releaseDateField,
                newReleaseBox,

                addBtn);

        root.setPadding(
                new Insets(25));

        root.setStyle(
                "-fx-background-color: #111111;");

        ScrollPane sp = new ScrollPane(root);

        sp.setFitToWidth(true);

        Scene scene = new Scene(sp, 900, 620);

        primaryStage.setScene(scene);

    }

    public static void main(String[] args) {
        launch(args);
    }
}