package br.gov.mec.aghu.parametrosistema.action;

import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsavel por cadastrar / editar um parametro de sistema.
 * 
 * @author rcorvalao
 */
public class ParametroController extends ActionController {
	
	private static final long serialVersionUID = -7641684212006885879L;

	private static final String PAGE_PESQUISAR_PARAMETRO = "parametroList";
	
	private static final Log LOG = LogFactory.getLog(ParametroController.class);
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private IParametroSistemaFacade parametroSistemaFacade;
	
	private AghParametros parametro;
	
	private Integer seq;

	private static final Enum[] fetchArgsInnerJoin = {AghParametros.Fields.SISTEMA};

	public String iniciar() {
	 


		if (seq != null) {
			parametro = parametroSistemaFacade.obterParametroPorId(seq, fetchArgsInnerJoin, null);
			
			if(parametro == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			parametro = new AghParametros();
		}
		
		return null;
	
	}
	
	public String confirmar() {
		try {
			final boolean insert =  (parametro != null && parametro.getSeq() == null);
			
			//SecurityContextAssociation.setPrincipal(new SimplePrincipal(loginUsuario));
			parametroSistemaFacade.persistirParametro(getParametro());
			
			this.apresentarMsgNegocio(Severity.INFO
					, (insert ? "MENSAGEM_SUCESSO_INCLUSAO_PARAMETRO" : "MENSAGEM_SUCESSO_ALTERACAO_PARAMETRO")
					, this.getParametro().getNome());
			
			this.setParametro(new AghParametros());
			
			return cancelar();
		} catch (BaseException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		seq = null;
		parametro = null;
		return PAGE_PESQUISAR_PARAMETRO;
	}
	
	public List<AghSistemas> pesquisarSistemaPorNome(String paramPesquisa) {
		return parametroSistemaFacade.pesquisarSistemaPorNome((String) paramPesquisa);
	}

	public AghParametros getParametro() {
		return parametro;
	}

	public void setParametro(AghParametros parametro) {
		this.parametro = parametro;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}