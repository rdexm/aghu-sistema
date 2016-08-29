package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsExameComponenteVisualPrescricao;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterExamesDaHemoterapiaController extends ActionController {

	private static final long serialVersionUID = 8769535793532380248L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;	

	private static final String PAGE_LIST = "pesquisarExamesDaHemoterapia";

	private Integer seq;
	private String pesquisaComponenteSaguineo;

	private AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao = new AbsExameComponenteVisualPrescricao();

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

		if (seq != null) {
			this.absExameComponenteVisualPrescricao = bancoDeSangueFacade.obterAbsExameComponenteVisualPrescricaoPorId(this.seq, 
																					AbsExameComponenteVisualPrescricao.Fields.CAMPO_LAUDO, 
																					AbsExameComponenteVisualPrescricao.Fields.COMPONENTE_SANGUINEO);
		} else {

			if(StringUtils.isNotBlank(pesquisaComponenteSaguineo)){
				this.absExameComponenteVisualPrescricao.setComponenteSanguineo(pesquisarComponenteSanguineoUnico(this.pesquisaComponenteSaguineo));
			}
		}
	
	}

	public String gravar() {
		try {
			if (this.seq == null || this.absExameComponenteVisualPrescricao.getSeq() == null) {
				
				this.bancoDeSangueFacade.manterAbsExameComponenteVisualPrescricao(absExameComponenteVisualPrescricao);				
				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_EXAME_HEMOTERAPIA", absExameComponenteVisualPrescricao.getCampoLaudo().getNome());
				
			} else {
				this.bancoDeSangueFacade.atualizarAbsExameComponenteVisualPrescricao(absExameComponenteVisualPrescricao);								
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_EXAME_HEMOTERAPIA", absExameComponenteVisualPrescricao.getCampoLaudo().getNome());
			}
			
			cancelar();			
			return PAGE_LIST;
			
		} catch (BaseException e) {			
			super.apresentarExcecaoNegocio(e);
			return null;
		}  catch (BaseRuntimeException e) {			
			super.apresentarExcecaoNegocio(e);
			return null;
		} 

	}

	public void cancelar(){
		this.seq = null;
		this.absExameComponenteVisualPrescricao = new AbsExameComponenteVisualPrescricao();
	}

	public String cancelarEdicao(){
		cancelar();		
		return PAGE_LIST;
	} 

	
	// suggestion - componente saguineo - passado por parametro
	public AbsComponenteSanguineo pesquisarComponenteSanguineoUnico(String param) {
		return this.bancoDeSangueFacade.obterComponenteSanguineoUnico(param);
	}

	// suggestion - componente saguineo
	public List<AbsComponenteSanguineo> pesquisarComponenteSanguineo(
			String param) {
		return  this.bancoDeSangueFacade.obterComponenteSanguineos(param);
	}

	// suggestion - laudo
	public List<AelCampoLaudo> pesquisarLaudo(String param) {
		return this.examesFacade.obterLaudo(param);
	}
	
	public Long pesquisarLaudoCount(Object param) {
		return this.examesFacade.pesquisarLaudoCount(param);
	}
	
	public AbsExameComponenteVisualPrescricao getAbsExameComponenteVisualPrescricao() {
		return absExameComponenteVisualPrescricao;
	}

	public void setAbsExameComponenteVisualPrescricao(
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) {
		this.absExameComponenteVisualPrescricao = absExameComponenteVisualPrescricao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	} 

	public String getPesquisaComponenteSaguineo() {
		return pesquisaComponenteSaguineo;
	}

	public void setPesquisaComponenteSaguineo(String pesquisaComponenteSaguineo) {
		this.pesquisaComponenteSaguineo = pesquisaComponenteSaguineo;
	} 
}
