package br.gov.mec.aghu.ambulatorio.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.jfree.util.Log;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.BoletimAtendimento;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaEspecialidadeAlteradaRetornoVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaEspecialidadeAlteradaVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaGradeAtendimentoVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.Especialidade;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendaVo;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioServiceVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.LaudoAihVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAgendamentoConsultaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
@Deprecated
public class AmbulatorioServiceImpl implements IAmbulatorioService {

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private MessagesUtils messagesUtils;
	
	
	@Override
	public BoletimAtendimento obterBoletimAtendimentoPelaConsulta(Integer nroConsulta) throws ServiceException, ApplicationBusinessException {
		List<RelatorioAgendamentoConsultaVO> vos = ambulatorioFacade.obterAgendamentoConsulta(nroConsulta);
		if(vos != null && !vos.isEmpty()) {
			BoletimAtendimento boletim = new BoletimAtendimento();
			try {
				BeanUtils.copyProperties(boletim, vos.get(0));
			} catch(IllegalAccessException | InvocationTargetException e1) {
				return null;		
			}
			return boletim;
		}
		return null;
	}
	
	@Override
	public Especialidade obterEspecialidadeDaConsulta(Integer numeroConsulta) throws ServiceException, ApplicationBusinessException{
		AacConsultas consulta = ambulatorioFacade.obterAacConsulta(numeroConsulta);
		if(consulta != null && consulta.getAtendimento() != null && consulta.getAtendimento().getEspecialidade() != null)  {
			return new Especialidade(consulta.getAtendimento().getEspecialidade().getSeq(), 
					consulta.getAtendimento().getEspecialidade().getSigla(), consulta.getAtendimento().getEspecialidade().getNomeEspecialidade());
		}
		return null;
	}
		
	@Override
	public GradeAgendaVo obterGradesAgendaPorEspecialidadeDataLimiteParametros(Short espSeq, 
			Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg , Date dataLimite) throws ServiceException {
		AacGradeAgendamenConsultas  aacGradeAgendamenConsultas = this.ambulatorioFacade.obterGradesAgendaPorEspecialidadeDataLimiteParametros(espSeq, pPagadorSUS, pTagDemanda, pCondATEmerg, dataLimite);
				
		GradeAgendaVo gradeAgendaVO = null;
				
		if (aacGradeAgendamenConsultas != null){
			gradeAgendaVO = new GradeAgendaVo();		
		    gradeAgendaVO.setGradeSeq(aacGradeAgendamenConsultas.getSeq());
		    gradeAgendaVO.setUnidadeFuncionalSeq(aacGradeAgendamenConsultas.getUnidadeFuncional().getSeq());
		}
		
		return gradeAgendaVO;
	}
	
	
	@Override
	public Integer inserirConsultaEmergencia(Date dataConsulta, Integer gradeSeq, Integer pacCodigo, Short pConvenioSUSPadrao,
										  Byte pSusPlanoAmbulatorio, Short pPagadorSUS, Short pTagDemanda,
										  Short pCondATEmerg, String nomeMicrocomputador) throws ServiceException {
		
		AacConsultas consulta = new AacConsultas();
		consulta.setDtConsulta(dataConsulta);
		consulta.setExcedeProgramacao(Boolean.TRUE);
		consulta.setGradeAgendamenConsulta(this.ambulatorioFacade.obterGradeAgendamentoParaMarcacaoConsultaEmergencia(gradeSeq));
		consulta.setPaciente(this.pacienteFacade.buscaPaciente(pacCodigo));		
		if (pConvenioSUSPadrao != null && pSusPlanoAmbulatorio != null) {
		consulta.setConvenioSaudePlano(this.faturamentoFacade.obterFatConvenioSaudePlano(pConvenioSUSPadrao, pSusPlanoAmbulatorio));
		}
		if (pCondATEmerg != null) {
		consulta.setCondicaoAtendimento(this.ambulatorioFacade.obterCondicaoAtendimentoPorCodigo(pCondATEmerg));
		}
		if(pTagDemanda != null) {
		consulta.setTipoAgendamento(this.ambulatorioFacade.obterTipoAgendamentoPorCodigo(pTagDemanda));
		}
		if (pPagadorSUS != null) {
		consulta.setPagador(this.ambulatorioFacade.obterPagadorPorCodigo(pPagadorSUS));
		}
		consulta.setSituacaoConsulta(this.ambulatorioFacade.obterSituacaoMarcada());

		AacFormaAgendamentoId formaAgendamentoId = new AacFormaAgendamentoId();
		formaAgendamentoId.setCaaSeq(Short.valueOf(String.valueOf(pCondATEmerg)));
		formaAgendamentoId.setPgdSeq(pPagadorSUS);
		formaAgendamentoId.setTagSeq(pTagDemanda);
									
        AacFormaAgendamento formaAgendamento = this.ambulatorioFacade.obterAacFormaAgendamentoPorChavePrimaria(formaAgendamentoId);
        consulta.setFormaAgendamento(formaAgendamento);
		
		AacConsultas consultaRetorno = null;
		try {
			consultaRetorno = this.ambulatorioFacade.inserirConsulta(consulta, nomeMicrocomputador,
					false, true, true, true);
		} catch (NumberFormatException e) {
			throw new ServiceException(e.getMessage());

		} catch (BaseException e) {
			throw new ServiceException(messagesUtils.getResourceBundleValue(e));
		}
		
		return (consultaRetorno != null) ? consultaRetorno.getNumero() : null;
	}
	
	
	@Override
	public Long pesquisarConsultasPorEspecialidade(Short espSeq, List<Integer> consultasPacientesEmAtendimento) {		
       return this.ambulatorioFacade.pesquisarConsultasPorEspecialidade(espSeq, consultasPacientesEmAtendimento);				
	}
	
