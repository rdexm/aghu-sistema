package br.gov.mec.aghu.compras.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCriterioEscolhaForn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroCriterioEscolhaFornecedorController extends ActionController {

	private static final long serialVersionUID = 832486845168837642L;

	@EJB
	private IComprasFacade comprasFacade;
	
	private ScoCriterioEscolhaForn criterioEscolhaForn;
	private boolean novo;
	private String voltarParaUrl;

	//Filtros
	private Short codigo;
	private String descricao;
	private Boolean situacao = Boolean.TRUE;

	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void inicio() {
	 

	 

		if(situacao == null){
			situacao = Boolean.TRUE;
		}
		if (!novo && codigo!=null) {
			this.criterioEscolhaForn = this.comprasFacade.obterCriterioEscolhaForn(codigo);
			this.montarCampos();
		}else{
			this.criterioEscolhaForn = new ScoCriterioEscolhaForn();
			this.codigo = null;
			this.descricao = null;
			this.situacao = Boolean.TRUE;
		}
		
	
	}
	

	private void montarCampos() {
		if(criterioEscolhaForn!=null){
			this.codigo = criterioEscolhaForn.getCodigo();
			this.descricao = criterioEscolhaForn.getDescricao()!=null?criterioEscolhaForn.getDescricao():null;
			if(criterioEscolhaForn.getSituacao()!=null){
				this.situacao = criterioEscolhaForn.getSituacao().isAtivo()?Boolean.TRUE:Boolean.FALSE;
			}
		}
	}
	
	public String gravar() throws ApplicationBusinessException, ApplicationBusinessException {
		try {
			
			if(descricao!=null && !descricao.isEmpty()){
				criterioEscolhaForn.setDescricao(descricao);
			}
			if(situacao!=null){
				criterioEscolhaForn.setSituacao(situacao?DominioSituacao.A:DominioSituacao.I);
			}
			
			if(novo){
				comprasFacade.persistirCriterioEscolhaForn(criterioEscolhaForn);	
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_M1_CRITERIO_ESCOLHA");
			}else{
				comprasFacade.atualizarCriterioEscolhaForn(criterioEscolhaForn);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_M2_CRITERIO_ESCOLHA");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return this.voltarParaUrl;
	}
	
	
	public String cancelar() {
		return voltarParaUrl;
	}
	
	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public ScoCriterioEscolhaForn getCriterioEscolhaForn() {
		return criterioEscolhaForn;
	}

	public void setCriterioEscolhaForn(ScoCriterioEscolhaForn criterioEscolhaForn) {
		this.criterioEscolhaForn = criterioEscolhaForn;
	}

	public boolean isNovo() {
		return novo;
	}

	public void setNovo(boolean novo) {
		this.novo = novo;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
}
