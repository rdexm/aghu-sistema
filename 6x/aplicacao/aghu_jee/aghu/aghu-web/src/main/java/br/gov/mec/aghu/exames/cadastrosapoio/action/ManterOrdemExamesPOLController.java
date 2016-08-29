package br.gov.mec.aghu.exames.cadastrosapoio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelOrdExameMatAnalise;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterOrdemExamesPOLController extends ActionController {

	private static final long serialVersionUID = 1775833194777542204L;

	private static final String PESQUISAR_ORDEM_EXAMES_POL = "exames-pesquisarOrdemExamesPOL";
	
	@EJB
	private IExamesFacade exameFacade;

	private AelOrdExameMatAnalise entidade;
	private AelExames exame;
	private AelMateriaisAnalises aelMateriaisAnalises;
	
	//atributos da pesquisa
	private Short ordemNivel1Pesquisa;
	private Short ordemNivel2Pesquisa;
	private String emaExaSigla;
	private Integer emaManSeq;
	private String examePesquisaSigla;
	private Integer materialAnalisePesquisaSeq;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar(){
	 

		
		if(StringUtils.isNotBlank(emaExaSigla) && emaManSeq != null) {
			AelExamesMaterialAnaliseId aelExamesMaterialAnaliseId = new AelExamesMaterialAnaliseId();
			aelExamesMaterialAnaliseId.setExaSigla(emaExaSigla);
			aelExamesMaterialAnaliseId.setManSeq(emaManSeq);
			entidade = exameFacade.recuperaAelOrdExameMatAnalisePorMaterial(aelExamesMaterialAnaliseId);

			if(entidade == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			setExame(getEntidade().getExame());
			setAelMateriaisAnalises(getEntidade().getExamesMaterialAnalise().getAelMateriaisAnalises());
		}
		
		return null;
	
	}
	
	public void alterar(){
		try {
			exameFacade.atualizarAelOrdExameMatAnalise(getEntidade());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_ORDEM_EXAMES");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String cancelar(){
		ordemNivel1Pesquisa = null;
		ordemNivel2Pesquisa = null;
		emaExaSigla = null;
		emaManSeq = null;
		examePesquisaSigla = null;
		materialAnalisePesquisaSeq = null;
		return PESQUISAR_ORDEM_EXAMES_POL;
	}
	
	public AelOrdExameMatAnalise getEntidade() {
		return entidade;
	}

	public void setEntidade(AelOrdExameMatAnalise entidade) {
		this.entidade = entidade;
	}

	public AelExames getExame() {
		return exame;
	}

	public void setExame(AelExames exame) {
		this.exame = exame;
	}

	public AelMateriaisAnalises getAelMateriaisAnalises() {
		return aelMateriaisAnalises;
	}

	public void setAelMateriaisAnalises(AelMateriaisAnalises aelMateriaisAnalises) {
		this.aelMateriaisAnalises = aelMateriaisAnalises;
	}

	public Short getOrdemNivel1Pesquisa() {
		return ordemNivel1Pesquisa;
	}

	public void setOrdemNivel1Pesquisa(Short ordemNivel1Pesquisa) {
		this.ordemNivel1Pesquisa = ordemNivel1Pesquisa;
	}

	public Short getOrdemNivel2Pesquisa() {
		return ordemNivel2Pesquisa;
	}

	public void setOrdemNivel2Pesquisa(Short ordemNivel2Pesquisa) {
		this.ordemNivel2Pesquisa = ordemNivel2Pesquisa;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public String getExamePesquisaSigla() {
		return examePesquisaSigla;
	}

	public void setExamePesquisaSigla(String examePesquisaSigla) {
		this.examePesquisaSigla = examePesquisaSigla;
	}

	public Integer getMaterialAnalisePesquisaSeq() {
		return materialAnalisePesquisaSeq;
	}

	public void setMaterialAnalisePesquisaSeq(Integer materialAnalisePesquisaSeq) {
		this.materialAnalisePesquisaSeq = materialAnalisePesquisaSeq;
	}
}