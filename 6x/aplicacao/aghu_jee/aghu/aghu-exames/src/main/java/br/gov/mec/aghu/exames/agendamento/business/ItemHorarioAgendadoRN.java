package br.gov.mec.aghu.exames.agendamento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * @ORADB: AELK_IHA_RN
 * 
 * @author tfelini
 */

@Stateless
public class ItemHorarioAgendadoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ItemHorarioAgendadoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
	@Inject
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;
	
	@Inject
	private AelHorarioExameDispDAO aelHorarioExameDispDAO;
	
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IExamesBeanFacade examesBeanFacade;

	private static final long serialVersionUID = -7525733953930739778L;

	public enum ItemHorarioAgendadoRNExceptionCode implements BusinessExceptionCode {
		AEL_00749, AEL_00750, AEL_00751, AEL_00752, AEL_00531, AEL_00884, AEL_00748, AEL_00753, AEL_00754, AEL_00755, AEL_00353;
		
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
		
	}
	
	public void inserirItemHorarioAgendado(AelItemHorarioAgendado item, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador) throws BaseException{
		if (item.getHorarioExameDisp()==null 
				&& item.getId()!=null 
				&& item.getId().getHedDthrAgenda()!=null
				&& item.getId().getHedGaeSeqp()!=null
				&& item.getId().getHedGaeUnfSeq()!=null){
			
			AelHorarioExameDisp horarioExameDisp = getAelHorarioExameDispDAO()
				.obterPorId(item.getId().getHedGaeUnfSeq(), item.getId().getHedGaeSeqp(), item.getId().getHedDthrAgenda());
			
			item.setHorarioExameDisp(horarioExameDisp);
		}
		if (item.getItemSolicitacaoExame()==null
				&& item.getId()!=null 
				&& item.getId().getIseSeqp()!=null
				&& item.getId().getIseSoeSeq()!=null){
			
			AelItemSolicitacaoExames itemSolicitacaoExame = getAelItemSolicitacaoExameDAO()
				.obterPorId(item.getId().getIseSoeSeq(), item.getId().getIseSeqp());
			
			item.setItemSolicitacaoExame(itemSolicitacaoExame);			
		}		
		
		this.preInserirItemHorarioAgendado(item);
		this.getAelItemHorarioAgendadoDAO().persistir(item);
		this.posInserirItemHorarioAgendado(item);
		this.enforceItemHorarioAgendado(item, itemSolicitacaoExameOriginal, DominioOperacaoBanco.INS, nomeMicrocomputador);
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB:AELT_IHA_BRI
	 */
	private void preInserirItemHorarioAgendado(AelItemHorarioAgendado itemHorarioAgendado) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.verificarSituacaoExame(itemHorarioAgendado);
		//Atualiza o servidor que está agendando um horário.
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(ItemHorarioAgendadoRNExceptionCode.AEL_00353);
		}
	}
	
	/**
	 * @throws BaseException 
	 * @ORADB:AELT_IHA_ARI
	 */
	private void posInserirItemHorarioAgendado(AelItemHorarioAgendado itemHorarioAgendado) throws BaseException {
		// atualiza sala de coleta da amostra
		this.atualizarSalaColetaAmostra(itemHorarioAgendado);
	}
	
	/**
	 * @ORADB:AELP_ENFORCE_IHA_RULES
	 * @param item
	 * @param operacao
	 * @throws BaseException 
	 */
	private void enforceItemHorarioAgendado(AelItemHorarioAgendado item, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, DominioOperacaoBanco operacao, String nomeMicrocomputador) throws BaseException {
		if (DominioOperacaoBanco.INS.equals(operacao)){
			// Atualiza situação na item solicitação exame. -- Situação qualquer >>>>> Agendado
			 this.atualizarItemSolicitacaoExame(item.getItemSolicitacaoExame(), itemSolicitacaoExameOriginal, nomeMicrocomputador);
		     //Atualiza situação na horários disponíveis. -- Situação qualquer >>>>>> Marcado
			 //Adaptado para permitir a inserção de um grupo de exames no mesmo horário
			 if(item.getHorarioExameDisp()==null) {
				 ItemHorarioAgendadoRNExceptionCode.AEL_00531.throwException();
			 }
			 else {
				 if(!item.getHorarioExameDisp().getSituacaoHorario().equals(DominioSituacaoHorario.M)) {
					 this.atualizarHorariosDisponiveis(item);
				 }
			 }
		}
		//else if (DominioOperacaoBanco.DEL.equals(operacao)){
			//TODO:Implementar enforce para deleção.
			/* -- RN_IHA004 Atualiza situação na horários disponíveis. --  Marcado >>>>> Liberado
		     	aelk_iha_rn.rn_ihap_atu_hor_mar(l_iha_saved_row.hed_gae_unf_seq,l_iha_saved_row.hed_gae_seqp,l_iha_saved_row.hed_dthr_agenda,l_iha_saved_row.ise_soe_seq, l_iha_saved_row.ise_seqp);*/		
		//}
	}
	
	/**
	 * @ORADB:AELK_IHA_RN.RN_IHAP_ATU_HOR_DISP
	 * Atualizar na tabela de horários disponíveis a situação para Marcado, somente para horário exame disponível com situação L liberado..
	 * @param horarioExameDisp
	 * @throws BaseException 
	 */
	void atualizarHorariosDisponiveis(AelItemHorarioAgendado item) throws BaseException{
		AelHorarioExameDisp horarioExameDisp = item.getHorarioExameDisp();
		if (horarioExameDisp == null || !horarioExameDisp.getSituacaoHorario().equals(DominioSituacaoHorario.L)){
			ItemHorarioAgendadoRNExceptionCode.AEL_00531.throwException();
		}else {
			horarioExameDisp.setSituacaoHorario(DominioSituacaoHorario.M);
			horarioExameDisp.getItemHorarioAgendados().add(item);
			getExamesFacade().atualizarHorarioExameDisp(horarioExameDisp);
		}
	}
	
	/**
	 * @ORADB:AELK_IHA_RN.RN_IHAP_ATU_ITEM
	 * Quando situação do item solicitação exame não estiver como Agendado, 
	 * atualizar na item solicitação exame a situação para Agendado.
	 * 
	 * @param itemSolicitacaoExame
	 * @throws BaseException 
	 */
	private void atualizarItemSolicitacaoExame(AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador) throws BaseException{
		
		AghParametros pSituacaoAgendado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_AGENDADO);
		AelSitItemSolicitacoes situacao = getAelSitItemSolicitacoesDAO().obterPeloId(pSituacaoAgendado.getVlrTexto());

		if (!itemSolicitacaoExame.getSituacaoItemSolicitacao().equals(situacao)){
			
			//AelItemSolicitacaoExames itemSolicitacaoExameOriginal = this.getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());
			
			itemSolicitacaoExame.setSituacaoItemSolicitacao(situacao);
			if (itemSolicitacaoExameOriginal != null){
				getExamesBeanFacade().atualizarItemSolicitacaoExame(itemSolicitacaoExame, itemSolicitacaoExameOriginal, nomeMicrocomputador);
			} else {
				getExamesBeanFacade().atualizarItemSolicitacaoExame(itemSolicitacaoExame, nomeMicrocomputador);
			}
		}
	}
	
	/**
	 * @ORADB:AELK_IHA_RN.RN_IHAP_VER_SIT_EXAM
	 * Esta regra verifica se o horário do exame está livre ou, se estiver marcado, 
	 * o grupo da grade permite agendar no horário já agendado e o exame já marcado 
	 * deve pertencer ao mesmo paciente. 
	 * 
	 * @param itemHorarioAgendado
	 * @throws ApplicationBusinessException 
	 */
	private void verificarSituacaoExame(AelItemHorarioAgendado itemHorarioAgendado) throws ApplicationBusinessException{
		if (itemHorarioAgendado.getHorarioExameDisp()==null){
			ItemHorarioAgendadoRNExceptionCode.AEL_00749.throwException(); //Horário escolhido para agendamento não encontrado
		}else{
			DominioSituacaoHorario situacaoHorario = itemHorarioAgendado.getHorarioExameDisp().getSituacaoHorario();
			
			if (situacaoHorario.equals(DominioSituacaoHorario.B) || situacaoHorario.equals(DominioSituacaoHorario.G)
					||situacaoHorario.equals(DominioSituacaoHorario.E)){
				ItemHorarioAgendadoRNExceptionCode.AEL_00750.throwException();//Horários na situação Gerado, Liberado ou Executados não podem ser agendados.
			}
			
			if (situacaoHorario.equals(DominioSituacaoHorario.M)){
				
				if (itemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame()==null){
					ItemHorarioAgendadoRNExceptionCode.AEL_00751.throwException();//Grade escolhida para agendamento não encontrada.	
				}
				
				if (itemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame().getGrupoExame()==null 
						|| !itemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame().getGrupoExame().getAgendaExMesmoHor()){
					ItemHorarioAgendadoRNExceptionCode.AEL_00752.throwException();//Grade não permite agendamento de exames no mesmo horário
				}
				Integer pacCodigo = null;

				if(itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento()!=null) {
					pacCodigo = itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getPaciente().getCodigo();
				}
				else {
					if(itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso()!=null) {
						if(itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente() != null){
							pacCodigo =  itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente().getCodigo();
						}
					}
				}
				//VAelGradeHrAgenda gradeHrAgenda = getVAelGradeHrAgendaDAO().pesquisarGradeHorarioAgendaMarcada(itemHorarioAgendado);
				if (pacCodigo==null){
					ItemHorarioAgendadoRNExceptionCode.AEL_00753.throwException();//Não encontrado paciente e solicitação do horário já agendado
					
				} else if (itemHorarioAgendado.getItemSolicitacaoExame()==null 
						|| itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame() == null 
						|| itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento()==null 
						|| itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getPaciente() ==null){
					ItemHorarioAgendadoRNExceptionCode.AEL_00754.throwException();//Não encontrado paciente da solicitação sendo agendada
				} else if (!itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getPaciente()
						.getCodigo().equals(pacCodigo)) {
					ItemHorarioAgendadoRNExceptionCode.AEL_00755.throwException();//O horário já agendado para outro paciente
				}
			}
		}
	}
	
	/**
	 * @ORADB:AELK_IHA_RN.RN_IHAP_ATU_SALA_AMO
	 * Atualiza sala de coleta da amostra quando item é agendado.
	 * 
	 * @param itemHorarioAgendado
	 * @throws BaseException 
	 */
	private void atualizarSalaColetaAmostra(AelItemHorarioAgendado itemHorarioAgendado) throws BaseException{
		if (itemHorarioAgendado!=null 
				&& itemHorarioAgendado.getHorarioExameDisp()!=null 
				&& itemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame()!=null ){
			
			AelSalasExecutorasExames salaExecutora = itemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame().getSalaExecutoraExames();
			List<AelAmostras> listaAmostras = getAelAmostrasDAO().buscaListaAmostrasPorItemExame(itemHorarioAgendado.getItemSolicitacaoExame());
	
			for (AelAmostras amostra : listaAmostras){
				amostra.setSalasExecutorasExames(salaExecutora);
				getExamesFacade().atualizarAmostra(amostra, Boolean.FALSE);
			}
		}
	}
	
	/**
	 * @ORADB:AELK_IHA_RN.RN_IHAP_VER_TEMPO
	 * Esta procedure implementa verificar se o horário escolhido para o agendamento 
	 * do exame comporta o tempo necessário para execução do exame. Se não comportar, 
	 * busca os próximos horários para reservar para este exame. Se houver algum
	 * problema, dá erro.
	 * 
	 * OBS: Se for horário extra ou o indicador de cálculo no grupo = 'N', então 
	 * não será necessário o uso de mais horários.
	 * 
	 * @param itemHorarioAgendado Agendamento de horario para um exame.
	 * @param seqp	Seq da solicitação do exame.
	 * @param permiteHoraExtra	Indica se agendamento pode ser realizado em horário extra.
	 * @param agendaExameMesmoHorario	Indica se deve utilizar o mesmo horário no agendamento em grupo.
	 * @param agendamentoSingular	Indica se agendamento é em grupo ou singular (um exame apenas).
	 * @param unfExecutoraExame	Unidade funcional executora do exame.
	 * @throws BaseException 
	 */
	public void verificarHorarioEscolhido(AelItemHorarioAgendado itemHorarioAgendado, Short seqp, Boolean permiteHoraExtra, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, AelUnfExecutaExames unfExecutoraExame, String nomeMicrocomputador) throws BaseException {
		
		List<AelHorarioExameDisp> listaHorariosMesmoDia = getAelHorarioExameDispDAO()
				.buscarOutrosHorariosDisponiveisNoDia(itemHorarioAgendado.getHorarioExameDisp());
		
		// Só deve considerar horários do exame caso não seja horário extra e
		// se agendamento for para o mesmo horario ou é agendamento singular (não é em grupo)
		if (!permiteHoraExtra && (agendaExameMesmoHorario || agendamentoSingular)) {
			
			if (unfExecutoraExame==null){
				ItemHorarioAgendadoRNExceptionCode.AEL_00884.throwException();
			}else if (unfExecutoraExame.getTempoMedioOcupSala()!=null//Se o exame não possui tempo de duração para agenda, não valida
						&& itemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame().getGrupoExame()!=null 
						&& itemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame().getGrupoExame().getCalculaTempo()){//Se ind_calcula_tempo do grupo = 'N', entao nao valida.

				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				if (listaHorariosMesmoDia == null || listaHorariosMesmoDia.size()==0){
					ItemHorarioAgendadoRNExceptionCode.AEL_00748.throwException(); //Horário escolhido para agendar o exame não foi encontrado
				}
				
				Boolean primeiro = Boolean.TRUE;
				Date dataBase = new Date();
				
				for (AelHorarioExameDisp horarioExameDisp : listaHorariosMesmoDia){
					
					if (primeiro){//Primeiro registro: onde o exame foi agendado
						primeiro = Boolean.FALSE;
						
						Calendar tempoMedioOcupacaoSala = Calendar.getInstance();
						tempoMedioOcupacaoSala.setTime(unfExecutoraExame.getTempoMedioOcupSala());
						
						Calendar dataAgenda = Calendar.getInstance();
						dataAgenda.setTime(horarioExameDisp.getId().getDthrAgenda());
						
						dataAgenda.add(Calendar.HOUR_OF_DAY, tempoMedioOcupacaoSala.get(Calendar.HOUR_OF_DAY));
						dataAgenda.add(Calendar.MINUTE, tempoMedioOcupacaoSala.get(Calendar.MINUTE));
						
						dataBase = dataAgenda.getTime(); //Armazena a data hora em que deve encerrar o exame
						
					}else{
						if (DateValidator.validaDataMenor(horarioExameDisp.getId().getDthrAgenda(), dataBase)){
							//Agenda o exame
							AelItemHorarioAgendadoId novoItemHorarioAgendadoId = new AelItemHorarioAgendadoId();
							novoItemHorarioAgendadoId.setHedDthrAgenda(horarioExameDisp.getId().getDthrAgenda());
							novoItemHorarioAgendadoId.setHedGaeSeqp(horarioExameDisp.getId().getGaeSeqp());
							novoItemHorarioAgendadoId.setHedGaeUnfSeq(horarioExameDisp.getId().getGaeUnfSeq());
							novoItemHorarioAgendadoId.setIseSeqp(seqp);
							novoItemHorarioAgendadoId.setIseSoeSeq(itemHorarioAgendado.getId().getIseSoeSeq());
							
							AelItemHorarioAgendado novoItemHorarioAgendado = new AelItemHorarioAgendado();
							novoItemHorarioAgendado.setId(novoItemHorarioAgendadoId);
							novoItemHorarioAgendado.setServidor(servidorLogado);
							
							inserirItemHorarioAgendado(novoItemHorarioAgendado, null, nomeMicrocomputador);
						}else{
							break;
						}
					}
				}
			} 
		}
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO(){
		return aelItemHorarioAgendadoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected AelAmostrasDAO getAelAmostrasDAO(){
		return aelAmostrasDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO(){
		return aelSitItemSolicitacoesDAO;
	}
	
	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO(){
		return aelHorarioExameDispDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO(){
		return aelItemSolicitacaoExameDAO;
	}
	
	protected IExamesBeanFacade getExamesBeanFacade() {
		return this.examesBeanFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
