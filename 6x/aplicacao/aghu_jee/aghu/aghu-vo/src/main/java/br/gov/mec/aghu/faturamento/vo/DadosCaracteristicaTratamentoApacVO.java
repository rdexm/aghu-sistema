package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatTratamentoApacCaract;

public class DadosCaracteristicaTratamentoApacVO {

	private final Integer valorNumerico;
	private final String valorChar;
	private final Date valorData;

	private DadosCaracteristicaTratamentoApacVO(final Integer valorNumerico,
			final String valorChar,
			final Date valorData) {

		super();

		this.valorNumerico = valorNumerico;
		this.valorChar = valorChar;
		this.valorData = valorData;
	}

	public DadosCaracteristicaTratamentoApacVO(final FatTratamentoApacCaract tac) {

		this(tac != null ? tac.getValorNumerico() : null, tac != null ? tac.getValorChar() : null, tac != null
				? tac.getValorData()
				: null);
	}

	public DadosCaracteristicaTratamentoApacVO(final FatCaractItemProcHosp cih) {

		this(cih != null ? cih.getValorNumerico() : null, cih != null ? cih.getValorChar() : null, cih != null
				? cih.getValorData()
				: null);
	}

	public Integer getValorNumerico() {

		return this.valorNumerico;
	}

	public String getValorChar() {

		return this.valorChar;
	}

	public Date getValorData() {

		return this.valorData;
	}
}