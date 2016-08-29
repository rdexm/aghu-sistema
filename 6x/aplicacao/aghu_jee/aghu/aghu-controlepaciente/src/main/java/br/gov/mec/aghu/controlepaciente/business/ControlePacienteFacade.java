package br.gov.mec.aghu.controlepaciente.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.controlepaciente.dao.EcpControlePacienteDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpHorarioControleDAO;
import br.gov.mec.aghu.controlepaciente.dao.EcpItemControleDAO;
import br.gov.mec.aghu.controlepaciente.vo.PacienteInternadoVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteVO;
import br.gov.mec.aghu.controlepaciente.vo.RelatorioRegistroControlePacienteVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.RapServidores;


@Stateless
public class ControlePacienteFacade extends BaseFacade implements IControlePacienteFacade {

	@EJB
	private ConfigurarListaPacientesEnfermagemON configurarListaPacientesEnfermagemON;
	@EJB
	private ListarPacientesRegistroControleON listarPacientesRegistroControleON;
	@EJB
	private RelatorioRegistroControlePacienteON relatorioRegistroControlePacienteON;
	@EJB
	private ManterControlesPacienteON manterControlesPacienteON;
	@EJB
	private VisualizarRegistrosControleON visualizarRegistrosControleON;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8341931086608875042L;

	@Inject
	private EcpControlePacienteDAO ecpControlePacienteDAO;
	
	@Inject
	private EcpItemControleDAO ecpItemControleDAO;

	@Inject
	private EcpHorarioControleDAO ecpHorarioControleDAO;
	
	@Override
	@Secure("#{s:hasPermission('manterRegistrosControlesPaciente','gravar')}")
	public void gravar(EcpHorarioControle horario, List<EcpControlePaciente> controles) throws BaseException {
		this.getManterControlesPacienteON().gravar(horario, controles);
	}

	/**
	 * Rotina que grava contigência de internação e coloca na fila para geração
	 * de pdf chama package no banco oracle - André Luiz Machado - 11/01/2012
	 */
	@Override
	public void dispararGeracaoContigencia(EcpHorarioControle horario) throws ApplicationBusinessException {
		this.getManterControlesPacienteON().dispararGeracaoContigencia(horario);
	}

	@Override
	public boolean validaUnidadeFuncionalInformatizada(AghAtendimentos atendimento, Short unfSeq)
			throws ApplicationBusinessException {
		return this.getManterControlesPacienteON().validaUnidadeFuncionalInformatizada(atendimento, unfSeq);
	}

	@Override
	public EcpHorarioControle obterHorariopelaDataHora(AipPacientes paciente, Date dataHora) {
		return this.getManterControlesPacienteON().obterHorariopelaDataHora(paciente, dataHora);
	}

	@Override
	public EcpHorarioControle obterHorarioPeloId(long seq) {
		return this.getEcpHorarioControleDAO().obterPorChavePrimaria(seq);
	}

