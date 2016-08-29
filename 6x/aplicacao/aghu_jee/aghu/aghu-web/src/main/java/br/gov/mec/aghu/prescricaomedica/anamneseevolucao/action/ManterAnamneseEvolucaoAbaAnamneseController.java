package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class ManterAnamneseEvolucaoAbaAnamneseController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 1345476586786L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private MpmAnamneses anamneseCorrente;
	private AghAtendimentos atendimento;
	private RapServidores servidorLogado;
	
	//Controllers Sliders
	@Inject
	private ManterAnamneseController manterAnamneseController;	
	
	@Inject
	private ManterNotaAdicionalAnamneseController manterNotaAdicionalAnamneseController;
	
	private Long seqAnamnese;
	private Integer seqAtendimento;
	
	public void iniciar(){
		
		//carrega informacao de atendimento 
		atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(seqAtendimento);
		try {
			//carrega informacao do usuario logado
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		iniciarSliderAnamnese();
		
		//verifica se possui anamnese validada para habilitar o slider de nota adicional
		obterAnamneseValidada();
		if(anamneseCorrente != null){
			iniciarSliderNotaAdicional();
		}
	}

	private void obterAnamneseValidada() {
		if(seqAnamnese != null){
			anamneseCorrente = prescricaoMedicaFacade.obterAnamneseValidadaPorAnamneses(seqAnamnese);
		} 
	}

	private void iniciarSliderNotaAdicional() {
		manterNotaAdicionalAnamneseController.setServidor(servidorLogado);
		manterNotaAdicionalAnamneseController.setAnamneseCorrente(anamneseCorrente);
		manterNotaAdicionalAnamneseController.iniciar();
	}

	private void iniciarSliderAnamnese() {
		this.manterAnamneseController.setSeqAtendimento(this.seqAtendimento);
		this.manterAnamneseController.setSeqAnamnese(this.seqAnamnese);
		this.manterAnamneseController.setServidor(servidorLogado);
		this.manterAnamneseController.iniciar();
	}

	public boolean habilitaPainelNotaAdicional() {
		if (anamneseCorrente != null) {
			return true;
		}
		return false;
	}

	public boolean verificarAlteracao(){
		return manterAnamneseController.verificarAlteracao() || manterNotaAdicionalAnamneseController.verificarAlteracao();
	}
	
	public void removerAnamnese() {
		this.prescricaoMedicaFacade.removerAnamnese(this.seqAnamnese);
	}
	
	public Long getSeqAnamnese() {
		return seqAnamnese;
	}

	public void setSeqAnamnese(Long seqAnamnese) {
		this.seqAnamnese = seqAnamnese;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}
	
	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public MpmAnamneses getAnamneseCorrente() {
		return anamneseCorrente;
	}

	public void setAnamneseCorrente(MpmAnamneses anamneseCorrente) {
		this.anamneseCorrente = anamneseCorrente;
	}
}
