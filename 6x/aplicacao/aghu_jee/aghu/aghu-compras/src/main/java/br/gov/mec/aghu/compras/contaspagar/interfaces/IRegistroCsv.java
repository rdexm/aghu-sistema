package br.gov.mec.aghu.compras.contaspagar.interfaces;

import java.io.Serializable;
import java.util.List;

public interface IRegistroCsv {

	public static final String NOVA_LINHA_ARQ_SUS_EQ_CR_LF = "\r\n";
	
	public interface CamposEnum	extends	Serializable {

		public abstract int getIndice();

		public abstract String getDescricao();
		public abstract String getCampo();
	}

	public abstract String obterNomeTemplate();

	public abstract List<String> obterTitulosComoLista();

	public abstract List<Object> obterRegistrosComoLista();

	
}

