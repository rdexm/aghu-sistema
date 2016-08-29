package br.gov.mec.aghu.exames.agendamento.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Period;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.GradeHorarioExtraVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.agendamento.vo.VAelHrGradeDispVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExameUnidExameDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoExamesDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioExameDispDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.dao.VAelGradeHrAgendaDAO;
import br.gov.mec.aghu.exames.dao.VAelHrGradeDispDAO;
import br.gov.mec.aghu.exames.dao.VAelHrGradeDispTrDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicPacDAO;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioExameDispId;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelGradeHrAgenda;
import br.gov.mec.aghu.model.VAelSolicPac;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class ItemHorarioAgendadoON extends BaseBusiness {

	@EJB
	private ConsultaHorarioLivreON consultaHorarioLivreON;
	
	@EJB
	private ConsultaHorarioLivreRN consultaHorarioLivreRN;
	
	@EJB
	private HorarioExameDispON horarioExameDispON;
	
	@EJB
	private ItemHorarioAgendadoRN itemHorarioAgendadoRN;
	
	private static final Log LOG = LogFactory.getLog(ItemHorarioAgendadoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelGradeAgendaExameDAO aelGradeAgendaExameDAO;
	
	@Inject
	private AelGrupoExameUnidExameDAO aelGrupoExameUnidExameDAO;
	
	@Inject
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private VAelHrGradeDispDAO vAelHrGradeDispDAO;
	
	@Inject
	private AelGrupoExamesDAO aelGrupoExamesDAO;
	
	@Inject
	private VAelGradeHrAgendaDAO vAelGradeHrAgendaDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelHorarioExameDispDAO aelHorarioExameDispDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private VAelHrGradeDispTrDAO vAelHrGradeDispTrDAO;
	
	@Inject
	private VAelSolicPacDAO vAelSolicPacDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7290423253156956360L;
	
	public enum ItemHorarioAgendadoONExceptionCode implements BusinessExceptionCode {
		AEL_00965, AEL_00730, AEL_01002, AEL_01439, SELECAO_EXAME_JA_AGENDADO, 
		MENSAGEM_ERRO_SELECIONAR_EXAME_JA_AGENDADO_MESMA_GRADE_UNIDADE;
	}
	
	public void inserir(AelItemHorarioAgendado itemHorarioAgendado, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, Boolean permiteHoraExtra, Boolean agendaExameMesmoHorario, 
			Boolean agendamentoSingular, String sigla, Integer materialAnalise, Short unfExecutora, String nomeMicrocomputador) throws BaseException {
//		try {
			this.eventoPreInserir(itemHorarioAgendado);
			this.getItemHorarioAgendadoRN().inserirItemHorarioAgendado(itemHorarioAgendado, itemSolicitacaoExameOriginal, nomeMicrocomputador);
			this.eventoPosInserir(itemHorarioAgendado, permiteHoraExtra, agendaExameMesmoHorario, agendamentoSingular, sigla, 
					materialAnalise, unfExecutora, nomeMicrocomputador);			
//		} catch (BaseException e) {
//			// Exceção com rollback precisa ser lançada devido ao uso de flush ao atualizar o itemSolicitacaoExame na enforce do
//			// método inserirItemHorarioAgendado e para o caso de ter lançado alguma ApplicationBusinessException após esse flush.
//			if (e instanceof ApplicationBusinessException) {
//				throw new ApplicationBusinessException(e);
//			} else {
//				throw e;
//			}
//		}
	}
	
	public void eventoPreInserir(AelItemHorarioAgendado itemHorarioAgendado) throws ApplicationBusinessException{
		if (itemHorarioAgendado.getItemSolicitacaoExame()!=null){
			List<VAelSolicPac> listaVAelSolicPac =  getVAelSolicPacDAO().buscarVAelSolicPacPorSeqSolicitacao
				(itemHorarioAgendado.getItemSolicitacaoExame().getSolicitacaoExame().getSeq());
			if (listaVAelSolicPac == null || listaVAelSolicPac.isEmpty()){
				throw new ApplicationBusinessException(ItemHorarioAgendadoONExceptionCode.AEL_00965);
			}
		}
		
		AelHorarioExameDispId horarioExameDispId = new AelHorarioExameDispId(
				itemHorarioAgendado.getId().getHedGaeUnfSeq(),
				itemHorarioAgendado.getId().getHedGaeSeqp(),
				itemHorarioAgendado.getId().getHedDthrAgenda());
		AelHorarioExameDisp horarioExameDisp = getAelHorarioExameDispDAO()
				.obterPorId(horarioExameDispId.getGaeUnfSeq(),
						horarioExameDispId.getGaeSeqp(),
						horarioExameDispId.getDthrAgenda());
				
		if (horarioExameDisp != null) {
			Long countHorarios = getVAelHrGradeDispTrDAO()
					.listarGradeDispPorHorarioExameDisp(
							horarioExameDispId.getDthrAgenda(),
							horarioExameDispId.getGaeSeqp(),
							horarioExameDispId.getGaeUnfSeq());
			if (countHorarios == 0) {
				throw new ApplicationBusinessException(
						ItemHorarioAgendadoONExceptionCode.AEL_00965);
			}
			
			List<VAelGradeHrAgenda> listaGradeHrAgenda = getVAelGradeHrAgendaDAO()
					.pesquisarGradeHorarioAgendaPorItemHorarioAgendado(
							itemHorarioAgendado);
			if (listaGradeHrAgenda == null || listaGradeHrAgenda.isEmpty()) {
				throw new ApplicationBusinessException(
						ItemHorarioAgendadoONExceptionCode.AEL_00730);
			}
		}
	}
	
	public void eventoPosInserir(AelItemHorarioAgendado itemHorarioAgendado, Boolean permiteHoraExtra, Boolean agendaExameMesmoHorario, 
			Boolean agendamentoSingular, String sigla, Integer materialAnalise, Short unfSeq, String nomeMicrocomputador) throws BaseException {
		AelUnfExecutaExames unfExecutoraExame = getAelUnfExecutaExamesDAO().obterAelUnfExecutaExames(sigla, materialAnalise, unfSeq);
		
		// Após inserir o horário, deve verificar se o exame ocupará mais de um horário e agendá-lo para este 
		// exame, caso seja necessário. Só deve inserir em mais de um horário caso o horário escolhido não seja EXTRA.
		getItemHorarioAgendadoRN().verificarHorarioEscolhido(itemHorarioAgendado, 
				itemHorarioAgendado.getItemSolicitacaoExame().getId().getSeqp(), permiteHoraExtra, 
				agendaExameMesmoHorario, agendamentoSingular, unfExecutoraExame, nomeMicrocomputador);
	}
	
	public void cancelarItemHorarioAgendadoMarcadoPorSelecaoExames(List<AgendamentoExameVO> examesAgendamentoSelecao, 
			Short globalUnfSeq, String nomeMicrocomputador) throws BaseException {
		
		for (AgendamentoExameVO agendamentoExameVO : examesAgendamentoSelecao) {
			if (agendamentoExameVO.getSelecionado()) {
				cancelarItemHorarioAgendadoMarcadoPorItemSolicitacaoExame(
						agendamentoExameVO.getItemExame(), agendamentoExameVO.getItemExameOriginal(), agendamentoExameVO.getDthrAgenda(), globalUnfSeq, nomeMicrocomputador);
			}
		}
	}
	
	public void cancelarItemHorarioAgendadoMarcado(AelItemHorarioAgendadoId itemHorarioAgendadoId, Short globalUnfSeq, String nomeMicrocomputador)	
			throws BaseException {
		
		AelItemHorarioAgendado itemHorarioAgendado = getAelItemHorarioAgendadoDAO()
				.obterPorChavePrimaria(itemHorarioAgendadoId);

		AelItemSolicitacaoExames itemSolicitacaoExame = itemHorarioAgendado.getItemSolicitacaoExame();
		AelItemSolicitacaoExames itemSolicitacaoExameOriginal = agendamentoExamesFacade.obterItemSolicitacaoExameOriginal(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());
		cancelarItemHorarioAgendadoMarcadoPorItemSolicitacaoExame(itemSolicitacaoExame, itemSolicitacaoExameOriginal,
				itemHorarioAgendado.getId().getHedDthrAgenda(), globalUnfSeq, nomeMicrocomputador);
	}
	
	private void cancelarItemHorarioAgendadoMarcadoPorItemSolicitacaoExame(AelItemSolicitacaoExames itemSolicitacaoExame, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, 
			Date dthrAgenda, Short globalUnfSeq, String nomeMicrocomputador) throws BaseException {
		
		// Verifica dthrAgenda (caso seja null, indica que o itemSolicitacaoExame 
		// não possui horário agendado para cancelamento).
		if (dthrAgenda == null) {
			throw new ApplicationBusinessException(ItemHorarioAgendadoONExceptionCode.AEL_01002);
		}
		
		// Chama regra que deleta registros de AelItemHorarioAgendado para o mesmo exame
		getExamesFacade().cancelarHorariosPorItemSolicitacaoExame(itemSolicitacaoExame, itemSolicitacaoExameOriginal, nomeMicrocomputador);
		
		List<AelItemHorarioAgendado> listaItemHorarioAgendado = 
				getAelItemHorarioAgendadoDAO().buscarPorItemSolicitacaoExame(itemSolicitacaoExame);
		
		for (AelItemHorarioAgendado itemHorarioAgendado : listaItemHorarioAgendado) {
			/* Verifica outros exames agendados do mesmo grupo neste mesmo horário
		 	a fim de efetuar o cancelamento dos mesmos */
			getExamesFacade().cancelarHorariosExamesAgendados(itemHorarioAgendado, globalUnfSeq, nomeMicrocomputador);		
		}
	}
	
	/**
	 * Método responsável por identificar quais 
	 * exames deverão ser agendados em grupo.
	 * 
	 * @param grupoExameSeq
	 * @param listaItemHorarioAgendadoVO
	 */
	public void identificarAgendamentoExamesEmGrupo(Integer grupoExameSeq, List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO,
			Boolean horarioExtra) {
		
		// Primeiro desfaz a identificação de exames já identificados em grupo previamente
		desfazerIdentificacaoAgendamentoExamesEmGrupo(listaItemHorarioAgendadoVO);
		
		// Horario selecionado pertence a agendamento em grupo (grupoExameSeq não é nulo e é diferente de zero)
		if (grupoExameSeq != null && grupoExameSeq != 0) {
			ItemHorarioAgendadoVO selecaoItemHorarioAgendadoVO = null;
			selecaoItemHorarioAgendadoVO = obterExameSelecionado(listaItemHorarioAgendadoVO);
			
			// Caso selecaoItemHorarioAgendadoVO (exame selecionado) não tenha agendamento
			if (selecaoItemHorarioAgendadoVO != null && selecaoItemHorarioAgendadoVO.getItemHorarioAgendadoId() == null) {
				// Busca os exames que pertencem ao mesmo grupo
				List<AelGrupoExameUnidExame> listaGrupoExameUnidExame =
						getAelGrupoExameUnidExameDAO().pesquisarGrupoExamePorListaExamesAgendamentoEGexSeq(
								listaItemHorarioAgendadoVO, grupoExameSeq);
				
				AelGrupoExames grupoExame = listaGrupoExameUnidExame.get(0).getGrupoExame();
				
				/*
				 * Identifica quais itemHorarioAgendadoVO deverão ser agendados em grupo
				 */
				for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
					for (AelGrupoExameUnidExame grupoExameUnidExame : listaGrupoExameUnidExame) {
						if (itemHorarioAgendadoVO.getItemHorarioAgendadoId() == null
								&& itemHorarioAgendadoVO.getSigla().equals(grupoExameUnidExame.getId().getUfeEmaExaSigla())
								&& itemHorarioAgendadoVO.getSeqMaterialAnalise().equals(grupoExameUnidExame.getId().getUfeEmaManSeq())
								&& itemHorarioAgendadoVO.getSeqUnidade().equals(grupoExameUnidExame.getId().getUfeUnfSeq())) {
									
							itemHorarioAgendadoVO.setIdentificadoAgendamentoExameEmGrupo(Boolean.TRUE);
						}
					}
				}
				
				if (horarioExtra && !grupoExame.getAgendaExMesmoHor()) {
					desfazerIdentificacaoAgendamentoExamesEmGrupo(listaItemHorarioAgendadoVO);
				}
			}
		}
	}
	
	public Boolean validarAgendamentoExamesEmGrupo(List<AgendamentoExameVO> agendamentosExameVO, Integer grupoExameSeq, Short idSelecionado) {
		List<AgendamentoExameVO> examesAindaNaoAgendados = new ArrayList<AgendamentoExameVO>();
		for (AgendamentoExameVO agendamentoExameVO : agendamentosExameVO) {
			if (DominioSituacaoItemSolicitacaoExame.AC.toString().equals(agendamentoExameVO.getItemExame().getSituacaoItemSolicitacao().getCodigo())
				|| DominioSituacaoItemSolicitacaoExame.AX.toString().equals(agendamentoExameVO.getItemExame().getSituacaoItemSolicitacao().getCodigo())
				|| agendamentoExameVO.getItemExame().getItemHorarioAgendado() == null || agendamentoExameVO.getItemExame().getItemHorarioAgendado().size() == 0) {
				examesAindaNaoAgendados.add(agendamentoExameVO);
			}
		}
		
		
		if (examesAindaNaoAgendados.isEmpty()){
			for (AgendamentoExameVO agendamentoExameVO : agendamentosExameVO) {
				if (agendamentoExameVO.getItemExame().getId().getSeqp().equals(idSelecionado)){
					examesAindaNaoAgendados.add(agendamentoExameVO);
				}
			}
		}
		
		List<ItemHorarioAgendadoVO> listaItemAindaNaoAgendados =
			popularItemHorarioAgendadoParaBuscarGrupoUnidExame(examesAindaNaoAgendados);
		
		List<AelGrupoExameUnidExame> listaGrupoExameUnidExame =
			getAelGrupoExameUnidExameDAO().pesquisarGrupoExamePorListaExamesAgendamentoEGexSeq(
					listaItemAindaNaoAgendados, grupoExameSeq);
		
		int quantidadeAgendamentoEmGrupoSelecionado = 0;
		for (ItemHorarioAgendadoVO itemHorarioAindaNaoAgendadoVO : listaItemAindaNaoAgendados) {
			for (AelGrupoExameUnidExame grupoExameUnidExame : listaGrupoExameUnidExame) {
				if (itemHorarioAindaNaoAgendadoVO.getItemHorarioAgendadoId() == null
						&& itemHorarioAindaNaoAgendadoVO.getSigla().equals(grupoExameUnidExame.getId().getUfeEmaExaSigla())
						&& itemHorarioAindaNaoAgendadoVO.getSeqMaterialAnalise().equals(grupoExameUnidExame.getId().getUfeEmaManSeq())
						&& itemHorarioAindaNaoAgendadoVO.getSeqUnidade().equals(grupoExameUnidExame.getId().getUfeUnfSeq())
						&& itemHorarioAindaNaoAgendadoVO.getSelecionado()) {
					quantidadeAgendamentoEmGrupoSelecionado++;
					break;
				}
			}
		}
		if(quantidadeAgendamentoEmGrupoSelecionado != listaGrupoExameUnidExame.size()) {
			return true;
		}
		
		return false;
	}
	
	private List<ItemHorarioAgendadoVO> popularItemHorarioAgendadoParaBuscarGrupoUnidExame(List<AgendamentoExameVO> examesAindaNaoAgendados) {
		List<ItemHorarioAgendadoVO> listaItemAindaNaoAgendados = new ArrayList<ItemHorarioAgendadoVO>();
		
		for(AgendamentoExameVO agendamentoExameVO: examesAindaNaoAgendados) {
			ItemHorarioAgendadoVO itemHorarioAindaNaoAgendado =  new ItemHorarioAgendadoVO();
			
			itemHorarioAindaNaoAgendado.setSelecionado(agendamentoExameVO.getSelecionado());
			
			if(agendamentoExameVO.getItemExame()!=null) {
				if(agendamentoExameVO.getItemExame().getExame()!=null){
					itemHorarioAindaNaoAgendado.setSigla(agendamentoExameVO.getItemExame().getExame().getSigla());
				}
				if(agendamentoExameVO.getItemExame().getMaterialAnalise()!=null) {
					itemHorarioAindaNaoAgendado.setSeqMaterialAnalise(agendamentoExameVO.getItemExame().getMaterialAnalise().getSeq());
				}
				if(agendamentoExameVO.getItemExame().getUnidadeFuncional()!=null){
					itemHorarioAindaNaoAgendado.setSeqUnidade(agendamentoExameVO.getItemExame().getUnidadeFuncional().getSeq());
				}
			}
			listaItemAindaNaoAgendados.add(itemHorarioAindaNaoAgendado);
		}
		return listaItemAindaNaoAgendados;
	}
	
	public void desfazerIdentificacaoAgendamentoExamesEmGrupo(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO) {
		// Desfaz a identificacao do grupo de exames
		for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
			itemHorarioAgendadoVO.setIdentificadoAgendamentoExameEmGrupo(Boolean.FALSE);
		}
	}
	
	public void gravarHorario(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO,
			VAelHrGradeDispVO vAelHrGradeDispVO, String nomeMicrocomputador) throws BaseException {
		
		if (verificarExistenciaExameAgendaGrupo(listaItemHorarioAgendadoVO)) {
			AelGrupoExames grupoExame = getAelGrupoExamesDAO().obterPorChavePrimaria(vAelHrGradeDispVO.getGrupoExame());
			Boolean agendaExameMesmoHorario = grupoExame.getAgendaExMesmoHor();
			if (listaItemHorarioAgendadoVO.size() == 1) {
				// Agendamento singular
				inserirItemHorarioAgendado(listaItemHorarioAgendadoVO.get(0), vAelHrGradeDispVO, agendaExameMesmoHorario, Boolean.TRUE, nomeMicrocomputador, listaItemHorarioAgendadoVO.get(0).getItemSolicitacaoExameOriginal());
			} else {
				// Agendamento em grupo
				for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
					if (itemHorarioAgendadoVO.getIdentificadoAgendamentoExameEmGrupo()) {
						if (Boolean.TRUE.equals(agendaExameMesmoHorario)) {
							inserirItemHorarioAgendado(itemHorarioAgendadoVO, vAelHrGradeDispVO, agendaExameMesmoHorario, Boolean.FALSE, nomeMicrocomputador, itemHorarioAgendadoVO.getItemSolicitacaoExameOriginal());	
						} else {
							agendarGrupoExamesSequencial(listaItemHorarioAgendadoVO, vAelHrGradeDispVO.getDthrAgenda(), 
									vAelHrGradeDispVO.getGrade(), vAelHrGradeDispVO.getSeqGrade(),
									obterTipoMarcacao(itemHorarioAgendadoVO.getSoeSeq()), obterTipoMarcacaoDiv(), nomeMicrocomputador);
							break;
						}
					}
				}
			}
			desfazerIdentificacaoAgendamentoExamesEmGrupo(listaItemHorarioAgendadoVO);
		} else {
			// Agendamento singular
			ItemHorarioAgendadoVO itemHorarioAgendadoVO = obterExameSelecionado(listaItemHorarioAgendadoVO);
			if (verificarExameAgendado(itemHorarioAgendadoVO)) {
				verificarExameAgendadoMesmaGradeUnidade(itemHorarioAgendadoVO, vAelHrGradeDispVO.getGrade(),
						vAelHrGradeDispVO.getSeqGrade());
			} else {
				inserirItemHorarioAgendado(itemHorarioAgendadoVO, vAelHrGradeDispVO, Boolean.FALSE, Boolean.TRUE, nomeMicrocomputador, itemHorarioAgendadoVO.getItemSolicitacaoExameOriginal());	
			}
		}
	}
	
	public Date gravarHorarioExtra(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO, GradeHorarioExtraVO gradeHorarioExtraVO, 
			Short unfExecutora, Date dataHora, AelTipoMarcacaoExame tipoMarcacaoExame, String nomeMicrocomputador) throws BaseException {

		Integer seqGrade = gradeHorarioExtraVO.getSeqGrade();
		Date dataDisponivel = getConsultaHorarioLivreON().obterDataHoraDisponivelParaGradeEUnidadeExecutora(
				unfExecutora, seqGrade, dataHora);
		
		AelHorarioExameDispDAO horarioExameDispDAO = getAelHorarioExameDispDAO();
		
		if (verificarExistenciaExameAgendaGrupo(listaItemHorarioAgendadoVO)) {
			AelGrupoExames grupoExame = getAelGrupoExamesDAO().obterPorChavePrimaria(gradeHorarioExtraVO.getGrupoExameSeq());
			Boolean agendaExameMesmoHorario = grupoExame.getAgendaExMesmoHor();
			if (listaItemHorarioAgendadoVO.size() == 1) {
				// Agendamento singular
				AelHorarioExameDisp horarioExameDisp = 
						getHorarioExameDispON().obterHorarioExameDisp(unfExecutora, seqGrade, dataDisponivel, tipoMarcacaoExame, horarioExameDispDAO);
				inserirItemHorarioAgendadoExtra(listaItemHorarioAgendadoVO.get(0), horarioExameDisp, agendaExameMesmoHorario, Boolean.TRUE, nomeMicrocomputador);
			} else {
				// Agendamento em grupo
				for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
					if ((itemHorarioAgendadoVO.getIdentificadoAgendamentoExameEmGrupo() && Boolean.TRUE.equals(agendaExameMesmoHorario)) 
							|| Boolean.TRUE.equals(itemHorarioAgendadoVO.getSelecionado())) {
						AelHorarioExameDisp horarioExameDisp = 
								getHorarioExameDispON().obterHorarioExameDisp(unfExecutora, seqGrade, dataDisponivel, tipoMarcacaoExame, horarioExameDispDAO);
						inserirItemHorarioAgendadoExtra(itemHorarioAgendadoVO, horarioExameDisp, agendaExameMesmoHorario, Boolean.FALSE, nomeMicrocomputador);
					}
				}
			}
			desfazerIdentificacaoAgendamentoExamesEmGrupo(listaItemHorarioAgendadoVO);
		} else {
			// Agendamento singular
			ItemHorarioAgendadoVO itemHorarioAgendadoVO = obterExameSelecionado(listaItemHorarioAgendadoVO);
			if (verificarExameAgendado(itemHorarioAgendadoVO)) {
				verificarExameAgendadoMesmaGradeUnidade(itemHorarioAgendadoVO, gradeHorarioExtraVO.getGrade(), 
						gradeHorarioExtraVO.getSeqGrade());
			} else {
				AelHorarioExameDisp horarioExameDisp = 
						getHorarioExameDispON().obterHorarioExameDisp(unfExecutora, seqGrade, dataDisponivel, tipoMarcacaoExame, horarioExameDispDAO);				
				inserirItemHorarioAgendadoExtra(itemHorarioAgendadoVO, horarioExameDisp, Boolean.FALSE, Boolean.TRUE, nomeMicrocomputador);			
			}
		}
		
		return dataDisponivel;
	}
	
	public void inserirItemHorarioAgendado(ItemHorarioAgendadoVO itemHorarioAgendadoVO, VAelHrGradeDispVO vAelHrGradeDispVO, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, String nomeMicrocomputador, AelItemSolicitacaoExames itemSolicitacaoExameOriginal) throws BaseException {
		AelItemHorarioAgendado itemHorarioAgendado = montarItemHorarioAgendado(vAelHrGradeDispVO.getDthrAgenda(),
				vAelHrGradeDispVO.getGrade(), vAelHrGradeDispVO.getSeqGrade(), itemHorarioAgendadoVO.getSeqp(), 
				itemHorarioAgendadoVO.getSoeSeq());
		inserir(itemHorarioAgendado, itemSolicitacaoExameOriginal, vAelHrGradeDispVO.getHrExtra(), agendaExameMesmoHorario, agendamentoSingular,
				itemHorarioAgendadoVO.getSigla(), itemHorarioAgendadoVO.getSeqMaterialAnalise(), itemHorarioAgendadoVO.getSeqUnidade(), nomeMicrocomputador);
	}
	
	public void inserirItemHorarioAgendadoExtra(ItemHorarioAgendadoVO itemHorarioAgendadoVO, AelHorarioExameDisp horarioExameDisp, 
			Boolean agendaExameMesmoHorario, Boolean agendamentoSingular, String nomeMicrocomputador)
					throws BaseException {
//		try {
			getConsultaHorarioLivreON().gravarHorarioExtra(horarioExameDisp);
			AelHorarioExameDispId horarioExameDispId = horarioExameDisp.getId();
			AelItemHorarioAgendado itemHorarioAgendado = montarItemHorarioAgendado(horarioExameDispId.getDthrAgenda(),
					horarioExameDispId.getGaeUnfSeq(), horarioExameDispId.getGaeSeqp(), itemHorarioAgendadoVO.getSeqp(), 
					itemHorarioAgendadoVO.getSoeSeq());
			itemHorarioAgendado.setHorarioExameDisp(horarioExameDisp);
			inserir(itemHorarioAgendado, itemHorarioAgendadoVO.getItemSolicitacaoExameOriginal(), horarioExameDisp.getIndHorarioExtra(), agendaExameMesmoHorario, agendamentoSingular, 
					itemHorarioAgendadoVO.getSigla(), itemHorarioAgendadoVO.getSeqMaterialAnalise(), itemHorarioAgendadoVO.getSeqUnidade(), nomeMicrocomputador);			
//		} catch (BaseException e) {
//			if (e instanceof ApplicationBusinessException) {
//				// Lança exceção com rollback (devido à chamada de gravarHorarioExtra(...) que pode fazer flush)
//				throw new ApplicationBusinessException(e);
//			} else {
//				throw e;
//			}
//		}
	}
	
	/**
	 * Método responsável por realizar o agendamento de horários na sequência para 
	 * um grupo de exames pertencentes ao mesmo grupo.
	 * 
	 * @param grade
	 * @param seqGrade
	 * @param dthrAgenda
	 * @param seqp
	 * @param soeSeq
	 * @param tipoMarc
	 * @param tipoMarcDiv
	 * @param quantidadeExames
	 * @throws BaseException 
	 */
	private void agendarGrupoExamesSequencial(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO, Date dthrAgenda, Short gaeUnfSeq, 
			Integer gaeSeqp, Short tipoMarc, Short tipoMarcDiv, String nomeMicrocomputador) 
					throws BaseException {
		
		Integer quantidadeExames = 0;
//		List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVoEmGrupo = new ArrayList<ItemHorarioAgendadoVO>();
		for(ItemHorarioAgendadoVO item : listaItemHorarioAgendadoVO) {
			if(item.getIdentificadoAgendamentoExameEmGrupo()) {
				quantidadeExames++;
			}
		}

		List<VAelHrGradeDispVO> horariosLivres = getVAelHrGradeDispDAO()
			.pesquisarHorariosLivresAteFinalDia(gaeUnfSeq, gaeSeqp, tipoMarc, tipoMarcDiv, dthrAgenda);
		
		if (horariosLivres.isEmpty() || horariosLivres.size() < quantidadeExames) {
			throw new ApplicationBusinessException(ItemHorarioAgendadoONExceptionCode.AEL_01439);
			//ItemHorarioAgendadoONExceptionCode.AEL_01439.throwException();
		}
		
		List<AelGradeAgendaExame> listaGradeAgendaExame = getAelGradeAgendaExameDAO().pesquisarGradeExameGrupoExame(gaeUnfSeq, gaeSeqp);
		
		AelGradeAgendaExame gradeAgendaExame = null;
		
		if (!listaGradeAgendaExame.isEmpty()) {
			gradeAgendaExame = listaGradeAgendaExame.get(0);	
		}
		
		int i = 0;
		for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
			if (i < horariosLivres.size()) {
				if (itemHorarioAgendadoVO.getIdentificadoAgendamentoExameEmGrupo()) {
				VAelHrGradeDispVO horario = horariosLivres.get(i);
				Date tempoMedioOcupSala = getAelUnfExecutaExamesDAO().obterTempoMedioOcupacaoSala(itemHorarioAgendadoVO.getSigla(), 
						itemHorarioAgendadoVO.getSeqMaterialAnalise(), itemHorarioAgendadoVO.getSeqUnidade());
				
				if (tempoMedioOcupSala != null && gradeAgendaExame != null 
						&& Boolean.TRUE.equals(gradeAgendaExame.getGrupoExame().getCalculaTempo())) {
					Calendar calTempoMedioOcupSala = Calendar.getInstance();
					calTempoMedioOcupSala.setTime(tempoMedioOcupSala);
					Integer minutosMedioOcupSala = calTempoMedioOcupSala.get(Calendar.HOUR_OF_DAY) * 60 
							+ calTempoMedioOcupSala.get(Calendar.MINUTE);
					if (minutosMedioOcupSala > 0) {
						// Se existe um próximo elemento na lista
						if (i+1 < horariosLivres.size()) {
							VAelHrGradeDispVO proximoHorario = horariosLivres.get(i+1);
							if (proximoHorario.getDthrAgenda() != null) {
								// Intervalo de um horário para o outro em minutos
								DateTime horaAtual = new DateTime(horario.getDthrAgenda());
								DateTime horaProxima = new DateTime(proximoHorario.getDthrAgenda());
								Period period = new Period(horaAtual, horaProxima);  
								Integer intervaloEntreHorarios = period.getMinutes();
								
								if (intervaloEntreHorarios < minutosMedioOcupSala) {
									throw new ApplicationBusinessException(ItemHorarioAgendadoONExceptionCode.AEL_01439);
									//ItemHorarioAgendadoONExceptionCode.AEL_01439.throwException(); // Este horário não comporta o tempo de execução dos exames desta solicitação. Favor escolher outro horário.
								}	
							}
						}
					}
				}
				
				if (!horario.getSituacaoHorario().equals(DominioSituacaoHorario.L)) {
					throw new ApplicationBusinessException(ItemHorarioAgendadoONExceptionCode.AEL_01439);
					//ItemHorarioAgendadoONExceptionCode.AEL_01439.throwException();
				}

				// Persiste o exame no horário disponível

					AelItemHorarioAgendado itemHorarioAgendado = montarItemHorarioAgendado(horario.getDthrAgenda(), gaeUnfSeq, gaeSeqp, 
							itemHorarioAgendadoVO.getSeqp(), itemHorarioAgendadoVO.getSoeSeq());
					inserir(itemHorarioAgendado, null, horario.getHrExtra(), Boolean.FALSE, Boolean.FALSE, itemHorarioAgendadoVO.getSigla(),
							itemHorarioAgendadoVO.getSeqMaterialAnalise(), itemHorarioAgendadoVO.getSeqUnidade(), nomeMicrocomputador);
					i++;
				}
			}
		}
	}	
	
	public Boolean verificarExameAgendado(ItemHorarioAgendadoVO itemHorarioAgendadoVO) {
		if (itemHorarioAgendadoVO.getItemHorarioAgendadoId() != null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	/**
	 * Verifica se o itemHorarioAgendadoVO (exame) já possui horário marcado para a mesma grade/unidade.
	 * Caso não possua, lança uma exceção indicando a necessidade de confirmar a gravação do agendamento.
	 *  
	 * @param itemHorarioAgendadoVO
	 * @param gaeUnfSeq
	 * @param gaeSeqp
	 * @throws ApplicationBusinessException
	 */
	private void verificarExameAgendadoMesmaGradeUnidade(ItemHorarioAgendadoVO itemHorarioAgendadoVO, Short gaeUnfSeq, Integer gaeSeqp) 
			throws ApplicationBusinessException {
		AelItemHorarioAgendadoId itemHorarioAgendadoId = itemHorarioAgendadoVO.getItemHorarioAgendadoId();
		if (itemHorarioAgendadoId != null 
				&& itemHorarioAgendadoId.getHedGaeUnfSeq().equals(gaeUnfSeq)
				&& itemHorarioAgendadoId.getHedGaeSeqp().equals(gaeSeqp)) {
			throw new ApplicationBusinessException(
					ItemHorarioAgendadoONExceptionCode.MENSAGEM_ERRO_SELECIONAR_EXAME_JA_AGENDADO_MESMA_GRADE_UNIDADE);
		} else {
			// Também é lançada uma exceção, pois o fluxo do código é alterado devido a 
			// necessidade de exibir uma de confirmação na tela para o usuário. Caso essa exceção
			// seja capturada na controller, um pedido de confirmação deverá ser exibido na tela.
			throw new ApplicationBusinessException(ItemHorarioAgendadoONExceptionCode.SELECAO_EXAME_JA_AGENDADO);
		}
	}
	
	private Boolean verificarExistenciaExameAgendaGrupo(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO) {
		for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
			if (itemHorarioAgendadoVO.getIdentificadoAgendamentoExameEmGrupo()) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}	
	
	private ItemHorarioAgendadoVO obterExameSelecionado(List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO) {
		for (ItemHorarioAgendadoVO itemHorarioAgendadoVO : listaItemHorarioAgendadoVO) {
			if (itemHorarioAgendadoVO.getSelecionado()) {
				return itemHorarioAgendadoVO;
			}
		}
		return null;
	}
	
	private AelItemHorarioAgendado montarItemHorarioAgendado(Date dthrAgenda, Short grade, Integer seqGrade, Short seqp, Integer soeSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelItemHorarioAgendado itemHorarioAgendado = new AelItemHorarioAgendado();
		AelItemHorarioAgendadoId itemHorarioAgendadoId = new AelItemHorarioAgendadoId();
		itemHorarioAgendadoId.setHedDthrAgenda(dthrAgenda);
		itemHorarioAgendadoId.setHedGaeUnfSeq(grade);
		itemHorarioAgendadoId.setHedGaeSeqp(seqGrade);
		itemHorarioAgendadoId.setIseSeqp(seqp);
		itemHorarioAgendadoId.setIseSoeSeq(soeSeq);
		itemHorarioAgendado.setId(itemHorarioAgendadoId);
		itemHorarioAgendado.setServidor(servidorLogado);
		
		return itemHorarioAgendado;
	}
	
	private Short obterTipoMarcacao(Integer soeSeq) throws ApplicationBusinessException {
		DominioOrigemAtendimento origemAtendimento = null;
		Short unfSeq = null;
		AelSolicitacaoExames solicitacaoExame = getAelSolicitacaoExameDAO().obterPorChavePrimaria(soeSeq);
		if (solicitacaoExame.getAtendimento() != null){
			AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
			if (atendimento != null){
				origemAtendimento = atendimento.getOrigem();
			}
		}
		unfSeq = solicitacaoExame.getUnidadeFuncional().getSeq();

		Short tipoMarcacao = this.getConsultaHorarioLivreRN().obterTipoMarcacao(origemAtendimento, unfSeq);
		return tipoMarcacao;
	}
	
	private Short obterTipoMarcacaoDiv() throws ApplicationBusinessException {
		Short tipoMarcacaoDiv = null;
		AghParametros parametroTipoMarcacao = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TIPO_MARC_DIV);
		if (parametroTipoMarcacao != null){
			tipoMarcacaoDiv = parametroTipoMarcacao.getVlrNumerico().shortValue();
		}
		return tipoMarcacaoDiv;
	}
	
	protected ItemHorarioAgendadoRN getItemHorarioAgendadoRN(){
		return itemHorarioAgendadoRN;
	}
	
	protected ConsultaHorarioLivreRN getConsultaHorarioLivreRN() {
		return consultaHorarioLivreRN;
	}
	
	protected ConsultaHorarioLivreON getConsultaHorarioLivreON() {
		return consultaHorarioLivreON;
	}
	
	protected HorarioExameDispON getHorarioExameDispON() {
		return horarioExameDispON;
	}
	
	protected VAelSolicPacDAO getVAelSolicPacDAO(){
		return vAelSolicPacDAO;
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected VAelGradeHrAgendaDAO getVAelGradeHrAgendaDAO(){
		return vAelGradeHrAgendaDAO;
	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO(){
		return aelUnfExecutaExamesDAO;
	}
	
	protected VAelHrGradeDispTrDAO getVAelHrGradeDispTrDAO(){
		return vAelHrGradeDispTrDAO;
	}
	
	protected AelGrupoExameUnidExameDAO getAelGrupoExameUnidExameDAO() {
		return aelGrupoExameUnidExameDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	

	protected AelGrupoExamesDAO getAelGrupoExamesDAO() {
		return aelGrupoExamesDAO;
	}
	
	protected VAelHrGradeDispDAO getVAelHrGradeDispDAO() {
		return vAelHrGradeDispDAO;
	}
	
	protected AelGradeAgendaExameDAO getAelGradeAgendaExameDAO() {
		return aelGradeAgendaExameDAO;
	}
	
	protected AelHorarioExameDispDAO getAelHorarioExameDispDAO() {
		return aelHorarioExameDispDAO;
	}

	public List<AgendamentoExameVO> reatacharListaExamesAgendamentoSelecao(List<AgendamentoExameVO> examesAgendamentoSelecao) {
		for (AgendamentoExameVO agendamentoExameVO : examesAgendamentoSelecao) {
			
//			getAelItemSolicitacaoExamesDAO().desatachar(agendamentoExameVO.getItemExame());
//			agendamentoExameVO.setItemExame(getAelItemSolicitacaoExamesDAO().obterOriginal(agendamentoExameVO.getItemExame()));
			agendamentoExameVO.setItemExame(getAelItemSolicitacaoExamesDAO().obterPorChavePrimaria(agendamentoExameVO.getItemExame().getId()));
			if (agendamentoExameVO.getUnidadeExecutora() != null) {
//				getAghUnidadesFuncionaisDAO().desatachar(agendamentoExameVO.getUnidadeExecutora());
				agendamentoExameVO.setUnidadeExecutora(getAghuFacade().obterAghUnidadesFuncionaisOriginal(agendamentoExameVO.getUnidadeExecutora()));
			}
		}
		return examesAgendamentoSelecao;
	}
	
	private AelItemSolicitacaoExameDAO getAelItemSolicitacaoExamesDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
