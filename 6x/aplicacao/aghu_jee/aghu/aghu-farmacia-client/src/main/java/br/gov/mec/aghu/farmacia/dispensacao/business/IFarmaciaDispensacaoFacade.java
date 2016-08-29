package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.vo.AfaDispensacaoMdtoVO;
import br.gov.mec.aghu.farmacia.vo.ConsultaDispensacaoMedicamentosVO;
import br.gov.mec.aghu.farmacia.vo.DispensacaoMedicamentosVO;
import br.gov.mec.aghu.farmacia.vo.ListaOcorrenciaVO;
import br.gov.mec.aghu.farmacia.vo.TicketMdtoDispensadoVO;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAfaPrcrDispMdtos;
import br.gov.mec.aghu.prescricaomedica.vo.DispensacaoFarmaciaVO;

public interface IFarmaciaDispensacaoFacade extends Serializable {

	public List<DispensacaoMedicamentosVO> pesquisarDispensacaoMdtos(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmPrescricaoMedica prescricaoMedica, Short unfSeq, Long seqPrescricaoNaoEletronica);

	public List<AghUnidadesFuncionais> listarFarmaciasAtivasByPesquisa(
			Object strPesquisa);

	public Long listarFarmaciasAtivasByPesquisaCount(Object strPesquisa);

