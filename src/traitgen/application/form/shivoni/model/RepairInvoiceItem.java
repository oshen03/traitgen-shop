/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shivoni.model;

public class RepairInvoiceItem {

    /**
     * @return the serviceId
     */
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @param serviceId the serviceId to set
     */
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the serviceCharge
     */
    public Double getServiceCharge() {
        return serviceCharge;
    }

    /**
     * @param serviceCharge the serviceCharge to set
     */
    public void setServiceCharge(Double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

   
    
    private String serviceId;
    private String serviceName;
    private Double serviceCharge;
     
    
}
