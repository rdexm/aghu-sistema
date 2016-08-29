package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.ResponsavelAfVO;
import br.gov.mec.aghu.core.action.ActionController;

public class ListaResponsaveisEtapasVersaoAFController extends ActionController {

	private static final long serialVersionUID = -8149832884603085607L;
	
	private Integer numeroAf;
	private Short complementoAf;
	private Short sequenciaAlteracao;
	private String origem;
	
	@EJB
	private IAutFornecimentoFacade afFacade;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public List<ResponsavelAfVO> getResponsaveis() {
		return afFacade.obterResponsaveisAutorizacaoFornJn(numeroAf, complementoAf, sequenciaAlteracao);
	}
	
	public String voltar(){
		return origem;
	}

	// Getters/Setters

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getComplementoAf() {
		return complementoAf;
	}

	public void setComplementoAf(Short complementoAf) {
		this.complementoAf = complementoAf;
	}

	public Short getSequenciaAlteracao() {
		return sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Short sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
}