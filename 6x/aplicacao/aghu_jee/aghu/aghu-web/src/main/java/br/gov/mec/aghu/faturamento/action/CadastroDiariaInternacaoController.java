package br.gov.mec.aghu.faturamento.action;


import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatDiariaInternacao;
import br.gov.mec.aghu.model.FatValorDiariaInternacao;
import br.gov.mec.aghu.model.FatValorDiariaInternacaoId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar o Cadastro de Diarias de Internação
 * @author thiago.cortes
 * #2146
 *
 */

public class CadastroDiariaInternacaoController extends ActionController {

	private static final long serialVersionUID = -1718942915357934441L;

	private static final String VOLTAR = "pesquisaDiariaInternacaoList";

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private FatDiariaInternacao fatDiariaInternacao = new FatDiariaInternacao();
	private FatValorDiariaInternacao fatValorDiariaInternacao = new FatValorDiariaInternacao();
	private FatValorDiariaInternacaoId fatValorDiariaInternacaoId = new FatValorDiariaInternacaoId();
	
	/**
	 * Flag para alternar a exibição dos botões: alterar, gravar e cancelar edição
	 */
	private Boolean editar = Boolean.FALSE;
	private List<FatValorDiariaInternacao> listaValoresDiariasInternacao;
	private List<FatValorDiariaInternacao> listaDataFimDiariaInternacao;
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
		editar = Boolean.FALSE;
		if(fatDiariaInternacao.getSeq() == null){
			fatDiariaInternacao.setSeq(null);

		}
			fatValorDiariaInternacaoId.setDinSeq(fatDiariaInternacao.getSeq());
			fatValorDiariaInternacao.setId(fatValorDiariaInternacaoId);
			
			if(fatValorDiariaInternacao.getId().getDinSeq() != null){
				if(fatDiariaInternacao.getSeq() == fatValorDiariaInternacao.getId().getDinSeq()){
					listarValoresDiariasInternacao();
				}
			}
	}
	
	public void limparCampos(){
		editar = Boolean.FALSE;
		fatValorDiariaInternacao = new FatValorDiariaInternacao();
		fatValorDiariaInternacaoId = new FatValorDiariaInternacaoId();
		fatValorDiariaInternacao.setId(fatValorDiariaInternacaoId);
	}
	
	public void persistirDiariaAutorizada(){	
		try{
			if(fatDiariaInternacao.getSeq() != null){
				this.faturamentoFacade.alterarDiariaInternacaoAutorizada(this.fatDiariaInternacao);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DIARIA_EDITADA_SUCESSO");
			}
			else{
				this.faturamentoFacade.persistirDiariaInternacaoAutorizada(this.fatDiariaInternacao);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DIARIA_CRIADA_SUCESSO");
			}
		}
		catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void adicionarValorDiaria(){
		
		try{
			fatValorDiariaInternacao.setId(fatValorDiariaInternacaoId);	
			fatValorDiariaInternacao.getId().setDinSeq(fatDiariaInternacao.getSeq());
			fatValorDiariaInternacao.setFatDiariaInternacao(fatDiariaInternacao);
			
			listaDataFimDiariaInternacao = faturamentoFacade.validarDiariaInternacao(fatDiariaInternacao.getSeq());
			if(!listaDataFimDiariaInternacao.isEmpty()){
				listaDataFimDiariaInternacao.get(0).setDataFimValidade(new Date());
				faturamentoFacade.alterarValorDiariaInternacao(listaDataFimDiariaInternacao.get(0));
			}
			faturamentoFacade.adicionarValorDiariaInternacao(this.fatValorDiariaInternacao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VALOR_DIARIA_CRIADA_SUCESSO");
			limparCampos();
			listarValoresDiariasInternacao();
		}
		catch(BaseException e){
			this.apresentarExcecaoNegocio(e);
		}
	}
	public void alterarValorDiaria(){
			
		try{
			faturamentoFacade.alterarValorDiariaInternacao(fatValorDiariaInternacao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VALOR_DIARIA_EDITADA_SUCESSO");
			limparCampos();
			listarValoresDiariasInternacao();
		}
		catch(BaseException e){
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar(){
		fatDiariaInternacao = new FatDiariaInternacao();
		listaValoresDiariasInternacao = null;
		limparCampos();
		return VOLTAR;
	}
	
	/**
	 * Cancelar edição do valor da diára
	 */
	public void cancelarEdicaoValorDiaria(){
		editar = Boolean.FALSE;
		listarValoresDiariasInternacao();
	}
	
	public void excluirValorDiariaAutorizada(){
		try{
			faturamentoFacade.removerValorDiariaInternacao(this.fatValorDiariaInternacao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_VALOR_DIARIA_EXCLUIDA_SUCESSO");
		}
		catch(BaseException e){
			this.apresentarExcecaoNegocio(e);
		}
		listarValoresDiariasInternacao();
	}
	
	public void editar(){		
		editar = Boolean.TRUE;
	}
	
	public void listarValoresDiariasInternacao(){
		fatValorDiariaInternacao = new FatValorDiariaInternacao();
		fatValorDiariaInternacao.setId(fatValorDiariaInternacaoId);
		
		listaValoresDiariasInternacao = faturamentoFacade.listarValorDiariaInternacao(null, fatValorDiariaInternacao, fatDiariaInternacao);
	}

	public FatDiariaInternacao getFatDiariaInternacao() {
		return fatDiariaInternacao;
	}

	public void setFatDiariaInternacao(FatDiariaInternacao fatDiariaInternacao) {
		this.fatDiariaInternacao = fatDiariaInternacao;
	}

	public FatValorDiariaInternacao getFatValorDiariaInternacao() {
		return fatValorDiariaInternacao;
	}

	public void setFatValorDiariaInternacao(
			FatValorDiariaInternacao fatValorDiariaInternacao) {
		this.fatValorDiariaInternacao = fatValorDiariaInternacao;
	}

	public List<FatValorDiariaInternacao> getListaValoresDiariasInternacao() {
		return listaValoresDiariasInternacao;
	}

	public void setListaValoresDiariasInternacao(
			List<FatValorDiariaInternacao> listaValoresDiariasInternacao) {
		this.listaValoresDiariasInternacao = listaValoresDiariasInternacao;
		
	}
	
	public List<FatValorDiariaInternacao> getListaDataFimDiariaInternacao() {
		return listaDataFimDiariaInternacao;
	}

	public void setlistaDataFimDiariaInternacao(
			List<FatValorDiariaInternacao> listaDataFimDiariaInternacao) {
		this.listaDataFimDiariaInternacao = listaDataFimDiariaInternacao;
		
	}

	public Boolean getEditar() {
		return editar;
	}

	public void setEditar(Boolean editar) {
		this.editar = editar;
	}

	public FatValorDiariaInternacaoId getFatValorDiariaInternacaoId() {
		return fatValorDiariaInternacaoId;
	}

	public void setFatValorDiariaInternacaoId(
			FatValorDiariaInternacaoId fatValorDiariaInternacaoId) {
		this.fatValorDiariaInternacaoId = fatValorDiariaInternacaoId;
	}	
}
