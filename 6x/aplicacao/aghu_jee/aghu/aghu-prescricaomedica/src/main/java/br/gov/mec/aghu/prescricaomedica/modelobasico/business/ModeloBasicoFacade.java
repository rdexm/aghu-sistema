package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidadoId;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.ItensModeloBasicoVO;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.MpmItemModeloBasicoDietaVO;

/**
 * Classe de fachada que disponibiliza uma interface para as funcionalidades do
 * sub-módulo "Modelo Básico da Prescrição" do módulo Prescrição Médica.
 */


@Modulo(ModuloEnum.PRESCRICAO_MEDICA)
@Stateless
public class ModeloBasicoFacade extends BaseFacade implements IModeloBasicoFacade {

	
	@EJB
	private ManterCuidadosModeloBasicoON manterCuidadosModeloBasicoON;
	
	@EJB
	private EscolherItensModeloBasicoON escolherItensModeloBasicoON;
	
	@EJB
	private ManterSolucaoModeloBasicoON manterSolucaoModeloBasicoON;
	
	@EJB
	private ManterDietasModeloBasicoON manterDietasModeloBasicoON;
	
	@EJB
	private ManterProcedimentosModeloBasicoON manterProcedimentosModeloBasicoON;
	
	@EJB
	private PesquisarCuidadoUsualON pesquisarCuidadoUsualON;
	
	@EJB
	private ManterMedicamentosModeloBasicoON manterMedicamentosModeloBasicoON;
	
	@EJB
	private ManterItensModeloBasicoON manterItensModeloBasicoON;
	
	@EJB
	private ManterCuidadoUsualON manterCuidadoUsualON;

	@EJB
	private ManterCuidadoUsualONBTM manterCuidadoUsualONBTM;

	@EJB
	private ManterModeloBasicoON manterModeloBasicoON;

	@Inject
	private MpmModeloBasicoMedicamentoDAO mpmModeloBasicoMedicamentoDAO;
		
	private static final long serialVersionUID = -9149061248978130037L;

	@Override
	public List<ItensModeloBasicoVO> obterListaItensModelo(Integer seqModelo) {
		return this.getManterItensModeloBasicoON().obterListaItensModelo(
				seqModelo);
	}

	@Override
	public List<MpmItemModeloBasicoDieta> obterListaItensDieta(
			Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq) {
		return this.getManterDietasModeloBasicoON().obterListaItensDieta(
				modeloBasicoPrescricaoSeq, modeloBasicoDietaSeq);
	}
	
