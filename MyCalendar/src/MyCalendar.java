import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

class CalendarData {
	final int week = 7;
	final int height = 6;
	int year;
	int month;
	int date;
	int day1;
	int[][] dates = new int[height][week];
	int[] lastDate = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	int lastday;
	Calendar today = Calendar.getInstance();
	Calendar cal;
	public CalendarData() {
		year = today.get(Calendar.YEAR);
		month = today.get(Calendar.MONTH);
		date = today.get(Calendar.DAY_OF_MONTH);
		calData(today);
	}
	private int leap(int year) {
		if(year%4 == 0 && year%100 != 0 || year%400 == 0) 
			return 1;
		else return 0;
	}
	public void calData(Calendar cal) {
		day1 = (cal.get(Calendar.DAY_OF_WEEK)+7-(cal.get(Calendar.DAY_OF_MONTH))%7)%7;
		if(month == 1) lastday = lastDate[month] + leap(year);
		else lastday = lastDate[month];
		for(int i=0; i<height; i++) {
			for(int j=0; j<week; j++) {
				dates[i][j] = 0;
			}
		}
		
		for(int i=0, num = 1, k = 0; i<height; i++) {
			if(i==0) k = day1;
			else k=0;
			for(int j=k; j<week; j++) {
				if(num <= lastday) dates[i][j]=num++;
			}
		}
	}
	public void moveMonth(int mon) {
	    month += mon;
	    int yearDiff = month / 12;
	    year += yearDiff;
	    month %= 12;
	    
	    if(month<0) {
	    	year--;
	    	month = 11;
	    }

	    cal = new GregorianCalendar(year, month, date);
	    calData(cal);
	}
}

public class MyCalendar extends CalendarData {
	JFrame mainFrame = new JFrame("My Calendar");
	
	
	JPanel Info;
	    JButton nextMon;
	    JButton prevMon;
	    JLabel todayInfo;
	    JLabel cur;
	    moveMonthBut lMoveMonthBut = new moveMonthBut();
	    
	JPanel calendar;
	    JLabel weekday[];
	    JButton datesBut[][] = new JButton[6][7];
	    dateButsListener ldateButs = new dateButsListener();
	    
	JPanel clockPanel;
	    JLabel clock;
	    
	JPanel memo;
	    JPanel ButPanel;
	    JLabel selDate = new JLabel();
	    JTextArea memoArea;
	    JButton save;
	    JButton del;
	    JLabel error = new JLabel();
	    JScrollPane scroll;
	    
