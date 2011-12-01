package com.trabalho3.topicos2;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class Trabalho3Activity extends MapActivity 
{
    /** Called when the activity is first created. */
	com.google.android.maps.MapView mv ;
	Button botao ;
    EditText etnome ;
    DBAdapter adapter ;
    MapController controle ;
    GeoPoint ponto, ponto2;
    Spinner s ;
    Cursor c;
    
    class MapOverlay extends com.google.android.maps.Overlay
    {
        @Override
        //método sobrescrito para desenhar a imagem no mapa
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
        {
            super.draw(canvas, mapView, shadow);                   
            ponto = mv.getMapCenter();
            
            Point screenPts = new Point();
            mv.getProjection().toPixels(ponto, screenPts);
 
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.push_pin);            
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-46, null);         
            return true;
        }
        
       
        public boolean onTouchEvent(MotionEvent event, MapView mapView) 
        {   
            //---when user lifts his finger---
            if (event.getAction() == 1) {                
                GeoPoint p = mapView.getProjection().fromPixels(
                    (int) event.getX(),
                    (int) event.getY());
 
                Toast.makeText(getBaseContext(), 
                        p.getLatitudeE6() / 1E6 + "," + 
                        p.getLongitudeE6() /1E6 , 
                        Toast.LENGTH_SHORT).show();

                
                
                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(
                    							p.getLatitudeE6()  / 1E6, 
                    							p.getLongitudeE6() / 1E6, 1);
 
                    String add = "";
                    if (addresses.size() > 0){
                        for (int i=0; i<addresses.get(0).getMaxAddressLineIndex();i++)
                           add += addresses.get(0).getAddressLine(i) + "\n";
                    }
                    Toast.makeText(getBaseContext(), add, Toast.LENGTH_SHORT).show();
                }
                catch (IOException e) {                
                    e.printStackTrace();
                }   
                return true;
            }
            else                
                return false;
        }      } 
        
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mv = (MapView) findViewById(R.id.mapView);
		botao = (Button)findViewById(R.id.button1);
		etnome = (EditText)findViewById(R.id.etNome);
		adapter = new DBAdapter(this).open();
		controle = mv.getController();
		s = (Spinner)findViewById(R.id.spinner1);
		
		 
		
		mv.setBuiltInZoomControls(true);
		mv.displayZoomControls(true);
		
mv.getZoomButtonsController().setAutoDismissed(false);
        mv.getZoomButtonsController().setVisible(true);
        
        
        botao.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) 
			{
				adapter.insereEntrada(String.valueOf(ponto.getLongitudeE6()), String.valueOf(ponto.getLatitudeE6()), etnome.getText().toString());
				getSpinnerAdapter();
			}
				
		});
        this.getSpinnerAdapter();
        
        controle = mv.getController();
        
        String coordinates[]={"-29.172596","-51.166302"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
     
        
        GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

        //para navegar o map para uma determinada localização
        controle.animateTo(p);
        controle.setZoom(17); //nível de zoom 
      
        
        MapOverlay mapOverlay = new MapOverlay();
        List<Overlay> listOfOverlays = mv.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);
        
		mv.invalidate();
        
    }

public void getSpinnerAdapter()
{
	Cursor c = adapter.getTodasEntradas();
	   startManagingCursor(c);             
	    String[] columns = new String[]{/*DBAdapter.KEY_ROWID, */DBAdapter.KEY_NOME};
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
        switch (item.getItemId()) 
        {
	        case 0: 
	        	c = adapter.getEntrada(s.getSelectedItemPosition());
	        	ponto2 = new GeoPoint(Integer.parseInt(c.getString(0)), Integer.parseInt(c.getString(1)));
	        	
	        	controle.setCenter(ponto2);
	        	
	        	
	        	break;
	            
	            
	        
	        case 1:
	            
	            if(mv.isSatellite())
	            {
	            	mv.setSatellite(false);
	            	Toast.makeText(this, "Satelite mode off", Toast.LENGTH_LONG).show();
	            }
	            else 
	            	{
	            		mv.setSatellite(true);
	            		Toast.makeText(this, "Satelite mode on", Toast.LENGTH_LONG).show();
	            	}
	            return true;
	        }
        return false;
    }
    
    
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