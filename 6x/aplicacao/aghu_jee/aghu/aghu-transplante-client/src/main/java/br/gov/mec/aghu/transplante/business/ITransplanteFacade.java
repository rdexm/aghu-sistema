package br.gov.mec.aghu.transplante.business;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioOrdenacRelatorioSitPacOrgao;
import br.gov.mec.aghu.dominio.DominioOrdenacRelatorioSitPacTmo;
import br.gov.mec.aghu.dominio.DominioRepeticaoRetorno;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.dominio.DominioTipoTransplante;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MtxColetaMaterialTmo;
import br.gov.mec.aghu.model.MtxComorbidade;
import br.gov.mec.aghu.model.MtxComorbidadePaciente;
import br.gov.mec.aghu.model.MtxContatoPacientes;
import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.model.MtxDoencaBases;
import br.gov.mec.aghu.model.MtxExameUltResults;
import br.gov.mec.aghu.model.MtxItemPeriodoRetorno;
import br.gov.mec.aghu.model.MtxMotivoAlteraSituacao;
import br.gov.mec.aghu.model.MtxOrigens;
import br.gov.mec.aghu.model.MtxPeriodoRetorno;
import br.gov.mec.aghu.model.MtxProcedimentoTransplantes;
import br.gov.mec.aghu.model.MtxRegistrosTMO;
import br.gov.mec.aghu.model.MtxResultadoExames;
import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.transplante.vo.AgendaTransplanteRetornoVO;
import br.gov.mec.aghu.transplante.vo.CriteriosPriorizacaoAtendVO;
import br.gov.mec.aghu.transplante.vo.ExtratoAlteracoesListaOrgaosVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoPermanenciaListVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoSobrevidaTransplanteVO;
import br.gov.mec.aghu.transplante.vo.FiltroTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.GerarExtratoListaTransplantesVO;
import br.gov.mec.aghu.transplante.vo.ListarTransplantesVO;
import br.gov.mec.aghu.transplante.vo.PacienteAguardandoTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplantadosOrgaoVO;
import br.gov.mec.aghu.transplante.vo.RelatorioTransplanteOrgaosSituacaoVO;
import br.gov.mec.aghu.transplante.vo.RelatorioTransplanteTmoSituacaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplanteMedulaOsseaVO;
import br.gov.mec.aghu.transplante.vo.RelatorioExtratoTransplantesPacienteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioPermanenciaPacienteListaTransplanteVO;
import br.gov.mec.aghu.transplante.vo.RelatorioSobrevidaPacienteTransplanteVO;
import br.gov.mec.aghu.transplante.vo.ResultadoExameCulturalVO;
import br.gov.mec.aghu.transplante.vo.TiposExamesPacienteVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.transplante.vo.TotalizadorAgendaTransplanteVO;

@SuppressWarnings({ "PMD.ExcessiveClassLength" })
public interface ITransplanteFacade extends Serializable {
	
	/**
	 * Obtem a lista dos contatos do paciente.
	 * 
	 * @param pacCodigo Código do Paciente 
	 * @return {@link List} de {@link MtxContatoPacientes}
	 */
	List<MtxContatoPacientes> obterListaContatoPacientesPorCodigoPaciente(Integer pacCodigo);

	/**
	 * Obtem a lista de Doença Base dado o orgão selecionado.
	 * 
	 * @param tipoOrgao Orgão selecionado
	 * @return {@link List} de {@link MtxDoencaBases}
	 */
	List<MtxDoencaBases> obterListaDoencaBasePorTipoOrgao(DominioTipoOrgao tipoOrgao);

	/**
	 * Persiste a entidade de Transplante, gravando as informações do registro sanguineo, e edita os Contatos do Paciente.
	 * 
	 * @param transplante {@link MtxTransplantes}
	 * @param regSanguineo {@link AipRegSanguineos}
	 * @param paciente {@link AipPacientes}
	 * @param listaContatosIncluidos {@link List} de {@link MtxContatoPacientes}
	 * @param listaContatosExcluidos {@link List} de {@link MtxContatoPacientes}
	 * @throws ApplicationBusinessException 
	 * @throws BaseListException
	 */
	void salvarTransplanteComManutencaoContatosRegSanguineo(MtxTransplantes transplante, AipRegSanguineos regSanguineo, 
			AipPacientes paciente, List<MtxContatoPacientes> listaContatosIncluidos, List<MtxContatoPacientes> listaContatosExcluidos) 
			throws ApplicationBusinessException, BaseListException;

