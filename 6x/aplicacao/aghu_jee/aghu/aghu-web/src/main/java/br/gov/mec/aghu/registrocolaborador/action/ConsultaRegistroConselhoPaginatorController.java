package br.gov.mec.aghu.registrocolaborador.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;



public class ConsultaRegistroConselhoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<RapQualificacao> dataModel;

	private static final long serialVersionUID = -4074640101301219096L;


	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	

	private Integer codigoPessoa;
	private String nomePessoa;
	private String numRegistro;
	private String siglaConselho;

	@Override
	public Long recuperarCount() {
		return registroColaboradorFacade.pesquisarQualificacoesCount(codigoPessoa, nomePessoa, numRegistro, siglaConselho);
	}

	@Override
	public List<RapQualificacao> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		List<RapQualificacao> rapConsultaRegistroConselhos = registroColaboradorFacade
				.pesquisarQualificacoes(codigoPessoa, nomePessoa, numRegistro,
						siglaConselho, firstResult, maxResult);

		if (rapConsultaRegistroConselhos == null) {
			rapConsultaRegistroConselhos = new ArrayList<RapQualificacao>();
		}

		return rapConsultaRegistroConselhos;
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limpar() {
		codigoPessoa = null;
		nomePessoa = null;
		numRegistro = null;
		siglaConselho = null;
		dataModel.setPesquisaAtiva(false);
	}

	// getters & setters
	public void setSiglaConselho(String siglaConselho) {
		this.siglaConselho = StringUtils.trimToNull(siglaConselho);
	}

	public String getSiglaConselho() {
		return siglaConselho;
	}

	public void setNumRegistro(String numRegistro) {
		this.numRegistro = StringUtils.trimToNull(numRegistro);
	}

	public String getNumRegistro() {
		return numRegistro;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = StringUtils.trimToNull(nomePessoa);
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setCodigoPessoa(Integer codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
	}

	public Integer getCodigoPessoa() {
		return codigoPessoa;
	}
 


	public DynamicDataModel<RapQualificacao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapQualificacao> dataModel) {
	 this.dataModel = dataModel;
	}
}
