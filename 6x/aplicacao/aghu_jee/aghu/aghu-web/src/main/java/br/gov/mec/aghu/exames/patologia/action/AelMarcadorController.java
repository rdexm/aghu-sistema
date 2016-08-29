package br.gov.mec.aghu.exames.patologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelMarcador;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class AelMarcadorController extends ActionController {

	private static final String AEL_MARCADOR_LIST = "aelMarcadorList";

	private static final long serialVersionUID = -6513217325113866273L;
	private static final Log LOG = LogFactory.getLog(AelMarcadorController.class);

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private AelMarcador aelMarcador;

	private Integer seqMarcador;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

		if (this.getSeqMarcador() == null) {
			this.setAelMarcador(new AelMarcador());
		} else {
			this.setAelMarcador(this.examesPatologiaFacade
					.obterAelMarcadorPorChavePrimaria(this.getSeqMarcador()));
		}
	
	}

	public String confirmar() {
		String result = null;
		try {
			this.examesPatologiaFacade.inserirAelMarcador(getAelMarcador());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MARCADOR_GRAVADO_SUCESSO", this.getAelMarcador().getMarcadorPedido());
			result = AEL_MARCADOR_LIST;

		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return result;
	}

	public String cancelar() {
		return AEL_MARCADOR_LIST;
	}

	public List<ScoMarcaComercial> pesquisarFabricante(String parametro) {
		try {
			return this.returnSGWithCount(this.comprasFacade.getListaMarcasByNomeOrCodigo(parametro,0, 100, ScoMarcaComercial.Fields.DESCRICAO.toString(), true),pesquisarFabricanteCount(parametro));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public Long pesquisarFabricanteCount(String parametro) {
		return comprasFacade.getListaMarcasByNomeOrCodigoCount(parametro);
	}

	// ### GETs e SETs ###

	public void setSeqMarcador(Integer seqMarcador) {
		this.seqMarcador = seqMarcador;
	}

	public Integer getSeqMarcador() {
		return seqMarcador;
	}

	public void setAelMarcador(AelMarcador aelMarcador) {
		this.aelMarcador = aelMarcador;
	}

	public AelMarcador getAelMarcador() {
		return aelMarcador;
	}
}
