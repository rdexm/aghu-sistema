package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class PesquisarContasHospitalaresParaDesdobramentoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1611464716421395468L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private Integer pacProntuario;

	private Integer pacCodigo;

	private Integer cthSeq;
	
	private DominioSituacaoConta situacao;
	@Inject @Paginator
	private DynamicDataModel<VFatContaHospitalarPac> dataModel;
	
	private VFatContaHospitalarPac contaSelecionada;
	
	private static final String REDIRECIONA_DESDOBRAR_CONTA = "desdobramentoContaHospitalar";
	
	@Inject
	private DesdobramentoContaHospitalarPaginatorController desdobramentoContaHospitalarPaginatorController;
	
	@PostConstruct
	public void iniciar() {
		begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		DominioSituacaoConta[] situacoes = null;
		if (this.situacao != null) {
			situacoes = new DominioSituacaoConta[] { this.situacao };
		} else {
			situacoes = new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F }; 
		}
		
		return this.faturamentoFacade.pesquisarVFatContaHospitalarPacCount(this.pacProntuario, null, this.pacCodigo, this.cthSeq, situacoes);
	}

	@Override
	public List<VFatContaHospitalarPac> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		DominioSituacaoConta[] situacoes = null;
		if (this.situacao != null) {
			situacoes = new DominioSituacaoConta[] { this.situacao };
		} else {
			situacoes = new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F }; 
		}
		
		return this.faturamentoFacade.pesquisarVFatContaHospitalarPac(this.pacProntuario, null, this.pacCodigo, this.cthSeq,
				situacoes, firstResult, maxResult, orderProperty,
				asc);
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.pacProntuario = null;
		this.pacCodigo = null;
		this.cthSeq = null;
		this.situacao = null;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public String editar(Integer chtSeq) {
		desdobramentoContaHospitalarPaginatorController.setCthSeq(chtSeq);
		desdobramentoContaHospitalarPaginatorController.setFrom("pesquisarContasHospitalaresParaDesdobramento");
		desdobramentoContaHospitalarPaginatorController.inicio();
		return REDIRECIONA_DESDOBRAR_CONTA;
	}
	
	public DominioSituacaoConta[] getSituacaoValue() {
		return new DominioSituacaoConta[] {DominioSituacaoConta.A, DominioSituacaoConta.F};
	}
	
	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public DominioSituacaoConta getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoConta situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<VFatContaHospitalarPac> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VFatContaHospitalarPac> dataModel) {
		this.dataModel = dataModel;
	}

	public VFatContaHospitalarPac getContaSelecionada() {
		return contaSelecionada;
	}

	public void setContaSelecionada(VFatContaHospitalarPac contaSelecionada) {
		this.contaSelecionada = contaSelecionada;
	}


}
