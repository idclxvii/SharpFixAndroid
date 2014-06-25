package tk.idclxvii.sharpfixandroid;

import android.os.AsyncTask;

public class GlobalAsyncTask<Params, Progress, Result> extends
	AsyncTask<Params, Progress, Result> {

	
	// AsyncTask < Params, Progress, Result >

	// This method runs the task in parallel. This method must be 
	// invoked in the UI - thread.
	//this.executeOnExecutor(THREAD_POOL_EXECUTOR, String sql);
	
	@Override
	protected Result doInBackground(Params... params) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override 
	protected void onPostExecute(Result returnedResult){
		// This runs on the UI thread and is called after doInBackground finishes
		// doInBackground passes its return value to this method after finishing its task
		
		
	}
	
	@Override
	protected void onPreExecute(){
		// runs on the UI thread before doInBackground is called
		 	
	}
	
	@Override 
	protected void onCancelled(Result result){
		
	}
	
	
	
}
