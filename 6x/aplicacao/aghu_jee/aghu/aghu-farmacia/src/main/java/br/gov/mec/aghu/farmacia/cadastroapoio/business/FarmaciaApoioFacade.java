package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.configuracao.dao.VAghUnidFuncionalDAO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.business.FarmaciaON;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaLocalDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaViaAdmUnfDAO;
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
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaAuxiliarDAO;

@Modulo(ModuloEnum.FARMACIA)
@Stateless
public class FarmaciaApoioFacade extends BaseFacade implements IFarmaciaApoioFacade {

	@Inject
	private AfaLocalDispensacaoMdtosDAO afaLocalDispensacaoMdtosDAO;
	
	@Inject
	private AfaViaAdmUnfDAO afaViaAdmUnfDAO;
	
	@EJB
	private SinonimoMedicamentoON sinonimoMedicamentoON;
	
	@EJB
	private ViaAdministracaoMedicamentoON viaAdministracaoMedicamentoON;
	
	@EJB
	private ManterViasAdmPermUndON manterViasAdmPermUndON;
	
	@EJB
	private MedicamentoEquivalenteCRUD medicamentoEquivalenteCRUD;
	
	@EJB
	private TipoUsoMdtoON tipoUsoMdtoON;
	
	@EJB
	private TipoApresentacaoMedicamentoON tipoApresentacaoMedicamentoON;
	
	@EJB
	private OcorrenciaCRUD ocorrenciaCRUD;
	
	@EJB
	private LocalDispensacaoMedicamentoCRUD localDispensacaoMedicamentoCRUD;
	
	@EJB
	private GrupoUsoMedicamentoON grupoUsoMedicamentoON;
	
	@EJB
	private ItemGrupoMedicamentoON itemGrupoMedicamentoON;
	
	@EJB
	private MedicamentoON medicamentoON;
	
	@EJB
	private FormaDosagemON formaDosagemON;
	
	@EJB
	private AfaViaAdmUnfRN afaViaAdmUnfRN;
	
	@EJB
	private LocalDispensacaoMedicamentoRN localDispensacaoMedicamentoRN;
	
	@EJB
	private LocalDispensacaoMedicamentoON localDispensacaoMedicamentoON;
	
	@EJB
	private ConsultarMedicamentosON consultarMedicamentosON;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;
	
	@Inject
	private MpmPrescricaoMedicaAuxiliarDAO mpmPrescricaoMedicaDAO;
	
	@EJB
	private FarmaciaON farmaciaON;
	
	@Inject
	private VAghUnidFuncionalDAO vAghUnidFuncionalDAO;
	
	@Inject 
	private AghAtendimentoDAO aghAtendimentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5736053954543564687L;
	
