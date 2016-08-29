package br.gov.mec.aghu.compras.contaspagar.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.contaspagar.interfaces.IRegistroCsv;

public abstract class AbstractRegistroCsv implements IRegistroCsv {

	private static final String TO_STRING_TOKEN_SEP = " ## ";
	public static final String EMPTY_STR = "";
	private static final String NEWLINE = System.getProperty("line.separator");
	private static final Log LOG = LogFactory.getLog(AbstractRegistroCsv.class);
	protected static final String NOME_TEMPLATE_LINHA_PONTO_VIRGULA = "csv_linha_ponto_virgula";

	protected static interface CampoParaDado<T> {

		public abstract T obterDado(CamposEnum campo);
	}

	private final String nomeTemplate;

	public abstract CamposEnum[] obterCampos();

	protected Object obterRegistro(final CamposEnum campo) {
		return getField(this, campo.getCampo());
	}

	protected void definirRegistro(final CamposEnum campo, Object value) {
		setField(this, campo.getCampo(), value);
	}

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

	protected static <T> List<T> iterarCampos(final CampoParaDado<T> map,
			CamposEnum[] campos) {

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
					throw new IllegalArgumentException(
							"Campo informado na posicao " + i
									+ " eh nullo, lista de registros: "
									+ Arrays.toString(camposOrd));
				}
				// Permite que campos com titulo nulo sejam omitidos
				if (c.getDescricao() != null) {
					dado = map.obterDado(c);
					result.add(dado);
				}
			}
		}

		return result;
	}

	protected AbstractRegistroCsv(final String nomeTemplate) {

		super();

		if (nomeTemplate == null) {
			throw new IllegalArgumentException(
					"Parametro nomeTemplate nao informado!!!");
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

	protected static List<String> internObterTitulosComoLista(
			CamposEnum[] campos) {

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
					// throw new
					// IllegalArgumentException("Registro para campo: ["
					// + campo.getDescricao() + "] eh nullo");
					result = EMPTY_STR;
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
		// titulos
		titulos = this.obterTitulosComoLista();
		if ((titulos != null) && !titulos.isEmpty()) {
			for (Object o : titulos) {
				result.append(o + TO_STRING_TOKEN_SEP);
			}
		} else {
			result.append("sem titulos");
		}
		result.append(NEWLINE);
		// dados
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

	public static final Object getField(Object instance, String field) {
		if (instance == null) {
			return null;
		}
		try {
			//return BeanUtils.getProperty(instance, field);
			 Field f = instance.getClass().getDeclaredField(field);
			 f.setAccessible(true);
			 return f.get(instance);
		} catch (SecurityException | IllegalArgumentException
				| IllegalAccessException | NoSuchFieldException e) {
			// TODO Auto-generated catch block
			LOG.error("Falha para pegar valor do atributo por reflexao", e);
			return null;
		}

	}

	public void copiarField(Object origem, String field) {
		copyField(origem, this, field);
	}

	public static void copyField(Object origem, Object destino, String field) {
		try {
			Field fo = origem.getClass().getDeclaredField(field);
			Field fd = destino.getClass().getDeclaredField(field);
			fo.setAccessible(true);
			fd.setAccessible(true);
			fd.set(destino, fo.get(origem));
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			LOG.error("Falha para copiar valor do atributo por reflexao", e);
		}
	}

	public static void setField(Object instance, String field, Object value) {
		try {
			Field f = instance.getClass().getDeclaredField(field);
			f.setAccessible(true);
			f.set(instance, value);
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			LOG.error("Falha para definir valor do atributo por reflexao", e);
		}
	}

}