	public List<ConsultaVO> obterConsultasPorNumero(List<Integer> numeros) throws ServiceException {
		
		List<AacConsultas> consultas = this.ambulatorioFacade.obterConsultasPorNumero(numeros);
		
		List<ConsultaVO> listaRetorno = new ArrayList<ConsultaVO>();
		for (AacConsultas consulta : consultas) {
			ConsultaVO vo = new ConsultaVO();
			vo.setDthrInicio(consulta.getDthrInicio());
			vo.setNumero(consulta.getNumero());
			
			listaRetorno.add(vo);
		}
		return listaRetorno;
	}
	
	
	/**
	 * #34380 Obter se foi alterado especialidade de uma consulta
	 */
	public List<ConsultaEspecialidadeAlteradaRetornoVO> obterConsultasEspecialidadeAlterada(Short espSeq, Integer grdSeq, Integer conNumero) throws ServiceException {
		List<ConsultaEspecialidadeAlteradaVO> consultas =  this.ambulatorioFacade.obterConsultasEspecialidadeAlterada(espSeq, grdSeq, conNumero);
		List<ConsultaEspecialidadeAlteradaRetornoVO> listaRetorno = new ArrayList<ConsultaEspecialidadeAlteradaRetornoVO>();
		
		for (ConsultaEspecialidadeAlteradaVO consulta : consultas) {
			ConsultaEspecialidadeAlteradaRetornoVO vo = new ConsultaEspecialidadeAlteradaRetornoVO();
			vo.setEspSeq(espSeq);
			vo.setGrdSeq(consulta.getGrdSeq());
			vo.setJnDateTime(consulta.getJnDateTime());
			
			listaRetorno.add(vo);
		}
		return listaRetorno;
	}
	
	
	/**
	 * #34389 e #34391 
	 * Obter informações sobre consulta por número de consulta e especialidade
	 */
	public List<ConsultaGradeAtendimentoVO> pesquisarPorConsultaPorNumeroConsultaEspecialidade(List<Integer> conNumero, Short espSeq) throws ServiceException {
		List<AacConsultas> consultas = this.ambulatorioFacade.pesquisarPorConsultaPorNumeroConsultaEspecialidade(conNumero, espSeq);
		List<ConsultaGradeAtendimentoVO> listaRetorno = new ArrayList<ConsultaGradeAtendimentoVO>();
		for (AacConsultas aacConsultas : consultas) {
			ConsultaGradeAtendimentoVO vo = new ConsultaGradeAtendimentoVO();
			vo.setConNumero(aacConsultas.getNumero());
			vo.setDthrInicio(aacConsultas.getDthrInicio());
			vo.setDtConsulta(aacConsultas.getDtConsulta());
			
			if (aacConsultas.getNumero() != null) {
				vo.setAtdSeq(this.ambulatorioFacade.obterAtendimentoPorConNumero(aacConsultas.getNumero()));
			}
	
			if(aacConsultas.getGradeAgendamenConsulta()!= null){
				vo.setGrdSeq(aacConsultas.getGradeAgendamenConsulta().getSeq());
				if (aacConsultas.getGradeAgendamenConsulta().getEspecialidade()!= null ) {
					vo.setEspSeq(aacConsultas.getGradeAgendamenConsulta().getEspecialidade().getSeq());		
					vo.setEspSigla(aacConsultas.getGradeAgendamenConsulta().getEspecialidade().getSigla());
				}
			}

			listaRetorno.add(vo);	
		}
		return listaRetorno;
		
	}
	
	
	
		
	/**
	 * Webservice #35100
	 */
	@Override
	public List<GradeAgendamentoAmbulatorioServiceVO> listarQuantidadeConsultasAmbulatorio(List<String> sitConsultas, Short espSeq,
			String situacaoMarcado, Short pPagadorSUS, Short pTagDemanda,
			Short pCondATEmerg, boolean isDtConsultaMaiorDtAtual) throws ServiceException {
		
		List<GradeAgendamentoAmbulatorioVO> consultas = ambulatorioFacade
				.listarQuantidadeConsultasAmbulatorio(sitConsultas, espSeq, situacaoMarcado, pPagadorSUS, pTagDemanda,
						pCondATEmerg, isDtConsultaMaiorDtAtual);
		
		List<GradeAgendamentoAmbulatorioServiceVO> listaRetorno = new ArrayList<GradeAgendamentoAmbulatorioServiceVO>();
		for (GradeAgendamentoAmbulatorioVO item : consultas) {
			GradeAgendamentoAmbulatorioServiceVO vo = new GradeAgendamentoAmbulatorioServiceVO();
			try {
				PropertyUtils.copyProperties(vo, item);
			} catch (Exception e) {
				Log.error("Erro", e);
			}
			listaRetorno.add(vo);	
		}
		return listaRetorno;
	}
	
