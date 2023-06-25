import java.net.*;
import java.io.*;
import javax.swing.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
 
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.util.Timer;
import java.util.TimerTask;

public class MyClient extends JFrame implements MouseListener,MouseMotionListener {
	private JButton buttonArray[];//�{�^���p�̔z��
	private JButton IconArray[];//�{�^���p�̔z��
	private JButton PassButton;
	private JButton resultButton;
	private JButton restartButton;
	private JButton exitButton;
	private JButton progressButton;
	private Container c;
	private ImageIcon blackIcon, whiteIcon, boardIcon,boardRedIcon;
	private ImageIcon myIcon, yourIcon;
	private ImageIcon blackUserIcon,whiteUserIcon,waitIcon,pass;
	private ImageIcon win,lose,draw,restart,exit;
	private ImageIcon progress;
	private int myColor;
	private int myTurn;
	private int turnCount;
	private int[] passCountList = new int[99];
	private int restartCount;
	PrintWriter out;//�o�͗p�̃��C�^�[

	public MyClient() {
		//���O�̓��̓_�C�A���O���J��
		String myName = JOptionPane.showInputDialog(null,"���O����͂��Ă�������","���O�̓���",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//���O���Ȃ��Ƃ��́C"No name"�Ƃ���
		}
		String IP = JOptionPane.showInputDialog(null,"IP�A�h���X����͂��Ă�������","IP�A�h���X�̓���",JOptionPane.QUESTION_MESSAGE);
		if(IP.equals("")){
			myName = "localhost";
		}

		//�E�B���h�E���쐬����
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�E�B���h�E�����Ƃ��ɁC����������悤�ɐݒ肷��
		setTitle("MyClient");//�E�B���h�E�̃^�C�g����ݒ肷��
		setSize(1200,900);//�E�B���h�E�̃T�C�Y��ݒ肷��
		c = getContentPane();//�t���[���̃y�C�����擾����

		//�A�C�R���̐ݒ�
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");
		boardRedIcon = new ImageIcon("GreenFrameRed.jpg");
		whiteUserIcon = new ImageIcon("white_rabitA.jpg");
		blackUserIcon = new ImageIcon("black_kurooniA.jpg");
		waitIcon = new ImageIcon("wait.jpg");
		pass = new ImageIcon("pass.jpg");
		win = new ImageIcon("win.jpg");
		lose = new ImageIcon("lose.jpg");
		draw = new ImageIcon("draw.jpg");
		restart = new ImageIcon("restart.jpg");
		exit = new ImageIcon("exit.jpg");
		progress = new ImageIcon("progress.jpg");
		


		c.setLayout(null);//�������C�A�E�g�̐ݒ���s��Ȃ�
		//�{�^���̐���
		buttonArray = new JButton[64];//�{�^���̔z����T�쐬����[0]����[4]�܂Ŏg����
		for(int i=0;i<64;i++){
			buttonArray[i] = new JButton(boardIcon);//�{�^���ɃA�C�R����ݒ肷��
			c.add(buttonArray[i]);//�y�C���ɓ\��t����
			int x = (i % 8) * 50;
			int y = (i / 8) * 50;
			buttonArray[i].setBounds(x + 10, y + 10,50,50);//�{�^���̑傫���ƈʒu��ݒ肷��D(x���W�Cy���W,x�̕�,y�̕��j
			buttonArray[i].addMouseListener(this);//�{�^�����}�E�X�ł�������Ƃ��ɔ�������悤�ɂ���
			buttonArray[i].addMouseMotionListener(this);//�{�^�����}�E�X�œ��������Ƃ����Ƃ��ɔ�������悤�ɂ���
			buttonArray[i].setActionCommand(Integer.toString(i));//�{�^���ɔz��̏���t������i�l�b�g���[�N����ăI�u�W�F�N�g�����ʂ��邽�߁j
		}
		//�����Ֆʂ̐ݒ�
		
		buttonArray[27].setIcon(whiteIcon);
		buttonArray[28].setIcon(blackIcon);
		buttonArray[35].setIcon(blackIcon);
		buttonArray[36].setIcon(whiteIcon);
		
		

		//�e�X�g

		/*
		for(int i = 0;i< 56; i++){
			buttonArray[i].setIcon(blackIcon);
		}
		for(int i = 57 ; i < 63; i++){
			buttonArray[i].setIcon(whiteIcon);
		}
		buttonArray[63].setIcon(blackIcon);
		*/
		

		
		//�p�X�{�^���̐ݒ�
		PassButton = new JButton(pass);//�{�^���ɃA�C�R����ݒ肷��
		c.add(PassButton);//�y�C���ɓ\��t����
		PassButton.setBounds(500,450,150,150);
		PassButton.addMouseListener(this);

		//���X�^�[�g�{�^���̐ݒ�
		restartButton = new JButton(restart);//�{�^���ɃA�C�R����ݒ肷��
		c.add(restartButton);//�y�C���ɓ\��t����
		restartButton.setBounds(500,650,300,150);
		restartButton.addMouseListener(this);
		  //���s�����܂�܂ł͔�\���A������
		restartButton.setVisible(false);
		restartButton.setEnabled(false);

		//�C�O�W�b�g�{�^���̐ݒ�
		exitButton = new JButton(exit);//�{�^���ɃA�C�R����ݒ肷��
		c.add(exitButton);//�y�C���ɓ\��t����
		exitButton.setBounds(800,650,300,150);
		exitButton.addMouseListener(this);
		  //���s�����܂�܂ł͔�\���A������
		exitButton.setVisible(false);
		exitButton.setEnabled(false);

		//���[�U�[�A�C�R���̐ݒ�
		IconArray = new JButton[1];
		IconArray[0] = new JButton(whiteUserIcon);//�{�^���ɃA�C�R����ݒ肷��
		c.add(IconArray[0]);//�y�C���ɓ\��t����
		IconArray[0].setBounds(500,10,400,400);
		IconArray[0].addMouseListener(this);

		//�틵�{�^���̐ݒ�
		progressButton = new JButton(progress);//�{�^���ɃA�C�R����ݒ肷��
		c.add(progressButton);//�y�C���ɓ\��t����
		progressButton.setBounds(700,450,150,150);
		progressButton.addMouseListener(this);

		
		
		//�^�[�������O�ɏ�����
		turnCount = 1;
		

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowClosing());
        setVisible(true);

