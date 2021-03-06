import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("Duplicates")
public class Kuldvillak extends Application {

    // Loeme küsimused failist sisse
    List<Küsimus> küsimused = loeKüsimused();
    // pealava on isendiväljana, sest nii on lihtsam sellele ligi pääseda kui vaja
    Stage peaLava;
    // vastamiste arv näitab seda, mitu mängijat on hetkel avatud küsimusele vastanud
    int vastamisteArv=1;
    // mitu mängijat mängib antud mängus, seda küsitakse programmi avamisel
    int mängijateArv=1;

    // Mängijaid saab maksimaalselt olla 3
    Mängija mängija1=null;
    Mängija mängija2=null;
    Mängija mängija3=null;

    // Mängijate punktid
    Label mängija1punktid = new Label();
    Label mängija2punktid = new Label();
    Label mängija3punktid = new Label();


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage peaLava) throws Exception {

        this.peaLava = peaLava;
        peaLava.setTitle("Kuldvillak");
        // ikooni lisamine aknale
        peaLava.getIcons().add(new Image("https://images-na.ssl-images-amazon.com/images/I/31M2GpmH5VL.png"));

        // mängijate arvu küsimine ja mängijate lisamine
        mängijateLisamine();

        // juure ja stseeni loomine
        BorderPane juur = new BorderPane();
        juur.setPadding(new Insets(5));
        Scene scene = new Scene(juur, 800, 570);
        scene.getStylesheets().add("style.css");  //css lisamine
        peaLava.setScene(scene);

        // mänguakna minimaalne suurus
        peaLava.setMinWidth(600);
        peaLava.setMinHeight(400);
        // suurust muutes säilitab kõrguse-laiuse suhte
        peaLava.minWidthProperty().bind(scene.heightProperty().multiply(1.5));
        peaLava.minHeightProperty().bind(scene.widthProperty().divide(1.5));

        // MÄNGURUUDUSTIKU LOOMINE
        GridPane valikuteRuudustik = mänguruudustikuLoomine();
        juur.setCenter(valikuteRuudustik);

        // Punktiriba
        HBox punktiriba = punktiRibaLoomine();
        juur.setBottom(punktiriba);

        // sündmuse lisamine, kui soovitakse mängu sulgeda
        aknaSulgemiseKinnitus(peaLava,"Kas soovite tõesti mängimise lõpetada?");

        peaLava.show();

    }//start




    //// MEETODID ////

    /**
     * Loeb küsimuste failist sisse seal olevad küsimused, lisab need listi ning tagastab listi.
     * Küsimuste failis on küsimused kujul(vv=vastusevariant):
     * "teema;küsimus;vv1;vv2;vv3;vv4;õige vastuse nr;õige vastuse eest saadavad punktid"
     */
    public static List<Küsimus> loeKüsimused() {
        List<Küsimus> küsimute_list = new ArrayList<>();

        try (FileInputStream sisendVoog = new FileInputStream("küsimused.txt");
             InputStreamReader lugeja = new InputStreamReader(sisendVoog, "UTF-8");
             BufferedReader puhvriga_lugeja = new BufferedReader(lugeja)) {
            //Sulgeb ise vood pärast

            while (true) {
                String rida = puhvriga_lugeja.readLine();
                if (rida == null) {
                    break;
                }

                String[] tükid = rida.split(";");

                // igaksjuhuks kontroll, kas küsimus on õigesti koostatud
                if (tükid.length == 8) {
                    Küsimus uus_küsimus = new Küsimus(tükid[0], tükid[1], tükid[2], tükid[3], tükid[4], tükid[5],
                            Integer.parseInt(tükid[6]), Integer.parseInt(tükid[7]));
                    küsimute_list.add(uus_küsimus);
                }
                // kui valesti koostatud, siis jätab küsimuse vahele, mitte ei jookse kokku
                else if (tükid.length != 8) {
                    System.out.println(rida + " - OLI VALESTI KOOSTATUD");
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }

        return küsimute_list;
    }

    //Meetod kirjutab faili mängija nime ja lõppskoori

    public void failiKirjutamine() {
        String fileContent ="Nimi: " + mängija1.getNimi() + " skoor: " + mängija1.getPunktid();
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("kuldvillaku_tulemused.txt", true)));
            out.println(fileContent);
            out.close();
        } catch (IOException e) {
        }
    }


    /**
     * Küsib mängijate arvu, ning seejärel loob mängijad, küsides iga mängija nime.
     */
    public void mängijateLisamine(){
        // mängijate arvu küsimist hetkel pole kasutusel, sest mäng toimib praegu ainult ühe mängijaga
    /*    // Mängijate arvu küsimise aken
        ChoiceDialog arvuKüsimine = new ChoiceDialog();
        arvuKüsimine.setTitle("");
        arvuKüsimine.setHeaderText("Kuldvillak");
        arvuKüsimine.setContentText("Vali mängijate arv: ");
        arvuKüsimine.getItems().addAll(1,2,3);
        Optional<Integer> mängijateArv = arvuKüsimine.showAndWait();

        if (mängijateArv.isPresent()){
            this.mängijateArv = mängijateArv.get();
        }
        else if(!mängijateArv.isPresent()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("Mängijate arv on valimata!");
            alert.showAndWait();
        }
        else {
            System.exit(0);
        }
    */

        // Mängija nime küsimise aken
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("");
        dialog.setHeaderText("Kuldvillak");
        dialog.setContentText("Palun sisesta oma nimi: ");

        String nimi = "";
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            nimi = result.get();
        }
        else {
            System.exit(0);
        }
        //Loon sisendi põhjal uue mängija
        Mängija uus_mängija = new Mängija(nimi);
        this.mängija1=uus_mängija;
    }


    /**
     * Loob 6 rea ja 5 veeruga mänguruudustiku ja tagastab selle. Iga teema jaoks on 1 veerg ning
     * igas veerus on ülevalt esimeses reas teema nimetus ning selle all 5 küsimust.
     */
    public GridPane mänguruudustikuLoomine(){

        GridPane mänguRuudustik = new GridPane();
        mänguRuudustik.setHgap(5);
        mänguRuudustik.setVgap(5);

        // kõik 5 veerud on sama laiusega
        for (int i = 0; i < 5; i++) {
            ColumnConstraints veerg = new ColumnConstraints();
            veerg.setPercentWidth(20);
            mänguRuudustik.getColumnConstraints().add(veerg);
        }

        // kõik 6 rida on sama kõrgusega
        for (int i = 0; i < 6; i++) {
            RowConstraints rida = new RowConstraints();
            rida.setPercentHeight(20);
            mänguRuudustik.getRowConstraints().add(rida);
        }

        // teemade sildid
        Label teema1 = new Label("Sport".toUpperCase());
        Label teema2 = new Label("Ajalugu".toUpperCase());
        Label teema3 = new Label("Varia".toUpperCase());
        Label teema4 = new Label("Ralli".toUpperCase());
        Label teema5 = new Label("Autod".toUpperCase());

        List<Label> teemad = Arrays.asList(teema1, teema2, teema3, teema4, teema5);
        teemad.forEach(teema -> teema.setFont(Font.font("Verdana", FontWeight.BOLD, 20)));

        // lisame teemade sildid mängulauale
        lisaRitta(teemad, mänguRuudustik, 0);
        teemad.forEach(teema -> teema.setAlignment(Pos.CENTER));


        // loome 1. teema küsimuste jaoks nupud ning lisame mängulauale
        Button küsimus100_1 = new Button("100");
        Button küsimus200_1 = new Button("200");
        Button küsimus300_1 = new Button("300");
        Button küsimus400_1 = new Button("400");
        Button küsimus500_1 = new Button("500");
        List<Button> esimeseTeemaNupud = Arrays.asList(küsimus100_1, küsimus200_1, küsimus300_1, küsimus400_1, küsimus500_1);
        lisaVeergu(esimeseTeemaNupud, mänguRuudustik, 1);


        // loome 2. teema küsimuste jaoks nupud ning lisame mängulauale
        Button küsimus100_2 = new Button("100");
        Button küsimus200_2 = new Button("200");
        Button küsimus300_2 = new Button("300");
        Button küsimus400_2 = new Button("400");
        Button küsimus500_2 = new Button("500");
        List<Button> teiseTeemaNupud = Arrays.asList(küsimus100_2, küsimus200_2,küsimus300_2,küsimus400_2,küsimus500_2);
        lisaVeergu(teiseTeemaNupud, mänguRuudustik, 2);


        // loome 3. teema küsimuste jaoks nupud ning lisame mängulauale
        Button küsimus100_3 = new Button("100");
        Button küsimus200_3 = new Button("200");
        Button küsimus300_3 = new Button("300");
        Button küsimus400_3 = new Button("400");
        Button küsimus500_3 = new Button("500");
        List<Button> kolmandaTeemaNupud = Arrays.asList(küsimus100_3,küsimus200_3,küsimus300_3,küsimus400_3,küsimus500_3);
        lisaVeergu(kolmandaTeemaNupud, mänguRuudustik, 3);

        // loome 4. teema küsimuste jaoks nupud ning lisame mängulauale
        Button küsimus100_4 = new Button("100");
        Button küsimus200_4 = new Button("200");
        Button küsimus300_4 = new Button("300");
        Button küsimus400_4 = new Button("400");
        Button küsimus500_4 = new Button("500");
        List<Button> neljandaTeemaNupud = Arrays.asList(küsimus100_4,küsimus200_4,küsimus300_4,küsimus400_4,küsimus500_4);
        lisaVeergu(neljandaTeemaNupud, mänguRuudustik, 4);

        // loome 5. teema küsimuste jaoks nupud ning lisame mängulauale
        Button küsimus100_5 = new Button("100");
        Button küsimus200_5 = new Button("200");
        Button küsimus300_5 = new Button("300");
        Button küsimus400_5 = new Button("400");
        Button küsimus500_5 = new Button("500");
        List<Button> viiendaTeemaNupud = Arrays.asList(küsimus100_5,küsimus200_5,küsimus300_5,küsimus400_5,küsimus500_5);
        lisaVeergu(viiendaTeemaNupud, mänguRuudustik, 5);

        // Sündmuste lisamine küsimuste nuppudele:
        // Teema: SPORT
        lisaSündmusedNuppudele(esimeseTeemaNupud,"Sport");

        // Teema: AJALUGU
        lisaSündmusedNuppudele(teiseTeemaNupud,"Ajalugu");

        // Teema: VARIA
        lisaSündmusedNuppudele(kolmandaTeemaNupud,"Varia");

        // Teema: RALLI
        lisaSündmusedNuppudele(neljandaTeemaNupud,"Ralli");

        // Teema: AUTOD
        lisaSündmusedNuppudele(viiendaTeemaNupud,"Autod");


        return mänguRuudustik;
    }

    /**
     *
     */
    public HBox punktiRibaLoomine(){
        HBox punktiriba = new HBox();
        punktiriba.setPadding(new Insets(5,20,5,20));

        if(mängijateArv>=1) {
            mängija1punktid.setText(mängija1.getNimi() + ": " + mängija1.getPunktid());
        }
        if(mängijateArv>=2) {
            mängija2punktid.setText(mängija2.getNimi()+": " + mängija2.getPunktid());
        }
        if(mängijateArv>=3) {
            mängija3punktid.setText(mängija3.getNimi() + ": " + mängija3.getPunktid());
        }

        punktiriba.getChildren().addAll(mängija1punktid,mängija2punktid,mängija3punktid);

        return punktiriba;
    }


    /**
     * Lisab etteantud sildid mänguruudustikku antud numbriga ritta.
     */
    public static void lisaRitta(List<Label> sildid, GridPane mänguRuudustik, int reaNr) {
        for (int i = 0; i < sildid.size(); i++) {
            Label silt = sildid.get(i);
            mänguRuudustik.add(silt, i, reaNr);
            silt.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }


    /**
     * Lisab etteantud nupud mänguruudustikku antud numbriga veergu.
     */
    public static void lisaVeergu(List<Button> nupud, GridPane mänguRuudustik, int veeruNr) {
        for (int i = 0; i < nupud.size(); i++) {
            Button nupp = nupud.get(i);
            mänguRuudustik.add(nupp, veeruNr-1, i+1);
            nupp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }


    /**
     * Kui antud lava üritatakse sulgeda, siis küsitakse uues aknas kinnitust sulgemise kohta.
     */
    public void aknaSulgemiseKinnitus(Stage lava, String sõnum){
        lava.setOnCloseRequest(event -> {
            // luuakse teine lava
            Stage kusimus = new Stage();
            // küsimuse ja kahe nupu loomine
            Label label = new Label(sõnum);

            failiKirjutamine(); //iga kord kui aken suletakse, siis kirjutatakse mängija lõpptulemus faili

            Button okButton = new Button("Jah");
            Button cancelButton = new Button("Ei");

            // sündmuse lisamine nupule Jah
            okButton.setOnAction(event1 -> {
                kusimus.hide();
            });

            // sündmuse lisamine nupule Ei
            cancelButton.setOnAction(event12 -> {
                lava.show();
                kusimus.hide();
            });

            // nuppude grupeerimine
            FlowPane pane = new FlowPane(10, 10);
            pane.setAlignment(Pos.CENTER);
            pane.getChildren().addAll(okButton, cancelButton);

            // küsimuse ja nuppude gruppi paigutamine
            VBox vBox = new VBox(10);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(label, pane);

            //stseeni loomine ja näitamine
            Scene stseen = new Scene(vBox, 300, 100);
            kusimus.setScene(stseen);
            kusimus.setResizable(false);
            kusimus.show();
            kusimus.setAlwaysOnTop(true);
        }); //siin lõpeb aknasündmuse kirjeldus

    }


    /**
     * Lisab antud nuppudele sündmused, mis käivituvad, kui nuppe vajutada.
     */
    public void lisaSündmusedNuppudele(List<Button> nupud, String teema){
        for (int i = 0; i < nupud.size(); i++) {
            nupuvajutus(nupud.get(i),teema, 100*(i+1));
        }
    }


    /**
     * Lisab antud nupule sündmuse, mis käivitub, kui seda vajutada.
     */
    public void nupuvajutus(Button nupp, String teema, int väärtus) {
        nupp.setOnMouseClicked(event -> {
            Küsimus uus = loosi_suvaline_küsimus(teema, väärtus);
            küsimuseAken(uus);
            // kui küsimus on ühe korra avatud, siis seda uuesti valida ei saa
            nupp.setDisable(true);
        });
    }


    /**
     * Valib failist loetud küsimuste hulgast suvalise küsimuse ja tagastab selle.
     */
    public Küsimus loosi_suvaline_küsimus(String teema, int väärtus) {

        Küsimus loositav_küsimus;

        while (true) {
            int max = küsimused.size();
            int min = 0;
            int range = max - min;

            int rand = (int)(Math.random() * range) + min;

            loositav_küsimus = küsimused.get(rand);

            // kontrollib kas suvaliselt valitud küsimus on sobiv ja kui on siis tagastab selle
            if (teema.equals(loositav_küsimus.getTeema()) && loositav_küsimus.getVäärtus() == väärtus) {
                return loositav_küsimus;
            }
        }
    }


    /**
     * Loob antud küsimuse kohta küsimuse akna, kus on 4 valikvastust.
     */
    public void küsimuseAken(Küsimus uus_küsimus) {

        Stage küsimuseAken = new Stage();
        //küsimuseAken.setHeight(380);
        //küsimuseAken.setWidth(700);
        küsimuseAken.initStyle(StageStyle.UTILITY);
        Group juur = new Group();
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));

        // küsimuse tekst
        Text tekst = new Text(uus_küsimus.getKüsimus());
        tekst.setFont(new Font(15));

        // valikuvariandid
        RadioButton valik1 = new RadioButton(uus_küsimus.getVastus1());
        valik1.setFont(new Font(13));
        RadioButton valik2 = new RadioButton(uus_küsimus.getVastus2());
        valik2.setFont(new Font(13));
        RadioButton valik3 = new RadioButton(uus_küsimus.getVastus3());
        valik3.setFont(new Font(13));
        RadioButton valik4 = new RadioButton(uus_küsimus.getVastus4());
        valik4.setFont(new Font(13));
        ToggleGroup valikud = new ToggleGroup();
        valikud.getToggles().addAll(valik1,valik2,valik3,valik4);

        // vastamise nupp
        Button vastaNupp = new Button();
        //vastaNupp.setPrefSize(80, 30);
        vastaNupp.setText("Vasta");

        // vastamisel antakse tagasisidet
        Text tagasiside = new Text();
        Text õigeVastus = new Text();
        int vastamisteArv=1;
        vastaNupp.setOnMouseClicked(event -> tagasiside(vastaNupp,valik1,valik2,valik3,valik4,tagasiside,
                                                        õigeVastus,uus_küsimus,küsimuseAken));

        // lisame küsimuse komponendid juurele
        vbox.getChildren().addAll(tekst,valik1, valik2, valik3,valik4, vastaNupp, tagasiside,õigeVastus);
        juur.getChildren().add(vbox);

        // stseeni loomine
        Scene stageScene = new Scene(juur, 700, 380, Color.LIGHTBLUE);
        stageScene.getStylesheets().add("küsimuseaken.css");
        tekst.getStyleClass().add("tekst");
        küsimuseAken.setScene(stageScene);
        küsimuseAken.setResizable(false);
        küsimuseAken.show();
        küsimuseAken.setAlwaysOnTop(true);

        // kui üritatakse akent kinni panna enne vastamist, küsib kinnitust
        aknaSulgemiseKinnitus(küsimuseAken, "Olete kindel, et ei soovi küsimusele vastata?");


    }


    //Siin klassis veits palju kordusi
    /**
     * Annab antud küsimuse aknas olevale küsimusele vastates samas aknas tagasisidet.
     */
    public void tagasiside(Button nupp,RadioButton valik1, RadioButton valik2, RadioButton valik3,RadioButton valik4,
                      Text tagasiside, Text õigeVastus, Küsimus küsimus, Stage küsimuseAken){

        if (valik1.isSelected() && küsimus.getÕige_vastuse_nr() == 0 && !valik2.isSelected()
                && !valik3.isSelected() && !valik4.isSelected()) {
            tagasiside.setText("Sinu vastus on õige." + System.lineSeparator() + "Said juurde: " + küsimus.getVäärtus() + " punkti");
            tagasiside.setFill(Color.GREEN);
            valik1.setTextFill(Color.GREEN);
            valik2.setTextFill(Color.RED);
            valik3.setTextFill(Color.RED);
            valik4.setTextFill(Color.RED);
        } else if (valik2.isSelected() && küsimus.getÕige_vastuse_nr() == 1 && !valik1.isSelected()
                && !valik3.isSelected() && !valik4.isSelected()) {
            tagasiside.setText("Sinu vastus on õige." + System.lineSeparator() + "Said juurde: " + küsimus.getVäärtus() + " punkti");
            tagasiside.setFill(Color.GREEN);
            valik2.setTextFill(Color.GREEN);
            valik1.setTextFill(Color.RED);
            valik3.setTextFill(Color.RED);
            valik4.setTextFill(Color.RED);
        } else if (valik3.isSelected() && küsimus.getÕige_vastuse_nr() == 2 && !valik1.isSelected()
                && !valik2.isSelected() && !valik4.isSelected()) {
            tagasiside.setText("Sinu vastus on õige." + System.lineSeparator() + "Said juurde: " + küsimus.getVäärtus() + " punkti");
            tagasiside.setFill(Color.GREEN);
            valik3.setTextFill(Color.GREEN);
            valik2.setTextFill(Color.RED);
            valik1.setTextFill(Color.RED);
            valik4.setTextFill(Color.RED);
        } else if (valik4.isSelected() && küsimus.getÕige_vastuse_nr() == 3 && !valik1.isSelected()
                && !valik2.isSelected() && !valik3.isSelected()) {
            tagasiside.setText("Sinu vastus on õige." + System.lineSeparator() + "Said juurde: " + küsimus.getVäärtus() + " punkti");
            tagasiside.setFill(Color.GREEN);
            valik4.setTextFill(Color.GREEN);
            valik1.setTextFill(Color.RED);
            valik3.setTextFill(Color.RED);
            valik2.setTextFill(Color.RED);
        } else {
            tagasiside.setText("Sinu vastus on vale.");
            tagasiside.setFill(Color.RED);
            mängija1.vähendaPunkte(küsimus.getVäärtus());
            mängija1punktid.setText(mängija1.getNimi() + ": " + mängija1.getPunktid());
        }

        // pärast õigesti vastamist ei saa enam uuesti vastusevariante valida ega vasta nupule vajutada
        if(tagasiside.getText().contains("õige")) {
            valik1.setDisable(true);
            valik2.setDisable(true);
            valik3.setDisable(true);
            valik4.setDisable(true);
            nupp.setDisable(true);
            vastamisteArv=1;
            mängija1.suurendaPunkte(küsimus.getVäärtus());
            mängija1punktid.setText(mängija1.getNimi() + ": " + mängija1.getPunktid());


            // kui on küsimusele vastatud, siis küsimuse akent sulgedes ei küsita kinnitust sulgemise kohta
            küsimuseAken.setOnCloseRequest(event -> küsimuseAken.hide());
        }
        // kui kõik mängijad vastavad valesti, näitab õiget vastust
        // ning ei saa enam uuesti vastusevariante valida ega vasta nupule vajutada
        else if(vastamisteArv==mängijateArv && tagasiside.getText().contains("vale")){
            õigeVastus.setText("Õige vastus:" + System.lineSeparator() + "\t" + küsimus.getÕigeVastus());
            valik1.setDisable(true);
            valik2.setDisable(true);
            valik3.setDisable(true);
            valik4.setDisable(true);
            nupp.setDisable(true);
            vastamisteArv=1;


            // kui on küsimusele vastatud, siis küsimuse akent sulgedes ei küsita kinnitust sulgemise kohta
            küsimuseAken.setOnCloseRequest(event -> küsimuseAken.hide());
        }
        // kui pole õigesti vastatud ja kõk mängijad pole veel vastanud
        else {
            vastamisteArv += 1;
        }
    }

}
