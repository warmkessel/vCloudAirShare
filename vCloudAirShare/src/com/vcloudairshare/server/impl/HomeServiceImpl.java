package com.vcloudairshare.server.impl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.vcloudairshare.server.communications.Constants;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.vcloudairshare.server.communications.VCloudAirComm;
import com.vcloudairshare.server.datastore.entity.VirtualMachine;
import com.vcloudairshare.server.datastore.service.DataServices;
import com.vcloudairshare.shared.enumeration.Status;
import com.vcloudairshare.shared.interfaces.HomeService;

public class HomeServiceImpl extends RemoteServiceServlet implements HomeService {
  /**
   * 
   */
  private static final long serialVersionUID = -5441875622545162286L;

//@Override
//public UserDTO authentication(String username, String password)
//		throws IllegalArgumentException {
//	
//	DataServices.getUsersService().findByCredential(username, password);
//	return new UserDTO();;
//}
	public Boolean power(String id, Boolean state){
		VirtualMachine vm = DataServices.getVirtualMachineService().findByAirId(id);
		if(vm != null){
			if(state){
				vm.setCondition(Status.INUSE.getId());
			}
			else{
				vm.setCondition(Status.AVAILABLE.getId());
			}
			DataServices.getVirtualMachineService().persist(vm);

			VCloudAirComm comm = new VCloudAirComm();
			comm.login();
			
		try {
		  String url = "https://de-germany-1-16.vchs.vmware.com/api/compute/api/vApp/";
		  url = url +  id + "/power/action/";
		  if(state) {
			  url = url + "powerOn";
		  } else {
			  url = url + "powerOff";
		  }
		  URL url_obj = new URL(url);
		  HttpURLConnection conn = (HttpURLConnection) url_obj.openConnection();
		  conn.setConnectTimeout(60000); // 60 Seconds
		  conn.setReadTimeout(60000);
		  conn.addRequestProperty("Accept", "application/*+xml;version=5.11");
		  conn.addRequestProperty("x-vcloud-authorization", comm.getVchstoken());
		  conn.addRequestProperty("Authorization", "Bearer " + comm.getVchsToken2());
		  conn.setRequestMethod("POST");
		  System.out.println("Request URL ... " + url);

		  // normally, 3xx is redirect
		  int status = conn.getResponseCode();

		  System.out.println("Response Code ... " + status);
		  InputStream inStream = conn.getInputStream();	
		  StringBuffer sb = new StringBuffer();
		  sb = VCloudAirComm.buildResponse(sb, inStream);
		  System.out.println("response = " + sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
