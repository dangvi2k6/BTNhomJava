package controller;

import dao.ThuocDAO;
import view.ThuocPanel;

public class ThuocController {
    
    private ThuocPanel view;
    private ThuocDAO dao;

    public ThuocController(ThuocPanel view) {
        this.view = view;
        this.dao = new ThuocDAO();
        
        // TODO: Move event listeners from ThuocPanel here
        // Example: view.getBtnAdd().addActionListener(e -> addThuoc());
    }
}
