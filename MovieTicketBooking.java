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

public class MovieTicketBooking extends Application {

    // ── Movie Data ─────────────────────────────────────────────────────────
    static class Movie {
        String title, rating, lang, genre, duration, desc, imageUrl, bannerColor;
        String[] languages; // for filtering

        Movie(String t, String r, String l, String g, String dur, String d, String img, String bc, String... langs) {
            title=t; rating=r; lang=l; genre=g; duration=dur; desc=d; imageUrl=img; bannerColor=bc;
            languages = langs;
        }
    }

    // ── All Movies (Now Showing + filtered sets) ───────────────────────────
    static final Movie[] ALL_MOVIES = {
        // ── Now Showing (default) ─────────────────────────────────────────
        new Movie("Dhurandhar 2","A","Hindi, Tamil, Telugu","Action / Thriller","2h 28m",
            "A legendary outlaw rises from the ashes to claim what's rightfully his, leaving a blazing trail of vengeance.",
            "file:dhurandhar2.jpeg","#c0392b","Hindi","Tamil","Telugu"),
        new Movie("Project Hail Mary","UA13+","English","Sci-Fi / Adventure","2h 15m",
            "One man, alone in deep space, must save humanity — with only an alien friend by his side.",
            "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=300&q=80","#2980b9","English"),
        new Movie("Dacoit","UA13+","Telugu, Hindi","Crime / Drama","2h 5m",
            "A ruthless gang wreaks havoc across the heartland until a lone cop decides enough is enough.",
            "https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?w=300&q=80","#8e44ad","Telugu","Hindi"),
        new Movie("Vaazha II: Biopic of a Billion Bros","UA13+","Malayalam","Drama / Comedy","2h 20m",
            "The billion-bro sequel is back — louder, wilder, and more chaotic than ever before.",
            "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=300&q=80","#e67e22","Malayalam"),
        new Movie("Interstellar Returns","U","English, Hindi","Sci-Fi / Epic","2h 49m",
            "Beyond the black hole, beyond time — humanity's last hope travels where no one has returned from.",
            "https://images.unsplash.com/photo-1464802686167-b939a6910659?w=300&q=80","#16a085","English","Hindi"),

        // ── English / Marvel Movies ────────────────────────────────────────
        new Movie("Avengers: Doomsday","UA13+","English","Action / Superhero","2h 55m",
            "The Avengers assemble one final time as Doctor Doom reshapes reality itself in a bid for total domination.",
            "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=300&q=80","#b22222","English"),
        new Movie("Spider-Man: Brand New Day","UA13+","English","Action / Adventure","2h 10m",
            "Peter Parker swings back into action as a mysterious new villain threatens the entire multiverse.",
            "https://images.unsplash.com/photo-1509343256512-d77a5cb3791b?w=300&q=80","#e74c3c","English"),
        new Movie("Thor: God of Thunder","UA13+","English","Action / Fantasy","2h 18m",
            "Thor must reclaim Mjolnir and rally ancient gods before a celestial darkness consumes all nine realms.",
            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300&q=80","#1a5276","English"),
        new Movie("Black Panther: Wakanda Forever II","UA13+","English","Action / Drama","2h 40m",
            "Wakanda faces a new threat from the depths of the ocean as a rival kingdom rises with devastating power.",
            "https://images.unsplash.com/photo-1534430480872-3498386e7856?w=300&q=80","#6c3483","English"),
        new Movie("Guardians of the Galaxy Vol. 4","UA13+","English","Action / Comedy","2h 22m",
            "Star-Lord and his ragtag crew embark on their most emotional mission yet to save one of their own.",
            "https://images.unsplash.com/photo-1419242902214-272b3f66ee7a?w=300&q=80","#117a65","English"),
        new Movie("Doctor Strange: Multiverse War","UA13+","English","Fantasy / Sci-Fi","2h 30m",
            "Strange battles rogue sorcerers across a thousand dimensions as the very fabric of magic tears apart.",
            "https://images.unsplash.com/photo-1531746790731-6c087fecd65a?w=300&q=80","#1f618d","English"),

        // ── Hindi Movies ──────────────────────────────────────────────────
        new Movie("Singham Returns 2","A","Hindi","Action / Masala","2h 35m",
            "ACP Bajirao Singham storms back, taking on a criminal empire that has infiltrated the highest corridors of power.",
            "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=300&q=80","#c0392b","Hindi"),
        new Movie("Stree 3","UA13+","Hindi","Horror / Comedy","2h 12m",
            "The legend of Stree returns — scarier, funnier, and more chaotic than ever as she haunts a new town.",
            "https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?w=300&q=80","#6c3483","Hindi"),
        new Movie("Pathaan 2","UA13+","Hindi","Spy / Thriller","2h 45m",
            "India's most dangerous spy faces a rogue intelligence network that threatens to ignite World War III.",
            "https://images.unsplash.com/photo-1562408590-e32931084e23?w=300&q=80","#1a5276","Hindi"),
        new Movie("Animal Park","A","Hindi","Crime / Action","2h 58m",
            "Ranvijay Singh returns fiercer than ever, waging a blood war that will define his brutal legacy forever.",
            "https://images.unsplash.com/photo-1478720568477-152d9b164e26?w=300&q=80","#922b21","Hindi"),
        new Movie("Rocky aur Rani 2","U","Hindi","Romance / Drama","2h 20m",
            "Rocky and Rani navigate the joys and storms of married life while juggling two completely opposite families.",
            "https://images.unsplash.com/photo-1485846234645-a62644f84728?w=300&q=80","#e67e22","Hindi"),

        // ── Tamil Movies ──────────────────────────────────────────────────
        new Movie("Thalapathy 69","UA13+","Tamil","Action / Drama","2h 50m",
            "Vijay's most powerful role yet — a street-level hero who takes on systemic corruption with raw fury.",
            "https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?w=300&q=80","#2e86c1","Tamil"),
        new Movie("Coolie 2","A","Tamil, Telugu, Hindi","Action / Thriller","2h 35m",
            "Rajinikanth is unstoppable as a railway worker who hides a shocking past that changes everything.",
            "https://images.unsplash.com/photo-1533488765986-dfa2a9939acd?w=300&q=80","#1a5276","Tamil"),

        // ── Malayalam Movies ───────────────────────────────────────────────
        new Movie("Lucifer 3","A","Malayalam","Crime / Thriller","2h 45m",
            "Stephen Nedumpally returns to dismantle a global cartel that threatens the very soul of his homeland.",
            "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=300&q=80","#117a65","Malayalam"),
        new Movie("Manjummel Boys 2","UA13+","Malayalam","Adventure / Drama","2h 15m",
            "A new group of friends face the ultimate survival test in the unforgiving wilderness of the Northeast.",
            "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=300&q=80","#138d75","Malayalam"),
    };

    // ── Seat Config ────────────────────────────────────────────────────────
    static final String[] ROWS       = {"A","B","C","D","E","F","G","H","J","K","L","M"};
    static final String[] SEAT_TYPES = {"PREMIUM","PREMIUM","PREMIUM","PREMIUM","ACCESSIBLE","STANDARD","STANDARD","STANDARD","STANDARD","VALUE","VALUE","VALUE"};
    static final int[]    PRICES     = {350,350,350,350,300,220,220,220,220,150,150,150};
    static final int      COLS       = 14;
    static final Set<String> PRE_BOOKED = new HashSet<>(Arrays.asList(
        "A3","A7","B2","B9","C5","D11","F4","F8","G6","H1","H14","J7","K3","K10","M5"
    ));

    // ── State ──────────────────────────────────────────────────────────────
    Map<String, Button> seatButtons   = new HashMap<>();
    Set<String>         bookedSeats   = new HashSet<>(PRE_BOOKED);
    Set<String>         selectedSeats = new LinkedHashSet<>();
    Movie               currentMovie  = null;
    String              currentTime   = "";
    String              activeFilter  = "All"; // track active language filter
    Stage               primaryStage;

    // ══════════════════════════════════════════════════════════════════════
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("🎬 CineRush — Movie Ticket Booking");
        showHomeScreen();
        stage.show();
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HOME SCREEN
    // ══════════════════════════════════════════════════════════════════════
    void showHomeScreen() {
        // Header — only Movies tab
        Label logo = new Label("🎬 CineRush");
        logo.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 26));
        logo.setTextFill(Color.web("#f59e0b"));

