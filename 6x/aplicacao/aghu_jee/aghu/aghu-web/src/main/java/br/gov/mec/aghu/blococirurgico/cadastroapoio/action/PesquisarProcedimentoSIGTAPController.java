package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;


public class PesquisarProcedimentoSIGTAPController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3762669677142340391L;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;	

	private static final String PCI_LIST = "procedimentoCirurgicoList";
	
	private Integer pciSeq;
	private Integer phiSeq;
	
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private List<FatProcedHospInternos> procedimentosIternos;
	private List<VFatAssociacaoProcedimento> procedimentosAssociados;
	

	public void iniciar() {
	 

	 

		this.procedimentoCirurgico = this.blocoCirurgicoCadastroApoioFacade.obterProcedimentoCirurgico(pciSeq);
		this.procedimentosIternos = this.faturamentoFacade.pesquisarProcedimentosInternosPeloSeqProcCirg(pciSeq);
		try {
			if(this.procedimentosIternos != null && !this.procedimentosIternos.isEmpty()) {
				this.phiSeq = this.procedimentosIternos.get(0).getSeq();
				this.procedimentosAssociados = this.faturamentoFacade.pesquisarVFatAssociacaoProcedimentoAtivosPorPhi(this.phiSeq);
			}
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	
	
	public void atulizarListaProcedimentosAssociados() {
		try {
			this.procedimentosAssociados = this.faturamentoFacade.pesquisarVFatAssociacaoProcedimentoAtivosPorPhi(this.phiSeq);
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String getDescProcedimentoHospitalar(Short iphPhoSeq) {
		FatProcedimentosHospitalares procedimentoHospitalar = this.faturamentoFacade.obterProcedimentoHospitalar(iphPhoSeq);
		return procedimentoHospitalar.getDescricao();
	}
	
	public String voltar() {
		return PCI_LIST;
	}
	
	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(
			MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public List<FatProcedHospInternos> getProcedimentosIternos() {
		return procedimentosIternos;
	}

	public void setProcedimentosIternos(
			List<FatProcedHospInternos> procedimentosIternos) {
		this.procedimentosIternos = procedimentosIternos;
	}

	public List<VFatAssociacaoProcedimento> getProcedimentosAssociados() {
		return procedimentosAssociados;
	}

	public void setProcedimentosAssociados(
			List<VFatAssociacaoProcedimento> procedimentosAssociados) {
		this.procedimentosAssociados = procedimentosAssociados;
	}
}
