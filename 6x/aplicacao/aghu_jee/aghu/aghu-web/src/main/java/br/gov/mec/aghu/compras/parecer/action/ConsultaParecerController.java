package br.gov.mec.aghu.compras.parecer.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.parecer.business.IParecerFacade;
import br.gov.mec.aghu.compras.vo.PareceresVO;
import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


public class ConsultaParecerController extends ActionController implements ActionPaginator{

	
	
	private static final String PARECER_CRUD = "compras-parecerCRUD";

	private static final long serialVersionUID = 1289998913199756967L;

	private enum ConsultarParecerControllerExceptionCode implements BusinessExceptionCode {
		TITLE_TOOLTIP_VENC_REG01,TITLE_TOOLTIP_VENC_REG02,LABEL_CONSULTA_PARECER_SUBPASTA
	}

	//@EJB
	//private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	protected IComprasFacade comprasFacade;	
	
	@EJB
	protected IParecerFacade parecerFacade;	
	
	private ScoMaterial material;
	private ScoGrupoMaterial grupoMaterial;
	private ScoMarcaComercial marcaComercial;
	private ScoMarcaModelo modeloComercial;
	private Boolean apenasUltimosPareceres = Boolean.TRUE;
	private DominioSituacao situacao = DominioSituacao.A;
	private DominioParecer parecerFinal;
	private ScoOrigemParecerTecnico pasta;
	private Integer nroSubPasta;
	private String nroRegistro;
	
	private Boolean pesquisou = Boolean.FALSE;
	
	private Boolean refazerPesquisa = Boolean.FALSE; 
	
	//private List<PareceresVO> listaPareceresVO = new ArrayList<PareceresVO>();
	
	private Integer codigoModelo;
	private Integer seqModelo;
	private Integer codigoMaterial;
	private String origem;
	private boolean cameFromCatalogo;
	private DominioSimNao matIndEstocavel;
	
	@Inject @Paginator
	private DynamicDataModel<PareceresVO> dataModel;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	protected IComprasFacade ComprasFacade;
	
	private static final String MANTER_MARCA_COMERCIAL = "compras-manterMarcaComercial";
	private static final String ESTOQUE_CONSULTAR_CATALOGO_MATERIAL = "estoque-consultarCatalogoMaterial";

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

		if (cameFromCatalogo) {
			if(StringUtils.isNotBlank(origem) &&
					origem.equalsIgnoreCase(ESTOQUE_CONSULTAR_CATALOGO_MATERIAL) &&
					this.getCodigoMaterial()!=null){
				this.cameFromCatalogo = false;
				setMaterial(this.comprasFacade.obterMaterialPorId(this.codigoMaterial));	
				this.atribuirGrupo();
			}
		}			

