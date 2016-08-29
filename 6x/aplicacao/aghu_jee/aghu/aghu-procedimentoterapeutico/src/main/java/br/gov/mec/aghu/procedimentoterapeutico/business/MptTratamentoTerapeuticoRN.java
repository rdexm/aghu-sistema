package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatTipoTratamentos;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTratamentoTerapeuticoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MptTratamentoTerapeuticoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MptTratamentoTerapeuticoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -1928348360354193051L;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private MptTratamentoTerapeuticoDAO mptTratamentoTerapeuticoDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	public enum MptTratamentoTerapeuticoRNExceptionCode implements BusinessExceptionCode {
		MPT_00189, MPT_00190, MPT_00191, MPT_00258, MPT_00451, MPT_00564;
		
	}
	
	public void inserirMptTratamentoTerapeuticoDialise(Short unfSeqDialise, Short espSeqDialise, Integer pacCodigo,
			Integer tipoTratDialise, Short cspCnvCodigo, Byte cspSeq, Integer matriculaResp, Short vinCodigoResp, String nomeMicrocomputador) throws BaseException {
		
		MptTratamentoTerapeutico tratamentoTerapeutico = new MptTratamentoTerapeutico();
		tratamentoTerapeutico.setDthrInicio(new Date());
		tratamentoTerapeutico.setUnfSeq(unfSeqDialise);
		tratamentoTerapeutico.setPaciente(this.pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo));
		tratamentoTerapeutico.setEspSeq(espSeqDialise);
		tratamentoTerapeutico.setTptSeq(tipoTratDialise);
		tratamentoTerapeutico.setCspCnvCodigo(cspCnvCodigo);
		tratamentoTerapeutico.setFatConvenioSaude(this.faturamentoApoioFacade.obterConvenioSaude(cspSeq.shortValue()));
		tratamentoTerapeutico.setServidorResponsavel(this.registroColaboradorFacade.buscarServidor(vinCodigoResp, matriculaResp));
		
		this.beforeInsertMptTratamentoTerapeutico(tratamentoTerapeutico);
		
		this.mptTratamentoTerapeuticoDAO.persistir(tratamentoTerapeutico);
		
		Integer matricula = tratamentoTerapeutico.getServidorResponsavel().getId().getMatricula();
		Short vinCodigo = tratamentoTerapeutico.getServidorResponsavel().getId().getVinCodigo();
		Short espSeq = tratamentoTerapeutico.getEspSeq();
		Short unfSeq = tratamentoTerapeutico.getUnfSeq();
		Date dthrInicio = tratamentoTerapeutico.getDthrInicio();
		Integer pacCod = tratamentoTerapeutico.getPaciente().getCodigo();
		Integer tptSeq = tratamentoTerapeutico.getTptSeq();
		Integer trpSeq = tratamentoTerapeutico.getSeq();
		
		this.incluiAtendimentoTratamentoTerapeutico(pacCod, unfSeq, espSeq, trpSeq, matricula, vinCodigo, dthrInicio, tptSeq, nomeMicrocomputador);
	}
	
	/**
	 * @ORADB TRIGGER MPTT_TRP_BRI
	 * @param tratamentoTerapeutico
	 * @throws ApplicationBusinessException
	 */
	public void beforeInsertMptTratamentoTerapeutico(MptTratamentoTerapeutico tratamentoTerapeutico) throws ApplicationBusinessException {
		
		RapServidores servidorLogado = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		tratamentoTerapeutico.setServidor(servidorLogado);
		tratamentoTerapeutico.setCriadoEm(new Date());
		
		Integer matriculaResp = tratamentoTerapeutico.getServidorResponsavel().getId().getMatricula();
		Short vinCodigoResp = tratamentoTerapeutico.getServidorResponsavel().getId().getVinCodigo();
		Short espSeq = tratamentoTerapeutico.getEspSeq();
		Short unfSeq = tratamentoTerapeutico.getUnfSeq();
		Date dthrInicio = tratamentoTerapeutico.getDthrInicio();
		Integer pacCodigo = tratamentoTerapeutico.getPaciente().getCodigo();
		Integer tptSeq = tratamentoTerapeutico.getTptSeq();
		
		this.validaServidorResponsavelAtivo(matriculaResp, vinCodigoResp, espSeq);
		
		this.validaUnidadeFuncionalAtiva(unfSeq);
		
		this.validaConvenioSaudePlano(tratamentoTerapeutico.getFatConvenioSaude());
		
		if (tratamentoTerapeutico.getAtendimento() != null) {
			this.validaAtendimentoVigente(tratamentoTerapeutico.getAtendimento().getSeq());
		}
		
		this.validaDataInicioTratamento(dthrInicio, pacCodigo);
		
		// @ORADB PROCEDURE RN_TRPP_VER_DT_FIM
		if (tratamentoTerapeutico.getDthrFim() != null) {
			throw new ApplicationBusinessException(MptTratamentoTerapeuticoRNExceptionCode.MPT_00564);
		}
		
		tratamentoTerapeutico.setDthrInicio(this.verificaAtualizacaoDthrInicio(pacCodigo, espSeq, tptSeq, dthrInicio));
	}
	
	/**
	 * @ORADB PROCEDURE RN_TRPP_SERV_ATIVO
	 * @param matricula
	 * @param codigo
	 * @param espSeq
	 * @throws ApplicationBusinessException
	 */
	public void validaServidorResponsavelAtivo(Integer matricula, Short codigo, Short espSeq) throws ApplicationBusinessException {
		AghProfEspecialidadesId id = new AghProfEspecialidadesId(matricula, codigo, espSeq);
		AghProfEspecialidades profEspecialidade = this.aghuFacade.obterProfEspecialidadeComServidorAtivoProgramado(id);
		
		if (profEspecialidade == null
				|| (profEspecialidade.getRapServidor().getIndSituacao().equals(DominioSituacaoVinculo.P)
						&& DateUtil.validaDataMenorIgual(profEspecialidade.getRapServidor().getDtFimVinculo(), new Date()))) {
			
			throw new ApplicationBusinessException(MptTratamentoTerapeuticoRNExceptionCode.MPT_00189);
		}
	}
	
	/**
	 * @ORADB PROCEDURE RN_TRPP_UNID_FUNC
	 * @param unfSeqDialise
	 * @throws ApplicationBusinessException
	 */
	public void validaUnidadeFuncionalAtiva(Short unfSeqDialise) throws ApplicationBusinessException {
		AghUnidadesFuncionais unidade = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeqDialise);
		if (unidade.getIndSitUnidFunc().equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(MptTratamentoTerapeuticoRNExceptionCode.MPT_00190);
		}
	}
	
	/**
	 * @ORADB PROCEDURE RN_TRPP_CONV_PLANO
	 * @param convenioSaude
	 * @throws ApplicationBusinessException
	 */
	public void validaConvenioSaudePlano(FatConvenioSaude convenioSaude) throws ApplicationBusinessException {
		if (convenioSaude.getSituacao().equals(DominioSituacao.I)) {
			throw new ApplicationBusinessException(MptTratamentoTerapeuticoRNExceptionCode.MPT_00191);
		}
	}
	
	/**
	 * @ORADB PROCEDURE RN_TRPP_ATD_VIGENTE
	 * @param atdSeq
	 * @throws ApplicationBusinessException
	 */
	public void validaAtendimentoVigente(Integer atdSeq) throws ApplicationBusinessException {
		AghAtendimentos atendimento = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
		if (!atendimento.getOrigem().equals(DominioOrigemAtendimento.A)) {
			throw new ApplicationBusinessException(MptTratamentoTerapeuticoRNExceptionCode.MPT_00258);
		}
	}
	
	/**
	 * @ORADB PROCEDURE RN_TRPP_DT_INICIO
	 * @param dthrInicioTrat
	 * @param pacCodigo
	 * @throws ApplicationBusinessException
	 */
	public void validaDataInicioTratamento(Date dthrInicioTrat, Integer pacCodigo) throws ApplicationBusinessException {
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo);
		if (DateUtil.validaDataMenor(DateUtil.truncaData(dthrInicioTrat), DateUtil.truncaData(paciente.getDtNascimento()))) {
			throw new ApplicationBusinessException(MptTratamentoTerapeuticoRNExceptionCode.MPT_00451);
		}
	}
	
	/**
	 * @ORADB PROCEDURE RN_TRPP_VER_ATU_INIC
	 * @param pacCodigo
	 * @param espSeq
	 * @param tptSeq
	 * @param dthrInicio
	 * @return
	 */
	public Date verificaAtualizacaoDthrInicio(Integer pacCodigo, Short espSeq, Integer tptSeq, Date dthrInicio) {
		FatTipoTratamentos tratamento = this.faturamentoApoioFacade.obterFatTipoTratamentosPorChavePrimaria(tptSeq);
		DominioTipoTratamentoAtendimento indTipoTratamento = this.obterTipoTratamentoPorCodigo(tratamento.getCodigo());
		
		List<AacAtendimentoApacs> listaDthrInicio = this.ambulatorioFacade.obterDataInicioAtendimentoExistente(pacCodigo, espSeq, indTipoTratamento);
		
		if (!listaDthrInicio.isEmpty()) {
			dthrInicio = listaDthrInicio.get(0).getAtendimento().getDthrInicio();
		}
		return dthrInicio;
	}
	
	/**
	 * @ORADB PROCEDURE RN_TRPP_INCLUI_ATD
	 * @param pacCodigo
	 * @param unfSeq
	 * @param espSeq
	 * @param trpSeq
	 * @param atdSeq
	 * @param matriculaResp
	 * @param vinCodigoResp
	 * @param dthrInicio
	 * @param tptSeq
	 * @throws BaseException 
	 */
	public void incluiAtendimentoTratamentoTerapeutico(Integer pacCodigo, Short unfSeq, Short espSeq, Integer trpSeq,
			Integer matriculaResp, Short vinCodigoResp, Date dthrInicio, Integer tptSeq, String nomeMicrocomputador) throws BaseException {
		
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo);
		
		FatTipoTratamentos tratamento = this.faturamentoApoioFacade.obterFatTipoTratamentosPorChavePrimaria(tptSeq);
		DominioTipoTratamentoAtendimento indTipoTratamento = this.obterTipoTratamentoPorCodigo(tratamento.getCodigo());
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		List<AacAtendimentoApacs> listaDthrInicio = this.ambulatorioFacade.obterDataInicioAtendimentoExistente(pacCodigo, espSeq, indTipoTratamento);
		
		if (listaDthrInicio.isEmpty()) {
			AghAtendimentos atendimento = new AghAtendimentos();
			atendimento.setPaciente(paciente);
			atendimento.setDthrInicio(dthrInicio);
			atendimento.setIndPacPediatrico(Boolean.FALSE);
			atendimento.setIndPacPrematuro(Boolean.FALSE);
			atendimento.setIndPacAtendimento(DominioPacAtendimento.N);
			atendimento.setUnidadeFuncional(this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq));
			atendimento.setEspecialidade(this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(espSeq));
			atendimento.setProntuario(paciente.getProntuario());
			atendimento.setIndResultadoNaoConsultado(Boolean.FALSE);
			atendimento.setOrigem(DominioOrigemAtendimento.A);
			atendimento.setTrpSeq(trpSeq);
			atendimento.setIndTipoTratamento(indTipoTratamento);
			atendimento.setServidor(this.registroColaboradorFacade.buscarServidor(vinCodigoResp, matriculaResp));
			atendimento.setDthrIngressoUnidade(new Date());
			atendimento.setTptSeq(tptSeq);
			
			this.pacienteFacade.inserirAtendimento(atendimento, null);
			
		} else {
			AacAtendimentoApacs atendimentoApac = listaDthrInicio.get(0);
			Date dthrIngressoUnidade = null;
			if (!atendimentoApac.getUnfSeq().equals(unfSeq)) {
				dthrIngressoUnidade = new Date();
			}
			
			AghAtendimentos atendimento = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(atendimentoApac.getAtendimento().getSeq());
			atendimento.setDthrInicio(dthrInicio);
			atendimento.setUnidadeFuncional(this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq));
			atendimento.setEspecialidade(this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(espSeq));
			atendimento.setTrpSeq(trpSeq);
			atendimento.setServidor(this.registroColaboradorFacade.buscarServidor(vinCodigoResp, matriculaResp));
			if (atendimento.getDthrIngressoUnidade() != null && dthrIngressoUnidade != null) {
				atendimento.setDthrIngressoUnidade(dthrIngressoUnidade);
				
			} else if (atendimento.getDthrIngressoUnidade() == null) {
				atendimento.setDthrIngressoUnidade(new Date());
			}
			atendimento.setTptSeq(tptSeq);
			
			this.pacienteFacade.atualizarAtendimento(atendimento, this.pacienteFacade.obterAghAtendimentosOriginal(atendimento),
					nomeMicrocomputador, servidorLogado, new Date());
		}
	}
	
	private DominioTipoTratamentoAtendimento obterTipoTratamentoPorCodigo(Integer codigo) {
		DominioTipoTratamentoAtendimento retorno = null;
		switch (codigo) {
		case 4:
			retorno = DominioTipoTratamentoAtendimento.VALOR_4;
			break;
		case 8:
			retorno = DominioTipoTratamentoAtendimento.VALOR_8;
			break;
		case 11:
			retorno = DominioTipoTratamentoAtendimento.VALOR_11;
			break;
		case 13:
			retorno = DominioTipoTratamentoAtendimento.VALOR_13;
			break;
		case 14:
			retorno = DominioTipoTratamentoAtendimento.VALOR_14;
			break;
		case 19:
			retorno = DominioTipoTratamentoAtendimento.VALOR_19;
			break;
		case 26:
			retorno = DominioTipoTratamentoAtendimento.VALOR_26;
			break;
		case 27:
			retorno = DominioTipoTratamentoAtendimento.VALOR_27;
			break;
		case 28:
			retorno = DominioTipoTratamentoAtendimento.VALOR_28;
			break;
		case 29:
			retorno = DominioTipoTratamentoAtendimento.VALOR_29;
			break;
		case 30:
			retorno = DominioTipoTratamentoAtendimento.VALOR_30;
			break;
		case 32:
			retorno = DominioTipoTratamentoAtendimento.VALOR_32;
			break;
		case 33:
			retorno = DominioTipoTratamentoAtendimento.VALOR_33;
			break;
		case 35:
			retorno = DominioTipoTratamentoAtendimento.VALOR_35;
			break;
		case 36:
			retorno = DominioTipoTratamentoAtendimento.VALOR_36;
			break;
		case 833:
			retorno = DominioTipoTratamentoAtendimento.VALOR_38;
			break;
		}
		return retorno;
	}
}
