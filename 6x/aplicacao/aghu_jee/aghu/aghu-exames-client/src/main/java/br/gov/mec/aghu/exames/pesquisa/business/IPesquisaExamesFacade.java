package br.gov.mec.aghu.exames.pesquisa.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.pesquisa.vo.ItensProtocoloVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExameSituacaoVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesVO;
import br.gov.mec.aghu.exames.pesquisa.vo.ResultadoPesquisaProtocoloVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosVO;
import br.gov.mec.aghu.exames.questionario.vo.QuestionarioVO;
import br.gov.mec.aghu.exames.questionario.vo.RespostaQuestaoVO;
import br.gov.mec.aghu.exames.vo.RelatorioCaracteristicasResultadosPorExameVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelEquipamentos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelProtocoloEntregaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.IAelSolicitacaoExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IPesquisaExamesFacade extends Serializable {

	public PesquisaExamesPacientesResultsVO buscaDadosSolicitacaoPorSoeSeq(Integer soeSeq) throws BaseException;

	public List<PesquisaExamesPacientesResultsVO> buscaDadosItensSolicitacaoPorSoeSeq(Integer soeSeq, Short unfSeq) throws BaseException;

	public List<PesquisaExamesPacientesResultsVO> buscaDadosItensSolicitacaoCancelarColetaPorSoeSeq(Integer soeSeq, Short unfSeq)
			throws BaseException;

	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExames(Object param);

	public Long pesquisarMotivoCancelaExamesCount(Object param);

	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExamesColeta(Object param);

	public void cancelarExamesNaAlta(AghAtendimentos atendimento, AinTiposAltaMedica tipoAltaMedica, String nomeMicrocomputador)
			throws BaseException;

	public void cancelarExames(AelItemSolicitacaoExames aelItemSolicitacaoExames, final AelMotivoCancelaExames motivoCancelar,
			String nomeMicrocomputador) throws BaseException;

	public boolean permiteVisualizarLaudoMedico();

	public boolean permitevisualizarLaudoAtdExt();

	public boolean permitevisualizarLaudoSamis();

	public void validaSituacaoExamesSelecionados(Map<Integer, Vector<Short>> solicitacoes, Boolean isHist, Boolean validarSitExecutando)
			throws ApplicationBusinessException;
	
	public boolean validarSituacaoExameSelecionado(final String situacaoCodigo);

	public Map<Integer, Vector<Short>> obterListaSolicitacoesImpressaoLaudo(final Map<Integer, Vector<Short>> solicitacoes,
			final List<PesquisaExamesPacientesResultsVO> listaResultados, final Integer prontuario,
			final DominioTipoImpressaoLaudo tipoImpressaoLaudo) throws ApplicationBusinessException;

	public Boolean validaQuantidadeExamesSelecionados(final Map<Integer, Vector<Short>> solicitacoes);

	public void validarExamesComResposta(final Integer soeSeq, final Short seqp) throws ApplicationBusinessException;

	/** #5870 **/

	public List<VAelExamesSolicitacao> pesquisaExameSolicitacao(String descricao, AghUnidadesFuncionais unidadeExecutora)
			throws ApplicationBusinessException;

	public List<PesquisaExameSituacaoVO> pesquisaExameSolicitacaoPacAtend(AghUnidadesFuncionais unidadeExecutora, Date dtHrInicial,
			Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio, AelSitItemSolicitacoes situacao,
			VAelExamesSolicitacao nomeExame, DominioOrigemAtendimento origemAtendimento,
			DominoOrigemMapaAmostraItemExame origemMapaTrabalho, Integer firstResult, Integer maxResult, String orderProperty, boolean asc)
			throws BaseException;

	public List<PesquisaExameSituacaoVO> pesquisaExameSolicitacaoPacAtendRel(AghUnidadesFuncionais unidadeExecutora, Date dtHrInicial,
			Date dtHrFinal, Date dtHrProgramado, DominioConvenioExameSituacao convenio, AelSitItemSolicitacoes situacao,
			VAelExamesSolicitacao nomeExame, DominioOrigemAtendimento origemAtendimento, DominoOrigemMapaAmostraItemExame origemMapaTrabalho)
			throws ApplicationBusinessException;

	public void validarFiltroPesquisaExameSolicitacaoPacAtend(Date dtHrInicial, Date dtHrFinal, Date dtHrProgramado,
			AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame) throws BaseException;

	public Long countExameSolicitacaoPacAtend(AghUnidadesFuncionais unidadeExecutora, Date dtHrInicial, Date dtHrFinal,
			Date dtHrProgramado, DominioConvenioExameSituacao convenio, AelSitItemSolicitacoes situacao, VAelExamesSolicitacao nomeExame,
			DominioOrigemAtendimento origemAtendimento, DominoOrigemMapaAmostraItemExame origemMapaTrabalho);

	/** #5431 **/
	public List<VAelSolicAtendsVO> pesquisaMonitoramentoColetasEmergencia(AghUnidadesFuncionais unidadeExecutora) throws BaseException;

	public List<AelItemSolicitacaoExames> pesquisaMonitoramentoColetasEmergenciaItensProgramados(AghUnidadesFuncionais unidadeExecutora,
			VAelSolicAtendsVO vo) throws BaseException;

	public DominioOrigemAtendimento validaLaudoOrigemPaciente(IAelSolicitacaoExames solicitacaoExame);
	
	public DominioOrigemAtendimento validaLaudoOrigemPaciente(AelSolicitacaoExames solicitacaoExame);

	public DominioOrigemAtendimento validaLaudoOrigemPacienteHist(AelSolicitacaoExamesHist solicitacaoExame);

	public List<AelAgrpPesquisas> buscaAgrupamentosPesquisa(Object pesquisa);

	public List<AinLeitos> obterLeitosAtivosPorUnf(Object pesquisa, Short unfSeq);

	public List<VAelExamesSolicitacao> obterNomeExames(Object objPesquisa);

	public Long obterNomeExamesCount(Object objPesquisa);

	/* Métodos de pesquisa dos exames */
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorSolicExa(Integer seq_solicitacao);

	List<PesquisaExamesPacientesVO> buscarAipPacientesPorNumeroAp(Long numeroAp, AelConfigExLaudoUnico configExame);

	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorConsulta(Integer seq_consulta);

	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorParametros(Integer prontuarioPac, String nomePaciente, AinLeitos leitoPac,
			AghUnidadesFuncionais unidadeFuncionalPac);

	// public AghAtendimentos
	// obterAtendimentoPorNumeroConsultaLeitoProntuario(Integer pProntuario,
	// Integer pConNumero, String leitoID);

	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorServidor(Integer matricula, Short vinCodigo);

	List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorPacienteHist(AipPacientes paciente) throws ApplicationBusinessException;

	public List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorPaciente(Integer codigo, Integer seq_consulta,
			PesquisaExamesFiltroVO filtro) throws ApplicationBusinessException;

	List<PesquisaExamesPacientesResultsVO> buscaExamesSolicitadosPorSolicitante(PesquisaExamesFiltroVO filtro)
			throws ApplicationBusinessException;

	/**
	 * 2211 - Manter Cadastro de Equipamentos
	 */

	public List<AelEquipamentos> pesquisaListaEquipamentos(AghUnidadesFuncionais unidadeExecutora) throws BaseException;

	/**
	 * 2376 - Imprimir lista de caracteristicas de resultados por exame
	 * 
	 * @throws BaseException
	 */
	public List<RelatorioCaracteristicasResultadosPorExameVO> pesquisarRelatorioCaracteristicasResultadosExame(String siglaExame,
			Integer manSeq) throws ApplicationBusinessException;

	public Long obterLeitosAtivosPorUnfCount(Object pesquisa, Short unfSeq);

	public List<RapServidores> obterServidorSolic(String objPesquisa);

	public Integer obterServidorSolicCount(String objPesquisa);

	public List<VAelExamesAtdDiversosVO> buscarVAelExamesAtdDiversos(VAelExamesAtdDiversosFiltroVO filtro);

	Boolean verGeraCarta(AelItemSolicitacaoExames aelItemSolicitacaoExames);

	void geraCartaCanc(AelItemSolicitacaoExames aelItemSolicitacaoExames) throws ApplicationBusinessException;

	void setTimeout(final Integer timeout) throws ApplicationBusinessException;

	void commit(Integer timeout) throws ApplicationBusinessException;

	public String getUrlImpax(Map<Integer, Vector<Short>> solicitacoes) throws BaseException;

	public void validarExamesComRespostaHistorico(Integer codigoSoeSelecionado, Short iseSeqSelecionado)
			throws ApplicationBusinessException;

	List<QuestionarioVO> pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(Integer soeSeq, Short seqp, Boolean isHist);

	public List<RespostaQuestaoVO> pesquisarRespostasPorQuestionarioEItemSolicitacaoExame(Integer qtnSeq, Integer soeSeq, Short seqp,
			Boolean isHist);

	List<Short> buscaExamesSolicitadosOrdenados(Integer solicitacao, List<Short> seqps, Boolean isHist);
	
	public List<AelItemSolicitacaoExames> obterDadosItensSolicitacaoPorSoeSeq(Integer soeSeq, Short seqp);
	
	public AelItemSolicitacaoExames obterDadoItensSolicitacaoPorSoeSeq(Integer soeSeq, Short seqp);
	
	public List<PesquisaExamesPacientesVO> buscarAipPacientesPorNumProtocolo(Long seq_protocolo);
	
	public List<ResultadoPesquisaProtocoloVO> buscarProtocolo(Long protocolo);
	
	public List<ResultadoPesquisaProtocoloVO> buscarProtocoloPorProntuario(Integer prontuario);
	
	public List<ResultadoPesquisaProtocoloVO> buscarProtocoloPorSolicitacao(Integer solicitacao);
	
	public List<ItensProtocoloVO> buscarItensProtocolo(Long protocolo);
	
	public AelProtocoloEntregaExames recuperarNovoProtocolo(Long protocolo);
	

}