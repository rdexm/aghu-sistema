package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.VAbsMovimentoComponente;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;

public class ConsultarHemoterapiasPOLController extends ActionController {

	private static final long serialVersionUID = -7493613236747718790L;

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	private List<VAbsMovimentoComponente> listaVAbsMovimentoComponente;
	
	private Integer pacCodigo;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;		
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		inicio();
	}
	
	public void inicio() {
		
		if (itemPOL!=null){
			pacCodigo=(Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE); 
		}
		pesquisar();
	}

	

	public void pesquisar() {
		listaVAbsMovimentoComponente = bancoDeSangueFacade.pesquisarItensSolHemoterapicasPOL(pacCodigo);
	}
	

	public IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}

	public void setBancoDeSangueFacade(IBancoDeSangueFacade bancoDeSangueFacade) {
		this.bancoDeSangueFacade = bancoDeSangueFacade;
	}

	public Integer getNumeroProntuario() {
		return pacCodigo;
	}

	public void setNumeroProntuario(Integer numeroProntuario) {
		this.pacCodigo = numeroProntuario;
	}

	public List<VAbsMovimentoComponente> getListaVAbsMovimentoComponente() {
		return listaVAbsMovimentoComponente;
	}

	public void setListaVAbsMovimentoComponente(
			List<VAbsMovimentoComponente> listaVAbsMovimentoComponente) {
		this.listaVAbsMovimentoComponente = listaVAbsMovimentoComponente;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
}