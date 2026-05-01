package view;

import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.ThuocDAO;
import model.ChiTietHoaDon;
import model.HoaDon;
import model.KhachHang;
import model.NhanVien;
import model.Thuoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class HoaDonPanel extends JPanel {

    private NhanVien currentUser;
    private ThuocDAO thuocDAO;
    private HoaDonDAO hoaDonDAO;
    private KhachHangDAO khachHangDAO;

    private DefaultTableModel thuocModel;
    private JTable tblThuoc;
    private JTextField txtTimThuoc;
    private JButton btnTimThuoc;
    private JButton btnThemVaoGio;

    private DefaultTableModel cartModel;
    private JTable tblCart;
    private JButton btnXoaKhoiGio;

    private JTextField txtSDTKhach;
    private JTextField txtTenKhach;
    private JButton btnTimKhach;
    private JLabel lblTongTien;
    private JButton btnThanhToan;

    private List<ChiTietHoaDon> gioHang;
    private KhachHang currentKhachHang;

    public HoaDonPanel(NhanVien currentUser) {
        this.currentUser = currentUser;
        this.thuocDAO = new ThuocDAO();
        this.hoaDonDAO = new HoaDonDAO();
        this.khachHangDAO = new KhachHangDAO();
        this.gioHang = new ArrayList<>();

        initComponents();
        loadDataThuoc();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- WEST: Danh sách thuốc ---
        JPanel pnlWest = new JPanel(new BorderLayout(5, 5));
        pnlWest.setPreferredSize(new Dimension(450, 0));
        pnlWest.setBorder(BorderFactory.createTitledBorder("Danh sách Thuốc"));

        JPanel pnlTimThuoc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTimThuoc = new JTextField(20);
        btnTimThuoc = new JButton("Tìm");
        btnTimThuoc.addActionListener(e -> timThuoc());
        pnlTimThuoc.add(new JLabel("Tìm thuốc:"));
        pnlTimThuoc.add(txtTimThuoc);
        pnlTimThuoc.add(btnTimThuoc);

        thuocModel = new DefaultTableModel(new String[]{"Mã", "Tên Thuốc", "Giá bán", "Tồn kho"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblThuoc = new JTable(thuocModel);
        JScrollPane scrollThuoc = new JScrollPane(tblThuoc);

        btnThemVaoGio = new JButton("Thêm vào giỏ >>");
        btnThemVaoGio.addActionListener(e -> themVaoGio());

        JPanel pnlThemGio = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlThemGio.add(btnThemVaoGio);

        pnlWest.add(pnlTimThuoc, BorderLayout.NORTH);
        pnlWest.add(scrollThuoc, BorderLayout.CENTER);
        pnlWest.add(pnlThemGio, BorderLayout.SOUTH);

        // --- CENTER: Giỏ hàng ---
        JPanel pnlCenter = new JPanel(new BorderLayout(5, 5));
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Giỏ Hàng"));

        cartModel = new DefaultTableModel(new String[]{"Mã Thuốc", "Tên Thuốc", "Số lượng", "Giá bán", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCart = new JTable(cartModel);
        JScrollPane scrollCart = new JScrollPane(tblCart);

        btnXoaKhoiGio = new JButton("Xóa khỏi giỏ");
        btnXoaKhoiGio.addActionListener(e -> xoaKhoiGio());

        JPanel pnlXoaGio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlXoaGio.add(btnXoaKhoiGio);

        pnlCenter.add(scrollCart, BorderLayout.CENTER);
        pnlCenter.add(pnlXoaGio, BorderLayout.SOUTH);

        // --- EAST: Thông tin khách hàng & Thanh toán ---
        JPanel pnlEast = new JPanel(new BorderLayout(5, 5));
        pnlEast.setPreferredSize(new Dimension(300, 0));

        JPanel pnlKhachHang = new JPanel(new GridLayout(4, 1, 5, 5));
        pnlKhachHang.setBorder(BorderFactory.createTitledBorder("Thông tin Khách hàng"));
        
        JPanel pnlSDT = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        txtSDTKhach = new JTextField(12);
        btnTimKhach = new JButton("Tìm");
        btnTimKhach.addActionListener(e -> timKhachHang());
        pnlSDT.add(txtSDTKhach);
        pnlSDT.add(btnTimKhach);

        pnlKhachHang.add(new JLabel("Số điện thoại:"));
        pnlKhachHang.add(pnlSDT);
        pnlKhachHang.add(new JLabel("Tên khách hàng:"));
        txtTenKhach = new JTextField();
        txtTenKhach.setEditable(false);
        pnlKhachHang.add(txtTenKhach);

        JPanel pnlThanhToan = new JPanel(new GridLayout(3, 1, 10, 10));
        pnlThanhToan.setBorder(BorderFactory.createTitledBorder("Thanh toán"));
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(Color.RED);
        
        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 18));
        btnThanhToan.setBackground(new Color(46, 204, 113));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.addActionListener(e -> thanhToan());

        pnlThanhToan.add(lblTongTien);
        pnlThanhToan.add(btnThanhToan);

        pnlEast.add(pnlKhachHang, BorderLayout.NORTH);
        pnlEast.add(pnlThanhToan, BorderLayout.SOUTH);

        add(pnlWest, BorderLayout.WEST);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlEast, BorderLayout.EAST);
    }

    private void loadDataThuoc() {
        thuocModel.setRowCount(0);
        List<Thuoc> list = thuocDAO.getAll();
        for (Thuoc t : list) {
            thuocModel.addRow(new Object[]{
                t.getMaThuoc(), t.getTenThuoc(), t.getGiaBan(), t.getSoLuongTon()
            });
        }
    }

    private void timThuoc() {
        String keyword = txtTimThuoc.getText().trim();
        thuocModel.setRowCount(0);
        List<Thuoc> list = thuocDAO.search(keyword);
        for (Thuoc t : list) {
            thuocModel.addRow(new Object[]{
                t.getMaThuoc(), t.getTenThuoc(), t.getGiaBan(), t.getSoLuongTon()
            });
        }
    }

    private void themVaoGio() {
        int selectedRow = tblThuoc.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc để thêm!");
            return;
        }

        String maThuoc = thuocModel.getValueAt(selectedRow, 0).toString();
        String tenThuoc = thuocModel.getValueAt(selectedRow, 1).toString();
        double giaBan = Double.parseDouble(thuocModel.getValueAt(selectedRow, 2).toString());
        int tonKho = Integer.parseInt(thuocModel.getValueAt(selectedRow, 3).toString());

        String soLuongStr = JOptionPane.showInputDialog(this, "Nhập số lượng:");
        if (soLuongStr == null || soLuongStr.trim().isEmpty()) return;

        try {
            int soLuong = Integer.parseInt(soLuongStr);
            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
                return;
            }
            if (soLuong > tonKho) {
                JOptionPane.showMessageDialog(this, "Không đủ tồn kho!");
                return;
            }

            // Kiểm tra xem đã có trong giỏ chưa
            boolean exist = false;
            for (ChiTietHoaDon ct : gioHang) {
                if (ct.getMaThuoc().equals(maThuoc)) {
                    if (ct.getSoLuong() + soLuong > tonKho) {
                        JOptionPane.showMessageDialog(this, "Tổng số lượng trong giỏ vượt tồn kho!");
                        return;
                    }
                    ct.setSoLuong(ct.getSoLuong() + soLuong);
                    ct.setThanhTien(ct.getSoLuong() * ct.getGiaBan());
                    exist = true;
                    break;
                }
            }

            if (!exist) {
                ChiTietHoaDon ct = new ChiTietHoaDon("", maThuoc, soLuong, giaBan, soLuong * giaBan);
                ct.setTenThuoc(tenThuoc);
                gioHang.add(ct);
            }

            capNhatGioHang();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
        }
    }

    private void xoaKhoiGio() {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn mục để xóa!");
            return;
        }
        gioHang.remove(selectedRow);
        capNhatGioHang();
    }

    private void capNhatGioHang() {
        cartModel.setRowCount(0);
        double tongTien = 0;
        for (ChiTietHoaDon ct : gioHang) {
            cartModel.addRow(new Object[]{
                ct.getMaThuoc(), ct.getTenThuoc(), ct.getSoLuong(), ct.getGiaBan(), ct.getThanhTien()
            });
            tongTien += ct.getThanhTien();
        }
        lblTongTien.setText("Tổng tiền: " + tongTien + " VNĐ");
    }

    private void timKhachHang() {
        String sdt = txtSDTKhach.getText().trim();
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập SDT!");
            return;
        }
        KhachHang kh = khachHangDAO.findBySdt(sdt);
        if (kh != null) {
            currentKhachHang = kh;
            txtTenKhach.setText(kh.getTenKH());
        } else {
            int choice = JOptionPane.showConfirmDialog(this, "Không tìm thấy khách hàng. Bạn có muốn thêm mới không?", "Thông báo", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                String tenKH = JOptionPane.showInputDialog(this, "Nhập tên khách hàng:");
                if (tenKH != null && !tenKH.trim().isEmpty()) {
                    currentKhachHang = new KhachHang("KH_NEW", tenKH, sdt, "", 0); 
                    txtTenKhach.setText(tenKH);
                    JOptionPane.showMessageDialog(this, "Đã ghi nhận tên khách hàng (chưa lưu vào DB).");
                }
            } else {
                currentKhachHang = null;
                txtTenKhach.setText("");
            }
        }
    }

    private void thanhToan() {
        if (gioHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        double tongTien = 0;
        for (ChiTietHoaDon ct : gioHang) {
            tongTien += ct.getThanhTien();
        }

        String maHD = hoaDonDAO.generateMaHD();
        String maKH = (currentKhachHang != null && !currentKhachHang.getMaKH().equals("KH_NEW")) ? currentKhachHang.getMaKH() : null;

        HoaDon hd = new HoaDon(maHD, new Date(System.currentTimeMillis()), currentUser.getMaNV(), maKH, tongTien);

        boolean success = hoaDonDAO.createHoaDon(hd, gioHang);
        if (success) {
            JOptionPane.showMessageDialog(this, "Thanh toán thành công! Mã HĐ: " + maHD);
            gioHang.clear();
            capNhatGioHang();
            txtSDTKhach.setText("");
            txtTenKhach.setText("");
            currentKhachHang = null;
            loadDataThuoc(); // Cập nhật lại tồn kho
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi thanh toán. Vui lòng thử lại!");
        }
    }
}
