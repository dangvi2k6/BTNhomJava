package controller;

import dao.NhanVienDAO;
import model.NhanVien;
import view.LoginFrame;
import view.MainFrame;

public class LoginController {

    private LoginFrame loginFrame;
    private NhanVienDAO nhanVienDAO;

    public LoginController(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.nhanVienDAO = new NhanVienDAO();
    }

    public void login(String username, String password) {
        // Kiểm tra trống
        if (username.isEmpty() || password.isEmpty()) {
            loginFrame.showError("Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        // Kiểm tra DB
        NhanVien nhanVien = nhanVienDAO.login(username, password);

        if (nhanVien != null) {
            // Đăng nhập thành công → mở MainFrame
            loginFrame.dispose();
            MainFrame mainFrame = new MainFrame(nhanVien);
            mainFrame.setVisible(true);
        } else {
            loginFrame.showError("Tài khoản hoặc mật khẩu không đúng!");
        }
    }
}
