package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.model.AfaViaAdmUnf;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;

/**
 * 
 * AfaViaAdmUnfsVO
 */
public class AfaViaAdmUnfVO {

	/**
      * 
    */
	private AfaViaAdmUnf afaViaAdmUnf;
	private AghUnidadesFuncionais unidadeFuncional;

	public AfaViaAdmUnf getAfaViaAdmUnf() {
		return afaViaAdmUnf;
	}

	public void setAfaViaAdmUnf(AfaViaAdmUnf afaViaAdmUnf) {
		this.afaViaAdmUnf = afaViaAdmUnf;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

}
