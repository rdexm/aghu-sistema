package br.gov.mec.aghu.prescricaomedica.business;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.AtestadoVO;
import br.gov.mec.aghu.comissoes.vo.SolicitacoesUsoMedicamentoVO;
import br.gov.mec.aghu.comissoes.vo.VMpmItemPrcrMdtosVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroTipoRespostaConsultoriaVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.constantes.TipoItemAprazamento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioDuracaoCalculo;
import br.gov.mec.aghu.dominio.DominioFinalizacao;
import br.gov.mec.aghu.dominio.DominioIndConcluidaSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioSituacaoPim2;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCalculoDose;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.dominio.DominioTipoMedicaoPeso;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoEspecial;
import br.gov.mec.aghu.dominio.DominioTipoReceituario;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.farmacia.vo.CodAtendimentoInformacaoPacienteVO;
import br.gov.mec.aghu.farmacia.vo.InformacoesPacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.farmacia.vo.QuantidadePrescricoesDispensacaoVO;
import br.gov.mec.aghu.faturamento.vo.ItemAlteracaoNptVO;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.internacao.vo.DadosAltaSumarioVO;
import br.gov.mec.aghu.internacao.vo.JustificativaComponenteSanguineoVO;
import br.gov.mec.aghu.internacao.vo.PesquisaFoneticaPrescricaoVO;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.paciente.prontuario.vo.AltaObitoSumarioVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelatorioEvolucoesPacienteVO;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumStatusItem;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.AfaCompoGrupoComponenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaComposicaoNptPadraoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaGrupoComponenteNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaItemNptPadraoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaMedicamentoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaCadastradaVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaEvolucaoEstadoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaPrincReceitasVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaRecomendacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioInfoComplVO;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumarioVO;
import br.gov.mec.aghu.prescricaomedica.vo.AvaliacaoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.CalculoAdultoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.CentralMensagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.CidAtendimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.CompSanguineoProcedHemoterapicoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConfirmacaoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaCompoGrupoComponenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaTipoComposicoesVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultarRetornoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultoriasInternacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.CursorPacVO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosDialiseVO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosPesoAlturaVO;
import br.gov.mec.aghu.prescricaomedica.vo.DetalhesParecerMedicamentosVO;
import br.gov.mec.aghu.prescricaomedica.vo.EstadoPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.GerarPDFSinanVO;
import br.gov.mec.aghu.prescricaomedica.vo.HistoricoParecerMedicamentosJnVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemDispensacaoFarmaciaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoDietaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.JustificativaMedicamentoUsoGeralVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.ListaPacientePrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.LocalDispensa2VO;
import br.gov.mec.aghu.prescricaomedica.vo.LocalDispensaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ModoUsoProcedimentoEspecialVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmComposicaoPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmSolicitacaoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PacienteListaProfissionalVO;
import br.gov.mec.aghu.prescricaomedica.vo.ParametrosProcedureVO;
import br.gov.mec.aghu.prescricaomedica.vo.ParecerPendenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ProcedimentoEspecialVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSolHemoterapicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioAltaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioAnamnesePacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioEstatisticaProdutividadeConsultorVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioLaudosProcSusVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioSumarioObitoVO;
import br.gov.mec.aghu.prescricaomedica.vo.RetornoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.SinamVO;
import br.gov.mec.aghu.prescricaomedica.vo.SolicitacaoConsultoriaVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPosAltaMotivoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaPrescricaoProcedimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosConsultoriasVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosCrgListasVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosCrgVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosVO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.TipoComposicaoComponenteVMpmDosagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaAtendimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificarDadosItensJustificativaPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.VisualizaDadosSolicitacaoConsultoriaVO;
import br.gov.mec.aghu.sig.custos.vo.NutricaoParenteralVO;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.vo.RapServidoresVO;

@SuppressWarnings({"PMD.ExcessiveClassLength"})
public interface IPrescricaoMedicaFacade extends Serializable {
	
	
	void agendarGerarDadosSumarioPrescricaoMedica(final String cron, final Date dataInicio, final Date dataFim, RapServidores servidorLogado, String nomeProcessoQuartz) throws ApplicationBusinessException; 

	/**
	 * Método que realiza o processo de tornar uma prescrição pendente.
	 * 
	 * @param prescricao
	 * @throws BaseException
	 */
	
	public void atualizarPrescricaoPendente(final Integer seqAtendimento,
			final Integer seqPrescricao, String nomeMicrocomputador) throws BaseException;

	/**
	 * Lista os itens confirmados de uma prescrição.
	 * 
	 * @param prescricao
	 * @param dataTrabalho
	 * @return
	 */
	
	public List<ItemPrescricaoMedica> listarItensPrescricaoMedicaConfirmados(
			final MpmPrescricaoMedica prescricao);

	/*
	 * Método responsável por executar a lógica de confirmar uma prescrição
	 * médica.
	 * 
	 * @param prescricao
	 * 
	 * @throws ApplicationBusinessException
	 */
	
	public ConfirmacaoPrescricaoVO confirmarPrescricaoMedica(MpmPrescricaoMedica prescricao, String nomeMicrocomputador, Date dataFimVinculoServidor)
			throws BaseException;

	/**
	 * Método responsável por executar a lógica de gravar as alteracoes de
	 * Sumario Alta - Evolucao e Estado do Paciente
	 * 
	 * @param altaEvolucaoEstadoVo
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	
	public AltaEvolucaoEstadoPacienteVO gravarAltaSumarioEvolucaoEstado(
			final AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoVo,
			final String origem) throws ApplicationBusinessException,
			ApplicationBusinessException;
	
	public AltaEvolucaoEstadoPacienteVO gravarAltaSumarioEstado(
			final AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoVo,
			final String origem) throws ApplicationBusinessException,
			ApplicationBusinessException;

	
	public AltaEvolucaoEstadoPacienteVO gravarAltaSumarioEvolucao(
			final AltaEvolucaoEstadoPacienteVO altaEvolucaoEstadoVo) throws ApplicationBusinessException,
			ApplicationBusinessException;


	/**
	 * Busca as informacoes pra o preenchimento da tela de Sumario de alta<br>
	 * Aba Evolucao<br>
	 * 
	 * @param altaSumario
	 *            , <code>MpmAltaSumario</code>
	 * @return <code>AltaEvolucaoEstadoPacienteVO</code>
	 */
	
	public AltaEvolucaoEstadoPacienteVO buscaAltaSumarioEvolucaoEstado(
			final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	/**
	 * Método responsável por executar a lógica de cancelar uma prescrição
	 * médica.
	 * 
	 * @param idAtendimento
	 * @param seqPrescricao
	 * @throws ApplicationBusinessException
	 */
	
	public void cancelarPrescricaoMedica(final Integer idAtendimento,
			final Integer seqPrescricao, String nomeMicrocomputador) throws BaseException;

	
	public String obtemNomeServidorEditado(final Short vinCodigo,
			final Integer matricula);

	
	public Date obterDataInternacao(final Integer int_seq,
			final Integer atu_seq, final Integer hod_seq)
			throws ApplicationBusinessException;

	
	
	public List<ItemPrescricaoMedicaVO> buscarItensPrescricaoMedica(
			final MpmPrescricaoMedicaId prescricaoMedicaId,
			final Boolean listarTodos) throws ApplicationBusinessException;

	
	public PrescricaoMedicaVO buscarDadosCabecalhoPrescricaoMedicaVO(
			final MpmPrescricaoMedicaId prescricaoMedicaId)
			throws ApplicationBusinessException;

	
	
	AghAtendimentos obterAtendimentoPorProntuario(final Integer prontuario);
	List<MpmPrescricaoMedica> obterMpmPrescricaoMedicaPorAghAtendimento(Integer seqAtendimento);

	MpmPrescricaoMedica obterPrescricaoMedicaPorId(final MpmPrescricaoMedicaId id);
	
	AghAtendimentos obterAtendimentoPorLeito(final String param) throws ApplicationBusinessException;

	
	public List<MpmPrescricaoMedica> pesquisarPrescricoesMedicasNaoEncerradasPorAtendimento(
			final Integer seqAtendimento);

	public MpmSumarioAlta obterSumarioAltaSemMotivoAltaPeloAtendimento(
			final Integer atdSeq);

	public MpmAltaSumario obterAltaSumarioConcluidaPeloAtendimento(
			final Integer apaAtdSeq);

	/**
	 * Retorna a lista de pacientes internados do profissional fornecido.
	 * 
	 * @param servidor
	 * @return lista de {@link PacienteListaProfissionalVO}
	 * @throws BaseException
	 */
	public List<PacienteListaProfissionalVO> listaPaciente(
			final RapServidores servidor) throws BaseException;
	
	public List<AelItemSolicitacaoExames> pesquisarExamesNaoVisualizados(final Integer atdSeq);

	/**
	 * Realiza as consistências antes de chamar a tela Sumário de Alta
	 * 
	 * @param atdSeq
	 * @throws BaseException
	 */
	public void realizarConsistenciasSumarioAlta(final Integer atdSeq)
			throws ApplicationBusinessException;

	public MpmTipoLaudo obterTipoLaudoComTiposSecundarios(Short seq);
	
	/**
	 * Realiza as consistências antes de chamar a tela Sumário de Óbito
	 * 
	 * @param atdSeq
	 * @throws ApplicationBusinessException
	 */
	public void realizarConsistenciasSumarioObito(final Integer atdSeq)
			throws ApplicationBusinessException;

	/**
	 * Verificar se existe motivo de óbito para o atendimento
	 * 
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean isMotivoAltaObito(final Integer atdSeq)
			throws ApplicationBusinessException;

	/**
	 * Realiza as consistências antes de chamar a tela de Diagnósticos
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void realizarConsistenciasDiagnosticos() throws ApplicationBusinessException;

	public AbsSolicitacoesHemoterapicas clonarSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica)
			throws ApplicationBusinessException;

	/**
	 * Utilizado pela estória Manter Prescricao Medicamento.
	 * 
	 * @return
	 */

	public List<MpmItemPrescricaoMdto> clonarItensPrescricaoMedicamento(
			final MpmPrescricaoMdto prescricaoMedicamento)
			throws ApplicationBusinessException;

	public MpmPrescricaoMdto ajustaIndPendenteN(
			final MpmPrescricaoMdto prescricaoMedicamento)
			throws ApplicationBusinessException;

	public MpmPrescricaoMdto ajustaIndPendenteN(
			final MpmPrescricaoMdto prescricaoMedicamento,
			final List<MpmItemPrescricaoMdto> listaItens)
			throws ApplicationBusinessException;

	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritosPelaChavePrescricao(
			final MpmPrescricaoMedicaId prescriao,
			final Date dtHrFimPrescricaoMedica, final Boolean isSolucao);

	public List<AbsSolicitacoesHemoterapicas> obterListaSolicitacoesHemoterapicasPelaChavePrescricao(
			final MpmPrescricaoMedicaId prescricaoId);

	public MpmPrescricaoMdto obterPrescricaoMedicamento(final Integer atdSeq,final Long seq);
	
	public MpmPrescricaoMdtosHist obterPrescricaoMedicamentoHist(final Integer atdSeq,
			final Long seq);

	public List<AfaMedicamentoPrescricaoVO> prepararListasMedicamento(
			final MpmAltaSumarioId id) throws ApplicationBusinessException;

	public void excluirJustificativaItemSolicitacaoHemoterapica(
			final AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa)
			throws ApplicationBusinessException;

	public AbsSolicitacoesHemoterapicas persistirSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
			throws BaseException;

	/**
	 * Este método chama a trigger MPMT_PDT_BRU de prescrição de dietas
	 * 
	 * @param prescricaoDieta
	 * @throws BaseException
	 */
	public void atualizarPrescricaoDieta(
			final MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;

	public void excluirSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador)
			throws BaseException;

	public List<VMpmDosagem> buscarDosagensMedicamento(
			final Integer medMatCodigo);

	public void verificarCriarPrescricao(final AghAtendimentos atendimento)
			throws ApplicationBusinessException;

	
	public Object criarPrescricao(
			final AghAtendimentos atendimento, final Date dataReferencia, String nomeMicrocomputador)
			throws BaseException;

	
	public void editarPrescricao(final MpmPrescricaoMedica prescricao,
			final Boolean cienteEmUso) throws BaseException;

	// TODO: 
	public List<MpmTipoFrequenciaAprazamento> buscarTipoFrequenciaAprazamento(
			final String strPesquisa);
	
	public Long buscarTipoFrequenciaAprazamentoCount(
			final String strPesquisa);

	// TODO: 
	public List<MpmTipoFrequenciaAprazamento> buscarTipoFrequenciaAprazamentoHemoterapico(
			final String strPesquisa);

	// TODO: 
	public Boolean validaBombaInfusao(final AghUnidadesFuncionais unidade,
			final AfaViaAdministracao viaAdministracao,
			final AfaMedicamento medicamento);

	/**
	 * 
	 * bsoliveira
	 * 
	 * 07/10/2010
	 * 
	 * Chama método que verifica se existe algum item na lista de itens da
	 * prescrição que devem ser excluido, cas exista chama o metodo de exclusão
	 * e remove o objeto da lista.
	 * 
	 * @throws ApplicationBusinessException
	 */

	// TODO: 
	public void verificaDoseFracionada(final Integer codigoMedicamento,
			final BigDecimal dose, final Integer seqFormaDosagem)
			throws ApplicationBusinessException;

	
	public void excluirSelecionados(final MpmPrescricaoMedica prescricaoMedica,
			final List<ItemPrescricaoMedicaVO> itens, String nomeMicrocomputador) throws BaseException;

	
	public void excluirPrescricaoMedicamento(
			final MpmPrescricaoMdto prescricaoMedicamento)
			throws ApplicationBusinessException;

	
	public void persistirPrescricaoMedicamento(
			final MpmPrescricaoMdto prescricaoMedicamento, String nomeMicrocomputador, MpmPrescricaoMdto prescricaoMedicamentoOriginal)
			throws BaseException;

	
	public void persistirPrescricaoMedicamento(
			final MpmPrescricaoMdto prescricaoMedicamento,
			final Boolean isCopiado, String nomeMicrocomputador, MpmPrescricaoMdto prescricaoMedicamentoOriginal) throws BaseException;

	
	public void persistirPrescricaoMedicamentos(
			final List<MpmPrescricaoMdto> prescricaoMedicamentos, String nomeMicrocomputador,  List<MpmPrescricaoMdto> prescricaoMedicamentoOriginal)
			throws BaseException;

	
	public void persistirParecerItemPrescricaoMedicamento(
			final MpmItemPrescParecerMdto itemParecer)
			throws ApplicationBusinessException;

	public List<AghEspecialidades> getListaEspecialidades(
			final RapServidores servidor) throws ApplicationBusinessException;

	public List<AghEspecialidades> getListaEspecialidades(final String parametro)
			throws ApplicationBusinessException;

	public void salvarListaEspecialidades(
			final List<AghEspecialidades> listaEspecialidades,
			final RapServidores servidor) throws ApplicationBusinessException;

	public List<AghEquipes> getListaEquipes(final RapServidores servidor)
			throws ApplicationBusinessException;

	public List<ProfessorCrmInternacaoVO> getListaResponsaveis(
			final RapServidores servidor) throws ApplicationBusinessException;

	public List<AghEquipes> getListaEquipesPorEspecialidade(
			final Object objPesquisa, final AghEspecialidades especialidade,
			final DominioSituacao situacao) throws ApplicationBusinessException;

	public List<VRapServidorConselho> getListaProfissionaisPorEspecialidade(
			final Object objPesquisa, final AghEspecialidades especialidade)
			throws ApplicationBusinessException;

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			final RapServidores servidor) throws ApplicationBusinessException;

