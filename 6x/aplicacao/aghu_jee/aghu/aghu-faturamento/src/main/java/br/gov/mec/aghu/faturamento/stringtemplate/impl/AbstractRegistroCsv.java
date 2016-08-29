package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroCsv;

public abstract class AbstractRegistroCsv
		implements
		RegistroCsv {

	private static final String TO_STRING_TOKEN_SEP = " ## ";
	private static final String NEWLINE = System.getProperty("line.separator");

	protected static interface CampoParaDado<T> {

		public abstract T obterDado(CamposEnum campo);
	}

	private final String nomeTemplate;

	protected abstract CamposEnum[] obterCampos();

	protected abstract Object obterRegistro(CamposEnum campo);

	protected static CamposEnum[] ordenarCampos(final CamposEnum[] campos) {

		CamposEnum[] result = null;

		result = Arrays.copyOf(campos, campos.length);
		Arrays.sort(result, new Comparator<CamposEnum>() {

			@Override
			public int compare(final CamposEnum o1, final CamposEnum o2) {

				return o1.getIndice() - o2.getIndice();
			}

		});

		return result;
	}

	protected static <T> List<T> iterarCampos(final CampoParaDado<T> map, CamposEnum[] campos) {

		List<T> result = null;
		T dado = null;
		CamposEnum c = null;
		CamposEnum[] camposOrd = null;

		if (map == null) {
			throw new IllegalArgumentException();
		}
		result = new LinkedList<T>();
		if ((campos != null) && (campos.length > 0)) {
			camposOrd = ordenarCampos(campos);
			for (int i = 0; i < camposOrd.length; i++) {
				c = camposOrd[i];
				if (c == null) {
					throw new IllegalArgumentException("Campo informado na posicao " + i + " eh nullo, lista de registros: " + Arrays.toString(camposOrd));
				}
				dado = map.obterDado(c);
				result.add(dado);
			}
		}

		return result;
	}

	protected AbstractRegistroCsv(final String nomeTemplate) {

		super();

		if (nomeTemplate == null) {
			throw new IllegalArgumentException("Parametro nomeTemplate nao informado!!!");
		}
		if (nomeTemplate.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.nomeTemplate = nomeTemplate.trim();
	}

	@Override
	public String obterNomeTemplate() {

		return this.nomeTemplate;
	}
	
	protected static List<String> internObterTitulosComoLista(CamposEnum[] campos) {
		
		List<String> result = null;
		CampoParaDado<String> map = null;

		map = new CampoParaDado<String>() {

			@Override
			public String obterDado(final CamposEnum campo) {

				return campo.getDescricao();
			}
		};
		result = iterarCampos(map, campos);

		return result;
	}

	@Override
	public List<String> obterTitulosComoLista() {

		List<String> result = null;

		result = internObterTitulosComoLista(this.obterCampos());

		return result;
	}

	@Override
	public List<Object> obterRegistrosComoLista() {

		List<Object> result = null;
		CampoParaDado<Object> map = null;

		map = new CampoParaDado<Object>() {

			@Override
			public Object obterDado(final CamposEnum campo) {

				Object result = null;

				result = AbstractRegistroCsv.this.obterRegistro(campo);
				if (result == null) {
					throw new IllegalArgumentException("Registro para campo: [" + campo.getDescricao() + "] eh nullo");
				}

				return result;
			}
		};
		result = iterarCampos(map, this.obterCampos());

		return result;
	}

	@Override
	public String toString() {

		StringBuffer result = null;
		List<String> titulos = null;
		List<Object> dados = null;

		result = new StringBuffer();
		//titulos
		titulos = this.obterTitulosComoLista();
		if ((titulos != null) && !titulos.isEmpty()) {
			for (Object o : titulos) {
				result.append(o + TO_STRING_TOKEN_SEP);
			}
		} else {
			result.append("sem titulos");
		}
		result.append(NEWLINE);
		//dados
		dados = this.obterRegistrosComoLista();
		if ((dados != null) && !dados.isEmpty()) {
			for (Object o : dados) {
				result.append(o + TO_STRING_TOKEN_SEP);
			}
		} else {
			result.append("sem dados");
		}
		result.append(NEWLINE);

		return result.toString();
	}

}
