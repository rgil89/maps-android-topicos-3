package com.trabalho3.topicos2;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;

public class Trabalho3Activity extends MapActivity 
{
    /** Called when the activity is first created. */
	com.google.android.maps.MapView mv ;
	Button botao ;
    EditText lat ;
    EditText longi ;
    EditText etnome ;
    DBAdapter adapter ;
    MapController controle ;
    GeoPoint ponto;
    Spinner s ;
    	
        
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
         mv = (com.google.android.maps.MapView)findViewById(R.id.mapView);
    	 botao = (Button)findViewById(R.id.button1);
         lat = (EditText)findViewById(R.id.etLati);
         longi = (EditText)findViewById(R.id.etLongi);
         etnome = (EditText)findViewById(R.id.etNome);
         adapter = new DBAdapter(this).open();
         controle = mv.getController();
         s = (Spinner)findViewById(R.id.spinner1);
        
        mv.setBuiltInZoomControls(true);
        botao.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) 
			{
				adapter.insereEntrada(longi.getText().toString(), lat.getText().toString(), etnome.getText().toString());
			}
				
		});
        this.getSpinnerAdapter();
        
    }

public void getSpinnerAdapter()
{
	Cursor c = adapter.getTodasEntradas();
	   startManagingCursor(c);             
	    String[] columns = new String[]{DBAdapter.KEY_ROWID, DBAdapter.KEY_NOME};
	    int[] to = new int []{android.R.id.text1};

	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, c, columns, to);

	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	    //get reference to the spinner...
	    Spinner s =(Spinner) findViewById(R.id.spinner1);
	    s.setAdapter(adapter);

	
	
	
	
}
    
    
    
    
    
    
    @Override
    protected boolean isRouteDisplayed() 
    {
      // TODO Auto-generated method stub
      return false;
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        criaMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  
    	
         return itemEscolhido(item);    
    }


    private void criaMenu(Menu menu){
        menu.setQwertyMode(true);
        MenuItem mnu1 = menu.add(0, 0, 0, "Carregar"); 
        mnu1.setAlphabeticShortcut('a');          
        
        MenuItem mnu2 = menu.add(0, 1, 1, "Satelite");
        mnu2.setAlphabeticShortcut('b'); 
    }
    
    private boolean itemEscolhido(MenuItem item){        
        switch (item.getItemId()) {
        
        case 0: 
        	ponto = new GeoPoint(Integer.parseInt(lat.getText().toString()), Integer.parseInt(longi.getText().toString()));
        	controle.setCenter(ponto);
            
            
        
        case 2:
            Toast.makeText(this, "Satelite mode on/off", Toast.LENGTH_LONG).show();
            if(mv.isSatellite())
            {
            	mv.setSatellite(false);
            }else mv.setSatellite(true);
            return true;
        }
        return false;
    }
    
    /*
     *  Métodos opcionais. Utilizados para a criação de menus de contexto.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,ContextMenuInfo menuInfo) {
         super.onCreateContextMenu(menu, view, menuInfo);
         criaMenu(menu);
    }
 
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {    
         return itemEscolhido(item);    
    }
}