	String weekName[] = {"Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat"};
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MyCalendar();
			}
		});
	}
	public MyCalendar() {
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createPanel();
		mainFrame.setSize(700,500);
		mainFrame.setVisible(true);	
	}
	
	private void createPanel() {
		File memoFolder = new File("Memo");
        if (!memoFolder.exists()) {
            memoFolder.mkdir();
        }
		Info = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 0, 0);
        
	    nextMon = new JButton(">");
	    nextMon.addActionListener(lMoveMonthBut);
	    prevMon = new JButton("<");
	    prevMon.addActionListener(lMoveMonthBut);
	    todayInfo = new JLabel("Today: " + today.get(Calendar.YEAR)+"/"+(today.get(Calendar.MONTH)+1)+"/"+today.get(Calendar.DAY_OF_MONTH));
	    cur = new JLabel();
	    cur.setText(String.format("<html><table width=100><tr><th><font size=5>%02d / %d</font></th></tr></table></html>", month + 1, year));
	    
	    
	    addComponent(Info, todayInfo, gbc, 2, 0, 3, 1);
	    addComponent(Info, prevMon, gbc, 1, 1, 1, 1);
	    addComponent(Info, cur, gbc, 2, 1, 2, 1);
	    addComponent(Info, nextMon, gbc, 4, 1, 1, 1);
	    mainFrame.add(Info);
	    
	    calendar = new JPanel(new GridLayout(0,7,2,2));
	    weekday = new JLabel[7];
	    for(int i=0; i<week; i++) {
	    	weekday[i] = new JLabel(weekName[i]);
	    	weekday[i].setBorder(null);
	    	weekday[i].setHorizontalAlignment(JLabel.CENTER);
	    	weekday[i].setFont(new Font("SansSerif", Font.BOLD, 14));
	    	weekday[i].setBackground(new Color(197, 184, 160));
	    	if(i==0)
	    		weekday[i].setForeground(new Color(200, 50, 50));
	    	else if(i==6)
	    		weekday[i].setForeground(new Color(50, 100, 200));
	    	else
	    		weekday[i].setForeground(Color.WHITE);
	    	weekday[i].setOpaque(true);
	    	calendar.add(weekday[i]);
	    }
	    for(int i=0; i<height;i++) {
	    	for(int j=0; j<week; j++) {
	    		datesBut[i][j] = new JButton();
	    		datesBut[i][j].setBorderPainted(false);
	    		datesBut[i][j].setContentAreaFilled(false);
	    		datesBut[i][j].setBackground(Color.WHITE);
	    		datesBut[i][j].setOpaque(true);
	    		datesBut[i][j].addActionListener(ldateButs);
	    		calendar.add(datesBut[i][j]);
	    	}
	    }
	    calendar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	    show();
	    
	    clockPanel = new JPanel();
	    clockPanel.setLayout(new BorderLayout());
	    Clock clockUpdater = new Clock();
	    clockUpdater.run();
	    clock = new JLabel("", SwingConstants.RIGHT);
	    clock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    clockPanel.add(clock, BorderLayout.NORTH);
	    selDate.setText(String.format("<html><font size=3>%d/%02d/%02d</html>",
	    	    today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, today.get(Calendar.DAY_OF_MONTH)));
	    selDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
	    
	    memo = new JPanel(new BorderLayout());
	    memoArea = new JTextArea();
	    memoArea.setLineWrap(true);
	    memoArea.setWrapStyleWord(true);
	    scroll = new JScrollPane(memoArea);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    scroll.setPreferredSize(new Dimension(200, 200));
	    read();
	    
	    ButPanel = new JPanel();
	    save = new JButton("Save");
	    save.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		try {
	    			File f = new File("Memo");
	    			if(!f.isDirectory()) f.mkdir();
	    			
	    			String filePath = "Memo/" + year + String.format("%02d", month+1) + String.format("%02d" , date) + ".txt";
	    			String memo = memoArea.getText();
	    			
	    			Files.write(Paths.get(filePath), memo.getBytes());
	    			error.setText("Saved.");
	    			show();
	    		} catch (IOException e) {
	    			error.setText("Error: " + e.getMessage());
	    		}
	    	}
	    });
	    
	    del = new JButton("Delete");
	    del.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		try {
		    		String filePath =  "Memo/" + year + String.format("%02d", month+1) + String.format("%02d" , date) + ".txt";
		    		if (Files.exists(Paths.get(filePath))) {
		    			Files.delete(Paths.get(filePath));
		    			show();
		    			error.setText("Deleted.");
		    		} else {
		    			error.setText("Not found.");
		    		}
		    		
		    		memoArea.setText("");
		    		show();
	    		} catch (IOException e) {
	    			error.setText("Error: " + e.getMessage());
	    		}
	    	}
	    });
	    
	    ButPanel.add(save);
	    ButPanel.add(del);
	    memo.add(selDate, BorderLayout.NORTH);
	    memo.add(scroll, BorderLayout.CENTER);
	    memo.add(ButPanel, BorderLayout.SOUTH);
	    
	    JPanel WestPanel = new JPanel(new BorderLayout());
	    Dimension InfoSize = Info.getPreferredSize();
	    InfoSize.height = 90;
	    Info.setPreferredSize(InfoSize);
	    WestPanel.add(Info, BorderLayout.NORTH);
	    WestPanel.add(calendar, BorderLayout.CENTER);
	    
	    JPanel EastPanel = new JPanel(new BorderLayout());
	    Dimension clockPanelSize = clockPanel.getPreferredSize();
	    clockPanelSize.height = 65;
	    clockPanel.setPreferredSize(clockPanelSize);
	    EastPanel.add(memo, BorderLayout.CENTER);
	    EastPanel.add(clockPanel, BorderLayout.NORTH);
	    EastPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

	    JPanel BottomPanel = new JPanel();
	    BottomPanel.add(error);
	    
	    mainFrame.setLayout(new BorderLayout());
	    mainFrame.add(WestPanel, BorderLayout.CENTER);
	    mainFrame.add(EastPanel, BorderLayout.EAST);
	    mainFrame.add(BottomPanel, BorderLayout.SOUTH);
	    mainFrame.setVisible(true);
	    
	}
	
	private void addComponent(Container container, Component component, GridBagConstraints gbc, int x, int y, int width, int height) {
	        gbc.gridx = x;
	        gbc.gridy = y;
	        gbc.gridwidth = width;
	        gbc.gridheight = height;
	        container.add(component, gbc);
	}
	 
	private void read() {
		try {
		    File memoFile = new File("Memo/" + String.format("%04d%02d%02d.txt", year, month + 1, date));
		    if (memoFile.exists()) {
		        String memoAreaText = Files.readString(memoFile.toPath());
		        memoArea.setText(memoAreaText);
		    } else {
		        memoArea.setText("");
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	 
	private void show() {
		for (int i = 0; i < height; i++) {
	        for (int j = 0; j < week; j++) {
	            String fontColor = (j == 0) ? "red" : ((j == 6) ? "blue" : "black");

	            int day = dates[i][j];
	            String fileName = String.format("Memo/%04d%02d%02d.txt", year, month+1, day);
	            File memoFile = new File(fileName);
	            datesBut[i][j].removeAll();
	            String text = String.format("<html><font color=%s>%d</font></html>", fontColor, day);
	            datesBut[i][j].setText(text);
	            
	            if (memoFile.exists()) {
	                datesBut[i][j].setBorderPainted(true);
	                datesBut[i][j].setBorder(new LineBorder(new Color(255, 182, 193)));
	            } else {
	                datesBut[i][j].setBorderPainted(false);
	            }
	            
	            if (month == today.get(Calendar.MONTH) &&
	                    year == today.get(Calendar.YEAR) &&
	                    day == today.get(Calendar.DAY_OF_MONTH)) {
	            	datesBut[i][j].setBackground(new Color(230, 230, 250));

	            }

	            datesBut[i][j].setVisible(day != 0);
	            
	            
	        }
	    }
	}
	
	private class moveMonthBut implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == nextMon) moveMonth(1);
			else if(e.getSource() == prevMon) moveMonth(-1);
			cur.setText(String.format("<html><table width=100><tr><th><font size=5>%02d / %d</font></th></tr></table></html>", month + 1, year));
			
			for (int x=0;x<height;x++) {
            	for(int y=0; y<week; y++) {
            		datesBut[x][y].setBackground(Color.WHITE);
            	}
            }
			show();
		}
	}
	
	private class dateButsListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < height; i++) {
	            for (int j = 0; j < week; j++) {
	                if (e.getSource() == datesBut[i][j]) {
	                    date = dates[i][j];
	                    cal = new GregorianCalendar(year, month, date);
	                    selDate.setText(String.format("<html><font size=3>%d/%02d/%02d</html>",
	                    	    year, month + 1, date));
	                    for (int x=0;x<height;x++) {
	                    	for(int y=0; y<week; y++) {
	                    		datesBut[x][y].setBackground(Color.WHITE);
	                    	}
	                    }
	                    
	                    show();
	                    datesBut[i][j].setBackground(new Color(255, 255, 102));
	                    read();
	                    return;
	                }
	            }
	        }
		}
	}
	
	private class Clock {
		public void run() {
			Timer timer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					updateClock();
				}
			});
			timer.start();
		}
		
		private void updateClock() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
			String formattedDate = dateFormat.format(new Date());
			clock.setText(formattedDate);
		}
	}
}


