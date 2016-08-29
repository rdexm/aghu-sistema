package br.gov.mec.aghu.compras.pac.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoQuadroAprovacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoQuadroAprovacaoVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoVO;
import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;
import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoCriterioEscolhaProposta;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacaoId;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.suprimentos.vo.ScoFaseSolicitacaoVO;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPacVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Serviço de Licitações
 * 
 * Responsável pelas regras de negócio das licitações.
 * 
 * @author mlcruz
 */
@Stateless
public class ScoLicitacaoON extends BaseBusiness {

	@EJB
	private ScoItemLicitacaoRN scoItemLicitacaoRN;
	
	@EJB
	private ScoItemLicitacaoON scoItemLicitacaoON;
	
	@EJB
	private ScoPropostaFornecedorON scoPropostaFornecedorON;
	
	@EJB
	private ScoLicitacaoRN scoLicitacaoRN;
	
	@EJB
	private ScoItemPropostaFornecedorRN scoItemPropostaFornecedorRN;

	@EJB
	private DadosItemLicitacaoON dadosItemLicitacaoON;
	
	
	private static final String SEPARADOR = ";";

    private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

	private static final String PREFIXO_PAC = "LISTA_PACS_";
	private static final String PREFIXO_ITENS_PAC = "ITENS_PAC_";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private static final String EXTENSAO = ".csv";
	
	private static final String SIM = "Sim";

	private static final String NAO = "Não";
	
	/** Situações de julgamento de itens pendentes. */
	private static final List<DominioSituacaoJulgamento> SITUACAO_JULGAMENTO_PENDENTE = 
			Arrays.asList(DominioSituacaoJulgamento.TE, DominioSituacaoJulgamento.AH, DominioSituacaoJulgamento.EN);

	
	private static final Log LOG = LogFactory.getLog(ScoLicitacaoON.class);
	
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
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@Inject
	private ScoItemLicitacaoQuadroAprovacaoDAO scoItemLicitacaoQuadroAprovacaoDAO;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;

	private static final long serialVersionUID = 800226067495236163L;
	
	public enum LicitacaoONExceptionCode implements
	BusinessExceptionCode {
		MENSAGEM_ERRO_FREQ_COMPRAS, MENSAGEM_DT_LIMITE_ACEITE_MENOR, MENSAGEM_DT_LIM_AC_MAIOR_DT_ABERTURA,
		MENSAGEM_DT_PUBLICACAO_MENOR, MENSAGEM_ARTIGO_INCISO_OBRIG, FREQ_COMPRAS_SO_POD_ALTR_CHEF,
		FREQ_COMPRAS_MAIOR_PARAM, MENSAGEM_PAC_JA_JULGADO, MENSAGEM_OBRIGATORIO_TIPO_PREGAO, MENSAGEM_ERRO_DT_REC_NULA,
		MENSAGEM_ERRO_DT_ABERTURA_MAIOR, MENSAGEM_ERRO_DT_PUBL_NULA, MENSAGEM_ERRO_DT_RECEB_MAIOR, DATA_INVALIDA_PAC,
		FORMATO_DATA_INVALIDO
	}

	public void encaminharParecerTecnico(ScoLicitacao licitacao) {
		licitacao.setSituacao(DominioSituacaoLicitacao.PT);
		licitacao.setDtEnvioParecTec(new Date());
		getScoLicitacaoDAO().atualizar(licitacao);
	}

	public void encaminharComissao(ScoLicitacao licitacao) {
		licitacao.setSituacao(DominioSituacaoLicitacao.CL);
		licitacao.setDtEnvioComisLicit(new Date());
		getScoLicitacaoDAO().atualizar(licitacao);
	}
	
	
	public void persistirLicitacao(ScoLicitacao licitacao , ScoLicitacao licitacaoClone )
			throws BaseException{
		
		if (licitacao.getModalidadeLicitacao().getExigeTipo() && licitacao.getTipoPregao() == null) {
			throw new ApplicationBusinessException(LicitacaoONExceptionCode.MENSAGEM_OBRIGATORIO_TIPO_PREGAO);
		}

		/* RN1 - Estória #5195 - Gerar Pac */
		this.validaFrequenciaCompras(licitacao);
		
		/*#30439 - #5195 - Gerar Pac - Validação de datas */
		this.validarDtsPublicacaoRecebAbertura(licitacao);
		
		
		if (licitacao.getNumero() == null) {
			// INCLUIR
			this.getScoLicitacaoRN().inserirLicitacao(licitacao);
			this.getScoLicitacaoDAO().flush();
			
				
		} else {
			// EDITAR
			this.getScoLicitacaoRN().alterarLicitacao(licitacao , licitacaoClone);
			this.getScoLicitacaoDAO().flush();
		}
		
	}
	
	
	
	
	
