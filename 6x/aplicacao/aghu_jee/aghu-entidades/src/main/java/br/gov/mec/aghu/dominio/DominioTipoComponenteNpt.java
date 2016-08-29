package br.gov.mec.aghu.dominio;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * #989 - Calcular Nutrição Parenteral Total: VO Tipo de Componente da Tela
 * 
 * @author aghu
 *
 */
public enum DominioTipoComponenteNpt implements Dominio {

	TEMPO_INFUSAO_SOLUCAO, GOTEJO_SOLUCAO, GOTEJO_LIPIDIOS, TEMPO_INFUSAO_LIPIDIOS, VOL_LIPIDIOS_10, VOL_LIPIDIOS_20, VOL_AA_10, VOL_AA_PED_10, VOL_NACL_20, VOL_KCL, VOL_K3PO4, RECALCULA, VOL_MGSO4, VOL_GLUCO_CA, VOL_OLIGO, VOL_OLIGO_PED, VOL_ACET_ZN, VOL_GLICOSE_50, VOL_GLICOSE_10, VOL_HEPARINA, VOLUME, LIPIDIOS, AMINOACIDOS, TIG, HEPARINA;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return this.toString();
	}

	public static DominioTipoComponenteNpt getInstance(final String valor) {
		if (StringUtils.isBlank(valor)) {
			throw new IllegalArgumentException();
		}
		return DominioTipoComponenteNpt.valueOf(StringUtils.trim(valor).toUpperCase());
	}

}
