package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoMapa;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelSeqCodbarraRedomeDAO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaRedomeVO;
import br.gov.mec.aghu.impressao.ISistemaImpressaoCUPS;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelSeqCodbarraRedome;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class EtiquetasRedomeON extends BaseBusiness {

	@Inject
	private AelSeqCodbarraRedomeDAO aelSeqCodbarraRedomeDAO;

	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private EtiquetasON etiquetasON;

	@EJB
	private ISistemaImpressaoCUPS sistemaImpressaoCups;

	@EJB
	private IAdministracaoFacade administracaoFacade;

	@EJB
	private ItemSolicitacaoExameRN itemSolicitacaoExameRN;

	private static final Log LOG = LogFactory.getLog(EtiquetasRedomeON.class);

	private static final String UP_XA = "^XA";
	private static final String UP_FS = "^FS";
	private static final String UP_PRA = "^PRA";
	private static final String UP_XA_IDR_TXT_XZ = "^XA^IDR:*.TXT^XZ";
	private static final String BARRA_ECOMERCIAL = "\\&";
	private static final char NEW_LINE = 10;

	private static final Integer NUMERO_VEZES_IMPRESSAO_REDOME = 2;
	private static final DominioTipoImpressaoMapa TIPO_IMPRESSAO_REDOME = DominioTipoImpressaoMapa.S;

	public void reimprimirAmostraRedome(AghUnidadesFuncionais unidadeExecutora, final Integer soeSeq, final Short seqp, final String nomeImpressora) throws BaseException {

		if (nomeImpressora == null) {
			throw new ApplicationBusinessException(EtiquetasON.EtiquetasONExceptionCode.ERRO_IMPRESSORA_ETIQUETA_NAO_ENCONTRADA, Severity.INFO);
		}

		if (unidadeExecutora == null || soeSeq == null || seqp == null) {
			LOG.error("soeSeq ou seqp nulo!");
		}

		unidadeExecutora = aghuFacade.obterAghUnidFuncionaisPeloId(unidadeExecutora.getSeq());

		final AelSolicitacaoExames solicitacaoExame = this.getExamesFacade().obterAelSolicitacaoExamePorChavePrimaria(soeSeq);

		gerarEtiquetasRedome(solicitacaoExame, unidadeExecutora, nomeImpressora, true, null,false);
	}


	/**
	 * ORADB AELC_ETIQ_REDOME (#48567)
	 *
	 * Gera etiquetas para amostras de exames REDOME
	 * @param isSolicitacaoNova 
	 *
	 */
	public void gerarEtiquetasRedome(	AelSolicitacaoExames solicitacaoExame,
										 AghUnidadesFuncionais unidadeExecutora,
										 String nomeImpressora,
										 boolean isReimpressao,
										 String situacaoItemExame, boolean isSolicitacaoNova) throws BaseException {

		if (solicitacaoExame == null || unidadeExecutora == null) {
			LOG.error("Solicitacao de exame ou unidade executora inválido!");
		}

		final List<AelAmostras> amostras = this.examesFacade.buscarAmostrasPorSolicitacaoExame(solicitacaoExame, null);

		if (amostras == null || amostras.isEmpty()) {
			LOG.error("Lista de amostras inválida!");
		}

		for (final AelAmostras amostra : amostras) {

			if (amostra == null || amostra.getId() == null || amostra.getId().getSeqp() == null) {
				LOG.error("Amostra inválida. Está nula!");
			}

			if (!verificarSeImprimeEtiquetaComoRedome(	unidadeExecutora,
					solicitacaoExame,
					amostra.getId().getSeqp())) {
				// Neste caso, não tem todos os dados disponíveis então imprime etiqueta normal da amostra
				getEtiquetasON().gerarEtiquetas(solicitacaoExame, amostra.getId().getSeqp(), unidadeExecutora,
						nomeImpressora, situacaoItemExame, isSolicitacaoNova);
				continue;
			}
		}

		// Aqui serão impressos as amostras REDOME restantes, que são as amostra REDOME com todas as informações
		// disponíveis. O método de validação é o verificarSeImprimeEtiquetaComoRedome()
		imprimirEtiquetasRedome(solicitacaoExame, unidadeExecutora, nomeImpressora, isReimpressao, amostras);
	}

	private void imprimirEtiquetasRedome(AelSolicitacaoExames solicitacaoExame,
										 AghUnidadesFuncionais unidadeExecutora,
										 String nomeImpressora,
										 boolean isReimpressao,
										 final List<AelAmostras> amostras) throws BaseException {

		Integer numeroVezesImpressao = (isReimpressao) ? 1 : NUMERO_VEZES_IMPRESSAO_REDOME;

		List<ImprimeEtiquetaRedomeVO> vos = carregarInformacoesParaImprimirEtiquetaRedome(solicitacaoExame,
				unidadeExecutora,
				amostras,
				numeroVezesImpressao,
				TIPO_IMPRESSAO_REDOME);

		StringBuffer zpl = new StringBuffer();

		for (ImprimeEtiquetaRedomeVO vo : vos) {
			zpl.append(gerarZplEtiquetasRedome(vo));
		}

		// Envia ZPL para impressora Zebra instalada no CUPS.
		sistemaImpressaoCups.imprimir(zpl.toString(), nomeImpressora);

		// Gera cópia de segurança das etiquetas
		getEtiquetasON().gerarCopiaSegurancaEtiquetas(zpl, DominioNomeRelatorio.TICKET_EXAMES);
	}

	private List<ImprimeEtiquetaRedomeVO> carregarInformacoesParaImprimirEtiquetaRedome(AelSolicitacaoExames solicitacaoExame,
																						AghUnidadesFuncionais unidadeExecutora,
																						final List<AelAmostras> amostras,
																						Integer numeroVezesImpressao,
																						DominioTipoImpressaoMapa tipoImpressao) throws BaseException {

		if (solicitacaoExame == null ||
				unidadeExecutora == null ||
				amostras == null ||
				amostras.isEmpty()) {

			LOG.error("Solicitacao de exame, unidade executora ou amostras inválido!");
			return null;
		}

		List<ImprimeEtiquetaRedomeVO> listaEtiquetas = new LinkedList<>();

		for (final AelAmostras amostra : amostras) {

			if (amostra == null || amostra.getId() == null || amostra.getId().getSeqp() == null) {
				LOG.error("Amostra inválida. Está nula!");
			}

			Boolean imprimirEtiquetaRedome = verificarSeImprimeEtiquetaComoRedome(	unidadeExecutora,
					solicitacaoExame,
					amostra.getId().getSeqp());

			if (imprimirEtiquetaRedome) {
				listaEtiquetas.add(	preencherInformacoesEtiquetaRedome(solicitacaoExame,
						amostra,
						numeroVezesImpressao,
						tipoImpressao));
			}
		}

		return listaEtiquetas;
	}

	private Integer obterSeqCodBarraRedome(final AelAmostras amostra) {

		List<AelSeqCodbarraRedome> codbarraRedome = getAelSeqCodbarraRedomeDAO().obterSeqCodBarraRedome(amostra);

		if (codbarraRedome == null || codbarraRedome.size() != 1) {
			LOG.error("Código barra não encontrado ou inválido!");
		}

		return codbarraRedome.get(0).getSeq();
	}

	private ImprimeEtiquetaRedomeVO preencherInformacoesEtiquetaRedome(	AelSolicitacaoExames solicitacaoExame,
																		   AelAmostras amostra,
																		   Integer numeroVezesImpressao,
																		   DominioTipoImpressaoMapa tipoImpressao) throws BaseException {

		String numSolicitacaoFormatado = StringUtils.leftPad(solicitacaoExame.getSeq().toString(), 8, '0'); //Formata número da solicitação com 8 casas

		StringBuilder siglaFormatada = new StringBuilder();
		siglaFormatada.append(solicitacaoExame.getAtendimento().getAtendimentoPacienteExterno().getCodigoDoador())
				.append(solicitacaoExame.getAtendimento().getAtendimentoPacienteExterno().getLaboratorioExterno().getSigla());

		// Nome deverá ser quebrado com último sobrenome na segunda linha
		StringBuilder separador = new StringBuilder();
		separador.append(BARRA_ECOMERCIAL).append(NEW_LINE);

		String codigoDoadorFormatado = abreviarNomeComUltimoSobrenomeEmNovaLinha(solicitacaoExame.getAtendimento().getPaciente().getNome(), separador.toString());
		String dataColetaDoadorFormatada = DateUtil.dataToString(solicitacaoExame.getAtendimento().getAtendimentoPacienteExterno().getDtColeta(), "dd/MM/yyyy");
		String numAmostraFormatado = StringUtils.leftPad(amostra.getId().getSeqp().toString(), 3, '0'); //Formata número da solicitação com 3 casas
		String codBarrasFormatado = StringUtils.leftPad(obterSeqCodBarraRedome(amostra).toString(), 6, '0'); //Formata número da solicitação com 6 casas

		ImprimeEtiquetaRedomeVO vo = new ImprimeEtiquetaRedomeVO();

		vo.setNumeroSolicitacao(numSolicitacaoFormatado);
		vo.setTipoImpressao(tipoImpressao);
		vo.setNumeroVezesImpressao(numeroVezesImpressao);
		vo.setSigla(siglaFormatada.toString());
		vo.setCodigoDoador(codigoDoadorFormatado);
		vo.setDataColetaDoador(dataColetaDoadorFormatada);
		vo.setNumeroAmostra(numAmostraFormatado);
		vo.setCodBarras(codBarrasFormatado);

		return vo;
	}

	private String gerarZplEtiquetasRedome(final ImprimeEtiquetaRedomeVO vo) {

		final StringBuilder textoZpl = new StringBuilder(300);

		if (vo == null ||
				vo.getTipoImpressao() == null ||
				vo.getNumeroVezesImpressao() == null) {

			LOG.error("ImprimeEtiquetaRedomeVO inválido.");
		}

		if (vo.getTipoImpressao().equals(DominioTipoImpressaoMapa.S)) {
			for (int i=0; i<vo.getNumeroVezesImpressao(); i++) {
				gerarZplEtiquetasRedomeTipoS(textoZpl, vo);
			}
		} else {
			for (int i=0; i<vo.getNumeroVezesImpressao(); i++) {
				gerarZplEtiquetasRedomeTipoNaoS(textoZpl, vo);
			}
		}

		return textoZpl.toString();
	}

	private void gerarZplEtiquetasRedomeTipoS(StringBuilder textoZpl, final ImprimeEtiquetaRedomeVO vo) {

		textoZpl.append(UP_XA).append(NEW_LINE)
				.append(UP_PRA).append(NEW_LINE)
				.append("^MD15").append(NEW_LINE)
				.append("^MNY").append(NEW_LINE)
				.append("^BY1,,30").append(NEW_LINE)
				.append("^LH0,0").append(NEW_LINE)
				.append("^LL440").append(NEW_LINE)
				.append("^FO025,030").append(NEW_LINE)
				.append("^FB440,4,0,C,0").append(NEW_LINE)
				.append("^ACN").append(NEW_LINE)
				.append("^FD").append(vo.getSigla()).append(" - ").append(vo.getCodigoDoador()).append(BARRA_ECOMERCIAL).append(NEW_LINE)
				.append(vo.getDataColetaDoador()).append(BARRA_ECOMERCIAL).append(NEW_LINE)
				.append(vo.getNumeroSolicitacao()).append(' ').append(vo.getNumeroAmostra()).append(UP_FS).append(NEW_LINE)
				.append("^FO160,100^BCN,45,N,N^FD").append(vo.getCodBarras()).append(UP_FS).append(NEW_LINE)
				.append("^FO170,150^ACN ^FD").append(vo.getCodBarras()).append(UP_FS).append(NEW_LINE)
				.append("^XZ").append(NEW_LINE)
				.append(UP_XA_IDR_TXT_XZ).append(NEW_LINE);
	}

	private void gerarZplEtiquetasRedomeTipoNaoS(StringBuilder textoZpl, final ImprimeEtiquetaRedomeVO vo) {

		textoZpl.append(UP_XA).append(NEW_LINE)
				.append(UP_PRA).append(NEW_LINE)
				.append("^MD15").append(NEW_LINE)
				.append("^MNY").append(NEW_LINE)
				.append("^BY1,,30").append(NEW_LINE)
				.append("^LH0,0").append(NEW_LINE)
				.append("^LL1160").append(NEW_LINE)
				.append("^FO070,030").append(NEW_LINE)
				.append("^FB160,8,0,C,0").append(NEW_LINE)
				.append("^ABN").append(NEW_LINE)
				.append("^FD").append(vo.getSigla()).append(NEW_LINE).append(BARRA_ECOMERCIAL).append(NEW_LINE).append(BARRA_ECOMERCIAL).append(NEW_LINE)
				.append(vo.getCodigoDoador()).append(BARRA_ECOMERCIAL).append(NEW_LINE).append(BARRA_ECOMERCIAL).append(NEW_LINE)
				.append(vo.getDataColetaDoador()).append(BARRA_ECOMERCIAL).append(NEW_LINE).append(BARRA_ECOMERCIAL).append(NEW_LINE)
				.append(vo.getNumeroSolicitacao()).append(' ').append(vo.getNumeroAmostra()).append(UP_FS).append(NEW_LINE)
				.append("^FO015,025^BCR,40,N,N^FD").append(vo.getCodBarras()).append(UP_FS).append(NEW_LINE)
				.append("^XZ").append(NEW_LINE)
				.append(UP_XA_IDR_TXT_XZ).append(NEW_LINE);
	}

	public String obterNomeImpressoraEtiquetasRedome(String nomeMicro) {

		AghMicrocomputador microcomputador = administracaoFacade.obterAghMicroComputadorPorNomeOuIP(nomeMicro, null);

		if (microcomputador != null && microcomputador.getImpressoraEtiquetas() != null) {
			return microcomputador.getImpressoraEtiquetas();
		}

		if (microcomputador != null && microcomputador.getAghUnidadesFuncionais() != null) {

			List<AghImpressoraPadraoUnids> lista = aghuFacade.listarAghImpressoraPadraoUnids(
					microcomputador.getAghUnidadesFuncionais().getSeq(), TipoDocumentoImpressao.ETIQUETA_BARRAS_REDOME);

			if (!lista.isEmpty()) {
				if (lista.get(0).getImpImpressora() != null) {
					return lista.get(0).getImpImpressora().getFilaImpressora();
				}
				if (lista.get(0).getNomeImpressora() != null) {
					return lista.get(0).getNomeImpressora();
				}
			}
		}

		return null;
	}

	private String abreviarNomeComUltimoSobrenomeEmNovaLinha(String nomeCompleto, final String separador) {

		String retorno;
		// String nomeCompleto = "MARIA MARGARIDA RIBEIRO DA COSTA DOS SANTOS";
		String[] nomePalavras = StringUtils.split(nomeCompleto, " ");
		StringBuffer palavra = new StringBuffer();
		int i;
		for (i = 1; i < nomePalavras.length - 1; i++) {
			palavra.append(StringUtils.substring(nomePalavras[i], 0, 1))
					.append(". ");
		}
		if (nomePalavras.length - 1 > 0) {
			retorno = nomePalavras[0] + " " + palavra
					+ separador + nomePalavras[nomePalavras.length - 1];
		} else {
			retorno = nomePalavras[0] + " " + palavra;
		}

		return retorno;
	}

	public boolean verificarSeImprimeEtiquetaComoRedome(AghUnidadesFuncionais unidadeExecutora, final AelSolicitacaoExames solicitacaoExame, final Short seqp) throws BaseException {

		// Realiza validações necessárias para impressão da etiqueta REDOME.
		// Se tiver alguma informação indisponível retorna false.
		if (solicitacaoExame == null ||
				solicitacaoExame.getAtendimento() == null ||
				solicitacaoExame.getAtendimento().getAtendimentoPacienteExterno() == null ||
				solicitacaoExame.getAtendimento().getAtendimentoPacienteExterno().getLaboratorioExterno() == null ||
				solicitacaoExame.getAtendimento().getPaciente() == null ||
				solicitacaoExame.getAtendimento().getPaciente().getNome() == null) {

			return false;
		}

		return verificarSeAmostraRedome(unidadeExecutora, solicitacaoExame.getSeq(), seqp);
	}

	private boolean verificarSeAmostraRedome(AghUnidadesFuncionais unidadeExecutora, final Integer soeSeq, final Short seqp) throws BaseException {

		final List<AelAmostraItemExames> listaAmostraItensExames = getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesPorAmostra(soeSeq, seqp.intValue());

		if (listaAmostraItensExames == null) {
			LOG.error("listaAmostraItensExames está nulo!");
		}

		for (AelAmostraItemExames amostraItemExame : listaAmostraItensExames) {

			if (amostraItemExame == null) {
				LOG.error("amostraItemExame está nulo!");
			}

			if (getItemSolicitacaoExameRN().verificarSeExameSendoSolicitadoRedome(amostraItemExame.getAelItemSolicitacaoExames(), unidadeExecutora)) {
				return true;
			}
		}

		return false;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public AelSeqCodbarraRedomeDAO getAelSeqCodbarraRedomeDAO() {
		return aelSeqCodbarraRedomeDAO;
	}

	public void setAelSeqCodbarraRedomeDAO(AelSeqCodbarraRedomeDAO aelSeqCodbarraRedomeDAO) {
		this.aelSeqCodbarraRedomeDAO = aelSeqCodbarraRedomeDAO;
	}

	public AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}

	public void setAelAmostraItemExamesDAO(AelAmostraItemExamesDAO aelAmostraItemExamesDAO) {
		this.aelAmostraItemExamesDAO = aelAmostraItemExamesDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	public EtiquetasON getEtiquetasON() {
		return etiquetasON;
	}

	public void setEtiquetasON(EtiquetasON etiquetasON) {
		this.etiquetasON = etiquetasON;
	}

	private ItemSolicitacaoExameRN getItemSolicitacaoExameRN() {
		return itemSolicitacaoExameRN;
	}
}