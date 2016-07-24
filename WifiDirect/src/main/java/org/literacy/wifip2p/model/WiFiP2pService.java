package org.literacy.wifip2p.model;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.NonNull;

import com.google.common.base.Objects;


/**
 * Created by oscarmakala on 22/07/2016.
 */
public final class WiFiP2pService {

    private final String instanceName;
    private final String serviceRegistrationType;
    private final WifiP2pDevice device;
    private final String address;
    private int port;

    public WiFiP2pService(@NonNull String instanceName, String serviceRegistrationType, @NonNull WifiP2pDevice wifiP2pDevice) {
        this.instanceName = instanceName;
        this.serviceRegistrationType = serviceRegistrationType;
        this.device = wifiP2pDevice;
        this.address = wifiP2pDevice.deviceAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getServiceRegistrationType() {
        return serviceRegistrationType;
    }

    public WifiP2pDevice getDevice() {
        return device;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WiFiP2pService wiFiP2pService = (WiFiP2pService) o;
        return Objects.equal(instanceName, wiFiP2pService.instanceName)
                && Objects.equal(serviceRegistrationType, wiFiP2pService.serviceRegistrationType) &&
                Objects.equal(address, wiFiP2pService.address);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(instanceName, serviceRegistrationType, address);
    }

}
