package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.math.BigInteger;

public interface RegistroAihDocumentoPaciente
		extends
			RegistroAih {

	public enum NomeAtributoTemplate implements
			NomeAtributoEnum {

		NU_DOC_PAC("nu_doc_pac"), ;

		private final String nome;

		private NomeAtributoTemplate(final String nome) {

			this.nome = nome;
		}

		@Override
		public String getNome() {

			return this.nome;
		}
	}

	public abstract BigInteger getNuDocPac();
}
