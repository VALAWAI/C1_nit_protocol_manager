/*
  Copyright 2022-2026 VALAWAI

  Use of this source code is governed by GNU General Public License version 3
  license that can be found in the LICENSE file or at
  https://opensource.org/license/gpl-3-0/
*/

package eu.valawai.c1_nit_protocol_manager.messages;

/**
 * The level of therapeutic intensity.
 *
 * @author UDT-IA, IIIA-CSIC
 */
public enum NITLevel {

	/**
	 * It includes all possible measures to prolong survival
	 */
	ONE,

	/**
	 * Includes all possible measures except CPR.
	 */
	TWO_A,

	/**
	 * Includes all possible measures except CPR and ICU.
	 */
	TWO_B,

	/**
	 * Includes complementary scans and non-invasive treatments.
	 */
	THREE,

	/**
	 * It includes empiric symptomatic treatments according to clinical suspicion,
	 * which can be agreed as temporary.
	 */
	FOUR,

	/**
	 * No complementary examinations or etiological treatments are carried out, only
	 * treatments for comfort.
	 */
	FIVE;

}
