package tk.idclxvii.sharpfixandroid;

import java.util.concurrent.Executor;

import tk.idclxvii.sharpfixandroid.utils.Logcat;
import tk.idclxvii.sharpfixandroid.utils.AsyncTask;
import android.app.*;
import android.content.*;


/**
 * This class is derived directly from the modified AsyncTask from 
 * API 11 (Honeycomb). The modified AsyncTask disables the use of
 * {@code executeOnExecutor(SERIAL_EXECUTOR)} on all API levels 
 * in exchange of enabling the use of {@code executeOnExecutor(THREAD_POOL_EXECUTOR)} 
 * on as low as API level 3. If serial execution of AsyncTask is really 
 * needed, the method {@code execute()} is the substitute for {@code executeOnExecutor(SERIAL_EXECUTOR)}
 * 
 * @author IDcLxViI
 *
 * @param <Params> The set of Parameters to be used by {@link #doInBackground(Params...)}
 * @param <Progress> The set of Parameters to be used by {@link #onProgressUpdate(Progress...)}
 * @param <Result>  The set of Parameters to be used by {@link #onPostExecute(Progress...)}
 * 
 * 
 */

public abstract class GlobalAsyncTask<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result>{

	// ProgressDialog Properties:
	private ProgressDialog dialog;
	private Context context = null;
	private String dialogTitle = "Loading";
	private String dialogMsg = "Please wait . . .";
	private Exception exception = null;
	
	
	// AsyncTask < Params, Progress, Result >

	// This method runs the task in parallel. This method must be 
	// invoked in the UI - thread.
	//this.executeOnExecutor(THREAD_POOL_EXECUTOR, String sql);
	/**
	 * This constructor allows the user explicitly specify
	 * that this instance will not use the ProgressDialog feature.
	 * @author IDcLxViI
	 * 
	 */
	public GlobalAsyncTask(){
		
	}
	/**
	 * This constructor allows the user explicitly specify
	 * whether this instance wants to use a ProgressDialog or not.
	 * If this constructor is used, the instance uses the default
	 * ProgressDialog title {@linkplain #dialogTitle} and
	 * message {@link #dialogMsg};
	 * @author IDcLxViI
	 *	
	 * @param context The context to be used by the ProgressDialog
	 */
	public GlobalAsyncTask(Context context) {
        this.context = context;
        // return this;
	}
	/**
	 * This constructor allows the user explicitly specify
	 * whether this instance wants to use a ProgressDialog or not.
	 * If this constructor is used, the instance uses the default
	 * ProgressDialog message {@link #dialogMsg};
	 * @author IDcLxViI
	 *	
	 * @param context The context to be used by the ProgressDialog
	 * @param title The title of the ProgressDialog
	 * 
	 * 
	 */
	public GlobalAsyncTask(Context context, String title) {
        this.context = context;
        this.dialogTitle = title;
        // return this;
	}
	
	
	/**
	 * This constructor allows the user explicitly specify
	 * whether this instance wants to use a ProgressDialog or not.
	 * 
	 * @author IDcLxViI
	 *	
	 * @param context The context to be used by the ProgressDialog
	 * @param title The title of the ProgressDialog
	 * @param msg The message on the ProgressDialog's body
	 * 
	 * 
	 */
	public GlobalAsyncTask(Context context, String title, String msg) {
        this.context = context;
        this.dialogTitle = title;
        this.dialogMsg = msg;
        // return this;
	}
	
	
	/*
	public final AsyncTask<Params, Progress, Result> start(Executor exe, Params...params ){
		
		// Executors: SERIAL_EXECUTOR for concurrent , THREAD_POOL_EXECUTOR for parallel
		
		return this.executeOnExecutor(exe, params);
	}
	*/
	
	
	
	@Override
	protected void onPreExecute(){
		//this.exec
		// runs on the UI thread before doInBackground is called
        if(this.context != null){
        	dialog = new ProgressDialog(this.context);
	        dialog.setTitle(this.dialogTitle);
	        dialog.setMessage(this.dialogMsg);
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.show();
        }
	}
	
	/**
	 * Gets the context that is used by this class' ProgressDialog
	 * @author IDcLxViI
	 *	
	 * @return The context used upon instantiating this instance or 
	 * {@code null} if this instance have not set a context.
	 * 
	 */
	public Context getContext(){
		return this.context;
	}
	
	/**
	 * {@code @Override} this on the subclass and place the code to be 
	 * executed by the {@code GlobalAsyncTask} here. This is the substitute
	 * of {@code doInBackground(Params...)} since {@code GlobalAsyncTask}
	 * does not allow overriding {@code doInBackground(Params...)}
	 * anymore. This is to ensure that the AsyncTask handles the exceptions
	 * internally rather than externally like the conventional method.
	 * When an exception is thrown, {@code #onException(Exception)} handles
	 * it which is needed to be overriden as well.
	 * @author IDcLxViI
	 *	
	 *@see #onException(Exception)
	 * @return The result of the {@code AsyncTask} computation
	 * @param params The set of parameters to be run on doInBackground
	 */
	protected abstract Result doTask(Params... params)throws Exception;
	
	@Override
	public final Result get(){
		try{
			return super.get();
		}catch(Exception e){
			this.exception = e;
			return null;
		}
	}
	
	@Override
	public final GlobalAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params){
		try{
			super.executeOnExecutor(exec, params);
		}catch(Exception e){
			this.exception = e;
		}
		return this;
	}
	
	@Override
	protected final Result doInBackground(Params... params){
		// TODO Auto-generated method stub
		try{
			return doTask(params);
		}catch(InterruptedException ie){
			this.onCancelled();
			return null;
		}catch(Exception e){
			this.exception = e;
			onException(e);
			//this.cancel(true);
			return null;
		}
		
	}
	
	/**
	 * Override this method to include additional
	 * exception handling. Please note that Logcat
	 * logs has already been removed so you might want
	 * to put stacktrace logging here if there are no 
	 * special actions to be taken when an exception occurs.
	 * @author IDcLxViI
	 *	
	 * 
	 * {@code null} if this instance have not set a context.
	 * 
	 */
	protected abstract void onException(Exception e);
	
	@Override
	protected void onProgressUpdate(Progress... values){
		// runs on the UI thread, place all codes that
		// changes UI properties here by calling publishProgress
		// inside the doTask / doInBackground
		
		
	}
	
	@Override 
	protected void onPostExecute(Result returnedResult){
		// This runs on the UI thread and is called after doInBackground finishes
		// doInBackground passes its return value to this method after finishing its task
		if(this.context != null){
			dialog.dismiss();
		}
		
		
		// cancel handling
		if(this.exception != null){
			// an exception has been caught!
			/*
			Logcat.logCaughtException((this.context != null ? this.context.getPackageName() : this.getClass().getName()),
					this.exception.getStackTrace());
			*/
			
			
		}else{
			
		}
	}
	
	
	
	@Override 
	protected void onCancelled(Result result){
		
	}


	
	
	
	
}


