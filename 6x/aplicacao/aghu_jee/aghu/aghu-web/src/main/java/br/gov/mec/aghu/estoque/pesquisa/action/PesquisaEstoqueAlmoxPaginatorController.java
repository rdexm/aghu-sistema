package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.EstoqueAlmoxarifadoVO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PesquisaEstoqueAlmoxPaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = -5271884724378314890L;
	private static final String MANTER_VALIDADE_MATERIAL = "estoque-manterValidadeMaterial";
	private static final String MANTER_MATERIAL_CRUD = "estoque-manterMaterialCRUD";
	private static final Log LOG = LogFactory.getLog(PesquisaEstoqueAlmoxPaginatorController.class);

	@Inject @Paginator
	private DynamicDataModel<EstoqueAlmoxarifadoVO> dataModel;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private Boolean visivel = false;
	
	private String novo = "novo";
	
	private DominioSimNao estocavel;
	
	private DominioSimNao consignado;
	
	private DominioSimNao validade;
	
	private SceEstoqueAlmoxarifado estoqueAlmox  = new SceEstoqueAlmoxarifado();
	
	private ScoGrupoMaterial grupoMaterial = null;
	
	private ScoUnidadeMedida unidadeMedida = null;
	
	private String termoLivre;
	
	private String criadoPor;
	private String alteradoPor;
	private String desativadoPor;
	private Date criadoEm;
	private Date alteradoEm;
	private Date desativadoEm;
	
	// Parâmetros da integração com Manter Materiais
	private Integer codigoMaterial;
	private Short seqAlmoxarifado;
	private Boolean indEstocavel;
	private Boolean situacaoMaterial;
	private String voltarPara;
	private Integer codigoFornecedor;
	private VScoClasMaterial classificacaoMaterial;
	
	private Map<SceEstoqueAlmoxarifado,EstoqueAlmoxarifadoVO> mapEstoques;
	
	private Boolean situacaoAtivo;
	
	private boolean desabilitaCadastroMateriais;
	
	private Integer fornecedorPadrao;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio(){
	 

		
		// Realiza a integração com estórias que utilizam o parâmetro "codigoMaterial"
		if(this.codigoMaterial != null || this.seqAlmoxarifado != null || this.codigoFornecedor != null){

			// Popula automaticamente filtros da pesquisa
			if(this.codigoMaterial != null){
				this.estoqueAlmox.setMaterial(this.comprasFacade.obterMaterialPorId(this.codigoMaterial));
			}
			
			if(this.codigoFornecedor != null){
				this.estoqueAlmox.setFornecedor(this.comprasFacade.obterFornecedorPorNumero(this.codigoFornecedor));
			}
			
			// Popula automaticamente filtros da pesquisa
			if(this.seqAlmoxarifado != null){
				this.estoqueAlmox.setAlmoxarifado(this.estoqueFacade.obterAlmoxarifadoPorId(this.seqAlmoxarifado));
			}
			
			// Seta situação estocável
			if (this.indEstocavel != null) {
				this.estocavel = DominioSimNao.getInstance(this.indEstocavel);			
			}
		
			// Seta situação do material no almoxarifado
			if(this.situacaoMaterial != null){
				this.estoqueAlmox.setIndSituacao(DominioSituacao.getInstance(this.situacaoMaterial));
			}
			// Pesquisa automaticamente
			this.pesquisar();
			if(getVoltarPara() != null) {
				if("manterMaterial".equalsIgnoreCase(getVoltarPara())) {
					setDesabilitaCadastroMateriais(Boolean.TRUE);
				}
			}
		} else if(this.estoqueAlmox != null && this.estoqueAlmox.getSeq()!= null){
			this.pesquisar();
			LOG.debug("pesquisando se for a primeira página da data table");
		}
	
	}
	
	//suggestions
	public List<SceAlmoxarifado> pesquisarAlmoxarifados(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}
	
	public List<ScoMaterial>pesquisarMateriais(String param){
		return comprasFacade.pesquisarMateriais(param);
	}
	
	public List<ScoFornecedor>pesquisarFornecedores(String param){
		return comprasFacade.pesquisarFornecedoresPorNumeroRazaoSocial(param);
	}

	public List<ScoUnidadeMedida> pesquisaIComprasCadastrosBasicosFacaderUnidadeMedida(String param) {
		return comprasCadastrosBasicosFacade.obterUnidadesMedida(param);
	}

	public List<ScoGrupoMaterial> pesquisarGrupoMaterial(String param) {
		return comprasFacade.obterGrupoMaterialPorSeqDescricao(param);
	}

	public List<VScoClasMaterial> obterClassificacaoMaterial(String param){
		return this.comprasFacade.pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(param, null);
	}
	
	/**
	 * Utilizado para abreviar a descrição do nome do material
	 * @param str
	 * @param maxWidth
	 * @return
	 */
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public void pesquisar() {
		
		if (this.consignado != null) {
			this.estoqueAlmox.setIndConsignado(this.consignado.isSim());
		}
		if (this.validade!= null) {
			this.estoqueAlmox.setIndControleValidade(this.validade.isSim());
		}
		if(this.estocavel!=null) {
			this.estoqueAlmox.setIndEstocavel(this.estocavel.isSim());
		}
		if(grupoMaterial != null) {
			if(this.estoqueAlmox.getMaterial() == null) {
				this.estoqueAlmox.setMaterial(new ScoMaterial());
				this.estoqueAlmox.getMaterial().setGrupoMaterial(grupoMaterial);
			} else {
				this.estoqueAlmox.getMaterial().setGrupoMaterial(grupoMaterial);
			}
		}
		if(unidadeMedida != null) {
			this.estoqueAlmox.setUnidadeMedida(unidadeMedida);
		}
		
		fornecedorPadrao = this.parametroFacade.getAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO).getVlrNumerico().intValue();
		if (fornecedorPadrao == null) {
			fornecedorPadrao = 1;
		}

		this.reiniciarPaginator();
		this.visivel = true;
	}
	
	public void limparPesquisa() {
		this.estoqueAlmox = new SceEstoqueAlmoxarifado();
		this.consignado = null;
		this.validade= null;
		this.estocavel=null;
		this.reiniciarPaginator();
		this.setAtivo(false);
		this.visivel = false;
		this.grupoMaterial = null;
		this.termoLivre = null;	
		this.unidadeMedida = null;
		this.situacaoAtivo = null;
		this.codigoMaterial = null;
		this.seqAlmoxarifado = null;
		this.codigoFornecedor = null;
		this.classificacaoMaterial = null;

		
	}
	
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		
		
		Integer seq = null;
		Integer codigo = null;
		Integer numero = null;
		Short seqAlmox = null;
		DominioSituacao situacao = null;
		Boolean estocavel = null;
		String codigoUnidadeMedida = null;
		Integer codigoGrupoMaterial = null;
		
		if(this.estoqueAlmox.getMaterial() != null){
			codigo = this.estoqueAlmox.getMaterial().getCodigo();
		}
		
		if(this.estoqueAlmox.getFornecedor() != null){
			numero = this.estoqueAlmox.getFornecedor().getNumero();
		}
		
		if(this.estoqueAlmox.getAlmoxarifado() != null){
			seqAlmox = this.estoqueAlmox.getAlmoxarifado().getSeq();
		}
		
		if(situacaoAtivo != null) {
			if(situacaoAtivo.booleanValue() == Boolean.TRUE) {
				situacao = DominioSituacao.A;
			} else {
				situacao = DominioSituacao.I;
			}
		}
		
		if(this.estoqueAlmox.getIndEstocavel() != null){
			estocavel = this.estoqueAlmox.getIndEstocavel();
		}
		
		if(this.estoqueAlmox.getSeq() != null){
			seq = this.estoqueAlmox.getSeq();
		}
		
		if(this.estoqueAlmox.getUnidadeMedida() != null) {
			codigoUnidadeMedida = this.estoqueAlmox.getUnidadeMedida().getCodigo();
		}
		
		if(this.estoqueAlmox.getMaterial() != null && this.estoqueAlmox.getMaterial().getGmtCodigo() != null) {
			codigoGrupoMaterial = this.estoqueAlmox.getMaterial().getGrupoMaterial().getCodigo();
		}
		
		return estoqueFacade.listaEstoqueAlmoxarifadoCount(seq, codigo,numero,seqAlmox,situacao,estocavel,codigoUnidadeMedida,codigoGrupoMaterial, termoLivre, classificacaoMaterial);		
	}
	
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public List<EstoqueAlmoxarifadoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		Integer seq = null;
		Integer codigo = null;
		Integer numero = null;
		Short seqAlmox = null;
		DominioSituacao situacao = null;		
		Boolean estocavel = null;
		String codigoUnidadeMedida = null;
		Integer codigoGrupoMaterial = null;
		
		if(this.estoqueAlmox.getMaterial() != null){
			codigo = this.estoqueAlmox.getMaterial().getCodigo();
		}
		
		if(this.estoqueAlmox.getFornecedor() != null){
			numero = this.estoqueAlmox.getFornecedor().getNumero();
		}
		
		if(this.estoqueAlmox.getAlmoxarifado() != null){
			seqAlmox = this.estoqueAlmox.getAlmoxarifado().getSeq();
		}
		
		if(situacaoAtivo != null) {
			if(situacaoAtivo.booleanValue() == Boolean.TRUE) {
				situacao = DominioSituacao.A;
			} else {
				situacao = DominioSituacao.I;
			}
		}
		
		if(this.estoqueAlmox.getIndEstocavel() != null){
			estocavel = this.estoqueAlmox.getIndEstocavel();
		}
		
		if(this.estoqueAlmox.getSeq() != null){
			seq = this.estoqueAlmox.getSeq();
		} 
		
		if(this.estoqueAlmox.getUnidadeMedida() != null) {
			codigoUnidadeMedida = this.estoqueAlmox.getUnidadeMedida().getCodigo();
		}
		
		if(this.estoqueAlmox.getMaterial() != null && this.estoqueAlmox.getMaterial().getGmtCodigo() != null) {
			codigoGrupoMaterial = this.estoqueAlmox.getMaterial().getGmtCodigo();
		}
		
		List<EstoqueAlmoxarifadoVO> result = null;
		try {
			
			result = this.estoqueFacade
					.pesquisarEstoqueAlmoxarifado(firstResult, maxResult,
							orderProperty, asc, codigo, numero, seqAlmox, situacao,
							estocavel, seq, codigoUnidadeMedida,
							codigoGrupoMaterial, termoLivre, classificacaoMaterial);
							
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	
		if (result == null){
			result = new ArrayList<EstoqueAlmoxarifadoVO>();
		}
		else{
			mapEstoques = new HashMap<SceEstoqueAlmoxarifado, EstoqueAlmoxarifadoVO>();
			for(EstoqueAlmoxarifadoVO vo : result){
				mapEstoques.put(vo.getEstoque(), vo);
			}
		}
		
		return result;
	}

	public String cadastrarMaterial(){
		return MANTER_MATERIAL_CRUD;
	}
	
	public String cadastrarValidade(){
		return MANTER_VALIDADE_MATERIAL;
	}
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		if(voltarPara != null){
			if(voltarPara.equals("manterMaterial")){
				return MANTER_MATERIAL_CRUD;
			} else if(voltarPara.equals("ESTORNAR_REQUISICAO_MATERIAL")){
				return "estoque-estornarRequisicaoMaterial";
			} else if(voltarPara.equals("ESTATISTICA_CONSUMO")){
				return "estoque-estatisticaConsumo";
			} else if(voltarPara.equals("efetivarRequisicaoMaterial")){
				return "estoque-efetivarRequisicaoMaterial";
			} else if(voltarPara.equals("PESQUISAR_ESTOQUE_GERAL")){
				return "estoque-pesquisarEstoqueGeral";
			} else if(voltarPara.equals("efetivarTransferenciaMateriaisEventual")){
				return "estoque-efetivarTransferenciaMateriaisEventual";
			}
			
			return voltarPara;
		}
		return null;
	}
	
	public void preencherDadosUsuario(Integer seq){
		
		SceEstoqueAlmoxarifado estqAlmox = null;
		
		Iterator<SceEstoqueAlmoxarifado> iterator = mapEstoques.keySet().iterator();
		
		while(iterator.hasNext() && estqAlmox == null) {
			
			SceEstoqueAlmoxarifado estoque = iterator.next();
		
			if(estoque.getSeq().equals(seq)){
				estqAlmox = estoque;
			}
		}
		
		if(estqAlmox != null){
			if(estqAlmox.getServidor() != null){
				this.criadoPor = estqAlmox.getServidor().getPessoaFisica().getNome();
				this.criadoEm = estqAlmox.getDtGeracao();
			}
			if(estqAlmox.getServidorAlterado() != null){
				this.alteradoPor = estqAlmox.getServidorAlterado().getPessoaFisica().getNome();
				this.alteradoEm = estqAlmox.getDtAlteracao();
			}
			if(estqAlmox.getServidorDesativado() != null){
				this.desativadoPor = estqAlmox.getServidorDesativado().getPessoaFisica().getNome();
				this.desativadoEm = estqAlmox.getDtDesativacao();
			}
		}
	}
	
	
	//getters e setters
	
	public void setAtivo(Boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public Boolean getAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
	public Boolean getVisivel() {
		return visivel;
	}

	public void setVisivel(Boolean visivel) {
		this.visivel = visivel;
	}
	
	public SceEstoqueAlmoxarifado getEstoqueAlmox() {
		return estoqueAlmox;
	}

	public void setEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox) {
		this.estoqueAlmox = estoqueAlmox;
	}

	public DominioSimNao getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(DominioSimNao estocavel) {
		this.estocavel = estocavel;
	}

	public DominioSimNao getConsignado() {
		return consignado;
	}

	public void setConsignado(DominioSimNao consignado) {
		this.consignado = consignado;
	}

	public DominioSimNao getValidade() {
		return validade;
	}

	public void setValidade(DominioSimNao validade) {
		this.validade = validade;
	}
	
	public String getNovo() {
		return novo;
	}

	public void setNovo(String novo) {
		this.novo = novo;
	}

	public String getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}

	public String getAlteradoPor() {
		return alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}

	public String getDesativadoPor() {
		return desativadoPor;
	}

	public void setDesativadoPor(String desativadoPor) {
		this.desativadoPor = desativadoPor;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public Date getDesativadoEm() {
		return desativadoEm;
	}

	public void setDesativadoEm(Date desativadoEm) {
		this.desativadoEm = desativadoEm;
	}
	
	public String getCriadoEmPor(){
		if(this.criadoEm != null && this.criadoPor != null){
			return DateUtil.dataToString(this.criadoEm, "dd/MM/yyyy") + " - " + this.criadoPor;
		}
		else{
			return "";
		}
	}

	public String getAlteradoEmPor(){
		if(this.alteradoEm != null && this.alteradoPor != null){
			return DateUtil.dataToString(this.alteradoEm, "dd/MM/yyyy") + " - " + this.alteradoPor;
		}
		else{
			return "";
		}
	}
	
	public String getDesativadoEmPor(){
		if(this.desativadoEm != null && this.desativadoPor != null){
			return DateUtil.dataToString(this.desativadoEm, "dd/MM/yyyy") + " - " + this.desativadoPor; 
		}
		else{
			return "";
		}
	}
	
	public Integer quantidadeDisponivelTodosEstoques(EstoqueAlmoxarifadoVO estoque) {
		return estoqueFacade.recupararQuantidadeDisponivelTodosEstoques(estoque);
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	
	public Short getSeqAlmoxarifado() {
		return seqAlmoxarifado;
	}
	
	public void setSeqAlmoxarifado(Short seqAlmoxarifado) {
		this.seqAlmoxarifado = seqAlmoxarifado;
	}
	
	public Boolean getIndEstocavel() {
		return indEstocavel;
	}
	
	public void setIndEstocavel(Boolean indEstocavel) {
		this.indEstocavel = indEstocavel;
	}
	
	public Boolean getSituacaoMaterial() {
		return situacaoMaterial;
	}
	
	public void setSituacaoMaterial(Boolean situacaoMaterial) {
		this.situacaoMaterial = situacaoMaterial;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}
	
	
	public String getTermoLivre() {
		return termoLivre;
	}

	public void setTermoLivre(String termoLivre) {
		this.termoLivre = termoLivre;
	}
	
	public VScoClasMaterial getClassificacaoMaterial() {
		return classificacaoMaterial;
	}

	public void setClassificacaoMaterial(VScoClasMaterial classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}
	
	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Integer getCodigoFornecedor() {
		return codigoFornecedor;
	}

	public void setCodigoFornecedor(Integer codigoFornecedor) {
		this.codigoFornecedor = codigoFornecedor;
	}

	public Boolean getSituacaoAtivo() {
		return situacaoAtivo;
	}

	public void setSituacaoAtivo(Boolean situacaoAtivo) {
		this.situacaoAtivo = situacaoAtivo;
	}

	public boolean isDesabilitaCadastroMateriais() {
		return desabilitaCadastroMateriais;
	}

	public void setDesabilitaCadastroMateriais(boolean desabilitaCadastroMateriais) {
		this.desabilitaCadastroMateriais = desabilitaCadastroMateriais;
	}

 


	public DynamicDataModel<EstoqueAlmoxarifadoVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<EstoqueAlmoxarifadoVO> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public Integer getFornecedorPadrao() {
		return fornecedorPadrao;
	}
}