package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.MpmAlergiaUsual;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCadastroAlergiaUsualController extends ActionController {

	private static final long serialVersionUID = 5394152510407915227L;

	private static final String PAGE_MANTER_CADASTRO_ALERGIA_USUAL_LIST = "prescricaomedica-manterCadastroAlergiaUsualList";
	
	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;
	
	@Inject
	private ManterCadastroAlergiaUsualPaginatorController manterCadastroAlergiaUsualPaginatorController;
	
	private boolean indSituacao;
	
	private boolean emEdicao;
	
	private MpmAlergiaUsual mpmAlergiaUsual;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void iniciar() {
		if(mpmAlergiaUsual != null && mpmAlergiaUsual.getSeq() != null){
			this.emEdicao = true;
		}else{
			mpmAlergiaUsual = new MpmAlergiaUsual();
			this.indSituacao = true;
			this.emEdicao = false;
		}
	}

	public String gravar() throws ApplicationBusinessException{
		if(!emEdicao){
			this.cadastrosBasicosPrescricaoMedicaFacade.salvarAlergiaUsual(mpmAlergiaUsual, indSituacao);
			this.apresentarMsgNegocio(Severity.INFO, "M01", truncar(mpmAlergiaUsual.getDescricao().trim(), 20));
		}else{
			this.cadastrosBasicosPrescricaoMedicaFacade.alterarAlergiaUsual(mpmAlergiaUsual, indSituacao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ALERGIA_USUAL_ALTERADA");
		}
		indSituacao = true;
		mpmAlergiaUsual = new MpmAlergiaUsual();
		this.manterCadastroAlergiaUsualPaginatorController.pesquisar();
		return PAGE_MANTER_CADASTRO_ALERGIA_USUAL_LIST;
	}
	
    public String truncar(String item, Integer maximo) {
           item = StringUtils.abbreviate(item, maximo);
        return item;
  }      
	
	public String voltar(){
		mpmAlergiaUsual = new MpmAlergiaUsual();
		indSituacao = Boolean.TRUE;
		return PAGE_MANTER_CADASTRO_ALERGIA_USUAL_LIST;
	}
	
	/*
	 * Getters and Setters
	 */
	
	public boolean isIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public MpmAlergiaUsual getMpmAlergiaUsual() {
		return mpmAlergiaUsual;
	}

	public void setMpmAlergiaUsual(MpmAlergiaUsual mpmAlergiaUsual) {
		this.mpmAlergiaUsual = mpmAlergiaUsual;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}
}
