package view;

import dao.NhanVienDAO;
import model.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NhanVienPanel extends JPanel {

    private final NhanVien currentUser;
    private final NhanVienDAO nvDAO;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Form fields
    private JTextField txtMaNV, txtTenNV, txtUsername, txtSdt, txtDiaChi;
    private JPasswordField txtPassword;
    private JComboBox<String> cboVaiTro;
    private JCheckBox chkShowPassword;

    private JTextField txtSearch;

    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;

    private char defaultEchoChar;

    public NhanVienPanel(NhanVien currentUser) {
        this.currentUser = currentUser;
        this.nvDAO = new NhanVienDAO();

        initComponents();
        loadTable(nvDAO.getAllStaff());
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        add(createNorthPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.SOUTH);

        // lưu echo char mặc định để restore (không dùng UIManager)
        defaultEchoChar = txtPassword.getEchoChar();

        // ===== EVENTS =====
        btnSearch.addActionListener(e -> {
            String kw = txtSearch.getText().trim();
            try {
                loadTable(kw.isEmpty() ? nvDAO.getAllStaff() : nvDAO.search(kw));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Tìm kiếm thất bại: " + ex.getMessage());
            }
        });
        

        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());

        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                // reset echo char theo UI mặc định
                txtPassword.setEchoChar(defaultEchoChar);
            }
        });
    }

    private JPanel createNorthPanel() {
        JPanel wrap = new JPanel(new BorderLayout(8, 8));

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(6, 6, 6, 6)
        ));

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        titleBar.add(lblTitle, BorderLayout.WEST);

        JLabel lblRole = new JLabel("Đăng nhập: " + currentUser.getTenNV() + " - " + currentUser.getVaiTro());
        lblRole.setForeground(new Color(90, 90, 90));
        titleBar.add(lblRole, BorderLayout.EAST);

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBar.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(18);
        btnSearch = new JButton("Tìm");
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);

        wrap.add(titleBar, BorderLayout.NORTH);
        wrap.add(searchBar, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel createTablePanel() {
        String[] columns = {"Mã NV", "Tên NV", "Username", "Vai trò", "SĐT", "Địa chỉ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromTable();
        });

        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Danh sách nhân viên"));
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));

        // ===== FORM GRID =====
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaNV = new JTextField(generateMa());
        txtTenNV = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        cboVaiTro = new JComboBox<>(new String[]{"Admin", "Staff"});
        txtSdt = new JTextField();
        txtDiaChi = new JTextField();
        chkShowPassword = new JCheckBox("Hiện mật khẩu");

        txtMaNV.setEditable(false);

        int r = 0;
        // Row 0
        addField(form, gbc, r, 0, "Mã NV:", txtMaNV);
        addField(form, gbc, r, 2, "Tên NV:", txtTenNV);
        r++;

        // Row 1
        addField(form, gbc, r, 0, "Username:", txtUsername);
        addPasswordField(form, gbc, r, 2, "Password:", txtPassword, chkShowPassword);
        r++;

        // Row 2
        addField(form, gbc, r, 0, "Vai trò:", cboVaiTro);
        addField(form, gbc, r, 2, "SĐT:", txtSdt);
        r++;

        // Row 3
        addField(form, gbc, r, 0, "Địa chỉ:", txtDiaChi);
        // spacer
        gbc.gridx = 2; gbc.gridy = r; gbc.gridwidth = 2;
        form.add(new JLabel(""), gbc);

        // ===== BUTTONS BAR =====
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm mới");

        btnBar.add(btnAdd);
        btnBar.add(btnUpdate);
        btnBar.add(btnDelete);
        btnBar.add(btnClear);

        // Phân quyền
        btnDelete.setEnabled(currentUser.isAdmin());

        panel.add(form, BorderLayout.CENTER);
        panel.add(btnBar, BorderLayout.SOUTH);
        return panel;
    }

    private void addField(JPanel form, GridBagConstraints gbc, int row, int col, String label, JComponent field) {
        // label
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel(label), gbc);

        // field
        gbc.gridx = col + 1;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(field, gbc);
    }

    private void addPasswordField(JPanel form, GridBagConstraints gbc, int row, int col,
                                  String label, JPasswordField field, JCheckBox show) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel(label), gbc);

        JPanel wrap = new JPanel(new BorderLayout(6, 0));
        wrap.add(field, BorderLayout.CENTER);
        wrap.add(show, BorderLayout.EAST);

        gbc.gridx = col + 1;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(wrap, gbc);
    }

    // ===== LOAD TABLE =====
    private void loadTable(List<NhanVien> list) {
        tableModel.setRowCount(0);
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[]{
                    nv.getMaNV(),
                    nv.getTenNV(),
                    nv.getUsername(),
                    nv.getVaiTro(),
                    nv.getSdt(),
                    nv.getDiaChi()
            });
        }
    }

    // ===== FILL FORM =====
    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        txtMaNV.setText(tableModel.getValueAt(row, 0).toString());
        txtTenNV.setText(tableModel.getValueAt(row, 1).toString());
        txtUsername.setText(tableModel.getValueAt(row, 2).toString());
        cboVaiTro.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        txtSdt.setText(valueAt(row, 4));
        txtDiaChi.setText(valueAt(row, 5));

        // Nếu bạn muốn show password thì cần lấy từ DB theo maNV.
        // Mặc định để trống để tránh lộ mật khẩu.
        txtPassword.setText("");
        chkShowPassword.setSelected(false);
        txtPassword.setEchoChar(defaultEchoChar);
    }

    private String valueAt(int row, int col) {
        Object v = tableModel.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    // ===== GET DATA =====
    private NhanVien getFormData() {
        NhanVien nv = new NhanVien();

        nv.setMaNV(txtMaNV.getText().trim());
        nv.setTenNV(txtTenNV.getText().trim());
        nv.setUsername(txtUsername.getText().trim());
        nv.setPassword(new String(txtPassword.getPassword()).trim());
        nv.setVaiTro((String) cboVaiTro.getSelectedItem());
        nv.setSdt(txtSdt.getText().trim());
        nv.setDiaChi(txtDiaChi.getText().trim());

        return nv;
    }

    // ===== ADD =====
    private void handleAdd() {
        if (txtTenNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập tên nhân viên!");
            return;
        }
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập username!");
            return;
        }
        if (new String(txtPassword.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Thêm mới phải nhập password!");
            return;
        }

        NhanVien nv = getFormData();
        nv.setMaNV(generateMa());

        if (nvDAO.insert(nv)) {
            loadTable(nvDAO.getAllStaff());
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!");
        }
    }

    // ===== UPDATE =====
    private void handleUpdate() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên để sửa!");
            return;
        }

        NhanVien nv = getFormData();

        // Nếu password trống khi update -> giữ nguyên password cũ (DAO nên hỗ trợ)
        // Ở đây xử lý đơn giản: yêu cầu nhập password khi sửa (bạn muốn theo cách nào?)
        // Mình để theo hướng mềm: nếu trống thì vẫn cho update, DAO sẽ cần update có điều kiện.
        if (nvDAO.update(nv)) {
            loadTable(nvDAO.getAllStaff());
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    // ===== DELETE =====
    private void handleDelete() {
        if (!currentUser.isAdmin()) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa nhân viên!");
            return;
        }
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên để xóa!");
            return;
        }

        String ma = txtMaNV.getText();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa nhân viên: " + ma + " ?", "Xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (nvDAO.delete(ma)) {
                loadTable(nvDAO.getAllStaff());
                clearForm();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            }
        }
    }

    // ===== CLEAR =====
    private void clearForm() {
        table.clearSelection();

        txtMaNV.setText(generateMa());
        txtTenNV.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cboVaiTro.setSelectedIndex(0);
        txtSdt.setText("");
        txtDiaChi.setText("");
        txtSearch.setText("");

        chkShowPassword.setSelected(false);
        txtPassword.setEchoChar(defaultEchoChar);

        loadTable(nvDAO.getAllStaff());
    }

    private String generateMa() {
        return nvDAO.generateMaNV();
    }
}