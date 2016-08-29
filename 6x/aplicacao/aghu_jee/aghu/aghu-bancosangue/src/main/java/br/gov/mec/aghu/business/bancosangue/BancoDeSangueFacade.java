package br.gov.mec.aghu.business.bancosangue;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.LockMode;

import br.gov.mec.aghu.bancosangue.dao.AbsAmostrasPacientesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsCandidatosDoadoresDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsComponentePesoFornecedorDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsComponenteSanguineoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsDoacoesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsEstoqueComponentesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsExameComponenteVisualPrescricaoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsFornecedorBolsasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsGrupoJustificativaComponenteSanguineoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsItemSolicitacaoHemoterapicaJustificativaDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsItensSolHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsJustificativaComponenteSanguineoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsMovimentosComponentesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsOrientacaoComponentesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsProcedHemoterapicoDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsRegSanguineoPacientesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesDoacoesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesPorAmostraDAO;
import br.gov.mec.aghu.bancosangue.dao.VAbsMovimentoComponenteDAO;
import br.gov.mec.aghu.bancosangue.dao.ValidadeAmostraDAO;
import br.gov.mec.aghu.bancosangue.vo.AtualizaCartaoPontoVO;
import br.gov.mec.aghu.bancosangue.vo.ItemSolicitacaoHemoterapicaVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.dominio.DominioMcoType;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioTipoPaciente;
import br.gov.mec.aghu.faturamento.vo.DoacaoColetaSangueVO;
import br.gov.mec.aghu.faturamento.vo.DoadorSangueTriagemClinicaVO;
import br.gov.mec.aghu.model.AbsAmostrasPacientes;
import br.gov.mec.aghu.model.AbsAmostrasPacientesId;
import br.gov.mec.aghu.model.AbsCandidatosDoadores;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedor;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedorId;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsDoacoes;
import br.gov.mec.aghu.model.AbsEstoqueComponentes;
import br.gov.mec.aghu.model.AbsExameComponenteVisualPrescricao;
import br.gov.mec.aghu.model.AbsFornecedorBolsas;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativaId;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicasId;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsMovimentosComponentes;
import br.gov.mec.aghu.model.AbsOrientacaoComponentes;
import br.gov.mec.aghu.model.AbsOrientacaoComponentesId;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsRegSanguineoPacientes;
import br.gov.mec.aghu.model.AbsSolicitacoesDoacoes;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AbsSolicitacoesPorAmostra;
import br.gov.mec.aghu.model.AbsValidAmostrasComponenteId;
import br.gov.mec.aghu.model.AbsValidAmostrasComponentes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.VAbsMovimentoComponente;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

/**
 * Porta de entrada do módulo Exames e Laudos.
 */
@Modulo(ModuloEnum.BANCO_SANGUE)
@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects" })
@Stateless
public class BancoDeSangueFacade extends BaseFacade implements IBancoDeSangueFacade {

	@EJB
	private JustificativaItemSolicitacaoHemoterapicaRN justificativaItemSolicitacaoHemoterapicaRN;
	@EJB
	private ManterComponentesSanguineosON manterComponentesSanguineosON;
	@EJB
	private AbsOrientacaoComponentesON absOrientacaoComponentesON;
	@EJB
	private PesquisarExamesDaHemoterapiaRN pesquisarExamesDaHemoterapiaRN;
	@EJB
	private ValidadeAmostraRN validadeAmostraRN;
	@EJB
	private GrupoJustificativaHemoterapiaON grupoJustificativaHemoterapiaON;
	@EJB
	private ManterPesoFornecedorON manterPesoFornecedorON;
	@EJB
	private SolicitacaoHemoterapicaRN solicitacaoHemoterapicaRN;
	@EJB
	private BancoDeSangueON bancoDeSangueON;
	@EJB
	private AbsComponenteSanguineoRN absComponenteSanguineoRN;
	@EJB
	private ItemSolicitacaoHemoterapicaRN itemSolicitacaoHemoterapicaRN;
	@EJB
	private ManterProcedHemoterapicoON manterProcedHemoterapicoON;
	@EJB
	private ProcedimentoHemoterapicoON procedimentoHemoterapicoON;
	@EJB
	private AbsSolicitacoesPorAmostraRN absSolicitacoesPorAmostraRN;
	
	@Inject
	private AbsComponenteSanguineoDAO absComponenteSanguineoDAO;
	
	@Inject
	private AbsSolicitacoesHemoterapicasDAO absSolicitacoesHemoterapicasDAO;
	
	@Inject
	private AbsEstoqueComponentesDAO absEstoqueComponentesDAO;
	
	@Inject
	private AbsItensSolHemoterapicasDAO absItensSolHemoterapicasDAO;
	
	@Inject
	private AbsProcedHemoterapicoDAO absProcedHemoterapicoDAO;
	
	@Inject
	private AbsExameComponenteVisualPrescricaoDAO absExameComponenteVisualPrescricaoDAO;
	
	@Inject
	private AbsJustificativaComponenteSanguineoDAO absJustificativaComponenteSanguineoDAO;
	
	@Inject
	private AbsItemSolicitacaoHemoterapicaJustificativaDAO absItemSolicitacaoHemoterapicaJustificativaDAO;
	
	@Inject
	private ValidadeAmostraDAO validadeAmostraDAO;
	
	@Inject
	private AbsGrupoJustificativaComponenteSanguineoDAO absGrupoJustificativaComponenteSanguineoDAO;
	
	@Inject
	private AbsSolicitacoesPorAmostraDAO absSolicitacoesPorAmostraDAO;
	
	@Inject
	private AbsCandidatosDoadoresDAO absCandidatosDoadoresDAO;
	
	@Inject
	private AbsFornecedorBolsasDAO absFornecedorBolsasDAO;
	
	@Inject
	private AbsOrientacaoComponentesDAO absOrientacaoComponentesDAO;
	
	@Inject
	private AbsSolicitacoesDoacoesDAO absSolicitacoesDoacoesDAO;
	
	@Inject
	private VAbsMovimentoComponenteDAO vAbsMovimentoComponenteDAO;
	
	@Inject
	private AbsMovimentosComponentesDAO absMovimentosComponentesDAO;
	
	@Inject
	private AbsDoacoesDAO absDoacoesDAO;
	
	@Inject
	private AbsAmostrasPacientesDAO absAmostrasPacientesDAO;
	
	@Inject
	private AbsRegSanguineoPacientesDAO absRegSanguineoPacientesDAO;
	
	@Inject
	private AbsComponentePesoFornecedorDAO absComponentePesoFornecedorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3664625436510295650L;
	
	/**
	 * Busca por pk um objeto de AbsItemSolicitacaoHemoterapicaJustificativa.
	 * 
	 * @param id
	 * @return
	 */
	public AbsItemSolicitacaoHemoterapicaJustificativa getAbsItemSolicitacaoHemoterapicaJustificativa(AbsItemSolicitacaoHemoterapicaJustificativaId id) {
		return getAbsItemSolicitacaoHemoterapicaJustificativaDAO().obterPorChavePrimaria(id);
	}
	

	@Override
	public String buscaJustificativaLaudoCsa(final Integer atdSeq, final Integer phiSeq) {
		return getBancoDeSangueON().buscaJustificativaLaudoCsa(atdSeq, phiSeq);
	}

	@Override
	public String buscaJustificativaLaudoPhe(final Integer atdSeq, final Integer phiSeq) {
		return getBancoDeSangueON().buscaJustificativaLaudoPhe(atdSeq, phiSeq);
	}

	@Override
	public AtualizaCartaoPontoVO atualizaCartaoPontoServidor()
			throws ApplicationBusinessException {
		return getBancoDeSangueON().atualizaCartaoPontoServidor();
	}

