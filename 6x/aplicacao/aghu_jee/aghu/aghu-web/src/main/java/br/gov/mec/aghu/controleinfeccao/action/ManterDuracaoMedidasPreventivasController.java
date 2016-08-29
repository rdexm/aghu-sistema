package br.gov.mec.aghu.controleinfeccao.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class ManterDuracaoMedidasPreventivasController extends ActionController {

	private static final String PAGINA_DURACAO_MEDIDAS_LIST = "controleinfeccao-pesquisarDuracaoMedidasPreventivas";
	private static final long serialVersionUID = -5159107032113993399L;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	// manter
	private String descricaoManter;
	
	private Short codigoManter;
	
	private Boolean situacaoManter;
	
	private Boolean emEdicao;
	
	private Boolean cadastroNovo;
	
	private Boolean exibeCodigo;
	
	private Boolean habilitaDescricao;
	
	private Short seqEditar;
	
	private Date criadoEmEditar;
	
	private MciDuracaoMedidaPreventiva itemEdicao;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

	 
	
		if(seqEditar != null){
			this.exibeCodigo = true;
			this.emEdicao = true;
			this.itemEdicao = controleInfeccaoFacade.obterDuracaoMedidaPreventiva(seqEditar);
			this.codigoManter = itemEdicao.getSeq();
			this.descricaoManter = itemEdicao.getDescricao();
			this.situacaoManter = DominioSituacao.A.equals(itemEdicao.getSituacao()) ? true: false;
			this.habilitaDescricao = false;
		}else{
			this.exibeCodigo = false;
			this.emEdicao = false;
			this.codigoManter = null;
			this.descricaoManter = null;
			this.situacaoManter = true;
			this.habilitaDescricao = true;
		}
	
	}
	
	
	public String voltarPesquisa(){
		this.emEdicao = null;
		this.seqEditar = null;
		this.itemEdicao = null;
		this.codigoManter = null;
		this.descricaoManter = null;
		this.situacaoManter = null;
		this.habilitaDescricao = null;
		return PAGINA_DURACAO_MEDIDAS_LIST;
	}
	
	public void inserir() throws ApplicationBusinessException{
		MciDuracaoMedidaPreventiva obj = new MciDuracaoMedidaPreventiva();
		obj.setDescricao(descricaoManter);
		obj.setSituacao(situacaoManter == true ? DominioSituacao.A : DominioSituacao.I);
		obj.setCriadoEm(new Date());
		controleInfeccaoFacade.inserirDuracaoMedidaPreventiva(obj);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_DURACAO_MEDIDAS", descricaoManter);
		voltarPesquisa();
	}
	
	public void atualizar() throws ApplicationBusinessException{
		itemEdicao.setSituacao(situacaoManter == true ? DominioSituacao.A : DominioSituacao.I);
		itemEdicao.setAlteradoEm(new Date());
		controleInfeccaoFacade.atualizarDuracaoMedidaPreventiva(itemEdicao);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_DURACAO_MEDIDAS", descricaoManter);
		voltarPesquisa();
	}
	
	public String persistirDados(){
		try {
			if(emEdicao){
				atualizar();
			}else{
				inserir();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return "";
		}
		return PAGINA_DURACAO_MEDIDAS_LIST;
	}
		
	public String getDescricaoManter() {
		return descricaoManter;
	}

	public void setDescricaoManter(String descricaoManter) {
		this.descricaoManter = descricaoManter;
	}

	public Short getCodigoManter() {
		return codigoManter;
	}

	public void setCodigoManter(Short codigoManter) {
		this.codigoManter = codigoManter;
	}


	public Boolean getSituacaoManter() {
		return situacaoManter;
	}


	public void setSituacaoManter(Boolean situacaoManter) {
		this.situacaoManter = situacaoManter;
	}


	public Boolean getEmEdicao() {
		return emEdicao;
	}


	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}


	public Boolean getCadastroNovo() {
		return cadastroNovo;
	}


	public void setCadastroNovo(Boolean cadastroNovo) {
		this.cadastroNovo = cadastroNovo;
	}


	public Boolean getExibeCodigo() {
		return exibeCodigo;
	}


	public void setExibeCodigo(Boolean exibeCodigo) {
		this.exibeCodigo = exibeCodigo;
	}

	public Boolean getHabilitaDescricao() {
		return habilitaDescricao;
	}


	public void setHabilitaDescricao(Boolean habilitaDescricao) {
		this.habilitaDescricao = habilitaDescricao;
	}


	public Short getSeqEditar() {
		return seqEditar;
	}


	public void setSeqEditar(Short seqEditar) {
		this.seqEditar = seqEditar;
	}


	public Date getCriadoEmEditar() {
		return criadoEmEditar;
	}


	public void setCriadoEmEditar(Date criadoEmEditar) {
		this.criadoEmEditar = criadoEmEditar;
	}


	public MciDuracaoMedidaPreventiva getItemEdicao() {
		return itemEdicao;
	}


	public void setItemEdicao(MciDuracaoMedidaPreventiva itemEdicao) {
		this.itemEdicao = itemEdicao;
	}
	
}
