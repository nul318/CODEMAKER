package com.example.codemaker;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MyFile extends Activity {

	String Title;
	String SubTitle;
	String Fileurl;
	String Contents;
	String Password;
	
	
	MyFile(String Title, String SubTitle, String Fileurl, String Contents, String Password){
		this.Title=Title;
		this.SubTitle=SubTitle;
		this.Fileurl=Fileurl;
		this.Contents=Contents;
		this.Password=Password;
	}
}
