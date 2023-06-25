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
	private JButton buttonArray[];//ボタン用の配列
	private JButton IconArray[];//ボタン用の配列
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
	PrintWriter out;//出力用のライター

	public MyClient() {
		//名前の入力ダイアログを開く
		String myName = JOptionPane.showInputDialog(null,"名前を入力してください","名前の入力",JOptionPane.QUESTION_MESSAGE);
		if(myName.equals("")){
			myName = "No name";//名前がないときは，"No name"とする
		}
		String IP = JOptionPane.showInputDialog(null,"IPアドレスを入力してください","IPアドレスの入力",JOptionPane.QUESTION_MESSAGE);
		if(IP.equals("")){
			myName = "localhost";
		}

		//ウィンドウを作成する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに，正しく閉じるように設定する
		setTitle("MyClient");//ウィンドウのタイトルを設定する
		setSize(1200,900);//ウィンドウのサイズを設定する
		c = getContentPane();//フレームのペインを取得する

		//アイコンの設定
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
		


		c.setLayout(null);//自動レイアウトの設定を行わない
		//ボタンの生成
		buttonArray = new JButton[64];//ボタンの配列を５個作成する[0]から[4]まで使える
		for(int i=0;i<64;i++){
			buttonArray[i] = new JButton(boardIcon);//ボタンにアイコンを設定する
			c.add(buttonArray[i]);//ペインに貼り付ける
			int x = (i % 8) * 50;
			int y = (i / 8) * 50;
			buttonArray[i].setBounds(x + 10, y + 10,50,50);//ボタンの大きさと位置を設定する．(x座標，y座標,xの幅,yの幅）
			buttonArray[i].addMouseListener(this);//ボタンをマウスでさわったときに反応するようにする
			buttonArray[i].addMouseMotionListener(this);//ボタンをマウスで動かそうとしたときに反応するようにする
			buttonArray[i].setActionCommand(Integer.toString(i));//ボタンに配列の情報を付加する（ネットワークを介してオブジェクトを識別するため）
		}
		//初期盤面の設定
		
		buttonArray[27].setIcon(whiteIcon);
		buttonArray[28].setIcon(blackIcon);
		buttonArray[35].setIcon(blackIcon);
		buttonArray[36].setIcon(whiteIcon);
		
		

		//テスト

		/*
		for(int i = 0;i< 56; i++){
			buttonArray[i].setIcon(blackIcon);
		}
		for(int i = 57 ; i < 63; i++){
			buttonArray[i].setIcon(whiteIcon);
		}
		buttonArray[63].setIcon(blackIcon);
		*/
		

		
		//パスボタンの設定
		PassButton = new JButton(pass);//ボタンにアイコンを設定する
		c.add(PassButton);//ペインに貼り付ける
		PassButton.setBounds(500,450,150,150);
		PassButton.addMouseListener(this);

		//リスタートボタンの設定
		restartButton = new JButton(restart);//ボタンにアイコンを設定する
		c.add(restartButton);//ペインに貼り付ける
		restartButton.setBounds(500,650,300,150);
		restartButton.addMouseListener(this);
		  //勝敗が決まるまでは非表示、無効化
		restartButton.setVisible(false);
		restartButton.setEnabled(false);

		//イグジットボタンの設定
		exitButton = new JButton(exit);//ボタンにアイコンを設定する
		c.add(exitButton);//ペインに貼り付ける
		exitButton.setBounds(800,650,300,150);
		exitButton.addMouseListener(this);
		  //勝敗が決まるまでは非表示、無効化
		exitButton.setVisible(false);
		exitButton.setEnabled(false);

		//ユーザーアイコンの設定
		IconArray = new JButton[1];
		IconArray[0] = new JButton(whiteUserIcon);//ボタンにアイコンを設定する
		c.add(IconArray[0]);//ペインに貼り付ける
		IconArray[0].setBounds(500,10,400,400);
		IconArray[0].addMouseListener(this);

		//戦況ボタンの設定
		progressButton = new JButton(progress);//ボタンにアイコンを設定する
		c.add(progressButton);//ペインに貼り付ける
		progressButton.setBounds(700,450,150,150);
		progressButton.addMouseListener(this);

		
		
		//ターン数を０に初期化
		turnCount = 1;
		

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowClosing());
        setVisible(true);

		//サーバに接続する
		Socket socket = null;
		try {
			//"localhost"は，自分内部への接続．localhostを接続先のIP Address（"133.42.155.201"形式）に設定すると他のPCのサーバと通信できる
			//10000はポート番号．IP Addressで接続するPCを決めて，ポート番号でそのPC上動作するプログラムを特定する
			socket = new Socket(IP, 10000);
		} catch (UnknownHostException e) {
			System.err.println("ホストの IP アドレスが判定できません: " + e);
		} catch (IOException e) {
			 System.err.println("エラーが発生しました: " + e);
		}
		
		MesgRecvThread mrt = new MesgRecvThread(socket, myName);//受信用のスレッドを作成する
		mrt.start();//スレッドを動かす（Runが動く）
	}
		
	//メッセージ受信のためのスレッド
	public class MesgRecvThread extends Thread {
		
		Socket socket;
		String myName;
		
		public MesgRecvThread(Socket s, String n){
			socket = s;
			myName = n;
		}
		
		//通信状況を監視し，受信データによって動作する
		public void run() {
			try{
				InputStreamReader sisr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(sisr);
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(myName);//接続の最初に名前を送る
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
				
				////ここでmycolor ,yourcolor を設定すると楽/////////////
				

				while(true) {
					String inputLine = br.readLine();//データを一行分だけ読み込んでみる
					if (inputLine != null) {//読み込んだときにデータが読み込まれたかどうかをチェックする
						System.out.println(inputLine);//デバッグ（動作確認用）にコンソールに出力する
						String[] inputTokens = inputLine.split(" ");	//入力データを解析するために、スペースで切り分ける
						String cmd = inputTokens[0];//コマンドの取り出し．１つ目の要素を取り出す
						if(cmd.equals("MOVE")){//cmdの文字と"MOVE"が同じか調べる．同じ時にtrueとなる
							//MOVEの時の処理(コマの移動の処理)
							String theBName = inputTokens[1];//ボタンの名前（番号）の取得
							int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
							int x = Integer.parseInt(inputTokens[2]);//数値に変換する
							int y = Integer.parseInt(inputTokens[3]);//数値に変換する
							buttonArray[theBnum].setLocation(x,y);//指定のボタンを位置をx,yに設定する
						}
						if(cmd.equals("PLACE")){//cmdの文字と"PLACE"が同じか調べる．同じ時にtrueとなる
							String theBName = inputTokens[1];//ボタンの名前（番号）の取得
							int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
							int theColor = Integer.parseInt(inputTokens[2]);//数値に変換する
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
						if(cmd.equals("FLIP")){//cmdの文字と"PLACE"が同じか調べる．同じ時にtrueとなる
							String theBName = inputTokens[1];//ボタンの名前（番号）の取得
							int theBnum = Integer.parseInt(theBName);//ボタンの名前を数値に変換する
							int theColor = Integer.parseInt(inputTokens[2]);//数値に変換する
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
							//ターン数を増やす
							turnCount++;
							//まだ置く場所があるかをコマを置くたびにチェックする
							
							
						}
						if(cmd.equals("PASS")){
							int theColor = Integer.parseInt(inputTokens[1]);
							myTurn = 1 - myTurn;
							passCountList[turnCount] = 1;  //ターンしたらターン数のリストのところに１を入れる
							if(myTurn == 0){
								JOptionPane.showMessageDialog(null,"相手がパスをしました" ,"メッセージ",JOptionPane.INFORMATION_MESSAGE);
							}
							/*
							if(theColor == myColor){
								myTurn = 1;
							}else {
								System.out.println("パスを相手から受け取りました");
								passCount++;
								System.out.println("パス回数:" + passCount);
								//ターンを切り替える処理
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

							//ターン数を増やす
							turnCount++;
						}
						if(cmd.equals("FINISH")){
							String theBName = inputTokens[1];
							int theColor = Integer.parseInt(inputTokens[2]);
							if(theBName.equals("RESTART")){//cmdの文字と"PLACE"が同じか調べる．同じ時にtrueとなる
								restartCount++;
								if(restartCount == 2){
									JOptionPane.showMessageDialog(null,"相手がゲーム再開を承諾しました！","ゲーム再開",JOptionPane.INFORMATION_MESSAGE);
									gameRestart();
									restartCount =0;
									resultButton.setVisible(false);
									resultButton.setEnabled(false);
								}	
							} else if(theBName.equals("EXIT")){
								if(theColor == myColor){ //自分が「
									JOptionPane.showMessageDialog(null,"お疲れ様でした。","ゲーム終了",JOptionPane.INFORMATION_MESSAGE);
									System.exit(0);
								} else {
									JOptionPane.showMessageDialog(null,"相手が終了を選択しました。お疲れ様でした。","ゲーム終了",JOptionPane.INFORMATION_MESSAGE);
									System.exit(0);
								}
							}
						}
						if(cmd.equals("ESCPAE")){ //相手が強制終了した場合の処理
							JOptionPane.showMessageDialog(null,"相手が強制終了したので、あなたの勝ちです","情報",JOptionPane.INFORMATION_MESSAGE);
							System.out.println("あなたの勝ちです");
							resultButton = new JButton(win);
							c.add(resultButton);//ペインに貼り付ける
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
				System.err.println("エラーが発生しました: " + e);
			}
		}
	}

	public static void main(String[] args) {
		MyClient net = new MyClient();
		net.setVisible(true);
	}
  	
	public void mouseClicked(MouseEvent e) {//ボタンをクリックしたときの処理
		System.out.println("クリック");
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
		Icon theIcon = theButton.getIcon();//theIconには，現在のボタンに設定されたアイコンが入る
		if(theButton.equals(progressButton)){//userIconをクリックすると現在のコマ数を表示させる
				int[] Countlist = pieceCount();
				int myCount = Countlist[0];
				int yourCount = Countlist[1];
				String message = " ";
				if(myCount > yourCount){
					message = "勝っています";
				}else if(myCount < yourCount){
					message = "負けています";
				}else if(myCount == yourCount){
					message = "引き分けています";
				}
				JOptionPane.showMessageDialog(null,"現在" + message +"(あなたのコマの数:" + myCount + " 相手のコマの数:" + yourCount +")","現在のコマ数",JOptionPane.INFORMATION_MESSAGE);
		}
		System.out.println(theIcon);
		if(myTurn == 0){
			

			//パスした情報を送信		
			if(theIcon.equals(pass)){
				System.out.println("パスしました");
				//JOptionPane.showMessageDialog(null,"パスしました。","メッセージ",JOptionPane.WARNING_MESSAGE);
				
				//送信情報を作成する（受信時には，この送った順番にデータを取り出す．スペースがデータの区切りとなる）
				String msg = "PASS" + " " + myColor;
				//サーバに情報を送る
				out.println(msg);//送信データをバッファに書き出す
				out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する

			}
			
			String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
			Point theBtnLocation = theButton.getLocation();//クリックしたボタンを座標を取得する

			System.out.println(theIcon);//デバッグ（確認用）に，クリックしたアイコンの名前を出力する
			System.out.println(theArrayIndex);
			if(theIcon.equals(boardIcon) || theIcon.equals(boardRedIcon) ){
				int index =  Integer.parseInt(theArrayIndex);
				int x = (index % 8) ;
				int y = (index / 8) ;
				System.out.println("★(" + x + "," + y + ")");

				if(judgeButton(y,x)){
					theButton.setIcon(myIcon);
					//送信情報を作成する（受信時には，この送った順番にデータを取り出す．スペースがデータの区切りとなる）
					String msg = "PLACE"+" "+theArrayIndex+" "+myColor;
					//サーバに情報を送る
					out.println(msg);//送信データをバッファに書き出す
					out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する

				}else{
					System.out.println("そこには配置できません");
				}
					repaint();//オブジェクトの再描画を行う
				
				/**if(theIcon == whiteIcon){//アイコンがwhiteIconと同じなら
					theButton.setIcon(blackIcon);//blackIconに設定する
				}else{
					theButton.setIcon(whiteIcon);//whiteIconに設定する
				}
				*/	
			} 
		}else if((theIcon.equals(whiteIcon)) ||(theIcon.equals(blackIcon)) || (theIcon.equals(boardIcon)) || (theIcon.equals(pass)) || (theIcon.equals(boardRedIcon))){
				System.out.println("君のターンじゃないよーーーー");
				JOptionPane.showMessageDialog(null,"君のターンじゃないよーーーー","警告",JOptionPane.WARNING_MESSAGE);
		}

		JButton theRestartButton = (JButton)e.getComponent();
		Icon theRestartIcon = theRestartButton.getIcon();

		if(theRestartIcon.equals(restart)){
			String msg = "FINISH" +" "+ "RESTART" + " " + myColor;
			//サーバに情報を送る
			out.println(msg);//送信データをバッファに書き出す
			out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する

			restartButton.setVisible(false);
			restartButton.setEnabled(false);
			exitButton.setVisible(false);
			exitButton.setEnabled(false);
		} else if(theRestartIcon.equals(exit)){
			String msg = "FINISH" +" " +"EXIT" + " " + myColor;
			//サーバに情報を送る
			out.println(msg);//送信データをバッファに書き出す
			out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
			restartButton.setVisible(false);
			restartButton.setEnabled(false);
			exitButton.setVisible(false);
			exitButton.setEnabled(false);
		}

		
		repaint();//画面のオブジェクトを描画し直す


	}
	
	public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったときの処理
		//System.out.println("マウスが入った");
	}
	
	public void mouseExited(MouseEvent e) {//マウスがオブジェクトから出たときの処理
		//System.out.println("マウス脱出");
	}
	
	public void mousePressed(MouseEvent e) {//マウスでオブジェクトを押したときの処理（クリックとの違いに注意）
		//System.out.println("マウスを押した");
	}
	
	public void mouseReleased(MouseEvent e) {//マウスで押していたオブジェクトを離したときの処理
		//System.out.println("マウスを放した");
	}
	
	public void mouseDragged(MouseEvent e) {//マウスでオブジェクトとをドラッグしているときの処理
		//System.out.println("マウスをドラッグ");
		/** 
		JButton theButton = (JButton)e.getComponent();//型が違うのでキャストする
		String theArrayIndex = theButton.getActionCommand();//ボタンの配列の番号を取り出す
		if(!(theArrayIndex.equals("1"))){
			Point theMLoc = e.getPoint();//発生元コンポーネントを基準とする相対座標
			System.out.println(theMLoc);//デバッグ（確認用）に，取得したマウスの位置をコンソールに出力する
			Point theBtnLocation = theButton.getLocation();//クリックしたボタンを座標を取得する
			theBtnLocation.x += theMLoc.x-15;//ボタンの真ん中当たりにマウスカーソルがくるように補正する
			theBtnLocation.y += theMLoc.y-15;//ボタンの真ん中当たりにマウスカーソルがくるように補正する
			theButton.setLocation(theBtnLocation);//マウスの位置にあわせてオブジェクトを移動する

	
			//送信情報を作成する（受信時には，この送った順番にデータを取り出す．スペースがデータの区切りとなる）
			String msg = "MOVE"+" "+theArrayIndex+" "+theBtnLocation.x+" "+theBtnLocation.y;

			//サーバに情報を送る
			out.println(msg);//送信データをバッファに書き出す
			out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する

			repaint();//オブジェクトの再描画を行う
		}
		**/
	}

	public void mouseMoved(MouseEvent e) {//マウスがオブジェクト上で移動したときの処理
		//System.out.println("マウス移動");
		int theMLocX = e.getX();//マウスのx座標を得る
		int theMLocY = e.getY();//マウスのy座標を得る
		//System.out.println(theMLocX+","+theMLocY);//コンソールに出力する
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
							//ボタンの位置情報を作る
							int msgy = y + dy;
							int msgx = x + dx;
							int theArrayIndex = msgy*8 + msgx;
							
							//サーバに情報を送る
							System.out.println("フリップ数：" + flipResult);
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
				System.out.println("カウントエラー");
			}
		}
		
		Countlist[0] = myCount;
		Countlist[1] = yourCount;
		Countlist[2] = boardCount;

		System.out.println("自分のコマ数：" + Countlist[0] +"相手のコマ数：" + Countlist[1] + "置いてないコマ数：" + Countlist[2]);
		
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
		JOptionPane.showMessageDialog(null,"試合が終了しました","メッセージ",JOptionPane.INFORMATION_MESSAGE);
		int[] Countlist = pieceCount();
		int myCount = Countlist[0];
		int yourCount = Countlist[1];
		
		if(myCount > yourCount){
			System.out.println("あなたの勝ちです");
			resultButton = new JButton(win);
			c.add(resultButton);//ペインに貼り付ける
			resultButton.setBounds(70,500,275,300);

		} else if(myCount < yourCount){
			System.out.println("あなたの負けです");
			resultButton = new JButton(lose);
			c.add(resultButton);//ペインに貼り付ける
			resultButton.setBounds(70,500,275,300);

		} else if (myCount == yourCount){
			System.out.println("引き分けです");
			resultButton = new JButton(draw);
			c.add(resultButton);//ペインに貼り付ける
			resultButton.setBounds(70,500,275,300);
		}
		
		  //勝敗が決まれば、表示、有効化
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
		
		//ターン数を０に初期化
		turnCount = 0;

		//テスト
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
	
	//盤面にコマを置ける場所があるかを調べる関数
	
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
								//置ける場所が1つでもあればＴＲＵＥで返す
								System.out.println("置ける場所がありました");
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
		//全部のマスに置けななかったらＦＡＬＳＥを返す
		System.out.println("置ける場所がありません");
		if(!(flag)){
			String msg = "PASS" + " " + myColor;
			//サーバに情報を送る
			out.println(msg);//送信データをバッファに書き出す
			out.flush();//送信データをフラッシュ（ネットワーク上にはき出す）する
		}
		return flag;
	}
	

	//閉じるボタンを押したときの処理の定義
	class WindowClosing extends WindowAdapter{
         public void windowClosing(WindowEvent e) {
             int ans = JOptionPane.showConfirmDialog(MyClient.this, "このまま終了するとあなたの負けになってしまいますがよろしいですか（終了するときはEXITボタンを押してください）");
             System.out.println(ans);
             if(ans == JOptionPane.YES_OPTION) {
                System.out.println("プログラムによる終了処理の実行");
				String msg = "ESCPAE";
				out.println(msg);
				out.flush();
                System.exit(0);
             }
         }
     }

	
	
}
