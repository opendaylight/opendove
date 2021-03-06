/*
 * Copyright IBM Corporation, 2013.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.opendove.odmc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class OpenDoveNetworkSubnetAssociation extends OpenDoveObject
    implements IfOpenDCSTrackedObject, IfOpenDGWTrackedObject {

    @XmlElement(name="uuid")
    String uuid;

    @XmlElement(name="vnid_id")
    int openDoveNetworkVnid;

    @XmlElement(name="subnet_id")
    String openDoveNetworkSubnetUuid;

    public OpenDoveNetworkSubnetAssociation() {
        uuid = java.util.UUID.randomUUID().toString();
        tombstoneFlag = false;
    }

    public int getOpenDoveNetworkVnid() {
        return openDoveNetworkVnid;
    }

    public void setOpenDoveNetworkVnid(int openDoveNetworkVnid) {
        this.openDoveNetworkVnid = openDoveNetworkVnid;
    }

    public String getOpenDoveNetworkSubnetUuid() {
        return openDoveNetworkSubnetUuid;
    }

    public void setOpenDoveNetworkSubnetUuid(String openDoveNetworkSubnetUuid) {
        this.openDoveNetworkSubnetUuid = openDoveNetworkSubnetUuid;
    }

    public boolean isTrackedByDCS() {
        return true;
    }

    public String getSBDcsUri() {
        return "/controller/sb/v2/opendove/odmc/networks/"+openDoveNetworkVnid+"/subnets/"+openDoveNetworkSubnetUuid;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    public boolean isTrackedByDGW() {
        return true;
    }

    public String getSBDgwUri() {
        //return "/controller/sb/v2/opendove/odmc/networkSubnets/"+uuid;
        return "/controller/sb/v2/opendove/odmc/networks/"+openDoveNetworkVnid+"/networkSubnets/"+openDoveNetworkSubnetUuid;
    }
}
