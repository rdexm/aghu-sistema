package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.dominio.DominioConformidadeHorarioSessao;
import br.gov.mec.aghu.dominio.DominioControleFrequenciaSituacao;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.faturamento.dao.FatContaApacDAO;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptControleFrequencia;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendamentoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptControleFrequenciaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptExtratoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptHorarioSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCicloDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTratamentoTerapeuticoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTurnoTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentosPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioReservadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorariosAgendamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAgendadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.protocolos.dao.MpaProtocoloAssistencialDAO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MptHorarioSessaoRN extends BaseBusiness {

	private static final String COR_VERDE = "#93C47D";

	private static final String COR_AZUL = "#00FFFF";

	private static final String COR_AMARELA = "#FFFFD9";

	private static final Log LOG = LogFactory.getLog(MptHorarioSessaoRN.class);
	
	private static final String DIAS_PRIMEIRA_CONSULTA = "P_DIAS_PRIMEIRA_CONSULTA";
	
	private static final String P_TEMPO_ATU_MANIPULACAO = "P_TEMPO_ATU_MANIPULACAO";

	private static final String SITUACAO_CONSULTA_M = String.valueOf('M');

	private static final String VIRGULA = String.valueOf(',');

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -9038521069189468941L;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private AgendamentoSessaoRN agendamentoSessaoRN;
	
	@Inject
	private MptAgendamentoSessaoDAO mptAgendamentoSessaoDAO;
	
	@Inject
	private MpaProtocoloAssistencialDAO mpaProtocoloAssistencialDAO;
	
	@Inject
	private MptLocalAtendimentoDAO mptLocalAtendimentoDAO;
	
	@Inject
	private MptHorarioSessaoDAO mptHorarioSessaoDAO;

	@Inject
	private MptSessaoDAO mptSessaoDAO;

	@Inject
	private MptProtocoloCicloDAO mptProtocoloCicloDAO;

	@Inject
	private AghParametrosDAO aghParametrosDAO;

	@Inject
	private MptTurnoTipoSessaoDAO mptTurnoTipoSessaoDAO;
	
	@Inject
	private MptCaracteristicaTipoSessaoDAO mptCaracteristicaTipoSessaoDAO;
	
	@Inject
	private MptExtratoSessaoDAO mptExtratoSessaoDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private MptControleFrequenciaDAO mptControleFrequenciaDAO;
	
	@Inject 
	private FatContaApacDAO fatContaApacDAO;
	
	@Inject
	private MptTratamentoTerapeuticoDAO mptTratamentoTerapeuticoDAO;
	
	@Inject
	private AacSituacaoConsultasDAO aacSituacaoConsultasDAO;
	
	enum MptHorarioSessaoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DATA_NAO_INFORMADA, MENSAGEM_TIPO_SESSAO_NAO_INFORMADO, MENSAGEM_SALA_NAO_INFORMADO, MENSAGEM_REGISTRO_NAO_ENCONTRADO, LABEL_VALIDAR_HORARIO,
		CONVENIO_NAO_ENCONTRADO, AGENDA_NAO_ENCONTRADA;
	}
	
	private final String apacSemaforoVermelho = "vermelho";
	private final String apacSemaforoVerde = "verde";
	private final String apacSemaforoAzul = "azul";
	
	public void inserirMptHorarioSessao(Short loaSeq, Short agsSeq, Short dia, Date tempo,
			Short ciclo, Integer sesSeq, Date dataInicio, Date dataFim,DominioSituacaoHorarioSessao indSituacao,
			String conNumeros) throws ApplicationBusinessException {
		
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		
		MptHorarioSessao horarioSessao = new MptHorarioSessao();
		MptAgendamentoSessao agendamentoSessao = this.getMptAgendamentoSessaoDAO().obterPorChavePrimaria(agsSeq);
		horarioSessao.setAgendamentoSessao(agendamentoSessao);
		MptLocalAtendimento localAtendimento = this.getMptLocalAtendimentoDAO().obterPorChavePrimaria(loaSeq);
		horarioSessao.setLocalAtendimento(localAtendimento);
		horarioSessao.setDia(dia);
		horarioSessao.setTempo(tempo);
		horarioSessao.setDataInicio(dataInicio);
		horarioSessao.setDataFim(dataFim);
		horarioSessao.setCiclo(ciclo);
		// #41689
		if (sesSeq == null) {
		horarioSessao.setIndSituacao(indSituacao);
			
		} else {
			MptSessao sessao = this.mptSessaoDAO.obterPorChavePrimaria(sesSeq);
			horarioSessao.setMptSessao(sessao);
			horarioSessao.setIndSituacao(DominioSituacaoHorarioSessao.M);
		}
		horarioSessao.setCriadoEm(new Date());
		horarioSessao.setServidor(servidorLogado);
		horarioSessao.setConsultasAmb(conNumeros);
		
		this.getMptHorarioSessaoDAO().persistir(horarioSessao);
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	public MptAgendamentoSessaoDAO getMptAgendamentoSessaoDAO() {
		return mptAgendamentoSessaoDAO;
	}

	public MptLocalAtendimentoDAO getMptLocalAtendimentoDAO() {
		return mptLocalAtendimentoDAO;
	}

	public MptHorarioSessaoDAO getMptHorarioSessaoDAO() {
		return mptHorarioSessaoDAO;
	}
	
	
	/**
	 * Consulta principal, lista de pacientes agendados. ()
	 * @param parametro
	 * @param codigoSala
	 * @return List<MptHorarioSessao>
	 * @throws ApplicationBusinessException 
	 */
	public List<ListaPacienteAgendadoVO> pesquisarListaPacientes(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		
		BigDecimal valorNumerico = aghParametrosDAO.obterValorNumericoAghParametros(DIAS_PRIMEIRA_CONSULTA);
		MpaProtocoloAssistencial protocoloOriginal = null;
		List<ListaPacienteAgendadoVO> listaPacientes = new ArrayList<ListaPacienteAgendadoVO>();
		if (mpaProtocoloAssistencial != null && mpaProtocoloAssistencial.getSeq() != null){
			protocoloOriginal = mpaProtocoloAssistencialDAO.obterPorChavePrimaria(mpaProtocoloAssistencial.getSeq(), MpaProtocoloAssistencial.Fields.MPA_VERSAO_PROT_ASSISTENCIALES);			
		}
		listaPacientes = this.mptHorarioSessaoDAO.pesquisarListaPacientes(dataInicio,horario, tipoSessao, sala, 
				acomodacao, tipoAcomodacao, protocoloOriginal, valorNumerico);
		
		for (ListaPacienteAgendadoVO listaPacienteAgendadoVO : listaPacientes) {
			if (listaPacienteAgendadoVO.getPrimeiraconsulta() == 0){
				listaPacienteAgendadoVO.setColorColunaAmarelo(COR_AMARELA);
			}
			if (listaPacienteAgendadoVO.getIndgmr() > 0){
				listaPacienteAgendadoVO.setColorLinha(COR_AZUL);
			}
			if (listaPacienteAgendadoVO.getSituacaoHorario().equals(DominioSituacaoHorarioSessao.R)){
				listaPacienteAgendadoVO.setColorColunaVerde(COR_VERDE);
			}
			if (listaPacienteAgendadoVO.getCiclo() != null){
				listaPacienteAgendadoVO.getListaProtocoloCiclo().addAll(mptProtocoloCicloDAO.buscarProtocoloCiclo(listaPacienteAgendadoVO.getCiclo()));				
			}
			if(listaPacienteAgendadoVO.getApacCount() == null || listaPacienteAgendadoVO.getApacCount() == 0){
				listaPacienteAgendadoVO.setApacSemaforo(apacSemaforoVermelho);
			}else if(listaPacienteAgendadoVO.getApacDataFim() == null || listaPacienteAgendadoVO.getApacDataFim().after(new Date())){
						listaPacienteAgendadoVO.setApacSemaforo(apacSemaforoVerde);
			}else if(listaPacienteAgendadoVO.getApacDataFim().before(new Date())){
				  		listaPacienteAgendadoVO.setApacSemaforo(apacSemaforoAzul);
			}
		}
		return listaPacientes;
	}
	
	
	/**
	 * Consulta principal, lista de pacientes agendados.
	 * @param parametro
	 * @param codigoSala
	 * @return Long
	 */
	public Long pesquisarListaPacientesCount(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		BigDecimal valorNumerico = aghParametrosDAO.obterValorNumericoAghParametros(DIAS_PRIMEIRA_CONSULTA);
		MpaProtocoloAssistencial protocoloOriginal = null;
		if (mpaProtocoloAssistencial != null && mpaProtocoloAssistencial.getSeq() != null){
			protocoloOriginal = mpaProtocoloAssistencialDAO.obterPorChavePrimaria(mpaProtocoloAssistencial.getSeq(), MpaProtocoloAssistencial.Fields.MPA_VERSAO_PROT_ASSISTENCIALES);
		}
		return this.mptHorarioSessaoDAO.pesquisarListaPacientesCount(dataInicio,horario, tipoSessao, sala, acomodacao, tipoAcomodacao, protocoloOriginal,  valorNumerico);
	}
	
	
	/**
	 * Valida Campos Obrigatórios.
	 * @param dataInicio
	 * @param tipoSessao
	 * @return String
	 * @throws ApplicationBusinessException
	 * @throws BaseListException 
	 */
	public String validarCampos(Date dataInicio, Short tipoSessao) throws BaseListException {
		BaseListException listaDeErros = new BaseListException();
		if(dataInicio == null ){
			listaDeErros.add(new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.MENSAGEM_DATA_NAO_INFORMADA));
		}

		if(tipoSessao == null){
			listaDeErros.add(new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.MENSAGEM_TIPO_SESSAO_NAO_INFORMADO));
		}
		if(listaDeErros.hasException()){
			throw listaDeErros;
		}
		return null;
	}

	public void validarCampoTipoSessao(Short tipoSessao) throws ApplicationBusinessException {
		
		if(tipoSessao == null){
			throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.MENSAGEM_TIPO_SESSAO_NAO_INFORMADO);
		}
	}

	public void validarCampoSala(Short sala) throws ApplicationBusinessException {
		
		if(sala == null){
			throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.MENSAGEM_SALA_NAO_INFORMADO);
		}
	}

	/**
	 * Valida a listagem se não encontrar dados.
	 * @param listagem
	 * @return String
	 * @throws ApplicationBusinessException
	 */
	public String validarListagem(List<ListaPacienteAgendadoVO> listagem) throws ApplicationBusinessException {
		
		if(listagem.isEmpty()){
			throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.MENSAGEM_REGISTRO_NAO_ENCONTRADO);
		}
		return null;
	}

	/**
	 * Valida a listagem da ABA3 se não encontrar dados.
	 * @param listagem
	 * @return String
	 * @throws ApplicationBusinessException
	 */
	public String validarListagemAba3(List<ListaPacienteAguardandoAtendimentoVO> listagem) throws ApplicationBusinessException {
		
		if(listagem.isEmpty()){
			throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.MENSAGEM_REGISTRO_NAO_ENCONTRADO);
		}
		return null;
	}

	/**
	 * Obtém o horário do turno.
	 * @param tipoSessaoSeq
	 * @param turno
	 * @return List<MptTurnoTipoSessao>
	 */
	public MptTurnoTipoSessao obterHorariosTurnos(Short tipoSessaoSeq, DominioTurno turno) {
		
		MptTurnoTipoSessao retorno =  mptTurnoTipoSessaoDAO.obterHorariosTurnos(tipoSessaoSeq, turno);
		
		return retorno;
	}
	
	//#41705
	public boolean exibirColunaAPAC(String sigla, Short tipoSessaoSeq){
		
		return mptCaracteristicaTipoSessaoDAO.existeCaracteristicaTipoSessao(sigla, tipoSessaoSeq);
	}
	
	public List<AgendamentosPacienteVO> obterAgendamentosPaciente(Integer pacCodigo){
		List<AgendamentosPacienteVO> agendamentos = mptHorarioSessaoDAO.obterAgendamentosPaciente(pacCodigo); //C1
		
		//RN01
		for(int i = 0; i < agendamentos.size(); i++ ) {
			String strProtocolo =  "";
			StringBuilder protocoloDescricao = new StringBuilder();
			StringBuilder protocoloTitulos = new StringBuilder();
			
			List<MptProtocoloCiclo> protocolos = mptProtocoloCicloDAO.buscarProtocoloCiclo(agendamentos.get(i).getSeqClo()); //C2
			agendamentos.get(i).setProtocolos(protocolos);
			
			for (MptProtocoloCiclo mptProtocoloCiclo : protocolos) {
					
				if(mptProtocoloCiclo.getDescricao() != null && protocoloDescricao.toString().isEmpty()){
					protocoloDescricao.append(mptProtocoloCiclo.getDescricao());
					protocoloDescricao.append(" - ");
				}else{
					protocoloTitulos.append(mptProtocoloCiclo.getMpaVersaoProtAssistencial().getMpaProtocoloAssistencial().getTitulo());
					protocoloTitulos.append(" - ");
				}
			}

			if(!protocoloDescricao.toString().isEmpty()){
				strProtocolo = protocoloDescricao.toString(); 
			}else{
				strProtocolo = protocoloTitulos.toString();
			}
							
			agendamentos.get(i).setProtocolo(strProtocolo.substring(0, strProtocolo.length() - 3));
		}
		return agendamentos;
	}

	/**
	 * Consulta principal, lista de pacientes aguardando atendimento. (C1)
	 * @param dataInicio
	 * @param horario
	 * @param tipoSessao
	 * @param sala
	 * @param acomodacao
	 * @param tipoAcomodacao
	 * @param mpaProtocoloAssistencial
	 * @return List<ListaPacienteAguardandoAtendimentoVO>
	 */
	public List<ListaPacienteAguardandoAtendimentoVO> pesquisarListaPacientesAguardandoAtendimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		
		MpaProtocoloAssistencial protocoloOriginal = null;
		List<ListaPacienteAguardandoAtendimentoVO> listaPacientes = new ArrayList<ListaPacienteAguardandoAtendimentoVO>();
		
		BigDecimal valorNumerico = aghParametrosDAO.obterValorNumericoAghParametros(DIAS_PRIMEIRA_CONSULTA);
		
		if (mpaProtocoloAssistencial != null && mpaProtocoloAssistencial.getSeq() != null){
			protocoloOriginal = mpaProtocoloAssistencialDAO.obterPorChavePrimaria(mpaProtocoloAssistencial.getSeq(), MpaProtocoloAssistencial.Fields.MPA_VERSAO_PROT_ASSISTENCIALES);			
		}		

		listaPacientes = this.mptHorarioSessaoDAO.pesquisarListaPacientesAguardandoAtendimento(dataInicio,horario, tipoSessao, sala, 
				acomodacao, tipoAcomodacao, protocoloOriginal, valorNumerico);
		
		for (ListaPacienteAguardandoAtendimentoVO listaPacienteAguardandoAtendimentoVO : listaPacientes) {
			if (listaPacienteAguardandoAtendimentoVO.getPrimeiraconsulta() == 0){
				listaPacienteAguardandoAtendimentoVO.setColorColunaAmarelo(COR_AMARELA);
			}
			if (listaPacienteAguardandoAtendimentoVO.getIndgmr() > 0){
				listaPacienteAguardandoAtendimentoVO.setColorLinhaAzul(COR_AZUL);
			}			
			
			if (listaPacienteAguardandoAtendimentoVO.getCiclo() != null){
				listaPacienteAguardandoAtendimentoVO.getListaProtocoloCiclo().addAll(mptProtocoloCicloDAO.buscarProtocoloCiclo(listaPacienteAguardandoAtendimentoVO.getCiclo()));				
			}
			
			if (listaPacienteAguardandoAtendimentoVO.getSeqSessao() != null){
				listaPacienteAguardandoAtendimentoVO.setManipulacao(mptSessaoDAO.obterManipulacao(listaPacienteAguardandoAtendimentoVO.getSeqSessao()));
			}
		}
		
		return listaPacientes;
	}	
	
	/**
	 * Obter os pacientes que estão no status de acolhimento (triagem)
	 * #41706
	 * 
	 * @param dataInicio
	 * @param horario
	 * @param tipoSessao
	 * @param sala
	 * @param acomodacao
	 * @param tipoAcomodacao
	 * @param mpaProtocoloAssistencial
	 * @return
	 */
	public List<PacienteAcolhimentoVO> obterPacientesParaAcolhimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {

		BigDecimal valorNumerico = aghParametrosDAO.obterValorNumericoAghParametros(DIAS_PRIMEIRA_CONSULTA);
		
		MpaProtocoloAssistencial protocoloOriginal = obterProtocoloOriginal(mpaProtocoloAssistencial);
		
		List<PacienteAcolhimentoVO> listaPacientes = mptHorarioSessaoDAO.obterPacientesParaAcolhimento(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, protocoloOriginal, valorNumerico);
		
		for (PacienteAcolhimentoVO pacienteAcolhimentoVO : listaPacientes) {
			if (pacienteAcolhimentoVO.getIndgmr() > 0){
				pacienteAcolhimentoVO.setColorLinha(COR_AZUL);
			}
			if (pacienteAcolhimentoVO.getSituacaoHorario().equals(DominioSituacaoHorarioSessao.R)){
				pacienteAcolhimentoVO.setColorColunaVerde(COR_VERDE);
			}
			if (pacienteAcolhimentoVO.getPrimeiraconsulta() == 0){
				pacienteAcolhimentoVO.setColorColunaAmarelo(COR_AMARELA);
			}

			if (pacienteAcolhimentoVO.getCodigoCiclo() != null){
				pacienteAcolhimentoVO.getListaProtocoloCiclo().addAll(mptProtocoloCicloDAO.buscarProtocoloCiclo(pacienteAcolhimentoVO.getCodigoCiclo()));				
			}
		}
		
		return listaPacientes;		
	}
	
	/**
	 * Valida a listagem se não encontrar dados.
	 * @param listagem
	 * @return String
	 * @throws ApplicationBusinessException
	 */
	public String validarListagemAcolhimento(List<PacienteAcolhimentoVO> listagem) throws ApplicationBusinessException {
		
		if(listagem.isEmpty()){
			throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.MENSAGEM_REGISTRO_NAO_ENCONTRADO);
		}
		return null;
	}
	
	
	/**
	 * Consulta principal, Lista de Pacientes Em Atendimento. (C1)
	 * @param dataInicio
	 * @param horario
	 * @param tipoSessao
	 * @param sala
	 * @param acomodacao
	 * @param tipoAcomodacao
	 * @param mpaProtocoloAssistencial
	 * @return List<ListaPacienteEmAtendimentoVO>
	 */
	public List<ListaPacienteEmAtendimentoVO> pesquisarListaPacientesEmAtendimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		
		MpaProtocoloAssistencial protocoloOriginal = null;
		List<ListaPacienteEmAtendimentoVO> listaPacientes = new ArrayList<ListaPacienteEmAtendimentoVO>();
		
		BigDecimal valorNumerico = aghParametrosDAO.obterValorNumericoAghParametros(DIAS_PRIMEIRA_CONSULTA);
		
		if (mpaProtocoloAssistencial != null && mpaProtocoloAssistencial.getSeq() != null){
			protocoloOriginal = mpaProtocoloAssistencialDAO.obterPorChavePrimaria(mpaProtocoloAssistencial.getSeq(), MpaProtocoloAssistencial.Fields.MPA_VERSAO_PROT_ASSISTENCIALES);			
		}		
		listaPacientes = this.mptHorarioSessaoDAO.listarPacientesEmAtendimento(dataInicio,horario, tipoSessao, sala, 
				acomodacao, tipoAcomodacao, protocoloOriginal, valorNumerico);
		
		for (ListaPacienteEmAtendimentoVO listaPacienteEmAtendimentoVO : listaPacientes) {
			if (listaPacienteEmAtendimentoVO.getPrimeiraconsulta() == 0){
				listaPacienteEmAtendimentoVO.setColorColunaAmarelo(COR_AMARELA);
			}
			if (listaPacienteEmAtendimentoVO.getIndgmr() > 0){
				listaPacienteEmAtendimentoVO.setColorLinhaAzul(COR_AZUL);
			}			
			
			if (listaPacienteEmAtendimentoVO.getCiclo() != null){
				listaPacienteEmAtendimentoVO.getListaProtocoloCiclo().addAll(mptProtocoloCicloDAO.buscarProtocoloCiclo(listaPacienteEmAtendimentoVO.getCiclo()));				
			}
			
			if (listaPacienteEmAtendimentoVO.getSeqSessao() != null){
				listaPacienteEmAtendimentoVO.setManipulacao(mptSessaoDAO.obterManipulacao(listaPacienteEmAtendimentoVO.getSeqSessao()));
			}
		}
		
		return listaPacientes;
	}
	
	public String validarListagemAba4(List<ListaPacienteEmAtendimentoVO> listagem) throws ApplicationBusinessException {
		
		if(listagem.isEmpty()){
			throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.MENSAGEM_REGISTRO_NAO_ENCONTRADO);
		}
		return null;
	}
	
	/**
	 * Retorna o tempo em segundos para a atualização da Manupulação.
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BigDecimal tempoManipulacao() {
		
		BigDecimal valorNumerico = aghParametrosDAO.obterValorNumericoAghParametros(P_TEMPO_ATU_MANIPULACAO);
		return valorNumerico;
	}
	
	/**
	 * Obter os pacientes com atendimento concluído
	 * #41709
	 * 
	 * @param dataInicio
	 * @param horario
	 * @param tipoSessao
	 * @param sala
	 * @param acomodacao
	 * @param tipoAcomodacao
	 * @param mpaProtocoloAssistencial
	 * @return
	 */
	public List<PacienteConcluidoVO> obterPacientesAtendimentoConcluido(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial) {

		BigDecimal valorNumerico = aghParametrosDAO.obterValorNumericoAghParametros(DIAS_PRIMEIRA_CONSULTA);
		
		MpaProtocoloAssistencial protocoloOriginal = obterProtocoloOriginal(mpaProtocoloAssistencial);
		
		List<PacienteConcluidoVO> listaPacientes = mptHorarioSessaoDAO.obterPacientesAtendimentoConcluido(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, protocoloOriginal, valorNumerico);
		
		for (PacienteConcluidoVO pacienteConcluidoVO : listaPacientes) {
			if (pacienteConcluidoVO.getIndgmr() > 0){
				pacienteConcluidoVO.setColorLinha(COR_AZUL);
			}
			if (pacienteConcluidoVO.getPrimeiraconsulta() == 0){
				pacienteConcluidoVO.setColorColunaAmarelo(COR_AMARELA);
			}

			if (pacienteConcluidoVO.getCodigoCiclo() != null){
				pacienteConcluidoVO.getListaProtocoloCiclo().addAll(mptProtocoloCicloDAO.buscarProtocoloCiclo(pacienteConcluidoVO.getCodigoCiclo()));				
			}
		}
		
		return listaPacientes;		
	}

	private MpaProtocoloAssistencial obterProtocoloOriginal(MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		MpaProtocoloAssistencial protocoloOriginal = null;
		if (mpaProtocoloAssistencial != null && mpaProtocoloAssistencial.getSeq() != null){
			protocoloOriginal = mpaProtocoloAssistencialDAO.obterPorChavePrimaria(mpaProtocoloAssistencial.getSeq(), MpaProtocoloAssistencial.Fields.MPA_VERSAO_PROT_ASSISTENCIALES);			
		}
		
		return protocoloOriginal;
	}
	
	/**
	 * Valida a listagem se não encontrar dados.
	 * #41709
	 * @param listagem
	 * @return String
	 * @throws ApplicationBusinessException
	 */
	public String validarListagemConcluido(List<PacienteConcluidoVO> listagem) throws ApplicationBusinessException {
		
		if(listagem.isEmpty()){
			throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.MENSAGEM_REGISTRO_NAO_ENCONTRADO);
		}
		return null;
	}

	/**
	 * Consulta Hora de Inicio
	 * @param seqSessao
	 * @return Date
	 */
	public Date pesquisarHoraInicio(Integer seqSessao) {
		
		List<MptExtratoSessao> retornoHorario = mptExtratoSessaoDAO.buscarHoraInicio(seqSessao);
		if (retornoHorario != null && !retornoHorario.isEmpty()){
			return retornoHorario.get(0).getCriadoEm();
		}		
		return null;
	}

	/**
	 * Cancela os horários reservados informados.
	 * 
	 * @param horariosReservados - Horários reservados
	 */
	public void cancelarReservas(List<HorarioReservadoVO> horariosReservados) {

		for (HorarioReservadoVO horario : horariosReservados) {
			MptHorarioSessao horarioSessao = getMptHorarioSessaoDAO().obterPorChavePrimaria(horario.getSeq());

			horarioSessao.setIndSituacao(DominioSituacaoHorarioSessao.C);

			getMptHorarioSessaoDAO().atualizar(horarioSessao);
		}
	}

	/**
	 * Confirma os horários reservados informados.
	 * 
	 * @param horariosReservados
	 * @param tpsSeq
	 * @param salaSeq
	 * @param espSeq
	 * @param prescricao
	 * @param nomeMicrocomputador
	 * @param listaPrescricoes
	 * 
	 * @throws BaseException
	 */
	public void confirmarReservas(List<HorarioReservadoVO> horariosReservados, Short tpsSeq, Short salaSeq, Short espSeq, Integer prescricao,
			String nomeMicrocomputador, List<CadIntervaloTempoVO> listaPrescricoes) throws BaseException {

		Integer pacCodigo = null;
		for (HorarioReservadoVO horario : horariosReservados) {
			MptHorarioSessao horarioSessao = getMptHorarioSessaoDAO().obterPorChavePrimaria(horario.getSeq());

			if (pacCodigo == null) {
				pacCodigo = horario.getPacCodigo();
			}

			if (DominioConformidadeHorarioSessao.SEM_DIFERENCAS.equals(horario.getConformidade())) {
				horarioSessao.setIndSituacao(DominioSituacaoHorarioSessao.M);
				horarioSessao.setMptSessao(getMptSessaoDAO().obterPorChavePrimaria(horario.getHorarioPrescricao().getSesSeq()));

				getMptHorarioSessaoDAO().atualizar(horarioSessao);

				atualizarAmbulatorioHorarioSessao(horarioSessao.getConsultasAmb(), pacCodigo, horario.getHorarioPrescricao().getPteSeq());
			} else if (DominioConformidadeHorarioSessao.HORAS_RESERVADAS_MAIORES_PRESCRITA.equals(horario.getConformidade())) {
				horarioSessao.setIndSituacao(DominioSituacaoHorarioSessao.M);
				horarioSessao.setMptSessao(getMptSessaoDAO().obterPorChavePrimaria(horario.getHorarioPrescricao().getSesSeq()));

				// Calculando hora fim.
				Calendar horaInicio = Calendar.getInstance();
				horaInicio.setTime(horarioSessao.getDataInicio());
				if (horario.getHorarioPrescricao().getTempoAdministracao() != null) {
					horaInicio.add(Calendar.MINUTE, horario.getHorarioPrescricao().getTempoAdministracao());
				}

				horarioSessao.setDataFim(horaInicio.getTime());

				getMptHorarioSessaoDAO().atualizar(horarioSessao);

				atualizarAmbulatorioHorarioSessao(horarioSessao.getConsultasAmb(), pacCodigo, horario.getHorarioPrescricao().getPteSeq());
			} else if (DominioConformidadeHorarioSessao.DIAS_RESERVADOS_MAIS_PRESCRICAO.equals(horario.getConformidade())) {
				horarioSessao.setIndSituacao(DominioSituacaoHorarioSessao.C);
				
				getMptHorarioSessaoDAO().atualizar(horarioSessao);
			} else {
				// RN05 estória #41724
				horarioSessao.setIndSituacao(DominioSituacaoHorarioSessao.C);

				getMptHorarioSessaoDAO().atualizar(horarioSessao);

				if (DominioConformidadeHorarioSessao.HORAS_PRESCRITAS_MAIORES_RESERVADA.equals(horario.getConformidade())) {
					List<CadIntervaloTempoVO> listaHorarios = new ArrayList<CadIntervaloTempoVO>();
	
					CadIntervaloTempoVO novoAgend = new CadIntervaloTempoVO();
	
					novoAgend.setDiaReferencia(horario.getHorarioPrescricao().getDiaReferencia());
					novoAgend.setHoraInicioReferencia(horario.getHorarioPrescricao().getHoraInicioReferencia());
					novoAgend.setHoraFimReferencia(horario.getHorarioPrescricao().getHoraFimReferencia());
					novoAgend.setQtdeHoras(horario.getHorarioPrescricao().getQtdeHoras());
					novoAgend.setCiclo(horario.getHorarioPrescricao().getCiclo());
					novoAgend.setSesSeq(horario.getHorarioPrescricao().getSesSeq());
					novoAgend.setAgendar(true);
	
					listaHorarios.add(novoAgend);
	
					List<HorariosAgendamentoSessaoVO> horariosGerados = getAgendamentoSessaoRN().sugerirAgendaSessao(listaHorarios, tpsSeq, salaSeq, espSeq,
							pacCodigo, prescricao, DominioTipoLocal.T, null, horarioSessao.getDataInicio(), horarioSessao.getDataFim(), null, new String[0], false);
	
					if (horariosGerados.get(0).getLogTentativas() != null) {
						throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.AGENDA_NAO_ENCONTRADA);
					}
					
					getAgendamentoSessaoRN().agendarSessao(horariosGerados, tpsSeq, salaSeq, null, pacCodigo,
							horariosReservados.get(0).getHorarioPrescricao().getVpaPtaSeq(), horariosReservados.get(0).getHorarioPrescricao().getVpaSeqp(), null,
							horarioSessao.getDataInicio(), horarioSessao.getDataFim(), nomeMicrocomputador);
				}
			}

		}

		outer: for (CadIntervaloTempoVO tempoPresc : listaPrescricoes) {
			for (HorarioReservadoVO horario : horariosReservados) {
				if (horario.getDia().equals(tempoPresc.getDiaReferencia())) {
					continue outer;
				}
			}

			List<CadIntervaloTempoVO> listaHorarios = new ArrayList<CadIntervaloTempoVO>();
			tempoPresc.setAgendar(true);
			listaHorarios.add(tempoPresc);

			List<HorariosAgendamentoSessaoVO> horariosGerados = getAgendamentoSessaoRN().sugerirAgendaSessao(listaHorarios, tpsSeq, salaSeq, espSeq, pacCodigo,
					prescricao, DominioTipoLocal.T, null, tempoPresc.getHoraInicioReferencia(), tempoPresc.getHoraFimReferencia(), null, new String[0], false);

			if (horariosGerados.get(0).getLogTentativas() != null) {
				throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.AGENDA_NAO_ENCONTRADA);
			}
			
			getAgendamentoSessaoRN().agendarSessao(horariosGerados, tpsSeq, salaSeq, null, pacCodigo,
					horariosReservados.get(0).getHorarioPrescricao().getVpaPtaSeq(), horariosReservados.get(0).getHorarioPrescricao().getVpaSeqp(), null,
					tempoPresc.getHoraInicioReferencia(), tempoPresc.getHoraFimReferencia(), nomeMicrocomputador);
		}
	}

	/**
	 * Atualiza a consulta relacionada ao paciente.
	 * 
	 * @param consultas
	 * @param pacCodigo
	 * @param pteSeq
	 * @throws ApplicationBusinessException 
	 */
	private void atualizarAmbulatorioHorarioSessao(String consultas, Integer pacCodigo, Integer pteSeq) throws ApplicationBusinessException {

		if (StringUtils.isNotBlank(consultas)) {
			String[] consultasArray = consultas.split(VIRGULA);

			MptTratamentoTerapeutico convenio = getMptTratamentoTerapeuticoDAO().obterConvenioPacienteAgendamento(pacCodigo, pteSeq);

			if (convenio == null) {
				throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.CONVENIO_NAO_ENCONTRADO);
			}

			AacConsultas consulta = getAmbulatorioFacade().obterAacConsulta(Integer.valueOf(consultasArray[0].trim()));
			if (consulta != null) {
				AacSituacaoConsultas sitConsulta = aacSituacaoConsultasDAO.obterPorChavePrimaria(SITUACAO_CONSULTA_M);
				AacRetornos retorno = getAmbulatorioFacade().obterRetorno(DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
				AacCondicaoAtendimento condAtend = getAmbulatorioFacade().obterCondicaoAtendimentoPorCodigo((short) 8);
				
				consulta.setPaciente(convenio.getPaciente());
				consulta.setConvenioSaudePlano(convenio.getConvenioSaudePlano());
				consulta.setStcSituacao(SITUACAO_CONSULTA_M);
				consulta.setIndSituacaoConsulta(sitConsulta);
				consulta.setRetorno(retorno);
				consulta.setCondicaoAtendimento(condAtend);
				
				if (DominioGrupoConvenio.S.equals(convenio.getFatConvenioSaude().getGrupoConvenio())) {
					AacTipoAgendamento tipoAgend = getAmbulatorioFacade().obterTipoAgendamentoPorCodigo((short) 1);
					AacPagador pagador = getAmbulatorioFacade().obterPagador((short) 1);

					consulta.setTipoAgendamento(tipoAgend);
					consulta.setPagador(pagador);

					getAmbulatorioFacade().atualizarConsulta(consulta);
				} else if (DominioGrupoConvenio.C.equals(convenio.getFatConvenioSaude().getGrupoConvenio())) {
					AacTipoAgendamento tipoAgend = getAmbulatorioFacade().obterTipoAgendamentoPorCodigo((short) 5);
					AacPagador pagador = getAmbulatorioFacade().obterPagador((short) 2);
					
					consulta.setTipoAgendamento(tipoAgend);
					consulta.setPagador(pagador);
					
					getAmbulatorioFacade().atualizarConsulta(consulta);
				}
			}
		}
	}

	/**
	 * Valida Hora Início e Hora Fim.
	 * @param MptSessao
	 * @throws ApplicationBusinessException
	 */
	public void validarHorario(MptSessao MptSessao) throws ApplicationBusinessException {
		
		if(MptSessao != null && MptSessao.getDthrInicio() != null && MptSessao.getDthrFim() != null){				
			if(DateUtil.validaHoraMaior(MptSessao.getDthrInicio(), MptSessao.getDthrFim())){
				throw new ApplicationBusinessException(MptHorarioSessaoRNExceptionCode.LABEL_VALIDAR_HORARIO);				
			}			
		}		
	}

	public void registrarControleFrequenciaRelatorio(String dataAgendado, Integer codPaciente, Integer seqSessao, Long numeroApac, Byte capSeqApac, DominioControleFrequenciaSituacao dominioControleFrequencia, Date dataReferencia, RapServidores servidorLogado) {
		MptControleFrequencia mptControleFrequencia = new MptControleFrequencia();
		String[] datas = dataAgendado.split("/");
		int valorMes = Integer.valueOf(datas[0]); 
		valorMes += 1;
		datas[0] = String.valueOf(valorMes);
				
		AipPacientes aipPaciente = aipPacientesDAO.pesquisarPacientePorCodigo(codPaciente);
		mptControleFrequencia.setPacCodigo(aipPaciente);
		
		MptSessao mptSessao = mptSessaoDAO.obterPorChavePrimaria(seqSessao);
		mptControleFrequencia.setSesSeq(mptSessao);
		
		if (numeroApac != null && capSeqApac != null){
			FatContaApac fatContaApac = fatContaApacDAO.buscaContaApacPorChave(numeroApac, capSeqApac);
			if(fatContaApac.getCpeMes() == Byte.valueOf(datas[0]) && fatContaApac.getCpeAno() == Short.valueOf(datas[1]) ) {
				fatContaApac.setIndCtrlFrequencia("S");
				fatContaApacDAO.atualizar(fatContaApac);
			
			}
			
			mptControleFrequencia.setFatContaApac(fatContaApac);
		}
		
		mptControleFrequencia.setSituacao(dominioControleFrequencia);

		Calendar data = Calendar.getInstance();
		data.setTime(dataReferencia);
		mptControleFrequencia.setMesReferencia(data.get(Calendar.MONTH) + 1);
		mptControleFrequencia.setAnoReferencia(data.get(Calendar.YEAR));
		mptControleFrequencia.setCriadoEm(new Date());
		mptControleFrequencia.setServidor(servidorLogado);
		
		mptControleFrequenciaDAO.persistir(mptControleFrequencia);
		
	}

	public MptSessaoDAO getMptSessaoDAO() {
		return mptSessaoDAO;
	}

	public MptTratamentoTerapeuticoDAO getMptTratamentoTerapeuticoDAO() {
		return mptTratamentoTerapeuticoDAO;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public AacSituacaoConsultasDAO getAacSituacaoConsultasDAO() {
		return aacSituacaoConsultasDAO;
	}

	public AgendamentoSessaoRN getAgendamentoSessaoRN() {
		return agendamentoSessaoRN;
	}	
}
