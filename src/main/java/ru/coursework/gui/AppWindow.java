package ru.coursework.gui;

import ru.coursework.jdbc.*;
import ru.coursework.security.PasswordHasher;
import ru.coursework.validation.UserDataValidation;
import ru.coursework.validation.UserValidation;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.NumberFormat;
import java.util.List;

import static ru.coursework.gui.OptionPane.showStyledDialog;

public class AppWindow {

    private static final PasswordHasher hasher = new PasswordHasher();

    private static final User user = new User();
    private static final UserData userData = new UserData();
    private static final Account account = new Account();
    private static final AccountTransaction accountTransaction = new AccountTransaction();
    private static final StockTransaction stockTransaction = new StockTransaction();
    private static final UserStock userStock = new UserStock();
    private static final Stock stock = new Stock();
    private static final Dividend dividend = new Dividend();

    private static final UserDataValidation userDataValidation = new UserDataValidation();
    private static final UserValidation userValidation = new UserValidation();

    private static final MouseAdapter mouseAdapter = getMouseAdapter();

    private static final JPanel mainPanel = new JPanel(new BorderLayout());
    private static final JPanel authPanel = new JPanel();
    private static final JPanel registerPanel = new JPanel();
    private static final JPanel headerPanelRight = new JPanel();
    private static final JPanel accountOperationPanel = new JPanel();
    private static final JPanel accountOperationButtonPanel = new JPanel();
    private static final JPanel transactionsHistoryPanel = new JPanel();
    private static final JPanel stockOperationPanel = new JPanel();
    private static final JPanel stockHistoryPanel = new JPanel();
    private static final JPanel stockPanel = new JPanel();
    private static final JPanel userStockPanel = new JPanel();
    private static final JPanel dividendPanel = new JPanel();

    private static JTextField nameField;
    private static JTextField loginField;
    private static JTextField mailField;
    private static JTextField phoneField;
    private static JPasswordField passwordField;
    private static JPasswordField acceptField;
    private static JFormattedTextField amountField;
    private static JFormattedTextField purchaseField;
    private static JFormattedTextField saleField;

    private static JComboBox<String> purchaseComboBox;
    private static JComboBox<String> saleComboBox;

    private static Long currentUserId;

    public static void openAppWindow() {
        JFrame appFrame = new JFrame("Приложение инвестиций");
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(1100, 550);
        ImageIcon icon = new ImageIcon("assets/icon.png");
        appFrame.setIconImage(icon.getImage());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        headerPanel.setBackground(new Color(0x6cebb6));

        headerPanelRight.setOpaque(false);

        JButton authButton = addHeaderButtonStyles("Войти", 85);

        JButton registerButton = addHeaderButtonStyles("Зарегистрироваться", 180);

        JPanel headerPanelLeft = new JPanel();
        headerPanelLeft.setOpaque(false);

        JButton actionButton = addHeaderButtonStyles("Пополнить/Вывести", 180);

        JButton transactionButton = addHeaderButtonStyles("Купить/Продать", 150);

        JButton stockButton = addHeaderButtonStyles("Акции", 85);

        JButton userStockButton = addHeaderButtonStyles("Ваши акции", 120);

        JButton dividendButton = addHeaderButtonStyles("Дивиденды", 120);

        MouseAdapter mouseAdapter = getMouseAdapter();

        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        mainPanel.setBackground(new Color(0xe8e8e8));

        authButton.addActionListener(e -> getAuthPanel());
        registerButton.addActionListener(e -> getRegisterPanel());
        actionButton.addActionListener(e -> getAccountOperationPanel());
        transactionButton.addActionListener(e -> getStockOperationPanel());
        stockButton.addActionListener(e -> getStockPanel());
        userStockButton.addActionListener(e -> getUserStockPanel());
        dividendButton.addActionListener(e -> getDividendPanel());

        authButton.addMouseListener(mouseAdapter);
        registerButton.addMouseListener(mouseAdapter);
        actionButton.addMouseListener(mouseAdapter);
        transactionButton.addMouseListener(mouseAdapter);
        stockButton.addMouseListener(mouseAdapter);
        userStockButton.addMouseListener(mouseAdapter);
        dividendButton.addMouseListener(mouseAdapter);

        headerPanelRight.add(authButton);
        headerPanelRight.add(registerButton);

        headerPanelLeft.add(actionButton);
        headerPanelLeft.add(transactionButton);
        headerPanelLeft.add(stockButton);
        headerPanelLeft.add(userStockButton);
        headerPanelLeft.add(dividendButton);

        headerPanel.add(headerPanelLeft, BorderLayout.WEST);
        headerPanel.add(headerPanelRight, BorderLayout.EAST);

        appFrame.add(headerPanel, BorderLayout.NORTH);
        appFrame.add(mainPanel);
        appFrame.setLocationRelativeTo(null);
        appFrame.setVisible(true);
    }

