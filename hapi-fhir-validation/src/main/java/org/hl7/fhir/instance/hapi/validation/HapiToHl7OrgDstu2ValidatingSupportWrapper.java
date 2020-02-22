package org.hl7.fhir.instance.hapi.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.context.support.IContextValidationSupport;
import org.apache.commons.lang3.Validate;
import org.hl7.fhir.common.hapi.validation.BaseValidationSupportWrapper;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.List;
import java.util.stream.Collectors;

public class HapiToHl7OrgDstu2ValidatingSupportWrapper extends BaseValidationSupportWrapper implements ca.uhn.fhir.context.support.IContextValidationSupport {
	private final FhirContext myHapiCtx;

	/**
	 * Constructor
	 */
	public HapiToHl7OrgDstu2ValidatingSupportWrapper(FhirContext theHapiFhirContext, IContextValidationSupport theWrap) {
		super(FhirContext.forDstu2Hl7Org(), theWrap);

		Validate.isTrue(theHapiFhirContext.getVersion().getVersion() == FhirVersionEnum.DSTU2);
		myHapiCtx = theHapiFhirContext;
	}

	@Override
	public List<IBaseResource> fetchAllConformanceResources() {
		return super.fetchAllConformanceResources();
	}

	@Override
	public List<IBaseResource> fetchAllStructureDefinitions() {
		return super
			.fetchAllStructureDefinitions()
			.stream()
			.map(t -> translate(t))
			.collect(Collectors.toList());
	}

	@Override
	public <T extends IBaseResource> T fetchResource(Class<T> theClass, String theUri) {
		Class<? extends IBaseResource> type = translateTypeToHapi(theClass);
		IBaseResource output = super.fetchResource(type, theUri);
		return theClass.cast(translate(output));
	}

	@Override
	public IBaseResource fetchCodeSystem(String theSystem) {
		IBaseResource output = super.fetchCodeSystem(theSystem);
		return translate(output);
	}

	@Override
	public IBaseResource fetchValueSet(String theUri) {
		return translate(super.fetchValueSet(theUri));
	}

	@Override
	public IBaseResource fetchStructureDefinition(String theUrl) {
		return translate(super.fetchStructureDefinition(theUrl));
	}

	private Class<? extends IBaseResource> translateTypeToHapi(Class<? extends IBaseResource> theCodeSystemType) {
		String resName = getFhirContext().getResourceDefinition(theCodeSystemType).getName();
		return myHapiCtx.getResourceDefinition(resName).getImplementingClass();
	}

	private IBaseResource translate(IBaseResource theInput) {
		if (theInput == null) {
			return null;
		}
		String encoded = getFhirContext().newJsonParser().encodeResourceToString(theInput);
		return getFhirContext().newJsonParser().parseResource(encoded);
	}
}