	@Override
	public AbsComponenteSanguineo obterComponeteSanguineoPorCodigo(final String codigo) {
		return this.getAbsComponenteSanguineoDAO()
				.obterComponeteSanguineoPorCodigo(codigo);
	}

	@Override
	public AbsProcedHemoterapico obterProcedHemoterapicoPorCodigo(final String codigo) {
		return this.getAbsProcedHemoterapicoDAO()
				.obterProcedHemoterapicoPorCodigo(codigo);
	}

	protected BancoDeSangueON getBancoDeSangueON() {
		return bancoDeSangueON;
	}

	protected VAbsMovimentoComponenteDAO getVAbsMovimentoComponenteDAO() {
		return vAbsMovimentoComponenteDAO;
	}

	private ItemSolicitacaoHemoterapicaRN getItemSolicitacaoHemoterapicaRN() {
		return itemSolicitacaoHemoterapicaRN;
	}

	@Override
	public void excluirItemSolicitacaoHemoterapica(
			final AbsItensSolHemoterapicas itensSolHemoterapicas)
			throws ApplicationBusinessException {
		getItemSolicitacaoHemoterapicaRN().excluirItemSolicitacaoHemoterapica(
				itensSolHemoterapicas);

	}

	@Override
	public AbsItensSolHemoterapicas inserirItemSolicitacaoHemoterapica(
			final AbsItensSolHemoterapicas itemSolHemoterapica)
			throws BaseException {
		return getItemSolicitacaoHemoterapicaRN()
				.inserirItemSolicitacaoHemoterapica(itemSolHemoterapica);
	}

	@Override
	public AbsItensSolHemoterapicas atualizarItemSolicitacaoHemoterapica(
			final AbsItensSolHemoterapicas itensSolHemoterapica)
			throws BaseException {
		return getItemSolicitacaoHemoterapicaRN()
				.atualizarItemSolicitacaoHemoterapica(itensSolHemoterapica);
	}

	private JustificativaItemSolicitacaoHemoterapicaRN getJustificativaItemSolicitacaoHemoterapicaRN() {
		return justificativaItemSolicitacaoHemoterapicaRN;
	}

	@Override
	public AbsItemSolicitacaoHemoterapicaJustificativa atualizarJustificativaItemSolicitacaoHemoterapica(
			final AbsItemSolicitacaoHemoterapicaJustificativa item)
			throws ApplicationBusinessException {
		return this.getJustificativaItemSolicitacaoHemoterapicaRN()
				.atualizarJustificativaItemSolicitacaoHemoterapica(item);

	}

	@Override
	public AbsItemSolicitacaoHemoterapicaJustificativa inserirJustificativaItemSolicitacaoHemoterapica(
			final AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa)
			throws ApplicationBusinessException {
		return this.getJustificativaItemSolicitacaoHemoterapicaRN()
				.inserirJustificativaItemSolicitacaoHemoterapica(
						itemSolicitacaoHemoterapicaJustificativa);
	}

	@Override
	public void excluirJustificativaItemSolicitacaoHemoterapica(
			final AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa) throws ApplicationBusinessException {
		this.getJustificativaItemSolicitacaoHemoterapicaRN()
				.excluirJustificativaItemSolicitacaoHemoterapica(
						itemSolicitacaoHemoterapicaJustificativa);

	}
	
	
	private SolicitacaoHemoterapicaRN getSolicitacaoHemoterapicaRN() {
		return solicitacaoHemoterapicaRN;
	}

