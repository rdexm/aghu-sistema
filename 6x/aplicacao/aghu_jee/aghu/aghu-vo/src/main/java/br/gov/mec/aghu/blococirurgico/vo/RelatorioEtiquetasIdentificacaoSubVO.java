package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class RelatorioEtiquetasIdentificacaoSubVO extends
		RelatorioEtiquetasIdentificacaoVO implements Serializable {

	private static final long serialVersionUID = -8297559146638161800L;

	@Override
	public int hashCode() {
		if (getProntuario() != null && getPacCodigo() != null
				&& getNome() != null) {
			return getProntuario().intValue() * getPacCodigo().intValue()
					* getNome().length() * 8;
		} else {
			return super.hashCode();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (getProntuario() != null && getPacCodigo() != null
				&& getNome() != null) {
			return getProntuario().equals(
					((RelatorioEtiquetasIdentificacaoSubVO) obj)
							.getProntuario())
					&& getPacCodigo().equals(
							((RelatorioEtiquetasIdentificacaoSubVO) obj)
									.getPacCodigo())
					&& getNome().equals(
							((RelatorioEtiquetasIdentificacaoSubVO) obj)
									.getNome());
		} else {
			return super.equals(obj);
		}
	}

}