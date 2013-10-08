/*
 * Copyright IBM Corporation, 2013.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.opendove.odmc.rest.southbound;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.text.SimpleDateFormat;
import java.util.Calendar;


import org.codehaus.enunciate.jaxrs.ResponseCode;
import org.codehaus.enunciate.jaxrs.StatusCodes;
import org.opendaylight.controller.northbound.commons.RestMessages;
import org.opendaylight.controller.northbound.commons.exception.ServiceUnavailableException;
import org.opendaylight.opendove.odmc.IfOpenDoveServiceApplianceCRU;
import org.opendaylight.opendove.odmc.OpenDoveCRUDInterfaces;
import org.opendaylight.opendove.odmc.OpenDoveServiceAppliance;

/**
 * Open DOVE Southbound REST APIs for DCS Service Appliance.<br>
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

@Path("/odcs")
public class OpenDoveDcsServiceApplianceSouthbound {

    /*
     *  REST Handler Function for DCS <==> DMC Registration
     */
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    @StatusCodes({
            @ResponseCode(code = 200, condition = "Operation successful"),
            @ResponseCode(code = 201, condition = "Registration Accepted"),
            @ResponseCode(code = 409, condition = "Service Appliance IP Address Conflict"),
            @ResponseCode(code = 500, condition = "Internal Error") })
    public Response processDcsRegistration (OpenDoveServiceAppliance appliance) {
        String dsaIP   = appliance.getIP();
        String dsaUUID = appliance.getUUID();

        IfOpenDoveServiceApplianceCRU sbInterface = OpenDoveCRUDInterfaces.getIfDoveServiceApplianceCRU(this);

        if (sbInterface == null) {
            throw new ServiceUnavailableException("OpenDove SB Interface "
                    + RestMessages.SERVICEUNAVAILABLE.toString());
        }

        if (sbInterface.dsaIPConflict(dsaIP, dsaUUID))
            return Response.status(409).build();
        appliance.initDefaults();

        // Set the Timestamp
        String timestamp = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(Calendar.getInstance().getTime());
        appliance.setTimestamp(timestamp);

        if (sbInterface.applianceExists(dsaUUID) ) {
             sbInterface.updateDoveServiceAppliance(dsaUUID, appliance);
           
             /* Service Appliance Exists in Cache, Just Return HTTP_OK(200) in this Case */
             return Response.status(200).entity(appliance).build();
        } else {
             sbInterface.addDoveServiceAppliance(dsaUUID, appliance);
        }

        return Response.status(201).entity(appliance).build();
    }

    /*
     *  REST Handler Function for DCS Heart-Beat
     */
    @Path("/{dsaUUID}")
    @PUT
    @Produces({ MediaType.APPLICATION_JSON })
    @StatusCodes({
            @ResponseCode(code = 200, condition = "Operation successful"),
            @ResponseCode(code = 409, condition = "Service Appliance IP Address Conflict"),
            @ResponseCode(code = 500, condition = "Internal Error") })

    public Response procesDcsHeartbeat (
                                 @PathParam("dsaUUID") String dsaUUID,
                                 OpenDoveServiceAppliance appliance) {
        String dsaIP   = appliance.getIP();
        appliance.setUUID(dsaUUID);
        //System.out.println("*********Inside DCS processHeartbeat  \n");

        IfOpenDoveServiceApplianceCRU sbInterface = OpenDoveCRUDInterfaces.getIfDoveServiceApplianceCRU(this);

        if (sbInterface == null) {
            throw new ServiceUnavailableException("OpenDove SB Interface "
                    + RestMessages.SERVICEUNAVAILABLE.toString());
        }

        if (sbInterface.dsaIPConflict(dsaIP, dsaUUID))
            return Response.status(409).build();
        appliance.initDefaults();

        // Set the Timestamp
        String timestamp = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(Calendar.getInstance().getTime());
        appliance.setTimestamp(timestamp);

        if (sbInterface.applianceExists(dsaUUID) ) {
            sbInterface.updateDoveServiceAppliance(dsaUUID, appliance);
        } else {
            /*
             * Heart-Beat will be accepted only for Registered Appliances
             */
            return Response.status(409).build();
        }

        return Response.status(200).entity(appliance).build();
    }
}
