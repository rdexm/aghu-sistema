package br.gov.mec.aghu.compras.pac.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLoteLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoPregaoBBVO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ScoItemLicitacaoRN extends BaseBusiness{

private static final String NOME_ARQUIVO_SOLICITAR_CODIGO_BB = "AOP713";

private static final String PREGAO_BB_COD_IDIOMA = "0001";

private static final String DATE_FORMAT = "yyyy-MM-dd-HH.mm.ss.SSSSSS";

private static final String CONSTANTE_0002 = "0002";

private static final String PREGAO_BB_COD_UNID_ORG = "001";

    @EJB
    private ScoItemLicitacaoON scoItemLicitacaoON;

	private static final Log LOG = LogFactory.getLog(ScoItemLicitacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;
	
	@Inject
	private ScoLoteLicitacaoDAO scoLoteLicitacaoDAO;
	
	@Inject
	private AghParametrosDAO aghParametrosDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1407727972903643639L;

	public enum ScoItemLicitacaoRNExceptionCode implements BusinessExceptionCode {
		SCO_ITEM_SEM_VALOR_UNIT, MENSAGEM_ITEMPAC_M03, MENSAGEM_ITEMPAC_M04, MENSAGEM_ITEMPAC_M05, 
		MENSAGEM_ITEMPAC_M06, MENSAGEM_ITEMPAC_M10, MENSAGEM_ITEMPAC_M14, MS04_SEM_PERMISSAO_DIRETORIO,
		MS05_DESEJA_VINCULAR_LOTE, MS02_SEM_PARAMETRO, MS06_MERCADORIA_BB_SEM_CONTEUDO, 
		SEM_PERMISSAO_DIRETORIO
	}

	public void inserirItemLicitacao(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException {
		this.preInserirItemLicitacao(itemLicitacao);
		this.getScoItemLicitacaoDAO().persistir(itemLicitacao);
		this.posInserirItemLicitacao(itemLicitacao);
	}
	
	public void atualizarItemLicitacao(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException {
		ScoItemLicitacao itemLicitacaoOld = this.getScoItemLicitacaoDAO().obterOriginal(itemLicitacao);
		
		this.preAtualizarItemLicitacao(itemLicitacao, itemLicitacaoOld);
		this.getScoItemLicitacaoDAO().merge(itemLicitacao);
		
		this.posAtualizarItemLicitacao(itemLicitacao, itemLicitacaoOld);
	}

	public void excluirItemLicitacao(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException {
		ScoItemLicitacao itemLicitacaoOld = this.getScoItemLicitacaoDAO().obterOriginal(itemLicitacao);
				
		this.getScoItemLicitacaoDAO().remover(itemLicitacao);
		this.posExcluirItemLicitacao(itemLicitacao, itemLicitacaoOld);
	}

	/**
	 * @ORADB SCOT_ITL_BRI
	 * @param itemLicitacao
	 * @throws ApplicationBusinessException
	 */
	private void preInserirItemLicitacao(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException {
		if(itemLicitacao == null || itemLicitacao.getValorUnitario() == null || itemLicitacao.getValorUnitario().equals(BigDecimal.ZERO)){
			throw new ApplicationBusinessException(ScoItemLicitacaoRNExceptionCode.SCO_ITEM_SEM_VALOR_UNIT, itemLicitacao.getId().getNumero());
		}
		
		// Todo item em princípio tem julg_parcial = 'N'.   Pode passar para 'S' na tela de alteração do item quando tem seu julgamento adiado.
		itemLicitacao.setJulgParcial(false);
	}

	
	
	/**
	 * @param itemLicitacao
	 * @param itemLicitacaoOld
	 * @throws ApplicationBusinessException
	 */
	private void posExcluirItemLicitacao(ScoItemLicitacao itemLicitacao, ScoItemLicitacao itemLicitacaoOld) throws ApplicationBusinessException {
		//
	}
	
	/**
	 * @ORADB SCOT_ITL_BRU
	 * @param itemLicitacao
	 * @param itemLicitacaoOld
	 * @throws ApplicationBusinessException
	 */
	private void preAtualizarItemLicitacao(ScoItemLicitacao itemLicitacao, ScoItemLicitacao itemLicitacaoOld) throws ApplicationBusinessException {				
			
		if (CoreUtil.modificados(itemLicitacao.getJulgParcial(), itemLicitacaoOld.getJulgParcial())) {
			// RN02
			if (this.getScoItemLicitacaoON().verificarLicitacaoProposta(itemLicitacaoOld.getId().getLctNumero(), 
					itemLicitacaoOld.getId().getNumero(), true)) {
				throw new ApplicationBusinessException(
						ScoItemLicitacaoRNExceptionCode.MENSAGEM_ITEMPAC_M03, itemLicitacaoOld.getId().getNumero());
			}
			
			if (itemLicitacao.getExclusao()) {
				throw new ApplicationBusinessException(
						ScoItemLicitacaoRNExceptionCode.MENSAGEM_ITEMPAC_M04, itemLicitacaoOld.getId().getNumero());
			}
			
		}
	
		if (!Objects.equals(itemLicitacao.getValorUnitario().setScale(2, RoundingMode.UP), itemLicitacaoOld.getValorUnitario().setScale(2, RoundingMode.UP))) {
			this.validarAlteracaoValor(itemLicitacaoOld.getValorUnitario(), itemLicitacao.getValorUnitario(), 
					itemLicitacaoOld.getId().getLctNumero(), itemLicitacaoOld.getId().getNumero());
		}
		
		this.validarSolicitacaoUtilizadaOutraLicitacao(itemLicitacaoOld.getId().getLctNumero(), 
				itemLicitacaoOld.getId().getNumero());
		
		if(itemLicitacao.getExclusao() && !itemLicitacaoOld.getExclusao()) {
			Long quantidadePropostasEscolhidas = getScoItemPropostaFornecedorDAO().obterQuantidadePropostasEscolhidasPeloNumLicitacaoENumeroItem(itemLicitacao.getId().getLctNumero(), itemLicitacao.getId().getNumero());
			if(quantidadePropostasEscolhidas > 0) {
				throw new ApplicationBusinessException(ScoItemLicitacaoRNExceptionCode.MENSAGEM_ITEMPAC_M03, itemLicitacao.getId().getNumero());
			}
			itemLicitacao.setDtExclusao(new Date());
		}
	}

	/**
	 * @ORADB SCOT_ITL_ASI - SCOP_ENFORCE_ITL_RULES
	 * @param itemLicitacao
	 * @throws ApplicationBusinessException
	 */
	private void posInserirItemLicitacao(ScoItemLicitacao itemLicitacao) throws ApplicationBusinessException {
		this.enforceItlRules(itemLicitacao, null, DominioOperacaoBanco.INS);
	}

	/**
	 * @ORADB SCOT_ITL_ASU - SCOP_ENFORCE_ITL_RULES
	 * @param itemLicitacao
	 * @param itemLicitacaoOld
	 * @throws ApplicationBusinessException
	 */
	private void posAtualizarItemLicitacao(ScoItemLicitacao itemLicitacao, ScoItemLicitacao itemLicitacaoOld) throws ApplicationBusinessException {
		this.atualizarSituacaoLicitacao(itemLicitacao.getId().getLctNumero());
		this.enforceItlRules(itemLicitacao, itemLicitacaoOld, DominioOperacaoBanco.UPD);
	}

	/**
	 * Valida se a solicitacao a qual o item de licitação se refere já existe em outra licitação 
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @throws ApplicationBusinessException
	 */
	public void validarSolicitacaoUtilizadaOutraLicitacao(Integer numeroLicitacao, Short numeroItem) throws ApplicationBusinessException {
		if (this.getScoFaseSolicitacaoDAO().pesquisarQuantidadeSCOutraLicitacaoPorNumeroLCTENumeroItem(numeroLicitacao, numeroItem) > 0 || 
			this.getScoFaseSolicitacaoDAO().pesquisarQuantidadeSSOutraLicitacaoPorNumeroLCTENumeroItem(numeroLicitacao, numeroItem) > 0) {
			
			List<ScoFaseSolicitacao> listaFasesAtual = this.getScoFaseSolicitacaoDAO().
					obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(numeroLicitacao, numeroItem);
			
			for (ScoFaseSolicitacao fase : listaFasesAtual) {
				if (fase.getExclusao() == Boolean.FALSE) {
					if (fase.getSolicitacaoDeCompra() != null && 
							fase.getSolicitacaoDeCompra().getExclusao() == Boolean.FALSE) {

						List<ScoFaseSolicitacao> listaFasesSolicitacao = this.getScoFaseSolicitacaoDAO().
								obterFaseSolicitacao(fase.getSolicitacaoDeCompra().getNumero(), false, DominioTipoSolicitacao.SC);
						
						for (ScoFaseSolicitacao item : listaFasesSolicitacao) {
							if (item.getItemLicitacao().getId().getLctNumero() != numeroLicitacao) {
								throw new ApplicationBusinessException(
										ScoItemLicitacaoRNExceptionCode.MENSAGEM_ITEMPAC_M05, 
											fase.getSolicitacaoDeCompra().getNumero(), 
											item.getItemLicitacao().getId().getLctNumero());		
							}
						}
					} else {
						if (fase.getSolicitacaoServico() != null && 
								fase.getSolicitacaoServico().getIndExclusao() == Boolean.FALSE) {
							
							List<ScoFaseSolicitacao> listaFasesSolicitacao = this.getScoFaseSolicitacaoDAO().
									obterFaseSolicitacao(fase.getSolicitacaoServico().getNumero(), false, DominioTipoSolicitacao.SS);
							
							for (ScoFaseSolicitacao item : listaFasesSolicitacao) {
								if (!item.getItemLicitacao().getId().getLctNumero().equals(numeroLicitacao)) {
									throw new ApplicationBusinessException(
											ScoItemLicitacaoRNExceptionCode.MENSAGEM_ITEMPAC_M06, 
												fase.getSolicitacaoServico().getNumero(), 
												item.getItemLicitacao().getId().getLctNumero());		
								}
							}
						}	
					}	
				}
			}
		}		
	}

	
	private void validarAlteracaoValor(BigDecimal vlrOriginal, BigDecimal vlrTela, Integer numeroLicitacao, Short numeroItemLicitacao) throws ApplicationBusinessException {
		if (!vlrOriginal.equals(vlrTela) && 
				this.getScoParcelasPagamentoDAO().verificarLicitacaoPossuiCondicaoPorValor(numeroLicitacao, numeroItemLicitacao)) {
			throw new ApplicationBusinessException(
					ScoItemLicitacaoRNExceptionCode.MENSAGEM_ITEMPAC_M14);
		}
	}
	
	/**
	 * @ORADB SCOP_ENFORCE_ITL_RULES
	 * @param itemLicitacao
	 * @param itemLicitacaoOld
	 * @param operacao
	 * @throws ApplicationBusinessException
	 */
	private void enforceItlRules(ScoItemLicitacao itemLicitacao, ScoItemLicitacao itemLicitacaoOld, DominioOperacaoBanco operacao) throws ApplicationBusinessException {
		Boolean confirmaClassif = Boolean.FALSE;
		/*confirmaClassif = (Boolean) obterContextoSessao("SCOK_ITL_V_CONFIRMA_GERA_CLASSIF");
		if(confirmaClassif==null){
			confirmaClassif=Boolean.FALSE;
		}*/
		if(confirmaClassif) {
			List<ScoItemLicitacao> solicitacoes = getScoItemLicitacaoDAO().pesquisarItemLicitacaoPorNumLicitacaoEIndExclusao(itemLicitacao.getId().getLctNumero(), Boolean.FALSE);
			for(ScoItemLicitacao itemSol : solicitacoes) {
				itemSol.setClassifItem(itemSol.getId().getNumero());
			}
		}
		
		/* testa o evento e o indicador que o mestre esta sendo excluido */
		if(!confirmaClassif && itemLicitacao.getExclusao()) {
			Long numeroSolicitacoes = getScoItemLicitacaoDAO().pesquisarItemLicitacaoPorNumLicitacaoEIndExclusaoCount(itemLicitacao.getId().getLctNumero(), Boolean.FALSE);
			if(numeroSolicitacoes == 0) {
				ScoLicitacao licitacao = getScoLicitacaoDAO().obterPorChavePrimaria(itemLicitacao.getLicitacao().getNumero());
				licitacao.setExclusao(true);
				licitacao.setMotivoExclusao("Exclusão dos Itens");
			}
		}
		
		if(DominioOperacaoBanco.UPD.equals(operacao)) {
			if(CoreUtil.modificados(itemLicitacao.getPropostaEscolhida(), itemLicitacaoOld.getPropostaEscolhida()) && itemLicitacao.getPropostaEscolhida()) {
				this.rnItlpAtuLicJulg(itemLicitacao.getId().getLctNumero());
			}
					
		}
		if(DominioOperacaoBanco.INS.equals(operacao)) {
			this.rnItlpAtuLicGer(itemLicitacao.getId().getLctNumero());
		}
	}

	/**
	 * @ORADB RN_ITLP_ATU_LIC_JULG
	 * @param lctNumero
	 * @throws ApplicationBusinessException
	 */
	private void rnItlpAtuLicJulg(Integer lctNumero) throws ApplicationBusinessException {
		Boolean todasEscolhidas = Boolean.FALSE;
		Long numeroSolicitacoes = getScoItemLicitacaoDAO().pesquisarItemLicitacaoPorNumLicitacaoEIndExclusaoEIndPropostaEscolhidaCount(lctNumero, Boolean.FALSE, Boolean.FALSE);
		if(numeroSolicitacoes.equals(0)) {
			todasEscolhidas = Boolean.TRUE;
		}
		if(todasEscolhidas) {
			ScoLicitacao licitacao = getScoLicitacaoDAO().obterPorChavePrimaria(lctNumero);
			licitacao.setSituacao(DominioSituacaoLicitacao.JU);
		}
	}

	/**
	 * Atualiza a situação da licitação de gerada para julgado
	 * @param numeroLicitacao
	 */
	private void atualizarSituacaoLicitacao(Integer numeroLicitacao) {
		ScoLicitacao licitacao = this.getScoLicitacaoDAO().obterPorChavePrimaria(numeroLicitacao);
		if (licitacao != null) {
			if (this.verificarTodosItensLicitacaoJulgados(licitacao)) {
				licitacao.setSituacao(DominioSituacaoLicitacao.JU);
			} else {
				licitacao.setSituacao(DominioSituacaoLicitacao.GR);
			}
			this.getScoLicitacaoDAO().persistir(licitacao);
		}
	}
	
	/**
	 * Verifica se todos os itens de uma licitação foram julgados
	 * @param licitacao
	 * @return
	 */
	private Boolean verificarTodosItensLicitacaoJulgados(ScoLicitacao licitacao) {
		Boolean ret = Boolean.TRUE;

		for (ScoItemLicitacao item : licitacao.getItensLicitacao()) {
			//Alterado para desconsiderar os itens cancelados na validação
			if (item.getMotivoCancel() == null) {
				if (item.getPropostaEscolhida() == Boolean.FALSE && item.getExclusao() == Boolean.FALSE) {
					ret = Boolean.FALSE;
				}
			}
		}

		return ret;
	}
	
	/**
	 * @ORADB RN_ITLP_ATU_LIC_JULG
	 * @param lctNumero
	 * @throws ApplicationBusinessException
	 */
	private void rnItlpAtuLicGer(Integer lctNumero) throws ApplicationBusinessException {
		ScoLicitacao licitacao = getScoLicitacaoDAO().obterPorChavePrimaria(lctNumero);
		licitacao.setSituacao(DominioSituacaoLicitacao.GR);
	}
	
	//header
	private String montarHeaderArqPregaoBB(String ParamCodCliente, String ParamIdtTrocaArq, String ParamConvIedAop, Integer ParamSequenciaPregaoBB){
		StringBuilder sb = new StringBuilder(100);
		
		sb.append(StringUtils.repeat("0", 11))
		.append(getResourceBundleValue("NOME_ARQ_REMESSA_LICIT_PREGAO_BB"))
		.append(StringUtils.repeat(StringUtils.SPACE, 2))
		.append(DateUtil.obterDataFormatada(Calendar.getInstance().getTime(), "ddMMyyyy"))
		.append(StringUtils.repeat(StringUtils.SPACE, 2))
		.append(ParamCodCliente)
		.append(StringUtils.repeat(StringUtils.SPACE, 2))
		.append(ParamIdtTrocaArq)
		.append(StringUtils.repeat(StringUtils.SPACE, 2))
		.append(ParamConvIedAop)
		.append(StringUtils.SPACE)
		.append(StringUtils.repeat(StringUtils.SPACE, 120))
		.append(StringUtils.leftPad(ParamSequenciaPregaoBB.toString(), 9, "0"))
		.append(StringUtils.repeat(StringUtils.SPACE, 835))
		.append(StringUtils.LF);
		
		return sb.toString();
	}
	
	//tipo 01
	private String montarCabecalhoLicitacaoArqPregaoBB(ScoLicitacao licitacao, String ParamConvIedAop){
		StringBuilder sb = new StringBuilder(20);
		Calendar dataAtual = Calendar.getInstance();
		
		sb.append("01")
		.append(PREGAO_BB_COD_UNID_ORG)
		.append(licitacao.getNumero().toString())
		.append(ParamConvIedAop)
		.append(StringUtils.leftPad(PREGAO_BB_COD_UNID_ORG, 9, "0"));
		switch (licitacao.getMlcCodigo()) {
			case "DI":
				sb.append(PREGAO_BB_COD_IDIOMA);
				break;
			case "PG":
				sb.append("0002");
				break;
			case "CV":
				sb.append("0003");
				break;
			case "CP":
				sb.append("0004");
				break;
			case "TP":
				sb.append("0004");
				break;
			case "CC":
				sb.append("0004");
				break;
			default:
				sb.append("0000");
				break;
		}
		sb.append(PREGAO_BB_COD_IDIOMA)
		.append("0103");
		if(licitacao.getNumEdital() != null){
			sb.append(StringUtils.rightPad(licitacao.getNumEdital().toString(), 10));
		} else if (licitacao.getNumDocLicit() != null){
			sb.append(StringUtils.rightPad(licitacao.getNumDocLicit().toString(), 10));			
		} else {
			sb.append(StringUtils.rightPad("0", 10));			
		}
		sb.append(StringUtils.rightPad(licitacao.getNumero().toString(), 20))
		.append(DateUtil.obterDataFormatada(dataAtual.getTime(), "dd.MM.yyyy"))
		.append(DateUtil.obterDataFormatada(DateUtil.adicionaHoras(dataAtual.getTime(), 24), DATE_FORMAT))
		.append(DateUtil.obterDataFormatada(DateUtil.adicionaHoras(dataAtual.getTime(), 48), DATE_FORMAT))
		.append(DateUtil.obterDataFormatada(DateUtil.adicionaHoras(dataAtual.getTime(), 72), DATE_FORMAT))
		.append(DateUtil.obterDataFormatada(DateUtil.adicionaHoras(dataAtual.getTime(), 96), DATE_FORMAT))
		.append(CONSTANTE_0002)
		.append(PREGAO_BB_COD_IDIOMA)
		.append(PREGAO_BB_COD_IDIOMA)
		.append(StringUtils.repeat(StringUtils.SPACE, 828))
		.append(StringUtils.LF);
		
		return sb.toString();
	}
	
	//tipo 02
	private String montarTextoLicitacaoArqPregaoBB(ScoLicitacao licitacao){
		StringBuilder sb = new StringBuilder();
		
		sb.append("02")
		.append(PREGAO_BB_COD_UNID_ORG)
		.append(licitacao.getNumero())
		.append(PREGAO_BB_COD_IDIOMA)
		.append(StringUtils.rightPad(licitacao.getDescricao().replace(StringUtils.LF, " "), 1000))
		.append(StringUtils.repeat(StringUtils.SPACE, 10))
		.append(StringUtils.LF);
		
		return sb.toString();
	}
	
	//tipo 03
	private String montarCabecalhoLoteArqPregaoBB(ScoLicitacao licitacao, ItemLicitacaoPregaoBBVO itemVO){
		StringBuilder sb = new StringBuilder();
		
		sb.append("03")
		.append(PREGAO_BB_COD_UNID_ORG)
		.append(licitacao.getNumero())
		.append(StringUtils.leftPad(itemVO.getLlcNumero().toString(), 4, "0"))
		.append("000500020002")
		.append(StringUtils.repeat(StringUtils.SPACE, 998))
		.append(StringUtils.LF);
		
		return sb.toString();
	}
	
	//tipo 04
	private String montarTextoLoteArqPregaoBB(ScoLicitacao licitacao, ItemLicitacaoPregaoBBVO itemVO){
		StringBuilder sb = new StringBuilder();
		
		sb.append("04")
		.append(PREGAO_BB_COD_UNID_ORG)
		.append(licitacao.getNumero())
		.append(StringUtils.leftPad(itemVO.getLlcNumero().toString(), 4, "0"))
		.append(PREGAO_BB_COD_IDIOMA);
		String descricaoFormatada = itemVO.retornarLlcDescricao(); 
		if(descricaoFormatada.length() > 1000){
			sb.append(descricaoFormatada.substring(0, 1000));
		}else{
			sb.append(StringUtils.rightPad(descricaoFormatada, 1000));
		}
		sb.append(StringUtils.repeat(StringUtils.SPACE, 6))
		.append(StringUtils.LF);
		
		return sb.toString();
	}
	
	//tipo 05
	private String montarItemLicitacaoArqPregaoBB(ScoLicitacao licitacao, ItemLicitacaoPregaoBBVO itemVO) throws ApplicationBusinessException{
		StringBuilder sb = new StringBuilder();
		
		sb.append("05")
		.append(PREGAO_BB_COD_UNID_ORG)
		.append(licitacao.getNumero())
		.append(StringUtils.leftPad(itemVO.getLlcNumero().toString(), 4, "0"))
		.append(StringUtils.leftPad(itemVO.getItlNumero().toString(), 4, "0"))
		.append(StringUtils.leftPad(itemVO.retornarGmtCodigoMercadoriaBB().toString(), 9, "0"))
		.append("0000")
		.append(StringUtils.leftPad(itemVO.retornarQtdeAprovada().toString(), 15, "0"))
		.append(StringUtils.repeat(StringUtils.SPACE, 978))
		.append(StringUtils.LF);
		
		return sb.toString();
	}
	
	//tipo 06
	private String montarTextoItemLicitacaoArqPregaoBB(ScoLicitacao licitacao, ItemLicitacaoPregaoBBVO itemVO){
		StringBuilder sb = new StringBuilder();
		
		sb.append("06")
		.append(PREGAO_BB_COD_UNID_ORG)
		.append(licitacao.getNumero())
		.append(StringUtils.leftPad(itemVO.getLlcNumero().toString(), 4, "0"))
		.append(StringUtils.leftPad(itemVO.getItlNumero().toString(), 4, "0"))
		.append(PREGAO_BB_COD_IDIOMA);
		String descricaoItemFormatada = itemVO.retornarDescricaoItem();
		if (descricaoItemFormatada.length() > 1000-13+itemVO.retornarMatCodigo().toString().length()){
			sb.append(descricaoItemFormatada.substring(0, 1000-13+itemVO.retornarMatCodigo().toString().length()) 
					+ " - Cod HCPA: " + itemVO.retornarMatCodigo().toString());			
		} else {
			sb.append(StringUtils.rightPad(descricaoItemFormatada
					+ " - Cod HCPA: " + itemVO.retornarMatCodigo().toString(), 1000));
		}
		sb.append(StringUtils.repeat(StringUtils.SPACE, 2))
		.append(StringUtils.LF);
		
		return sb.toString();
	}
	
	//tipo 07
	private String montarAutorizacaoLicitacaoArqPregaoBB(ScoLicitacao licitacao){
		StringBuilder sb = new StringBuilder();
		
		sb.append("07")
		.append(PREGAO_BB_COD_UNID_ORG)
		.append(licitacao.getNumero())
		.append("000056435")
		.append(CONSTANTE_0002)
		.append(StringUtils.repeat(StringUtils.SPACE, 1001))
		.append(StringUtils.LF);
		
		return sb.toString();
	}
	
	//Trailler
	private String montarTraillerArqPregaoBB(Integer qtdeLinhas){
		StringBuilder sb = new StringBuilder();
		qtdeLinhas++;
		sb.append(StringUtils.repeat("9", 11))
		.append(StringUtils.leftPad(qtdeLinhas.toString(), 9, "0"))
		.append(StringUtils.repeat(StringUtils.SPACE, 1005))
		.append(StringUtils.LF);
		
		return sb.toString();
	}
	
	/**
	 * Verifica se a licitação possui lotes associados.
	 * @param licitacao Objeto licitacao que deve ser validado.
	 * @return String vazia se há lotes cadastrados e uma mensagem caso não haja lotes cadastrados.
	 * @throws ApplicationBusinessException
	 */
	private String validarLoteLicitacao(List<ScoLicitacao> licitacaoList) {
		for (ScoLicitacao licitacao : licitacaoList){
			List<ScoLoteLicitacao> listaLote = scoLoteLicitacaoDAO.listarLotes(licitacao);
			if (listaLote == null || listaLote.isEmpty()){
				return getResourceBundleValue("MS05_DESEJA_VINCULAR_LOTE", licitacao.getNumero(), licitacao.getDescricao());
			}
		}
		return StringUtils.EMPTY;
	}
	
	private void validarParametroDiretorio(AghParametros parametro) throws ApplicationBusinessException{
		if(parametro == null || parametro.getVlrTexto() == null || parametro.getVlrTexto().isEmpty()){
			throw new ApplicationBusinessException(ScoItemLicitacaoRNExceptionCode.MS02_SEM_PARAMETRO);
		}
	}
	/**
	 * @ORADB AOP711_EMISSAO_LICITACAO
	 * @param licitacao
	 * @param listaItensLicitacao
	 * @param ParamCodCliente
	 * @param ParamIdtTrocaArq
	 * @param ParamConvIedAop
	 * @param ParamSequenciaPregaoBB
	 * @param ParamDirRem
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	public String montarArqPregaoBB(List<Integer> nrosPac, AghParametros paramDir) throws ApplicationBusinessException {
		validarParametroDiretorio(paramDir);
		scoItemLicitacaoON.validarLicitacaoSelecionada(nrosPac);
		
		List<ScoLicitacao> listLicitacao = scoLicitacaoDAO.obterListaLicitacoesPorNumeros(nrosPac);
		String ParamCodCliente = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_PG_BB_COD_CLIENTE.toString()).getVlrTexto();
		String ParamIdtTrocaArq = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_PG_BB_IDT_TROCA_ARQ.toString()).getVlrTexto();
		String ParamConvIedAop = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_PG_BB_CONV_IED_AOP.toString()).getVlrTexto();
				
		AghParametros ParamSequenciaPregaoBB = null;
		Integer sequenciaPregaoAtual = 0;
		List<ItemLicitacaoPregaoBBVO> listaItensLicitacao = null;
		final String ENCODE = "ISO-8859-1";
		File file = null;
		Writer out = null;
		int qtdeLinhas = 0;
		String nomeArquivo = StringUtils.EMPTY;
		String retorno =  StringUtils.EMPTY;
		
		String msgValidaLote = validarLoteLicitacao(listLicitacao);
		if (msgValidaLote != StringUtils.EMPTY){
			return msgValidaLote;
		}
		
		for (ScoLicitacao licitacao : listLicitacao){
			StringBuilder sb = new StringBuilder();
			ParamSequenciaPregaoBB = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_SEQUENCIA_PREGAO_BB.toString());
			if(ParamSequenciaPregaoBB != null){
				sequenciaPregaoAtual = ParamSequenciaPregaoBB.getVlrNumerico().intValue() + 1;
			} 
			qtdeLinhas = 0;
			file = null;
			out = null;
			nomeArquivo = StringUtils.EMPTY;
			//header
			sb.append(montarHeaderArqPregaoBB(ParamCodCliente, ParamIdtTrocaArq, ParamConvIedAop, sequenciaPregaoAtual))
			
			//tipo01
			.append(montarCabecalhoLicitacaoArqPregaoBB(licitacao, ParamConvIedAop))
			
			//tipo02
			.append(montarTextoLicitacaoArqPregaoBB(licitacao));
			qtdeLinhas = qtdeLinhas + 3;
			Integer codPac = licitacao.getNumero();
			listaItensLicitacao = scoLoteLicitacaoDAO.obterItensLicitacaoPregaoBB(codPac); 
			Map<String, String> controleItens = new HashMap<String, String>();
			for(ItemLicitacaoPregaoBBVO itemVO : listaItensLicitacao){
				if (controleItens.get(licitacao.getNumero().toString() + itemVO.getLlcNumero().toString()) == null) {
					controleItens.put(licitacao.getNumero().toString() + itemVO.getLlcNumero(), itemVO.getLlcDescricao());
					//tipo03
					sb.append(montarCabecalhoLoteArqPregaoBB(licitacao, itemVO));
					qtdeLinhas++;
					//tipo04
					sb.append(montarTextoLoteArqPregaoBB(licitacao, itemVO));
					qtdeLinhas++;
				}
				//tipo05
				if(itemVO.retornarGmtCodigoMercadoriaBB() == null || itemVO.retornarGmtCodigoMercadoriaBB() == 0){
					retorno = getResourceBundleValue("MS06_MERCADORIA_BB_SEM_CONTEUDO");
				}
				sb.append(montarItemLicitacaoArqPregaoBB(licitacao, itemVO));
				qtdeLinhas++;
				//tipo06
				sb.append(montarTextoItemLicitacaoArqPregaoBB(licitacao, itemVO));
				qtdeLinhas++;
			}
			//tipo07
			sb.append(montarAutorizacaoLicitacaoArqPregaoBB(licitacao));
			qtdeLinhas++;
			//trailler
			sb.append(montarTraillerArqPregaoBB(qtdeLinhas));
						
			nomeArquivo = paramDir.getVlrTexto() + getResourceBundleValue("NOME_ARQ_REMESSA_LICIT_PREGAO_BB") + "_" 
						+ licitacao.getNumero().toString() + getResourceBundleValue("EXTENSAO_REM");
			try {
				file = new File(nomeArquivo);
				out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);			
				out.write(sb.toString());
				out.flush();
				out.close();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ScoItemLicitacaoRNExceptionCode.MS04_SEM_PERMISSAO_DIRETORIO, paramDir.getVlrTexto());
			}
						
			//U1
			ParamSequenciaPregaoBB.setVlrNumerico(new BigDecimal(sequenciaPregaoAtual));
			aghParametrosDAO.atualizar(ParamSequenciaPregaoBB);
			aghParametrosDAO.flush();
			//U2
			licitacao.setDtGeracaoArqRemessa(new Date());
			licitacao.setNomeArqRemessa(getResourceBundleValue("NOME_ARQ_REMESSA_LICIT_PREGAO_BB") + "_" 
					+ licitacao.getNumero().toString() + getResourceBundleValue("EXTENSAO_REM"));
			scoLicitacaoDAO.atualizar(licitacao);
			scoLicitacaoDAO.flush();
		}
		return retorno;
	}
	
	/**
	@ORADB AOP713_SOLIC_CODIGOS_BB
	**/
	public String montarSolCodBB(List<Integer> P_PG_BB_NrosCli, AghParametros P_PG_BB_ParamDir) throws ApplicationBusinessException {
		validarParametroDiretorio(P_PG_BB_ParamDir);

		AghParametros P_PG_BB_ParamSequenciaPregaoBB = null;
		Integer sequenciaPregao = null;
		
//		List<ScoLicitacao> listLicitacao = scoLicitacaoDAO.obterListaLicitacoesPorNumeros(P_PG_BB_NrosCli);
		String P_PG_BB_ParamCodCliente = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_PG_BB_COD_CLIENTE.toString()).getVlrTexto();
		String P_PG_BB_ParamIdtTrocaArq = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_PG_BB_IDT_TROCA_ARQ.toString()).getVlrTexto();
		String P_PG_BB_ParamConvIedAop = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_PG_BB_CONV_IED_AOP.toString()).getVlrTexto();
		P_PG_BB_ParamSequenciaPregaoBB = aghParametrosDAO.obterAghParametroPorNome(AghuParametrosEnum.P_SEQUENCIA_PREGAO_BB.toString());
		
		if(P_PG_BB_ParamSequenciaPregaoBB != null){
			sequenciaPregao = P_PG_BB_ParamSequenciaPregaoBB.getVlrNumerico().intValue() + 1;
		}
		
		final String ENCODE = "ISO-8859-1";
		File file = null;
		Writer out = null;
		String nomeArquivo = StringUtils.EMPTY;
		String retorno =  StringUtils.EMPTY;
		
		StringBuilder sb = new StringBuilder();
		sb.append(montarArquivo(P_PG_BB_ParamCodCliente, P_PG_BB_ParamIdtTrocaArq, P_PG_BB_ParamConvIedAop, sequenciaPregao));

		nomeArquivo = P_PG_BB_ParamDir.getVlrTexto() + NOME_ARQUIVO_SOLICITAR_CODIGO_BB	+ getResourceBundleValue("EXTENSAO_REM");

		try {
			file = new File(nomeArquivo);
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(sb.toString());
			out.flush();
			out.close();

		} catch (Exception e) {
			throw new ApplicationBusinessException(ScoItemLicitacaoRNExceptionCode.SEM_PERMISSAO_DIRETORIO,	P_PG_BB_ParamDir.getVlrTexto());
		}
		
		return retorno;
	}
	
	public String montarArquivo(String P_PG_BB_ParamCodCliente, String P_PG_BB_ParamIdtTrocaArq, String P_PG_BB_ParamConvIedAop, Integer sequenciaPregao){
		int qtdlinha = 0;
		StringBuilder sb = new StringBuilder(100);
		sb.append(StringUtils.repeat("0", 11))
		.append(NOME_ARQUIVO_SOLICITAR_CODIGO_BB)
		.append(StringUtils.repeat(StringUtils.SPACE, 2))
		.append(DateUtil.obterDataFormatada(Calendar.getInstance().getTime(), "ddMMyyyy"))
		.append(StringUtils.repeat(StringUtils.SPACE, 2))
		.append(StringUtils.leftPad(P_PG_BB_ParamCodCliente, 8, StringUtils.SPACE))
		.append(StringUtils.repeat(StringUtils.SPACE, 2))
		.append(StringUtils.leftPad(P_PG_BB_ParamIdtTrocaArq , 8 , "0"))
		.append(StringUtils.repeat(StringUtils.SPACE, 2))
		.append(StringUtils.leftPad(P_PG_BB_ParamConvIedAop, 8 ,"0"))
		.append(StringUtils.SPACE)
		.append(StringUtils.repeat(StringUtils.SPACE, 120))
		.append("000000001")
		.append(StringUtils.LF)
		.append("00")
		.append(StringUtils.repeat(StringUtils.SPACE, 188))
		.append(StringUtils.LF)
		.append("99999999999")
		.append(StringUtils.leftPad("3", 9, "0"));
		qtdlinha++;
		sb.append(StringUtils.repeat(StringUtils.SPACE, 170));
		
		if(qtdlinha == 0){
			sb.append("Nenhum dado encontrado para a Licitação informada.");
		}
		return sb.toString();
	}
	
	private ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	private ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	protected ScoItemLicitacaoON getScoItemLicitacaoON() {
		return scoItemLicitacaoON;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	protected ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}


}