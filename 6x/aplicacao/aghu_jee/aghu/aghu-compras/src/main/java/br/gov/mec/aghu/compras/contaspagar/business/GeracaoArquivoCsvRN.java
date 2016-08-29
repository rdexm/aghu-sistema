package br.gov.mec.aghu.compras.contaspagar.business;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.contaspagar.impl.AbstractRegistroCsv;
import br.gov.mec.aghu.compras.contaspagar.interfaces.IRegistroCsv;
import br.gov.mec.aghu.compras.contaspagar.interfaces.IRegistroCsv.CamposEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * @author gandriotti
 *
 */
@Stateless
public class GeracaoArquivoCsvRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4467456695207690887L;
	
	protected static final String EXTENSAO_ARQUIVO_CSV = ".CSV";
	protected static final boolean IS_CHARSET_ISO = true;
	protected static final boolean IS_NOT_CHARSET_ISO = false;
	
	private static final Log LOG = LogFactory.getLog(GeracaoArquivoCsvRN.class);
	
	@EJB
	private GeracaoArquivoRN geracaoArquivoRN;
	

	protected static List<Object> converterTitulosParaRegistros(
			final List<String> titulos) {

		List<Object> result = null;

		result = new LinkedList<Object>();
		for (String t : titulos) {
			result.add(t);
		}

		return result;
	}

	protected GeradorRegistroCsv getGeracaoRegistroCsv() {
		return GeradorRegistroCsvArquivo.getInstance();
	}

	protected static IRegistroCsv obterRegistroTitulo(final IRegistroCsv baseReg) {
		final List<Object> dados = converterTitulosParaRegistros(baseReg
				.obterTitulosComoLista());

		return new IRegistroCsv() {

			@Override
			public List<String> obterTitulosComoLista() {
				return baseReg.obterTitulosComoLista();
			}

			@Override
			public List<Object> obterRegistrosComoLista() {
				return dados;
			}

			@Override
			public String obterNomeTemplate() {
				return baseReg.obterNomeTemplate();
			}

		};
	}

	protected IRegistroCsv obterRegistro(Class<? extends AbstractRegistroCsv> clazz, final BaseBean valor) {
		//throws InstantiationException, IllegalAccessException
		AbstractRegistroCsv result = null;
		try {
			result = (AbstractRegistroCsv) clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		}
		
		for(CamposEnum campo : result.obterCampos()){
			result.copiarField(valor, campo.getCampo());
		}

		return result;
	}

	protected List<IRegistroCsv> obterListaRegistrosCsv(Class<? extends AbstractRegistroCsv> clazz, final List<? extends BaseBean> valor) {
		List<IRegistroCsv> result = null;
		IRegistroCsv reg = null;

		result = new LinkedList<IRegistroCsv>();
		for (BaseBean vo : valor) {
			reg = this.obterRegistro(clazz, vo);
			result.add(reg);
		}

		return result;
	}

	protected List<String> obterListaEntradasCsv(
			Class<? extends AbstractRegistroCsv> clazz, final List<? extends BaseBean> valor,
			final GeradorRegistroCsv gerador, final String arquivo,
			final String titulo) throws IOException,
			ApplicationBusinessException {

		List<String> result = null;
		List<IRegistroCsv> listaReg = null;
		IRegistroCsv regTitulo = null;
		// BuscaDivHospNatDespVO vo = null;
		String linha = null;

		
		listaReg = this.obterListaRegistrosCsv(clazz, valor);
	

		if ((listaReg != null) && !listaReg.isEmpty()) {
			result = new LinkedList<String>();
			// adicionando linha de titulos
			regTitulo = obterRegistroTitulo(listaReg.get(0));
			if (titulo != null) {
				result.add(titulo);
			}
			listaReg.add(0, regTitulo);

			for (IRegistroCsv reg : listaReg) {

				linha = gerador.obterRegistroFormatado(reg);
				result.add(linha);

			}
		}

		return result;
	}

	public static String obterNomeArquivo(final String nomeArquivo,
			final boolean removerUltimoIfem) {

		String result = null;
		int ifemNdx = 0;
		int pontoNdx = 0;

		if (nomeArquivo == null) {
			throw new IllegalArgumentException(
					"Parametro nomeArquivo nao informado!!!");
		}

		result = nomeArquivo;
		if (removerUltimoIfem) {
			pontoNdx = result.lastIndexOf('.');
			ifemNdx = result.lastIndexOf('-');
			if ((ifemNdx >= 0) && (ifemNdx < pontoNdx)) {
				result = result.substring(0, ifemNdx)
						+ result.substring(pontoNdx, result.length());
			}
		}

		return result;
	}

	public static String obterNomeArquivo(final URI uriArquivo,
			final boolean removerUltimoIfem) {

		String result = null;
		File fileArq = null;

		if (uriArquivo == null) {
			throw new IllegalArgumentException(
					"Parametro uriArquivo nao informado!!!");
		}

		fileArq = new File(uriArquivo);
		result = obterNomeArquivo(fileArq, removerUltimoIfem);

		return result;
	}

	public static String obterNomeArquivo(final File fileArquivo,
			final boolean removerUltimoIfem) {

		String result = null;
		String nomeArquivo = null;

		// check
		if (fileArquivo == null) {
			throw new IllegalArgumentException(
					"Parametro fileArquivo nao informado!!!");
		}
		// algo
		nomeArquivo = fileArquivo.getName();
		result = obterNomeArquivo(nomeArquivo, removerUltimoIfem);

		return result;
	}

	public URI gerarDadosEmArquivo(final Class<? extends AbstractRegistroCsv> clazz, final List<? extends BaseBean> listaVO,
			final String prefixoArqCsv, final String titulo)
			throws ApplicationBusinessException, IOException {

		URI result = null;
		GeradorRegistroCsv gerador = null;
		List<String> listaEntrada = null;

		if (listaVO == null) {
			throw new ApplicationBusinessException(
					"Parametro listaVO nao informado!!!", Severity.ERROR);

		}
		if (listaVO.isEmpty()) {
			throw new ApplicationBusinessException("listaVO vazia vazia",
					Severity.ERROR);

		}
		if (prefixoArqCsv == null) {
			throw new ApplicationBusinessException(
					"Parametro prefixoArqCsv nao informado", Severity.ERROR);

		}
		if (StringUtils.isBlank(prefixoArqCsv)) {
			throw new ApplicationBusinessException(
					"Parametro prefixoArqCsv nao informado", Severity.ERROR);

		}
		gerador = this.getGeracaoRegistroCsv();
		result = geracaoArquivoRN.obterURIArquivo(prefixoArqCsv.trim(), EXTENSAO_ARQUIVO_CSV);
		listaEntrada = this.obterListaEntradasCsv(clazz, listaVO, gerador,
				prefixoArqCsv, titulo);

		if (listaEntrada != null) {
			geracaoArquivoRN.gravarListaEntradasEmArquivo(result, listaEntrada, IS_CHARSET_ISO);
		}

		return result;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}