	/**
	 * #41772
	 * @author marcelo.deus
	 * @param codTransplante
	 * @return
	 */
	AipPacientes buscarPacientePorCodTransplante(Integer codTransplante);
	MtxColetaMaterialTmo buscarColetaMaterialTMOPorTransplanteSeq(Integer codTransplante);
	void gravarColetaMaterialTMO(MtxColetaMaterialTmo coletaMaterialTmo);
	void atualizarColetaMaterialTMO(MtxColetaMaterialTmo coletaMaterialTmo);
	MtxTransplantes obterTransplantePorSeq(Integer codTransplante);
	String buscarValorCampoCD34(Integer codigo, Integer codMaterial);
	BigDecimal buscarValorLeucocitosTotais(Integer codigo);
	List<ResultadoExameCulturalVO> obterListaExameCultural(Integer codigo,	Integer codMaterial);
	void validarCamposObrigatoriosColetaMaterialTmo(MtxColetaMaterialTmo coletaMaterialTmo) throws ApplicationBusinessException, BaseListException;
		
	//41770
	public List<MtxOrigens> pesquisarMtxOrigensPorSeqCodDescricao(String pesquisa);
	public Long pesquisarMtxOrigensPorSeqCodDescricaoCount(String pesquisa);
	public String calcularIdadeIngresso(Date dtIngresso, Date dtNascimento);
	public Double obterEscore(Integer seqCid, Date dtIngresso, Date dtNascimento);
	public void persitirPacienteListaTransplanteTMO(MtxTransplantes transplante, AipRegSanguineos regSanguineos, AipPacientes paciente, MtxCriterioPriorizacaoTmo statusDoenca, boolean grupoSanguineoFatorAlterado) throws ApplicationBusinessException, BaseListException;
	//41770 FIM
	
	//#41768 - C2
	public List<CriteriosPriorizacaoAtendVO> pesquisarCriteriosPriorizacaoAtendimento(CriteriosPriorizacaoAtendVO filtro, 
			Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc);
	public Long pesquisarCriteriosPriorizacaoAtendimentoCount(CriteriosPriorizacaoAtendVO filtro);
	//#41768 - C1
	public List<AghCid> pesquisarCidPorSeqCodDescricao(String pesquisa);
	public Long pesquisarCidPorSeqCodDescricaoCount(String pesquisa);
	
	//#41768 - RN
	public boolean verificarExistenciaRegistro(Integer cidSeq);
	void gravarMtxCriterioPriorizacaoTmo(MtxCriterioPriorizacaoTmo obj) throws ApplicationBusinessException;
	
	void atualizarMtxCriterioPriorizacaoTmo(MtxCriterioPriorizacaoTmo obj)throws ApplicationBusinessException;
	public AghCid obterCidSeq(Integer seq);
	
	public void validarCamposObrigatorios(CriteriosPriorizacaoAtendVO filtro) throws ApplicationBusinessException, BaseListException;
	
	public List<ListarTransplantesVO> obterPacientesAguardandoTransplantePorFiltro(
			ListarTransplantesVO filtro, Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) throws ApplicationBusinessException;

	public Long obterPacientesAguardandoTransplantePorFiltroCount(
			ListarTransplantesVO filtro);

	//#46495
	public List<MtxCriterioPriorizacaoTmo> obterStatusDoencaPaciente(DominioSituacaoTmo tipo);
	public MtxCriterioPriorizacaoTmo obterCoeficiente(Integer statusDoenca);


	//#46358
	List<MtxDoencaBases> obterListaDoencasBase(Integer firstResult,	Integer maxResults, String orderProperty, 
											   boolean asc, MtxDoencaBases mtxDoencaBases);

