package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.util.LinkedList;
import java.util.List;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihComum;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihDadosOpm;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihOpmSus;

/**
 * AIH 07
 * @author gandriotti
 *
 */
public class RegistroAihOpmSusPadrao
		extends
			AbstractAgrupamentoRegistroAih
		implements
			RegistroAihOpmSus {

	private final RegistroAihComum comum;
	private final List<RegistroAihDadosOpm> dadosOpm;

	public RegistroAihOpmSusPadrao(final RegistroAihComum comum,
			final List<RegistroAihDadosOpm> dadosOpm) {

		super();

		//check args
		if (comum == null) {
			throw new IllegalArgumentException("Parametro comum nao informado!!!");
		}
		if (dadosOpm == null) {
			throw new IllegalArgumentException("Parametro dadosOpm nao informado!!!");
		}
		//algo
		this.comum = comum;
		this.dadosOpm = dadosOpm;
	}

	@Override
	public List<RegistroAih> obterListaRegistros() {

		List<RegistroAih> result = null;

		result = new LinkedList<RegistroAih>();
		result.add(this.getComum());
		result.addAll(this.getDadosOpm());

		return result;
	}

	@Override
	public RegistroAihComum getComum() {

		return this.comum;
	}

	@Override
	public List<RegistroAihDadosOpm> getDadosOpm() {

		return this.dadosOpm;
	}
}
