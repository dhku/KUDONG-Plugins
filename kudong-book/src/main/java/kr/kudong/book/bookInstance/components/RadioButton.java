package kr.kudong.book.bookInstance.components;

import java.util.ArrayList;
import java.util.List;

public class RadioButton
{
	private List<ToggleButton> btnList;
	public RadioButton()
	{
		this.btnList = new ArrayList<>();
	}
	
	public void addBtn(ToggleButton btn)
	{
		this.btnList.add(btn);
	}

	public void toggle(ToggleButton button)
	{
		for(ToggleButton btn : this.btnList)
		{
			if(!btn.equals(button))
			{
				btn.setState(false);
			}
		}
		
	}
	

	
}
