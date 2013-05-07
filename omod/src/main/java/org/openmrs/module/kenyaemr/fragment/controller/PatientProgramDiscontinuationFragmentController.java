/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.kenyaemr.fragment.controller;

import org.openmrs.*;
import org.openmrs.api.context.Context;
import org.openmrs.module.kenyaemr.MetadataConstants;
import org.openmrs.module.kenyaemr.util.KenyaEmrUtils;
import org.openmrs.ui.framework.annotation.FragmentParam;
import org.openmrs.ui.framework.fragment.FragmentModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Patient program discontinuation fragment
 */
public class PatientProgramDiscontinuationFragmentController {
	
	public void controller(@FragmentParam("patientProgram") PatientProgram enrollment,
						   @FragmentParam("encounterType") EncounterType encounterType,
						   @FragmentParam("complete") boolean complete,
						   FragmentModel model) {

		Encounter encounter = KenyaEmrUtils.lastEncounterInProgram(enrollment, encounterType);

		Map<String, Object> dataPoints = new LinkedHashMap<String, Object>();


		dataPoints.put("Completed", enrollment.getDateCompleted());

		if (complete) {
			if (enrollment.getOutcome() != null) {
				dataPoints.put("Outcome", enrollment.getOutcome());
			}

			if (enrollment.getProgram().getUuid().equals(MetadataConstants.HIV_PROGRAM_UUID)) {
				addHivDataPoints(dataPoints, enrollment, encounter);
			}
			else if (enrollment.getProgram().getUuid().equals(MetadataConstants.TB_PROGRAM_UUID)) {
				addTbDataPoints(dataPoints, enrollment, encounter);
			}
		}

		model.put("encounter", encounter);
		model.put("dataPoints", dataPoints);
	}

	private void addHivDataPoints(Map<String, Object> dataPoints, PatientProgram enrollment, Encounter encounter) {
		if (encounter != null) {
			Obs reasonObs = KenyaEmrUtils.firstObsInEncounter(encounter, Context.getConceptService().getConceptByUuid(MetadataConstants.REASON_FOR_PROGRAM_DISCONTINUATION_CONCEPT_UUID));
			if (reasonObs != null) {
				dataPoints.put("Reason", reasonObs.getValueCoded());
			}
		}
	}

	private void addTbDataPoints(Map<String, Object> dataPoints, PatientProgram enrollment, Encounter encounter) {
		if (encounter != null) {
			Obs outcomeObs = KenyaEmrUtils.firstObsInEncounter(encounter, Context.getConceptService().getConceptByUuid(MetadataConstants.TUBERCULOSIS_TREATMENT_OUTCOME_CONCEPT_UUID));
			if (outcomeObs != null) {
				dataPoints.put("Outcome", outcomeObs.getValueCoded());
			}
		}
	}
}