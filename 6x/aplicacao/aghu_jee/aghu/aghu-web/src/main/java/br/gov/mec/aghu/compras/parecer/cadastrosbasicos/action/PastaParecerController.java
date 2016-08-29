package br.gov.mec.aghu.compras.parecer.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.parecer.cadastrosbasicos.business.IParecerCadastrosBasicosFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.model.ScoOrigemParecerTecnico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class PastaParecerController extends ActionController {

	private static final Log LOG = LogFactory.getLog(PastaParecerController.class);

	private static final long serialVersionUID = -5139633314014450692L;

	private static final String PASTA_PARECER_LIST = "pastaParecerList";

	@EJB
	private IParecerCadastrosBasicosFacade parecerCadastrosBasicosFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	private ScoOrigemParecerTecnico pasta;
	private Integer codigo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 

		if (this.getCodigo() == null) {
			this.setPasta(new ScoOrigemParecerTecnico());
		} else {
			this.setPasta(this.parecerCadastrosBasicosFacade.obterOrigemParecer(this.getCodigo()));

			if(pasta == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		}
		
		return null;
	
	}

	public String gravar() {
		try {
			if (this.getPasta() != null) {
				final boolean novo = this.getPasta().getCodigo() == null;

				if (novo) {
					this.parecerCadastrosBasicosFacade.inserirOrigemParecer(this.getPasta());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PASTA_PARECER_INSERT_SUCESSO");
				} else {
					this.parecerCadastrosBasicosFacade.alterarOrigemParecer(this.getPasta());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PASTA_PARECER_UPDATE_SUCESSO");
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
	
	public List<ScoAgrupamentoMaterial> pesquisarAgrupamentoAtivos(String parametro) {
		return this.parecerCadastrosBasicosFacade.pesquisarAgrupamentoMaterialPorCodigoOuDescricao(parametro, true);
	}
	
	public List<FccCentroCustos> pesquisarCentroCustoAtivos(String parametro) throws BaseException {
		return centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricao(parametro);
	}
		
	public String cancelar() {
		codigo = null;
		pasta = null;
		return PASTA_PARECER_LIST;
	}

	public ScoOrigemParecerTecnico getPasta() {
		return pasta;
	}

	public void setPasta(ScoOrigemParecerTecnico pasta) {
		this.pasta = pasta;
	}
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
}