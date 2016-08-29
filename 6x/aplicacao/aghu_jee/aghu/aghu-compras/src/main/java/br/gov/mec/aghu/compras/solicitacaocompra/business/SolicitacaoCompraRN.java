package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoCaracteristicaUsuarioCentroCustoDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoModalidadeLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoScJnDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.QuantidadeProgramadaRecebidaItemVO;
import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoConveniosFinanceiro;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.model.ScoScJn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.orcamento.business.IOrcamentoFacade;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength","PMD.AghuTooManyMethods","PMD.ExcessiveMethodLength" })
@Stateless
public class SolicitacaoCompraRN extends BaseBusiness {

	private static final String GRAVAR = "gravar";

	private static final Log LOG = LogFactory.getLog(SolicitacaoCompraRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private ScoModalidadeLicitacaoDAO scoModalidadeLicitacaoDAO;
	
	@Inject
	private ScoPontoParadaSolicitacaoDAO scoPontoParadaSolicitacaoDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private ScoScJnDAO scoScJnDAO;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IOrcamentoFacade orcamentoFacade;
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;

	@Inject
	private ScoCaracteristicaUsuarioCentroCustoDAO scoCaracteristicaUsuarioCentroCustoDAO;

	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;

	@EJB
	private SolicitacaoCompraON solicitacaoCompraON;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1401862739567031484L;

	public enum SolicitacaoCompraRNExceptionCode implements
			BusinessExceptionCode {
		SCO_00294, SCO_00487, CENTRO_CUSTO_NAO_ENCONTRADO, SC_NAO_ENCONTRADA, SC_EM_LICITACAO_NAO_PODE_ENV_PPARADA, SC_EM_AF_PENDENTE_NAO_DEVE_ENV_PPARADA, SC_EM_AF_EFETIVADA_NAO_DEVE_ENV_PPARADA, MENSAGEM_ENCAMINHAR_SCO_RN_01, MENSAGEM_ERRO_SC_CC_PERMISSAO, MENSAGEM_ERRO_SC_URGENTE_PRIORITARIO, MENSAGEM_ERRO_SC_QTDE_SOLICITADA, MENSAGEM_ERRO_SC_QTDE_APROVADA, MENSAGEM_ERRO_SC_ATIVA_LIC_M12, MENSAGEM_ERRO_SC_ATIVA_FASE_LIC_M13, MENSAGEM_ERRO_SC_ATIVA_FASE_AF_M14, MENSAGEM_ERRO_SC_ATIVA_AF_M15, MENSAGEM_ERRO_SC_MATERIAL_DESCRICAO_OBR_M06, MENSAGEM_ERRO_SC_COMPRADOR_M07, MENSAGEM_ERRO_SC_QTDE_APROV_M08, MENSAGEM_ERRO_SC_VLR_UNIT_PREV_M09, MENSAGEM_ERRO_SC_CC_SERV_MAT_M10, MENSAGEM_ERRO_SC_CC_OBRA_MAT_ENG_M11,MENSAGEM_ERRO_SC_QTDE_SOL_M12, MENSAGEM_ERRO_SC_MATERIAL_M13,
		MENSAGEM_PARAMETRO_PPS_PARECER_NAO_ENCONTRADO, MENSAGEM_SC_IND_DEVOLUCAO_E_JUSTIFICATIVA_DEVOLUCAO_INVALIDA, MENSAGEM_SC_IND_URGENTE_E_MOTIVO_URGENCIA_INVALIDA, MENSAGEM_SC_IND_EXCLUSAO_E_MOTIVO_EXCLUSAO_INVALIDA,MENSAGEM_ERRO_SC_CC_OBRA_PROJETO_M19,ERRO_CLONE_LICITACAO,MENSAGEM_ERRO_SC_VLR_UNITARIO_M20,
		NATUREZA_NAO_PERMITIDA_PARA_SC, VERBA_GESTAO_NAO_PERMITIDA_PARA_SC, CENTRO_CUSTO_NAO_PERMITIDO_PARA_SC, MENSAGEM_DATA_ANTERIOR_INFORMADA, URGENCIA_OBRIGATORIA, PRIORIDADE_OBRIGATORIA, EXCLUSIVIDADE_OBRIGATORIA,
		 MENSAGEM_PARAMETRO_PADRAO_AUTORIZACAO_NAO_ENCONTRADO,MENSAGEM_PARAMETRO_PPS_PADRAO_NAO_DEFINIDO, MENSAGEM_ENCAMINHAR_SCO_RN_02;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	/***
	 * RN12 - Metodo de negocio o qual é ativado apos a busca de materias e
	 * torna o obrigatorio o preenchimento do campo descricao da Solicitação de
	 * compra
	 * 
	 * @param scoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean isRequeridDescricaoCompra(ScoMaterial scoMaterial)
			throws ApplicationBusinessException {
		AghParametros codMaterialNaoCadastrado = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_MAT_N_CADASTRADO);

		if (scoMaterial != null) {
			return ((scoMaterial.getCodigo().equals(
					codMaterialNaoCadastrado.getVlrNumerico().intValue()) || (scoMaterial
					.getIndGenericoBoolean() == true)));
		} else {
			return false;
		}
	}

	/*****
	 * RN20 e RN21 - Metodo de negocio chamado apos o preenchimento do campo
	 * quantidade busca o ultimo valor unitario para o material caso ele não
	 * seja Generico nem patrimonio
	 * 
	 * @param scoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Double getUltimoValorCompra(ScoMaterial scoMaterial)
			throws ApplicationBusinessException {
		return this.getEstoqueFacade().getUltimoValorCompra(scoMaterial);		
	}

	
	/*********************************
	 * RN22
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public boolean isReadonlyEdicao(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean temPermissaoComprador, Boolean temPermissaoPlanejamento, Boolean temPermissaoGeral) {	
		if (solicitacaoDeCompra.getPontoParadaProxima() == null ||
				temPermissaoComprador || temPermissaoPlanejamento || temPermissaoGeral) {
			return false;
		}
		
		
		FccCentroCustos ccFipe = this.getScoCaracteristicaUsuarioCentroCustoDAO().obterCcAplicacaoGeracaoGppg(getServidorLogadoFacade().obterServidorLogado());
		Boolean possuiCaractGppg = false;
		if (ccFipe != null) {
			Set<Integer> listaHierarquica = getCentroCustoFacade().pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
			if (listaHierarquica.contains(solicitacaoDeCompra.getCentroCustoAplicada().getCodigo())) {
				possuiCaractGppg = true;
			}
		}
		
		if (possuiCaractGppg &&	(solicitacaoDeCompra.getNumero() != null && !this.getScoFasesSolicitacaoDAO().getScEmFases(solicitacaoDeCompra.getNumero()))) {
			return false;
		}
		
		return !(Objects.equals(solicitacaoDeCompra.getPontoParadaProxima().getTipoPontoParada(), DominioTipoPontoParada.SL) ||
				Objects.equals(solicitacaoDeCompra.getPontoParadaProxima().getTipoPontoParada(), DominioTipoPontoParada.CH));
	}
	
	/**

	

	 * RN 36 Não permitir devolução com pontos de parada (atual e anterior) iguais
	 * 
	 * @param solicitacaoDeCompra
	 * @param usuarioLogado
	 * @return
	 */
	public boolean isReadonlyDevolucao(ScoSolicitacaoDeCompra solicitacaoDeCompra,
			Boolean temPermissaoComprador, Boolean temPermissaoPlanejamento, Boolean temPermissaoGeral){
		if (solicitacaoDeCompra.getPontoParadaProxima() != null && solicitacaoDeCompra.getPontoParada() != null && solicitacaoDeCompra.getPontoParadaProxima().getCodigo().equals(solicitacaoDeCompra.getPontoParada().getCodigo())){
			return true;
		} else{
			return isReadonlyEdicao(solicitacaoDeCompra, temPermissaoComprador, temPermissaoPlanejamento, temPermissaoGeral);
		}
	}
	
	
	public Boolean isPerfilComprador(ScoSolicitacaoDeCompra solicitacaoDeCompra, String usuario) {
		if (getICascaFacade().usuarioTemPermissao(usuario, "cadastrarSCComprador")){
			return true;
		} else {
			return false;
		}
	}

	public Boolean isPerfilPlanejamento(ScoSolicitacaoDeCompra solicitacaoDeCompra, String usuario) {
		if (getICascaFacade().usuarioTemPermissao(usuario, "cadastrarSCPlanejamento")){
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean isPerfilGeral(ScoSolicitacaoDeCompra solicitacaoDeCompra, String usuario) {
		if (getICascaFacade().usuarioTemPermissao(usuario, "cadastrarSCGeral")){
			return true;
		} else {
			return false;
		}
	}

	/*********************************
	 * RN23
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 */
	public void validaUrgentePrioritario(
			ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		if (!solicitacaoDeCompra.getUrgente()) {
			solicitacaoDeCompra.setMotivoUrgencia(null);
			solicitacaoDeCompra.setDtMaxAtendimento(null);
		}
		if (!solicitacaoDeCompra.getPrioridade()) {
			solicitacaoDeCompra.setMotivoPrioridade(null);
		}
		
		if (solicitacaoDeCompra.getUrgente()
				&& solicitacaoDeCompra.getPrioridade()) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_URGENTE_PRIORITARIO);

		}
	}

	/*********************************
	 * RN 25 - Verifica se SC está liberada para exclusão (Não pode estar em
	 * Lict ou AF não excluídas).
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 */
	public void validaSCLiberadaExclusao(
			ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		ScoFaseSolicitacao scoFaseSolicitacao = this
				.getScoFasesSolicitacaoDAO().obterSCAtivoLicitacao(
						solicitacaoDeCompra);

		if (scoFaseSolicitacao != null) {
			Integer lctNumero = scoFaseSolicitacao.getItemLicitacao().getId()
					.getLctNumero();
			Short numero = scoFaseSolicitacao.getItemLicitacao().getId()
					.getNumero();

			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_ATIVA_LIC_M12,
					lctNumero, numero);
		}

		scoFaseSolicitacao = this.getScoFasesSolicitacaoDAO()
				.obterSCAtivoFasesLicitacao(solicitacaoDeCompra);
		if (scoFaseSolicitacao != null) {
			Integer lctNumero = scoFaseSolicitacao.getItemLicitacao().getId()
					.getLctNumero();

			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_ATIVA_FASE_LIC_M13,
					lctNumero, scoFaseSolicitacao.getNumero());
		}

		scoFaseSolicitacao = this.getScoFasesSolicitacaoDAO()
				.obterSCAtivoFasesAF(solicitacaoDeCompra);
		if (scoFaseSolicitacao != null) {
			Integer lctNumero = scoFaseSolicitacao.getItemAutorizacaoForn()
					.getAutorizacoesForn().getPropostaFornecedor().getId()
					.getLctNumero();
			Short nro_complemento = scoFaseSolicitacao.getItemAutorizacaoForn()
					.getAutorizacoesForn().getNroComplemento();

			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_ATIVA_FASE_AF_M14,
					lctNumero, nro_complemento);
		}