	/*
	 * FormaDosagem
	 */
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarInclusao(br.gov.mec.aghu.model.AfaFormaDosagem)
	 */
	@Override
	public void efetuarInclusao(AfaFormaDosagem entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		this.getFormaDosagemON().inserir(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarAlteracao(br.gov.mec.aghu.model.AfaFormaDosagem)
	 */
	@Override
	public void efetuarAlteracao(AfaFormaDosagem entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.getFormaDosagemON().atualizar(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarRemocao(br.gov.mec.aghu.model.AfaFormaDosagem)
	 */
	@Override
	public void efetuarRemocao(AfaFormaDosagem entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.getFormaDosagemON().remover(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterRazoesExcessaoFormaDosagem()
	 */
	@Override
	@BypassInactiveModule
	public List<String> obterRazoesExcessaoFormaDosagem() {

		return this.getFormaDosagemON().getRazaoExecessao();
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#listaFormaDosagemMedicamento(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaFormaDosagem> listaFormaDosagemMedicamento(Integer matCodigo) {

		return ((AfaFormaDosagemDAO) this.getFormaDosagemON().getEntidadeDAO())
				.listaFormaDosagemMedicamento(matCodigo);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarListaPaginadaFormaDosagem(br.gov.mec.aghu.model.AfaMedicamento, java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaFormaDosagem> recuperarListaPaginadaFormaDosagem(AfaMedicamento medicamento,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		return this.getFormaDosagemON().pesquisar(firstResult, maxResult, orderProperty, asc,
				medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarCountFormaDosagem(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long recuperarCountFormaDosagem(AfaMedicamento medicamento) {

		return this.getFormaDosagemON().pesquisarCount(medicamento);
	}

	/*
	 * ViaAdministracaoMedicamento
	 */
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarInclusao(br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public void efetuarInclusao(AfaViaAdministracaoMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.getViaAdministracaoMedicamentoON().inserir(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
		
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarAlteracao(br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento)
	 */
	@Override
	public void efetuarAlteracao(AfaViaAdministracaoMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.getViaAdministracaoMedicamentoON().atualizar(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarRemocao(br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento)
	 */
	@Override
	public void efetuarRemocao(AfaViaAdministracaoMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.getViaAdministracaoMedicamentoON().remover(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterRazoesExcessaoViaAdministracaoMedicamento()
	 */
	@Override
	@BypassInactiveModule
	public List<String> obterRazoesExcessaoViaAdministracaoMedicamento() {

		return this.getViaAdministracaoMedicamentoON().getRazaoExecessao();
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarListaPaginadaViaAdministracaoMedicamento(br.gov.mec.aghu.model.AfaMedicamento, java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaViaAdministracaoMedicamento> recuperarListaPaginadaViaAdministracaoMedicamento(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {

		return this.getViaAdministracaoMedicamentoON().pesquisar(firstResult, maxResult,
				orderProperty, asc, medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarCountViaAdministracaoMedicamento(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long recuperarCountViaAdministracaoMedicamento(AfaMedicamento medicamento) {

		return this.getViaAdministracaoMedicamentoON().pesquisarCount(medicamento);
	}
	
	// GRUPO DE USO DE MEDICAMENTO
	protected GrupoUsoMedicamentoON getGrupoUsoMedicamentoON() {
		return grupoUsoMedicamentoON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarGrupoUsoMedicamento(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaGrupoUsoMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaGrupoUsoMedicamento> pesquisarGrupoUsoMedicamento(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AfaGrupoUsoMedicamento elemento) {
		return getGrupoUsoMedicamentoON().pesquisar(firstResult, maxResult, orderProperty, asc,
				elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarGrupoUsoMedicamentoCount(br.gov.mec.aghu.model.AfaGrupoUsoMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarGrupoUsoMedicamentoCount(AfaGrupoUsoMedicamento elemento) {
		return getGrupoUsoMedicamentoON().pesquisarCount(elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterGrupoUsoMedicamentoPorChavePrimaria(java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public AfaGrupoUsoMedicamento obterGrupoUsoMedicamentoPorChavePrimaria(Integer chavePrimaria) {
		return getGrupoUsoMedicamentoON().obterPorChavePrimaria(chavePrimaria);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#inserirAtualizar(br.gov.mec.aghu.model.AfaGrupoUsoMedicamento)
	 */
	@Override
	public AfaGrupoUsoMedicamento inserirAtualizar(AfaGrupoUsoMedicamento elemento)
			throws ApplicationBusinessException {
		return getGrupoUsoMedicamentoON().inserirAtualizar(elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#removerGrupoUsoMedicamento(br.gov.mec.aghu.model.AfaGrupoUsoMedicamento)
	 */
	@Override
	public void removerGrupoUsoMedicamento(Integer seqAfaGrupoUsoMedicamento)
			throws BaseException {
		getGrupoUsoMedicamentoON().remover(seqAfaGrupoUsoMedicamento);
	}
	
	protected OcorrenciaCRUD getOcorrenciaCRUD() {
		
		return ocorrenciaCRUD;
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#persistirOcorrencia(br.gov.mec.aghu.model.AfaTipoOcorDispensacao)
	 */
	@Override
	public void persistirOcorrencia(AfaTipoOcorDispensacao ocorrencia)
			throws ApplicationBusinessException {
		getOcorrenciaCRUD().persistirOcorrencia(ocorrencia);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#removerOcorrencia(br.gov.mec.aghu.model.AfaTipoOcorDispensacao)
	 */
	@Override
	public void removerOcorrencia(AfaTipoOcorDispensacao ocorrencia)
			throws ApplicationBusinessException {
		getOcorrenciaCRUD().removerOcorrencia(ocorrencia);
	}
	
	//MEDICAMENTO EQUIVALENTE
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarListaPaginadaMedicamentoEquivalente(br.gov.mec.aghu.model.AfaMedicamento, java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamentoEquivalente> recuperarListaPaginadaMedicamentoEquivalente(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getMedicamentoEquivalenteCRUD().pesquisar(firstResult, maxResult, orderProperty, asc, medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarCountMedicamentoEquivalente(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long recuperarCountMedicamentoEquivalente(AfaMedicamento medicamento) {
		return getMedicamentoEquivalenteCRUD().pesquisarCount(medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarInclusao(br.gov.mec.aghu.model.AfaMedicamentoEquivalente)
	 */
	@Override
	public void efetuarInclusao(AfaMedicamentoEquivalente entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		this.getMedicamentoEquivalenteCRUD().inserir(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterMedicamentoEquivalentePorChavePrimaria(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@BypassInactiveModule
	public AfaMedicamentoEquivalente obterMedicamentoEquivalentePorChavePrimaria(Integer medMatCodigo, Integer medMatCodigoEquivalente){
		return getMedicamentoEquivalenteCRUD().obterPorChavePrimaria(medMatCodigo, medMatCodigoEquivalente);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarAlteracao(br.gov.mec.aghu.model.AfaMedicamentoEquivalente)
	 */
	@Override
	public void efetuarAlteracao(AfaMedicamentoEquivalente entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		this.getMedicamentoEquivalenteCRUD().atualizar(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarTodosMedicamentos(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarTodosMedicamentos(Object strPesquisa) {
		return this.getMedicamentoEquivalenteCRUD().pesquisarTodosMedicamentos(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarTodosMedicamentosCount(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarTodosMedicamentosCount(Object strPesquisa) {
		return this.getMedicamentoEquivalenteCRUD().pesquisarTodosMedicamentosCount(strPesquisa);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterRazoesExcessaoMedicamentoEquivalente()
	 */
	@Override
	@BypassInactiveModule
	public List<String> obterRazoesExcessaoMedicamentoEquivalente() {
		return this.getMedicamentoEquivalenteCRUD().getRazaoExecessao();
	}
	
	protected LocalDispensacaoMedicamentoCRUD getLocalDispensacaoMedicamentoCRUD() {
		
		return localDispensacaoMedicamentoCRUD;
	}
	
	protected LocalDispensacaoMedicamentoON getLocalDispensacaoMedicamentoON() {
		return localDispensacaoMedicamentoON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterLocalDispensacao(br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosId)
	 */
	@Override
	@BypassInactiveModule
	public AfaLocalDispensacaoMdtos obterLocalDispensacao(AfaLocalDispensacaoMdtosId AfaLocalDispensacaoMdtosId) {
		return getLocalDispensacaoMedicamentoCRUD().obterPorChavePrimaria(AfaLocalDispensacaoMdtosId);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#atualizarLocalDispensacaoMedicamento(br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos)
	 */
	@Override
	@BypassInactiveModule
	public void atualizarLocalDispensacaoMedicamento(AfaLocalDispensacaoMdtos entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {

		this.getLocalDispensacaoMedicamentoCRUD().atualizar(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#persistirLocalDispensacaoMedicamento(br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos)
	 */
	@Override
	public void persistirLocalDispensacaoMedicamento(AfaLocalDispensacaoMdtos localDispensacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
		throws IllegalStateException, BaseException {
		getLocalDispensacaoMedicamentoCRUD().inserir(localDispensacao, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#persistirLocalDispensacaoMedicamentoComUnfsDisponiveis(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	public void persistirLocalDispensacaoMedicamentoComUnfsDisponiveis(AfaMedicamento medicamento) throws ApplicationBusinessException	{
		getLocalDispensacaoMedicamentoON().persistirLocaisDispensacaoDisponiveis(medicamento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#removerLocalDispensacaoMedicamento(br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos)
	 */
	@Override
	public void removerLocalDispensacaoMedicamento(AfaLocalDispensacaoMdtos localDispensacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
		throws IllegalStateException, BaseException {
		getLocalDispensacaoMedicamentoCRUD().remover(localDispensacao, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	protected MedicamentoEquivalenteCRUD getMedicamentoEquivalenteCRUD() {
		
		return medicamentoEquivalenteCRUD;
	}
	
	protected FormaDosagemON getFormaDosagemON() {
		return formaDosagemON;
	}

	protected ViaAdministracaoMedicamentoON getViaAdministracaoMedicamentoON() {

		return viaAdministracaoMedicamentoON;
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarCountAghUnidadesFuncionaisDisponiveis(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long recuperarCountAghUnidadesFuncionaisDisponiveis(AfaMedicamento medicamento) {

		return getLocalDispensacaoMedicamentoON().buscarCountUnidadesFuncionaisDisponivelLocalDispensacao(medicamento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarListaPaginadaAghUnidadesFuncionaisDisponiveis(br.gov.mec.aghu.model.AfaMedicamento, java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionais> recuperarListaPaginadaAghUnidadesFuncionaisDisponiveis(AfaMedicamento medicamento, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		return getLocalDispensacaoMedicamentoON().listaUnidadesFuncionaisDisponiveis(medicamento, firstResult, maxResult, orderProperty, asc);
	}
	
	// SINONIMO DE MEDICAMENTO
	protected SinonimoMedicamentoON getSinonimoMedicamentoON() {
		return sinonimoMedicamentoON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#removerSinonimoMedicamento(br.gov.mec.aghu.model.AfaSinonimoMedicamento)
	 */
	@Override
	public void removerSinonimoMedicamento(AfaSinonimoMedicamento elemento)
			throws ApplicationBusinessException {
		getSinonimoMedicamentoON().removerSinonimoMedicamento(elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterSinonimoMedicamentoPorChavePrimaria(br.gov.mec.aghu.model.AfaSinonimoMedicamentoId)
	 */
	@Override
	@BypassInactiveModule
	public AfaSinonimoMedicamento obterSinonimoMedicamentoPorChavePrimaria(
			AfaSinonimoMedicamentoId chavePrimaria) {
		return getSinonimoMedicamentoON().obterPorChavePrimaria(chavePrimaria);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#inserirAtualizarSinonimoMedicamento(br.gov.mec.aghu.model.AfaSinonimoMedicamento)
	 */
	@Override
	public AfaSinonimoMedicamento inserirAtualizarSinonimoMedicamento(
			AfaSinonimoMedicamento elemento) throws ApplicationBusinessException {
		return getSinonimoMedicamentoON().inserirAtualizarSinonimoMedicamento(elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarListaPaginadaSinonimoMedicamento(br.gov.mec.aghu.model.AfaMedicamento, java.lang.Integer, java.lang.Integer, boolean)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaSinonimoMedicamento> recuperarListaPaginadaSinonimoMedicamento(
			AfaMedicamento medicamento, Integer firstResult, Integer maxResult, boolean asc) {

		return this.getSinonimoMedicamentoON().pesquisar(firstResult, maxResult,
				AfaSinonimoMedicamento.Fields.DESCRICAO.toString(), asc, medicamento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#recuperarCountSinonimoMedicamento(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long recuperarCountSinonimoMedicamento(AfaMedicamento medicamento) {

		return this.getSinonimoMedicamentoON().pesquisarCount(medicamento);
	}
	
	// TIPO DE APRESENTAÇÃO DE MEDICAMENTO
	protected TipoApresentacaoMedicamentoON getTipoApresentacaoMedicamentoON() {
		return tipoApresentacaoMedicamentoON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#inserirAtualizarTipoApresentacaoMedicamento(br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento, java.lang.String)
	 */
	@Override
	public AfaTipoApresentacaoMedicamento inserirAtualizarTipoApresentacaoMedicamento(
			AfaTipoApresentacaoMedicamento elemento, Boolean novoRegistro)
			throws ApplicationBusinessException {
		return getTipoApresentacaoMedicamentoON().inserirAtualizar(elemento, novoRegistro);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterTipoApresentacaoMedicamentoPorChavePrimaria(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public AfaTipoApresentacaoMedicamento obterTipoApresentacaoMedicamentoPorChavePrimaria(
			String chavePrimaria) {
		return getTipoApresentacaoMedicamentoON().obterPorChavePrimaria(chavePrimaria);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarTipoApresentacaoMedicamento(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoApresentacaoMedicamento> pesquisarTipoApresentacaoMedicamento(
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AfaTipoApresentacaoMedicamento elemento) {
		return getTipoApresentacaoMedicamentoON().pesquisar(firstResult, maxResult, orderProperty,
				asc, elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarTipoApresentacaoMedicamentoCount(br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarTipoApresentacaoMedicamentoCount(AfaTipoApresentacaoMedicamento elemento) {
		return getTipoApresentacaoMedicamentoON().pesquisarCount(elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisaTipoApresentacaoMedicamentosAtivos(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoApresentacaoMedicamento> pesquisaTipoApresentacaoMedicamentosAtivos(
			Object siglaOuDescricao) {
		return getTipoApresentacaoMedicamentoON().pesquisaTipoApresentacaoMedicamentosAtivos(
				siglaOuDescricao);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#removerTipoApresentacaoMedicamento(br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public void removerTipoApresentacaoMedicamento(
			String siglaAfaTipoApresentacaoMedicamento) throws BaseException {
		getTipoApresentacaoMedicamentoON().remover(siglaAfaTipoApresentacaoMedicamento);
	}

	// TIPO DE USO DE MEDICAMENTO
	protected TipoUsoMdtoON getTipoUsoMdtoON() {
		return tipoUsoMdtoON;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#inserirAtualizarTipoUsoMdto(br.gov.mec.aghu.model.AfaTipoUsoMdto, java.lang.String)
	 */
	@Override
	public AfaTipoUsoMdto inserirAtualizarTipoUsoMdto(AfaTipoUsoMdto elemento, Boolean novoRegistro)
			throws ApplicationBusinessException {
		return getTipoUsoMdtoON().inserirAtualizar(elemento, novoRegistro);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterTipoUsoMdtoPorChavePrimaria(java.lang.String)
	 */
	@Override
	@BypassInactiveModule
	public AfaTipoUsoMdto obterTipoUsoMdtoPorChavePrimaria(String chavePrimaria) {
		return getTipoUsoMdtoON().obterPorChavePrimaria(chavePrimaria);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarTipoUsoMdto(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaTipoUsoMdto)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoUsoMdto> pesquisarTipoUsoMdto(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AfaTipoUsoMdto elemento) {
		return getTipoUsoMdtoON().pesquisar(firstResult, maxResult, orderProperty, asc, elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarTipoUsoMdtoCount(br.gov.mec.aghu.model.AfaTipoUsoMdto)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarTipoUsoMdtoCount(AfaTipoUsoMdto elemento) {
		return getTipoUsoMdtoON().pesquisarCount(elemento);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisaTipoUsoMdtoAtivos(java.lang.Object)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaTipoUsoMdto> pesquisaTipoUsoMdtoAtivos(Object siglaOuDescricao) {
		return getTipoUsoMdtoON().pesquisaTipoUsoMdtoAtivos(siglaOuDescricao);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#removerTipoUsoMdto(br.gov.mec.aghu.model.AfaTipoUsoMdto)
	 */
	@Override
	public void removerTipoUsoMdto(String siglaAfaTipoUsoMdto)
			throws BaseException {
		getTipoUsoMdtoON().remover(siglaAfaTipoUsoMdto);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#consultarGruposMedicamentoCount(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long consultarGruposMedicamentoCount(AfaMedicamento medicamento) {
		return getItemGrupoMedicamentoON().pesquisarCount(medicamento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#consultarGruposMedicamento(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaItemGrupoMedicamento> consultarGruposMedicamento(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AfaMedicamento medicamento) {
		return getItemGrupoMedicamentoON().pesquisar(firstResult, maxResult, orderProperty, asc, medicamento);
	}
	
	private ItemGrupoMedicamentoON getItemGrupoMedicamentoON(){
		return itemGrupoMedicamentoON;
	}
	
	private ConsultarMedicamentosON getConsultarMedicamentosON(){
		return consultarMedicamentosON;
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#consultarMedicamentoCount(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long consultarMedicamentoCount(AfaMedicamento medicamento) {
		return getConsultarMedicamentosON().pesquisarCount(medicamento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#consultarMedicamento(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> consultarMedicamento(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, AfaMedicamento medicamento) {
		return getConsultarMedicamentosON().pesquisar(firstResult, maxResult, orderProperty, asc, medicamento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarInclusao(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	@SuppressWarnings("deprecation")
	public void efetuarInclusao(AfaMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.getMedicamentoON().inserir(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarAlteracao(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void efetuarAlteracao(AfaMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.getMedicamentoON().atualizar(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#efetuarRemocao(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void efetuarRemocao(AfaMedicamento entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.getMedicamentoON().remover(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#obterRazoesExcessaoMedicamento()
	 */
	@Override
	@SuppressWarnings("deprecation")
	@BypassInactiveModule
	public List<String> obterRazoesExcessaoMedicamento() {

		return this.getMedicamentoON().getRazaoExecessao();
	}
	
	protected MedicamentoON getMedicamentoON() {
		return medicamentoON;
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarListaAfaMedicamentoCount(br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public Long pesquisarListaAfaMedicamentoCount(AfaMedicamento medicamento){
		return getMedicamentoON().pesquisarCount(medicamento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#pesquisarListaAfaMedicamento(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AfaMedicamento)
	 */
	@Override
	@BypassInactiveModule
	public List<AfaMedicamento> pesquisarListaAfaMedicamento(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaMedicamento elemento) {
		return getMedicamentoON().pesquisar(firstResult, maxResult, orderProperty, asc, elemento);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade#existeCaractUnFuncional(br.gov.mec.aghu.model.AghUnidadesFuncionais, java.lang.String[])
	 */
	@Override
	@BypassInactiveModule
	public DominioSimNao existeCaractUnFuncional(AghUnidadesFuncionais unidadeFuncional, String[] caracteristicaArray) {
		return getLocalDispensacaoMedicamentoRN().existeCaractUnFuncional(unidadeFuncional, caracteristicaArray);
	}
	
	protected LocalDispensacaoMedicamentoRN getLocalDispensacaoMedicamentoRN() {
		return localDispensacaoMedicamentoRN;
	}

	@Override
	@BypassInactiveModule
	public List<AghUnidadesFuncionaisVO> listarUnidadeExecutora(Object param) {
		return getManterViasAdmPermUndON().obterUnidadesExecutora(param);
	}
	
	@Override
	@BypassInactiveModule
	public Long listarUnidadeExecutoraCount(Object param) {
		return getAghUnidadesFuncionaisDAO().obterUnidadesExecutoraCount(param);
	}
	
	@Override
	@BypassInactiveModule
	public AghUnidadesFuncionaisVO obterUnidadeFuncionalVO(Short seq) {
		return getManterViasAdmPermUndON().obterUnidadeFuncionalVO(seq);
	}
	
	@Override
	public void gravarViaAdministracao(final AfaViaAdmUnf unf, final Boolean edita) throws ApplicationBusinessException {
		getManterViasAdmPermUndON().gravarViaAdmUnidade(unf, edita);
	}
	
	@Override
	public List<AfaLocalDispensacaoMdtos> pesqueisarLocalDispensacaoPorUnidades(
			AghUnidadesFuncionais unidadeFuncionalSolicitante, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return this.getDispensaMedicamentoON().pesqueisarLocalDispensacaoPorUnidadesON(unidadeFuncionalSolicitante, firstResult, maxResult, orderProperty, asc);
	}	

	@Override
	public Long pesqueisarLocalDispensacaoPorUnidadesCount(AghUnidadesFuncionais unidadeFuncionalSolicitante){
		return this.getDispensaMedicamentoON().pesqueisarLocalDispensacaoPorUnidadesCountON(unidadeFuncionalSolicitante);
	}	

	@Override
	public void incluirTodosMedicamentoPorUnidades(AghUnidadesFuncionais unidadeFuncionalSolicitante, RapServidores servidor) throws ApplicationBusinessException {
		this.getDispensaMedicamentoON().vincular(unidadeFuncionalSolicitante, servidor);
	}

    private LocalDispensacaoMedicamentoON getDispensaMedicamentoON() {
         return localDispensacaoMedicamentoON;
    }
	
	protected ManterViasAdmPermUndON getManterViasAdmPermUndON(){
		return manterViasAdmPermUndON;
	}

	protected AfaViaAdmUnfRN getAfaViaAdmUnfRN() {
		return afaViaAdmUnfRN;
	}
	
	protected AfaViaAdmUnfDAO getAfaViaAdmUnfDAO() {
		return afaViaAdmUnfDAO;
	}
	
	private AghUnidadesFuncionaisDAO getAghUnidadesFuncionaisDAO() {
		return aghUnidadesFuncionaisDAO;
	}

	@Override
	@BypassInactiveModule
	public AfaLocalDispensacaoMdtos obterLocalDispensacaoOriginal(
			AfaLocalDispensacaoMdtos entidade) {
		return afaLocalDispensacaoMdtosDAO.obterOriginal(entidade);
	}
	
	@Override
	@BypassInactiveModule
	public AfaMedicamento obterMedicamentoEdicao(final Integer matCodigo) {
		return getMedicamentoON().obterMedicamentoEdicao(matCodigo);
	}
	
	@Override
	public List<MpmPrescricaoMedVO> obterDataReferenciaPrescricaoMedica(Integer seq, String descricao) {
		return mpmPrescricaoMedicaDAO.obterDataReferenciaPrescricaoMedica(seq, descricao);
	}
	
	public void validarEnvioInformacoesPrescribentes(PacienteAgendamentoPrescribenteVO paciente) throws ApplicationBusinessException{
		farmaciaON.validarEnvioInformacoesPrescribentes(paciente);
	}

	@Override
	public void validarNulidadeInformacaoPrescribente(
			InformacoesPacienteAgendamentoPrescribenteVO informacoesPacienteAgendamentoPrescribenteVO) throws ApplicationBusinessException {
		farmaciaON.validarNulidadeInformacaoPrescribente(informacoesPacienteAgendamentoPrescribenteVO);
	}

	@Override
	public void validarRetornoPaciente(
			PacienteAgendamentoPrescribenteVO prescribenteVO) throws ApplicationBusinessException {
		farmaciaON.validarRetornoPaciente(prescribenteVO);
	}

	@Override
	public String gravarMpmInformacaoPrescribentes(InformacoesPacienteAgendamentoPrescribenteVO inPrescribenteVO,
			MpmPrescricaoMedVO mpmPrescricaoMedica, PacienteAgendamentoPrescribenteVO prescribenteVO, String informacaoPrescribente, UnidadeFuncionalVO unidadeFuncional) throws ApplicationBusinessException {
		return farmaciaON.gravarMpmInformacaoPrescribentes(inPrescribenteVO, mpmPrescricaoMedica, prescribenteVO, informacaoPrescribente, unidadeFuncional);
	}

	@Override
	public List<UnidadeFuncionalVO> obterUnidadeFuncionalOrigemInformacaoPrescribente(String descricao) {
		return vAghUnidFuncionalDAO.obterUnidadeFuncionalOrigemInformacaoPrescribente(descricao);
	}

	@Override
	public Boolean validarData(String descricao) {
		return farmaciaON.validacaoData(descricao);
	}

	@Override
	public UnidadeFuncionalVO obterPorUnfSeq(Short unfSeq) {
		return vAghUnidFuncionalDAO.obterPorUnfSeq(unfSeq);
	}
	
	
	@Override
	public List<InformacaoEnviadaPrescribenteVO> pesquisarInformacaoEnviadaPrescribente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			InformacaoEnviadaPrescribenteVO filtro) {
		return aghAtendimentoDAO.pesquisarInformacaoEnviadaPrescribente(firstResult,maxResult,orderProperty,asc,filtro);
	}
	
	@Override
	public Long pesquisarInformacaoEnviadaPrescribenteCount(InformacaoEnviadaPrescribenteVO filtro){
		return aghAtendimentoDAO.pesquisarInformacaoEnviadaPrescribenteCount(filtro);
	}
	
	
	public Long consultaPorDescricaoUnidadeFuncionalCount(String param){
		return aghUnidadesFuncionaisDAO.pesquisarPorSequencialOuDescricaoCount(param);
	}
	
	@Override
	public List<AghUnidadesFuncionais> consultaPorDescricaoUnidadeFuncional(String param){
		return aghUnidadesFuncionaisDAO.pesquisarPorSequencialOuDescricao(param);
	}
	
	public List<AghUnidadesFuncionais> consultaPorDescricaoUnidadeFuncionalOrderCodDesc(String param){
		return aghUnidadesFuncionaisDAO.pesquisarPorSequencialOuDescricaoOrderCodDesc(param);
	}
	
	     
	@Override
	public AghUnidadesFuncionais obterValoresPrescricaoMedica(Short seqUnf) {
		return aghUnidadesFuncionaisDAO.obterValoresPrescricaoMedica(seqUnf);
	}
	
}