		//�T�[�o�ɐڑ�����
		Socket socket = null;
		try {
			//"localhost"�́C���������ւ̐ڑ��Dlocalhost��ڑ����IP Address�i"133.42.155.201"�`���j�ɐݒ肷��Ƒ���PC�̃T�[�o�ƒʐM�ł���
			//10000�̓|�[�g�ԍ��DIP Address�Őڑ�����PC�����߂āC�|�[�g�ԍ��ł���PC�㓮�삷��v���O��������肷��
			socket = new Socket(IP, 10000);
		} catch (UnknownHostException e) {
			System.err.println("�z�X�g�� IP �A�h���X������ł��܂���: " + e);
		} catch (IOException e) {
			 System.err.println("�G���[���������܂���: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//��M�p�̃X���b�h���쐬����
		mrt.start();//�X���b�h�𓮂����iRun�������j
	}
		
	//���b�Z�[�W��M�̂��߂̃X���b�h
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//�ʐM�󋵂��Ď����C��M�f�[�^�ɂ���ē��삷��
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//�ڑ��̍ŏ��ɖ��O�𑗂�
				String myNumberStr = br.readLine();
				int myNumberInt =  Integer.parseInt(myNumberStr);
				if(myNumberInt % 2 == 0){
					myColor = 0;
				}else{
					myColor = 1;
				}
				if(myColor == 0){
					myIcon = blackIcon;
					yourIcon = whiteIcon;
					myTurn = 0;
					System.out.println("black");
					IconArray[0].setIcon(blackUserIcon);
				}else if(myColor  == 1){
					yourIcon = blackIcon;
					myIcon = whiteIcon;
					myTurn = 1;
					System.out.println("white");
					IconArray[0].setIcon(whiteUserIcon);
				}
				
				if(myTurn == 1){
					IconArray[0].setIcon(waitIcon);
				}
				canPutCheck();
				
				////������mycolor ,yourcolor ��ݒ肷��Ɗy/////////////
				

				while(true) {
					String inputLine = br.readLine();//�f�[�^����s�������ǂݍ���ł݂�
					if (inputLine != null) {//�ǂݍ��񂾂Ƃ��Ƀf�[�^���ǂݍ��܂ꂽ���ǂ������`�F�b�N����
						System.out.println(inputLine);//�f�o�b�O�i����m�F�p�j�ɃR���\�[���ɏo�͂���
						String[] inputTokens = inputLine.split(" ");	//���̓f�[�^����͂��邽�߂ɁA�X�y�[�X�Ő؂蕪����
						String cmd = inputTokens[0];//�R�}���h�̎��o���D�P�ڂ̗v�f�����o��
						if(cmd.equals("MOVE")){//cmd�̕�����"MOVE"�����������ׂ�D��������true�ƂȂ�
							//MOVE�̎��̏���(�R�}�̈ړ��̏���)
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
							int x = Integer.parseInt(inputTokens[2]);//���l�ɕϊ�����
							int y = Integer.parseInt(inputTokens[3]);//���l�ɕϊ�����
							buttonArray[theBnum].setLocation(x,y);//�w��̃{�^�����ʒu��x,y�ɐݒ肷��
						}
						if(cmd.equals("PLACE")){//cmd�̕�����"PLACE"�����������ׂ�D��������true�ƂȂ�
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
							int theColor = Integer.parseInt(inputTokens[2]);//���l�ɕϊ�����
							if(theColor == 0){
								buttonArray[theBnum].setIcon(blackIcon);
							}else if(theColor == 1){
								buttonArray[theBnum].setIcon(whiteIcon);
							}
							if((!(boardExist())) || (!(onlyExit()))){
								gameJudge();
							}
							canPutCheck();
							
						}
						if(cmd.equals("FLIP")){//cmd�̕�����"PLACE"�����������ׂ�D��������true�ƂȂ�
							String theBName = inputTokens[1];//�{�^���̖��O�i�ԍ��j�̎擾
							int theBnum = Integer.parseInt(theBName);//�{�^���̖��O�𐔒l�ɕϊ�����
							int theColor = Integer.parseInt(inputTokens[2]);//���l�ɕϊ�����
							if(theColor == 0){
								buttonArray[theBnum].setIcon(blackIcon);
							}else if(theColor == 1){
								buttonArray[theBnum].setIcon(whiteIcon);
							}
							if(theColor== myColor){
								myTurn = 1;
							} else{
								myTurn = 0;
							}
							if(myTurn == 1){
								IconArray[0].setIcon(waitIcon);
							}else{
								if(myIcon.equals(whiteIcon)){
									IconArray[0].setIcon(whiteUserIcon);
								}else if(myIcon.equals(blackIcon)){
									IconArray[0].setIcon(blackUserIcon);
								}
							}
							//�^�[�����𑝂₷
							turnCount++;
							//�܂��u���ꏊ�����邩���R�}��u�����тɃ`�F�b�N����
							
							
						}
						if(cmd.equals("PASS")){
							int theColor = Integer.parseInt(inputTokens[1]);
							myTurn = 1 - myTurn;
							passCountList[turnCount] = 1;  //�^�[��������^�[�����̃��X�g�̂Ƃ���ɂP������
							if(myTurn == 0){
								JOptionPane.showMessageDialog(null,"���肪�p�X�����܂���" ,"���b�Z�[�W",JOptionPane.INFORMATION_MESSAGE);
							}
							/*
							if(theColor == myColor){
								myTurn = 1;
							}else {
								System.out.println("�p�X�𑊎肩��󂯎��܂���");
								passCount++;
								System.out.println("�p�X��:" + passCount);
								//�^�[����؂�ւ��鏈��
								myTurn = 0;
							}
							*/


							if(myTurn == 1){
								IconArray[0].setIcon(waitIcon);
							}else{
								if(myIcon.equals(whiteIcon)){
									IconArray[0].setIcon(whiteUserIcon);
								}else if(myIcon.equals(blackIcon)){
									IconArray[0].setIcon(blackUserIcon);
								}
							}
							
							
							if(passCountList[turnCount - 1] == 1){
								gameJudge();
							}

							//�^�[�����𑝂₷
							turnCount++;
						}
						if(cmd.equals("FINISH")){
							String theBName = inputTokens[1];
							int theColor = Integer.parseInt(inputTokens[2]);
							if(theBName.equals("RESTART")){//cmd�̕�����"PLACE"�����������ׂ�D��������true�ƂȂ�
								restartCount++;
								if(restartCount == 2){
									JOptionPane.showMessageDialog(null,"���肪�Q�[���ĊJ���������܂����I","�Q�[���ĊJ",JOptionPane.INFORMATION_MESSAGE);
									gameRestart();
									restartCount =0;
									resultButton.setVisible(false);
									resultButton.setEnabled(false);
								}	
							} else if(theBName.equals("EXIT")){
								if(theColor == myColor){ //�������u
									JOptionPane.showMessageDialog(null,"�����l�ł����B","�Q�[���I��",JOptionPane.INFORMATION_MESSAGE);
									System.exit(0);
								} else {
									JOptionPane.showMessageDialog(null,"���肪�I����I�����܂����B�����l�ł����B","�Q�[���I��",JOptionPane.INFORMATION_MESSAGE);
									System.exit(0);
								}
							}
						}
						if(cmd.equals("ESCPAE")){ //���肪�����I�������ꍇ�̏���
							JOptionPane.showMessageDialog(null,"���肪�����I�������̂ŁA���Ȃ��̏����ł�","���",JOptionPane.INFORMATION_MESSAGE);
							System.out.println("���Ȃ��̏����ł�");
							resultButton = new JButton(win);
							c.add(resultButton);//�y�C���ɓ\��t����
							resultButton.setBounds(70,500,275,300);
							resultButton.setVisible(true);
							resultButton.setEnabled(true);
							exitButton.setVisible(true);
							exitButton.setEnabled(true);
						}



					}else{
						break;
					}
				
				}
				socket.close();
			} catch (IOException e) {
				System.err.println("�G���[���������܂���: " + e);
			}
		}
	}

	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}
  	
	public void mouseClicked(MouseEvent e) {//�{�^�����N���b�N�����Ƃ��̏���
		System.out.println("�N���b�N");
		JButton theButton = (JButton)e.getComponent();//�N���b�N�����I�u�W�F�N�g�𓾂�D�^���Ⴄ�̂ŃL���X�g����
		Icon theIcon = theButton.getIcon();//theIcon�ɂ́C���݂̃{�^���ɐݒ肳�ꂽ�A�C�R��������
		if(theButton.equals(progressButton)){//userIcon���N���b�N����ƌ��݂̃R�}����\��������
				int[] Countlist = pieceCount();
				int myCount = Countlist[0];
				int yourCount = Countlist[1];
				String message = " ";
				if(myCount > yourCount){
					message = "�����Ă��܂�";
				}else if(myCount < yourCount){
					message = "�����Ă��܂�";
				}else if(myCount == yourCount){
					message = "���������Ă��܂�";
				}
				JOptionPane.showMessageDialog(null,"����" + message +"(���Ȃ��̃R�}�̐�:" + myCount + " ����̃R�}�̐�:" + yourCount +")","���݂̃R�}��",JOptionPane.INFORMATION_MESSAGE);
		}
		System.out.println(theIcon);
		if(myTurn == 0){
			

			//�p�X�������𑗐M		
			if(theIcon.equals(pass)){
				System.out.println("�p�X���܂���");
				//JOptionPane.showMessageDialog(null,"�p�X���܂����B","���b�Z�[�W",JOptionPane.WARNING_MESSAGE);
				
				//���M�����쐬����i��M���ɂ́C���̑��������ԂɃf�[�^�����o���D�X�y�[�X���f�[�^�̋�؂�ƂȂ�j
				String msg = "PASS" + " " + myColor;
				//�T�[�o�ɏ��𑗂�
				out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
				out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����

			}
			
			String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
			Point theBtnLocation = theButton.getLocation();//�N���b�N�����{�^�������W���擾����

			System.out.println(theIcon);//�f�o�b�O�i�m�F�p�j�ɁC�N���b�N�����A�C�R���̖��O���o�͂���
			System.out.println(theArrayIndex);
			if(theIcon.equals(boardIcon) || theIcon.equals(boardRedIcon) ){
				int index =  Integer.parseInt(theArrayIndex);
				int x = (index % 8) ;
				int y = (index / 8) ;
				System.out.println("��(" + x + "," + y + ")");

				if(judgeButton(y,x)){
					theButton.setIcon(myIcon);
					//���M�����쐬����i��M���ɂ́C���̑��������ԂɃf�[�^�����o���D�X�y�[�X���f�[�^�̋�؂�ƂȂ�j
					String msg = "PLACE"+" "+theArrayIndex+" "+myColor;
					//�T�[�o�ɏ��𑗂�
					out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
					out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����

				}else{
					System.out.println("�����ɂ͔z�u�ł��܂���");
				}
					repaint();//�I�u�W�F�N�g�̍ĕ`����s��
				
				/**if(theIcon == whiteIcon){//�A�C�R����whiteIcon�Ɠ����Ȃ�
					theButton.setIcon(blackIcon);//blackIcon�ɐݒ肷��
				}else{
					theButton.setIcon(whiteIcon);//whiteIcon�ɐݒ肷��
				}
				*/	
			} 
		}else if((theIcon.equals(whiteIcon)) ||(theIcon.equals(blackIcon)) || (theIcon.equals(boardIcon)) || (theIcon.equals(pass)) || (theIcon.equals(boardRedIcon))){
				System.out.println("�N�̃^�[������Ȃ���[�[�[�[");
				JOptionPane.showMessageDialog(null,"�N�̃^�[������Ȃ���[�[�[�[","�x��",JOptionPane.WARNING_MESSAGE);
		}

		JButton theRestartButton = (JButton)e.getComponent();
		Icon theRestartIcon = theRestartButton.getIcon();

		if(theRestartIcon.equals(restart)){
			String msg = "FINISH" +" "+ "RESTART" + " " + myColor;
			//�T�[�o�ɏ��𑗂�
			out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
			out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����

			restartButton.setVisible(false);
			restartButton.setEnabled(false);
			exitButton.setVisible(false);
			exitButton.setEnabled(false);
		} else if(theRestartIcon.equals(exit)){
			String msg = "FINISH" +" " +"EXIT" + " " + myColor;
			//�T�[�o�ɏ��𑗂�
			out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
			out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
			restartButton.setVisible(false);
			restartButton.setEnabled(false);
			exitButton.setVisible(false);
			exitButton.setEnabled(false);
		}

		
		repaint();//��ʂ̃I�u�W�F�N�g��`�悵����


	}
	
	public void mouseEntered(MouseEvent e) {//�}�E�X���I�u�W�F�N�g�ɓ������Ƃ��̏���
		//System.out.println("�}�E�X��������");
	}
	
	public void mouseExited(MouseEvent e) {//�}�E�X���I�u�W�F�N�g����o���Ƃ��̏���
		//System.out.println("�}�E�X�E�o");
	}
	
	public void mousePressed(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g���������Ƃ��̏����i�N���b�N�Ƃ̈Ⴂ�ɒ��Ӂj
		//System.out.println("�}�E�X��������");
	}
	
	public void mouseReleased(MouseEvent e) {//�}�E�X�ŉ����Ă����I�u�W�F�N�g�𗣂����Ƃ��̏���
		//System.out.println("�}�E�X�������");
	}
	
	public void mouseDragged(MouseEvent e) {//�}�E�X�ŃI�u�W�F�N�g�Ƃ��h���b�O���Ă���Ƃ��̏���
		//System.out.println("�}�E�X���h���b�O");
		/** 
		JButton theButton = (JButton)e.getComponent();//�^���Ⴄ�̂ŃL���X�g����
		String theArrayIndex = theButton.getActionCommand();//�{�^���̔z��̔ԍ������o��
		if(!(theArrayIndex.equals("1"))){
			Point theMLoc = e.getPoint();//�������R���|�[�l���g����Ƃ��鑊�΍��W
			System.out.println(theMLoc);//�f�o�b�O�i�m�F�p�j�ɁC�擾�����}�E�X�̈ʒu���R���\�[���ɏo�͂���
			Point theBtnLocation = theButton.getLocation();//�N���b�N�����{�^�������W���擾����
			theBtnLocation.x += theMLoc.x-15;//�{�^���̐^�񒆓�����Ƀ}�E�X�J�[�\��������悤�ɕ␳����
			theBtnLocation.y += theMLoc.y-15;//�{�^���̐^�񒆓�����Ƀ}�E�X�J�[�\��������悤�ɕ␳����
			theButton.setLocation(theBtnLocation);//�}�E�X�̈ʒu�ɂ��킹�ăI�u�W�F�N�g���ړ�����

	
			//���M�����쐬����i��M���ɂ́C���̑��������ԂɃf�[�^�����o���D�X�y�[�X���f�[�^�̋�؂�ƂȂ�j
			String msg = "MOVE"+" "+theArrayIndex+" "+theBtnLocation.x+" "+theBtnLocation.y;

			//�T�[�o�ɏ��𑗂�
			out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
			out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����

			repaint();//�I�u�W�F�N�g�̍ĕ`����s��
		}
		**/
	}

	public void mouseMoved(MouseEvent e) {//�}�E�X���I�u�W�F�N�g��ňړ������Ƃ��̏���
		//System.out.println("�}�E�X�ړ�");
		int theMLocX = e.getX();//�}�E�X��x���W�𓾂�
		int theMLocY = e.getY();//�}�E�X��y���W�𓾂�
		//System.out.println(theMLocX+","+theMLocY);//�R���\�[���ɏo�͂���
	}

	public boolean judgeButton(int y,int x){
		boolean flag =false;
		for(int j = -1; j < 2; j++){
			for (int i = -1; i< 2; i++){
				//int index = (y+j) *  8 + (x+i);
				//System.out.println("(" + (y+j) + "," + (x+i) + ")");
				//System.out.println(buttonArray[index].getIcon());
				if(!(i == 0 && j == 0)){
					//System.out.println(yourIcon);
					if(flipButtons(y,x,j,i) > 0){

						int flipResult =flipButtons(y,x,j,i);
						flag = true;
						for(int dy=j, dx=i, k=0; k<flipResult; k++, dy+=j, dx+=i){
							//�{�^���̈ʒu�������
							int msgy = y + dy;
							int msgx = x + dx;
							int theArrayIndex = msgy*8 + msgx;
							
							//�T�[�o�ɏ��𑗂�
							System.out.println("�t���b�v���F" + flipResult);
							String msg = "FLIP"+" "+theArrayIndex+" "+myColor;
							out.println(msg);
							out.flush();
						}
						//break;
					}
				}
				
				/*
				if(buttonArray[index].getIcon().equals(yourIcon)){
					flag = true;
					break;
				}
				*/
				//System.out.println("theIcon="+theIcon+", yourIcon="+yourIcon);
			}
		}


		return flag;
	}
	
	public int flipButtons(int y ,int x, int j, int i){
		int flipNum = 0;
		for(int dy=j, dx=i; ; dy+=j, dx+=i) {
			int index = (y + dy) *  8 + (x + dx);
			if((y + dy) < 8 && (y + dy) > -1 &&  (x + dx) < 8 && (x + dx) > -1){
				/*if((y + dy) == 0 || (x + dx) == 0){
					flipNum = 0;
					return flipNum;
				
				}else*/ if(buttonArray[index].getIcon().equals(boardIcon) || buttonArray[index].getIcon().equals(boardRedIcon)){
					flipNum = 0;
					return flipNum;
				}
				else if(buttonArray[index].getIcon().equals(myIcon)){
					return flipNum;
				}else if(buttonArray[index].getIcon().equals(yourIcon)){
					flipNum++;
				}else{
					System.out.println("ELSE");
				}
			}else{
				flipNum = 0;
				return flipNum;
			}
		}
	}

	public int[] pieceCount(){
		int myCount = 0;
		int yourCount = 0;
		int boardCount = 0;
		int[] Countlist =new int[3];
		for(int i=0;i<64;i++){
			Icon countIcon = buttonArray[i].getIcon();	
			if(countIcon.equals(myIcon)){
				myCount++;
			} else if(countIcon.equals(yourIcon)){
				yourCount++;
			} else if(countIcon.equals(boardIcon) || countIcon.equals(boardRedIcon) ){
				boardCount++;
			} else{
				System.out.println("�J�E���g�G���[");
			}
		}
		
		Countlist[0] = myCount;
		Countlist[1] = yourCount;
		Countlist[2] = boardCount;

		System.out.println("�����̃R�}���F" + Countlist[0] +"����̃R�}���F" + Countlist[1] + "�u���ĂȂ��R�}���F" + Countlist[2]);
		
		return Countlist;
	}

	public boolean boardExist(){
		boolean flag = false;
		for(int i=0;i<64;i++){
			Icon countIcon = buttonArray[i].getIcon();	
			if(countIcon.equals(boardIcon) || countIcon.equals(boardRedIcon)){
				flag = true;
				return flag;
			}
		}
		return flag;
	}

	public boolean onlyExit(){
		boolean flag = true;
		int black = 0;
		int white = 0;
		for(int i=0;i<64;i++){
			Icon countIcon = buttonArray[i].getIcon();	
			if(countIcon.equals(blackIcon)){
				black++;
			} else if(countIcon.equals(whiteIcon)){
				white++;
			}
		}

		if(black == 0 || white == 0 ){
			flag = false;
		}
		return flag;
	}

	public void gameJudge(){
		JOptionPane.showMessageDialog(null,"�������I�����܂���","���b�Z�[�W",JOptionPane.INFORMATION_MESSAGE);
		int[] Countlist = pieceCount();
		int myCount = Countlist[0];
		int yourCount = Countlist[1];
		
		if(myCount > yourCount){
			System.out.println("���Ȃ��̏����ł�");
			resultButton = new JButton(win);
			c.add(resultButton);//�y�C���ɓ\��t����
			resultButton.setBounds(70,500,275,300);

		} else if(myCount < yourCount){
			System.out.println("���Ȃ��̕����ł�");
			resultButton = new JButton(lose);
			c.add(resultButton);//�y�C���ɓ\��t����
			resultButton.setBounds(70,500,275,300);

		} else if (myCount == yourCount){
			System.out.println("���������ł�");
			resultButton = new JButton(draw);
			c.add(resultButton);//�y�C���ɓ\��t����
			resultButton.setBounds(70,500,275,300);
		}
		
		  //���s�����܂�΁A�\���A�L����
		restartButton.setVisible(true);
		restartButton.setEnabled(true);
		exitButton.setVisible(true);
		exitButton.setEnabled(true);
		
	}

	public void gameRestart(){
		
		for(int i=0;i<64;i++){
			buttonArray[i].setIcon(boardIcon);
		}
		buttonArray[27].setIcon(whiteIcon);
		buttonArray[28].setIcon(blackIcon);
		buttonArray[35].setIcon(blackIcon);
		buttonArray[36].setIcon(whiteIcon);
		
		//�^�[�������O�ɏ�����
		turnCount = 0;

		//�e�X�g
		/*
		for(int i = 0;i< 56; i++){
			buttonArray[i].setIcon(whiteIcon);
		}
		for(int i = 57 ; i < 63; i++){
			buttonArray[i].setIcon(blackIcon);
		}
		buttonArray[63].setIcon(whiteIcon);
		buttonArray[60].setIcon(boardIcon);
		*/
	}
	
	//�ՖʂɃR�}��u����ꏊ�����邩�𒲂ׂ�֐�
	
	public boolean canPutCheck(){
		boolean flag = false;
		boolean flag02 = false;
		for(int index = 0 ; index < 64; index++){
			flag02 = false;
			if(buttonArray[index].getIcon().equals(boardIcon) || buttonArray[index].getIcon().equals(boardRedIcon)){
				int x = (index % 8) ;
				int y = (index / 8) ;
				for(int j = -1; j < 2; j++){
					for (int i = -1; i< 2; i++){
						if(!(i == 0 && j == 0)){
							if(flipButtons(y,x,j,i) > 0){
								//�u����ꏊ��1�ł�����΂s�q�t�d�ŕԂ�
								System.out.println("�u����ꏊ������܂���");
								buttonArray[index].setIcon(boardRedIcon);
								flag = true;
								flag02 = true;
								break;
							}
						}
					}
				}
				if(flag02 == true){
				buttonArray[index].setIcon(boardRedIcon);
				}else{
				buttonArray[index].setIcon(boardIcon);
				}
			}
			
			
		}
		//�S���̃}�X�ɒu���ȂȂ�������e�`�k�r�d��Ԃ�
		System.out.println("�u����ꏊ������܂���");
		if(!(flag)){
			String msg = "PASS" + " " + myColor;
			//�T�[�o�ɏ��𑗂�
			out.println(msg);//���M�f�[�^���o�b�t�@�ɏ����o��
			out.flush();//���M�f�[�^���t���b�V���i�l�b�g���[�N��ɂ͂��o���j����
		}
		return flag;
	}
	

	//����{�^�����������Ƃ��̏����̒�`
	class WindowClosing extends WindowAdapter{
         public void windowClosing(WindowEvent e) {
             int ans = JOptionPane.showConfirmDialog(MyClient.this, "���̂܂܏I������Ƃ��Ȃ��̕����ɂȂ��Ă��܂��܂�����낵���ł����i�I������Ƃ���EXIT�{�^���������Ă��������j");
             System.out.println(ans);
             if(ans == JOptionPane.YES_OPTION) {
                System.out.println("�v���O�����ɂ��I�������̎��s");
				String msg = "ESCPAE";
				out.println(msg);
				out.flush();
                System.exit(0);
             }
         }
     }

	
	
}
