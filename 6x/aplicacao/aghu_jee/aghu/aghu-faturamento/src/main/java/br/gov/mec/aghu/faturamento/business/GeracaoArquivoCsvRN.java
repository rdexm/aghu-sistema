package br.gov.mec.aghu.faturamento.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.exception.FaturamentoDebugCode;
import br.gov.mec.aghu.faturamento.stringtemplate.GeradorRegistroCsvArquivo;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.RegistroCsvContasPeriodo;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.GeradorRegistroCsv;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroCsv;
import br.gov.mec.aghu.faturamento.vo.BuscaContaVO;

/**
 * @author gandriotti
 *
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class GeracaoArquivoCsvRN extends AbstractGeracaoArquivoRN {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8449626929596878955L;
	protected static final String EXTENSAO_ARQUIVO_CSV = ".CSV";
	protected static final boolean IS_CHARSET_ISO = true;
	
	
	public GeracaoArquivoCsvRN() {
		super(new GeracaoArquivoLog());
	}
	
	//TODO migracao remover este contrutor
	public GeracaoArquivoCsvRN(final GeracaoArquivoLog logger) {
		super(logger);

		if (logger == null) {
			throw new IllegalArgumentException("Parametro logger nao informado!!!");
		}
	}
	
	protected GeradorRegistroCsv getGeracaoRegistroCsv() {
		return GeradorRegistroCsvArquivo.getInstance();
	}

	
	protected static List<Object> converterTitulosParaRegistros(final List<String> titulos) {

		List<Object> result = null;

		result = new LinkedList<Object>();
		for (String t : titulos) {
			result.add(t);
		}

		return result;
	}
	

	protected static RegistroCsv obterRegistroTitulo(final RegistroCsv baseReg) {
		final List<Object> dados = converterTitulosParaRegistros(baseReg.obterTitulosComoLista());

		return new RegistroCsv() {

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

	protected RegistroCsv obterRegistro(final BuscaContaVO valor) {

		RegistroCsv result = null;
		Integer cthSeq = null;
		Integer pacProntuario = null;
		String pacNome = null;
		DominioSituacaoConta cthIndSit = null;
		String unfDesc = null;
		String intLeito = null;
		Long cthNroAih = null;
		Boolean isDesdobramento = null;
		Date cthDataInt = null;
		Date cthDataAlta = null;
		Date cthDataSms = null;
		Boolean isForaEstado = null;
		Boolean isEspCir = null;
		Boolean isCobraAih = null;
		String fcfDesc = null;
		String cthSmsStatus = null;
		String cthCodExcCrit = null;
		String iphCodDescSol = null;
		String iphCodDescReal = null;
		BigDecimal valorTotal = null;
		String caracterInternacao = null;
		String msgErro = null;
		Integer cmce = null;
		
		//atributos
		cthSeq = valor.getCthSeq();
		pacProntuario = valor.getProntuario();
		pacNome = valor.getNome();
		cthIndSit = valor.getIndSituacao();
		unfDesc = valor.getDescricao();
		intLeito = valor.getLeitoID();
		cthNroAih = valor.getNroAih();
		isDesdobramento = valor.getIsDesdobrada();
		cthDataInt = valor.getDataInternacaoAdministrativa();
		cthDataAlta = valor.getDtAltaAdministrativa();
		cthDataSms = valor.getDtEnvioSms();
		isForaEstado = valor.getIsForaEstado();
		isEspCir = valor.getIsEspCir();
		isCobraAih = valor.getIsCobraAih();
		fcfDesc = valor.getFcfDesc();
		cthSmsStatus = valor.getStatusSms();
		cthCodExcCrit = valor.getCodigo();
		iphCodDescSol = valor.getIphSsmSol();
		iphCodDescReal = valor.getIphSsmReal();
		valorTotal = valor.getValorTotal();
		caracterInternacao = valor.getCaracterInternacao();
		msgErro = valor.getMsgErro();
		cmce = valor.getCmce();
		
		result = new RegistroCsvContasPeriodo(cthSeq, pacProntuario, pacNome, cthIndSit, unfDesc, intLeito, cthNroAih, cmce, isDesdobramento, cthDataInt,
				cthDataAlta, cthDataSms, caracterInternacao, isForaEstado, isEspCir, isCobraAih, fcfDesc, cthSmsStatus, cthCodExcCrit, iphCodDescSol, iphCodDescReal,
				msgErro, valorTotal);

		return result;
	}

	protected List<RegistroCsv> obterListaRegistrosCsv(final List<BuscaContaVO> valor) {

		List<RegistroCsv> result = null;
		RegistroCsv reg = null;

		result = new LinkedList<RegistroCsv>();
		for (BuscaContaVO vo : valor) {
			reg = this.obterRegistro(vo);
			result.add(reg);
		}

		return result;
	}

	protected List<String> obterListaEntradasCsv(
			final List<BuscaContaVO> valor,
			final GeradorRegistroCsv gerador,
			final String arquivo)
			throws IOException {

		List<String> result = null;
		List<RegistroCsv> listaReg = null;
		RegistroCsv regTitulo = null;
		BuscaContaVO vo = null;
		String linha = null;
		int ndx = 0;

		listaReg = this.obterListaRegistrosCsv(valor);
		if ((listaReg != null) && !listaReg.isEmpty()) {
			result = new LinkedList<String>();
			ndx = 0;
			// adicionando linha de titulos
			regTitulo = obterRegistroTitulo(listaReg.get(0));
			listaReg.add(0, regTitulo);
			for (RegistroCsv reg : listaReg) {
				try {
					linha = gerador.obterRegistroFormatado(reg);
					result.add(linha);
				} catch (Exception e) {
					if ((ndx > 0) && (ndx <= valor.size())) {
						vo = valor.get(ndx - 1); // primeira linha eh de titulos
						this.logFile(FaturamentoDebugCode.ARQ_ERRO_GERANDO_ENTRADA_ARQUIVO, vo.getCthSeq(), vo.getProntuario(), arquivo,
								e.getLocalizedMessage());
					} else {
						this.logFile(FaturamentoDebugCode.ARQ_ERRO_GERANDO_ENTRADA_ARQUIVO, null, null, arquivo, e.getLocalizedMessage());
					}
				}
				ndx++;
			}
		}

		return result;
	}


	public URI gerarDadosEmArquivo(
			final List<BuscaContaVO> listaVO,
			final String prefixoArqCsv)
			throws IOException {

		URI result = null;
		GeradorRegistroCsv gerador = null;
		List<String> listaEntrada = null;
		int lstEntSize = 0;

		if (listaVO == null) {
			throw new IllegalArgumentException("Parametro listaVO nao informado!!!");
		}
		if (listaVO.isEmpty()) {
			throw new IllegalArgumentException();
		}
		if (prefixoArqCsv == null) {
			throw new IllegalArgumentException("Parametro prefixoArqCsv nao informado!!!");
		}
		if (StringUtils.isBlank(prefixoArqCsv)) {
			throw new IllegalArgumentException();
		}
		gerador = this.getGeracaoRegistroCsv();
		result = obterURIArquivo(prefixoArqCsv.trim(), EXTENSAO_ARQUIVO_CSV);
		listaEntrada = this.obterListaEntradasCsv(listaVO, gerador, prefixoArqCsv);
		lstEntSize = listaEntrada != null
				? listaEntrada.size() - 1 // entrada com os titulos
				: 0;
		if (lstEntSize != listaVO.size()) {
			this.logFile(FaturamentoDebugCode.ARQ_ERRO_GERACAO_ENTRADA_REG, prefixoArqCsv, Integer.valueOf(listaVO.size()), Integer.valueOf(lstEntSize));
		}
		if (listaEntrada != null) {
			this.gravarListaEntradasEmArquivo(result, listaEntrada, IS_CHARSET_ISO);
		}

		return result;
	}
}
