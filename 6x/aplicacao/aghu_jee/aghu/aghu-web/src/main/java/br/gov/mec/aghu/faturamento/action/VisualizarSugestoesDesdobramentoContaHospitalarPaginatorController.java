package br.gov.mec.aghu.faturamento.action;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatContaSugestaoDesdobr;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class VisualizarSugestoesDesdobramentoContaHospitalarPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 7459241485840234489L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private Integer cthSeq;
	private Integer pacCodigo;
	private Integer prontuario;
	private String pacNome;
	private DominioSituacaoConta situacao;

	private Date dtSugestao;
	private Integer mdsSeq;
	private String descricao;
	
	private String origem;
	
	@Inject @Paginator
	private DynamicDataModel<FatContaSugestaoDesdobr> dataModel;
	
	public void limpar(){
		dtSugestao = null;
		mdsSeq = null;
		descricao = null;
		dataModel.reiniciarPaginator();
	}
	
	public void pesquisar(){
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return faturamentoFacade.pesquisarSugestoesDesdobramentoCount(dtSugestao, mdsSeq, descricao, cthSeq);
	}

	@Override
	public List<FatContaSugestaoDesdobr> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return faturamentoFacade.pesquisarSugestoesDesdobramento(dtSugestao, mdsSeq, descricao, cthSeq, firstResult, maxResult, orderProperty, asc);
	}

	public String voltar() {
		return origem;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public DominioSituacaoConta getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoConta situacao) {
		this.situacao = situacao;
	}

	public Date getDtSugestao() {
		return dtSugestao;
	}

	public void setDtSugestao(Date dtSugestao) {
		this.dtSugestao = dtSugestao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Integer getMdsSeq() {
		return mdsSeq;
	}

	public void setMdsSeq(Integer mdsSeq) {
		this.mdsSeq = mdsSeq;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public DynamicDataModel<FatContaSugestaoDesdobr> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatContaSugestaoDesdobr> dataModel) {
		this.dataModel = dataModel;
	}

	
}