	@Override
	public AbsSolicitacoesHemoterapicas atualizarSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador) throws BaseException {
		return getSolicitacaoHemoterapicaRN().atualizarSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
	}

	@Override
	public void inserirSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica, String nomeMicrocomputador) throws ApplicationBusinessException {
		this.getSolicitacaoHemoterapicaRN().inserirSolicitacaoHemoterapica(solicitacaoHemoterapica, nomeMicrocomputador);
		
	}
	
	/**
	 * Remove todas as solicitações hemoterápicas de um atendimento.
	 * 
	 * @param atendimento
	 */
	@Override
	public void excluirSolicitacoesHemoterapicasPorAtendimeto(final AghAtendimentos atendimento){
		this.getSolicitacaoHemoterapicaRN().excluirSolicitacoesHemoterapicasPorAtendimeto(atendimento);
	}

	@Override
	public void excluirSolicitacaoHemoterapica(
			final AbsSolicitacoesHemoterapicas solicitacaoHemoterapica) throws ApplicationBusinessException {
		this.getSolicitacaoHemoterapicaRN().excluirSolicitacaoHemoterapica(solicitacaoHemoterapica);
		
	}
	
	
	
	@Override
	public List<AbsProcedHemoterapico> listarAbsProcedHemoterapicos(final Object objPesquisa){
		return this.getAbsProcedHemoterapicoDAO().listarAbsProcedHemoterapicos(objPesquisa);
	}
	
	@Override
	public Long listarAbsProcedHemoterapicoCount(final Object objPesquisa){
		return this.getAbsProcedHemoterapicoDAO().listarAbsProcedHemoterapicoCount(objPesquisa);
	}
	
	@Override
	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineos(final Object objPesquisa){
		return this.getAbsComponenteSanguineoDAO().listarAbsComponenteSanguineos(objPesquisa);
	}
	
	@Override
	public Long listarAbsComponenteSanguineosCount(final Object objPesquisa){
		return this.getAbsComponenteSanguineoDAO().listarAbsComponenteSanguineosCount(objPesquisa);
	}
	
	@Override
	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineosAtivos(final Object objPesquisa){
		return this.getAbsComponenteSanguineoDAO().listarAbsComponenteSanguineosAtivos(objPesquisa);
	}
	
	@Override
	public Long listarAbsComponenteSanguineosAtivosCount(final Object objPesquisa){
		return this.getAbsComponenteSanguineoDAO().listarAbsComponenteSanguineosAtivosCount(objPesquisa);
	}

	
	@Override
	public void cancelaAmostrasHemoterapicas(final AghAtendimentos atendimento, final AinTiposAltaMedica tipoAltaMedica, String nomeMicrocomputador) throws ApplicationBusinessException {
		this.getSolicitacaoHemoterapicaRN().cancelaAmostrasHemoterapicas(atendimento, tipoAltaMedica, nomeMicrocomputador);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarCandidatosDoadoresPorNacionalidadeCount(
			final AipNacionalidades nacionalidade){
		return getAbsCandidatosDoadoresDAO().pesquisarCandidatosDoadoresPorNacionalidadeCount(nacionalidade);
	}
	
	@Override
	@BypassInactiveModule
	public Long pesquisarCandidatosDoadoresPorOcupacaoCount(
			final AipOcupacoes ocupacao){
		return getAbsCandidatosDoadoresDAO().pesquisarCandidatosDoadoresPorOcupacaoCount(ocupacao);
	}
	
	protected AbsCandidatosDoadoresDAO getAbsCandidatosDoadoresDAO(){
		return absCandidatosDoadoresDAO;
	}
	
	@Override
	@BypassInactiveModule
	public List<VAbsMovimentoComponente> pesquisarItensSolHemoterapicasPOL(
			final Integer codigoPaciente) {
		
		return this.getSolicitacaoHemoterapicaRN().pesquisarItensSolHemoterapicasPOL(codigoPaciente);
	}
		
	protected GrupoJustificativaHemoterapiaON getGrupoJustificativaHemoterapiaON() {
		return grupoJustificativaHemoterapiaON;
	}
	
	@Override
	public List<AbsGrupoJustificativaComponenteSanguineo> pesquisarAbsGrupoJustificativaComponenteSanguineo(
			int firstResult, int maxResults, String orderProperty, Boolean asc,
			Short seq, String descricao, DominioSituacao situacao, String titulo) {
		return getGrupoJustificativaHemoterapiaON().pesquisarGrupoJustificativaComponenteSanguineo(
				firstResult, maxResults, orderProperty, asc, seq, descricao, situacao, titulo);
	}

	@Override
	public Long pesquisarAbsGrupoJustificativaComponenteSanguineoCount(
			Short seq, String descricao, DominioSituacao situacao, String titulo) {
		return getGrupoJustificativaHemoterapiaON().pesquisarGrupoJustificativaComponenteSanguineoCount(seq, descricao, situacao, titulo);
	}
	
	@Override
	public AbsGrupoJustificativaComponenteSanguineo obterGrupoJustificativaComponenteSanguineo(Short seq) {
		return getGrupoJustificativaHemoterapiaON().obterGrupoJustificativaComponenteSanguineo(seq);
	}
	
	@Override
	public void persistirGrupoJustificativaComponenteSanguineo(AbsGrupoJustificativaComponenteSanguineo grupo) throws ApplicationBusinessException {
		getGrupoJustificativaHemoterapiaON().persistirGrupoJustificativaComponenteSanguineo(grupo);
	}
	
	@Override
	public void persistirJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativa) throws BaseListException, ApplicationBusinessException {
		getGrupoJustificativaHemoterapiaON().persistirJustificativaComponenteSanguineo(justificativa);
	}
	
	protected ManterProcedHemoterapicoON getManterProcedHemoterapicoON(){
		return manterProcedHemoterapicoON;
	}
	
	@Override
	public AbsGrupoJustificativaComponenteSanguineo obterGrupoJustificativaPorId(Short seq) {
		return getGrupoJustificativaHemoterapiaON().obterGrupoJustificativaPorId(seq);
	}
	
	@Override
	public List<AbsProcedHemoterapico> listarAbsProcedHemoterapicos(String codigo, String descricao, Boolean indAmostra, Boolean indJustificativa, DominioSituacao indSituacao, int firstResult, int maxResults){
		return getManterProcedHemoterapicoON().listarAbsProcedHemoterapicos(codigo, descricao, indAmostra, indJustificativa, indSituacao, firstResult, maxResults);
	}
	
	@Override
	public Long listarAbsProcedHemoterapicosCount(String codigo, String descricao, Boolean indAmostra, Boolean indJustificativa, DominioSituacao indSituacao){
		return getManterProcedHemoterapicoON().listarAbsProcedHemoterapicosCount(codigo, descricao, indAmostra, indJustificativa, indSituacao);
	}
	
	protected AbsJustificativaComponenteSanguineoDAO getAbsJustificativaComponenteSanguineoDAO() {
		return absJustificativaComponenteSanguineoDAO;
	}
	
	@Override
	public List<AbsJustificativaComponenteSanguineo> pesquisarJustificativaComponenteSanguineosPorGrupoJustificativaCompomenteSanguineo(Short seqGrupoJustificativaComponenteSanguineo) {
		return getAbsJustificativaComponenteSanguineoDAO().pesquisarJustificativaComponenteSanguineosPorGrupoJustificativaCompomenteSanguineo(seqGrupoJustificativaComponenteSanguineo);
	}
	
	protected ProcedimentoHemoterapicoON getProcedimentoHemoterapicoON() {
		return procedimentoHemoterapicoON;
	}
	
	protected AbsProcedHemoterapicoDAO getAbsProcedHemoterapicoDAO(){
		return absProcedHemoterapicoDAO;
	}
	
	@Override
	public void persistirProcedimentoHemoterapico(AbsProcedHemoterapico procedimento) throws BaseException {
		getProcedimentoHemoterapicoON().persistirProcedimentoHemoterapico(procedimento);
	}

	@Override
	public void excluirProcedimentoHemoterapico(String codigo) throws ApplicationBusinessException {
		getProcedimentoHemoterapicoON().excluirProcedimentoHemoterapico(codigo);
	}
	
	@Override
	public AbsProcedHemoterapico obterProcedimentoHemoterapicoPorCodigo(String codigo){
		return getAbsProcedHemoterapicoDAO().obterPorChavePrimaria(codigo);
	}

	@Override
	public List<AbsProcedHemoterapico> pesquisarProcedimentoHemoterapicoPorCodigoDescricao(
			Object param) {
		return getAbsProcedHemoterapicoDAO().pesquisarProcedimentosHemoterapicosPorCodigoDescricao(param);
	}
	

	/**
	 * 
	 */
	@Override
	public List<AbsJustificativaComponenteSanguineo> pesquisarJustificativaUsoHemoterapico(Integer seq,
																						   String codigoComponenteSanguineo,
																						   String codigoProcedimentoHemoterapico,
																						   Short seqGrupoJustificativaComponenteSanguineo,
																						   DominioTipoPaciente tipoPaciente, 
																						   DominioSimNao descricaoLivre,
																						   DominioSituacao situacao, 
																						   String descricao,
																						   int firstResult, 
																						   int maxResult) {
		
		return getAbsJustificativaComponenteSanguineoDAO().pesquisarJustificativaUsoHemoterapico(seq,
																								 codigoComponenteSanguineo,
																								 codigoProcedimentoHemoterapico,
																								 seqGrupoJustificativaComponenteSanguineo, 
																								 tipoPaciente,
																								 descricaoLivre, 
																								 situacao, 
																								 descricao,
																								 firstResult,
																								 maxResult);
	}
	

	@Override
	public Long pesquisarJustificativaUsoHemoterapicoCount(Integer seq,
													          String codigoComponenteSanguineo,
												 			  String codigoProcedimentoHemoterapico,
												 			  Short seqGrupoJustificativaComponenteSanguineo,
												 			  DominioTipoPaciente tipoPaciente,
												 			  DominioSimNao descricaoLivre,
												 			  DominioSituacao situacao,
												 			  String descricao) {
		
		return getAbsJustificativaComponenteSanguineoDAO().pesquisarJustificativaUsoHemoterapicoCount(seq,
																								      codigoComponenteSanguineo, 
																								      codigoProcedimentoHemoterapico, 
																								      seqGrupoJustificativaComponenteSanguineo, 
																								      tipoPaciente, 
																								      descricaoLivre, 
																								      situacao, 
																								      descricao);
	}
	
	protected AbsGrupoJustificativaComponenteSanguineoDAO getAbsGrupoJustificativaComponenteSanguineoDAO() {
		return absGrupoJustificativaComponenteSanguineoDAO;
	}

	@Override
	public AbsJustificativaComponenteSanguineo obterAbsJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativa) {
		return getAbsJustificativaComponenteSanguineoDAO().obterOriginal(justificativa);
	}

	/**
	 * Pesquisa utilizada na suggestion box estoria #6399
	 */
	@Override
	public List<AbsGrupoJustificativaComponenteSanguineo> pesquisarGrupoJustificativaComponenteSanguineo(String param) {
		return getAbsGrupoJustificativaComponenteSanguineoDAO().pesquisarGrupoJustificativaComponenteSanguineo(param);
	}

	@Override
	public AbsJustificativaComponenteSanguineo obterAbsJustificativaComponenteSanguineoPorSeq(Integer seq, Enum... fetchArgs) {	
		return getAbsJustificativaComponenteSanguineoDAO().obterPorChavePrimaria(seq, true, fetchArgs);
	}
	
	protected AbsAmostrasPacientesDAO getAbsAmostrasPacientesDAO() {
		return absAmostrasPacientesDAO;
	}
	
	protected AbsDoacoesDAO getAbsDoacoesDAO() {
		return absDoacoesDAO;
	}
	
	protected AbsEstoqueComponentesDAO getAbsEstoqueComponentesDAO() {
		return absEstoqueComponentesDAO;
	}
	
	protected AbsMovimentosComponentesDAO getAbsMovimentosComponentesDAO() {
		return absMovimentosComponentesDAO;
	}
	
	protected AbsRegSanguineoPacientesDAO getAbsRegSanguineoPacientesDAO() {
		return absRegSanguineoPacientesDAO;
	}
	
	protected AbsSolicitacoesDoacoesDAO getAbsSolicitacoesDoacoesDAO() {
		return absSolicitacoesDoacoesDAO;
	}
	
	protected AbsSolicitacoesPorAmostraDAO getAbsSolicitacoesPorAmostraDAO() {
		return absSolicitacoesPorAmostraDAO;
	}

	@Override
	public List<AbsSolicitacoesDoacoes> pesquisarAbsSolicitacoesDoacoes(
			Integer pacCodigo) {
		return this.getAbsSolicitacoesDoacoesDAO()
				.pesquisarAbsSolicitacoesDoacoes(pacCodigo);
	}

	@Override
	public void removerAbsSolicitacoesDoacoes(
			AbsSolicitacoesDoacoes absSolicitacoesDoacoes, boolean flush) {
		this.getAbsSolicitacoesDoacoesDAO().remover(
				absSolicitacoesDoacoes);
		if (flush){
			this.getAbsSolicitacoesDoacoesDAO().flush();
		}
	}

	@Override
	public Short obterMaxSeqAbsSolicitacoesDoacoes(Integer pacCodigo) {
		return this.getAbsSolicitacoesDoacoesDAO()
				.obterMaxSeqAbsSolicitacoesDoacoes(pacCodigo);
	}

	@Override
	public AbsSolicitacoesDoacoes inserirAbsSolicitacoesDoacoes(
			AbsSolicitacoesDoacoes absSolicitacoesDoacoes, boolean flush) {
		this.getAbsSolicitacoesDoacoesDAO().persistir(absSolicitacoesDoacoes);
		if (flush) {
			this.getAbsSolicitacoesDoacoesDAO().flush();
		}
		return absSolicitacoesDoacoes;
	}

	@Override
	public List<AbsSolicitacoesPorAmostra> pesquisarAbsSolicitacoesPorAmostra(
			Integer pacCodigo) {
		return this.getAbsSolicitacoesPorAmostraDAO()
				.pesquisarAbsSolicitacoesPorAmostra(pacCodigo);
	}

	@Override
	public List<AbsRegSanguineoPacientes> listarRegSanguineoPacientesPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getAbsRegSanguineoPacientesDAO()
				.listarRegSanguineoPacientesPorCodigoPaciente(pacCodigo);
	}

	@Override
	public void persistirAbsRegSanguineoPacientes(
			AbsRegSanguineoPacientes absRegSanguineoPacientes) {
		this.getAbsRegSanguineoPacientesDAO().persistir(
				absRegSanguineoPacientes);
	}

	@Override
	public List<AbsMovimentosComponentes> listarMovimentosComponentesPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getAbsMovimentosComponentesDAO()
				.listarMovimentosComponentesPorCodigoPaciente(pacCodigo);
	}

	@Override
	public void persistirAbsMovimentosComponentes(
			AbsMovimentosComponentes absMovimentosComponentes) {
		this.getAbsMovimentosComponentesDAO().persistir(
				absMovimentosComponentes);
	}

	@Override
	public void persistirAbsEstoqueComponentes(
			AbsEstoqueComponentes absEstoqueComponentes) {
		this.getAbsEstoqueComponentesDAO().persistir(absEstoqueComponentes);
	}

	@Override
	public List<AbsEstoqueComponentes> listarEstoqueComponentesPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getAbsEstoqueComponentesDAO()
				.listarEstoqueComponentesPorCodigoPaciente(pacCodigo);
	}

	@Override
	public void atualizarAbsDoacoes(AbsDoacoes absDoacoes, boolean flush) {
		this.getAbsDoacoesDAO().atualizar(absDoacoes);
		if(flush){
			this.getAbsDoacoesDAO().flush();
		}
	}

	@Override
	public AbsAmostrasPacientes obterAbsAmostrasPacientesPorChavePrimaria(
			AbsAmostrasPacientesId id) {
		return this.getAbsAmostrasPacientesDAO().obterPorChavePrimaria(id);
	}

	@Override
	public void persistirAbsAmostrasPacientes(
			AbsAmostrasPacientes absAmostrasPacientes) {
		this.getAbsAmostrasPacientesDAO().persistir(absAmostrasPacientes);
	}

	@Override
	public void desatacharAbsAmostrasPacientes(
			AbsAmostrasPacientes absAmostrasPacientes) {
		this.getAbsAmostrasPacientesDAO().desatachar(absAmostrasPacientes);
	}

	@Override
	public void removerAbsAmostrasPacientes(
			AbsAmostrasPacientes absAmostrasPacientes, boolean flush) {
		this.getAbsAmostrasPacientesDAO().remover(absAmostrasPacientes);
		if (flush){
			this.getAbsAmostrasPacientesDAO().flush();
		}
	}

	@Override
	public List<AbsAmostrasPacientes> pesquisarAmostrasPaciente(
			Integer pacCodigo) {
		return this.getAbsAmostrasPacientesDAO().pesquisarAmostrasPaciente(
				pacCodigo);
	}

	@Override	
	public List<ItemSolicitacaoHemoterapicaVO> obterListaItemSolicitacaoHemoterapicaVO(Integer atdSeq, Integer seq){
		return getItensSolHemoterapicasDAO().obterListaItemSolicitacaoHemoterapicaVO(atdSeq, seq);
	}
	
	@Override
	public AbsItensSolHemoterapicas obterItemSolicitacaoHemoterapicaVO(AbsItensSolHemoterapicasId id){
		return getItensSolHemoterapicasDAO().obterPorChavePrimaria(id);
	}
	
	@Override
	public List<AbsItensSolHemoterapicas> pesquisarItensHemoterapiaComponentesSanguineos(Integer sheAtdSeq, Integer sheSeq){
		return getItensSolHemoterapicasDAO().pesquisarItensHemoterapiaComponentesSanguineos(sheAtdSeq, sheSeq);
	}

	@Override
	@BypassInactiveModule
	public void refreshSolicitacoesHeoterapicas(AbsSolicitacoesHemoterapicas elemento){
		getAbsSolicitacoesHemoterapicasDAO().refresh(elemento);
	}
	
	@Override	
	public void refreshAbsItemSolicitacaoHemoterapicaJustificativa(AbsItemSolicitacaoHemoterapicaJustificativa elemento){
		getAbsItemSolicitacaoHemoterapicaJustificativaDAO().refresh(elemento);
	}	
	
	
	@Override
	public List<AbsSolicitacoesHemoterapicas> pesquisarSolicitacoesHemoterapicasRelatorio(
			MpmPrescricaoMedica prescricaoMedica) {
		return getAbsSolicitacoesHemoterapicasDAO().pesquisarSolicitacoesHemoterapicasRelatorio(prescricaoMedica);
	}
	
	@Override	
	public List<AbsItensSolHemoterapicas> pesquisarItensHemoterapiaProcedimentos(Integer sheAtdSeq, Integer sheSeq){
		return getItensSolHemoterapicasDAO().pesquisarItensHemoterapiaProcedimentos(sheAtdSeq, sheSeq);
	}
	

	@Override	
	public List<AbsItemSolicitacaoHemoterapicaJustificativa> pesquisarJustificativasItemHemoterapia(Integer sheAtdSeq,
			Integer sheSeq, Short ishSequencia){
		return getAbsItemSolicitacaoHemoterapicaJustificativaDAO().pesquisarJustificativasItemHemoterapia(sheAtdSeq, sheSeq, ishSequencia);
	}
	

	@Override	
	public List<AbsComponenteSanguineo> obterComponetesSanguineosAtivos(){
		return getAbsComponenteSanguineoDAO().obterComponetesSanguineosAtivos();
	}

	@Override	
	public List<AbsProcedHemoterapico> obterProcedHemoterapicosAtivos(){
		return getAbsProcedHemoterapicoDAO().obterProcedHemoterapicosAtivos();
	}
	
	@Override
	@BypassInactiveModule
	public List<AbsSolicitacoesHemoterapicas> pesquisarTodasHemoterapiasPrescricaoMedica(
			MpmPrescricaoMedicaId id) {
		return getAbsSolicitacoesHemoterapicasDAO().pesquisarTodasHemoterapiasPrescricaoMedica(id);
	}
	

	protected AbsComponenteSanguineoDAO getAbsComponenteSanguineoDAO(){
		return absComponenteSanguineoDAO;
	}
	
	protected AbsItensSolHemoterapicasDAO getItensSolHemoterapicasDAO(){
		return absItensSolHemoterapicasDAO;
	}	
	
	protected AbsItemSolicitacaoHemoterapicaJustificativaDAO getAbsItemSolicitacaoHemoterapicaJustificativaDAO(){
		return absItemSolicitacaoHemoterapicaJustificativaDAO;
	}	
	
	
	public AbsItemSolicitacaoHemoterapicaJustificativa getObterPorChavePrimariaAbsItemSolicitacaoHemoterapicaJustificativa(
			final AbsItemSolicitacaoHemoterapicaJustificativaId id) {
		return this.getAbsItemSolicitacaoHemoterapicaJustificativaDAO()
				.obterPorChavePrimaria(id);
	}

	public List<AbsJustificativaComponenteSanguineo> listarJustificativasPadraoDoComponenteOuProcedimento(
			final String procedHemoterapicoCodigo, final String componenteSanguineoCodigo,
			final Short seqGrupoJustificativaComponenteSanguineo,
			final Boolean indPacPediatrico) {
		return this.getAbsJustificativaComponenteSanguineoDAO()
				.listarJustificativasPadraoDoComponenteOuProcedimento(
						procedHemoterapicoCodigo, componenteSanguineoCodigo,
						seqGrupoJustificativaComponenteSanguineo,
						indPacPediatrico);

	}
	
	public List<AbsGrupoJustificativaComponenteSanguineo> listarGruposJustifcativasDoComponenteOuProcedimento(
			final String procedHemoterapicoCodigo, final String componenteSanguineoCodigo) {
		return this.getAbsGrupoJustificativaComponenteSanguineoDAO()
				.listarGruposJustifcativasDoComponenteOuProcedimento(
						procedHemoterapicoCodigo, componenteSanguineoCodigo);
	}	
	
	public AbsSolicitacoesHemoterapicas obterSolicitacoesHemoterapicasComItensSolicitacoes(
			final Integer atdSeq, final Integer seq) {
		return this.getManterProcedHemoterapicoON().obterPorChavePrimariaComItensSolicitacoes(atdSeq, seq);
	}
	
	public AbsSolicitacoesHemoterapicas obterSolicitacoesHemoterapicas(
			final Integer atdSeq, final Integer seq) {
		return this.getAbsSolicitacoesHemoterapicasDAO().obterPorChavePrimaria(new AbsSolicitacoesHemoterapicasId(atdSeq, seq));
	}
	
	public AbsItensSolHemoterapicas obterItemSolHemoterapica(final Integer atdSeq,
			final Integer seq, final Short sequencia) {
		return this.getItensSolHemoterapicasDAO().obterPorChavePrimaria(
				new AbsItensSolHemoterapicasId(atdSeq, seq, sequencia));
	}
	
	public String buscarDescricaoJustificativaSolicitacao(
			final String procedHemoterapicoCodigo, final String componenteSanguineoCodigo) {
		return this.getAbsGrupoJustificativaComponenteSanguineoDAO()
				.buscarDescricaoJustificativaSolicitacao(
						procedHemoterapicoCodigo, componenteSanguineoCodigo);
	}
	
	public List<AbsComponenteSanguineo> listaComponentesSanguineos(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AbsComponenteSanguineo componenteSanguineo) {
		return this.getAbsComponenteSanguineoDAO().pesquisarComponentesSanguineos(firstResult, maxResult, orderProperty, asc, componenteSanguineo);
	}

	public Long pesquisarComponentesSanguineosCount(AbsComponenteSanguineo absComponentesSanguineo) {
		return this.getAbsComponenteSanguineoDAO().pesquisarComponentesSanguineoCount(absComponentesSanguineo);
	}	
	
	public Long obterComponenteSanguineosCount(Object param){
		return getAbsComponenteSanguineoDAO().listarAbsComponenteSanguineosCount(param);
	}
	
	public List<AbsComponenteSanguineo> obterComponenteSanguineos(Object param){
		return getAbsComponenteSanguineoDAO().listarAbsComponenteSanguineos(param);
	}	
	
	public AbsComponenteSanguineo obterAbsComponenteSanguineoPorId(String codigo) {
		return this.getAbsComponenteSanguineoDAO().obterComponeteSanguineoPorCodigo(codigo);
	}
	
	private AbsExameComponenteVisualPrescricaoDAO getAbsExameComponenteVisualPrescricaoDAO(){
		return absExameComponenteVisualPrescricaoDAO;
	}	
	
	public List<AbsExameComponenteVisualPrescricao> listaExameComponenteVisualPrescricao(Integer firstResult,Integer maxResult, String orderProperty, boolean asc,
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) {
		return getAbsExameComponenteVisualPrescricaoDAO().listaExameComponenteVisualPrescricao(firstResult, maxResult, orderProperty, asc, absExameComponenteVisualPrescricao);
	}	
	
	public Long listaExameComponenteVisualPrescricaoCount(AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) {
		return getAbsExameComponenteVisualPrescricaoDAO().listaExameComponenteVisualPrescricaoCount(absExameComponenteVisualPrescricao);
	}
	
	public AbsComponenteSanguineo obterComponenteSanguineoUnico(String param) {		
		return getAbsComponenteSanguineoDAO().obterComponeteSanguineoPorCodigo(param);
	}
	
	public AbsExameComponenteVisualPrescricao obterAbsExameComponenteVisualPrescricaoPorId(Integer id) {
		return getAbsExameComponenteVisualPrescricaoDAO().obterPorChavePrimaria(id);
	}	
	
	public AbsExameComponenteVisualPrescricao obterAbsExameComponenteVisualPrescricaoPorId(Integer id, Enum ...fields) {
		return getAbsExameComponenteVisualPrescricaoDAO().obterPorChavePrimaria(id, fields);
	}
	
	public List<AbsOrientacaoComponentes> listaOrientacoes(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, final Short seq, final String descricao, final DominioSituacao situacao, final String componenteSaguineo) {
		return this.getAbsOrientacaoComponentesDAO().pesquisarOrientacaoComponentes(firstResult, maxResult, orderProperty, asc, seq,descricao,situacao,componenteSaguineo);
	}
	
	public Long pesquisarOrientacaoComponentesCount(final Short seq, final String descricao, final DominioSituacao situacao, final String componenteSaguineo) {
		return this.getAbsOrientacaoComponentesDAO().pesquisarOrientacaoComponentesCount(seq,descricao,situacao,componenteSaguineo);
	}

	public AbsOrientacaoComponentes obterAbsOrientacaoComponentesPorId(AbsOrientacaoComponentesId id,  Boolean left, Enum... fetchEnums) {
		return getAbsOrientacaoComponentesDAO().obterPorChavePrimaria(id, left, fetchEnums);
	}

	public void atualizarAbsOrientacaoComponentes(AbsOrientacaoComponentes absOrientacaoComponentes) throws ApplicationBusinessException {
		getAbsOrientacaoComponentesRN().atualizarAbsOrientacaoComponentes(absOrientacaoComponentes); 
	}

	
	public void manterAbsOrientacaoComponentes(AbsOrientacaoComponentes absOrientacaoComponentes) throws ApplicationBusinessException {
		getAbsOrientacaoComponentesRN().persistirAbsOrientacaoComponentes(absOrientacaoComponentes);
		
	}
	
	private AbsOrientacaoComponentesDAO getAbsOrientacaoComponentesDAO() {
		return absOrientacaoComponentesDAO;
	}	
	
	private AbsOrientacaoComponentesON getAbsOrientacaoComponentesRN() {
		return absOrientacaoComponentesON;
	}

	private ManterPesoFornecedorON getManterPesoFornecedorON() {
		return manterPesoFornecedorON;
	}

	public AbsExameComponenteVisualPrescricao obterAbsExameComponenteVisualPrescricaoId(Integer id) {
		return getAbsExameComponenteVisualPrescricaoDAO().absExameComponenteVisualPrescricaoId(id);
	}

	public List<AbsComponentePesoFornecedor> listaPesoFornecedor(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AbsComponentePesoFornecedor componentePesoFornecedor) {
		return this.getAbsComponentePesoFornecedorDAO().pesquisarComponentePesoFornecedor(firstResult, maxResult, orderProperty, asc, componentePesoFornecedor);
	}

	public Long pesquisarComponentePesoFornecedorCount(AbsComponentePesoFornecedor componentePesoFornecedor) {
		return this.getAbsComponentePesoFornecedorDAO().pesquisarComponentePesoFornecedorCount(componentePesoFornecedor);
	}
	
	private AbsFornecedorBolsasDAO getAbsFornecedorBolsasDAO() {
		return absFornecedorBolsasDAO;
	}
	
	private AbsComponentePesoFornecedorDAO getAbsComponentePesoFornecedorDAO() {
		return absComponentePesoFornecedorDAO;
	}

	public List<AbsFornecedorBolsas> pesquisarFornecedor(Object obj) {
		return this.getAbsFornecedorBolsasDAO().pesquisarFornecedor(obj);
	}

	@Override
	public Long pesquisarFornecedorCount(Object obj) {
		return this.getAbsFornecedorBolsasDAO().pesquisarFornecedorCount(obj);
	}

	public AbsFornecedorBolsas obterFornecedorBolsa(Integer fboSeq) {
		return getAbsFornecedorBolsasDAO().obterComponeteFornecedorBolsasPorCodigo(fboSeq);
	}	
	
	protected ManterComponentesSanguineosON getManterComponentesSanguineosON(){
		return manterComponentesSanguineosON;
	}
	
	@Override
	public void gravarRegistro(AbsComponenteSanguineo absComponenteSanguineo, Boolean edita) throws ApplicationBusinessException{
		getManterComponentesSanguineosON().gravarRegistro(absComponenteSanguineo, edita);
	}
	
	@Override
	public void verificaInativo(AbsComponenteSanguineo absComponenteSanguineo) throws ApplicationBusinessException{
		getManterComponentesSanguineosON().verificaInativo(absComponenteSanguineo);
	}
	
	@Override
	public void verificaJustificativa(AbsComponenteSanguineo absComponenteSanguineo) throws ApplicationBusinessException{
		getManterComponentesSanguineosON().verificaJustificativa(absComponenteSanguineo);
	}	
	
	@Override
	@BypassInactiveModule
	public List<AbsSolicitacoesHemoterapicas> buscaSolicitacoesHemoterapicas(MpmPrescricaoMedicaId id, Boolean listarTodas) {
		return getAbsSolicitacoesHemoterapicasDAO().buscaSolicitacoesHemoterapicas(id, listarTodas);
	}

	@Override	
	@BypassInactiveModule
	public void refreshAbsItensSolHemoterapicas(AbsItensSolHemoterapicas elemento) {
		getAbsItensSolHemoterapicasDAO().refresh(elemento);
	}
	
	protected AbsSolicitacoesHemoterapicasDAO getAbsSolicitacoesHemoterapicasDAO() {
		return absSolicitacoesHemoterapicasDAO;
	}

	protected AbsItensSolHemoterapicasDAO getAbsItensSolHemoterapicasDAO() {
		return absItensSolHemoterapicasDAO;
	}	
	
	@Override		
	public List<DoacaoColetaSangueVO> listarRegSanguineoPacienteComExistsSemAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, DominioOrigemAtendimento origem){
		return getAbsRegSanguineoPacientesDAO().listarRegSanguineoPacienteComExistsSemAgrupamento(vDtHrInicio, vDtHrFim, matricula, vinCodigo, origem);
	}
	
	@Override	
	public List<AbsItensSolHemoterapicas> pesquisarItensHemoterapia(Integer sheAtdSeq, Integer sheSeq){
		return getAbsItensSolHemoterapicasDAO().pesquisarItensHemoterapia(sheAtdSeq, sheSeq);
	}

	@Override	
	public void removerAbsItensSolHemoterapicas(AbsItensSolHemoterapicas elemento){
		getAbsItensSolHemoterapicasDAO().remover(elemento);
	}
	
	@Override	
	public List<AbsSolicitacoesHemoterapicas> pesquisarHemoterapiasParaCancelar(
			Integer atdSeq, Integer pmeSeq, Date dthrMovimento) {
		return getAbsSolicitacoesHemoterapicasDAO().pesquisarHemoterapiasParaCancelar(atdSeq, pmeSeq, dthrMovimento);
	}
	
	@Override
	public void removerAbsItemSolicitacaoHemoterapicaJustificativa(AbsItemSolicitacaoHemoterapicaJustificativa elemento){
		getAbsItemSolicitacaoHemoterapicaJustificativaDAO().remover(elemento);
	}

	@Override	
	public List<AbsAmostrasPacientes> amostrasPaciente(AipPacientes paciente, Date dthrInicio){
		return getAbsAmostrasPacientesDAO().amostrasPaciente(paciente, dthrInicio);
	}
	
	@Override		
	public List<AbsEstoqueComponentes> listarEstoquesComponentes(Integer pacCodigo, Date dthrInicio){
		return getAbsEstoqueComponentesDAO().listarEstoquesComponentes(pacCodigo, dthrInicio);
	}
	
	@Override	
	public List<AbsMovimentosComponentes> listarMovimentosComponentes(Integer pacCodigo, Date dthrInicio){
		return getAbsMovimentosComponentesDAO().listarMovimentosComponentes(pacCodigo, dthrInicio);
	}
	
	@Override	
	public void substituirPacienteRegSanguineoPacientes(AipPacientes pacienteOrigem, AipPacientes pacienteDestino, Date dthrInicio) {
		getAbsRegSanguineoPacientesDAO().substituirPacienteRegSanguineoPacientes(pacienteOrigem, pacienteDestino, dthrInicio);
	}
	
	@Override	
	public List<AbsSolicitacoesDoacoes> listarSolicitacoesDoacoes(Integer pacCodigo, Date dthrSolicitacao, LockMode lockMode){
		return getAbsSolicitacoesDoacoesDAO().listarSolicitacoesDoacoes(pacCodigo, dthrSolicitacao, lockMode);
	}
	
	@Override	
	public Short obterSequenciaSolicitacoesDoacoes(Integer pacCodigo) {
		return getAbsSolicitacoesDoacoesDAO().obterSequenciaSolicitacoesDoacoes(pacCodigo);
	}
	
	@Override	
	public AbsSolicitacoesHemoterapicas obterSolicitacaoPorSeqESitColeta(Integer seq, DominioSituacaoColeta sitColeta){
		return getAbsSolicitacoesHemoterapicasDAO().obterSolicitacaoPorSeqESitColeta(seq, sitColeta);
	}
	
	@Override	
	public List<DoacaoColetaSangueVO> listarDoacaoPorPeriodo(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo) {
		return getAbsDoacoesDAO().listarDoacaoPorPeriodo(vDtHrInicio, vDtHrFim, matricula, vinCodigo);
	}
		
	@Override	
	public List<DoacaoColetaSangueVO> listarDoacaoPorTipo(Date vDtHrInicio, Date vDtHrFim,
			String tipo, Integer matricula, Short vinCodigo) {
		return getAbsDoacoesDAO().listarDoacaoPorTipo(vDtHrInicio, vDtHrFim, tipo, matricula, vinCodigo);
	}

	@Override	
	public List<DoacaoColetaSangueVO> listarDoacaoColetaSangue(Date vDtHrInicio, Date vDtHrFim, 
			String tipo, Integer matricula, Short vinCodigo){
		return getAbsDoacoesDAO().listarDoacaoColetaSangue(vDtHrInicio, vDtHrFim, tipo, matricula, vinCodigo);
	}
	
	@Override	
	public List<DoadorSangueTriagemClinicaVO> listarDoadorSangueTriagemClinica(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo) {
		return getAbsDoacoesDAO().listarDoadorSangueTriagemClinica(vDtHrInicio, vDtHrFim, matricula, vinCodigo);
	}	

	@Override	
	public List<DoacaoColetaSangueVO> listarMovimentosComponentesSemExistsComAgrupamentoIrradiado(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, DominioMcoType mcoType, 
			Boolean indEstorno, Boolean indIrradiado) {
		return getAbsMovimentosComponentesDAO().listarMovimentosComponentesSemExistsComAgrupamentoIrradiado
			(vDtHrInicio, vDtHrFim, matricula, vinCodigo, mcoType, indEstorno, indIrradiado);
	}
		
	@Override	
	public List<DoacaoColetaSangueVO> listarTransfusaoComExistsSemAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo, DominioOrigemAtendimento origem){
		return getAbsMovimentosComponentesDAO().listarTransfusaoComExistsSemAgrupamento
			(vDtHrInicio, vDtHrFim, matricula, vinCodigo, unfSeqs, mcoType, indEstorno, ecoCsaCodigo, origem);
	}
	
	@Override	
	public List<DoacaoColetaSangueVO> listarTransfusaoSemExistsSemAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo) {
		return getAbsMovimentosComponentesDAO().listarTransfusaoSemExistsSemAgrupamento
			(vDtHrInicio, vDtHrFim, matricula, vinCodigo, unfSeqs, mcoType, indEstorno, ecoCsaCodigo);
	}
	
	@Override	
	public List<DoacaoColetaSangueVO> listarTransfusaoComExistsComAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo, DominioOrigemAtendimento origem) {
		return getAbsMovimentosComponentesDAO().listarTransfusaoComExistsComAgrupamento
			(vDtHrInicio, vDtHrFim, matricula, vinCodigo, unfSeqs, mcoType, indEstorno, ecoCsaCodigo, origem);
	}
	
	@Override	
	public List<DoacaoColetaSangueVO> listarTransfusaoSemExistsComAgrupamento(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo) {
		return getAbsMovimentosComponentesDAO().listarTransfusaoSemExistsComAgrupamento
			(vDtHrInicio, vDtHrFim, matricula, vinCodigo, unfSeqs, mcoType, indEstorno, ecoCsaCodigo);
	}
	
	
	@Override	
	public List<DoacaoColetaSangueVO> listarTransfusaoComExistsSemAgrupamentoComDoacao(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, Boolean indEstorno, 
			String ecoCsaCodigo, DominioOrigemAtendimento origem, String tdoCodigo, Boolean isTdoCodigoIgual) {
		return getAbsMovimentosComponentesDAO().listarTransfusaoComExistsSemAgrupamentoComDoacao
			(vDtHrInicio, vDtHrFim, matricula, vinCodigo, unfSeqs, mcoType, indEstorno, ecoCsaCodigo, origem, tdoCodigo, isTdoCodigoIgual);		
	}
	
	@Override	
	public List<DoacaoColetaSangueVO> listarTransfusaoSemExistsSemAgrupamentoComDoacao(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, Short[] unfSeqs, DominioMcoType mcoType, Boolean indEstorno, 
			String ecoCsaCodigo,  String tdoCodigo, Boolean isTdoCodigoIgual) {
		return getAbsMovimentosComponentesDAO().listarTransfusaoSemExistsSemAgrupamentoComDoacao
			(vDtHrInicio, vDtHrFim, matricula, vinCodigo, unfSeqs, mcoType, indEstorno, ecoCsaCodigo, tdoCodigo, isTdoCodigoIgual);
			
	}
		
	@Override	
	public List<DoacaoColetaSangueVO> listarMovimentosComponentesSemExistsComAgrupamentoFiltrado(Date vDtHrInicio, Date vDtHrFim,
			Integer matricula, Short vinCodigo, DominioMcoType mcoType, 
			Boolean indEstorno, String ecoCsaCodigo, Boolean indFiltrado) {
		return getAbsMovimentosComponentesDAO().listarMovimentosComponentesSemExistsComAgrupamentoFiltrado
			(vDtHrInicio, vDtHrFim, matricula, vinCodigo, mcoType, indEstorno, ecoCsaCodigo, indFiltrado);
	}
	
	@Override		
	public List<DoacaoColetaSangueVO> listarMovimentosComponentesSemExistsComAgrupamentoDataFiltrado(Date vDtHrInicio, Date vDtHrFim,
			DominioMcoType mcoType, Boolean indEstorno, String ecoCsaCodigo, Boolean indFiltrado){
		return getAbsMovimentosComponentesDAO().listarMovimentosComponentesSemExistsComAgrupamentoDataFiltrado
			(vDtHrInicio, vDtHrFim, mcoType, indEstorno, ecoCsaCodigo, indFiltrado);
	}
	
	@Override	
	public List<AbsExameComponenteVisualPrescricao> buscarExamesComponenteVisualPrescricao(
			String codigoComponenteSanguineo,
			String codigoProcedimentoHemoterapico) {
		return getAbsExameComponenteVisualPrescricaoDAO().buscarExamesComponenteVisualPrescricao
			(codigoComponenteSanguineo, codigoProcedimentoHemoterapico);
	}
	
	@Override
	public AbsComponenteSanguineo obterComponenteSanguineosPorCodigo(
			String componenteSanguineoCodigo){
		return getAbsComponenteSanguineoDAO().obterPorChavePrimaria(componenteSanguineoCodigo);
	}	
	
	@Override
	public List<AbsSolicitacoesHemoterapicas> buscarSolicitacoesHemoterapicasPendentes(
			MpmPrescricaoMedica prescricao, Date data) {
		return getAbsSolicitacoesHemoterapicasDAO().buscarSolicitacoesHemoterapicasPendentes(prescricao, data);
	}	
	
	@Override
	public void inserirAbsAmostrasPacientes(AbsAmostrasPacientes absAmostrasPacientes, boolean flush) {
		this.getAbsAmostrasPacientesDAO().persistir(absAmostrasPacientes);
		if (flush){
			this.getAbsAmostrasPacientesDAO().flush();
		}
	}	
	
	@Override	
	public List<AbsSolicitacoesHemoterapicas> obterPrescricoesHemoterapicaPai(
			Integer seq, Integer seqAtendimento, Date dataHoraInicio,
			Date dataHoraFim){
		return getAbsSolicitacoesHemoterapicasDAO().obterPrescricoesHemoterapicaPai(seq, seqAtendimento, dataHoraInicio, dataHoraFim);
	}
	
	
	@Override	
	public List<AbsSolicitacoesHemoterapicas> obterSolicitacoesHemoterapicasParaSumarioPrescricao(
			Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao){
		return getAbsSolicitacoesHemoterapicasDAO().obterSolicitacoesHemoterapicasParaSumarioPrescricao
			(pmeAtdSeq, dataInicioPrescricao, dataFimPrescricao);
	}
	

	@Override
	public void excluirAbsExameComponenteVisualPrescricao(Integer seqExameComponenteVisualPrescricao){
		getPesquisarExamesDaHemoterapiaRN().remover(seqExameComponenteVisualPrescricao);
	}

	protected PesquisarExamesDaHemoterapiaRN getPesquisarExamesDaHemoterapiaRN() {
		// TODO Auto-generated method stub
		return pesquisarExamesDaHemoterapiaRN;
	}

	@Override
	public void manterAbsExameComponenteVisualPrescricao(
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) throws ApplicationBusinessException {
		getPesquisarExamesDaHemoterapiaRN().persistirExamesDaHemoterapia(absExameComponenteVisualPrescricao);
	}

	@Override
	public void atualizarAbsExameComponenteVisualPrescricao(
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) throws ApplicationBusinessException {
		getPesquisarExamesDaHemoterapiaRN().atualizarExamesDaHemoterapia(absExameComponenteVisualPrescricao);
	}
	
	@Override	
	public List<AbsItensSolHemoterapicas> buscaItensSolHemoterapicas(Integer pAtdSeq, Integer curPdtSeq){
		return getAbsItensSolHemoterapicasDAO().buscaItensSolHemoterapicas(pAtdSeq, curPdtSeq);
	}
	
	@Override
	public AbsComponentePesoFornecedor obterAbsComponentePesoFornecedorPorChavePrimaria(AbsComponentePesoFornecedorId id, Boolean left, Enum... enumFetch) {
		return getAbsComponentePesoFornecedorDAO().obterPorChavePrimaria(id, left, enumFetch); 
	}

	@Override
	public void atualizarAbsComponentePesoFornecedor(AbsComponentePesoFornecedor elemento) throws ApplicationBusinessException {
		getManterPesoFornecedorON().atualizarAbsComponentePesoFornecedor(elemento); 
	}
	
	@Override
	public void excluirAbsComponentePesoFornecedor(AbsComponentePesoFornecedorId id) throws ApplicationBusinessException {
		getManterPesoFornecedorON().excluirAbsComponentePesoFornecedor(id); 
	}
	
	@Override
	public void inserirAbsComponentePesoFornecedor(AbsComponentePesoFornecedor elemento) throws ApplicationBusinessException {
		getManterPesoFornecedorON().inserirAbsComponentePesoFornecedor(elemento); 
	}

	public void alterarIndSugestao(AbsComponentePesoFornecedor absComponentePesoFornecedor) throws ApplicationBusinessException {
		getManterPesoFornecedorON().alterarIndSugestao(absComponentePesoFornecedor); 
	}
	
	@Override
	public AbsValidAmostrasComponentes obterValidadeAmostraPorCodigo(final AbsValidAmostrasComponenteId id) {
		return getValidadeAmostraDAO().obterValidadeAmostraPorCodigo(id.getCsaCodigo(), id.getSeqp());
	}
	
	@Override
	public List<AbsValidAmostrasComponentes> obterListaValidadeAmostrasPorCodigo(Integer firstResult, Integer maxResult,String orderProperty, boolean asc,final String codComponente,Integer codigo, Integer idadeIni, Integer idadeFim) {
		return getValidadeAmostraDAO().obterListaValidadeAmostrasPorCodigo(firstResult,maxResult,orderProperty,asc,codComponente, codigo,idadeIni, idadeFim);
	}

	@Override
	public Long obterValidadeAmostrasPorCodigoCount(final String codComponente,Integer codigo, Integer idadeIni, Integer idadeFim) {
		return this.getValidadeAmostraDAO().obterListaValidadeAmostrasPorCodigoCount(codComponente, codigo, idadeIni, idadeFim);
	}
	
	@Override
	public void persistOrUpdate(AbsValidAmostrasComponentes validadeAmostra) throws BaseException{
		 getValidadeAmostraRN().persistir(validadeAmostra);
	}
	
	@Override
	public List<AbsCandidatosDoadores> obterCandidatosDoadoresList(String parametro) {
		return getAbsCandidatosDoadoresDAO().obterCandidatosDoadoresList(parametro);
	}
	
	protected ValidadeAmostraDAO getValidadeAmostraDAO() {
		return validadeAmostraDAO;
	}
		
	protected ValidadeAmostraRN getValidadeAmostraRN() {
		return validadeAmostraRN;
	}

	@Override
	@BypassInactiveModule
	public List<LinhaReportVO> pesquisarComponentesSanguineosAgendas(
			Integer agdSeq) {
		return getAbsComponenteSanguineoDAO().pesquisarComponentesSanguineosAgendas(agdSeq);
	}

	@Override
	public void inserirFatProcedHospInternos(ScoMaterial matCodigo,
											 MbcProcedimentoCirurgicos pciSeq, 
											 MpmProcedEspecialDiversos pedSeq,
											 String csaCodigo, 
											 String pheCodigo, 
											 String descricao,
											 DominioSituacao indSituacao, 
											 Short euuSeq, 
											 Integer cduSeq,
											 Short cuiSeq, 
											 Integer tidSeq) throws ApplicationBusinessException,ApplicationBusinessException {
		
		getAbsComponenteSanguineoRN().inserirFatProcedHospInternos(matCodigo, pciSeq, pedSeq, csaCodigo, pheCodigo, descricao, 
																   indSituacao, euuSeq, cduSeq, cuiSeq, tidSeq);
	}
	
	private AbsComponenteSanguineoRN getAbsComponenteSanguineoRN() {
		return absComponenteSanguineoRN;
	}

	@Override
	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineosNaoExisteNaCirurgia(
			Object objPesquisa, Integer crgSeq) {
		return getAbsComponenteSanguineoDAO().listarAbsComponenteSanguineosNaoExisteNaCirurgia(objPesquisa, crgSeq);
	}

	@Override
	public Long listarAbsComponenteSanguineosNaoExisteNaCirurgiaCount(
			Object objPesquisa, Integer crgSeq) {
		return getAbsComponenteSanguineoDAO().listarAbsComponenteSanguineosNaoExisteNaCirurgiaCount(objPesquisa, crgSeq);
	}

	@Override
	public AbsComponenteSanguineo obterAbsComponenteSanguineoOriginal(
			String codigo) {
		return this.getAbsComponenteSanguineoDAO().obterOriginal(codigo);
	}

	@Override
	public Boolean existeAmostraPendente(Integer seqAtendimento,
			Integer seqCirurgia, Integer validade) {
		return this.getAbsAmostrasPacientesDAO().existeAmostraPendente(seqAtendimento, seqCirurgia, validade);
	}
	
	@Override
	public void inserirSolicitacaoPorAmostra(AbsSolicitacoesPorAmostra solicitacaoPorAmostra) throws BaseException {
		this.absSolicitacoesPorAmostraRN.inserirSolicitacaoPorAmostra(solicitacaoPorAmostra);
	}
	
	@Override
	public List<AbsItensSolHemoterapicas> pesquisarAbsItensSolHemoterapicasPorAtdSeqSheSeq(Integer sheAtdSeq, Integer sheSeq) {
		return this.absItensSolHemoterapicasDAO.pesquisarAbsItensSolHemoterapicasPorAtdSeqSheSeq(sheAtdSeq, sheSeq);
	}
	
	@Override
	public Boolean existeAbsComponenteSanguineo(String codigo) {
		return this.absComponenteSanguineoRN.existeAbsComponenteSanguineo(codigo);
	}
}