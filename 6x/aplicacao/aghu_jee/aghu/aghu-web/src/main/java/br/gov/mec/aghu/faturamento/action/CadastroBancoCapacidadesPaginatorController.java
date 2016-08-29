package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatBancoCapacidadeVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroBancoCapacidadesPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7585397579513345903L;

	@Inject @Paginator
	private DynamicDataModel<FatBancoCapacidadeVO> dataModel;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	// FILTRO
	private Integer mes;
	private Integer ano;
	private Integer numeroLeitos;
	private Integer capacidade;
	private Integer utilizacao;	
	private AghClinicas clinica;
	private boolean erroCampoObrigatorio;
	private List<FatBancoCapacidadeVO> lista;
	private Long count;

	@PostConstruct
	public void init() {		
		begin(conversation);
	}

	public List<AghClinicas> pesquisarClinicas(String parametro) {
		return this.returnSGWithCount(this.faturamentoFacade.pesquisarClinicas(parametro),pesquisarClinicasCount(parametro));
	}

	public Long pesquisarClinicasCount(String parametro) {
		return this.faturamentoFacade.pesquisarClinicasCount(parametro);
	}

	public void pesquisar() {
		erroCampoObrigatorio = false;
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.mes = null;
		this.ano = null;
		this.numeroLeitos = null;
		this.capacidade = null;
		this.utilizacao = null;	
		this.clinica = null;

		this.dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {		
		if (!erroCampoObrigatorio) {
			count = this.faturamentoFacade.pesquisarBancosCapacidadeCount(this.mes, this.ano, this.numeroLeitos, this.capacidade, this.utilizacao, clinica);
		}
		return count;
	} 


	@Override
	public List<FatBancoCapacidadeVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		if (!erroCampoObrigatorio) {
			lista = this.faturamentoFacade.pesquisarBancosCapacidade(this.mes, this.ano, this.numeroLeitos, this.capacidade, this.utilizacao, clinica, firstResult, maxResult, orderProperty, asc);	
		}
		return lista;
	}

	public void atualizarBancoCapacidade(FatBancoCapacidadeVO item) {
		try {
			erroCampoObrigatorio = false;
			if (item.getNumeroLeitos() == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_NUMERO_LEITOS_OBRIGATORIO");
				erroCampoObrigatorio = true;
				return;
			}
			item = faturamentoFacade.atualizarBancoCapacidade(item);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_BANCO_CAPACIDADE");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getNumeroLeitos() {
		return numeroLeitos;
	}

	public void setNumeroLeitos(Integer numeroLeitos) {
		this.numeroLeitos = numeroLeitos;
	}

	public Integer getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Integer capacidade) {
		this.capacidade = capacidade;
	}

	public Integer getUtilizacao() {
		return utilizacao;
	}

	public void setUtilizacao(Integer utilizacao) {
		this.utilizacao = utilizacao;
	}

	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}
	public DynamicDataModel<FatBancoCapacidadeVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatBancoCapacidadeVO> dataModel) {
		this.dataModel = dataModel;
	}
}
