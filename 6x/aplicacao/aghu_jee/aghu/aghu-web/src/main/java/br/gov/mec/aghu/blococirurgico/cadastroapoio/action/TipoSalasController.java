package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcTipoSala;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class TipoSalasController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3163068797654878760L;
	
	//Campos filtro
	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;
	
	//Campos gravar
	private String descricaoGravar;
	private Boolean ativo;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirgCadFacade;
	
	private List<MbcTipoSala> listaTipoSala;
	private Boolean pesquisaEfetuada;
	
	
	public void iniciar() {
		limparPesquisa();
	}
	
	public void pesquisar() {
		listaTipoSala = blocoCirgCadFacade.pesquisarTipoSalas(codigo, descricao, situacao);
		pesquisaEfetuada = true;
	}
	
	public void limparPesquisa() {
		codigo = null;
		descricao = null;
		situacao = null;
		descricaoGravar = null;
		ativo = true;
		pesquisaEfetuada = false;
		listaTipoSala = null;
	}
	
	public void gravarTipoSala() {
		MbcTipoSala tipoSala = new MbcTipoSala();
		tipoSala.setDescricao(descricaoGravar);
		tipoSala.setSituacao(ativo ? DominioSituacao.A : DominioSituacao.I);
		try {
			
			blocoCirgCadFacade.gravarMbcTipoSala(tipoSala);			
			descricaoGravar = null;
			ativo = true;
			pesquisar();
			this.apresentarMsgNegocio(Severity.INFO, "LABEL_TIPOS_SALAS_MENSAGEM_INCLUSAO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void editarTipo(MbcTipoSala item) {
		if(DominioSituacao.A.equals(item.getSituacao())) {
			item.setSituacao(DominioSituacao.I);
		} else {
			item.setSituacao(DominioSituacao.A);
		}
		try {
			blocoCirgCadFacade.gravarMbcTipoSala(item);			
			this.apresentarMsgNegocio(Severity.INFO, "LABEL_TIPOS_SALAS_MENSAGEM_ALTERACAO_SITUACAO");
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean isActive(MbcTipoSala item) {
		if(DominioSituacao.A.equals(item.getSituacao())) {
			return true;
		} else {
			return false;
		}
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

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<MbcTipoSala> getListaTipoSala() {
		return listaTipoSala;
	}

	public void setListaTipoSala(List<MbcTipoSala> listaTipoSala) {
		this.listaTipoSala = listaTipoSala;
	}

	public String getDescricaoGravar() {
		return descricaoGravar;
	}

	public void setDescricaoGravar(String descricaoGravar) {
		this.descricaoGravar = descricaoGravar;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getPesquisaEfetuada() {
		return pesquisaEfetuada;
	}

	public void setPesquisaEfetuada(Boolean pesquisaEfetuada) {
		this.pesquisaEfetuada = pesquisaEfetuada;
	}

}