package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.io.Serializable;
import java.util.List;

public interface RegistroCsv {

	public interface CamposEnum
			extends
				Serializable {

		public abstract int getIndice();

		public abstract String getDescricao();
	}

	public abstract String obterNomeTemplate();

	public abstract List<String> obterTitulosComoLista();

	public abstract List<Object> obterRegistrosComoLista();
}
