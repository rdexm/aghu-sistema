package br.gov.mec.aghu.compras.parecer.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerAvalConsulDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerAvalDesempDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerAvalTecnicaDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerAvaliacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerOcorrenciaDAO;
import br.gov.mec.aghu.compras.vo.PareceresAvaliacaoVO;
import br.gov.mec.aghu.compras.vo.PareceresVO;
import br.gov.mec.aghu.compras.vo.PropFornecAvalParecerVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoParecer;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.model.ScoParecerAvalConsul;
import br.gov.mec.aghu.model.ScoParecerAvalDesemp;
import br.gov.mec.aghu.model.ScoParecerAvalTecnica;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;


/**
 * Porta de entrada do módulo parecer.
 * 
 */
@Modulo(ModuloEnum.COMPRAS)
@Stateless
public class ParecerFacade extends BaseFacade implements IParecerFacade {
	
	@EJB
	private ScoParecerOcorrenciaON scoParecerOcorrenciaON;
	@EJB
	private ScoParecerItemPropostaFornecedorON scoParecerItemPropostaFornecedorON;
	@EJB
	private AvaliacaoPropostasParecerTecnicoON avaliacaoPropostasParecerTecnicoON;
	@EJB
	private ScoParecerMaterialON scoParecerMaterialON;
	@EJB
	private ScoParecerAvaliacaoON scoParecerAvaliacaoON;

	@Inject
	private ScoParecerAvalConsulDAO scoParecerAvalConsulDAO;
	
	@Inject
	private ScoParecerAvalDesempDAO scoParecerAvalDesempDAO;
	
	@Inject
	private ScoParecerAvaliacaoDAO scoParecerAvaliacaoDAO;
	
	@Inject
	private ScoParecerOcorrenciaDAO scoParecerOcorrenciaDAO;
	
	@Inject
	private ScoParecerMaterialDAO scoParecerMaterialDAO;
	
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	
	@Inject
	private ScoParecerAvalTecnicaDAO scoParecerAvalTecnicaDAO;	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5836436067464121527L;
	
	@Override
	public List<PareceresVO> pesquisarPareceres(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro,
			Boolean matIndEstocavel, Integer firstResult, Integer maxResult) {		
		
		return getScoParecerMaterialON().pesquisarPareceres(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, nroRegistro, matIndEstocavel, firstResult, maxResult);
	}
	
	@Override
	public ScoParecerMaterial obterParecer(Integer codigo){
		return this.getScoParecerMaterialDAO().obterPorChavePrimaria(codigo, true,
				ScoParecerMaterial.Fields.MATERIAL,
					ScoParecerMaterial.Fields.MARCA_COMERCIAL,
						ScoParecerMaterial.Fields.MARCA_MODELO);
	}
	
	@Override
	public ScoParecerMaterial obterParecerTecnicoDuplicidade(ScoParecerMaterial scoParecerMaterial){
		return this.getScoParecerMaterialDAO().obterParecerTecnicoDuplicidade(scoParecerMaterial);
	}
	
	@Override
	public void alterarParecerMaterial(ScoParecerMaterial scoParecerMaterial)
			throws ApplicationBusinessException {
		this.getScoParecerMaterialON().alterar(scoParecerMaterial);
	}
	
	@Override
	public ScoParecerMaterial obterParecerTecnicoAtivo(ScoParecerMaterial scoParecerMaterial, Boolean isMarca){
		return this.getScoParecerMaterialDAO().obterParecerTecnicoAtivo(scoParecerMaterial, isMarca);
	}
	
	@Override
	public void inserirParecerMaterial(ScoParecerMaterial scoParecerMaterial)
			throws ApplicationBusinessException{
		this.getScoParecerMaterialON().inserir(scoParecerMaterial);
	}
	
	@Override
	public ScoParecerAvaliacao obterUltimaAvaliacaoParecer(ScoParecerMaterial scoParecerMaterial){	
		return this.getScoParecerAvaliacaoDAO().obterUltimaAvaliacaoParecer(scoParecerMaterial);
	}
	
	@Override
	public ScoParecerOcorrencia obterUltimaOcorrenciaParecer(ScoParecerMaterial scoParecerMaterial){
		return this.getScoParecerOcorrenciaDAO().obterUltimaOcorrenciaParecer(scoParecerMaterial);
	}	
	
