package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornJnDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.ItensAutFornVO;
import br.gov.mec.aghu.dominio.DominioCriterioJulgamentoOrcamento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoItemAutorizacaoFornRN extends BaseBusiness{

private static final String LIBERAR_ALT_CRITICA = "liberarAltCritica";

private static final String ASSINAR_AF = "assinarAF";

@EJB
private ScoAutorizacaoFornRN scoAutorizacaoFornRN;

	private static final Log LOG = LogFactory.getLog(ScoItemAutorizacaoFornRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@EJB
	private IPacFacade pacFacade;
	
	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ScoItemAutorizacaoFornJnDAO scoItemAutorizacaoFornJnDAO;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	private static final long serialVersionUID = -7940202453470295231L;

	public enum ScoItemAutorizacaoFornRNExceptionCode implements BusinessExceptionCode {
		ERRO_ATUALIZAR_ITEM_AUT_FORNECIMENTO, ERRO_INSERIR_ITEM_AUT_FORNECIMENTO,MENSAGEM_QTDE_SOLICITADA_MENOR_RECEBIDA,
		MENSAGEM_VALOR_ITEM_MENOR_EFETIVADO, MENSAGEM_ITEM_AF_SEM_PROPOSTA, ERRO_NAO_EXISTE_FASE_MSG14,	ERRO_MENSAGEM_MSG29,
		ERRO_MENSAGEM_MSG30, ERRO_MENSAGEM_MSG31, ERRO_MENSAGEM_MSG32, ERRO_MENSAGEM_MSG33, ERRO_MENSAGEM_MSG34, ERRO_MENSAGEM_MSG35,
		ERRO_MENSAGEM_MSG36, ERRO_MENSAGEM_MSG37, ERRO_MENSAGEM_MSG38, ERRO_MENSAGEM_MSG39, ERRO_MENSAGEM_MSG40, ERRO_MENSAGEM_MSG41,
		ERRO_MENSAGEM_MAT_CONSIG, ERRO_QTD_SOLICITADA_MULTIPLO_FATOR_CONVERSAO;
	}
	
	
	
	
	/**
	 * @ORADB SCOT_AFN_BSU, SCOT_AFN_AGHU_BRU, SCOT_AFN_BRU,
	 * SCOT_AFN_ARU, SCOT_AFN_ASU
	 * 
	 * @param itemAutorizacaoForn
	 * @return
	 * @throws BaseException 
	 * @throws ApplicationBusinessException 
	 */	
	public ScoItemAutorizacaoForn atualizarItemAutorizacaoFornecimento(
			ScoItemAutorizacaoForn itemAutorizacaoForn) throws ApplicationBusinessException {
				
		ScoItemAutorizacaoForn itemAfOriginal = this.getScoItemAutorizacaoFornDAO().obterOriginal(itemAutorizacaoForn);		
		
		
		// RN 47
		this.validaIndConsignado(itemAutorizacaoForn.getIndConsignado(), itemAutorizacaoForn.getIndContrato());
		
		if (itemAfOriginal != null) {
			// RN 23 e RN 24
			if ( (Objects.equals(itemAfOriginal.getIndSituacao(),DominioSituacaoAutorizacaoFornecedor.EF) &&
				 !Objects.equals(itemAutorizacaoForn.getIndSituacao(), DominioSituacaoAutorizacaoFornecedor.EF)) ||
				 (!Objects.equals(itemAfOriginal.getIndSituacao(), DominioSituacaoAutorizacaoFornecedor.EF) &&
						 Objects.equals(itemAutorizacaoForn.getIndSituacao(), DominioSituacaoAutorizacaoFornecedor.EF))){
				this.atualizaSituacaoPAC(itemAutorizacaoForn.getAutorizacoesForn());
				this.atualizaSolicitacao(itemAutorizacaoForn, itemAfOriginal);
			}		
					
			if ( (Objects.equals(itemAfOriginal.getIndSituacao(), DominioSituacaoAutorizacaoFornecedor.EX) &&
				 (!Objects.equals(itemAutorizacaoForn.getIndSituacao(), DominioSituacaoAutorizacaoFornecedor.EX))) ||
				 (!Objects.equals(itemAutorizacaoForn.getValorEfetivado(), itemAfOriginal.getValorEfetivado())) ||
				 (!Objects.equals(itemAutorizacaoForn.getQtdeSolicitada(), itemAfOriginal.getQtdeSolicitada())) ||
				 (!Objects.equals(itemAutorizacaoForn.getValorUnitario(), itemAfOriginal.getValorEfetivado()))){
			      this.validarLei8666(itemAutorizacaoForn);
				
			}
			
			Integer fatorConversaoFornOriginal = (Integer) CoreUtil.nvl(itemAfOriginal.getFatorConversaoForn(), 1);
			Integer fatorConversaoFornAtual = (Integer) CoreUtil.nvl(itemAutorizacaoForn.getFatorConversaoForn(), 1);

			if (!itemAutorizacaoForn.getQtdeSolicitada().equals(itemAfOriginal.getQtdeSolicitada()) || 
					!fatorConversaoFornAtual.equals(fatorConversaoFornOriginal)){
				this.validarRelacaoQtdSolicitadaFatorConversaoForn(itemAutorizacaoForn);
			}
		}
		
		try {
			this.validarIntegridadeItemAutorizacaoFornecimento(itemAutorizacaoForn, itemAfOriginal);
		} catch (BaseException e) {			
			throw new ApplicationBusinessException(e);
		}
		
		this.getScoItemAutorizacaoFornDAO().merge(itemAutorizacaoForn);
		
		if (!itemAfOriginal.getIndSituacao().equals(itemAutorizacaoForn.getIndSituacao())){
			this.atualizarSituacaoAf(itemAfOriginal, itemAutorizacaoForn);
		}
		
		return itemAutorizacaoForn;
	
	}
	
	public ScoItemAutorizacaoForn inserirItemAutorizacaoFornecimento(
			ScoItemAutorizacaoForn itemAutorizacaoFornecimento) throws ApplicationBusinessException {
		try {
			this.validarRelacaoQtdSolicitadaFatorConversaoForn(itemAutorizacaoFornecimento);
			this.getScoItemAutorizacaoFornDAO().persistir(
					itemAutorizacaoFornecimento);
		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ScoItemAutorizacaoFornRNExceptionCode.ERRO_INSERIR_ITEM_AUT_FORNECIMENTO);
		}
		return itemAutorizacaoFornecimento;
	}
	
	/**
	 * Para gravação (atualização ou inclusao de novo registro) de item de AF, a quantidade solicitada deve
	 * ser um múltiplo do fator de conversão informado pelo fornecedor
	 * 
	 * @param itemAutorizacaoForn
	 * @throws ApplicationBusinessException  
	 */
	private void validarRelacaoQtdSolicitadaFatorConversaoForn(ScoItemAutorizacaoForn itemAutorizacaoForn) throws ApplicationBusinessException{
		
		if (itemAutorizacaoForn.getQtdeSolicitada() != null && itemAutorizacaoForn.getFatorConversaoForn() != null && itemAutorizacaoForn.getFatorConversaoForn() > 0){

			if (itemAutorizacaoForn.getQtdeSolicitada() % itemAutorizacaoForn.getFatorConversaoForn() != 0){
				
				throw new ApplicationBusinessException(
						ScoItemAutorizacaoFornRNExceptionCode.ERRO_QTD_SOLICITADA_MULTIPLO_FATOR_CONVERSAO, itemAutorizacaoForn.getQtdeSolicitada(), 
						itemAutorizacaoForn.getId().getNumero(), itemAutorizacaoForn.getFatorConversaoForn());
			}			
		}
		
	}
	
	public void atualizarSequenciaAlteracaoItemAf(Integer afnNumero, Integer numero, Integer sequenciaAlteracao) throws BaseException {
		ScoItemAutorizacaoFornId iafId = new ScoItemAutorizacaoFornId();
		iafId.setAfnNumero(afnNumero);
		iafId.setNumero(numero);
		ScoItemAutorizacaoForn itemAf = this.getScoItemAutorizacaoFornDAO().obterPorChavePrimaria(iafId);
		if (itemAf != null) {
			itemAf.setSequenciaAlteracao(sequenciaAlteracao+1);
			this.atualizarItemAutorizacaoFornecimento(itemAf);
		}
	}
	
	/**
	 * @throws BaseException 
	 * @ORADB SCOK_IAF_RN – PROCEDURE RN_IAFP_ATU_LIC_EFET
	 * 
	 */
	public void atualizaSituacaoPAC(ScoAutorizacaoForn autorizacaoFornecimento) throws ApplicationBusinessException{
		
		Boolean flagTodasEfetivadas = true;
		List<ScoLicitacao> listaLicitacoes = new ArrayList<ScoLicitacao>();
		if (autorizacaoFornecimento != null){
			List<ScoItemAutorizacaoForn> listaIaf = this.getScoItemAutorizacaoFornDAO().pesquisarItemAfPorNumeroAf(autorizacaoFornecimento.getNumero());
			if (listaIaf != null) {
				for(ScoItemAutorizacaoForn itemAutorizaoForn: listaIaf){
					if (itemAutorizaoForn.getIndExclusao().equals(Boolean.FALSE)){
						ScoItemPropostaFornecedor itemPropostaFornecedor = itemAutorizaoForn.getItemPropostaFornecedor();
						
						if (itemPropostaFornecedor.getIndEscolhido() &&
						    itemPropostaFornecedor.getIndExclusao().equals(Boolean.FALSE)){
						
							ScoItemLicitacao itemLicitacao = itemPropostaFornecedor.getItemLicitacao();
							
							if (itemLicitacao.getExclusao().equals(Boolean.FALSE)){
						        if (!itemAutorizaoForn.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EF)){
							        flagTodasEfetivadas = false;						
						        }
						        listaLicitacoes.add(itemLicitacao.getLicitacao());
							}					   
						}
					}				
				}	
			}
		}
		for (ScoLicitacao licitacao: listaLicitacoes){			
			if (flagTodasEfetivadas){
                licitacao.setSituacao(DominioSituacaoLicitacao.EF);
			}
			else {
				licitacao.setSituacao(DominioSituacaoLicitacao.JU);
			}
			
			this.getPacFacade().alterarLicitacao(licitacao, getScoLicitacaoDAO().obterOriginal(licitacao.getNumero()));						
		}
		
	}
	
	/**
	 * @ORADB SCOK_IAF_RN – PROCEDURE RN_IAFP_ATU_SLC_EFET
	 * 
	 */
	public void atualizaSolicitacao(ScoItemAutorizacaoForn itemAFNovo , ScoItemAutorizacaoForn itemAFOriginal){
		
		List<ScoFaseSolicitacao> listaFases = itemAFOriginal.getScoFaseSolicitacao();
		if (listaFases != null) {
			
			for (ScoFaseSolicitacao fase: listaFases){
				Boolean indEfetivada = (itemAFNovo.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EF));
				ScoSolicitacaoDeCompra solicitacaoCompra = fase.getSolicitacaoDeCompra();
				ScoSolicitacaoServico solicitacaoServico = fase.getSolicitacaoServico();
				
				if (solicitacaoCompra != null){
					if (solicitacaoCompra.getExclusao() == false){
						solicitacaoCompra.setEfetivada(indEfetivada);
						this.getScoSolicitacoesDeComprasDAO().atualizar(solicitacaoCompra);
					}
					
				}
				else if (solicitacaoServico != null){
					if (solicitacaoServico.getIndExclusao() == false){
						solicitacaoServico.setIndEfetivada(indEfetivada);
						this.getScoSolicitacaoServicoDAO().atualizar(solicitacaoServico);
					}
				}
					
			}
			
		}			
	}
	
	/**
	 * @ORADB SCOT_IAF_BRU
	 * @param itemAf
	 * @throws BaseException 
	 */
	public void validarIntegridadeItemAutorizacaoFornecimento(ScoItemAutorizacaoForn itemAf, ScoItemAutorizacaoForn itemAfOriginal) throws BaseException {
		List<ScoFaseSolicitacao> listaFases = this.getComprasFacade().pesquisarFasePorIafAfnNumeroIafNumero(itemAf.getId().getAfnNumero(), itemAf.getId().getNumero());
		
		if (listaFases != null && !listaFases.isEmpty()) {
			// RN 40 e RN 41
			this.validarItemEntregue(listaFases, itemAf.getQtdeRecebida(), itemAf.getQtdeSolicitada(),
					                 itemAf.getValorUnitario(), itemAf.getValorEfetivado());
			// RN 42, RN 43 e RN 44
			this.atualizarSituacaoItemAutorizacaoFornecimento(itemAf, listaFases.get(0), false);
			// RN 45
			this.atualizarQuantidadeEntregueSc(itemAf, itemAfOriginal, listaFases);
			// RN 46
			this.validarAlteracaoIndExclusaoItemAf(itemAf, itemAfOriginal, listaFases);
		}
	}
	
	/**
	 * @ORADB RN_IAFP_VER_VLR_COMP - Package SCOK_IAF
	 * Regra referente a aplicação da LEI 8666, que deve ser acionada quando alterar o VALOR_EFETIVADO ou
	 *  a QTDE_SOLICITADA ou o VALOR_UNITARIO  ou IND_SITUACAO de 'EX' para outra situação.
	 * @param itemAf
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	public void validarLei8666(ScoItemAutorizacaoForn itemAf) throws ApplicationBusinessException{
		
        List<ScoFaseSolicitacao> listaFaseSolicitacao = this.getScoFaseSolicitacaoDAO().pesquisaItensAutPropostaFornecedor(itemAf.getId());
        ScoSolicitacaoDeCompra solicitacaoCompra = null;		
		ScoGrupoMaterial grupoMaterial = null;
		ScoFaseSolicitacao faseSolicitacao = null;
		boolean flagLiberarCritica = false;
		boolean flagCriticaValor = false;		
		ScoPropostaFornecedor propostaFornecedor = null;
		ScoLicitacao licitacao = null;
		ScoModalidadeLicitacao modalidadeLicitacao = null;
		ScoAutorizacaoForn autorizacaoFornecimento = null;
		BigDecimal valorTotalLict = BigDecimal.ZERO;
		
		
		
		if (listaFaseSolicitacao != null && !listaFaseSolicitacao.isEmpty()) {
			faseSolicitacao = listaFaseSolicitacao.get(0);
			solicitacaoCompra = faseSolicitacao.getSolicitacaoDeCompra();						
			if (solicitacaoCompra != null){
				grupoMaterial = solicitacaoCompra.getMaterial().getGrupoMaterial();
			}
		}
		
		/*** PASSO 1 - BUSCA PARAMETRO P_LIBERA_CRITICA_LEI_8666 ***/
		AghParametros parametroLiberaCriticaLei8666 = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_LIBERA_CRITICA_LEI_8666);			
		flagLiberarCritica = parametroLiberaCriticaLei8666.getVlrTexto().equals(DominioSimNao.S.toString());		
				
	    /*** PASSO 2 ***/		
		AghParametros parametroGrupoMaterialOrteseProtese = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRP_MAT_ORT_PROT);			
		Integer parametroCodigoGrupoMaterialOrteseProtese = parametroGrupoMaterialOrteseProtese.getVlrNumerico().intValue();			
		flagCriticaValor = (grupoMaterial != null) ? !(parametroCodigoGrupoMaterialOrteseProtese.equals(grupoMaterial.getCodigo())) : true;
				
		/*** PASSO 4 ***/		
		autorizacaoFornecimento = scoAutorizacaoFornDAO.obterAfDetalhadaByNumero(itemAf.getId().getAfnNumero());	
		if (autorizacaoFornecimento != null){
			propostaFornecedor = autorizacaoFornecimento.getPropostaFornecedor();		
			if (propostaFornecedor != null){			
				licitacao = propostaFornecedor.getLicitacao();
				if (licitacao != null){
					modalidadeLicitacao = licitacao.getModalidadeLicitacao();				
				}			
			}
		}
		
		/*** PASSO 6 ***/
		if (licitacao != null){			
		    valorTotalLict = this.getScoFaseSolicitacaoDAO().obterValorReforcoPorNumeroLicOuItemAF(licitacao.getNumero(), null, null);
		}
		
		/*** PASSO 7 ****/
		this.validarLei866Passo7(faseSolicitacao, modalidadeLicitacao, licitacao, autorizacaoFornecimento, valorTotalLict, flagLiberarCritica, flagCriticaValor);
		
		/**** PASSO 8 ***/
		if (licitacao != null){			
		    valorTotalLict = this.getScoFaseSolicitacaoDAO().obterValorReforcoPorNumeroLicOuItemAF(licitacao.getNumero(), itemAf.getId().getAfnNumero(), itemAf.getId().getNumero());
		}
		this.validarLei8666Passo8(faseSolicitacao, modalidadeLicitacao, licitacao, autorizacaoFornecimento, itemAf, propostaFornecedor, valorTotalLict, flagLiberarCritica, flagCriticaValor);
	}
	
	/**
	 * valida Licitacao e retorna mensagem de erro
	 * @param itemAf
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	public void validarLicitacao(ScoLicitacao licitacao, Integer artigoLicitacao, String incisoLicitacao, BigDecimal valorTotalLict, BigDecimal valor, boolean flagLiberarCritica, String mensagem , BigDecimal valorMensagem) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (licitacao.getArtigoLicitacao() != null &&
			licitacao.getIncisoArtigoLicitacao() != null) {
			if (licitacao.getArtigoLicitacao().equals(artigoLicitacao) &&
				incisoLicitacao.contains(licitacao.getIncisoArtigoLicitacao()) &&
				valorTotalLict.compareTo(valor) == 1 &&
				 ((getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), ASSINAR_AF) ||
				  getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), LIBERAR_ALT_CRITICA) && 
					  !flagLiberarCritica) ||
					  (!getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), ASSINAR_AF) ||
					   !getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), LIBERAR_ALT_CRITICA) && 
					   flagLiberarCritica))){		
			     throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.valueOf(mensagem),
							(valorMensagem != null ? valorMensagem.toString() : BigDecimal.ZERO));
			}
		}
	}
	

	/**
	 * Regra referente a aplicação da LEI 8666, que deve ser acionada quando alterar o VALOR_EFETIVADO ou
	 *  a QTDE_SOLICITADA ou o VALOR_UNITARIO  ou IND_SITUACAO de 'EX' para outra situação. Passo 7
	 * @param itemAf
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	public void validarLei866Passo7(ScoFaseSolicitacao faseSolicitacao, ScoModalidadeLicitacao modalidadeLicitacao, ScoLicitacao licitacao, ScoAutorizacaoForn autorizacaoFornecimento, BigDecimal valorTotalLict, boolean flagLiberarCritica, boolean flagCriticaValor ) throws ApplicationBusinessException{
		
		/*** PASSO 7 ***/
		AghParametros parametroGrupoNaturezaEspeciais = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GND_ESPECIAIS);
		
		if (faseSolicitacao != null){
			if (faseSolicitacao.getItemAutorizacaoForn() != null) {
				if (!faseSolicitacao.getItemAutorizacaoForn().getIndSituacao().equals(DominioSituacaoAutorizacaoFornecimento.EF)){
					if(modalidadeLicitacao != null){
						if (modalidadeLicitacao.getCodigo().equals("DI")){
							if (licitacao != null){
																
								validarLicitacao(licitacao, 24, "1,01,I", valorTotalLict, modalidadeLicitacao.getValorPermitidoEng(), flagLiberarCritica, "ERRO_MENSAGEM_MSG33", modalidadeLicitacao.getValorPermitidoEng());
								validarLicitacao(licitacao, 24, "2,02,II", valorTotalLict, modalidadeLicitacao.getValorPermitido(), flagLiberarCritica, "ERRO_MENSAGEM_MSG34", modalidadeLicitacao.getValorPermitido());
								
								if (licitacao.getArtigoLicitacao() != null &&
									licitacao.getIncisoArtigoLicitacao() != null) {
									if  (!licitacao.getArtigoLicitacao().equals(24) ||
										(licitacao.getArtigoLicitacao().equals(24) &&
										!(licitacao.getIncisoArtigoLicitacao().contains("1,01,I,2,02,II"))) &&
										!parametroGrupoNaturezaEspeciais.getVlrTexto().contains(autorizacaoFornecimento.getGrupoNaturezaDespesa().getCodigo().toString())){
											throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.valueOf("ERRO_MENSAGEM_MSG35"), autorizacaoFornecimento.getGrupoNaturezaDespesa().getCodigo().toString());										
									}
								}
								
								if (parametroGrupoNaturezaEspeciais.getVlrTexto().contains(autorizacaoFornecimento.getGrupoNaturezaDespesa().getCodigo().toString())){
									flagCriticaValor = false;										
								}
							}
						} else if (modalidadeLicitacao.getCodigo().equals("IN")){
								 flagCriticaValor = false;
						} else if (modalidadeLicitacao.getCodigo().equals("CV") ||
								   modalidadeLicitacao.getCodigo().equals("TP")){		
								   if (licitacao != null){
									   String mensagem1 = (modalidadeLicitacao.getCodigo().equals("CV")) ? "ERRO_MENSAGEM_MSG36" : "ERRO_MENSAGEM_MSG39"; 
									   String mensagem2 = (modalidadeLicitacao.getCodigo().equals("CV")) ? "ERRO_MENSAGEM_MSG37" : "ERRO_MENSAGEM_MSG40";
									   String mensagem3 = (modalidadeLicitacao.getCodigo().equals("CV")) ? "ERRO_MENSAGEM_MSG38" : "ERRO_MENSAGEM_MSG41";
																		  
									   validarLicitacao(licitacao, 23, "1,01,I", valorTotalLict, modalidadeLicitacao.getValorPermitidoEng(), flagLiberarCritica, mensagem1 , modalidadeLicitacao.getValorPermitidoEng());
									   validarLicitacao(licitacao, 23, "2,02,II", valorTotalLict, modalidadeLicitacao.getValorPermitido(), flagLiberarCritica, mensagem2, modalidadeLicitacao.getValorPermitidoEng());
									
										if (licitacao.getArtigoLicitacao() != null &&
												licitacao.getIncisoArtigoLicitacao() != null) {
										   if (!licitacao.getArtigoLicitacao().equals(23) ||
											  (licitacao.getArtigoLicitacao().equals(23) &&
											  !(licitacao.getIncisoArtigoLicitacao().contains("1,01,I,2,02,II")))){ 
											    	throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.valueOf(mensagem3), licitacao.getArtigoLicitacao().toString(), licitacao.getIncisoArtigoLicitacao());									
										   }
										}
								   }
						}						
					}
				}
			}
		}
	}
	
	/**
	 * Calcula a quantidade ou valor aprovado dos itens da proposta do fornecedor utilizada pela validarLei8666Passo8
	 * @param itemAf
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal calcularQuantidadeValorAprovada(ScoPropostaFornecedor propostaFornecedor, DominioTipoFaseSolicitacao tipoSolicitacao){
		BigDecimal qtdeValorAprovada = BigDecimal.ZERO;
		for (ScoItemPropostaFornecedor itemPropostaFornecedor: propostaFornecedor.getItens()){
			if (itemPropostaFornecedor.getIndEscolhido() &&
				!itemPropostaFornecedor.getIndExclusao()){
				
				if (tipoSolicitacao.equals(DominioTipoFaseSolicitacao.C)) {
					qtdeValorAprovada = qtdeValorAprovada.add(new BigDecimal(itemPropostaFornecedor.getQuantidade()).multiply(new BigDecimal(itemPropostaFornecedor.getFatorConversao())));
				} else {
					qtdeValorAprovada = qtdeValorAprovada.add(new BigDecimal(itemPropostaFornecedor.getQuantidade()).multiply((BigDecimal) CoreUtil.nvl(itemPropostaFornecedor.getValorUnitario(), BigDecimal.ZERO)));
				}    
				
			}
		}	
		return qtdeValorAprovada;
	}
	
	/**
	 * Regra referente a aplicação da LEI 8666, que deve ser acionada quando alterar o VALOR_EFETIVADO ou
	 *  a QTDE_SOLICITADA ou o VALOR_UNITARIO  ou IND_SITUACAO de 'EX' para outra situação. Passo 8
	 * @param itemAf
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	public void validarLei8666Passo8(ScoFaseSolicitacao faseSolicitacao, ScoModalidadeLicitacao modalidadeLicitacao, ScoLicitacao licitacao, ScoAutorizacaoForn autorizacaoFornecimento, ScoItemAutorizacaoForn itemAf, ScoPropostaFornecedor propostaFornecedor, BigDecimal valorTotalLict, boolean flagLiberarCritica, boolean flagCriticaValor ) throws ApplicationBusinessException{
		/**** PASSO 8 ***/
		AghParametros parametroModalidadeEmpenho = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MODL_EMP_CONTRATO);
		
		if (licitacao != null &&
			faseSolicitacao != null){
			if (licitacao.getModalidadeEmpenho() != null && licitacao.getModalidadeEmpenho().getCodigo() == parametroModalidadeEmpenho.getVlrNumerico().intValue() &&
				faseSolicitacao.getTipo().equals(DominioTipoFaseSolicitacao.C)){				
				this.validarLei8666Passo8Compra(licitacao, itemAf, propostaFornecedor, flagLiberarCritica, flagCriticaValor);
			}
			else {
			    this.validarLei8666Passo8Servico(licitacao, autorizacaoFornecimento, itemAf, propostaFornecedor, valorTotalLict, flagLiberarCritica, flagCriticaValor);
			}
		}
		
	}
	
	/**
	 * Regra referente a aplicação da LEI 8666, que deve ser acionada quando alterar o VALOR_EFETIVADO ou
	 *  a QTDE_SOLICITADA ou o VALOR_UNITARIO  ou IND_SITUACAO de 'EX' para outra situação. Passo 8 Compra
	 * @param itemAf
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	public void validarLei8666Passo8Compra(ScoLicitacao licitacao, ScoItemAutorizacaoForn itemAf, ScoPropostaFornecedor propostaFornecedor, boolean flagLiberarCritica, boolean flagCriticaValor ) throws ApplicationBusinessException{
		if(licitacao.getCritJulgOrcamento().equals(DominioCriterioJulgamentoOrcamento.I)){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			Integer qtdeTotalItem = itemAf.getQtdeSolicitada();
			
			Integer qtdeAprovada = this.calcularQuantidadeValorAprovada(propostaFornecedor,DominioTipoFaseSolicitacao.C).intValue();								
			qtdeAprovada = qtdeAprovada * (licitacao.getFrequenciaEntrega() != null ? licitacao.getFrequenciaEntrega(): 0); 
			
			if (!getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), ASSINAR_AF) &&
				!getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), LIBERAR_ALT_CRITICA)){
				
				AghParametros parametroPercLimiteCompra = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PERC_LIMITE_COMPRA_NORMAL);
				
				if (qtdeTotalItem > (qtdeAprovada * (1 + (parametroPercLimiteCompra.getVlrNumerico().intValue() / 100)))){
					if (flagCriticaValor && !itemAf.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecimento.EF)){
						throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.ERRO_MENSAGEM_MSG29, parametroPercLimiteCompra.getVlrNumerico().intValue(), qtdeAprovada);
					}
				}						
			}
			else {
				AghParametros parametroPercLimiteCompra = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PERC_LIMITE_COMPRA_CHEFIA);
				
				if (qtdeTotalItem > (qtdeAprovada * (1 + (parametroPercLimiteCompra.getVlrNumerico().intValue() / 100)))){
					if (flagCriticaValor && !itemAf.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecimento.EF) &&
						!flagLiberarCritica){
						throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.ERRO_MENSAGEM_MSG30, qtdeAprovada, parametroPercLimiteCompra.getVlrNumerico().intValue());
					}
				}
			}
		}
	}
	
	/**
	 * Regra referente a aplicação da LEI 8666, que deve ser acionada quando alterar o VALOR_EFETIVADO ou
	 *  a QTDE_SOLICITADA ou o VALOR_UNITARIO  ou IND_SITUACAO de 'EX' para outra situação. Passo 8 Serviço
	 * @param itemAf
	 * @param usuarioLogado
	 * @throws ApplicationBusinessException
	 */
	public void validarLei8666Passo8Servico(ScoLicitacao licitacao,ScoAutorizacaoForn autorizacaoFornecimento, ScoItemAutorizacaoForn itemAf, ScoPropostaFornecedor propostaFornecedor, BigDecimal valorTotalLict, boolean flagLiberarCritica, boolean flagCriticaValor) throws ApplicationBusinessException{
		if (autorizacaoFornecimento != null){
			Short codigoFormaPag = 0 ;
			ScoCondicaoPagamentoPropos condicaoPagamentoPropos = autorizacaoFornecimento.getCondicaoPagamentoPropos();
			if (condicaoPagamentoPropos!= null) {
				ScoFormaPagamento formaPagamento = condicaoPagamentoPropos.getFormaPagamento();
				if (formaPagamento != null) {
					codigoFormaPag = formaPagamento.getCodigo();
				}
			}
			
			AghParametros parametroFormaPagamentoImportacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORMA_PGTO_IMPORTACAO);
			
			if (licitacao != null) {	
				
				BigDecimal valor = BigDecimal.ZERO;
				
				if(Objects.equals(licitacao.getCritJulgOrcamento(), DominioCriterioJulgamentoOrcamento.I) &&
				   parametroFormaPagamentoImportacao.getVlrNumerico().shortValue() != codigoFormaPag){							
					valor = this.getScoFaseSolicitacaoDAO().obterValorReforcoPorNumeroLicOuItemAF(null, itemAf.getId().getAfnNumero(), itemAf.getId().getNumero());								
				}
				else if (!Objects.equals(licitacao.getCritJulgOrcamento(), DominioCriterioJulgamentoOrcamento.I) &&	
						 parametroFormaPagamentoImportacao.getVlrNumerico().shortValue() == codigoFormaPag){
					valor = valorTotalLict;
				}
				
				BigDecimal valorItemAprovado = this.calcularQuantidadeValorAprovada(propostaFornecedor,DominioTipoFaseSolicitacao.S);
				   
			 
				valorItemAprovado = valorItemAprovado.multiply((licitacao.getFrequenciaEntrega() !=null ? new BigDecimal(licitacao.getFrequenciaEntrega()): BigDecimal.ZERO)) ; 

				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				   if (!getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), ASSINAR_AF) &&
					   !getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), LIBERAR_ALT_CRITICA)){
							
					   AghParametros parametroPercLimiteCompra = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PERC_LIMITE_COMPRA_NORMAL);
					
					   BigDecimal valorItemAprovPerc = valorItemAprovado;
					   BigDecimal valorPercentual = valorItemAprovado.multiply(new BigDecimal(parametroPercLimiteCompra.getVlrNumerico().doubleValue() / 100));
					   valorItemAprovPerc = valorItemAprovPerc.add(valorPercentual);
					   					   					   
					   if (valor.compareTo(valorItemAprovPerc) > 0){
						  if (flagCriticaValor && !Objects.equals(itemAf.getIndSituacao(), DominioSituacaoAutorizacaoFornecimento.EF)){
							  throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.ERRO_MENSAGEM_MSG31, parametroPercLimiteCompra.getVlrNumerico().intValue(), valorItemAprovado);
						  }
					   }						
				   }
				   else {							   
						AghParametros parametroPercLimiteCompra = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PERC_LIMITE_COMPRA_CHEFIA);
												
						BigDecimal valorItemAprovPerc = valorItemAprovado;
						BigDecimal valorPercentual = valorItemAprovado.multiply(new BigDecimal(parametroPercLimiteCompra.getVlrNumerico().doubleValue() / 100));
						valorItemAprovPerc = valorItemAprovPerc.add(valorPercentual);
												
						if (valor.compareTo(valorItemAprovPerc) > 0){
							if (flagCriticaValor && !Objects.equals(itemAf.getIndSituacao(), DominioSituacaoAutorizacaoFornecimento.EF) &&
								!flagLiberarCritica){
								throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.ERRO_MENSAGEM_MSG32,valorItemAprovado, parametroPercLimiteCompra.getVlrNumerico().intValue());
							}
						}
				   }			
				
			}
		}
	}
	
	
	public void validarItemEntregue(List<ScoFaseSolicitacao> fasesSolicitacao, Integer qtdeRecebida, Integer qtdeSolicitada,
                                    Double valorUnitario, Double valorEfetivado) throws ApplicationBusinessException {		
			
		if (fasesSolicitacao != null && !fasesSolicitacao.isEmpty()){	
			
			qtdeRecebida = (Integer) CoreUtil.nvl(qtdeRecebida, 0);		
			qtdeSolicitada = (Integer) CoreUtil.nvl(qtdeSolicitada,0);
			valorUnitario = (Double) CoreUtil.nvl(valorUnitario,0);
			valorEfetivado = (Double) CoreUtil.nvl(valorEfetivado,0);
			
			ScoFaseSolicitacao faseSolicitacao = fasesSolicitacao.get(0);		
			if (faseSolicitacao.getTipo().equals(DominioTipoFaseSolicitacao.C)) {				
				if (qtdeSolicitada < qtdeRecebida) {
					throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.MENSAGEM_QTDE_SOLICITADA_MENOR_RECEBIDA);
				}
			} else {
				if (valorUnitario < valorEfetivado) { 
					throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.MENSAGEM_VALOR_ITEM_MENOR_EFETIVADO);
				}
			}
		}
		else {
			throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.ERRO_NAO_EXISTE_FASE_MSG14);
		}
	}
	
	public void atualizarSituacaoItemAutorizacaoFornecimento(ScoItemAutorizacaoForn itemAf, ScoFaseSolicitacao faseSolicitacao, Boolean chkEstorno) {
		if (!itemAf.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EX) && !itemAf.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EP)) {
			
			Integer qtdeRecebida = (Integer) CoreUtil.nvl(itemAf.getQtdeRecebida(), 0);		
			Integer qtdeSolicitada = (Integer) CoreUtil.nvl(itemAf.getQtdeSolicitada(),0);
			Double valorUnitario = (Double) CoreUtil.nvl(itemAf.getValorUnitario(),0);
			Double valorEfetivado = (Double) CoreUtil.nvl(itemAf.getValorEfetivado(),0);
			
			
			if (faseSolicitacao.getTipo().equals(DominioTipoFaseSolicitacao.C)) {				
				if (qtdeRecebida == 0 && qtdeSolicitada > 0) {
					itemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.AE);
				} else if (qtdeRecebida < qtdeSolicitada) {
					itemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.PA);
				} else if (qtdeRecebida >= qtdeSolicitada) {
					itemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.EF);
				}
			} else {
				if (valorEfetivado == 0.00 && (qtdeSolicitada * valorUnitario > 0.00)) {
					itemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.AE);
				} else if (itemAf.getValorEfetivado() < itemAf.getValorUnitario()) {
					itemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.PA);
				}
					
				if (!chkEstorno && valorEfetivado.equals(valorUnitario)) {
					itemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.EF);					
				}
			}
		}
	}
	
	public void atualizarQuantidadeEntregueSc(ScoItemAutorizacaoForn itemAf, ScoItemAutorizacaoForn itemOriginal, List<ScoFaseSolicitacao> listaFases) {
		if (listaFases != null){
			for (ScoFaseSolicitacao fase : listaFases) {
				ScoSolicitacaoDeCompra solCompra = null;
				if (!fase.getExclusao() && fase.getSolicitacaoDeCompra() != null) {
					solCompra = fase.getSolicitacaoDeCompra();
					
					if (!solCompra.getExclusao()) {
						
						Long qtdeRecebida = ((Integer) CoreUtil.nvl(itemAf.getQtdeRecebida(), 0)).longValue();	
						Long qtdeRecebidaOriginal = ((Integer) CoreUtil.nvl(itemOriginal.getQtdeRecebida(), 0)).longValue();	
						Long fatorConversao = ((Integer) CoreUtil.nvl(itemAf.getFatorConversao(),0)).longValue();
						Long qtdeEntregueSC = (solCompra.getQtdeEntregue() != null ? solCompra.getQtdeEntregue() : 0);
						
						
						solCompra.setQtdeEntregue(qtdeEntregueSC + 
								((qtdeRecebida - qtdeRecebidaOriginal) * fatorConversao));
						
						this.getScoSolicitacoesDeComprasDAO().atualizar(solCompra);
					}
				}
			}
		}
	}
	
	public void validarAlteracaoIndExclusaoItemAf(ScoItemAutorizacaoForn itemAf, ScoItemAutorizacaoForn itemOriginal, List<ScoFaseSolicitacao> listaFases) throws BaseException {
		if (itemAf.getIndExclusao() && !itemOriginal.getIndExclusao()) {
			this.alterarIndExclusaoItemAf(itemAf, true, (itemOriginal.getItemPropostaFornecedor() != null), listaFases);
		} else if (!itemAf.getIndExclusao() && itemOriginal.getIndExclusao()) {
			this.alterarIndExclusaoItemAf(itemAf, false, (itemOriginal.getItemPropostaFornecedor() != null), listaFases);
		}
	}
	
	private void alterarIndExclusaoItemAf(ScoItemAutorizacaoForn itemAf, Boolean inativar, Boolean possuiItemProposta, List<ScoFaseSolicitacao> listaFases) throws BaseException {
		Date dtExclusao = null;
		if (inativar) {
			dtExclusao = new Date();
			itemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.EX);
		} else {
			itemAf.setIndSituacao(DominioSituacaoAutorizacaoFornecedor.AE);
		}
		
		itemAf.setDtExclusao(dtExclusao);
		itemAf.setIndExclusao(inativar);
		
		for (ScoFaseSolicitacao fase : listaFases) {
			ScoFaseSolicitacao fasePersist = this.getScoFaseSolicitacaoDAO().obterPorChavePrimaria(fase.getNumero());
			if (fasePersist != null) {
				fasePersist.setExclusao(inativar);
				fasePersist.setDtExclusao(dtExclusao);
				
				this.getComprasFacade().atualizarScoFaseSolicitacao(fasePersist, fasePersist);
				
			}
			
			if (possuiItemProposta) {
				ScoItemLicitacao itemLicitacao = (fase.getItemLicitacao() != null) ? this.getScoItemLicitacaoDAO().obterPorChavePrimaria(fase.getItemLicitacao().getId()) : null;
				
				if (itemLicitacao != null) {
					itemLicitacao.setEmAf(!inativar);
					this.getPacFacade().atualizarItemLicitacao(itemLicitacao);					
				} 
			} else {
				throw new ApplicationBusinessException(ScoItemAutorizacaoFornRNExceptionCode.MENSAGEM_ITEM_AF_SEM_PROPOSTA);
			}
		}
	}
	
	public ScoItemAutorizacaoForn obterItemAfOriginal(ItensAutFornVO itemAf) {
		ScoItemAutorizacaoFornId itemId = new ScoItemAutorizacaoFornId();
		itemId.setAfnNumero(itemAf.getAfnNumero());
		itemId.setNumero(itemAf.getNumero());
		ScoItemAutorizacaoForn scoItemAutForn = this.getScoItemAutorizacaoFornDAO().obterOriginal(itemId);		
		scoItemAutForn.setScoFaseSolicitacao(this.getScoFaseSolicitacaoDAO().pesquisaItensAutPropostaFornecedor(itemId));	
		scoItemAutForn.setItemAutorizacaoFornJn(new HashSet<ScoItemAutorizacaoFornJn>(this.getScoItemAutorizacaoFornJnDAO().pesquisarItemAutFornJnPorItem(itemAf.getAfnNumero(), itemAf.getNumero())));
		
		scoItemAutForn.setIndContrato((Boolean) CoreUtil.nvl(scoItemAutForn.getIndContrato(),false));
		scoItemAutForn.setIndConsignado((Boolean) CoreUtil.nvl(scoItemAutForn.getIndConsignado(),false));		
		scoItemAutForn.setIndEstorno((Boolean) CoreUtil.nvl(scoItemAutForn.getIndEstorno(),false));
		scoItemAutForn.setIndExclusao((Boolean) CoreUtil.nvl(scoItemAutForn.getIndExclusao(),false));
		scoItemAutForn.setIndPreferencialCum((Boolean) CoreUtil.nvl(scoItemAutForn.getIndPreferencialCum(),false));
		scoItemAutForn.setIndProgrEntgAuto((Boolean) CoreUtil.nvl(scoItemAutForn.getIndProgrEntgAuto(),false));
		scoItemAutForn.setIndRecebimento((Boolean) CoreUtil.nvl(scoItemAutForn.getIndRecebimento(),false));
		
		return scoItemAutForn;
	}
	
	public void validaIndConsignado(Boolean indConsignado, Boolean indContrato) throws ApplicationBusinessException{
		if (Boolean.TRUE.equals(indConsignado) &&
			Boolean.FALSE.equals(indContrato)){
			throw new ApplicationBusinessException(
					ScoItemAutorizacaoFornRNExceptionCode.ERRO_MENSAGEM_MAT_CONSIG); 
		}
	}
	
	private DominioSituacaoAutorizacaoFornecimento calcularSituacaoAf(Boolean temEx, Boolean temEf, Boolean temEp, Boolean temAe, Boolean temPa) {
		DominioSituacaoAutorizacaoFornecimento situacaoAf = null;
		
		if (!temEf && !temEp && !temAe && !temPa) {
			situacaoAf = DominioSituacaoAutorizacaoFornecimento.EX;
		} else if (temEf && !temEp && !temAe && !temPa) {
			situacaoAf = DominioSituacaoAutorizacaoFornecimento.EF;
		} else if ((!temEf && temEp && !temAe && !temPa) || (temEf && temEp && !temAe && !temPa)) {
			situacaoAf = DominioSituacaoAutorizacaoFornecimento.EP;
		} else if (!temEf && !temEp && temAe && !temPa) {
			situacaoAf = DominioSituacaoAutorizacaoFornecimento.AE;
		} else if ((temPa) || (temEf && temAe) || (temEp && temAe)) {
			situacaoAf = DominioSituacaoAutorizacaoFornecimento.PA;
		} else if (temEx) {
			situacaoAf = DominioSituacaoAutorizacaoFornecimento.EX;
		}
		
		return situacaoAf;
	}
	
	// RN 47
	public DominioSituacaoAutorizacaoFornecimento obterSituacaoAf(List<ScoItemAutorizacaoForn> listaItens) {
		Boolean temEx = Boolean.FALSE;
		Boolean temEf = Boolean.FALSE;
		Boolean temEp = Boolean.FALSE;
		Boolean temAe = Boolean.FALSE;
		Boolean temPa = Boolean.FALSE;
		
		for (ScoItemAutorizacaoForn item : listaItens) {			
			if (item.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EX)) {
				temEx = Boolean.TRUE;
			} else if (item.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EF)) {
				temEf = Boolean.TRUE;
			} else if (item.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.EP)) {
				temEp = Boolean.TRUE;
			} else if (item.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.AE)) {
				temAe = Boolean.TRUE;
			} else if (item.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecedor.PA)) {
				temPa = Boolean.TRUE;
			}
		}
		
		return this.calcularSituacaoAf(temEx, temEf, temEp, temAe, temPa);
	}
	
	public void atualizarSituacaoAf(ScoItemAutorizacaoForn  itemAfOriginal, ScoItemAutorizacaoForn  itemAf) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		ScoAutorizacaoForn af = this.scoAutorizacaoFornDAO.obterPorChavePrimaria(itemAf.getId().getAfnNumero());

		if (af != null) {
			DominioSituacaoAutorizacaoFornecimento situacao = obterSituacaoAf(af.getItensAutorizacaoForn());
			af.setSituacao(situacao);
			af.setServidorControlado(servidorLogado);

			if (!Objects.equals(itemAfOriginal.getValorUnitario(), itemAf.getValorUnitario()) ||
				(!Objects.equals(itemAfOriginal.getQtdeSolicitada(), itemAf.getQtdeSolicitada()) && 
							this.getScoFaseSolicitacaoDAO().obterTipoFaseSolicitacaoPorNumeroAF(itemAf.getId().getAfnNumero()).equals(DominioTipoFaseSolicitacao.C))) {
				af.setDtAlteracao(new Date());
			}

			// nao pode passar pela trigger da AF pois da loop (uma RN chama a outra)  
			this.scoAutorizacaoFornDAO.persistir(af);	
		}

	}
	
	protected ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}
	
	protected ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return scoSolicitacaoServicoDAO;
	}
		
	protected IPacFacade getPacFacade() {
		return pacFacade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	protected ScoItemAutorizacaoFornJnDAO getScoItemAutorizacaoFornJnDAO() {
		return scoItemAutorizacaoFornJnDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}
	
	protected ScoAutorizacaoFornRN getScoAutorizacaoFornRN() {
		return scoAutorizacaoFornRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
