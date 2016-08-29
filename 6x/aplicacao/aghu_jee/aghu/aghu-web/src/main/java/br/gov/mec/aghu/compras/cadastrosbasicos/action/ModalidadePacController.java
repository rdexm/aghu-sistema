package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class ModalidadePacController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ModalidadePacController.class);

	private static final long serialVersionUID = 8794876046365052L;

	private static final String MODALIDADE_PAC_LIST = "modalidadePacList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	private ScoModalidadeLicitacao modalidadePac;
	private String codigo;
	private Boolean indAtivo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 

		if (this.getCodigo() == null) {
			this.setIndAtivo(true);
			this.setModalidadePac(new ScoModalidadeLicitacao());
			this.getModalidadePac().setSituacao(DominioSituacao.A);
			
		} else {
			modalidadePac = comprasCadastrosBasicosFacade.obterModalidadeLicitacao(this.getCodigo());

			if(modalidadePac == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			if (this.getModalidadePac().getSituacao() == DominioSituacao.A) {
				this.setIndAtivo(true);
			}
			else {
				this.setIndAtivo(false);
			}
		}
		return null;
	
	}

	public String gravar() {
		try {
			if (this.getModalidadePac() != null) {
				
				if (this.indAtivo) {
					this.getModalidadePac().setSituacao(DominioSituacao.A);
				}
				else {
					this.getModalidadePac().setSituacao(DominioSituacao.I);
				}
				
				if (this.modalidadePac.getVersion() == null) {
					this.comprasCadastrosBasicosFacade.inserirModalidadeLicitacao(this.getModalidadePac());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MODALIDADE_PAC_INSERT_SUCESSO_M01");
				} else {
					this.comprasCadastrosBasicosFacade.alterarModalidadeLicitacao(this.getModalidadePac());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MODALIDADE_PAC_UPDATE_SUCESSO_M02");
				}

				return cancelar();
			}
			
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}
	
	public String cancelar() {
		codigo = null;
		modalidadePac = null;
		return MODALIDADE_PAC_LIST;
	}

	public ScoModalidadeLicitacao getModalidadePac() {
		return modalidadePac;
	}

	public void setModalidadePac(ScoModalidadeLicitacao modalidadePac) {
		this.modalidadePac = modalidadePac;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}
}