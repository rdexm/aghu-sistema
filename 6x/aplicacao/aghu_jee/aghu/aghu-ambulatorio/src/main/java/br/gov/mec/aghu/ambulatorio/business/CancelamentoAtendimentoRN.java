package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.GenericJDBCException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamExtratoControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemReceituarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLogEmUsosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSituacaoAtendimentosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioMotivoPendencia;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.MamExtratoControlesId;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * ORADB: Package MAMK_CANCELAR
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength","PMD.AtributoEmSeamContextManager"})
@Stateless
public class CancelamentoAtendimentoRN extends BaseBusiness{
	
	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	@EJB
	private ProcedimentoAtendimentoConsultaRN procedimentoAtendimentoConsultaRN;
	
	@EJB
	private FinalizaAtendimentoRN finalizaAtendimentoRN;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	private static final Log LOG = LogFactory.getLog(CancelamentoAtendimentoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private MamItemEvolucoesDAO mamItemEvolucoesDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private MamReceituariosDAO mamReceituariosDAO;
	
	@Inject
	private MamNotaAdicionalEvolucoesDAO mamNotaAdicionalEvolucoesDAO;
	
	@Inject
	private MamItemReceituarioDAO mamItemReceituarioDAO;
	
	@Inject
	private MamControlesDAO mamControlesDAO;
	
	@Inject
	private MamExtratoControlesDAO mamExtratoControlesDAO;
	
	@EJB
	private IExamesBeanFacade examesBeanFacade;
	
	@Inject
	private MamLogEmUsosDAO mamLogEmUsosDAO;
	
	@Inject
	private MamItemAnamnesesDAO mamItemAnamnesesDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject
	private MamSituacaoAtendimentosDAO mamSituacaoAtendimentosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MamNotaAdicionalAnamnesesDAO mamNotaAdicionalAnamnesesDAO;
	
	@Inject
	private MamProcedimentoRealizadoDAO mamProcedimentoRealizadoDAO;
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4199626157643039241L;
	public Boolean cancelaTudo = true;
	
	private enum CancelamentoAtendimentoRNExceptionCode implements BusinessExceptionCode {
		MAM_01025, MAM_01026, MAM_01027, MAM_01028, MAM_01029, MAM_01065, MAM_01066, MAM_01067, MAM_01068, 
		MAM_01069, MAM_01034, MAM_01032, MAM_01033, MAM_01030, MAM_01031, MAM_00976, MAM_00977, MAM_00978,
		MAM_00979, MAM_00980, MAM_00981, MAM_00982, MAM_00983, MAM_00984, ERRO_CLONE_NOTA_ADICIONAL_ANAMNESE,
		ERRO_CLONE_NOTA_ADICIONAL_EVOLUCAO, ERRO_CLONE_PROCEDIMENTO_REALIZADO, MAM_01048, MAM_00858, MAM_00859,
		MAM_00860, MAM_00861, ERRO_CLONE_RECEITUARIO, MAM_00862, MAM_00635, MAM_00273, MAM_00274, MAM_00999,
		MAM_00275, MAM_00276, MAM_00277,ERRO_CONTROLE_ALTERADO,MAM_01195,MAM_01508
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CANC
	 * @param conNumero
	 * @param servidor
	 * @throws BaseException 
	 */
	//Alguns trechos foram comentados por não serem necessários na estória 6219, os mesmos devem ser implementados quando houver necessidade
	public void cancelarAtendimento(Integer conNumero, String nomeMicrocomputador) throws BaseException,GenericJDBCException{

		this.cancelaTudo = false;
		this.cancelarAnamnese(conNumero, null);
		this.cancelarNotaAdicionalAnamnese(conNumero, null);
		/*
		mamp_canc_figura_ana
			(p_con_numero,
			 NULL);
		*/
		this.cancelarEvolucao(conNumero, null);
		this.cancelarNotaAdicionalEvolucao(conNumero, null);
		/* 
		 cancela a figura evolução 
		mamp_canc_figura_evo
			(p_con_numero,
			 NULL);
		--
		 cancela o relatorio 
		mamp_canc_relatorio
			(p_con_numero,
			 NULL);
		--
		 cancela os atestados 
		mamp_canc_atestado
			(p_con_numero,
			 NULL);
		--*/
		this.cancelarReceituario(conNumero, null);
		/*
		cancela os receitas 
		mamp_canc_cuidado
			(p_con_numero,
			 NULL);
		--
		 cancela os diagnosticos 
		mamp_canc_diag
			(p_con_numero,
			 NULL);
		--
		 cancela os medicamentos ativos  
		mamp_canc_mav
			(p_con_numero,
			 NULL);
		--*/
		this.cancelarProcedimento(conNumero, null);
		/*
		 cancela os motivos de atendimento 
		mamp_canc_motivo
			(p_con_numero,
			 NULL);
		--
		 atualiza a solicitacao de retorno 
		mamp_canc_solic_ret
			(p_con_numero,
			 NULL);
		--
		 atualiza a interconsulta 
		mamp_canc_intercon
			(p_con_numero,
		       NULL);
		--
		 atualiza o laudo aih 
		mamp_canc_laudo_aih
			(p_con_numero,
			 NULL);
		--
		 cancela a solicitação hemoterápica  
		mamp_canc_solic_hemo
			(p_con_numero,
			 NULL);
		--
		 cancela a solicitação de procedimentos 
		mamp_canc_solic_proc
			(p_con_numero,
			 NULL);
		--*/
		this.cancelarExames(conNumero, null, nomeMicrocomputador);
		/*
		 cancela a alta 
		mamp_canc_alta
			(p_con_numero,
			 NULL);
		--
		 cancela lembretes 
		mamp_canc_lembrete
			(p_con_numero,
			 NULL);
		--*/
		this.cancelarControle(conNumero, nomeMicrocomputador);
		/*
		 - cancela tudo o que tiver sobrado nas temporárias
		mamp_canc_temps (p_con_numero);
		 */
		this.cancelaTudo = true;
	}
	/**
	 * Function
	 * ORADB MAMC_GET_DT_ULT_MOV
	 * @param conNumero
	 * @param servidor
	 * @return
	 *  
	 */
	public Date obterDataUltimoMovimentacao(Integer conNumero, RapServidores servidor) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidor==null ||servidor.getId()== null){
			servidor = servidorLogado;
		}
		Date dataMovimentacao = this.getMamLogEmUsosDAO().obterUltimaDataPorConsultaEServidor(conNumero, servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
		if(dataMovimentacao==null){
			dataMovimentacao = new Date();
		}
		return dataMovimentacao;
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CANC_NOTAS_ANA
	 * @param conNumero
	 * @param anaSeq
	 *  
	 */
	public void cancelarNotaAdicionalAnamnese(Integer conNumero, Long naaSeq) throws ApplicationBusinessException{
		Date dthrMovimento;
		if(cancelaTudo){
			dthrMovimento = null;
		} else {
			dthrMovimento = this.obterDataUltimoMovimentacao(conNumero, null);
		}
		List<MamNotaAdicionalAnamneses> lista = this.getMamNotaAdicionalAnamnesesDAO().pesquisarNotaAdicionalAnamnesesParaCancelamento(conNumero, dthrMovimento, naaSeq);
		for(MamNotaAdicionalAnamneses notaAdicionalAnamneses:lista){
			Integer naaSeqAux = null;
			if(notaAdicionalAnamneses.getNotaAdicionalAnamnese()!=null){
				 naaSeqAux = notaAdicionalAnamneses.getNotaAdicionalAnamnese().getSeq(); 
			}
			if(notaAdicionalAnamneses!=null && notaAdicionalAnamneses.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarNotaAdicionalAnamneseRascunho(notaAdicionalAnamneses.getSeq(), naaSeqAux);
			} else if(notaAdicionalAnamneses.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarNotaAdicionalAnamnesePendente(notaAdicionalAnamneses.getSeq(), naaSeqAux);
			} else if(notaAdicionalAnamneses.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarNotaAdicionalAnamneseExclusao(notaAdicionalAnamneses.getSeq());
			}	
		}
	}
	
	public void tratarNotaAdicionalAnamneseRascunho(Integer seq, Integer naaSeq) throws ApplicationBusinessException {
		try{
			MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(seq);
			this.getMarcacaoConsultaRN().removerNotaAdicionalAnamneses(notaAdicionalAnamneses);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01025);
		}
		if(naaSeq!=null){
			MamNotaAdicionalAnamneses notaAdicionalAnamnesesOld = null;
			MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(naaSeq);
			try{
				notaAdicionalAnamnesesOld = (MamNotaAdicionalAnamneses) BeanUtils.cloneBean(notaAdicionalAnamneses);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_ANAMNESE);
			}
			notaAdicionalAnamneses.setDthrMvto(null);
			notaAdicionalAnamneses.setServidorMvto(null);
			try{
				this.getMarcacaoConsultaRN().atualizarNotaAdicionalAnamneses(notaAdicionalAnamneses, notaAdicionalAnamnesesOld);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01026);	
			}
		}
	}
	
	public void tratarNotaAdicionalEvolucaoRascunho(Integer seq, Integer nevSeq) throws ApplicationBusinessException{
		try{
			MamNotaAdicionalEvolucoes notaAdicionalEvolucao = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(seq);
			this.getMarcacaoConsultaRN().removerNotaAdicionalEvolucoes(notaAdicionalEvolucao);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01030);
		}
		if(nevSeq!=null){
			MamNotaAdicionalEvolucoes notaAdicionalEvolucaoOld =  null;
			MamNotaAdicionalEvolucoes notaAdicionalEvolucao = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(nevSeq);
			try{
				notaAdicionalEvolucaoOld = (MamNotaAdicionalEvolucoes) BeanUtils.cloneBean(notaAdicionalEvolucao);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_EVOLUCAO);
			}
			notaAdicionalEvolucao.setDthrMvto(null);
			notaAdicionalEvolucao.setServidorMvto(null);
			try{
				this.getMarcacaoConsultaRN().atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucao, notaAdicionalEvolucaoOld);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01031);	
			}
		}
	}
	
	public void tratarProcedimentoRealizadoRascunho(Long seq, Long polSeq) throws ApplicationBusinessException {
		try{
			MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(seq);
			this.getProcedimentoAtendimentoConsultaRN().removerProcedimentoRealizado(procedimento, false);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01065);
		}
		if(polSeq!=null){
			MamProcedimentoRealizado procedimentoOld = null;
			MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(polSeq);
			try{
				procedimentoOld = (MamProcedimentoRealizado) BeanUtils.cloneBean(procedimento);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.ERRO_CLONE_PROCEDIMENTO_REALIZADO);
			}
			procedimento.setDthrMovimento(null);
			procedimento.setServidorMovimento(null);
			try{
				this.getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(procedimentoOld, procedimento, true);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01066);	
			}
		}
	}
	
	//TODO Trecho comentado pois a tabela mam_resposta_evolucoes nao será utilizada no cancelar atendimento
	public void tratarEvolucaoRascunho(Long seq, Long evoSeq) throws ApplicationBusinessException{
		List<MamItemEvolucoes> listaItem = this.getMamItemEvolucoesDAO().pesquisarItemEvolucoesPorEvolucao(seq);
		for(MamItemEvolucoes itemEvolucao: listaItem){
			try{
				this.getMarcacaoConsultaRN().removerItemEvolucao(itemEvolucao);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00976);
			}
		}
	
		/*List<MamRespostaEvolucoes> listaResposta = this.getMamRespostaEvolucoesDAO().pesquisarRespostaEvolucaoPorEvolucao(evoSeq);
		for(MamRespostaEvolucoes respostaEvolucao: listaResposta){
			try{
				this.getMarcacaoConsultaRN().removerRespostaEvolucao(respostaEvolucao);
			} catch(Exception e){
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00977);
			}
		}*/
		
		try{
			MamEvolucoes evolucao = this.getMamEvolucoesDAO().obterPorChavePrimaria(seq);
			this.getMarcacaoConsultaRN().removerEvolucao(evolucao);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00978);
		}
		if(evoSeq!=null){
			MamEvolucoes evolucao = this.getMamEvolucoesDAO().obterPorChavePrimaria(evoSeq);
			evolucao.setDthrMvto(null);
			evolucao.setServidorMvto(null);
			try{
				this.getMarcacaoConsultaRN().atualizarEvolucao(evolucao);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00979);	
			}
		}
	}
	
	public void tratarReceituarioRascunho(Long seq, Long rctSeq) throws ApplicationBusinessException{
		List<MamItemReceituario> listaItem = this.getMamItemReceituarioDAO().pesquisarMamItemReceituario(seq);
		for(MamItemReceituario itemReceituario: listaItem){
			try{
				this.getAmbulatorioFacade().removerItemReceituario(itemReceituario);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01048);
			}
		}
	
		try{
			MamReceituarios receituario = this.getMamReceituariosDAO().obterPorChavePrimaria(seq);
			this.getAmbulatorioFacade().removerReceituario(receituario);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00858);
		}
		if(rctSeq!=null){
			MamReceituarios receituario = this.getMamReceituariosDAO().obterPorChavePrimaria(rctSeq);
			receituario.setDthrMvto(null);
			receituario.setServidorMovimento(null);
			try{
				this.getAmbulatorioFacade().atualizarReceituario(receituario);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00859);	
			}
		}
	}
	
	
	
	public void tratarNotaAdicionalAnamnesePendente(Integer seq, Integer naaSeq) throws ApplicationBusinessException{
		try{
			MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(seq);
			this.getMarcacaoConsultaRN().removerNotaAdicionalAnamneses(notaAdicionalAnamneses);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01027);
		}
		if(naaSeq!=null){
			MamNotaAdicionalAnamneses notaAdicionalAnamneseOld = null;
			MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(naaSeq);
			try{
				notaAdicionalAnamneseOld = (MamNotaAdicionalAnamneses) BeanUtils.cloneBean(notaAdicionalAnamneses);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_ANAMNESE);
			}
			notaAdicionalAnamneses.setDthrMvto(null);
			notaAdicionalAnamneses.setServidorMvto(null);
			notaAdicionalAnamneses.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getMarcacaoConsultaRN().atualizarNotaAdicionalAnamneses(notaAdicionalAnamneses, notaAdicionalAnamneseOld);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01028);	
			}
		}
	}
	
	public void tratarNotaAdicionalEvolucaoPendente(Integer seq, Integer nevSeq) throws ApplicationBusinessException{
		try{
			MamNotaAdicionalEvolucoes notaAdicionalEvolucao = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(seq);
			this.getMarcacaoConsultaRN().removerNotaAdicionalEvolucoes(notaAdicionalEvolucao);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01032);
		}
		if(nevSeq!=null){
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoesOld = null;
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoes = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(nevSeq);
			try{
				notaAdicionalEvolucoesOld = (MamNotaAdicionalEvolucoes) BeanUtils.cloneBean(notaAdicionalEvolucoes);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_EVOLUCAO);
			}
			notaAdicionalEvolucoes.setDthrMvto(null);
			notaAdicionalEvolucoes.setServidorMvto(null);
			notaAdicionalEvolucoes.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getMarcacaoConsultaRN().atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucoes, notaAdicionalEvolucoesOld);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01033);	
			}
		}
	}
	
	public void tratarProcedimentoRealizadoPendente(Long seq, Long polSeq) throws ApplicationBusinessException{
		try{
			MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(seq);
			this.getProcedimentoAtendimentoConsultaRN().removerProcedimentoRealizado(procedimento, false);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01067);
		}
		if(polSeq!=null){
			MamProcedimentoRealizado procedimentoOld = null;
			MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(polSeq);
			try{
				procedimentoOld = (MamProcedimentoRealizado) BeanUtils.cloneBean(procedimento);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.ERRO_CLONE_PROCEDIMENTO_REALIZADO);
			}
			procedimento.setDthrMovimento(null);
			procedimento.setServidorMovimento(null);
			procedimento.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(procedimentoOld, procedimento, true);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01068);	
			}
		}
	}
	
	//TODO Trecho comentado pois a tabela mam_resposta_evolucoes não será usada no cancelar atendimento
	public void tratarEvolucaoPendente(Long seq, Long evoSeq) throws ApplicationBusinessException{
		List<MamItemEvolucoes> listaItem = this.getMamItemEvolucoesDAO().pesquisarItemEvolucoesPorEvolucao(seq);
		for(MamItemEvolucoes itemEvolucao: listaItem){
			try{
				this.getMarcacaoConsultaRN().removerItemEvolucao(itemEvolucao);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00980);
			}
		}
	
		/*List<MamRespostaEvolucoes> listaResposta = this.getMamRespostaEvolucoesDAO().pesquisarRespostaEvolucaoPorEvolucao(evoSeq);
		for(MamRespostaEvolucoes respostaEvolucao: listaResposta){
			try{
				this.getMarcacaoConsultaRN().removerRespostaEvolucao(respostaEvolucao);
			} catch(Exception e){
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00981);
			}
		}*/
		
		try{
			MamEvolucoes evolucao = this.getMamEvolucoesDAO().obterPorChavePrimaria(seq);
			this.getMarcacaoConsultaRN().removerEvolucao(evolucao);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00982);
		}
		if(evoSeq!=null){
			MamEvolucoes evolucao = this.getMamEvolucoesDAO().obterPorChavePrimaria(evoSeq);
			evolucao.setDthrMvto(null);
			evolucao.setServidorMvto(null);
			evolucao.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getMarcacaoConsultaRN().atualizarEvolucao(evolucao);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00983);	
			}
		}
	}
	
	public void tratarReceituarioPendente(Long seq, Long rctSeq) throws ApplicationBusinessException{
		List<MamItemReceituario> listaItem = this.getMamItemReceituarioDAO().pesquisarMamItemReceituario(seq);
		for(MamItemReceituario itemReceituario: listaItem){
			try{
				this.getAmbulatorioFacade().removerItemReceituario(itemReceituario);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00860);
			}
		}
		try{
			MamReceituarios receituario = this.getMamReceituariosDAO().obterPorChavePrimaria(seq);
			this.getAmbulatorioFacade().removerReceituario(receituario);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00860);
		}
		if(rctSeq!=null){
			MamReceituarios receituario = this.getMamReceituariosDAO().obterPorChavePrimaria(rctSeq);
			receituario.setDthrMvto(null);
			receituario.setServidorMovimento(null);
			receituario.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getAmbulatorioFacade().atualizarReceituario(receituario);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00861);	
			}
		}
	}
	
	//TODO Trecho comentado pois a tabela mam_resposta_anamneses nao sera usada no cancelar atendimento
	public void tratarAnamneseRascunho(Long seq, Long anaSeq) throws ApplicationBusinessException{
		List<MamItemAnamneses> listaItem = this.getMamItemAnamnesesDAO().pesquisarItemAnamnesesPorAnamneses(seq);
		for(MamItemAnamneses itemAnamnese: listaItem){
			try{
				this.getMarcacaoConsultaRN().removerItemAnamnese(itemAnamnese);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00635);
			}
		}
	
		/*     BEGIN
	       DELETE FROM mam_resposta_anamneses
	       WHERE ana_seq = t_seq;
	       EXCEPTION
	         WHEN regra_negocio THEN
	              RAISE;
	         WHEN OTHERS THEN
	              raise_application_error (-20000, 'MAM-00636 #1'||SQLERRM);
	     END;
	*/  
		MamAnamneses anamnese = this.getMamAnamnesesDAO().obterPorChavePrimaria(seq);
		try{
			this.getMarcacaoConsultaRN().removerAnamnese(anamnese);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00273);
		}
		
		if(anaSeq!=null){
			MamAnamneses anamneseAux = this.getMamAnamnesesDAO().obterPorChavePrimaria(anaSeq);
			anamneseAux.setDthrMvto(null);
			anamneseAux.setServidorMvto(null);
			try{
				this.getMarcacaoConsultaRN().atualizarAnamnese(anamnese);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00274);	
			}
		}
	}
	
	//TODO Trecho comentado pois a tabela mam_resposta_anamneses nao sera usada no cancelar atendimento
	public void tratarAnamnesePendente(Long seq, Long anaSeq) throws ApplicationBusinessException{
		List<MamItemAnamneses> listaItem = this.getMamItemAnamnesesDAO().pesquisarItemAnamnesesPorAnamneses(seq);
		for(MamItemAnamneses itemAnamnese: listaItem){
			try{
				this.getMarcacaoConsultaRN().removerItemAnamnese(itemAnamnese);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00999);
			}
		}
	
		/*     BEGIN
	       DELETE FROM mam_resposta_anamneses
	       WHERE ana_seq = t_seq;
	       EXCEPTION
	         WHEN regra_negocio THEN
	              RAISE;
	         WHEN OTHERS THEN
	              raise_application_error (-20000, 'MAM-00639 #1'||SQLERRM);
	     END;
	*/ 
		MamAnamneses anamnese = this.getMamAnamnesesDAO().obterPorChavePrimaria(seq);
		try{
			this.getMarcacaoConsultaRN().removerAnamnese(anamnese);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00275);
		}
		
		if(anaSeq!=null){
			MamAnamneses anamneseAux = this.getMamAnamnesesDAO().obterPorChavePrimaria(anaSeq);
			anamneseAux.setDthrMvto(null);
			anamneseAux.setServidorMvto(null);
			anamneseAux.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getMarcacaoConsultaRN().atualizarAnamnese(anamneseAux);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00276);	
			}
		}
	}
	
	public void tratarReceituarioExclusao(Long seq) throws ApplicationBusinessException{
		MamReceituarios receituario = this.getMamReceituariosDAO().obterPorChavePrimaria(seq);
		receituario.setDthrMvto(null);
		receituario.setServidorMovimento(null);
		receituario.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getAmbulatorioFacade().atualizarReceituario(receituario);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00862);	
		}
	}

	
	public void tratarNotaAdicionalAnamneseExclusao(Integer seq) throws ApplicationBusinessException{
		MamNotaAdicionalAnamneses notaAdicionalAnamneseOld = null;
		MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(seq);
		try{
			notaAdicionalAnamneseOld = (MamNotaAdicionalAnamneses) BeanUtils.cloneBean(notaAdicionalAnamneses);	
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_ANAMNESE);
		}
		notaAdicionalAnamneses.setDthrMvto(null);
		notaAdicionalAnamneses.setServidorMvto(null);
		notaAdicionalAnamneses.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getMarcacaoConsultaRN().atualizarNotaAdicionalAnamneses(notaAdicionalAnamneses, notaAdicionalAnamneseOld);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01029);	
		}
	}
	
	public void tratarNotaAdicionalEvolucaoExclusao(Integer seq) throws ApplicationBusinessException{
		MamNotaAdicionalEvolucoes notaAdicionalEvolucaoOld = null;
		MamNotaAdicionalEvolucoes notaAdicionalEvolucao = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(seq);
		try{
			notaAdicionalEvolucaoOld = (MamNotaAdicionalEvolucoes) BeanUtils.cloneBean(notaAdicionalEvolucao);	
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_EVOLUCAO);
		}
		notaAdicionalEvolucao.setDthrMvto(null);
		notaAdicionalEvolucao.setServidorMvto(null);
		notaAdicionalEvolucao.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getMarcacaoConsultaRN().atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucao, notaAdicionalEvolucaoOld);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01034);	
		}
	}
	
	public void tratarProcedimentoRealizadoExclusao(Long seq) throws ApplicationBusinessException{
		MamProcedimentoRealizado procedimentoOld = null;
		MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(seq);
		try{
			procedimentoOld = (MamProcedimentoRealizado) BeanUtils.cloneBean(procedimento);	
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.ERRO_CLONE_PROCEDIMENTO_REALIZADO);
		}
		procedimento.setDthrMovimento(null);
		procedimento.setServidorMovimento(null);
		procedimento.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(procedimentoOld, procedimento, true);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01069);	
		}
	}
	
	public void tratarEvolucaoExclusao(Long seq) throws ApplicationBusinessException{
		MamEvolucoes evolucao = this.getMamEvolucoesDAO().obterPorChavePrimaria(seq);
		evolucao.setDthrMvto(null);
		evolucao.setServidorMvto(null);
		evolucao.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getMarcacaoConsultaRN().atualizarEvolucao(evolucao);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00984);	
		}
	}
	
	public void tratarAnamneseExclusao(Long seq) throws ApplicationBusinessException{
		MamAnamneses anamnese = this.getMamAnamnesesDAO().obterPorChavePrimaria(seq);
		anamnese.setDthrMvto(null);
		anamnese.setServidorMvto(null);
		anamnese.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getMarcacaoConsultaRN().atualizarAnamnese(anamnese);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_00277);	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CANC_PROCED
	 * @param conNumero
	 * @param polSeq
	 *  
	 */
	public void cancelarProcedimento(Integer conNumero, Long polSeq) throws ApplicationBusinessException{
		Date dthrMovimento;
		if(cancelaTudo){
			dthrMovimento = null;
		} else {
			dthrMovimento = this.obterDataUltimoMovimentacao(conNumero, null);
		}
		List<MamProcedimentoRealizado> lista = this.getMamProcedimentoRealizadoDAO().pesquisarProcedimentoRealizadoParaCancelamento(conNumero, dthrMovimento, polSeq);
		for(MamProcedimentoRealizado procedimento:lista){
			Long polSeqAux = null;
			if(procedimento.getProcedimentoRealizado()!=null){
				 polSeqAux = procedimento.getProcedimentoRealizado().getSeq(); 
			}
			if(procedimento!=null && procedimento.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarProcedimentoRealizadoRascunho(procedimento.getSeq(), polSeqAux);
			} else if(procedimento.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarProcedimentoRealizadoPendente(procedimento.getSeq(), polSeqAux);
			} else if(procedimento.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarProcedimentoRealizadoExclusao(procedimento.getSeq());
			}	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CANC_NOTAS_EVO
	 * @param conNumero
	 * @param nevSeq
	 *  
	 */
	public void cancelarNotaAdicionalEvolucao(Integer conNumero, Integer nevSeq) throws ApplicationBusinessException{
		Date dthrMovimento;
		if(cancelaTudo){
			dthrMovimento = null;
		} else {
			dthrMovimento = this.obterDataUltimoMovimentacao(conNumero, null);
		}
		List<MamNotaAdicionalEvolucoes> lista = this.getMamNotaAdicionalEvolucoesDAO().pesquisarNotaAdicionalEvolucaoParaCancelamento(conNumero, dthrMovimento, nevSeq);
		for(MamNotaAdicionalEvolucoes notaAdicionalEvolucao:lista){
			Integer nevSeqAux = null;
			if(notaAdicionalEvolucao.getNotaAdicionalEvolucao()!=null){
				 nevSeqAux = notaAdicionalEvolucao.getNotaAdicionalEvolucao().getSeq(); 
			}
			if(notaAdicionalEvolucao!=null && notaAdicionalEvolucao.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarNotaAdicionalEvolucaoRascunho(notaAdicionalEvolucao.getSeq(), nevSeqAux);
			} else if(notaAdicionalEvolucao.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarNotaAdicionalEvolucaoPendente(notaAdicionalEvolucao.getSeq(), nevSeqAux);
			} else if(notaAdicionalEvolucao.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarNotaAdicionalEvolucaoExclusao(notaAdicionalEvolucao.getSeq());
			}	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CANC_EVOLUCAO
	 * @param conNumero
	 * @param evoSeq
	 *  
	 */
	public void cancelarEvolucao(Integer conNumero, Long evoSeq) throws ApplicationBusinessException{
		Date dthrMovimento;
		if(cancelaTudo){
			dthrMovimento = null;
		} else {
			dthrMovimento = this.obterDataUltimoMovimentacao(conNumero, null);
		}
		List<MamEvolucoes> lista = this.getMamEvolucoesDAO().pesquisarEvolucaoParaCancelamento(conNumero, dthrMovimento, evoSeq);
		for(MamEvolucoes evolucao:lista){
			//mamp_canc_del_tmp_e(r_evo.seq);
			Long evoSeqAux = null;
			if(evolucao.getEvolucao()!=null){
				 evoSeqAux = evolucao.getEvolucao().getSeq(); 
			}
			if(evolucao!=null && evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarEvolucaoRascunho(evolucao.getSeq(), evoSeqAux);
			} else if(evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarEvolucaoPendente(evolucao.getSeq(), evoSeqAux);
			} else if(evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarEvolucaoExclusao(evolucao.getSeq());
			}	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CANC_RECEITA
	 * @param conNumero
	 * @param rctSeq
	 *  
	 */
	public void cancelarReceituario(Integer conNumero, Long rctSeq) throws ApplicationBusinessException{
		Date dthrMovimento;
		if(cancelaTudo){
			dthrMovimento = null;
		} else {
			dthrMovimento = this.obterDataUltimoMovimentacao(conNumero, null);
		}
		List<MamReceituarios> lista = this.getMamReceituariosDAO().pesquisarReceituarioParaCancelamento(conNumero, dthrMovimento, rctSeq);
		for(MamReceituarios receituario:lista){
			Long rctSeqAux = null;
			if(receituario.getReceituario()!=null){
				 rctSeqAux = receituario.getReceituario().getSeq(); 
			}
			if(receituario!=null && receituario.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarReceituarioRascunho(receituario.getSeq(), rctSeqAux);
			} else if(receituario.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarReceituarioPendente(receituario.getSeq(), rctSeqAux);
			} else if(receituario.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarReceituarioExclusao(receituario.getSeq());
			}	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CANC_ANAMNESE
	 * @param conNumero
	 * @param anaSeq
	 *  
	 */
	public void cancelarAnamnese(Integer conNumero, Long anaSeq) throws ApplicationBusinessException{
		Date dthrMovimento;
		if(cancelaTudo){
			dthrMovimento = null;
		} else {
			dthrMovimento = this.obterDataUltimoMovimentacao(conNumero, null);
		}
		List<MamAnamneses> lista = this.getMamAnamnesesDAO().pesquisarAnamneseParaCancelamento(conNumero, dthrMovimento, anaSeq);
		for(MamAnamneses anamnese:lista){
			/*mamp_canc_del_tmp_a
			(r_ana.seq);*/
			Long anaSeqAux = null;
			if(anamnese.getAnamnese()!=null){
				 anaSeqAux = anamnese.getAnamnese().getSeq(); 
			}
			if(anamnese!=null && anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarAnamneseRascunho(anamnese.getSeq(), anaSeqAux);
			} else if(anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarAnamnesePendente(anamnese.getSeq(), anaSeqAux);
			} else if(anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarAnamneseExclusao(anamnese.getSeq());
			}	
		}
	}
	public void cancelarAtendimentoSituacao(Integer conNumero, String nomeMicrocomputador) throws ApplicationBusinessException,GenericJDBCException{
		if(!verificaExtratoPacAtendidoOuFechado(conNumero)){
			this.cancelarControle(conNumero, nomeMicrocomputador);
		}else{
			throw new ApplicationBusinessException(CancelamentoAtendimentoRNExceptionCode.MAM_01195);
		}
	}
	
	public boolean verificaExtratoPacAtendidoOuFechado(Integer conNumero) throws ApplicationBusinessException{
		AghParametros parametroCancelado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_CANCELADO);
		AghParametros parametroFechado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_FECHADO);
		AghParametros parametroConcluido = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_CONCLUIDO);
		
		Short seqCancelado = parametroCancelado.getVlrNumerico().shortValue();
		Short seqFechado = parametroFechado.getVlrNumerico().shortValue();
		Short seqConcluido = parametroConcluido.getVlrNumerico().shortValue();
		
		List<MamControles> listaControle = this.getMamControlesDAO().pesquisarControlePorNumeroConsulta(conNumero);
		for(MamControles controle: listaControle){
			List<MamExtratoControles> listaExtratoControle = this.getMamExtratoControlesDAO().pesquisarExtratoControlePorNumeroControleESituacaoNaoCancelado(controle.getSeq(), seqCancelado);
			for(MamExtratoControles extratoControle: listaExtratoControle){
				if(extratoControle.getSituacaoAtendimento().getSeq().equals(seqFechado) ||
				   extratoControle.getSituacaoAtendimento().getSeq().equals(seqConcluido)){
					return Boolean.TRUE;
				}
			}
		}	
		return Boolean.FALSE;
	}
	/**
	 * Procedure
	 * ORADB MAMP_CANC_CONTROLE
	 * @param conNumero
	 * @param servidor
	 *  
	 */  
	public void cancelarControle(Integer conNumero, String nomeMicrocomputador) throws ApplicationBusinessException,GenericJDBCException {
		
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_CANCELADO);
		Short seqCancelado = parametro.getVlrNumerico().shortValue();
		Short satSeq = null;
		List<MamControles> listaControle = this.getMamControlesDAO().pesquisarControlePorNumeroConsulta(conNumero);
		for(MamControles controle: listaControle){
			List<MamExtratoControles> listaExtratoControle = this.getMamExtratoControlesDAO().pesquisarExtratoControlePorNumeroControleESituacaoNaoCancelado(controle.getSeq(), seqCancelado);
			Integer count = 0;
			for(MamExtratoControles extratoControle: listaExtratoControle){
				count++;
				if(extratoControle.getSituacaoAtendimento()!=null){
					satSeq = extratoControle.getSituacaoAtendimento().getSeq();	
				}
				if(count==2){
					break;
				}
			}
		}
		this.atualizarControleAmbulatorio(conNumero, new Date(), seqCancelado, nomeMicrocomputador);
		if(satSeq!=null){
			this.atualizarControleAmbulatorio(conNumero, new Date(), satSeq, nomeMicrocomputador);	
		}
		this.getFinalizaAtendimentoRN().atualizarSituacaoControle(conNumero, nomeMicrocomputador);
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CANC_EXAMES
	 * @param conNumero
	 * @param soeSeq
	 * @throws BaseException 
	 */
	public void cancelarExames(Integer conNumero, Integer soeSeq, String nomeMicrocomputador) throws BaseException{
		AghParametros parametroPendente = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_PENDENTE);
		String situacaoPendente = parametroPendente.getVlrTexto();
		AghParametros parametroCancelado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
		String situacaoCancelado = parametroCancelado.getVlrTexto();
		Date dthrMovimento;
		if(cancelaTudo){
			dthrMovimento =  null;
		} else {
			dthrMovimento = this.obterDataUltimoMovimentacao(conNumero, null);
		}
		List<AelSolicitacaoExames> listaSolicitacaoExames = this.getExamesFacade().pesquisarSolicitacaoExamePorItemSolicitacaoExameParaCancelamento(conNumero, dthrMovimento, soeSeq);
		for(AelSolicitacaoExames solicitacaoExame: listaSolicitacaoExames){
			List<AelItemSolicitacaoExames> listaItemSolicitacaoExames = this.getExamesFacade().pesquisarItemSolicitacaoExamePorSolicitacaoExame(solicitacaoExame.getSeq());
			for(AelItemSolicitacaoExames item: listaItemSolicitacaoExames){
				if(item.getSituacaoItemSolicitacao()!=null){
					String sitCodigo = item.getSituacaoItemSolicitacao().getCodigo();
					if(!StringUtils.isBlank(sitCodigo)&&sitCodigo.substring(0,2).equals(situacaoPendente)){
						AelSitItemSolicitacoes situacao = this.getExamesFacade().pesquisaSituacaoItemExame(situacaoCancelado);
						item.setSituacaoItemSolicitacao(situacao);
						this.getExamesBeanFacade().atualizarItemSolicitacaoExame(item, nomeMicrocomputador);
					}
				}
			}
		}
	}
	
	/**
	 * ORADB Function C_BUSCA_SITUACAO_PENDENCIA 
	 * 
	 * @param situacao
	 *  
	 */
	public Short buscaSituacaoPendencia(DominioMotivoPendencia situacao) throws ApplicationBusinessException {
		IParametroFacade parametroFacade = getParametroFacade();
		AghParametros parametro = null;
		
		if (DominioMotivoPendencia.EXA.equals(situacao)){
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_EXAMES);
		} else if (DominioMotivoPendencia.PRE.equals(situacao)){
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_PROFE);
		} else if (DominioMotivoPendencia.POS.equals(situacao)){
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_POST);
		} else if (DominioMotivoPendencia.OUT.equals(situacao)){
			parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_OUT);
		}
		if (parametro != null && parametro.getVlrNumerico() != null){
			return parametro.getVlrNumerico().shortValue();
		}
		return null;
	}
	
	/**
	 * ORADB Procedure MAMP_PEND 
	 * 
	 * @param conNumero
	 * @param dthrMvto
	 * @param satSeq
	 *  
	 */
	public void mampPend(Integer conNumero, Date dthrMvto, Short satSeq, String nomeMicrocomputador) throws ApplicationBusinessException {
		
		/* conclusão do atendimento em controles e extratos */
		atualizarControleAmbulatorio(conNumero, dthrMvto, satSeq, nomeMicrocomputador);
		
		/* deixa a anamnese pendente */
		pendenciaAnamnese(conNumero, null);
		
		/* deixa a nota adicional de anamnese pendente */
		pendenciaNotasAnamnese(conNumero, null);
		
		/* deixa a nota adicional de evolucao pendente */
		pendenciaNotasEvolucao(conNumero, null);
		
		/* deixa a evolucao pendente */
		pendenciaEvolucao(conNumero, null);
		
		/* deixa o receita pendente */
		pendenciaReceituario(conNumero, null);
		
		/* deixa os procedimentos pendentes */
		pendenciaProcedimento(conNumero, null);
		
		/* atualiza o controle */
		pendenciaControle(conNumero);
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_ATU_CONTROLE
	 * @param conNumero
	 * @param dthrMovimento
	 * @param satSeq
	 *  
	 */
	public void atualizarControleAmbulatorio(Integer conNumero, Date dthrMovimento, Short satSeq, String nomeMicrocomputador) throws ApplicationBusinessException, GenericJDBCException {
		List<MamControles> listaControle = this.getMamControlesDAO().pesquisarControlePorNumeroConsulta(conNumero);
		for(MamControles controle: listaControle){
			controle.setDthrMovimento(dthrMovimento);
			MamSituacaoAtendimentos situacaoAtendimento = this.getMamSituacoAtendimentosDAO().obterPorChavePrimaria(satSeq);
			controle.setSituacaoAtendimento(situacaoAtendimento);
			try{
				this.getMarcacaoConsultaRN().atualizarControles(controle, nomeMicrocomputador);	
			} catch(Exception e){
				super.logError(e);
			}
			Short excSeqP = null;
			excSeqP = this.getMarcacaoConsultaRN().obterExtratoControleSeqP(controle);
			MamExtratoControles extratoControle = new MamExtratoControles();
			extratoControle.setControle(controle);
			MamExtratoControlesId id = new MamExtratoControlesId();
			id.setCtlSeq(controle.getSeq());
			id.setSeqp(excSeqP);
			extratoControle.setId(id);
			extratoControle.setDthrMovimento(dthrMovimento);
			extratoControle.setSituacaoAtendimento(situacaoAtendimento);
			try{
				this.getMarcacaoConsultaRN().inserirExtratoControles(extratoControle, nomeMicrocomputador);	
				controle.getExtratoControles().add(extratoControle);
			} catch(Exception e){
				super.logError(e);
			}	
		}
	}
	
	/**
	 * ORADB Procedure MAMP_PEND_ANAMNESE 
	 * 
	 * @param conNumero
	 * @param anaSeq
	 */
	public void pendenciaAnamnese(Integer conNumero, Long anaSeq) {
		MamAnamnesesDAO dao = getMamAnamnesesDAO();
		List<MamAnamneses> listaAnamnese = dao.pesquisarAnamnesePorNumeroSeqEPendencia(conNumero, anaSeq, DominioIndPendenteAmbulatorio.R);
		for (MamAnamneses anamnese : listaAnamnese) {
			if (anamnese.getAnamnese() != null) {
				MamAnamneses elemento = dao.obterPorChavePrimaria(anamnese.getAnamnese().getSeq());
				if (elemento != null) { 
					elemento.setDthrMvto(null);
					elemento.setServidorMvto(null);
					dao.atualizar(elemento);
					dao.flush();
				}
			}
			
			List<MamAnamneses> listaExclusao = dao.pesquisarAnamnesePorAnaSeq(anamnese.getSeq());
			for (MamAnamneses elemento : listaExclusao) {
				dao.remover(elemento);
				dao.flush();
			}
			
			dao.remover(anamnese);
			dao.flush();
		}
	}
	
	
	/**
	 * ORADB Procedure MAMP_PEND_NOTAS_ANA 
	 * 
	 * @param conNumero
	 * @param dthrMovimento
	 * @param naaSeq
	 */
	public void pendenciaNotasAnamnese(Integer conNumero, Long naaSeq) {
		MamNotaAdicionalAnamnesesDAO dao = getMamNotaAdicionalAnamnesesDAO(); 
		List<MamNotaAdicionalAnamneses> listaAnamnese = dao.pesquisarNotaAdicionalAnamnesePorNumeroSeqEPendencia(conNumero, naaSeq, DominioIndPendenteAmbulatorio.R);
		for (MamNotaAdicionalAnamneses anamnese : listaAnamnese) {
			if (anamnese.getNotaAdicionalAnamnese() != null) {
				MamNotaAdicionalAnamneses elemento = dao.obterPorChavePrimaria(anamnese.getNotaAdicionalAnamnese().getSeq());
				if (elemento != null) { 
					elemento.setDthrMvto(null);
					elemento.setServidorMvto(null);
					dao.atualizar(elemento);
					dao.flush();
				}
			}
			
			dao.remover(anamnese);
			dao.flush();
		}
	}
	
	/**
	 * ORADB Procedure MAMP_PEND_EVOLUCAO 
	 * 
	 * @param conNumero
	 * @param evoSeq
	 */
	public void pendenciaEvolucao(Integer conNumero, Long evoSeq) {
		MamEvolucoesDAO dao = getMamEvolucoesDAO();
		List<MamEvolucoes> listaEvolucoes = dao.pesquisarEvolucoesPorNumeroSeqEPendencia(conNumero, evoSeq, DominioIndPendenteAmbulatorio.R);
		for (MamEvolucoes evolucao : listaEvolucoes) {
			if (evolucao.getEvolucao() != null) {
				MamEvolucoes elemento = dao.obterPorChavePrimaria(evolucao.getEvolucao().getSeq());
				if (elemento != null) { 
					elemento.setDthrMvto(null);
					elemento.setServidorMvto(null);
					dao.atualizar(elemento);
					dao.flush();
				}
			}
			
			List<MamEvolucoes> listaExclusao = dao.pesquisarMamEvolucoesPorEvoSeq(evolucao.getSeq());
			for (MamEvolucoes elemento : listaExclusao) {
				dao.remover(elemento);
				dao.flush();
			}
			
			dao.remover(evolucao);
			dao.flush();
		}
	}
	
	/**
	 * ORADB Procedure MAMP_PEND_NOTAS_EVO 
	 * 
	 * @param conNumero
	 * @param nevSeq
	 */
	public void pendenciaNotasEvolucao(Integer conNumero, Long nevSeq) {
		MamNotaAdicionalEvolucoesDAO dao = getMamNotaAdicionalEvolucoesDAO(); 
		List<MamNotaAdicionalEvolucoes> listaNotasEvolucao = dao.pesquisarNotaAdicionalEvolucoesPorNumeroSeqEPendencia(conNumero, nevSeq, DominioIndPendenteAmbulatorio.R);
		for (MamNotaAdicionalEvolucoes notas : listaNotasEvolucao) {
			if (notas.getNotaAdicionalEvolucao() != null) {
				MamNotaAdicionalEvolucoes elemento = dao.obterPorChavePrimaria(notas.getNotaAdicionalEvolucao().getSeq());
				if (elemento != null) { 
					elemento.setDthrMvto(null);
					elemento.setServidorMvto(null);
					dao.atualizar(elemento);
					dao.flush();
				}
			}
			
			dao.remover(notas);
			dao.flush();
		}
	}
		
	/**
	 * ORADB Procedure MAMP_PEND_PROCED 
	 * 
	 * @param conNumero
	 * @param polSeq
	 */
	public void pendenciaProcedimento(Integer conNumero, Long polSeq) {
		MamProcedimentoRealizadoDAO dao = getMamProcedimentoRealizadoDAO(); 
		List<MamProcedimentoRealizado> listaProcedimento = dao.pesquisarPendenciaProcedimentoRealizadoPorNumeroSeqEPendencia(conNumero, polSeq, DominioIndPendenteAmbulatorio.R);
		for (MamProcedimentoRealizado procedimento : listaProcedimento) {
			if (procedimento.getProcedimentoRealizado() != null) {
				MamProcedimentoRealizado elemento = dao.obterPorChavePrimaria(procedimento.getProcedimentoRealizado().getSeq());
				if (elemento != null) { 
					elemento.setDthrMovimento(null);
					elemento.setServidorMovimento(null);
					dao.atualizar(elemento);
					dao.flush();
				}
			}
			
			dao.remover(procedimento);
			dao.flush();
		}
	}
	
	/**
	 * ORADB Procedure MAMP_PEND_RECEITA 
	 * 
	 * @param conNumero
	 * @param rctSeq
	 */
	public void pendenciaReceituario(Integer conNumero, Long rctSeq) {
		MamReceituariosDAO dao = getMamReceituariosDAO(); 
		List<MamReceituarios> listaReceituario = dao.pesquisarReceituarioPorNumeroSeqEPendencia(conNumero, rctSeq, DominioIndPendenteAmbulatorio.R);
		for (MamReceituarios receituario : listaReceituario) {
			if (receituario.getReceituario() != null) {
				MamReceituarios elemento = dao.obterPorChavePrimaria(receituario.getReceituario().getSeq());
				if (elemento != null) { 
					elemento.setDthrMvto(null);
					elemento.setServidorMovimento(null);
					dao.atualizar(elemento);
					dao.flush();
				}
			}
			
			dao.remover(receituario);
			dao.flush();
		}
	}
	
	/**
	 * ORADB Procedure MAMP_PEND_CONTROLE 
	 * 
	 * @param conNumero
	 */
	public void pendenciaControle(Integer conNumero) {
		MamControlesDAO dao = getMamControlesDAO();
		List<MamControles> listaConstroles = dao.pesquisarControlesPorNumeroConsultaESituacao(conNumero);
		for (MamControles elemento : listaConstroles) { 
			elemento.setSituacao(DominioSituacaoControle.L);
			dao.atualizar(elemento);
			dao.flush();
		}
	}
		
	public Boolean getCancelaTudo() {
		return cancelaTudo;
	}

	public void setCancelaTudo(Boolean cancelaTudo) {
		this.cancelaTudo = cancelaTudo;
	}
	
	protected MamLogEmUsosDAO getMamLogEmUsosDAO(){
		return mamLogEmUsosDAO;
	}
	
	protected MamNotaAdicionalAnamnesesDAO getMamNotaAdicionalAnamnesesDAO(){
		return mamNotaAdicionalAnamnesesDAO;
	}	
	
	protected MamProcedimentoRealizadoDAO getMamProcedimentoRealizadoDAO(){
		return mamProcedimentoRealizadoDAO;
	}	
	
	protected MamNotaAdicionalEvolucoesDAO getMamNotaAdicionalEvolucoesDAO(){
		return mamNotaAdicionalEvolucoesDAO;
	}
	
	protected MamItemReceituarioDAO getMamItemReceituarioDAO(){
		return mamItemReceituarioDAO;
	}
	
	protected MamEvolucoesDAO getMamEvolucoesDAO(){
		return mamEvolucoesDAO;
	}
	
	protected MamItemEvolucoesDAO getMamItemEvolucoesDAO(){
		return mamItemEvolucoesDAO;
	}
	
	protected MamItemAnamnesesDAO getMamItemAnamnesesDAO(){
		return mamItemAnamnesesDAO;
	}
	
	protected MamReceituariosDAO getMamReceituariosDAO(){
		return mamReceituariosDAO;
	}
	
	protected MamAnamnesesDAO getMamAnamnesesDAO(){
		return mamAnamnesesDAO;
	}
	
	protected MamControlesDAO getMamControlesDAO(){
		return mamControlesDAO;
	}
	
	protected MamSituacaoAtendimentosDAO getMamSituacoAtendimentosDAO(){
		return mamSituacaoAtendimentosDAO;
	}
	
	protected MamExtratoControlesDAO getMamExtratoControlesDAO(){
		return mamExtratoControlesDAO;
	}
	
	protected MarcacaoConsultaRN getMarcacaoConsultaRN(){
		return marcacaoConsultaRN;
	}
	
	protected ProcedimentoAtendimentoConsultaRN getProcedimentoAtendimentoConsultaRN(){
		return procedimentoAtendimentoConsultaRN;
	}
	
	protected FinalizaAtendimentoRN getFinalizaAtendimentoRN(){
		return finalizaAtendimentoRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IExamesBeanFacade getExamesBeanFacade() {
		return this.examesBeanFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
