package br.gov.mec.aghu.compras.parecer.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.parecer.business.IParecerFacade;
import br.gov.mec.aghu.dominio.DominioParecer;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;
import br.gov.mec.aghu.suprimentos.action.ManterMarcaComercialController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * Classe responsável por controlar as ações do criação e edição de Parecer
 * 
 */

public class ParecerController extends ActionController {

	

	private static final String SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";

	private static final String CONSULTAR_HISTORICO_PARECER = "consultarHistoricoParecer";

	private static final String MANTER_MARCA_COMERCIAL = "compras-manterMarcaComercial";

	private static final String PARECER_OCORRENCIA_CRUD = "parecerOcorrenciaCRUD";

	private static final String PARECER_AVALIACAO_CRUD = "parecerAvaliacaoCRUD";

	private static final Log LOG = LogFactory.getLog(ParecerController.class);

	private static final long serialVersionUID = 0L;

	@EJB
	protected IComprasFacade comprasFacade;	
	
	@EJB
	protected IParecerFacade parecerFacade;	
	
	@Inject
	private ManterMarcaComercialController manterMarcaComercialController;
	
	private ScoMarcaComercial marcaComercialInserida;

    private Boolean cameFromMarcaModeloCrud = false;

	private ScoMarcaModelo marcaModeloInserida;		

	private ScoParecerMaterial  parecerMaterial = new ScoParecerMaterial();
	private Boolean situacao = false;
    
	private List<ScoParecerAvaliacao> listaParecerAvaliacao = new ArrayList<ScoParecerAvaliacao>();
	private List<ScoParecerOcorrencia> listaParecerOcorrencia = new ArrayList<ScoParecerOcorrencia>();
	
	private Integer codigoParecerMaterial;

	private Boolean readOnlyPasta = Boolean.FALSE;
	
	private Boolean modoEdit = false;
	
	private String voltarPara = null;
	
	private Integer codigoMaterial = null;
	private Integer codigoMarca = null;
	private Integer codigoModelo = null;
	private boolean criouNovo = false;
	
	@Inject
	private ParecerAvaliacaoController parecerAvaliacaoController;
	

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

	 

        // so limpa os dados se nao e edit e nao veio do crud de modelo #47567
		if(!this.getModoEdit() && !this.getCameFromMarcaModeloCrud()){
			this.parecerMaterial = new ScoParecerMaterial();
			this.listaParecerAvaliacao = new ArrayList<ScoParecerAvaliacao>();
			this.listaParecerOcorrencia = new ArrayList<ScoParecerOcorrencia>();
			this.readOnlyPasta = false;
			this.situacao = true;
			
			if (this.codigoMaterial != null) {
				   this.getParecerMaterial().setMaterial(this.comprasFacade.obterMaterialPorId(this.codigoMaterial));
				   this.setaValorPasta();				  
			}
			
		}else{
            this.setCameFromMarcaModeloCrud(false);
        	this.carregarValoresInseridos();
        }
		
		this.setListaParecerAvaliacao(new ArrayList<ScoParecerAvaliacao>());
		this.setListaParecerOcorrencia(new ArrayList<ScoParecerOcorrencia>());
		
	
		
