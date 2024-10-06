package cz.fejdam;

import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.FontFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;

public class Main {

    private static boolean fullPage = true;
    private static File[] selectedFiles = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Image to PDF Converter");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        Font systemFont = new Font("Dialog", Font.PLAIN, 12);

        JButton selectFilesButton = new JButton("Select Images");
        selectFilesButton.setFont(systemFont);

        JRadioButton fullPageOption = new JRadioButton("Full Page", true);
        JRadioButton fitSizeOption = new JRadioButton("Fit to Page");
        ButtonGroup group = new ButtonGroup();
        group.add(fullPageOption);
        group.add(fitSizeOption);

        fullPageOption.addActionListener(e -> fullPage = true);
        fitSizeOption.addActionListener(e -> fullPage = false);

        JButton startProcessButton = new JButton("Start Process");
        startProcessButton.setFont(systemFont);

        mainPanel.add(selectFilesButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(fullPageOption);
        mainPanel.add(fitSizeOption);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(startProcessButton);

        JPanel statusPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("Status: Waiting for input");
        JLabel versionLabel = new JLabel("Version: 1.0.0", SwingConstants.RIGHT);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(versionLabel, BorderLayout.EAST);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        selectFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fileDialog = new FileDialog((Frame) null, "Select Images", FileDialog.LOAD);
                fileDialog.setMultipleMode(true);
                fileDialog.setVisible(true);
                selectedFiles = fileDialog.getFiles();

                if (selectedFiles.length > 0) {
                    statusLabel.setText("Status: " + selectedFiles.length + " images selected.");
                } else {
                    statusLabel.setText("Status: No images selected.");
                }
            }
        });

        startProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFiles != null && selectedFiles.length > 0) {
                    convertImagesToPDF(selectedFiles, fullPage);
                    statusLabel.setText("Status: PDF created successfully.");
                } else {
                    statusLabel.setText("Status: No images selected.");
                }
            }
        });
    }

    public static void convertImagesToPDF(File[] imageFiles, boolean fullPage) {
        Document document = new Document();
        try {
            String home = System.getProperty("user.home");
            String outputFile = home + "/Desktop/converted_images.pdf";

            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();

            for (File imageFile : imageFiles) {
                Image img = Image.getInstance(imageFile.getAbsolutePath());

                if (fullPage) {
                    img.setAlignment(Element.ALIGN_CENTER);
                    img.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                    document.setPageSize(PageSize.A4);
                } else {
                    img.scaleToFit(500, 700);
                }

                document.add(img);
                document.newPage();
            }

            Paragraph footer = new Paragraph("created by: Filip \"Fejdam\" Adam\nGitHub: https://github.com/FiDaLip/imgToPDF",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
