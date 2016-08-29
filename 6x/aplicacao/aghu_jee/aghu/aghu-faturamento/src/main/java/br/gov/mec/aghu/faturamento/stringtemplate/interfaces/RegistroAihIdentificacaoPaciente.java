package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.math.BigInteger;
import java.util.Date;

import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTipoDocPac;

public interface RegistroAihIdentificacaoPaciente
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		NM_PACIENTE("nm_paciente"),
		DT_NASC_PAC("dt_nasc_pac"),
		SEXO_PAC("sexo_pac"),
		RACA_COR("raca_cor"),
		NM_MAE_PAC("nm_mae_pac"),
		NM_RESP_PAC("nm_resp_pac"),
		TP_DOC_PAC("tp_doc_pac"),
		ETNIA_INDIGENA("etnia_indigena"),
		NU_CNS("nu_cns"),
		NAC_PAC("nac_pac"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract String getNmPaciente();

	public abstract Date getDtNascPac();

	public abstract String getSexoPac();

	public abstract String getRacaCor();

	public abstract String getNmMaePac();

	public abstract String getNmRespPac();

	public abstract DominioTipoDocPac getTpDocPac();

	public abstract Integer getEtniaIndigena();

	public abstract BigInteger getNuCns();

	public abstract Short getNacPac();
}