	@Override
	public void persistirParecerAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao, ScoParecerAvalTecnica scoParecerAvalTecnica, 
            ScoParecerAvalConsul scoParecerAvalConsul, ScoParecerAvalDesemp scoParecerAvalDesemp) throws ApplicationBusinessException{
		  this.getScoParecerAvaliacaoON().persistirParecerAvaliacao(scoParecerAvaliacao, scoParecerAvalTecnica, scoParecerAvalConsul, scoParecerAvalDesemp);
		
	}
	
	@Override
	public ScoParecerAvaliacao obterParecerAvaliacaoPorCodigo(Integer codigo){
		return this.getScoParecerAvaliacaoON().obterParecerAvaliacaoPorCodigo(codigo);
	}
	
	@Override
	public ScoParecerAvalTecnica obterParecerAvalTecnicaPorAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao){
		return this.getScoParecerAvalTecnicaDAO().obterParecerAvalTecnicaPorAvaliacao(scoParecerAvaliacao);
	}
	
	@Override
	public Integer obterMaxNumeroSubPasta(ScoOrigemParecerTecnico pasta) {
		return this.getScoParecerMaterialDAO().obterMaxNumeroSubPasta(pasta);
	}
	
	@Override
	public ScoParecerAvalConsul obterParecerAvalConsulPorAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao){
		return this.getScoParecerAvalConsulDAO().obterParecerAvalConsulPorAvaliacao(scoParecerAvaliacao);
	}
	
	@Override
	public ScoParecerAvalDesemp obterParecerAvalDesempPorAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao){	
		return this.getScoParecerAvalDesempDAO().obterParecerAvalDesempPorAvaliacao(scoParecerAvaliacao);
	}
	
	@Override
	public  List<ScoParecerOcorrencia> listaOcorrenciaParecer(ScoParecerMaterial scoParecerMaterial, DominioSituacao situacao){
		return this.getScoParecerOcorrenciaDAO().listaOcorrenciaParecer(scoParecerMaterial, situacao);
	}
	
	@Override
	public void persistirParecerOcorrencia(ScoParecerOcorrencia scoParecerOcorrencia) throws ApplicationBusinessException{
		this.getScoParecerOcorrenciaON().persistirParecerOcorrencia(scoParecerOcorrencia);
	}
	
	@Override
	public ScoParecerOcorrencia clonarParecerOcorrencia(ScoParecerOcorrencia  scoParecerOcorrencia) throws ApplicationBusinessException{
		return this.getScoParecerOcorrenciaON().clonarParecerOcorrencia(scoParecerOcorrencia);
	}
	
	@Override
	public List<PareceresAvaliacaoVO> listaAvaliacaoParecer(ScoParecerMaterial scoParecerMaterial){
		return this.getScoParecerAvaliacaoDAO().listaAvaliacaoParecer(scoParecerMaterial);
	}
	
	@Override
	public List<PropFornecAvalParecerVO> pesquisarItensPropostaFornecedorPAC(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer numeroPAC, List<DominioSituacaoParecer> listaSituacaoParecer){
		return this.getConsultaAvalProposFornecedorParecerON().pesquisarItensPropostaFornecedorPAC(firstResult, maxResult, orderProperty, asc, numeroPAC, listaSituacaoParecer);
	}
	@Override
	public Long contarItensPropostaFornecedorPAC(Integer numeroPAC, List<DominioSituacaoParecer> listaSituacaoParecer){
		return this.getConsultaAvalProposFornecedorParecerON().contarItensPropostaFornecedorPAC(numeroPAC, listaSituacaoParecer);
		
	}
	
	protected ScoParecerMaterialON getScoParecerMaterialON() {
		return scoParecerMaterialON;
	}
	
	protected ScoParecerMaterialDAO getScoParecerMaterialDAO() {
		return scoParecerMaterialDAO;
	}
	
	protected ScoParecerAvaliacaoDAO getScoParecerAvaliacaoDAO() {
		return scoParecerAvaliacaoDAO;
	}
	protected ScoParecerOcorrenciaDAO getScoParecerOcorrenciaDAO() {
		return scoParecerOcorrenciaDAO;
	}
	protected ScoParecerAvaliacaoON getScoParecerAvaliacaoON() {
		return scoParecerAvaliacaoON;
	}
	protected ScoParecerAvalTecnicaDAO getScoParecerAvalTecnicaDAO() {
		return scoParecerAvalTecnicaDAO;
	}
	protected ScoParecerAvalConsulDAO getScoParecerAvalConsulDAO() {
		return scoParecerAvalConsulDAO;
	}
	protected ScoParecerAvalDesempDAO getScoParecerAvalDesempDAO() {
		return scoParecerAvalDesempDAO;
	}	
	
	
	protected ScoParecerOcorrenciaON getScoParecerOcorrenciaON() {
		return scoParecerOcorrenciaON;
	}

	@Override
	@Secure("#{s:hasPermission('consultarParecerTecnico','visualizar')}")
	public Integer obterScParaAnaliseTecnica(ScoItemPropostaFornecedorId id) {
		return getScoItemPropostaFornecedorDAO().obterScParaAnaliseTecnica(id);
	}

	@Override
	@Secure("#{s:hasPermission('cadastrarParecerTecnico', 'gravar')}")
	public void gravarAnaliseTecnica(ScoItemPropostaFornecedor itemProposta)
			throws BaseException {
		getScoParecerItemPropostaFornecedorON().gravarAnaliseTecnica(itemProposta);
	}
	
	protected ScoParecerItemPropostaFornecedorON getScoParecerItemPropostaFornecedorON() {
		return scoParecerItemPropostaFornecedorON;
	}
	
	protected AvaliacaoPropostasParecerTecnicoON getConsultaAvalProposFornecedorParecerON() {
		return avaliacaoPropostasParecerTecnicoON;
	}
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.compras.parecer.business.IParecerFacade#verificarCampoObrigatorio(br.gov.mec.aghu.model.ScoParecerMaterial)
	 */
	@Override
	public void verificarCampoObrigatorio(final ScoParecerMaterial parecerMaterial) throws ApplicationBusinessException {
		this.getScoParecerMaterialON().verificarCampoObrigatorioCadastroParecer(parecerMaterial);
	}
	
	@Override
	public Long pesquisarPareceresCount(ScoMaterial material,
			ScoGrupoMaterial grupoMaterial, ScoMarcaComercial marcaComercial,
			ScoMarcaModelo modeloComercial, Boolean apenasUltimosPareceres,
			DominioSituacao situacao, DominioParecer parecerFinal,
			ScoOrigemParecerTecnico pasta, Integer nroSubPasta, String nroRegistro, Boolean matIndEstocavel){
		return this.getScoParecerMaterialON().pesquisarPareceresCount(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, nroRegistro, matIndEstocavel);
	}
}
