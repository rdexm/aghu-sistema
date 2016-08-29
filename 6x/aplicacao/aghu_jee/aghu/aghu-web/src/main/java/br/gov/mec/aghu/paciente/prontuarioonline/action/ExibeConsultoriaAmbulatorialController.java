package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;

public class ExibeConsultoriaAmbulatorialController extends ActionController {

	private static final long serialVersionUID = 6042571137962583729L;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade; 

	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;
	
	private List<MamInterconsultas> interconsultas;
	private MamInterconsultas interconsulta; 
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation, true);
		interconsultas = null;
	}
	
	public void inicio() {
		final Integer pacCodigo = (Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE);
		if (pacCodigo != null && interconsultas == null || interconsultas.isEmpty()) {
			interconsultas = prontuarioOnlineFacade.pesquisarConsultoriasAmbulatoriais(pacCodigo);
		}
	}
	
	public String getNomeServidorValidaCapitalizado(MamInterconsultas interconsulta) {
		return ambulatorioFacade.obterDescricaoCidCapitalizada(interconsulta.getServidorValida().getPessoaFisica().getNome(),CapitalizeEnum.TODAS);
		// então... o obterDescricaoCidCapitalizada() é na verdade a implementação do mpmc_minusculo() do banco...
		// porque o programador que implementou esta função especificou seu nome apenas para capitalização de descrição de CID
		// sendo esta função genérica eu não sei... mas é a mesma função segundo a anotação ORADB dela... =P
	}
	
	public String getNomeServidorValidaCapitalizadoTrunc(MamInterconsultas interconsulta) {
		return ambulatorioFacade.obterDescricaoCidCapitalizada(interconsulta.getServidorValida().getPessoaFisica().getNomeTrunc(35L),CapitalizeEnum.TODAS); 
	}

	public List<MamInterconsultas> getInterconsultas() {
		return interconsultas;
	}

	public void setInterconsultas(List<MamInterconsultas> interconsultas) {
		this.interconsultas = interconsultas;
	} 

	public MamInterconsultas getInterconsulta() {
		return interconsulta;
	}

	public void setInterconsulta(MamInterconsultas interconsulta) {
		this.interconsulta = interconsulta;
	}

	public NodoPOLVO getItemPOL() {
		return itemPOL;
	}

	public void setItemPOL(NodoPOLVO itemPOL) {
		this.itemPOL = itemPOL;
	}
}