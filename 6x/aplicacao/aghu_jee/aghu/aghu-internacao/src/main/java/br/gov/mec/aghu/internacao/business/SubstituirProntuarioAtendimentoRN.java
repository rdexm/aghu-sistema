package br.gov.mec.aghu.internacao.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AbsAmostrasPacientes;
import br.gov.mec.aghu.model.AbsAmostrasPacientesId;
import br.gov.mec.aghu.model.AbsDoacoes;
import br.gov.mec.aghu.model.AbsEstoqueComponentes;
import br.gov.mec.aghu.model.AbsMovimentosComponentes;
import br.gov.mec.aghu.model.AbsSolicitacoesDoacoes;
import br.gov.mec.aghu.model.AbsSolicitacoesDoacoesId;
import br.gov.mec.aghu.model.AbsSolicitacoesPorAmostra;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelPacUnidFuncionaisId;
import br.gov.mec.aghu.model.AelProtocoloInternoUnids;
import br.gov.mec.aghu.model.AelProtocoloInternoUnidsId;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.model.MciMvtoProcedimentoRiscos;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class SubstituirProntuarioAtendimentoRN extends BaseBusiness {

	@EJB
	private InternacaoRN internacaoRN;
	
	private static final Log LOG = LogFactory.getLog(SubstituirProntuarioAtendimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@Inject
	private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private AinExtratoLeitosDAO ainExtratoLeitosDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1781681664256232056L;

	/*
	 *  substituindo paciente no atendimento de urgência, caso exista.
	 *  
		update  ain_atendimentos_urgencia atu
	    set atu.pac_codigo = p_codigo_destino
		where atu.pac_codigo =  p_codigo_origem
		and atu.seq =  v_atu_seq;
	*/
	public void substituirPacienteAtendimentoUrgencia(
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AinAtendimentosUrgencia atendimentoUrgencia, 
			String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor
	) throws BaseException {
		InternacaoRN internacaoRN = this.getInternacaoRN();
		AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO = this.getAinAtendimentosUrgenciaDAO();
		
		if (atendimentoUrgencia != null && atendimentoUrgencia.getPaciente().equals(pacienteOrigem)) {
			ainAtendimentosUrgenciaDAO.desatachar(atendimentoUrgencia);
			AinAtendimentosUrgencia auOriginal = ainAtendimentosUrgenciaDAO.obterAtendimentoUrgencia(atendimentoUrgencia.getSeq(),
					LockOptions.UPGRADE);
			atendimentoUrgencia.setPaciente(pacienteDestino);
			internacaoRN.validarAtendimentoUrgenciaAntesAtualizar ( // Trigger Before row update
					atendimentoUrgencia, auOriginal, servidorLogado != null ? servidorLogado.getUsuario() : null);
			ainAtendimentosUrgenciaDAO.merge(atendimentoUrgencia);
			ainAtendimentosUrgenciaDAO.flush();
			// Trigger After row update
			//internacaoRN.validarAtendimentoUrgenciaAposAtualizar(atendimentoUrgencia, auOriginal);
			internacaoRN.processarEnforceAtendimentoUrgencia( // Enforce
					atendimentoUrgencia, auOriginal, TipoOperacaoEnum.UPDATE, nomeMicrocomputador, dataFimVinculoServidor
			);
		}		
	}
	
	/*
		update  ain_extrato_leitos exl
        set  exl.pac_codigo = p_codigo_destino
		where exl.pac_codigo = p_codigo_origem
		and exl.int_seq = p1_int_seq;
 	*/
	public void substituirPacienteExtratoLeito(
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AinInternacao internacao
	) {
		if (pacienteOrigem == null || internacao == null) {
			return;
		}
		
		AinExtratoLeitosDAO ainExtratoLeitosDAO = this.getAinExtratoLeitosDAO();
		
		List<AinExtratoLeitos> extratos = ainExtratoLeitosDAO.listaExtratosLeitos(pacienteOrigem, internacao);

		for (AinExtratoLeitos ext : extratos) {
			ext.setPaciente(pacienteDestino);
		}
		// Não existe trigger de update para AinExtratoLeitos :)
		ainExtratoLeitosDAO.flush();
	}
	
	/*
	 * substituindo cirurgias, caso exista
	 * 
		update mbc_cirurgias
        set pac_codigo = p_codigo_destino
		where pac_codigo = p_codigo_origem
		and atd_seq = v_atd_seq;
	 */
	public void substituirPacienteCirurgias(
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento
	) {
		if (pacienteOrigem == null || atendimento == null) {
			return;
		}
		
		IBlocoCirurgicoFacade blocoCirurgicoFacade = getBlocoCirurgicoFacade();
		
		List<MbcCirurgias> cirurgias = blocoCirurgicoFacade.listarCirurgias(pacienteOrigem, atendimento);

		for (MbcCirurgias cir : cirurgias) {
			cir.setPaciente(pacienteDestino);
		}
		// TODO: Chamar triggers de MbcCirurgias quando forem migradas
		flush();
	}
	
	/*
	 * substituindo consultas, caso exista.
	 * 
		update aac_consultas
        set pac_codigo = p_codigo_destino
		where pac_codigo = p_codigo_origem
		and numero = v_con_numero;
	 */
	public void substituirPacienteConsulta(AipPacientes pacienteOrigem, AipPacientes pacienteDestino, AghAtendimentos atendimento) {
	
		
		AacConsultas consulta = atendimento.getConsulta();
		if (consulta != null && Objects.equals(consulta.getPaciente(), pacienteOrigem)) {
			consulta.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de AacConsultas quando forem migradas
			flush();
		}
	}
	
	/*
		update FAT_PROCED_AMB_REALIZADOS
        set pac_codigo = p_codigo_destino
		where pac_codigo = p_codigo_origem
			and atd_seq  = v_atd_seq
			and prh_con_numero is null;
	 */
	public void substituirPacienteProcedimentosAmbulatorialRealizados(
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento
	) {
		if (pacienteOrigem == null || atendimento == null) {
			return;
		}
		
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

		List<FatProcedAmbRealizado> procedAmbRealizados = faturamentoFacade.listarProcedAmbRealizadosPrhConNumeroNulo(
				pacienteOrigem.getCodigo(), atendimento);

		for (FatProcedAmbRealizado proc : procedAmbRealizados) {
			proc.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de FatProcedAmbRealizados quando forem migradas
			flush();
		}
	}
	
	/*
		update agh_atendimentos atd
		set atd.pac_codigo = p_codigo_destino,
            atd.prontuario = v_prontuario_destino
        where atd.pac_codigo = p_codigo_origem
			  and seq = v_atd_seq;
	 */
	public void substituirPacienteAtendimento(
		AipPacientes pacienteOrigem,
		AipPacientes pacienteDestino,
		AghAtendimentos atendimento, String nomeMicrocomputador		
	) throws BaseException {
		IPacienteFacade pacienteFacade = this.getPacienteFacade();
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (atendimento != null && atendimento.getPaciente().equals(pacienteOrigem)) {
			AghAtendimentos atendimentoOld = pacienteFacade.clonarAtendimento(atendimento);
			atendimento.setPaciente(pacienteDestino);
			atendimento.setProntuario(pacienteDestino.getProntuario());
			pacienteFacade.atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, new Date());
			
		}
		pacienteFacade.aippSubstProntConv ( // aipp_subst_pront_conv
				pacienteOrigem == null ? null : pacienteOrigem.getProntuario(), 
				pacienteDestino == null ? null : pacienteDestino.getProntuario() 
		);
	}
	
	/*
	 * substituindo MCI_MVTO_FATOR_PREDISPONENTES, caso exista.
	 * 
		update MCI_MVTO_FATOR_PREDISPONENTES
        set pac_codigo = p_codigo_destino
		where pac_codigo = p_codigo_origem
			and atd_seq  = v_atd_seq;
	 */
	public void substituirPacienteMvtoFatorPredisponentes (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento
	) {
		if (pacienteOrigem == null || atendimento == null) {
			return;
		}
		
		IControleInfeccaoFacade controleInfeccaoFacade = this.getControleInfeccaoFacade();

		List<MciMvtoFatorPredisponentes> fatores = controleInfeccaoFacade.listarMvtoFatorPredisponentes(pacienteOrigem.getCodigo(), atendimento);
		
		for (MciMvtoFatorPredisponentes fat : fatores) {
			fat.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de MciMvtoFatorPredisponentes quando forem migradas
			flush();
		}
	}
	
	/*
	 * substituindo MCI_MVTO_PROCEDIMENTO_RISCOS, caso exista. 
	 *
		update MCI_MVTO_PROCEDIMENTO_RISCOS
        set pac_codigo  =  p_codigo_destino
		where pac_codigo  =  p_codigo_origem
        and atd_seq         =  v_atd_seq;
	 */
	public void substituirPacienteMvtoProcedimentosRisco (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento
	) {
		if (pacienteOrigem == null || atendimento == null) {
			return;
		}
		
		IControleInfeccaoFacade controleInfeccaoFacade = this.getControleInfeccaoFacade();
		
		List<MciMvtoProcedimentoRiscos> lista = controleInfeccaoFacade.listarMvtosProcedimentosRiscos(pacienteOrigem.getCodigo(), atendimento);

		for (MciMvtoProcedimentoRiscos item : lista) {
			item.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de MciMvtoProcedimentoRiscos quando forem migradas
			flush();
		}
	}
	
	/*
	 * substituindo MCI_MVTO_MEDIDA_PREVENTIVAS, caso exista. 
	 *
		update MCI_MVTO_MEDIDA_PREVENTIVAS
        set pac_codigo  =  p_codigo_destino
		where pac_codigo  =  p_codigo_origem
        and atd_seq         =  v_atd_seq;
	 */
	public void substituirPacienteMvtoMedidasPreventivas (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento
	) {
		if (pacienteOrigem == null || atendimento == null) {
			return;
		}
		
		IControleInfeccaoFacade controleInfeccaoFacade = this.getControleInfeccaoFacade();
		
		List<MciMvtoMedidaPreventivas> lista = controleInfeccaoFacade.listarMvtosMedidasPreventivas(pacienteOrigem.getCodigo(), atendimento);

		for (MciMvtoMedidaPreventivas item : lista) {
			item.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de MciMvtoMedidaPreventivas quando forem migradas
			flush();
		}
	}

	/*
	 * substituindo MCI_MVTO_INFECCAO_TOPOGRAFIAS, caso exista. 
	 *
		update MCI_MVTO_INFECCAO_TOPOGRAFIAS
        set pac_codigo  =  p_codigo_destino
		where pac_codigo  =  p_codigo_origem
        and atd_seq         =  v_atd_seq;
	 */
	public void substituirPacienteMvtoInfeccaoTopografias (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento
	) {
		if (pacienteOrigem == null || atendimento == null) {
			return;
		}
		IControleInfeccaoFacade controleInfeccaoFacade = this.getControleInfeccaoFacade();
		
		List<MciMvtoInfeccaoTopografias> lista = controleInfeccaoFacade.listarMvtosInfeccoesTopografias(
				pacienteOrigem.getCodigo(), atendimento);

		for (MciMvtoInfeccaoTopografias item : lista) {
			item.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de MciMvtoInfeccaoTopografias quando forem
			// migradas
			flush();
		}
	}

	/*
	 * substituindo MPM_ALTA_SUMARIOS, caso exista.  
	 *
		update mpm_alta_sumarios asu
        set asu.pac_codigo     =  p_codigo_destino
		where asu.pac_codigo     =  p_codigo_origem
        and asu.apa_atd_seq  =  v_atd_seq;
	 */
	public void substituirPacienteAltaSumarios (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento
	) {
		if (pacienteOrigem == null || atendimento == null) {
			return;
		}
		
		IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();
		
		List<MpmAltaSumario> lista = prescricaoMedicaFacade.listarAltasSumarios(pacienteOrigem.getCodigo(), atendimento);

		for (MpmAltaSumario item : lista) {
			item.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de MpmAltaSumario quando forem migradas
			flush();
		}
	}

	public void substituirPacienteAtendimentoPacientes (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento
	) {
		if (pacienteOrigem == null || atendimento == null) {
			return;
		}
		
		IAghuFacade aghuFacade = this.getAghuFacade();

		List<AghAtendimentoPacientes> lista = aghuFacade.listarAtendimentosPacientes(pacienteOrigem.getCodigo(),
				atendimento.getSeq());

		for (AghAtendimentoPacientes item : lista) {
			item.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de AghAtendimentoPacientes quando forem migradas
			flush();
		}
	}
	
	/*
	 * Cursor c_apa
	 */
	private List<AbsAmostrasPacientes> amostrasPaciente(AipPacientes paciente, Date dthrInicio) {
		return this.getBancoDeSangueFacade().amostrasPaciente(paciente, dthrInicio);
	}

	/*
	 * 	substituindo ABS_AMOSTRAS_PACIENTES, caso exista.
	 *  substituindo ABS_SOLICITACOES_POR_AMOSTRA, caso exista. 
	 */
	public void substituirPacienteAmostrasPaciente(
			AipPacientes pacienteOrigem, 
			AipPacientes pacienteDestino,
			Date dthrInicio
	) {
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();

		for (AbsAmostrasPacientes amostra : amostrasPaciente(pacienteOrigem, dthrInicio)) {
			Set<AbsSolicitacoesPorAmostra> solicitacoes = amostra.getSolicitacoesPorAmostras();
			AbsAmostrasPacientes novaAmostra = new AbsAmostrasPacientes();
			novaAmostra.setId(new AbsAmostrasPacientesId(
					pacienteDestino == null ? null : pacienteDestino.getCodigo(),
					amostra.getId().getDthrAmostra()
			));
			novaAmostra.setAlteradoEm(amostra.getAlteradoEm());
			novaAmostra.setCriadoEm(amostra.getCriadoEm());
			novaAmostra.setIndSituacao(amostra.getIndSituacao());
			novaAmostra.setNroAmostra(amostra.getNroAmostra());
			novaAmostra.setNroSolicitacoesAtendidas(amostra.getNroSolicitacoesAtendidas());
			novaAmostra.setObservacao(amostra.getObservacao());
			novaAmostra.setRapServidores(amostra.getRapServidores());
			// TODO: Chamar triggers de insert de AbsAmostrasPacientes quando forem migradas
			bancoDeSangueFacade.inserirAbsAmostrasPacientes(novaAmostra, true);
			
			for (AbsSolicitacoesPorAmostra sol : solicitacoes) {
				sol.setAmostraPaciente(novaAmostra);
				novaAmostra.getSolicitacoesPorAmostras().add(sol);
				// TODO: No Oracle era feito DELETE + INSERT.
				//       Aqui estamos fazendo somente UPDATE.
				//       Verificar se existe alguma lógia nas triggers de DELETE e UPDATE
				flush();
			}
		}
		
		for (AbsAmostrasPacientes item : amostrasPaciente(pacienteOrigem, dthrInicio)) {
			// TODO: Chamar triggers de delete de AbsAmostrasPacientes quando forem migradas
			bancoDeSangueFacade.removerAbsAmostrasPacientes(item, true);
		}
	}
	
	/*
	 * substituindo ABS_ESTOQUE_COMPONENTES, caso exista.  
	 *
		update ABS_ESTOQUE_COMPONENTES
        set pac_codigo  =  p_codigo_destino
		where pac_codigo  =  p_codigo_origem
        and criado_em  >= v_dthr_inicio;
	 */
	public void substituirPacienteEstoqueComponentes (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			Date dthrInicio
	) {
		if (pacienteOrigem == null || dthrInicio == null) {
			return;
		}
		
		
		List<AbsEstoqueComponentes> lista = this.getBancoDeSangueFacade().listarEstoquesComponentes(pacienteOrigem.getCodigo(), dthrInicio);

		for (AbsEstoqueComponentes item : lista) {
			item.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de AbsEstoqueComponentes quando forem migradas
			flush();
		}
	}

	/*
	 * substituindo ABS_MOVIMENTOS_COMPONENTES, caso exista.
	 *
		update ABS_MOVIMENTOS_COMPONENTES
        set pac_codigo  =  p_codigo_destino
		where pac_codigo  =  p_codigo_origem
        and criado_em  >= v_dthr_inicio;
	 */
	public void substituirPacienteMovimentosComponentes (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			Date dthrInicio
	) {
		if (pacienteOrigem == null || dthrInicio == null) {
			return;
		}
		
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();
		
		List<AbsMovimentosComponentes> lista = bancoDeSangueFacade.listarMovimentosComponentes(pacienteOrigem.getCodigo(), dthrInicio);

		for (AbsMovimentosComponentes item : lista) {
			item.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de AbsMovimentosComponentes quando forem migradas
			flush();
		}
	}

	public void substituirPacienteRegSanguineoPacientes(AipPacientes pacienteOrigem, AipPacientes pacienteDestino, Date dthrInicio) {
		this.getBancoDeSangueFacade().substituirPacienteRegSanguineoPacientes(pacienteOrigem, pacienteDestino, dthrInicio);
	}

	/*
	 * substituindo AIP_LOG_PRONT_ONLINES, caso exista.
	 *
		update AIP_LOG_PRONT_ONLINES
        set pac_codigo  =  p_codigo_destino
		where pac_codigo  =  p_codigo_origem
        and criado_em  >= v_dthr_inicio;
	 */
	public void substituirPacienteLogProntuariosOnline (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			Date dthrInicio
	) {
		if (pacienteOrigem == null || dthrInicio == null) {
			return;
		}
		
		IPacienteFacade pacienteFacade = this.getPacienteFacade();
		
		List<AipLogProntOnlines> lista = pacienteFacade.listarLogProntOnlines(pacienteOrigem.getCodigo(), dthrInicio);

		for (AipLogProntOnlines item : lista) {
			if(pacienteDestino == null){
				item.setPaciente(null);
			}
			else{
				item.setPaciente(pacienteDestino);
			}
			// TODO: Chamar triggers de AipLogProntOnlines quando forem migradas
			flush();
		}
	}

	/*
		update MPT_TRATAMENTO_TERAPEUTICOS
		set pac_codigo=p_codigo_destino
		where pac_codigo=p_codigo_origem
        and atd_seq  =  v_atd_seq;
	 */
	public void substituirPacienteTratamentosTerapeuticos (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento
	) {
		if (pacienteOrigem == null || atendimento == null) {
			return;
		}
		IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade = this.getProcedimentoTerapeuticoFacade();
		
		List<MptTratamentoTerapeutico> lista = procedimentoTerapeuticoFacade.listarTratamentosTerapeuticos(pacienteOrigem.getCodigo(), atendimento);

		for (MptTratamentoTerapeutico item : lista) {
			item.setPaciente(pacienteDestino);
			// TODO: Chamar triggers de MptTratamentoTerapeutico quando forem
			// migradas
			flush();
		}
	}

	/*
		UPDATE aip_movimentacao_prontuarios
		SET pac_codigo=p_codigo_destino
		WHERE pac_codigo=p_codigo_origem
        and  data_movimento  >= v_dthr_inicio ;
	 */
	public void substituirPacienteMovimentacoesProntuario (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			Date dthrInicio
	) {
		IPacienteFacade pacienteFacade = this.getPacienteFacade();
		
		if (pacienteOrigem == null || dthrInicio == null) {
			return;
		}
		
		List<AipMovimentacaoProntuarios> lista = pacienteFacade.listarMovimentacoesProntuarios(pacienteOrigem, dthrInicio);

		for (AipMovimentacaoProntuarios item : lista) {
			//Obtém o objeto antigo
			AipMovimentacaoProntuarios itemOld = replicarMovimentacaoProntuarios(item);
			item.setPaciente(pacienteDestino);
			pacienteFacade.atualizarMovimentacaoProntuario(item, itemOld);
		}
	}
	
	/**
	 * Replica o objeto AipMovimentacaoProntuarios
	 * @param movimentacaoOriginal
	 * @return
	 */
	public AipMovimentacaoProntuarios replicarMovimentacaoProntuarios(AipMovimentacaoProntuarios movimentacaoOriginal){
		
		AipMovimentacaoProntuarios movimentacaoOld = new AipMovimentacaoProntuarios();
		movimentacaoOld.setSeq(movimentacaoOriginal.getSeq());
		movimentacaoOld.setObservacoes(movimentacaoOriginal.getObservacoes());
		movimentacaoOld.setVolumes(movimentacaoOriginal.getVolumes());
		movimentacaoOld.setTipoEnvio(movimentacaoOriginal.getTipoEnvio());
		movimentacaoOld.setSituacao(movimentacaoOriginal.getSituacao());
		movimentacaoOld.setDataMovimento(movimentacaoOriginal.getDataMovimento());
		movimentacaoOld.setDataRetirada(movimentacaoOriginal.getDataRetirada());
		movimentacaoOld.setDataDevolucao(movimentacaoOriginal.getDataDevolucao());
		movimentacaoOld.setServidor(movimentacaoOriginal.getServidor());
		movimentacaoOld.setServidorRetirado(movimentacaoOriginal.getServidorRetirado());
		movimentacaoOld.setSolicitante(movimentacaoOriginal.getSolicitante());
		movimentacaoOld.setSolicitacao(movimentacaoOriginal.getSolicitacao());
		movimentacaoOld.setLocal(movimentacaoOriginal.getLocal());
		movimentacaoOld.setCriadoEm(movimentacaoOriginal.getCriadoEm());
		movimentacaoOld.setDataCadastroOrigemProntuario(movimentacaoOriginal.getDataCadastroOrigemProntuario());
		movimentacaoOld.setPaciente(movimentacaoOriginal.getPaciente());
		
		return movimentacaoOld;

	}

	public Short obterSequenciaSolicitacoesDoacoes(AipPacientes paciente) {
		if (paciente == null) {
			return 1;
		}

		return this.getBancoDeSangueFacade().obterSequenciaSolicitacoesDoacoes(paciente.getCodigo());
	}

	/*
		update abs_doacoes
		set sdo_pac_codigo=p_codigo_destino,
			sdo_sequencia = p_seq
		where sdo_pac_codigo = p_codigo_origem
			and sdo_sequencia = r1.sequencia;
	 */
	public void atualizarDoacoes (
			AbsSolicitacoesDoacoes solicitacaoOrigem,
			AbsSolicitacoesDoacoes solicitacaoDestino
	) {
		if (solicitacaoOrigem == null) {
			return;
		}
		for (AbsDoacoes doacao : solicitacaoOrigem.getDoacoes()) {
			doacao.setAbsSolicitacoesDoacoes(solicitacaoDestino);
		}
		// TODO: Chamar triggers de AbsDoacoes quando forem migradas		
		this.flush();
	}
	
	public void substituirPacienteSolicitacoesDoacao (
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			Date dthrInicio
	) {
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();
		
		Short sequencia = obterSequenciaSolicitacoesDoacoes(pacienteDestino); 
		
		List<AbsSolicitacoesDoacoes> lista = bancoDeSangueFacade.listarSolicitacoesDoacoes(pacienteOrigem.getCodigo(),
				dthrInicio, LockMode.UPGRADE);

		for (AbsSolicitacoesDoacoes solicitacaoOrigem : lista) {
			AbsSolicitacoesDoacoes novaSolicitacao = new AbsSolicitacoesDoacoes();
			AbsSolicitacoesDoacoesId id = new AbsSolicitacoesDoacoesId(pacienteDestino.getCodigo(), sequencia);
			novaSolicitacao.setId(id);
			novaSolicitacao.setDthrSolicitacao(solicitacaoOrigem.getDthrSolicitacao());
			novaSolicitacao.setNroDoadores(solicitacaoOrigem.getNroDoadores());
			novaSolicitacao.setObservacao(solicitacaoOrigem.getObservacao());
			novaSolicitacao.setDtEncerramento(solicitacaoOrigem.getDtEncerramento());
			novaSolicitacao.setIndSituacao(solicitacaoOrigem.getIndSituacao());
			novaSolicitacao.setSerMatricula(solicitacaoOrigem.getSerMatricula());
			novaSolicitacao.setSerVinCodigo(solicitacaoOrigem.getSerVinCodigo());
			novaSolicitacao.setIndAutomatica(solicitacaoOrigem.getIndAutomatica());
			// TODO: Chamar triggers de insert de AbsSolicitacoesDoacoes quando forem migradas
			bancoDeSangueFacade.inserirAbsSolicitacoesDoacoes(novaSolicitacao, true);
			atualizarDoacoes(solicitacaoOrigem, novaSolicitacao);
			sequencia++;
			// TODO: Chamar triggers de delete de AbsSolicitacoesDoacoes quando forem migradas
			bancoDeSangueFacade.removerAbsSolicitacoesDoacoes(solicitacaoOrigem, true);
		}
	}

	private List<AelProtocoloInternoUnids> listarProtocolosInternosUnids(AipPacientes paciente, Date criadoEm) {
		return this.getExamesLaudosFacade().listarProtocolosInternosUnids(paciente, criadoEm);
	}

	private AelProtocoloInternoUnids copiarProtocoloInternoUnidParaOutroPaciente(
		AelProtocoloInternoUnids piuOrigem, 
		AipPacientes pacienteDestino
	) {
		AelProtocoloInternoUnids piuDestino = new AelProtocoloInternoUnids();
		piuDestino.setId(
				new AelProtocoloInternoUnidsId(
						pacienteDestino == null ? null : pacienteDestino.getCodigo(), 
						piuOrigem.getId().getUnidadeFuncional()
				)					
		);
		piuDestino.setCriadoEm(piuOrigem.getCriadoEm());
		piuDestino.setNroProtocolo(piuOrigem.getNroProtocolo());
		piuDestino.setServidor(piuOrigem.getServidor());
		return piuDestino;
	}
	
	private AelPacUnidFuncionais copiarPacUnidFuncionalParaOutroPaciente(
			AelPacUnidFuncionais pufOrigem,
			AipPacientes pacienteDestino
	) {
		AelPacUnidFuncionais pufDestino = new AelPacUnidFuncionais();
		pufDestino.setId(
				new AelPacUnidFuncionaisId(
						pacienteDestino == null ? null : pacienteDestino.getCodigo(), 
						pufOrigem.getId().getUnidadeFuncional(),
						pufOrigem.getId().getSeqp()
				)
		);
		pufDestino.setUnfExecutaExames(pufOrigem.getUnfExecutaExames());
		pufDestino.setCriadoEm(pufOrigem.getCriadoEm());
		pufDestino.setServidor(pufOrigem.getServidor());
		if(pufOrigem.getItemSolicitacaoExames() != null) {
			pufDestino.setItemSolicitacaoExames(pufOrigem.getItemSolicitacaoExames());
		}
		pufDestino.setDtExecucao(pufOrigem.getDtExecucao());
		pufDestino.setIdentificadorComplementar(pufOrigem.getIdentificadorComplementar());
		pufDestino.setCondicaoPac(pufOrigem.getCondicaoPac());
		pufDestino.setNroFilme(pufOrigem.getNroFilme());
		pufDestino.setObservacao(pufOrigem.getObservacao());
		pufDestino.setServidorAlterado(pufOrigem.getServidorAlterado());
		pufDestino.setAlteradoEm(pufOrigem.getAlteradoEm());
		pufDestino.setEquipamento(pufOrigem.getEquipamento());
		return pufDestino;
	}
	
	/*
	 * ORADB: sub procedure ins_del_piu_puf (dentro da procedure AIPP_SUBS_PRONT_ATD) 
	 */
	public void substituirPacienteProtocoloInternoUnidadesPacUnidadesFuncionais(
			AipPacientes pacienteOrigem, 
			AipPacientes pacienteDestino,
			Date dthrInicio
	) throws BaseException {
		IExamesLaudosFacade examesLaudosFacade = this.getExamesLaudosFacade();
		
		List<AelProtocoloInternoUnids> protIntUnidLista = new ArrayList<AelProtocoloInternoUnids>();
		List<AelPacUnidFuncionais> pacUnidFuncLista = new ArrayList<AelPacUnidFuncionais>();
		
		// Cria tudo em memória para não violar a constraint AEL_PIU_UK1 (UNF_SEQ, NRO_PROTOCOLO)
		for (AelProtocoloInternoUnids piuOrigem : listarProtocolosInternosUnids(pacienteOrigem, dthrInicio)) {			
			protIntUnidLista.add(copiarProtocoloInternoUnidParaOutroPaciente(piuOrigem, pacienteDestino));
			for (AelPacUnidFuncionais pufOrigem : piuOrigem.getPacUnidFuncionais()) {
				pacUnidFuncLista.add(copiarPacUnidFuncionalParaOutroPaciente(pufOrigem, pacienteDestino));
			}
		}
		
		for (AelProtocoloInternoUnids piuOrigem : listarProtocolosInternosUnids(pacienteOrigem, dthrInicio)) {
			for (AelPacUnidFuncionais pufOrigem : piuOrigem.getPacUnidFuncionais()) {
				examesLaudosFacade.excluirAelPacienteUnidadeFuncional(pufOrigem);
			}
			examesLaudosFacade.excluirAelProtocoloInternoUnids(piuOrigem);
		}
		
		for (AelProtocoloInternoUnids piu : protIntUnidLista) {
			examesLaudosFacade.inserirAelProtocoloInternoUnids(piu);
		}
		for (AelPacUnidFuncionais puf : pacUnidFuncLista) {
			examesLaudosFacade.inserirAelPacienteUnidadeFuncional(puf);
		}
		
	}
	
	/*
	 * ORADB: Procedure AIPP_SUBS_PRONT_ATD
	 */
	public void substituirProntuarioAtendimento(
			AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino,
			AghAtendimentos atendimento, String nomeMicrocomputador,RapServidores servidorLogado, final Date dataFimVinculoServidor
	) throws BaseException {
		substituirPacienteAtendimentoUrgencia(pacienteOrigem, pacienteDestino, atendimento.getAtendimentoUrgencia(), nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		substituirPacienteExtratoLeito(pacienteOrigem, pacienteDestino, atendimento.getInternacao());
		substituirPacienteCirurgias(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteConsulta(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteProcedimentosAmbulatorialRealizados(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteAtendimento(pacienteOrigem, pacienteDestino, atendimento, nomeMicrocomputador);
		substituirPacienteMvtoFatorPredisponentes(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteMvtoProcedimentosRisco(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteMvtoMedidasPreventivas(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteMvtoInfeccaoTopografias(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteAltaSumarios(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteAtendimentoPacientes(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteAmostrasPaciente(pacienteOrigem, pacienteDestino, atendimento.getDthrInicio());
		substituirPacienteEstoqueComponentes(pacienteOrigem, pacienteDestino, atendimento.getDthrInicio());
		substituirPacienteRegSanguineoPacientes(pacienteOrigem, pacienteDestino, atendimento.getDthrInicio());
		substituirPacienteMovimentosComponentes(pacienteOrigem, pacienteDestino, atendimento.getDthrInicio());
		substituirPacienteRegSanguineoPacientes(pacienteOrigem, pacienteDestino, atendimento.getDthrInicio());
		substituirPacienteLogProntuariosOnline(pacienteOrigem, pacienteDestino, atendimento.getDthrInicio());
		substituirPacienteTratamentosTerapeuticos(pacienteOrigem, pacienteDestino, atendimento);
		substituirPacienteMovimentacoesProntuario(pacienteOrigem, pacienteDestino, atendimento.getDthrInicio());
		substituirPacienteSolicitacoesDoacao(pacienteOrigem, pacienteDestino, atendimento.getDthrInicio());
		substituirPacienteProtocoloInternoUnidadesPacUnidadesFuncionais(pacienteOrigem, pacienteDestino, atendimento.getDthrInicio());
	}

	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	protected InternacaoRN getInternacaoRN() {
		return internacaoRN;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return this.bancoDeSangueFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO() {
		return ainAtendimentosUrgenciaDAO;
	}

	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO() {
		return ainExtratoLeitosDAO;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return this.prescricaoMedicaFacade;
	}
	
	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade(){
		return this.procedimentoTerapeuticoFacade;
	}
	
	protected IControleInfeccaoFacade getControleInfeccaoFacade() {
		return this.controleInfeccaoFacade;
	}

}
