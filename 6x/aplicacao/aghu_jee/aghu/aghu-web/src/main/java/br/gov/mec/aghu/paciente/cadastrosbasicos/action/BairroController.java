package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class BairroController extends ActionController {

	private static final long serialVersionUID = -642156003440052011L;
	
	private static final Log LOG = LogFactory.getLog(BairroController.class);
	
	private static final String REDIRECIONA_LISTAR_BAIRRO = "bairroList";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	private AipBairros bairro = new AipBairros();

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String salvar() {
		try {
			boolean novoCadastro = bairro.getCodigo() != null;
			
			cadastrosBasicosPacienteFacade.persistirBairro(this.bairro);
			
			if (novoCadastro) {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ATUALIZACAO_BAIRRO");

			} else {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_BAIRRO");
			}
			
			bairro = new AipBairros();
			
			return REDIRECIONA_LISTAR_BAIRRO;
		} catch (ApplicationBusinessException e) {
			LOG.debug("Erro ao persistir/atualizar bairro:", e);
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String cancelar() {
		bairro = new AipBairros();
		return REDIRECIONA_LISTAR_BAIRRO;
	}

	public AipBairros getBairro() {
		return bairro;
	}

	public void setBairro(AipBairros bairro) {
		this.bairro = bairro;
	}
}