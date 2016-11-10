package com.example.codemaker;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Code extends Activity {

	String CodeName;
	boolean emphasis=false;
	
	Code(String CodeName){
		this.CodeName=CodeName;
	}
}
