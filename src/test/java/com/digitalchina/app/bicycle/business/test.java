package com.digitalchina.app.bicycle.business;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class test {
	
	
	public static long convert2long(String data) {
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
		Date dt = null;
		try {
			dt = sdf.parse(data);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		return dt.getTime();    
	}
	/*List<Map<String,Object>> myresults = new ArrayList<>(); ;
	Map<String,Object> map1 = new HashMap<String,Object>();
	Map<String,Object> map2 = new HashMap<String,Object>();
	Map<String,Object> map3 = new HashMap<String,Object>();
	Map<String,Object> map4 = new HashMap<String,Object>();
	Map<String,Object> map5 = new HashMap<String,Object>();
	Map<String,Object> map6 = new HashMap<String,Object>();
	Map<String,Object> map7 = new HashMap<String,Object>();
	Map<String,Object> map8 = new HashMap<String,Object>();
	Map<String,Object> map9 = new HashMap<String,Object>();
	Map<String,Object> map10 = new HashMap<String,Object>();
	
	public test() {
		map1.put("fee","0");
		map1.put("parkName","深盐路-20");
		map1.put("flag","还车记录");
		map1.put("operateTime","2016-08-25 17:28:12");
		map1.put("status","正常还车");
		map1.put("equipNum","11066215");
		map1.put("type","2");
		map1.put("bikeNum","00000323");
		map1.put("cardNum","YT0038989");
		
		map2.put("fee","0");
		map2.put("parkName","海山路-2");
		map2.put("flag","借车记录");
		map2.put("operateTime","2016-08-25 17:19:24");
		map2.put("status","正常取车");
		map2.put("equipNum","11017122");
		map2.put("type","1");
		map2.put("bikeNum","00000323");
		map2.put("cardNum","YT0038989");
		
		map3.put("fee","0");
		map3.put("parkName","海山路-2");
		map3.put("flag","还车记录");
		map3.put("operateTime","22016-08-25 16:22:49");
		map3.put("status","正常还车");
		map3.put("equipNum","11017140");
		map3.put("type","2");
		map3.put("bikeNum","00000684");
		map3.put("cardNum","YT0038989");
		
		map4.put("fee","0");
		map4.put("parkName","深盐路-20");
		map4.put("flag","还车记录");
		map4.put("operateTime","2016-08-25 17:28:12");
		map4.put("status","正常还车");
		map4.put("equipNum","11066215");
		map4.put("type","2");
		map4.put("bikeNum","00000323");
		map4.put("cardNum","YT0038989");
		
		map5.put("fee","0");
		map5.put("parkName","海山路-2");
		map5.put("flag","借车记录");
		map5.put("operateTime","2016-08-25 17:19:24");
		map5.put("status","正常取车");
		map5.put("equipNum","11017122");
		map5.put("type","1");
		map5.put("bikeNum","00000323");
		map5.put("cardNum","YT0038989");
		
		map6.put("fee","0");
		map6.put("parkName","海山路-2");
		map6.put("flag","还车记录");
		map6.put("operateTime","22016-08-25 16:22:49");
		map6.put("status","正常还车");
		map6.put("equipNum","11017140");
		map6.put("type","2");
		map6.put("bikeNum","00000684");
		map6.put("cardNum","YT0038989");
		
		map7.put("fee","0");
		map7.put("parkName","深盐路-20");
		map7.put("flag","还车记录");
		map7.put("operateTime","2016-08-25 17:28:12");
		map7.put("status","正常还车");
		map7.put("equipNum","11066215");
		map7.put("type","2");
		map7.put("bikeNum","00000323");
		map7.put("cardNum","YT0038989");
		
		map8.put("fee","0");
		map8.put("parkName","海山路-2");
		map8.put("flag","借车记录");
		map8.put("operateTime","2016-08-25 17:19:24");
		map8.put("status","正常取车");
		map8.put("equipNum","11017122");
		map8.put("type","1");
		map8.put("bikeNum","00000323");
		map8.put("cardNum","YT0038989");
		
		map9.put("fee","0");
		map9.put("parkName","海山路-2");
		map9.put("flag","还车记录");
		map9.put("operateTime","22016-08-25 16:22:49");
		map9.put("status","正常还车");
		map9.put("equipNum","11017140");
		map9.put("type","2");
		map9.put("bikeNum","00000684");
		map9.put("cardNum","YT0038989");
		
		map10.put("fee","0");
		map10.put("parkName","海山路-2");
		map10.put("flag","还车记录");
		map10.put("operateTime","22016-08-25 16:22:49");
		map10.put("status","正常还车");
		map10.put("equipNum","11017140");
		map10.put("type","2");
		map10.put("bikeNum","00000684");
		map10.put("cardNum","YT0038989");
	}
	
	public void format(Map<String,Object> map) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		   try {
		    date = sdf.parse((String)map.get("operateTime"));
		   } catch (Exception e) {
		    e.printStackTrace();
		   }
		Long time = date.getTime();
		map.put("operateTime", time);
	}*/
	
	/* public static Map<String,Object> sortMap(Map<String,Object> map1,Map<String,Object> map2) {  
	        
		 Map<String,Object> map3,Map<String,Object> map3
		 ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(oldMap.entrySet());  
	        Collections.sort(list, new Comparator<Map.Entry<String,Object>>() {  
	  
	            @Override  
	            public int compare(Entry<java.lang.String, Object> arg0,  
	                    Entry<java.lang.String, Object> arg1) {  
	                return arg0.getValue() - arg1.getValue();  
	            }  
	        });  
	        Map newMap = new LinkedHashMap();  
	        for (int i = 0; i < list.size(); i++) {  
	            newMap.put(list.get(i).getKey(), list.get(i).getValue());  
	        }  
	        return newMap;  
	  }  
	
	 public static Map sortMap(Map oldMap) {  
	        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(oldMap.entrySet());  
	        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {  
	  
	            @Override  
	            public int compare(Entry<java.lang.String, Integer> arg0,  
	                    Entry<java.lang.String, Integer> arg1) {  
	                return arg0.getValue() - arg1.getValue();  
	            }  
	        });  
	        Map newMap = new LinkedHashMap();  
	        for (int i = 0; i < list.size(); i++) {  
	            newMap.put(list.get(i).getKey(), list.get(i).getValue());  
	        }  
	        return newMap;  
	    }  
	*/
	
	public static void main(String args[]) {
		System.out.println(convert2long("2016-08-25 17:28:12"));
		System.out.println(convert2long("2016-08-25 17:19:24"));
		/*long a = System.currentTimeMillis();
		new hw1().f(100000);
		
		long b = System.currentTimeMillis();
		System.out.println((b-a)+"毫秒");
		
		
		ArrayList<Integer> li = new ArrayList<Integer>();
		for (int k = 0; k < 100000; k++) {
			li.add(k);
		}
		long a2 = System.currentTimeMillis();
		new hw1().threadCal(li);
		long b2 = System.currentTimeMillis();
		System.out.println((b2-a2)+"毫秒");*/
		
	}
	
	/*public int g(int i) {
		return (1+i+2*i*i+3*i*i*i);
	}

	public int f(int i) {
		int result = 0;
		for(int j=0; j<i; j++) {
			g(j);
		}
		return result;
	}
	
	public int sum(int i) {
		int result = 0;
		for(int j=0; j<i; j++) {
			result += f(j);
		}
		return result;
	}
	
	
	public void threadCal(ArrayList<Integer> li) {
		
		final CountDownLatch cdl = new CountDownLatch(li.size());
		Observable
			.from(li)
			.flatMap(
					new Func1<Integer, Observable<Integer>>(){

						@Override
						public Observable<Integer> call(Integer i) {
							return Observable.just(g(i));
						}
						
			})		
			.subscribeOn(SchedulerHandler.computation())
			.observeOn(SchedulerHandler.computation())
			.subscribe(new Subscriber<Integer>() {

				@Override
				public void onCompleted() {
					cdl.countDown(); 
				}
	
				@Override
				public void onError(Throwable e) {
					e.printStackTrace();
					this.onCompleted();
				}
	
				@Override
				public void onNext(Integer t) {
					this.onCompleted();
				}
			});

			try {
				cdl.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		
	}*/
}


