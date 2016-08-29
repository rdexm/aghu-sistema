package br.gov.mec.aghu.bancosangue.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsOrientacaoComponentes;
import br.gov.mec.aghu.model.AbsOrientacaoComponentesId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterOrientacoesController extends ActionController {


	private static final long serialVersionUID = -6978241677174344934L;

	private static final String PESQUISAR_ORIENTACOES = "pesquisarOrientacoes";

	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;	

	private AbsOrientacaoComponentes absOrientacaoComponentes;
	private String componenteSanguineoCodigo;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar(){
		
		if(absOrientacaoComponentes != null && absOrientacaoComponentes.getId() != null){
			absOrientacaoComponentes = bancoDeSangueFacade.obterAbsOrientacaoComponentesPorId(absOrientacaoComponentes.getId(), true,
					AbsOrientacaoComponentes.Fields.COMPONENTE_SANGUINEO);
			
			if(absOrientacaoComponentes == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			absOrientacaoComponentes = new AbsOrientacaoComponentes();
			absOrientacaoComponentes.setId(new AbsOrientacaoComponentesId());
			absOrientacaoComponentes.setIndSituacao(DominioSituacao.A);
			
			if(componenteSanguineoCodigo != null){
				absOrientacaoComponentes.setComponenteSanguineo(bancoDeSangueFacade.obterComponenteSanguineosPorCodigo(componenteSanguineoCodigo));
				absOrientacaoComponentes.getId().setCsaCodigo(absOrientacaoComponentes.getComponenteSanguineo().getCodigo());
			}
		}
		
		return null;
	
	}
	
	// suggestion - componente saguineo - passado por parametro
	public AbsComponenteSanguineo pesquisarComponenteSanguineoUnico(String param) {
		return bancoDeSangueFacade.obterComponenteSanguineoUnico(param);
	}
	
	//suggestion - componente saguineo
	public List<AbsComponenteSanguineo> pesquisarComponenteSanguineo(Object param) {
		return bancoDeSangueFacade.obterComponenteSanguineos(param);
	}
	
	//Gravar
	public String gravar() throws BaseException {
		try {
			if (absOrientacaoComponentes.getId().getSeqp() == null) {
				bancoDeSangueFacade.manterAbsOrientacaoComponentes(absOrientacaoComponentes);								
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_ORIENTACAO_COMPONENTES");
				
			} else {
				bancoDeSangueFacade.atualizarAbsOrientacaoComponentes(absOrientacaoComponentes);								
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ATUALIZAR_ORIENTACAO_COMPONENTES");
			}
			
			return cancelar();
		} catch (BaseException e) {			
			super.apresentarExcecaoNegocio(e);
			return null;
			
		}  catch (BaseRuntimeException e) {			
			super.apresentarExcecaoNegocio(e);
			return null;
		} 
	}
	
	public String cancelar() {
		setAbsOrientacaoComponentes(null);		
		return PESQUISAR_ORIENTACOES;
	}

	public AbsOrientacaoComponentes getAbsOrientacaoComponentes() {
		return absOrientacaoComponentes;
	}

	public void setAbsOrientacaoComponentes(
			AbsOrientacaoComponentes absOrientacaoComponentes) {
		this.absOrientacaoComponentes = absOrientacaoComponentes;
	}

	public String getComponenteSanguineoCodigo() {
		return componenteSanguineoCodigo;
	}

	public void setComponenteSanguineoCodigo(String componenteSanguineoCodigo) {
		this.componenteSanguineoCodigo = componenteSanguineoCodigo;
	}
}