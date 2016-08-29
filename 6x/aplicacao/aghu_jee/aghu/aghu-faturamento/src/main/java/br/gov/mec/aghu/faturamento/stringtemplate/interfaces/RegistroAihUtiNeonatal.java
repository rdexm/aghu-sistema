package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioSaidaUtineo;

public interface RegistroAihUtiNeonatal
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		SAIDA_UTINEO("saida_utineo"),
		PESO_UTINEO("peso_utineo"),
		MESGEST_UTINEO("mesgest_utineo"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract DominioSaidaUtineo getSaidaUtineo();

	public abstract Integer getPesoUtineo();

	public abstract Integer getMesgestUtineo();
}