	public List<VAfaPrcrDispMdtos> pesquisarPacientesParaDispensacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Date dataReferencia, AghUnidadesFuncionais farmacia,
			AghUnidadesFuncionais unidadeFuncionalPrescricao, AinLeitos leito,
			AinQuartos quarto, AipPacientes paciente)
			throws ApplicationBusinessException;

	public Long pesquisarPacientesParaDispensacaoCount(Date dataReferencia,
			AghUnidadesFuncionais farmacia,
			AghUnidadesFuncionais unidadeFuncionalPrescricao, AinLeitos leito,
			AinQuartos quarto, AipPacientes paciente);

	// Estória #5387
	public List<AfaDispensacaoMdtos> pesquisarItensDispensacaoMdtos(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeSolicitante,
			Integer prontuario, String nomePaciente, Date dtHrInclusaoItem,
			AfaMedicamento medicamento,
			DominioSituacaoDispensacaoMdto situacao,
			AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos,
			String etiqueta, MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica);

	// Estória #5387
	public Long pesquisarItensDispensacaoMdtosCount(
			AghUnidadesFuncionais unidadeSolicitante, Integer prontuario,
			String nomePaciente, Date dtHrInclusaoItem,
			AfaMedicamento medicamento,
			DominioSituacaoDispensacaoMdto situacao,
			AghUnidadesFuncionais farmacia, AghAtendimentos aghAtendimentos,
			String etiqueta, MpmPrescricaoMedica prescricaoMedica, String loteCodigo, Boolean indPmNaoEletronica);

	//#5388 
	public List<MpmItemPrescricaoMdto> recuperarListaTriagemMedicamentosPrescritos(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmPrescricaoMedica prescricaoMedica,
			AfaMedicamento medicamento);

	public void estornarMedicamentoDispensadoByEtiquetaComCb(String pEtiqueta, String nomeMicrocomputador)
			throws BaseException;

	public void validarDatas(Date dataDeReferenciaInicio,
			Date dataDeReferenciaFim) throws ApplicationBusinessException;

	public void processaAfaTipoOcorBySeqInAfaDispMdtoEstorno(
			AfaDispensacaoMdtos adm, Short seqMotivoEstornoPrescricaNaoEletronica) throws ApplicationBusinessException;

	public void realizaEstornoMedicamentoDispensadoDaLista(
			List<AfaDispensacaoMdtos> listaDispensacaoModificada,
			List<AfaDispensacaoMdtos> listaDispensacaoOriginal,
			String nomeMicrocomputador)
			throws BaseException;

	public List<AfaDispensacaoMdtos> recuperarListaTriagemMedicamentosPrescricao(
			MpmPrescricaoMedica prescricaoMedica);

	public List<AfaTipoOcorDispensacao> pesquisarTipoOcorrenciasAtivasENaoEstornada();

	public Boolean processaTipoOcorrenciaParaListaMedicamentosPrescricao(
			List<AfaDispensacaoMdtos> listaTriagem) throws ApplicationBusinessException;

	public List<AfaTipoOcorDispensacao> pesquisarTodosMotivosOcorrenciaDispensacao(
			Object strPesquisa);

	public Long pesquisarTodosMotivosOcorrenciaDispensacaoCount(
			Object strPesquisa);

	public void realizaTriagemMedicamentoPrescricao(
			List<AfaDispensacaoMdtos> listaTriagemModificada,
			List<AfaDispensacaoMdtos> listaTriagemOriginal,
			String nomeMicrocomputador)
			throws BaseException;

	public List<AfaDispensacaoMdtos> recuperarlistaOcorrenciasMdtosDispensados(
			AghUnidadesFuncionais unidadeFuncional, Date data,
			AfaTipoOcorDispensacao tipoOcorDispensacao, Integer prontuario,
			AinLeitos leito, AghUnidadesFuncionais farmacia)
			throws ApplicationBusinessException;

	public void assinaDispMdtoSemUsoDeEtiqueta(AfaDispensacaoMdtos adm, 
			AghMicrocomputador micro, 
			AghMicrocomputador microDispensador, String nomeMicrocomputador) throws BaseException;

	public void pesquisaEtiquetaComCbMedicamento(String etiqueta,
			List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados,
			AghUnidadesFuncionais farmacia, AghMicrocomputador micro, 
			AghMicrocomputador microDispensador) throws BaseException;

	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtoPorItemPrescrito(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, Boolean b);

	public List<AfaDispensacaoMdtos> criarListaNovaMovimentacao();

	public String recuperaDescricaoMdtoPrescrito(
			MpmPrescricaoMedica prescricaoMedica,
			MpmItemPrescricaoMdto itemPrescrito);

	public DominioSituacaoItemPrescritoDispensacaoMdto verificarSituacaoItemPrescritoIncluidoFarmacia(
			AfaDispensacaoMdtos selecao);

	public void persistirMovimentoDispensacaoMdtos(
			MpmPrescricaoMedica prescricaoMedica,
			MpmItemPrescricaoMdto itemPrescrito,
			List<AfaDispensacaoMdtos> medicamentosDispensadosModificados,
			List<AfaDispensacaoMdtos> medicamentosDispensadosOriginal,
			String nomeMicrocomputador)
			throws BaseException;

	public Short getLocalDispensa2(Integer atendimentoSeq,
			Integer medMatCodigo, BigDecimal dose, Integer fdsSeq,
			Short farmPadrao) throws ApplicationBusinessException;

	public void dispensarMedicamentoByEtiquetaComCb(String nroEtiqueta, 
			List<AfaDispensacaoMdtos> listaMdtosPrescritos, 
			AghMicrocomputador microDispensador, String nomeMicrocomputador) throws BaseException;

	public void assinaDispensarMdtoQtdeTotalSemEtiqueta(
			AfaDispensacaoMdtos admNew, String nomeMicrocomputador)
			throws BaseException;

	public Boolean verificaDispensacaoAntesDeEvento(Integer pmeAtdSeq,
			Integer pmeSeq, AghUnidadesFuncionais unidadeFuncionalMicro);

	public List<DispensacaoFarmaciaVO> popularDispensacaoFarmaciaVO(
			MpmPrescricaoMedicaId prescricaoMedicaId,
			List<MpmPrescricaoMdto> listaPrescricaoMdto, Boolean alterado)
			throws BaseException;

	public List<MpmPrescricaoMdto> buscarPrescricaoMedicamentos(
			MpmPrescricaoMedicaId prescricaoMedicaId)
			throws ApplicationBusinessException;

	public List<MpmPrescricaoMdto> buscarPrescricaoMedicamentosConfirmados(
			MpmPrescricaoMedicaId prescricaoMedicaId)
			throws ApplicationBusinessException;

	public List<AghUnidadesFuncionais> listarFarmacias();

	public List<DispensacaoFarmaciaVO> ordenarDispensacaoFarmaciaVO(
			List<DispensacaoFarmaciaVO> lista);

	public void persisteDispMdtoCbSps(AfaDispMdtoCbSps dmc, String nomeMicrocomputador)
			throws ApplicationBusinessException;

	public List<AfaDispensacaoMdtos> pesquisarListaDispMdtoDispensarPelaPrescricao(
			Integer pmeAtdSeq, Integer pmeSeq,
			AghUnidadesFuncionais unidadeFuncionalMicro);

	public void processaAfaTipoOcorBySeqInAfaDispMdto(AfaDispensacaoMdtos adm)
			throws ApplicationBusinessException;

	public void processaAghUnfSeqInAfaDispMdto(AfaDispensacaoMdtos adm)
			throws ApplicationBusinessException;

	public void processaImagensSituacoesDispensacao(AfaDispensacaoMdtos adm);

	public Integer processaProximaPrescricaoTriagemMedicamentoByProntuario(
			Integer atdSeqPrescricao, Integer seqPrescricao,
			Boolean proximoRegistro);

	public List<MpmPrescricaoMedica> pesquisarAlterarDispensacaoDeMedicamentos(
			Integer firstResult, Integer maxResult, String orderProperty,
			Boolean asc, AinLeitos leito, Integer numeroPrescricao,
			Date dthrDataInicioValidade, Date dthrDataFimValidade,
			Integer numeroProntuario, AipPacientes paciente);

	public Long pesquisarAlterarDispensacaoDeMedicamentosCount(
			AinLeitos leito, Integer numeroPrescricao,
			Date dthrDataInicioValidade, Date dthrDataFimValidade,
			Integer numeroProntuario, AipPacientes paciente);

	public void processaCorSinaleiroPosAtualizacao(
			List<AfaDispensacaoMdtos> listaOcorrenciasMdtosDispensados);

	public AipPacientes obterPacienteComAtendimentoPorProntuarioOUCodigo(
			Integer numeroProntuario, Integer codigoPaciente)
			throws ApplicationBusinessException;

	public void processaMdtoSelecaoInMovTriagemDisp(AfaDispensacaoMdtos adm,
			List<AfaMedicamento> medicamentos)
			throws ApplicationBusinessException;

	public void processarQtdeMaterialDisponivelEstoque(AfaDispensacaoMdtos adm);

	public String validaSeMedicamentoVencidoByEtiqueta(String etiqueta)
			throws ApplicationBusinessException;

	public void validaSeMicroComputadorDispensador(
			AghUnidadesFuncionais unidadeFuncional, AfaDispensacaoMdtos admNew,
			AghMicrocomputador microUserDispensador)
			throws ApplicationBusinessException;

	public Object[] obterNomeUnidFuncComputadorDispMdtoCb(AghUnidadesFuncionais unidadeFuncionalMicro, 
			String nomeComputador) throws ApplicationBusinessException;

	// # 15278 - RELATÓRIO LISTA OCORRENCIA
	public List<ListaOcorrenciaVO> recuperarRelatorioListaOcorrencia(
			String unidade, String dtReferencia, String ocorrencia, String unidFarmacia, Boolean unidPsiquiatrica)
			throws ApplicationBusinessException;

	public void recuperarRelatorioListaOcorrenciaCount(String unidade,
			String dtReferencia, String ocorrencia, String unidFarmacia)
			throws ApplicationBusinessException;

	public Boolean verificarAcessoOcorrenciaDeTriagem(String nomeComputador);

	public AghUnidadesFuncionais getFarmaciaMicroComputador(
			AghMicrocomputador micro, 
			String computadorNomeRede) throws ApplicationBusinessException;

	/**
	 * Executa a consulta fazendo join de AfaDispensacaoMdtos com procedimentoHospInterno
	 */
	public List<AfaDispensacaoMdtoVO> obterDispensacaoMdtosPorAtendimentoEDataCriacaoEntreDataIntEDataAlta(Integer atdSeq, Date dataInt, Date dataAlta);
	
	public void refresh(List<AfaDispensacaoMdtos> listaDispensacao);

	String validarCodigoBarrasEtiqueta(String nroEtiqueta);

	Boolean verificarPermissaoDispensacaoMdtoSemEtiqueta(AfaDispensacaoMdtos dispMdto);

	List<AfaDispensacaoMdtos> recuperarListaDispensacaoMedicamentos(
			Date dataDeReferenciaInicio, Date dataDeReferenciaFim,
			AfaMedicamento medicamento, AipPacientes paciente,
			boolean objectAtachado, Long seqAfaDispSelecionadaCheckBox)
			throws ApplicationBusinessException;

	DominioSituacaoDispensacaoMdto[] pesquisarFiltroDispensacaoMdtos();
	

	Long pesquisarDispensacaoMdtosCount(MpmPrescricaoMedica prescricaoMedica, Short unfSeq, Long seqPrescricaoNaoEletronica);
	
	public List<AfaPrescricaoMedicamento> pesquisarPrescricaoMedicamentos(Integer prontuario, Date dtReferenciaMinima);
	
	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtoPorPrescricaoNaoEletronica(
			Long seq, Integer matCodigo);
	
	public void persistirAfaPrescricaoMedicamento(
			AfaPrescricaoMedicamento prescricaoMedicamento) throws ApplicationBusinessException;
	
	public void persistirAfaDispMdtoComPrescricaoNaoEletronica(
			Integer medMatCodigo, BigDecimal qtdeDispensada,
			String observacao, Long seqAfaPrescricaoMedicamento,
			Integer atdSeq, String nomeMicrocomputador, RapServidores servidorLogado,
			Short unfSeq, Short unfSeqSolicitante) throws ApplicationBusinessException, BaseException;

	public void atualizarAfaDispMdtoComPrescricaoNaoEletronica(AfaDispensacaoMdtos afaDispOld,
			BigDecimal qtdeDispensada, String observacao,
			String nomeMicrocomputador, RapServidores servidorLogado, Boolean edicaoDeDispensacaoComCB) throws ApplicationBusinessException, BaseException;

	public void dispensaMdtoComCBPrescricaoNaoEletronica(String nroEtiqueta,Integer atdSeq,
			String nomeMicrocomputador,	RapServidores servidorLogado, Short unfSeq, Short unfSeqSolicitante,
			Long seqAfaPrescricaoMedicamento) throws ApplicationBusinessException, BaseException;
	
	public void efetuarEstornoDispensacaoMdtoNaoEletronica(
			AfaDispensacaoMdtos dispensacaoSemCB, String nomeComputadorRede, RapServidores usuarioLogado) throws ApplicationBusinessException, BaseException;
	
	public String gerarTicketDispensacaoMdto(Integer prontuario, 
			String local, String paciente, 
			String prescricao_inicio, 
			String prescricao_fim, 
			Boolean dispencacaoComMdto, Boolean prescricaoEletronica,
			List<TicketMdtoDispensadoVO> listaMdto,
			String relatorioEmitidoPor) throws ApplicationBusinessException;			
	
	public void atualizarRegistroImpressao(
			List<TicketMdtoDispensadoVO> listaMdtoDispensado,
			RapServidores servidorLogado);
	
	public Date pesquisarMaxDataHrTicket(Integer atdSeqPrescricao,
			Integer seqPrescricao, AghUnidadesFuncionais unidadeFuncionalMicro,
			Long pmmSeq);
	
	public List<TicketMdtoDispensadoVO> pesquisarDispensacaoMdto(
			Integer atdSeqPrescricao, Integer seqPrescricao,
			AghUnidadesFuncionais unidadeFuncionalMicro, Long pmmSeq);

	Date recuperarDataLimite() throws ApplicationBusinessException;
	
	public ConsultaDispensacaoMedicamentosVO preencherVoDispensacaoMedicamentos(
			MpmPrescricaoMedica prescricaoMedica, Long seqPrescricaoNaoEletronica);
}