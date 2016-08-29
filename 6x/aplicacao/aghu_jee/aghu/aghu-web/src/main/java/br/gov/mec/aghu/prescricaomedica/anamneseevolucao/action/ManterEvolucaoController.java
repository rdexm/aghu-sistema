package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;


public class ManterEvolucaoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 14350067867867L;
		
	private boolean evolucaoCorrente;
	private boolean permiteExclusao;
	private MpmEvolucoes evolucao;
	private MpmAnamneses anamneses;
	private RapServidores servidor;
	private String descricaoEvolucao;
	private String oldDescricaoEvolucao;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	public void iniciar() {
		if(DominioIndPendenteAmbulatorio.R.equals(evolucao.getPendente()) && evolucao.getDescricao() == null){
			try {
				descricaoEvolucao = ambulatorioFacade.getDescricaoItemEvolucao();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}else{
			descricaoEvolucao = evolucao.getDescricao();
		}
		
		if(descricaoEvolucao != null){
			// guardar valor original para validar se possui alteracao na aba
			oldDescricaoEvolucao = descricaoEvolucao;
		}
		
	}
	
	public String adicionar(){		

		Date dataReferencia;
		try {
			dataReferencia = this.prescricaoMedicaFacade.obterDataReferenciaEvolucao(this.anamneses.getAtendimento());
			this.evolucao = this.prescricaoMedicaFacade.criarMpmEvolucaoComDescricao(this.evolucao.getDescricao(), this.anamneses, dataReferencia, servidor);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	public String concluir(){
		try {
			prescricaoMedicaFacade.concluirEvolucoes(evolucao, descricaoEvolucao, servidor);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EVOLUCAO_VALIDADA");
			return "listarAnamneseEvolucoes";
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String deixarPendente(){
		try {
			prescricaoMedicaFacade.deixarPendenteEvolucao(evolucao, descricaoEvolucao, servidor);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EVOLUCAO_PENDENTE");
			return "listarAnamneseEvolucoes";
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void validarExclusao(){
		try {
			prescricaoMedicaFacade.validarEclusaoEvolucao(evolucao.getSeq(), servidor);
			permiteExclusao = true;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			permiteExclusao = false;
		}
	}
	
	public String excluir(){
		try {
			prescricaoMedicaFacade.excluirEvolucao(evolucao.getSeq(), servidor);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EVOLUCAO_EXCLUIDA");
			return "listarAnamneseEvolucoes";
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public boolean verificarAutorEvolucaoEdicao(){
		if(evolucao == null || CoreUtil.modificados(servidor, evolucao.getServidor()) && DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente())){
			return false;
		}
		return true;
	}
	
	public boolean verificarEvolucaoConcluida(){
		return evolucao != null && DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente()) ? Boolean.TRUE : Boolean.FALSE;
	}
	
	public boolean verificaEvolucaoCorrente(){
		if(evolucao != null){
			return DateUtil.validaDataMaiorIgual(DateUtil.truncaData(new Date()), DateUtil.truncaData(evolucao.getDthrReferencia())) && DateUtil.validaDataMenorIgual(new Date(), evolucao.getDthrFim());	
		}
		return false;
	}
	
	public boolean verificarAlteracao(){
		if(CoreUtil.modificados(descricaoEvolucao, oldDescricaoEvolucao)){
			return true;
		}
		return false;
	}
	
	public boolean isEvolucaoCorrente() {
		return evolucaoCorrente;
	}

	public MpmAnamneses getAnamneses() {
		return anamneses;
	}

	public void setAnamneses(MpmAnamneses anamneses) {
		this.anamneses = anamneses;
	}

	public MpmEvolucoes getEvolucao() {
		return evolucao;
	}

	public void setEvolucao(MpmEvolucoes evolucao) {
		this.evolucao = evolucao;
	}



	public RapServidores getServidor() {
		return servidor;
	}



	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public String getDescricaoEvolucao() {
		return descricaoEvolucao;
	}

	public void setDescricaoEvolucao(String descricaoEvolucao) {
		this.descricaoEvolucao = descricaoEvolucao;
	}

	public void setPermiteExclusao(boolean permiteExclusao) {
		this.permiteExclusao = permiteExclusao;
	}

	public boolean isPermiteExclusao() {
		return permiteExclusao;
	}

	public String getOldDescricaoEvolucao() {
		return oldDescricaoEvolucao;
	}

	public void setOldDescricaoEvolucao(String oldDescricaoEvolucao) {
		this.oldDescricaoEvolucao = oldDescricaoEvolucao;
	}
		
}
