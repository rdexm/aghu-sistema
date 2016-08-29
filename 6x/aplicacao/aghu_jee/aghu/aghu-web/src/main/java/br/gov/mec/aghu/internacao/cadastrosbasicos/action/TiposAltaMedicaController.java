package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de
 * Tipos de Alta Medica.
 */


public class TiposAltaMedicaController  extends ActionController  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3399092673071207369L;
	
	private final String PAGE_PESQUISAR_TIPO_ALTA_MEDICA = "tiposAltaMedicaList";

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private AinTiposAltaMedica tipoAltaMedica;
	
	private Boolean update;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		tipoAltaMedica=new AinTiposAltaMedica();
	}
	
	public String cancelar() {
		tipoAltaMedica=new AinTiposAltaMedica();
		return PAGE_PESQUISAR_TIPO_ALTA_MEDICA;
	}
	
	public String confirmar() {
		try {
			if(!update){
				this.tipoAltaMedica.setIndSituacao(DominioSituacao.A);
			}
			
			this.cadastrosBasicosInternacaoFacade.persistirTipoAltaMedica(this.tipoAltaMedica, update);
			
			if (!update) {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_TIPOALTAMEDICA",this.tipoAltaMedica.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EDICAO_TIPOALTAMEDICA",this.tipoAltaMedica.getDescricao());
			}
			
			return cancelar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public List<MpmMotivoAltaMedica> listarMotivosAltaMedica(){
		return cadastrosBasicosInternacaoFacade.pesquisarMotivosAltaMedica(null);
	}
	
	public AinTiposAltaMedica getTipoAltaMedica() {
		return tipoAltaMedica;
	}

	public void setTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica) {
		this.tipoAltaMedica = tipoAltaMedica;
	}

	public Boolean getUpdate() {
		return update;
	}

	public void setUpdate(Boolean update) {
		this.update = update;
	}
	
	
}
