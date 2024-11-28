/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages;

import java.util.ArrayList;
import java.util.Arrays;

import eu.valawai.c1_nit_protocol_manager.ValueGenerator;

/**
 * Test the {@link TreatmentPayload}.
 *
 * @see TreatmentPayload
 *
 * @author UDT-IA, IIIA-CSIC
 */
public class TreatmentPayloadTest extends PayloadTestCase<TreatmentPayload> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreatmentPayload createEmptyModel() {

		return new TreatmentPayload();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillIn(TreatmentPayload model) {

		model.id = ValueGenerator.nextPattern("treatment_id_{0}");
		model.patient_id = ValueGenerator.nextPattern("patient_id_{0}");
		model.created_time = ValueGenerator.nextPastTime();
		model.before_status = new PatientStatusCriteriaPayloadTest().nextModel();
		model.actions = new ArrayList<>(Arrays.asList(TreatmentAction.values()));
		ValueGenerator.shuffle(model.actions);
		final var max = ValueGenerator.rnd().nextInt(1, model.actions.size());
		model.actions = model.actions.subList(0, max);
		model.expected_status = new PatientStatusCriteriaPayloadTest().nextModel();
	}

}
