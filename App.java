import java.util.*;
import java.sql.*;

public class App {

    public static void displayBook(ResultSet rs, String format) throws Exception {
        while (rs.next()) {
            System.out.format(format, rs.getInt("BookID"), rs.getString("BookName"), rs.getString("AuthorName"),
                    rs.getString("Genre"), rs.getInt("Copies"), rs.getInt("Price"));

        }
    }

    public static void main(String[] args) throws Exception {
        Connection con = null;
        con = connection.getConnection();
        Statement s = con.createStatement();
        Scanner input = new Scanner(System.in);
        ResultSet rs;

        while (true) {
            System.out.println(
                    "\n0) Add Books to library.\n1) Display all books in Library.\n2) Display book information.\n3) Display Genres.\n4) Loan a book.\n5) Return a book.\n6) Apply Filters.\n7) Customer details.\n10) Quit.\n");
            System.out.print("Enter command number : ");
            int f = input.nextInt();
            if (f == 0) {
                System.out.print("No of books to be added : ");
                int addBooks = input.nextInt();
                System.out.println(
                        "\nFormat:-\n{Bookname}\n{Authorname}\n{Genre}\n{Copies}\n{Price}\n\nStart adding books:-");
                for (int i = 0; i < addBooks; i++) {
                    input.nextLine();
                    String bookname = input.nextLine();
                    String authorname = input.nextLine();
                    String genre = input.nextLine();
                    int copies = input.nextInt();
                    int price = input.nextInt();

                    s.executeUpdate("INSERT INTO `books`(BookName,AuthorName,Genre,Copies,Price) VALUE ('"
                            + bookname + "','" + authorname + "','" + genre + "'," + copies + "," + price + ")");
                }
                System.out.println("Data saved in the library");
            } else if (f == 1) {
                System.out.println("Books available in our library:-");
                rs = s.executeQuery("select * from books");
                String format = "|%1$-8s|%2$-60s|%3$-20s|%4$-20s|%5$-15s|%6$-15s\n";
                System.out.format(format, "BookID", "BookName", "AuthorName", "Genre", "Copies", "Price");
                displayBook(rs, format);
            } else if (f == 2) {
                input.nextLine();
                System.out.print("Enter book id : ");
                int id = input.nextInt();
                rs = s.executeQuery("select * from books where BookID=" + id);
                String format = "|%1$-8s|%2$-60s|%3$-20s|%4$-20s|%5$-15s|%6$-15s\n";
                System.out.format(format, "BookID", "BookName", "AuthorName", "Genre", "Copies", "Price");
                displayBook(rs, format);
            } else if (f == 3) {
                input.nextLine();
                System.out.println("Genres available:-");
                rs = s.executeQuery("select Genre from books group by Genre");
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            } else if (f == 4) {
                input.nextLine();
                System.out.print("Enter your name : ");
                String cname = input.nextLine();
                System.out.print("Enter book Id : ");
                int id = input.nextInt();
                ResultSet rb = s.executeQuery("select BookName,Price,Copies from books where BookID='" + id + "'");
                rb.next();
                if (rb.getInt(3) == 0) {
                    System.out.println("Book not available.");
                    continue;
                }
                String bn = rb.getString(1);
                Integer amt = (Integer) rb.getInt(2) / 10;
                int cop = rb.getInt(3) - 1;
                s.executeUpdate(
                        "UPDATE books SET Copies = '" + cop + "' WHERE BookID ='" + id + "';");
                rs = s.executeQuery(
                        "SELECT * FROM Customers WHERE CustomerName IN ('" + cname + "');");
                if (rs.next()) {

                    int c = rs.getInt("BooksBorrowed") + 1;
                    s.executeUpdate(
                            "UPDATE customers SET BooksBorrowed = '" + c + "' WHERE CustomerName ='" + cname + "';");
                    ResultSet rc = s.executeQuery("select * from customers where CustomerName='" + cname + "'");
                    rc.next();
                    int ci = rc.getInt("CustomerID");

                    input.nextLine();
                    System.out.println("Enter borrow date(yyyy-mm-dd) : ");
                    String borrowdate = input.nextLine();
                    ResultSet dd = s.executeQuery("SELECT DATE_ADD('" + borrowdate + "', INTERVAL 1 MONTH);");
                    dd.next();
                    String duedate = dd.getString(1);
                    s.executeUpdate(
                            "INSERT INTO `BORROWERRECORD`(BookID,BookName,CustomerID,CustomerName,BorrowDate,DueDate,Amount,Returned) VALUE ("
                                    + id + ",'" + bn + "','" + ci + "','" + cname + "','" + borrowdate + "','" + duedate
                                    + "'," + amt + ",'No')");
                } else {
                    System.out.print("Enter Contact number : ");
                    Long cnum = input.nextLong();
                    input.nextLine();
                    System.out.println("Enter borrow date(yyyy-mm-dd) : ");

                    String borrowdate = input.nextLine();
                    s.executeUpdate("INSERT INTO `customers`(CustomerName,ContactNo,BooksBorrowed) VALUE ('"
                            + cname + "'," + cnum + "," + 1 + ")");
                    ResultSet rc = s.executeQuery("select * from customers where CustomerName='" + cname + "'");
                    rc.next();

                    int ci = rc.getInt("CustomerID");

                    ResultSet dd = s.executeQuery("SELECT DATE_ADD('" + borrowdate + "', INTERVAL 1 MONTH);");
                    dd.next();
                    String duedate = dd.getString(1);
                    s.executeUpdate(
                            "INSERT INTO `BORROWERRECORD`(BookID,BookName,CustomerID,CustomerName,BorrowDate,DueDate,Amount,Returned) VALUE ("
                                    + id + ",'" + bn + "','" + ci + "','" + cname + "','" + borrowdate + "','" + duedate
                                    + "'," + amt + ",'No')");
                }
            } else if (f == 5) {
                input.nextLine();
                System.out.print("Enter your name : ");
                String cname = input.nextLine();
                System.out.print("Enter book Id : ");
                int id = input.nextInt();
                input.nextLine();
                System.out.print("Return date(yyyy-mm-dd) : ");
                String rd = input.nextLine();

                ResultSet rb = s.executeQuery("select BookName,Price,Copies from books where BookID='" + id + "'");
                rb.next();
                String bn = rb.getString(1);
                int am = (Integer) rb.getInt(2) / 10;
                int pen = (Integer) rb.getInt(2) / 100;
                int cop = rb.getInt(3) + 1;
                s.executeUpdate(
                        "UPDATE books SET Copies = '" + cop + "' WHERE BookID ='" + id + "';");
                rs = s.executeQuery(
                        "SELECT * FROM Customers WHERE CustomerName IN ('" + cname + "');");
                if (rs.next()) {
                    int c = rs.getInt("BooksBorrowed") - 1;
                    s.executeUpdate(
                            "UPDATE customers SET BooksBorrowed = '" + c + "' WHERE CustomerName ='" + cname + "';");
                    ResultSet rc = s.executeQuery("select * from customers where CustomerName='" + cname + "'");
                    rc.next();
                    int ci = rc.getInt("CustomerID");

                    ResultSet rbr = s.executeQuery(
                            "select * from borrowerrecord where CustomerName='" + cname + "' and BookID='" + id + "'");
                    rbr.next();
                    String bd = rbr.getString("BorrowDate"), dd = rbr.getString("DueDate");
                    int bi = rbr.getInt("BorrowerID");
                    ResultSet t_f = s.executeQuery("SELECT DATEDIFF('" + rd + "', '" + dd + "')  ");
                    t_f.next();
                    int ddiff = t_f.getInt(1);
                    System.out.println("You have to pay : " + (ddiff * pen + am));
                    
                    s.executeUpdate(
                            "INSERT INTO `bookreturnrecord`(BorrowerID,BookID,BookName,CustomerID,CustomerName,BorrowDate,DueDate,BookReturnDate,Returned) VALUE ("
                                    + bi + ","
                                    + id + ",'" + bn + "','" + ci + "','" + cname + "','" + bd + "','" + dd + "','" + rd
                                    + "','Yes')");
                    s.executeUpdate(
                            "UPDATE borrowerrecord SET Returned = 'Yes'  WHERE CustomerName ='" + cname + "' and bookID='"+id+"';");
                            s.executeUpdate("UPDATE borrowerrecord SET ReturnedDate='"+rd+"'  WHERE CustomerName ='" + cname + "' and bookID='"+id+"';");                }
            } else if (f == 6) {
                input.nextLine();
                System.out.println("Enter the filters : ");
                System.out.println("Note : Specify the filter if you want otherwise press enter");
                System.out.print("Based on bookname : ");
                String bn=input.nextLine();
                System.out.println("Note : Specify the filter if you want otherwise press enter");
                System.out.print("Based on authorname : ");
                String an=input.nextLine();
                System.out.println("Note : Specify the filter if you want otherwise press enter");
                System.out.print("Based on genre : ");
                String g=input.nextLine();
                System.out.println("Note : Specify the filter if you want otherwise press 0");
                System.out.print("Based on price : ");
                int p=input.nextInt();
                ResultSet rd=s.executeQuery("select * from books");
                String format = "|%1$-8s|%2$-60s|%3$-20s|%4$-20s|%5$-15s|%6$-15s\n";
                System.out.format(format, "BookID", "BookName", "AuthorName", "Genre", "Copies", "Price");
                while(rd.next()){
                    if((bn.isEmpty()||rd.getString("BookName").equals(bn))&&(an.isEmpty()||rd.getString("AuthorName").equals(an))&&(g.isEmpty()||rd.getString("Genre").equals(g))&&(p==0||rd.getInt("Price")==p)){
                       
                System.out.format(format, rd.getInt("BookID"), rd.getString("BookName"), rd.getString("AuthorName"),
                rd.getString("Genre"), rd.getInt("Copies"), rd.getInt("Price"));                    }
                    
                }
            } else if (f == 7) {
                input.nextLine();
                System.out.print("Enter your name : ");
                String cname = input.nextLine();
                System.out.println("Borrow details:-");
                ResultSet rc = s.executeQuery("select * from borrowerrecord where CustomerName='" + cname + "'");
                String format = "|%1$-8s|%2$-50s|%3$-15s|%4$-15s|%5$-15s|%6$-10s|%7$-10s\n";
                System.out.format(format, "BookID", "BookName", "BorrowDate","DueDate", "ReturnedDate","Amount", "Returned");
                while (rc.next()) {

                    System.out.format(format, rc.getInt("BookId"), rc.getString("BookName"), rc.getString("BorrowDate"),rc.getString("DueDate"),rc.getString("ReturnedDate"),
                            rc.getBigDecimal("Amount"), rc.getString("Returned"));
                }
            } else if (f == 10) {
                input.close();
                s.close();
                return;
            }
        }
    }
}

class connection {
    Connection con = null;

    public static Connection getConnection() throws Exception {
    /*
      Install MYSQL in your machine.
      Create a database named Librarydb
      Create a table named books with BookName,AuthorName,Genre,Copies,Price.
      Create a table named Customers with CustomerName,ContactNo,BooksBorrowed
      Create a table named borrowerrecord with BookID,BookName,CustomerID,CustomerName,BorrowDate,DueDate,Amount,Returned
      Create a table named bookreturnrecord with BorrowerID,BookID,BookName,CustomerID,CustomerName,BorrowDate,DueDate,BookReturnDate,Returned
      
    */
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/librarydb";
            String username = "Enter your UserName";
            String password = "Enter your Password";
            Class.forName(driver);

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected");
            return conn;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}
