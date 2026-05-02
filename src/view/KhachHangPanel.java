package view;

import dao.KhachHangDAO;
import model.KhachHang;
import model.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KhachHangPanel extends JPanel {

    private NhanVien currentUser;
    private KhachHangDAO khDAO;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField txtMaKH, txtTenKH, txtSDT, txtDiaChi, txtDiem;
    private JTextField txtSearch;

    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;

    public KhachHangPanel(NhanVien currentUser) {
        this.currentUser = currentUser;
        this.khDAO = new KhachHangDAO();
        initComponents();
        loadTable(khDAO.getAll());
    }

    private void initComponents() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // ===== TOP: SEARCH =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Tìm kiếm:"));

        txtSearch = new JTextField(15);
        btnSearch = new JButton("Tìm");

        topPanel.add(txtSearch);
        topPanel.add(btnSearch);

        add(topPanel, BorderLayout.NORTH);

        // ===== CENTER: TABLE =====
        String[] columns = {"Mã KH", "Tên KH", "SĐT", "Địa chỉ", "Điểm"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== EAST: FORM =====
        add(createFormPanel(), BorderLayout.EAST);

        // ===== EVENTS =====

        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            loadTable(kw.isEmpty() ? khDAO.getAll() : khDAO.search(kw));
        });

        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        panel.setPreferredSize(new Dimension(260, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Mã KH:", "Tên KH:", "SĐT:", "Địa chỉ:", "Điểm:"};

        txtMaKH = new JTextField(generateMa());
        txtTenKH = new JTextField();
        txtSDT = new JTextField();
        txtDiaChi = new JTextField();
        txtDiem = new JTextField("0");

        JTextField[] fields = {txtMaKH, txtTenKH, txtSDT, txtDiaChi, txtDiem};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        txtMaKH.setEditable(false);

        // BUTTONS
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);

        panel.add(btnPanel, gbc);

        // Phân quyền
        btnDelete.setEnabled(currentUser.isAdmin());

        return panel;
    }

    // ===== LOAD TABLE =====
    private void loadTable(List<KhachHang> list) {
        tableModel.setRowCount(0);

        for (KhachHang k : list) {
            tableModel.addRow(new Object[]{
                    k.getMaKH(),
                    k.getTenKH(),
                    k.getSdt(),
                    k.getDiaChi(),
                    k.getDiemTichLuy()
            });
        }
    }

    // ===== FILL FORM =====
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtMaKH.setText(tableModel.getValueAt(row, 0).toString());
        txtTenKH.setText(tableModel.getValueAt(row, 1).toString());
        txtSDT.setText(tableModel.getValueAt(row, 2).toString());
        txtDiaChi.setText(tableModel.getValueAt(row, 3).toString());
        txtDiem.setText(tableModel.getValueAt(row, 4).toString());
    }

    // ===== GET DATA =====
    private KhachHang getFormData() {
        KhachHang k = new KhachHang();

        k.setMaKH(txtMaKH.getText().trim());
        k.setTenKH(txtTenKH.getText().trim());
        k.setSdt(txtSDT.getText().trim());
        k.setDiaChi(txtDiaChi.getText().trim());

        try {
            k.setDiemTichLuy(Integer.parseInt(txtDiem.getText().trim()));
        } catch (Exception e) {
            k.setDiemTichLuy(0);
        }

        return k;
    }

    // ===== ADD =====
    private void handleAdd() {
        if (txtTenKH.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập tên khách!");
            return;
        }

        KhachHang k = getFormData();
        k.setMaKH(generateMa());

        if (khDAO.add(k)) {
            loadTable(khDAO.getAll());
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!");
        }
    }

    // ===== UPDATE =====
    private void handleUpdate() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Chọn khách để sửa!");
            return;
        }

        if (khDAO.update(getFormData())) {
            loadTable(khDAO.getAll());
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    // ===== DELETE =====
    private void handleDelete() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Chọn khách để xóa!");
            return;
        }

        String ma = txtMaKH.getText();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa khách: " + ma + " ?", "Xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (khDAO.delete(ma)) {
                loadTable(khDAO.getAll());
                clearForm();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            }
        }
    }

    // ===== CLEAR =====
    private void clearForm() {
        //table.clearSelection();

        txtMaKH.setText(generateMa());
        txtTenKH.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtDiem.setText("0");
        txtSearch.setText("");

        loadTable(khDAO.getAll());
    }

    private String generateMa() {
        return khDAO.generateMaKH();
    }
}