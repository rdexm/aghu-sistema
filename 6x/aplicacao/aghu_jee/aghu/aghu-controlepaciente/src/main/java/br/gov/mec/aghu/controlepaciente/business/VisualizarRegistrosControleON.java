package br.gov.mec.aghu.controlepaciente.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.dao.EcpControlePacienteDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpHorarioControleDAO;
import br.gov.mec.aghu.controlepaciente.vo.DescricaoControlePacienteVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioPeriodoRegistroControlePaciente;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class VisualizarRegistrosControleON extends BaseBusiness {

	private static final String ARGUMENTO_INVALIDO = "Argumento inválido";

	private static final Log LOG = LogFactory.getLog(VisualizarRegistrosControleON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private EcpHorarioControleDAO ecpHorarioControleDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private EcpControlePacienteDAO ecpControlePacienteDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5987556233369902527L;

	public enum VisualizarRegistrosControleONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_LEITO_OBRIGATORIO,
		MENSAGEM_PRONTUARIO_NULO,
		MENSAGEM_MAIS_DE_UM_ATENDIMENTO_ENCONTRADO_LEITO,
		MENSAGEM_LEITO_NAO_ENCONTRADO_COM_ID,
		MENSAGEM_NENHUM_ATENDIMENTO_ENCONTRADO_LEITO,
		MENSAGEM_DATA_INICIAL_NULA,
		MENSAGEM_DATA_FINAL_NULA,
		MENSAGEM_DATA_FINAL_ANTES_INICIAL,
		MENSAGEM_PERIODO_MAXIMO_EXCEDIDO,
		MENSAGEM_PERIODO_MAXIMO_EXCEDIDO_COM_MENSAGEM_PARAMETRO,
		MENSAGEM_PARAMETRO_PRAZO_NAO_DEFINIDO, 
		MENSAGEM_PRAZO_EXCEDIDO_PARA_EXCLUSAO,
		MENSAGEM_USUARIO_INVALIDO_EXCLUSAO;
	}

	/**
	 * Busca atendimento do paciente através do número do prontuário em
	 * atendimento vigente
	 * 
	 * @param prontuario
	 * @return atendimento
	 */
	public AghAtendimentos obterAtendimentoVigentePorProntuarioPaciente(
			Integer prontuario) throws ApplicationBusinessException {
		if (prontuario == null) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_PRONTUARIO_NULO);
		}
		AipPacientes paciente = getPacienteFacade().obterPacientePorProntuario(prontuario);
		//AghAtendimentos atendimento = this.getAghuFacade().obterAtendimentoVigenteDetalhado(paciente);
		AghAtendimentos atendimento = this.getAghuFacade().obterAtendimentoAtual(paciente);
		return atendimento;
	}

	public AghAtendimentos obterAtendimentoAtualPorLeitoId(String leitoId)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(leitoId)) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_LEITO_OBRIGATORIO);
		}
		AinLeitos leito = this.getInternacaoFacade().obterLeitoPorId(leitoId);
		if (leito == null) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_LEITO_NAO_ENCONTRADO_COM_ID, leitoId);
		}
		List<AghAtendimentos> listaAtendimentos = this.getAghuFacade()
				.pesquisarAtendimentoVigente(leito);
		
		if (listaAtendimentos.size() == 1) {
			return listaAtendimentos.get(0);
		}
		if (listaAtendimentos.size() > 1) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_MAIS_DE_UM_ATENDIMENTO_ENCONTRADO_LEITO,
					leitoId);
		}

		throw new ApplicationBusinessException(
				VisualizarRegistrosControleONExceptionCode.MENSAGEM_NENHUM_ATENDIMENTO_ENCONTRADO_LEITO,
				leitoId);
	}
	
	/**
	 * Realizar a pesquisa dos registros existentes para o paciente por atendimento e paciente.
	 * 
	 * @param atendimentoSeq
	 * @param paciente
	 * @return List<EcpControlePaciente>
	 * @throws AGHUNegocioException
	 */
	public List<EcpControlePaciente> pesquisarRegistrosPacientePorAtdEPaciente(Integer atendimentoSeq, AipPacientes paciente) {
		EcpControlePacienteDAO ecpControlePacienteDAO = getEcpControlePacienteDAO();
		return ecpControlePacienteDAO.listarControlePacientePorAtendimentoEPaciente(atendimentoSeq,	paciente.getCodigo());
	}

	/**
	 * Realizar a pesquisa dos registros existentes para o paciente
	 * 
	 * @param atendimentoSeq
	 * @param paciente
	 * @param leitoID
	 * @param dataInicial
	 * @param dataFinal
	 * @param listaItensControle
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<RegistroControlePacienteVO> pesquisarRegistrosPaciente(
			Integer atendimentoSeq, AipPacientes paciente, String leitoID,
			Date dataInicial, Date dataFinal,
			List<EcpItemControle> listaItensControle, Long trgSeq)
			throws ApplicationBusinessException {
		

		List<EcpHorarioControle> listaHorarioControle = this
				.getEcpHorarioControleDAO()
				.listarHorarioControlePorPacientePeriodo(paciente, dataInicial,
						dataFinal, false, trgSeq);

		List<RegistroControlePacienteVO> listaRegistroControleVO = new ArrayList<RegistroControlePacienteVO>();

		String[] valorControles = null;
		String[] valorTextoMedicao = null;
		Boolean[] limiteControles = null;

		EcpControlePacienteDAO ecpControlePacienteDAO = getEcpControlePacienteDAO();
		for (EcpHorarioControle horarioControle : listaHorarioControle) {

			valorControles = new String[listaItensControle.size()];
			valorTextoMedicao = new String[listaItensControle.size()];
			limiteControles = new Boolean[listaItensControle.size()];
			
			obtemValorControleEMedicao(listaItensControle, valorControles, limiteControles, valorTextoMedicao, ecpControlePacienteDAO, horarioControle);

			listaRegistroControleVO.add(new RegistroControlePacienteVO(
						horarioControle.getSeq(),
						horarioControle.getDataHora(), 
						montarDescricaoAnotacao(horarioControle),
						horarioControle.getAtendimento() != null ? horarioControle.getAtendimento().getSeq() : null,
						horarioControle.getTrgSeq(),
						valorControles,
						valorTextoMedicao,
						limiteControles, 
						horarioControle.getAtendimento() != null ? horarioControle.getAtendimento().getDescricaoLocalizacao(false) : null,
						horarioControle.getPaciente().getCodigo(),
						horarioControle.getUnidadeFuncional() != null ? horarioControle.getUnidadeFuncional().getSeq() : null,
						StringUtils.isNotBlank(horarioControle.getAnotacoes()),
						horarioControle.getAnotacoes(), horarioControle.getCriadoEm()));
			
		}

		return listaRegistroControleVO;
	}

	private void obtemValorControleEMedicao(
			List<EcpItemControle> listaItensControle, String[] valorControles,
			Boolean[] limiteControles,
			String[] valorTextoMedicao,
			EcpControlePacienteDAO ecpControlePacienteDAO,
			EcpHorarioControle horarioControle) {
		List<EcpControlePaciente> listaControlesPaciente = ecpControlePacienteDAO.listaControlesHorario(horarioControle, null);

		for (EcpControlePaciente controlePaciente : listaControlesPaciente) {
			int indice = 0;
			for (EcpItemControle itemControle : listaItensControle) {
				if (itemControle.getSeq().equals(controlePaciente.getItem().getSeq())) {
					valorControles[indice] = controlePaciente.getMedicaoFormatada();
					limiteControles[indice] = controlePaciente.getForaLimiteNormal();
					valorTextoMedicao[indice] = StringUtils.isBlank(controlePaciente.getTexto()) ? StringUtils.EMPTY : controlePaciente.getTexto();
				}
				indice++;
			}
		}
	}
	
	/**
	 * Concatenar a anotação do horário com a sigla do conselho e o número registro do conselho (quando existir)
	 * 
	 * @param horarioControle
	 * @return
	 */
	public String montarDescricaoAnotacao(EcpHorarioControle horarioControle) {

		if (horarioControle == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}
		StringBuilder servLocalizacao = new StringBuilder("Informado por: ");
		servLocalizacao.append(horarioControle.getServidor().getPessoaFisica().getNome());

		//Concatenar o local com a sigla do conselho e o número registro do conselho (quando existir)
		if (horarioControle.getServidor() != null) {			
			DescricaoControlePacienteVO descricao = getRegistroColaboradorFacade()
					.buscarDescricaoAnotacaoControlePaciente(
							horarioControle.getServidor().getId().getVinCodigo(),
							horarioControle.getServidor().getId().getMatricula());
			
			if(descricao != null && descricao.getSiglaConselho() != null) {
				servLocalizacao.append(", ");
				servLocalizacao.append(descricao.getSiglaConselho());
				
				if(descricao.getNroRegistroConselho() != null) {
					servLocalizacao.append(": ");
					servLocalizacao.append(descricao.getNroRegistroConselho());
				}
			}
		}
		
		if(horarioControle != null && horarioControle.getCriadoEm() != null){
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			String hora = sdf.format(horarioControle.getCriadoEm());
			servLocalizacao.append(" às ");
			servLocalizacao.append(hora);
		}
		
		if (StringUtils.isNotBlank(servLocalizacao)){
			return servLocalizacao.toString();
		} else {
			return null;
		}
	}
	
	/**
	 * Realizar a validação de datas para a pesquisa dos registros do paciente
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @throws ApplicationBusinessException
	 */
	public void validarDatasPesquisaRegistrosPaciente(Date dataInicial,
			Date dataFinal) throws ApplicationBusinessException {
		if (dataInicial == null) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_DATA_INICIAL_NULA);
		}
		if (dataFinal == null) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_DATA_FINAL_NULA);
		}
		if (dataFinal.before(dataInicial)) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_DATA_FINAL_ANTES_INICIAL);
		}
		// verifica consistencia de datas conforme o período máximo definido no
		// sistema
		int valorParametroDias = DominioPeriodoRegistroControlePaciente.DIAS15
				.getValorEmDias();
		long valorPeriodoMaximoEmMillis = DominioPeriodoRegistroControlePaciente.DIAS15
				.getValorEmMilisegundos();
		AghParametros aghParametro = this.getParametroFacade()
				.buscarAghParametro(
						AghuParametrosEnum.P_AGHU_NRO_DIAS_VISUALIZA_CONTROLES);
		if (aghParametro != null) {
			valorParametroDias = aghParametro.getVlrNumerico().intValue();
			valorPeriodoMaximoEmMillis = valorParametroDias	* DateUtils.MILLIS_PER_DAY; // valor em milissegundos
		}
		long millisDataInicial = dataInicial.getTime();
		long millisDataFinal = dataFinal.getTime();
		if (millisDataFinal - millisDataInicial > valorPeriodoMaximoEmMillis) {
			if (aghParametro != null) {
				throw new ApplicationBusinessException(
						VisualizarRegistrosControleONExceptionCode.MENSAGEM_PERIODO_MAXIMO_EXCEDIDO, valorParametroDias);
			} else {
				throw new ApplicationBusinessException(
						VisualizarRegistrosControleONExceptionCode.MENSAGEM_PERIODO_MAXIMO_EXCEDIDO_COM_MENSAGEM_PARAMETRO,	valorParametroDias);
			}
		}

	}

	/**
	 * Realizar a exclusão de horários de controles de paciente. Verifica se a
	 * exclusão é permitida através do prazo definido no parâmetro:
	 * P_AGHU_PRAZO_EXCLUI_CONTROLES
	 * 
	 * @param seqHorarioControle
	 * @throws AGHUBaseException
	 */
	@SuppressWarnings("ucd")
	public void excluirRegistroControlePaciente(Long seqHorarioControle)
			throws BaseException {
		
		if (seqHorarioControle == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}
		
		// carrega o horario controle
		EcpHorarioControle horarioControle = this.getEcpHorarioControleDAO()
				.obterPorChavePrimaria(seqHorarioControle);

		this.verificarPrazoExclusao(horarioControle);
		this.verificarUsuarioExclusao(horarioControle);
		
		// realiza a exclusão dos controles vinculados ao horário
		List<EcpControlePaciente> listaControlePacientes = new ArrayList<EcpControlePaciente>(
				horarioControle.getControlePacientes());
		EcpControlePacienteDAO ecpControlePacienteDAO = getEcpControlePacienteDAO();
		for (EcpControlePaciente controlePaciente : listaControlePacientes) {
			ecpControlePacienteDAO.remover(controlePaciente);
			ecpControlePacienteDAO.flush();
		}

		// realiza a exclusão do horário de controle
		this.getEcpHorarioControleDAO().remover(horarioControle);
		this.getEcpHorarioControleDAO().flush();
	}

	/**
	 * Verificar se não excedeu o prazo para a exclusão
	 * 
	 * @param horarioControle
	 * @throws AGHUBaseException
	 */
	private void verificarPrazoExclusao(EcpHorarioControle horarioControle)
			throws BaseException {

		if (horarioControle == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		// verifica o prazo para a exclusão
		int valorPrazoHoras = this.obterParametroPrazoExclusao();
		long valorPrazoEmMillis = valorPrazoHoras * DateUtils.MILLIS_PER_HOUR;

		long millisDataAtual = Calendar.getInstance().getTimeInMillis();
		long millisDataHorarioControle = horarioControle.getDataHora()
				.getTime();
		if (millisDataAtual - millisDataHorarioControle > valorPrazoEmMillis) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_PRAZO_EXCEDIDO_PARA_EXCLUSAO,
					valorPrazoHoras);
		}

	}

	/**
	 * Verificar se o usuário logado pode excluir um horário e seus itens
	 * 
	 * @param horarioControle
	 * @throws AGHUBaseException
	 */
	private void verificarUsuarioExclusao(EcpHorarioControle horarioControle)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (horarioControle == null) {
			throw new IllegalArgumentException(ARGUMENTO_INVALIDO);
		}

		// verifica se o usuário que registrou o horário é o mesmo que está
		// logado
		if (horarioControle.getServidor() != null
				&& !horarioControle.getServidor().equals(servidorLogado)) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_USUARIO_INVALIDO_EXCLUSAO,
					horarioControle.getServidor());
		}

		// verificar se o usuário que registrou cada um dos itens é o mesmo que
		// está logado
		for (EcpControlePaciente controlePaciente : horarioControle
				.getControlePacientes()) {
			if (controlePaciente.getServidor() != null
					&& !controlePaciente.getServidor().equals(servidorLogado)) {
				throw new ApplicationBusinessException(
						VisualizarRegistrosControleONExceptionCode.MENSAGEM_USUARIO_INVALIDO_EXCLUSAO,
						controlePaciente.getServidor());
			}
		}
	}
	
	/**
	 * Realiza a busca do parâmetro que informa o prazo para exclusão de
	 * horários
	 * 
	 * @return
	 * @throws AGHUBaseException
	 */
	protected Integer obterParametroPrazoExclusao() throws BaseException {

		AghParametros aghParametro = this.getParametroFacade()
				.buscarAghParametro(
						AghuParametrosEnum.P_AGHU_PRAZO_EXCLUI_CONTROLES);

		if (aghParametro == null || aghParametro.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					VisualizarRegistrosControleONExceptionCode.MENSAGEM_PARAMETRO_PRAZO_NAO_DEFINIDO);
		}

		return aghParametro.getVlrNumerico().intValue();

	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected EcpHorarioControleDAO getEcpHorarioControleDAO() {
		return ecpHorarioControleDAO;
	}

	protected EcpControlePacienteDAO getEcpControlePacienteDAO() {
		return ecpControlePacienteDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	protected IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