		if(this.refazerPesquisa){
			this.pesquisar();
			this.refazerPesquisa = false;
		}
	
	}
	
	
	public void pesquisar() {
		this.setPesquisou(Boolean.TRUE);
		this.dataModel.reiniciarPaginator();				
		
	}
	
	
	@Override
	public List<PareceresVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		
		return this.parecerFacade.pesquisarPareceres(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, nroRegistro, DominioSimNao.getBooleanInstance(matIndEstocavel), firstResult, maxResults);
	}
	
	@Override
	public Long recuperarCount() {
		return this.parecerFacade.pesquisarPareceresCount(material, grupoMaterial, marcaComercial, modeloComercial, apenasUltimosPareceres, situacao, parecerFinal, pasta, nroSubPasta, nroRegistro, DominioSimNao.getBooleanInstance(matIndEstocavel));
		
	}
	

	public void limparPesquisa() {
		
		this.setMaterial(null);
		this.setGrupoMaterial(null);
		this.setMarcaComercial(null);
		this.setModeloComercial(null);
		this.setApenasUltimosPareceres(Boolean.TRUE);
		this.setSituacao(DominioSituacao.A);
		this.setParecerFinal(null);
		this.setPasta(null);
		this.setNroSubPasta(null);
		this.setNroRegistro(null);
		this.dataModel.limparPesquisa();
		this.setPesquisou(Boolean.FALSE);
		this.setMatIndEstocavel(null);
	}
	
	public String iniciarInclusao(){
		return PARECER_CRUD;
	}
	
	public String redirecionarParecerCrud(){
		return PARECER_CRUD;
	}	
	
	public String selecionarMaterial(){
		return ESTOQUE_CONSULTAR_CATALOGO_MATERIAL;
	}
	
	public String truncarTexto(String texto) {
		return (StringUtils.isNotBlank(texto)) ? StringUtils.abbreviate(texto, 30) : "";			
	}	
	
	//Suggestion Material
	public List<ScoMaterial> listarMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.listarMaterial(filter, null, this.grupoMaterial != null ? this.grupoMaterial.getCodigo(): null, null),
				                      listarMateriaisCount(filter));
	}
	
	public Long listarMateriaisCount(String filter){
		return this.comprasFacade.listarScoMateriaisCount(filter);
	}
	
	//Suggestion Grupo Material
	public List<ScoGrupoMaterial> listarGrupoMateriais(String filter){
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorFiltro(filter),listarGrupoMateriaisCount(filter));
	}
	
	public Long listarGrupoMateriaisCount(String filter){
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(filter);
	}
	
	//Suggestion Marca	
	public List<ScoMarcaComercial> pesquisarMarcaComercial(String param) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricao(param),pesquisarMarcaComercialCount(param));
	}
	
	public Long pesquisarMarcaComercialCount(String param) {
		return this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricaoCount(param);
	}

	//Suggestion Modelo
	public void limparModeloComercial() {
		this.modeloComercial = null;
	}

	public List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricao(String param) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricao(param, this.marcaComercial, null),pesquisarMarcaModeloPorCodigoDescricaoCount(param));
	}
	
	public Long pesquisarMarcaModeloPorCodigoDescricaoCount(String param) {
		return this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricaoCount(param, this.marcaComercial, null);
	}
	
	//Suggestion Pasta
	public List<ScoOrigemParecerTecnico> pesquisarOrigemParecerTecnicoPorSeqDescricao(String param) {
		return this.returnSGWithCount(this.comprasFacade.obterOrigemParecerTecnico(param),pesquisarOrigemParecerTecnicoPorSeqDescricaoCount(param));
	}
	
	public Long pesquisarOrigemParecerTecnicoPorSeqDescricaoCount(String param) {
		return this.comprasFacade.obterOrigemParecerTecnicoCount(param);
	}
	
	public String obterDescricaoOrigemParecerTecnico(Integer codigoPasta, Integer numeroSubPasta){
		List<ScoOrigemParecerTecnico> listaOrigemParecerTecnico = new ArrayList<ScoOrigemParecerTecnico>();
		StringBuffer ret =  new StringBuffer();
		if (codigoPasta != null) {
		    listaOrigemParecerTecnico = this.pesquisarOrigemParecerTecnicoPorSeqDescricao(codigoPasta.toString());
		}
		
		if (listaOrigemParecerTecnico.size() == 1){
			ret.append(listaOrigemParecerTecnico.get(0).getDescricao());
			
		}
		
		if (numeroSubPasta != null){
			if (ret.length() > 0) {
				ret.append("</br>");
			}
			ret.append(this.getBundle().getString(ConsultarParecerControllerExceptionCode.LABEL_CONSULTA_PARECER_SUBPASTA.toString()));
			ret.append(": ");
			ret.append(numeroSubPasta.toString());
		}
		
		return ret.toString();
		
	}
	
	public Boolean isVencidoReg(Date dataVenctRegistro){
		if (dataVenctRegistro !=null){
			return (dataVenctRegistro.before(new Date()));
		}
		return false;
	}
	
	public String obterMensagemDTVenctReg(Date dataVenctRegistro){
		if (isVencidoReg(dataVenctRegistro)){
				return this.getBundle().getString(ConsultarParecerControllerExceptionCode.TITLE_TOOLTIP_VENC_REG02.toString());		
		}
		return this.getBundle().getString(ConsultarParecerControllerExceptionCode.TITLE_TOOLTIP_VENC_REG01.toString());
	}
	
	public String obterNomePessoaFisicaServidor(RapServidores servidor) throws ApplicationBusinessException {
		if(servidor != null) {
			RapServidores servidorFind = this.registroColaboradorFacade.obterServidor(servidor.getId());
			if (servidorFind.getPessoaFisica() != null) {
			   RapPessoasFisicas pesFis = this.registroColaboradorFacade.obterPessoaFisica(servidorFind.getPessoaFisica().getCodigo());
			   if (pesFis != null){
				   return pesFis.getNome();
			   }
			}							
		}
		 return "";
	}
	
	public void atribuirGrupo(){
		if (this.material != null && this.material.getGrupoMaterial() != null) {
			this.setGrupoMaterial(ComprasFacade.obterGrupoMaterialPorId(this.material.getGrupoMaterial().getCodigo()));
		}
	}
	
	public void apagarGrupo(){
		this.setGrupoMaterial(null);
		this.setMaterial(null);
	}
	
	public void apagarMaterial(){
		this.setGrupoMaterial(null);
	}
	
	public String adicionarMarcaComercial() {		
		return MANTER_MARCA_COMERCIAL;
	}
	
	// ### GETs e SETs ###	
	public Boolean getPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	public ScoMarcaModelo getModeloComercial() {
		return modeloComercial;
	}

	public void setModeloComercial(ScoMarcaModelo modeloComercial) {
		this.modeloComercial = modeloComercial;
	}

	public Boolean getApenasUltimosPareceres() {
		return apenasUltimosPareceres;
	}

	public void setApenasUltimosPareceres(Boolean apenasUltimosPareceres) {
		this.apenasUltimosPareceres = apenasUltimosPareceres;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioParecer getParecerFinal() {
		return parecerFinal;
	}

	public void setParecerFinal(DominioParecer parecerFinal) {
		this.parecerFinal = parecerFinal;
	}

	public ScoOrigemParecerTecnico getPasta() {
		return pasta;
	}

	public void setPasta(ScoOrigemParecerTecnico pasta) {
		this.pasta = pasta;
	}	
	
	public Integer getNroSubPasta() {
		return nroSubPasta;
	}

	public void setNroSubPasta(Integer nroSubPasta) {
		this.nroSubPasta = nroSubPasta;
	}

	/*public List<PareceresVO> getListaPareceresVO() {
		return listaPareceresVO;
	}

	public void setListaPareceresVO(List<PareceresVO> listaPareceresVO) {
		this.listaPareceresVO = listaPareceresVO;
	}*/

	public Boolean getRefazerPesquisa() {
		return refazerPesquisa;
	}

	public void setRefazerPesquisa(Boolean refazerPesquisa) {
		this.refazerPesquisa = refazerPesquisa;
	}

	public void setCodigoModelo(Integer codigoModelo) {
		this.codigoModelo = codigoModelo;
	}

	public Integer getCodigoModelo() {
		return codigoModelo;
	}

	public void setSeqModelo(Integer seqModelo) {
		this.seqModelo = seqModelo;
	}

	public Integer getSeqModelo() {
		return seqModelo;
	}

	public String getNroRegistro() {
		return nroRegistro;
	}

	public void setNroRegistro(String nroRegistro) {
		this.nroRegistro = nroRegistro;
	}

	public DynamicDataModel<PareceresVO> getDataModel() {
		return dataModel;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public boolean isCameFromCatalogo() {
		return cameFromCatalogo;
	}

	public void setCameFromCatalogo(boolean cameFromCatalogo) {
		this.cameFromCatalogo = cameFromCatalogo;
	}

	public void setDataModel(DynamicDataModel<PareceresVO> dataModel) {
		this.dataModel = dataModel;
	}

	public DominioSimNao getMatIndEstocavel() {
		return matIndEstocavel;
	}

	public void setMatIndEstocavel(DominioSimNao matIndEstocavel) {
		this.matIndEstocavel = matIndEstocavel;
	}

	
	
	
}