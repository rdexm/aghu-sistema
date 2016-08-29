package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.constantes.TipoItemAprazamento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeCuidadoUnf;
import br.gov.mec.aghu.model.EpeCuidadoUnfId;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpeDataItemSumario;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.model.EpeUnidPacAtendimento;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoMedicamentoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoCuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.EpePrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.EtiologiaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PacienteEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelSumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SinalSintomaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SumarioPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.vo.ListaPacientePrescricaoVO;

public interface IPrescricaoEnfermagemFacade extends Serializable {
	
	
	void agendarGerarDadosSumarioPrescricaoEnfermagem(String cron, Date dataInicio, Date dataFim) throws ApplicationBusinessException;


	
	List<PacienteEnfermagemVO> listarPacientes(
			final RapServidores servidor) throws BaseException;

	Boolean verificarCuidadoJaExistePrescricao(Short cuiSeq,
			EpePrescricaoEnfermagemId penId);

	void confirmarPrescricaoEnfermagem(
			EpePrescricaoEnfermagem prescricaoEnfermagem)
			throws BaseException;

	public abstract void confirmarPendentePrescricaoEnfermagem(
			EpePrescricaoEnfermagem prescricaoEnfermagem)
			throws BaseException;

	public abstract EpePrescricaoEnfermagem clonarPrescricaoEnfermagem(
			EpePrescricaoEnfermagem prescricaoEnfermagem)
			throws ApplicationBusinessException;

	public abstract EpePrescricoesCuidados alterarPrescricaoCuidado(
			EpePrescricoesCuidados prescricaoCuidadoNew, String descricao, boolean cuidadosAnterioresComErro)
			throws BaseException;

	public abstract void inserirPrescricaoCuidado(
			EpePrescricoesCuidados prescricaoCuidado, String descricao) throws BaseException;

	public abstract void inserirPrescCuidDiagnostico(
			EpePrescricoesCuidados prescricaoCuidado, CuidadoVO cuidadoVO);

	public abstract List<AghAtendimentos> obterAtendimentoAtualPorProntuario(
			Integer prontuario);

	
	public abstract AghAtendimentos obterUnicoAtendimentoAtualPorProntuario(
			Integer prontuario);

	public abstract AghAtendimentos obterAtendimentoPorLeito(String param)
			throws ApplicationBusinessException;

	public abstract List<EpePrescricaoEnfermagem> pesquisarPrescricaoEnfermagemNaoEncerradaPorAtendimento(
			Integer atdSeq, Date dataAtual);

	public abstract Date obterDataReferenciaProximaPrescricao(
			AghAtendimentos atendimento, Date dataAtual) throws ApplicationBusinessException;

	public abstract void editarPrescricao(
			EpePrescricaoEnfermagem prescricaoEnfermagemOld,
			EpePrescricaoEnfermagem prescricaoEnfermagemNew, Boolean cienteEmUso)
			throws BaseException;

	public abstract void atualizarPrescricao(
			EpePrescricaoEnfermagem prescricaoEnfermagemOld,
			EpePrescricaoEnfermagem prescricaoEnfermagemNew, boolean flush)
			throws BaseException;

	public abstract void verificarCriarPrescricao(
			final AghAtendimentos atendimento)
			throws ApplicationBusinessException;

	public abstract EpePrescricaoEnfermagem criarPrescricao(
			final AghAtendimentos atendimento, Date dataReferencia)
			throws BaseException;

	public abstract PrescricaoEnfermagemVO buscarDadosCabecalhoPrescricaoEnfermagemVO(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId)
			throws ApplicationBusinessException;
	
	public abstract PrescricaoEnfermagemVO buscarDadosCabecalhoPrescricaoEnfermagemUtilizadoPrescricaoMedicaVO(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId)
			throws ApplicationBusinessException;

	public abstract EpePrescricaoEnfermagem obterPrescricaoEnfermagemPorId(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId);

	public abstract PrescricaoEnfermagemVO popularDadosCabecalhoPrescricaoEnfermagemVO(
			EpePrescricaoEnfermagem prescricaoEnfermagem)
			throws ApplicationBusinessException;

