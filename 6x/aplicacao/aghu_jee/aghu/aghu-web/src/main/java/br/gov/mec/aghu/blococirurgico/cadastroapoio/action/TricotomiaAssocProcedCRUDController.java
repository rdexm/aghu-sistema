package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.model.MbcAreaTricProcCirg;
import br.gov.mec.aghu.model.MbcAreaTricProcCirgId;
import br.gov.mec.aghu.model.MbcAreaTricotomia;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class TricotomiaAssocProcedCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3762669670002340391L;
	private static final String PCI_LIST = "procedimentoCirurgicoList";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	private Integer pciSeq;

	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private MbcAreaTricProcCirg area;
	private MbcAreaTricotomia areaTricotomia;
	private Short atrSeq;
	private Boolean mostraModalExclusaoItem;
	
	private List<MbcAreaTricProcCirg> lista;

	public void iniciar() {
	 

	 

		this.procedimentoCirurgico = this.blocoCirurgicoCadastroApoioFacade.obterProcedimentoCirurgico(pciSeq);
		this.lista = this.blocoCirurgicoCadastroApoioFacade.buscarAreaTricPeloSeqProcedimento(pciSeq);
		this.area = new MbcAreaTricProcCirg();
	
	}
	

	public void remover() {
		this.blocoCirurgicoCadastroApoioFacade.removerAreaTricProcedCirurgico(area);
		this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOVER_ASSC_AREA_TRIC_PROCED_CIRURGICO");
		this.lista = this.blocoCirurgicoCadastroApoioFacade.buscarAreaTricPeloSeqProcedimento(area.getId().getPciSeq());
		
	}

	public String voltar() {
		return PCI_LIST;
	}
	
	public void prepararExclusao(MbcAreaTricProcCirg item) {
		this.atrSeq = item.getId().getAtrSeq();
		this.mostraModalExclusaoItem = Boolean.TRUE;
	}
	
	public void gravar() {
		
		this.area.setId(new MbcAreaTricProcCirgId(pciSeq, areaTricotomia.getSeq()));
		area.setMbcAreaTricotomia(areaTricotomia);
		
		try {
			
					
			this.blocoCirurgicoCadastroApoioFacade.persistirAreaTricProcedCirurgico(area);
			this.lista = this.blocoCirurgicoCadastroApoioFacade.buscarAreaTricPeloSeqProcedimento(pciSeq);

			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_ASSC_AREA_TRIC_PROCED_CIRURGICO");
			
			this.area = new MbcAreaTricProcCirg();
			this.areaTricotomia = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<MbcAreaTricotomia> pesquisarArearTricotomia(String filtro) {
		return this.blocoCirurgicoCadastroApoioFacade.pesquisarPorSeqOuDescricao((String)filtro);
	}

	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(
			MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public MbcAreaTricotomia getAreaTricotomia() {
		return areaTricotomia;
	}

	public void setAreaTricotomia(MbcAreaTricotomia areaTricotomia) {
		this.areaTricotomia = areaTricotomia;
	}

	public MbcAreaTricProcCirg getArea() {
		return area;
	}

	public void setArea(MbcAreaTricProcCirg area) {
		this.area = area;
	}

	public List<MbcAreaTricProcCirg> getLista() {
		return lista;
	}

	public void setLista(List<MbcAreaTricProcCirg> lista) {
		this.lista = lista;
	}

	public Short getAtrSeq() {
		return atrSeq;
	}

	public void setAtrSeq(Short atrSeq) {
		this.atrSeq = atrSeq;
	}

	public Boolean getMostraModalExclusaoItem() {
		return mostraModalExclusaoItem;
	}

	public void setMostraModalExclusaoItem(Boolean mostraModalExclusaoItem) {
		this.mostraModalExclusaoItem = mostraModalExclusaoItem;
	}
	
	
}
