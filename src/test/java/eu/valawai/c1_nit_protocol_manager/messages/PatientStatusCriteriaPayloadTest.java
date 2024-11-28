/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages;

import eu.valawai.c1_nit_protocol_manager.ValueGenerator;

/**
 * Test the {@link PatientStatusCriteriaPayload}.
 *
 * @see PatientStatusCriteriaPayload
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class PatientStatusCriteriaPayloadTest extends PayloadTestCase<PatientStatusCriteriaPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PatientStatusCriteriaPayload createEmptyModel() {

		return new PatientStatusCriteriaPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(PatientStatusCriteriaPayload model) {

		model.age_range = ValueGenerator.next(AgeRangeOption.values());
		model.ccd = ValueGenerator.nextBoolean();
		model.clinical_risk_group = ValueGenerator.next(ClinicalRiskGroupOption.values());
		model.discomfort_degree = ValueGenerator.next(DiscomfortDegree.values());
		model.expected_survival = ValueGenerator.next(SurvivalOptions.values());
		model.frail_VIG = ValueGenerator.next(SPICT_Scale.values());
		model.has_advance_directives = ValueGenerator.nextBoolean();
		model.has_been_informed = ValueGenerator.nextBoolean();
		model.has_cognitive_impairment = ValueGenerator.next(CognitiveImpairmentLevel.values());
		model.has_emocional_pain = ValueGenerator.nextBoolean();
		model.has_social_support = ValueGenerator.nextBoolean();
		model.independence_at_admission = ValueGenerator.next(BarthelIndex.values());
		model.independence_instrumental_activities = ValueGenerator.nextInteger(8);
		model.is_coerced = ValueGenerator.nextBoolean();
		model.is_competent = ValueGenerator.nextBoolean();
		model.maca = ValueGenerator.nextBoolean();
		model.nit_level = ValueGenerator.next(NITLevel.values());
	}

}
