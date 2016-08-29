package br.gov.mec.aghu.compras.parecer.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.parecer.cadastrosbasicos.business.IParecerCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class AgrupamentoMaterialController extends ActionController {


	private static final Log LOG = LogFactory.getLog(AgrupamentoMaterialController.class);

	private static final long serialVersionUID = -7626137544256850184L;

	private static final String AGRUPAMENTO_MATERIAL_LIST = "agrupamentoMaterialList";

	@EJB
	private IParecerCadastrosBasicosFacade parecerCadastrosBasicosFacade;
	
	private ScoAgrupamentoMaterial agrupMaterial;
	private Short codigo;
	private Boolean indAtivo;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 

		if (this.getCodigo() == null) {
			this.setAgrupMaterial(new ScoAgrupamentoMaterial());
			this.setIndAtivo(true);
			
		} else {
			agrupMaterial =parecerCadastrosBasicosFacade.obterAgrupamentoMaterial(this.getCodigo());
			if(agrupMaterial == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			if (this.getAgrupMaterial().getIndSituacao() == DominioSituacao.A) {
				this.setIndAtivo(true);
			} else {
				this.setIndAtivo(false);
			}
		}
		
		return null;
	
	}

	public String gravar() {
		try {
			if (this.getAgrupMaterial() != null) {
				final boolean novo = this.getAgrupMaterial().getCodigo() == null;
				
				if (this.indAtivo) {
					this.getAgrupMaterial().setIndSituacao(DominioSituacao.A);
				}
				else {
					this.getAgrupMaterial().setIndSituacao(DominioSituacao.I);
				}

				if (novo) {
					this.parecerCadastrosBasicosFacade.inserirAgrupamentoMaterial(this.getAgrupMaterial());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AGRUP_MATERIAL_INSERT_SUCESSO");
				} else {
					this.parecerCadastrosBasicosFacade.alterarAgrupamentoMaterial(this.getAgrupMaterial());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AGRUP_MATERIAL_UPDATE_SUCESSO");
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
		agrupMaterial = null;
		return AGRUPAMENTO_MATERIAL_LIST;
	}

	public ScoAgrupamentoMaterial getAgrupMaterial() {
		return agrupMaterial;
	}

	public void setAgrupMaterial(ScoAgrupamentoMaterial agrupMaterial) {
		this.agrupMaterial = agrupMaterial;
	}
	
	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	
	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}
}