	public void validaFrequenciaCompras(ScoLicitacao licitacao)	throws ApplicationBusinessException {		
		if (licitacao.getFrequenciaEntrega() != null) {
			if (licitacao.getFrequenciaEntrega() <= 0) {
				licitacao.setFrequenciaEntrega(null);
				throw new ApplicationBusinessException(LicitacaoONExceptionCode.MENSAGEM_ERRO_FREQ_COMPRAS);
			}
		}
	}
	
	
	public void validarDtsPublicacaoRecebAbertura(ScoLicitacao licitacao)	throws ApplicationBusinessException {		
		if (licitacao.getDthrAberturaProposta() != null) {
			if (licitacao.getDtLimiteAceiteProposta() ==null) {
				throw new ApplicationBusinessException(LicitacaoONExceptionCode.MENSAGEM_ERRO_DT_REC_NULA);
			}
			else {
				if (licitacao.getDthrAberturaProposta() != null && licitacao.getDthrAberturaProposta().before(licitacao.getDtLimiteAceiteProposta())){
					throw new ApplicationBusinessException(LicitacaoONExceptionCode.MENSAGEM_ERRO_DT_ABERTURA_MAIOR);
				}
				if (licitacao.getDtPublicacao()==null){
					throw new ApplicationBusinessException(LicitacaoONExceptionCode.MENSAGEM_ERRO_DT_PUBL_NULA);
				}
				else if (licitacao.getDtLimiteAceiteProposta() != null && licitacao.getDtLimiteAceiteProposta().before(licitacao.getDtPublicacao())){
					throw new ApplicationBusinessException(LicitacaoONExceptionCode.MENSAGEM_ERRO_DT_RECEB_MAIOR);
				}
			}
		}
		else {
			if (licitacao.getDtLimiteAceiteProposta()!=null){
				if (licitacao.getDtPublicacao()==null){
					throw new ApplicationBusinessException(LicitacaoONExceptionCode.MENSAGEM_ERRO_DT_PUBL_NULA);
				}
				else if (licitacao.getDthrAberturaProposta() != null && licitacao.getDthrAberturaProposta().before(licitacao.getDtLimiteAceiteProposta())){
					throw new ApplicationBusinessException(LicitacaoONExceptionCode.MENSAGEM_ERRO_DT_ABERTURA_MAIOR);
				}
			}
		}
	}
	
	protected ScoLicitacaoRN getScoLicitacaoRN() {
		return scoLicitacaoRN;
	}

