package br.gov.mec.aghu.internacao.business.vo;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;

public class LeitoQuartoUnidadeFuncionalVO {
	
	private AinLeitos leito;
	private AinQuartos quarto;
	private AghUnidadesFuncionais unidadeFuncional;
	
	public LeitoQuartoUnidadeFuncionalVO(
			AinLeitos leito, 
			AinQuartos quarto, 
			AghUnidadesFuncionais unidadeFuncional
	) {
		this.leito = leito;
		this.quarto = quarto;
		this.unidadeFuncional = unidadeFuncional;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

}