	public abstract List<CuidadoVO> buscarCuidadosPrescricaoEnfermagem(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId,
			Boolean listarTodas);

	public abstract void removerCuidadosSelecionados(
			List<CuidadoVO> listaCuidadoVO) throws ApplicationBusinessException;

	List<EpePrescricaoEnfermagemVO> pesquisarPrescricaoEnfermagemPorAtendimentoEmergencia(Integer atdSeq);
	
	public abstract List<SinalSintomaVO> pesquisarSinaisSintomasPorGrupoSubgrupoEDescricao(
			Short dgnSnbGnbSeq, Short dgnSnbSequencia, String descricao);

	public abstract List<DiagnosticoVO> pesquisarDiagnosticoPorSinalSintoma(
			Integer codigo);

	public abstract List<DiagnosticoVO> pesquisarDiagnosticos(
			Short dgnSnbGnbSeq, Short dgnSnbSequencia, String dgnDescricao,
			Short dgnSequencia);

	public abstract List<EtiologiaVO> pesquisarEtiologiaPorDiagnostico(
			Short snbGnbSeq, Short snbSequencia, Short sequencia);

	public abstract List<EpePrescricoesCuidados> pesquisarCuidadosAtivosAtribuidos(
			Integer penSeq, Integer penAtdSeq, Date dthrFim);

	public abstract List<EpeGrupoNecesBasica> pesquisarGruposNecesBasica(
			Object filtro);

	public abstract List<EpeSubgrupoNecesBasica> pesquisarSubgruposNecesBasica(
			Object filtro, Short gnbSeq);

	public abstract List<EpeDiagnostico> pesquisarDiagnosticos(Object filtro,
			Short gnbSeq, Short snbSequencia);

	public abstract List<EpeFatRelacionado> pesquisarEtiologias(Object filtro,
			Short gnbSeq, Short snbSequencia, Short sequencia);

	public abstract List<String> gerarAprazamentoPrescricaoEnfermagem(
			EpePrescricaoEnfermagem prescricaoEnfermagem, Date dthrInicioItem,
			Date dthrFimItem,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			TipoItemAprazamento tipoItem, Date dtHrInicioTratamento,
			Boolean indNecessario, Short frequencia);

	public abstract List<PrescricaoEnfermagemVO> obterRelatorioPrescricaoEnfermagem(
			PrescricaoEnfermagemVO prescricaoEnfermagemVO)
			throws BaseException;

	public abstract void verificaListaCuidadosVO(
			PrescricaoEnfermagemVO prescricaoEnfermagemVO)
			throws BaseException;

	public abstract void cancelaPrescricaoEnfermagem(Integer penSeqAtendimento,
			Integer penSeq) throws ApplicationBusinessException;

	public abstract List<EpeCuidados> pesquisarCuidadosAtivosPorGrupoDiagnosticoEtiologiaUnica(
			Short fdgDgnSnbGnbSeq, Short fdgDgnSnbSequencia,
			Short fdgDgnSequencia, Short fdgFreSeq);
	
