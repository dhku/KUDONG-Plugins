package example;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import kr.kudong.book.AldarBookCore;
import kr.kudong.book.AldarBookManager;
import kr.kudong.book.api.AldarBook;
import kr.kudong.book.api.AldarBookAPI;
import kr.kudong.book.bookInstance.Book;
import kr.kudong.book.bookInstance.components.Button;
import kr.kudong.book.bookInstance.components.Label;
import kr.kudong.book.bookInstance.components.RadioButton;
import kr.kudong.book.bookInstance.components.SelectableMember;
import kr.kudong.book.bookInstance.components.Switch;
import kr.kudong.book.bookInstance.components.ToggleButton;
import kr.kudong.book.bookInstance.components.UrlButton;
import kr.kudong.book.playerdata.AldarBookPlayer;

public class Example implements CommandExecutor
{


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label,
			String[] args)
	{
		
		
		Player player = (Player)sender;
		Plugin core =  Bukkit.getPluginManager().getPlugin("kudong-book");
		AldarBookAPI bookManager = ((AldarBookCore)core).getBookAPI();
		AldarBook book;
		
		RadioButton rb = new RadioButton();
		book = bookManager.createAldarBook("메뉴");
			
		book.addComponent(new Label("제목", ChatColor.GOLD + "" + ChatColor.BOLD + "<알다르 채팅 채널 리스트>", 0, 0, 0))
			.addComponent(new Label("제목1", ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + ">자유 1채널", 0, 1, 0))
			.addComponent(new Switch<String>("스위치1", 0, 1, 13, (a, b) ->
								{
									System.out.println("콜백함수가 정상적으로 작동되고 있습니다.");
									AldarBook book2 = bookManager.createAldarBook("메뉴2");
									book2.addComponent(new Label("제목", "<알다르 채팅 채널 리스트>dfdfgdgd", 0, 0, 0));
									book2.addComponent(new Label("제목2", "<알다르 채팅 채널 리스트>345", 0, 1, 0));
									//book2.addComponent(new Label("제목3", "어떤 생각이나 일을 글자로 나타낸 것이다. 음성 정보인 언어를 글자의 형태로 기록하게 됨에 따라 인류의 정보량은 폭발적으로 증가하였다.", 0, 2, 10));
									book2.addComponent(new Label("제목3", ":0: 안녕하세요 :developer:", 0, 2, 10));
									book2.addComponent(new ToggleButton("버튼3", 0, 3, 0,false, (c, d) ->{}));
									book2.addComponent(new ToggleButton("버튼4", 0, 4, 0,true, (c, d) ->{}));
									bookManager.openBook(book2, player.getUniqueId());
									
								}, 0, new SelectableMember<String>("예", "[ON]") , new SelectableMember<String>("아니요", "[OFF]")))
			.addComponent(new Label("제목12", ChatColor.GREEN + "RadioButton-1", 1, 2, 0))
			.addComponent(new ToggleButton("버튼1", 1, 2, 20, (a, b) -> { }, rb))
			.addComponent(new Label("제목13", ChatColor.GREEN + "RadioButton-2", 1, 3, 0))
			.addComponent(new ToggleButton("버튼2", 1, 3, 20, (a, b) -> {}, rb))
			.addComponent(new Label("제목14", ChatColor.GREEN + "RadioButton-3", 1, 4, 0))
			.addComponent(new ToggleButton("버튼3", 1, 4, 20, (a, b) ->{}, rb))
			.addComponent(new UrlButton("버튼4", "[홈페이지 버튼 클릭]", "https://www.naver.com", "www.naver.com 으로 이동합니다.",
						0, 7, 4, (a, b) -> { }))
			.addComponent(new Button("버튼5", "[눌러보랑께]", 0, 8, 4, (a, b) ->{ }));
					
			bookManager.openBook(book, player.getUniqueId());

//		
//		AldarBook book = null;
//		
//		if (manager.isExistChachingBookInstance("안녕", player)) 
//		{
//			book = manager.createOrGetChachingBookInstance("안녕", player);
//			manager.asyncOpenBook(book,playerdata);
//			return true;
//		}
//		
//		book = manager.createOrGetChachingBookInstance("안녕", player);
//		
//		SelectableMember<String> yes = new SelectableMember("예" , "[ON]");
//		SelectableMember<String> no = new SelectableMember("아니요" , "[OFF]");
//		
//		new Label("제목", ChatColor.GOLD+""+ChatColor.BOLD+"<알다르 채팅 채널 리스트>",0, 0, 0, book);
//		
//		new Label("제목1", ChatColor.DARK_GREEN+""+ChatColor.ITALIC+">자유 1채널",0, 2, 0, book);
//		
//		Switch<String> s = new Switch<String>("스위치1",0, 2, 13, book ,(a,b) -> {
//			
//			System.out.println("콜백함수가 정상적으로 작동되고 있습니다.");
//			
//		} , 0, yes, no);
//		
//		manager.registerComponent(s);
//		
//		new Label("제목2", ChatColor.DARK_GREEN+""+ChatColor.ITALIC+">자유 2채널",0, 3, 0, book);
//		
//		Switch<String> s2 = new Switch<String>("스위치2",0, 3, 13, book ,(a,b) -> {
//			
//			System.out.println("콜백함수가 정상적으로 작동되고 있습니다.");
//			
//		} , 0, yes, no);
//		
//		manager.registerComponent(s2);
//		
//		new Label("제목3", ChatColor.DARK_GREEN+""+ChatColor.ITALIC+">자유 3채널",0, 4, 0, book);
//		
//		Switch<String> s3 = new Switch<String>("스위치3",0, 4, 13, book ,(a,b) -> {
//			
//			System.out.println("콜백함수가 정상적으로 작동되고 있습니다.");
//			
//		} , 0, yes, no);
//		
//		manager.registerComponent(s3);
//		
//		new Label("제목4", ChatColor.DARK_GREEN+""+ChatColor.ITALIC+">자유 4채널",0, 5, 0, book);
//		
//		Switch<String> s4 = new Switch<String>("스위치4",0, 5, 13, book ,(a,b) -> {
//			
//			System.out.println("콜백함수가 정상적으로 작동되고 있습니다.");
//			
//		} , 0, yes, no);
//		
//		manager.registerComponent(s4);
//		
//		new Label("제목5", ChatColor.DARK_GREEN+""+ChatColor.ITALIC+">자유 5채널",0, 6, 0, book);
//		
//		Switch<String> s5 = new Switch<String>("스위치5",0, 6, 13, book ,(a,b) -> {
//			
//			System.out.println("콜백함수가 정상적으로 작동되고 있습니다.");
//			
//		} , 0, yes, no);
//		
//		manager.registerComponent(s5);
//		
//		new Label("제목6", ChatColor.DARK_GREEN+""+ChatColor.ITALIC+">자유 6채널",0, 7, 0, book);
//		
//		Switch<String> s6 = new Switch<String>("스위치6",0, 7, 13, book ,(a,b) -> {
//			
//			System.out.println("콜백함수가 정상적으로 작동되고 있습니다.");
//			
//		} , 0, yes, no);
//		
//		manager.registerComponent(s6);
//		
//		new Label("제목7", ChatColor.BLUE +"플레이어 네임: <"+player.getName()+">",0, 9, 0, book);
//		new Label("제목8", ChatColor.BLUE +"현재 접속서버 <"+player.getServer().getName()+">",0, 10, 0, book);
//		
//		
//		new Label("제목9", ChatColor.GOLD + "<라디오 버튼 테스트>",1, 0, 0, book);
//		
//		
//		RadioButton rb = new RadioButton();
//		new Label("제목12", ChatColor.GREEN +"RadioButton-1",1, 2, 0, book);
//		ToggleButton button1 = new ToggleButton("버튼1",1, 2, 20, book, (a,b)-> {}, rb);
//		rb.addBtn(button1);
//		manager.registerComponent(button1);
//		
//		new Label("제목13", ChatColor.GREEN +"RadioButton-2",1, 3, 0, book);
//		ToggleButton button2 = new ToggleButton("버튼2",1, 3, 20, book, (a,b)-> {}, rb) ;
//		rb.addBtn(button2);
//		manager.registerComponent(button2);
//		
//		new Label("제목14", ChatColor.GREEN +"RadioButton-3",1, 4, 0, book);
//		ToggleButton button3 = new ToggleButton("버튼3",1, 4, 20, book, (a,b)-> {}, rb) ;
//		rb.addBtn(button3);
//		manager.registerComponent(button3);
//		
//		UrlButton button4 = new UrlButton("버튼4", "[홈페이지 버튼 클릭]", "https://www.naver.com", "www.naver.com 으로 이동합니다.", 1, 7, 4, book, (a,b)-> {});
//		manager.registerComponent(button4);
//		
//		Button button5 = new Button("버튼5", "[눌러보랑께]", 1, 8, 4, book, (a,b)-> {});
//		manager.registerComponent(button5);
//		
//		
//		new Label("제목15", "＜￣｀ヽ、             ／￣＞",2, 4, 0, book);
//		new Label("제목16", " ゝ、  ＼   ／⌒ヽ,ノ  /´",2, 5, 0, book);
//		new Label("제목17", "    ゝ、  `（ ´･ω･)／",2, 6, 0, book);
//		new Label("제목18", "     >     ,ノ",2, 7, 0, book);
//		new Label("제목19", "      ∠_,,,/´””",2, 8, 0, book);
//
//		manager.asyncOpenBook(book,playerdata);
//		
		return true;
		
	}



}
