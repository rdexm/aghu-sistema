package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.GenericJDBCException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.AtendimentoPacientesAgendadosON.AtendimentoPacientesAgendadosONExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultaProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamExtratoControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemReceituarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.dao.VMamProcXCidDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalarId;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamItemAnamneses;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class FinalizaAtendimentoRN extends BaseBusiness {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	@EJB
	private ProcedimentoConsultaRN procedimentoConsultaRN;
	
	@EJB
	private ProcedimentoConsultaON procedimentoConsultaON;
	
	@EJB
	private ProcedimentoAtendimentoConsultaRN procedimentoAtendimentoConsultaRN;
	
	@EJB
	private CancelamentoAtendimentoRN cancelamentoAtendimentoRN;
	
	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;

	private static final Log LOG = LogFactory.getLog(FinalizaAtendimentoRN.class);
	
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
	
	@Inject
	private VMamProcXCidDAO vMamProcXCidDAO;
	
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
	
	@EJB
	private IExamesBeanFacade examesBeanFacade;
	
	@Inject
	private MamExtratoControlesDAO mamExtratoControlesDAO;
	
	@Inject
	private MamProcedimentoDAO mamProcedimentoDAO;
	
	@Inject
	private MamItemAnamnesesDAO mamItemAnamnesesDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject
	private AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MamNotaAdicionalAnamnesesDAO mamNotaAdicionalAnamnesesDAO;
	
	@Inject
	private MamProcedimentoRealizadoDAO mamProcedimentoRealizadoDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -5246338962781986991L;
	
	private static final Byte VALOR_PADRAO_QUANTIDADE_CONSULTA = Byte.valueOf("1");

	private enum FinalizaAtendimentoRNExceptionCode implements BusinessExceptionCode {
		MAM_00995, MAM_00996,MAM_00997, MAM_00998, MAM_01000, MAM_01001, MAM_01002, MAM_01003, MAM_01004, MAM_01005,MAM_00967,
		MAM_00968, MAM_00970, MAM_00971, MAM_00972, MAM_00973, MAM_00975, MAM_00901, MAM_00902, MAM_00903, MAM_00904,
		ERRO_CLONE_NOTA_ADICIONAL_ANAMNESES, ERRO_CLONE_NOTA_ADICIONAL_EVOLUCAO, ERRO_CLONE_PROCEDIMENTO_REALIZADO, MAM_00895,
		MAM_00840, MAM_00841, MAM_00842, MAM_00843, MAM_00844, MAM_00633, MAM_00258, MAM_00260, MAM_00261, MAM_00263, MAM_00272,
		MAM_00268, MAM_01201, MAM_01759, MAM_00574,ERRO_ATENDER,ERRO_ATENDER_SITUACAO;
		
		public void throwException(Object... params)
			throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
		
	}
	private void atualizaControleAoConcluirAtend(Integer conNumero,Date dataMovimento,String nomeMicrocomputador) throws BaseException,GenericJDBCException{
		AghParametros parametroConcluido = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_CONCLUIDO);
		Short seqConcluido = null;
		if(parametroConcluido!=null){
			seqConcluido = parametroConcluido.getVlrNumerico().shortValue();
		}
		AghParametros parametroFechado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_FECHADO);
		Short seqFechado = null;
		if(parametroFechado!=null){
			seqFechado = parametroFechado.getVlrNumerico().shortValue();
		}
		Long count = this.getMamExtratoControlesDAO().pesquisarExtratoControlePorNumeroConSultaESituacaoConcluidoCount(conNumero, seqConcluido);
		if(count!=null && count>0){
			this.getCancelamentoAtendimentoRN().atualizarControleAmbulatorio(conNumero, dataMovimento, seqFechado, nomeMicrocomputador);
			this.atualizarSituacaoControle(conNumero, nomeMicrocomputador);
		} else {
			this.getCancelamentoAtendimentoRN().atualizarControleAmbulatorio(conNumero, dataMovimento, seqConcluido, nomeMicrocomputador);
			this.atualizarSituacaoControle(conNumero, nomeMicrocomputador);
		}
	}
	public void concluirAtendimento(Integer conNumero,Date dataMovimento,String nomeMicrocomputador,final Date dataFimVinculoServidor) throws BaseException,GenericJDBCException{
		List<MamControles> controles = this.getMamControlesDAO().pesquisarControlePorNumeroConsulta(conNumero);
		
		if(controles.size() == 0){
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_ATENDER_SITUACAO);
		}
		
		for(MamControles controle : controles){			
			if(controle != null && (controle.getSituacaoAtendimento().getPacAtend() || controle.getSituacaoAtendimento().getAtendReaberto())){
				this.atualizaControleAoConcluirAtend(conNumero, dataMovimento, nomeMicrocomputador);
				this.atualizarDataFimAtendimento(conNumero, dataMovimento, nomeMicrocomputador, dataFimVinculoServidor);
				this.flush();
			}else{
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_ATENDER_SITUACAO);
			}
		}
	}
	/**
	 * Procedure
	 * 
	 * ORADB MAMP_CONC
	 * @throws BaseException 
	 * 
	 */
	//TODO trechos de codigo foram comentados por nao fazerem parte do escopo da estoria 6220, devem ser implementados quando necessários
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void concluirAtendimento(AacConsultas consulta, Date dataMovimento, Short vinCodigo, Integer matricula, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException,GenericJDBCException{
/*		 Faz a conclusão do atendimento ambulatorial 
		PROCEDURE MAMP_CONC
		 (P_CON_NUMERO IN NUMBER
		 ,P_DTHR_MVTO IN DATE
		 ,P_SER_VIN_CODIGO IN NUMBER
		 ,P_SER_MATRICULA IN NUMBER
		 )
		 IS
		BEGIN
		DECLARE
		--
		regra_negocio EXCEPTION;
		PRAGMA EXCEPTION_INIT(regra_negocio, -20000);
		--
		v_ser_vin_codigo		rap_servidores.vin_codigo%type;
		v_ser_matricula		rap_servidores.matricula%type;
		--
		v_vlr_data      DATE;
		v_vlr_texto     VARCHAR2(2000);
		v_msg           VARCHAR2(2000);
		v_seq_concluido NUMBER;
		v_seq_fechado NUMBER;
		--
		CURSOR c_concluido
			(c_con_numero agh_atendimentos.con_numero%type,
		       c_sit_conc   mam_situacao_atendimentos.seq%type)
		IS
		SELECT 1
		FROM mam_controles ctl,
		     mam_extrato_controles exc
		WHERE ctl.con_numero = c_con_numero
		  AND ctl.seq        = exc.ctl_seq
		  AND exc.sat_seq+0  = c_sit_conc;
		--
		v_concluido NUMBER;
		--*/

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(vinCodigo==null || matricula==null){
			RapServidores servidor = servidorLogado;
			if(servidor!=null && servidor.getId()!=null){
				vinCodigo = servidor.getId().getVinCodigo();
				matricula = servidor.getId().getMatricula();
			}
		}
		atualizaControleAoConcluirAtend(consulta.getNumero(), dataMovimento, nomeMicrocomputador);
	/*   AghParametros parametroConcluido = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_CONCLUIDO);
		Short seqConcluido = null;
		if(parametroConcluido!=null){
			seqConcluido = parametroConcluido.getVlrNumerico().shortValue();
		}
		AghParametros parametroFechado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_FECHADO);
		Short seqFechado = null;
		if(parametroFechado!=null){
			seqFechado = parametroFechado.getVlrNumerico().shortValue();
		}
		Long count = this.getMamExtratoControlesDAO().pesquisarExtratoControlePorNumeroConSultaESituacaoConcluidoCount(consulta.getNumero(), seqConcluido);
		if(count!=null && count>0){
			this.getCancelamentoAtendimentoRN().atualizarControleAmbulatorio(consulta.getNumero(), dataMovimento, seqFechado, nomeMicrocomputador);
			this.atualizarSituacaoControle(consulta.getNumero(), nomeMicrocomputador);
		} else {
			this.getCancelamentoAtendimentoRN().atualizarControleAmbulatorio(consulta.getNumero(), dataMovimento, seqConcluido, nomeMicrocomputador);
			this.atualizarSituacaoControle(consulta.getNumero(), nomeMicrocomputador);
		}*/
		
		/*
		
		BEGIN
		   mamp_conc_fig_ana
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01926 #1'||SQLERRM);
		END;
		*/
		
		this.concluirAnamnese(consulta.getNumero(), dataMovimento, vinCodigo, matricula, null);
		this.concluirNotasAdicionaisAnamnese(consulta.getNumero(), dataMovimento,  vinCodigo, matricula, null);
		
		this.concluirEvolucao(consulta.getNumero(), dataMovimento, vinCodigo, matricula, null);
		this.concluirNotasAdicionaisEvolucao(consulta.getNumero(), dataMovimento, vinCodigo, matricula, null);
		
		/*
		-- concluir figuras da evolução
		BEGIN
		  mamp_conc_fig_evo
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01929 #1'||SQLERRM);
		END;
		--
		-- concluir o relatorio
		--
		BEGIN
		  mamp_conc_relatorio
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01930 #1'||SQLERRM);
		END;
		--*/
		this.concluirLimpaReceituarios(consulta.getNumero());
		this.concluirReceita(consulta.getNumero(), dataMovimento, vinCodigo, matricula, null);
		/*
		-- deleta os cuidados sem itens de cuidado
		--
		BEGIN
		  mamp_conc_limpa_cuid(p_con_numero);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01945 #1'||SQLERRM);
		END;
		--
		-- concluir cuidados
		--
		BEGIN
		  mamp_conc_cuidado
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01933 #1'||SQLERRM);
		END;
		--
		-- concluir os atestados
		--
		BEGIN
		  mamp_conc_atestado
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01934 #1'||SQLERRM);
		END;
		--
		-- concluir os diagnosticos
		--
		BEGIN
		  mamp_conc_dia
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01938 #1'||SQLERRM);
		END;
		--
		-- concluir os procedimentos
		--*/
		this.concluirProcedimento(consulta.getNumero(), dataMovimento, vinCodigo, matricula, null);
		this.concluirFaturamento(consulta.getNumero(), nomeMicrocomputador, dataFimVinculoServidor);
		/*
		-- concluir o motivo atendimento
		--
		BEGIN
		  mamp_conc_motivo
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01936 #1'||SQLERRM);
		END;
		--
		-- concluir os medicamentos ativos
		--
		BEGIN
		  mamp_conc_mav
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01937 #1'||SQLERRM);
		END;
		--
		-- concluir AS interconsultas
		--
		BEGIN
		  mamp_conc_intercon
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01939 #1'||SQLERRM);
		END;
		--
		-- concluir AS solicitações de retorno
		--
		BEGIN
		  mamp_conc_solic_ret
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01940 #1'||SQLERRM);
		END;
		--
		-- concluir os laudos de aih
		--
		BEGIN
		  mamp_conc_laudo_aih
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01941 #1'||SQLERRM);
		END;
		--
		-- concluir a alta ambulatorial
		--
		BEGIN
		  mamp_conc_alta
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01942 #1'||SQLERRM);
		END;
		--
		-- concluir a solicitacao de exames
		--*/
		this.concluirExames(consulta.getNumero(), null, nomeMicrocomputador);
		/*
		-- concluir solic hemoterapica
		--
		BEGIN
		  mamp_conc_solic_hemo
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01944 #1'||SQLERRM);
		END;
		--
		-- concluir solic procedimentos
		--
		BEGIN
		  mamp_conc_solic_proc
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-02422 #1'||SQLERRM);
		END;
		--
		-- concluir lembretes
		--
		BEGIN
		  mamp_conc_lembrete
			(p_con_numero,
			 p_dthr_mvto,
			 v_ser_vin_codigo,
			 v_ser_matricula,
			 NULL);
		EXCEPTION
		  WHEN regra_negocio THEN
		    RAISE;
		  WHEN OTHERS THEN
		    raise_application_error (-20000, 'MAM-01944 #1'||SQLERRM);
		END;
		--
		COMMIT;
		--
		END;
		END MAMP_CONC;*/
		this.atualizarDataFimAtendimento(consulta.getNumero(), new Date(), nomeMicrocomputador, dataFimVinculoServidor);
		this.flush();
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB AACP_ATU_DT_FIM_ATEN
	 * @param conNumero
	 * @param dataFim
	 * @throws BaseException 
	 * @throws NumberFormatException 
	 * @throws Exception
	 */
	public void atualizarDataFimAtendimento(Integer conNumero, Date dataFim, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		AacConsultas consulta = this.getAacConsultasDAO().obterPorChavePrimaria(conNumero);
		AacConsultas consultaAnterior = this.getAmbulatorioFacade().clonarConsulta(consulta);
		
		consulta.setDthrFim(dataFim);
		getAmbulatorioConsultaRN().atualizarConsulta(consultaAnterior, consulta, false, nomeMicrocomputador, dataFimVinculoServidor, false, true);
	}
	
	
	public void atualizarSituacaoControle(Integer conNumero, String nomeMicrocomputador) {
		List<MamControles> listaControle = this.getMamControlesDAO().pesquisarControlePorNumeroConsulta(conNumero);
		for(MamControles controle: listaControle){
			try{
				if(controle.getSituacao()!=null && controle.getSituacao().equals(DominioSituacaoControle.U)){
					controle.setSituacao(DominioSituacaoControle.L);	
					this.getMarcacaoConsultaRN().atualizarControles(controle, nomeMicrocomputador);	
				} 
			}
			catch(Exception e){
				super.logError(e);
			}	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CONC_FATURAM
	 * @param conNumero
	 *  
	 * @throws ApplicationBusinessException 
	 */
	//TODO fazer chamada para MAMP_INS_PROCED_FAT quando a mesma for implementada
	public void concluirFaturamento(Integer conNumero, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		AghParametros parametroIntegraFaturamento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_INTEGRA_FATURAMENTO);
		if(parametroIntegraFaturamento!=null && parametroIntegraFaturamento.getVlrTexto().equals(DominioSimNao.S.toString().substring(0, 1))){
			this.inserirProcedimentoFaturamento(conNumero, nomeMicrocomputador, dataFimVinculoServidor);
			this.removerProcedimentoFaturamento(conNumero, nomeMicrocomputador, dataFimVinculoServidor); // Remove da consulta os procedimentos não realizados (desmarcados)
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_INS_PROCED_FAT
	 * @param conNumero
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void inserirProcedimentoFaturamento(Integer conNumero, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		List<MamProcedimentoRealizado> listaProcedimentoRealizado = this.getMamProcedimentoRealizadoDAO().pesquisarProcedimentoRealizadoPorConsultaFaturamento(conNumero);
		ProcedimentoConsultaRN procedimentoConsultaRN = this.getProcedimentoConsultaRN();
		ProcedimentoConsultaON procedimentoConsultaON = this.getProcedimentoConsultaON();
		for(MamProcedimentoRealizado procedimentoRealizado: listaProcedimentoRealizado){
			AacConsultaProcedHospitalarId id = new AacConsultaProcedHospitalarId();
				id.setConNumero(conNumero);
				
				FatProcedHospInternos procedHospInterno = null;
				if(procedimentoRealizado!=null && procedimentoRealizado.getProcedimento()!=null && procedimentoRealizado.getProcedimento().getProcedEspecialDiverso()!=null
					&& procedimentoRealizado.getProcedimento().getProcedEspecialDiverso().getProcedHospInterno()!=null){
					procedHospInterno = procedimentoRealizado.getProcedimento().getProcedEspecialDiverso().getProcedHospInterno();	
				}
				id.setPhiSeq(procedHospInterno.getSeq());
				AacConsultaProcedHospitalar consultaProcedHospitalar = null;
				AacConsultaProcedHospitalar consultaProcedHospitalarOld = null;
				if(id!=null){
					consultaProcedHospitalar = this.getAacConsultaProcedHospitalarDAO().obterPorChavePrimaria(id);	
				}
				if(consultaProcedHospitalar==null){
					AacConsultas consulta = getAacConsultasDAO().obterConsulta(conNumero);
					AacConsultaProcedHospitalar consultaProcedHospitalarNew = new AacConsultaProcedHospitalar();
					AacConsultaProcedHospitalarId consultaProcedHospitalarId = new AacConsultaProcedHospitalarId();
					consultaProcedHospitalarId.setConNumero(conNumero);
					consultaProcedHospitalarId.setPhiSeq(procedHospInterno.getSeq());
					consultaProcedHospitalarNew.setId(consultaProcedHospitalarId);
					consultaProcedHospitalarNew.setConsulta(false);
					consultaProcedHospitalarNew.setConsultas(consulta);
					consultaProcedHospitalarNew.setProcedHospInterno(procedHospInterno);
					if(procedimentoRealizado.getQuantidade()!=null){
						consultaProcedHospitalarNew.setQuantidade(procedimentoRealizado.getQuantidade());
					} else{
						consultaProcedHospitalarNew.setQuantidade(VALOR_PADRAO_QUANTIDADE_CONSULTA);
					}
					consultaProcedHospitalarNew.setCid(procedimentoRealizado.getCid());
					//procedimentoConsultaRN.inserirProcedimentoConsulta(consultaProcedHospitalarNew, false, nomeMicrocomputador, dataFimVinculoServidor);
					procedimentoConsultaRN.inserirProcedimentoConsulta(consultaProcedHospitalarNew, false, nomeMicrocomputador, dataFimVinculoServidor, false, false, false);
				} else {
					consultaProcedHospitalarOld = procedimentoConsultaON.clonarConsultaProcedHospitalar(consultaProcedHospitalar);
					if(procedimentoRealizado.getQuantidade()!=null){
						consultaProcedHospitalar.setQuantidade(procedimentoRealizado.getQuantidade());
					} else{
						consultaProcedHospitalar.setQuantidade(VALOR_PADRAO_QUANTIDADE_CONSULTA);
					}
					consultaProcedHospitalar.setCid(procedimentoRealizado.getCid());
					procedimentoConsultaRN.atualizarConsultaProcedimentoHospitalar(consultaProcedHospitalarOld, consultaProcedHospitalar, nomeMicrocomputador, dataFimVinculoServidor);
				}
		}
	}
	
	/**
	 * Remove da tabela AacConsultaProcedHospitalar 
	 * (e consequentemente da FatProcedAmbRealizado) os procedimentos 
	 * que foram desmarcados no atendimento médico.
	 * Essa implementação compreende a Evolução #14159.
	 * 
	 * @param conNumero
	 */
	public void removerProcedimentoFaturamento(Integer conNumero,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		
		ProcedimentoConsultaON procedimentoConsultaON = getProcedimentoConsultaON();
		
		List<AacConsultaProcedHospitalar> listaConsultaProcedHospitalar = this.getAacConsultaProcedHospitalarDAO().
				buscarConsultaProcedHospPorNumeroConsultaIndicadorConsultaNao(conNumero);
		
		List<MamProcedimentoRealizado> listaProcedimentoRealizado = this.getMamProcedimentoRealizadoDAO()
				.pesquisarProcedimentoRealizadoPorConsultaFaturamento(conNumero);
		
		for (AacConsultaProcedHospitalar consultaProcedHospitalar : listaConsultaProcedHospitalar) {
			FatProcedHospInternos procedHospInterno = consultaProcedHospitalar.getProcedHospInterno();
			Boolean ehProcedimentoRealizado = false;
			for (MamProcedimentoRealizado procedimentoRealizado : listaProcedimentoRealizado) {
				MpmProcedEspecialDiversos procedEspecialDiverso = 
						procedimentoRealizado.getProcedimento().getProcedEspecialDiverso();
				if (procedEspecialDiverso != null && procedEspecialDiverso.getProcedHospInterno().equals(procedHospInterno)) {
					ehProcedimentoRealizado = true;
					break;
				}
			}
			if (!ehProcedimentoRealizado) {
				procedimentoConsultaON.removerProcedimentoConsulta(
						consultaProcedHospitalar, nomeMicrocomputador, dataFimVinculoServidor);					
			}
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CONC_NOTAS_ANA
	 *  
	 */
	public void concluirNotasAdicionaisAnamnese(Integer conNumero, Date dataMovimento, Short vinCodigo, Integer matricula, Integer naaSeq) throws ApplicationBusinessException {
		List<MamNotaAdicionalAnamneses> lista = this.getMamNotaAdicionalAnamnesesDAO().pesquisarNotaAdicionalAnamnesesParaConclusao(conNumero, naaSeq);
		RapServidoresId servidorId =  new RapServidoresId();
		servidorId.setMatricula(matricula);
		servidorId.setVinCodigo(vinCodigo);
		RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(servidorId);
		for(MamNotaAdicionalAnamneses notaAdicionalAnamneses: lista){
			Integer naaSeqAux = null;
			if(notaAdicionalAnamneses.getNotaAdicionalAnamnese()!=null){
				 naaSeqAux = notaAdicionalAnamneses.getNotaAdicionalAnamnese().getSeq(); 
			}
			if(notaAdicionalAnamneses.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarNotaAdicionalAnamneseRascunho(notaAdicionalAnamneses.getSeq(), naaSeqAux);
			} else if (notaAdicionalAnamneses.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarNotaAdicionalAnamnesePendente(notaAdicionalAnamneses.getSeq(), naaSeqAux, dataMovimento, servidor);
			}  else if (notaAdicionalAnamneses.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarNotaAdicionalAnamneseExclusao(notaAdicionalAnamneses.getSeq(), dataMovimento, servidor);
			}
		}
	}
	
	
	public void tratarNotaAdicionalAnamneseRascunho(Integer seq, Integer naaSeq) throws ApplicationBusinessException{
		try{
			MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(seq);
			this.getMarcacaoConsultaRN().removerNotaAdicionalAnamneses(notaAdicionalAnamneses);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00995);
		}
		if(naaSeq!=null){
			MamNotaAdicionalAnamneses notaAdicionalAnamnesesOld = null;
			MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(naaSeq);
			try{
				notaAdicionalAnamnesesOld = (MamNotaAdicionalAnamneses) BeanUtils.cloneBean(notaAdicionalAnamneses);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_ANAMNESES);
			}
			notaAdicionalAnamneses.setDthrMvto(null);
			notaAdicionalAnamneses.setServidorMvto(null);
			try{
				this.getMarcacaoConsultaRN().atualizarNotaAdicionalAnamneses(notaAdicionalAnamneses, notaAdicionalAnamnesesOld);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00996);	
			}
		}
	}
	
	public void tratarNotaAdicionalAnamnesePendente(Integer seq, Integer naaSeq, Date dataMovimento, RapServidores servidor) throws ApplicationBusinessException{
		try{
			MamNotaAdicionalAnamneses notaAdicionalAnamneseOld = null;
			MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(seq);
			try{
				notaAdicionalAnamneseOld = (MamNotaAdicionalAnamneses) BeanUtils.cloneBean(notaAdicionalAnamneses);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_ANAMNESES);
			}
			notaAdicionalAnamneses.setPendente(DominioIndPendenteAmbulatorio.V);
			notaAdicionalAnamneses.setDthrValida(dataMovimento);
			notaAdicionalAnamneses.setServidorValida(servidor);
			this.getMarcacaoConsultaRN().atualizarNotaAdicionalAnamneses(notaAdicionalAnamneses, notaAdicionalAnamneseOld);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00997);
		}
		if(naaSeq!=null){
			MamNotaAdicionalAnamneses notaAdicionalAnamneseOld = null;
			MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(naaSeq);
			try{
				notaAdicionalAnamneseOld = (MamNotaAdicionalAnamneses) BeanUtils.cloneBean(notaAdicionalAnamneses);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_ANAMNESES);
			}
			notaAdicionalAnamneses.setDthrValidaMvto(dataMovimento);
			notaAdicionalAnamneses.setServidorValidaMvto(servidor);
			notaAdicionalAnamneses.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getMarcacaoConsultaRN().atualizarNotaAdicionalAnamneses(notaAdicionalAnamneses, notaAdicionalAnamneseOld);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00998);	
			}
		}
	}
	
	public void tratarNotaAdicionalAnamneseExclusao(Integer seq, Date dataMovimento, RapServidores servidor) throws ApplicationBusinessException{
		MamNotaAdicionalAnamneses notaAdicionalAnamneseOld = null;
		MamNotaAdicionalAnamneses notaAdicionalAnamneses = this.getMamNotaAdicionalAnamnesesDAO().obterPorChavePrimaria(seq);
		try{
			notaAdicionalAnamneseOld = (MamNotaAdicionalAnamneses) BeanUtils.cloneBean(notaAdicionalAnamneses);	
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_ANAMNESES);
		}
		notaAdicionalAnamneses.setDthrValidaMvto(dataMovimento);
		notaAdicionalAnamneses.setServidorValidaMvto(servidor);
		notaAdicionalAnamneses.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getMarcacaoConsultaRN().atualizarNotaAdicionalAnamneses(notaAdicionalAnamneses, notaAdicionalAnamneseOld);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_01000);	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CONC_NOTAS_EVO
	 */
	public void concluirNotasAdicionaisEvolucao(Integer conNumero, Date dataMovimento, Short vinCodigo, Integer matricula, Integer nevSeq) throws ApplicationBusinessException{
		List<MamNotaAdicionalEvolucoes> lista = this.getMamNotaAdicionalEvolucoesDAO().pesquisarNotaAdicionalEvolucaoParaConclusao(conNumero, nevSeq);
		RapServidoresId servidorId =  new RapServidoresId();
		servidorId.setMatricula(matricula);
		servidorId.setVinCodigo(vinCodigo);
		RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(servidorId);
		for(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes: lista){
			Integer nevSeqAux = null;
			if(notaAdicionalEvolucoes.getNotaAdicionalEvolucao()!=null){
				 nevSeqAux = notaAdicionalEvolucoes.getNotaAdicionalEvolucao().getSeq(); 
			}
			if(notaAdicionalEvolucoes.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarNotaAdicionalEvolucaoRascunho(notaAdicionalEvolucoes.getSeq(), nevSeqAux);
			} else if (notaAdicionalEvolucoes.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarNotaAdicionalEvolucaoPendente(notaAdicionalEvolucoes.getSeq(), nevSeqAux, dataMovimento, servidor);
			}  else if (notaAdicionalEvolucoes.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarNotaAdicionalEvolucaoExclusao(notaAdicionalEvolucoes.getSeq(), dataMovimento, servidor);
			}
		}
	}
	
	
	public void tratarNotaAdicionalEvolucaoRascunho(Integer seq, Integer nevSeq) throws ApplicationBusinessException{
		try{
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoes = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(seq);
			this.getMarcacaoConsultaRN().removerNotaAdicionalEvolucoes(notaAdicionalEvolucoes);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_01001);
		}
		if(nevSeq!=null){
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoesOld = null;
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoes = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(nevSeq);
			try{
				notaAdicionalEvolucoesOld = (MamNotaAdicionalEvolucoes) BeanUtils.cloneBean(notaAdicionalEvolucoes);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_EVOLUCAO);
			}
			notaAdicionalEvolucoes.setDthrMvto(null);
			notaAdicionalEvolucoes.setServidorMvto(null);
			try{
				this.getMarcacaoConsultaRN().atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucoes, notaAdicionalEvolucoesOld);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_01002);	
			}
		}
	}
	
	public void tratarNotaAdicionalEvolucaoPendente(Integer seq, Integer nevSeq, Date dataMovimento, RapServidores servidor) throws ApplicationBusinessException{
		try{
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoesOld = null;
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoes = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(seq);
			try{
				notaAdicionalEvolucoesOld = (MamNotaAdicionalEvolucoes) BeanUtils.cloneBean(notaAdicionalEvolucoes);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_ANAMNESES);
			}
			notaAdicionalEvolucoes.setPendente(DominioIndPendenteAmbulatorio.V);
			notaAdicionalEvolucoes.setDthrValida(dataMovimento);
			notaAdicionalEvolucoes.setServidorValida(servidor);
			this.getMarcacaoConsultaRN().atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucoes, notaAdicionalEvolucoesOld);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_01003);
		}
		
		if(nevSeq!=null){
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoesOld = null;
			MamNotaAdicionalEvolucoes notaAdicionalEvolucoes = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(nevSeq);
			try{
				notaAdicionalEvolucoesOld = (MamNotaAdicionalEvolucoes) BeanUtils.cloneBean(notaAdicionalEvolucoes);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_EVOLUCAO);
			}
			notaAdicionalEvolucoes.setDthrValidaMvto(dataMovimento);
			notaAdicionalEvolucoes.setServidorValidaMvto(servidor);
			notaAdicionalEvolucoes.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getMarcacaoConsultaRN().atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucoes, notaAdicionalEvolucoesOld);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_01004);	
			}
		}
	}
	
	public void tratarNotaAdicionalEvolucaoExclusao(Integer seq, Date dataMovimento, RapServidores servidor) throws ApplicationBusinessException{
		MamNotaAdicionalEvolucoes notaAdicionalEvolucaoOld = null;
		MamNotaAdicionalEvolucoes notaAdicionalEvolucoes = this.getMamNotaAdicionalEvolucoesDAO().obterPorChavePrimaria(seq);
		try{
			notaAdicionalEvolucaoOld = (MamNotaAdicionalEvolucoes) BeanUtils.cloneBean(notaAdicionalEvolucoes);	
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_NOTA_ADICIONAL_EVOLUCAO);
		}
		notaAdicionalEvolucoes.setDthrValidaMvto(dataMovimento);
		notaAdicionalEvolucoes.setServidorValidaMvto(servidor);
		notaAdicionalEvolucoes.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getMarcacaoConsultaRN().atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucoes, notaAdicionalEvolucaoOld);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_01005);	
		}
	}
	
	/**
	 * Procedure 
	 * ORADB MAMP_CONC_EVOLUCAO
	 *  
	 */
	//TODO trechos de codigo foram comentados por nao fazerem parte do escopo da estoria 6220, devem ser implementados quando necessários
	public void concluirEvolucao(Integer conNumero, Date dataMovimento, Short vinCodigo, Integer matricula, Long evoSeq) throws ApplicationBusinessException {
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(matricula);
		id.setVinCodigo(vinCodigo);
		RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(id);
		List<MamEvolucoes> lista = this.getMamEvolucoesDAO().pesquisarEvolucaoParaConclusao(conNumero, evoSeq);
		for(MamEvolucoes evolucao:lista){
			//mamp_canc_del_tmp_e(r_evo.seq);
			Long evoSeqAux = null;
			if(evolucao.getEvolucao()!=null){
				 evoSeqAux = evolucao.getEvolucao().getSeq(); 
			}
			if(!evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.limparRespostasEvolucao(evolucao.getSeq());
				this.limparItensEvolucao(evolucao.getSeq());
			}
			if(evolucao!=null && evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarEvolucaoRascunho(evolucao.getSeq(), evoSeqAux);
			} else if(evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarEvolucaoPendente(evolucao.getSeq(), evoSeqAux, dataMovimento, servidor);
			/*	mamp_conc_tmp_evo
				(r_evo.seq,
				 r_evo.evo_seq,
				 p_ser_vin_codigo,
				 p_ser_matricula);*/
			} else if(evolucao.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarEvolucaoExclusao(evolucao.getSeq(), dataMovimento, servidor);
			}	
			 /* mamp_conc_del_tmp_e
				(r_evo.seq);*/
		}
	}
	
	
	
	/**
	 * 
	 */
	//TODO procedure nao foi migrada por nao fazer parte do escopo da estoria 6220, deve ser migrada quando necessária
	private void limparRespostasEvolucao(Long seq){
		/*PROCEDURE p_limpa_respostas
		(t_seq	IN	NUMBER)
	IS
	  --
	  CURSOR cur_rev
		(c_evo_seq		mam_resposta_evolucoes.evo_seq%type)
	  IS
	    SELECT
	      evo_seq,
	      qus_qut_seq,
		qus_seqp,
		seqp
	    FROM
	      mam_resposta_evolucoes
	    WHERE
	      evo_seq = c_evo_seq AND
	      resposta IS NULL AND
	      vvq_qus_qut_seq IS NULL AND
	      vvq_qus_seqp IS NULL AND
	      vvq_seqp IS NULL;
	  --*/
		
		/*
	  BEGIN
	    --
	    FOR r_rev IN cur_rev (t_seq)
	    LOOP
	      --
	      IF mamk_generica.mamc_respondeu_cust
					('E',
					 r_rev.evo_seq,
					 r_rev.qus_qut_seq,
					 r_rev.qus_seqp) = 'N'
	      THEN
	        BEGIN
	          DELETE FROM mam_resposta_evolucoes
	           WHERE evo_seq     = r_rev.evo_seq
	             AND qus_qut_seq = r_rev.qus_qut_seq
	             AND qus_seqp    = r_rev.qus_seqp
	             AND seqp        = r_rev.seqp;
	          EXCEPTION
	             WHEN regra_negocio THEN
	               RAISE;
	             WHEN OTHERS THEN
	                raise_application_error (-20000, 'MAM-00974 #1'||SQLERRM);
	        END;
	      END IF;
	      --
	    END LOOP;
	  END;*/
	}
	
	/**
	 * Procedure
	 * ORADB p_limpa_itens
	 * @param seq
	 */
	private void limparItensEvolucao(Long seq) throws ApplicationBusinessException{
		List<MamItemEvolucoes> lista = this.getMamItemEvolucoesDAO().pesquisarItemEvolucoesPorEvolucaoEDescricaoNula(seq);
		for(MamItemEvolucoes itemEvolucao: lista){
			try{
				this.getMarcacaoConsultaRN().removerItemEvolucao(itemEvolucao);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00975);
			}
		}
	}
	
	//TODO trechos de codigo foram comentados por nao fazerem parte do escopo da estoria 6220, devem ser implementados quando necessários
	public void tratarEvolucaoRascunho(Long seq, Long evoSeq) throws ApplicationBusinessException{
		List<MamItemEvolucoes> listaItem = this.getMamItemEvolucoesDAO().pesquisarItemEvolucoesPorEvolucao(evoSeq);
		for(MamItemEvolucoes itemEvolucao: listaItem){
			try{
				this.getMarcacaoConsultaRN().removerItemEvolucao(itemEvolucao);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00967);
			}
		}
		/*BEGIN
	      DELETE FROM mam_resposta_evolucoes
	      WHERE evo_seq = t_seq;
	      EXCEPTION
	        WHEN regra_negocio THEN
	             RAISE;
	        WHEN OTHERS THEN
	             raise_application_error (-20000, 'MAM-00968 #1'||SQLERRM);
	    END;*/
		
		try{
			MamEvolucoes evolucao = this.getMamEvolucoesDAO().obterPorChavePrimaria(seq);
			this.getMarcacaoConsultaRN().removerEvolucao(evolucao);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00968);
		}
		if(evoSeq!=null){
			MamEvolucoes evolucao = this.getMamEvolucoesDAO().obterPorChavePrimaria(evoSeq);
			evolucao.setDthrMvto(null);
			evolucao.setServidorMvto(null);
			try{
				this.getMarcacaoConsultaRN().atualizarEvolucao(evolucao);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00970);	
			}
		}
	}
	
	public void tratarEvolucaoPendente(Long seq, Long evoSeq, Date dataMovimento, RapServidores servidor) throws ApplicationBusinessException{
		MamEvolucoes evolucao = this.getMamEvolucoesDAO().obterPorChavePrimaria(seq);
		evolucao.setDthrValida(dataMovimento);
		evolucao.setServidorValida(servidor);
		evolucao.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getMarcacaoConsultaRN().atualizarEvolucao(evolucao);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00971);	
		}
		if(evoSeq!=null){
			MamEvolucoes evolucaoAux = this.getMamEvolucoesDAO().obterPorChavePrimaria(evoSeq);
			evolucaoAux.setDthrValidaMvto(dataMovimento);
			evolucaoAux.setServidorValidaMvto(servidor);
			evolucaoAux.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getMarcacaoConsultaRN().atualizarEvolucao(evolucao);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00972);	
			}
		}
	}
	
	
	public void tratarEvolucaoExclusao(Long seq, Date dataMovimento, RapServidores servidor) throws ApplicationBusinessException{
		MamEvolucoes evolucao = this.getMamEvolucoesDAO().obterPorChavePrimaria(seq);
		evolucao.setDthrValidaMvto(dataMovimento);
		evolucao.setServidorValidaMvto(servidor);
		evolucao.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getMarcacaoConsultaRN().atualizarEvolucao(evolucao);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00973);	
		}
	}
	/**
	 * Procedure
	 * ORADB MAMP_CONC_PROCED
	 *  
	 */
	//TODO trechos de codigo foram comentados por nao fazerem parte do escopo da estoria 6220, devem ser implementados quando necessários
	public void concluirProcedimento(Integer conNumero, Date dataMovimento, Short vinCodigo, Integer matricula, Long polSeq) throws ApplicationBusinessException{
//		Boolean erro;
		List<MamProcedimentoRealizado> lista = this.getMamProcedimentoRealizadoDAO().pesquisarProcedimentoRealizadoParaConclusao(conNumero, polSeq);
		for(MamProcedimentoRealizado procedimentoRealizado: lista){
			Long polSeqAux = null;
			if(procedimentoRealizado.getProcedimentoRealizado()!=null){
				 polSeqAux = procedimentoRealizado.getProcedimentoRealizado().getSeq(); 
			}
			if(procedimentoRealizado!=null && procedimentoRealizado.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarProcedimentoRealizadoRascunho(procedimentoRealizado.getSeq(), polSeqAux);
			} else if(procedimentoRealizado.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
//				erro = false;
				Integer cidSeq = null;
				Integer prdSeq = null;
				if(procedimentoRealizado!=null && procedimentoRealizado.getCid()!=null){
					cidSeq = procedimentoRealizado.getCid().getSeq();
				}
				if(procedimentoRealizado!=null && procedimentoRealizado.getProcedimento()!=null){
					prdSeq = procedimentoRealizado.getProcedimento().getSeq();
				}

				if(cidSeq==null){
					this.verificarCidProcedimento(conNumero, prdSeq, cidSeq);
				}
//				erro = false;
				/* mamk_pol_rn.rn_polp_atu_dia
					(r_pol.pol_seq,
					 r_pol.seq,
					 p_ser_matricula,
					 p_ser_vin_codigo,
					 v_erro);*/
//				if(erro){
//					//raise_application_error (-20000, v_erro);
//				}
				this.tratarProcedimentoRealizadoPendente(procedimentoRealizado.getSeq(), polSeqAux, dataMovimento, vinCodigo, matricula);
			} else if(procedimentoRealizado.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
//				erro = false;
				/*mamk_pol_rn.rn_polp_atu_dia
				(r_pol.seq,
				 NULL,
				 p_ser_matricula,
				 p_ser_vin_codigo,
				 v_erro);*/
//				if(erro){
//	//				        raise_application_error (-20000, v_erro);
//				}
				this.tratarProcedimentoRealizadoExclusao(procedimentoRealizado.getSeq(), dataMovimento, vinCodigo, matricula);
			}
		}
	}
	//este método foi criado pois na finalizacao do atendimento é necessario o rollback em caso de exception
	public void verificarCidProcedimento(Integer consultaNumero, Integer prdSeq, 
			Integer cidSeq) throws ApplicationBusinessException {
		Long numCidsParaInformar = 0l;
		Short vlrPagadorSus = 0;
		
		AacConsultas consulta = getAacConsultasDAO().obterConsulta(consultaNumero);
		
		AghParametros paramPagadorSus = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PAGADOR_SUS);
		
		if (paramPagadorSus != null && paramPagadorSus.getVlrNumerico() != null) {
			vlrPagadorSus = paramPagadorSus.getVlrNumerico().shortValue();
		}
		
		if (consulta.getPagador() != null && consulta.getPagador().getSeq().equals(vlrPagadorSus) && prdSeq != null) {
			numCidsParaInformar = getVMamProcXCidDAO().pesquisarCidsParaProcedimentoAtendimentoMedicoCount(null, prdSeq);
		}		

		MamProcedimento procedimento = getMamProcedimentoDAO().obterPorChavePrimaria(prdSeq);
		if (numCidsParaInformar == 0) {
			if (cidSeq != null) {
				throw new ApplicationBusinessException(
						AtendimentoPacientesAgendadosONExceptionCode.MSG_NAO_INFORME_CID_PARA_O_PROCEDIMENTO, 
						procedimento.getDescricao());
			}
		}
		else if (cidSeq == null) {
				throw new ApplicationBusinessException(
						AtendimentoPacientesAgendadosONExceptionCode.MSG_INFORME_CID_PARA_O_PROCEDIMENTO, 
						procedimento.getDescricao());
			}
		
	}
	
	public void tratarProcedimentoRealizadoRascunho(Long seq, Long polSeq) throws ApplicationBusinessException{
		try{
			MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(seq);
			this.getProcedimentoAtendimentoConsultaRN().removerProcedimentoRealizado(procedimento, false);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00901);
		}
		if(polSeq!=null){
			MamProcedimentoRealizado procedimentoOld = null;
			MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(polSeq);
			try{
				procedimentoOld = (MamProcedimentoRealizado) BeanUtils.cloneBean(procedimento);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_PROCEDIMENTO_REALIZADO);
			}
			procedimento.setDthrMovimento(null);
			procedimento.setServidorMovimento(null);
			try{
				this.getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(procedimentoOld, procedimento, true);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00902);	
			}
		}
	}
	
	public void tratarProcedimentoRealizadoPendente(Long seq, Long polSeq, Date dataMovimento, Short vinCodigo, Integer matricula) throws ApplicationBusinessException {
		RapServidoresId id = new RapServidoresId();
		id.setVinCodigo(vinCodigo);
		id.setMatricula(matricula);
		RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(id);
		
		try{
			MamProcedimentoRealizado procedimentoOld = null;
			MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(seq);
			try{
				procedimentoOld = (MamProcedimentoRealizado) BeanUtils.cloneBean(procedimento);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_PROCEDIMENTO_REALIZADO);
			}
			procedimento.setPendente(DominioIndPendenteAmbulatorio.V);
			procedimento.setDthrValida(dataMovimento);
			procedimento.setServidorValida(servidor);
			this.getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(procedimentoOld, procedimento, true);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00903);
		}
		if(polSeq!=null){
			MamProcedimentoRealizado procedimentoOld = null;
			MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(polSeq);
			try{
				procedimentoOld = (MamProcedimentoRealizado) BeanUtils.cloneBean(procedimento);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_PROCEDIMENTO_REALIZADO);
			}
			procedimento.setDthrValidaMovimento(dataMovimento);
			procedimento.setServidorValidaMovimento(servidor);
			procedimento.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(procedimentoOld, procedimento, true);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00903);	
			}
		}
	}
	
	public void tratarProcedimentoRealizadoExclusao(Long seq, Date dataMovimento, Short vinCodigo, Integer matricula) throws ApplicationBusinessException{
		RapServidoresId id = new RapServidoresId();
		id.setVinCodigo(vinCodigo);
		id.setMatricula(matricula);
		RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(id);
		
		MamProcedimentoRealizado procedimentoOld = null;
		MamProcedimentoRealizado procedimento = this.getMamProcedimentoRealizadoDAO().obterPorChavePrimaria(seq);
		try{
			procedimentoOld = (MamProcedimentoRealizado) BeanUtils.cloneBean(procedimento);	
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.ERRO_CLONE_PROCEDIMENTO_REALIZADO);
		}
		procedimento.setDthrValidaMovimento(dataMovimento);
		procedimento.setServidorValidaMovimento(servidor);
		procedimento.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getProcedimentoAtendimentoConsultaRN().atualizarProcedimentoRealizado(procedimentoOld, procedimento, true);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00904);	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CONC_RECEITA
	 */
	public void concluirReceita(Integer conNumero, Date dataMovimento, Short vinCodigo, Integer matricula, Long rctSeq) throws ApplicationBusinessException{
		List<MamReceituarios> lista = this.getMamReceituariosDAO().pesquisarReceituarioParaConclusao(conNumero, dataMovimento, rctSeq);
		for(MamReceituarios receituario: lista){
			Long rctSeqAux = null;
			if(receituario.getReceituario()!=null){
				 rctSeqAux = receituario.getReceituario().getSeq(); 
			}
			if(receituario.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarReceituarioRascunho(receituario.getSeq(), rctSeqAux);
			} else if(receituario.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarReceituarioPendente(receituario.getSeq(), rctSeqAux, dataMovimento, vinCodigo, matricula);
			} else if(receituario.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarReceituarioExclusao(receituario.getSeq(), dataMovimento, vinCodigo, matricula);
			}
		}
	}
	
	public void tratarReceituarioRascunho(Long seq, Long rctSeq) throws ApplicationBusinessException{
		List<MamItemReceituario> listaItem = this.getMamItemReceituarioDAO().pesquisarMamItemReceituario(rctSeq);
		for(MamItemReceituario itemReceituario: listaItem){
			try{
				this.getAmbulatorioFacade().removerItemReceituario(itemReceituario);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00895);
			}
		}
	
		try{
			MamReceituarios receituario = this.getMamReceituariosDAO().obterPorChavePrimaria(seq);
			this.getAmbulatorioFacade().removerReceituario(receituario);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00840);
		}
		if(rctSeq!=null){
			MamReceituarios receituario = this.getMamReceituariosDAO().obterPorChavePrimaria(rctSeq);
			receituario.setDthrMvto(null);
			receituario.setServidorMovimento(null);
			try{
				this.getAmbulatorioFacade().atualizarReceituario(receituario);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00841);	
			}
		}
	}
	
	public void tratarReceituarioPendente(Long seq, Long rctSeq, Date dataMovimento, Short vinCodigo, Integer matricula) throws ApplicationBusinessException{
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(matricula);
		id.setVinCodigo(vinCodigo);
		RapServidores servidor = this.getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(id);
		MamReceituarios receituario = this.getMamReceituariosDAO().obterPorChavePrimaria(seq);
		receituario.setPendente(DominioIndPendenteAmbulatorio.V);
		receituario.setDthrValida(dataMovimento);
		receituario.setServidorValida(servidor);
		try{
			this.getAmbulatorioFacade().atualizarReceituario(receituario);	
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00842);
		}
		
		if(rctSeq!=null){
			MamReceituarios receituarioAux = this.getMamReceituariosDAO().obterPorChavePrimaria(rctSeq);
			receituarioAux.setPendente(DominioIndPendenteAmbulatorio.V);
			receituarioAux.setDthrValidaMvto(dataMovimento);
			receituarioAux.setServidorValidaMovimento(servidor);
			
			try{
				this.getAmbulatorioFacade().atualizarReceituario(receituarioAux);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00843);	
			}
		}
	}
	
	public void tratarReceituarioExclusao(Long seq, Date dataMovimento, Short vinCodigo, Integer matricula) throws ApplicationBusinessException{
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(matricula);
		id.setVinCodigo(vinCodigo);
		RapServidores servidor = this.getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(id);
		MamReceituarios receituario = this.getMamReceituariosDAO().obterPorChavePrimaria(seq);
		receituario.setDthrValidaMvto(dataMovimento);
		receituario.setServidorValidaMovimento(servidor);
		receituario.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getAmbulatorioFacade().atualizarReceituario(receituario);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00844);	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CONC_ANAMNESE
	 */
	public void concluirAnamnese(Integer conNumero, Date dataMovimento, Short vinCodigo, Integer matricula, Long anaSeq) throws ApplicationBusinessException{
		RapServidoresId servidorId =  new RapServidoresId();
		servidorId.setMatricula(matricula);
		servidorId.setVinCodigo(vinCodigo);
		RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(servidorId);
		
		List<MamAnamneses> lista = this.getMamAnamnesesDAO().pesquisarAnamneseParaConclusao(conNumero, anaSeq);
		for(MamAnamneses anamnese:lista){
			Long anaSeqAux = null;
			if(anamnese.getAnamnese()!=null){
				 anaSeqAux = anamnese.getAnamnese().getSeq(); 
			}
	
			if(!anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.limparRespostasAnamnese(anamnese.getSeq());
				this.limparItensAnamnese(anamnese.getSeq());
			}
			if(anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.R)){
				this.tratarAnamneseRascunho(anamnese.getSeq(), anaSeqAux);
			} else if(anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.P)){
				this.tratarAnamnesePendente(anamnese.getSeq(), anaSeqAux, dataMovimento, servidor);
			     /*mamp_conc_tmp_ana
					(r_ana.seq,
					 r_ana.ana_seq,
					 p_ser_vin_codigo,
					 p_ser_matricula);*/
			} else if(anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.E)){
				this.tratarAnamneseExclusao(anamnese.getSeq(), dataMovimento, servidor);
			}
		}
		/*  mamp_conc_del_tmp_a
			(r_ana.seq);*/
	}
	
	public void limparItensAnamnese(Long seq) throws ApplicationBusinessException{
		List<MamItemAnamneses> lista = this.getMamItemAnamnesesDAO().pesquisarItemAnamnesePorAnamnesesEDescricaoNula(seq);
		for(MamItemAnamneses itemAnamneses: lista){
			try{
				this.getMarcacaoConsultaRN().removerItemAnamnese(itemAnamneses);	
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00272);
			}
		}
	}
	
	//TODO procedure nao foi migrada por nao fazer parte do escopo da estoria 6220, deve ser migrada quando necessária
	public void limparRespostasAnamnese(Long seq){
		/*PROCEDURE p_limpa_respostas
		(t_seq	IN	NUMBER)
		IS
		  --
		  CURSOR cur_rea
			(c_ana_seq		mam_resposta_anamneses.ana_seq%type)
		  IS
		    SELECT
		      ana_seq,
		      qus_qut_seq,
		      qus_seqp,
			seqp
		    FROM
		      mam_resposta_anamneses
		    WHERE
		      ana_seq = c_ana_seq AND
		      resposta IS NULL AND
		      vvq_qus_qut_seq IS NULL AND
		      vvq_qus_seqp IS NULL AND
		      vvq_seqp IS NULL;
		  --
		  BEGIN
		    --
		    FOR r_rea IN cur_rea (t_seq)
		    LOOP
		       --
		       IF mamk_generica.mamc_respondeu_cust
						('A',
						 r_rea.ana_seq,
						 r_rea.qus_qut_seq,
						 r_rea.qus_seqp) = 'N'
		       THEN
		          --
		          BEGIN
		            DELETE FROM mam_resposta_anamneses
		             WHERE ana_seq     = r_rea.ana_seq
		               AND qus_qut_seq = r_rea.qus_qut_seq
		               AND qus_seqp    = r_rea.qus_seqp
		               AND seqp        = r_rea.seqp;
		            EXCEPTION
		              WHEN regra_negocio THEN
		                RAISE;
		              WHEN OTHERS THEN
		                raise_application_error (-20000, 'MAM-00271 #1'||SQLERRM);
		          END;
		          --
		       END IF;
		       --
		    END LOOP;
		    --
		  END;*/
	}
	
	//TODO trechos de codigo foram comentados por nao fazerem parte do escopo da estoria 6220, devem ser implementados quando necessários
	public void tratarAnamneseRascunho(Long seq, Long anaSeq) throws ApplicationBusinessException{
		List<MamItemAnamneses> listaItem = this.getMamItemAnamnesesDAO().pesquisarItemAnamnesesPorAnamneses(anaSeq);
		for(MamItemAnamneses itemAnamnese: listaItem){
			try{
				this.getMarcacaoConsultaRN().removerItemAnamnese(itemAnamnese);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00633);
			}
		}
		/*   BEGIN
		       DELETE FROM mam_resposta_anamneses
		       WHERE ana_seq = t_seq;
		       EXCEPTION
		         WHEN regra_negocio THEN
		              RAISE;
		         WHEN OTHERS THEN
		              raise_application_error (-20000, 'MAM-00634 #1'||SQLERRM);
		     END;
	*/  
		
		MamAnamneses anamnese = this.getMamAnamnesesDAO().obterPorChavePrimaria(seq);
		try{
			this.getMarcacaoConsultaRN().removerAnamnese(anamnese);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00258);
		}
		
		if(anaSeq!=null){
			MamAnamneses anamneseAux = this.getMamAnamnesesDAO().obterPorChavePrimaria(anaSeq);
			anamneseAux.setDthrMvto(null);
			anamneseAux.setServidorMvto(null);
			try{
				this.getMarcacaoConsultaRN().atualizarAnamnese(anamnese);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00263);	
			}
		}
	}
	
	public void tratarAnamnesePendente(Long seq, Long anaSeq, Date dataMovimento, RapServidores servidor) throws ApplicationBusinessException{
	    MamAnamneses anamnese = this.getMamAnamnesesDAO().obterPorChavePrimaria(seq);
	    anamnese.setPendente(DominioIndPendenteAmbulatorio.V);
	    anamnese.setDthrValida(dataMovimento);
	    anamnese.setServidorValida(servidor);
	    
		try{
			this.getMarcacaoConsultaRN().atualizarAnamnese(anamnese);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00260);
		}
		
		if(anaSeq!=null){
			MamAnamneses anamneseAux = this.getMamAnamnesesDAO().obterPorChavePrimaria(anaSeq);
			anamneseAux.setDthrValidaMvto(dataMovimento);
			anamneseAux.setServidorValidaMvto(servidor);
			anamneseAux.setPendente(DominioIndPendenteAmbulatorio.V);
			try{
				this.getMarcacaoConsultaRN().atualizarAnamnese(anamneseAux);
			} catch(Exception e){
				logError(EXCECAO_CAPTURADA, e);
				throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00261);	
			}
		}
	}
	
	public void tratarAnamneseExclusao(Long seq, Date dataMovimento, RapServidores servidor) throws ApplicationBusinessException{
		MamAnamneses anamnese = this.getMamAnamnesesDAO().obterPorChavePrimaria(seq);
		anamnese.setDthrValidaMvto(dataMovimento);
		anamnese.setServidorValidaMvto(servidor);
		anamnese.setPendente(DominioIndPendenteAmbulatorio.V);
		try{
			this.getMarcacaoConsultaRN().atualizarAnamnese(anamnese);
		} catch(Exception e){
			logError(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00268);	
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CONC_EXAMES
	 * @param conNumero
	 * @param soeSeq
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void concluirExames(Integer conNumero, Integer soeSeq, String nomeMicrocomputador) throws ApplicationBusinessException {
		AghParametros parametroPendente = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_PENDENTE);
		String situacaoPendente = parametroPendente.getVlrTexto();
		List<AelItemSolicitacaoExames> lista = this.getExamesFacade().pesquisarItemSolicitacaoExameParaConclusao(conNumero, soeSeq, false);
		for(AelItemSolicitacaoExames item:lista){
			if(item.getSituacaoItemSolicitacao()!=null && item.getSituacaoItemSolicitacao().getCodigo().substring(0, 2).equals(situacaoPendente)){
				
				Integer soeSeqAux = null;
				Short seqP = null;
				if(item.getId()!=null){
					soeSeqAux = item.getId().getSoeSeq();
					seqP = item.getId().getSeqp();
				}
				
				List<AelExtratoItemSolicitacao> listaAux = this.getExamesFacade().pesquisarExtratoItemSolicitacaoConclusao(soeSeqAux, seqP);
				for(AelExtratoItemSolicitacao extratoItem: listaAux){
					if(!(extratoItem.getAelSitItemSolicitacoes()!=null && extratoItem.getAelSitItemSolicitacoes().getCodigo().substring(0, 2).equals(situacaoPendente))){
						AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
						id.setSoeSeq(soeSeqAux);
						id.setSeqp(seqP);
						AelItemSolicitacaoExames itemSolicitacaoExames = this.getExamesFacade().obteritemSolicitacaoExamesPorChavePrimaria(id);
						itemSolicitacaoExames.setSituacaoItemSolicitacao(extratoItem.getAelSitItemSolicitacoes());
						try{
							this.getExamesBeanFacade().atualizarItemSolicitacaoExame(itemSolicitacaoExames, nomeMicrocomputador);
							break;
						} catch(Exception e){
							logError(EXCECAO_CAPTURADA, e);
							//throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_01201, e);
							FinalizaAtendimentoRNExceptionCode.MAM_01201.throwException(e.getMessage());
						}
					}
				}
				
			}
		}
	}
	
	/**
	 * Procedure
	 * ORADB MAMP_CONC_LIMPA_REC
	 * 
	 */
	public void concluirLimpaReceituarios(Integer conNumero) throws ApplicationBusinessException{
		List<MamReceituarios> listaReceituarios = this.getMamReceituariosDAO().pesquisarReceituarioPorConsultaEIndPendenteDiferente(conNumero, DominioIndPendenteAmbulatorio.V);
		for(MamReceituarios receituario: listaReceituarios){
			List<MamItemReceituario> listaItemReceituarios = this.getMamItemReceituarioDAO().pesquisarMamItemReceituario(receituario.getSeq());
			if(listaItemReceituarios==null || listaItemReceituarios.size()==0){
				MamReceituarios receituarioOriginal = this.getMamReceituariosDAO().obterMamReceituarioOriginal(receituario);
				if (DominioIndPendenteAmbulatorio.V.equals(receituarioOriginal.getPendente())) {
					throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_00574);
				}
				try{
					this.getAmbulatorioFacade().removerReceituario(receituario);	
				} catch(ApplicationBusinessException e){
					logError(e);
				}
				
			}
		}
	}

	/**
	 * Procedure
	 * ORADB MAMP_CONC_EXAMES_RES
	 *  
	 */
	@SuppressWarnings("ucd")
	public void concluirExameResponsavel(Integer conNumero, Integer soeSeq, Short vinCodigo, Integer matricula, String nomeMicrocomputador) throws ApplicationBusinessException {
		RapServidoresId id = new RapServidoresId();
		id.setVinCodigo(vinCodigo);
		id.setMatricula(matricula);
		RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(id);
		AghParametros parametroPendente = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_PENDENTE);
		String situacaoPendente = parametroPendente.getVlrTexto();
		List<AelItemSolicitacaoExames> lista = this.getExamesFacade().pesquisarItemSolicitacaoExameParaConclusao(conNumero, soeSeq, true);
		for(AelItemSolicitacaoExames item: lista){
			if(item.getSituacaoItemSolicitacao()!=null && item.getSituacaoItemSolicitacao().getCodigo().substring(0, 2).equals(situacaoPendente)){
				Integer soeSeqAux = null;
				if(item.getId()!=null){
					soeSeqAux = item.getId().getSoeSeq();
				}
				AelSolicitacaoExames solicitacaoExame = this.getExamesFacade().obterAelSolicitacaoExamePorChavePrimaria(soeSeqAux);
				solicitacaoExame.setServidorResponsabilidade(servidor);
				try{
					this.getExamesBeanFacade().atualizarSolicitacaoExame(solicitacaoExame, null, nomeMicrocomputador);	
				} catch(Exception e){
					logError(EXCECAO_CAPTURADA, e);
					throw new ApplicationBusinessException(FinalizaAtendimentoRNExceptionCode.MAM_01759);	
				}
			}
		}
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IExamesBeanFacade getExamesBeanFacade() {
		return this.examesBeanFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected MamExtratoControlesDAO getMamExtratoControlesDAO() {
		return mamExtratoControlesDAO;
	}
	
	protected MamNotaAdicionalAnamnesesDAO getMamNotaAdicionalAnamnesesDAO() {
		return mamNotaAdicionalAnamnesesDAO;
	}
	
	protected MamNotaAdicionalEvolucoesDAO getMamNotaAdicionalEvolucoesDAO() {
		return mamNotaAdicionalEvolucoesDAO;
	}
	
		
	protected MamEvolucoesDAO getMamEvolucoesDAO() {
		return mamEvolucoesDAO;
	}
	
	protected MamItemEvolucoesDAO getMamItemEvolucoesDAO() {
		return mamItemEvolucoesDAO;
	}
	
	protected MamProcedimentoRealizadoDAO getMamProcedimentoRealizadoDAO() {
		return mamProcedimentoRealizadoDAO;
	}
	
	protected MamItemReceituarioDAO getMamItemReceituarioDAO() {
		return mamItemReceituarioDAO;
	}
	
	protected MamReceituariosDAO getMamReceituariosDAO() {
		return mamReceituariosDAO;
	}
	
	protected MamAnamnesesDAO getMamAnamnesesDAO() {
		return mamAnamnesesDAO;
	}
	
	protected MamItemAnamnesesDAO getMamItemAnamnesesDAO() {
		return mamItemAnamnesesDAO;
	}
	
	protected IExamesFacade getExamesFacade(){
		return this.examesFacade;
	}
	
	protected CancelamentoAtendimentoRN getCancelamentoAtendimentoRN(){
		return cancelamentoAtendimentoRN;
	}
	
	protected MarcacaoConsultaRN getMarcacaoConsultaRN(){
		return marcacaoConsultaRN;
	}
	
	protected ProcedimentoAtendimentoConsultaRN getProcedimentoAtendimentoConsultaRN(){
		return procedimentoAtendimentoConsultaRN;
	}
	
	protected AmbulatorioConsultaRN getAmbulatorioConsultaRN(){
		return ambulatorioConsultaRN;
	}
	
	protected ProcedimentoConsultaRN getProcedimentoConsultaRN(){
		return procedimentoConsultaRN;
	}
	
	protected MamControlesDAO getMamControlesDAO(){
		return mamControlesDAO;
	}
	
	protected AacConsultasDAO getAacConsultasDAO(){
		return aacConsultasDAO;
	}
	
	protected VMamProcXCidDAO getVMamProcXCidDAO() {
		return vMamProcXCidDAO;
	}
	
	protected MamProcedimentoDAO getMamProcedimentoDAO() {
		return mamProcedimentoDAO;
	}
	
	protected AacConsultaProcedHospitalarDAO getAacConsultaProcedHospitalarDAO() {
		return aacConsultaProcedHospitalarDAO;
	}
	
	protected ProcedimentoConsultaON getProcedimentoConsultaON() {
		return procedimentoConsultaON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
