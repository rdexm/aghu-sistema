package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

public interface RegistroAihInternacao
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		NU_PRONTUARIO("nu_prontuario"),
		NU_ENFERMARIA("nu_enfermaria"),
		NU_LEITO("nu_leito"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract Long getNuProntuario();

	public abstract String getNuEnfermaria();

	public abstract String getNuLeito();
}
