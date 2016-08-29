package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.ICidFacade;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CapituloCidController extends ActionController{

	private static final long serialVersionUID = -3681064855754657548L;
	private static final String CAPITULO_CID_LIST = "capituloCidList";

	@EJB
	private ICidFacade cidFacade;
	
	private AghCapitulosCid capituloCid;	
	private Integer capituloCidSeq;

	@PostConstruct
	public void init(){
		this.begin(conversation);
	}
	
	public void inicializar(){
	 

		if (this.getCapituloCidSeq() == null) {
			capituloCid = new AghCapitulosCid();
			capituloCid.setIndDiagPrincipal(DominioSimNao.S);
			capituloCid.setIndSituacao(DominioSituacao.A);
		}
	
	}

	/**
	 * Método que realiza a ação do botão "Confirmar" na criação de um novo Capítulo CID
	 * 
	 * @return Tela para onde deverá ser redirecionado após a inclusão
	 */
	public String confirmar() {
		try{
			
			cidFacade.persistirCapituloCid(capituloCid);
						
			if (this.capituloCid.getSeq() == null) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_CAPITULO_CID", this.capituloCid.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_CAPITULO_CID", this.capituloCid.getDescricao());
			}
						
			return cancelar();
		}
		catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}		
	}
	
	/**
	 * Método que realiza a ação do botão cancelar na tela de Incluir novo
	 * Capitulo CID
	 * 
	 * @return Tela destino
	 */
	public String cancelar() {
		capituloCidSeq = null;
		capituloCid = new AghCapitulosCid();
		return CAPITULO_CID_LIST;
	}
	
	//SET's e GET's	
	public AghCapitulosCid getCapituloCid() {
		return capituloCid;
	}

	public void set(AghCapitulosCid capituloCid) {
		this.capituloCid = capituloCid;
	}
	
	public Integer getCapituloCidSeq() {
		return capituloCidSeq;
	}

	public void setCapituloCidSeq(Integer capituloCidSeq) {
		this.capituloCidSeq = capituloCidSeq;
	}

	public void setCapituloCid(AghCapitulosCid capituloCid) {
		this.capituloCid = capituloCid;
	}	
}