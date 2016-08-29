package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.vo.InformacaoEnviadaPrescribenteVO;
import br.gov.mec.aghu.farmacia.vo.InformacoesPacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.farmacia.vo.MpmPrescricaoMedVO;
import br.gov.mec.aghu.farmacia.vo.PacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.farmacia.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.internacao.vo.AghUnidadesFuncionaisVO;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamento;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosId;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalente;
import br.gov.mec.aghu.model.AfaSinonimoMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoId;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AfaViaAdmUnf;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;

public interface IFarmaciaApoioFacade extends Serializable {

	/*
	 * FormaDosagem
	 */
	public abstract void efetuarInclusao(AfaFormaDosagem entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract void efetuarAlteracao(AfaFormaDosagem entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract void efetuarRemocao(AfaFormaDosagem entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract List<String> obterRazoesExcessaoFormaDosagem();

	public abstract List<AfaFormaDosagem> listaFormaDosagemMedicamento(
			Integer matCodigo);

	public abstract List<AfaFormaDosagem> recuperarListaPaginadaFormaDosagem(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	public abstract Long recuperarCountFormaDosagem(
			AfaMedicamento medicamento);

	/*
	 * ViaAdministracaoMedicamento
	 */
	public abstract void efetuarInclusao(AfaViaAdministracaoMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract void efetuarAlteracao(
			AfaViaAdministracaoMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract void efetuarRemocao(AfaViaAdministracaoMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract List<String> obterRazoesExcessaoViaAdministracaoMedicamento();

	public abstract List<AfaViaAdministracaoMedicamento> recuperarListaPaginadaViaAdministracaoMedicamento(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	public abstract Long recuperarCountViaAdministracaoMedicamento(
			AfaMedicamento medicamento);

	public abstract List<AfaGrupoUsoMedicamento> pesquisarGrupoUsoMedicamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaGrupoUsoMedicamento elemento);

	public abstract Long pesquisarGrupoUsoMedicamentoCount(
			AfaGrupoUsoMedicamento elemento);

	public abstract AfaGrupoUsoMedicamento obterGrupoUsoMedicamentoPorChavePrimaria(
			Integer chavePrimaria);

	public abstract AfaGrupoUsoMedicamento inserirAtualizar(
			AfaGrupoUsoMedicamento elemento) throws ApplicationBusinessException;

	public abstract void removerGrupoUsoMedicamento(
			Integer seqAfaGrupoUsoMedicamento) throws BaseException;

	public abstract void persistirOcorrencia(AfaTipoOcorDispensacao ocorrencia)
			throws ApplicationBusinessException;

	public abstract void removerOcorrencia(AfaTipoOcorDispensacao ocorrencia)
			throws ApplicationBusinessException;

	// MEDICAMENTO EQUIVALENTE
	public abstract List<AfaMedicamentoEquivalente> recuperarListaPaginadaMedicamentoEquivalente(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	public abstract Long recuperarCountMedicamentoEquivalente(
			AfaMedicamento medicamento);

	public abstract void efetuarInclusao(AfaMedicamentoEquivalente entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract AfaMedicamentoEquivalente obterMedicamentoEquivalentePorChavePrimaria(
			Integer medMatCodigo, Integer medMatCodigoEquivalente);

	public abstract void efetuarAlteracao(AfaMedicamentoEquivalente entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract List<AfaMedicamento> pesquisarTodosMedicamentos(
			Object strPesquisa);

	public abstract Long pesquisarTodosMedicamentosCount(Object strPesquisa);

	public abstract List<String> obterRazoesExcessaoMedicamentoEquivalente();

	public abstract AfaLocalDispensacaoMdtos obterLocalDispensacao(
			AfaLocalDispensacaoMdtosId AfaLocalDispensacaoMdtosId);

	public abstract void atualizarLocalDispensacaoMedicamento(
			AfaLocalDispensacaoMdtos entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException,
			BaseException;

	public abstract void persistirLocalDispensacaoMedicamento(
			AfaLocalDispensacaoMdtos localDispensacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	/**
	 * Delega para camada de negócio
	 * 
	 * @param medicamento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public abstract void persistirLocalDispensacaoMedicamentoComUnfsDisponiveis(
			AfaMedicamento medicamento) throws ApplicationBusinessException,
			ApplicationBusinessException;

	public abstract void removerLocalDispensacaoMedicamento(
			AfaLocalDispensacaoMdtos localDispensacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract Long recuperarCountAghUnidadesFuncionaisDisponiveis(
			AfaMedicamento medicamento);

	public abstract List<AghUnidadesFuncionais> recuperarListaPaginadaAghUnidadesFuncionaisDisponiveis(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc);

	public abstract void removerSinonimoMedicamento(
			AfaSinonimoMedicamento elemento) throws ApplicationBusinessException;

	public abstract AfaSinonimoMedicamento obterSinonimoMedicamentoPorChavePrimaria(
			AfaSinonimoMedicamentoId chavePrimaria);

	public abstract AfaSinonimoMedicamento inserirAtualizarSinonimoMedicamento(
			AfaSinonimoMedicamento elemento) throws ApplicationBusinessException;

	public abstract List<AfaSinonimoMedicamento> recuperarListaPaginadaSinonimoMedicamento(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			boolean asc);

	public abstract Long recuperarCountSinonimoMedicamento(
			AfaMedicamento medicamento);

	public abstract AfaTipoApresentacaoMedicamento inserirAtualizarTipoApresentacaoMedicamento(
			AfaTipoApresentacaoMedicamento elemento, Boolean novoRegistro)
			throws ApplicationBusinessException;

	public abstract AfaTipoApresentacaoMedicamento obterTipoApresentacaoMedicamentoPorChavePrimaria(
			String chavePrimaria);

	public abstract List<AfaTipoApresentacaoMedicamento> pesquisarTipoApresentacaoMedicamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaTipoApresentacaoMedicamento elemento);

	public abstract Long pesquisarTipoApresentacaoMedicamentoCount(
			AfaTipoApresentacaoMedicamento elemento);

	public abstract List<AfaTipoApresentacaoMedicamento> pesquisaTipoApresentacaoMedicamentosAtivos(
			Object siglaOuDescricao);

	public abstract void removerTipoApresentacaoMedicamento(
			String siglaAfaTipoApresentacaoMedicamento) throws BaseException;

	public abstract AfaTipoUsoMdto inserirAtualizarTipoUsoMdto(
			AfaTipoUsoMdto elemento, Boolean novoRegistro)
			throws ApplicationBusinessException;

	public abstract AfaTipoUsoMdto obterTipoUsoMdtoPorChavePrimaria(
			String chavePrimaria);

	public abstract List<AfaTipoUsoMdto> pesquisarTipoUsoMdto(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaTipoUsoMdto elemento);

	public abstract Long pesquisarTipoUsoMdtoCount(AfaTipoUsoMdto elemento);

	public abstract List<AfaTipoUsoMdto> pesquisaTipoUsoMdtoAtivos(
			Object siglaOuDescricao);

	public abstract void removerTipoUsoMdto(String siglaAfaTipoUsoMdto)
			throws BaseException;

	public abstract Long consultarGruposMedicamentoCount(
			AfaMedicamento medicamento);

	public abstract List<AfaItemGrupoMedicamento> consultarGruposMedicamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento);

	public abstract Long consultarMedicamentoCount(AfaMedicamento medicamento);

	public abstract List<AfaMedicamento> consultarMedicamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento medicamento);

	public abstract void efetuarInclusao(AfaMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract void efetuarAlteracao(AfaMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract void efetuarRemocao(AfaMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws IllegalStateException, BaseException;

	public abstract List<String> obterRazoesExcessaoMedicamento();

	public abstract Long pesquisarListaAfaMedicamentoCount(
			AfaMedicamento medicamento);

	public abstract List<AfaMedicamento> pesquisarListaAfaMedicamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AfaMedicamento elemento);

	public abstract DominioSimNao existeCaractUnFuncional(
			AghUnidadesFuncionais unidadeFuncional, String[] caracteristicaArray);
	
	List<AghUnidadesFuncionaisVO> listarUnidadeExecutora(Object param);
	
	Long listarUnidadeExecutoraCount(Object param);
	
	public AghUnidadesFuncionaisVO obterUnidadeFuncionalVO(Short seq);

	public void gravarViaAdministracao(final AfaViaAdmUnf unf,
			final Boolean edita) throws ApplicationBusinessException;

	public abstract AfaLocalDispensacaoMdtos obterLocalDispensacaoOriginal(
			AfaLocalDispensacaoMdtos entidade);
	
	AfaMedicamento obterMedicamentoEdicao(final Integer matCodigo);

	public List<MpmPrescricaoMedVO> obterDataReferenciaPrescricaoMedica(Integer seq, String descricao);
	
	public void validarEnvioInformacoesPrescribentes(PacienteAgendamentoPrescribenteVO paciente) throws ApplicationBusinessException;
	
	public void validarNulidadeInformacaoPrescribente(InformacoesPacienteAgendamentoPrescribenteVO informacoesPacienteAgendamentoPrescribenteVO) throws ApplicationBusinessException;
	
	public void validarRetornoPaciente(PacienteAgendamentoPrescribenteVO prescribenteVO) throws ApplicationBusinessException;
	
	public String gravarMpmInformacaoPrescribentes(InformacoesPacienteAgendamentoPrescribenteVO inPrescribenteVO, 
			MpmPrescricaoMedVO mpmPrescricaoMedica, PacienteAgendamentoPrescribenteVO prescribenteVO, String informacaoPrescribente, UnidadeFuncionalVO unidadeFuncional) throws ApplicationBusinessException;

	List<UnidadeFuncionalVO> obterUnidadeFuncionalOrigemInformacaoPrescribente(String descricao);
	
	Boolean validarData(String descricao);
	
	UnidadeFuncionalVO obterPorUnfSeq(Short unfSeq);

	List<InformacaoEnviadaPrescribenteVO> pesquisarInformacaoEnviadaPrescribente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			InformacaoEnviadaPrescribenteVO filtro); 

	Long pesquisarInformacaoEnviadaPrescribenteCount(InformacaoEnviadaPrescribenteVO filtro);

	Long consultaPorDescricaoUnidadeFuncionalCount(String param);

	List<AghUnidadesFuncionais> consultaPorDescricaoUnidadeFuncional(String param);
	
	AghUnidadesFuncionais obterValoresPrescricaoMedica(Short seqUnf);
	
	List<AghUnidadesFuncionais> consultaPorDescricaoUnidadeFuncionalOrderCodDesc(String Param);
	
	public abstract List<AfaLocalDispensacaoMdtos> pesqueisarLocalDispensacaoPorUnidades(

			AghUnidadesFuncionais unidadeFuncionalSolicitante,

			Integer firstResult, Integer maxResult, String orderProperty,

			boolean asc);

	public abstract Long pesqueisarLocalDispensacaoPorUnidadesCount(

			AghUnidadesFuncionais unidadeFuncionalSolicitante);

	public abstract void incluirTodosMedicamentoPorUnidades(AghUnidadesFuncionais unidadeFuncionalSolicitante, RapServidores servidor) throws ApplicationBusinessException;
	
}