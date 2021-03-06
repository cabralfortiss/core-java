/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.common.messages;

import eu.arrowhead.common.database.ArrowheadService;
import eu.arrowhead.common.exception.BadPayloadException;
import java.util.HashSet;
import java.util.Set;

public class ServiceQueryForm {

  private ArrowheadService service;
  private boolean pingProviders;
  private boolean metadataSearch;
  private int version = 1;

  public ServiceQueryForm() {
  }

  public ServiceQueryForm(ArrowheadService service, boolean pingProviders, boolean metadataSearch) {
    this.service = service;
    this.pingProviders = pingProviders;
    this.metadataSearch = metadataSearch;
  }

  public ServiceQueryForm(ServiceRequestForm srf) {
    this.service = srf.getRequestedService();
    this.pingProviders = srf.getOrchestrationFlags().get("pingProvider");
    this.metadataSearch = srf.getOrchestrationFlags().get("metadataSearch");
  }

  public ArrowheadService getService() {
    return service;
  }

  public void setService(ArrowheadService service) {
    this.service = service;
  }

  public boolean isPingProviders() {
    return pingProviders;
  }

  public void setPingProviders(boolean pingProviders) {
    this.pingProviders = pingProviders;
  }

  public boolean isMetadataSearch() {
    return metadataSearch;
  }

  public void setMetadataSearch(boolean metadataSearch) {
    this.metadataSearch = metadataSearch;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public Set<String> missingFields(boolean throwException, Set<String> mandatoryFields) {
    Set<String> mf = new HashSet<>();
    if (mandatoryFields != null) {
      mf.addAll(mandatoryFields);
    }
    if (service == null) {
      mf.add("service");
    } else {
      if (metadataSearch) {
        mf.add("serviceMetadata");
      }
      mf = service.missingFields(false, false, mf);
    }
    if (throwException && !mf.isEmpty()) {
      throw new BadPayloadException("Missing mandatory fields for " + getClass().getSimpleName() + ": " + String.join(", ", mf));
    }
    return mf;
  }

}