		if (this.getCodigoParecerMaterial() == null && (this.marcaComercialInserida != null || (this.marcaModeloInserida != null))) {
			if (marcaComercialInserida != null) {
				this.getParecerMaterial().setMarcaComercial(this.marcaComercialInserida);
			}
			if (marcaModeloInserida != null) {
				this.getParecerMaterial().setScoMarcaModelo(this.marcaModeloInserida);
			}
			 this.setaValorSubpasta();
		} else {
			this.marcaComercialInserida = null;
			this.marcaModeloInserida = null;
			
			
			if (this.getCodigoParecerMaterial() == null) {
				if (this.getParecerMaterial() == null) {
					this.setParecerMaterial(new ScoParecerMaterial());
					this.setSituacao(Boolean.TRUE);
				}
				if (this.codigoMaterial != null) {
				   this.getParecerMaterial().setMaterial(this.comprasFacade.obterMaterialPorId(this.codigoMaterial));
				}
				if (this.codigoMarca != null) {
				   this.getParecerMaterial().setMarcaComercial(this.comprasFacade.obterMarcaComercialPorCodigo(this.codigoMarca));
				   if (this.codigoModelo != null) {
					   this.getParecerMaterial().setScoMarcaModelo(this.comprasFacade.buscaScoMarcaModeloPorId(this.codigoModelo,this.codigoMarca));
				   }
				}
				 this.setaValorSubpasta();
			} else {
				this.setParecerMaterial(this.parecerFacade.obterParecer(this.getCodigoParecerMaterial()));
				this.setSituacao(this.getParecerMaterial().getIndSituacao().equals(DominioSituacao.A));

				ScoParecerAvaliacao scoParecerAvaliacao = this.parecerFacade.obterUltimaAvaliacaoParecer(this.getParecerMaterial());
				ScoParecerOcorrencia scoParecerOcorrencia = this.parecerFacade.obterUltimaOcorrenciaParecer(this.getParecerMaterial());

				if (scoParecerAvaliacao != null) {
					this.getListaParecerAvaliacao().add(scoParecerAvaliacao);
				}
				if (scoParecerOcorrencia != null) {
					this.getListaParecerOcorrencia().add(scoParecerOcorrencia);
				}
			}
		}
	
	}

    public Boolean getCameFromMarcaModeloCrud(){
        return this.cameFromMarcaModeloCrud;
    }

    public void setCameFromMarcaModeloCrud(Boolean cameFromMarcaModeloCrud) {
        this.cameFromMarcaModeloCrud = cameFromMarcaModeloCrud;
    }

	private void carregarValoresInseridos(){
		this.marcaComercialInserida = this.manterMarcaComercialController.getMarcaComercialInserida();
		this.marcaModeloInserida = this.manterMarcaComercialController.getMarcaModeloInserida();
	}
	
	public String gravar() {		
		
		final boolean novo = this.getParecerMaterial().getCodigo() == null;
		try {
			this.parecerFacade.verificarCampoObrigatorio(this.getParecerMaterial());
			if (this.getParecerMaterial() != null) {
				
				this.getParecerMaterial().setIndSituacao(this.getSituacao().equals(Boolean.TRUE) ? DominioSituacao.A : DominioSituacao.I);
				this.marcaComercialInserida = null;
				this.marcaModeloInserida = null;
				
				if (novo) {			
					if (parecerMaterial.getScoMarcaModelo() == null) {
						this.apresentarMsgNegocio(Severity.ERROR, "LABEL_CAMPO_OBRIGATORIO_MODELO");
						return null;
					}
					this.parecerFacade.inserirParecerMaterial(this.getParecerMaterial());	
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PARECER_MATERIAL_SUCESSO_M03");
					
				} else {					
					this.parecerFacade.alterarParecerMaterial(this.getParecerMaterial());					
				}
				
				this.setCodigoParecerMaterial(this.getParecerMaterial().getCodigo());				
				
		
			}
		}  catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			return null;
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}	
		
		if (novo) {	
			parecerAvaliacaoController.setCodigoParecer(this.getCodigoParecerMaterial());
			parecerAvaliacaoController.setCodigo(null);	
			parecerAvaliacaoController.setVoltarParaUrl(this.voltarPara);
			return PARECER_AVALIACAO_CRUD;
		}
		else {
			return null;
		}
	}	
	
	public String voltar(){
		return this.voltarPara;
	}
	
	public String redirecionarParecerAvaliacao(){
		return PARECER_AVALIACAO_CRUD;
	}
	
	public String redirecionarParecerAvaliacaoAposGravar(){
		if (criouNovo) {	
			return this.redirecionarParecerAvaliacaoAposGravar();
		}
		else {
			return null;
		}
	}
	
	
	public String redirecionarParecerOcorrencia(){
		return PARECER_OCORRENCIA_CRUD;
	}
	
	public String adicionarMarcaComercial() {
		this.marcaComercialInserida = null;
		this.marcaModeloInserida = null;
		this.getParecerMaterial().setMarcaComercial(null);
		this.getParecerMaterial().setScoMarcaModelo(null);
		this.cameFromMarcaModeloCrud = true;
		return MANTER_MARCA_COMERCIAL;
	}

	public String adicionarModelo() {
		this.cameFromMarcaModeloCrud = true;
		return MANTER_MARCA_COMERCIAL;
	}

	public String redirecionaHistorico(){
		return CONSULTAR_HISTORICO_PARECER;
	}
	
	public void setaValorPasta(){
		ScoParecerMaterial scoParecerMaterial = this.parecerFacade.obterParecerTecnicoAtivo(this.getParecerMaterial(), false);		
		this.setReadOnlyPasta(Boolean.FALSE);
		if (scoParecerMaterial != null){
			if (scoParecerMaterial.getOrigemParecerTecnico() != null){
				this.getParecerMaterial().setOrigemParecerTecnico(scoParecerMaterial.getOrigemParecerTecnico());
				this.setReadOnlyPasta(Boolean.TRUE);
			}
			if (scoParecerMaterial.getOrigemParecerTecnico()!=null && this.getParecerMaterial().getMaterial()!=null && this.getParecerMaterial().getMarcaComercial()!=null){
				ScoParecerMaterial scoParecerMaterialPorMarca = this.parecerFacade.obterParecerTecnicoAtivo(this.getParecerMaterial(), true);
				if(scoParecerMaterialPorMarca!=null){
					this.getParecerMaterial().setNumeroSubPasta(scoParecerMaterialPorMarca.getNumeroSubPasta());	
				} else {
					this.getParecerMaterial().setNumeroSubPasta(this.parecerFacade.obterMaxNumeroSubPasta(this.getParecerMaterial().getOrigemParecerTecnico()));		
				}
				
			}
		}  
	}
	
	public void setaValorSubpasta(){
		ScoParecerMaterial scoParecerMaterial = this.parecerFacade.obterParecerTecnicoAtivo(this.getParecerMaterial(), true);		
		if (this.getParecerMaterial().getMaterial()!=null && this.getParecerMaterial().getMarcaComercial()!=null && this.getParecerMaterial().getOrigemParecerTecnico()!=null){
			if (scoParecerMaterial != null){
				this.getParecerMaterial().setNumeroSubPasta(scoParecerMaterial.getNumeroSubPasta());
			}
			else {
				this.getParecerMaterial().setNumeroSubPasta(this.parecerFacade.obterMaxNumeroSubPasta(this.getParecerMaterial().getOrigemParecerTecnico()));
			} 
		} 
	}	

	public void selecionarSubPasta(){
		if(this.getParecerMaterial().getMaterial()!=null && this.getParecerMaterial().getMarcaComercial()!=null){
			this.getParecerMaterial().setNumeroSubPasta(this.parecerFacade.obterMaxNumeroSubPasta(this.getParecerMaterial().getOrigemParecerTecnico()));	
		}
	}
	
	public void limparModeloComercial() {
		this.getParecerMaterial().setScoMarcaModelo(null);
		this.getParecerMaterial().setNumeroSubPasta(null);
	}
	
	public void limparPasta(){
		this.getParecerMaterial().setOrigemParecerTecnico(null);
		this.getParecerMaterial().setNumeroSubPasta(null);
		this.setReadOnlyPasta(Boolean.FALSE);
	}
	
	public void limparSubPasta(){
		this.getParecerMaterial().setNumeroSubPasta(null);
	}
	
	public Boolean isInativo(){
		if(this.getParecerMaterial()==null||this.getParecerMaterial().getCodigo()==null||this.getParecerMaterial().getIndSituacao().equals(DominioSituacao.A)){
			return false;
		} else {
			return true;
		}
	}
	
	public Boolean isReadOnlyUltimaAvaliacao(){
		if (this.getListaParecerAvaliacao() != null && this.getListaParecerAvaliacao().size() > 0){
			return (DominioParecer.EA.equals(this.getListaParecerAvaliacao().get(0).getParecerGeral()));
		}
		return false;
	}
	
	//Suggesion Material
	public List<ScoMaterial> listarMateriais(String param) {	
		return this.returnSGWithCount(this.comprasFacade.listarMateriaisAtivos(param, null),listarMateriaisCount(param));	
	}
	
	public Long listarMateriaisCount(String param)	{
		return this.comprasFacade.listarMateriaisAtivosCount(param, null);				
	}
	
	//Suggesion Marca Comercial
	public List<ScoMarcaComercial> pesquisarMarcaComercial(String param) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricaoAtivo(param),pesquisarMarcaComercialCount(param));
	}
	
	public Long pesquisarMarcaComercialCount(String param) {
		return this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricaoAtivoCount(param);
	}
	
	//Suggesion Modelo
	public List<ScoMarcaModelo> pesquisarMarcaModeloPorCodigoDescricao(String param) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricao(param, this.getParecerMaterial().getMarcaComercial(), true),pesquisarMarcaModeloPorCodigoDescricaoCount(param));
	}
	
	public Long pesquisarMarcaModeloPorCodigoDescricaoCount(String param) {
		return this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricaoCount(param, this.getParecerMaterial().getMarcaComercial(), true);
	}
	
	//Suggesion Pasta
	public List<ScoOrigemParecerTecnico> pesquisarOrigemParecerTecnicoPorSeqDescricao(String param) {
		return this.returnSGWithCount(this.comprasFacade.obterOrigemParecerTecnico(param),pesquisarOrigemParecerTecnicoPorSeqDescricaoCount(param));
	}
	
	public Long pesquisarOrigemParecerTecnicoPorSeqDescricaoCount(String param) {
		return this.comprasFacade.obterOrigemParecerTecnicoCount(param);
	}
		
	public String redirecionaAnexos(){
		return SUPRIMENTOS_ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	} 
	
	// ### GETs e SETs ###

	public boolean isReadOnlyPasta() {
		return readOnlyPasta;
	}

	public void setReadOnlyPasta(Boolean readOnlyPasta) {
		this.readOnlyPasta = readOnlyPasta;
	}

	public void setVoltarPara(final String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}
	
	public Integer getCodigoParecerMaterial() {
		return codigoParecerMaterial;
	}

	public void setCodigoParecerMaterial(Integer codigo) {
		this.codigoParecerMaterial = codigo;
	}

	public ScoParecerMaterial getParecerMaterial() {
		return parecerMaterial;
	}

	public void setParecerMaterial(ScoParecerMaterial parecerMaterial) {
		this.parecerMaterial = parecerMaterial;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public List<ScoParecerAvaliacao> getListaParecerAvaliacao() {
		return listaParecerAvaliacao;
	}

	public void setListaParecerAvaliacao(List<ScoParecerAvaliacao> listaParecerAvaliacao) {
		this.listaParecerAvaliacao = listaParecerAvaliacao;
	}

	public List<ScoParecerOcorrencia> getListaParecerOcorrencia() {
		return listaParecerOcorrencia;
	}

	public void setListaParecerOcorrencia(List<ScoParecerOcorrencia> listaParecerOcorrencia) {
		this.listaParecerOcorrencia = listaParecerOcorrencia;
	}

	public Boolean getModoEdit() {
		return modoEdit;
	}

	public void setModoEdit(Boolean modoEdit) {
		this.modoEdit = modoEdit;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getCodigoMarca() {
		return codigoMarca;
	}

	public void setCodigoMarca(Integer codigoMarca) {
		this.codigoMarca = codigoMarca;
	}

	public Integer getCodigoModelo() {
		return codigoModelo;
	}

	public void setCodigoModelo(Integer codigoModelo) {
		this.codigoModelo = codigoModelo;
	}

	public ScoMarcaComercial getMarcaComercialInserida() {
		return marcaComercialInserida;
	}

	public void setMarcaComercialInserida(ScoMarcaComercial marcaComercialInserida) {
		this.marcaComercialInserida = marcaComercialInserida;
	}

	public ScoMarcaModelo getMarcaModeloInserida() {
		return marcaModeloInserida;
	}

	public void setMarcaModeloInserida(ScoMarcaModelo marcaModeloInserida) {
		this.marcaModeloInserida = marcaModeloInserida;
	}
}