package view;

import dao.ThuocDAO;
import model.NhanVien;
import model.Thuoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ThuocPanel extends JPanel {

    private NhanVien currentUser;
    private ThuocDAO thuocDAO;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField txtMaThuoc, txtTenThuoc, txtLoaiThuoc, txtHangSX;
    private JTextField txtGiaNhap, txtGiaBan, txtSoLuong, txtNgaysx, txtHanSD;

    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnSapHet, btnGanHetHan;
    private JTextField txtSearch;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public ThuocPanel(NhanVien currentUser) {
        this.currentUser = currentUser;
        this.thuocDAO = new ThuocDAO();
        initComponents();
        loadTable(thuocDAO.getAll());
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // === TOP: Thanh tìm kiếm và lọc ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(15);
        btnSearch     = new JButton("Tìm");
        btnSapHet     = new JButton("Sắp hết");
        btnGanHetHan  = new JButton("Gần hết hạn");

        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnSapHet);
        topPanel.add(btnGanHetHan);
        add(topPanel, BorderLayout.NORTH);

        // === CENTER: Bảng danh sách thuốc ===
        String[] columns = {"Mã thuốc", "Tên thuốc", "Loại", "Hãng SX", "Giá nhập", "Giá bán", "Tồn kho", "Ngày SX", "Hạn SD"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } // Không cho sửa trực tiếp
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Click vào hàng → điền vào form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // === EAST: Form nhập liệu ===
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.EAST);

        // Sự kiện buttons
        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            loadTable(kw.isEmpty() ? thuocDAO.getAll() : thuocDAO.search(kw));
        });

        btnSapHet.addActionListener(e -> loadTable(thuocDAO.getThuocSapHet()));
        btnGanHetHan.addActionListener(e -> loadTable(thuocDAO.getThuocGanHetHan()));

        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin thuốc"));
        panel.setPreferredSize(new Dimension(260, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Mã thuốc:", "Tên thuốc:", "Loại thuốc:", "Hãng SX:", "Giá nhập:", "Giá bán:", "Tồn kho:", "Ngày SX:", "Hạn SD:"};
        txtMaThuoc  = new JTextField(generateMa());
        txtTenThuoc = new JTextField();
        txtLoaiThuoc = new JTextField();
        txtHangSX   = new JTextField();
        txtGiaNhap  = new JTextField("0");
        txtGiaBan   = new JTextField("0");
        txtSoLuong  = new JTextField("0");
        txtNgaysx   = new JTextField("dd/MM/yyyy");
        txtHanSD    = new JTextField("dd/MM/yyyy");

        JTextField[] fields = {txtMaThuoc, txtTenThuoc, txtLoaiThuoc, txtHangSX, txtGiaNhap, txtGiaBan, txtSoLuong, txtNgaysx, txtHanSD};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        txtMaThuoc.setEditable(false); // Mã tự sinh

        // Buttons
        btnAdd    = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear  = new JButton("Làm mới");

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        panel.add(btnPanel, gbc);

        // Chỉ Admin mới được xóa
        btnDelete.setEnabled(currentUser.isAdmin());

        return panel;
    }

    // Nạp dữ liệu vào JTable
    private void loadTable(List<Thuoc> list) {
        tableModel.setRowCount(0);
        for (Thuoc t : list) {
            tableModel.addRow(new Object[]{
                t.getMaThuoc(), t.getTenThuoc(), t.getLoaiThuoc(), t.getHangSanXuat(),
                String.format("%,.0f", t.getGiaNhap()),
                String.format("%,.0f", t.getGiaBan()),
                t.getSoLuongTon(),
                t.getNgaySanXuat() != null ? sdf.format(t.getNgaySanXuat()) : "",
                t.getHanSuDung()   != null ? sdf.format(t.getHanSuDung())   : ""
            });
        }
    }

    // Lấy dữ liệu từ hàng được chọn → điền vào form
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtMaThuoc.setText((String) tableModel.getValueAt(row, 0));
        txtTenThuoc.setText((String) tableModel.getValueAt(row, 1));
        txtLoaiThuoc.setText((String) tableModel.getValueAt(row, 2));
        txtHangSX.setText((String) tableModel.getValueAt(row, 3));
        txtGiaNhap.setText(tableModel.getValueAt(row, 4).toString().replace(",", ""));
        txtGiaBan.setText(tableModel.getValueAt(row, 5).toString().replace(",", ""));
        txtSoLuong.setText(tableModel.getValueAt(row, 6).toString());
        txtNgaysx.setText(tableModel.getValueAt(row, 7).toString());
        txtHanSD.setText(tableModel.getValueAt(row, 8).toString());
    }

    // Đọc form → tạo object Thuoc
    private Thuoc getFormData() {
        Thuoc t = new Thuoc();
        t.setMaThuoc(txtMaThuoc.getText().trim());
        t.setTenThuoc(txtTenThuoc.getText().trim());
        t.setLoaiThuoc(txtLoaiThuoc.getText().trim());
        t.setHangSanXuat(txtHangSX.getText().trim());
        try { t.setGiaNhap(Double.parseDouble(txtGiaNhap.getText().trim())); } catch (Exception ex) { t.setGiaNhap(0); }
        try { t.setGiaBan(Double.parseDouble(txtGiaBan.getText().trim())); }  catch (Exception ex) { t.setGiaBan(0); }
        try { t.setSoLuongTon(Integer.parseInt(txtSoLuong.getText().trim())); } catch (Exception ex) { t.setSoLuongTon(0); }
        try { t.setNgaySanXuat(sdf.parse(txtNgaysx.getText().trim())); } catch (ParseException ex) { t.setNgaySanXuat(null); }
        try { t.setHanSuDung(sdf.parse(txtHanSD.getText().trim())); }   catch (ParseException ex) { t.setHanSuDung(null); }
        return t;
    }

    private void handleAdd() {
        if (txtTenThuoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên thuốc!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Thuoc t = getFormData();
        if (thuocDAO.add(t)) {
            loadTable(thuocDAO.getAll());
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm thuốc thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại! Mã thuốc đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc cần sửa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Thuoc t = getFormData();
        if (thuocDAO.update(t)) {
            loadTable(thuocDAO.getAll());
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thuốc cần xóa!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = txtMaThuoc.getText();
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa thuốc: " + ma + "?", "Xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (thuocDAO.delete(ma)) {
                loadTable(thuocDAO.getAll());
                clearForm();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        table.clearSelection();
        txtMaThuoc.setText(generateMa());
        txtTenThuoc.setText("");
        txtLoaiThuoc.setText("");
        txtHangSX.setText("");
        txtGiaNhap.setText("0");
        txtGiaBan.setText("0");
        txtSoLuong.setText("0");
        txtNgaysx.setText("dd/MM/yyyy");
        txtHanSD.setText("dd/MM/yyyy");
        txtSearch.setText("");
        loadTable(thuocDAO.getAll());
    }

    private String generateMa() {
        return new ThuocDAO().generateMaThuoc();
    }
}
