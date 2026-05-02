package view;

import model.NhanVien;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private NhanVien currentUser;   // Nhân viên đang đăng nhập

    // Layout chính
    private CardLayout cardLayout;
    private JPanel panelContent;

    // Các panel con (sẽ được các thành viên khác code)
    private JPanel panelThuoc;
    private JPanel panelKhachHang;
    private JPanel panelNhanVien;
    private JPanel panelBanHang;
    private JPanel panelNhapHang;
    private JPanel panelBaoCao;

    public MainFrame(NhanVien currentUser) {
        this.currentUser = currentUser;
        initComponents();
    }

    private void initComponents() {
        setTitle("Quản Lý Nhà Thuốc - " + currentUser.getTenNV() + " [" + currentUser.getVaiTro() + "]");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout tổng: Sidebar (Tây) + Content (Trung tâm)
        setLayout(new BorderLayout());

        // === HEADER ===
        JPanel panelHeader = createHeader();
        add(panelHeader, BorderLayout.NORTH);

        // === SIDEBAR ===
        JPanel panelSidebar = createSidebar();
        add(panelSidebar, BorderLayout.WEST);

        // === CONTENT (CardLayout) ===
        cardLayout = new CardLayout();
        panelContent = new JPanel(cardLayout);

        // Khởi tạo các panel và thêm vào CardLayout
        panelThuoc     = new view.ThuocPanel(currentUser);
        panelKhachHang = new view.KhachHangPanel(currentUser);
        panelNhanVien  = new view.NhanVienPanel(currentUser);
        panelBanHang   = new view.HoaDonPanel(currentUser);
        panelNhapHang  = new PlaceholderPanel("Nhập Hàng");
        panelBaoCao    = new view.BaoCaoPanel();

        panelContent.add(panelThuoc,     "THUOC");
        panelContent.add(panelKhachHang, "KHACHHANG");
        panelContent.add(panelNhanVien,  "NHANVIEN");
        panelContent.add(panelBanHang,   "BANHANG");
        panelContent.add(panelNhapHang,  "NHAPHANG");
        panelContent.add(panelBaoCao,    "BAOCAO");

        add(panelContent, BorderLayout.CENTER);

        // Hiển thị panel đầu tiên
        cardLayout.show(panelContent, "THUOC");
    }

    // Tạo Header
    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 102, 204));
        header.setPreferredSize(new Dimension(0, 50));

        // ===== LEFT: TITLE =====
        JLabel lblTitle = new JLabel("  PHẦN MỀM QUẢN LÝ NHÀ THUỐC");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        // ===== RIGHT: USER + LOGOUT =====
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setOpaque(false); // quan trọng để giữ màu nền

        JLabel lblUser = new JLabel("Xin chào: " + currentUser.getTenNV());
        lblUser.setFont(new Font("Arial", Font.PLAIN, 13));
        lblUser.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(Color.BLACK);

        btnLogout.addActionListener(e -> logout());

        rightPanel.add(lblUser);
        rightPanel.add(btnLogout);

        // ===== ADD TO HEADER =====
        header.add(lblTitle, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // Tạo Sidebar menu
    private JPanel createSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(180, 0));

        // Tạo các nút menu
        String[] menuNames  = {"Quản lý Thuốc", "Bán hàng", "Nhập hàng",
                                "Khách hàng", "Nhân viên", "Báo cáo"};
        String[] menuCards  = {"THUOC", "BANHANG", "NHAPHANG", "KHACHHANG", "NHANVIEN", "BAOCAO"};

        panel.add(Box.createVerticalStrut(10));

        for (int i = 0; i < menuNames.length; i++) {
            final String card = menuCards[i];
            JButton btn = createMenuButton(menuNames[i]);

            btn.addActionListener(e -> {
                    // Kiểm tra quyền: chỉ Admin mới xem Nhân viên
                    if ("NHANVIEN".equals(card) && !currentUser.isAdmin()) {
                        JOptionPane.showMessageDialog(this,
                            "Bạn không có quyền truy cập chức năng này!",
                            "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    cardLayout.show(panelContent, card);
                }
            );

            panel.add(btn);
            panel.add(Box.createVerticalStrut(2));
        }

        panel.add(Box.createVerticalGlue()); // Đẩy nút logout xuống cuối
        return panel;
    }

    // Tạo nút sidebar
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setBackground(new Color(44, 62, 80));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(52, 152, 219));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(44, 62, 80));
            }
        });

        return btn;
    }

    // Đăng xuất
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }

    // -----------------------------------------------
    // Panel tạm - thành viên khác sẽ thay thế
    // -----------------------------------------------
    static class PlaceholderPanel extends JPanel {
        public PlaceholderPanel(String title) {
            setLayout(new BorderLayout());
            JLabel lbl = new JLabel(title + " - Đang phát triển...", SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 20));
            lbl.setForeground(Color.GRAY);
            add(lbl, BorderLayout.CENTER);
        }
    }
}