	List<EpeCuidados> listarCuidadosEnfermagem(Short codigo, String descricao, Boolean indCci,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	Long listarCuidadosEnfermagemCount(Short codigo, String descricao, Boolean indCci);
	
	void atualizarEpeCuidados(EpeCuidados epeCuidados);

	public abstract EpeFatRelacionado obterDescricaoEtiologiaPorSeq(
			Short fdgFreSqe);

	public abstract List<DiagnosticoEtiologiaVO> listarPrescCuidDiagnosticoPorAtdSeqDataInicioDataFim(
			Integer atdSeq, Date dthrInicio, Date dthrFim, Date dthrMovimento);

	public abstract void removerPrescCuidadosDiagnosticosSelecionados(
			List<DiagnosticoEtiologiaVO> listaDiagnosticoEtiologiaVO,
			Integer penAtdSeq, Integer penSeq)
			throws ApplicationBusinessException;

	
	public abstract List<ItemPrescricaoEnfermagemVO> buscarItensPrescricaoEnfermagem(
			EpePrescricaoEnfermagemId prescricaoId, Boolean listarTodos)
			throws ApplicationBusinessException;
	
	public List<EpePrescricaoEnfermagem> pesquisarPrescricaoEnfermagemPorAtendimentoEDataFimAteDataAtual(
			final Integer atdSeq, final Date dataFim);

	
	//(TransactionPropagationType.REQUIRED)
	public abstract void geraDadosSumarioPrescricaoEnfermagem(
			Integer seqAtendimento, DominioTipoEmissaoSumario tipoEmissao)
			throws ApplicationBusinessException;


	
	public abstract List<EpeCuidados> pesquisarCuidadosEnfermagem(
			String parametro);

	public abstract boolean existeComunicadoUlceraPressaoPorAtendimento(
			Integer atdSeq);

	public List<RelSumarioPrescricaoEnfermagemVO> pesquisaGrupoDescricaoStatus(
			SumarioPrescricaoEnfermagemVO prescricao, boolean limitValor);
	
	public List<EpeDataItemSumario> listarDataItemSumario(Integer apaAtdSeq);
	
	public List<EpePrescricaoEnfermagem> listarPrescricoesParaGerarSumarioDePrescricaoEnfermagem(Integer seqAtendimento, Date dthrInicioPrescricao, Date dthrFimPrescricao);
	
	public List<Date> executarCursorEpe(Integer atdSeq);

	List<EpeUnidPacAtendimento> obterEpeUnidPacAtendimentoPorApaAtdSeqApaSeq(Integer apaAtdSeq, Integer apaSeq);

	List<EpeGrupoNecesBasica> pesquisarGrupoNecesBasicaAtivoOrderSeq(Object filtro);

	Long pesquisarGrupoNecesBasicaAtivoOrderSeqCount(Object filtro);

	List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasicaAtivoOrderSeq(Object filtro, Short gnbSeq);

	Long pesquisarSubgrupoNecessBasicaAtivoOrderSeqCount(Object filtro, Short gnbSeq);

	Long pesquisarDiagnosticosCount(Short gnbSeq, Short snbSequencia);

	List<EpeDiagnostico> pesquisarDiagnosticos(Short gnbSeq,
			Short snbSequencia, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	EpeDiagnostico obterDiagnosticoPorChavePrimaria(EpeDiagnosticoId id);

	EpeSubgrupoNecesBasica obterEpeSubgrupoNecesBasicaPorChavePrimaria(
			EpeSubgrupoNecesBasicaId id);

	EpeGrupoNecesBasica obterEpeGrupoNecesBasica(Short seq);

	public abstract EpeGrupoNecesBasica obterDescricaoGrupoNecessidadesHumanasPorSeq(
			Short seqGrupoNecessidadesHumanas);
	
	public List<EpeCuidadoUnf> pesquisarEpeCuidadoUnf(Short seq);
	
	void validarCuidadoRotina(Boolean indRotina, List<EpeCuidadoUnf> listaCuidadoUnidades) throws ApplicationBusinessException;
	
	public List<EpeCuidados> pesquisarEpeCuidadosPorCodigoDescricao(Short seq, String descricao, int firstResult, int maxResults, 
				String orderProperty, Boolean asc, Boolean orderBy);

	Long pesquisarEpeCuidadosPorCodigoDescricaoCount(Short seq, String descricao);
	
	String alterarSituacao(EpeCuidadoUnf epeCuidadoUnf);
	
	public EpeCuidados obterEpeCuidadosPorSeq(Short seq);

	public List<EpeCuidadoUnf> obterEpeCuidadoUnfPorEpeCuidadoSeq(
			Short seqEpeCuidado);

	public abstract void restaurarEpeCuidadoUnf(
			List<EpeCuidadoUnf> listaCuidadoUnidades);

	public abstract String gravarEpeCuidadoUnfs(EpeCuidadoUnf epeCuidadoUnf, EpeCuidados epeCuidado, AghUnidadesFuncionais aghUnidadeFuncional) throws ApplicationBusinessException;

	public abstract void removerEpeCuidadoUnf(EpeCuidadoUnfId id) throws ApplicationBusinessException;
	
	public List<EpeGrupoNecesBasica> pesquisarGruposNecesBasicaTodos(Object filtro);
	
	public Long pesquisarGruposNecesBasicaTodosCount(Object filtro);
	
	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasicaTodos(Object filtro, Short gnbSeq);
	
	public Long pesquisarSubgrupoNecessBasicaTodosCount(Object filtro, Short gnbSeq);
	
	public List<EpeDiagnostico> pesquisarDiagnosticosTodos(Object filtro, Short gnbSeq, Short snbSequencia);
	
	public Long pesquisarDiagnosticosTodosCount(Object filtro, Short gnbSeq, Short snbSequencia);
	
	public List<DiagnosticoCuidadoVO> pesquisarDiagnosticosLista(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia, Short freSeq, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	public Long pesquisarDiagnosticosListaCount(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia, Short freSeq);
	
	public List<EpeCuidados> pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticos(String parametro, Short dgnSnbGnbSeq, Short dgnSnbSequencia,
			Short dgnSequencia, Short freSeq);
	
	public Long pesquisarCuidadosAtivosPorSeqOuDescricaoNaoAtribuidosDiagnosticosCount(String parametro, Short dgnSnbGnbSeq, Short dgnSnbSequencia,
			Short dgnSequencia, Short freSeq);
	
	public List<EpeCuidadoDiagnostico> obterEpeCuidadoDiagnosticoPorEpeCuidadoSeq(Short cuidadoSeq);
	
	public List<EpeCuidadoDiagnostico> pesquisarCuidadosDiagnosticos(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc);
	
	public Long pesquisarCuidadosDiagnosticosCount(Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia, Short freSeq);
	
	public List<EpeCuidados> pesquisarEpeCuidadosPorCodigo(Short seq);

	public String gravar(EpeCuidados epeCuidados,
			Short seqEpeCuidado) throws ApplicationBusinessException ;
	
	public abstract String excluirEpeCuidados(Short seq) throws ApplicationBusinessException, BaseListException,
			ApplicationBusinessException;

	List<EpeCuidados> pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentos(String parametro, Integer medMatCodigo);
	
	Long pesquisarCuidadosAtivosPorMatCodigoOuDescricaoNaoAtribuidosMedicamentosCount(String parametro, Integer medMatCodigo);

	List<CuidadoMedicamentoVO> pesquisarCuidadosMedicamentos(Integer matCodigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	Long pesquisarCuidadosMedicamentosCount(Integer matCodigo);
	

	public List<EpeDiagnostico> pesquisarDiagnosticosPorGrpSgrpDiag(
			Short snbGnbSeq, Short snbSequencia, Short dgnSequencia,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);

	public Long pesquisarDiagnosticosPorGrpSgrpDiagCount(
			Short snbGnbSeq, Short snbSequencia, Short dgnSequencia);

	public abstract Long pesquisarEtiologiasDiagnosticosCount(
			Short snbGnbSeq, Short snbSequencia, Short sequencia);

	public abstract List<EpeFatRelDiagnostico> pesquisarEtiologiasDiagnosticos(
			Short snbGnbSeq, Short snbSequencia, Short sequencia,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc);
	
	
	/**
	 * #34383  - Busca prescrição enfermagem de um atendimento
	 * @param atdSeq
	 * @return
	 */
	List<EpePrescricaoEnfermagem> pesquisarPrescricaoEnfermagemPorAtendimento(
			Integer atdSeq);
	
	List<EpeCuidados> pesquisarCuidadosdeRotinasAtivos(Short unidadeF);

	List<CuidadoVO> pesquisarCuidadosPrescEnfermagem(Integer penSeq, Integer penAtdSeq, Date dthrFim);
	
	public List<ListaPacientePrescricaoVO> retornarListaImpressaoPrescricaoEnfermagem(List<PacienteEnfermagemVO> listaPacientesEnfermagem);
	
	public abstract void validarDataPrescricao(final Date dtPrescricao, final List<EpePrescricaoEnfermagem> listaPrescricaoEnfermagem) throws ApplicationBusinessException;
	
	AghAtendimentos obterUltimoAtendimentoEmAndamentoPorPaciente(Integer pacCodigo, String nome) throws ApplicationBusinessException;
}