	@Override
	public EcpControlePaciente obterControlePeloId(long seq) {
		return this.getEcpControlePacienteDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@Secure("#{s:hasPermission('visualizarRegistrosControlePaciente','excluir')}")
	public void excluir(EcpControlePaciente controlePaciente) throws ApplicationBusinessException {
		this.getManterControlesPacienteON().excluir(controlePaciente);
	}

	@Override
	@Secure("#{s:hasPermission('visualizarRegistrosControlePaciente','excluir')}")
	public void excluir(EcpHorarioControle horarioControle) throws ApplicationBusinessException {
		this.getManterControlesPacienteON().excluir(horarioControle);
	}

	@Override
	public List<EcpControlePaciente> listaControlesHorario(EcpHorarioControle horario, EcpGrupoControle grupo) {
		return this.getManterControlesPacienteON().listaControlesHorario(horario, grupo);
	}

	// mostra mensagem item fora do limite da normalidade
	@Override
	public String mensagemItemForaLimitesNormalidade(EcpHorarioControle horario, EcpControlePaciente controlePaciente)
			throws ApplicationBusinessException {
		return this.getManterControlesPacienteON().mensagemItemForaLimitesNormalidade(horario, controlePaciente);
	}

	protected ManterControlesPacienteON getManterControlesPacienteON() {
		return manterControlesPacienteON;
	}

	@Override
	public List<PacienteInternadoVO> pesquisarPacientesInternados(RapServidores servidor) throws BaseException {
		return this.getListarPacientesInternadosON().pesquisarPacientesInternados(servidor);
	}

	protected ListarPacientesRegistroControleON getListarPacientesInternadosON() {
		return listarPacientesRegistroControleON;
	}

	@Override
	public List<AghUnidadesFuncionais> buscarListaUnidadesFuncionais(RapServidores servidor)
			throws ApplicationBusinessException {
		return this.getConfigurarListaPacientesEnfermagemON().buscarListaUnidadesFuncionais(servidor);
	}

	@Override
	public void salvarListaUnidadesFuncionaisEnfermagem(List<AghUnidadesFuncionais> listaServUnFuncionais,
			RapServidores servidor) throws ApplicationBusinessException {
		getConfigurarListaPacientesEnfermagemON().salvarListaUnidadesFuncionais(listaServUnFuncionais, servidor);
	}

	@Override
	public List<AghUnidadesFuncionais> pesquisarListaUnidadesFuncionais(String paramString)
			throws ApplicationBusinessException {
		return getConfigurarListaPacientesEnfermagemON().pesquisarListaUnidadesFuncionais(paramString);
	}


	private EcpControlePacienteDAO getEcpControlePacienteDAO() {
		return ecpControlePacienteDAO;
	}
	
	private EcpItemControleDAO getEcpItemControleDAO() {
		return ecpItemControleDAO;
	}

	private EcpHorarioControleDAO getEcpHorarioControleDAO() {
		return ecpHorarioControleDAO;
	}

	// métodos da estória configurar lista de pacientes
	private ConfigurarListaPacientesEnfermagemON getConfigurarListaPacientesEnfermagemON() {
		return configurarListaPacientesEnfermagemON;
	}

	// #6737 - Visualizar Registro de Controles de Pacientes
	private VisualizarRegistrosControleON getVisualizarRegistrosControleON() {
		return visualizarRegistrosControleON;
	}

	@Override
	public AghAtendimentos pesquisarAtendimentoVigentePorLeito(String leito) throws ApplicationBusinessException {
		return getVisualizarRegistrosControleON().obterAtendimentoAtualPorLeitoId(leito);
	}

	@Override
	public AghAtendimentos pesquisarAtendimentoVigentePorProntuarioPaciente(Integer numeroProntuario)
			throws ApplicationBusinessException {
		return getVisualizarRegistrosControleON().obterAtendimentoVigentePorProntuarioPaciente(numeroProntuario);
	}

	@Override
	public List<RegistroControlePacienteVO> pesquisarRegistrosPaciente(Integer atendimentoSeq, AipPacientes paciente,
			String leitoId, Date dataInicial, Date dataFinal, List<EcpItemControle> listaItensControle, Long trgSeq)
					throws ApplicationBusinessException {
		return getVisualizarRegistrosControleON().pesquisarRegistrosPaciente(atendimentoSeq, paciente, leitoId,
				dataInicial, dataFinal, listaItensControle, trgSeq);

	}

	@Override
	@Secure("#{s:hasPermission('visualizarRegistrosControlePaciente','excluir')}")
	public void excluirRegistroControlePaciente(Long seqHorarioControle) throws ApplicationBusinessException {
		this.getManterControlesPacienteON().excluirRegistroControlePaciente(seqHorarioControle);
	}
	
	@Override
	public void atualizarAtdSeqHorarioControlePaciente(Integer atdSeq, Long trgSeq) throws BaseException {
		this.getManterControlesPacienteON().atualizarAtdSeqHorarioControlePaciente(atdSeq, trgSeq);
	}

	/**
	 * Relatorio Registro de Controles de Pacientes
	 * 
	 * @param pacCodigo
	 * @param dataHoraInicio
	 * @param dataHoraFim
	 * @param aghAtendimentos
	 * @return
	 * @throws BaseException
	 */
	@Override
	public List<RelatorioRegistroControlePacienteVO> criaRelatorioRegistroControlePaciente(
			Integer pacCodigo, Date dataHoraInicio, Date dataHoraFim, AghAtendimentos aghAtendimentos)
			throws ApplicationBusinessException {
		return this.getRelatorioRegistroControlePacienteON()
				.criaRelatorioRegistroControlePaciente(pacCodigo,
						dataHoraInicio, dataHoraFim, aghAtendimentos);
	}

	@Override
	public void validaDataInicial(Integer atdSeq, Date dataHoraInicial) throws ApplicationBusinessException {
		this.getRelatorioRegistroControlePacienteON().validaDataInicial(atdSeq, dataHoraInicial);
	}

	@Override
	public void validaDataFinal(Integer atdSeq, Date dataHoraFinal)
			throws BaseException {
	//	this.getRelatorioRegistroControlePacienteON().validarDataFinal(atdSeq,
	//			dataHoraFinal);
	}

	@Override
	public void verificaDuracaoAtendimento(Date dthrInicio, Date dthrFim)
			throws ApplicationBusinessException, BaseException {
		this.getRelatorioRegistroControlePacienteON().verificaDuracaoAtendimento(dthrInicio, dthrFim);
	}

	@Override
	public boolean datasForaIntervaloAtendimento(Date dthrInicio, Date dthrFim) {
		return this.getRelatorioRegistroControlePacienteON().datasForaIntervaloAtendimento(dthrInicio, dthrFim);
	}

	@Override
	public void validaDatasInicialFinal(Integer atdSeq, Date dthrInicio, Date dthrFim)
			throws ApplicationBusinessException {
		this.getRelatorioRegistroControlePacienteON().validaDatasInicialFinal(atdSeq, dthrInicio, dthrFim);
	}


	public RelatorioRegistroControlePacienteON getRelatorioRegistroControlePacienteON() {
		return relatorioRegistroControlePacienteON;
	}

	public void validaDatasInicialFinalInternacao(Date dthrInicio, Date dthrFim) throws ApplicationBusinessException {
		this.getRelatorioRegistroControlePacienteON().validaDatasInicialFinalInternacao(dthrInicio, dthrFim);
	}

	@Override
	public void validarDatasPesquisaRegistrosPaciente(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		getVisualizarRegistrosControleON().validarDatasPesquisaRegistrosPaciente(dataInicial, dataFinal);
	}

	@Override
	public List<EcpHorarioControle> listarHorarioControlePorSeqAtendimento(Integer seqAtendimento) {
		return this.getEcpHorarioControleDAO().listarHorarioControlePorSeqAtendimento(seqAtendimento);
	}

	@Override
	public List<EcpControlePaciente> listarControlePacientePorAtendimentoEPaciente(Integer atdSeq, Integer pacCodigo) {
		return this.getEcpControlePacienteDAO().listarControlePacientePorAtendimentoEPaciente(atdSeq, pacCodigo);
	}
	
	public Boolean verificarExisteControlePacientePorAtendimentoEPaciente(Integer atdSeq, Integer pacCodigo) {
		return this.getEcpControlePacienteDAO().verificarExisteControlePacientePorAtendimentoEPaciente(atdSeq,
				pacCodigo);
	}

	@Override
	public List<EcpControlePaciente> obterUltimosDadosControlePacientePelaCodigoPaciente(Integer pacCodigo) {
		return getManterControlesPacienteON().obterUltimosDadosControlePacientePelaCodigoPaciente(pacCodigo);
	}

	@Override
	public List<EcpControlePaciente> obterUltimosDadosControlePacientePelaConsulta(Integer numeroConsulta) {
		return getManterControlesPacienteON().obterUltimosDadosControlePacientePelaConsulta(numeroConsulta);
	}
	
	@Override
	public List<EcpItemControle> pesquisarDadosSinaisVitais(List<Short> listSeqs) {
		return ecpItemControleDAO.pesquisarSinaisVitaisAtivosPorDescricaoGrupo(listSeqs);
	}

	@Override
	public List<EcpItemControle> pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(String strPesquisa,
			Integer seqGrupo, Integer maxResults) {
		return ecpItemControleDAO.pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(strPesquisa, seqGrupo,
				maxResults);
	}

	@Override
	public Long pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupoCount(String strPesquisa, Integer seqGrupo) {
		return ecpItemControleDAO.pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupoCount(strPesquisa, seqGrupo);
	}
	
	@Override
	public Boolean verificarExisteSinalVitalPorPaciente(Short iceSeq, Integer pacCodigo) {
		return this.getEcpControlePacienteDAO().verificarExisteSinalVitalPorPaciente(iceSeq, pacCodigo);
	}
	
	@Override
	public String obterDescricaoItemControle(Short iceSeq) {
		return this.getEcpItemControleDAO().obterDescricaoItemControle(iceSeq);
	}
	
	@Override
	public BigDecimal buscarMedicaoUlceraPressaoPorAtendimento(Integer atdSeq, Short paramControleUP) {
		return this.getEcpHorarioControleDAO().buscarMedicaoUlceraPressaoPorAtendimento(atdSeq, paramControleUP);
	}
	
	@Override
	public Boolean verificarExisteSinalVitalPorPaciente(Integer pacCodigo) {
		return this.getEcpControlePacienteDAO().verificarExisteSinalVitalPorPaciente(pacCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public List<EcpControlePaciente> pesquisarRegistrosPacientePorAtdEPaciente(Integer atendimentoSeq,
			AipPacientes paciente) {
		return getVisualizarRegistrosControleON().pesquisarRegistrosPacientePorAtdEPaciente(atendimentoSeq, paciente);
	}
	
	@Override
	@BypassInactiveModule
	public List<EcpHorarioControle> pesquisarHorarioControlePorPaciente(Integer pacCodigo) {
		return getEcpHorarioControleDAO().pesquisarHorarioControlePorPaciente(pacCodigo);
	}
	
	@Override
	@BypassInactiveModule
	public EcpHorarioControle atualizarHorarioControle(EcpHorarioControle horarioControle) {
		return getEcpHorarioControleDAO().atualizar(horarioControle);
	}

	
	
	/**
	 * Obtem os horários de controle de paciente através do código da consulta
	 * 
	 * #48659
	 * @param consultaCodigo Código da consulta
	 * @return
	 */
	@Override
	public List<EcpHorarioControle> listarHorarioControlePorConsulta(Integer consultaCodigo) {
		return this.getEcpHorarioControleDAO().listarHorarioControlePorConsulta(consultaCodigo);
}
}
