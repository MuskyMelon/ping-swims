class ScoreSave {
  Table table; // a table fot the data
  final String filename = "saveFile.csv"; // name and path of the file
  boolean isSaved; // check if it is saved

  final int listlength = 10; 
  int [] score = new int [listlength];
  String [] name = new String [listlength];

  int tempHighscore;
  String tempName;
  int check;

  void setup() {
    isSaved = false;
    File f = new File(dataPath(filename));
    if (f.exists()) {
      print("exist");
    } else {
      table = new Table();
      table.addColumn("id");
      table.addColumn("Name");
      table.addColumn("Highscore");


      table.addRow();
      table.setString(0, "Name", "Mar");
      table.setInt(0, "Highscore", 90);

      table.addRow();
      table.setString(1, "Name", "Jef");
      table.setInt(1, "Highscore", 80);

      table.addRow();
      table. setString(2, "Name", "Max");
      table.setInt(2, "Highscore", 70);

      table.addRow();
      table.setString(3, "Name", "Kel");
      table.setInt(3, "Highscore", 60);

      table.addRow();
      table.setString(4, "Name", "Kev");
      table.setInt(4, "Highscore", 50);

      table.addRow();
      table.setString(5, "Name", "Mar");
      table.setInt(5, "Highscore", 40);

      table.addRow();
      table.setString(6, "Name", "Jef");
      table.setInt(6, "Highscore", 30);
      table.addRow();
      table.setString(7, "Name", "Max");
      table.setInt(7, "Highscore", 20);

      table.addRow();
      table.setString(8, "Name", "Kel");
      table.setInt(8, "Highscore", 10);

      table.addRow();
      table.setString(9, "Name", "Kev");
      table.setInt(9, "Highscore", 5);
      saveTable(table, "./data/" + filename);
    }

    table = loadTable("./data/" + filename, "header");

    for (int i = 0; i < listlength; i++) {
      println(i);
      tempHighscore = table.getInt(i, "Highscore");
      println(i+tempHighscore);
      score[i] = tempHighscore;
      tempName = table.getString(i, "Name");
      name[i] = tempName;
    }
  }
  void scoreCheck(int newScore, String newName) {
    if (isSaved == false) {
      print("Name:" + newName);
      check = 0; // added this, everything else is fine
      if (newScore > score[0]) { 
        check = 10;
      } else if (newScore > score[1]) { 
        check = 9;
      } else if (newScore > score[2]) { 
        check = 8;
      } else if (newScore > score[3]) { 
        check = 7;
      } else if (newScore > score[4]) { 
        check = 6;
      } else if (newScore > score[5]) { 
        check = 5;
      } else if (newScore > score[6]) { 
        check = 4;
      } else if (newScore > score[7]) { 
        check = 3;
      } else if (newScore > score[8]) { 
        check = 2;
      } else if (newScore > score[9]) { 
        check = 1;
      }

      if (check == 1) {
        score[9] = newScore;

        name[9] = newName;
      } else if (check == 2) {
        score[9]  = score[8];
        score[8] = newScore;

        name[9] = name[8];
        name[8] = newName;
      } else if (check == 3) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = newName;
      } else if (check == 4) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = newName;
      } else if (check == 5) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = newName;
      } else if (check == 6) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = newName;
      } else if (check == 7) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = score[3];
        score[3] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = name[3];
        name[3] = newName;
      } else if (check == 8) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = score[3];
        score[3] = score[2];
        score[2] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = name[3];
        name[3] = name[2];
        name[2] = newName;
      } else if (check == 9) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = score[3];
        score[3] = score[2];
        score[2] = score[1];
        score[1] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = name[3];
        name[3] = name[2];
        name[2] = name[1];
        name[1] = newName;
      } else if (check == 10) {
        score[9] = score[8];
        score[8] = score[7];
        score[7] = score[6];
        score[6] = score[5];
        score[5] = score[4];
        score[4] = score[3];
        score[3] = score[2];
        score[2] = score[1];
        score[1] = score[0];
        score[0] = newScore;

        name[9] = name[8];
        name[8] = name[7];
        name[7] = name[6];
        name[6] = name[5];
        name[5] = name[4];
        name[4] = name[3];
        name[3] = name[2];
        name[2] = name[1];
        name[1] = name[0];
        name[0] = newName;
      }
      table.clearRows();
      for (int i = 0; i < listlength; i++) {
        println (name[i], score[i]);
        table.addRow();
        table.setString(i, "Name", name[i]);
        table.setInt(i, "Highscore", score[i]);
      }

      saveTable(table, "./data/" + filename);
    }
    isSaved = true;
  }

  void ScoreScreen() {
  }
}