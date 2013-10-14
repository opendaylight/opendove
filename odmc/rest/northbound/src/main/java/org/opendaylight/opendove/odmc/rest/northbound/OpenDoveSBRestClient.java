/*
 * Copyright IBM Corporation, 2013.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.opendove.odmc.rest.northbound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONArray;
import org.opendaylight.controller.commons.httpclient.*;
import org.opendaylight.opendove.odmc.OpenDoveServiceAppliance;
import org.opendaylight.opendove.odmc.IfOpenDoveServiceApplianceCRU;
import org.opendaylight.opendove.odmc.OpenDoveCRUDInterfaces;

/**
 * Open DOVE REST Client Interface Class for  Service Appliances (DCS and DGW etc.).<br>
 *
 * <br>
 * <br>
 * Authentication scheme [for now]: <b>HTTP Basic</b><br>
 * Authentication realm : <b>opendaylight</b><br>
 * Transport : <b>HTTP and HTTPS</b><br>
 * <br>
 * HTTPS Authentication is disabled by default. Administrator can enable it in
 * tomcat-server.xml after adding a proper keystore / SSL certificate from a
 * trusted authority.<br>
 * More info :
 * http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html#Configuration
 *
 */

public class OpenDoveSBRestClient {

    public OpenDoveSBRestClient () {
    }

    /*
     *  REST Client Method for "DCS service-appliance Role Assignment"
     */

    public Integer assignDcsServiceApplianceRole(OpenDoveServiceAppliance appliance) {

        String  dsaIP   = appliance.getIP();
        Integer dcs_rest_service_port = appliance.getDcsRestServicePort();


        try {
            String action = "start";
            JSONObject jo = new JSONObject().put("action", action);

            // execute HTTP request and verify response code
            String uri = "http://" + dsaIP + ":" + dcs_rest_service_port + "/controller/sb/v2/opendove/odcs/role";
            HTTPRequest request = new HTTPRequest();
            request.setMethod("PUT");
            request.setUri(uri);
            request.setEntity(jo.toString());

            Map<String, List<String>> headers = new HashMap<String, List<String>>();
            //  String authString = "admin:admin";
            //  byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            //  String authStringEnc = new String(authEncBytes);
            List<String> header = new ArrayList<String>();
            //  header.add("Basic "+authStringEnc);
            //  headers.put("Authorization", header);
            //  header = new ArrayList<String>();
            header.add("application/json");
            headers.put("Content-Type", header);
            headers.put("Accept", header);
            request.setHeaders(headers);
            HTTPResponse response = HTTPClient.sendRequest(request);
            return response.getStatus();
        } catch (Exception e) {
            return 400;
        }
    }
    /*
     *  REST Client Method for Providing DCS Cluster Nodes Details to All DCS Nodes that are 
     *  in Role Assigned State
     *  Return:
     *    1: for Success, if o-DMC receives HTTP_OK(200) from all the Nodes
     *   -1: failure for All other cases. 
     */

    public Integer sendDcsClusterInfo() {

        Integer dcs_rest_service_port;
        Integer dcs_raw_service_port;
        String  uuid;
        Integer ip_family;
        String  dsaIP;

        Integer  retVal = 1; // Success

        IfOpenDoveServiceApplianceCRU sbInterface = OpenDoveCRUDInterfaces.getIfDoveServiceApplianceCRU(this);
        List<OpenDoveServiceAppliance> oDCSs = sbInterface.getRoleAssignedDcsAppliances();

        Iterator<OpenDoveServiceAppliance> iterator = oDCSs.iterator();

        JSONArray dcs_list = new JSONArray();
        while (iterator.hasNext()) {
            OpenDoveServiceAppliance appliance =  iterator.next();
            ip_family = appliance.getIPFamily ();
            dsaIP     = appliance.getIP();
            uuid      = appliance.getUUID();
            dcs_rest_service_port = appliance.getDcsRestServicePort();
            dcs_raw_service_port = appliance.getDcsRawServicePort();

            JSONObject dcs = new JSONObject();
            try {
                  dcs.put("uuid", uuid);
                  dcs.put("ip_family", ip_family);
                  dcs.put("ip", dsaIP);
                  dcs.put("dcs_rest_service_port", dcs_rest_service_port);
                  dcs.put("dcs_raw_service_port",  dcs_raw_service_port);
            } catch ( Exception e ) {
               retVal = -1;
               return retVal;
            }
            dcs_list.put(dcs);
        }

        //String jsonList = dcs_list.toString();
        JSONObject jo;
        try {
              jo = new JSONObject().put("dps", dcs_list);
        } catch ( Exception e ) {
              retVal = -1;
              return retVal;
        }
        
        iterator = oDCSs.iterator();

        while (iterator.hasNext()) {
             try {
                 OpenDoveServiceAppliance appliance =  iterator.next();
                 dsaIP     = appliance.getIP();
                 dcs_rest_service_port = appliance.getDcsRestServicePort();
                 // execute HTTP request and verify response code
                 String uri = "http://" + dsaIP + ":" + dcs_rest_service_port + "/controller/sb/v2/opendove/odcs/cluster";
                 HTTPRequest request = new HTTPRequest();
                 request.setMethod("PUT");
                 request.setUri(uri);
                 //request.setEntity(dcs_list.toString());
                 request.setEntity(jo.toString());

                 Map<String, List<String>> headers = new HashMap<String, List<String>>();
                 // String authString = "admin:admin";
                 // byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
                 // String authStringEnc = new String(authEncBytes);
                 List<String> header = new ArrayList<String>();
                 // header.add("Basic "+authStringEnc);
                 // headers.put("Authorization", header);
                 // header = new ArrayList<String>();
                 header.add("application/json");
                 headers.put("Content-Type", header);
                 headers.put("Accept", header);
                 request.setHeaders(headers);
                 HTTPResponse response = HTTPClient.sendRequest(request);

                 if ( (response.getStatus() != 200)  || (response.getStatus() != 204)) {
                     retVal = -1;
                 } 
             } catch (Exception e) {
                 retVal =  -1;
                 return retVal;
             }
        } // end of while.
        return retVal;
    }
    /*
     *  REST Client Method for "DGW service-appliance Role Assignment"
     */

    public Integer assignDgwServiceApplianceRole(OpenDoveServiceAppliance appliance) {

        
        String  dsaIP   = appliance.getIP();
        Integer dgw_rest_service_port = appliance.getDgwRestServicePort();


        try {
            String action = "start";
            JSONObject jo = new JSONObject().put("action", action);

            // execute HTTP request and verify response code
            String uri = "http://" + dsaIP + ":" + dgw_rest_service_port + "/controller/sb/v2/opendove/odgw/role";
            HTTPRequest request = new HTTPRequest();
            request.setMethod("PUT");
            request.setUri(uri);
            request.setEntity(jo.toString());

            Map<String, List<String>> headers = new HashMap<String, List<String>>();
            //  String authString = "admin:admin";
            //  byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            //  String authStringEnc = new String(authEncBytes);
            List<String> header = new ArrayList<String>();
            //  header.add("Basic "+authStringEnc);
            //  headers.put("Authorization", header);
            //  header = new ArrayList<String>();
            header.add("application/json");
            headers.put("Content-Type", header);
            headers.put("Accept", header);
            request.setHeaders(headers);
            HTTPResponse response = HTTPClient.sendRequest(request);
            return response.getStatus();
        } catch (Exception e) {
            return 400;
        }
    }
}