	Long obterListaDoencasBaseCount(MtxDoencaBases mtxDoencaBases);
	public boolean excluirDoencaBase(MtxDoencaBases mtxDoencaBase);
	public boolean inserirDoencaBase(MtxDoencaBases mtxDoencaBase);
	public boolean atualizarDoencaBase(MtxDoencaBases mtxDoencaBase);
	
	//#46359
	public List<MtxOrigens> pesquisarOrigemPaciente(MtxOrigens mtxOrigens, Integer firstResult, Integer maxResults, String orderProperty, boolean asc);
	public void gravarAtualizarOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException;
	public void excluirOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException;
	public Long pesquisarOrigemPacienteCount(MtxOrigens mtxOrigens, boolean isEqual);
	public void validarInclusaoOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException;
	public void validarExclusaoOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException;
	
	//#46378
	public List<MtxMotivoAlteraSituacao> obterListaMotivoAlteraSituacao(Integer firstResult, Integer maxResults, String orderProperty,
																			boolean asc, MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao);
	public Long obterListaMotivoAlteraSituacaoCount(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao);
	public void inserirMotivoAlteraSituacao(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao);
	public void atualizarMotivoAlteraSituacao(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao);
	public boolean excluirMotivoAlteraSituacao(MtxMotivoAlteraSituacao mtxMotivoAlteraSituacao);
	
