package br.gov.mec.aghu.compras.business;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioDiaSemanaMes;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.DadosItemAptoProgramacaoEntregaVO;
import br.gov.mec.aghu.estoque.vo.GrupoMaterialNumeroSolicitacaoVO;
import br.gov.mec.aghu.estoque.vo.QuantidadesVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

/**
 * Métodos auxiliares das Regras de negócio de #5554 – Programar automático parcelas de entrega da autorização de fornecimento
 * 
 * @author luismoura
 * 
 */
@Stateless
public class ProgramacaoAutomaticaON extends BaseBusiness {
	private static final long serialVersionUID = -4022277895751272349L;
	
	private static final Log LOG = LogFactory.getLog(ProgramacaoAutomaticaON.class);

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

	@Inject
	private ScoMaterialDAO scoMaterialDAO;

	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;

	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO scoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	/**
	 * Busca data do dia favorável anterior à data da primeira parcela
	 * 
	 * @param dataPrimeiraParcela
	 * @param diaFavoravel
	 * @param diaSemanaEntrega
	 * @return
	 */
	public Date obterDataDiaFavoravel(Date dataPrimeiraParcela, Integer diaFavoravel, Integer diaSemanaEntrega) {

		Calendar calendarNovaDataPrimeiraParcela = Calendar.getInstance();
		calendarNovaDataPrimeiraParcela.setTime(dataPrimeiraParcela);

		// a Data Primeira Parcela será o último dia da semana, anterior à Data da Primeira
		// Parcela, cujo número seja igual ao diaFavoravelEntrega (sendo Domingo = 1, Segunda Feira = 2, ... Sábado = 7).

		while (diaSemanaEntrega.intValue() != diaFavoravel.intValue()) {
			calendarNovaDataPrimeiraParcela.add(Calendar.DAY_OF_YEAR, -1);
			diaSemanaEntrega = calendarNovaDataPrimeiraParcela.get(Calendar.DAY_OF_WEEK);
		}

		Date novaDataPrimeiraParcela = calendarNovaDataPrimeiraParcela.getTime();
		return novaDataPrimeiraParcela;
	}

	/**
	 * Busca o dia favorável para a entrega
	 * 
	 * @param numeroFornecedor
	 * @param matCodigo
	 * @return
	 */
	public Integer obterDiaFavoravel(Integer numeroFornecedor, Integer matCodigo) {
		//Byte diaFavoravelFornecedor = this.getScoFornecedorDAO().buscarDiaFavoravel(numeroFornecedor);
		Byte diaFavoravelFornecedor = this.scoFornecedorDAO.buscarDiaFavoravel(numeroFornecedor);
		//DominioDiaSemanaMes diaFavoravelMaterial = this.getScoMaterialDAO().buscarDiaFavoravel(matCodigo);
		DominioDiaSemanaMes diaFavoravelMaterial = this.scoMaterialDAO.buscarDiaFavoravel(matCodigo);

		Integer diaFavoravel = null;
		if (diaFavoravelFornecedor != null) {
			diaFavoravel = diaFavoravelFornecedor.intValue();
		} else if (diaFavoravelMaterial != null) {
			diaFavoravel = diaFavoravelMaterial.getCodigo();
		}

		return diaFavoravel;
	}

