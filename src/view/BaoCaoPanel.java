package view;

import dao.ThongKeDAO;
import dao.ThuocDAO;
import model.Thuoc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BaoCaoPanel extends JPanel {
    private ThongKeDAO thongKeDAO = new ThongKeDAO();
    private ThuocDAO thuocDAO = new ThuocDAO();

    private JTable tblDoanhThu, tblTopThuoc, tblCanhBao;
    private DefaultTableModel modelDoanhThu, modelTopThuoc, modelCanhBao;
    private JSpinner spinTuNgay, spinDenNgay;
    private JLabel lblTongDoanhThu;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private DecimalFormat df = new DecimalFormat("#,###.## VND");

    public BaoCaoPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header Panel with Date filters
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        pnlHeader.setBorder(BorderFactory.createTitledBorder("Lọc Thời Gian"));

        // Setup spinners for Date
        Calendar cal = Calendar.getInstance();
        Date denNgay = cal.getTime();
        cal.add(Calendar.MONTH, -1); // Default to last 1 month
        Date tuNgay = cal.getTime();

        SpinnerDateModel modelTuNgay = new SpinnerDateModel(tuNgay, null, null, Calendar.DAY_OF_MONTH);
        SpinnerDateModel modelDenNgay = new SpinnerDateModel(denNgay, null, null, Calendar.DAY_OF_MONTH);

        spinTuNgay = new JSpinner(modelTuNgay);
        spinDenNgay = new JSpinner(modelDenNgay);
        JSpinner.DateEditor editorTuNgay = new JSpinner.DateEditor(spinTuNgay, "dd/MM/yyyy");
        JSpinner.DateEditor editorDenNgay = new JSpinner.DateEditor(spinDenNgay, "dd/MM/yyyy");
        spinTuNgay.setEditor(editorTuNgay);
        spinDenNgay.setEditor(editorDenNgay);

        JButton btnLoc = new JButton("Lọc Dữ Liệu");
        btnLoc.addActionListener(e -> loadData());

        pnlHeader.add(new JLabel("Từ ngày:"));
        pnlHeader.add(spinTuNgay);
        pnlHeader.add(new JLabel("Đến ngày:"));
        pnlHeader.add(spinDenNgay);
        pnlHeader.add(btnLoc);

        add(pnlHeader, BorderLayout.NORTH);

        // TabbedPane for different reports
        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Tab Doanh Thu
        JPanel pnlDoanhThu = new JPanel(new BorderLayout());
        String[] colsDoanhThu = {"Ngày Bán", "Số Hóa Đơn", "Tổng Doanh Thu"};
        modelDoanhThu = new DefaultTableModel(colsDoanhThu, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblDoanhThu = new JTable(modelDoanhThu);
        pnlDoanhThu.add(new JScrollPane(tblDoanhThu), BorderLayout.CENTER);

        JPanel pnlTong = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTongDoanhThu = new JLabel("Tổng doanh thu: 0 VND");
        lblTongDoanhThu.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongDoanhThu.setForeground(Color.RED);
        pnlTong.add(lblTongDoanhThu);
        pnlDoanhThu.add(pnlTong, BorderLayout.SOUTH);

        tabbedPane.addTab("Thống Kê Doanh Thu", pnlDoanhThu);

        // 2. Tab Top Thuốc
        JPanel pnlTopThuoc = new JPanel(new BorderLayout());
        String[] colsTopThuoc = {"Mã Thuốc", "Tên Thuốc", "Số Lượng Đã Bán", "Thành Tiền"};
        modelTopThuoc = new DefaultTableModel(colsTopThuoc, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblTopThuoc = new JTable(modelTopThuoc);
        pnlTopThuoc.add(new JScrollPane(tblTopThuoc), BorderLayout.CENTER);
        tabbedPane.addTab("Top Thuốc Bán Chạy", pnlTopThuoc);

        // 3. Tab Cảnh Báo
        JPanel pnlCanhBao = new JPanel(new BorderLayout());
        String[] colsCanhBao = {"Mã Thuốc", "Tên Thuốc", "Số Lượng Tồn", "Hạn Sử Dụng", "Tình Trạng"};
        modelCanhBao = new DefaultTableModel(colsCanhBao, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCanhBao = new JTable(modelCanhBao);
        pnlCanhBao.add(new JScrollPane(tblCanhBao), BorderLayout.CENTER);
        tabbedPane.addTab("Cảnh Báo Thuốc", pnlCanhBao);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void loadData() {
        Date tuNgay = (Date) spinTuNgay.getValue();
        Date denNgay = (Date) spinDenNgay.getValue();

        // 1. Load Doanh Thu
        modelDoanhThu.setRowCount(0);
        List<Object[]> doanhThuList = thongKeDAO.getDoanhThu(tuNgay, denNgay);
        double tongDoanhThu = 0;
        for (Object[] row : doanhThuList) {
            double dt = (Double) row[2];
            tongDoanhThu += dt;
            modelDoanhThu.addRow(new Object[]{
                row[0] != null ? sdf.format((java.sql.Date)row[0]) : "",
                row[1],
                df.format(dt)
            });
        }
        lblTongDoanhThu.setText("Tổng doanh thu: " + df.format(tongDoanhThu));

        // 2. Load Top Thuốc
        modelTopThuoc.setRowCount(0);
        List<Object[]> topThuocList = thongKeDAO.getTopThuocBanChay(tuNgay, denNgay);
        for (Object[] row : topThuocList) {
            modelTopThuoc.addRow(new Object[]{
                row[0], row[1], row[2], df.format((Double)row[3])
            });
        }

        // 3. Load Cảnh Báo
        modelCanhBao.setRowCount(0);
        List<Thuoc> sapHet = thuocDAO.getThuocSapHet();
        for (Thuoc t : sapHet) {
            modelCanhBao.addRow(new Object[]{
                t.getMaThuoc(), t.getTenThuoc(), t.getSoLuongTon(),
                t.getHanSuDung() != null ? sdf.format(t.getHanSuDung()) : "",
                "Sắp hết hàng (<= 10)"
            });
        }

        List<Thuoc> ganHetHan = thuocDAO.getThuocGanHetHan();
        for (Thuoc t : ganHetHan) {
            // Avoid duplicates if a medicine is both low stock and near expiration
            boolean exists = false;
            for (int i = 0; i < modelCanhBao.getRowCount(); i++) {
                if (modelCanhBao.getValueAt(i, 0).equals(t.getMaThuoc())) {
                    modelCanhBao.setValueAt("Sắp hết hàng & Gần hết hạn", i, 4);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                modelCanhBao.addRow(new Object[]{
                    t.getMaThuoc(), t.getTenThuoc(), t.getSoLuongTon(),
                    t.getHanSuDung() != null ? sdf.format(t.getHanSuDung()) : "",
                    "Gần hết hạn (<= 90 ngày)"
                });
            }
        }
    }
}