        Label moviesTab = new Label("Movies");
        moviesTab.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        moviesTab.setTextFill(Color.WHITE);
        moviesTab.setStyle("-fx-border-color: #f59e0b; -fx-border-width: 0 0 2 0; -fx-padding: 0 0 4 0;");

        HBox header = new HBox(30, logo, moviesTab);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #333333; -fx-border-width: 0 0 1 0;");

        // Filter row — now functional
        String[] filterLabels = {"All","English","Hindi","Tamil","Malayalam","New Releases"};
        HBox filterRow = new HBox(10);
        filterRow.setPadding(new Insets(10, 20, 10, 20));
        filterRow.setStyle("-fx-background-color: #111111;");
        filterRow.setAlignment(Pos.CENTER_LEFT);

        // Movie Grid (will be rebuilt on filter change)
        FlowPane movieGrid = new FlowPane(16, 16);
        movieGrid.setPadding(new Insets(0, 24, 24, 24));
        movieGrid.setStyle("-fx-background-color: #111111;");

        Label browseLabel = new Label("Now Showing");
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
                chip.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: #000000; -fx-border-color: #f59e0b; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14 5 14; -fx-font-size: 12; -fx-font-weight: bold; -fx-cursor: hand;");
            } else {
                chip.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #cccccc; -fx-border-color: #444444; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14 5 14; -fx-font-size: 12; -fx-cursor: hand;");
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
                            c.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: #000000; -fx-border-color: #f59e0b; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14 5 14; -fx-font-size: 12; -fx-font-weight: bold; -fx-cursor: hand;");
                        } else {
                            c.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #cccccc; -fx-border-color: #444444; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 5 14 5 14; -fx-font-size: 12; -fx-cursor: hand;");
                        }
                    }
                }
            });
            filterRow.getChildren().add(chip);
        }

        ScrollPane scrollPane = new ScrollPane(movieGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #111111; -fx-background-color: #111111; -fx-border-color: transparent;");

        VBox root = new VBox(header, filterRow, browseLabel, scrollPane);
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
            if (filter.equals("All") || filter.equals("New Releases")) {
                // Show only the original 5 "Now Showing" movies
                show = Arrays.asList("Dhurandhar 2","Project Hail Mary","Dacoit","Vaazha II: Biopic of a Billion Bros","Interstellar Returns").contains(m.title);
            } else {
                for (String lang : m.languages) {
                    if (lang.equals(filter)) { show = true; break; }
                }
            }
            if (show) {
                grid.getChildren().add(createMovieCard(m));
            }
        }
    }

    VBox createMovieCard(Movie m) {
        ImageView img = new ImageView();
        img.setFitWidth(170);
        img.setFitHeight(220);
        img.setPreserveRatio(false);
        try {
            img.setImage(new Image(m.imageUrl, 170, 220, false, true, true));
        } catch (Exception e) {
            // fallback
        }
        img.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0, 0, 4);");

        Label genreTag = new Label(m.genre);
        genreTag.setStyle("-fx-background-color: " + m.bannerColor + "; -fx-text-fill: white; -fx-font-size: 10; -fx-font-weight: bold; -fx-padding: 3 8 3 8; -fx-background-radius: 20;");

        StackPane thumbPane = new StackPane(img, genreTag);
        StackPane.setAlignment(genreTag, Pos.BOTTOM_LEFT);
        StackPane.setMargin(genreTag, new Insets(0, 0, 8, 8));
        thumbPane.setPrefSize(170, 220);
        thumbPane.setStyle("-fx-background-color: #2a2a2a;");

        Label titleLbl = new Label(m.title);
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(160);

        Label metaLbl = new Label(m.rating + " | " + m.lang.split(",")[0]);
        metaLbl.setTextFill(Color.web("#888888"));
        metaLbl.setFont(Font.font("Arial", 11));

        VBox info = new VBox(6, titleLbl, metaLbl);
        info.setPadding(new Insets(10, 12, 12, 12));

        VBox card = new VBox(thumbPane, info);
        card.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #333333; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
        card.setPrefWidth(170);

        card.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
            st.setToX(1.04); st.setToY(1.04); st.play();
            card.setStyle("-fx-background-color: #222222; -fx-border-color: " + m.bannerColor + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
        });
        card.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), card);
            st.setToX(1.0); st.setToY(1.0); st.play();
            card.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #333333; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
        });
        card.setOnMouseClicked(e -> showMovieDetail(m));

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  MOVIE DETAIL SCREEN
    // ══════════════════════════════════════════════════════════════════════
    void showMovieDetail(Movie m) {
        currentMovie = m;

        Button backBtn = styledBtn("← Back", "#444444", "#ffffff");
        backBtn.setOnAction(e -> showHomeScreen());

        ImageView poster = new ImageView();
        poster.setFitWidth(160); poster.setFitHeight(220);
        poster.setPreserveRatio(false);
        try { poster.setImage(new Image(m.imageUrl, 160, 220, false, true, true)); } catch (Exception ignored) {}
        poster.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 20, 0, 0, 8);");

        Label genrePill = new Label("  " + m.genre + "  ");
        genrePill.setStyle("-fx-background-color: " + m.bannerColor + "; -fx-text-fill: white; -fx-font-size: 11; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 4 12 4 12;");

        Label titleLbl = new Label(m.title);
        titleLbl.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 28));
        titleLbl.setTextFill(Color.WHITE);
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(500);

        Label subLbl = new Label(m.rating + "  |  " + m.lang + "  |  " + m.duration);
        subLbl.setTextFill(Color.web("#aaaaaa"));
        subLbl.setFont(Font.font("Arial", 13));

        Label descLbl = new Label(m.desc);
        descLbl.setTextFill(Color.web("#cccccc"));
        descLbl.setFont(Font.font("Arial", 13));
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(500);

        Label ratingLbl = new Label("★★★★☆  8.2 / 10");
        ratingLbl.setStyle("-fx-text-fill: " + m.bannerColor + "; -fx-font-size: 16; -fx-font-weight: bold;");

        VBox metaBox = new VBox(10, genrePill, titleLbl, subLbl, descLbl, ratingLbl);
        metaBox.setAlignment(Pos.TOP_LEFT);

        HBox heroBox = new HBox(28, poster, metaBox);
        heroBox.setAlignment(Pos.CENTER_LEFT);
        heroBox.setPadding(new Insets(20, 28, 24, 28));
        heroBox.setStyle("-fx-background-color: linear-gradient(to bottom, " + m.bannerColor + "88, #0a0a0a);");

        VBox headerArea = new VBox(0);
        HBox topBar = new HBox(backBtn);
        topBar.setPadding(new Insets(12, 28, 0, 28));
        topBar.setStyle("-fx-background-color: " + m.bannerColor + "88;");
        headerArea.getChildren().addAll(topBar, heroBox);

        Label showLabel = new Label("🕐  Select Showtime");
        showLabel.setFont(Font.font("Arial Black", FontWeight.BOLD, 16));
        showLabel.setTextFill(Color.WHITE);

        HBox timeBox = new HBox(12);
        for (String t : new String[]{"10:00 AM","1:30 PM","4:45 PM","8:00 PM","11:30 PM"}) {
            Button tb = new Button(t);
            tb.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #22d3ee; -fx-border-color: #22d3ee; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 18 10 18; -fx-font-weight: bold; -fx-font-size: 13; -fx-cursor: hand;");
            tb.setOnMouseEntered(e -> tb.setStyle("-fx-background-color: #22d3ee; -fx-text-fill: #000000; -fx-border-color: #22d3ee; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 18 10 18; -fx-font-weight: bold; -fx-font-size: 13; -fx-cursor: hand;"));
            tb.setOnMouseExited(e -> tb.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #22d3ee; -fx-border-color: #22d3ee; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 18 10 18; -fx-font-weight: bold; -fx-font-size: 13; -fx-cursor: hand;"));
            tb.setOnAction(e -> { currentTime = t; showSeatScreen(); });
            timeBox.getChildren().add(tb);
        }

        VBox showSection = new VBox(12, showLabel, timeBox);
        showSection.setPadding(new Insets(20, 28, 16, 28));
        showSection.setStyle("-fx-background-color: #0d0d0d;");

        Label priceLabel = new Label("💺  Seat Prices");
        priceLabel.setFont(Font.font("Arial Black", FontWeight.BOLD, 16));
        priceLabel.setTextFill(Color.WHITE);

        HBox priceBox = new HBox(16);
        String[][] priceInfo = {{"#22d3ee","PREMIUM","₹350"},{"#a855f7","STANDARD","₹220"},{"#22c55e","VALUE","₹150"},{"#a78bfa","ACCESSIBLE","₹300"}};
        for (String[] p : priceInfo) {
            Rectangle sq = new Rectangle(22, 22, Color.web(p[0]));
            sq.setArcWidth(5); sq.setArcHeight(5);
            Label typeLbl = new Label(p[1]);
            typeLbl.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11; -fx-font-weight: bold;");
            Label amtLbl = new Label(p[2]);
            amtLbl.setStyle("-fx-text-fill: " + p[0] + "; -fx-font-size: 18; -fx-font-weight: bold;");
            VBox priceMeta = new VBox(2, typeLbl, amtLbl);
            HBox pCard = new HBox(10, sq, priceMeta);
            pCard.setAlignment(Pos.CENTER_LEFT);
            pCard.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #333333; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12 16 12 16;");
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
    //  SEAT SELECTION SCREEN
    // ══════════════════════════════════════════════════════════════════════
    void showSeatScreen() {
        seatButtons.clear();
        selectedSeats.clear();

        Button backBtn = styledBtn("← Back", "#333333", "#ffffff");
        backBtn.setOnAction(e -> showMovieDetail(currentMovie));

        Label movieLbl = new Label(currentMovie.title);
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
        glow.setArcWidth(5); glow.setArcHeight(5);
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
            String row       = ROWS[r];
            String type      = SEAT_TYPES[r];
            int    price     = PRICES[r];
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
        String[][] legendItems = {{"#22d3ee","PREMIUM ₹350"},{"#a855f7","STANDARD ₹220"},{"#22c55e","VALUE ₹150"},{"#a78bfa","ACCESSIBLE ₹300"},{"#f59e0b","Selected"},{"#dc2626","Booked"}};
        for (String[] li : legendItems) {
            Rectangle dot = new Rectangle(13, 13, Color.web(li[0]));
            dot.setArcWidth(3); dot.setArcHeight(3);
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
        proceedBtn.setStyle("-fx-background-color: linear-gradient(to right, #22d3ee, #a855f7); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 12 28 12 28; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");
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
            case "PREMIUM":    return new String[]{"#22d3ee","#f59e0b","#dc2626"};
            case "STANDARD":   return new String[]{"#a855f7","#f59e0b","#dc2626"};
            case "VALUE":      return new String[]{"#22c55e","#f59e0b","#dc2626"};
            case "ACCESSIBLE": return new String[]{"#a78bfa","#f59e0b","#dc2626"};
            default:           return new String[]{"#888888","#f59e0b","#dc2626"};
        }
    }

    String seatStyle(String color, boolean disabled) {
        return "-fx-background-color: " + color + "; -fx-border-color: transparent; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: " + (disabled ? "default" : "hand") + "; -fx-opacity: " + (disabled ? "0.45" : "1.0") + ";";
    }

    void updateBookingBar(Label totalLbl, Label seatsLbl) {
        int total = 0;
        for (String sid : selectedSeats) {
            String row = sid.replaceAll("\\d","");
            for (int i = 0; i < ROWS.length; i++) {
                if (ROWS[i].equals(row)) { total += PRICES[i]; break; }
            }
        }
        totalLbl.setText("Total: ₹" + total);
        seatsLbl.setText(selectedSeats.isEmpty() ? "No seats selected" : selectedSeats.size() + " seat(s): " + String.join(", ", selectedSeats));
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PAYMENT SCREEN
    // ══════════════════════════════════════════════════════════════════════
    void showPaymentScreen() {
        int total = 0;
        for (String sid : selectedSeats) {
            String row = sid.replaceAll("\\d","");
            for (int i = 0; i < ROWS.length; i++) {
                if (ROWS[i].equals(row)) { total += PRICES[i]; break; }
            }
        }
        final int finalTotal = total;
        int gst = (int)(finalTotal * 0.18);
        int convFee = 100;
        int ticketBase = finalTotal - gst - convFee;

        Label payTitle = new Label("🔐 Secure Checkout");
        payTitle.setFont(Font.font("Arial Black", FontWeight.BOLD, 20));
        payTitle.setTextFill(Color.WHITE);

        Label banner = new Label("  Fill the form below to complete your purchase.  ");
        banner.setStyle("-fx-background-color: #1a2a3a; -fx-text-fill: #7dd3fc; -fx-font-size: 12; -fx-padding: 8 14 8 14; -fx-background-radius: 8;");

        Label sec1 = sectionLabel("1 · Your Information");
        TextField firstName = payField("First Name");
        TextField lastName  = payField("Last Name");
        TextField email     = payField("Email Address");
        TextField phone     = payField("Phone Number");
        HBox nameRow = new HBox(12, firstName, lastName);

        Label sec2 = sectionLabel("2 · Payment Method");

        ToggleGroup methodGroup = new ToggleGroup();
        ToggleButton cardBtn = methodToggle("💳 Card", methodGroup);
        ToggleButton upiBtn  = methodToggle("📱 UPI", methodGroup);
        ToggleButton netBtn  = methodToggle("🏦 Net Banking", methodGroup);
        cardBtn.setSelected(true);
        HBox methodRow = new HBox(8, cardBtn, upiBtn, netBtn);

        TextField cardNum = payField("Card Number");
        cardNum.setPrefWidth(Integer.MAX_VALUE);
        TextField expiry  = payField("MM / YY");
        TextField cvv     = payField("CVV");
        HBox cardRow = new HBox(12, expiry, cvv);

        TextField upiId = payField("yourname@upi");
        upiId.setPrefWidth(Integer.MAX_VALUE);

        ComboBox<String> bankSelect = new ComboBox<>();
        bankSelect.getItems().addAll("SBI Bank","HDFC Bank","ICICI Bank","Axis Bank","Kotak Bank");
        bankSelect.setValue("SBI Bank");
        bankSelect.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: white; -fx-border-color: #333333; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 13; -fx-padding: 8;");
        bankSelect.setMaxWidth(Double.MAX_VALUE);

        StackPane cardPane = new StackPane(new VBox(10, cardNum, cardRow));
        StackPane upiPane  = new StackPane(upiId);
        StackPane netPane  = new StackPane(bankSelect);
        upiPane.setVisible(false); netPane.setVisible(false);
        StackPane methodContent = new StackPane(cardPane, upiPane, netPane);

        cardBtn.setOnAction(e -> { cardPane.setVisible(true); upiPane.setVisible(false); netPane.setVisible(false); });
        upiBtn.setOnAction(e  -> { cardPane.setVisible(false); upiPane.setVisible(true);  netPane.setVisible(false); });
        netBtn.setOnAction(e  -> { cardPane.setVisible(false); upiPane.setVisible(false); netPane.setVisible(true);  });

        Button payNowBtn = new Button("🔒  Pay ₹" + finalTotal + " Now");
        payNowBtn.setMaxWidth(Double.MAX_VALUE);
        payNowBtn.setStyle("-fx-background-color: linear-gradient(to right, #22c55e, #16a34a); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15; -fx-padding: 13; -fx-border-radius: 10; -fx-background-radius: 10; -fx-cursor: hand;");

        Label secureNote = new Label("🛡 Secured by RazorPay • PCI-DSS Compliant");
        secureNote.setStyle("-fx-text-fill: #555555; -fx-font-size: 11;");
        secureNote.setAlignment(Pos.CENTER);

        payNowBtn.setOnAction(e -> {
            payNowBtn.setDisable(true);
            payNowBtn.setText("⏳ Processing Payment...");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(ev -> showTicketScreen(finalTotal));
            pause.play();
        });

        VBox leftPanel = new VBox(12, payTitle, banner, sec1, nameRow, email, phone, sec2, methodRow, methodContent, payNowBtn, secureNote);
        leftPanel.setPadding(new Insets(30, 30, 30, 30));
        leftPanel.setStyle("-fx-background-color: #0f0f1a;");
        leftPanel.setPrefWidth(520);

        ImageView posterImg = new ImageView();
        posterImg.setFitWidth(240); posterImg.setFitHeight(150);
        posterImg.setPreserveRatio(false);
        try { posterImg.setImage(new Image(currentMovie.imageUrl, 240, 150, false, true, true)); } catch (Exception ignored) {}

        Label ordTitle = new Label("🎟 Purchase Details");
        ordTitle.setFont(Font.font("Arial Black", FontWeight.BOLD, 15));
        ordTitle.setTextFill(Color.web("#f59e0b"));

        Label ordMovie = new Label(currentMovie.title);
        ordMovie.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ordMovie.setTextFill(Color.WHITE);
        ordMovie.setWrapText(true);

        Label ordTime  = orderMeta("🕐 " + currentTime + "  |  Screen 5");
        Label ordSeats = orderMeta("💺 " + String.join(", ", selectedSeats));

        Separator sep1 = new Separator(); sep1.setStyle("-fx-background-color: #333333;");
        Label ordBase  = orderRow("Net Ticket(s) Price", "₹" + ticketBase);
        Label ordGst   = orderRow("GST (18%)", "₹" + gst);
        Label ordConv  = orderRow("Convenience Fee", "₹" + convFee);
        Separator sep2 = new Separator(); sep2.setStyle("-fx-background-color: #333333;");

        Label ordTotal = new Label("Total:  ₹" + finalTotal);
        ordTotal.setFont(Font.font("Arial Black", FontWeight.BOLD, 18));
        ordTotal.setTextFill(Color.web("#f59e0b"));

        Label features = new Label("✅ Access Online\n✅ Nearby Location\n✅ Mobile Transfer Available\n✅ Multi-Screen Available");
        features.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12; -fx-line-spacing: 4;");

        VBox rightPanel = new VBox(12, ordTitle, posterImg, ordMovie, ordTime, ordSeats, sep1, ordBase, ordGst, ordConv, sep2, ordTotal, features);
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
    //  TICKET / CONFIRMATION SCREEN
    // ══════════════════════════════════════════════════════════════════════
    void showTicketScreen(int total) {
        String bookingId = "TRK" + Integer.toHexString((int)(Math.random()*0xFFFFFF)).toUpperCase();
        String qrCode    = "QR-" + Long.toHexString(System.currentTimeMillis()).toUpperCase().substring(4);
        int gst     = (int)(total * 0.18);
        int convFee = 100;
        int tickBase = total - gst - convFee;

        VBox screen = new VBox();
        screen.setStyle("-fx-background-color: linear-gradient(to bottom right, #1a1a2e, #16213e, #0f3460);");
        screen.setAlignment(Pos.CENTER);
        screen.setPadding(new Insets(30));

        VBox ticket = new VBox();
        ticket.setPrefWidth(330);
        ticket.setMaxWidth(330);
        ticket.setStyle("-fx-background-color: white; -fx-border-radius: 18; -fx-background-radius: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.7), 40, 0, 0, 10);");

        Label shareRow = new Label("📤  SHARE YOUR TICKET");
        shareRow.setStyle("-fx-text-fill: #7dd3fc; -fx-font-size: 12; -fx-font-weight: bold;");

        ImageView tPoster = new ImageView();
        tPoster.setFitWidth(100); tPoster.setFitHeight(130);
        tPoster.setPreserveRatio(false);
        try { tPoster.setImage(new Image(currentMovie.imageUrl, 100, 130, false, true, true)); } catch (Exception ignored) {}
        tPoster.setStyle("-fx-border-color: #f59e0b; -fx-border-width: 3; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label tMovieName = new Label(currentMovie.title);
        tMovieName.setFont(Font.font("Arial Black", FontWeight.BOLD, 14));
        tMovieName.setTextFill(Color.WHITE);
        tMovieName.setWrapText(true);
        tMovieName.setMaxWidth(240);

        Label tLang = new Label(currentMovie.lang.split(",")[0] + ", 2D");
        tLang.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12;");
        Label tDate = new Label("📅 " + java.time.LocalDate.now() + "  |  🕐 " + currentTime);
        tDate.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11;");
        Label tVenue = new Label("📍 PVR: Elan Miracle, Sec 84  |  Screen 5");
        tVenue.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11;");
        tVenue.setWrapText(true);

        VBox topSection = new VBox(8, shareRow, tPoster, tMovieName, tLang, tDate, tVenue);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(20, 16, 16, 16));
        topSection.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #0f3460); -fx-border-radius: 18 18 0 0; -fx-background-radius: 18 18 0 0;");

        Label dash = new Label("- - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        dash.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11;");
        HBox dashes = new HBox(dash);
        dashes.setAlignment(Pos.CENTER);
        dashes.setPadding(new Insets(2,0,2,0));

        Label seatCountLbl = new Label(selectedSeats.size() + " Ticket(s)");
        seatCountLbl.setStyle("-fx-text-fill: #111111; -fx-font-weight: bold; -fx-font-size: 13;");
        Label screenLbl2 = new Label("SCREEN 5");
        screenLbl2.setStyle("-fx-text-fill: #888888; -fx-font-size: 11;");
        Label screenIdLbl = new Label(qrCode);
        screenIdLbl.setStyle("-fx-text-fill: #888888; -fx-font-size: 10;");

        GridPane qrGrid = new GridPane();
        qrGrid.setHgap(1); qrGrid.setVgap(1);
        Random rng = new Random(bookingId.hashCode());
        for (int r2 = 0; r2 < 10; r2++) {
            for (int c2 = 0; c2 < 10; c2++) {
                Rectangle cell = new Rectangle(6, 6, rng.nextBoolean() ? Color.BLACK : Color.WHITE);
                cell.setArcWidth(1); cell.setArcHeight(1);
                qrGrid.add(cell, c2, r2);
            }
        }
        VBox qrBox = new VBox(8, qrGrid, screenIdLbl);
        qrBox.setAlignment(Pos.CENTER);
        qrBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12;");

        Label bookIdLbl = new Label("BOOKING ID   " + bookingId);
        bookIdLbl.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #111111; -fx-font-size: 11; -fx-font-weight: bold; -fx-padding: 8 12 8 12; -fx-background-radius: 8;");

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
        successBadge.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-padding: 12; -fx-alignment: center;");
        successBadge.setMaxWidth(Double.MAX_VALUE);

        Button homeBtn = new Button("🏠  Back to Home");
        homeBtn.setMaxWidth(Double.MAX_VALUE);
        homeBtn.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #7dd3fc; -fx-font-weight: bold; -fx-font-size: 13; -fx-padding: 12; -fx-border-radius: 0 0 18 18; -fx-background-radius: 0 0 18 18; -fx-cursor: hand;");
        homeBtn.setOnAction(e -> { bookedSeats.addAll(selectedSeats); selectedSeats.clear(); showHomeScreen(); });

        ticket.getChildren().addAll(topSection, dashes, bodySection, successBadge, homeBtn);

        ticket.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(600), ticket);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        screen.getChildren().add(ticket);

        ScrollPane sp = new ScrollPane(screen);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Scene scene = new Scene(sp, 900, 620);
        primaryStage.setScene(scene);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════
    Button styledBtn(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + fg + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8 16 8 16; -fx-cursor: hand; -fx-font-size: 13;");
        return b;
    }

    TextField payField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #333333; -fx-text-fill: white; -fx-prompt-text-fill: #666666; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10 14 10 14; -fx-font-size: 13;");
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
        tb.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #aaaaaa; -fx-border-color: #333333; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 9 16 9 16; -fx-font-size: 13; -fx-cursor: hand;");
        tb.selectedProperty().addListener((obs, o, selected) -> {
            if (selected)
                tb.setStyle("-fx-background-color: #22d3ee22; -fx-text-fill: #22d3ee; -fx-border-color: #22d3ee; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 9 16 9 16; -fx-font-size: 13; -fx-cursor: hand;");
            else
                tb.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #aaaaaa; -fx-border-color: #333333; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 9 16 9 16; -fx-font-size: 13; -fx-cursor: hand;");
        });
        return tb;
    }

    public static void main(String[] args) {
        launch(args);
    }
}