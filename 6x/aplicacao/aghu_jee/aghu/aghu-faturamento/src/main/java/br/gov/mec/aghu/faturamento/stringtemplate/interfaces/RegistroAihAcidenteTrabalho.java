package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

public interface RegistroAihAcidenteTrabalho
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		CNPJ_EMPREG("cnpj_empreg"),
		CBOR("cbor"),
		CNAER("cnaer"),
		TP_VINCPREV("tp_vincprev"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract Long getCnpjEmpreg();

	public abstract Integer getCbor();

	public abstract Integer getCnaer();

	public abstract Integer getTpVincprev();
}
