package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpClasseImpressao;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.model.cups.ImpServidorCups;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ComputadorImpressoraController extends ActionController {

	private static final long serialVersionUID = -2898613941867055916L;

	private ImpComputadorImpressora impComputadorImpressora;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	private static final String PESQUISAR_COMPUTADOR_IMPRESSORA = "pesquisarComputadorImpressora";

	private Integer idComputadorImpressora;

	private boolean computadorReadOnly;

	private boolean classeImpressaoReadOnly;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
		if(idComputadorImpressora != null){
			try {
				impComputadorImpressora = cadastrosBasicosCupsFacade.obterComputadorImpressora(idComputadorImpressora);
				
				if(impComputadorImpressora == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				}
				
				setComputadorReadOnly(true);
				setClasseImpressaoReadOnly(true);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
		} else {
			try {
				impComputadorImpressora = new ImpComputadorImpressora();
				
				// diferença máxima de dias entre data inicial e data final
				int idClasseImpressaoSugerida =  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_ID_CLASSE_IMPRESSAO_SUGERIDA); 
				
				// Seta classe A como padrão.
				ImpClasseImpressao classe = cadastrosBasicosCupsFacade.obterClasseImpressao(idClasseImpressaoSugerida);
				if(classe != null){
					impComputadorImpressora.setImpClasseImpressao(classe);
				}
				
				setComputadorReadOnly(false);
				setClasseImpressaoReadOnly(false);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	

	public String gravar() {

		try {

			boolean indicaEdicao = impComputadorImpressora.getId() != null;

			cadastrosBasicosCupsFacade.gravarComputadorImpressora(impComputadorImpressora);

			if (indicaEdicao) {
				apresentarMsgNegocio(Severity.INFO, "COMPUTADOR_IMPRESSORA_ALTERADO_COM_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "COMPUTADOR_IMPRESSORA_INCLUIDO_COM_SUCESSO");
			}

			return cancelar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar() {
		impComputadorImpressora = null;
		idComputadorImpressora = null;
		return PESQUISAR_COMPUTADOR_IMPRESSORA;
	}

	public List<ImpComputador> pesquisarComputador(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarComputador(paramPesquisa);
	}

	public List<ImpClasseImpressao> pesquisarClasseImpressao(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarClasseImpressao(paramPesquisa);
	}

	public List<ImpServidorCups> pesquisarServidorCups(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarServidorCups(paramPesquisa);
	}

	public List<ImpImpressora> pesquisarImpressora(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarImpressora(paramPesquisa);
	}
	
	public Integer getIdComputadorImpressora() {
		return idComputadorImpressora;
	}

	public void setIdComputadorImpressora(Integer idComputadorImpressora) {
		this.idComputadorImpressora = idComputadorImpressora;
	}

	public boolean isComputadorReadOnly() {
		return computadorReadOnly;
	}

	public void setComputadorReadOnly(boolean computadorReadOnly) {
		this.computadorReadOnly = computadorReadOnly;
	}

	public boolean isClasseImpressaoReadOnly() {
		return classeImpressaoReadOnly;
	}

	public void setClasseImpressaoReadOnly(boolean classeImpressaoReadOnly) {
		this.classeImpressaoReadOnly = classeImpressaoReadOnly;
	}

	public void setImpComputadorImpressora(
			ImpComputadorImpressora impComputadorImpressora) {
		this.impComputadorImpressora = impComputadorImpressora;
	}

	public ImpComputadorImpressora getImpComputadorImpressora() {
		return impComputadorImpressora;
	}

}
