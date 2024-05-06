import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;

//Abdullah Hazuri 220218341 START

public class Server {

  private int port;
  private List<User> clients;
  private ServerSocket server;

  // port is set "12354" by deafult for testing

  public static void main(String[] args) throws IOException {
    new Server(12345).run();
  }

  public Server(int port) {
    this.port = port;
    this.clients = new ArrayList<User>();
  }

  public void run() throws IOException {
    server = new ServerSocket(port);
    System.out.println("CENG216 Instant Messaging Group Project");
    System.out.println("Port 12345 is now open.");

    while (true) {
      // accepts a new client
      Socket client = server.accept();

      // get nickname of newUser
      String nickname = (new Scanner(client.getInputStream())).nextLine();
      nickname = nickname.replace(",", ""); //  remove "," from username
      nickname = nickname.replace(" ", "_");// replace spaces with "_"
      System.out.println("New Client: \"" + nickname + "\"\n\t");

      // create new User
      User newUser = new User(client, nickname);

      // add newUser to user list
      this.clients.add(newUser);

      // Welcome msg
      newUser.getOutStream().println(
          "<b>Welcome</b> " + newUser.toString());

      // create a new thread for newUser incoming msgs handling
      new Thread(new UserHandler(this, newUser)).start();
    }
  }

  //Abdullah Hazuri finish

  //Mohanad Qaid 210218301 START

  // delete a user from the list Function
  public void removeUser(User user) {
    this.clients.remove(user);
  }

  // send incoming msg to all Users
  public void broadcastMessages(String msg, User userSender) {
    for (User client : this.clients) {
      client.getOutStream().println(
          userSender.toString() + "<span>: " + msg + "</span>");
    }
  }

  // send list of clients to all Users Function
  public void broadcastAllUsers() {
    for (User client : this.clients) {
      client.getOutStream().println(this.clients);
    }
  }

  // send messag to a User (String)
  public void sendMessageToUser(String msg, User userSender, String user) {
    boolean find = false;
    for (User client : this.clients) {
      if (client.getNickname().equals(user) && client != userSender) {
        find = true;
        userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() + ": " + msg);
        client.getOutStream().println(
            "(<b>Private</b>)" + userSender.toString() + "<span>: " + msg + "</span>");
      }
    }
    if (!find) {
      userSender.getOutStream().println(userSender.toString() + " -> (<b>no one!</b>): " + msg);
    }
  }
}


//Mohanad Qaid finish



//Mohammed Ahmed 210218302 START

class UserHandler implements Runnable {

  private Server server;
  private User user;

  public UserHandler(Server server, User user) {
    this.server = server;
    this.user = user;
    this.server.broadcastAllUsers();
  }

  public void run() {
    String message;

    // when there is a new message, broadcast to all
    Scanner sc = new Scanner(this.user.getInputStream());
    while (sc.hasNextLine()) {
      message = sc.nextLine();

      // private msg
      if (message.charAt(0) == '@') {
        if (message.contains(" ")) {
          System.out.println("private msg : " + message);
          int firstSpace = message.indexOf(" ");
          String userPrivate = message.substring(1, firstSpace);
          server.sendMessageToUser(
              message.substring(
                  firstSpace + 1, message.length()),
              user, userPrivate);
        }

        // changing color (test) not done yet
      } else if (message.charAt(0) == '#') {
        user.changeColor(message); //لا تشرح
        // update color for all other users
        this.server.broadcastAllUsers();
      } else {
        // update user list
        server.broadcastMessages(message, user);
      }
    }
    // end of Thread
    server.removeUser(user);
    this.server.broadcastAllUsers();
    sc.close();
  }
}


// Mohammed Ahmed finish



//Abdallah alhendawi 210218362 START

class User {
  private static int nbUser = 0;
  private int userId;
  private PrintStream streamOut;
  private InputStream streamIn;
  private String nickname;
  private Socket client;
  private String color;

  // constructor
  public User(Socket client, String name) throws IOException {
    this.streamOut = new PrintStream(client.getOutputStream());
    this.streamIn = client.getInputStream();
    this.client = client;
    this.nickname = name;
    this.userId = nbUser;
    nbUser += 1;
  }

  //change color user (test)
  //Code snippet got from stackoverflow 
  //had no time to finish it
  public void changeColor(String hexColor) {
    
      this.color = hexColor; //لا تشرح
      this.getOutStream().println("<b>Color changed successfully</b> " + this.toString()); //still working on it , not finished 
  }

  
  public PrintStream getOutStream(){
    return this.streamOut;
  }

  public InputStream getInputStream(){
    return this.streamIn;
  }

  public String getNickname(){
    return this.nickname;
  }

  // print user with his color not done yet
  public String toString(){

    return "<u><span style='color:"+ this.color //لا تشرح
      +"'>" + this.getNickname() + "</span></u>";

  }
}

// class ColorInt {
//     public static String[] mColors = {
//             "#3079ab", // dark blue
//             "#e15258", // red
//             "#f9845b", // orange
//             "#7d669e", // purple
//             "#53bbb4", // aqua
//             "#51b46d", // green
//             "#e0ab18", // mustard
//             "#f092b0", // pink
//             "#e8d174", // yellow
//             "#e39e54", // orange
//             "#d64d4d", // red
//             "#4d7358", // green
//     };

//     public static String getColor(int i) {
//         return mColors[i % mColors.length];
//     }
// }

//Abdallah alhendawi finish