	/**
	 * Webservice #34515
	 */
	@Override
	public Integer atualizarConsultaEmergencia(Integer numeroConsulta, Integer pacCodigo, Date dataHoraInicio, Short pConvenioSUSPadrao, Byte pSusPlanoAmbulatorio,
			String indSitConsulta, String nomeMicrocomputador) throws ServiceException {
		
		AacConsultas consulta = this.ambulatorioFacade.obterAacConsulta(numeroConsulta);
		consulta.setPaciente(this.pacienteFacade.buscaPaciente(pacCodigo));
		if (pConvenioSUSPadrao != null && pSusPlanoAmbulatorio != null) {
			consulta.setConvenioSaudePlano(this.faturamentoFacade.obterFatConvenioSaudePlano(pConvenioSUSPadrao, pSusPlanoAmbulatorio));
		}
		if (indSitConsulta != null){
			consulta.setSituacaoConsulta(this.ambulatorioFacade.pesquisarSituacaoConsultaPeloId(indSitConsulta));
		}
		
		if (dataHoraInicio != null){
			consulta.setDthrInicio(dataHoraInicio);
		}
		
		// #49832
		AacRetornos retorno = this.ambulatorioFacade.obterRetorno(DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
		consulta.setRetorno(retorno);
		
		AacConsultas consultaRetorno = null;
		
		try {
			consultaRetorno = this.ambulatorioFacade.atualizarConsulta(consulta, null, nomeMicrocomputador, new Date(), false,
					false, false, false);
		} catch (NumberFormatException e) {
			throw new ServiceException(e.getMessage());

		} catch (BaseException e) {
			throw new ServiceException(messagesUtils.getResourceBundleValue(e));
		}
		return (consultaRetorno != null) ? consultaRetorno.getNumero() : null;
	}

	@Override
	public List<Integer> pesquisarConsultaPorPacienteUnidadeFuncional(Integer pacCodigo, Short unfSeq1, Short unfSeq2) throws ServiceException {
		if (pacCodigo == null || unfSeq1 == null || unfSeq2 == null) {
			throw new ServiceException("Parâmetros obrigatórios");
		}
		try {
			return ambulatorioFacade.pesquisarConsultaPorPacienteUnidadeFuncional(pacCodigo, unfSeq1, unfSeq2);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Date> pesquisarPorPacienteConsultaGestacao(Integer pacCodigo, Integer conNumero, Short gsoSeqp) throws ServiceException {
		if (pacCodigo == null || conNumero == null || gsoSeqp == null) {
			throw new ServiceException("Parâmetros obrigatórios");
		}
		try {
			return ambulatorioFacade.pesquisarPorPacienteConsultaGestacao(pacCodigo, conNumero, gsoSeqp);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<LaudoAihVO> pesquisarLaudosAihPorConsultaPaciente(Integer conNumero, Integer pacCodigo) throws ServiceException {
		if (conNumero == null || pacCodigo == null) {
			throw new ServiceException("Parâmetros obrigatórios");
		}
		try {
			List<LaudoAihVO> result = new ArrayList<LaudoAihVO>();
			List<MamLaudoAih> list = ambulatorioFacade.pesquisarLaudosAihPorConsultaPaciente(conNumero, pacCodigo);
			if (list != null && !list.isEmpty()) {
				for (MamLaudoAih mamLaudoAih : list) {
					LaudoAihVO laudo = new LaudoAihVO();
					laudo.setSeqEspecialidade(mamLaudoAih.getEspecialidade() != null ? mamLaudoAih.getEspecialidade().getSeq() : null);
					if (mamLaudoAih.getServidorRespInternacao() != null) {
						laudo.setMatricula(mamLaudoAih.getServidorRespInternacao().getId().getMatricula());
						laudo.setVinCodigo(mamLaudoAih.getServidorRespInternacao().getId().getVinCodigo());
					} else {
						laudo.setMatricula(null);
						laudo.setVinCodigo(null);
					}
					if (mamLaudoAih.getFatItemProcedHospital() != null) {
						laudo.setSeqIPH(mamLaudoAih.getFatItemProcedHospital().getId().getSeq());
						laudo.setSeqPHO(mamLaudoAih.getFatItemProcedHospital().getId().getPhoSeq());
					} else {
						laudo.setSeqIPH(null);
						laudo.setSeqPHO(null);
					}
					laudo.setSeqCID(mamLaudoAih.getAghCid() != null ? mamLaudoAih.getAghCid().getSeq() : null);

					result.add(laudo);
				}
			}
			return result;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public Short obtemEspecialidadeDaGradePeloNumeroConsulta(Integer conNumero) {
		return ambulatorioFacade.obtemCodigoEspecialidadeGradePeloNumeroConsulta(conNumero);
	}
	
	@Override
	public Short obterUnidadeAssociadaAgendaPorNumeroConsulta(Integer conNumero){
		return this.ambulatorioFacade.obterUnidadeAssociadaAgendaPorNumeroConsulta(conNumero);
	}

	@Override
	public Boolean validarProcesso(Short serVinCodigo, Integer serMatricula, Short seqProcesso) throws ApplicationBusinessException {
		return this.ambulatorioFacade.validarProcesso(serVinCodigo, serMatricula, seqProcesso);
	}

	@Override
	public Integer obterAtendimentoPorConNumero(Integer conNumero) {
		return this.ambulatorioFacade.obterAtendimentoPorConNumero(conNumero);
	}
	
	@Override
	public Integer obterAtdSeqPorNumeroConsulta(Integer numeroConsulta){
		return this.ambulatorioFacade.obterAtdSeqPorNumeroConsulta(numeroConsulta);
	}
}