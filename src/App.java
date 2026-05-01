import view.LoginFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Chạy trên Event Dispatch Thread (EDT) - chuẩn Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Dùng giao diện hệ thống (trông đẹp hơn)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
