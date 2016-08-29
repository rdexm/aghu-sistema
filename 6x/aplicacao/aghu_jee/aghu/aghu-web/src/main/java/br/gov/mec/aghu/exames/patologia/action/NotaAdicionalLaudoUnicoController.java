package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelNotaAdicionalAp;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;


public class NotaAdicionalLaudoUnicoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 3968402469824627883L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private Long luxSeq;
	private String textoNotaAdicional;
	private String notasAdicionaisAnteriores;

	public void inicio(Long seq) {
		this.luxSeq = seq;
		
		final List<AelNotaAdicionalAp> notas = this.examesPatologiaFacade.obterListaNotasAdicionaisPeloExameApSeq(this.luxSeq);
		
		final StringBuffer notasAdicionais = new StringBuffer();
		for(AelNotaAdicionalAp nota : notas) {
			notasAdicionais.append("Em: ").append(DateUtil.dataToString(nota.getCriadoEm(), "dd/MM/yyyy HH:mm"));
			notasAdicionais.append('\n').append(nota.getNotas()).append('\n');
		}
		
		notasAdicionaisAnteriores = notasAdicionais.toString();
	}
	
	public void limpar() {
		textoNotaAdicional = null;
		notasAdicionaisAnteriores = null;
	}
	
	public void gravarNotaAdicional(){
		AelNotaAdicionalAp nota = new AelNotaAdicionalAp();
		nota.setNotas(textoNotaAdicional);
		try {			
			
			nota.setAelExameAp(this.examesPatologiaFacade.obterAelExameApPorChavePrimaria(this.luxSeq));
			this.examesPatologiaFacade.persistirAelNotaAdicionalAp(nota);
			this.examesPatologiaFacade.gravarNotasAdicionais(nota.getNotas(), luxSeq);
			textoNotaAdicional=null;
			this.inicio(this.luxSeq);
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String getTextoNotaAdicional() {
		return textoNotaAdicional;
	}

	public void setTextoNotaAdicional(String textoNotaAdicional) {
		this.textoNotaAdicional = textoNotaAdicional;
	}

	public String getNotasAdicionaisAnteriores() {
		return notasAdicionaisAnteriores;
	}

	public void setNotasAdicionaisAnteriores(String notasAdicionaisAnteriores) {
		this.notasAdicionaisAnteriores = notasAdicionaisAnteriores;
	}

}