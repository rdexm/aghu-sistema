package br.gov.mec.aghu.internacao.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.business.vo.EscalaIntenacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class PesquisaEscalaProfissionaisInternacaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8137990400970859505L;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	@Inject @Paginator
	private DynamicDataModel<EscalaIntenacaoVO> dataModel;
	private EscalaIntenacaoVO objetoSelecionado;
	private Short codigoConvenio;
	private Integer matriculaServidor;
	private Short vinculoServidor;
	private Integer seqEspecialidade;
	private Date dataInicio;
	private Date dataFim;

	
	public void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return internacaoFacade.pesquisarEscalaCount(
				vinculoServidor, matriculaServidor, seqEspecialidade,
				codigoConvenio, dataInicio, dataFim);
	}

	@Override
	public List<EscalaIntenacaoVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<EscalaIntenacaoVO> lista = new ArrayList<EscalaIntenacaoVO>();
		try {
			lista = internacaoFacade.montarEscalaVO(
					vinculoServidor, matriculaServidor, seqEspecialidade,
					codigoConvenio, dataInicio, dataFim, firstResult,
					maxResult, orderProperty, asc);
		} catch (ApplicationBusinessException e) {
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


	// getters and setters
	public Short getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(Short codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}

	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}

	public Short getVinculoServidor() {
		return vinculoServidor;
	}

	public void setVinculoServidor(Short vinculoServidor) {
		this.vinculoServidor = vinculoServidor;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Integer seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public EscalaIntenacaoVO getObjetoSelecionado() {
		return objetoSelecionado;
	}

	public void setObjetoSelecionado(EscalaIntenacaoVO objetoSelecionado) {
		this.objetoSelecionado = objetoSelecionado;
	}

	public DynamicDataModel<EscalaIntenacaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EscalaIntenacaoVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	
	
}
