package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.util.Date;

public interface RegistroAihRegistroCivil
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		NUMERO_DN("numero_dn"),
		NOME_RN("nome_rn"),
		RS_CART("rs_cart"),
		LIVRO_RN("livro_rn"),
		FOLHA_RN("folha_rn"),
		TERMO_RN("termo_rn"),
		DT_EMIS_RN("dt_emis_rn"),
		LINHA("linha"),
		MATRICULA("matricula"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract Long getNumeroDn();

	public abstract String getNomeRn();

	public abstract String getRsCart();

	public abstract String getLivroRn();

	public abstract Short getFolhaRn();

	public abstract Integer getTermoRn();

	public abstract Date getDtEmisRn();

	public abstract Short getLinha();

	public abstract String getMatricula();
}
