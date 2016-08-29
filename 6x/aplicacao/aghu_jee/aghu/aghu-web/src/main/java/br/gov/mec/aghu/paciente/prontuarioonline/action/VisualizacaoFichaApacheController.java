package br.gov.mec.aghu.paciente.prontuarioonline.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;


public class VisualizacaoFichaApacheController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	private static final long serialVersionUID = 9128561133740413436L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private Integer atdSeq;
	private Short seqp;
	
	private AghAtendimentos atendimento;
	
	private MpmFichaApache fichaApache;
	
	private Integer pontuacaoFichaApache;
	
	private String voltarPara;
	
	public void inicio() {
		atendimento = aghuFacade.buscarDadosPacientePorAtendimento(atdSeq, 
				DominioOrigemAtendimento.I, DominioOrigemAtendimento.H,
				DominioOrigemAtendimento.U, DominioOrigemAtendimento.N);
		
		fichaApache = prescricaoMedicaFacade.pesquisarFichaApacheComEscalaGrasgows(atdSeq, seqp);
		
		pontuacaoFichaApache = prescricaoMedicaFacade.calcularPontuacaoFichaApache(fichaApache);
	}
	
	public String voltar() {
		return voltarPara;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public MpmFichaApache getFichaApache() {
		return fichaApache;
	}

	public void setFichaApache(MpmFichaApache fichaApache) {
		this.fichaApache = fichaApache;
	}

	public Integer getPontuacaoFichaApache() {
		return pontuacaoFichaApache;
	}

	public void setPontuacaoFichaApache(Integer pontuacaoFichaApache) {
		this.pontuacaoFichaApache = pontuacaoFichaApache;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	
}