		scoFaseSolicitacao = this.getScoFasesSolicitacaoDAO().obterSCAtivaAF(
				solicitacaoDeCompra);
		if (scoFaseSolicitacao != null) {
			Integer lctNumero = scoFaseSolicitacao.getItemAutorizacaoForn()
					.getAutorizacoesForn().getPropostaFornecedor().getId()
					.getLctNumero();
			Short nro_complemento = scoFaseSolicitacao.getItemAutorizacaoForn()
					.getAutorizacoesForn().getNroComplemento();

			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_ATIVA_AF_M15,
					lctNumero, nro_complemento);
		}

	}

	/****
	 * Inicio dos Metodos validacao chamados apenas na insercao
	 **/

	/****
	 * Metodo que valida a data de analise da SC que não pode ser maior que hoje
	 * Adicionado na inclusão
	 * @ORADB: SCOT_SLC_BRI
	 * @param solicitacaoCompra
	 */
	public void validaDataAnalise(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		if (solicitacaoDeCompra.getDtAnalise() != null
				&& DateValidator.validaDataMenor(
						solicitacaoDeCompra.getDtAnalise(), new Date())) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.SCO_00487);
		}
	}

	/******
	 * Valida natureza de Despesa utilizado na inserção de registro novo
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 */
	/*public void validaNaturezaDespesa(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {
		if (solicitacaoDeCompra.getVbgSeq() != null &&
			solicitacaoDeCompra.getConvenioFinanceiro() != null) {
			Integer quantidade = this
					.getOrcamentoFacade()
					.obterQuantidadePrevisoesDoExecCorrentePeloGrupoNaturezaDespeza(
							solicitacaoDeCompra.getVbgSeq());
			if (quantidade <= 0
					&& solicitacaoDeCompra.getNaturezaDespesa() != null) {
				throw new ApplicationBusinessException(
						SolicitacaoCompraRNExceptionCode.SCO_00328);
			}
		}
	}*/

	/*************
	 * Valida Quantidade Solicitada e Aprovada que deve ser maior que zero
	 * chamado na inserção
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 * @ORADB: SCOT_SLC_BRU
	 */
	
	public void validaQtdeSolicitadaAprovada(
			ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		if (solicitacaoDeCompra.getQtdeSolicitada() == null){
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_QTDE_SOL_M12);
		}
		if (solicitacaoDeCompra.getQtdeSolicitada() <= 0) {
			solicitacaoDeCompra.setQtdeSolicitada(null);
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_QTDE_SOLICITADA);
		}

		if (solicitacaoDeCompra.getQtdeAprovada() == null){
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_QTDE_APROV_M08);
			
		}
		if (solicitacaoDeCompra.getQtdeAprovada() <= 0) {
			solicitacaoDeCompra.setQtdeAprovada(null);
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_QTDE_APROVADA);
		}

	}

	/****
	 * Fim dos Metodos validacao chamados apenas na insercao
	 **/

	/****
	 * Inicio dos Metodos que setam valores aos campos da socilitacao de compras
	 * usados apenas na insercao
	 **/

	/*** Atualiza o servidor quando nao é geração automatica ***/
	public void setaServidorGeracaoNaoAutomatica(
			ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		/* ATUALIZA CARTAO SERVIDOR */
		if (!solicitacaoDeCompra.getGeracaoAutomatica()) {
			solicitacaoDeCompra
					.setServidor(servidorLogado);
		}
	}

	/****
	 * Atualiza o usuario que excluiu a SC marcou o checkbox de exclusao e mudou
	 * o status de N para S
	 ***/
	public void setaExclusao(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {
		/* CONSISTE SE SOLICITAÇÃO FOI EXCLUIDA ATUALIZA DATA DA EXCLUSÃO */
		/* E SERVIDOR RESPONSÁVEL PELA MESMA - RN TPL010_3 */
		if (solicitacaoDeCompra.getExclusao() != null)
		{			
			boolean flagMotivoExclusao = false;
			if (solicitacaoDeCompra.getMotivoExclusao() != null) {
			   solicitacaoDeCompra.setMotivoExclusao(solicitacaoDeCompra.getMotivoExclusao().trim());
			   flagMotivoExclusao = (solicitacaoDeCompra.getMotivoExclusao().length() > 0 ? true : false); 
			   
			}
			else {
				flagMotivoExclusao = true;
			}
			
			if (solicitacaoDeCompra.getExclusao()
					&& flagMotivoExclusao) {
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				solicitacaoDeCompra.setServidorExclusao(servidorLogado);
				solicitacaoDeCompra.setDtExclusao(new Date());
			}
		}
	}

	/****
	 * Atualiza o centro de custo de aplicação e o almoxarifado se o material
	 * for estocavel e nao for de um grupo engenharia
	 * 
	 * @param solicitacaoDeCompra
	 */
	public void setaValoresCentroCustoAlmoxarifadoSeq(
			ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException {
		
		if (solicitacaoDeCompra.getMaterial() == null){
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_MATERIAL_M13);
		}
//	xxxxxxx
		if (solicitacaoDeCompra.getMaterial().getIndEstocavelBoolean()
				&& !solicitacaoDeCompra.getMaterial().getGrupoMaterial()
						.getEngenhari()) {
			solicitacaoDeCompra.setCentroCustoAplicada(solicitacaoDeCompra
					.getMaterial().getAlmoxarifado().getCentroCusto());
			solicitacaoDeCompra.setAlmoxarifado(solicitacaoDeCompra
					.getMaterial().getAlmoxarifado());
		}

	}

	/***
	 * Atualiza orcamentoPrevio para N caso o parametro centro de custo de
	 * projeto seja igual ao centor de custo de aplicacao da SC foi utilizado na
	 * comparação apenas os dois primeiros digitos pois representam o nodo pai
	 * da hierarquia do centro de custo
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 */
	public void setaOrcamentoPrevio(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {
		FsoVerbaGestao vbFipe = getCadastrosBasicosOrcamentoFacade().obterVerbaGestaoProjetoFipe(solicitacaoDeCompra.getCentroCustoAplicada());
		
		if (vbFipe != null) {
			if (solicitacaoDeCompra.getVerbaGestao() == null) {
				solicitacaoDeCompra.setVerbaGestao(vbFipe);
			}
			
			solicitacaoDeCompra.setOrcamentoPrevio(DominioSimNao.N.toString());
		}		
	}

	/***
	 * Metodo que seta o ponto de parada para solicitante na inclusao da solicitação
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 */
	public void setaPontoParada(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		ScoPontoParadaSolicitacao ppsSolicitante = this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.SL);
		
		solicitacaoDeCompra.setPontoParada(ppsSolicitante);
		solicitacaoDeCompra.setPontoParadaProxima(ppsSolicitante);

	}
	
	/*****
	 * Metodo que chama os outros metodos setando os valores defaults para
	 * inserção da SC
	 * @ORADB: SCOT_SLC_BRI
	 ***/
	public void setaValoresDefault(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		if (solicitacaoDeCompra.getGeracaoAutomatica() == null) {
			solicitacaoDeCompra.setGeracaoAutomatica(false);
		}

		if (solicitacaoDeCompra.getEfetivada() == null) {
			solicitacaoDeCompra.setEfetivada(false);
		}

		if (solicitacaoDeCompra.getFundoFixo() == null) {
			solicitacaoDeCompra.setFundoFixo(false);
		}
		if (solicitacaoDeCompra.getFundoFixo() == null) {
			solicitacaoDeCompra.setFundoFixo(false);
		}
		if (solicitacaoDeCompra.getExclusao() == null) {
			solicitacaoDeCompra.setExclusao(false);
		}

		this.setaServidorGeracaoNaoAutomatica(solicitacaoDeCompra);	
		
		this.setaExclusao(solicitacaoDeCompra);
		
		if (solicitacaoDeCompra.getMaterial() == null){
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_MATERIAL_M13);
		}
		
		solicitacaoDeCompra.setUnidadeMedida(solicitacaoDeCompra.getMaterial()
				.getUnidadeMedida());
		
		this.setaValoresCentroCustoAlmoxarifadoSeq(solicitacaoDeCompra);
		this.setaOrcamentoPrevio(solicitacaoDeCompra);
		
		if (solicitacaoDeCompra.getPontoParada()==null && solicitacaoDeCompra.getPontoParadaProxima()==null){
			this.setaPontoParada(solicitacaoDeCompra);
		}
		this.alteraQuantidadeAprovada(solicitacaoDeCompra);

	}


	/***
	 * Metodo que valida o servidor comprador
	 * @oradb: SCOT_SLC_BRU
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 * @throws ApplicationBusinessException
	 */
	public void validaServidorComprador(
			ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws ApplicationBusinessException {
		if (solicitacaoDeCompra.getServidorCompra() != null
				&& CoreUtil.modificados(solicitacaoDeCompra.getServidorCompra(),
						solicitacaoDeCompraOld.getServidorCompra())) {
			if (!this.getRegistroColaboradorFacade()
					.isServidorCompradorAtivoPorVinculoMatricula(
							solicitacaoDeCompra.getServidorCompra().getId()
									.getVinCodigo(),
							solicitacaoDeCompra.getServidorCompra().getId()
									.getMatricula())) {
				throw new ApplicationBusinessException(
						SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_COMPRADOR_M07);
			}
		}
	}

	/***
	 * Metodo que valida a alteracao dos campos qtde Aprovada e Valor unitario
	 * @ORADB: SCOT_SLC_BRU
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 * @throws ApplicationBusinessException
	 */
	public void validaQuantidadeAprovadaValorUnitarioPrevistoAlteracao(
			ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws ApplicationBusinessException {
		if (solicitacaoDeCompraOld.getQtdeAprovada() != null
				|| solicitacaoDeCompraOld.getValorUnitPrevisto() != null
				|| solicitacaoDeCompraOld.getVbgSeq() != null) {

			if (solicitacaoDeCompra.getQtdeAprovada() == null
					&& solicitacaoDeCompraOld.getQtdeAprovada() != null) {
				throw new ApplicationBusinessException(
						SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_QTDE_APROV_M08);
			}

			if (solicitacaoDeCompra.getValorUnitPrevisto() == null
					&& solicitacaoDeCompraOld.getValorUnitPrevisto() != null) {
				throw new ApplicationBusinessException(
						SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_VLR_UNIT_PREV_M09);
			}

		}

	}
	
	

	/**
	 * @ORADB SCOK_SLC_RN - RN_SLCP_VER_PPS
	 * @throws BaseException
	 *             rnSlcpVerPps
	 */
	public void validaProximoPontoParada(ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws ApplicationBusinessException {

		if (CoreUtil.modificados(solicitacaoDeCompra.getPontoParadaProxima(), solicitacaoDeCompraOld.getPontoParadaProxima())) {

			if (solicitacaoDeCompra.getDtAnalise() == null) {
				// Ponto Parada Licitação
				ScoPontoParadaSolicitacao pontoParadaLicitacao = this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.LI);
				if (!solicitacaoDeCompra.getPontoParada().getCodigo().equals(pontoParadaLicitacao.getCodigo().shortValue())) {
					/* Verifica se PPS está coerente com a Fase da SC */
					Long quantidade = getScoSolicitacoesDeComprasDAO().obterQuantidadeSolicitacoesEmLicitaca(solicitacaoDeCompra.getNumero());

					if (quantidade > 0 && CoreUtil.modificados(solicitacaoDeCompra.getPontoParada(), solicitacaoDeCompra.getPontoParadaProxima())) {
						throw new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.SC_EM_LICITACAO_NAO_PODE_ENV_PPARADA);
					}

					quantidade = getScoSolicitacoesDeComprasDAO()
							.obterQuantidadeSolicitacoesEmAfPorNumESituacao(
									solicitacaoDeCompra.getNumero(),
									new DominioSituacaoAutorizacaoFornecimento[] { DominioSituacaoAutorizacaoFornecimento.AE,
											DominioSituacaoAutorizacaoFornecimento.PA });
					if (quantidade > 0) {
						throw new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.SC_EM_AF_PENDENTE_NAO_DEVE_ENV_PPARADA);
					}

					quantidade = getScoSolicitacoesDeComprasDAO()
							.obterQuantidadeSolicitacoesEmAfPorNumESituacao(
									solicitacaoDeCompra.getNumero(),
									new DominioSituacaoAutorizacaoFornecimento[] { DominioSituacaoAutorizacaoFornecimento.EF,
											DominioSituacaoAutorizacaoFornecimento.EP });
					if (quantidade > 0) {
						throw new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.SC_EM_AF_EFETIVADA_NAO_DEVE_ENV_PPARADA);
					}
				}
			}
		}
	}
	


	/***
	 * Metodo que seta o servidor que excluiu a SC
	 * @ORADB: SCOT_SLC_BRU
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 * @throws ApplicationBusinessException
	 */
	public void setaExclusaoAlteracao(
			ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws ApplicationBusinessException {		
		
		if (solicitacaoDeCompraOld != null &&
				!solicitacaoDeCompraOld.getExclusao()
				&& solicitacaoDeCompra.getExclusao()) {
			this.validaSCLiberadaExclusao(solicitacaoDeCompra);
			this.setaExclusao(solicitacaoDeCompra);
		}
	}

	/******
	 * Metodo que Seta o servidor que alterou a SC é necessario monitorar a
	 * alteracao apenas de alguns campos
	 *  @ORADB: SCOT_SLC_BRU
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 * @throws ApplicationBusinessException
	 */
	public void setaServidorAlteracao(
			ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if(	CoreUtil.modificados(solicitacaoDeCompra.getCentroCusto(), solicitacaoDeCompraOld.getCentroCusto()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getCentroCustoAplicada(), solicitacaoDeCompraOld.getCentroCustoAplicada()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getQtdeAprovada(), solicitacaoDeCompraOld.getQtdeAprovada()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getValorUnitPrevisto(), solicitacaoDeCompraOld.getValorUnitPrevisto()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getMaterial(), solicitacaoDeCompraOld.getMaterial()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getQtdeSolicitada(), solicitacaoDeCompraOld.getQtdeSolicitada()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getUrgente(), solicitacaoDeCompraOld.getUrgente()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getDevolucao(), solicitacaoDeCompraOld.getDevolucao()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getModalidadeLicitacao(), solicitacaoDeCompraOld.getModalidadeLicitacao()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getNaturezaDespesa(), solicitacaoDeCompraOld.getNaturezaDespesa()) ||				
			CoreUtil.modificados(solicitacaoDeCompra.getCentroCustoAutzTecnica(), solicitacaoDeCompraOld.getCentroCustoAutzTecnica()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getDiasDuracao(), solicitacaoDeCompraOld.getDiasDuracao()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getQtdeReforco(), solicitacaoDeCompraOld.getQtdeReforco()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getDescTecnica(), solicitacaoDeCompraOld.getDescTecnica()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getDescTecnicaCont(), solicitacaoDeCompraOld.getDescTecnicaCont()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getAplicacao(), solicitacaoDeCompraOld.getAplicacao()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getJustificativaUso(), solicitacaoDeCompraOld.getJustificativaUso()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getValorUnitPrevisto(), solicitacaoDeCompraOld.getValorUnitPrevisto()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getDtAnalise(), solicitacaoDeCompraOld.getDtAnalise()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getMotivoUrgencia(), solicitacaoDeCompraOld.getMotivoUrgencia()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getJustificativaDevolucao(), solicitacaoDeCompraOld.getJustificativaDevolucao()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getDescricao(), solicitacaoDeCompraOld.getDescricao()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getPrioridade(), solicitacaoDeCompraOld.getPrioridade()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getMotivoPrioridade(), solicitacaoDeCompraOld.getMotivoPrioridade()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getVbgSeq(), solicitacaoDeCompraOld.getVbgSeq()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getDtMaxAtendimento(), solicitacaoDeCompraOld.getDtMaxAtendimento()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getMatExclusivo(),solicitacaoDeCompraOld.getMatExclusivo()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getJustificativaExclusividade(),solicitacaoDeCompraOld.getJustificativaExclusividade())) {
		
			solicitacaoDeCompra.setServidorAlteracao(servidorLogado);
			solicitacaoDeCompra.setDtAlteracao(new Date());
		}

	}

	/****
	 * Metodo que seta o servidor que alterou dados de orcamento da SC
	 *  @ORADB: SCOT_SLC_BRU
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 * @throws ApplicationBusinessException
	 */
	public void setaServidorOrcamentoAlteracao(
			ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws ApplicationBusinessException {

		if (CoreUtil.modificados(solicitacaoDeCompra.getNaturezaDespesa(),solicitacaoDeCompraOld.getNaturezaDespesa()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getVbgSeq(),solicitacaoDeCompraOld.getVbgSeq()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getCentroCustoAutzTecnica(),solicitacaoDeCompraOld.getCentroCustoAutzTecnica()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getOrcamentoPrevio(),solicitacaoDeCompraOld.getOrcamentoPrevio()) ||
			CoreUtil.modificados(solicitacaoDeCompra.getNroInvestimento(),solicitacaoDeCompraOld.getNroInvestimento())) {

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			solicitacaoDeCompra.setServidorOrcamento(servidorLogado);
			solicitacaoDeCompra.setDtOrcamento(new Date());

		}
	}

	/****
	 * Metodo que seta campo efetivada para Sim
	 * @oradb: SCOT_SLC_BRU
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 */
	public void setaEfetiva(ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraOld) {
		
		if (solicitacaoDeCompra.getQtdeEntregue() != null &&
			solicitacaoDeCompra.getFundoFixo() != null){
			if (solicitacaoDeCompra.getQtdeEntregue() > 0
					&& solicitacaoDeCompra.getFundoFixo()
					&& CoreUtil.modificados(solicitacaoDeCompra.getQtdeEntregue(), solicitacaoDeCompraOld.getQtdeEntregue())) {
	
				solicitacaoDeCompra.setEfetivada(true);
			}
		}
	}

	/****
	 * Metodo que seta qtd aprovada quando a mesma for modificada e o ponto
	 * proximo ponto de parada for igual ao parametro
	 * @ORADB: SCOT_SLC_BRU
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 * @throws ApplicationBusinessException
	 */
	public void setaQtdeAprovadaAlteracao(
			ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws ApplicationBusinessException {
		Short codPontoParada = this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.SL).getCodigo();

		if (solicitacaoDeCompra.getPontoParadaProxima().getCodigo()
				.equals(codPontoParada)
				&& CoreUtil.modificados(solicitacaoDeCompra.getQtdeSolicitada(),solicitacaoDeCompraOld.getQtdeSolicitada())) {

			alteraQuantidadeAprovada(solicitacaoDeCompra);
		}

	}
	
	public void criaJournalDevolvidoAlteracao(
			ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraOld)
			throws ApplicationBusinessException {		
		
		if ((!solicitacaoDeCompraOld.getDevolucao() && solicitacaoDeCompra.getDevolucao()) ||
				!Objects.equals(solicitacaoDeCompra.getPontoParadaProxima(), solicitacaoDeCompraOld.getPontoParadaProxima())) {
			this.inserirSolicitacaoCompraJournal(solicitacaoDeCompra, DominioOperacoesJournal.UPD);
		}
	}

	
	/****
	 * Metodo que valida a data de solicitacao da SC que não pode ser maior que
	 * hoje Adicionado na inclusão e alteração *
	 * @ORADB: SCOT_SLC_BRI - BRU
	 * @param solicitacaoCompra
	 */
	public void validaDataSolicitacao(ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {

		if (solicitacaoDeCompra.getDtSolicitacao() != null
				&& DateUtil.validaDataMaior(
						solicitacaoDeCompra.getDtSolicitacao(), new Date())) {
			throw new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.SCO_00294);
		}
	}

	/****
	 * RN 11 - Metodo Acionado na acao gravar caso a quantidade solicitada
	 * Não esteja em branca seta o valor dela para o da quantidade aprovada *
	 * 
	 * @param solicitacaoCompra
	 */
	public void alteraQuantidadeAprovada(
			ScoSolicitacaoDeCompra solicitacaoDeCompra) {

		if (solicitacaoDeCompra.getQtdeSolicitada() != null) {
			solicitacaoDeCompra.setQtdeAprovada(solicitacaoDeCompra
					.getQtdeSolicitada());
		}
	}
	
	/***
	 * Metodo que valida Valor unitario que quando preenchido deve ser maior que zero
	 * @ORADB: SCOT_SLC_BRU
	 * @param solicitacaoDeCompra
	 * @param solicitacaoDeCompraOld
	 * @throws ApplicationBusinessException
	 */
	public void validaValorUnitarioPrevisto(
			ScoSolicitacaoDeCompra solicitacaoDeCompra)
			throws ApplicationBusinessException {
		

		if (solicitacaoDeCompra.getValorUnitPrevisto() != null) {
			// Valor unitário previsto deve ser maior que zero. 
			if (!(solicitacaoDeCompra.getValorUnitPrevisto().compareTo(
					BigDecimal.ZERO) > 0)) {
				throw new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.MENSAGEM_ERRO_SC_VLR_UNITARIO_M20);
			}
		}
		

	}
	
	/****
	 * Fim dos Metodos validacao chamados na inserção e alteração
	 **/

	/*****
	 * RN 14 - INSERI NA JOURNAL QUE ESTE CASO SERVE COMO UMA TABELA DE
	 * ENCAMINHAMENTOS O PROXIMO PONTO DE PARADA SEMPRE È IGUAL AO PONTO DE
	 * PARADA QUANDO A SOLICITACAO È NOVA OU SEJA QAUNDO È INSERT. Metodo
	 * chamado no inserir da solicitacao de compras
	 * 
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 */
	public void inserirSolicitacaoCompraJournal(ScoSolicitacaoDeCompra solicitacaoDeCompra, DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoScJn jn = new ScoScJn();
		jn.setNumero(solicitacaoDeCompra.getNumero());
		jn.setDevolucao(solicitacaoDeCompra.getDevolucao());
		jn.setPontoParadaSolicitacao(solicitacaoDeCompra
				.getPontoParadaProxima());

		jn.setOperacao(operacao);
		//(DominioOperacoesJournal.INS);
		jn.setServidor(servidorLogado);
		jn.setNomeUsuario(servidorLogado.getUsuario());

		getScoScJnDAO().persistir(jn);
		getScoScJnDAO().flush();

	}

	public void inserirSolicitacaoCompra(
			ScoSolicitacaoDeCompra solicitacaoDeCompra, boolean flagFlush, boolean validarMaterialDireto)
					throws ApplicationBusinessException {
		this.validarJustificativas(solicitacaoDeCompra);
		
		RapServidores servidor = getServidorLogadoFacade().obterServidorLogado();
		if (validarMaterialDireto) {
			this.validarCompraMaterialDireto(solicitacaoDeCompra, servidor);
		}
		
		if(solicitacaoDeCompra != null){
			validaDataSolicitacao(solicitacaoDeCompra);
			validaDataAnalise(solicitacaoDeCompra);
//xxxxx


			
			setaValoresDefault(solicitacaoDeCompra);
			validaQtdeSolicitadaAprovada(solicitacaoDeCompra);
			validaValorUnitarioPrevisto(solicitacaoDeCompra);
			validarRegrasOrcamentarias(solicitacaoDeCompra, null);
		}		
		/********* INSERE SOLICITACAO DE COMPRAS *******/
		this.getScoSolicitacoesDeComprasDAO().persistir(solicitacaoDeCompra);
		
		if (flagFlush == true) {
			this.getScoSolicitacoesDeComprasDAO().flush();
		}
		
		/**** INSERE NA JOURNAL ****/
		this.inserirSolicitacaoCompraJournal(solicitacaoDeCompra, DominioOperacoesJournal.INS);
	}
	
	private void validarCompraMaterialDireto(ScoSolicitacaoDeCompra solicitacaoDeCompra, RapServidores servidor) throws ApplicationBusinessException {
		Boolean temPermissaoCadastrar = cascaFacade.usuarioTemPermissao(servidor.getUsuario(), "cadastrarSolicitacaoCompras");
		Boolean temPermissaoChefia = cascaFacade.usuarioTemPermissao(servidor.getUsuario(), "cadastrarSCChefias");    
		Boolean temPermissaoAreasEspecificas = cascaFacade.usuarioTemPermissao(servidor.getUsuario(), "cadastrarSCAreasEspecificas");   
		Boolean temPermissaoGeral = this.isPerfilGeral(solicitacaoDeCompra, servidor.getUsuario()); 
		Boolean temPermissaoPlanejamento = this.isPerfilPlanejamento(solicitacaoDeCompra, servidor.getUsuario());
		solicitacaoCompraON.verificarMaterialSelecionado(solicitacaoDeCompra.getMaterial(), temPermissaoCadastrar, temPermissaoChefia, temPermissaoAreasEspecificas, temPermissaoGeral, temPermissaoPlanejamento);
	}
	
	private void validarJustificativas(final ScoSolicitacaoDeCompra solicitacaoDeCompra) throws ApplicationBusinessException {
		if (solicitacaoDeCompra != null) {
			if (solicitacaoDeCompra.getUrgente() != null && solicitacaoDeCompra.getUrgente() && StringUtils.isEmpty(solicitacaoDeCompra.getMotivoUrgencia())) {
				throw new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.URGENCIA_OBRIGATORIA);
			}

			if (solicitacaoDeCompra.getPrioridade() != null && solicitacaoDeCompra.getPrioridade()
					&& StringUtils.isEmpty(solicitacaoDeCompra.getMotivoPrioridade())) {
				throw new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.PRIORIDADE_OBRIGATORIA);
			}

			if (solicitacaoDeCompra.getMatExclusivo() != null && solicitacaoDeCompra.getMatExclusivo()
					&& StringUtils.isEmpty(solicitacaoDeCompra.getJustificativaExclusividade())) {
				throw  new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.EXCLUSIVIDADE_OBRIGATORIA);
			}
		}
	}

	public void inserirSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra, Boolean validarMaterialDireto)
					throws ApplicationBusinessException {
		this.inserirSolicitacaoCompra(solicitacaoDeCompra, true, validarMaterialDireto);
	}		

	public void validarRegrasOrcamentarias(
			ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoSolicitacaoDeCompra solicitacaoDeCompraClone) throws ApplicationBusinessException {		
		validarCentroCustoParam(solicitacaoDeCompra, solicitacaoDeCompraClone);
		validarNaturezaParam(solicitacaoDeCompra, solicitacaoDeCompraClone);
		validarVerbaGestaoParam(solicitacaoDeCompra, solicitacaoDeCompraClone);
	}
	
	

	@SuppressWarnings("PMD.NPathComplexity")
	public ScoSolicitacaoDeCompra clonarSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoCompra) throws ApplicationBusinessException{
		solicitacaoCompra = this.getScoSolicitacoesDeComprasDAO().merge(solicitacaoCompra);		
		ScoSolicitacaoDeCompra solicitacaoCompraCloneLocal = null;
		try{
			solicitacaoCompraCloneLocal = (ScoSolicitacaoDeCompra) BeanUtils.cloneBean(solicitacaoCompra);
		} catch(Exception e){
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.ERRO_CLONE_LICITACAO);
		}
		
		if(solicitacaoCompra.getPontoParada() != null) {
			//ScoPontoParadaSolicitacao pontoParadaSolicitacao = new ScoPontoParadaSolicitacao();
			//pontoParadaSolicitacao.setCodigo(solicitacaoCompra.getPontoParada().getCodigo());
			ScoPontoParadaSolicitacao pontoParadaSolicitacao = getComprasCadastrosBasicosFacade().obterPontoParada(solicitacaoCompra.getPontoParada().getCodigo());
			solicitacaoCompraCloneLocal.setPontoParada(pontoParadaSolicitacao);
		}
		

		if(solicitacaoCompra.getPontoParadaProxima() != null) {
			//ScoPontoParadaSolicitacao pontoParadaSolicitacao = new ScoPontoParadaSolicitacao();
			//pontoParadaSolicitacao.setCodigo(solicitacaoCompra.getPontoParadaProxima().getCodigo());
			ScoPontoParadaSolicitacao pontoParadaSolicitacao = getComprasCadastrosBasicosFacade().obterPontoParada(solicitacaoCompra.getPontoParadaProxima().getCodigo());
			solicitacaoCompraCloneLocal.setPontoParadaProxima(pontoParadaSolicitacao);
		}

		if(solicitacaoCompra.getUnidadeMedida() != null) {
			//ScoUnidadeMedida unidadeMedida = new ScoUnidadeMedida();
			//unidadeMedida.setCodigo(solicitacaoCompra.getUnidadeMedida().getCodigo());
			ScoUnidadeMedida unidadeMedida = getComprasFacade().obterUnidadeMedidaPorSeq(solicitacaoCompra.getUnidadeMedida().getCodigo());
			solicitacaoCompraCloneLocal.setUnidadeMedida(unidadeMedida);
		}

		if(solicitacaoCompra.getModalidadeLicitacao() != null) {
			//ScoModalidadeLicitacao modalidade = new ScoModalidadeLicitacao();
			//modalidade.setCodigo(solicitacaoCompra.getModalidadeLicitacao().getCodigo());	
			ScoModalidadeLicitacao modalidade = this.getScoModalidadeLicitacaoDAO().obterPorChavePrimaria(solicitacaoCompra.getModalidadeLicitacao().getCodigo());
			solicitacaoCompraCloneLocal.setModalidadeLicitacao(modalidade);
		}

		if(solicitacaoCompra.getPaciente() != null) {
			//AipPacientes paciente = new AipPacientes();
			//paciente.setCodigo(solicitacaoCompra.getPaciente().getCodigo());
			AipPacientes paciente = this.getPacienteFacade().obterPacientePorCodigo(solicitacaoCompra.getPaciente().getCodigo());
			solicitacaoCompraCloneLocal.setPaciente(paciente);
		}

		if(solicitacaoCompra.getMaterial() != null) {
			//ScoMaterial material = new ScoMaterial();
			//material.setCodigo(solicitacaoCompra.getMaterial().getCodigo());
			ScoMaterial material = this.getComprasFacade().obterMaterialPorId(solicitacaoCompra.getMaterial().getCodigo());
			solicitacaoCompraCloneLocal.setMaterial(material);
		}
		
		if(solicitacaoCompra.getCentroCusto() != null) {
			//FccCentroCustos centroCusto = new FccCentroCustos();
			//centroCusto.setCodigo(solicitacaoCompra.getCentroCusto().getCodigo());
			FccCentroCustos centroCusto = this.getCentroCustoFacade().obterFccCentroCustos(solicitacaoCompra.getCentroCusto().getCodigo());			
			solicitacaoCompraCloneLocal.setCentroCusto(centroCusto);
		}

		if(solicitacaoCompra.getCentroCustoAutzTecnica() != null) {
			//FccCentroCustos centroCusto = new FccCentroCustos();
			//centroCusto.setCodigo(solicitacaoCompra.getCentroCustoAutzTecnica().getCodigo());
			FccCentroCustos centroCusto = this.getCentroCustoFacade().obterFccCentroCustos(solicitacaoCompra.getCentroCustoAutzTecnica().getCodigo());
			solicitacaoCompraCloneLocal.setCentroCustoAutzTecnica(centroCusto);
		}

		if(solicitacaoCompra.getCentroCustoAplicada() != null) {
			FccCentroCustos centroCusto = this.getCentroCustoFacade().obterFccCentroCustos(solicitacaoCompra.getCentroCustoAplicada().getCodigo());			
			solicitacaoCompraCloneLocal.setCentroCustoAplicada(centroCusto);
		}

		if(solicitacaoCompra.getAlmoxarifado() != null) {
			//SceAlmoxarifado almoxarifado = new SceAlmoxarifado();
			//almoxarifado.setSeq(solicitacaoCompra.getAlmoxarifado().getSeq());
			SceAlmoxarifado almoxarifado = this.getEstoqueFacade().obterAlmoxarifadoPorId(solicitacaoCompra.getAlmoxarifado().getSeq());
			solicitacaoCompraCloneLocal.setAlmoxarifado(almoxarifado);
		}

		if(solicitacaoCompra.getConvenioFinanceiro() != null) {			
			FsoConveniosFinanceiro conveniosFinanceiro = this.getOrcamentoFacade().obterConvenioPorChave(solicitacaoCompra.getConvenioFinanceiro().getCodigo());
			solicitacaoCompraCloneLocal.setConvenioFinanceiro(conveniosFinanceiro);
		}

		if(solicitacaoCompra.getServidorAlteracao() != null) {
			//RapServidores servidor = new RapServidores();
			//servidor.setId(new RapServidoresId(solicitacaoCompra.getServidorAlteracao().getId().getMatricula(), solicitacaoCompra.getServidorAlteracao().getId().getVinCodigo()));
			RapServidores servidor =  getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(solicitacaoCompra.getServidorAlteracao().getId().getMatricula(), solicitacaoCompra.getServidorAlteracao().getId().getVinCodigo());
			solicitacaoCompraCloneLocal.setServidorAlteracao(servidor);
		}

		if(solicitacaoCompra.getServidorAutorizacao() != null) {
			//RapServidores servidor = new RapServidores();
			//servidor.setId(new RapServidoresId(solicitacaoCompra.getServidorAutorizacao().getId().getMatricula(), solicitacaoCompra.getServidorAutorizacao().getId().getVinCodigo()));
			RapServidores servidor =  getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(solicitacaoCompra.getServidorAutorizacao().getId().getMatricula(), solicitacaoCompra.getServidorAutorizacao().getId().getVinCodigo());
			solicitacaoCompraCloneLocal.setServidorAutorizacao(servidor);
		}

		if(solicitacaoCompra.getServidorCompra() != null) {
			//RapServidores servidor = new RapServidores();
			//servidor.setId(new RapServidoresId(solicitacaoCompra.getServidorCompra().getId().getMatricula(), solicitacaoCompra.getServidorCompra().getId().getVinCodigo()));
			RapServidores servidor =  getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(solicitacaoCompra.getServidorCompra().getId().getMatricula(), solicitacaoCompra.getServidorCompra().getId().getVinCodigo());
			solicitacaoCompraCloneLocal.setServidorCompra(servidor);
		}

		if(solicitacaoCompra.getServidorExclusao() != null) {
			//RapServidores servidor = new RapServidores();
			//servidor.setId(new RapServidoresId(solicitacaoCompra.getServidorExclusao().getId().getMatricula(), solicitacaoCompra.getServidorExclusao().getId().getVinCodigo()));
			RapServidores servidor =  getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(solicitacaoCompra.getServidorExclusao().getId().getMatricula(), solicitacaoCompra.getServidorExclusao().getId().getVinCodigo());
			solicitacaoCompraCloneLocal.setServidorExclusao(servidor);
		}

		if(solicitacaoCompra.getServidorOrcamento() != null) {
			//RapServidores servidor = new RapServidores();
			//servidor.setId(new RapServidoresId(solicitacaoCompra.getServidorOrcamento().getId().getMatricula(), solicitacaoCompra.getServidorOrcamento().getId().getVinCodigo()));
			RapServidores servidor =  getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(solicitacaoCompra.getServidorOrcamento().getId().getMatricula(), solicitacaoCompra.getServidorOrcamento().getId().getVinCodigo());
			solicitacaoCompraCloneLocal.setServidorOrcamento(servidor);
		}

		if(solicitacaoCompra.getServidor() != null) {
			//RapServidores servidor = new RapServidores();
			//servidor.setId(new RapServidoresId(solicitacaoCompra.getServidor().getId().getMatricula(), solicitacaoCompra.getServidor().getId().getVinCodigo()));
			RapServidores servidor =  getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(solicitacaoCompra.getServidor().getId().getMatricula(), solicitacaoCompra.getServidor().getId().getVinCodigo());
			solicitacaoCompraCloneLocal.setServidor(servidor);
		}

		if(solicitacaoCompra.getNaturezaDespesa() != null) {
			//FsoNaturezaDespesa naturezaDespesa = new FsoNaturezaDespesa();
			//naturezaDespesa.setId(new FsoNaturezaDespesaId(solicitacaoCompra.getNaturezaDespesa().getId().getGndCodigo(), solicitacaoCompra.getNaturezaDespesa().getId().getCodigo()));
			FsoNaturezaDespesa naturezaDespesa = getCadastrosBasicosOrcamentoFacade().obterNaturezaDespesa(new FsoNaturezaDespesaId(solicitacaoCompra.getNaturezaDespesa().getId().getGndCodigo(), solicitacaoCompra.getNaturezaDespesa().getId().getCodigo()));
			solicitacaoCompraCloneLocal.setNaturezaDespesa(naturezaDespesa);
		}
		

		return solicitacaoCompraCloneLocal;
	}	

	public void atualizarSolicitacaoCompra(
			ScoSolicitacaoDeCompra solicitacaoDeCompra,
			ScoSolicitacaoDeCompra solicitacaoDeCompraClone)
			throws ApplicationBusinessException {
		RapServidores servidor = getServidorLogadoFacade().obterServidorLogado();
		this.validarCompraMaterialDireto(solicitacaoDeCompra, servidor);
		this.validarJustificativas(solicitacaoDeCompra);
		ScoSolicitacaoDeCompra solicitacaoDeCompraOld = solicitacaoDeCompraClone;

		this.validaDataSolicitacao(solicitacaoDeCompra);
		this.validaValorUnitarioPrevisto(solicitacaoDeCompra);
		this.setaExclusaoAlteracao(solicitacaoDeCompra, solicitacaoDeCompraOld);
		this.setaServidorAlteracao(solicitacaoDeCompra, solicitacaoDeCompraOld);
		this.setaServidorOrcamentoAlteracao(solicitacaoDeCompra, solicitacaoDeCompraOld);
		this.validaServidorComprador(solicitacaoDeCompra,	solicitacaoDeCompraOld);
		this.setaEfetiva(solicitacaoDeCompra, solicitacaoDeCompraOld);
		this.setaQtdeAprovadaAlteracao(solicitacaoDeCompra,	solicitacaoDeCompraOld);
		this.validaQuantidadeAprovadaValorUnitarioPrevistoAlteracao(solicitacaoDeCompra, solicitacaoDeCompraOld);
				
		this.validaProximoPontoParada(solicitacaoDeCompra, solicitacaoDeCompraOld);
		
		validarRegrasOrcamentarias(solicitacaoDeCompra, solicitacaoDeCompraClone);

		solicitacaoDeCompra.setUnidadeMedida(solicitacaoDeCompra.getMaterial()
				.getUnidadeMedida());
		
		if (solicitacaoDeCompra.getVerbaGestao() != null && solicitacaoDeCompra.getConvenioFinanceiro() == null) {
			
			FsoConveniosFinanceiro convFin = this.getOrcamentoFacade().obterFsoConveniosFinanceiroPorChavePrimaria(solicitacaoDeCompra.getVerbaGestao().getSeq()); 
			
			if (convFin != null) {
				solicitacaoDeCompra.setConvenioFinanceiro(convFin);
			}
		}
		
		/***** Atualiza Solicitacao de Compras ****/
		//try {
		this.getScoSolicitacoesDeComprasDAO().atualizar(solicitacaoDeCompra);
		this.getScoSolicitacoesDeComprasDAO().flush();
		
		this.criaJournalDevolvidoAlteracao(solicitacaoDeCompra, solicitacaoDeCompraOld);
	}

	/**
	 * Autoriza uma lista de codigos de solicitacoes de compras
	 * @param listaSolicitacoes
	 * @throws BaseException 
	 */
	public void autorizarListaSolicitacaoCompras(List<Integer> listaSolicitacoes) throws ApplicationBusinessException {
		
		AghParametros parametroPpsPadrao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_PADRAO_AUTORIZA_SC);

		if (parametroPpsPadrao == null  || parametroPpsPadrao.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_PARAMETRO_PADRAO_AUTORIZACAO_NAO_ENCONTRADO);
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		for (Integer item : listaSolicitacoes) {

			ScoSolicitacaoDeCompra solicitacao = getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(item);
			ScoSolicitacaoDeCompra solicitacaoOld = getScoSolicitacoesDeComprasDAO().obterOriginal(item);

			ScoPontoParadaSolicitacao ppsPadrao = this.getScoPontoParadaSolicitacaoDAO().obterPorChavePrimaria(parametroPpsPadrao.getVlrNumerico().shortValue());
			
			if (ppsPadrao == null) {
				throw new ApplicationBusinessException(
						SolicitacaoCompraRNExceptionCode.MENSAGEM_PARAMETRO_PPS_PADRAO_NAO_DEFINIDO);
			}
			
			solicitacao.setServidorAutorizacao(servidorLogado);
			solicitacao.setPontoParada(solicitacao.getPontoParadaProxima());
			solicitacao.setPontoParadaProxima(ppsPadrao);
			solicitacao.setDtAutorizacao(new Date());
			desfazerDevolucao(solicitacao);
			this.atualizarSolicitacaoCompra(solicitacao, solicitacaoOld);
		}

	}
	
	/**
	 * Insere na journal que nao é journal uma atualização de ponto de parada
	 * @param numeroSolicitacao
	 * @param proximoPontoParada
	 */
	public void inserirAtualizacaoSolicitacaoComprasJournal(Integer numeroSolicitacao, ScoPontoParadaSolicitacao proximoPontoParada) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoScJn jn = new ScoScJn();
		jn.setNumero(numeroSolicitacao);			
		jn.setDevolucao(false);
		jn.setOperacao(DominioOperacoesJournal.UPD);
		jn.setPontoParadaSolicitacao(proximoPontoParada);
		jn.setServidor(servidorLogado);
		jn.setNomeUsuario(servidorLogado.getUsuario());
		
		getScoScJnDAO().persistir(jn);
	}
	
	/**
	 * Encaminha uma lista de códigos de solicitação de compras para determinado ponto de parada
	 * @param listaSolicitacoes
	 * @param pontoParadaAtual
	 * @param proximoPontoParada
	 * @param funcionarioComprador
	 * @param autorizacaoChefia
	 * @throws BaseException 
	 */
	public void encaminharListaSolicitacaoCompras(
			List<Integer> listaSolicitacoes,
			ScoPontoParadaSolicitacao pontoParadaAtual,
			ScoPontoParadaSolicitacao proximoPontoParada,
			RapServidores funcionarioComprador,
			Boolean autorizacaoChefia) throws BaseException {

		// não permite encaminhar para um ponto de parada "comprador" sem o
		// comprador definido
		if (this.getComprasCadastrosBasicosFacade()
				.verificarPontoParadaComprador(proximoPontoParada)
				&& funcionarioComprador == null) {
			throw new ApplicationBusinessException(
					SolicitacaoCompraRNExceptionCode.MENSAGEM_ENCAMINHAR_SCO_RN_01);
		}

		RapServidores comprador = null;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		for (Integer item : listaSolicitacoes) {

			if (funcionarioComprador != null) {
				comprador = this.getRegistroColaboradorFacade().obterServidor(
						funcionarioComprador);
			}

			ScoSolicitacaoDeCompra solicitacao = getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(item);
			ScoSolicitacaoDeCompra solicitacaoOld = getScoSolicitacoesDeComprasDAO().obterOriginal(item);

			
			
			if (!autorizacaoChefia) {
				// remove a autorizacao quando volta para o ponto SL ou CH
				if ((Objects.equals(proximoPontoParada.getTipoPontoParada(), DominioTipoPontoParada.SL) ||
						Objects.equals(proximoPontoParada.getTipoPontoParada(), DominioTipoPontoParada.CH)) &&
						(solicitacao.getDtAutorizacao() != null || solicitacao.getServidorAutorizacao() != null)) {
					solicitacao.setDtAutorizacao(null);
					solicitacao.setServidorAutorizacao(null);
				}
				// nao permite encaminhar para comprador ou licitacao se nao esta autorizada
				if ((Objects.equals(proximoPontoParada.getTipoPontoParada(), DominioTipoPontoParada.CP) ||
						Objects.equals(proximoPontoParada.getTipoPontoParada(), DominioTipoPontoParada.LI)) &&
						(solicitacao.getDtAutorizacao() == null || solicitacao.getServidorAutorizacao() == null)) {
						throw new ApplicationBusinessException(SolicitacaoCompraRNExceptionCode.MENSAGEM_ENCAMINHAR_SCO_RN_02);
				}	
			} else {
				solicitacao.setDtAutorizacao(new Date());
				solicitacao.setServidorAutorizacao(servidorLogado);
			}
			
			solicitacao.setPontoParada(solicitacao.getPontoParadaProxima());
			solicitacao.setPontoParadaProxima(proximoPontoParada);			
			
			if (funcionarioComprador != null) {
				solicitacao.setServidorCompra(comprador);
			}

			desfazerDevolucao(solicitacao);
			this.atualizarSolicitacaoCompra(solicitacao, solicitacaoOld);
		}
	}
	
	/**
	 * #25484 RN1
	 * @param listaSolicitacoes
	 * @throws ApplicationBusinessException
	 */
	public void gerarParcelasPorScsMateriaisDiretos(List<Integer> listaSolicitacoes) throws ApplicationBusinessException{
		
		AghParametros centroCustoAplic = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.CENTRO_CUSTO_APLIC);
		AghParametros centroCustoSolic = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.CENTRO_CUSTO_SOLIC);
		Integer CcAplic = centroCustoAplic.getVlrNumerico().intValue();
		Integer CcSolic = centroCustoSolic.getVlrNumerico().intValue();
		for (Integer item : listaSolicitacoes) {
			ScoSolicitacaoDeCompra solicitacao = getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(item);
			Integer codigoMaterial = solicitacao.getMaterial().getCodigo();
			Integer codigoCCT = solicitacao.getCentroCusto().getCodigo();
			Integer slcNumero = solicitacao.getNumero();
			// executar consulta C1
			Boolean achouSC = Boolean.FALSE;
			Boolean achouSP = Boolean.FALSE;
			
			List<ScoItemAutorizacaoForn> itemAfs = getScoItemAutorizacaoFornDAO().listarAfsAssociadasComSaldo(codigoMaterial, codigoCCT, CcAplic, CcSolic);
			if(!itemAfs.isEmpty()){
				for (ScoItemAutorizacaoForn scoItemAutorizacaoForn : itemAfs) {
					Integer afnNumero = scoItemAutorizacaoForn.getId().getAfnNumero();
					Integer iafNumero = scoItemAutorizacaoForn.getId().getNumero();
					QuantidadeProgramadaRecebidaItemVO qtdSaldo  = null;
					//consulta C2
					qtdSaldo = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterSaldoProgramado(afnNumero, iafNumero);
					if(qtdSaldo.getQuantidadeEntregueTotal()!=null && qtdSaldo.getQuantidadeTotal()!=null){
					 //C3
						achouSP=Boolean.TRUE;
						Integer qtdSolicitacao = scoSolicitacaoProgramacaoEntregaDAO.obterQtdeSolicPorNumeroSolicitacao(slcNumero);
						
						if(qtdSolicitacao == null){
							qtdSolicitacao = 0;
						}
						
						Integer saldoDisponivel = null;
						if(qtdSolicitacao!=null &&  solicitacao.getQtdeAprovada()!=null){
							Long qtdAprovada = solicitacao.getQtdeAprovada();
							if(qtdSolicitacao < qtdAprovada){
								 achouSC = Boolean.FALSE;
								//executar RN2
								Integer qtdSolicitada = scoItemAutorizacaoForn.getQtdeSolicitada();
								Integer qtdRecebida = scoItemAutorizacaoForn.getQtdeRecebida();
								Double valorUnitario = scoItemAutorizacaoForn.getValorUnitario();
								Date dtPrevEntrega = null;
								if(scoItemAutorizacaoForn.getAutorizacoesForn()!=null){
									dtPrevEntrega = scoItemAutorizacaoForn.getAutorizacoesForn().getDtPrevEntrega();
								}	
								//RN2
								saldoDisponivel = consultarSaldoDisponivel(afnNumero, iafNumero, qtdSolicitada, qtdRecebida);
								if(saldoDisponivel>0){
									//c4
									 List<ScoProgEntregaItemAutorizacaoFornecimento>  progEntrega = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterParcelasAssociadas(afnNumero, iafNumero);
									 if(progEntrega!=null && !progEntrega.isEmpty()){
										
										 ScoProgEntregaItemAutorizacaoFornecimento progEntregaItem = progEntrega.get(0);
										 Integer seq = progEntregaItem.getId().getSeq();
										 Integer parcela = progEntregaItem.getId().getParcela();
										 Integer qtde = progEntregaItem.getQtde();
										 Double valor = progEntregaItem.getValorEfetivado();
										 //atualizar a maior parcela
										 atualizarParcela(progEntregaItem, qtdAprovada, saldoDisponivel, valorUnitario, slcNumero);
										 //Executar U1 e I2
										 //PEA_IAF_AFN_NUMERO, PEA_IAF_NUMERO NUMBER, PEA_SEQ NUMBER,PEA_PARCELA, QTDE, VALOR
										 incluirSolicitacaoProgEntregas(afnNumero, iafNumero,seq, parcela, qtde, valor, slcNumero);
									 }else{
										 //Executar I1 e I2
										 inserirParcelas(afnNumero, iafNumero, qtdAprovada, saldoDisponivel, valorUnitario, slcNumero, dtPrevEntrega);
									 }
									 
								}
							}else{
								// atualiza solicitao compra U3
								achouSC = Boolean.TRUE;
								atualizaSolicitacaoDeCompra(solicitacao);
							}
						}
						if(!achouSC && achouSP && qtdSolicitacao!=null){
							 //Se chegar no final dos itens e não atender a SC, atualizar a solicitação de compra atual e incluir uma nova solicitação (U2 e I3).
							incluirSolicitacaoDeCompra(solicitacao,qtdSolicitacao);
						 }
					 }
				}	 
			}
		}
	}
	/**
	 *  #25484 RN2
	 * @param afnNumero
	 * @param iafNumero
	 * @param qtdSolicitada
	 * @param qtdRecebida
	 * @return
	 */
	public Integer consultarSaldoDisponivel(Integer afnNumero,Integer iafNumero, Integer qtdSolicitada, Integer qtdRecebida ){
		QuantidadeProgramadaRecebidaItemVO qtdProg = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterSaldoProgramado(afnNumero, iafNumero);
		Integer saldoParcelas = qtdProg.getQuantidadeTotal() - qtdProg.getQuantidadeEntregueTotal();
		Integer saldoItem = qtdSolicitada - qtdRecebida;
		Integer saldoDisponivel = saldoItem - saldoParcelas;
		return saldoDisponivel;
	}
	
	/**
	 * #25484 RN3
	 * @param iafAfnNum
	 * @param iafNumero
	 * @return
	 */
	public Integer obterNumeroProximaParcela(Integer iafAfnNum, Integer iafNumero){
		Integer numeroMaximo = getAutFornecimentoFacade().obterMaxNumeroParcela(iafAfnNum, iafNumero);
		numeroMaximo++;
		return numeroMaximo;
	}
	
	/**
	 * 
	 * @param solicitacaoDeCompra
	 * @param qtdeAprovada
	 * @throws ApplicationBusinessException
	 * obterServidor
	 */
	private RapServidores obterServidorPlanejamento() throws ApplicationBusinessException{
		AghParametros matriculaPlanejamentoParametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_MATR_CHEFE_PLANEJ);
		AghParametros vinculoPlanejamentoParametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_VIN_CHEFE_PLANEJ);
		Integer matriculaChefePlanejamento = matriculaPlanejamentoParametro.getVlrNumerico().intValue();
		Short   vinculoChefePlanejamento = vinculoPlanejamentoParametro.getVlrNumerico().shortValue();
		RapServidores servidorPlanejamento =  getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(matriculaChefePlanejamento, vinculoChefePlanejamento);
		return servidorPlanejamento;
	}
	
	/**
	 * #25484 I1 Incluir parcela
	 * @param iafAfnNum
	 * @param iafNumero
	 * @param qtdAprovada
	 * @param saldoDisponivel
	 * @param valorUnitario
	 * @param slcNumero
	 * @throws ApplicationBusinessException
	 */
	public void inserirParcelas(Integer iafAfnNum, Integer iafNumero, Long qtdAprovada, Integer saldoDisponivel, Double valorUnitario, Integer slcNumero, Date dtPrevEntrega)throws ApplicationBusinessException{
		RapServidores servidorPlanejamento =  obterServidorPlanejamento();
		ScoProgEntregaItemAutorizacaoFornecimento itemAfs = new ScoProgEntregaItemAutorizacaoFornecimento();
		ScoProgEntregaItemAutorizacaoFornecimentoId id = new ScoProgEntregaItemAutorizacaoFornecimentoId();
		id.setIafNumero(iafNumero);
		id.setIafAfnNumero(iafAfnNum);
		id.setParcela(obterNumeroProximaParcela(iafAfnNum, iafNumero));
		id.setSeq(Integer.valueOf(1));
		itemAfs.setId(id);
		itemAfs.setDtGeracao(new Date());
		itemAfs.setDtPrevEntrega(dtPrevEntrega);
		Integer qtde = 0;
		Double valorTotal = Double.valueOf(0);
		if(qtdAprovada <= saldoDisponivel){
			qtde = qtde + qtdAprovada.intValue();
			valorTotal = qtdAprovada * valorUnitario + valorTotal;
		}else{
			qtde = qtde + saldoDisponivel;
			valorTotal = saldoDisponivel * valorUnitario + valorTotal;
		}
		itemAfs.setQtde(qtde);
		itemAfs.setValorTotal(valorTotal);
		itemAfs.setRapServidor(servidorPlanejamento);
		itemAfs.setRapServidorAlteracao(servidorPlanejamento);
		itemAfs.setRapServidorLibPlanej(servidorPlanejamento);
		itemAfs.setQtdeEntregue(Integer.valueOf(0));
		itemAfs.setIndPlanejamento(Boolean.TRUE);
		itemAfs.setIndAssinatura(Boolean.FALSE);
		itemAfs.setIndEmpenhada(DominioAfEmpenhada.N);
		itemAfs.setIndEnvioFornecedor(Boolean.FALSE);
		itemAfs.setIndRecalculoAutomatico(Boolean.FALSE);
		itemAfs.setIndRecalculoManual(Boolean.FALSE);
		itemAfs.setIndCancelada(Boolean.FALSE);
		itemAfs.setValorEfetivado(Double.valueOf(0));
		itemAfs.setIndEfetivada(Boolean.FALSE);
		itemAfs.setIndEntregaImediata(Boolean.FALSE);
		itemAfs.setQtdeEntregueProv(Integer.valueOf(0));
		itemAfs.setQtdeEntregueAMais(Integer.valueOf(0));
		itemAfs.setIndTramiteInterno(Boolean.FALSE);
		itemAfs.setIndConversaoUnidade(Boolean.FALSE);
		itemAfs.setIndPublicado(Boolean.FALSE);
		itemAfs.setSlcNumero(slcNumero);
		this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().persistir(itemAfs);
		this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
		Integer seq = itemAfs.getId().getSeq();
		Integer parcela = itemAfs.getId().getParcela();
		Double valor = itemAfs.getValorEfetivado();
		incluirSolicitacaoProgEntregas(iafAfnNum, iafNumero,seq, parcela, qtde, valor, slcNumero);
	}
	/**
	 * #25484  I2 incluirSolicitacaoProgEntregas
	 * @param iafAfnNum
	 * @param iafNumero
	 * @param seq
	 * @param parcela
	 * @param qtde
	 * @param valor
	 * @param SlcNumero
	 * @throws ApplicationBusinessException
	 * Incluir solicitação de programação de entregas
	 */
	public void incluirSolicitacaoProgEntregas(Integer iafAfnNum, Integer iafNumero, Integer seq ,Integer parcela ,Integer qtde ,Double valor,Integer SlcNumero)throws ApplicationBusinessException{
		ScoProgEntregaItemAutorizacaoFornecimentoId id = new ScoProgEntregaItemAutorizacaoFornecimentoId();
		id.setIafNumero(iafNumero);
		id.setIafAfnNumero(iafAfnNum);
		id.setParcela(parcela);
		id.setSeq(seq);
		ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAf = getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(id);
		ScoSolicitacaoProgramacaoEntrega scoSolicitacaoProgramacaoEntrega = new  ScoSolicitacaoProgramacaoEntrega();
		scoSolicitacaoProgramacaoEntrega.setProgEntregaItemAf(progEntregaItemAf);
		scoSolicitacaoProgramacaoEntrega.setQtde(qtde);
		scoSolicitacaoProgramacaoEntrega.setValor(valor);
		ScoSolicitacaoDeCompra solicitacao = getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(SlcNumero);
		scoSolicitacaoProgramacaoEntrega.setSolicitacaoCompra(solicitacao);
		scoSolicitacaoProgramacaoEntrega.setIndPrioridade(Short.valueOf("1"));
		scoSolicitacaoProgramacaoEntrega.setQtdeEntregue(Integer.valueOf(0));
		scoSolicitacaoProgramacaoEntrega.setValorEfetivado(Double.valueOf(0));
		this.getScoSolicitacaoProgramacaoEntregaDAO().persistir(scoSolicitacaoProgramacaoEntrega);
		this.getScoSolicitacaoProgramacaoEntregaDAO().flush();
		scoSolicitacaoProgramacaoEntrega.getSeq();
		String mensagem =  getResourceBundleValue("MENSAGEM_ATUALIZADA_PARCELA_SC", parcela.toString(), scoSolicitacaoProgramacaoEntrega.getSolicitacaoCompra().getNumero().toString());
		logInfo(mensagem);
	}
		
	/**
	 * #25484 I3 Incluir solicitação de compra
	 * @param solicitacaoDeCompra
	 * @param qtdeAprovada
	 * @throws ApplicationBusinessException
	 */
	public void incluirSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra, Integer qtdeSolic) throws ApplicationBusinessException{
		ScoSolicitacaoDeCompra newSolicitacao = new ScoSolicitacaoDeCompra();
		try {
			newSolicitacao = (ScoSolicitacaoDeCompra) BeanUtils.cloneBean(solicitacaoDeCompra);
		} catch (Exception e) {
			throw new ApplicationBusinessException((BaseException) e);
		}
		newSolicitacao.setNumero(null);
		newSolicitacao.setVersion(null);
		RapServidores servidor =  obterServidorPlanejamento();
		newSolicitacao.setServidor(servidor);
		Long qtdeAprovada = solicitacaoDeCompra.getQtdeAprovada();
		Long newQtdeAprovada = qtdeAprovada - qtdeSolic;
		newSolicitacao.setQtdeAprovada(newQtdeAprovada);
		newSolicitacao.setDtSolicitacao(new Date());
		newSolicitacao.setJustificativaUso(getResourceBundleValue("JUSTIFICATIVA_GERACAO_SC",  solicitacaoDeCompra.getNumero().toString()));
		newSolicitacao.setFases(null);
		getScoSolicitacoesDeComprasDAO().persistir(newSolicitacao);
		getScoSolicitacoesDeComprasDAO().flush();
		Integer newSolicNumero =  newSolicitacao.getNumero();
		atualizarSolicitacaoCompras(solicitacaoDeCompra, qtdeSolic, newSolicNumero);
	}
	
	/**
	 * #25484 U1 atualiza Parcela
	 * 
	 */
	public void atualizarParcela(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItem,
			Long qtdAprovada, Integer saldoDisponivel, Double valorUnitario, Integer slcNumero) throws ApplicationBusinessException{
		RapServidores servidorPlanejamento =  obterServidorPlanejamento();
		Integer qtde = progEntregaItem.getQtde();
		Double valorTotal = progEntregaItem.getValorTotal();
		if(qtdAprovada <= saldoDisponivel){
			qtde = qtde + qtdAprovada.intValue();
			valorTotal = qtdAprovada * valorUnitario + valorTotal;
		}else{
			qtde = qtde + saldoDisponivel;
			valorTotal = saldoDisponivel * valorUnitario + valorTotal;
		}
		progEntregaItem.setRapServidorAlteracao(servidorPlanejamento);
		progEntregaItem.setRapServidorLibPlanej(servidorPlanejamento);
		progEntregaItem.setIndPlanejamento(Boolean.TRUE);
		progEntregaItem.setDtAlteracao(new Date());
		progEntregaItem.setDtLibPlanejamento(new Date());
		progEntregaItem.setQtde(qtde);
		progEntregaItem.setValorTotal(valorTotal);
		this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().atualizar(progEntregaItem);
		this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
		String mensagem = this.getResourceBundleValue("MENSAGEM_ATUALIZADA_PARCELA_SC", progEntregaItem.getId().getParcela().toString(), slcNumero);
		logInfo(mensagem);
	}
	
	/**
	 *  #25484 U2 atualiza solicitação de compra
	 * @param solicitacaoDeCompra
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSolicitacaoCompras(ScoSolicitacaoDeCompra solicitacaoDeCompra, Integer qtdeSolic, Integer newSolicNumero)throws ApplicationBusinessException{
		solicitacaoDeCompra.setQtdeAprovada(qtdeSolic.longValue());
		solicitacaoDeCompra.setDtAlteracao(new Date());
		RapServidores servidor =  obterServidorPlanejamento();
		solicitacaoDeCompra.setJustificativaUso(this.getResourceBundleValue("JUSTIFICATIVA_ALTERACAO_SC",  newSolicNumero.toString()));
		solicitacaoDeCompra.setPontoParada(solicitacaoDeCompra.getPontoParadaProxima());
		solicitacaoDeCompra.getPontoParadaProxima().setTipoPontoParada(DominioTipoPontoParada.LI);
		solicitacaoDeCompra.setServidorAlteracao(servidor);
		getScoSolicitacoesDeComprasDAO().atualizar(solicitacaoDeCompra);
	}
	
	/**
	 * #25484 U3 atualiza solicitação de compra
	 * @param solicitacaoDeCompra
	 */
	public void atualizaSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra){
		solicitacaoDeCompra.setPontoParada(solicitacaoDeCompra.getPontoParadaProxima());
		solicitacaoDeCompra.getPontoParadaProxima().setTipoPontoParada(DominioTipoPontoParada.LI);
		getScoSolicitacoesDeComprasDAO().atualizar(solicitacaoDeCompra);
		getScoSolicitacoesDeComprasDAO().flush();
	}
	
	
	/**
	 * Define SC como não devolvida e sem justificativa de devolução.
	 * 
	 * @param solicitacao SC
	 */
	private void desfazerDevolucao(ScoSolicitacaoDeCompra solicitacao) {
		solicitacao.setDevolucao(false);
		solicitacao.setJustificativaDevolucao(null);
	}

	public ScoSolicitacaoDeCompra duplicarSCPorPAC(ScoSolicitacaoDeCompra solicitacaoDeCompra) 
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		solicitacaoDeCompra.setNumero(null);
		solicitacaoDeCompra.setServidor(servidorLogado);
		solicitacaoDeCompra.setServidorAutorizacao(null);
		solicitacaoDeCompra.setDtAutorizacao(null);	
		solicitacaoDeCompra.setServidorCompra(null);
		solicitacaoDeCompra.setDtSolicitacao(new Date());
		solicitacaoDeCompra.setDtDigitacao(new Date());
		solicitacaoDeCompra.setExclusao(false);
		solicitacaoDeCompra.setUrgente(false);
		solicitacaoDeCompra.setDevolucao(false);
		solicitacaoDeCompra.setMotivoExclusao(null);
		
		solicitacaoDeCompra.setDtExclusao(null);		
		solicitacaoDeCompra.setDtAnalise(null);
		solicitacaoDeCompra.setMotivoUrgencia(null);
		solicitacaoDeCompra.setJustificativaDevolucao(null);
		solicitacaoDeCompra.setServidorExclusao(null);
		solicitacaoDeCompra.setServidorAlteracao(null);
		solicitacaoDeCompra.setDtAlteracao(null);
		solicitacaoDeCompra.setQtdeEntregue(null);
		solicitacaoDeCompra.setEfetivada(false);
		solicitacaoDeCompra.setPaciente(null);
		solicitacaoDeCompra.setSlcNumeroVinculado(null);
		solicitacaoDeCompra.setFases(null);
		
		this.inserirSolicitacaoCompra(solicitacaoDeCompra, false);
		return solicitacaoDeCompra;
		
	}
	
	/**
	 * Inativa uma lista de solicitações de compras
	 * @param listaSolicitacoes
	 * @param motivoExclusao
	 * @param servidor
	 * @throws BaseException 
	 */
	public void inativarListaSolicitacaoCompras(
			List<Integer> listaSolicitacoes, String motivoExclusao)
			throws BaseException {
		RapServidores servidor = this.getServidorLogadoFacade().obterServidorLogado();
		for (Integer item : listaSolicitacoes) {
			ScoSolicitacaoDeCompra solicitacao = getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(item);
			ScoSolicitacaoDeCompra solicitacaoOld = getScoSolicitacoesDeComprasDAO().obterOriginal(item);
			
			solicitacao.setExclusao(true);
			solicitacao.setMotivoExclusao(motivoExclusao);
			solicitacao.setDtExclusao(new Date());
			solicitacao.setServidorExclusao(servidor);

			this.atualizarSolicitacaoCompra(solicitacao, solicitacaoOld);
		}
	}
	
	/**
	 * Valida se centro de custo aplicação da SC está parametrizado.
	 * 
	 * @param novaSc SC.
	 * @param scAntiga 
	 * @throws BaseException
	 */
	private void validarCentroCustoParam(ScoSolicitacaoDeCompra novaSc, ScoSolicitacaoDeCompra scAntiga)
			throws ApplicationBusinessException {
		if (scAntiga != null && !CoreUtil.modificados(novaSc.getMaterial(), scAntiga.getMaterial())
				&& !CoreUtil.modificados(novaSc.getCentroCusto(), scAntiga.getCentroCusto())
				&& !CoreUtil.modificados(novaSc.getValorUnitPrevisto(), scAntiga.getValorUnitPrevisto())
				&& !CoreUtil.modificados(novaSc.getCentroCustoAplicada(), scAntiga.getCentroCustoAplicada())) {
			return;
		} else if (!getCadastrosBasicosOrcamentoFacade().isCentroCustoValidScParam(
				novaSc.getMaterial(), novaSc.getCentroCusto(), novaSc.getValorUnitPrevisto(),
				novaSc.getCentroCustoAplicada())) {
			SolicitacaoCompraRNExceptionCode.CENTRO_CUSTO_NAO_PERMITIDO_PARA_SC
					.throwException();
		}
	}

	/**
	 * Valida se natureza da SC está parametrizado.
	 * 
	 * @param novaSc SC.
	 * @param solicitacaoDeCompraClone 
	 * @throws BaseException
	 */
	private void validarNaturezaParam(ScoSolicitacaoDeCompra novaSc, ScoSolicitacaoDeCompra scAntiga) throws ApplicationBusinessException {
		if (scAntiga != null && !CoreUtil.modificados(novaSc.getMaterial(), scAntiga.getMaterial())
				&& !CoreUtil.modificados(novaSc.getCentroCusto(), scAntiga.getCentroCusto())
				&& !CoreUtil.modificados(novaSc.getValorUnitPrevisto(), scAntiga.getValorUnitPrevisto())
				&& !CoreUtil.modificados(novaSc.getNaturezaDespesa(), scAntiga.getNaturezaDespesa())) {
			return;
		} else if (!getCadastrosBasicosOrcamentoFacade().isNaturezaValidScParam(
				novaSc.getMaterial(), novaSc.getCentroCusto(), novaSc.getValorUnitPrevisto(),
				novaSc.getNaturezaDespesa())) {
			SolicitacaoCompraRNExceptionCode.NATUREZA_NAO_PERMITIDA_PARA_SC.throwException();
			
		}
	}

	/**
	 * Verifica se o usuario possui permissão ao componente/método associado
	 * @param componente
	 * @param metodo
	 * @return
	 */
	private Boolean verificarPermissoesComponenteMetodo(String usuario, String componente, String metodo) {
		return cascaFacade.usuarioTemPermissao(usuario, componente, metodo);
	}
	
	/**
	 * Verifica se o usuario logado possui algum perfil relacionado às solicitações de compras
	 * @param usuario
	 * @param gravar
	 * @return Boolean
	 */
	public Boolean verificarPermissoesSolicitacaoCompras(String usuario, Boolean gravar) {
		Boolean ret = Boolean.FALSE;
		
		if (gravar) {
			ret = (this.verificarPermissoesComponenteMetodo(usuario, "cadastrarSolicitacaoCompras", GRAVAR) ||
					this.verificarPermissoesComponenteMetodo(usuario, "cadastrarSCPlanejamento", GRAVAR) || 
					this.verificarPermissoesComponenteMetodo(usuario, "cadastrarSCComprador", GRAVAR) ||
					this.verificarPermissoesComponenteMetodo(usuario, "cadastrarSCAreasEspecificas", GRAVAR) ||
					this.verificarPermissoesComponenteMetodo(usuario, "cadastrarSCChefias", GRAVAR) ||
					this.verificarPermissoesComponenteMetodo(usuario, "cadastrarSCGeral", GRAVAR));
		} else {
			ret = this.verificarPermissoesComponenteMetodo(usuario, "consultarSolicitacaoCompras", "visualizar");
		}
		return ret;
	}
	
	/**
	 * Valida se verba de gestão da SC está parametrizado.
	 * 
	 * @param novaSc SC.
	 * @param solicitacaoDeCompraClone 
	 * @throws BaseException 
	 */
	private void validarVerbaGestaoParam(
			ScoSolicitacaoDeCompra novaSc, ScoSolicitacaoDeCompra scAntiga) throws ApplicationBusinessException {
		if (scAntiga != null && !CoreUtil.modificados(novaSc.getMaterial(), scAntiga.getMaterial())
				&& !CoreUtil.modificados(novaSc.getCentroCusto(), scAntiga.getCentroCusto())
				&& !CoreUtil.modificados(novaSc.getValorUnitPrevisto(), scAntiga.getValorUnitPrevisto())
				&& !CoreUtil.modificados(novaSc.getVerbaGestao(), scAntiga.getVerbaGestao())) {
			return;
		} else if (!getCadastrosBasicosOrcamentoFacade().isVerbaGestaoValidScParam(
				novaSc.getMaterial(), novaSc.getCentroCusto(), novaSc.getValorUnitPrevisto(),
				novaSc.getVerbaGestao())) {
			SolicitacaoCompraRNExceptionCode.VERBA_GESTAO_NAO_PERMITIDA_PARA_SC
					.throwException();
		}
	}
	
	protected ICadastrosBasicosOrcamentoFacade getCadastrosBasicosOrcamentoFacade() {
		return cadastrosBasicosOrcamentoFacade;
	}
	
	/**
	 * Retorna do cadastro de pontos de parada o ponto de parada do solicitante
	 * @return ScoPontoParadaSolicitacao
	 */
	public ScoPontoParadaSolicitacao getPpsSolicitante()  {
		return this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.SL);
	}
	
	/**
	 * Retorna do cadastro de pontos de parada o ponto de parada da chefia
	 * @return ScoPontoParadaSolicitacao
	 */
	public ScoPontoParadaSolicitacao getPpsAutorizacao() {
		return this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.CH);
	}
	
	/**
	 * Retorna do cadastro de pontos de parada o ponto de parada do parecer tecnico
	 * @return ScoPontoParadaSolicitacao
	 */
	public ScoPontoParadaSolicitacao getPpsParecerTecnico() {
		return this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.PT);
	}
		
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected IOrcamentoFacade getOrcamentoFacade() {
		return this.orcamentoFacade;
	}

	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	

	protected IAutFornecimentoFacade getAutFornecimentoFacade(){
		return autFornecimentoFacade;
	}
	
	
	protected IPacienteFacade getPacienteFacade() {
		return this.pacienteFacade;
	}
	
	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO(){
		return scoItemAutorizacaoFornDAO;
	}
	
	protected ScoScJnDAO getScoScJnDAO() {
		return scoScJnDAO;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFasesSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}

	protected ScoModalidadeLicitacaoDAO getScoModalidadeLicitacaoDAO(){
		return scoModalidadeLicitacaoDAO;
	}
	
		
	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	protected ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return scoPontoParadaSolicitacaoDAO;
	}
	
	protected ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO(){
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}
	
	protected ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO() {
		return scoSolicitacaoProgramacaoEntregaDAO;
	}
	
	protected ScoCaracteristicaUsuarioCentroCustoDAO getScoCaracteristicaUsuarioCentroCustoDAO() {
		return scoCaracteristicaUsuarioCentroCustoDAO;
	}
	
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}