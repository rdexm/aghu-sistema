package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

public interface RegistroAihEnderecoPaciente
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		TP_LOGRADOURO("tp_logradouro"),
		LOGR_PAC("logr_pac"),
		NU_END_PAC("nu_end_pac"),
		COMPL_END_PAC("compl_end_pac"),
		BAIRRO_PAC("bairro_pac"),
		COD_MUN_END_PAC("cod_mun_end_pac"),
		UF_PAC("uf_pac"),
		CEP_PAC("cep_pac"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract Short getTpLogradouro();

	public abstract String getLogrPac();

	public abstract Integer getNuEndPac();

	public abstract String getComplEndPac();

	public abstract String getBairroPac();

	public abstract Integer getCodMunEndPac();

	public abstract String getUfPac();

	public abstract Integer getCepPac();
}
