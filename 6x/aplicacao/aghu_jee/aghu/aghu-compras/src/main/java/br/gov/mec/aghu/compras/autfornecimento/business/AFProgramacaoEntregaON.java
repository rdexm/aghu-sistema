package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoProgramacaoEntregaDAO;
import br.gov.mec.aghu.compras.vo.ConsultaItensAFProgramacaoEntregaVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class AFProgramacaoEntregaON extends BaseBusiness {

	private static final long serialVersionUID = -148027677264781838L;
	
	public enum AFProgramacaoEntregaONExceptionCode implements BusinessExceptionCode {
		
	}
	
	private static final Log LOG =  LogFactory.getLog(AFProgramacaoEntregaON.class);
	
	@EJB
	private ScoItemAutorizacaoFornRN scoItemAutorizacaoFornRN;
	
	@EJB
	private ScoAutorizacaoFornRN scoAutorizacaoFornRN;
	
	@Inject
	private ScoSolicitacaoProgramacaoEntregaDAO scoSolicitacaoProgramacaoEntregaDAO;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public List<ConsultaItensAFProgramacaoEntregaVO> consultarItensAFProgramacaoEntrega(final Integer numeroAF) throws BaseException{
		
		final Boolean isIndExclusao = Boolean.FALSE;
		
		List<ConsultaItensAFProgramacaoEntregaVO> listVO = new  ArrayList<ConsultaItensAFProgramacaoEntregaVO>();
		
		listVO.addAll(getScoAutorizacaoFornDAO().consultarItensAFProgramacaoEntregaMaterial(numeroAF, isIndExclusao));
		listVO.addAll(getScoAutorizacaoFornDAO().consultarItensAFProgramacaoEntregaServico(numeroAF, isIndExclusao));

		return formatarConsultaItensAFProgramacaoEntregaVO(removerLinhasDuplicada(listVO));
		
	}
	
	public List<ConsultaItensAFProgramacaoEntregaVO> removerLinhasDuplicada(List<ConsultaItensAFProgramacaoEntregaVO> listVO) {
		Set<ConsultaItensAFProgramacaoEntregaVO> listFiltrada =  new HashSet<ConsultaItensAFProgramacaoEntregaVO>();
		listFiltrada.addAll(listVO);
	
		return new  ArrayList<ConsultaItensAFProgramacaoEntregaVO>(listFiltrada);

	}

	public void manterAutorizacaoFornecimento(final Integer numeroAF,
			final List<ConsultaItensAFProgramacaoEntregaVO> listVO) throws BaseException {
		
		for (ConsultaItensAFProgramacaoEntregaVO vo : listVO) {			
			atualizarScoItemAutorizacaoForn(numeroAF, vo);
		}
		
		atualizarScoAutorizacaoForn(listVO, numeroAF);
	}
	
	private void atualizarScoAutorizacaoForn(final List<ConsultaItensAFProgramacaoEntregaVO> listVO, final Integer numeroAF ) throws BaseException {
		
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		
		ScoAutorizacaoForn autorizacaoForn =  getScoAutorizacaoFornDAO().obterPorChavePrimaria(numeroAF);
		autorizacaoForn.setDtAlteracao(DateUtil.truncaData(new Date()));
		autorizacaoForn.setServidorControlado(servidorLogado);
		
		if (listVO != null && !listVO.isEmpty()){
			autorizacaoForn.setEntregaProgramada(listVO.get(0).getEntregaProgramada());
		}
		
		getScoAutorizacaoFornRN().atualizarAutorizacaoFornecimento(autorizacaoForn);
		
	}
	
	private void atualizarScoItemAutorizacaoForn(final Integer numeroAF,
			final ConsultaItensAFProgramacaoEntregaVO vo) throws BaseException {
		
		ScoItemAutorizacaoFornId id = new ScoItemAutorizacaoFornId();
		id.setAfnNumero(numeroAF);
		id.setNumero(vo.getNumeroDoItem().intValue());
		ScoItemAutorizacaoForn scoItemAutorizacaoForn = getScoItemAutorizacaoFornDAO().obterPorChavePrimaria(id);
		
		if(Boolean.TRUE.equals(scoItemAutorizacaoForn.getIndContrato()) && Boolean.FALSE.equals(vo.getIndContrato())){
			excluirParcelasNaoAssinadasItemDaAF(numeroAF, vo.getNumero());
		}
		
		scoItemAutorizacaoForn.setIndContrato(vo.getIndContrato());
		scoItemAutorizacaoForn.setIndProgrEntgAuto(vo.getIndProgrEntgAuto());
		scoItemAutorizacaoForn.setIndAnaliseProgrPlanej(vo.getIndAnaliseProgrPlanej());
		scoItemAutorizacaoForn.setIndProgrEntgBloq(vo.getIndProgrEntgBloq());

		getScoItemAutorizacaoFornRN().atualizarItemAutorizacaoFornecimento(scoItemAutorizacaoForn);
	
	}
	
	public void excluirParcelasNaoAssinadasItemDaAF(final Integer numeroAF, final Short numeroItem) throws BaseException {
		
		final Integer iafAfnNumero = numeroAF;
		final Integer iafNumero = numeroItem.intValue();
		final Boolean isIndAssinatura = Boolean.TRUE;
		final Integer qtdeEntregue = 0;
		
		
		List<ScoProgEntregaItemAutorizacaoFornecimento> progEntregaItemAutorizacaoFornecimentos = getScoProgEntregaItemAutorizacaoFornecimentoDAO().listarParcelasAssociadas(iafAfnNumero, iafNumero);
		for (ScoProgEntregaItemAutorizacaoFornecimento scoProgEntregaItemAutorizacaoFornecimento : progEntregaItemAutorizacaoFornecimentos) {
			Integer iafAfnNum = scoProgEntregaItemAutorizacaoFornecimento.getId().getIafAfnNumero();
			Integer iafNum = scoProgEntregaItemAutorizacaoFornecimento.getId().getIafNumero();
			Integer seq = scoProgEntregaItemAutorizacaoFornecimento.getId().getSeq();
			Integer parcela = scoProgEntregaItemAutorizacaoFornecimento.getId().getParcela();
			List<ScoSolicitacaoProgramacaoEntrega> ScoSolicitacaoProgramacaoEntregas = getScoSolicitacaoProgramacaoEntregaDAO().listarSolicitacaoByItemAFId(iafAfnNum, iafNum, seq, parcela);
			for (ScoSolicitacaoProgramacaoEntrega scoSolicitacaoProgramacaoEntrega : ScoSolicitacaoProgramacaoEntregas) {
				getScoSolicitacaoProgramacaoEntregaDAO().remover(scoSolicitacaoProgramacaoEntrega);
			}
		}
		getScoSolicitacaoProgramacaoEntregaDAO().flush();
		getScoProgEntregaItemAutorizacaoFornecimentoDAO().exclusaoParcelasNaoAssinadasItemAF(iafAfnNumero, iafNumero, isIndAssinatura, qtdeEntregue);
		
		
	}
	
	private List<ConsultaItensAFProgramacaoEntregaVO> formatarConsultaItensAFProgramacaoEntregaVO(
			List<ConsultaItensAFProgramacaoEntregaVO> listVO) {

		for (ConsultaItensAFProgramacaoEntregaVO vo : listVO) {
			vo.setSituacao(formatarSituacao(vo));
			vo.setSituacaoDescricao(vo.getIndSituacao().getDescricao());
			vo.setNomeHint(vo.getNome());
			vo.setNome(formatarNome(vo.getNome()));
			vo.setEstocavel(formatarEstocavel(vo.getIsEstocavel()));
			vo.setEstocavelHint(formatarEstocavelHint(vo.getEstocavel()));
			vo.setDescricaoHint(vo.getDescricao());
			vo.setDescricao(formatarDescricao(vo.getDescricao()));
		}
		
		return listVO;
	}
	
	private String formatarDescricao(String descricao) {
		return StringUtils.substring(descricao, 0, 5);
	}
	
	private String formatarSituacao(ConsultaItensAFProgramacaoEntregaVO vo) {
		return StringUtils.substring(vo.getIndSituacao().toString(), 0, 8);
	}
	
	private String formatarNome(String nome) {
		return StringUtils.substring(nome, 0, 32) + "...";
	}
	
	private String formatarEstocavel(Boolean isEstocavel) {
		if (isEstocavel != null){
			return Boolean.TRUE.equals(isEstocavel) ? "E" : "D";
		} 
		// servico
		return "S";
	}

	private String formatarEstocavelHint(String estocavel) {
		if (!"S".equals(estocavel)) {
			String descricaoD = this.getResourceBundleValue("LABEL_ITENS_AF_PROG_ENTREGA_ED_D");
			String descricaoE = this.getResourceBundleValue("LABEL_ITENS_AF_PROG_ENTREGA_ED_E");

			return "E".equals(estocavel) ? descricaoE : descricaoD;
		} else {
			return this.getResourceBundleValue("LABEL_ITENS_AF_PROG_ENTREGA_ED_SER");
		}
	}
		
	private ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}
	
	private ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}
	
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}
	
	private ScoSolicitacaoProgramacaoEntregaDAO getScoSolicitacaoProgramacaoEntregaDAO(){
		return scoSolicitacaoProgramacaoEntregaDAO;
	}
	
	private ScoAutorizacaoFornRN getScoAutorizacaoFornRN(){
		return scoAutorizacaoFornRN;
	}
	
	private ScoItemAutorizacaoFornRN getScoItemAutorizacaoFornRN(){
		return scoItemAutorizacaoFornRN;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