	public void inserirMtxExameUltResults(MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException;
	public void atualizarMtxExameUltResults(MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException;
	public void excluirMtxExameUltResults(MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException;
	public List<MtxExameUltResults> pesquisarExamesLaudosCampo(String exameSigla, Integer laudoSeq, DominioSituacao situacao, 
			   												   Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	public Long pesquisarExamesLaudosCampoCount(String exameSigla, Integer laudoSeq, DominioSituacao situacao);
	
	//#46203
	public List<MtxComorbidade> pesquisarComorbidade(MtxComorbidade mtxComorbidade, Integer firstResult, Integer maxResults, String orderProperty, boolean asc);
	public Long pesquisarComorbidadeCount(MtxComorbidade mtxComorbidade);
	public void gravarAtualizarComorbidade(MtxComorbidade mtxComorbidade) throws ApplicationBusinessException, BaseException;
	public void validarInclusaoComorbidade(MtxComorbidade mtxComorbidade) throws ApplicationBusinessException, BaseException;
    public Long pesquisarDoencaCount(MtxComorbidade mtxComorbidade);
    public List<MtxComorbidade> pesquisarDoenca(MtxComorbidade mtxComorbidade);
    public void validarInputOrigemPaciente(MtxOrigens mtxOrigens) throws ApplicationBusinessException, BaseException;

    //#41790
  	public List<RelatorioPermanenciaPacienteListaTransplanteVO> gerarRelatorioPermanenciaPacienteListaTransplante(FiltroTempoPermanenciaListVO filtro);
  	public void validarRegrasTelaRelatorioFilaTransplante(FiltroTempoPermanenciaListVO filtro) throws BaseListException;
  	public boolean verificaTipoTMOFilaTransplante(FiltroTempoPermanenciaListVO filtro);
  	public boolean verificaTipoTransplanteRelatorioFila(FiltroTempoPermanenciaListVO filtro);
  	public String gerarRelatorioTempoPermanenciaCSV(List<RelatorioPermanenciaPacienteListaTransplanteVO> colecao) throws BaseException, IOException;
  	
  	//#41792
  	public List<RelatorioSobrevidaPacienteTransplanteVO> gerarRelatorioSobrevidaTransplante(FiltroTempoSobrevidaTransplanteVO filtro);
  	public String gerarRelatorioSobrevidaTransplanteCSV(List<RelatorioSobrevidaPacienteTransplanteVO> colecao) throws BaseException, IOException;
  	public void validarRegrasTelaRelatorioSobrevidaTransplante(FiltroTempoSobrevidaTransplanteVO filtro) throws BaseListException;
  	public String gerarRelatorioSobrevidaCSV(List<RelatorioSobrevidaPacienteTransplanteVO> colecao) throws BaseException, IOException;

  	//#49361
	public List<RelatorioExtratoTransplantesPacienteVO> pesquisarExtratoTransplantePorFiltros(
			Integer pacCodigo, FiltroTempoPermanenciaListVO filtro);

	//#49361
	public String gerarRelatorioExtratoTransplantePacienteCSV(
			List<RelatorioExtratoTransplantesPacienteVO> colecao)
			throws BaseException, IOException;

	//#49361
	public List<RelatorioExtratoTransplantesPacienteVO> formatarExtratoPacienteTransplante(
			List<RelatorioExtratoTransplantesPacienteVO> colecao);

	//#49361
		public String validarDatas(FiltroTempoPermanenciaListVO filtro, int numeroMaximo);
		
	//49657
	List<MtxProcedimentoTransplantes> obterProcedimentosAssociados(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, MtxProcedimentoTransplantes mtxProcedimentoTransplantes, String procedimento);
	
	List<MbcProcedimentoCirurgicos> obterListaProcedimentoTransplantes(String procedimento);
	
	Long obterListaProcedimentoTransplantesCount(String procedimento);
	
	Long pesquisarListaProcedimentoTransplantesCount(MtxProcedimentoTransplantes mtxProcedimentoTransplantes, String procedimento);

	List<MtxProcedimentoTransplantes> verificarMtxProcedimentoTransplantes(
			MtxProcedimentoTransplantes mtxProcedimentoTransplantes);

	void editarProcedimentoTransplantes(
			MtxProcedimentoTransplantes mtxProcedimentoTransplantes)
			throws ApplicationBusinessException;

	void adicionarProcedimentoTransplantes(
			MtxProcedimentoTransplantes mtxProcedimentoTransplantes)
			throws ApplicationBusinessException;

	void excluirProcedimentoTransplantes(
			MtxProcedimentoTransplantes mtxProcedimentoTransplantes)
			throws ApplicationBusinessException;
	//#49356
	public List<RelatorioExtratoTransplantesPacienteVO> pesquisarPacienteComObtitoListaTranplante(
				FiltroTempoPermanenciaListVO filtro, Integer masSeq);

	//#49356
	public String gerarRelatorioPacientesComObitoListaEsperaTranplenteCSV(
			List<RelatorioExtratoTransplantesPacienteVO> colecao)
			throws BaseException, IOException;

	//#49356
	public List<RelatorioExtratoTransplantesPacienteVO> removerRepetidosLista(
			List<RelatorioExtratoTransplantesPacienteVO> listaRelatorio);
			
	List<ListarTransplantesVO> obterPacientesTransplantadosPorFiltro(ListarTransplantesVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc) throws ApplicationBusinessException;
	Long obterPacientesTransplantadosPorFiltroCount(ListarTransplantesVO filtro);
	String obterIdadeFormatada(Date dataNascimento);
	public void validarTipoTransplante(ListarTransplantesVO filtro) throws ApplicationBusinessException;

	public MtxTransplantes obterTransplanteEdicao(Integer transplanteSeq);
	
	//#41798
	public List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesAguardandoTransplanteOrgao(FiltroTransplanteOrgaoVO filtro, 
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc, boolean paginacao);

	public void verificarDoencasPacientesON(List<PacienteAguardandoTransplanteOrgaoVO> listaRetorno, boolean isCount) throws CloneNotSupportedException, ApplicationBusinessException;
	
	Boolean verificarExisteResultadoExame(Integer pacCodigo);
	
	//#44801
	public List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesRetiradosOrgao(FiltroTransplanteOrgaoVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc, boolean paginacao)  throws ApplicationBusinessException, CloneNotSupportedException ;
	//#41720
	public List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesInativosAguardandoTransplanteOrgao(FiltroTransplanteOrgaoVO filtro, 
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc, boolean paginacao);

	public void verificarDoencasPacientesInativoON(List<PacienteAguardandoTransplanteOrgaoVO> listaRetorno) throws CloneNotSupportedException, ApplicationBusinessException;
	
	//#46771
	public List<GerarExtratoListaTransplantesVO> consultarListagemExtratoTransplante(Integer trpSeq);
	
public List<ExtratoAlteracoesListaOrgaosVO> obterExtratoAlteracoesListaOrgaos(Integer trpSeq);
	
	//#44807
	public List<PacienteTransplantadosOrgaoVO> obterListaPacientesTransplantadosOrgao(FiltroTransplanteOrgaoVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc);
	public Long obterListaPacientesTransplantadosOrgaoCount(FiltroTransplanteOrgaoVO filtro);
	
	/**
	 * #47143 - INICIO
	 */
	public AipPacientes obterDadosPaciente(Integer seqTransplante);
		
	public TiposExamesPacienteVO obterTiposExamesPaciente(AipPacientes paciente, MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException;
		
	public void atualizarResultadoExames(MtxResultadoExames resultadoExames);
		
	public void excluirResultadoExames(MtxResultadoExames resultadoExames) throws ApplicationBusinessException;
		
	public void salvarResultadoExames(MtxResultadoExames resultadoExames, Integer seqTransplante) throws ApplicationBusinessException;
		
	public Boolean verificarHcvReagente(Integer seqTransplante, AipPacientes paciente, MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException;
		
	public List<MtxResultadoExames> obterResultadoExames(Integer seqTransplante, AipPacientes paciente);

	List<TiposExamesPacienteVO> buscaUltimosResultados(AipPacientes paciente);
	/**  * #47143 - FIM  */
	
	//#41787
	public boolean mudarStatusPacienteTMO(Integer trpSeq, DominioSituacaoTransplante situacao, Integer pacCodigo, Integer masSeq) throws ApplicationBusinessException;
	public ListarTransplantesVO obterPacientePorCodTransplante(Integer codTransplante);
	public List<MtxMotivoAlteraSituacao> listarMotivosAlteracaoSituacao();
	//#41779
	public PacienteAguardandoTransplanteOrgaoVO obterPacientePorCodTransplanteRins(Integer codTransplante);
	public boolean mudarStatusPacienteRins(Integer trpSeq, DominioSituacaoTransplante situacao, Integer masSeq) throws ApplicationBusinessException;
	
	//#50188
	public void gravarAtualizarTipoRetorno(MtxTipoRetorno mtxTipoRetorno) throws ApplicationBusinessException, BaseListException;
	public void validarTipoRetorno(MtxTipoRetorno mtxTipoRetorno) throws ApplicationBusinessException, BaseListException;
	public List<MtxTipoRetorno> pesquisarTipoRetorno(MtxTipoRetorno mtxTipoRetorno);
	
	//#49925
	List<MtxItemPeriodoRetorno> pesquisarItemPeriodoRetorno(DominioTipoRetorno tipoRetorno, Integer seqPeriodoRetorno, String pesquisaSuggestion);
	List<AghEspecialidades> obterEspecialidadesAtivas();
	List<AgendaTransplanteRetornoVO> obterAgendaTransplanteComPrevisaoRetorno(Integer codPaciente, List<AghEspecialidades> listaEspecialidade, DominioTipoRetorno tipoRetorno, MtxItemPeriodoRetorno descricaoTipoRetorno);
	List<TotalizadorAgendaTransplanteVO> obterTotalConsultasPorDia(List<AgendaTransplanteRetornoVO> listaAgenda, MtxItemPeriodoRetorno descricaoTipoRetorno);
	void atualizarObservacaoTransplante(MtxTransplantes observacaoTransplante); 

	//#49923
	public List<MtxPeriodoRetorno> consultarPeriodoRetorno (MtxPeriodoRetorno mtxPeriodoRetorno, DominioRepeticaoRetorno repeticao, MtxPeriodoRetorno selecionado);
	public List<MtxTipoRetorno> listarPeriodoRetorno (DominioTipoRetorno indTipo, String descricao);
	public void gravarRegistroPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno, MtxTipoRetorno selecionaDescricao, DominioSimNao dominioSimNao,List<MtxItemPeriodoRetorno> listaItem);	
	public void editarRegistroPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno, MtxTipoRetorno selecionaDescricao, DominioSimNao dominioSimNao,List<MtxItemPeriodoRetorno> listaItem, List<MtxItemPeriodoRetorno> listaItemExcluir);
	public List<MtxItemPeriodoRetorno> consultarItensPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno);
	public Long listarPeriodoRetornoCount(DominioTipoRetorno indTipo, String param);
	//48373
	public List<RelatorioTransplanteOrgaosSituacaoVO> pesquisarTransplante(DominioTipoOrgao dominioTipoOrgao, Integer prontuario, Date dataInicial, Date dataFinal,
			List<DominioSituacaoTransplante> listaDominioSituacaoTransplanteSelecionados, DominioOrdenacRelatorioSitPacOrgao ordenacao, DominioSexo sexo);
	public String gerarCSVRelatorioTransplanteOrgaosSituacao(String nomeHospital,
			List<RelatorioTransplanteOrgaosSituacaoVO> listaRelatorioTransplanteOrgaosSituacaoVO) throws IOException;
	public void validarData(Date dataInicial, Date dataFinal) throws ApplicationBusinessException;
	public void validarListaRelatorioTransplanteOrgaosSituacaoVO(List<RelatorioTransplanteOrgaosSituacaoVO> listaRelatorioTransplanteOrgaosSituacaoVO)
			throws ApplicationBusinessException;
	public List<RelatorioTransplanteTmoSituacaoVO> pesquisarTransplante(DominioSituacaoTmo situacaoTmo, DominioTipoAlogenico tipoAlogenico, Integer prontuario,
			Date dataInicial, Date dataFinal, List<DominioSituacaoTransplante> listaDominioSituacaoTransplanteSelecionados, DominioOrdenacRelatorioSitPacTmo ordenacao);
	public void validarListaRelatorioTransplanteTmoSituacaoVO(List<RelatorioTransplanteTmoSituacaoVO> listaRelatorioTransplanteTmoSituacaoVO)
			throws ApplicationBusinessException;
	public String gerarCSVRelatorioTransplanteTmoSituacao(String nomeHospital, List<RelatorioTransplanteTmoSituacaoVO> listaRelatorioTransplanteTmoSituacaoVO)
			throws IOException;
	public void registarTransplantes(List<MbcProcedimentoCirurgicos> listaProcedimentos, Integer codPaciente) throws ApplicationBusinessException;
	//#49346
	public PacienteTransplanteMedulaOsseaVO carregarDadosPaciente(Integer prontuario, Object tipo);
	public List<MtxComorbidade> pesquisarComorbidadePorTipoDescricaoCid(MtxComorbidade mtxComorbidade);
	public Long pesquisarComorbidadePorTipoDescricaoCidCount(MtxComorbidade mtxComorbidade);
	public List<MtxComorbidadePaciente> carregarComorbidadesPaciente(DominioTipoTransplante tipo, AipPacientes aipPacientes);
	public void gravarComorbidadePaciente(MtxComorbidadePaciente mtxComorbidadePaciente) throws ApplicationBusinessException, BaseException;
	public void excluirComorbidadePaciente(MtxComorbidadePaciente mtxComorbidadePaciente)throws ApplicationBusinessException, BaseException;
	public boolean validarGravarComorbidadePaciente(List<MtxComorbidade> listaComorbidades,List<MtxComorbidade> listaComorbidadesExcluidas);
	public List<MtxComorbidade> concatenarCID(List<MtxComorbidade> listaComorbidade);
	public List<MtxComorbidade> removerComorbidadePacienteJaInseridas(List<MtxComorbidade> listaComorbidades, AipPacientes aipPacientes);
	
	//#50081
	public List<MtxTransplantes> obtemTransplantados(DominioSituacaoTmo tipoTmo, Integer codPacienteReceptor);
	
	public void persistirMtxRegistrosTMO(MtxRegistrosTMO mtxRegistrosTMO) throws ApplicationBusinessException;
	
	public void atualizarMtxRegistrosTMO(MtxRegistrosTMO mtxRegistrosTMO) throws ApplicationBusinessException;
	
	public MtxRegistrosTMO obterRegistroTransplantePorTransplante(Integer transplanteSeq);

	
}