	@Override
	public List<MpmItemModeloBasicoDietaVO> obterListaItensDietaVO(
			Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq) {
		return this.getManterDietasModeloBasicoON().obterListaItensDietaVO(
				modeloBasicoPrescricaoSeq, modeloBasicoDietaSeq);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade#obterListaCuidados(java.lang.Integer)
	 */
	@Override
	public List<MpmModeloBasicoCuidado> obterListaCuidados(
			Integer modeloBasicoPrescricaoSeq) {

		return this.getManterCuidadosModeloBasicoON().obterListaCuidados(
				modeloBasicoPrescricaoSeq);
	}

	@Override
	public MpmModeloBasicoPrescricao obterModeloBasico(Integer seqModelo) {
		return this.getManterItensModeloBasicoON().obterModeloBasico(seqModelo);
	}
	
	public MpmModeloBasicoPrescricao obterModeloBasicoPrescricaoComServidorPorId(
			Integer seq) throws ApplicationBusinessException {
		return this.getManterItensModeloBasicoON().obterModeloBasicoPrescricaoComServidorPorId(seq);
	}

	@Override
	public MpmCuidadoUsual obterCuidadoUsual(Integer seq) {
		return this.getManterCuidadosModeloBasicoON().obterCuidadoUsual(seq);
	}

	// manter Dietas modelo básico

	@Override
	public void inserir(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {
		this.getManterDietasModeloBasicoON().inserir(itemDieta);
	}

	@Override
	public void alterar(MpmModeloBasicoDieta dieta) throws ApplicationBusinessException {
		this.getManterDietasModeloBasicoON().alterar(dieta);
	}

	@Override
	public void alterar(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws ApplicationBusinessException {
		this.getManterCuidadosModeloBasicoON().alterar(modeloBasicoCuidado);
	}

	@Override
	public MpmModeloBasicoDieta obterModeloBasicoDieta(
			Integer modeloBasicoPrescricaoSeq, Integer seq) {
		return this.getManterDietasModeloBasicoON().obterModeloBasicoDieta(
				modeloBasicoPrescricaoSeq, seq);
	}

	@Override
	public MpmModeloBasicoCuidado obterModeloBasicoCuidado(
			Integer modeloBasicoPrescricaoSeq, Integer seq) {
		return this.getManterCuidadosModeloBasicoON().obterModeloBasicoCuidado(
				modeloBasicoPrescricaoSeq, seq);
	}

	@Override
	public MpmModeloBasicoMedicamento obterModeloBasicoMedicamento(
			Integer seqModelo, Integer seqItemModelo) {
		return this.getManterMedicamentosModeloBasicoON()
				.obterModeloBasicoMedicamento(seqModelo, seqItemModelo);
	}
	
	@Override
	public MpmModeloBasicoMedicamento obterModeloBasicoMedicamento(MpmModeloBasicoMedicamentoId id, boolean left, Enum ...fields) {
		return this.getMpmModeloBasicoMedicamentoDAO().obterPorChavePrimaria(id, left, fields);
	}

	@Override
	public MpmModeloBasicoProcedimento obterModeloBasicoProcedimento(
			Integer seqModelo, Integer seqItemModelo) {
		return this.getManterProcedimentosModeloBasicoON()
				.obterModeloBasicoProcedimento(seqModelo, seqItemModelo);
	}

	@Override
	public List<MpmTipoFrequenciaAprazamento> obterListaTipoFrequenciaAprazamento(
			String strPesquisa) {
		return this.getManterDietasModeloBasicoON()
				.obterListaTipoFrequenciaAprazamento(strPesquisa);
	}

	@Override
	public Long obterListaTipoFrequenciaAprazamentoCount(String strPesquisa) {
		return this.getManterDietasModeloBasicoON()
				.obterListaTipoFrequenciaAprazamentoCount(strPesquisa);
	}
	
	@Override
	@BypassInactiveModule
	public List<AnuTipoItemDieta> obterTiposItemDieta(Object descricaoOuId) {
		return this.getManterDietasModeloBasicoON().obterTiposItemDieta(
				descricaoOuId);
	}

	@Override
	@BypassInactiveModule
	public Long obterTiposItemDietaCount(Object idOuDescricao) {
		return this.getManterDietasModeloBasicoON().obterTiposItemDietaCount(idOuDescricao);
	}
	
	@Override
	public List<MpmModeloBasicoPrescricao> listarModelosBasicos() {
		return this.getManterModeloBasicoON().listarModelosBasicos();
	}

	@Override
	public List<MpmModeloBasicoPrescricao> pequisarModelosPublicos(
			String descricaoModelo, String descricaoCentroCusto,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		return this.getManterModeloBasicoON().pequisarModelosPublicos(
				descricaoModelo, descricaoCentroCusto, firstResult, maxResult,
				orderProperty, asc);

	}

	@Override
	public Long pequisarModelosPublicosCount(String descricaoModelo, String descricaoCentroCusto) {

		return this.getManterModeloBasicoON().pequisarModelosPublicosCount(descricaoModelo, descricaoCentroCusto);

	}

	@Override
	public void inserir(MpmModeloBasicoPrescricao modeloBasico)
			throws ApplicationBusinessException {
		this.getManterModeloBasicoON().incluir(modeloBasico);
	}

	@Override
	public void inserir(MpmModeloBasicoCuidado modeloBasicoCuidado)
			throws BaseException {
		this.getManterCuidadosModeloBasicoON().incluir(modeloBasicoCuidado);
	}

	@Override
	public void inserir(
			MpmModeloBasicoProcedimento modeloBasicoProcedimento,
			List<MpmModeloBasicoModoUsoProcedimento> listaModoUsoProdedimentoEspecial)
			throws BaseException {
		this.getManterProcedimentosModeloBasicoON().incluir(
				modeloBasicoProcedimento, listaModoUsoProdedimentoEspecial);
	}

	@Override
	public void alterar(MpmModeloBasicoPrescricao modeloBasico)
			throws ApplicationBusinessException {
		this.getManterModeloBasicoON().alterar(modeloBasico);
	}

	@Override
	public void excluirModeloBasico(Integer modeloBasicoSeq)
			throws ApplicationBusinessException {
		this.getManterModeloBasicoON().excluirModeloBasico(modeloBasicoSeq);
	}

	@Override
	public void excluir(Object object) throws BaseException {
		this.getManterItensModeloBasicoON().excluir(object);
	}

	@Override
	public void alterar(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {
		this.getManterDietasModeloBasicoON().alterar(itemDieta);
	}

	@Override
	public void excluir(MpmItemModeloBasicoDieta itemDieta)
			throws ApplicationBusinessException {
		this.getManterDietasModeloBasicoON().excluir(itemDieta);
	}

	@Override
	public MpmItemModeloBasicoDieta obterItemDieta(
			Integer modeloBasicoPrescricaoSeq, Integer modeloBasicoDietaSeq,
			Integer tipoItemDietaSeq) throws ApplicationBusinessException {
		return this.getManterDietasModeloBasicoON().obterItemDieta(
				modeloBasicoPrescricaoSeq, modeloBasicoDietaSeq,
				tipoItemDietaSeq);
	}

	@Override
	public boolean isAlterouDieta(MpmModeloBasicoDieta dieta) {
		return this.getManterDietasModeloBasicoON().isAlterouDieta(dieta);
	}

	// Manter Procedimentos Modelo Basico
	@Override
	public List<MpmModeloBasicoProcedimento> obterListaProcedimentos(
			MpmModeloBasicoPrescricao modeloBasicoPrescricao) {
		return this.getManterProcedimentosModeloBasicoON()
				.obterListaProcedimentos(modeloBasicoPrescricao);
	}

	@Override
	public List<MpmModeloBasicoModoUsoProcedimento> obterListaModoDeUsoDoModelo(
			MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento)
			throws ApplicationBusinessException {
		return this.getManterProcedimentosModeloBasicoON()
				.obterListaModoDeUsoDoModelo(mpmModeloBasicoProcedimento);
	}

    @Override
    public void removeListaModoDeUsoDoModelo(MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento) throws ApplicationBusinessException{
        this.getManterProcedimentosModeloBasicoON().removeListaModoDeUsoDoModelo(mpmModeloBasicoProcedimento);
    }

	@Override
	public Boolean incluirItensSelecionados(
			MpmPrescricaoMedica prescricaoMedica,
			List<ItensModeloBasicoVO> itens, String nomeMicrocomputador) throws BaseException {
		return this.getEscolherItensModeloBasicoON()
				.incluirItensSelecionados(prescricaoMedica, itens, nomeMicrocomputador);
	}

	// getters & setters
	private EscolherItensModeloBasicoON getEscolherItensModeloBasicoON() {
		return escolherItensModeloBasicoON;
	}

	private ManterModeloBasicoON getManterModeloBasicoON() {
		return manterModeloBasicoON;
	}

	private ManterItensModeloBasicoON getManterItensModeloBasicoON() {
		return manterItensModeloBasicoON;
	}

	private ManterDietasModeloBasicoON getManterDietasModeloBasicoON() {
		return manterDietasModeloBasicoON;
	}

	private ManterCuidadosModeloBasicoON getManterCuidadosModeloBasicoON() {
		return manterCuidadosModeloBasicoON;
	}

	private ManterMedicamentosModeloBasicoON getManterMedicamentosModeloBasicoON() {
		return manterMedicamentosModeloBasicoON;
	}

	private ManterProcedimentosModeloBasicoON getManterProcedimentosModeloBasicoON() {
		return manterProcedimentosModeloBasicoON;
	}


	@Override
	public String getDescricaoEditadaDieta(MpmModeloBasicoDieta dieta) {
		return this.getManterItensModeloBasicoON().getDescricaoEditadaDieta(
				dieta);
	}
	
	@Override
	public String getDescricaoEditadaMedicamento(MpmModeloBasicoMedicamento medicamento) {
		return this.getManterItensModeloBasicoON().getDescricaoEditadaMedicamento(medicamento);
	}

	@Override
	public String getDescricaoEditadaMedicamentoItem(
			MpmModeloBasicoMedicamento medicamento) {
		return this.getManterItensModeloBasicoON()
				.getDescricaoEditadaMedicamentoItem(medicamento);
	}

	@Override
	public String getDescricaoEditadaProcedimento(
			MpmModeloBasicoProcedimento procedimento) {
		return this.getManterItensModeloBasicoON()
				.getDescricaoEditadaProcedimento(procedimento);
	}
	
	@Override
	public String getDescricaoEditadaModeloBasicoProcedimento(MpmModeloBasicoProcedimento procedimento) {
		return this.getManterItensModeloBasicoON().getDescricaoEditadaModeloBasicoProcedimento(procedimento);
	}
	
	@Override
	public MpmModeloBasicoCuidado obterItemCuidado(MpmModeloBasicoCuidadoId id) {
		return this.getManterCuidadosModeloBasicoON().obterItemCuidado(id);
	}

	@Override
	public List<MpmCuidadoUsual> obterListaCuidadosUsuais(
			Object cuidadoUsualPesquisa) {
		return this.getManterCuidadosModeloBasicoON().obterListaCuidadoUsual(
				cuidadoUsualPesquisa);
	}
	
	@Override
	public Long obterListaCuidadoUsualCount(Object cuidadoUsualPesquisa) {
		return this.getManterCuidadosModeloBasicoON().obterListaCuidadoUsualCount(
				cuidadoUsualPesquisa); 
	}
	
	@Override
	public String obterDescricaoEditadaModeloBasicoCuidado(final Integer modeloBasicoPrescricaoSeq, final Integer seq) {
		return this.getManterCuidadosModeloBasicoON().obterDescricaoEditadaModeloBasicoCuidado(modeloBasicoPrescricaoSeq, seq);
	}
	
	@Override
	public String obterDescricaoEditadaModeloBasicoCuidado(MpmModeloBasicoCuidado modeloBasicoCuidado) {
		return this.getManterCuidadosModeloBasicoON().obterDescricaoEditadaModeloBasicoCuidado(modeloBasicoCuidado);
	}
	
	@Override
	public List<MpmModeloBasicoMedicamento> obterListaSolucoesDoModeloBasico(
			MpmModeloBasicoPrescricao modeloBasico) {
		return this.getMpmModeloBasicoMedicamentoDAO().pesquisarSolucoes(
				modeloBasico);
	}

	@Override
	public MpmModeloBasicoMedicamento obterModeloBasicoSolucao(
			Integer seqModelo, Integer seqItemModelo) {
		return this.getManterSolucaoModeloBasicoON().obterModeloBasicoSolucao(
				seqModelo, seqItemModelo);
	}

	private MpmModeloBasicoMedicamentoDAO getMpmModeloBasicoMedicamentoDAO() {
		return mpmModeloBasicoMedicamentoDAO;
	}

	@Override
	public List<MpmModeloBasicoMedicamento> obterListaMedicamentosModelo(
			Integer modeloBasicoPrescricaoSeq) {
		return this.getManterMedicamentosModeloBasicoON()
				.obterListaMedicamentos(modeloBasicoPrescricaoSeq);
	}

	@Override
	public void inserir(MpmItemModeloBasicoMedicamento itemMedicamento)
			throws ApplicationBusinessException {
		this.getManterMedicamentosModeloBasicoON().inserir(itemMedicamento);
	}

	@Override
	public void alterar(MpmItemModeloBasicoMedicamento itemMedicamento)
			throws ApplicationBusinessException {
		this.getManterMedicamentosModeloBasicoON().alterar(itemMedicamento);
	}

	@Override
	public List<MpmItemModeloBasicoMedicamento> obterItemMedicamento(
			Integer modeloBasicoPrescricaoSeq,
			Integer modeloBasicoMedicamentoSeq)
			throws ApplicationBusinessException {
		return this.getManterMedicamentosModeloBasicoON()
				.obterListaItensMedicamentos(modeloBasicoPrescricaoSeq,
						modeloBasicoMedicamentoSeq);
	}

	private ManterSolucaoModeloBasicoON getManterSolucaoModeloBasicoON() {
		return manterSolucaoModeloBasicoON;
	}

	@Override
	public String gravarSolucao(MpmModeloBasicoMedicamento solucao,
			List<MpmItemModeloBasicoMedicamento> itensModeloMedicamento, List<MpmItemModeloBasicoMedicamento> listaExcluidos)
			throws BaseException {
		return this.getManterSolucaoModeloBasicoON().gravarSolucao(solucao,
				itensModeloMedicamento, listaExcluidos);
	}

	@Override
	public void removerSolucoesSelecionadas(
			MpmModeloBasicoMedicamento modeloBasicoMedicamento)
			throws ApplicationBusinessException {
		this.getManterSolucaoModeloBasicoON().removerSolucoesSelecionadas(
				modeloBasicoMedicamento);
	}

	@Override
	public void alterar(
			MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento,
			List<MpmModeloBasicoModoUsoProcedimento> mpmModeloBasicoModoUsoProcedimentos)
			throws BaseException {
		this.getManterProcedimentosModeloBasicoON().alterar(
				mpmModeloBasicoProcedimento,
				mpmModeloBasicoModoUsoProcedimentos);
	}



	@Override
	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			String paramString) throws ApplicationBusinessException {
		return this.getPesquisarCuidadoUsualON().getListaUnidadesFuncionais(
				paramString);
	}

	@Override
	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais(
			RapServidores servidor) throws ApplicationBusinessException {
		return this.getPesquisarCuidadoUsualON().getListaUnidadesFuncionais(
				servidor);
	}

	private PesquisarCuidadoUsualON getPesquisarCuidadoUsualON() {
		return pesquisarCuidadoUsualON;
	}

	@Override
	public Long pesquisarCuidadoUsualCount(
			Integer codigoPesquisaCuidadoUsual,
			String descricaoPesquisaCuidadoUsual,
			DominioSituacao situacaoPesquisaCuidadoUsual,
			AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual) {
		return getPesquisarCuidadoUsualON().pesquisarCuidadoUsualCount(
				codigoPesquisaCuidadoUsual, descricaoPesquisaCuidadoUsual,
				situacaoPesquisaCuidadoUsual,
				unidadeFuncionalPesquisaCuidadoUsual);
	}

	@Override
	public List<MpmCuidadoUsual> pesquisarCuidadoUsual(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigoPesquisaCuidadoUsual,
			String descricaoPesquisaCuidadoUsual,
			DominioSituacao situacaoPesquisaCuidadoUsual,
			AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual) {

		return getPesquisarCuidadoUsualON().pesquisarCuidadoUsual(firstResult,
				maxResult, orderProperty, asc, codigoPesquisaCuidadoUsual,
				descricaoPesquisaCuidadoUsual, situacaoPesquisaCuidadoUsual,
				unidadeFuncionalPesquisaCuidadoUsual);
	}

	@Override
	public void excluir(Integer seqCuidado) throws ApplicationBusinessException {
		this.getPesquisarCuidadoUsualON().excluir(seqCuidado);
	}

	@Override
	public void copiarModeloBasico(Integer seq)
			throws BaseException {
		this.getManterModeloBasicoON().copiarModeloBasico(seq);
	}

	private ManterCuidadoUsualON getManterCuidadoUsualON() {
		return manterCuidadoUsualON;
	}

	private ManterCuidadoUsualONBTM getManterCuidadoUsualONBTM() {
		return manterCuidadoUsualONBTM;
	}

	@Override
	public void alterar(MpmCuidadoUsual cuidadoUsual, List<Short> ufsInseridas,  List<Short> ufsExcluidas) throws ApplicationBusinessException {
		this.getManterCuidadoUsualON().alterar(cuidadoUsual, ufsInseridas, ufsExcluidas);
	}

	@Override
	public void inserir(MpmCuidadoUsual cuidadoUsual, List<Short> ufsInseridas) throws ApplicationBusinessException {
		this.getManterCuidadoUsualON().inserir(cuidadoUsual, ufsInseridas);
	}

	@Override
	public List<MpmCuidadoUsualUnf> listaUnidadeFuncionalPorCuidadoUsual(
			Integer seqCuidado) throws ApplicationBusinessException {
		return getManterCuidadoUsualON().listaUnidadeFuncionalPorCuidadoUsual(
				seqCuidado);
	}
	
	@Override
	public MpmTipoFrequenciaAprazamento obterTipoFrequenciaAprazamento(
			Short seq) {
		return this.getManterDietasModeloBasicoON().obterTipoFrequenciaAprazamento(seq);
	}
	
	@Override
	public List<MpmCuidadoUsualUnf> listaUnidadeFuncionalPorCuidadoUsual() {
		return getManterCuidadoUsualON().listaUnidadeFuncionalPorCuidadoUsual();
	}
	
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade#pesquisarCuidadoUsual(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, java.lang.Integer, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao, br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	@Override
	public List<MpmCuidadoUsualUnf> pesquisarCuidadoUsualUnf(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual) {
		return getPesquisarCuidadoUsualON().pesquisarCuidadoUsualUnf(firstResult,
				maxResult, orderProperty, asc, 
				unidadeFuncionalPesquisaCuidadoUsual);
	}	

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade#pesquisarCuidadoUsualCount(java.lang.Integer, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao, br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	@Override
	public Integer pesquisarCuidadoUsualUnfCount(AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual) {
		return getPesquisarCuidadoUsualON().pesquisarCuidadoUsualUnfCount(unidadeFuncionalPesquisaCuidadoUsual);
	}
	
	/* (non-Javadoc)
	 * @see 
	 */
	@Override
	public void excluir(MpmCuidadoUsualUnf mpmCuidadoUsualunf, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		this.getManterCuidadoUsualON().excluir(mpmCuidadoUsualunf,servidorLogado);
	}

	/* (non-Javadoc)
	 * @see 
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void incluirTodosCuidadosUnf(RapServidores rapServidores) throws ApplicationBusinessException {
		this.getManterCuidadoUsualONBTM().incluirTodosCuidadosUnf(rapServidores);
	}	

	/* (non-Javadoc)
	 * @see 
	 */
	@Override
	public void incluirCuidadosUnf(AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual,RapServidores rapServidores) throws ApplicationBusinessException {
		this.getManterCuidadoUsualON().incluirCuidadosUnf(unidadeFuncionalPesquisaCuidadoUsual,rapServidores);
	}
}