	/**
	 * Busca o fornecedor padrão nos parametros
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected Integer obterFornecedorPadrao() throws ApplicationBusinessException {
		AghParametros parametro = this.obterParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		if (parametro == null || parametro.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE, AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		}
		return parametro.getVlrNumerico().intValue();
	}

	/**
	 * Busca o fornecedor padrão nos parametros
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal obterPercentualReposicaoItensEspacoFisico() throws ApplicationBusinessException {
		AghParametros parametro = this.obterParametro(AghuParametrosEnum.P_PERCENTUAL_REPOSICAO_ITENS_ESPACO_FISICO);
		if (parametro == null || parametro.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE, AghuParametrosEnum.P_PERCENTUAL_REPOSICAO_ITENS_ESPACO_FISICO);
		}
		return parametro.getVlrNumerico();
	}

	/**
	 * Busca o almoxarifado referência nos parametros
	 * 
	 * C3 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public SceAlmoxarifado obterAlmoxarifadoReferencia() throws ApplicationBusinessException {
		AghParametros parametro = this.obterParametro(AghuParametrosEnum.P_ALMOX_REFERENCIA);
		if (parametro == null || parametro.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE, AghuParametrosEnum.P_ALMOX_REFERENCIA);
		}
		return this.getEstoqueFacade().obterSceAlmoxarifadoPorChavePrimaria(parametro.getVlrNumerico().shortValue());
	}

	/**
	 * Busca o data de competencia padrão nos parametros
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected Date obterCompetenciaPadrao() throws ApplicationBusinessException {
		AghParametros parametro = this.obterParametro(AghuParametrosEnum.P_COMPETENCIA);
		if (parametro == null || parametro.getVlrData() == null) {
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE, AghuParametrosEnum.P_COMPETENCIA);
		}
		return parametro.getVlrData();
	}

	/**
	 * Busca o data de competencia padrão nos parametros
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected boolean obterParametroAlmoxUnico() throws ApplicationBusinessException {
		AghParametros parametro = this.obterParametro(AghuParametrosEnum.P_ALMOX_UNICO);
		if (parametro == null || parametro.getVlrTexto() == null) {
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE, AghuParametrosEnum.P_ALMOX_UNICO);
		}
		return DominioSimNao.S.toString().equalsIgnoreCase(parametro.getVlrTexto().trim());
	}

	/**
	 * Busca o número de dias que uma entrega pode ser feita antes do vencimento (P_NDIAS_ENTREGA_ANTES_VENCIMENTO)
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Integer obterNumeroDiasEntregueAntesVencimento() throws ApplicationBusinessException {
		AghParametros parametro = this.obterParametro(AghuParametrosEnum.P_NDIAS_ENTREGA_ANTES_VENCIMENTO);
		if (parametro == null || parametro.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE, AghuParametrosEnum.P_NDIAS_ENTREGA_ANTES_VENCIMENTO);
		}
		return parametro.getVlrNumerico().intValue();
	}

	/**
	 * Busca um parâmetro de sistema
	 * 
	 * @param nome
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected AghParametros obterParametro(AghuParametrosEnum nome) throws ApplicationBusinessException {
		return this.getParametroFacade().buscarAghParametro(nome);

	}

	/**
	 * Busca ScoItemAutorizacaoForn ainda não assinada
	 * 
	 * C4 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param numero
	 * @param nroComplemento
	 * @param lctNumero
	 * @return
	 */
	public Integer obterSaldoNaoAssinado(Integer numero, Short nroComplemento, Integer lctNumero) {

		Long totalAssinado = this.scoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO.buscarTotalScoItemAutorizacaoFornAssinado(numero, nroComplemento,
				lctNumero);

		ScoItemAutorizacaoForn scoItemAutorizacaoForn = this.scoItemAutorizacaoFornDAO.buscarScoItemAutorizacaoForn(numero, nroComplemento, lctNumero);

		int assinado = totalAssinado != null ? totalAssinado.intValue() : 0;
		int solicitado = scoItemAutorizacaoForn != null ? scoItemAutorizacaoForn.getQtdeSolicitada() : 0;
		return solicitado - assinado;
	}

	/**
	 * Calcula o SaldoAdiantamentoAF
	 * 
	 * @param afnNumero
	 * @param numero
	 * @return
	 */
	public Integer obterSaldoAdiantamentoAF(Integer afnNumero, Integer numero) {
		// SaldoAdiantamentoAF = quantidade – quantidadeDevolvida
		Long quantidade = 0L;
		Long quantidadeDevolvida = 0L;
		{
			QuantidadesVO qtds = this.getEstoqueFacade().obterQuantidadeQuantidadeDevolvidaSceItemEntrSaidSemLicitacao(afnNumero, numero);
			if (qtds != null) {
				quantidade = qtds.getQuantidade1() != null ? qtds.getQuantidade1() : 0;
				quantidadeDevolvida = qtds.getQuantidade2() != null ? qtds.getQuantidade2() : 0;
			}
		}

		return quantidade.intValue() - quantidadeDevolvida.intValue();
	}

	/**
	 * Busca a quantidade de item de rewcebimento provisório
	 * 
	 * @param matCodigo
	 * @return
	 */
	public Integer obterQuantidadeItemRecebProvisorio(Integer matCodigo) {
		// Saldo do Material fora do estoque = quantidadeRecParcelas + SaldoAdiantamentoAF
		Long quantidadeRecParcelas = this.getEstoqueFacade().obterQuantidadeRecParcelasSceItemRecebProvisorio(matCodigo);
		if (quantidadeRecParcelas == null) {
			quantidadeRecParcelas = 0l;
		}
		return quantidadeRecParcelas.intValue();
	}

