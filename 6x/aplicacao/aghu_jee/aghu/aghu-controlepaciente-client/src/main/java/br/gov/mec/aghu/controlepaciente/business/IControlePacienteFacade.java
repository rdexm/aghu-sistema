package br.gov.mec.aghu.controlepaciente.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.controlepaciente.vo.PacienteInternadoVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteVO;
import br.gov.mec.aghu.controlepaciente.vo.RelatorioRegistroControlePacienteVO;
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

public interface IControlePacienteFacade extends Serializable {

	void gravar(EcpHorarioControle horario,List<EcpControlePaciente> controles) throws BaseException;
	
	/**
	 * Rotina que grava contigência de internação e coloca na fila para geração
	 * de pdf chama package no banco oracle - André Luiz Machado - 11/01/2012
	 * 
	 * @param horario
	 * @throws ApplicationBusinessException
	 */
	void dispararGeracaoContigencia(EcpHorarioControle horario)
			throws ApplicationBusinessException;

	boolean validaUnidadeFuncionalInformatizada(
			AghAtendimentos atendimento, Short unfSeq) throws ApplicationBusinessException;

	EcpHorarioControle obterHorariopelaDataHora(AipPacientes paciente,
			Date dataHora);

	EcpHorarioControle obterHorarioPeloId(long seq);

	EcpControlePaciente obterControlePeloId(long seq);

	void excluir(EcpControlePaciente controlePaciente)
			throws ApplicationBusinessException;

	void excluir(EcpHorarioControle horarioControle)
			throws ApplicationBusinessException;

	List<EcpControlePaciente> listaControlesHorario(
			EcpHorarioControle horario, EcpGrupoControle grupo);

	// mostra mensagem item fora do limite da normalidade
	String mensagemItemForaLimitesNormalidade(
			EcpHorarioControle horario, EcpControlePaciente controlePaciente)
			throws ApplicationBusinessException;

	List<PacienteInternadoVO> pesquisarPacientesInternados(
			RapServidores servidor) throws BaseException;

	List<AghUnidadesFuncionais> buscarListaUnidadesFuncionais(
			RapServidores servidor) throws ApplicationBusinessException;

	void salvarListaUnidadesFuncionaisEnfermagem(
			List<AghUnidadesFuncionais> listaServUnFuncionais,
			RapServidores servidor) throws ApplicationBusinessException;

	List<AghUnidadesFuncionais> pesquisarListaUnidadesFuncionais(
			String paramString) throws ApplicationBusinessException;

	AghAtendimentos pesquisarAtendimentoVigentePorLeito(String leito)
			throws ApplicationBusinessException;

	AghAtendimentos pesquisarAtendimentoVigentePorProntuarioPaciente(
			Integer numeroProntuario) throws ApplicationBusinessException;

	List<RegistroControlePacienteVO> pesquisarRegistrosPaciente(
			Integer atendimentoSeq, AipPacientes paciente, String leitoId,
			Date dataInicial, Date dataFinal,
			List<EcpItemControle> listaItensControle, Long trgSeq)
			throws ApplicationBusinessException;

	void excluirRegistroControlePaciente(Long seqHorarioControle) throws ApplicationBusinessException;
	
	void atualizarAtdSeqHorarioControlePaciente(Integer atdSeq, Long trgSeq) throws BaseException;

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
	List<RelatorioRegistroControlePacienteVO> criaRelatorioRegistroControlePaciente(
			Integer pacCodigo, Date dataHoraInicio, Date dataHoraFim, AghAtendimentos aghAtendimentos)
			throws ApplicationBusinessException;

	void validaDataInicial(Integer atdSeq, Date dataHoraInicial)
			throws ApplicationBusinessException;

	void validaDataFinal(Integer atdSeq, Date dataHoraFinal)
			throws BaseException;

	void verificaDuracaoAtendimento(Date dthrInicio, Date dthrFim) throws ApplicationBusinessException, BaseException;

	boolean datasForaIntervaloAtendimento(Date dthrInicio, Date dthrFim);

	void validaDatasInicialFinal(Integer atdSeq, Date dthrInicio,
			Date dthrFim) throws ApplicationBusinessException;

	void validaDatasInicialFinalInternacao(Date dthrInicio, Date dthrFim)
			throws ApplicationBusinessException;

	void validarDatasPesquisaRegistrosPaciente(Date dataInicial,
			Date dataFinal) throws ApplicationBusinessException;

	List<EcpHorarioControle> listarHorarioControlePorSeqAtendimento(
			Integer seqAtendimento);

	List<EcpControlePaciente> listarControlePacientePorAtendimentoEPaciente(
			Integer atdSeq, Integer pacCodigo);
	
	Boolean verificarExisteControlePacientePorAtendimentoEPaciente(Integer atdSeq, Integer pacCodigo);

	/**
	 * Busca os dados dos respectivos sinais vitais
	 * 
	 * #35315
	 * 
	 * @param listSeqs
	 * @return
	 */
	List<EcpItemControle> pesquisarDadosSinaisVitais(List<Short> listSeqs);

	/**
	 * Busca os sinais vitais por sigla, descricao, grupo e situacao
	 * 
	 * #35290
	 * 
	 * @param strPesquisa
	 * @param seqGrupo
	 * @param maxResults
	 * @return
	 */
	List<EcpItemControle> pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(String strPesquisa, Integer seqGrupo, Integer maxResults);

	/**
	 * Busca número de sinais vitais por sigla, descricao, grupo e situacao
	 * 
	 * #35290
	 * 
	 * @param strPesquisa
	 * @param seqGrupo
	 * @return
	 */
	Long pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupoCount(String strPesquisa, Integer seqGrupo);
	
	Boolean verificarExisteSinalVitalPorPaciente(Short iceSeq, Integer pacCodigo);
	
	String obterDescricaoItemControle(Short iceSeq);

	List<EcpControlePaciente> obterUltimosDadosControlePacientePelaConsulta(Integer numeroConsulta);

	List<EcpControlePaciente> obterUltimosDadosControlePacientePelaCodigoPaciente(Integer pacCodigo);
	
	BigDecimal buscarMedicaoUlceraPressaoPorAtendimento(Integer atdSeq, Short paramControleUP);
	
	Boolean verificarExisteSinalVitalPorPaciente(Integer pacCodigo);
	
	/**
	 * Obtem os horários de controle de paciente através do código da consulta
	 * 
	 * #48659
	 * @param consultaCodigo Código da consulta
	 * @return
	 */
	List<EcpHorarioControle> listarHorarioControlePorConsulta(Integer consultaCodigo);

	public List<EcpControlePaciente> pesquisarRegistrosPacientePorAtdEPaciente(
			Integer intSeq, AipPacientes paciente);

	List<EcpHorarioControle> pesquisarHorarioControlePorPaciente(Integer pacCodigo);

	EcpHorarioControle atualizarHorarioControle(EcpHorarioControle horarioControle);
	
}
