package br.gov.mec.aghu.exames.business;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelMatrizSituacaoDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ItemSolicitacaoExameRN.ItemSolicitacaoExameRNExceptionCode;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioExameDispId;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelMatrizSituacao;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class AelItemHorarioAgendadoRN extends BaseBusiness {


@EJB
private AelHorarioExameDispRN aelHorarioExameDispRN;

@EJB
private AelAmostrasRN aelAmostrasRN;

@EJB
private IExamesFacade examesFacade;

@Inject
private AelMatrizSituacaoDAO aelMatrizSituacaoDAO;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

private static final Log LOG = LogFactory.getLog(AelItemHorarioAgendadoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@EJB
private IAgendamentoExamesFacade agendamentoExamesFacade;

@Inject
private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;


public enum AelItemHorarioAgendadoRNExceptionCode implements BusinessExceptionCode {
	AEL_01690, AEL_00728, AEL_00477;
}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5420532802380508266L;

	/**
	 * Remover objeto AelItemHorarioAgendado SEM FLUSH
	 * @param {AelItemHorarioAgendado} amostra
	 * @return {AelItemHorarioAgendado}
	 * @throws BaseException
	 */
	public void remover(AelItemHorarioAgendado itemHorarioAgendado, Boolean flush, String nomeMicrocomputador, AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws BaseException {
		
		this.beforeDeleteAelItemHorarioAgendado(itemSolicitacaoExame, itemSolicitacaoExameOriginal, nomeMicrocomputador);
		
		this.getAelItemHorarioAgendadoDAO().remover(itemHorarioAgendado);
		
		if (flush){
			this.getAelItemHorarioAgendadoDAO().flush();
		}
		
		this.afterDeleteAelItemHorarioAgendado(itemHorarioAgendado);
		
		this.enforceDeleteAelItemHorarioAgendado(itemHorarioAgendado);
		
	}
	
	/**
	 * ORADB AELT_IHA_BRD
	 * @param {AelItemHorarioAgendado} itemHorarioAgendado
	 * @throws BaseException
	 */
	protected void beforeDeleteAelItemHorarioAgendado( AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador) throws BaseException {
		
		this.atualizarItemAgendamento(itemSolicitacaoExame, itemSolicitacaoExameOriginal, nomeMicrocomputador);
		
	}
	
	/**
	 * ORADB AELT_IHA_ARD
	 * @param {AelItemHorarioAgendado} itemHorarioAgendado
	 * @throws BaseException
	 */
	protected void afterDeleteAelItemHorarioAgendado(
			AelItemHorarioAgendado itemHorarioAgendado) throws BaseException {
		
		this.atualizarSalaAmostra(itemHorarioAgendado, DominioOperacaoBanco.DEL);
		
	}
	
	/**
	 * ORADB AELP_ENFORCE_IHA_RULES - EVENTO DE DELETE
	 * @param {AelItemHorarioAgendado} itemHorarioAgendado
	 * @throws BaseException
	 */
	protected void enforceDeleteAelItemHorarioAgendado(
			AelItemHorarioAgendado itemHorarioAgendado) throws BaseException {
		
		this.atualizarHoraMarcada(itemHorarioAgendado);
		
	}

	/**
	 * ORADB AELK_IHA_RN.RN_IHAP_ATU_ITEM_AG
	 * 
	 * Atualiza situação na item solicitação exame.
	 * 
	 * @param {AelItemHorarioAgendado} itemHorarioAgendado
	 * @throws BaseException 
	 */
	protected void atualizarItemAgendamento(AelItemSolicitacaoExames itemSolicitacaoExames, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador) throws BaseException {
		
			String sitCodigo = itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo();
			
			if (DominioSituacaoItemSolicitacaoExame.AG.toString().equals(sitCodigo)) {
				
				Boolean coletavel = itemSolicitacaoExames.getMaterialAnalise().getIndColetavel();
				
				if (coletavel) {
					AelSitItemSolicitacoes sitItemSolicitacoes = this.getAelSitItemSolicitacoesDAO().obterPeloId(DominioSituacaoItemSolicitacaoExame.AC.toString());
					itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);
					AelMatrizSituacao matrizSituacao = verificarPermissaoMatrizTrans(itemSolicitacaoExameOriginal, itemSolicitacaoExames);
						if (usuarioTemPermissao(itemSolicitacaoExames, itemSolicitacaoExameOriginal, matrizSituacao)) {
							this.getSolicitacaoExameFacade().atualizar(itemSolicitacaoExames, itemSolicitacaoExameOriginal, nomeMicrocomputador);
						} else {
							itemSolicitacaoExames.setSituacaoItemSolicitacao(this.getAelSitItemSolicitacoesDAO().obterPeloId(DominioSituacaoItemSolicitacaoExame.AG.toString()));
							usuarioSemPermissao(matrizSituacao);
						}
				} else {
					AelSitItemSolicitacoes sitItemSolicitacoes = this.getAelSitItemSolicitacoesDAO().obterPeloId(DominioSituacaoItemSolicitacaoExame.AX.toString());
					itemSolicitacaoExames.setSituacaoItemSolicitacao(sitItemSolicitacoes);
					AelMatrizSituacao matrizSituacao = verificarPermissaoMatrizTrans(itemSolicitacaoExameOriginal, itemSolicitacaoExames);
						if (usuarioTemPermissao(itemSolicitacaoExames, itemSolicitacaoExameOriginal, matrizSituacao)) {
							this.getSolicitacaoExameFacade().atualizar(itemSolicitacaoExames, itemSolicitacaoExameOriginal, nomeMicrocomputador);
						} else {
							itemSolicitacaoExames.setSituacaoItemSolicitacao(this.getAelSitItemSolicitacoesDAO().obterPeloId(DominioSituacaoItemSolicitacaoExame.AG.toString()));
							usuarioSemPermissao(matrizSituacao);
						}
				}
			}
	}

	private void usuarioSemPermissao(AelMatrizSituacao matrizSituacao)	throws ApplicationBusinessException {
		
		String situacaoDe = "";
		String situacaoPara = "";

		if (matrizSituacao.getSituacaoItemSolicitacao() != null) {
			situacaoDe = matrizSituacao.getSituacaoItemSolicitacao().getDescricao();
		}

		if (matrizSituacao.getSituacaoItemSolicitacaoPara() != null) {
			situacaoPara = matrizSituacao.getSituacaoItemSolicitacaoPara().getDescricao();
		}
		throw new ApplicationBusinessException(AelItemHorarioAgendadoRNExceptionCode.AEL_00728, situacaoDe, situacaoPara);
	}

	private Boolean usuarioTemPermissao(AelItemSolicitacaoExames itemSolicitacaoExames,AelItemSolicitacaoExames itemSolicitacaoExameOriginal, AelMatrizSituacao matrizSituacao) throws ApplicationBusinessException {
		boolean temPermissao = false;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		temPermissao = getExamesFacade().validarPermissaoAlterarExameSituacao(matrizSituacao, servidorLogado);
		
		return temPermissao;
	}
	
	protected AelMatrizSituacao verificarPermissaoMatrizTrans(AelItemSolicitacaoExames itemOriginal, AelItemSolicitacaoExames item)	throws ApplicationBusinessException {
		AelSitItemSolicitacoes sitCodigoOriginal = null;
		boolean transicoesOk = false;
		
		if (item.getId() != null) {
			if (itemOriginal != null) {
				sitCodigoOriginal = itemOriginal.getSituacaoItemSolicitacao();
			}
	
			List<AelMatrizSituacao> lista = getAelMatrizSituacaoDAO().pesquisarMatrizSituacaoPorSituacaoOrigem(sitCodigoOriginal);
			AelMatrizSituacao matrizSituacao = null;
			for (AelMatrizSituacao matrizSit : lista) {
				if (item.getSituacaoItemSolicitacao().equals(matrizSit.getSituacaoItemSolicitacaoPara())) {
					matrizSituacao = matrizSit;
					transicoesOk = true;
					break;
				}
			}
			if (!transicoesOk) {
				throw new ApplicationBusinessException(
						AelItemHorarioAgendadoRNExceptionCode.AEL_00477);
			}
			return matrizSituacao;
			
		} else {
			throw new ApplicationBusinessException(ItemSolicitacaoExameRNExceptionCode.AEL_00477);
		}
		
	}
	
	/**
	 * ORADB AELK_IHA_RN.RN_IHAP_ATU_SALA_AMO
	 * 
	 * Atualiza sala de coleta da amostra.
	 * 
	 * @param {AelItemHorarioAgendado} itemHorarioAgendado
	 * @throws BaseException 
	 */
	protected void atualizarSalaAmostra(
			AelItemHorarioAgendado itemHorarioAgendado,
			DominioOperacaoBanco operacao) throws BaseException {

		AelSalasExecutorasExames salaExecutoraExame = null;
		
		if (DominioOperacaoBanco.UPD.equals(operacao) || DominioOperacaoBanco.INS.equals(operacao)) {

			AelGradeAgendaExame gradeAgendaExame = itemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame();
			salaExecutoraExame = gradeAgendaExame.getSalaExecutoraExames();

		}	
		try{
			Set<AelAmostras> listAmostra = itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAelAmostras();
			for (AelAmostras amostra : listAmostra) {
	
				String amostraSeqp = amostra.getId().getSeqp().toString();
				List<AelAmostraItemExames> listAmostraItemExames = itemHorarioAgendado.getItemSolicitacaoExame().getAelAmostraItemExames();
				for (AelAmostraItemExames amostraItemExames : listAmostraItemExames) {
	
					String amostraItemExamesSeqp = amostraItemExames.getId().getAmoSeqp().toString();
					if (amostraSeqp.equals(amostraItemExamesSeqp) && 
							(salaExecutoraExame!=null || amostra.getSalasExecutorasExames()!=null) &&
							(salaExecutoraExame!=null && !salaExecutoraExame.equals(amostra.getSalasExecutorasExames())) &&
							(amostra.getSalasExecutorasExames()!=null && !amostra.getSalasExecutorasExames().equals(salaExecutoraExame))) {
						amostra.setSalasExecutorasExames(salaExecutoraExame);
						this.getAelAmostrasRN().atualizarAelAmostra(amostra, false);
						break;
					}	
				}	
			}
		}catch(Exception e){
			throw new ApplicationBusinessException(AelItemHorarioAgendadoRNExceptionCode.AEL_01690);
		}

	}
	
	/**
	 * ORADB AELK_IHA_RN.RN_IHAP_ATU_HOR_MAR
	 * 
	 * Atualizar na tabela de horários disponíveis a situação de Marcado para
     * Liberado, caso não haja outros exames para este horário.
	 * 
	 * @param {AelItemHorarioAgendado} itemHorarioAgendado
	 * @throws BaseException 
	 */
	protected void atualizarHoraMarcada(AelItemHorarioAgendado itemHorarioAgendado) throws BaseException {
		
		List<AelItemHorarioAgendado> listItemHorarioAgendado = this
				.getAelItemHorarioAgendadoDAO()
				.buscarExamesAgendadosNoMesmoHorario(
						itemHorarioAgendado.getHorarioExameDisp().getId().getGaeUnfSeq(),
						itemHorarioAgendado.getHorarioExameDisp().getId().getGaeSeqp(),
						itemHorarioAgendado.getHorarioExameDisp().getId().getDthrAgenda(),
						itemHorarioAgendado.getItemSolicitacaoExame());
		
		if (listItemHorarioAgendado.isEmpty()) {
			
			AelHorarioExameDisp horarioExameDisp = itemHorarioAgendado.getHorarioExameDisp();
			
			if (horarioExameDisp.getIndHorarioExtra()) {
				
				horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.B);
				
			} else {
				
				horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.L);
				
			}
			
			this.getAelHorarioExameDispRN().atualizarSemFlush(horarioExameDisp);
			
		}
		
	}	
	
	/**
	 * ORADB: AELK_IHA_RN.RN_IHAP_ATU_CANC_ISE
	 * 
	 * Deleta os horários agendados para o itemSolicitacaoExame informado.
	 * 
	 * @param {AelItemSolicitacaoExames} itemSolicitacaoExame
	 * @throws BaseException
	 */
	public void cancelarHorariosPorItemSolicitacaoExame(AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador) throws BaseException {
		List<AelItemHorarioAgendado> listaItemHorarioAgendado = getAelItemHorarioAgendadoDAO()
				.buscarPorItemSolicitacaoExame(itemSolicitacaoExame);
		
		for (AelItemHorarioAgendado itemHorarioAgendado : listaItemHorarioAgendado) {
			Set<AelItemHorarioAgendado> setItemHorarioAgendado = itemHorarioAgendado.getHorarioExameDisp().getItemHorarioAgendados();
			if (!setItemHorarioAgendado.isEmpty()) {
				setItemHorarioAgendado.remove(itemHorarioAgendado);	
			}
			remover(itemHorarioAgendado, Boolean.FALSE, nomeMicrocomputador, itemSolicitacaoExame, itemSolicitacaoExameOriginal);
		}
	}
	
	/**
	 * ORADB: AELP_VER_EXAMES_AGENDADOS
	 * 
	 * @param itemHorarioAgendado
	 */
	public void cancelarHorariosExamesAgendados(AelItemHorarioAgendado itemHorarioAgendado, Short globalUnfSeq, String nomeMicrocomputador) 
			throws BaseException {
		AelHorarioExameDispId horarioExameDispId = itemHorarioAgendado.getHorarioExameDisp().getId();
		AelItemSolicitacaoExames itemSolicitacaoExame = itemHorarioAgendado.getItemSolicitacaoExame();
		AelItemSolicitacaoExamesId itemSolicitacaoExameId = itemHorarioAgendado.getItemSolicitacaoExame().getId();		

		DominioSimNaoRestritoAreaExecutora simNaoRestritoAreaExecutora = null;
		if (globalUnfSeq == null) {
			simNaoRestritoAreaExecutora = DominioSimNaoRestritoAreaExecutora.S;
		} else {
			simNaoRestritoAreaExecutora = DominioSimNaoRestritoAreaExecutora.R;
		}
		
		AghParametros paramSituacaoAgendado = getParametroFacade().obterAghParametro(
				AghuParametrosEnum.P_SITUACAO_AGENDADO);
		
		// Busca horarios contendo o mesmo iseSoeSeq, mas com 
		// iseSeqp diferente ao do itemSolicitacaoExame passado por argumento
		List<AelItemHorarioAgendado> listaItemHorarioAgendado = getAelItemHorarioAgendadoDAO()
				.pesquisarItemHorarioAgendadoPorGradeEItemSolicitacaoExame(
						horarioExameDispId.getGaeUnfSeq(),
						horarioExameDispId.getGaeSeqp(), 
						itemSolicitacaoExameId.getSoeSeq(), 
						itemSolicitacaoExameId.getSeqp(), 
						itemSolicitacaoExame.getExame().getSigla(),
						itemSolicitacaoExame.getMaterialAnalise().getSeq(),
						itemSolicitacaoExame.getUnidadeFuncional().getSeq(),
						paramSituacaoAgendado.getVlrTexto(), simNaoRestritoAreaExecutora);
		
		for (AelItemHorarioAgendado itemHoraAgendado : listaItemHorarioAgendado) {
			
			AelItemSolicitacaoExames itemSolicitacaoExameOriginal = agendamentoExamesFacade.obterItemSolicitacaoExameOriginal(itemHoraAgendado.getItemSolicitacaoExame().getId().getSoeSeq(), itemHoraAgendado.getItemSolicitacaoExame().getId().getSeqp());
			
			cancelarHorariosPorItemSolicitacaoExame(itemHoraAgendado.getItemSolicitacaoExame(), itemSolicitacaoExameOriginal, nomeMicrocomputador);
		}
	}

	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO(){
		return aelItemHorarioAgendadoDAO;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO(){
		return aelSitItemSolicitacoesDAO;
	}
	
	protected AelAmostrasRN getAelAmostrasRN(){
		return aelAmostrasRN;
	}	

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AelHorarioExameDispRN getAelHorarioExameDispRN() {
		return aelHorarioExameDispRN;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected AelMatrizSituacaoDAO getAelMatrizSituacaoDAO() {
		return aelMatrizSituacaoDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

}
