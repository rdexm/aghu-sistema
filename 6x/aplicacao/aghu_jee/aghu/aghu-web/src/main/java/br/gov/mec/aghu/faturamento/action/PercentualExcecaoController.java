package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatExcecaoPercentual;
import br.gov.mec.aghu.model.FatExcecaoPercentualId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PercentualExcecaoController extends ActionController {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 332397066566832797L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private static final String PAGE_PESQUISA_PERCENTUAL_EXCECAO = "percentualExcecaoList";
	
	private FatItensProcedHospitalar fatItensProcedHospitalar;
	private FatExcecaoPercentualId fatExcecaoPercentualId = new FatExcecaoPercentualId();
	private FatExcecaoPercentual fatExcecaoPercentual = new FatExcecaoPercentual();
 
	private List<FatExcecaoPercentual> listExcecoesPercentual;

	private Boolean flagEditar = Boolean.TRUE;
	
	@PostConstruct
	public void inicializar() {
		begin(conversation);
	}
	
	public void preencherTabela() {
		setFatExcecaoPercentual(new FatExcecaoPercentual());
		FatExcecaoPercentualId id = new FatExcecaoPercentualId();
		id.setIphPhoSeq(fatItensProcedHospitalar.getId().getPhoSeq());
		id.setIphSeq(fatItensProcedHospitalar.getSeq());
		getFatExcecaoPercentual().setId(id);
		listExcecoesPercentual = faturamentoFacade.listarExcecaoPercentual(0, 10, null, true, getFatExcecaoPercentual());
	}
	
	public void adicionar(){
		fatExcecaoPercentualId.setIphSeq(fatItensProcedHospitalar.getSeq());
		fatExcecaoPercentualId.setIphPhoSeq(fatItensProcedHospitalar.getId().getPhoSeq());
		
		int maiorSeq = 0;
		for (int i = 0; i < listExcecoesPercentual.size(); i++) {
			if (listExcecoesPercentual.get(i).getId().getSeqp() > maiorSeq) {
				maiorSeq = listExcecoesPercentual.get(i).getId().getSeqp();
			}
		}
		fatExcecaoPercentualId.setSeqp((byte)(maiorSeq + 1));
		fatExcecaoPercentual.setId(getFatExcecaoPercentualId());
		try {
			this.faturamentoFacade.adicionarExcecaoPercentual(fatExcecaoPercentual);
			this.apresentarMsgNegocio(Severity.INFO, "PERCENTUAL DE EXCEÇÃO ADICIONADA COM SUCESSO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}		

		preencherTabela();
	}
	
	public void excluir(){
		setFatExcecaoPercentual(fatExcecaoPercentual);
		faturamentoFacade.excluirExcecaoPercentual(fatExcecaoPercentual);
		this.apresentarMsgNegocio(Severity.INFO, "PERCENTUAL DE EXCEÇÃO EXCLUIDA COM SUCESSO");
		flagEditar = Boolean.TRUE;
		
		preencherTabela();
	}
	
	public void cancelar(){
		flagEditar = Boolean.TRUE;
		preencherTabela();
	}
	
	public void buttonEditar(){
		fatExcecaoPercentualId.setIphSeq(fatItensProcedHospitalar.getSeq());
		fatExcecaoPercentualId.setIphPhoSeq(fatItensProcedHospitalar.getId().getPhoSeq());
		
		this.faturamentoFacade.alterarExcecaoPercentual(fatExcecaoPercentual);
		setFlagEditar(Boolean.TRUE);
		
		preencherTabela();
	}
	
	public void iconeEditar(){
		setFatExcecaoPercentual(fatExcecaoPercentual);
		setFlagEditar(Boolean.FALSE);
	}
	
	public String voltar() {
		this.flagEditar = Boolean.TRUE;
		return PAGE_PESQUISA_PERCENTUAL_EXCECAO;
	}

	/*
	 * GETs e SETs.
	 */
	public FatItensProcedHospitalar getFatItensProcedHospitalar() {
		return fatItensProcedHospitalar;
	}

	public void setFatItensProcedHospitalar(FatItensProcedHospitalar fatItensProcedHospitalar) {
		this.fatItensProcedHospitalar = fatItensProcedHospitalar;
		preencherTabela();
	}

	public FatExcecaoPercentual getFatExcecaoPercentual() {
		return fatExcecaoPercentual;
	}

	public void setFatExcecaoPercentual(FatExcecaoPercentual fatExcecaoPercentual) {
		this.fatExcecaoPercentual = fatExcecaoPercentual;
	}

	public FatExcecaoPercentualId getFatExcecaoPercentualId() {
		return fatExcecaoPercentualId;
	}

	public void setFatExcecaoPercentualId(FatExcecaoPercentualId fatExcecaoPercentualId) {
		this.fatExcecaoPercentualId = fatExcecaoPercentualId;
	}

	public List<FatExcecaoPercentual> getListExcecoesPercentual() {
		return listExcecoesPercentual;
	}

	public void setListExcecoesPercentual(List<FatExcecaoPercentual> listExcecoesPercentual) {
		this.listExcecoesPercentual = listExcecoesPercentual;
	}

	public Boolean getFlagEditar() {
		return flagEditar;
	}

	public void setFlagEditar(Boolean flag_editar) {
		this.flagEditar = flag_editar;
	}
}

