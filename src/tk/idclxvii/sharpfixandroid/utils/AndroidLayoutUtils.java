package tk.idclxvii.sharpfixandroid.utils;

import java.io.*;
import java.util.*;

import tk.idclxvii.sharpfixandroid.R;

import android.app.*;
import android.content.Context;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;

public class AndroidLayoutUtils {

	private final String TAG = this.getClass().getSimpleName();
	
	public static CheckBox fixCheckBoxPaddingLeft(Context a, CheckBox cb, float dp){
		final float scale = a.getResources().getDisplayMetrics().density;
		cb.setPadding(cb.getPaddingLeft() + (int)(dp * scale + 0.5f),
		        cb.getPaddingTop(),
		        cb.getPaddingRight(),
		        cb.getPaddingBottom());
		return cb;
	}
	
	public static RadioButton fixRadioButtonPaddingLeft(Context a, RadioButton rb, float dp){
		final float scale = a.getResources().getDisplayMetrics().density;
		rb.setPadding(rb.getPaddingLeft() + (int)(dp * scale + 0.5f),
				rb.getPaddingTop(),
				rb.getPaddingRight(),
				rb.getPaddingBottom());
		return rb;
	}
	
	
	public interface CustomListView{
		
		
		public class ListItem{
			public int Id;
		    public String IconFile;
		    public String Name;

		    public ListItem(int id, String iconFile, String name) {

		        Id = id;
		        IconFile = iconFile;
		        Name = name;

		    }
		}
		
		public static class Model {
			
		    public static ArrayList<ListItem> Items = new ArrayList<ListItem>();

		    public static void LoadModel(int order, String imageName, String name) {
		    	
		        Items.add(new ListItem(order, imageName, name));
		        //Items.add(new ListItem(2, "folder_icon.png", "Folder"));
		        

		    }

		    public static void refreshModel(){
		    	Items = new ArrayList<ListItem>();
		    }
		    
		    public static ListItem getById(int id){
		        for(ListItem item : Items) {
		            if (item.Id == id) {
		                return item;
		            }
		        }
		        return null;
		    }
		}
		
		
		public class ItemAdapter extends ArrayAdapter {
		    
			private final Context context;
		    private final String[] Ids;
		    private final int rowResourceId;

		    public ItemAdapter(Context context, int textViewResourceId, String[] objects) {

		        super(context, textViewResourceId, objects);
		        this.context = context;
		        this.Ids = objects;
		        this.rowResourceId = textViewResourceId;

		    }

		    @Override
		    public View getView(int position, View convertView, ViewGroup parent) {

		    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		        View rowView = inflater.inflate(rowResourceId, parent, false);
		        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
		        TextView textView = (TextView) rowView.findViewById(R.id.textView);

		        
		        	//int id = Integer.parseInt(Ids[position]);
			        String imageFile = Model.getById(position).IconFile;
	
			        textView.setText(Model.getById(position).Name);
			        // get input stream
			        InputStream ims = null;
			        try {
			            ims = context.getAssets().open(imageFile);
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
			        // load image as Drawable
			        Drawable d = Drawable.createFromStream(ims, null);
			        // set image to ImageView
			        imageView.setImageDrawable(d);
			        return rowView;
		       
		    }
		    
		    
		}
	}
	
}
