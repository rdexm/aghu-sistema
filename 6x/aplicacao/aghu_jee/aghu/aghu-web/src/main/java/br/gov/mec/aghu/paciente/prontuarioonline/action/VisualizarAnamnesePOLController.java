package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmNotaAdicionalAnamneses;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.CabecalhoAnamneseEvolucaoController;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.RelatorioAnamnesePacienteController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;


public class VisualizarAnamnesePOLController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	  this.begin(conversation);
	}

	private static final long serialVersionUID = 2175770723653572327L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private CabecalhoAnamneseEvolucaoController cabecalhoAnamneseEvolucaoController;	
	
	@Inject
	private RelatorioAnamnesePacienteController relatorioAnamnesePacienteController;
	
	private MpmAnamneses anamnese;
	private List<MpmNotaAdicionalAnamneses> listaNotasAdicionais; 
	
	private Integer seqAtendimento;	
	private String voltarPara;

	public String iniciar() {
		if(this.seqAtendimento != null) {
			iniciarCabecalho();
			this.anamnese = this.prescricaoMedicaFacade.obterAnamneseValidaParaAtendimento(this.seqAtendimento);			
		}
		if(this.anamnese == null) {
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_ANAMNESE_EXCLUIDA");
			return this.voltarPara;
		}		
		this.listaNotasAdicionais = this.prescricaoMedicaFacade.listarNotasAdicionaisAnamnese(this.anamnese.getSeq());		
		return null;
	}

	private void iniciarCabecalho() {
		this.cabecalhoAnamneseEvolucaoController.setSeqAtendimento(this.seqAtendimento);
		this.cabecalhoAnamneseEvolucaoController.iniciar();
	}
	
	public void imprimirAnamnese() {
		this.relatorioAnamnesePacienteController.setSeqAnamnese(this.anamnese.getSeq());
		this.relatorioAnamnesePacienteController.directPrint();
	}
	
	public String visualizarImpressaoAnamnese() {
		this.relatorioAnamnesePacienteController.setSeqAnamnese(this.anamnese.getSeq());
		this.relatorioAnamnesePacienteController.setVoltarPara("paciente-visualizarAnamnesePOL");
		return "prescricaomedica-visualizarImpressaoAnamnese";
	}
	
	public MpmAnamneses getAnamnese() {
		return anamnese;
	}

	public List<MpmNotaAdicionalAnamneses> getListaNotasAdicionais() {
		return listaNotasAdicionais;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}	
	
}