    private static void getAuthPanel() {
        authPanel.setLayout(new BoxLayout(authPanel, BoxLayout.Y_AXIS));
        authPanel.setOpaque(false);

        mainPanel.removeAll();
        authPanel.removeAll();

        JLabel loginLabel = addLabelStyles("Введите логин");

        loginField = addTextFieldStyles();

        JLabel passwordLabel = addLabelStyles("Введите пароль");

        passwordField = addPasswordFieldStyles();

        JButton button = addButtonStyles("Войти");

        button.addActionListener(e -> doUserAuthentication());

        authPanel.add(loginLabel);
        authPanel.add(loginField);
        authPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        authPanel.add(passwordLabel);
        authPanel.add(passwordField);
        authPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        authPanel.add(button);
        mainPanel.add(authPanel, BorderLayout.EAST);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void doUserAuthentication() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());
        String msg = user.authenticateUser(login, password);

        if (msg.equals("Вы авторизованы")) {
            showStyledDialog(mainPanel, msg, "Вход", JOptionPane.INFORMATION_MESSAGE);
            currentUserId = user.getUserId(login);
            headerPanelRight.removeAll();
            mainPanel.removeAll();
            authPanel.removeAll();
            mainPanel.repaint();
            mainPanel.revalidate();
            Long id = user.getUserId(login);
            int balance = account.getBalance(id);

            JLabel accountLabel = new JLabel("Баланс: " + balance + " руб.");
            accountLabel.setMaximumSize(new Dimension(200, 25));
            accountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            accountLabel.setFont(new Font("Impact", Font.PLAIN, 16));
            accountLabel.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
            headerPanelRight.add(accountLabel);
        } else {
            showStyledDialog(mainPanel, msg, "Ошибка авторизации", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private static void getRegisterPanel() {
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setOpaque(false);

        mainPanel.removeAll();
        registerPanel.removeAll();

        JLabel nameLabel = addLabelStyles("Введите ФИО");

        nameField = addTextFieldStyles();

        JLabel loginLabel = addLabelStyles("Введите логин");

        loginField = addTextFieldStyles();

        JLabel mailLabel = addLabelStyles("Введите почту");

        mailField = addTextFieldStyles();

        JLabel phoneLabel = addLabelStyles("Введите номер телефона");

        phoneField = addTextFieldStyles();

        JLabel passwordLabel = addLabelStyles("Введите пароль");

        passwordField = addPasswordFieldStyles();

        JLabel acceptLabel = addLabelStyles("Подтвердите пароль");

        acceptField = addPasswordFieldStyles();

        JButton button = addButtonStyles("Зарегистрироваться");

        button.addActionListener(e -> doUserRegistration());

        registerPanel.add(nameLabel);
        registerPanel.add(nameField);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        registerPanel.add(loginLabel);
        registerPanel.add(loginField);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        registerPanel.add(mailLabel);
        registerPanel.add(mailField);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        registerPanel.add(phoneLabel);
        registerPanel.add(phoneField);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        registerPanel.add(passwordLabel);
        registerPanel.add(passwordField);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        registerPanel.add(acceptLabel);
        registerPanel.add(acceptField);
        registerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        registerPanel.add(button);
        mainPanel.add(registerPanel, BorderLayout.EAST);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void doUserRegistration() {
        String fullname = nameField.getText();
        String login = loginField.getText();
        String email = mailField.getText();
        String number = phoneField.getText();
        String password = new String(passwordField.getPassword());
        String acceptPassword = new String(acceptField.getPassword());

        if (!userValidation.isLoginValid(login)) {
            showStyledDialog(mainPanel, "Проверьте логин\nВ логине могут содержаться только цифры и латиница", "Ошибка в регистрации", JOptionPane.ERROR_MESSAGE);
        } else if (!userDataValidation.isUserEmailValid(email)) {
            showStyledDialog(mainPanel, "Проверьте корректность почты\nШаблон почты - (user@mail.ru)", "Ошибка в регистрации", JOptionPane.ERROR_MESSAGE);
        } else if (!userDataValidation.isUserNumberValid(number)) {
            showStyledDialog(mainPanel, "Проверьте корректность номера\nШаблон номера - (+7__________)", "Ошибка в регистрации", JOptionPane.ERROR_MESSAGE);
        } else if (!userValidation.isPasswordValid(password)) {
            showStyledDialog(mainPanel, "Проверьте корректность пароля\nПароль должен содержать строчные и прописные буквы, цифры, и спец.символы\nМинимальная длина - 8", "Ошибка в регистрации", JOptionPane.ERROR_MESSAGE);
        } else if (!password.equals(acceptPassword)) {
            showStyledDialog(mainPanel, "Проверьте пароль", "Ошибка в регистрации", JOptionPane.ERROR_MESSAGE);
        } else {
            String hash = hasher.hashPassword(password);
            user.createUser(login, hash);
            Long id = user.getUserId(login);
            userData.createUserData(id, fullname, email, number);

            mainPanel.removeAll();
            registerPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    private static void getAccountOperationPanel() {
        if (currentUserId == null) {
            showStyledDialog(mainPanel, "Вы не авторизованы", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        accountOperationPanel.removeAll();
        accountOperationButtonPanel.removeAll();
        transactionsHistoryPanel.removeAll();
        mainPanel.removeAll();

        accountOperationPanel.setLayout(new BoxLayout(accountOperationPanel, BoxLayout.Y_AXIS));
        accountOperationPanel.setOpaque(false);

        accountOperationButtonPanel.setLayout(new BoxLayout(accountOperationButtonPanel, BoxLayout.Y_AXIS));
        accountOperationButtonPanel.setOpaque(false);

        JLabel amountLabel = addLabelStyles("Введите сумму");

        amountField = addFormattedTextFieldStyles();

        JButton depositButton = addButtonStyles("Пополнить");
        JButton withdrawButton = addButtonStyles("Вывести");

        depositButton.addActionListener(e -> doAccountDeposit());
        withdrawButton.addActionListener(e -> doAccountWithdraw());

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setOpaque(false);

        transactionsHistoryPanel.setLayout(new BoxLayout(transactionsHistoryPanel, BoxLayout.Y_AXIS));
        transactionsHistoryPanel.setOpaque(false);

        JLabel transactionLabel = addLabelStyles("История операций");
        transactionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        transactionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String[] columnNames = {"Сумма", "Дата", "Время"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        JTable transactionTable = new JTable(model);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 14));
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        transactionTable.setShowGrid(false);
        transactionTable.setShowHorizontalLines(false);
        transactionTable.setShowVerticalLines(false);
        transactionTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        try {
            List<String[]> transactions = accountTransaction.getAccountTransactions(currentUserId);
            for (String[] transaction : transactions) {
                model.addRow(new Object[]{transaction[0], transaction[1], transaction[2]});
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < transactionTable.getColumnCount(); i++) {
            transactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        ((JLabel)transactionTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        transactionsHistoryPanel.add(transactionLabel);
        transactionsHistoryPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        transactionsHistoryPanel.add(scrollPane);

        accountOperationPanel.add(amountLabel);
        accountOperationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        accountOperationPanel.add(amountField);
        accountOperationButtonPanel.add(depositButton);
        accountOperationButtonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        accountOperationButtonPanel.add(withdrawButton);
        containerPanel.add(accountOperationPanel);
        containerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        containerPanel.add(accountOperationButtonPanel);

        mainPanel.add(containerPanel, BorderLayout.WEST);
        mainPanel.add(transactionsHistoryPanel, BorderLayout.EAST);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void doAccountDeposit() {
        int amount = new Integer(amountField.getText());
        account.updateUserAccount(currentUserId, amount);
        Date transactionDate = new Date(System.currentTimeMillis());
        Time transactionTime = new Time(System.currentTimeMillis());

        int balance = account.getBalance(currentUserId);
        updateBalance(balance);

        showStyledDialog(mainPanel, "Счет пополнен на " + amount + " руб.", "Уведомление о пополнении", JOptionPane.INFORMATION_MESSAGE);
        accountTransaction.createAccountTransaction(currentUserId, amount, transactionDate, transactionTime);

        accountOperationPanel.removeAll();
        accountOperationButtonPanel.removeAll();
        mainPanel.removeAll();

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void doAccountWithdraw() {
        try {
            int amount = Integer.parseInt(amountField.getText());
            Date transactionDate = new Date(System.currentTimeMillis());
            Time transactionTime = new Time(System.currentTimeMillis());
            accountTransaction.createAccountTransaction(currentUserId, -amount, transactionDate, transactionTime);
            account.updateUserAccount(currentUserId, -amount);
            int balance = account.getBalance(currentUserId);
            updateBalance(balance);
            showStyledDialog(mainPanel, "Со счета снято " + amount + " руб.", "Уведомление о выводе", JOptionPane.INFORMATION_MESSAGE);

            accountOperationPanel.removeAll();
            accountOperationButtonPanel.removeAll();
            mainPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                if (sqlEx.getMessage().contains("Недостаточно средств для вывода")) {
                    showStyledDialog(mainPanel, sqlEx.getMessage(), "Ошибка вывода", JOptionPane.ERROR_MESSAGE);
                } else {
                    showStyledDialog(mainPanel, "Ошибка при выполнении операции: " + sqlEx.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showStyledDialog(mainPanel, "Неизвестная ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void updateBalance(int balance) {
        headerPanelRight.removeAll();
        JLabel accountLabel = new JLabel("Баланс: " + balance + " руб");
        accountLabel.setMaximumSize(new Dimension(200, 25));
        accountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        accountLabel.setFont(new Font("Impact", Font.PLAIN, 16));
        accountLabel.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        headerPanelRight.add(accountLabel);
        headerPanelRight.revalidate();
        headerPanelRight.repaint();
    }

    private static void getStockPanel() {
        stockPanel.removeAll();
        mainPanel.removeAll();

        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        stockPanel.setOpaque(false);

        JLabel stockLabel = addLabelStyles("Доступные акции");
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        stockLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stockLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] columnNames = {"Компания", "Цена за шт."};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        JTable stockTable = new JTable(model);
        stockTable.setFont(new Font("Arial", Font.PLAIN, 14));
        stockTable.setRowHeight(25);
        stockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        stockTable.setShowGrid(false);
        stockTable.setShowHorizontalLines(false);
        stockTable.setShowVerticalLines(false);
        stockTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        try {
            List<String[]> stocks = stock.getStocks();
            for (String[] s : stocks) {
                model.addRow(new Object[]{s[0], s[1]});
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < stockTable.getColumnCount(); i++) {
            stockTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        ((JLabel)stockTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        stockPanel.add(stockLabel);
        stockPanel.add(scrollPane);
        mainPanel.add(stockPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void getDividendPanel() {
        dividendPanel.removeAll();
        mainPanel.removeAll();

        dividendPanel.setLayout(new BoxLayout(dividendPanel, BoxLayout.Y_AXIS));
        dividendPanel.setOpaque(false);

        JLabel dividendLabel = addLabelStyles("Ближайшие выплаты");
        dividendLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dividendLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dividendLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] columnNames = {"Акция", "Сумма выплаты", "Процент от стоимости акции", "Дата выплаты"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        JTable dividendTable = new JTable(model);
        dividendTable.setFont(new Font("Arial", Font.PLAIN, 14));
        dividendTable.setRowHeight(25);
        dividendTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        dividendTable.setShowGrid(false);
        dividendTable.setShowHorizontalLines(false);
        dividendTable.setShowVerticalLines(false);
        dividendTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        try {
            List<String[]> dividends = dividend.getDividends();
            for (String[] d : dividends) {
                model.addRow(new Object[]{d[0], d[1], d[2], d[3]});
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < dividendTable.getColumnCount(); i++) {
            dividendTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        ((JLabel)dividendTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(dividendTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        dividendPanel.add(dividendLabel);
        dividendPanel.add(scrollPane);
        mainPanel.add(dividendPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void getStockOperationPanel() {
        if (currentUserId == null) {
            showStyledDialog(mainPanel, "Вы не авторизованы", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        stockOperationPanel.removeAll();
        stockHistoryPanel.removeAll();
        mainPanel.removeAll();

        stockOperationPanel.setLayout(new BoxLayout(stockOperationPanel, BoxLayout.Y_AXIS));
        stockOperationPanel.setOpaque(false);

        JLabel purchaseLabel = addLabelStyles("Выберите акцию");

        List<String> purchaseStocks = stock.getStockNames();
        purchaseComboBox = new JComboBox<>(purchaseStocks.toArray(new String[0]));
        purchaseComboBox.setMaximumSize(new Dimension(300, 30));

        purchaseField = addFormattedTextFieldStyles();

        JButton purchaseButton = addButtonStyles("Купить");
        purchaseButton.addActionListener(e -> purchaseStock());

        JLabel saleLabel = addLabelStyles("Выберите акцию");

        List<String> saleStocks = userStock.getUserStockNames(currentUserId);
        saleComboBox = new JComboBox<>(saleStocks.toArray(new String[0]));
        saleComboBox.setMaximumSize(new Dimension(300, 30));

        saleField = addFormattedTextFieldStyles();

        JButton saleButton = addButtonStyles("Продать");
        saleButton.addActionListener(e -> saleStock());

        JLabel purchaseSumLabel = addLabelStyles("Сумма: 0");
        JLabel saleSumLabel = addLabelStyles("Сумма: 0");

        DocumentListener purchaseListener = createStockSumListener(purchaseComboBox, purchaseField, purchaseSumLabel);
        DocumentListener saleListener = createStockSumListener(saleComboBox, saleField, saleSumLabel);

        purchaseField.getDocument().addDocumentListener(purchaseListener);
        saleField.getDocument().addDocumentListener(saleListener);

        purchaseComboBox.addActionListener(e -> purchaseListener.insertUpdate(null));
        saleComboBox.addActionListener(e -> saleListener.insertUpdate(null));

        stockHistoryPanel.setLayout(new BoxLayout(stockHistoryPanel, BoxLayout.Y_AXIS));
        stockHistoryPanel.setOpaque(false);

        JLabel stockTransactionLabel = addLabelStyles("История операций");
        stockTransactionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        stockTransactionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String[] columnNames = {"Акция", "Количество", "Общая сумма", "Дата", "Время"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        JTable stockTransactionTable = new JTable(model);
        stockTransactionTable.setFont(new Font("Arial", Font.PLAIN, 14));
        stockTransactionTable.setRowHeight(25);
        stockTransactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        stockTransactionTable.setShowGrid(false);
        stockTransactionTable.setShowHorizontalLines(false);
        stockTransactionTable.setShowVerticalLines(false);
        stockTransactionTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        try {
            List<String[]> stockTransactions = stockTransaction.getStockTransactionInfo(currentUserId);
            for (String[] st : stockTransactions) {
                model.addRow(new Object[]{st[0], st[1], st[2], st[3], st[4]});
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < stockTransactionTable.getColumnCount(); i++) {
            stockTransactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        ((JLabel)stockTransactionTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(stockTransactionTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        stockOperationPanel.add(purchaseLabel);
        stockOperationPanel.add(purchaseComboBox);
        stockOperationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        stockOperationPanel.add(purchaseField);
        stockOperationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        stockOperationPanel.add(purchaseSumLabel);
        stockOperationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        stockOperationPanel.add(purchaseButton);
        stockOperationPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        stockOperationPanel.add(saleLabel);
        stockOperationPanel.add(saleComboBox);
        stockOperationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        stockOperationPanel.add(saleField);
        stockOperationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        stockOperationPanel.add(saleSumLabel);
        stockOperationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        stockOperationPanel.add(saleButton);

        stockHistoryPanel.add(stockTransactionLabel);
        stockHistoryPanel.add(scrollPane);

        mainPanel.add(stockHistoryPanel, BorderLayout.EAST);
        mainPanel.add(stockOperationPanel, BorderLayout.WEST);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void purchaseStock() {
        String selectedStock = (String) purchaseComboBox.getSelectedItem();
        int count = Integer.parseInt(purchaseField.getText().replaceAll(",", ""));

        Long stockId = stock.getStockId(selectedStock);
        int stockPrice = stock.getStockPrice(stockId);
        int totalSum = stockPrice * count;
        int balance = account.getBalance(currentUserId);

        if (balance < totalSum) {
            showStyledDialog(mainPanel, "Недостаточно средств для покупки", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date transactionDate = new Date(System.currentTimeMillis());
        Time transactionTime = new Time(System.currentTimeMillis());
        stockTransaction.createStockTransaction(currentUserId, stockId, count, -totalSum, transactionDate, transactionTime);

        userStock.addUserStock(currentUserId, stockId, count);

        account.updateUserAccount(currentUserId, -totalSum);
        updateBalance(account.getBalance(currentUserId));

        showStyledDialog(mainPanel, "Успешно куплено " + count + " акций " + selectedStock, "Покупка акций", JOptionPane.INFORMATION_MESSAGE);

        stockOperationPanel.removeAll();
        stockHistoryPanel.removeAll();
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static void saleStock() {
        try {
            String selectedStock = (String) saleComboBox.getSelectedItem();
            int count = Integer.parseInt(saleField.getText());

            if (count <= 0) {
                showStyledDialog(mainPanel, "Количество должно быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Long stockId = stock.getStockId(selectedStock);
            int stockPrice = stock.getStockPrice(stockId);
            int totalSum = stockPrice * count;

            Date transactionDate = new Date(System.currentTimeMillis());
            Time transactionTime = new Time(System.currentTimeMillis());

            stockTransaction.createStockTransaction(currentUserId, stockId, -count, totalSum, transactionDate, transactionTime);

            userStock.addUserStock(currentUserId, stockId, -count);

            account.updateUserAccount(currentUserId, totalSum);
            updateBalance(account.getBalance(currentUserId));

            showStyledDialog(mainPanel, "Успешно продано " + count + " акций " + selectedStock,
                    "Продажа акций", JOptionPane.INFORMATION_MESSAGE);

            stockOperationPanel.removeAll();
            stockHistoryPanel.removeAll();
            mainPanel.removeAll();
            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                if (sqlEx.getMessage().contains("Недостаточно акций")) {
                    showStyledDialog(mainPanel, sqlEx.getMessage(), "Ошибка продажи", JOptionPane.ERROR_MESSAGE);
                } else {
                    showStyledDialog(mainPanel, "Ошибка при выполнении операции: " + sqlEx.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                showStyledDialog(mainPanel, "Неизвестная ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void getUserStockPanel() {
        if (currentUserId == null) {
            showStyledDialog(mainPanel, "Вы не авторизованы", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        userStockPanel.removeAll();
        mainPanel.removeAll();

        userStockPanel.setLayout(new BoxLayout(userStockPanel, BoxLayout.Y_AXIS));
        userStockPanel.setOpaque(false);

        JLabel userStocksLabel = addLabelStyles("Ваши акции");
        userStocksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userStocksLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userStocksLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        long stockSummary = 0;
        try {
            stockSummary = userStock.getUserStockSummary(currentUserId);
        } catch (Exception e) {
            System.err.println("Ошибка при расчете стоимости портфеля: " + e.getMessage());
        }

        JLabel stockSummaryLabel = addLabelStyles("Общая стоимость: " + stockSummary + " руб.");
        stockSummaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        stockSummaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        stockSummaryLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        stockSummaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        stockSummaryLabel.setForeground(new Color(0x0c4d48));

        String[] columnNames = {"Акция", "Количество", "Общая сумма" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        JTable userStocksTable = new JTable(model);
        userStocksTable.setFont(new Font("Arial", Font.PLAIN, 14));
        userStocksTable.setRowHeight(25);
        userStocksTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        userStocksTable.setShowGrid(false);
        userStocksTable.setShowHorizontalLines(false);
        userStocksTable.setShowVerticalLines(false);
        userStocksTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());

        try {
            List<String[]> userStocks = userStock.getUserStocks(currentUserId);
            for (String[] us : userStocks) {
                model.addRow(new Object[]{us[0], us[1], us[2]});
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < userStocksTable.getColumnCount(); i++) {
            userStocksTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        ((JLabel)userStocksTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(userStocksTable);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        userStockPanel.add(userStocksLabel);
        userStockPanel.add(scrollPane);
        userStockPanel.add(stockSummaryLabel);
        mainPanel.add(userStockPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private static MouseAdapter getMouseAdapter() {
        return new MouseAdapter() {
            private final Border defaultBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
            private final Border hoverBorder = BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x34918b)),
                    BorderFactory.createEmptyBorder(1, 1, 1, 1)
                    );

            @Override
            public void mouseEntered(MouseEvent event) {
                JButton button = (JButton) event.getSource();
                button.setBorder(hoverBorder);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                JButton button = (JButton) event.getSource();
                button.setBorder(defaultBorder);
            }

            @Override
            public void mousePressed(MouseEvent event) {
                JButton button = (JButton) event.getSource();
                button.setBackground(new Color(0x34918b));
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                JButton button = (JButton) event.getSource();
                button.setBackground(new Color(0x0c4d48));
            }
        };
    }

    private static JButton addHeaderButtonStyles(String text, int width) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(width, 35));
        button.setFont(new Font("Impact", Font.PLAIN, 16));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(new Color(0x0c4d48));
        button.setForeground(Color.WHITE);
        return button;
    }

    private static JLabel addLabelStyles(String text) {
        JLabel label = new JLabel(text) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 25);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        label.setMaximumSize(new Dimension(200, 25));
        label.setFont(new Font("Dialog", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private static JTextField addTextFieldStyles() {
        JTextField textField = new JTextField() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 25);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setBorder(BorderFactory.createLineBorder(new Color(0x0c4d48), 2));
        return textField;
    }

    private static JPasswordField addPasswordFieldStyles() {
        JPasswordField passwordField = new JPasswordField() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 25);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0x0c4d48), 2));
        return passwordField;
    }

    private static JFormattedTextField addFormattedTextFieldStyles() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);

        JFormattedTextField formattedTextField = new JFormattedTextField(format) {
            @Override
            public void processKeyEvent(KeyEvent ev) {
                if (ev.getID() == KeyEvent.KEY_TYPED) {
                    char c = ev.getKeyChar();
                    if (!Character.isDigit(c) && c != '\b') {
                        ev.consume();
                        return;
                    }
                }
                super.processKeyEvent(ev);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 25);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };

        formattedTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        formattedTextField.setBorder(BorderFactory.createLineBorder(new Color(0x0c4d48), 2));
        return formattedTextField;
    }

    private static JButton addButtonStyles(String text) {
        JButton button = new JButton(text) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(160, 30);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Dialog", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(new Color(0x0c4d48));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        button.addMouseListener(mouseAdapter);
        return button;
    }

    private static DocumentListener createStockSumListener(JComboBox<String> comboBox, JTextField textField, JLabel sumLabel) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSum();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSum();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSum();
            }

            private void updateSum() {
                try {
                    String stockName = (String) comboBox.getSelectedItem();
                    Long stockId = stock.getStockId(stockName);
                    int price = stock.getStockPrice(stockId);
                    int quantity = Integer.parseInt(textField.getText().trim());
                    int sum = price * quantity;
                    sumLabel.setText(String.format("Сумма: " + sum));
                } catch (Exception ex) {
                    sumLabel.setText("Сумма: 0");
                }
            }
        };
    }
}
