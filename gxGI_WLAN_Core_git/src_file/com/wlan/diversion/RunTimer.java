package com.wlan.diversion;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import com.wlan.comm.publicLoadConf;

public class RunTimer {
  private final Timer timer = new Timer();
  private final int minutes;

  public RunTimer(int minutes) {
    this.minutes = minutes;
  }

  public void start() {
    timer.schedule(new TimerTask() {
      public void run() {
        try {
          runagain();
          timer.cancel();
        }
        catch (SQLException ex) {
        }
        catch (Exception ex) {
        }
        timer.cancel();
      }

      private void runagain() throws Exception {
        com.wlan.diversionWlan.allBeging all = new com.wlan.diversionWlan.allBeging();
        try {
          all.run();
        } catch (Exception ex) {
          System.out.println("timer Exception:" + ex);
          ex.printStackTrace();
        }
        finally {
          RunTimer alarm = new RunTimer(publicLoadConf.timerJg);
          alarm.start();
        }
      }
    }, minutes);
  }

  public static void main(String[] args) {
	  
	  Timer timer = new Timer();
	  timer.schedule(new TimerTask() {
		
		@Override
		public void run() {
			new TimerLoad();
		}
	}, 500);
//	  ExecutorService service = Executors.newCachedThreadPool();
//	  service.execute(new Runnable() {
//		
//		@Override
//		public void run() {
//			
//		}
//	});
  }
}
