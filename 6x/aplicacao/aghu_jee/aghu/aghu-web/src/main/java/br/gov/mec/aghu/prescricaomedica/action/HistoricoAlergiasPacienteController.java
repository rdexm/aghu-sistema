package br.gov.mec.aghu.prescricaomedica.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.AipAlergiaPacientes;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;

public class HistoricoAlergiasPacienteController extends ActionController {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2551965060039685311L;
	
	public static final String CONSULTAR_ALERGIAS="prescricaomedica-manterPrescricaoAlergia"; 
	

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private List<AipAlergiaPacientes> aipAlergiaPacientes;
	
	private AipAlergiaPacientes alergiaSelecionada;

	public AipAlergiaPacientes getAlergiaSelecionada() {
		return alergiaSelecionada;
	}

	public void setAlergiaSelecionada(AipAlergiaPacientes alergiaSelecionada) {
		this.alergiaSelecionada = alergiaSelecionada;
	}

	private PrescricaoMedicaVO prescricaoMedicaVO = new PrescricaoMedicaVO();
	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	private Integer pacCodigo;
	private String informadoPor;
	private String canceladoPor;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Realiza todas as inicializações necessárias para a apresentação da tela
	 * no modo de inclusão ou edição.
	 */
	public void iniciar() {
		
		//prescricaoMedicaVO
		aipAlergiaPacientes = prescricaoMedicaFacade.obterAipAlergiasPacientesHistorico(pacCodigo);
		for (AipAlergiaPacientes aipAlergiaPac : aipAlergiaPacientes) {
			if(aipAlergiaPac.getAusSeq() != null){
				aipAlergiaPac.setDescricao(this.prescricaoMedicaFacade.obterMpmAlergiaUsualPorSeq(
						aipAlergiaPac.getAusSeq()).getDescricao());
			}
		}
		
		Collections.sort(aipAlergiaPacientes, new Comparator<AipAlergiaPacientes>() {
			@Override
			public int compare(AipAlergiaPacientes c1, AipAlergiaPacientes c2) {
				return c1.getDescricao().compareTo(c2.getDescricao());
			}
		});
	}

	
	public void setarInformadoPorAlteradoPor(){
		if(this.alergiaSelecionada.getSerMatricula()!=null){
			informadoPor = (String) registroColaboradorFacade.obterNomeServidor(this.alergiaSelecionada.getSerMatricula(), this.alergiaSelecionada.getSerVinCodigo());
		}
		else{
			informadoPor = StringUtils.EMPTY;
		}
		if(this.alergiaSelecionada.getSerMatriculaInativada()!=null){
			canceladoPor = (String) registroColaboradorFacade.obterNomeServidor(this.alergiaSelecionada.getSerMatriculaInativada(), this.alergiaSelecionada.getSerVinCodigoInativada());
		}
		else{
			canceladoPor = StringUtils.EMPTY;
		}
	}
	
	public void limpar() {
		informadoPor = StringUtils.EMPTY;
		canceladoPor = StringUtils.EMPTY;
		alergiaSelecionada = null;
		pacCodigo = null;
	}
	
	/**
	 * get's and set's
	 * @return
	 */
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public List<AipAlergiaPacientes> getAipAlergiaPacientes() {
		return aipAlergiaPacientes;
	}

	public void setAipAlergiaPacientes(List<AipAlergiaPacientes> aipAlergiaPacientes) {
		this.aipAlergiaPacientes = aipAlergiaPacientes;
	}
	
	public String getInformadoPor() {
		return informadoPor;
	}

	public void setInformadoPor(String informadoPor) {
		this.informadoPor = informadoPor;
	}

	public String getCanceladoPor() {
		return canceladoPor;
	}

	public void setCanceladoPor(String canceladoPor) {
		this.canceladoPor = canceladoPor;
	}
	
	public String voltar(){
		limpar();
		return CONSULTAR_ALERGIAS;
	}
	

}