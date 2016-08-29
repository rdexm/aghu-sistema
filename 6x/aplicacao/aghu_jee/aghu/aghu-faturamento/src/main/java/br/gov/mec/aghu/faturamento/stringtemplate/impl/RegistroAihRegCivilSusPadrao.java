package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.util.LinkedList;
import java.util.List;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihComum;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihRegCivilSus;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihRegistroCivil;

/**
 * AIH 04
 * @author gandriotti
 *
 */
public class RegistroAihRegCivilSusPadrao
		extends
			AbstractAgrupamentoRegistroAih
		implements
			RegistroAihRegCivilSus {

	private final RegistroAihComum comum;
	private final List<RegistroAihRegistroCivil> registroCivil;

	public RegistroAihRegCivilSusPadrao(final RegistroAihComum comum,
			final List<RegistroAihRegistroCivil> registroCivil) {

		super();

		//check args
		if (comum == null) {
			throw new IllegalArgumentException("Parametro comum nao informado!!!");
		}
		if (registroCivil == null) {
			throw new IllegalArgumentException("Parametro registroCivil nao informado!!!");
		}
		//algo
		this.comum = comum;
		this.registroCivil = registroCivil;
	}

	@Override
	public List<RegistroAih> obterListaRegistros() {

		List<RegistroAih> result = null;

		result = new LinkedList<RegistroAih>();
		result.add(this.getComum());
		result.addAll(this.getRegistroCivil());

		return result;
	}

	@Override
	public RegistroAihComum getComum() {

		return this.comum;
	}

	@Override
	public List<RegistroAihRegistroCivil> getRegistroCivil() {

		return this.registroCivil;
	}
}