	/**
	 * Obtem o saldo total: saldoDisponivel + saldoBloqueado
	 * 
	 * @param matCodigo
	 * @param indControleValidade
	 * @param dataReferencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Integer obterSaldoTotal(Integer matCodigo, boolean indControleValidade, Date dataReferencia) throws ApplicationBusinessException {
		Long saldoDisponivel = 0L;
		Long saldoBloqueado = 0L;
		{
			boolean almoxUnico = this.obterParametroAlmoxUnico();
			QuantidadesVO qtds = this.getEstoqueFacade().obterQtdDisponivelQtdBloqueadaSceValidade(matCodigo, indControleValidade, dataReferencia, almoxUnico);
			if (qtds != null) {
				saldoDisponivel = qtds.getQuantidade1() != null ? qtds.getQuantidade1() : 0;
				saldoBloqueado = qtds.getQuantidade2() != null ? qtds.getQuantidade2() : 0;
			}
		}
		return saldoDisponivel.intValue() + saldoBloqueado.intValue();
	}

	public DominioClassifABC obterClassificacaoABC(DadosItemAptoProgramacaoEntregaVO dadosItemApto) throws ApplicationBusinessException {
		Date dataCompetenciaPadrao = this.obterCompetenciaPadrao();
		DominioClassifABC classificacaoABC = this.getEstoqueFacade().obterClassificacaoABCMaterialSceEstoqueGeral(dadosItemApto.getCodigoMaterial(),
				dadosItemApto.getFornecedorPadrao(), dataCompetenciaPadrao);
		return classificacaoABC;
	}

	public Object[] obterDadosAutorizacaoFornecedor(DadosItemAptoProgramacaoEntregaVO dadosItemApto) {
		ScoItemAutorizacaoFornId id = new ScoItemAutorizacaoFornId();
		id.setAfnNumero(dadosItemApto.getAfnNumero());
		id.setNumero(dadosItemApto.getNumero());
//		ScoItemAutorizacaoForn scoItemAutorizacaoForn = this.getScoItemAutorizacaoFornDAO().obterPorChavePrimaria(id);
		ScoItemAutorizacaoForn scoItemAutorizacaoForn = this.scoItemAutorizacaoFornDAO.obterPorChavePrimaria(id);

		Integer lctNumero = scoItemAutorizacaoForn.getAutorizacoesForn().getPropostaFornecedor().getId().getLctNumero();
		Short nroComplemento = scoItemAutorizacaoForn.getAutorizacoesForn().getNroComplemento();
		Double valorUnitario = scoItemAutorizacaoForn.getValorUnitario();

		return new Object[] { lctNumero, nroComplemento, valorUnitario };
	}

	public Integer obterFatorConversao(Integer numero, Short nroComplemento, Integer lctNumero) {
		Integer fatorConversao = 1;
		//ScoItemAutorizacaoForn scoItemAutorizacaoForn = this.getScoItemAutorizacaoFornDAO().buscarScoItemAutorizacaoForn(numero, nroComplemento, lctNumero);
		ScoItemAutorizacaoForn scoItemAutorizacaoForn = this.scoItemAutorizacaoFornDAO.buscarScoItemAutorizacaoForn(numero, nroComplemento, lctNumero);
		if (scoItemAutorizacaoForn != null && scoItemAutorizacaoForn.getFatorConversaoForn() != null) {
			fatorConversao = scoItemAutorizacaoForn.getFatorConversaoForn();
		}
		return fatorConversao;
	}

	public Date obterDataEnezimaParcela(Date dataPrimeiraParcela, BigDecimal intervaloParcelas, Integer numeroParcela) {
		Integer qtDias = intervaloParcelas.multiply(new BigDecimal(numeroParcela - 1)).intValue();
		return DateUtil.adicionaDias(dataPrimeiraParcela, qtDias);
	}

	/**
	 * Monta um vo com os dados necessários para geração de parcelas de item valido
	 * 
	 * @param afnNumero
	 * @param numero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public DadosItemAptoProgramacaoEntregaVO obterDadosItemApto(Integer afnNumero, Integer numero) throws ApplicationBusinessException {

		// Busca o fornecedor padrão do parametro de sistema
		Integer fornecedorPadrao = this.obterFornecedorPadrao();

		Integer codigoMaterial = null;
		Integer slcNumero = null;
		Integer numeroFornecedor = null;
		boolean indUtilizaEspaco = false;
		Integer tempoReposicao = 1;
		BigDecimal consumoDiario = BigDecimal.ZERO;
		Integer estoqueAlmoxSeq = null;
		boolean indControleValidade = false;
		Integer qtdePontoPedido = null;
		Integer qtdeEstqMax = null;
		Integer qtdeEspacoArmazena = null;

		// C6 para buscar material e numero da solicitacao
		GrupoMaterialNumeroSolicitacaoVO grpMatNumSolic = this.getEstoqueFacade().obterCodigoGrupoMaterialNumeroSolicitacaoSceItemRecebProvisorio(afnNumero, numero);
		if (grpMatNumSolic != null) {
			codigoMaterial = grpMatNumSolic.getCodigoMaterial();
			slcNumero = grpMatNumSolic.getNumeroSolicitacao();
		}

		// A consulta C1 será necessária para obter dados para a regra RN10
		SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = this.getEstoqueFacade().obterSceEstoqueAlmoxarifadoPorMaterialFornecedor(codigoMaterial, fornecedorPadrao);

		if (sceEstoqueAlmoxarifado != null) {

			estoqueAlmoxSeq = sceEstoqueAlmoxarifado.getSeq();

			qtdePontoPedido = sceEstoqueAlmoxarifado.getQtdePontoPedido();

			qtdeEstqMax = sceEstoqueAlmoxarifado.getQtdeEstqMax();

			qtdeEspacoArmazena = sceEstoqueAlmoxarifado.getQtdeEspacoArmazena();

			indControleValidade = sceEstoqueAlmoxarifado.getIndControleValidade();

			if (sceEstoqueAlmoxarifado.getFornecedor() != null) {
				numeroFornecedor = sceEstoqueAlmoxarifado.getFornecedor().getNumero();
			}
			if (sceEstoqueAlmoxarifado.getMaterial() != null && sceEstoqueAlmoxarifado.getMaterial().getIndUtilizaEspacoFisico() != null) {
				indUtilizaEspaco = sceEstoqueAlmoxarifado.getMaterial().getIndUtilizaEspacoFisico().booleanValue();
			}

			// Se tempo_reposição = 0 || reposição = null, tempo_reposição = 1
			if (sceEstoqueAlmoxarifado.getTempoReposicao() != null && sceEstoqueAlmoxarifado.getTempoReposicao().intValue() > 0) {
				tempoReposicao = sceEstoqueAlmoxarifado.getTempoReposicao();
			}

			// consumoDiário = trunc((qtde_ponto_pedido / tempo_reposicao), 6)
			if (sceEstoqueAlmoxarifado.getQtdePontoPedido() != null) {
				consumoDiario = new BigDecimal(sceEstoqueAlmoxarifado.getQtdePontoPedido()).divide(new BigDecimal(tempoReposicao), 6, RoundingMode.HALF_EVEN);
			}
		}

		DadosItemAptoProgramacaoEntregaVO dadoItemApto = new DadosItemAptoProgramacaoEntregaVO(afnNumero, numero);
		dadoItemApto.setCodigoMaterial(codigoMaterial);
		dadoItemApto.setSlcNumero(slcNumero);
		dadoItemApto.setEstoqueAlmoxSeq(estoqueAlmoxSeq);
		dadoItemApto.setNumeroFornecedor(numeroFornecedor);
		dadoItemApto.setIndUtilizaEspaco(indUtilizaEspaco);
		dadoItemApto.setIndControleValidade(indControleValidade);
		dadoItemApto.setTempoReposicao(tempoReposicao);
		dadoItemApto.setConsumoDiario(consumoDiario);
		dadoItemApto.setQtdePontoPedido(qtdePontoPedido);
		dadoItemApto.setQtdeEstqMax(qtdeEstqMax);
		dadoItemApto.setQtdeEspacoArmazena(qtdeEspacoArmazena);
		dadoItemApto.setFornecedorPadrao(fornecedorPadrao);

		return dadoItemApto;
	}

	// ------------------ GETs e SETs

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	protected ScoMaterialDAO getScoMaterialDAO() {
		return scoMaterialDAO;
	}

	protected ScoFornecedorDAO getScoFornecedorDAO() {
		return scoFornecedorDAO;
	}

	protected ScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO getScoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoAutomaticoDAO;
	}
}