	public List<AghEquipes> getListaEquipes(final String paramString)
			throws ApplicationBusinessException;

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			final String paramString) throws ApplicationBusinessException;

	public void salvarListaEquipes(final List<AghEquipes> listaEquipes,
			final RapServidores servidor) throws ApplicationBusinessException;

	public void salvarListaResponsaveis(
			final List<ProfessorCrmInternacaoVO> listaResponsaveis,
			final RapServidores servidor) throws ApplicationBusinessException;

	public void salvarListaUnidadesFuncionais(
			final List<AghUnidadesFuncionais> listaUnidadesFuncionais,
			final RapServidores servidor) throws ApplicationBusinessException;

	public List<AghAtendimentos> getListaAtendimentos(
			final RapServidores servidor) throws ApplicationBusinessException;

	public MpmListaPacCpa getPacienteCuidadosPosAnestesicos(
			final RapServidores servidor) throws ApplicationBusinessException;

	public MpmAltaSumario pesquisarAltaSumarios(MpmAltaSumarioId id);
	
	Long obterQtAltasSumario(final Integer atdSeq);
	
	public void salvarIndicadorPacientesAtendimento(
			final boolean indicadorPacCPA, final RapServidores servidor)
			throws ApplicationBusinessException;

	public List<AghAtendimentos> getListaAtendimentos(final Integer prontuario)
			throws ApplicationBusinessException;

	public void salvarListaAtendimentos(
			final List<AghAtendimentos> listaServAtendimentos,
			final RapServidores servidor) throws ApplicationBusinessException;

	public List<AghAtendimentos> pesquisaFoneticaAtendimentos(
			final String nomePesquisaFonetica,
			final String leitoPesquisaFonetica,
			final String quartoPesquisaFonetica,
			final AghUnidadesFuncionais unidadeFuncionalPesquisaFoneticaSelecionada)
			throws ApplicationBusinessException;

	public List<MbcProcedimentoCirurgicos> getListaProcedimentoCirurgicosRealizadosNoLeito(
			final Object objPesquisa);

	public List<ScoMaterial> getListaMateriaisOrteseseProteses(
			final BigDecimal paramVlNumerico, final Object objPesquisa)
			throws ApplicationBusinessException;

	public List<MpmTipoModoUsoProcedimento> getListaTipoModoUsoProcedEspeciaisDiversos(
			final Object objPesquisa,
			final MpmProcedEspecialDiversos procedEspecial)
			throws ApplicationBusinessException;

	public List<MpmProcedEspecialDiversos> getListaProcedEspecialDiversos(
			final Object objPesquisa);

	public List<ItemPrescricaoMedicaVO> buscaListaMedicamentoPorPrescricaoMedica(
			final MpmPrescricaoMedicaId prescricaoMedicaId);

	public List<ItemPrescricaoMedicaVO> buscaListaProcedimentoEspecialPorPrescricaoMedica(
			final MpmPrescricaoMedicaId prescricaoMedicaId);

	public Short buscaCalculoQuantidade24Horas(final Short frequencia,
			final Short seqTipoFrequenciaAprazamento,
			final BigDecimal dosePrescrita, final Integer seqFormaDosagem,
			final Integer codigoMedicamento) throws ApplicationBusinessException,
			ApplicationBusinessException;

	/**
	 * Sumário de Alta
	 * 
	 * @param apaAtdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */

	public MpmAltaSumario obterAltaSumariosAtivoConcluido(
			final Integer apaAtdSeq);

	public Boolean habilitarAltaSumario(final Integer atdSeq);

	
	public AltaSumarioVO populaAltaSumarioVO(final MpmAltaSumario altaSumario,
			final String origem) throws BaseException;

	public MpmUnidadeMedidaMedica obterUnidadeMedicaPorId(
			final Integer fdsUmmSeq);

	public void atualizarIdade(final AltaSumarioVO altaSumarioVO)
			throws ApplicationBusinessException;

	public void atualizarDiasPermanencia(final AltaSumarioVO altaSumarioVO)
			throws ApplicationBusinessException;

	public String obterDescricaoServicoOutrasEquipes(
			final Integer asuApaAtdSeq, final Integer asuApaSeq,
			final Short asuSeqp) throws ApplicationBusinessException;

	/**
	 * Atualiza os campos informados na alta sumário especificada pelo parâmetro
	 * id.
	 * 
	 * @param id
	 *            identificador da alta sumário.
	 * @param diasPermanencia
	 * @param idadeDias
	 * @param idadeMeses
	 * @param idadeAnos
	 * @param dataAlta
	 * @throws ApplicationBusinessException
	 */
	
	public void atualizarAltaSumario(final MpmAltaSumarioId id,
			final Short diasPermanencia, final Integer idadeDias,
			final Integer idadeMeses, final Short idadeAnos, final Date dataAlta, String nomeMicrocomputador)
			throws ApplicationBusinessException;
			
	public void atualizarAltaSumario(final MpmAltaSumario altaSumario, String nomeMicrocomputador)
			throws BaseException;

	public void atualizarAltaSumarioViaRN(final MpmAltaSumario altaSumario, String nomeMicrocomputador)
			throws ApplicationBusinessException;

	public void verificarDataTRF(final Integer novoAtdSeq,
			final Date novoDtHrTransf) throws ApplicationBusinessException;

	
	public Date obterDataReferenciaProximaPrescricao(
			final AghAtendimentos atendimento)
			throws ApplicationBusinessException;

	
	public MpmPrescricaoMedica obterUltimaPrescricaoAtendimento(
			final Integer atdSeq, final Date dtReferencia, final Date dataInicio)
			throws ApplicationBusinessException;

	public ProcedimentoEspecialVO buscaPrescricaoProcedimentoEspecialVOPorId(
			final MpmPrescricaoProcedimentoId idPrescProc);

	
	public void gravarPrescricaoProcedimentoEspecial(
			final MpmPrescricaoProcedimento prescProc,
			final List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao,
			final DominioTipoProcedimentoEspecial tipo, String nomeMicrocomputador, Boolean formChanged) throws BaseException;

	public void verificarTipoModoUsoProcedimento(final Short novoTupSeqp,
			final Short novoTupPedSeq, final Short novaQuantidade)
			throws ApplicationBusinessException;

	public void geraDadosSumarioPrescricao(final Integer seqAtendimento,
			final DominioTipoEmissaoSumario tipoEmissao)
			throws ApplicationBusinessException;

	public void atualizarLaudo(final MpmLaudo laudoNew, final MpmLaudo laudoOld)
			throws BaseException;

	public void inserirLaudo(final MpmLaudo laudoNew) throws BaseException;

	public List<MpmLaudo> listarLaudosPorAtendimento(final Integer atdSeq);

	public List<MpmTextoPadraoLaudo> listarTextoPadraoLaudosPorLaudo(
			final Integer laudoSeq);

	/**
	 * 
	 * Verifica se o paciente está com previsão de alta nas próximas XX hors (48);
	 * Implementação da função  MPMC_PAC_PRV_ALTA_48H.
	 * @author andremachado - 09/03/2012
	 * @param aghAtendimentos
	 * @return
	 */
	
	public boolean verificaPrevisaoAltaProxima(
			final AghAtendimentos aghAtendimentos);

	/**
	 * Faz uma pré validação do objeto antes mesmo da prescrição de dieta
	 * existir.
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	public void preValidar(final MpmItemPrescricaoDieta itemDieta,
			final List<ItemPrescricaoDietaVO> listaItens)
			throws BaseException;

	public void excluirPrescricaoDieta(final MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador)
			throws BaseException;

	public void removerPrescricaoProcedimento(
			final MpmPrescricaoProcedimento prescricaoProcedimento) throws BaseException;

	public void atualizarPrescricaoProcedimento(
			final MpmPrescricaoProcedimento prescricaoProcedimento, String nomeMicrocomputador)
			throws BaseException;

	/**
	 * Retorna lista com todos os cuidados usuais ativos e que podem ser
	 * prescritos para a unidade onde o paciente está sendo atendido.
	 * 
	 * @param descricao
	 * @param unidade
	 * @return
	 */
	public List<MpmCuidadoUsual> getListaCuidadosUsuaisAtivosUnidade(
			final Object descricao, final AghUnidadesFuncionais unidade);

	List<MpmCuidadoUsual> listarCuidadosMedicos(Integer codigo, String descricao, Boolean indCci,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	Long listarCuidadosMedicosCount(Integer codigo, String descricao, Boolean indCci);
	
	void atualizarMpmCuidadoUsual(MpmCuidadoUsual mpmCuidadoUsual);
	
	List<MpmTipoRespostaConsultoria> listarTiposRespostasConsultoria(FiltroTipoRespostaConsultoriaVO filtro,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	Long listarTiposRespostasConsultoriaCount(FiltroTipoRespostaConsultoriaVO filtro);
	
	void inserirTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException;
	
	void atualizarTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException;
	
	void excluirTipoRespostaConsultoria(MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria)
			throws ApplicationBusinessException;
	
	MpmTipoRespostaConsultoria obterMpmRespostaConsultoriaPorSeq(Short seq);
	
	AghEspecialidades obterEspecialidadePorUsuarioConsultor();
	
	void verificarAcessoProfissionalEspecialidade(Short espSeq) throws ApplicationBusinessException;
	
	List<ConsultoriasInternacaoVO> listarConsultoriasInternacaoPorAtendimento(Short espSeq, Short unfSeq, DominioTipoSolicitacaoConsultoria tipo,
			DominioSimNao urgencia, DominioSituacaoConsultoria situacao) throws ApplicationBusinessException;
	
	public MpmAltaSumario obterAltaSumario(final MpmAltaSumarioId id);
	Integer obterNextValPleSeq();

	/**
	 * Retorna o número de registros de cuidados usuais ativos e que podem ser
	 * prescritos para a unidade onde o paciente está sendo atendido.
	 * 
	 * @param descricao
	 * @param unidade
	 * @return
	 */
	public Long getListaCuidadosUsuaisAtivosUnidadeCount(
			final Object descricao, final AghUnidadesFuncionais unidade);

	public List<MpmPrescricaoCuidado> obterListaCuidadosPrescritos(
			final MpmPrescricaoMedicaId prescricaoMedicaId, final Date dthrFim);

	public MpmPrescricaoCuidado obterPrescricaoCuidado(final Integer atdSeq,
			final Long seq);

	public MpmAltaSumario recuperarSumarioAlta(final Integer altanAtdSeq,
			final Integer altanApaSeq) throws ApplicationBusinessException;

	public MpmAltaSumario gerarAltaSumario(final Integer altanAtdSeq,
			final Integer altanApaSeq, final String altanListaOrigem)
			throws BaseException;

	public MpmAltaSumario versionarAltaSumario(
			final MpmAltaSumario altaSumario, final String altanListaOrigem, String nomeMicrocomputador)
			throws BaseException;

	public List<MpmCidAtendimento> buscarMpmCidsPorAtendimento(
			final AghAtendimentos atendimento) throws ApplicationBusinessException;

	public List<MpmCidAtendimento> buscarHistoricoMpmCidsPorAtendimento(
			final AghAtendimentos atendimento) throws ApplicationBusinessException;

	public boolean verificarEmergencia(final Integer altanAtdSeq,
			final Integer altanApaSeq, final Short seqp)
			throws ApplicationBusinessException;

	public boolean inserirAltaDiagPrincipal(
			final SumarioAltaDiagnosticosCidVO altaDiagnosticosCidVO)
			throws BaseException;

	public void removerAltaDiagPrincipal(
			final SumarioAltaDiagnosticosCidVO altaDiagnosticosCidVO)
			throws ApplicationBusinessException;

	public void gerarAltaOtrProcedimento(final MpmAltaSumario altaSumario)
			throws BaseException;

	public void gerarAltaOutraEquipeSumr(final MpmAltaSumario altaSumario)
			throws BaseException;

	public List<MpmTipoLaudoConvenio> pesquisarTipoLaudoConvenio(Short tipoLaudoSeq);
	
	public List<MpmTipoLaudoTextoPadrao> pesquisarTipoLaudoTextoPadrao(Short tipoLaudoSeq);
	
	public void inserirMpmCidAtendimento(
			final MpmCidAtendimento mpmCidAtendimento,
			final RapServidores servidor) throws ApplicationBusinessException;

	public void atualizarMpmCidAtendimento(
			final MpmCidAtendimento mpmCidAtendimento,
			final RapServidores servidor) throws ApplicationBusinessException;

	public void excluirMpmCidAtendimento(
			final MpmCidAtendimento mpmCidParaExcluir,
			final RapServidores servidor) throws ApplicationBusinessException;

	public void inserir(final MpmPrescricaoCuidado prescricaoCuidado, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	public void alterarPrescricaoCuidado(
			final MpmPrescricaoCuidado prescricaoCuidado, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	public void verificarRegrasNegocioAtualizacaoConsultoria(
			final MpmSolicitacaoConsultoria solicitacaoConsultoria)
			throws BaseException;

	public void excluirSolicitacaoConsultoria(
			final MpmSolicitacaoConsultoria solicitacaoConsultoria)
			throws BaseException;

	public void removerPrescricaoCuidado(
			final MpmPrescricaoCuidado prescricaoCuidado) throws ApplicationBusinessException;

	public String buscaProcedimentoHospitalarInternoAgrupa(
			final Integer phiSeq, final Short cnvCodigo, final Byte cspSeq,
			final Short phoSeq) throws ApplicationBusinessException;

	/**
	 * Obtém o número de vias de um relatório conforme a unidade funcional
	 * 
	 * @param prescricaoMedica
	 * @return
	 */
	public Byte obterNumeroDeViasRelatorio(
			final MpmPrescricaoMedica prescricaoMedica);

	public Long buscaDescricaoProcedimentoHospitalarInterno(
			final Integer phiSeq, final Short cnvCodigo, final Byte cspSeq,
			final Short phoSeq) throws ApplicationBusinessException;

	public String buscaJustificativaItemLaudo(final Integer atdSeq,
			final Integer phiSeq);

	public void gerarAltaCirgRealizada(final MpmAltaSumario altaSumario,
			final Integer pacCodigo) throws BaseException;

	public void gerarAltaConsultoria(final MpmAltaSumario altaSumario)
			throws BaseException;

	public String obterDescricaoFormatadaDietaRelatorioItensConfirmados(
			final MpmPrescricaoDieta dietaConfirmada,
			final Boolean inclusaoExclusao, final Boolean impressaoTotal);

	public String obterDescricaoAlteracaoDietaRelatorioItensConfirmados(
			final MpmPrescricaoDieta dieta);

	public String obterDescricaoFormatadaCuidadoRelatorioItensConfirmados(final Integer atdSeq, final Long seqCuidado);

	public String obterDescricaoAlteracaoCuidadoRelatorioItensConfirmados(
			final MpmPrescricaoCuidado cuidado);

	public String obterDescricaoFormatadaMedicamentoSolucaoRelatorioItensConfirmados(
			final MpmPrescricaoMdto medicamentoConfirmada,
			final Boolean inclusaoExclusao, final Boolean impressaoTotal,
			final Boolean isUpperCase,
			final Boolean incluirCodigoMedicamentos) throws ApplicationBusinessException;

	public List<ItemDispensacaoFarmaciaVO> obterDescricaoDosagemItensMedicamentoSolucaoDispensacaoFarmacia(
			final MpmPrescricaoMdto medicamentoSolucao, final Boolean isUpperCase);
	
	public String obterDescricaoAlteracaoMedicamentoSolucaoDispensacaoFarmacia(
			final MpmPrescricaoMdto prescricaoMedicamento, String aprazamento,final Boolean isUppercase) throws ApplicationBusinessException;
	
	public List<ItemDispensacaoFarmaciaVO> obterDescricaoDosagemAlteracaoItensMedicamentoSolucaoDispensacaoFarmacia(
			final MpmPrescricaoMdto medicamentoSolucao, final Boolean isUpperCase);
	
	public String obterDescricaoAlteracaoMedicamentoSolucaoRelatorioItensConfirmados(
			final MpmPrescricaoMdto medicamentoConfirmada,
			final Boolean isUpperCase) throws ApplicationBusinessException;

	public String obterDescricaoFormatadaConsultoriaRelatorioItensConfirmados(Integer atdSeq, Integer seq);

	public String obterDescricaoAlteracaoConsultoriaRelatorioItensConfirmados(final Integer atdSeq, final Integer seq);

	public String obterDescricaoFormatadaSolicitacaoHemoterapicasRelatorioItensConfirmados(Integer atdSeq, Integer seq, Boolean impressaoTotal, Boolean inclusaoExclusao);

	public String obterDescricaoAlteracaoSolicitacaoHemoterapicasRelatorioItensConfirmados(Integer atdSeq, Integer seq, Boolean impressaoTotal);

	public String obterDescricaoFormatadaProcedimentoRelatorioItensConfirmados(final Integer atdSeq, final Long seq, Boolean impressaoTotal, Boolean inclusaoExclusao);

	public List<ItemPrescricaoMedicaVO> buscarItensPrescricaoMedica(MpmPrescricaoMedicaId prescricaoMedicaId,
			Boolean dieta, Boolean cuidadoMedico, Boolean medicamento, 
			Boolean solucao, Boolean consultoria, Boolean hemoterapia, 
			Boolean nutricaoParental, Boolean procedimento, Boolean listarTodas
			) throws ApplicationBusinessException;
	
	public String obterDescricaoAlteracaoProcedimentoRelatorioItensConfirmados(final Integer atdSeq, final Long seq);

	/**
	 * Retorna a prescrição médica pelo id. * @param atdSeq
	 * 
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoMedica obterPrescricaoPorId(final Integer atdSeq,
			final Integer seq);

	public MpmPrescricaoProcedimento obterProcedimentoPorId(
			final Integer atdSeq, final Long seq);

	public Integer recuperarAtendimentoPaciente(final Integer altanAtdSeq)
			throws ApplicationBusinessException;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void desbloquearAlta(final Integer atdSeq, String nomeMicrocomputador)
			throws ApplicationBusinessException, Exception;

	public List<RelatorioLaudosProcSusVO> pesquisaLaudoProcedimentoSus(
			final Integer seqAtendimento, final Integer apaSeq, final Short seqp)
			throws ApplicationBusinessException;

	public List<RelatorioLaudosProcSusVO> pesquisaLaudoProcedimentoSus(
			final Integer seqAtendimento, final Integer apaSeq, final Short seqp, RapServidores servidorLogado)
			throws ApplicationBusinessException;

	public List<RelSolHemoterapicaVO> pesquisarHemoterapiasRelatorio(
			final MpmPrescricaoMedica prescricaoMedica,
			final EnumTipoImpressao tipoImpressao, RapServidores servidorValida, Date dataMovimento) throws BaseException;

	public MpmPrescricaoDieta obterPrescricaoDieta(final MpmPrescricaoDietaId id);

	public MpmPrescricaoDieta obterPosterior(final MpmPrescricaoDieta dieta);

	/**
	 * Grava alterações na prescrição de dieta criando, alterando ou excluido
	 * itens associados.
	 * 
	 * @see gravar(MpmPrescricaoDieta, List, List, List)
	 * @param dieta
	 * @param novos
	 * @param alterados
	 * @param excluidos
	 * @throws CloneNotSupportedException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 */
	public void gravar(final MpmPrescricaoDieta dieta,
			final List<MpmItemPrescricaoDieta> novos,
			final List<MpmItemPrescricaoDieta> alterados,
			final List<MpmItemPrescricaoDieta> excluidos, String nomeMicrocomputador)
			throws BaseException, CloneNotSupportedException;

	/**
	 * Grava inclusão de prescrição de dieta criando itens associados.
	 * 
	 * @see gravar(MpmPrescricaoDieta, List)
	 * @param dieta
	 * @param novos
	 */
	public void gravar(final MpmPrescricaoDieta dieta,
			final List<MpmItemPrescricaoDieta> novos, String nomeMicrocomputador) throws BaseException;

	

	



	public void inserir(final MpmPrescricaoDieta dieta,
			final List<MpmItemPrescricaoDieta> itensDieta)
			throws BaseException;



	public void removerPrescricaoMedicamento(
			final MpmPrescricaoMedica prescricaoMedica,
			final MpmPrescricaoMdto prescicaoMedicamento, String nomeMicrocomputador
			, MpmPrescricaoMdto prescricaoMedicamentoOriginal)
			throws BaseException;

	public Boolean isPrescricaoVigente(final Date dthrInicio, final Date dthrFim)
			throws ApplicationBusinessException;

	public boolean isPrescricaoVigente(
			final MpmPrescricaoMedica prescricaoMedica);

	public void validarInserirDieta(final MpmPrescricaoDieta prescricaoDieta)
			throws ApplicationBusinessException;

	public void verificaPrescricaoMedica(final Integer seqAtendimento,
			final Date dataHoraInicio, final Date dataHoraFim,
			final Date dataHoraMovimentoPendente,
			final DominioIndPendenteItemPrescricao pendente,
			final String operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException;

	public List<SumarioAltaDiagnosticosCidVO> pesquisarMotivosInternacaoCombo(
			final MpmAltaSumarioId id);

	public List<MpmAltaDiagMtvoInternacao> obterMpmAltaDiagMtvoInternacao(
			final Integer altanAtdSeq, final Integer altanApaSeq,
			final Short altanAsuSeqp) throws ApplicationBusinessException;

	public MpmAltaDiagPrincipal obterAltaDiagPrincipal(
			final Integer altanAtdSeq, final Integer altanApaSeq,
			final Short altanAsuSeqp) throws ApplicationBusinessException;

	public List<MpmAltaDiagSecundario> obterAltaDiagSecundario(
			final Integer altanAtdSeq, final Integer altanApaSeq,
			final Short altanAsuSeqp) throws ApplicationBusinessException;

	public void inserirAltaDiagMtvoInternacao(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException;

	public MpmObtCausaDireta obterObtCausaDireta(final Integer altanAtdSeq,
			final Integer altanApaSeq, final Short altanAsuSeqp)
			throws BaseException;

	public String inserirObtCausaDireta(
			final SumarioAltaDiagnosticosCidVO cidsCausaDiretaVO)
			throws ApplicationBusinessException;

	public void removerObtCausaDireta(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException;

	public void removerObtCausaDireta(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	public void removerObitoNecropsia(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	public MpmObitoNecropsia obterMpmObitoNecropsia(
			final MpmAltaSumario altaSumario);

	public String gravarObitoNecropsia(final MpmAltaSumario altaSumario,
			final DominioSimNao dominioSimNao) throws BaseException;

	public void atualizarMpmAltaDiagMtvoInternacao(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException;

	public void atualizarObtCausaDireta(
			final SumarioAltaDiagnosticosCidVO cidsCausaDiretaVO)
			throws ApplicationBusinessException;

	public void verificaPrescricaoMedicaUpdate(final Integer seqAtendimento,
			final Date dataHoraInicio, final Date dataHoraFim,
			final Date dataHoraMovimentoPendente,
			final DominioIndPendenteItemPrescricao pendente,
			final String operacao, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws ApplicationBusinessException;

	public void removerAltaDiagMtvoInternacao(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException;

	public void removerAltaDiagSecundario(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException;

	public void inserirAltaDiagSecundario(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException;

	public void atualizarAltaDiagSecundario(
			final SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO)
			throws ApplicationBusinessException;

	public List<CompSanguineoProcedHemoterapicoVO> pesquisarCompSanguineoProcedHemoterapico()
			throws ApplicationBusinessException;

	/**
	 * Recebe um MAP contendo uma lista de justificativasVO para cada grupo de
	 * justificativas, onde o Id do grupo é chave no MAP.
	 * 
	 * @param justificativasSelecionadas
	 */
	List<AbsItemSolicitacaoHemoterapicaJustificativa> gravarItemSolicitacaoHemoterapicaJustificativa(
			final Map<Short, List<JustificativaComponenteSanguineoVO>> justificativasSelecionadas)
			throws ApplicationBusinessException;

	/**
	 * Retorna os itens da prescrição dieta fornecida.
	 * 
	 * @param dieta
	 * @return
	 */
	public Set<MpmItemPrescricaoDieta> obterItensPrescricaoDieta(
			final MpmPrescricaoDieta dieta);
	
	public SumarioAltaProcedimentosCrgListasVO pesquisarCirurgiasRealizadas(
			final MpmAltaSumarioId id) throws ApplicationBusinessException;

	public List<SumarioAltaProcedimentosCrgVO> pesquisarCirurgiasRelizadasCombo(
			final MpmAltaSumarioId id);

	public List<SumarioAltaProcedimentosCrgVO> pesquisarCirurgiasRelizadasGrid(
			final MpmAltaSumarioId id) throws ApplicationBusinessException;

	public List<SumarioAltaProcedimentosConsultoriasVO> pesquisarConsultoriasCombo(
			final MpmAltaSumarioId id);

	public List<SumarioAltaProcedimentosConsultoriasVO> pesquisarConsultoriasGrid(
			final MpmAltaSumarioId id) throws ApplicationBusinessException;

	public List<SumarioAltaPrescricaoProcedimentoVO> pesquisarPrescricaoProcedimentoCombo(
			final MpmAltaSumarioId id);

	public List<SumarioAltaPrescricaoProcedimentoVO> pesquisarPrescricaoProcedimentoGrid(
			final MpmAltaSumarioId id) throws ApplicationBusinessException;

	public void populaListaComboPrescricaoProcedimento(
			final List<SumarioAltaPrescricaoProcedimentoVO> listaComboPrescricaoProcedimentos,
			final MpmAltaSumarioId id);

	public List<SumarioAltaProcedimentosVO> pesquisarPrescricaoOutrosProcedimentoGrid(
			final MpmAltaSumarioId id) throws ApplicationBusinessException;

	public void inserirAltaCirgRealizada(
			final SumarioAltaProcedimentosCrgVO vo, final Date dthrCirurgia)
			throws BaseException;

	public String obterMensagemResourceBundle(String key);
	
	public void atualizarAltaCirgRealizada(
			final SumarioAltaProcedimentosCrgVO vo, final Date dthrCirurgia)
			throws BaseException;

	public void removerAltaCirgRealizada(final SumarioAltaProcedimentosCrgVO vo)
			throws ApplicationBusinessException;

	public void inserirAltaOtrProcedimento(
			final SumarioAltaPrescricaoProcedimentoVO vo,
			final Date dataProcedimento) throws BaseException;

	public void atualizarAltaOtrProcedimento(
			final SumarioAltaPrescricaoProcedimentoVO vo,
			final Date dataProcedimento) throws BaseException;

	public void removerAltaOtrProcedimento(
			final SumarioAltaPrescricaoProcedimentoVO vo)
			throws ApplicationBusinessException;

	public void inserirAltaOtrProcedimento(final SumarioAltaProcedimentosVO vo)
			throws BaseException;

	public void atualizarAltaOtrProcedimento(
			final SumarioAltaProcedimentosVO vo, final Date dataProcedimento,
			final String complemento) throws BaseException;

	public void removerAltaOtrProcedimento(final SumarioAltaProcedimentosVO vo)
			throws ApplicationBusinessException;

	public void inserirAltaConsultoria(
			final SumarioAltaProcedimentosConsultoriasVO itemEdicao,
			final Date dataConsultoria) throws BaseException;

	public void atualizarAltaConsultoria(
			final SumarioAltaProcedimentosConsultoriasVO itemEdicao,
			final Date dataConsultoria) throws BaseException;

	public void removerAltaConsultoria(
			final SumarioAltaProcedimentosConsultoriasVO itemEdicao)
			throws ApplicationBusinessException;

	public List<AghEspecialidades> listarEspecialidades(final Object objPesquisa);
	
	public Long listarEspecialidadesCount(final Object objPesquisa);

	public List<AghEspecialidades> listarEspecialidadesAtivas(
			final Object objPesquisa);

	public List<AghEspecialidades> listarEspecialidadesPorServidor(final RapServidores servidor);

	public List<AghProfEspecialidades> listarConsultoresPorEspecialidade(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final AghEspecialidades especialidade);
	
	public Long listarConsultoresPorEspecialidadeCount(final AghEspecialidades especialidade);

	public List<VMpmOtrProcedSum> listarVMpmOtrProcedSum(
			final String objPesquisa);

	public VMpmOtrProcedSum obterVMpmOtrProcedSum(final Integer matCodigo,
			final Integer pciSeq, final Short pedSeq);

	public void inserirMpmAltaPrincFarmaco(final AfaMedicamentoPrescricaoVO vo)
			throws ApplicationBusinessException;

	public void removerMpmAltaPrincFarmaco(final AfaMedicamentoPrescricaoVO vo)
			throws ApplicationBusinessException;

	public void inserirPrescricaoDieta(final MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador)
			throws BaseException;

	public List<String> gerarAprazamento(final Integer atdSeq, final Integer prescSeq,
			final Date dthrInicioItem, final Date dthrFimItem, Short seqTipoFrequenciaAprazamento,
			final TipoItemAprazamento tipoItem,
			final Date dtHrInicioTratamento, final Boolean indNecessario,
			final Short frequencia);

	public List<String> buscaResultadoExames(final AipPacientes paciente,
			final String codigoComponenteSanguineo,
			final String codigoProcedimentoHemoterapico)
			throws ApplicationBusinessException;

	
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			final Integer matricula, final Short vinculo)
			throws ApplicationBusinessException;
	
	/**
	 * Busca em MPM_MOTIVO_ALTA_MEDICAS.<br>
	 * Entidade: <code>MpmMotivoAltaMedica</code><br>
	 * Executa a regra pra nao retornar os itens jah associados ao Sumario de
	 * Alta.
	 * 
	 * @param altaSumario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SumarioAltaPosAltaMotivoVO> listaMotivoAltaMedica(
			final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	public List<SumarioAltaPosAltaMotivoVO> listaPlanoPosAlta(
			final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	public List<SumarioAltaPosAltaMotivoVO> buscaAltaMotivo(
			final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	public List<SumarioAltaPosAltaMotivoVO> buscaAltaPlano(
			final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	/**
	 * Grava ou altera as informações de <code> MpmAltaMotivo </code>
	 * 
	 * @param umMpmAltaMotivo
	 * @throws BaseException
	 */
	public void gravarAltaMotivo(final MpmAltaMotivo umMpmAltaMotivo)
			throws BaseException;

	/**
	 * Exclui um <code> MpmAltaMotivo </code> no banco.
	 * 
	 * @param vo
	 */
	public void removerAltaMotivo(final MpmAltaMotivo umMpmAltaMotivo)
			throws BaseException;

	public void gravarAltaPlano(final MpmAltaPlano umMpmAltaPlano)
			throws BaseException;

	public void removerAltaPlano(final MpmAltaPlano umMpmAltaPlano)
			throws BaseException;

	public Boolean verificarPrimeiraImpressao(RapServidores servidorValida);

	/**
	 * Método responsável por retornar uma lista de informação complementar.
	 * 
	 * @author gfmenezes
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */

	public List<AltaSumarioInfoComplVO> findListaInformacaoComplementar(
			final MpmAltaSumarioId id) throws ApplicationBusinessException;

	public EnumStatusItem buscarStatusItem(final ItemPrescricaoMedica item, Date dataMovimento)
			throws ApplicationBusinessException;

	/**
	 * Método responsável pela adição de uma informação complementar.
	 * 
	 * @author gfmenezes
	 * 
	 * @param informacaoComplementarVO
	 * @throws ApplicationBusinessException
	 */
	public void gravarAltaSumarioInformacaoComplementar(
			final String complemento,
			final AltaSumarioInfoComplVO informacaoComplementarVO)
			throws ApplicationBusinessException;

	/**
	 * Método responsável por excluir uma informação complementar.
	 * 
	 * @author gfmenezes
	 * 
	 * @param informacaoComplementarVO
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaSumarioInformacaoComplementar(
			final AltaSumarioInfoComplVO informacaoComplementarVO)
			throws ApplicationBusinessException;

	public List<VAelExamesSolicitacao> obterNomeExames(final Object objPesquisa);

	public AelUnfExecutaExames buscarAelUnfExecutaExamesPorID(
			final Integer manSeq, final String sigla, final Integer unfSeq);

	public AelMateriaisAnalises buscarAelMateriaisAnalisesPorAelUnfExecutaExames(
			final AelUnfExecutaExames aelUnfExecutaExames);

	public AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorAelUnfExecutaExames(
			final AelUnfExecutaExames aelUnfExecutaExames);

	public void inserirAltaItemPedidoExame(final MpmAltaItemPedidoExame item)
			throws BaseException;

	public List<MpmAltaItemPedidoExame> obterMpmAltaItemPedidoExame(
			final Integer altanAtdSeq, final Integer altanApaSeq,
			final Short altanAsuSeqp) throws ApplicationBusinessException;

	public void excluirMpmAltaItemPedidoExame(
			final MpmAltaItemPedidoExame altaItemPedidoExame)
			throws ApplicationBusinessException;
	
	public MpmItemPrescricaoMdto obterItemMedicamentoNaoDiluente(
			List<MpmItemPrescricaoMdto> listaItens);

	/**
	 * Busca os itens da Ultima Prescricao Medica associado ao
	 * altaSumario.atendimento.<br>
	 * Retorna todos os itens da Ultima Prescricao Medica os que estiverem
	 * associados ao SumarioAlta<br>
	 * estaram marcados. <code>ItemPrescricaoMedicaVO.marcado</code><br>
	 * 
	 * @param altaSumario
	 * @return
	 * @throws BaseException
	 */
	public List<ItemPrescricaoMedicaVO> buscaItensRecomendacaoPlanoPosAltaPrescricaoMedica(
			final MpmAltaSumario altaSumario) throws BaseException;

	public List<ItemPrescricaoMedicaVO> buscaItensPrescricaoMedicaMarcado(
			final MpmAltaSumario altaSumario) throws BaseException;

	/**
	 * 
	 * Atualiza os Itens Recomendados no Plano Pos Alta que deve ficar
	 * associados ao AltaSumario.<br>
	 * Remove (inativa) os que estavam associados e nao estao mais.<br>
	 * 
	 * @param altaSumario
	 * @param listaItens
	 * @throws BaseException
	 */
	public void gravarRecomendacaoPlanoPosAltaPrescricaoMedica(
			final MpmAltaSumario altaSumario,
			final List<ItemPrescricaoMedicaVO> listaItensInsert)
			throws BaseException;

	/**
	 * Retorna uma lista de alta recomendação
	 * 
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AltaRecomendacaoVO> listarAltaRecomendacao(
			final MpmAltaSumarioId id) throws BaseException;

	/**
	 * Método que exclui alta não cadastrada
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	public void excluirAltaNaoCadastrado(final AltaRecomendacaoVO vo)
			throws BaseException;

	/**
	 * Método que grava um alta não cadastrada
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	public void gravarAltaNaoCadastrado(final AltaRecomendacaoVO vo)
			throws BaseException;

	/**
	 * Método que atualiza uma alta não cadastrada
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAltaNaoCadastrado(final AltaRecomendacaoVO vo)
			throws BaseException;

	/**
	 * Método que atualiza uma alta cadastrada
	 * 
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAltaCadastrada(final AltaCadastradaVO vo)
			throws BaseException;

	public void atualizarAltaItemPrescricao(final MpmAltaSumario altaSumario,
			final ItemPrescricaoMedicaVO itemPrescricaoMedicaVO)
			throws ApplicationBusinessException;

	public void inativarAltaItemPrescricao(final MpmAltaSumario altaSumario,
			final ItemPrescricaoMedicaVO itemPrescricaoMedicaVO)
			throws BaseException;

	/**
	 * Valida informações de ionterface da Aba Procedimentos Slider Informações
	 * Complementares.
	 * 
	 * @param {AltaSumarioInfoComplVO} vo
	 * @throws BaseException
	 */
	public void validarInformacoesComplementares(final String complemento,
			final AltaSumarioInfoComplVO vo) throws BaseException;

	

	public MpmAltaPedidoExame obterMpmAltaPedidoExame(final Integer integer,
			final Integer integer2, final Short short1)
			throws ApplicationBusinessException;

	public void refazer(final Integer atdSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException;

	public Long countPesquisaListaSumAltaReimp(final Integer prontuario,
			final Integer codigo);

	public List<MpmListaServSumrAlta> listaPesquisaListaSumAltaReimp(
			final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc,
			final Integer prontuario, final Integer codigo);

	public List<RapServidores> listaProfissionaisEquipAtendimento(
			final RapServidores servidor);

	public List<MpmMotivoReinternacao> obterMpmMotivoReinternacao(
			final Object parametro);

	public MpmAltaReinternacao obterMpmAltaReinternacao(
			final Integer altanAtdSeq, final Integer altanApaSeq,
			final Short altanAsuSeqp) throws ApplicationBusinessException;

	public MpmMotivoReinternacao obterMotivoReinternacaoPeloId(final Integer seq);

	public void gravarAltaReinternacao(
			final MpmAltaReinternacao altaReinternacao) throws BaseException;

	public void removerAltaReinternacao(
			final MpmAltaReinternacao altaReinternacao) throws BaseException;

	public String gravarAltaPedidoExame(final MpmAltaPedidoExame altaPedidoExame)
			throws BaseException;

	public void removerAltaPedidoExame(final MpmAltaPedidoExame altaPedidoExame)
			throws BaseException;

	public String gravarObtGravidezAnterior(final MpmAltaSumario altaSumario,
			final DominioSimNao indicadorNecropsia)
			throws ApplicationBusinessException;

	public MpmObtGravidezAnterior obterMpmObtGravidezAnterior(
			final MpmAltaSumario altaSumario);

	public List<MpmObtCausaAntecedente> obterMpmObtCausaAntecedente(
			final Integer apaAtdSeq, final Integer apaSeq, final Short seqp)
			throws BaseException;

	public List<MpmObtOutraCausa> obterMpmObtOutraCausa(
			final Integer apaAtdSeq, final Integer apaSeq, final Short seqp)
			throws ApplicationBusinessException;

	public String persistirCausaAntecedente(
			final SumarioAltaDiagnosticosCidVO itemSelecionado)
			throws BaseException;

	public String persistirOutraCausa(
			final SumarioAltaDiagnosticosCidVO itemSelecionado)
			throws BaseException;

	public void cancelarSumario(final MpmAltaSumario altaSumario, String nomeMicrocomputador)
			throws BaseException;

	public void removerObtCausaAntecedente(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	public void removerObtCausaAntecedente(
			final SumarioAltaDiagnosticosCidVO itemGrid)
			throws ApplicationBusinessException;

	public void removerObtOutraCausa(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	public void removerObtOutraCausa(final SumarioAltaDiagnosticosCidVO itemGrid)
			throws ApplicationBusinessException;

	public List<AltaCadastradaVO> listarAltaCadastrada(
			final MpmAltaSumario altaSumario) throws BaseException;

	public List<AltaCadastradaVO> listarAltaCadastradaGravada(
			final MpmAltaSumario altaSumario) throws BaseException;

	public void gravarAltasCadastradasSelecionadas(
			final List<AltaCadastradaVO> listaAltasCadastradas,
			final MpmAltaSumario altaSumario) throws BaseException;

	public List<RelatorioConsultoriaVO> gerarDadosRelatorioConsultoria(
			final RapServidores servidor, final AghAtendimentos atd,
			final PrescricaoMedicaVO prescricao, Boolean validarIndImpSolicConsultoria) throws ApplicationBusinessException;

	public void inserirMotivoIngressoCti(
			final MpmMotivoIngressoCti motivoIngressoCti)
			throws ApplicationBusinessException;

	public void validarCamposObrigatoriosSumarioAlta(
			final MpmAltaSumario altaSumario) throws BaseException;

	/**
	 * Executa a Finalizacao da Conclusao do Sumario de Alta.<br>
	 * Validações para conclusao do Sumario de Alta.<br>
	 * Gera Laudos.<br>
	 * Verifica se precisa de Justificativas.<br>
	 * Atualiza flag no banco indicando a conclusao da alta do paciente.<br>
	 * 
	 * @param altaSumario
	 * @return <code>ehSolicitarJustificativa</code>
	 * @throws BaseException
	 */
	public boolean concluirSumarioAlta(final MpmAltaSumario altaSumario, String nomeMicrocomputador, RapServidores servidorValida)
			throws BaseException;

	public List<AtestadoVO> obterListaDocumentosPacienteAtestados(Integer apaAtdSeq, Integer apaSeq, Short seqp, Short tasSeq) throws ApplicationBusinessException;
	
	AtestadoVO obterDocumentoPacienteAtestado(Long atsSeq, Boolean imprimirAtestado) throws ApplicationBusinessException;

	public List<MpmAltaSumario> pesquisarAltaSumarios(Integer atdSeq);
	
	/**
	 * Executa Update no MpmAltaSumario.<br>
	 * Seta o indConcluido pra true.<br>
	 * 
	 * @param altaSumario
	 * @throws BaseException
	 */
	public void continuarConclusaoSumarioAlta(final MpmAltaSumario altaSumario, String nomeMicrocomputador)
			throws BaseException;

	/**
	 * Cancela a Conclusao do Sumaro de Alta.<br>
	 * Remove os Laudos gerados.<br>
	 * 
	 * Chamada no cancelar da tela de Justificativas de Laudos no Sumario de
	 * Alta.<br>
	 * 
	 * @param altaSumario
	 * @throws BaseException
	 */
	public void cancelarConclusaoSumarioAlta(final MpmAltaSumario altaSumario)
			throws BaseException;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void atualizarSumarioAlta(final MpmSumarioAlta sumarioAlta, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException, Exception;

	public void removerAltaMotivo(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	public void removerAltaEstadoPaciente(final MpmAltaSumario altaSumario)
			throws ApplicationBusinessException;

	public void verificarEscalaGlasglow(final Integer seqEscalaGlasglow,
			final Integer seqAtendimento) throws ApplicationBusinessException;

	public void verificarAtendimento(final Date dataHoraInicio,
			final Integer seqAtendimento, final Date dataRealizacao)
			throws ApplicationBusinessException;

	public VerificaAtendimentoVO verificarAtendimento(
			final Date dataHoraInicio, final Date dataHoraFim,
			final Integer seqAtendimento, final Integer seqHospitalDia,
			final Integer seqInternacao, final Integer seqAtendimentoUrgencia)
			throws ApplicationBusinessException;

	public String obterComplementoInformacaoComplementar(
			final MpmAltaSumarioId id) throws ApplicationBusinessException;

	public boolean verificaModoUsoProcedimentoEspecialPrescrito(Short pedSeq, Short seqp);
	
	public String obterDescricaoFormatadaMedicamentoSolucaoDispensacaoFarmacia(
			final MpmPrescricaoMdto medicamentoSolucao, String aprazamento,final Boolean inclusaoExclusao,
			final Boolean impressaoTotal, final Boolean isUpperCase) throws ApplicationBusinessException;

	/**
	 * 
	 * @param fichaApache
	 * @throws ApplicationBusinessException
	 */
	public void inserirFichaApache(final MpmFichaApache fichaApache)
			throws ApplicationBusinessException;

	public void verificaExigeObservacao(final Integer codigoMedicamento,
			final String observacao) throws ApplicationBusinessException;

	public void geraLaudoInternacao(final AghAtendimentos atendimento,
			final Date dthrUltimoEvento,
			final AghuParametrosEnum pLaudoAcompanhante,
			final FatConvenioSaudePlano convenioSaudePlano)
			throws BaseException;

	public void inserirAltaSumario(final MpmAltaSumario altaSum)
			throws ApplicationBusinessException;

	public void incluirPrescricaoCuidado(
			final MpmPrescricaoCuidado prescricaoCuidado, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException;

	public void inserirPrescricaoMedicamentoModeloBasico(
			final MpmPrescricaoMdto mpmPrescricaoMedicamento, String nomeMicrocomputador)
			throws BaseException;

	public Boolean isUnidadeBombaInfusao(
			final AghUnidadesFuncionais unidadeFuncional);



	public void inserirCargaPrescricaoProcedimentoEspecial(
			final MpmPrescricaoProcedimento procedimento,
			final MpmPrescricaoMedica prescricaoMedica, String nomeMicrocomputador) throws BaseException;

	public void removerAtendProf(final Integer atdSeq);

	public void removerSumariosPendentes(final Integer atdSeq);

	public void validarAprazamento(
			final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			final Short frequencia) throws ApplicationBusinessException;

	public RelSumarioAltaVO criaRelatorioSumarioAltaPorId(final MpmAltaSumarioId id);
	
	public RelatorioSumarioObitoVO criaRelatorioSumarioAlta(
			final MpmAltaSumarioId id, final String tipoImpressao)
			throws BaseException;

	public void excluirAprazamentoFrequencia(final MpmAprazamentoFrequenciaId id) throws ApplicationBusinessException;

	public Long countTipoFrequenciaAprazamento(
			final MpmTipoFrequenciaAprazamento entityFilter);

	public List<MpmTipoFrequenciaAprazamento> pesquisarTipoFrequenciaAprazamento(
			final MpmTipoFrequenciaAprazamento entityFilter,
			final Integer firstResult, final Integer maxResults,
			final String orderProperty, final boolean asc);

	public void validarExclusaoTipoFrequenciaAprazamento(
			final MpmTipoFrequenciaAprazamento entity,
			final boolean validaAprazamentos)
			throws ApplicationBusinessException;

	public void criarAprazamentoFrequencia(
			final MpmAprazamentoFrequencia detail,
			final MpmTipoFrequenciaAprazamento entity) throws ApplicationBusinessException;

	
	public Boolean verificarServidorMedico(final Integer serMatricula,
			final Short serCodigo) throws ApplicationBusinessException;

	public boolean verificaProcedimentosEspeciasPrescritos(
			final Short codigoProcedimento);

	public boolean existeProcedimentosComLaudoJustificativaParaImpressao(
			final AghAtendimentos atendimento) throws BaseException;

	public void excluirTipoFrequenciaAprazamento(final Short seq) throws ApplicationBusinessException;

	/**
	 * Este método verifica se o hospital tem ambulatório ou não.
	 * 
	 * @author gfmenezes
	 * 
	 * @return
	 * @throws BaseException
	 */
	public Boolean existeAmbulatorio() throws BaseException;

	public void inativarAltaRecomendacaoCadastrada(
			final MpmAltaSumario altaSumario,
			final MpmAltaRecomendacaoId altaRecomendacaoId)
			throws BaseException;

	public void salvarTipoFrequenciaAprazamento(
			final MpmTipoFrequenciaAprazamento entity,
			final List<MpmAprazamentoFrequencia> aprazamentos,
			final List<MpmAprazamentoFrequencia> excluidosList)
			throws ApplicationBusinessException;

	
	public String buscarResumoLocalPaciente(final AghAtendimentos atendimento)
			throws ApplicationBusinessException;

	public String buscarResumoLocalPacienteII(final AghAtendimentos atendimento)
			throws ApplicationBusinessException;

	public String buscarResumoLocalPaciente(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional)
			throws ApplicationBusinessException;
	
	public String buscarResumoLocalPaciente2(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional)
			throws ApplicationBusinessException;	

	public boolean verificaModoUsoEmModeloBasico(Short pedSeq, Short seqp);

	public boolean verificaProcedimentoEspecialEmModeloBasico(
			final MpmProcedEspecialDiversos procedimento);

	/**
	 * @param pCthSeq
	 * @return
	 */
	public Integer buscarCidAlta(final Integer pCthSeq);


	public List<MpmProcedEspecialDiversos> listarProcedimentosEspeciaisAtivosComProcedimentoIntAtivoPorPhiSeq(
			final Integer phiSeq);

	public List<MpmProcedEspecialDiversos> listarMpmProcedEspecialDiversos(
			final Object objPesquisa);

	public Long listarMpmProcedEspecialDiversosCount(final Object objPesquisa);

	
	public Object[] buscaConsProf(Integer matricula,Short vincodigo) throws ApplicationBusinessException;

	public List<MpmTipoLaudo> listarTiposLaudo(final Integer firstResult,
			final Integer maxResult, final String orderProperty,
			final boolean asc, final Short seq, final String descricao,
			final DominioSituacao situacao);



	public Long listarTiposLaudoCount(final Short seq,
			final String descricao, final DominioSituacao situacao);

	public List<MpmTextoPadraoLaudo> listarTextoPadraoLaudo(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer seq,
			final String descricao, final DominioSituacao situacao);

	public Long listarTextoPadraoLaudoCount(final Integer seq,
			final String descricao, final DominioSituacao situacao);

	public MpmTipoLaudo obterMpmTipoLaudoPorChavePrimaria(final Short seq);

	public MpmTextoPadraoLaudo obterMpmTextoPadraoLaudoPorChavePrimaria(
			final Integer seq);

	public void inserirMpmTipoLaudo(final MpmTipoLaudo tipoLaudo,
			final boolean flush) throws BaseException;

	
	public void atualizarMpmTipoLaudo(final MpmTipoLaudo tipoLaudo,
			final boolean flush) throws BaseException;

	public void inserirMpmTextoPadraoLaudo(
			final MpmTextoPadraoLaudo textoPadraoLaudo, final boolean flush)
			throws BaseException;

	public void atualizarMpmTextoPadraoLaudo(
			final MpmTextoPadraoLaudo textoPadraoLaudo, final boolean flush)
			throws BaseException;

	public Date atualizaInicioTratamento(final Date dataHoraInicio,
			final Date dataHoraInicioAdm)
			throws ApplicationBusinessException;

	/**
	 * Calcula o número de vezes que um aprazamento
	 * 
	 */
	public Long calculoNumeroVezesAprazamento24Horas(
			final MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			final Short frequencia);

	public MpmTipoLaudoConvenio obterMpmTipoLaudoConvenioPorChavePrimaria(
			final MpmTipoLaudoConvenioId id);

	public MpmTipoLaudoTextoPadrao obterMpmTipoLaudoTextoPadraoPorChavePrimaria(
			final MpmTipoLaudoTextoPadraoId id);

	public void inserirMpmTipoLaudoConvenio(
			final MpmTipoLaudoConvenio tipoLaudoConvenio, final boolean flush)
			throws BaseException;

	public void removerMpmTipoLaudoConvenio(
			final MpmTipoLaudoConvenio tipoLaudoConvenio, final boolean flush);

	public void inserirMpmTipoLaudoTextoPadrao(
			final MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao,
			final boolean flush) throws BaseException;

	public void removerMpmTipoLaudoTextoPadrao(
			final MpmTipoLaudoTextoPadrao tipoLaudoTextoPadrao,
			final boolean flush);

	public List<MpmTextoPadraoLaudo> pesquisarTextosPadrao(final String filtro);

	public Long pesquisarTextosPadraoCount(final String filtro);

	public void clear();

	public List<MpmPrescricaoMdto> pesquisarPrescricaoMdtoNovo(
			final Integer pmeAtdSeq, final Date pmeData,
			final Date pmeDthrInicio, final Date pmeDthrFim);

	public List<MpmPrescricaoMdto> pesquisarPrescricaoMdtoSitConfirmado(
			final Integer pmeAtdSeq, final Integer pmeSeq);

	public Long pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendenteCount(
			final MpmPrescricaoMedica prescricaoMedica,
			final AfaMedicamento medicamento);

	public MpmItemPrescricaoMdto obterMpmItemPrescricaoMdtoPorChavePrimaria(
			final MpmItemPrescricaoMdtoId chavePrimaria);

	public List<MpmItemPrescricaoMdto> obterListaItemPrescricaoParaMdto(
			final AfaMedicamento entidade);

	public MpmPrescricaoMedica obterMpmPrescricaoMedicaPorChavePrimaria(
			final MpmPrescricaoMedicaId mpmId);

	public VMpmPrescrMdtos obtemPrescMdto(final Integer imePmdAtdSeq,
			final Long vPmdSeq, final Integer imeMedMatCodigo,
			final Short imeSeqp);

	public VMpmMdtosDescr obterVMpmMdtosDescrPorMedicamento(
			final AfaMedicamento medicamento);

	public List<MpmItemPrescricaoMdto> pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendente(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final MpmPrescricaoMedica prescricaoMedica,
			final AfaMedicamento medicamento);

	public VMpmDosagem obterVMpmDosagem(final Integer matCodigo,
			final Integer seq);

	public MpmUnidadeMedidaMedica obterUnidadesMedidaMedicaPeloId(
			final Integer seq);

	List<MpmPrescricaoMdto> pesquisaMedicamentosPOL(final Integer atdSeq);
	List<MpmPrescricaoMdto> pesquisaMedicamentosPOL(final Integer atdSeq, DominioIndPendenteItemPrescricao dominio);
	
	public List<MpmPrescricaoMdtosHist> pesquisaMedicamentosHistPOL(final Integer atdSeq);

	
	public MpmAltaSumario pesquisarAltaSumarioConcluido(final Integer apaAtdSeq);

	
	public MpmCidAtendimento pesquisaCidAtendimentoPrincipal(
			final AghAtendimentos atendimento);

	
	public List<MpmSolicitacaoConsultoria> pesquisaSolicitacoesConsultoria(
			final Integer atdSeq);

	public Long pesquisaSolicitacoesConsultoriaCount(
			final Integer atdSeq);
	
	public List<MpmAltaImpDiagnostica> pesquisarAltaImpDiagnosticaPorAtendimento(
			final Integer atdSeq);

	public MpmSolicitacaoConsultoria obterSolicitacaoConsultoriaPorId(
			final Integer idAtendimento, final Integer seqSolicitacaoconsultoria);

	
	public String obterRespostasConsultoria(final Integer idAtendimento,
			final Integer seqConsultoria, final Integer ordem);

	/**
	 * Método utilizado para pesquisar as consultorias de uma prescrição médica
	 * 
	 * @param prescricaoMedica
	 * @return
	 */
	public List<MpmSolicitacaoConsultoria> pesquisarConsultoriasPorPrescricao(
			final MpmPrescricaoMedica prescricaoMedica);

	/**
	 * Método que verifica se já existe uma solicitação de consultoria para esta
	 * especialidade em uma mesma prescrição de um mesmo atendimento. TODO
	 * alterar nome de metodo para pesquisarSolicitacaoConsultoria.
	 * 
	 * @param especialidade
	 * @param idAtendimento
	 * @return
	 */
	public MpmSolicitacaoConsultoria verificarSolicitacaoConsultoriaAtivaPorEspecialidade(
			final Short idEspecialidade, final Integer idAtendimento,
			final Integer idPrescricao);

	/**
	 * método responsável por fazer a persistência
	 * 
	 * @param solicitacaoConsultoria
	 * @param idAtendimento
	 * @param idPrescricao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public MpmSolicitacaoConsultoria persistirSolicitacaoConsultoria(
			final MpmSolicitacaoConsultoria solicitacaoConsultoria,
			final Integer idAtendimento, final Integer idPrescricao)
			throws BaseException;

	/**
	 * 
	 * @param solicitacaoConsultoria
	 * @throws BaseException
	 */
	void atualizarVisualizacaoConsultoria(MpmSolicitacaoConsultoria solicitacaoConsultoria) throws BaseException;
	
	/**
	 * 
	 * @param informacaoPrescribente
	 * @throws BaseException
	 */
	void atualizarVisualizacaoInformacaoPrescribente(MpmInformacaoPrescribente informacaoPrescribente, RapServidores servidorLogado) throws BaseException;
	
	/**
	 * 
	 * @param atdSeq
	 * @throws BaseException
	 */
	void atualizarVisualizacaoParecer(Integer atdSeq, ParecerPendenteVO parecerPendenteVO) throws BaseException;
	
	public MpmTipoFrequenciaAprazamento obterTipoFrequenciaAprazamentoId(
			final Short tFASeq);

	
	public List<Date> listarDataIngressoUnidadeOrdenadoPorSeqpDesc(
			final Integer atdSeq);

	public List<MpmAltaSumario> listarAltasSumarioPorAtendimento(
			final Integer atdSeq);

	public MpmAltaSumario obterAltaSumarioPeloId(final Integer apaAtdSeq,
			final Integer apaSeq, final Short seqp);

	public List<Object> executaCursorAltaSumarioAtendimentoEnforceRN(
			final Integer atdSeq);

	
	public Date pesquisarDataAltaInternacao(final Integer internacaoSeq);

	
	public DadosAltaSumarioVO buscarDadosAltaSumario(final Integer internacaoSeq);

	
	public Date obterDataAltaPorInternacao(final Integer intSeq);

	
	public Date obterDataAltaInternacaoComMotivoAlta(final Integer intSeq);

	/**
	 * Efetua a busca de horários de início de aprazamento
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param unfSeq
	 * @param situacao
	 * @return Lista de horários de início de aprazamento
	 */
	public List<MpmHorarioInicAprazamento> pesquisarHorariosInicioAprazamentos(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Short unfSeq,
			final DominioSituacao situacao);

	/**
	 * Efetua o count para a busca de horários de início de aprazamento
	 * @param unfSeq
	 * @param situacao
	 * @return Count
	 */
	public Long pesquisarHorariosInicioAprazamentosCount(final Short unfSeq,
			final DominioSituacao situacao);

	/**
	 * Obtém unidades funcionais relativas à aprazamento por código ou descricao e situação
	 * @param paramPesquisa Código ou descrição
	 * @return Lista de Unidades Funcionais
	 */
	public List<AghUnidadesFuncionaisVO> pesquisarUnidadesFuncionaisAprazamentoPorCodigoDescricao(
			final Object paramPesquisa);

	/**
	 * Pesquisa tipos de aprazamento ativos e de frequência por código ou descrição
	 * @param objPesquisa Códuigo/ Descrição
	 * @return Lista de tipos de aprazamento
	 */
	public List<MpmTipoFrequenciaAprazamento> pesquisarTipoAprazamentoAtivoFrequenciaPorCodigoDescricao(
			final Object objPesquisa);

	/**
	 * Inclui ou atualiza o o horário de início de aprazamento
	 * @param horarioAprazamento
	 * @throws BaseListException
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 */
	public void persistirHorarioInicioAprazamento(
			final MpmHorarioInicAprazamento horarioAprazamento)
			throws BaseListException, ApplicationBusinessException, BaseException;

	/**
	 * Remove um horário de início de aprazamento de acordo com o id informado.
	 * @param horarioAprazamentoId
	 */
	public void removerHorarioAprazamento(
			final MpmHorarioInicAprazamentoId horarioAprazamentoId);

	/**
	 * ORADB Forms AINP_VERIFICA_PRESCRICAO (CURSOR C_PRESC) obtém a prescrição
	 * médica
	 * 
	 * @param atdSeq
	 * @return
	 */
	public MpmPrescricaoMedica obterPrescricaoMedica(final Integer atdSeq);

	public void removerMpmFichaApache(final MpmFichaApache mpmFichaApache,
			final boolean flush);

	public void removerMpmMotivoIngressoCti(
			final MpmMotivoIngressoCti motivoIngressoCti, final boolean flush);

	public void removerMpmLaudo(final MpmLaudo mpmLaudo, final boolean flush) throws ApplicationBusinessException;
	
	public void removerMamPcIntItemParada(final MamPcIntItemParada itemParada);
	
	public void removerMamPcIntParada(final MamPcIntParada pcIntParada);
	
	public void removerMpmPim2(final MpmPim2 pim2) throws ApplicationBusinessException;

	/**
	 * Método que obtém as fichas apache de um atendimento
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmFichaApache> pesquisarFichasApachePorAtendimento(
			final Integer atdSeq);

	/**
	 * Método que obtém os laudos não impressos de um atendimento
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmLaudo> pesquisarLaudosNaoImpressosPorAtendimento(
			final Integer atdSeq);

	/**
	 * Método que obtém os laudos impressos de um atendimento
	 * 
	 * @param atdSeq
	 * @return
	 */
	public List<MpmLaudo> pesquisarLaudosImpressosPorAtendimento(
			final Integer atdSeq);

	public void removerMpmAltaSumario(final MpmAltaSumario mpmAltaSumario,
			final boolean flush);

	public void verificarStatusCid(final AghCid cid)
			throws ApplicationBusinessException;

	public void validaServidorLogado() throws ApplicationBusinessException;

	public Long pesquisarAlterarDispensacaoDeMedicamentosCount(
			final AinLeitos leito, final Integer numeroPrescricao,
			final Date dthrDataInicioValidade, final Date dthrDataFimValidade,
			final Integer numeroProntuario, final AipPacientes paciente);

	public List<MpmPrescricaoMedica> pesquisarAlterarDispensacaoDeMedicamentos(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final Boolean asc,
			final AinLeitos leito, final Integer numeroPrescricao,
			final Date dthrDataInicioValidade, final Date dthrDataFimValidade,
			final Integer numeroProntuario, final AipPacientes paciente);

	public List<MpmPrescricaoMedica> pesquisarPrescricaoMedicaPorAtendimentoEDataFimAteDataAtual(
			final Integer atdSeq, final Date dataFim);

	public PrescricaoMedicaVO buscarDadosCabecalhoContraCheque(
			final MpmPrescricaoMedica prescricao, final Boolean listaVaziaItens)
			throws BaseException;

	public Date obterDataInternacao2(final Integer seqAtendimento)
			throws ApplicationBusinessException;

	public String obterLocalPaciente(final Integer apaAtdSeq)
			throws ApplicationBusinessException;

	public void inserirPim2(final MpmPim2 pim2);

	public void atualizarPim2(final MpmPim2 pim2, final MpmPim2 pim2Old)
			throws ApplicationBusinessException;

	public MpmPim2 clonarPim2(final MpmPim2 pim2) throws ApplicationBusinessException;
	
	/**
	 * @author lucasbuzzo
	 * #3476
	 * 
	 * @param Integer firstResult
	 * @param Integer maxResult
	 * @param String orderProperty
	 * @param boolean asc
	 * @param AghUnidadesFuncionais aghUnidadesFuncionais
	 * @return List<MpmCidUnidFuncional>
	 */
	public List<MpmCidUnidFuncional> listaCidUnidadeFuncional(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais aghUnidadesFuncionais);

	/**
	 * @author lucasbuzzo
	 * #3476
	 * 
	 * @param Integer firstResult
	 * @param Integer maxResult
	 * @param String orderProperty
	 * @param boolean asc
	 * @param AghUnidadesFuncionais aghUnidadesFuncionais
	 * @return Integer
	 */
	public Long listaCidUnidadeFuncionalCount(
			AghUnidadesFuncionais aghUnidadesFuncionais);

	/**
	 * @author lucasbuzzo
	 * #3476
	 *	
	 * @param 
	 * @return AghCidDAO
	 * @throws ApplicationBusinessException 
	 */
	public void persistirCidUnidadeFuncional(
			MpmCidUnidFuncional mpmCidUnidFuncional)
			throws ApplicationBusinessException;

	/**
	 * @author lucasbuzzo
	 * #3476
	 *	
	 * @param MpmCidUnidFuncionalId id
	 * @return MpmCidUnidFuncional
	 */
	public MpmCidUnidFuncional obterMpmCidUnidFuncionalPorId(
			MpmCidUnidFuncionalId id);

	/**
	 * @author lucasbuzzo
	 * #3476
	 *	
	 * @param MpmCidUnidFuncionalId id
	 * @return MpmCidUnidFuncional
	 */
	public void excluirMpmCidAtendimento(
			MpmCidUnidFuncionalId id);

	/**
	 * @author heliz
	 * #15822
	 * Métodos implementados para a #15822 - Visualizar sumário de transferência do paciente
	 * na internação
	 * @param object 
	 */
	//-------------------------------------------------------------------------------------------------------
	public Boolean verificarSumarioTransfPacInternacao(Integer atdSeq,
			Short seqpAltaSumario);

	public List<MpmAltaSumario> pesquisarAltaSumarioTransferencia(
			AghAtendimentos atendimento, Short seqpAltaSumario);

	public List<LinhaReportVO> obterOutrasEquipes(Integer apaAtdSeq,
			Integer apaSeq, Short seqp);

	public MpmTrfDestino obterEquipeDestino(Integer apaAtdSeq, Integer apaSeq,
			Short seqp);

	public String obterMotivoTransferencia(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) throws ApplicationBusinessException;

	public List<MpmTrfDiagnostico> obterTransferenciaDiagPrincipais(
			Integer apaAtdSeq, Integer apaSeq, Short seqp)
			throws ApplicationBusinessException;

	public String obterTransferenciaDiagSecundario(Integer apaAtdSeq,
			Integer apaSeq, Short seqp) throws ApplicationBusinessException;

	public List<MpmAltaOtrProcedimento> obterMpmAltaOtrProcedimento(
			Integer apaAtdSeq, Integer apaSeq, Short seqp)
			throws ApplicationBusinessException;

	public List<LinhaReportVO> obterAltaCirgRealizadas(Integer apaAtdSeq,
			Integer apaSeq, Short seqp);

	public List<LinhaReportVO> obterAltaConsultorias(Integer apaAtdSeq,
			Integer apaSeq, Short seqp);

	public List<LinhaReportVO> obterPrincFarmacos(Integer apaAtdSeq,
			Integer apaSeq, Short seqp);

	public List<LinhaReportVO> obterComplFarmacos(Integer apaAtdSeq,
			Integer apaSeq, Short seqp);

	public String obterEvolucao(Integer apaAtdSeq, Integer apaSeq, Short seqp);

	public MpmAltaMotivo obterMpmAltaMotivo(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) throws ApplicationBusinessException;

	public MpmAltaPlano obterMpmAltaPlano(Integer apaAtdSeq, Integer apaSeq,
			Short seqp) throws ApplicationBusinessException;

	public List<MpmAltaRecomendacao> obterMpmAltaRecomendacoes(
			Integer apaAtdSeq, Integer apaSeq, Short seqp,
			DominioSituacao situacao) throws ApplicationBusinessException;

	//---------------------------------------------------------------------------------------------------

	public boolean existeAltaSumarioConcluidaPorAtendimento(Integer atdSeq);

	public List<AfaMedicamentoPrescricaoVO> obterListaAltaPrincFarmacoPorIndCarga(
        	Integer apaAtdSeq, Integer apaSeq, Short seqp, Boolean indCarga);

	public List<MpmDataItemSumario> listarDataItemSumario(Integer apaAtdSeq);

	public List<RelSumarioPrescricaoVO> pesquisaGrupoDescricaoStatus(
			SumarioPrescricaoMedicaVO prescricao, boolean limitValor);

	public List<QuantidadePrescricoesDispensacaoVO> pesquisarRelatorioQuantidadePrescricoesDispensacao(
			Date dataEmissaoInicio, Date dataEmissaoFim);

	public List<Date> executarCursorPrCr(Integer atdSeq);

	public Boolean executarCursorPrescricao(Integer atdSeq);

	public List<MpmPrescricaoMedica> listarPrescricoesMedicasParaGerarSumarioDePrescricao(
			Integer seqAtendimento, Date dthrInicioPrescricao,
			Date dthrFimPrescricao);

	public void atualizarMpmPrescricaoMedicaDepreciado(
			MpmPrescricaoMedica mpmPrescricaoMedica);

	public List<MpmSolicitacaoConsultoria> pesquisarSolicitacaoConsultoriaPorInternacaoOutrasEspecialidades(
			Integer intSeq);

	List<MpmAltaSumario> pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(Integer atdSeq);
	
	Map<Integer, List<MpmAltaSumario>> pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(List<Integer> atdSeqs);

	public DominioSimNao verificarAltaSumarioObito(AghAtendimentos atendimento);

	public List<MpmAltaSumario> listarAltasSumario(Integer atdSeq,
			DominioSituacao situacao,
			DominioIndTipoAltaSumarios[] tiposAltaSumario);

	public Long buscaAltaSumarioTransferenciaCount(Integer apaAtdSeq);

	public List<Object> executarCursorAltaSumario(Integer atdSeq);

	public List<Object> executarCursorSumario(Integer atdSeq);

	public MpmAltaSumario obterAltaSumarioPorAtendimento(
			AghAtendimentos atendimento);

	public List<MpmAltaSumario> listarAltasSumarioPorCodigoPaciente(
			Integer pacCodigo);

	public List<MpmAltaSumario> listarAltasSumarios(Integer pacCodigo,
			AghAtendimentos atendimento);

	public List<MpmAltaSumario> obterTrfDestinoComAltaSumarioEPaciente(
			Integer apaAtdSeq);

	public void persistirMpmAltaSumario(MpmAltaSumario mpmAltaSumario);

	public List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamentoDigitaFrequencia(
			Boolean listarApenasAprazamentoSemFrequencia, Object parametro);
		
	public List<MpmAprazamentoFrequencia> listarAprazamentosFrequenciaPorTipo(
			MpmTipoFrequenciaAprazamento tipo);

	public Date obterHoraInicialPorTipoFrequenciaAprazamento(
			AghUnidadesFuncionais unidade,
			MpmTipoFrequenciaAprazamento tipoFrequencia, Short frequencia);

	public List<MpmAltaPedidoExame> pesquisarMpmAltaPedidoExamePorZonaESala(
			Short unfSeq, Byte sala);
	
	public abstract MpmUnidadeTempo obterMpmUnidadeTempoPorId(Integer seq);

	public abstract void persistirMpmUnidadeTempo(MpmUnidadeTempo unidadeTempo)
			throws ApplicationBusinessException, BaseException;

	public List<Integer> listarSeqsCidAtendimentoPorCodigoAtendimento(
			Integer newAtdSeq);

	public Short obterProximoSeqPMpmFichaApache(Integer seqAtendimento);

	public List<MpmListaServEquipe> pesquisarListaServidorEquipePorServidor(
			Integer matricula, Short vinCodigo, Date dataFim);
	
	public List<MpmListaServEspecialidade> pesquisarListaServidorEspecialidadePorEspecialiade(
			Short seqEspecialidade, Date dataFim);

	public void removerListaServidorSumarioAltaPorAtendimento(
			Integer seqAtendimento);

	public void inserirListaServidorSumarioAlta(MpmListaServSumrAltaId id);

	public MpmListaServSumrAlta obterMpmListaServSumrAltaPorChavePrimaria(
			MpmListaServSumrAltaId id);

	public List<MpmPacAtendProfissional> pesquisarPacienteAtendimentoProfissional(
			Integer seqAtendimento, Date dataFim);

	public List<MpmParamCalculoPrescricao> pesquisarMpmParamCalculoPrescricoes(
			Integer pesoPacCodigo, Integer alturaPacCodigo);

	public List<MpmPim2> pesquisarPim2PorAtendimentoSituacao(
			Integer seqAtendimento, DominioSituacaoPim2 situacao);

	public List<MpmServidorUnidFuncional> pesquisarServidorUnidadeFuncional(
			Short seqUnidadeFuncional, Date dataFim);

	public List<MpmPostoSaude> listarPostosSaudePorNumeroLogradouroParesEImpares(
			Integer atuSeq, Integer nroLogradouro);

	public MpmSumarioAlta obterMpmSumarioAltaPorChavePrimaria(Integer seq);

	public void inserirMpmFichaApacheJn(MpmFichaApacheJn mpmFichaApacheJn);

	public abstract void excluirUnidadeTempo(Short seqMpmUnidadeTempo)
			throws ApplicationBusinessException;
	
	/**
	 * Método de pesquisa para a página de listagem. Pesquisa por código e/ou
	 * descrição e/ou situação
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param mpmUnidadeTempo
	 * @return
	 */
	public abstract List<MpmUnidadeTempo> listaUnidadeTempo(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmUnidadeTempo mpmUnidadeTempo);

	public abstract Long pesquisarUnidadeTempoCount(
			MpmUnidadeTempo mpmUnidadeTempo);
	
	public abstract List<MpmUnidadeMedidaMedica> getListaUnidadeMedidaMedica();
	
	public LocalDispensaVO obterLocalDispensaVO(Integer medMatCodigo, Integer atendimentoSeq);
	
	public List<MpmItemPrescricaoMdto> listarItensPrescricaoMedicamentoFarmaciaMe(Integer atendimentoSeq, Date dthrInicio, Date dthrFim);
	
	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritosConfirmadosPelaChavePrescricao(MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica, Boolean isSolucao);
	
	public List<LocalDispensa2VO> listarPrescricaoItemMedicamentoFarmaciaMov(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento);

	public List<LocalDispensa2VO> listarPrescricaoMedicamentoFarmaciaMov(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento);
	
	public List<MpmPrescricaoMdto> obterListaMedicamentosPrescritos(MpmPrescricaoMedicaId prescricao, Date dtHrFimPrescricaoMedica, Boolean isSolucao);

	
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			RapServidores servidorValida) throws ApplicationBusinessException, BaseException;
	
	/**
	 * Busca laudo no período de validade pelo seq do atendimento
	 * 
	 * @param atdSeq
	 * @param pDtDesdobr
	 * @return
	 */
	public abstract List<MpmLaudo> listarLaudosPorAtendimentoData(
			final Integer atdSeq, final Date pDtDesdobr);
	
	/**
	 * Método que pesquisa Motivos de ingresso CTI por um atendimento
	 * @param atdSeq
	 * @return
	 */
	public List<MpmMotivoIngressoCti> pesquisarMotivoIngressoCtisPorAtendimento(AghAtendimentos atendimento);
	
	List<MpmMotivoIngressoCti> pesquisarMotivoIngressoCtiPorAtdSeq(Integer atdSeq);
	
	/**
	 * Desatacha o objeto motivoReinternacao da session
	 * 
	 * @param motivoReinternacao
	 */
	public void desatachar(MpmMotivoReinternacao motivoReinternacao);
	public void desatachar(MpmPrescricaoMdto mpmPrescricaoMdto);
	
	public MpmMotivoReinternacao atualizarMpmMotivoReinternacao(MpmMotivoReinternacao motivoReinternacao);
	
	public MpmMotivoReinternacao inserirMpmMotivoReinternacao(MpmMotivoReinternacao motivoReinternacao);
	
	public void removerMpmMotivoReinternacao(MpmMotivoReinternacao motivoReinternacao);
	
	/**
	 * Busca na base uma lista de MpmMotivoReinternacao pelo filtro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param motivoReinternacao
	 * @return
	 */
	public List<MpmMotivoReinternacao> pesquisarMpmMotivoReinternacao(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			MpmMotivoReinternacao motivoReinternacao);
	
	/**
	 * Busca na base o número de elementos da lista de MpmMotivoReinternacao
	 * pelo filtro
	 * 
	 * @param MotivoReinternacao
	 * @return
	 */
	public Long pesquisarMpmMotivoReinternacaoCount(MpmMotivoReinternacao motivoReinternacao);
	
	public String obterNomePosto(Integer codigoAreaAtuacao, Integer numeroLogradouro, Integer opcao);
	
	public List<MpmPrescricaoNpt> obterItensPrescricaoNpt(Integer atdSeq, Integer prescricaoMedicaSeq, Date dataInicio, Date dataFim);
	
	public  List<ItemAlteracaoNptVO> pesquisarAlteracoesNpt(Integer atdSeq, Integer pmeSeq, Date pmeDthrIniMvto);
	
	/**
	 * obtém a Nutrição Parental Total pelo id.
	 * 
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoNpt obterNutricaoParentalTotalPeloId(Integer atdSeq, Integer seq);
	
	public List<MpmPrescricaoProcedimento> obterItensPrescricaoProcedimento(
			Integer atdSeq, Integer prescricaoMedicaSeq, Date dataInicio,
			Date dataFim);
	
	public  List<ItemAlteracaoNptVO> pesquisarProcedimentosEspeciaisDiversos(Integer atdSeq, Integer pmeSeq, Date pmeDthrIniMvto);

	/**
	 * Busca uma PrescricaoProcedimento, fazendo junção com ProcedEspecialDiversos, ProcedimentoCirurgicos e Materiais, filtrando por ID.
	 * @param seq
	 * @param atdSeq
	 * @return
	 */
	public MpmPrescricaoProcedimento obterPrescricaoProcedimentoPorId(Long seq, Integer atdSeq);
	
	
	public MpmPrescricaoNpt obterPrescricaoNptPorChavePrimaria(MpmPrescricaoNptId id);
	
	public void desatacharMpmProcedEspecialDiversos(MpmProcedEspecialDiversos mpmProcedEspecialDiversos);
	
	public List<MpmSolicitacaoConsultoria> obterConsultoriaAtiva(Integer atdSeq);

	public List<MpmSolicitacaoConsultHist> obterHistoricoConsultoria(Integer atdSeq);

	MpmParamCalculoPrescricao obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(Integer atdSeq);
	
	void inicializarCadastroPesoAltura(Integer atdSeq, DadosPesoAlturaVO vo);

	BigDecimal calculaSC(Boolean pacientePediatrico, BigDecimal peso,
			BigDecimal altura);

	void atualizarDadosPesoAltura(Integer pacCodigo, Integer atdSeq,
			BigDecimal peso, DominioTipoMedicaoPeso tipoMedicaoPeso,
			BigDecimal altura, DominioTipoMedicaoPeso tipoMedicaoAltura,
			BigDecimal sc, BigDecimal scCalculada, DadosPesoAlturaVO vo)
			throws ApplicationBusinessException;
	
	List<MpmUnidadeMedidaMedica> getListaUnidadeMedidaMedicaAtivasPeloMedFmDosagPeloCodigoOuDescricao(Integer matCodigo);

	Boolean possuiDadosPesoAlturaDia(Integer atdSeq);

	Object[] calculoDose(Short frequencia, Short tfqSeq,
			BigDecimal qtdParamCalculo,
			DominioUnidadeBaseParametroCalculo baseParamCalculo,
			DominioTipoCalculoDose tipoCalculoDose, Integer duracao,
			DominioDuracaoCalculo unidadeTempo, BigDecimal peso,
			BigDecimal altura, BigDecimal sc);
	
	
	public List<MamPcIntItemParada> pesquisarItemParadaPorAtendimento(AghAtendimentos atendimento);
	
	public List<MpmPim2> pesquisarPim2PorAtendimento(Integer seqAtendimento, DominioSituacaoPim2 situacao);
	
	public List<MamPcIntParada> pesquisarPcIntParadaPorAtendimento(AghAtendimentos atendimento);
	
	List<MamPcIntParada> pesquisarParadaInternacaoPorAtendimento(Integer atendimento);

	public MpmTipoFrequenciaAprazamento obterTipoFrequeciaAprazPorChavePrimaria(Short icsTfqSeq);

	List<MamPcIntItemParada> pesquisarItemParadaPorAtendimento(Integer atdSeq,
			Date dthrCriacao, Float ordem, Boolean isOrdemMaiorIgual);

	MamPcSumExameTab obterSumarioExameTabela(Integer atdSeq, Date dthrCriacao);

	List<MamPcSumExameMasc> pesquisarSumarioExamesMasc(Integer atdSeq,
			Date dthrCriacao, Integer pacCodigo, Boolean recemNascido);

	List<MamPcSumExameTab> pesquisarSumarioExameTabela(Integer atdSeq,
			Date dthrCriacao, Integer pacCodigo, Boolean recemNascido);

	List<MamPcSumMascLinha> pesquisarSumarioMascLinha(Integer atdSeq,
			Date dthrCriacao, Integer ordemRelatorio);

	MamPcSumObservacao obterSumarioObservacao(Integer atdSeq, Date dthrCriacao,
			Integer pacCodigo, DominioSumarioExame pertenceSumario,
			Date dthrEvento, Boolean recemNascido);

	MamPcSumMascCampoEdit obterSumarioMascCampoEdit(Integer atdSeq,
			Date dthrCriacao, Integer nroLinha, Integer ordemRelatorio);

	List<MamPcControlePac> pesquisarControlePaciente(Integer atdSeq, Date dthrCriacao);

	public Long obterNomeExamesCount(Object objPesquisa);

	BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			Integer matricula, Short vinculo, Boolean testaDataFimVinculo)
			throws ApplicationBusinessException;
	
	List<MpmPim2> pesquisarPim2PorAtendimentoSituacao(Integer seqAtendimento,
			List<DominioSituacaoPim2> situacao);

	/**
	 * Retorna sumário de alta e obito para o atendimento fornecido.
	 * @param atdSeq
	 * @return
	 */
	public List<AltaObitoSumarioVO> pesquisarAltaObitoSumariosAtdSeqSituacaoIndConcluido(Integer atdSeq,
			DominioSituacao dominioSituacao, DominioIndConcluido dominioIndConcluido);
	
	List<CidAtendimentoVO> pesquisarCidAtendimentoEmAndamentoOrigemInternacaoPorPacCodigo(Integer pacCodigo);
	
	MpmPim2 obterMpmPim2PorChavePrimaria(Long seqPim2);

	List<MpmListaServResponsavel> pesquisarServidorResponsavel(
			RapServidores servidorResp, Date dataFim);

	List<MpmSolicitacaoConsultoriaVO> pesquisaSolicitacoesConsultoriaVO(
			Integer atdSeq);

	/**
	 * Busca todos as altas secundarios relacionado a um atendimento.
	 * 
	 * @author rmalvezzi
	 * @param atendimento	Atendimento a qual a alta está relacionada
	 * @return				Lista de {@link MpmAltaDiagSecundario} que satisfazem os filtros
	 */
	List<MpmAltaDiagSecundario> buscaAltasSecundariosPorAtendimento(AghAtendimentos atendimento);

	/**
	 * Busca a alta principal de um atendimento.
	 * 
	 * @author rmalvezzi
	 * @param atendimento	Atendimento a qual a alta está relacionada
	 * @return				{@link MpmAltaDiagPrincipal} que satisfaz os filtros
	 */
	MpmAltaDiagPrincipal buscaAltaPrincipalPorAtendimento(AghAtendimentos atendimento);
	
	List<MpmAltaTriagem> pesquisarAltaTriagemPeloMpmAltaSumarioId(
			MpmAltaSumarioId altaSumarioId);

	List<MpmAltaTrgSinalVital> pesquisarAltaTrgSinalVitalPorMpmAltaSumarioIdEAltaTriagemSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp);

	List<MpmAltaTrgMedicacao> pesquisarAltaTrgMedicacaoPorMpmAltaSumarioIdEAltaTriagemSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp);
	
	List<MpmAltaTrgExame> pesquisarAltaTrgExamePorMpmAltaSumarioIdEAltaTriagemSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp);

	List<MpmAltaTrgAlergia> pesquisarAltaTrgAlergiaPorMpmAltaSumarioIdEAltaTriagemSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer altaTriagemSeqp);

	List<MpmAltaAtendimento> pesquisarAltaAtendimentoPeloMpmAltaSumarioId(
			MpmAltaSumarioId altaSumarioId);

	public List<MamItemReceituario> listarItemReceituarioPorAltaSumario(
			Integer atdSeq, Integer apaSeq, Short seqp);

	List<MpmAltaAtendMotivo> pesquisarAltaAtendMotivoPorMpmAltaSumarioIdEAltaAtendSeqp(
			MpmAltaSumarioId altaSumarioId, Integer altaAtendSeqp);

	List<MpmAltaAtendRegistro> pesquisarAltaAtendRegistroPorMpmAltaSumarioIdEAltaAtendSeqp(
			MpmAltaSumarioId altaSumarioId, Integer altaAtendseqp);

	MpmAltaPlano obterMpmAltaPlanoObterPorChavePrimaria(MpmAltaSumarioId id);

	MpmAltaEstadoPaciente obterMpmAltaEstadoPacientePorChavePrimaria(
			MpmAltaSumarioId id);

	List<MpmAltaImpDiagnostica> listarAltaImpDiagnosticaPorIdSemSeqp(
			Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp);

	List<MpmAltaPrincExame> listarMpmAltaPrincExamePorIdSemSeqpIndImprime(
			Integer apaAtdSeq, Integer apaSeq, Short seqp);

	List<MpmAltaPrincFarmaco> obterMpmAltaPrincFarmaco(
			Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, boolean apenasAtivos) throws ApplicationBusinessException;

	public Long pesquisarAltaTriagemPeloMpmAltaSumarioIdCount(
			MpmAltaSumarioId idAltaSumario);
	
	List<MpmAltaConsultoria> obterListaAltaConsultoriaPeloAltaSumarioId(
			MpmAltaSumarioId altaSumarioId);

	List<MpmAltaRespostaConsultoria> pesquisarAltaRespostaConsultoriaPorMpmAltaSumarioIdEAltaConsultoriaSeqp(
			Integer apaAtdSeq, Integer apaSeq, Short seqp, Short altaConsultoriaSeqp);

	MpmAltaEvolucao obterMpmAltaEvolucao(
			Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp);
	MpmFichaApache pesquisarFichaApacheComEscalaGrasgows(Integer atdSeq, Short seqp);
	
	Integer calcularPontuacaoFichaApache(MpmFichaApache fichaApche);
	
	List<MpmProcedEspecialRm> listarProcedimentosRmAtivosPeloPedSeq(Short pedSeq);

	public MpmTipoFrequenciaAprazamento getMpmTipoFrequenciaAprazamentoPorId(Short seq);
	
	public List<MpmPrescricaoMedicaVO> listarPrescricaoMedicaSituacaoDispensacao(
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc,
			final String leitoId, final Integer prontuario, final String nome,
			final Date dtHrInicio, final Date dtHrFim,
			final String seqPrescricao, Integer seqPrescricaoInt, Boolean indPacAtendimento, Boolean indPmNaoEletronica) throws ApplicationBusinessException;

	public Long listarPrescricaoMedicaSituacaoDispensacaoCount(
			final String leitoId, final Integer prontuario, final String nome,
			final Date dtHrInicio, final Date dtHrFim,
			final String seqPrescricao, Integer seqPrescricaoInt, Boolean indPacAtendimento, Boolean indPmNaoEletronica) throws ApplicationBusinessException;
	
	Long pesquisarAssociacoesPorServidorCount(final  RapServidores servidor);
	
	List<MpmProcedEspecialRm> listarProcedimentosRmPeloPedSeq(Short pedSeq);
	
	/**
	* Busca uma lista de VOs de Nutricao Parenteral com prescricoes realizadas para o atendimento informado dentro
	* das datas do processamento informado. Utilizado no modulo de custos.
	* 
	* @param atendimento
	* @param processamento
	* @return Lista de VOs de Nutricao Parenteral
	* @author rhrosa
	*/
	List<NutricaoParenteralVO> buscarNutricoesParenteraisPrescritas(Integer ateSeq, Date dataInicioProcessamento, Date dataFimProcessamento);

	MpmPrescricaoMedica obterPrescricaoComFatConvenioSaude(
			Integer atdSeqPrescricao, Integer seqPrescricao);
	
	MpmModeloBasicoProcedimento obterModeloBasicoProcedimentoPorChavePrimaria(MpmModeloBasicoProcedimentoId id, boolean left, Enum<?> ...fields);

	Object[] buscaConsProf(RapServidores rapServidor)
			throws ApplicationBusinessException;

	MpmMotivoAltaMedica obterMpmMotivoAltaMedica(Short seq);
	
	List<MpmPostoSaude> listarMpmPostoSaudePorSeqDescricao(final Object parametro);
	
	Long listarMpmPostoSaudePorSeqDescricaoCount(final Object parametro);
	
	MpmPostoSaude obterPostoSaudePorChavePrimaria(Integer seq);


	/**
	 * #34382 - Busca prescrição médica de um atendimento
	 * @param atdSeq
	 * @return
	 */
	List<MpmPrescricaoMedica> obterPrescricaoTecnicaPorAtendimentoOrderCriadoEm(
			Integer atdSeq);
	
	List<MpmSolicitacaoConsultoria> pesquisarConsultoriaEnfermagemResposta(Integer atdSeq);
	
	Boolean verificarExisteConsultoriaEnfermagemResposta(Integer atdSeq);	
	
	List<MpmSolicitacaoConsultoria> pesquisarConsultoriaEnfermagemSolicitada(Integer atdSeq);
	
	Boolean verificarExisteConsultoriaEnfermagemSolicitada(Integer atdSeq);	
	
	List<MpmSolicitacaoConsultoria> pesquisarSolicitacaoConsultoriaAtivaEnfermagem(Integer atdSeq);
	
	Boolean verificarExisteSolicitacaoConsultoriaAtivaEnfermagem(Integer atdSeq);
	
	/**
	 * #38994 - Serviço que retorna altas por numero da consulta
	 * @param conNumero
	 * @return
	 */
	MpmAltaSumario pesquisarAltaSumariosPorNumeroConsulta(Integer conNumero);
	
	/**
	 * #39002 - Busca Ultima Prescricao Medica
	 * @param atdSeq
	 * @return
	 */
	MpmPrescricaoMedica obterPrescricaoMedicaPorAtendimento(Integer atdSeq);
	
	/**
	 * #39007 - Serviço que retorna alta sumario por atendimento
	 * @param atdSeq
	 * @return
	 */
	MpmAltaSumario obterMpmAltaSumarioPorAtendimento(Integer atdSeq);
	
	/**
	 * #39010 - Busca alta de sumário concluído
	 * @param atdSeq
	 * @return
	 */
	MpmAltaSumario obterMpmAltaSumarioConcluidoPorAtendimento(Integer atdSeq);
	
	/**
	 * #39012 - Serviço para atualizar Sumario Alta apagando dados alta
	 * @param atdSeq
	 * @param nomeMicromputador
	 * @throws ApplicationBusinessException 
	 * @throws Exception
	 */
	void atualizarSumarioAltaApagarDadosAlta(Integer atdSeq, String nomeMicromputador) throws ApplicationBusinessException;
	
	/**
	 * #39013 - Serviço que estorna alta sumario
	 * @param seqp
	 * @param atdSeq
	 * @param apaSeq
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	void atualizarAltaSumarioEstorno(Short seqp, Integer atdSeq, Integer apaSeq, String nomeMicrocomputador) throws ApplicationBusinessException;
	
	/**
	 * #39018  #39019 #39020 #39021 #39022 #39023 #39014 #39015 #39016
	 * Serviço que desbloqueia sumario alta
	 * @param atdSeq
	 * @param apaSeq
	 * @param seqp
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	void desbloquearSumarioAlta(Integer atdSeq, Integer apaSeq, Short seqp, String nomeMicrocomputador) throws ApplicationBusinessException;

	/**
	 * Lista conjunto de mensagens a serem exibidas na popup Central de Mensagens.
	 * 
	 * @param atdSeq - Código do Atendimento
	 * @return Lista de mensagens
	 */
	public List<CentralMensagemVO> listarMensagensPendentes(Integer atdSeq);

	/**
	 * Obtém Dados do Paciente selecionado para ser exibido na Central de Mensagens.
	 * 
	 * @param atdSeq - Código do Atendimento
	 * @param prescricaoSeq - Código da Prescrição Médica
	 * 
	 * @return Dados do Paciente
	 */
	public MpmPrescricaoMedica obterPacienteCentralMensagens(Integer atdSeq, Integer prescricaoSeq);

	public List<MpmPrescricaoMedica> pesquisaPrescricoesMedicasPorAtendimento(Integer seqAtendimento);
	
	public List<MpmPrescricaoMedica> pesquisarPrescricoesMedicaNaoPendentes(Integer atdSeq, DominioSituacaoPrescricao situacaoPrescricao);
	public List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamento(String parametro);
	public Long obterListaTipoFrequenciaAprazamentoCount(String parametro);
	public Long obterListaTipoFrequenciaAprazamentoDigitaFrequenciaCount(Boolean listarApenasAprazamentoSemFrequencia, Object parametro);
	public MpmSolicitacaoConsultoria obterMpmSolicitacaoConsultoriaPorIdComPaciente(Integer atdSeq, Integer seq);
	public List<MpmTipoRespostaConsultoria> pesquisarTiposRespostasConsultoria(DominioIndConcluidaSolicitacaoConsultoria indConcluida);
	public Long listarVMpmOtrProcedSumCount(final String objPesquisa);
	VisualizaDadosSolicitacaoConsultoriaVO obterDadosSolicitacaoConsultoria(final Integer atdSeq, final Integer seq) throws ApplicationBusinessException;

	void inserirRespostasConsultoria(List<MpmRespostaConsultoria> respostas, DominioFinalizacao indFinalizacao) throws ApplicationBusinessException, BaseListException;
	
	public String pesquisarRespostasConsultoriaPorAtdSeqConsultoria(Integer atdSeq, Integer scnSeq, Integer ordem);
	
	RelatorioEstatisticaProdutividadeConsultorVO pesquisarRelatorioEstatisticaProdutividadeConsultor(final Short espSeq, final Date dtInicio, final Date dtFim) throws ApplicationBusinessException;
	
	public boolean validarMesmoProfissionalPorVinculoEMatricula(RapServidoresVO profissionalDe, RapServidoresVO profissionalPara);
	
	public List<RetornoConsultoriaVO> pesquisarRetornoConsultorias(Integer atdSeq, Integer scnSeq) throws ApplicationBusinessException;
	
    public List<ScoMaterial> obterMateriaisOrteseseProtesesPrescricao(final BigDecimal paramVlNumerico,
                                                                      final String  objPesquisa);
    
    List<JustificativaMedicamentoUsoGeralVO> obterListaMedicamentosUsoRestritoPorAtendimento(Integer atdSeq, String indAntiMicrobiano, Boolean indQuimioterapico, Short gupSeq);

    public List<JustificativaMedicamentoUsoGeralVO> obterMedicamentosIndicesRestritosPorAtendimento(Integer atdSeq, Short paramGupTb);

    List<AipPacientes> pesquisarPacientePorAtendimento(Integer atdSeq);
    
    MpmJustificativaUsoMdto persistirMpmJustificativaUsoMdto(MpmJustificativaUsoMdto justificativa,
    		List<JustificativaMedicamentoUsoGeralVO> medicamentos) throws ApplicationBusinessException;
    
	public MpmPrescricaoMdto obterMpmPrescricaoMdtoPorChavePrimaria(Object pk, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);

	public Integer transferirPacienteEmAcompanhamento(RapServidoresVO profissionalDe, RapServidoresVO profissionalPara) throws ApplicationBusinessException;
	
	public void persistirPacAtendProfissional(MpmPacAtendProfissional mpmPacAtendProfissional, RapServidores servidorPara) throws ApplicationBusinessException;

	boolean verificaMpmPacAtendProfissionalCadastrado(MpmPacAtendProfissional mpmPacAtendProfissional)throws ApplicationBusinessException;

	public MpmPrescricaoMedicaVO pesquisaPrescricaoMedicaPorAtendimentoESeq(Integer atdSeq, Integer seq);
	
	public List<SolicitacaoConsultoriaVO> pesquisaSolicitacaoConsultoriaPorAtendimentoEDataInicioEDataFim(Integer atdSeq, Date dthrInicio, Date dthrFim);
	
	List<ConsultoriasInternacaoVO> formatarColecaoRelatorioConsultorias(List<ConsultoriasInternacaoVO> colecao, DominioSituacaoConsultoria situacaoFiltro);
	/**
	 * @author thiago.cortes
	 * #3506
	 */
	public List<ConsultaTipoComposicoesVO> pesquisarTiposComposicoesNPT(AfaTipoComposicoes afaTipoComposicoes);
	public List<ConsultaCompoGrupoComponenteVO> pesquisarListaGrupoComposicoesNPT(Short seqSelecionado);
	public void removerAfaTipoComposicoes(Short seq) throws ApplicationBusinessException;
	public List<AfaGrupoComponenteNpt> pesquisarGrupoComponenteAtivo(final Object pesquisa);
	public AfaGrupoComponenteNpt obterGrupoComponente(final Short seq);
	public void adicionarGrupoComponentesAssociados(Short seqGrupo, Short seqTipoComposicao, boolean ativoGrupo) throws ApplicationBusinessException;
	public void alterarGrupoComponentesAssociados(Short seqGrupo, Short seqTipoComposicao, boolean ativoGrupo) throws ApplicationBusinessException;
	public void removerGrupoComponentesAssociados(ConsultaCompoGrupoComponenteVO listaGrupoComponenteSelecionado);
	public void gravarAfaTipoComposicoes(AfaTipoComposicoes afaTipoComposicoes) throws ApplicationBusinessException;
	public void alterarAfaTipoComposicoes(AfaTipoComposicoes afaTipoComposicoes) throws ApplicationBusinessException;
	public void validaAlterarAtivo(Boolean ativo,Short seq ) throws ApplicationBusinessException;
	
	void inserirCompoGrupoComponentes(AfaCompoGrupoComponente item, AfaTipoComposicoes tipo, AfaGrupoComponenteNptVO grupo) throws ApplicationBusinessException;
	
	void removerGrupoComponentes(AfaGrupoComponenteNptVO item) throws ApplicationBusinessException;

	List<AfaCompoGrupoComponenteVO> obterListaCompoGrupo(Short gcnSeq);

	List<AfaTipoComposicoes> obterListaSuggestionTipoComposicoes(String strPesquisa,List<AfaCompoGrupoComponenteVO> lista);

	void removerCompo(AfaCompoGrupoComponenteVO item)
			throws ApplicationBusinessException;
	
	void inserirGrupoComponentes(AfaGrupoComponenteNptVO item);
	
	void alterarCompoGrupoComponentes(AfaCompoGrupoComponenteVO item,
			AfaTipoComposicoes tic, AfaGrupoComponenteNptVO grupo)
			throws ApplicationBusinessException;
	
	AfaTipoComposicoes obterTipoPorSeq(Short seq);
	
	List<AfaGrupoComponenteNptVO> obterListaGrupoComponentes(Short seq,
			DominioSituacao situacao, String descricao);
	
	List<MpmJustificativaNpt> listarJustificativasNPT(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Short seq, final String descricao, final DominioSituacao situacao);

	public Long listarJustificativaNPTCount(final Short seq, final String descricao, final DominioSituacao situacao);

	public MpmJustificativaNpt obterMpmJustificativaNPToPorChavePrimaria(final Short seq);

	public abstract void persistirMpmJustificativasNPT(MpmJustificativaNpt justificativaNpt) throws BaseException;
	
	boolean verificarPrimeiraEntradaUnidadeFuncional(Integer ateSeq) throws ApplicationBusinessException;
	
	void gravarFormularioSinam(SinamVO vo) throws ApplicationBusinessException;
	
	AghAtendimentos obterAtendimentoPorSeq(Integer seq);
	
	void persistir(MpmEscoreSaps3 sap);

	List<MpmEscoreSaps3> pesquisarEscorePendentePorAtendimento(Integer atdSeq);
	
	MpmEscoreSaps3 obterEscoreSaps3(Integer seq);

	void persistirNotificacaoTuberculostatico(SinamVO vo) throws ApplicationBusinessException;
	
	void persistirMpmControlPrevAltas(MpmControlPrevAltas controlPrevAltas);

	SinamVO obterNotificacaoTuberculostatica(Integer ntbSeq);
	
	void atualizarEscoreSaps3(MpmEscoreSaps3 item);
	
	Double calcularObito(Short pontosSaps, Double somaObito, Double multiObito,
			Double subObito);
	
	List<VMpmMdtosDescr> obterSuggestionMedicamento(String strPesquisa);
	
	public RapServidores obterServidorCriacaoAltaSumario(Integer seqAtendimento);
	
	public AfaTipoVelocAdministracoes obterTipoVelocidadeAdministracaoPorSeqTipoVelocAdministracao(Short id);
	
	public void atualizarComposicao(AfaComposicaoNptPadrao afaComposicaoNptPadrao);
	
	public AfaItemNptPadrao obterItemNptPadrao(AfaItemNptPadraoId id);
	
	public void excluiItemNptPadrao(AfaItemNptPadraoId id) throws ApplicationBusinessException;
	
	public AfaComponenteNpt obterComponentePorId(Integer id);
	
	public VMpmDosagem obterVMpmDosagemPorId(Integer id);
	
	public void salvarComponente(AfaItemNptPadrao afaItemNptPadraoAdd);
	
	public void atualizarItemNptPadrao(AfaItemNptPadrao afaItemNptPadraoAdd);
	
	public AfaItemNptPadrao obterAfaItemNptPadraoPorId(AfaItemNptPadraoId afaItemNptPadraoId);
	
	public AfaFormaDosagem obterAfaFormaDosagemPorId(Integer id);
	
	List<AfaGrupoComponenteNpt> obterSuggestionGrupoComponentes(String strPesquisa);
	
	void gravarComponenteNpt(AfaComponenteNpt entity, Integer matCodigo, Short gcnSeq) throws ApplicationBusinessException;
	
	AfaComponenteNpt obterComponenteNptPorChave(Integer seq);
	
	AfaMedicamento obterMedicamento(Integer seq);
	
	void atualizarComponenteNpt(AfaComponenteNpt entity, Integer matCodigo, Short gcnSeq) throws ApplicationBusinessException;
	
	void removerComponenteNpt(ComponenteNPTVO selecionado)throws ApplicationBusinessException;
	
	List<MpmUnidadeMedidaMedica> obterSuggestionUnidade(String strPesquisa);
	
	List<AfaParamComponenteNpt> listarComponentesPorMatCodigo(Integer cod);
	
	List<AfaDecimalComponenteNpt> listarCasasDecimaisPorMatCodigo(Integer cod);
	
	void gravarParamComponenteNpt(AfaParamComponenteNpt entity)throws ApplicationBusinessException;
	
	void atualizarParamComponenteNpt(AfaParamComponenteNpt entity)throws ApplicationBusinessException;
	
	MpmUnidadeMedidaMedica obterUnidadeMedica(Integer seq);
	
	void removerParamComponenteNpt(AfaParamComponenteNpt entity)throws ApplicationBusinessException;
	
	void removerCasaComponenteNpt(AfaDecimalComponenteNpt entity)throws ApplicationBusinessException;
	
	void gravarCasaComponenteNpt(AfaDecimalComponenteNpt entity)throws ApplicationBusinessException;
	
	void atualizarCasaComponenteNpt(AfaDecimalComponenteNpt entity)throws ApplicationBusinessException;
	
	Short obterParamComponenteCount(Integer componenteSeq);
	
	Short obterCasaComponenteCount(Integer componenteSeq);
	Long popularResultadosParamSaps3(String parametro, Integer atdSeq);
	
	String salvarDiagPacCti(PrescricaoMedicaVO prescricaoMedicaVO, String complemento, AghCid aghCid, Integer pmeSeqAtendimento) throws ApplicationBusinessException;
	
	MpmInformacaoPrescribente obterMpmInformacaoPrescribentePorChavePrimaria(Integer seq);
	
	//	#5795
	InformacoesPacienteAgendamentoPrescribenteVO obterInformacoesPacienteAgendamentoPrescribenteVO(Integer seq);
	
	RapServidores obterServidorPorUsuario(final String login) throws ApplicationBusinessException;
	
	void validarAlteracaoMpmInformacaoPrescribentes(MpmInformacaoPrescribente novo, MpmInformacaoPrescribente old) throws ApplicationBusinessException;
	
	void validarInsercaoMpmInformacaoPrescribentes(MpmInformacaoPrescribente novo) throws ApplicationBusinessException;
	
	VerificarDadosItensJustificativaPrescricaoVO mpmpVerDadosItens(Integer atdSeq) throws ApplicationBusinessException;
	
	CodAtendimentoInformacaoPacienteVO buscarCodigoInformacoesPaciente(Integer seq);
	
	MpmPrescricaoMedica obterValoresPrescricaoMedica(Integer atdSeqPme, Integer seqPme);
	
	List<FatSituacaoSaidaPaciente> obterListaEstadosClinicos(Short idade, DominioSexoDeterminante sexo);
	
	List<FatSituacaoSaidaPaciente> listarEstadoClinicoPacienteObitoAtivos(Short idade, DominioSexoDeterminante sexo);
	
	/**
	 * obtém Lista de mat_Codigo do Procedimento Especial.
	 * @param pedSeq
	 * @return
	 */
	public List<Integer> buscarListaMatCodigoProcedEspecial(Short pedSeq);
	
	/**
	 * obtém AghAtendimentos
	 * @param Seq - AghAtendimento
	 * @return
	 */
	public AghAtendimentos buscarAghAtendimento(Integer seqAtendimento);
	
	/**
	 * obtém AghAtendimentos
	 * @param Seq - AghAtendimento
	 * @return
	 */
	public AghAtendimentos buscarAghAtendimentoComCentroDeCusto(Integer seqAtendimento);
	
	/**
	 * obtém MpmCidAtendimento
	 * @param Seq - AghAtendimento
	 * @return
	 */
	public List<MpmCidAtendimento> buscarListaMpmCidAtendimento(Integer seqAtendimento);
	
	/**
	 * obtém Impressora de destino da RM
	 * @ORADB RMSP_GERA_RM_PRCS - C2
	 * @param TipoDocumentoImpressao
	 * @return
	 */
	public List<AghImpressoraPadraoUnids> buscarImpressoraDestinoRM(TipoDocumentoImpressao tipoDocumentoImpressao);

	/**
	 * obtém lista de SceEstoqueAlmoxarifadoRM
	 * @ORADB RMSP_GERA_RM_PRCS - C3
	 * @param pedSeqProcEspRm, matCodigoProcEspRm
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public List<SceEstoqueAlmoxarifado> buscarsceEstoqueAlmoxarifado(Short pedSeqProcEspRm, Integer matCodigoProcEspRm) throws ApplicationBusinessException;

	public String montarLocalizacaoPaciente(Integer seqAghAtendimento) throws ApplicationBusinessException;
	
	public List<AfaComposicaoNptPadraoVO> obterListaComposicaoNptPadraoVO(Short seq);
	
	public List<AfaItemNptPadraoVO> obterListaAfaItemNptPadrao(Short idComposicao, Short idFormula);
	
	public List<AfaMensCalculoNpt> listarMensagensCalculoNpt(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AfaMensCalculoNpt filtro);

	public Long listarMensagensCalculoNptCount(AfaMensCalculoNpt filtro);
    
	public void excluirMensagemCalculoNpt(AfaMensCalculoNpt item);
   
	public void salvarMensagemCalculoNpt(AfaMensCalculoNpt item, RapServidores servidorLogado) throws BaseException, ApplicationBusinessException ;

	public RapServidores obterHintAfaMensCalculoNpt(AfaMensCalculoNpt item);

	public List<AfaFormulaNptPadrao> obterFormulaNptPadrao(AfaFormulaNptPadrao filtro);
    
	public void excluiFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao) throws ApplicationBusinessException;
     
	public AfaComposicaoNptPadrao obterComposicaoNptPadrao(AfaComposicaoNptPadraoId seq);

	public void excluiComposicaoNptPadrao(AfaComposicaoNptPadrao AfaComposicaoNptPadrao) throws ApplicationBusinessException;
    
	public VMpmDosagem obterVMpmDosagemPorAfaItem(AfaItemNptPadrao afaItemNptPadrao);

	public void persistirFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao,RapServidores rapServidores);
	
	public void atualizarFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao,RapServidores rapServidores);
	
	public	AfaFormulaNptPadrao obterFormulaNptPadraoPorPk(Short seq);
	
	public List<AfaTipoComposicoes> pesquisaAfaTipoComposicoesPorFiltro(Object filtro);
	
	public long pesquisaAfaTipoComposicoesPorFiltroCount(Object filtro);
	
	public List<AfaTipoVelocAdministracoes> pesquisaAfaTipoVelocAdministracoesAtivos(Object objeto);
	
	public long pesquisaAfaTipoVelocAdministracoesAtivosCount(Object objeto);
	
	public List<TipoComposicaoComponenteVMpmDosagemVO> pesquisaComponenteVinculadoComposicaoFormula(short seqAfaTipoComposicoes,Object objeto);
	
	public long pesquisaComponenteVinculadoComposicaoFormulaCount(short seqAfaTipoComposicoes,Object objeto);
	
	public List<VMpmDosagem> pesquisarVMpmDosagemPorfiltro(Integer medMatCodigo,Object objeto);
	
	public long pesquisarVMpmDosagemPorfiltroCount(Integer medMatCodigo,Object objeto);
	
	public void salvarComposicao(AfaComposicaoNptPadrao afaComposicaoNptPadrao);	
	
	List<AfaFormulaNptPadrao> listarFormulaNptPadrao();
	
	//#990
	List<MpmJustificativaNpt> pesquisarJustificativaNptPorDescricao(String param);
	
	Long pesquisarJustificativaNptPorDescricaoCount(String param);
	
	Boolean isFormulaPadraoOuLivre(Short seq);
	
	String montarMensagemPrescricaoNpt(Integer atdSeq);
			    
	AfaFormulaNptPadrao obterFormulaPediatricao(Integer atdSeq);

	void persistirPrescricaoNpt(Short fnpSeq, String descricaoFormula, MpmJustificativaNpt justificativa, PrescricaoMedicaVO prescricaoMedicaVO, MpmPrescricaoNptVO prescricaoNptVO, String nomeMicrocomputador, Boolean desconsiderarItensNulos) throws ApplicationBusinessException;
	    
	boolean temComposicoesComponentesNulos(MpmPrescricaoNptVO vo);

	void excluirPrescricaoNpt(MpmPrescricaoNpt prescricaoNpt, String nomeComputador) throws ApplicationBusinessException;
	   
	void atualizarPrescricaoNpt(MpmPrescricaoNpt prescricao, String nomeMicrocomputador, Date dataFimVinculoServidor) throws ApplicationBusinessException;
	 
	MpmPrescricaoNptVO buscarDadosPrescricaoNpt(Integer atdSeq, Integer pnpSeq);
	 
	List<MpmComposicaoPrescricaoNptVO> buscarComposicoesNptDescritaPorId(Integer seq, Integer atdSeq);
	 
	List<MpmItemPrescricaoNptVO> buscarComponentesComposicaoNptDescritaPorId(Integer cptPnpAtdSeq, Integer cntPnpSeq, Short cptSeqp);
	
	MpmJustificativaNpt obterJustificativaPorChavePrimaria(final Short seq);
	 
	void excluirPrescricaoNptPorItem(ItemPrescricaoMedicaVO item, String nomeComputador) throws ApplicationBusinessException;

	String descricaoFormatadaNpt(MpmPrescricaoNpt prescricaoNpts);
	
	List<ListaPacientePrescricaoVO> obterListaDePacientes(Integer matricula, Short vinCodigo);
	
	DadosDialiseVO obterCaminhoDialise(Integer atdSeq, String nomeMicrocomputador) throws BaseException;
	
	public Integer obterIdadePaciente(Integer codPaciente);
	
	public String obterNomeUsualPacitente(Integer matricula, Short vinCodigo);

    public void removerModoDeUso(
            List<ModoUsoProcedimentoEspecialVO> listaModoUsoParaExclusao) throws BaseException;
    
   

	List<MpmSolicitacaoConsultoria> pesquisarSolicitacaoConsultoriaPorServidorEspecialidade(
			RapServidores servidor);

	void gerarPendenciasSolicitacaoConsultoria();
    
    void inserirNovaReceita(MamReceituarios receitaGeral, List<MamItemReceituario> itensReceitaGeral,MamReceituarios receitaEspecial, List<MamItemReceituario> itensReceitaEspecial) throws ApplicationBusinessException;
     
    public List<VMamMedicamentos> obterMedicamentosReceitaPorDescricaoOuCodigo(String descricaoCodigo);

	public Long obterMedicamentosReceitaPorDescricaoOuCodigoCount(String descricaoCodigo);
    
    void validarItemReceita(MamItemReceituario item) throws BaseListException;

	List<AipAlergiaPacientes> listarAlergiasPacientesPorCodigoPaciente(
			Integer pacCodigo);
			    
    /**#45341 C1 - Obtem a sigla e o nome da especialidade pela sequencia da especialidade**/
	public AghEspecialidades obterSiglaNomeEspecialidadePorEspSeq(Short pEspSeq);
	/**#45341 C2 - Retorna apenas um resultado com o nome, número do prontuário e codigo do paciente **/
	public ConsultarRetornoConsultoriaVO obterPacienteNroProntuario(Integer pAtdSeq);

	List<AltaPrincReceitasVO> obterListaMedicamentosPrescritosAlta(Integer apaAtdSeq, Integer apaSeq);
	    
	public void persistirMpmAlta(List<AltaPrincReceitasVO> listaAltaPrincReceitasAux1, List<AltaPrincReceitasVO> listaAltaPrincReceitasAux2, MpmAltaSumario altoSumario) throws BaseException;

	 List<MamReceituarios> buscarDadosReceituario(Integer atendimentoSeq,DominioTipoReceituario tipo);
	   
	 void excluirReceita(MamReceituarios receitaGeral,MamReceituarios receitaEspecial) throws ApplicationBusinessException;
	 
	 void prescricaoMedicaTemPeloMenosUmaDieta(PrescricaoMedicaVO prescricaoMedicaVO) throws ApplicationBusinessException;
	 
	 MamReceituarios obterReceituario(Long seq);
	 
	void verificarPrescricaoCancelada(MpmPrescricaoMedica prescricao)
			throws ApplicationBusinessException;	
	
	 List<VMamMedicamentos> obterMedicamentosReceitaPorDescricaoExata(String descricao);
	 
	 public boolean verificarPacienteInternadoCaracteristicaControlePrevisao(Integer atendimentoSeq) throws ApplicationBusinessException;
	 
	 
	 
		//3468
	List<AipAlergiaPacientes> obterAipAlergiasPacientes(Integer pacCodigo);

	List<MpmAlergiaUsual> obterMpmAlergiasUsual(String parametro);
		
	Long obterMpmAlergiasUsualCount(String parametro);
		
	MpmAlergiaUsual obterMpmAlergiaUsualPorSeq(Integer seq);
		
	List<AipAlergiaPacientes> atualizarListaAipAlergiaPacientes(List<AipAlergiaPacientes> listaAipAlergiaPacientes, AipAlergiaPacientes aipPacientesSelecionado,
		MpmAlergiaUsual mpmAlergiaUsualSelecionado, boolean motivoCancelamentoHabilitado, String descricaoNaoCadastrado, String motivoCancelamento, RapServidores servidorLogado) throws ApplicationBusinessException, CloneNotSupportedException;

	void adicionarAipAlergiaPacientes(List<AipAlergiaPacientes> listaAipAlergiaPacientes, AipAlergiaPacientes aipPacientesSelecionado,
		MpmAlergiaUsual mpmAlergiaUsualSelecionado, boolean motivoCancelamentoHabilitado, String descricaoNaoCadastrado, String motivoCancelamento, int contador,
		AipPacientes paciente) throws ApplicationBusinessException;

	MpmPrescricaoMedica obterPrescricaoComAtendimentoPaciente(Integer atdSeq,
			Integer seq);

	/**
	 * #44179
	 * @author marcelo.deus 
	 */
	void atualizarIndPacientePediatrico(PrescricaoMedicaVO prescricaoMedicaVO) throws ApplicationBusinessException;
	String buscarEstadoPaciente(Integer pmeSeqAtendimento, Integer pmeSeq);
	boolean verificaExistenciaAlergiaCadastradaPaciente(Integer atendimentoSeq);
	ParametrosProcedureVO verificarParametros(String obterLoginUsuarioLogado, Integer codAtendimento, Integer seqPrescricao, ParametrosProcedureVO parametrosProcedureVO) throws ApplicationBusinessException;
	AacConsultas obterConsultaPorAtendimentoSeq(Integer codAtendimento);
	AghAtendimentos obterAghUnidadeFuncionalPorAtendimentoSeq(Integer codAtendimento);
	boolean obterCaracteristicaPorUnfSeq(Short seq, ConstanteAghCaractUnidFuncionais caracteristica);
	Long buscarTriagemEstadoPaciente(Integer pmeSeqAtendimento, Integer pmeSeq);
	void validarCIDAtendimento(ParametrosProcedureVO parametrosProcedureVO,Integer codAtendimento,Integer seqPrescricao) throws ApplicationBusinessException;
		
	//3468 RESTANTE
		
	public void gravarAipAlergiaPacientes(List<AipAlergiaPacientes> listaAipAlergiaPacientes, RapServidores servidorLogado) throws ApplicationBusinessException;
	
	public List<AipAlergiaPacientes> obterAipAlergiasPacientesHistorico(Integer pacCodigo);
	
	public void validaListaMedicamentosPrescritosAlta(Integer apaAtdSeq, Integer apaSeq, Short seqp, Integer atdSeq);

	/**
	 * #45234
	 */
	public List<MpmTextoPadraoParecer> pesquisarMpmTextoPadraoParecer(String sigla, String descricao);
	public void editarMpmTextoPadraoParecer (MpmTextoPadraoParecer mpmTextoPadraoParecer, String siglaNova, String descricao) throws BaseException;
	public void removerMpmTextoPadraoParecer(MpmTextoPadraoParecer mpmTextoPadraoParecer) throws BaseException;
	public void adicionarMpmTextoPadraoParecer(String sigla, String descricao) throws BaseException;
	public Object obterSiglaMpmTextoPadraoParecer(String sigla);
	
	MpmTextoPadraoParecer obterMpmTextoPadraoParecerOriginal(String sigla);
	
	
	Long obterQuantidadePrescricoesVerificarAnamnese(Integer prontuario);
	
	Boolean verificarAnamneseCriarPrescricao(AghAtendimentos atd) throws ApplicationBusinessException;

	List<MpmItemPrescParecerMdto> pesquisarMpmItemPrescParecerMdtoPorProtuarioLeito(
			Integer prontuario,Date parametroDataFim, Integer atdSeq);


	List<AinLeitos> pesquisarLeitosPorUnidadeFuncionalSalaQuarto(Object param);


	Long countPesquisarLeitosPorUnidadeFuncionalSalaQuarto(Object param);


	AghAtendimentos obterAghAtendimentosPorProntuario(Integer prontuario,
			Date parametroDataFim);


	AghAtendimentos obterAghAtendimentosPorLeitoID(String leitoID,boolean filtarIndPacAtendimento);
	
	/**
	 * Obtém as informações da justificativa de uso de medicamento, conforme código de justificativa informado.
	 * 
	 * @param jumSeq - Código da justificativa
	 * @return Dados da Justificativa solicitada
	 */
	MpmJustificativaUsoMdto obterDadosJustificativaUsoMedicamento(Integer jumSeq);

	void atualizarMpmJustificativaUsoMdto(MpmJustificativaUsoMdto justificativa);

	/**
	 * #45269
	 * @param pSolicitacao
	 * @throws ApplicationBusinessException 
	 */
	AvaliacaoMedicamentoVO imprimirAvaliacaoMedicamento(Integer pSolicitacao, Integer pMedMatCodigo) throws ApplicationBusinessException;
	
//	#1291 - SB1
	public List<AfaMedicamento> pesquisarMedicamentosSB1(String parametro);
	
//	#1291 - SB1
	public Long pesquisarMedicamentosSB1Count(String parametro);	
	
//	#1291 - SB2
	public List<AfaTipoUsoMdto> pesquisarTipoUsoSB2(String parametro);
	
//	#1291 - SB2
	public Integer pesquisarTipoUsoSB2Count(String parametro);		

//	#1291 - SB3
	public List<AfaGrupoUsoMedicamento> pesquisarGrupoUsoSB3(String parametro);
	
//	#1291 - SB3
	public Integer pesquisarGrupoUsoSB3Count(String parametro);
	
//	#1291 - SB4
	public List<VAghUnidFuncional> pesquisarVUnidFuncionalSB4(String parametro);
	
//	#1291 - SB4
	public Long pesquisarVUnidFuncionalSB4Count(String parametro);
	
//	#1291 - SB5
	public List<VMpmpProfInterna> pesquisarVMpmpProfInternaSB5(String parametro);
	
//	#1291 - SB5
	public Long pesquisarVMpmpProfInternaSB5Count(String parametro);
	
//	#1291 - SB6
	public List<VMedicoSolicitante> pesquisarVMedicoSolicitanteSB6(String parametro);
	
//	#1291 - SB6
	public Long pesquisarVMedicoSolicitanteSB6Count(String parametro);
	
//	#1291 - C7
	public List<VMpmItemPrcrMdtosVO> pesquisarSolicitacoesUsoMedicamento(Integer arg0, Integer arg1,
			String arg2, boolean arg3, SolicitacoesUsoMedicamentoVO filtro);
	
//	#1291 - C7
	public Long pesquisarSolicitacoesUsoMedicamentoCount(SolicitacoesUsoMedicamentoVO filtro);
	
	public AinQuartos pesquisarAinQuartoPorId(Integer numeroLeito);
	
	public AghUnidadesFuncionais  pesquisarAghUnidadesFuncionaisPorId(Integer unfSeq);
	
	public AinLeitos pesquisarAinLeitoPorId(String LtoLtoId);
	
	
	public Boolean justificativaAntiga(String indicacao, String infeccaoTratar, String diagnostico);
	
	public Boolean usoRestriAntimicrobianoIgualNao(Short gupSeq, String indicacao) 
			throws ApplicationBusinessException;
	
	public Boolean usoRestriAntimicrobianoIgualSim(Short gupSeq, String infeccaoTratar) 
			throws ApplicationBusinessException;
	
	public Boolean naoPadronAntimicrobianoIgualNao(Short gupSeq, String indicacao, String infeccaoTratar) 
			throws ApplicationBusinessException;
	
	public Boolean naoPadronAntimicrobianoIgualSim(Short gupSeq, String indicacao, String infeccaoTratar) 
			throws ApplicationBusinessException;
	
	public Boolean indQuimioterapicoIgualSim(String diagnostico);
	
	public void atualizarSituacaoMedicamento(Integer seqSolicitacao, Date dataInicio, Date dataFim, 
			DominioIndRespAvaliacao indResponsavelAvaliacao);
	
	public String funcaoCfSolicitanteFormula(Integer curJumSeq) throws ApplicationBusinessException;
	
	public String obterNomeServidor(Integer serMatricula, Short serVinCodigo);
	
	public String obterRegConselho(Integer serMatricula, Short serVinCodigo);
	
	public String obterSiglaServidor(Integer serMatricula, Short serVinCodigo);
	
	public DetalhesParecerMedicamentosVO obterDetalhesParecerMedicamentos(BigDecimal parecerSeq,MpmItemPrescParecerMdtoId mpmItemPrescParecerMdtoId);

	public List<HistoricoParecerMedicamentosJnVO> obterHistoricoParecerMedicamentos(BigDecimal parecerSeq);


	AinLeitos obterLeitoQuartoUnidadeFuncionalPorId(String leitoID);

	/**
	 * Obtém detalhamento da justificativa em formato de String.
	 * 
	 * @ORADB Mpmp_popula_sintaxe
	 * 
	 * @return Justificativa detalhada
	 * @throws ApplicationBusinessException 
	 */
	public String obterDetalhesJustificativaUsoMedicamento(Integer jumSeq) throws ApplicationBusinessException;

	/**
	 * #45250
	 * @param jumSeq
	 * @throws ApplicationBusinessException 
	 */
	AvaliacaoMedicamentoVO imprimirSolicitacaoMedicamentoAvaliar(MpmJustificativaUsoMdto jumSeq) throws ApplicationBusinessException;

	/**
	 * Realiza as validações e a consulta para geração de PDF SINAN.
	 * 
	 * @param atdSeq - Código do Atendimento
	 * @return Dados a serem exibidos no relatório
	 * @throws ApplicationBusinessException
	 */
	GerarPDFSinanVO gerarPdfSinan(Integer atdSeq) throws ApplicationBusinessException;

	Long[] obterContagemTotais(Integer matricula, Short vinCodigo, DominioIndRespAvaliacao respAvaliacao);
	
	void aprovarLote(Integer matricula, Short vinCodigo, DominioIndRespAvaliacao respAvaliacao) throws ApplicationBusinessException;
	
	//TODO VIRGINIA
	public List<AfaItemNptPadraoVO> obterListaAfaItemNptPadraoOrder(Short idComposicao, Short idFormula);
	//
	 
    /*
     * #1378
     */
    public List<MamTipoEstadoPaciente> obterListaTipoEstadoPacientePrescricao(Integer atdSeq);
    public void manterEstadoPaciente(MamEstadoPaciente estadoPaciente, Long rgtSeq);
	public void alterarEstadoPaciente(EstadoPacienteVO estadoAnterior, MamEstadoPaciente estado, Long atdSeq) throws ApplicationBusinessException; 
	public EstadoPacienteVO obterEstadoPacientePeloTrgSeq(Long trgSeq);
	public EstadoPacienteVO obterEstadoPacientePeloAtdSeq(Long atdSeq);
	public Long obterEsaSeqPorRgtSeq(Long rgtSeq);
	public MamEstadoPaciente obterMamEstadoPacienteAtdSeq(Long atdSeq);
	MamTrgEncInterno recuperarCurTei(Long cTrgSeq);
	CursorPacVO buscarCursorPac(Integer cConNumero);
	Date buscarDataNascimentoPaciente(Integer pPacCodigo);
	MamRegistro obterRegistroPorSeq(Long registro);

	
	public void montarAltaNaoCadastradaGestacao(final MpmAltaSumario altaSumario) throws ApplicationBusinessException;
	Boolean verificaPendenciaFichaApache(Integer atdSeq);

	public String obterIdadeFormatada(Date dtNascimento);

	public void validarPesquisaSolicitacoesUsoMedicamentos(SolicitacoesUsoMedicamentoVO filtro) throws ApplicationBusinessException;
		
	/**
	 * @author marcelo.deus
	 * #44179 - Consulta para retornar o tipo do estado do paciente, parte da melhoria 49372
	 * @param estadoPaciente
	 */
	MamTipoEstadoPaciente obterTipoEstadoPacientePorDescricao(String estadoPaciente);
	Long obterTriagemPorPaciente(Integer atendimentoSeq);
		
		

	boolean validarSumarioConcluidoAltaEObitoPorAtdSeq(Integer atdSeq);


	List<AghAtendimentosVO> pesquisarAghAtendimentosPorProntuario(
			Integer prontuario,String leitoID,Date parametroDataFim);
		
	void validarProporcaoGlicose50Menor100(BigDecimal paramPercGlic50) throws ApplicationBusinessException;
		
	void validarProporcaoGlicose10Menor100(BigDecimal paramPercGlic10) throws ApplicationBusinessException;
	
	void validarSomaProporcaoGlicose(BigDecimal paramPercGlic50, BigDecimal paramPercGlic10) throws ApplicationBusinessException;
	
	void validarIntervaloTempoInfusaoSolucao(Short paramTempInfusaoSol) throws ApplicationBusinessException;
	
	void validarIntervaloTempoInfusaoLipidios(Short paramTempInfusaoLip) throws ApplicationBusinessException;

	CalculoAdultoNptVO iniciarCalculoNpt(MpmPrescricaoNptVO prescricaoNptVo) throws ApplicationBusinessException;

	void atualizarPesoAlturaCalculoNpt(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException;

	void gravarCalculoNpt(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException;
	
	List<PesquisaFoneticaPrescricaoVO> pesquisarPorFonemasPrescricao(Integer firstResult,
			Integer maxResults, String nome, String nomeMae,
			boolean respeitarOrdem, Date dataNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException;
	
	Long pesquisarPorFonemasPrescricaoCount(String nome, String nomeMae,
			Boolean respeitarOrdem, Date dtNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException ;
	
	/**
	 * @author marcelo.deus
	 * #44281
	 */
	void verificarDosagemMedicamentoSolucao(Integer medMatCodigo, BigDecimal pimDose, Integer seq) throws ApplicationBusinessException;
	void validarCampoDose(BigDecimal dose) throws ApplicationBusinessException;
	
	public List<MpmItemPrescricaoMdto> listarPrescMedicamentoDetalhePorPacienteAtendimento(Integer pacCodigo, Integer atdSeq);
	
	/**
	 * Obter informação do serviço e médico do atendimento infornmado
	 * #50931 - P1
	 * @ORADB mamc_int_servico_eqp
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	String obterServicoMedicoDoAtendimento(Integer atdSeq) throws ApplicationBusinessException;
	
	
	void persistirNotaAdicionalAnamnese(MpmNotaAdicionalAnamneses notaAdicional, String descricao, RapServidores servidor, String nomeEspecialidade);

	List<MpmNotaAdicionalAnamneses> listarNotasAdicionaisAnamnese(Long seqAnamneses);

	MpmEvolucoes buscarMpmEvolucoes(Long seqEvolucao);

	void persistirNotaAdicionalEvolucao(MpmNotaAdicionalEvolucoes notaAdicional, String descricao, String nomeEspecialidade);

	MpmAnamneses obterAnamneseValidadaPorAnamneses(Long seqAnamneses);

	List<MpmNotaAdicionalEvolucoes> listarNotasAdicionaisEvolucoes(Long seqEvolucoes);

	MpmAnamneses criarAnamnese(AghAtendimentos atendimento, RapServidores servidor) throws ApplicationBusinessException;

	MpmAnamneses obterAnamneseAtendimento(Integer atdSeq);

	/**
	 * Caso anamnese esteja PENDENTE, passa sua situação para EM USO e sincroniza com o banco
	 * 
	 * @param anamnese
	 * @return
	 */

	void iniciarEdicaoAnamnese(MpmAnamneses anamnese, RapServidores servidor);

	void persistirMpmAnamnese(MpmAnamneses anamnese);

	void persistirMpmEvolucoes(MpmEvolucoes evolucao);

	void atualizarMpmEvolucoes(MpmEvolucoes evolucao);

	boolean existeAnamneseValidaParaAtendimento(Integer atdSeq);

	List<MpmEvolucoes> obterEvolucoesAnamnese(MpmAnamneses anamnese, Date data, DominioIndPendenteAmbulatorio situacao);

	void atualizarMpmEvolucaoEmUso(MpmEvolucoes evolucao, RapServidores servidor);

	void validarEvolucaoEmUso(MpmEvolucoes evolucao) throws ApplicationBusinessException;

	MpmAnamneses obterAnamneseValidadaPorAtendimento(Integer seqAtendimento);

	MpmEvolucoes criarEvolucao(MpmAnamneses anamnese, Date dataReferencia, RapServidores servidor) throws ApplicationBusinessException;

	List<MpmEvolucoes> pesquisarEvolucoesAnteriores(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Long anaSeq, Date dataInicial, Date dataFinal);

	Long pesquisarEvolucoesAnterioresCount(Long anaSeq, Date dataInicial, Date dataFinal);

	MpmAnamneses obterAnamnese(Integer seqAtendimento, Long seqAnamnese, RapServidores servidor) throws ApplicationBusinessException;

	boolean verificarEvolucoesNotasAdicionais(Long anaSeq);

	void concluirAnamnese(MpmAnamneses anamnese, RapServidores servidor) throws ApplicationBusinessException;

	void deixarPendenteAnamnese(MpmAnamneses anamnese, RapServidores servidor) throws ApplicationBusinessException;

	void concluirEvolucoes(MpmEvolucoes evolucao, String descricaoEvolucao, RapServidores servidor) throws ApplicationBusinessException;

	void deixarPendenteEvolucao(MpmEvolucoes evolucao, String descricaoEvolucao, RapServidores servidor) throws ApplicationBusinessException;

	void validarEclusaoEvolucao(Long seqEvolucao, RapServidores servidor) throws ApplicationBusinessException;

	void excluirEvolucao(Long seqEvolucao, RapServidores servidor) throws ApplicationBusinessException;

	List<MpmEvolucoes> obterEvolucoesAnamnese(MpmAnamneses anamnese, Date data, List<DominioIndPendenteAmbulatorio> situacoes);

	boolean verificarAnamneseValida(Long anaSeq);

	MpmAnamneses obterAnamneseValidaParaAtendimento(Integer atdSeq);

	boolean possuiNotaAdicionalEvolucao(Long seqEvolucoes);

	List<MpmEvolucoes> listarEvolucoesConcluidasAnamnese(Long seqAnamnese);

	boolean verificarEvolucoesAnamnesePorSituacao(Long anaSeq, DominioIndPendenteAmbulatorio pendente, boolean situacaoIgual);

	boolean verificarEvolucoesValidadas(Long seqAnamnese);

	List<MpmNotaAdicionalEvolucoes> listarNotasAdicionaisEvolucao(Long seqEvolucao);

	List<RelatorioEvolucoesPacienteVO> gerarRelatorioEvolucaoPaciente(Long seqEvolucao);

	List<RelatorioAnamnesePacienteVO> gerarRelatorioAnamnesePaciente(Long seqAnamnese);

	Date obterDataReferenciaEvolucao(AghAtendimentos atendimento) throws ApplicationBusinessException;

	MpmEvolucoes criarMpmEvolucaoComDescricao(String descricao, MpmAnamneses anamnese, Date dataReferencia, RapServidores servidor) throws ApplicationBusinessException;

	Boolean verificarAdiantamentoEvolucao(AghAtendimentos atendimento, Date dataAtual);

	MpmAnamneses obterMpmAnamnese(Long seqAnamnese);

	MpmAnamneses obterAnamneseDetalhamento(MpmAnamneses anamnese);

	AghAtendimentos obterAtendimentoPorProntuarioLeito(Integer prontuario);

	public void removerAnamnese(Long seqAnamnese);

	public void removerEvolucao(Long seqEvolucao) throws ApplicationBusinessException;

	public void verificaGrupoUsoMedicamentoTuberculostatico(
			List<MpmItemPrescricaoMdto> itensPrescricaoMdtos) throws BaseException;
	
	public List<MpmEvolucoes> obterEvolucoesAnamnese(MpmAnamneses anamnese,
			List<DominioIndPendenteAmbulatorio> situacoes, Date dataInicio,
			Date dataFim);

	void atualizarMpmAltaSumario(MpmAltaSumario mpmAltaSumario);
	
	public void persistirPrescricaoMedicamentoCancelar(final MpmPrescricaoMdto prescricaoMedicamento, String nomeMicrocomputador, MpmPrescricaoMdto prescricaoMedicamentoOriginal)
			throws BaseException;
	
	String buscarResumoLocalPacienteUniFuncional(AghAtendimentos atendimento) throws ApplicationBusinessException;
}