package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoJustificativa;
import br.gov.mec.aghu.model.ScoJustificativa;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class JustificativaController extends ActionController {

	private static final Log LOG = LogFactory.getLog(JustificativaController.class);

	private static final long serialVersionUID = 0L;

	private static final String JUSTIFICATIVA_LIST = "justificativaList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private ScoJustificativa  justificativa = new ScoJustificativa();


	private Short codigo;

	private boolean consulta = false;

	private String voltarPara = null;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

	 

		if (this.getCodigo() == null) {
			this.setJustificativa(new ScoJustificativa());
			this.getJustificativa().setTipo(DominioTipoJustificativa.E);
			this.getJustificativa().setIndSituacao(DominioSituacao.A);
			
		} else {
			justificativa = comprasCadastrosBasicosFacade.obterJustificativa(this.getCodigo());

			if(justificativa == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		}
		return null;
	
	}

	public String gravar() {
		try {
			if (this.getJustificativa() != null) {
				final boolean novo = this.getJustificativa().getCodigo() == null;
				
				if (novo) {
					this.comprasCadastrosBasicosFacade.inserirJustificativa(this.getJustificativa());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JUSTIFICATIVA_INSERT_SUCESSO", getJustificativa().getDescricao());
					
				} else {
					this.comprasCadastrosBasicosFacade.alterarJustificativa(this.getJustificativa());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_JUSTIFICATIVA_UPDATE_SUCESSO", getJustificativa().getDescricao());
				}
			}
			
			return cancelar();
			
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
		justificativa = null;
		return JUSTIFICATIVA_LIST;
	}

	public void setConsulta(final boolean consulta) {
		this.consulta = consulta;
	}

	public boolean isConsulta() {
		return consulta;
	}

	public void setVoltarPara(final String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public ScoJustificativa getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(ScoJustificativa justificativa) {
		this.justificativa = justificativa;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
}