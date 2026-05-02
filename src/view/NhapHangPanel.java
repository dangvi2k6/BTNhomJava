package view;

import dao.NhaCungCapDAO;
import dao.NhapHangDAO;
import dao.ThuocDAO;
import model.ChiTietNhap;
import model.NhanVien;
import model.PhieuNhap;
import model.Thuoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class NhapHangPanel extends JPanel {

    private final NhanVien currentUser;
    private final ThuocDAO thuocDAO;
    private final NhapHangDAO nhapHangDAO;
    private final NhaCungCapDAO nccDAO;

    // WEST: thuốc
    private DefaultTableModel thuocModel;
    private JTable tblThuoc;
    private JTextField txtTimThuoc;
    private JButton btnTimThuoc;
    private JButton btnThemVaoPhieu;

    // CENTER: chi tiết nhập
    private DefaultTableModel cartModel;
    private JTable tblCart;
    private JButton btnXoaDong;

    // EAST: thông tin phiếu
    private JLabel lblMaPhieu;
    private JLabel lblNgayNhap;
    private JComboBox<String> cboNCC; // hiển thị "ma - tên"
    private JTextArea txtGhiChu;
    private JLabel lblTongTien;
    private JButton btnLuuPhieu;

    private final List<ChiTietNhap> dsNhap = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private Map<String, String> mapNCC = new LinkedHashMap<>(); // maNCC -> tenNCC

    public NhapHangPanel(NhanVien currentUser) {
        this.currentUser = currentUser;
        this.thuocDAO = new ThuocDAO();
        this.nhapHangDAO = new NhapHangDAO();
        this.nccDAO = new NhaCungCapDAO();

        initComponents();
        loadDataThuoc();
        loadNCC();
        refreshPhieuInfo();
        capNhatBangNhap();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- WEST: Danh sách thuốc ---
        JPanel pnlWest = new JPanel(new BorderLayout(5, 5));
        pnlWest.setPreferredSize(new Dimension(450, 0));
        pnlWest.setBorder(BorderFactory.createTitledBorder("Danh sách Thuốc"));

        JPanel pnlTim = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtTimThuoc = new JTextField(20);
        btnTimThuoc = new JButton("Tìm");
        btnTimThuoc.addActionListener(e -> timThuoc());
        pnlTim.add(new JLabel("Tìm thuốc:"));
        pnlTim.add(txtTimThuoc);
        pnlTim.add(btnTimThuoc);

        thuocModel = new DefaultTableModel(new String[]{"Mã", "Tên Thuốc", "Giá nhập hiện tại", "Tồn kho"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblThuoc = new JTable(thuocModel);
        JScrollPane scrollThuoc = new JScrollPane(tblThuoc);

        btnThemVaoPhieu = new JButton("Thêm vào phiếu >>");
        btnThemVaoPhieu.addActionListener(e -> themVaoPhieu());

        JPanel pnlThem = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlThem.add(btnThemVaoPhieu);

        pnlWest.add(pnlTim, BorderLayout.NORTH);
        pnlWest.add(scrollThuoc, BorderLayout.CENTER);
        pnlWest.add(pnlThem, BorderLayout.SOUTH);

        // --- CENTER: Chi tiết phiếu nhập ---
        JPanel pnlCenter = new JPanel(new BorderLayout(5, 5));
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Chi tiết nhập (theo lô / hạn dùng)"));

        cartModel = new DefaultTableModel(
                new String[]{"Mã", "Tên thuốc", "Số lô", "Hạn SD", "SL", "Giá nhập", "Thành tiền"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tblCart = new JTable(cartModel);
        JScrollPane scrollCart = new JScrollPane(tblCart);

        btnXoaDong = new JButton("Xóa dòng");
        btnXoaDong.addActionListener(e -> xoaDong());

        JPanel pnlXoa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlXoa.add(btnXoaDong);

        pnlCenter.add(scrollCart, BorderLayout.CENTER);
        pnlCenter.add(pnlXoa, BorderLayout.SOUTH);

        // --- EAST: Thông tin phiếu + Lưu ---
        JPanel pnlEast = new JPanel(new BorderLayout(8, 8));
        pnlEast.setPreferredSize(new Dimension(330, 0));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBorder(BorderFactory.createTitledBorder("Thông tin Phiếu nhập"));

        lblMaPhieu = new JLabel();
        lblNgayNhap = new JLabel();

        pnlInfo.add(new JLabel("Mã phiếu:"));
        pnlInfo.add(lblMaPhieu);
        pnlInfo.add(Box.createVerticalStrut(6));

        pnlInfo.add(new JLabel("Ngày nhập:"));
        pnlInfo.add(lblNgayNhap);
        pnlInfo.add(Box.createVerticalStrut(10));

        pnlInfo.add(new JLabel("Nhà cung cấp:"));
        cboNCC = new JComboBox<>();
        pnlInfo.add(cboNCC);
        pnlInfo.add(Box.createVerticalStrut(10));

        pnlInfo.add(new JLabel("Ghi chú:"));
        txtGhiChu = new JTextArea(5, 20);
        pnlInfo.add(new JScrollPane(txtGhiChu));
        pnlInfo.add(Box.createVerticalStrut(10));

        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(Color.RED);
        pnlInfo.add(lblTongTien);

        btnLuuPhieu = new JButton("LƯU PHIẾU NHẬP");
        btnLuuPhieu.setFont(new Font("Arial", Font.BOLD, 16));
        btnLuuPhieu.setBackground(new Color(52, 152, 219));
        btnLuuPhieu.setForeground(Color.WHITE);
        btnLuuPhieu.addActionListener(e -> luuPhieu());

        JPanel pnlSave = new JPanel(new GridLayout(1, 1));
        pnlSave.add(btnLuuPhieu);

        pnlEast.add(pnlInfo, BorderLayout.CENTER);
        pnlEast.add(pnlSave, BorderLayout.SOUTH);

        add(pnlWest, BorderLayout.WEST);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlEast, BorderLayout.EAST);
    }

    private void refreshPhieuInfo() {
        lblMaPhieu.setText(nhapHangDAO.generateMaPhieu());
        lblNgayNhap.setText(sdf.format(new Date()));
    }

    private void loadNCC() {
        mapNCC = nccDAO.getAllMap();
        cboNCC.removeAllItems();
        for (Map.Entry<String, String> e : mapNCC.entrySet()) {
            cboNCC.addItem(e.getKey() + " - " + e.getValue());
        }
        if (cboNCC.getItemCount() > 0) cboNCC.setSelectedIndex(0);
    }

    private String getSelectedMaNCC() {
        Object item = cboNCC.getSelectedItem();
        if (item == null) return null;
        String s = item.toString();
        int idx = s.indexOf(" - ");
        return idx > 0 ? s.substring(0, idx) : s;
    }

    private void loadDataThuoc() {
        thuocModel.setRowCount(0);
        List<Thuoc> list = thuocDAO.getAll();
        for (Thuoc t : list) {
            thuocModel.addRow(new Object[]{
                    t.getMaThuoc(), t.getTenThuoc(), t.getGiaNhap(), t.getSoLuongTon()
            });
        }
    }

    private void timThuoc() {
        String keyword = txtTimThuoc.getText().trim();
        thuocModel.setRowCount(0);
        List<Thuoc> list = thuocDAO.search(keyword);
        for (Thuoc t : list) {
            thuocModel.addRow(new Object[]{
                    t.getMaThuoc(), t.getTenThuoc(), t.getGiaNhap(), t.getSoLuongTon()
            });
        }
    }

    private void themVaoPhieu() {
        int selectedRow = tblThuoc.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc để nhập!");
            return;
        }

        String maThuoc = thuocModel.getValueAt(selectedRow, 0).toString();
        String tenThuoc = thuocModel.getValueAt(selectedRow, 1).toString();

        String soLo = JOptionPane.showInputDialog(this, "Nhập SỐ LÔ:");
        if (soLo == null || soLo.trim().isEmpty()) return;

        String hanStr = JOptionPane.showInputDialog(this, "Nhập HẠN SỬ DỤNG (dd/MM/yyyy):");
        if (hanStr == null || hanStr.trim().isEmpty()) return;

        String soLuongStr = JOptionPane.showInputDialog(this, "Nhập SỐ LƯỢNG:");
        if (soLuongStr == null || soLuongStr.trim().isEmpty()) return;

        String giaNhapStr = JOptionPane.showInputDialog(this, "Nhập GIÁ NHẬP:");
        if (giaNhapStr == null || giaNhapStr.trim().isEmpty()) return;

        try {
            Date hanSD = sdf.parse(hanStr.trim());
            int soLuong = Integer.parseInt(soLuongStr.trim());
            double giaNhap = Double.parseDouble(giaNhapStr.trim());

            if (soLuong <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải > 0!");
                return;
            }
            if (giaNhap < 0) {
                JOptionPane.showMessageDialog(this, "Giá nhập không hợp lệ!");
                return;
            }

            // Merge nếu trùng maThuoc + soLo
            boolean merged = false;
            for (ChiTietNhap ct : dsNhap) {
                if (ct.getMaThuoc().equals(maThuoc) && ct.getSoLo().equalsIgnoreCase(soLo.trim())) {
                    ct.setSoLuong(ct.getSoLuong() + soLuong);
                    ct.setGiaNhap(giaNhap);
                    ct.setHanSuDung(hanSD);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                ChiTietNhap ct = new ChiTietNhap("", maThuoc, soLuong, giaNhap, soLo.trim(), hanSD);
                ct.setTenThuoc(tenThuoc);
                dsNhap.add(ct);
            }

            capNhatBangNhap();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ! (HSD dd/MM/yyyy, SL số nguyên, Giá số)");
        }
    }

    private void xoaDong() {
        int row = tblCart.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa!");
            return;
        }
        dsNhap.remove(row);
        capNhatBangNhap();
    }

    private void capNhatBangNhap() {
        cartModel.setRowCount(0);
        double tong = 0;

        for (ChiTietNhap ct : dsNhap) {
            cartModel.addRow(new Object[]{
                    ct.getMaThuoc(),
                    ct.getTenThuoc(),
                    ct.getSoLo(),
                    sdf.format(ct.getHanSuDung()),
                    ct.getSoLuong(),
                    ct.getGiaNhap(),
                    ct.getThanhTien()
            });
            tong += ct.getThanhTien();
        }

        lblTongTien.setText("Tổng tiền: " + String.format("%,.0f", tong) + " VNĐ");
    }

    private void luuPhieu() {
        if (dsNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có thuốc trong phiếu nhập!");
            return;
        }

        String maNCC = getSelectedMaNCC();
        if (maNCC == null || maNCC.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn nhà cung cấp!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Xác nhận lưu phiếu nhập?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;

        String maPhieu = lblMaPhieu.getText();
        double tong = 0;
        for (ChiTietNhap ct : dsNhap) tong += ct.getThanhTien();

        PhieuNhap pn = new PhieuNhap(maPhieu, new Date(), maNCC, currentUser.getMaNV(), tong);

        for (ChiTietNhap ct : dsNhap) ct.setMaPhieu(maPhieu);

        boolean ok = nhapHangDAO.createPhieuNhap(pn, dsNhap);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Lưu phiếu nhập thành công! Mã PN: " + maPhieu);

            dsNhap.clear();
            capNhatBangNhap();
            txtGhiChu.setText("");

            refreshPhieuInfo();
            loadDataThuoc();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu phiếu nhập thất bại!");
        }
    }
}