	protected ScoPropostaFornecedorON getScoPropostaFornecedorON() {
		return scoPropostaFornecedorON;
	}
	
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	
	
	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}		
	
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	public List<PacParaJulgamentoVO> pesquisarPacsParaJulgamento(
			PacParaJulgamentoCriteriaVO criteria, Integer first, Integer max,
			String order, boolean asc) {
		List<PacParaJulgamentoVO> result = getScoLicitacaoDAO()
				.pesquisarPacsParaJulgamento(criteria, first, max, order, asc);
		
		for (PacParaJulgamentoVO pac : result) {
			pac.setQtdeItens(getScoItemLicitacaoDAO().contarItensLicitacao(pac.getNumero()).intValue());
			
			pac.setQtdeItensJulgados(getScoItemLicitacaoDAO().contarItensLicitacaoJulgados(pac.getNumero()).intValue());
		}
		
		return result;
	}

	/**
	 * Realiza as ações de julgar, desclassificar, cancelar ou deixar pendente um item de proposta do fornecedor
	 * @param itemProposta
	 * @param faseSolicitacao
	 * @param condicaoPagamentoEscolhida
	 * @param motivoCancelamento
	 * @param pendentePor
	 * @param criterioEscolha
	 * @param motivoDesclassificacao
	 * @throws BaseException 
	 */
	public void registrarJulgamentoPac(ScoItemPropostaFornecedor itemProposta, 
			ScoFaseSolicitacaoVO faseSolicitacao,
			ScoCondicaoPagamentoPropos condicaoPagamentoEscolhida,
			DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento,
			DominioSituacaoJulgamento pendentePor,
			ScoCriterioEscolhaProposta criterioEscolha,
			DominioMotivoDesclassificacaoItemProposta motivoDesclassificacao, RapServidores servidorLogado) throws BaseException {
		
		if (motivoCancelamento == null) {
			this.reverterCancelamentoPac(faseSolicitacao, servidorLogado);
		}
		if (motivoCancelamento != null) {
			this.cancelarPac(faseSolicitacao, motivoCancelamento, servidorLogado);
		} 
		if (pendentePor != null) {
			this.gravarPacJulgamentoPendente(faseSolicitacao, pendentePor);
		} 
		if (pendentePor == null) {
			this.reverterPacJulgamentoPendente(faseSolicitacao, pendentePor, motivoCancelamento);
		}
		
		this.getScoPropostaFornecedorON().executarAcoesJulgamento(
				itemProposta, faseSolicitacao, condicaoPagamentoEscolhida, motivoCancelamento, pendentePor, criterioEscolha, motivoDesclassificacao);

	}	
	
	/**
	 * Cancela um item da licitação
	 * @param faseSolicitacao
	 * @param motivoCancelamento
	 * @throws BaseException 
	 */
	private void cancelarPac(ScoFaseSolicitacaoVO faseSolicitacao,
			DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento, RapServidores servidorLogado)  throws BaseException{
		
		ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(faseSolicitacao.getItemLicitacao().getId());
		
		if (itemLicitacao != null) {
			
			this.getScoItemLicitacaoON().verificarPropostasItemLicitacaoEmAf(itemLicitacao.getId().getLctNumero(),	itemLicitacao.getId().getNumero());
			
			// Quando o Item da Licitação esta sendo cancelado deve-se setar o campo ind_exclusão p/ TRUE
			// e copiar a descrição do cancelamento para o campo MOTIVO_EXCLUSAO
			// As fases da solicitação deste item e seus pontos de parada também devem ser atualizados
			if (itemLicitacao.getMotivoCancel() == null){
				itemLicitacao.setExclusao(Boolean.TRUE);
				itemLicitacao.setMotivoExclusao(motivoCancelamento.getDescricao());										
				this.getScoItemLicitacaoON().realizarExclusaoLogica(itemLicitacao.getId().getLctNumero(), itemLicitacao.getId().getNumero(), motivoCancelamento.getDescricao());
			}
			
			itemLicitacao.setMotivoCancel(motivoCancelamento);
			itemLicitacao.setPropostaEscolhida(Boolean.TRUE);
			itemLicitacao.setSituacaoJulgamento(DominioSituacaoJulgamento.SJ);
						
			this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);
			
			// RN04 - se cancelar o item do pac e tiver propostas escolhidas, desfaz a escolha
			List<ScoItemPropostaFornecedor> listaPropostas = 
					this.getScoItemPropostaFornecedorDAO().pesquisarItemPropostaEscolhidaPorNumeroLicitacaoENumeroItem(faseSolicitacao.getItemLicitacao().getId().getLctNumero(), faseSolicitacao.getItemLicitacao().getId().getNumero());
			
			for (ScoItemPropostaFornecedor item : listaPropostas) {
				ScoItemPropostaFornecedor itProp = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(item.getId());
				itProp.setIndEscolhido(Boolean.FALSE);
				itProp.setDtEscolha(null);
				itProp.setCondicaoPagamentoPropos(null);
				itProp.setCriterioEscolhaProposta(null);
				this.getScoItemPropostaFornecedorRN().atualizarItemPropostaFornecedor(itProp);
			}			
		}
	}

		
	/**
	 * Desfaz o cancelamento de um item do PAC
	 * @param faseSolicitacao
	 * @throws BaseException 
	 */
	private void reverterCancelamentoPac(ScoFaseSolicitacaoVO faseSolicitacao, RapServidores servidorLogado)  throws BaseException {
		ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(faseSolicitacao.getItemLicitacao().getId());
		
		if (itemLicitacao != null) {
			
			this.getScoItemLicitacaoON().verificarPropostasItemLicitacaoEmAf(itemLicitacao.getId().getLctNumero(), itemLicitacao.getId().getNumero());

			// Quando o Item da Licitação estava cancelado e este cancelamento esta sendo desfeito 
			// deve-se setar o campo ind_exclusão p/ FALSE e retirar a descrição do motivo da exclusão			
			// As fases da solicitação deste item e seus pontos de parada também devem ser atualizados
			if (itemLicitacao.getMotivoCancel() != null){
				itemLicitacao.setExclusao(Boolean.FALSE);
				itemLicitacao.setMotivoExclusao(null);
				this.getScoItemLicitacaoON().reativarItemPac(itemLicitacao.getId().getLctNumero(), itemLicitacao.getId().getNumero());
			}
			
			itemLicitacao.setMotivoCancel(null);
			itemLicitacao.setPropostaEscolhida(Boolean.FALSE);
			
			this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);			
		}
	}

	/**
	 * Marca um item de licitação como pendente
	 * @param faseSolicitacao
	 * @param pendentePor
	 * @throws ApplicationBusinessException
	 */
	private void gravarPacJulgamentoPendente(ScoFaseSolicitacaoVO faseSolicitacao,
			DominioSituacaoJulgamento pendentePor) throws ApplicationBusinessException {

		ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(new ScoItemLicitacaoId(faseSolicitacao.getNumeroPac(), faseSolicitacao.getNumeroItemPac()));
		
		if (itemLicitacao != null) {
			
			this.getScoItemLicitacaoON().verificarPropostasItemLicitacaoEmAf(itemLicitacao.getId().getLctNumero(),	itemLicitacao.getId().getNumero());
			
			List<ScoItemPropostaFornecedor> listaPropostas = this.getScoItemPropostaFornecedorDAO().
					pesquisarItemPropostaPorNumeroLicitacaoENumeroItem(faseSolicitacao.getNumeroPac(), faseSolicitacao.getNumeroItemPac());
			
			for (ScoItemPropostaFornecedor item : listaPropostas) {
				ScoItemPropostaFornecedor itProp = this.getScoItemPropostaFornecedorDAO().obterPorChavePrimaria(item.getId());
				
				if (itProp != null) {
					itProp.setIndEscolhido(Boolean.FALSE);
					itProp.setDtEscolha(null);
					itProp.setCondicaoPagamentoPropos(null);
					itProp.setCriterioEscolhaProposta(null);
					this.getScoItemPropostaFornecedorRN().atualizarItemPropostaFornecedor(itProp);
				}
			}

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			itemLicitacao.setMotivoCancel(null);
			itemLicitacao.setPropostaEscolhida(Boolean.FALSE);
			itemLicitacao.setSituacaoJulgamento(pendentePor);
			itemLicitacao.setJulgParcial(true);
			itemLicitacao.setDtJulgParcial(new Date());
			itemLicitacao.setServidorJulgParcial(servidorLogado);
			this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);
		}
	}

	/**
	 * Remove o indicador de item da licitação pendente
	 * @param faseSolicitacao
	 * @param pendentePor
	 * @param motivoCancelamento
	 * @throws ApplicationBusinessException
	 */
	private void reverterPacJulgamentoPendente(ScoFaseSolicitacaoVO faseSolicitacao,
			DominioSituacaoJulgamento pendentePor,
			DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento) throws ApplicationBusinessException {		
		ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().obterPorChavePrimaria(new ScoItemLicitacaoId(faseSolicitacao.getNumeroPac(), faseSolicitacao.getNumeroItemPac()));
		
		if (itemLicitacao != null) {
			
			this.getScoItemLicitacaoON().verificarPropostasItemLicitacaoEmAf(itemLicitacao.getId().getLctNumero(),	itemLicitacao.getId().getNumero());
			
			if (motivoCancelamento == null) {
				itemLicitacao.setMotivoCancel(null);
			}
			itemLicitacao.setPropostaEscolhida(Boolean.FALSE);
			itemLicitacao.setSituacaoJulgamento(null);
			itemLicitacao.setJulgParcial(false);
			itemLicitacao.setDtJulgParcial(null);
			itemLicitacao.setServidorJulgParcial(null);
			this.getScoItemLicitacaoRN().atualizarItemLicitacao(itemLicitacao);			
		}
	}

	public void encaminharComprador(Set<Integer> ids, Boolean limpaParecer) 
			throws ApplicationBusinessException {
		for (Integer id : ids) {
			ScoLicitacao pac = getScoLicitacaoDAO()
					.obterPorChavePrimaria(id);
			
			if (DominioSituacaoLicitacao.JU.equals(pac.getSituacao()) && !limpaParecer) {
				throw new ApplicationBusinessException(
						LicitacaoONExceptionCode.MENSAGEM_PAC_JA_JULGADO);
			}
			
			pac.setSituacao(DominioSituacaoLicitacao.GR);
			
			if (limpaParecer) {
				pac.setDtEnvioParecTec(null);
			}
			getScoLicitacaoDAO().atualizar(pac);
		}
	}

	public List<ItemLicitacaoQuadroAprovacaoVO> pesquisarPacsQuadroAprovacao(Set<Integer> ids, Boolean assinatura) {
		List<ItemLicitacaoQuadroAprovacaoVO> itens = new ArrayList<ItemLicitacaoQuadroAprovacaoVO>();
		String[][] params = assinatura ? getAssinaturas() : null;
		
		// Itens sem proposta.
		List<ItemLicitacaoQuadroAprovacaoVO> itensSemProposta = getScoItemLicitacaoQuadroAprovacaoDAO()
				.pesquisarItensSemProposta(ids, assinatura);

		for (ItemLicitacaoQuadroAprovacaoVO item : itensSemProposta) {
			setAssinaturas(item, params);
			item.setSemProposta(true);

			if (item.getMotivoCancelamento() != null) {
				item.setMotivo(item.getMotivoCancelamento().getDescricao());
			}
		}

		itens.addAll(itensSemProposta);

		// Itens com proposta.
		List<ItemLicitacaoQuadroAprovacaoVO> itensComProposta = getScoItemLicitacaoQuadroAprovacaoDAO()
				.pesquisarItensComProposta(ids, assinatura);

		for (ItemLicitacaoQuadroAprovacaoVO item : itensComProposta) {
			setAssinaturas(item, params);

			if (item.getMotivoCancelamento() == null) {
				if (Boolean.FALSE.equals(item.getExcluido())) {
					item.setPendente(true);

					if (item.getSituacaoJulgamento() != null) {
						item.setMotivo(item.getSituacaoJulgamento()
								.getDescricao());
					}
				}
			} else {
				item.setMotivo(item.getMotivoCancelamento().getDescricao());
			}
		}

		itens.addAll(itensComProposta);

		// Itens com proposta escolhida.
		List<ItemLicitacaoQuadroAprovacaoVO> itensComPropostaEscolhida = getScoItemLicitacaoQuadroAprovacaoDAO()
				.pesquisarItensComPropostaEscolhida(ids, assinatura);

		for (ItemLicitacaoQuadroAprovacaoVO item : itensComPropostaEscolhida) {
			setAssinaturas(item, params);

			Long countParcelas = getScoParcelasPagamentoDAO()
					.contarParcelasPagamentoProposta(item.getCondicao());
			item.setParcelas(countParcelas != null ? countParcelas.shortValue() : Short.valueOf("0"));
		}

		itens.addAll(itensComPropostaEscolhida);

		Collections.sort(itens,
				new Comparator<ItemLicitacaoQuadroAprovacaoVO>() {
					@Override
					public int compare(ItemLicitacaoQuadroAprovacaoVO o1,
							ItemLicitacaoQuadroAprovacaoVO o2) {
						int comp = o1.getNumeroPac().compareTo(
								o2.getNumeroPac());

						if (comp == 0) {
							return o1.getNumeroItem().compareTo(
									o2.getNumeroItem());
						} else {
							return comp;
						}
					}
				});

		return itens;
	}

	/** Define assinaturas. */
	private void setAssinaturas(ItemLicitacaoQuadroAprovacaoVO item,
			String[][] params) {
		if (params != null) {
			if ("TP".equals(item.getModalidade().getCodigo())
					|| "CC".equals(item.getModalidade().getCodigo())) {
				item.setNomeAssinatura1(params[0][0]);
				item.setDeptoAssinatura1(params[0][1]);
				item.setNomeAssinatura2(params[1][0]);
				item.setDeptoAssinatura2(params[1][1]);
				item.setNomeAssinatura3(params[2][0]);
				item.setDeptoAssinatura3(params[2][1]);
				item.setNomeAssinatura4(params[3][0]);
				item.setDeptoAssinatura4(params[3][1]);
				item.setNomeAssinatura5(params[4][0]);
				item.setDeptoAssinatura5(params[4][1]);
				item.setNomeAssinatura6(params[5][0]);
				item.setDeptoAssinatura6(params[5][1]);
			} else if (item.getNomeAssinatura1() != null) {
				item.setDeptoAssinatura1(getDeptoGestor());
			}
		}
	}

	private String getDeptoGestor() {
		return getResourceBundleValue("LABEL_RELATORIO_QUADRO_APROVACAO_GESTOR_PAC");
	}
	
	/**
	 * Obtem nomes e deptos. dos membros do comite de licitação.
	 * 
	 * @return Nomes de deptos.
	 */
	private String[][] getAssinaturas() {
		String[][] assinaturas = new String[6][2];
		
		if (getParametroFacade().verificarExisteAghParametro(
				AghuParametrosEnum.P_COMIS_LICT_MEMBRO_1)) {
			try {
				// Membro #1 do Comite de Licitação
				AghParametros p = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_COMIS_LICT_MEMBRO_1);
				assinaturas[0][0] = p.getVlrTexto();
				assinaturas[0][1] = p.getDescricao();
			} catch (ApplicationBusinessException e) {
				logError(e.getMessage());
			}
		} else {
			assinaturas[0][0] = null;
			assinaturas[0][1] = null;
		}
		
		if (getParametroFacade().verificarExisteAghParametro(
				AghuParametrosEnum.P_COMIS_LICT_MEMBRO_2)) {
			try {
				// Membro #2 do Comite de Licitação
				AghParametros p = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_COMIS_LICT_MEMBRO_2);
				assinaturas[1][0] = p.getVlrTexto();
				assinaturas[1][1] = p.getDescricao();
			} catch (ApplicationBusinessException e) {
				logError(e.getMessage());
			}
		} else {
			assinaturas[1][0] = null;
			assinaturas[1][1] = null;
		}
		
		if (getParametroFacade().verificarExisteAghParametro(
				AghuParametrosEnum.P_COMIS_LICT_MEMBRO_3)) {
			try {
				// Membro #3 do Comite de Licitação
				AghParametros p = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_COMIS_LICT_MEMBRO_3);
				assinaturas[2][0] = p.getVlrTexto();
				assinaturas[2][1] = p.getDescricao();
			} catch (ApplicationBusinessException e) {
				logError(e.getMessage());
			}
		} else {
			assinaturas[2][0] = null;
			assinaturas[2][1] = null;
		}
		
		if (getParametroFacade().verificarExisteAghParametro(
				AghuParametrosEnum.P_COMIS_LICT_MEMBRO_4)) {
			try {
				// Membro #4 do Comite de Licitação
				AghParametros p = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_COMIS_LICT_MEMBRO_4);
				assinaturas[3][0] = p.getVlrTexto();
				assinaturas[3][1] = p.getDescricao();
			} catch (ApplicationBusinessException e) {
				logError(e.getMessage());
			}
		} else {
			assinaturas[3][0] = null;
			assinaturas[3][1] = null;
		}
		
		if (getParametroFacade().verificarExisteAghParametro(
				AghuParametrosEnum.P_COMIS_LICT_MEMBRO_5)) {
			try {
				// Membro #5 do Comite de Licitação
				AghParametros p = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_COMIS_LICT_MEMBRO_5);
				assinaturas[4][0] = p.getVlrTexto();
				assinaturas[4][1] = p.getDescricao();
			} catch (ApplicationBusinessException e) {
				logError(e.getMessage());
			}
		} else {
			assinaturas[4][0] = null;
			assinaturas[4][1] = null;
		}
		
		if (getParametroFacade().verificarExisteAghParametro(
				AghuParametrosEnum.P_COMIS_LICT_MEMBRO_6)) {
			try {
				// Membro #6 do Comite de Licitação
				AghParametros p = getParametroFacade().buscarAghParametro(
						AghuParametrosEnum.P_COMIS_LICT_MEMBRO_6);
				assinaturas[5][0] = p.getVlrTexto();
				assinaturas[5][1] = p.getDescricao();
			} catch (ApplicationBusinessException e) {
				logError(e.getMessage());
			}
		} else {
			assinaturas[5][0] = null;
			assinaturas[5][1] = null;
		}
		
		return assinaturas;
	}
	
	public List<Integer> retornarListaNumeroSolicicaoCompraPorPAC(ScoLicitacao scoLicitacao){
		List<Integer> listaNumeroSC = new ArrayList<Integer>();
		
		if (scoLicitacao.getItensLicitacao() != null) {
			for (ScoItemLicitacao itemLicitacao : scoLicitacao.getItensLicitacao()){
				Integer numeroSC = null;
				if(itemLicitacao.getFasesSolicitacao() !=null && !itemLicitacao.getFasesSolicitacao().isEmpty()){
					ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(itemLicitacao.getFasesSolicitacao()).get(0);
					if(fase.getSolicitacaoDeCompra() != null){
						numeroSC = fase.getSolicitacaoDeCompra().getNumero();
						listaNumeroSC.add(numeroSC);
					}
				}	
			}
		}
		
		return listaNumeroSC;
	}
	
	
	public List<Integer> retornarListaNumeroSolicicaoServicoPorPAC(ScoLicitacao scoLicitacao){
		List<Integer> listaNumeroSS = new ArrayList<Integer>();
		
		if (scoLicitacao.getItensLicitacao() != null) {
			for (ScoItemLicitacao itemLicitacao : scoLicitacao.getItensLicitacao()){
				Integer numeroSS = null;
				if(itemLicitacao.getFasesSolicitacao() !=null && !itemLicitacao.getFasesSolicitacao().isEmpty()){
					ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(itemLicitacao.getFasesSolicitacao()).get(0);
					if(fase.getSolicitacaoServico() != null){
						numeroSS = fase.getSolicitacaoServico().getNumero();
						listaNumeroSS.add(numeroSS);
					}
				}	
			}
		}
		return listaNumeroSS;
	}
	
	//#5550 ON1 ON2
	public void validarDatasPregaoEletronicoBB(Date dataInicio, Date dataFim) throws ApplicationBusinessException {
		validarFormatoDataPregaoEletronicoBB(dataInicio);
		validarFormatoDataPregaoEletronicoBB(dataFim);
		if(dataInicio != null && dataFim != null){
			if(dataFim.before(dataInicio)){
				throw new ApplicationBusinessException(LicitacaoONExceptionCode.DATA_INVALIDA_PAC);
			}
		}
	}
	
	public void validarFormatoDataPregaoEletronicoBB(Date data) throws ApplicationBusinessException {
		if(data != null){
			if(!formatarData(data).matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")){
				throw new ApplicationBusinessException(LicitacaoONExceptionCode.FORMATO_DATA_INVALIDO);
			}
		}
	}
	
	private String formatarData(Date data){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(data);
	}
	
	
	
	public boolean isPendente(ScoItemPacVO item) {
		return Boolean.TRUE.equals(item.getIndJulgada()) && SITUACAO_JULGAMENTO_PENDENTE.contains(item.getItemLicitacaoOriginal().getSituacaoJulgamento());
	}	
	
    public String geraArquivoPAC(List<ScoLicitacao> listaExcel) throws IOException {
		
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    Calendar c1 = Calendar.getInstance(); // today
		
		File file = File.createTempFile(PREFIXO_PAC+sdf.format(c1.getTime())+"_", EXTENSAO);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(file),ISO_8859_1);
		
		// GERA CABEÇALHOS DO CSV
		out.write(geraCabecalhoPAC());
		
		// ESCREVE LINHAS DO CSV
		for (ScoLicitacao consulta : listaExcel) {
			out.write(System.getProperty("line.separator"));
			out.write(geraLinhaPAC(consulta));
		}
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}
	
	
	public String geraArquivoItensPAC(List<ScoItemPacVO> listaExcel,Integer pac) throws IOException {
		
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    Calendar c1 = Calendar.getInstance(); // today
		
		File file = File.createTempFile(PREFIXO_ITENS_PAC+sdf.format(c1.getTime())+"_", EXTENSAO);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(file),ISO_8859_1);
		
		// GERA CABEÇALHOS DO CSV
		out.write(geraCabecalhoItensPAC());
		
		// ESCREVE LINHAS DO CSV
		for (ScoItemPacVO consulta : listaExcel) {
			out.write(System.getProperty("line.separator"));
			out.write(geraLinhaItensPAC(consulta,pac));
		}
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	public String geraCabecalhoItensPAC() {
		return "Item; Solicitação; Tipo da Solicitação; Código do Material/Serviço; " +
				"Nome do Material/Serviço; Descrição do Material/Serviço; Unidade de Medida; " +
				"Quantidade; Valor Unitário Previsto; Frequência de Entrega; Período; " +
				"Item Excluído; Motivo da Exclusão; Escolhido; Fornecedor Vencedor; " +
				"Critério de Escolha; Número AF; Item Pendente";
	}

	public String geraCabecalhoPAC() {
			return "Número do PAC" + SEPARADOR + "Descrição do PAC" + SEPARADOR + "Modalidade"
					+ SEPARADOR + "Tipo" + SEPARADOR + "Data da Geração"
					+ SEPARADOR + "Número do Documento" + SEPARADOR + "Edital"
					+ SEPARADOR + "Ano" + SEPARADOR + "Artigo"
					+ SEPARADOR + "Inciso" + SEPARADOR + "Tipo PAC"
					+ SEPARADOR + "Modalidade Empenho" + SEPARADOR + "Critério Julgamento Orçamento" 
					+ SEPARADOR + "Frequência de Entrega" + SEPARADOR + "Tempo Atendimento" 
					+ SEPARADOR + "Matrícula Gestor" + SEPARADOR + "Vínculo Gestor" 
					+ SEPARADOR + "Nome Gestor" + SEPARADOR + "Situação";
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	public String geraLinhaItensPAC(ScoItemPacVO vo,Integer pac) {
		StringBuilder texto = new StringBuilder();
		
		// Item
		if(vo.getNumeroItem() != null){
			addText(vo.getNumeroItem(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Solicitação
		if(vo.getNumeroSolicitacao() != null){
			addText(vo.getNumeroSolicitacao(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Tipo da Solicitação
		if(vo.getTipoSolicitacao() != null){
			addText(vo.getTipoSolicitacao(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Código do Material/Serviço
		if(vo.getItemLicitacaoOriginal() != null){
			addText(dadosItemLicitacaoON.obterCodMatServ(vo.getItemLicitacaoOriginal()), texto);
		} else {
			addText(" ", texto);
		}
		
		// Nome do Material/Serviço
		String itemNome = "";
		if(vo.getItemLicitacaoOriginal() != null){
			itemNome = dadosItemLicitacaoON.obterNomeMaterialServico(vo.getItemLicitacaoOriginal(), false);
		}
		addText(itemNome != null ? itemNome.replace(";"," ") : "", texto);
		
		// Descrição do Material/Serviço
		String itemDescricao = " ";
		if(vo.getItemLicitacaoOriginal() != null){
			itemDescricao = dadosItemLicitacaoON.obterDescricaoMaterialServico(vo.getItemLicitacaoOriginal());
		}
		
		if(itemDescricao != null && !itemDescricao.isEmpty()){
			addText(itemDescricao !=null ? itemDescricao.replace(";"," ") : "", texto);
		} else {
			addText(itemDescricao, texto);
		}

		// Unidade de Medida
		if(vo.getUnidadeMaterial() != null){
			addText(vo.getUnidadeMaterial(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Quantidade
		if(vo.getQtdeSolicitada() != null){
			addText(vo.getQtdeSolicitada(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Valor Unitário Previsto
		String valor = " ";
		if(vo.getValorUnitarioPrevisto() != null){
			valor = formataDecimal(vo.getValorUnitarioPrevisto());
		}
		addText(valor, texto);
		
		// Frequência de Entrega
		if(vo.getFrequenciaEntrega() != null){
			addText(vo.getFrequenciaEntrega(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Período
		if(vo.getIndFrequencia() != null){
			addText(vo.getIndFrequencia(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Item Excluído
		if(vo.getIndExclusao() == null){
			addText(NAO, texto);
		}else{
			addText(vo.getIndExclusao() == true ? SIM : NAO, texto);
		}
		
		// Motivo da Exclusão
		if(vo.getMotivoExclusao() != null){
			addText(vo.getMotivoExclusao(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Escolhido
		if(vo.getIndEscolhido() == null){
			addText(NAO, texto);
		}else{
			addText(vo.getIndEscolhido() == true ? SIM : NAO, texto);
		}		
		
		// Fornecedor Vencedor
		if(vo.getForncedorVencedor() != null){
			addText(vo.getForncedorVencedor().getNomeFantasia(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Critério de Escolha
		if(vo.getCriterioEscolha() != null && !vo.getCriterioEscolha().isEmpty()){
			addText(vo.getCriterioEscolha(), texto);
		} else {
			addText(" ", texto);
		}
		
		// Número AF
		if(vo.getComplemento() == null){
			addText(" ", texto);
		} else {
			addText(pac+"/"+vo.getComplemento(), texto);
		}
		
		// Item Pendente
		addText(isPendente(vo) == true ? SIM : NAO, texto);

        //#48336 - Quebra de linha estava desconfigurando relatorio
        String result = texto.toString().replaceAll("\\n", "");
		return result;
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	public String geraLinhaPAC(ScoLicitacao vo) {
		StringBuilder texto = new StringBuilder();
		addText(vo.getNumero() , texto);
		addText(vo.getDescricao().replace("\n"," ").replace(";"," "), texto);
		addText(vo.getModalidadeLicitacao() != null ? vo.getModalidadeLicitacao().getDescricao().replace(";"," ") : "", texto);
		addText(vo.getTipoPregao() != null ? vo.getTipoPregao().getDescricao().replace(";"," ") : "", texto);
		addText(DateUtil.obterDataFormatada(vo.getDtDigitacao(), DD_MM_YYYY),texto);
		addText(vo.getNumDocLicit(), texto);
		addText(vo.getNumEdital(), texto);
		addText(vo.getAnoComplemento(), texto);
		addText(vo.getArtigoLicitacao(), texto);
		addText(vo.getIncisoArtigoLicitacao(), texto);
		addText(vo.getTipo() != null ? vo.getTipo().getDescricao() : "", texto);
		addText(vo.getModalidadeEmpenho() != null ? vo.getModalidadeEmpenho().getDescricao().replace(";"," ") : "", texto);
		addText(vo.getCritJulgOrcamento() != null ? vo.getCritJulgOrcamento().getDescricao().replace(";"," ") : "", texto);
		addText(vo.getFrequenciaEntrega(), texto);
		addText(vo.getTempoAtendimento(), texto);
		addText(vo.getServidorGestor() != null && vo.getServidorGestor().getId() != null ? vo.getServidorGestor().getId().getMatricula() : "", texto);
		addText(vo.getServidorGestor() != null && vo.getServidorGestor().getVinculo() != null ? vo.getServidorGestor().getVinculo().getDescricao().replace(";"," ")  : "", texto);
		addText(vo.getServidorGestor() != null && vo.getServidorGestor().getPessoaFisica() != null ? vo.getServidorGestor().getPessoaFisica().getNome() : "", texto);
		addText(vo.getSituacao() != null ? vo.getSituacao().getDescricao().replace(";"," ") : "", texto);
		return texto.toString();
	}
	
	private void addText(Object texto, StringBuilder sb) {
		if (texto != null) {
			sb.append(texto);
		}
		sb.append(SEPARADOR);
	}
	
	
	
	
	
    public String formataDecimal(BigDecimal vlrFator){    
        BigDecimal numFormatado  = vlrFator.setScale(2, BigDecimal.ROUND_HALF_EVEN);   
        String a = numFormatado.toString();
        return a.replace(".",",");          
    } 
	protected ScoItemLicitacaoQuadroAprovacaoDAO getScoItemLicitacaoQuadroAprovacaoDAO() {
		return scoItemLicitacaoQuadroAprovacaoDAO;
	}

	protected ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}
	
	protected ScoItemLicitacaoRN getScoItemLicitacaoRN() {
		return scoItemLicitacaoRN;
	}
	
	protected ScoItemLicitacaoON getScoItemLicitacaoON() {
		return scoItemLicitacaoON;
	}
	
	protected ScoItemPropostaFornecedorRN  getScoItemPropostaFornecedorRN() {
		return scoItemPropostaFornecedorRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}