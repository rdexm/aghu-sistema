package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinTipoCaracteristicaLeito;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de tipo de
 * característica de leito.
 * 
 * @author riccosta
 */

public class TiposCaracteristicasLeitoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1597756475045432641L;
	
	private final String PAGE_PESQUISAR_TIPO_CARACTERISTICA_LEITO = "tiposCaracteristicaLeitoList";

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Tipo de unidade funcional a ser criada/editada.
	 */
	private AinTipoCaracteristicaLeito tipoCaracteristicaLeito;

	
	@PostConstruct
	public void init() {
		begin(conversation);
		tipoCaracteristicaLeito=new AinTipoCaracteristicaLeito();
	}
	
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de tipo
	 * de unidade funcional
	 */
	public String confirmar() {
		try {
			boolean update = this.getTipoCaracteristicaLeito().getCodigo()!=null;
			
			this.cadastrosBasicosInternacaoFacade.persistirTiposCaracteristicaLeito(this.tipoCaracteristicaLeito);

			if (!update) {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_TIPO_CARACTERISTICA_LEITO",this.tipoCaracteristicaLeito.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EDICAO_TIPO_CARACTERISTICA_LEITO",this.tipoCaracteristicaLeito.getDescricao());
			}
			tipoCaracteristicaLeito=new AinTipoCaracteristicaLeito();
			return PAGE_PESQUISAR_TIPO_CARACTERISTICA_LEITO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de tipo
	 * de unidade funcional
	 */
	public String cancelar() {
		tipoCaracteristicaLeito=new AinTipoCaracteristicaLeito();
		return PAGE_PESQUISAR_TIPO_CARACTERISTICA_LEITO;
	}


	public AinTipoCaracteristicaLeito getTipoCaracteristicaLeito() {
		return tipoCaracteristicaLeito;
	}


	public void setTipoCaracteristicaLeito(AinTipoCaracteristicaLeito tipoCaracteristicaLeito) {
		this.tipoCaracteristicaLeito = tipoCaracteristicaLeito;
	}
	

}
