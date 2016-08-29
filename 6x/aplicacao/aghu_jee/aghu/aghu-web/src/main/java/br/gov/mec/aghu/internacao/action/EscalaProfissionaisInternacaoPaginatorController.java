package br.gov.mec.aghu.internacao.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.business.vo.ProfissionaisEscalaIntenacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class EscalaProfissionaisInternacaoPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 8636578445235096748L;

	@EJB
	private IInternacaoFacade internacaoFacade;
    @Inject @Paginator
	private DynamicDataModel<ProfissionaisEscalaIntenacaoVO> dataModel;
	
	private ProfissionaisEscalaIntenacaoVO objetoSelecionado;
	
	private Integer matricula;
	private Short vinculo;
	private String nomeServidor;
	private String conselhoProfissional;
	private String siglaEspecialidade;
	private Short codigoConvenio;
	private String descricaoConvenio;

	
	
	public void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		Long result = 0L;
		try{
			result = internacaoFacade
					.pesquisarProfissionaisEscalaCount(vinculo, matricula,
							conselhoProfissional, nomeServidor,
							siglaEspecialidade, codigoConvenio,
							descricaoConvenio);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return result;
	}
	


	@Override
	public List<ProfissionaisEscalaIntenacaoVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<ProfissionaisEscalaIntenacaoVO> lista = new ArrayList<ProfissionaisEscalaIntenacaoVO>();

		try {
			lista = internacaoFacade
					.pesquisarProfissionaisEscala(vinculo, matricula,
							conselhoProfissional, nomeServidor,
							siglaEspecialidade, codigoConvenio,
							descricaoConvenio, firstResult, maxResult,
							orderProperty, asc);
		} catch (ApplicationBusinessException e) {
			this.dataModel.setPesquisaAtiva(false);
			apresentarExcecaoNegocio(e); 
		}
		return lista;
	}
	
	public void setAtivo(Boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public Boolean getAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
	public void setMaxResults(Integer results){
		this.dataModel.setDefaultMaxRow(results);
	}

	public DynamicDataModel<ProfissionaisEscalaIntenacaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<ProfissionaisEscalaIntenacaoVO> dataModel) {
		this.dataModel = dataModel;
	}

	// getters and setters
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public String getConselhoProfissional() {
		return conselhoProfissional;
	}

	public void setConselhoProfissional(String conselhoProfissional) {
		this.conselhoProfissional = conselhoProfissional;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public Short getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(Short codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}

	public void setDescricaoConvenio(String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}

	public ProfissionaisEscalaIntenacaoVO getObjetoSelecionado() {
		return objetoSelecionado;
	}

	public void setObjetoSelecionado(ProfissionaisEscalaIntenacaoVO objetoSelecionado) {
		this.objetoSelecionado = objetoSelecionado;
	}
}
